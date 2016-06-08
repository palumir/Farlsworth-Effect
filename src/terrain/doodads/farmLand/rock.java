package terrain.doodads.farmLand;

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

public class rock extends chunk {
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default name.
	private static String DEFAULT_CHUNK_NAME = "rock";
	
	// Sprite stuff.
	private static String DEFAULT_CHUNK_SPRITESHEET = "images/doodads/farmLand/"+ DEFAULT_CHUNK_NAME + ".png";
	
	// Dimensions
	private static int DEFAULT_CHUNK_WIDTH = 32;
	private static int DEFAULT_CHUNK_HEIGHT = 22;
	
	// The actual type.
	private static generalChunkType typeReference = new generalChunkType(DEFAULT_CHUNK_NAME, DEFAULT_CHUNK_SPRITESHEET, DEFAULT_CHUNK_WIDTH, DEFAULT_CHUNK_HEIGHT); 
	
	///////////////
	/// METHODS ///
	///////////////
	// Constructor
	public rock(int newX, int newY, int i) {
		super(typeReference, newX, newY, i, 0);
		setPassable(false);
		if(mode.getCurrentMode().equals("topDown")) {
			setHitBoxAdjustmentY(0);
			height = DEFAULT_CHUNK_HEIGHT;
			width = DEFAULT_CHUNK_WIDTH;
		}
		else {
			setHitBoxAdjustmentY(0);
			height = DEFAULT_CHUNK_HEIGHT;
			width = DEFAULT_CHUNK_WIDTH;
		}
	}
}
