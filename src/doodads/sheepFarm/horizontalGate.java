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
import utilities.stringUtils;
import utilities.time;
import zones.zone;

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
		interactable = true;
	}
	
	// Create interact sequence
	public interactBox makeNormalInteractSequence() {
		
		// Placeholder for each individual textSeries.
		textSeries s;
					
		// Start of conversation.
		textSeries startOfConversation = null;
			
		if(!open) {
			startOfConversation = new textSeries("StartWithButtons", "StartWithButtons");
			
			if((player.getCurrentPlayer() != null && player.getCurrentPlayer().getPlayerInventory().hasKey(keyName)) || hasBeenOpened.isCompleted()) {
				s = startOfConversation.addChild("Open", "You open the gate.");
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
		
		return new interactBox(startOfConversation, "Gate");
	}
	
	// Interact stuff.
	public void doInteractStuff() {
		
		// Open gate?
		if(!open 
			&& interactSequence != null 
			&& interactSequence.getTheText().getButtonText().equals("Open")
			&& interactSequence.getTheText().isEnd()
			&& interactSequence.isDisplayOn()) {
			open();
		}
			
		// Close gate?
		if(open 
			&& interactSequence != null 
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
		if(hasBeenOpened.isCompleted() || (player.getCurrentPlayer() != null && player.getCurrentPlayer().getPlayerInventory().hasKey(keyName))) {
			
			// Play sound
			sound s = new sound(openGate);
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
