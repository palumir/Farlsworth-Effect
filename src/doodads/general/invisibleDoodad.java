package doodads.general;

import modes.mode;
import terrain.chunk;
import terrain.generalChunkType;

public class invisibleDoodad extends chunk {
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default name.
	private static String DEFAULT_CHUNK_NAME = "Invisible Doodad";
	
	// Dimensions
	private static int DEFAULT_SPRITE_WIDTH = 33;
	private static int DEFAULT_SPRITE_HEIGHT = 29;

	// Topdown
	private static int DEFAULT_TOPDOWN_ADJUSTMENT_Y = 0;
	private static int DEFAULT_TOPDOWN_WIDTH = 33;
	private static int DEFAULT_TOPDOWN_HEIGHT = 29;

	// Platformer.
	private static int DEFAULT_PLATFORMER_ADJUSTMENT_Y = 0;
	private static int DEFAULT_PLATFORMER_HEIGHT = 33;
	private static int DEFAULT_PLATFORMER_WIDTH = 29;
	
	// The actual type.
	private static generalChunkType typeReference = new generalChunkType(DEFAULT_CHUNK_NAME, DEFAULT_SPRITE_WIDTH, DEFAULT_SPRITE_HEIGHT);  
	
	
	// Update
	@Override
	public void update() {
	}
	
	
	// Constructor
	public invisibleDoodad(int newX, int newY) {
		super(typeReference, newX, newY, 0, 0);
		showHitBox();
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
		
		// Passable.
		setPassable(false);
		
		// Don't draw object
		setDrawObject(false);
	}
}
