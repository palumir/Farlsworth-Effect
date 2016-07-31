package doodads.sheepFarm;

import interactions.event;
import interactions.interactBox;
import interactions.textSeries;
import modes.mode;
import sounds.sound;
import terrain.chunk;
import terrain.generalChunkType;
import units.player;
import units.bosses.farlsworth;
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
	private int interactTimes = 0;
	private boolean screamingJoke = false;
	
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
				
				// Screaming joke
				if(screamingJoke) {
					s = startOfConversation.addChild(null, "HOLY SHNIKIES!! SOMEBODY HELP!!!");
					s.setTalker("Talking Gate");
					s.setEnd();
				}
				// Farlsworth gate joke
				else if(isForestGateAndUnopenable() && !talkedToForestGateOnce) {
					
					if(interactTimes == 0) {
						s = startOfConversation.addChild("Open", "No.");
						s.setTalker("Talking Gate");
						s = s.addChild(null, "That's a pretty rude way to ask, don't you think?");
						s.setTalker("Talking Gate");
						s.setEnd();
						interactTimes++;
					}
					else if(interactTimes == 1) {
						s = startOfConversation.addChild("Please open?", "No.");
						s.setTalker("Talking Gate");
						s = s.addChild(null, "That was way more polite though, thanks.");
						s.setTalker("Talking Gate");
						s.setEnd();
						interactTimes++;
					}
					else if(interactTimes == 2) {
						s = startOfConversation.addChild("Are you just joshing around?", "If I was just joshing hard I would tell you.");
						s.setTalker("Talking Gate");
						s = s.addChild(null, "People usually only josh big time if they let you know.");
						s.setTalker("Talking Gate");
						s = s.addChild(null, "Or it's just a trash josh.");
						s.setTalker("Talking Gate");
						s = s.addChild(null, "Farmer just asked you to do a quest, right?");
						s.setTalker("Talking Gate");
						s = s.addChild(null, "To get Farnsgurn's wool or something like that?");
						s.setTalker("Talking Gate");
						s = s.addChild(null, "Go do it man don't commit and dip.");
						s.setTalker("Talking Gate");
						s = s.addChild(null, "Nobody likes a commit and dip.");
						s.setTalker("Talking Gate");
						s = s.addChild(null, "Or a dine and dash.");
						s.setTalker("Talking Gate");
						s = s.addChild(null, "Or a fart and dart.");
						s.setTalker("Talking Gate");
						s = s.addChild(null, "You're just going to josh yourself.");
						s.setTalker("Talking Gate");
						s.setEnd();
						interactTimes++;
					}
					else {
						s = startOfConversation.addChild("Open", "Farlsbones is in the pen to your right.");
						s.setTalker("Talking Gate");
						s = s.addChild(null, "Go get his wool man.");
						s.setTalker("Talking Gate");
						s = s.addChild(null, "Quit joshing me hard.");
						s.setTalker("Talking Gate");
						s.setEnd();
						talkedToForestGateOnce = true;
						interactTimes++;
					}
				}
				else if(isForestGateAndUnopenable() && talkedToForestGateOnce) {
					s = startOfConversation.addChild("Open", "Go get that wool.");
					s.setTalker("Talking Gate");
					s = s.addChild(null, "Frannyburns is in the pen to your right.");
					s.setTalker("Talking Gate");
					s = s.addChild(null, "I have nothing more to say.");
					s.setTalker("Talking Gate");
					s = s.addChild(null, "The gate life is a dull one.");
					s.setTalker("Talking Gate");
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
		
		return new interactBox(startOfConversation, this, isUnit);
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
		setShowInteractable(false);
		
		// Open gate.
		setPassable(true);
		chunkImage = typeReference.getChunkImage(1, 0);
		open = true;
	}
	
	// Close the gate
	public void close() {
		
		// Set that the gate is closed.
		isOpen.setCompleted(false);
		setShowInteractable(true);
		
		// Play sound
		sound s = new sound(closeGate);
		s.start();
		
		// Close the gate.
		setPassable(false);
		chunkImage = typeReference.getChunkImage(0, 0);
		open = false;
	}
}
