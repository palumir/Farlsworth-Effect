package doodads.cave;


import interactions.interactBox;
import interactions.textSeries;
import modes.mode;
import terrain.chunk;
import terrain.generalChunkType;

public class caveBone extends chunk {
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default name.
	private static String DEFAULT_CHUNK_NAME = "bone";
	
	// Sprite stuff.
	private static String DEFAULT_CHUNK_SPRITESHEET = "images/doodads/farmLand/sheepFarm/" + DEFAULT_CHUNK_NAME + ".png";
	
	// Dimensions
	public static int DEFAULT_CHUNK_WIDTH = 32;
	public static int DEFAULT_CHUNK_HEIGHT = 32;
	
	// Interactsequence.
	private interactBox interactSequence;
	
	// The actual type.
	private static generalChunkType typeReference = new generalChunkType(DEFAULT_CHUNK_NAME, DEFAULT_CHUNK_SPRITESHEET, DEFAULT_CHUNK_WIDTH, DEFAULT_CHUNK_HEIGHT); 
	
	///////////////
	/// METHODS ///
	///////////////
	// Constructor
	public caveBone(int newX, int newY, int i) {
		super(typeReference, newX, newY, i, 0);
		if(mode.getCurrentMode().equals("topDown")) {
			setHitBoxAdjustmentY(0);
			setWidth(28);
			setHeight(5);
		}
		else {
			setHitBoxAdjustmentY(0);
			setHeight(DEFAULT_CHUNK_HEIGHT);
			setWidth(DEFAULT_CHUNK_WIDTH);
		}
		setInteractable(true);
		setPassable(true);
	}
	
	// Create interact sequence
		public interactBox makeNormalInteractSequence() {
			
			// Placeholder for each individual textSeries.
			textSeries s;
						
			// Start of conversation.
			textSeries startOfConversation = null;
			
			// TODO:
			startOfConversation = new textSeries(null, "Looks like these people couldn't find their way out.");
			startOfConversation.setEnd();
			
			return new interactBox(startOfConversation, this);
		}
		
		// Interact with object. Should be over-ridden.
		public void interactWith() { 
			interactSequence = makeNormalInteractSequence();
			interactSequence.toggleDisplay();
		}
}
