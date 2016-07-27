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

public class farmKey extends key {
	////////////////
	/// DEFAULTS ///
	////////////////
	// Bottle name
	public static String DEFAULT_KEY_NAME = "Farm Key";
	
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
	public farmKey() {
		super(DEFAULT_KEY_NAME);
	}
	
	// Get item ref.
	public item getItemRef() {
		return keyRef;
	}
	
	// On floor.
	public farmKey(int x, int y) {
		super(DEFAULT_KEY_NAME,x,y);
	}
	
	// React to being picked up.
	@Override
	public void reactToPickup() {
		player currPlayer = player.getPlayer();
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