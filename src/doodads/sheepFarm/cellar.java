package doodads.sheepFarm;

import interactions.interactBox;
import interactions.textSeries;
import modes.mode;
import terrain.chunk;
import terrain.generalChunkType;

public class cellar extends chunk {
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default name.
	private static String DEFAULT_CHUNK_NAME = "cellar";
	
	// Sprite stuff.
	private static String DEFAULT_CHUNK_SPRITESHEET = "images/doodads/farmLand/sheepFarm/"+ DEFAULT_CHUNK_NAME + ".png";
	
	// Sound
	private String eating = "sounds/effects/doodads/eating.wav";
	public static String clearBush = "sounds/effects/doodads/bush.wav";
	
	// Bushtype
	private int bushType = 0;
	
	// Dimensions
	private static int DEFAULT_CHUNK_WIDTH = 36;
	private static int DEFAULT_CHUNK_HEIGHT = 70;
	
	// The actual type.
	private static generalChunkType typeReference = new generalChunkType(DEFAULT_CHUNK_NAME, DEFAULT_CHUNK_SPRITESHEET, DEFAULT_CHUNK_WIDTH, DEFAULT_CHUNK_HEIGHT);
	
	////////////
	// FIELDS //
	////////////
	private interactBox interactSequence;
	
	///////////////
	/// METHODS ///
	///////////////
	// Constructor
	public cellar(int newX, int newY, int i) {
		super(typeReference, newX, newY, i, 0);
		bushType = i;
		if(mode.getCurrentMode().equals("topDown")) {
			setWidth(DEFAULT_CHUNK_WIDTH);
			setHeight(DEFAULT_CHUNK_HEIGHT);
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
		startOfConversation = new textSeries(null, "It's a cellar.");
		startOfConversation.setEnd();
			
		return new interactBox(startOfConversation, this);
	}
	
	// Update
	@Override
	public void update() {
	}
	
	// Interact with object. Should be over-ridden.
	public void interactWith() { 
		interactSequence = makeNormalInteractSequence();
		interactSequence.toggleDisplay();
	}
}
