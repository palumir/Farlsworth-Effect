package items.bottles;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import UI.tooltipString;
import drawing.spriteSheet;
import drawing.spriteSheet.spriteSheetInfo;
import effects.effect;
import effects.effectTypes.savePoint;
import effects.interfaceEffects.floatingString;
import items.bottle;
import items.item;
import sounds.sound;
import units.player;
import utilities.saveState;
import utilities.stringUtils;

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
	// Already double jumped.
	private boolean alreadyDoubleJumped = false;
	
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
			if(!isAlreadyDoubleJumped()) {
				sound s = new sound(bottle.bottleDrink);
				s.start();
				setChargesLeft(getChargesLeft() - 1);
				doubleJump();
			}
			else {
				player currPlayer = player.getPlayer();
				effect e = new floatingString("On Cooldown", DEFAULT_DROP_COLOR, currPlayer.getIntX() + currPlayer.getWidth()/2, currPlayer.getIntY() + currPlayer.getHeight()/2, 1.3f);
				e.setAnimationDuration(3f);
			}
		}
	}
	
	// Doublejump
	public void doubleJump() {
		setAlreadyDoubleJumped(true);
		player.getPlayer().setAlreadyJumped(true);
		player.getPlayer().setJumping(true);
		player.getPlayer().setFallSpeed(-player.getPlayer().getJumpSpeed());
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

	public boolean isAlreadyDoubleJumped() {
		return alreadyDoubleJumped;
	}

	public void setAlreadyDoubleJumped(boolean alreadyDoubleJumped) {
		this.alreadyDoubleJumped = alreadyDoubleJumped;
	}
}