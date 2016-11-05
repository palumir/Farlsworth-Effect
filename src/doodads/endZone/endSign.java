package doodads.endZone;

import interactions.interactBox;
import interactions.textSeries;
import modes.mode;
import terrain.chunk;
import terrain.generalChunkType;

public class endSign extends chunk {
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default name.
	private static String DEFAULT_CHUNK_NAME = "sign";
	
	// Sprite stuff.
	private static String DEFAULT_CHUNK_SPRITESHEET = "images/doodads/farmLand/endZone/"+ DEFAULT_CHUNK_NAME + ".png";
	
	// Dimensions
	public static int DEFAULT_CHUNK_WIDTH = 27;
	public static int DEFAULT_CHUNK_HEIGHT = 34;
	
	// The actual type.
	private static generalChunkType typeReference = new generalChunkType(DEFAULT_CHUNK_NAME, DEFAULT_CHUNK_SPRITESHEET, DEFAULT_CHUNK_WIDTH, DEFAULT_CHUNK_HEIGHT); 
	
	////////////////
	/// FIELDS /////
	////////////////
	
	// Sequence
	private interactBox interactSequence;
	
	///////////////
	/// METHODS ///
	///////////////
	
	// Create interact sequence
	public interactBox makeNormalInteractSequence() {
		
		// Placeholder for each individual textSeries.
		textSeries s;
					
		// Start of conversation.
		textSeries startOfConversation = null;
		startOfConversation = new textSeries(null, "A grave.");
		s = startOfConversation.addChild(null, "You might need one of these soon if you're not careful.");
		s.setEnd();
		
		return new interactBox(startOfConversation, this);
	}
	
	// Interact stuff.
	public void doInteractStuff() {
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
	
	///////////////
	/// METHODS ///
	///////////////
	// Constructor
	public endSign(int newX, int newY, int i) {
		super(typeReference, newX, newY, i, 0);
		//showHitBox();
		//showUnitPosition();
		if(mode.getCurrentMode().equals("topDown")) {
			setHitBoxAdjustmentY(-10);
			setHeight(7);
		}
		else {
			setHitBoxAdjustmentY(0);
			setHeight(DEFAULT_CHUNK_HEIGHT);
			setWidth(DEFAULT_CHUNK_WIDTH);
		}
		setInteractable(true);
		setPassable(false);
	}
}
