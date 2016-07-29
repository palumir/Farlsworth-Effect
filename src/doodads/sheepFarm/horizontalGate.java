package doodads.sheepFarm;

import java.util.Random;

import drawing.camera;
import interactions.event;
import interactions.interactBox;
import interactions.textSeries;
import modes.mode;
import sounds.sound;
import terrain.chunk;
import terrain.chunkType;
import terrain.generalChunkType;
import units.humanType;
import units.player;
import units.unit;
import units.unitType;
import units.bosses.farlsworth;
import utilities.stringUtils;
import utilities.time;
import zones.zone;
import zones.farmLand.sheepFarm;

public class horizontalGate extends chunk {
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default name.
	private static String DEFAULT_CHUNK_NAME = "horizontalGate";
	
	// Sprite stuff.
	private static String DEFAULT_CHUNK_SPRITESHEET = "images/doodads/farmLand/sheepFarm/"+ DEFAULT_CHUNK_NAME + ".png";
	
	// Sequence for fences.
	private interactBox interactSequence;
	
	// Dimensions
	public static int DEFAULT_CHUNK_WIDTH = 46;
	public static int DEFAULT_CHUNK_HEIGHT = 34;
	
	// Sounds
	private String openGate = "sounds/effects/doodads/openGate.wav";
	private String closeGate = "sounds/effects/doodads/closeGate.wav";
	
	// Key name.
	private String keyName;
	
	// Unique gate name.
	private String uniqueGateName; 
	
	// Open or closed.
	private boolean open = false;
	
	// Talked to the gate? For forest joke.
	private boolean talkedToForestGateOnce = false;
	
	// Event on whether or not the gate is open.
	private event hasBeenOpened;
	private event isOpen;
	
	// The actual type.
	private static generalChunkType typeReference = new generalChunkType(DEFAULT_CHUNK_NAME, DEFAULT_CHUNK_SPRITESHEET, DEFAULT_CHUNK_WIDTH, DEFAULT_CHUNK_HEIGHT);  
	
	///////////////
	/// METHODS ///
	///////////////
	// Constructor
	public horizontalGate(String newGateName, String newKeyName, int newX, int newY, int i) {
		super(typeReference, newX, newY, i, 0);
		
		// Key name.
		keyName = newKeyName;
		
		// Event for gate open.
		isOpen = new event(newGateName + "isOpen");
		hasBeenOpened = new event(newGateName + "hasBeenOpened");
		
		// Check if we have save data on the gate.
		if(isOpen.isCompleted()) {
			forceOpen();
		}
		else {
			setPassable(false);
		}
		
		// Set gate name.
		uniqueGateName = newGateName;
		
		if(mode.getCurrentMode().equals("topDown")) {
			setHitBoxAdjustmentY(14);
			setHeight(6);
		}
		else {
			setHitBoxAdjustmentY(0);
			setHeight(DEFAULT_CHUNK_HEIGHT);
			setWidth(DEFAULT_CHUNK_WIDTH);
		}
		if(i==1) setPassable(true);
		setInteractable(true);
	}
	
