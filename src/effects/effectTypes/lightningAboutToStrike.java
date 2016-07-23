package effects.effectTypes;

import java.util.Random;

import drawing.camera;
import drawing.spriteSheet;
import drawing.spriteSheet.spriteSheetInfo;
import effects.effect;
import effects.effectType;
import modes.mode;
import sounds.sound;
import units.humanType;
import units.unit;
import units.unitType;
import utilities.time;
import utilities.utility;
import zones.zone;

public class lightningAboutToStrike extends effect {
	
	// Default dimensions.
	public static int DEFAULT_SPRITE_WIDTH = 32;
	public static int DEFAULT_SPRITE_HEIGHT = 8;
	
	// Platformer real dimensions
	public static int DEFAULT_PLATFORMER_HEIGHT = DEFAULT_SPRITE_HEIGHT;
	public static int DEFAULT_PLATFORMER_WIDTH = DEFAULT_SPRITE_WIDTH;
	public static int DEFAULT_PLATFORMER_ADJUSTMENT_Y = 0;
	
	// TopDown real dimensions
	public static int DEFAULT_TOPDOWN_HEIGHT = DEFAULT_SPRITE_HEIGHT;
	public static int DEFAULT_TOPDOWN_WIDTH = DEFAULT_SPRITE_WIDTH;
	public static int DEFAULT_TOPDOWN_ADJUSTMENT_Y = 0;
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default name.
	private static String DEFAULT_EFFECT_NAME = "lightningAboutToStrike";
	
	// Effect sprite stuff.
	private static String DEFAULT_EFFECT_SPRITESHEET = "images/effects/" + DEFAULT_EFFECT_NAME + ".png";
	
	// Sound
	public static String soundEffect = "sounds/effects/weather/preLightningStrike.wav";
	
	// Duration
	private static float DEFAULT_ANIMATION_DURATION = 0.2f;
	
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
	
	
	
	//////////////
	/// FIELDS ///
	//////////////
	
	///////////////
	/// METHODS ///
	///////////////
	// Constructor
	public lightningAboutToStrike(int newX, int newY, boolean muted) {
		super(theEffectType, newX, newY);
		
		// Play sound
		if(!muted) {
			sound s = new sound(soundEffect);
			s.setPosition(getIntX(),getIntY(),sound.DEFAULT_SOUND_RADIUS);
			s.start();
		}
				
		// Make adjustments on hitbox if we're in topDown.
		setHeight(getDefaultHeight());
		setWidth(getDefaultWidth());
		setHitBoxAdjustmentY(getDefaultHitBoxAdjustmentY());
				
		// Has no timer.
		hasATimer = false;

	}
	
	// Constructor
	public lightningAboutToStrike(int newX, int newY) {
		super(theEffectType, newX, newY);
		
		// Play sound
		sound s = new sound(soundEffect);
		s.setPosition(getIntX(),getIntY(),sound.DEFAULT_SOUND_RADIUS);
		s.start();
				
		// Make adjustments on hitbox if we're in topDown.
		setHeight(getDefaultHeight());
		setWidth(getDefaultWidth());
		setHitBoxAdjustmentY(getDefaultHitBoxAdjustmentY());
				
		// Has no timer.
		hasATimer = false;

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
