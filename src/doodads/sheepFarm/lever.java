package doodads.sheepFarm;

import interactions.interactBox;
import interactions.textSeries;
import modes.mode;
import sounds.sound;
import terrain.chunk;
import terrain.generalChunkType;

public class lever extends chunk {
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default name.
	private static String DEFAULT_CHUNK_NAME = "lever";
	
	// Sprite stuff.
	private static String DEFAULT_CHUNK_SPRITESHEET = "images/doodads/farmLand/sheepFarm/"+ DEFAULT_CHUNK_NAME + ".png";
	
	// Sounds
	private String leverPull = "sounds/effects/doodads/lever.wav";
	
	// Dimensions
	private static int DEFAULT_CHUNK_WIDTH = 26;
	private static int DEFAULT_CHUNK_HEIGHT = 25;
	
	// The actual type.
	private static generalChunkType typeReference = new generalChunkType(DEFAULT_CHUNK_NAME, DEFAULT_CHUNK_SPRITESHEET, DEFAULT_CHUNK_WIDTH, DEFAULT_CHUNK_HEIGHT);
	
	////////////
	// FIELDS //
	////////////
	private interactBox interactSequence;
	
	// On or off
	private boolean on = false;
	
	///////////////
	/// METHODS ///
	///////////////
	// Constructor
	public lever(int newX, int newY, int i) {
		super(typeReference, newX, newY, i, 0);
		if(mode.getCurrentMode().equals("topDown")) {
			setHitBoxAdjustmentY(5);
			setWidth(50);
			setHeight(15);
		}
		else {
			setHitBoxAdjustmentY(0);
			setHeight(DEFAULT_CHUNK_HEIGHT);
			setWidth(DEFAULT_CHUNK_WIDTH);
		}
		
		// Set interactable.
		setInteractable(true);
		
		// Set not passable.
		setPassable(false);
	}
	
	// Create interact sequence
	public interactBox makeNormalInteractSequence() {
		
		// Placeholder for each individual textSeries.
		textSeries s;
					
		// Start of conversation.
		textSeries startOfConversation = null;
		
		// Start of conversation.
		startOfConversation = new textSeries("Start with buttons", "A lever.");
			
		if(!on) {
			s = startOfConversation.addChild("Switch lever on", "The lever was switched on.");
			s.setEnd();
		}
		else {
			s = startOfConversation.addChild("Switch lever off", "The lever was switched off.");
			s.setEnd();
		}
		return new interactBox(startOfConversation, this);
	}
	
	// Interact stuff.
	public void doInteractStuff() {
			
		// On.
		if(!on &&
			interactSequence != null 
			&& interactSequence.getTextSeries().isEnd() 
			&& interactSequence.getTextSeries().getButtonText() != null
			&& interactSequence.getTextSeries().getButtonText().equals("Switch lever on")) {
			
			// TODO: OPEN SECRET PASSAGE.
			System.out.println("Open secret passage TODO:");
			setOn();
		}
		
		// Off.
		if(on &&
			interactSequence != null 
			&& interactSequence.getTextSeries().isEnd() 
			&& interactSequence.getTextSeries().getButtonText() != null
			&& interactSequence.getTextSeries().getButtonText().equals("Switch lever off")) {
				
			setOff();
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
	public void setOn() {
		sound s = new sound(leverPull);
		s.start();
		on = true;
		setChunkImage(typeReference.getChunkImage(1, 0));
	}
	
	// Close the gate
	public void setOff() {
		sound s = new sound(leverPull);
		s.start();
		on = false;
		setChunkImage(typeReference.getChunkImage(0, 0));
	}
}
