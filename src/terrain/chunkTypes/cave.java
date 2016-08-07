package terrain.chunkTypes;

import terrain.chunk;
import terrain.generalChunkType;
import terrain.groundTile;
import zones.zone;

public class cave extends groundTile {
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default name.
	private static String DEFAULT_CHUNK_NAME = "cave";
	
	// KNIGHT sprite stuff.
	private static String DEFAULT_CHUNK_SPRITESHEET = "images/terrain/cave.png";
	
	// Dimensions
	public static int DEFAULT_CHUNK_WIDTH = 32;
	public static int DEFAULT_CHUNK_HEIGHT = 32;
	
	// The actual type.
	private static generalChunkType typeReference = new generalChunkType(DEFAULT_CHUNK_NAME, DEFAULT_CHUNK_SPRITESHEET, DEFAULT_CHUNK_WIDTH, DEFAULT_CHUNK_HEIGHT);  
	
	// Create function
	public static chunk createChunk(int newX, int newY) {
		if(!zone.loadedOnce) {
			chunk t = new cave(newX,newY);
			t.setReloadObject(false);
			return t;
		}
		else {
			return null;
		}
	}
	
	///////////////
	/// METHODS ///
	///////////////
	// Constructor
	public cave(int newX, int newY) {
		super(typeReference, newX, newY,0);
		setPassable(false);
	}
	
	// Constructor
	public cave(int newX, int newY, int n) {
		super(typeReference, newX, newY,n);
		setPassable(false);
	}
}
