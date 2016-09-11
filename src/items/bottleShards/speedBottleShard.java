package items.bottleShards;

import java.awt.Point;
import java.awt.image.BufferedImage;

import UI.tooltipString;
import drawing.spriteSheet;
import drawing.spriteSheet.spriteSheetInfo;
import effects.effectTypes.savePoint;
import items.bottle;
import items.bottleShard;
import items.item;
import sounds.sound;
import units.player;
import utilities.saveState;

public class speedBottleShard extends bottleShard {
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Bottle name
	public static String DEFAULT_BOTTLE_NAME = "Speed Bottle Shard";
	
	//////////////
	/// FIELDS ///
	//////////////
	public static spriteSheet bottleSpriteSheetRef = new spriteSheet(new spriteSheetInfo(
			"images/doodads/items/bottleShard.png", 
			bottleShard.DEFAULT_SPRITE_WIDTH, 
			bottleShard.DEFAULT_SPRITE_HEIGHT,
			0,
			0
			));
	
	public static bottleShard itemRef;
	
	public int shardNumber;
	
	///////////////
	/// METHODS ///
	///////////////
	
	// On floor.
	public speedBottleShard(int x, int y) {
		super(DEFAULT_BOTTLE_NAME,x,y);
		
		// Weapon stats.
		setStats();
	}
	
	// Set stats
	public void setStats() {
		
		// Rarity
		rarity = "Rare";
	}
	
	// React to being picked up.
	@Override
	public void reactToPickup() {
	}

	// Get the item ground image.
	public BufferedImage getImage() {
		return bottleSpriteSheetRef.getSprite(0, 0);
	}

}