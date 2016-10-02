package doodads;

import interactions.event;
import interactions.interactBox;
import interactions.textSeries;
import sounds.sound;
import terrain.chunk;
import terrain.generalChunkType;
import units.player;

public abstract class openable extends chunk {
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Sequence for fences.
	private interactBox interactSequence;
	
	// Sounds
	private String openGate = "sounds/effects/doodads/openGate.wav";
	private String closeGate = "sounds/effects/doodads/closeGate.wav";
	
	// Key name.
	private String keyName;
	
	// Open or closed.
	private boolean open = false;
	
	// Event on whether or not the gate is open.
	protected event hasBeenOpened;
	protected event isOpen;

	///////////////
	/// METHODS ///
	///////////////
	// Constructor
	public openable(generalChunkType typeReference, String newKeyName, int newX, int newY) {
		super(typeReference, newX, newY, 0, 0);
		
		// Key name.
		keyName = newKeyName;
		
		// Event for gate open.
		isOpen = new event("openable" + newKeyName + newX + newY + "isOpen");
		hasBeenOpened = new event("openable" + newKeyName + newX + newY + "hasBeenOpened");
		
		// Check if we have save data on the gate.
		if(isOpen.isCompleted()) {
			forceOpen();
		}
		else {
			setPassable(false);
		}
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
				s = startOfConversation.addChild("Open", "You open it.");	
			}
			else {
				s = startOfConversation.addChild("Open", "You don't have the key.");
			}
			
			s.setEnd();
		}
		
		else {
			startOfConversation = new textSeries("StartWithButtons", "StartWithButtons");
			s = startOfConversation.addChild("Close", "You close it.");
			s.setEnd();
		}
		
		return new interactBox(startOfConversation, this);
	}
	
	// Interact stuff.
	public void doInteractStuff() {
		
		// Open gate?
		if(!open 
			&& interactSequence != null 
			&& interactSequence.getTextSeries().getButtonText() != null
			&& interactSequence.getTextSeries().getButtonText().equals("Open")
			&& interactSequence.getTextSeries().isEnd()
			&& interactSequence.isDisplayOn()) {
			open();
		}
			
		// Close gate?
		if(open 
			&& interactSequence != null 
			&& interactSequence.getTextSeries().getButtonText() != null
			&& interactSequence.getTextSeries().getButtonText().equals("Close")
			&& interactSequence.getTextSeries().isEnd()
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
	public void forceOpenWithSound() {
		
		// Set that the gate has been opened before.
		hasBeenOpened.setCompleted(true);
		isOpen.setCompleted(true);
		setShowInteractable(false);
		
		// Play sound
		sound s = new sound(openGate);
		s.setPosition(this.getIntX(), this.getIntY(), sound.DEFAULT_SOUND_RADIUS);
		s.start();
		
		// Open gate.
		setPassable(true);
		setChunkImage(getTypeReference().getChunkImage(1, 0));
		open = true;
	}
	
	// Force gate open, no matter what.
	public void forceOpen() {
		
		// Set that the gate has been opened before.
		hasBeenOpened.setCompleted(true);
		isOpen.setCompleted(true);
		setShowInteractable(false);
		
		// Open gate.
		setPassable(true);
		setChunkImage(getTypeReference().getChunkImage(1, 0));
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
		setChunkImage(getTypeReference().getChunkImage(0, 0));
		open = false;
	}
	
	public abstract generalChunkType getTypeReference();
}
