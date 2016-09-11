package items;

import drawing.spriteSheet;
import units.player;

public abstract class key extends item {
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default sprite stuff
	public static int DEFAULT_SPRITE_WIDTH = 20;
	public static int DEFAULT_SPRITE_HEIGHT = 11;
	protected static int DEFAULT_SPRITE_ADJUSTMENT_X = 0;
	protected static int DEFAULT_SPRITE_ADJUSTMENT_Y = 0;
	
	//////////////
	/// FIELDS ///
	//////////////
	
	// Bottle sheet.
	protected static spriteSheet keySpriteSheet = null;
	
	// Does the player actually own the item?
	public static boolean inInventory = false;
	
	///////////////
	/// METHODS ///
	///////////////
	
	// For weapon being in your inventory.
	public key(String newName) {
		super(newName,null,0,0,0,0);
		
		// It is, of course, non-equippable.
		equippable = false;
		
		// Break up the spriteSheet. Assumed to be regular human character size, for now.
		setDrawObject(false);
		inInventory = false;
	}
	
	// For weapon being in your floor
	public key(String newName, int x, int y) {
		super(newName,null,x,y,0,0);
		
		// Set the width and height.
		setWidth(getImage().getWidth());
		setHeight(getImage().getHeight());
		
		// It is, of course, non-equippable.
		equippable = false;
		inInventory = false;
	}
	
	// Equip item
	public void equip() {
		// Do nothing.
	}
	
	// Update.
	@Override
	public void update() {
		if(this.isDrawObject() && this.collides(this.getIntX(), this.getIntY(), player.getPlayer())) {
			pickUp();
		}
	}

}