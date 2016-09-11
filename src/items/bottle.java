package items;

import UI.tooltipString;
import drawing.spriteSheet;
import interactions.event;
import sounds.sound;
import units.player;
import utilities.saveState;

public abstract class bottle extends item {
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default sprite stuff
	public static int DEFAULT_SPRITE_WIDTH = 20;
	public static int DEFAULT_SPRITE_HEIGHT = 29;
	protected static int DEFAULT_SPRITE_ADJUSTMENT_X = 0;
	protected static int DEFAULT_SPRITE_ADJUSTMENT_Y = 0;
	
	// Sounds
	public static float DEFAULT_BOTTLE_DRINK_VOLUME = 1f;
	
	//////////////
	/// FIELDS ///
	//////////////
	
	// Bottle charges.
	private int chargesLeft = 0;
	private int maxCharges = 1;
	
	// Bottle sheet.
	protected static spriteSheet bottleSpriteSheet = null;
	
	// Sound.
	public static String bottleDrink = "sounds/effects/player/UI/bottleDrink.wav";
	
	// Does the player actually own the item?
	public static boolean inInventory = false;
	
	///////////////
	/// METHODS ///
	///////////////
	
	// For weapon being in your inventory.
	public bottle(String newName) {
		super(newName,null,0,0,0,0);
		
		// It is, of course, equippable.
		equippable = true;
		
		// Break up the spriteSheet. Assumed to be regular human character size, for now.
		setDrawObject(false);
		inInventory = false;
	}
	
	// For weapon being in your floor
	public bottle(String newName, int x, int y) {
		super(newName,null,x,y,0,0);
		
		// Set the width and height.
		setWidth(getImage().getWidth());
		setHeight(getImage().getHeight());
		
		// It is, of course, equippable.
		equippable = true;
		inInventory = false;
	}
	
	// Use charge.
	public void useCharge() {
	}
	
	// Use
	@Override
	public void use() {
		useCharge();
	}
	
	// Refill
	public void refill() {
		setChargesLeft(getMaxCharges());
	}

	public int getChargesLeft() {
		return chargesLeft;
	}

	public void setChargesLeft(int chargesLeft) {
		this.chargesLeft = chargesLeft;
	}

	public int getMaxCharges() {
		return maxCharges;
	}

	public void setMaxCharges(int maxCharges) {
		this.maxCharges = maxCharges;
	}

}