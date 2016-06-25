package items.keys;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import drawing.spriteSheet;
import drawing.animation.animation;
import drawing.animation.animationPack;
import drawing.spriteSheet.spriteSheetInfo;
import drawing.userInterface.tooltipString;
import items.key;
import items.item;
import items.key;
import items.weapon;
import units.player;
import utilities.saveState;

public class sheepKey extends key {
	////////////////
	/// DEFAULTS ///
	////////////////
	// Bottle name
	public static String DEFAULT_KEY_NAME = "Sheep Key";
	
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
	
	public static key keyRef;
	
	///////////////
	/// METHODS ///
	///////////////
	
	// In inventory.
	public sheepKey() {
		super(DEFAULT_KEY_NAME);
	}
	
	// On floor.
	public sheepKey(int x, int y) {
		super(DEFAULT_KEY_NAME,x,y);
	}
	
	// React to being picked up.
	@Override
	public void reactToPickup() {
		player currPlayer = player.getCurrentPlayer();
		if(currPlayer != null) {
			tooltipString t = new tooltipString("Press 'i' to open inventory.");
		}
	}

	// Get the item ground image.
	public BufferedImage getImage() {
		return keySpriteSheetRef.getSprite(0, 0); // Full key.
	}
	
	// Get weapon reference.
	public static key getBottle() {
		return keyRef;
	}
}