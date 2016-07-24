package terrain;

// Something to differentiate doodads from simple tiles.
public class groundTile extends chunk {

	// Constructor
	public groundTile(chunkType c, int newX, int newY) {
		super(c, newX, newY);
		setImportantEnoughToReload(false);
	}
	
	// Constructor
	public groundTile(chunkType c, int newX, int newY, int i) {
		super(c, newX, newY, i, 0);
		setImportantEnoughToReload(false);
	}
	
}