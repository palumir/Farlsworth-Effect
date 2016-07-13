package doodads.sheepFarm;


import interactions.interactBox;
import interactions.textSeries;
import modes.mode;
import terrain.chunk;
import terrain.generalChunkType;
import units.player;
import utilities.utility;

public class bone extends chunk {
	
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
	
	// RandomNum
	private int randomNum;
	
	// Interactsequence.
	private interactBox interactSequence;
	
	// The actual type.
	private static generalChunkType typeReference = new generalChunkType(DEFAULT_CHUNK_NAME, DEFAULT_CHUNK_SPRITESHEET, DEFAULT_CHUNK_WIDTH, DEFAULT_CHUNK_HEIGHT); 
	
	///////////////
	/// METHODS ///
	///////////////
	// Constructor
	public bone(int newX, int newY, int i) {
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
		// Do a random interaction.
		randomNum = utility.RNG.nextInt(6);
		setInteractable(true);
		setPassable(true);
	}
	
	// Create interact sequence
		public interactBox makeNormalInteractSequence() {
			
			// Placeholder for each individual textSeries.
			textSeries s;
						
			// Start of conversation.
			textSeries startOfConversation = null;
			
				
			if(randomNum == 0) {
				startOfConversation = new textSeries(null, "Bone appetit.");
				startOfConversation.setEnd();
			}
			
			if(randomNum == 1) {
				startOfConversation = new textSeries(null, "This guy met a ruff end.");
				startOfConversation.setEnd();
			}
			
			if(randomNum == 2) {
				startOfConversation = new textSeries(null, "This dude must have barked up the wrong tree.");
				startOfConversation.setEnd();
			}
			
			if(randomNum == 3) {
				startOfConversation = new textSeries(null, "A bone. It's bone dry.");
				startOfConversation.setEnd();
			}
			
			if(randomNum == 4) {
				startOfConversation = new textSeries(null, "Some bones. That's about it. It's nothing humerus.");
				startOfConversation.setEnd();
			}
			
			if(randomNum == 5) {
				startOfConversation = new textSeries(null, "What are you ... Sherlock Bones?");
				startOfConversation.setEnd();
			}
			
			return new interactBox(startOfConversation, "Gate");
		}
		
		// Interact with object. Should be over-ridden.
		public void interactWith() { 
			interactSequence = makeNormalInteractSequence();
			interactSequence.toggleDisplay();
		}
}
