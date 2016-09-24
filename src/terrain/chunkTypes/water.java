package terrain.chunkTypes;

import terrain.chunk;
import terrain.generalChunkType;
import terrain.groundTile;
import utilities.utility;
import zones.zone;

public class water extends groundTile {
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default name.
	private static String DEFAULT_CHUNK_NAME = "Water";
	
	// Tile sprite stuff
	private static String DEFAULT_CHUNK_SPRITESHEET = "images/terrain/water.png";
	
	// Dimensions
	public static int DEFAULT_CHUNK_WIDTH = 32;
	public static int DEFAULT_CHUNK_HEIGHT = 32;
	
	// The actual type.
	private static generalChunkType typeReference = new generalChunkType(DEFAULT_CHUNK_NAME, DEFAULT_CHUNK_SPRITESHEET, DEFAULT_CHUNK_WIDTH, DEFAULT_CHUNK_HEIGHT); 
	
	///////////////
	/// METHODS ///
	///////////////
	
	// A tile of water.
	public water(int newX, int newY) {
		super(typeReference, newX, newY,utility.RNG.nextInt(typeReference.getChunkTypeSpriteSheet().getAnimation(0).size()));
		this.setPassable(false);
		backgroundDoodad = true;
	}
	
	// A tile of water. Dumby constructor for adding water.
	public water(int newX, int newY, int n) {
		super(typeReference, newX, newY,utility.RNG.nextInt(typeReference.getChunkTypeSpriteSheet().getAnimation(0).size()));
		this.setPassable(false);
		backgroundDoodad = true;
	}
	
	// Create function
	public static chunk createChunk(int newX, int newY) {
		if(!zone.loadedOnce) {
			chunk t = new water(newX,newY,utility.RNG.nextInt(typeReference.getChunkTypeSpriteSheet().getAnimation(0).size()));
			t.setReloadObject(false);
			return t;
		}
		else {
			return null;
		}
	}
}
