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
	
	// Weapon stats
	protected int attackDamage = 0;
	protected float attackTime = 0f;
	protected float baseAttackTime = 0f;
	protected String attackSound = "";
	protected int attackWidth = 0;
	protected int attackLength = 0;
	protected float critChance = 0;
	protected float backSwing = 0;
	protected float critDamage = 0;
	protected float attackVariability = 0;
	private String range = "short";
	private String speed = "fast";
	
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
		setWidth(getImage().getWidth());
		setHeight(getImage().getHeight());
		
		// It is, of course, equippable.
		equippable = true;
		
		// Break up the spriteSheet. Assumed to be regular human character size, for now.
		if(player.getPlayer()!=null) {
			if(player.getPlayer().getPlayerInventory().hasItem(this.name)) {
				setDrawObject(false);
			}
		}
		else setDrawObject(true);
		inInventory = false;
	}
	
	// Update.
	@Override
	public void update() {
		if(this.isDrawObject() && this.collides(this.getIntX(), this.getIntY(), player.getPlayer())) {
			pickUp();
		}
	}

	public String getSpeed() {
		return speed;
	}

	public void setSpeed(String speed) {
		this.speed = speed;
	}

	public String getRange() {
		return range;
	}

	public void setRange(String range) {
		this.range = range;
	}

	public int getAttackDamage() {
		return attackDamage;
	}

	public void setAttackDamage(int attackDamage) {
		this.attackDamage = attackDamage;
	}
}