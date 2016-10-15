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
		setKeyName(newKeyName);
		
		// Event for gate open.
		isOpen = event.createEvent("openable" + newKeyName + newX + newY + "isOpen");
		hasBeenOpened = event.createEvent("openable" + newKeyName + newX + newY + "hasBeenOpened");
		
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
			
		if(!isOpen()) {
			startOfConversation = new textSeries("StartWithButtons", "StartWithButtons");
			startOfConversation.setTalker("");
			
			if((player.getPlayer() != null && player.getPlayer().getPlayerInventory().hasKey(getKeyName())) || hasBeenOpened.isCompleted() || keyName==null) {
				s = startOfConversation.addChild("Open", "You open it.");	
			}
			else {
				s = startOfConversation.addChild("Open", "It's locked.");
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
		if(!isOpen() 
			&& interactSequence != null 
			&& interactSequence.getTextSeries().getButtonText() != null
			&& interactSequence.getTextSeries().getButtonText().equals("Open")
			&& interactSequence.getTextSeries().isEnd()
			&& interactSequence.isDisplayOn()) {
			open();
		}
			
		// Close gate?
		if(isOpen() 
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
		if(hasBeenOpened.isCompleted() || keyName == null || (player.getPlayer() != null && player.getPlayer().getPlayerInventory().hasKey(getKeyName()))) {
			
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
		setOpen(true);
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
		setOpen(true);
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
		setOpen(false);
	}
	
	public abstract generalChunkType getTypeReference();

	public String getKeyName() {
		return keyName;
	}

	public void setKeyName(String keyName) {
		this.keyName = keyName;
	}

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}
}
