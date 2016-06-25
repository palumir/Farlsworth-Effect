package doodads.farmLand;

import java.util.Random;

import drawing.camera;
import modes.mode;
import terrain.chunk;
import terrain.chunkType;
import terrain.generalChunkType;
import units.humanType;
import units.unit;
import units.unitType;
import utilities.time;
import zones.zone;

public class blackSmith extends chunk {
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default name.
	private static String DEFAULT_CHUNK_NAME = "blackSmith";
	
	// Sprite stuff.
	private static String DEFAULT_CHUNK_SPRITESHEET = "images/doodads/farmLand/"+ DEFAULT_CHUNK_NAME + ".png";
	
	// Dimensions
	private static int DEFAULT_CHUNK_WIDTH = 256;
	private static int DEFAULT_CHUNK_HEIGHT = 242;
	
	// The actual type.
	private static generalChunkType typeReference = new generalChunkType(DEFAULT_CHUNK_NAME, DEFAULT_CHUNK_SPRITESHEET, DEFAULT_CHUNK_WIDTH, DEFAULT_CHUNK_HEIGHT);  
	
	///////////////
	/// METHODS ///
	///////////////
	// Constructor
	public blackSmith(int newX, int newY, int i) {
		super(typeReference, newX, newY, i, 0);
		if(mode.getCurrentMode().equals("topDown")) {
			setHitBoxAdjustmentY(70);
			setHeight(60);
			setWidth(180);
		}
		else {
			setHitBoxAdjustmentY(0);
			setHeight(DEFAULT_CHUNK_HEIGHT);
			setWidth(DEFAULT_CHUNK_WIDTH);
		}
		setPassable(false);
	}
}
