package items.bottles;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import UI.tooltipString;
import drawing.spriteSheet;
import drawing.spriteSheet.spriteSheetInfo;
import effects.effectTypes.savePoint;
import items.bottle;
import items.item;
import sounds.sound;
import units.player;
import utilities.saveState;

public class saveBottle extends bottle {
	////////////////
	/// DEFAULTS ///
	////////////////
	// Bottle name
	public static String DEFAULT_BOTTLE_NAME = "Save Bottle";
	
	// Bottle stats.
	public static int DEFAULT_MAX_CHARGES = 3;
	
	//////////////
	/// FIELDS ///
	//////////////
	public static spriteSheet bottleSpriteSheetRef = new spriteSheet(new spriteSheetInfo(
			"images/doodads/items/saveBottle.png", 
			bottle.DEFAULT_SPRITE_WIDTH, 
			bottle.DEFAULT_SPRITE_HEIGHT,
			bottle.DEFAULT_SPRITE_ADJUSTMENT_X,
			bottle.DEFAULT_SPRITE_ADJUSTMENT_Y
			));
	
	///////////////
	/// METHODS ///
	///////////////
	
	// On floor.
	public saveBottle(int x, int y) {
		super(DEFAULT_BOTTLE_NAME,x,y);
		
		// Weapon stats.
		setStats();
	}
	
	// Set stats
	public void setStats() {
		
		// Rarity
		rarity = "Common";
		
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
			
			// Set position to be last bottle charge.
			player.getPlayer().lastSaveBottle = new Point(player.getPlayer().getIntX(), player.getPlayer().getIntY());
			
			// Put down indicator and destroy old one.
			if(player.getPlayer().lastSaveBottleChargeIndicator != null) player.getPlayer().lastSaveBottleChargeIndicator.destroy();
			savePoint.createSavePoint();
			
			// Save.
			saveState.createSaveState();
		}
	}
	
	// React to being picked up.
	@Override
	public void reactToPickup() {
		player currPlayer = player.getPlayer();
		if(currPlayer != null) {
			currPlayer.getPlayerInventory().equipItem(this, KeyEvent.VK_ENTER);
			tooltipString t = new tooltipString("Press 'enter' to use a charge of the Save Bottle.");
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