package items;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import drawing.spriteSheet;
import units.player;

public abstract class weapon extends item {
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default sprite stuff
	public static int DEFAULT_SPRITE_WIDTH = 64;
	public static int DEFAULT_SPRITE_HEIGHT = 64;
	protected static int DEFAULT_SPRITE_ADJUSTMENT_X = 0;
	protected static int DEFAULT_SPRITE_ADJUSTMENT_Y = 6;
	
	//////////////
	/// FIELDS ///
	//////////////
	
	// The actual animation
	protected spriteSheet weaponSpriteSheet;
	
	// Does the player actually own the item?
	public static boolean inInventory = false;
	
	// Weapon stats
	protected int attackDamage = 0;
	protected float attackTime = 0f;
	protected float baseAttackTime = 0f;
	protected int attackWidth = 0;
	protected int attackLength = 0;
	protected String range = "short";
	protected String speed = "fast";
	
	///////////////
	/// METHODS ///
	///////////////
	
	// For weapon being in your inventory.
	public weapon(String newName, spriteSheet newSpriteSheet) {
		super(newName, null,0,0,0,0);
		
		// It is, of course, equippable.
		equippable = true;
		
		// Break up the spriteSheet. Assumed to be regular human character size, for now.
		setDrawObject(false);
		inInventory = false;
		weaponSpriteSheet = newSpriteSheet;
	}
	
	// For weapon being in your floor
	public weapon(String newName, int x, int y) {
		super(newName, null,x,y,0,0);
		
		// Set the width and height.
		width = getImage().getWidth();
		height = getImage().getHeight();
		
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
	
	// Update.
	@Override
	public void update() {
		if(this.isDrawObject() && this.collides(this.getX(), this.getY(), player.getCurrentPlayer())) {
			pickUp();
		}
	}
	
	// Draw the weapon.
	@Override
	public void drawObject(Graphics g) {
		g.drawImage(getImage(), 
				drawX, 
				drawY, 
				getImage().getWidth(), 
				getImage().getHeight(), 
				null);
	}
}