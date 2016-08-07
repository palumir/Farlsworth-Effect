package terrain;

// Something to differentiate doodads from simple tiles.
public class groundTile extends chunk {
	
	// Width and height
	public static int DEFAULT_TILE_WIDTH = 32;
	public static int DEFAULT_TILE_HEIGHT = 32;

	// Constructor
	public groundTile(chunkType c, int newX, int newY, int i) {
		super(c, newX, newY, i, 0);
	}
	
}