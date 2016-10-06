package items.bottleShards;

import java.awt.image.BufferedImage;

import drawing.spriteSheet;
import drawing.spriteSheet.spriteSheetInfo;
import items.bottle;
import items.bottleShard;
import items.item;
import items.bottles.jumpBottle;
import units.player;

public class jumpBottleShard extends bottleShard {
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Bottle name
	public static String DEFAULT_BOTTLE_NAME = "Jump Bottle Shard";
	
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
	public jumpBottleShard(int x, int y) {
		super(DEFAULT_BOTTLE_NAME,x,y);
		
		// Weapon stats.
		setStats();
		
		createGlow();
	}
	
	// Set stats
	public void setStats() {
		
		// Rarity
		quality = "Alright";
	}
	
	// Get bottle type
	public Class getBottleType() {
		return jumpBottle.class;
	}
	
	// Set bottle
	public void buildBottle() {
		
		// Remove all shards from inventory.
		player currPlayer = player.getPlayer();
		for(int i = 0; i < currPlayer.getPlayerInventory().size(); i++) {
			item currItem = currPlayer.getPlayerInventory().get(i);
			if(currItem instanceof jumpBottleShard) {
				currPlayer.getPlayerInventory().remove(i);
				i--;
			}
		}
		
		// Give new saveBottle.
		bottle b = new jumpBottle(0,0);
		b.pickUp();
		
	}

	// Get the item ground image.
	public BufferedImage getImage() {
		return bottleSpriteSheetRef.getSprite(0, 0);
	}

}