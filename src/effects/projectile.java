package effects;

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
import units.animalType;
import units.humanType;
import units.player;
import units.unit;
import units.unitType;
import utilities.intTuple;
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
	protected int DEFAULT_PROJECTILE_MOVESPEED = 5;
	
	// Effect sound
	protected static float DEFAULT_VOLUME = 0.8f;
	protected static int DEFAULT_SOUND_RADIUS = 1000;
	
	//////////////
	/// FIELDS ///
	//////////////
	
	// Enemy or allied projectile
	private boolean allied = false;
	
	// Projectile rise/run
	private int rise;
	private int run;
	
	// Came from
	private int cameFromX;
	private int cameFromY;
	
	// Travelling to
	private int moveToX;
	private int moveToY;
	
	// Move-speed
	private int moveSpeed = DEFAULT_PROJECTILE_MOVESPEED;
	
	///////////////
	/// METHODS ///
	///////////////
	// Constructor
	public projectile(effectType theEffectType, int newX, int newY, int newMoveToX, int newMoveToY) {
		super(theEffectType, newX, newY);
		
		// Set came from
		cameFromX = newX;
		cameFromY = newY;
		
		// Set move to
		moveToX = newMoveToX;
		moveToY = newMoveToY;
		
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
		return getX() < x2 && 
		 getX() + getWidth() > x1 && 
		 getY() < y2 && 
		 getY() + getHeight() > y1;
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
	
	// Set rise run
	public void setRiseRun() {
		float yDistance = (moveToY - getY());
		float xDistance = (moveToX - getX());
		float distanceXY = (float) Math.sqrt(yDistance * yDistance
					+ xDistance * xDistance);
		rise = (int) ((yDistance/distanceXY)*getMoveSpeed());
		run = (int) ((xDistance/distanceXY)*getMoveSpeed());
	}
	
	public boolean pastPoint = false;
	
	// Update unit
	@Override
	public void update() {
		//System.out.println(rise);
		/*if(Math.abs(moveToX - getX()) < 3*moveSpeed && Math.abs(moveToY - getY()) < 3*moveSpeed) {
			pastPoint = true;
		}
		if(!pastPoint) setRiseRun();*/
		
		// Set new X and Y.
		setX(getX() + run);
		setY(getY() + rise);
		
		player currPlayer = player.getCurrentPlayer();
		
		boolean isWithin;
		if(!isAllied()) {
			// If we hit the player, explode it.
			isWithin = getX() + getWidth()/2 > currPlayer.getX() && 
				 getX() + getWidth()/2 < currPlayer.getX() + currPlayer.getWidth() && 
				 getY() + getHeight()/2 > currPlayer.getY() && 
				 getY() + getHeight()/2 < currPlayer.getY() + currPlayer.getHeight();
		}
		else {
			isWithin = unit.getUnitsInBox(getX(), getY(), getX() + getWidth(), getY() + getHeight()) != null;
		}
				 
		// If we collide with something, explode it.
		intTuple tupleXY = chunk.collidesWith(this, getX() + run, getY() + rise);
		boolean isCollide = tupleXY.x == 1 || tupleXY.y == 1;
		if(isWithin || isCollide) {
			explode();
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

	public int getMoveSpeed() {
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

}
