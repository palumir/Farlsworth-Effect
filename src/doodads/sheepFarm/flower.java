package doodads.sheepFarm;

import interactions.interactBox;
import interactions.textSeries;
import modes.mode;
import sounds.sound;
import terrain.chunk;
import terrain.generalChunkType;

public class flower extends chunk {
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default name.
	private static String DEFAULT_CHUNK_NAME = "flower";
	
	// Sprite stuff.
	private static String DEFAULT_CHUNK_SPRITESHEET = "images/doodads/farmLand/sheepFarm/flower.png";
	
	// Dimensions
	public static int DEFAULT_SPRITE_WIDTH = 18;
	public static int DEFAULT_SPRITE_HEIGHT = 17;

	// Topdown
	private static int DEFAULT_TOPDOWN_ADJUSTMENT_Y = 0;
	private static int DEFAULT_TOPDOWN_WIDTH = DEFAULT_SPRITE_WIDTH;
	private static int DEFAULT_TOPDOWN_HEIGHT = DEFAULT_SPRITE_HEIGHT;

	// Platformer.
	private static int DEFAULT_PLATFORMER_ADJUSTMENT_Y = 0;
	private static int DEFAULT_PLATFORMER_HEIGHT = DEFAULT_SPRITE_WIDTH;
	private static int DEFAULT_PLATFORMER_WIDTH = DEFAULT_SPRITE_HEIGHT;
	
	// Flower type.
	private int flowerType = 0;
	
	// The actual type.
	private static generalChunkType typeReference = new generalChunkType(DEFAULT_CHUNK_NAME, DEFAULT_CHUNK_SPRITESHEET, DEFAULT_SPRITE_WIDTH, DEFAULT_SPRITE_HEIGHT);  
	
	////////////////
	/// FIELDS /////
	////////////////
	
	// Sequence
	private interactBox interactSequence;
	
	///////////////
	/// METHODS ///
	///////////////
	
	// Update
	@Override
	public void update() {
	}
	
	// Constructor
	public flower(int newX, int newY, int i) {
		super(typeReference, newX, newY, i, 0);
		if(mode.getCurrentMode().equals("topDown")) {
			setHitBoxAdjustmentY(DEFAULT_TOPDOWN_ADJUSTMENT_Y);
			setWidth(DEFAULT_TOPDOWN_WIDTH);
			setHeight(DEFAULT_TOPDOWN_HEIGHT);
		}
		else {
			setHitBoxAdjustmentY(DEFAULT_PLATFORMER_ADJUSTMENT_Y);
			setHeight(DEFAULT_PLATFORMER_HEIGHT);
			setWidth(DEFAULT_PLATFORMER_WIDTH);
		}
		
		// Set fields.
		flowerType = i;
		setPassable(true);
		backgroundDoodad = true;
	}
}
