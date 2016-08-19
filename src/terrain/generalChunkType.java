package terrain;

import java.awt.image.BufferedImage;

import drawing.spriteSheet;
import drawing.spriteSheet.spriteSheetInfo;
import utilities.utility;

public class generalChunkType extends chunkType {
	/////////////////////////
	////// DEFAULTS /////////
	/////////////////////////
	
	// Default sprite stuff
	private static int DEFAULT_SPRITE_ADJUSTMENT_X = 0;
	private static int DEFAULT_SPRITE_ADJUSTMENT_Y = 0;
	
	///////////////
	/// METHODS ///
	///////////////
	public generalChunkType(String newName, String spriteSheetLocation, int defaultWidth, int defaultHeight) {
		super(newName, 
				new spriteSheet(new spriteSheetInfo(
				spriteSheetLocation, 
				defaultWidth, 
				defaultHeight,
				DEFAULT_SPRITE_ADJUSTMENT_X,
				DEFAULT_SPRITE_ADJUSTMENT_Y
				))
			    );
	}	
	
	public generalChunkType(String newName, int defaultWidth, int defaultHeight) {
		super(newName, 
				null
			    );
	}	
	
	/////////////////////////
	// Getters and setters //
	/////////////////////////
	
	// Returns a given chunk image from the spritefile. Usually for
	// doodads.
	public BufferedImage getChunkImage(int givenX, int givenY) {
			// For now, return a random from the first row.
			return getChunkTypeSpriteSheet().getSprite(givenX, givenY);
	}
}