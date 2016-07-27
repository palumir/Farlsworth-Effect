package effects.projectiles;

import java.util.ArrayList;
import java.util.Random;

import drawing.camera;
import drawing.spriteSheet;
import drawing.spriteSheet.spriteSheetInfo;
import effects.effect;
import effects.effectType;
import effects.projectile;
import effects.effectTypes.fire;
import modes.mode;
import sounds.sound;
import terrain.chunk;
import units.humanType;
import units.player;
import units.unit;
import units.unitType;
import utilities.intTuple;
import utilities.time;
import utilities.utility;
import zones.zone;

public class spinningFireLog extends projectile {
	
	// Default dimensions.
	public static int DEFAULT_SPRITE_WIDTH = 30;
	public static int DEFAULT_SPRITE_HEIGHT = 30;
	
	// Platformer real dimensions
	public static int DEFAULT_PLATFORMER_HEIGHT = DEFAULT_SPRITE_WIDTH;
	public static int DEFAULT_PLATFORMER_WIDTH = DEFAULT_SPRITE_HEIGHT;
	public static int DEFAULT_PLATFORMER_ADJUSTMENT_Y = 0;
	
	// TopDown real dimensions
	public static int DEFAULT_TOPDOWN_HEIGHT = DEFAULT_SPRITE_WIDTH;
	public static int DEFAULT_TOPDOWN_WIDTH = DEFAULT_SPRITE_HEIGHT;
	public static int DEFAULT_TOPDOWN_ADJUSTMENT_Y = 0;
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default name.
	private static String DEFAULT_EFFECT_NAME = "spinningFireLog";
	
	// Effect sprite stuff.
	private static String DEFAULT_EFFECT_SPRITESHEET = "images/effects/" + DEFAULT_EFFECT_NAME + ".png";
	
	// Duration
	private static float DEFAULT_ANIMATION_DURATION = 0.3f;
	
	// Movespeed
	public static int DEFAULT_MOVESPEED = 2;
	
	// The actual type.
	private static effectType theEffectType =
			new effectType(DEFAULT_EFFECT_NAME,
					new spriteSheet(new spriteSheetInfo(
							DEFAULT_EFFECT_SPRITESHEET, 
							DEFAULT_SPRITE_WIDTH, 
							DEFAULT_SPRITE_HEIGHT,
							0,
							0
							)),
							DEFAULT_ANIMATION_DURATION);	
	
	///////////////
	/// METHODS ///
	///////////////
	// Constructor
	public spinningFireLog(int newX, int newY, int newMoveToX, int newMoveToY, int damage) {
		super(theEffectType, newX, newY, newMoveToX, newMoveToY, damage);
		moveSpeed = DEFAULT_MOVESPEED;
		collisionOn = true;
		hasATimer = false;
		setRiseRun();
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
	
	// Update unit
	@Override
	public void update() {
		
		// Set floatX and Y
		floatX += run;
		floatY += rise;
		
		// Set new X and Y.
		setFloatX((int)floatX);
		setFloatY((int)floatY);
		
		player currPlayer = player.getPlayer();
		
		boolean isWithin;
		if(!isAllied()) {
			// If we hit the player, explode it.
			isWithin = currPlayer.isWithinRadius(getIntX() + getWidth()/2, getIntY()+getHeight()/2, getWidth()/2);
			intTuple tupleXY = chunk.collidesWith(this, getIntX() + (int)run, getIntY() + (int)rise);
			boolean isCollide = (tupleXY.x != 0 || tupleXY.y != 0) && collisionOn;
			if(isWithin || isCollide) { 
				currPlayer.hurt(damage, 1);
				explode();
			}
		}
		else {
			ArrayList<unit> uList = unit.getUnitsInBox(getIntX(), getIntY(), getIntX() + getWidth(), getIntY() + getHeight());
			isWithin = (uList != null) && ((uList.contains(currPlayer) && (uList.size() > 1)) || (uList.size() >= 1 && !uList.contains(currPlayer)));
			intTuple tupleXY = chunk.collidesWith(this, getIntX() + (int)run, getIntY() + (int)rise);
			boolean isCollide = (tupleXY.x != 0 || tupleXY.y != 0) && collisionOn;
			if(isWithin || isCollide) {
				explode();
			}
			
		}
		
		// Run animation.
		if(getCurrentAnimation() != null) getCurrentAnimation().playAnimation();
	}
	
	public void explode() {
		//poisonExplode p = new poisonExplode(getIntX()-poisonExplode.getDefaultWidth()/2,getIntY()-poisonExplode.getDefaultHeight()/2, isAllied(), damage);
		fire.igniteRuffageInBox(getIntX()-10, getIntY()-10, getIntX() + getWidth()+10, getIntY() + getHeight()+10);
		this.destroy();
	}
	
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

}
