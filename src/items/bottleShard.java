package items;

import UI.tooltipString;
import drawing.spriteSheet;
import interactions.event;
import items.bottleShards.jumpBottleShard;
import sounds.sound;
import units.player;
import utilities.saveState;

public abstract class bottleShard extends item {
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default sprite stuff
	public static int DEFAULT_SPRITE_WIDTH = 27;
	public static int DEFAULT_SPRITE_HEIGHT = 29;
	protected static int DEFAULT_SPRITE_ADJUSTMENT_X = 0;
	protected static int DEFAULT_SPRITE_ADJUSTMENT_Y = 0;
	
	//////////////
	/// FIELDS ///
	//////////////
	
	// Bottle sheet.
	protected static spriteSheet bottleShardSpriteSheet = null;
	
	// Does the player actually own the item?
	public static boolean inInventory = false;
	
	// Total shards
	public int totalShards = 3;
	
	///////////////
	/// METHODS ///
	///////////////
	
	// For bottle shard being in your floor
	public bottleShard(String newName, int x, int y) {
		super(newName,null,x,y,0,0);
		
		// Set the width and height.
		setWidth(getImage().getWidth());
		setHeight(getImage().getHeight());
		
		// It is, of course, equippable.
		equippable = false;
		inInventory = false;
	}
	
	// Respond to pickup
	@Override
	public void reactToPickup() {
		
		// Remove all shards from inventory.
		discoverAnimation.destroy();
		player currPlayer = player.getPlayer();
		int count = 0;
		for(int i = 0; i < currPlayer.getPlayerInventory().size(); i++) {
			item currItem = currPlayer.getPlayerInventory().get(i);
			if(currItem.getName().equals(this.getName())) {
				count++;
			}
		}
		
		if(count>=totalShards) {
			this.buildBottle();
		}
	}
	
	public abstract void buildBottle();
	
	public abstract Class getBottleType();

	// Update.
	@Override
	public void update() {
		if(this.isDrawObject() && this.collides(this.getIntX(), this.getIntY(), player.getPlayer())) {
			pickUp();
		}
	}

}