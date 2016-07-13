package effects.effectTypes;

import java.util.ArrayList;
import java.util.Random;

import drawing.camera;
import drawing.spriteSheet;
import drawing.spriteSheet.spriteSheetInfo;
import effects.effect;
import effects.effectType;
import effects.projectile;
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

public class rockPiece extends projectile {
	
	// Default dimensions.
	public static int DEFAULT_SPRITE_WIDTH = 11;
	public static int DEFAULT_SPRITE_HEIGHT = 11;
	
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
	private static String DEFAULT_EFFECT_NAME = "rockPiece";
	
	// Effect sprite stuff.
	private static String DEFAULT_EFFECT_SPRITESHEET = "images/effects/" + DEFAULT_EFFECT_NAME + ".png";
	
	// Duration
	private static float DEFAULT_ANIMATION_DURATION = 10f;
	
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
	public rockPiece(int newX, int newY, int newMoveToX, int newMoveToY, int damage) {
		super(theEffectType, newX, newY, newMoveToX, newMoveToY, damage);
		moveSpeed = DEFAULT_MOVESPEED;
		collisionOn = false;
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
		
		player currPlayer = player.getCurrentPlayer();
		
		boolean isWithin;
		if(!isAllied()) {
			// If we hit the player, explode it.
			isWithin = currPlayer.isWithinRadius(getIntX() + getWidth()/2, getIntY()+getHeight()/2, getWidth()/2);
			if(isWithin) { 
				currPlayer.hurt(damage, 1);
				explode();
			}
		}
		else {
			ArrayList<unit> uList = unit.getUnitsInBox(getIntX(), getIntY(), getIntX() + getWidth(), getIntY() + getHeight());
			isWithin = (uList != null) && ((uList.contains(currPlayer) && (uList.size() > 1)) || (uList.size() >= 1 && !uList.contains(currPlayer)));
			if(isWithin) {
				explode();
			}
			
		}
		
		// Run animation.
		if(getCurrentAnimation() != null) getCurrentAnimation().playAnimation();
		if(time.getTime() - timeStarted >= animationDuration*1000) {
			explode();
		}
	}
	
	public void explode() {
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
