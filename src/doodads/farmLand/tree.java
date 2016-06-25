package doodads.farmLand;

import java.util.Random;

import drawing.camera;
import drawing.gameCanvas;
import modes.mode;
import terrain.chunk;
import terrain.chunkType;
import terrain.generalChunkType;
import units.humanType;
import units.unit;
import units.unitType;
import utilities.time;
import zones.zone;

public class tree extends chunk {
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default name.
	private static String DEFAULT_CHUNK_NAME = "tree";
	
	// Sprite stuff.
	private static String DEFAULT_CHUNK_SPRITESHEET = "images/doodads/farmLand/"+ DEFAULT_CHUNK_NAME + ".png";
	
	// Dimensions
	public static int DEFAULT_CHUNK_WIDTH = 125;
	public static int DEFAULT_CHUNK_HEIGHT = 133;
	
	// The actual type.
	private static generalChunkType typeReference = new generalChunkType(DEFAULT_CHUNK_NAME, DEFAULT_CHUNK_SPRITESHEET, DEFAULT_CHUNK_WIDTH, DEFAULT_CHUNK_HEIGHT);  
	
	///////////////
	/// METHODS ///
	///////////////
	// Constructor
	public tree(int newX, int newY, int i) {
		super(typeReference, newX, newY, i, 0);
		if(mode.getCurrentMode().equals("topDown")) {
			setHitBoxAdjustmentY(58);
			setWidth(30);
			setHeight(13);
		}
		else {
			setHitBoxAdjustmentY(0);
			setHeight(DEFAULT_CHUNK_HEIGHT);
			setWidth(DEFAULT_CHUNK_WIDTH);
		}
		setPassable(false);
	}
}