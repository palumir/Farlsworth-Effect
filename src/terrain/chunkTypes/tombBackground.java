package terrain.chunkTypes;

import java.util.Random;

import drawing.camera;
import modes.mode;
import terrain.chunk;
import terrain.chunkType;
import terrain.generalChunkType;
import terrain.groundTile;
import units.humanType;
import units.unit;
import units.unitType;
import utilities.time;
import zones.zone;

public class tombBackground extends groundTile {
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default name.
	private static String DEFAULT_CHUNK_NAME = "tombBackground";
	
	// KNIGHT sprite stuff.
	private static String DEFAULT_CHUNK_SPRITESHEET = "images/terrain/tombBackground.png";
	
	// Dimensions
	public static int DEFAULT_CHUNK_WIDTH = 64;
	public static int DEFAULT_CHUNK_HEIGHT = 64;
	
	// The actual type.
	private static generalChunkType typeReference = new generalChunkType(DEFAULT_CHUNK_NAME, DEFAULT_CHUNK_SPRITESHEET, DEFAULT_CHUNK_WIDTH, DEFAULT_CHUNK_HEIGHT);  
	
	///////////////
	/// METHODS ///
	///////////////
	// Constructor
	public tombBackground(int newX, int newY) {
		super(typeReference, newX, newY);
		setPassable(false);
	}
}
