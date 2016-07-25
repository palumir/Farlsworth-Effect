package effects;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

import drawing.camera;
import drawing.spriteSheet;
import drawing.spriteSheet.spriteSheetInfo;
import effects.effect;
import effects.effectType;
import modes.mode;
import sounds.sound;
import terrain.chunk;
import units.humanType;
import units.player;
import units.unit;
import units.unitType;
import utilities.intTuple;
import utilities.mathUtils;
import utilities.time;
import utilities.utility;
import zones.zone;

public abstract class projectile extends effect {
	
	// Default dimensions.
	public static int DEFAULT_SPRITE_WIDTH = 10;
	public static int DEFAULT_SPRITE_HEIGHT = 10;
	
	// Platformer real dimensions
	public static int DEFAULT_PLATFORMER_HEIGHT = DEFAULT_SPRITE_WIDTH;
	public static int DEFAULT_PLATFORMER_WIDTH = DEFAULT_SPRITE_HEIGHT;
	public static int DEFAULT_PLATFORMER_ADJUSTMENT_Y = 0;
	
	// TopDown real dimensions
	public static int DEFAULT_TOPDOWN_HEIGHT = 10;
	public static int DEFAULT_TOPDOWN_WIDTH = 10;
	public static int DEFAULT_TOPDOWN_ADJUSTMENT_Y = 0;
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// List of all projectiles
	public static ArrayList<projectile> allProjectiles = new ArrayList<projectile>();

	// Movespeed
	protected int DEFAULT_PROJECTILE_MOVESPEED = 3;
	
	// Effect sound
	protected static float DEFAULT_VOLUME = 0.8f;
	protected static int DEFAULT_SOUND_RADIUS = 1000;
	
	// Projectile damage.
	
	//////////////
	/// FIELDS ///
	//////////////
	
	// Enemy or allied projectile
	protected boolean allied = false;
	
	// Projectile rise/run
	protected float rise;
	protected float run;
	protected int intRise;
	protected int intRun;
	
	// Came from
	private int cameFromX;
	private int cameFromY;
	
	// Travelling to
	protected int moveToX;
	protected int moveToY;
	
	// Float x and Y
	protected float floatX = 0;
	protected float floatY = 0;
	
	// Move-speed
	protected float moveSpeed = DEFAULT_PROJECTILE_MOVESPEED;
	
	// Reflectable
	private boolean reflectable = false;
	
	// Damage
	protected int damage;
	
	// Collision?
	protected boolean collisionOn = true;
	
	///////////////
	/// METHODS ///
	///////////////
	// Constructor
	public projectile(effectType theEffectType, int newX, int newY, int newMoveToX, int newMoveToY, int damage) {
		super(theEffectType, newX, newY);
		
		// Set came from
		cameFromX = newX;
		cameFromY = newY;
		
		// Set floatX and floatY
		floatX = newX;
		floatY = newY;
		
		// Set move to
		moveToX = newMoveToX;
		moveToY = newMoveToY;
		
		// Set damage.
		this.damage = damage;
		
		// Set rise/run
		setRiseRun();
		
		// Make adjustments on hitbox if we're in topDown.
		setHeight(getDefaultHeight());
		setWidth(getDefaultWidth());
		setHitBoxAdjustmentY(getDefaultHitBoxAdjustmentY());
		
		// Add to list of projectiles
		allProjectiles.add(this);
	}
	
	///////////////////////////
	/// GETTERS AND SETTERS ///
	///////////////////////////
	
	// Get default width.
	public static int getDefaultWidth() {
		if(mode.getCurrentMode().equals("topDown")) {
			return DEFAULT_TOPDOWN_WIDTH;
		}
		else {
			return DEFAULT_PLATFORMER_WIDTH;
		}
	}
	
	
	// Get units in box.
	public static ArrayList<projectile> getProjectilesInBox(int x1, int y1, int x2, int y2) {
		ArrayList<projectile> returnList = new ArrayList<projectile>();
		for(int i = 0; i < allProjectiles.size(); i++) {
			projectile u = allProjectiles.get(i);
			if(u.isWithin(x1, y1, x2, y2)) {
				returnList.add(u);
			}
		}
		if(returnList.size()==0) return null;
		return returnList;
	}
	
