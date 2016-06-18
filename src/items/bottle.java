package items;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import drawing.spriteSheet;
import sounds.sound;
import units.player;

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
	public static float DEFAULT_BOTTLE_DRINK_VOLUME = 0.7f;
	
	//////////////
	/// FIELDS ///
	//////////////
	
	// Bottle charges.
	private int chargesLeft = 0;
	private int maxCharges = 1;
	
	// Heal percent.
	private float healPercent = 0;
	
	// Bottle sheet.
	protected static spriteSheet bottleSpriteSheet = null;
	
	// Sound.
	private sound bottleDrink = new sound("sounds/effects/player/UI/bottleDrink.wav");
	
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
		
		// Break up the spriteSheet. Assumed to be regular human character size, for now.
		if(player.getCurrentPlayer()!=null) {
			if(player.getCurrentPlayer().getPlayerInventory().hasItem(this)) {
				setDrawObject(false);
			}
		}
		else setDrawObject(true);
		inInventory = false;
	}
	
	// Equip item
	public void equip() {
		
		// Equip the weapon.
		player.getCurrentPlayer().setEquippedBottle(this);
		
		// Change the player's stats based on the weapon's strength and their
		// level.
		
	}
	
	// Use charge.
	public void useCharge() {
		player currPlayer = player.getCurrentPlayer();
		if(getChargesLeft() > 0) {
			bottleDrink.playSound(DEFAULT_BOTTLE_DRINK_VOLUME);
			setChargesLeft(getChargesLeft() - 1);
			int healHp = (int) (getHealPercent()*currPlayer.getMaxHealthPoints());
			currPlayer.heal(healHp);
		}
	}
	
	// Update.
	@Override
	public void update() {
		if(this.isDrawObject() && this.collides(this.getX(), this.getY(), player.getCurrentPlayer())) {
			pickUp();
		}
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

	public float getHealPercent() {
		return healPercent;
	}

	public void setHealPercent(float healPercent) {
		this.healPercent = healPercent;
	}
}