	// Create interact sequence
	public interactBox makeNormalInteractSequence() {
		
		// Placeholder for each individual textSeries.
		textSeries s;
					
		// Start of conversation.
		textSeries startOfConversation = null;
			
		if(!open) {
			startOfConversation = new textSeries("StartWithButtons", "StartWithButtons");
			startOfConversation.setTalker("");
			
			if((player.getPlayer() != null && player.getPlayer().getPlayerInventory().hasKey(keyName)) || hasBeenOpened.isCompleted()) {
				
				// Farlsworth gate joke
				if(isForestGateAndUnopenable() && !talkedToForestGateOnce) {
					s = startOfConversation.addChild("Open", "I'm not trying to pass judgement or anything.");
					s.setTalker("What's going on?");
					s = s.addChild(null, "Because I am a gate and all.");
					s.setTalker("Is this gate speaking to me?");
					s = s.addChild(null, "But don't you have some wool to retrieve nearby?");
					s.setTalker("Somebody must be playing a practical joke or something");
					s = s.addChild(null, "I'm not all-knowing or something.");
					s.setTalker("Farmer, is that you just joshing around?");
					s = s.addChild(null, "I just overheard you talking to the \"farmer\"'.");
					s.setTalker("It really isn't");
					s = s.addChild(null, "Though, I am a talking gate.");
					s.setTalker("I'm having a casual conversation with a gate");
					s = s.addChild(null, "But that's life.");
					s.setTalker("What is my life");
					s = s.addChild(null, "We're all talking gates at heart.");
					s.setTalker("I must have not gotten enough sleep");
					talkedToForestGateOnce = true;
				}
				else if(isForestGateAndUnopenable() && talkedToForestGateOnce) {
					s = startOfConversation.addChild("Open", "Go get that wool.");
					s.setTalker("A talking gate, I suppose");
					s = s.addChild(null, "Farlsworth is in the pen to my left.");
					s.setTalker("A talking gate, I suppose");
					s = s.addChild(null, "I am facing toward you, before you ask.");
					s.setTalker("A talking gate, I suppose");
					s = s.addChild(null, "I have nothing more to say.");
					s.setTalker("A talking gate, I suppose");
					s = s.addChild(null, "The gate life is a dull one.");
					s.setTalker("A talking gate, I suppose");
				}
				else {
					s = startOfConversation.addChild("Open", "You open the gate.");
				}
				
			}
			else {
				s = startOfConversation.addChild("Open", "You don't have the key.");
			}
			
			s.setEnd();
		}
		
		else {
			startOfConversation = new textSeries("StartWithButtons", "StartWithButtons");
			s = startOfConversation.addChild("Close", "You close the gate.");
			s.setEnd();
		}
		
		boolean isUnit = false;
		if(isForestGateAndUnopenable()) {
			isUnit = true;
		}
		
		return new interactBox(startOfConversation, "Gate", isUnit);
	}
	
	// Is it the forest gate
	public boolean isForestGateAndUnopenable() {
		return sheepFarm.forestGate != null &&
				this.equals(sheepFarm.forestGate) 
				&& farlsworth.pastSpawnFarm != null && !farlsworth.pastSpawnFarm.isCompleted()
				&& player.getPlayer() != null && player.getPlayer().getPlayerInventory().hasKey(keyName);
	}
	
	// Interact stuff.
	public void doInteractStuff() {
		
		// Open gate?
		if(!open 
			&& interactSequence != null 
			&& interactSequence.getTheText().getButtonText() != null
			&& interactSequence.getTheText().getButtonText().equals("Open")
			&& interactSequence.getTheText().isEnd()
			&& interactSequence.isDisplayOn()) {
			if(!isForestGateAndUnopenable()) open();
		}
			
		// Close gate?
		if(open 
			&& interactSequence != null 
			&& interactSequence.getTheText().getButtonText() != null
			&& interactSequence.getTheText().getButtonText().equals("Close")
			&& interactSequence.getTheText().isEnd()
			&& interactSequence.isDisplayOn()) {
				close();
		}
	}
	
	// Update
	@Override
	public void update() {
		doInteractStuff();
	}
	
	// Interact with object. Should be over-ridden.
	public void interactWith() { 
		interactSequence = makeNormalInteractSequence();
		interactSequence.toggleDisplay();
	}
	
	// Open the gate
	public void open() {
		if(hasBeenOpened.isCompleted() || (player.getPlayer() != null && player.getPlayer().getPlayerInventory().hasKey(keyName))) {
			
			// Play sound
			sound s = new sound(openGate);
			s.setPosition(this.getIntX(), this.getIntY(), sound.DEFAULT_SOUND_RADIUS);
			s.start();
			
			// Open gate.
			forceOpen();
		}
	}
	
	// Force gate open, no matter what.
	public void forceOpen() {
		
		// Set that the gate has been opened before.
		hasBeenOpened.setCompleted(true);
		isOpen.setCompleted(true);
		
		// Open gate.
		setPassable(true);
		chunkImage = typeReference.getChunkImage(1, 0);
		open = true;
	}
	
	// Close the gate
	public void close() {
		
		// Set that the gate is closed.
		isOpen.setCompleted(false);
		
		// Play sound
		sound s = new sound(closeGate);
		s.start();
		
		// Close the gate.
		setPassable(false);
		chunkImage = typeReference.getChunkImage(0, 0);
		open = false;
	}
}