	// Check if a unit is within 
	public boolean isWithin(int x1, int y1, int x2, int y2) {
		return getIntX() < x2 && 
		 getIntX() + getWidth() > x1 && 
		 getIntY() < y2 && 
		 getIntY() + getHeight() > y1;
	}
	

	// Send projectile back.
	public void sendBack() {
		allied = !allied;
		
		// Switch where it's going.
		int holdX = moveToX;
		int holdY = moveToY;
		moveToX = cameFromX;
		moveToY = cameFromY;
		cameFromX = holdX;
		cameFromY = holdY;
		
		// Set new rise/run.
		setRiseRun();
	}
	
	// Set rise run. Optimized to make projectiles kinda accurate.
	public void setRiseRun() {
		float yDistance = (moveToY - getIntY());
		float xDistance = (moveToX - getIntX());
		float distanceXY = (float) Math.sqrt(yDistance * yDistance
					+ xDistance * xDistance);
		
		// Calculate rise values.
		float floatRise = ((yDistance/distanceXY)*getMoveSpeed());
		rise = floatRise;
		
		// Calculate run values.
		float floatRun = ((xDistance/distanceXY)*getMoveSpeed());
		run = floatRun;
	}
	
	// Update unit
	@Override
	public void update() {
		
		// Set floatX and Y
		floatX += run;
		floatY += rise;
		
		// Set new X and Y.
		setFloatX((int)floatX);
		setFloatY((int)floatY);
		
		player currPlayer = player.getCurrentPlayer();
		
		boolean isWithin;
		if(!isAllied()) {
			// If we hit the player, explode it.
			isWithin = currPlayer.isWithinRadius(getIntX() + getWidth()/2, getIntY()+getHeight()/2, getWidth()/2);
		}
		else {
			ArrayList<unit> uList = unit.getUnitsInBox(getIntX(), getIntY(), getIntX() + getWidth(), getIntY() + getHeight());
			isWithin = (uList != null) && ((uList.contains(currPlayer) && (uList.size() > 1)) || (uList.size() >= 1 && !uList.contains(currPlayer)));
		}
				 
		// If we collide with something, explode it.
		intTuple tupleXY = chunk.collidesWith(this, getIntX() + (int)run, getIntY() + (int)rise);
		boolean isCollide = (tupleXY.x == 1 || tupleXY.y == 1) && collisionOn;
		if(isWithin || isCollide) {
			if(currPlayer.isWithinRadius(getIntX() + getWidth()/2, getIntY()+getHeight()/2, getWidth()/2) &&
					!isAllied() &&
					currPlayer.isShielding()) {
				boolean b = currPlayer.hurt(0, 1);
				if(!b) sendBack();
				else {
					explode();
				}
			}
			else {
				explode();
			}
		}
		
		// Run animation.
		if(getCurrentAnimation() != null) getCurrentAnimation().playAnimation();
		if(time.getTime() - timeStarted >= animationDuration*1000) {
			explode();
		}
	}
	
	public abstract void explode();
	
	// Get default height.
	public static int getDefaultHeight() {
		if(mode.getCurrentMode().equals("topDown")) {
			return DEFAULT_TOPDOWN_HEIGHT;
		}
		else {
			return DEFAULT_PLATFORMER_HEIGHT;
		}
	}
	
	// Get default hitbox adjustment Y.
	public static int getDefaultHitBoxAdjustmentY() {
		if(mode.getCurrentMode().equals("topDown")) {
			return DEFAULT_TOPDOWN_ADJUSTMENT_Y;
		}
		else {
			return DEFAULT_PLATFORMER_ADJUSTMENT_Y;
		}
	}

	public float getMoveSpeed() {
		return moveSpeed;
	}

	public void setMoveSpeed(int moveSpeed) {
		this.moveSpeed = moveSpeed;
	}

	public boolean isAllied() {
		return allied;
	}

	public void setAllied(boolean allied) {
		this.allied = allied;
	}

	public boolean isReflectable() {
		return reflectable;
	}

	public void setReflectable(boolean reflectable) {
		this.reflectable = reflectable;
	}

}
