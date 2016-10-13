package terrain.chunkTypes;

import modes.mode;
import terrain.chunk;
import terrain.generalChunkType;
import terrain.groundTile;
import utilities.utility;
import zones.zone;

public class lightWood extends groundTile {
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default name.
	private static String DEFAULT_CHUNK_NAME = "lightWood";
	
	// Tile sprite stuff
	private static String DEFAULT_CHUNK_SPRITESHEET = "images/terrain/" + DEFAULT_CHUNK_NAME + ".png";
	
	// Dimensions
	public static int DEFAULT_CHUNK_WIDTH = 32;
	public static int DEFAULT_CHUNK_HEIGHT = 32;
	
	// The actual type.
	private static generalChunkType typeReference = new generalChunkType(DEFAULT_CHUNK_NAME, DEFAULT_CHUNK_SPRITESHEET, DEFAULT_CHUNK_WIDTH, DEFAULT_CHUNK_HEIGHT);  
	
	// Create function
	public static chunk createChunk(int newX, int newY, int i) {
		if(!zone.loadedOnce) {
			chunk t = new lightWood(newX,newY, utility.RNG.nextInt(typeReference.getChunkTypeSpriteSheet().getAnimation(0).size()));
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
	public lightWood(int newX, int newY) {
		super(typeReference, newX, newY,utility.RNG.nextInt(typeReference.getChunkTypeSpriteSheet().getAnimation(0).size()));
		if(mode.getCurrentMode() == "topDown") this.setPassable(true);
		else this.setPassable(false);
	}
	
	// Constructor
	public lightWood(int newX, int newY, int n) {
		super(typeReference, newX, newY,utility.RNG.nextInt(typeReference.getChunkTypeSpriteSheet().getAnimation(0).size()));
		if(mode.getCurrentMode() == "topDown") this.setPassable(true);
		else this.setPassable(false);
	}
}
