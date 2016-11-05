package items.bottles;

import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import drawing.spriteSheet;
import drawing.spriteSheet.spriteSheetInfo;
import effects.buff;
import effects.buffs.pushBottleBuff;
import effects.interfaceEffects.tooltipString;
import items.bottle;
import sounds.sound;
import units.player;

public class pushBottle extends bottle {
	////////////////
	/// DEFAULTS ///
	////////////////
	// Bottle name
	public static String DEFAULT_BOTTLE_NAME = "Push Bottle";
	
	// Bottle stats.
	public static int DEFAULT_MAX_CHARGES = 3;
	
	// Jump in top Down. This might suck
	public static int DEFAULT_JUMP_DISTANCE_TOPDOWN = 150;
	
	// If bottle is in inventory., this is it.
	public static pushBottle bottleRef;
	
	//////////////
	/// FIELDS ///
	//////////////
	
	public static spriteSheet bottleSpriteSheetRef = new spriteSheet(new spriteSheetInfo(
			"images/doodads/items/jumpBottle.png", 
			bottle.DEFAULT_SPRITE_WIDTH, 
			bottle.DEFAULT_SPRITE_HEIGHT,
			bottle.DEFAULT_SPRITE_ADJUSTMENT_X,
			bottle.DEFAULT_SPRITE_ADJUSTMENT_Y
			));
	
	///////////////
	/// METHODS ///
	///////////////
	
	// On floor.
	public pushBottle(int x, int y) {
		super(DEFAULT_BOTTLE_NAME,x,y);
		
		bottleRef = this;
		
		// Weapon stats.
		setStats();
	}
	
	// Set stats
	public void setStats() {
		
		// Rarity
		quality = "Alright";
		description = "Push yourself!";
		
		// Set item's stats
		// Bottle charges.
		setChargesLeft(3);
		setMaxCharges(DEFAULT_MAX_CHARGES);
	}
	
	// Use charge.
	@Override
	public void useCharge() {
		if(getChargesLeft() > 0) {
			sound s = new sound(bottle.bottleDrink);
			s.start();
			setChargesLeft(getChargesLeft() - 1);
			doubleJump();
		}
	}
	
	// Doublejump
	public void doubleJump() {
		
		player currPlayer = player.getPlayer();
		
		// Platform
		buff b = new pushBottleBuff(player.getPlayer());
	}
	
	// React to being picked up.
	@Override
	public void reactToPickup() {
		player currPlayer = player.getPlayer();
		if(currPlayer != null) {
			currPlayer.getPlayerInventory().equipItem(this, KeyEvent.VK_SPACE);
			tooltipString t = new tooltipString("Press 'space' to push yourself with the Push Bottle.");
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
}