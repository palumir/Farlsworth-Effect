package items.bottles;

import java.awt.image.BufferedImage;

import drawing.spriteSheet;
import drawing.spriteSheet.spriteSheetInfo;
import effects.buff;
import effects.buffs.jumpBottleBuff;
import items.bottle;
import modes.mode;
import sounds.sound;
import units.player;

public class jumpBottle extends bottle {
	////////////////
	/// DEFAULTS ///
	////////////////
	// Bottle name
	public static String DEFAULT_BOTTLE_NAME = "Jump Bottle";
	
	// Bottle stats.
	public static int DEFAULT_MAX_CHARGES = 3;
	
	// If bottle is in inventory., this is it.
	public static jumpBottle bottleRef;
	
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
	public jumpBottle(int x, int y) {
		super(DEFAULT_BOTTLE_NAME,x,y);
		
		bottleRef = this;
		
		// Weapon stats.
		setStats();
	}
	
	// Set stats
	public void setStats() {
		
		// Rarity
		rarity = "Rare";
		description = "Double jump!";
		
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
		
		if(mode.getCurrentMode().equals("platformer")) {
			buff b = new jumpBottleBuff(player.getPlayer());
		}
		if(mode.getCurrentMode().equals("topDown")) {
			player.getPlayer().touchDown();
			player.getPlayer().setFallSpeed(-player.getPlayer().getJumpSpeed());
		}
	}
	
	// React to being picked up.
	@Override
	public void reactToPickup() {
		player currPlayer = player.getPlayer();
		if(currPlayer != null) {
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