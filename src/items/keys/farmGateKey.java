package items.keys;

import java.awt.image.BufferedImage;

import drawing.spriteSheet;
import drawing.spriteSheet.spriteSheetInfo;
import items.key;

public class farmGateKey extends key {
	////////////////
	/// DEFAULTS ///
	////////////////
	// Bottle name
	public static String DEFAULT_KEY_NAME = "Not TV Key";
	
	//////////////
	/// FIELDS ///
	//////////////
	public static spriteSheet keySpriteSheetRef = new spriteSheet(new spriteSheetInfo(
			"images/doodads/items/key.png", 
			key.DEFAULT_SPRITE_WIDTH, 
			key.DEFAULT_SPRITE_HEIGHT,
			key.DEFAULT_SPRITE_ADJUSTMENT_X,
			key.DEFAULT_SPRITE_ADJUSTMENT_Y
			));
	
	///////////////
	/// METHODS ///
	///////////////
	
	// On floor.
	public farmGateKey(int x, int y) {
		super(DEFAULT_KEY_NAME,x,y);
		setStats();
	}
	
	public void setStats() {
		quality = "Alright";
	}
	
	// React to being picked up.
	@Override
	public void reactToPickup() {
	}

	// Get the item ground image.
	public BufferedImage getImage() {
		return keySpriteSheetRef.getSprite(0, 0); // Full key.
	}
}