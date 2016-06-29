package items.bottles;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import drawing.spriteSheet;
import drawing.animation.animation;
import drawing.animation.animationPack;
import drawing.spriteSheet.spriteSheetInfo;
import drawing.userInterface.tooltipString;
import items.bottle;
import items.item;
import items.weapon;
import units.player;
import utilities.saveState;

public class normalBottle extends bottle {
	////////////////
	/// DEFAULTS ///
	////////////////
	// Bottle name
	public static String DEFAULT_BOTTLE_NAME = "bottle";
	
	// Bottle stats.
	public static int DEFAULT_MAX_CHARGES = 3;
	
	// Bottle heal percentage.
	public static float DEFAULT_HEAL_PERCENT = .6f;
	
	//////////////
	/// FIELDS ///
	//////////////
	public static spriteSheet bottleSpriteSheetRef = new spriteSheet(new spriteSheetInfo(
			"images/doodads/items/bottle.png", 
			bottle.DEFAULT_SPRITE_WIDTH, 
			bottle.DEFAULT_SPRITE_HEIGHT,
			bottle.DEFAULT_SPRITE_ADJUSTMENT_X,
			bottle.DEFAULT_SPRITE_ADJUSTMENT_Y
			));
	
	public static bottle bottleRef;
	
	///////////////
	/// METHODS ///
	///////////////
	
	// In inventory.
	public normalBottle() {
		super(DEFAULT_BOTTLE_NAME);
		
		// Weapon stats.
		setStats();
	}
	
	// On floor.
	public normalBottle(int x, int y) {
		super(DEFAULT_BOTTLE_NAME,x,y);
		
		// Weapon stats.
		setStats();
	}
	
	// Get item ref.
	public item getItemRef() {
		return bottleRef;
	}
	
	// Set stats
	public void setStats() {
		
		// Set item's stats
		// Bottle charges.
		setChargesLeft(3);
		setMaxCharges(DEFAULT_MAX_CHARGES);
		
		// Heal percent.
		setHealPercent(DEFAULT_HEAL_PERCENT);
	}
	
	// React to being picked up.
	@Override
	public void reactToPickup() {
		player currPlayer = player.getCurrentPlayer();
		if(currPlayer != null) {
			tooltipString t = new tooltipString("Press 'q' or 'shift' to use bottle.");
		}
	}

	// Get the item ground image.
	public BufferedImage getImage() {
		if(getChargesLeft() == 0) {
			return bottleSpriteSheetRef.getSprite(0, 0); // Empty bottle.
		}
		else if (getChargesLeft() == getMaxCharges()) {
			return bottleSpriteSheetRef.getSprite(1, 0); // Full bottle.
		}
		else if (getChargesLeft() == getMaxCharges() - 1) {
			return bottleSpriteSheetRef.getSprite(2, 0); // Full bottle.
		}
		else {
			return bottleSpriteSheetRef.getSprite(3, 0); // Full bottle.
		}
	}
	
	// Get weapon reference.
	public static bottle getBottle() {
		return bottleRef;
	}
}