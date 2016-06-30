package effects.effectTypes;

import java.util.ArrayList;
import java.util.Random;

import drawing.camera;
import drawing.spriteSheet;
import drawing.spriteSheet.spriteSheetInfo;
import effects.effect;
import effects.effectType;
import modes.mode;
import sounds.sound;
import units.animalType;
import units.humanType;
import units.player;
import units.unit;
import units.unitType;
import utilities.time;
import utilities.utility;
import zones.zone;

public class poisonExplode extends effect {
	
	// Default dimensions.
	public static int DEFAULT_SPRITE_WIDTH =47;
	public static int DEFAULT_SPRITE_HEIGHT = 47;
	
	// Platformer real dimensions
	public static int DEFAULT_PLATFORMER_HEIGHT = DEFAULT_SPRITE_WIDTH;
	public static int DEFAULT_PLATFORMER_WIDTH = DEFAULT_SPRITE_HEIGHT;
	public static int DEFAULT_PLATFORMER_ADJUSTMENT_Y = 0;
	
	// TopDown real dimensions
	public static int DEFAULT_TOPDOWN_HEIGHT = 47;
	public static int DEFAULT_TOPDOWN_WIDTH = 47;
	public static int DEFAULT_TOPDOWN_ADJUSTMENT_Y = 0;
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default name.
	private static String DEFAULT_EFFECT_NAME = "poisonExplode";
	
	// Did it already hurt somebody?
	private boolean alreadyHurt = false;
	
	// Effect sprite stuff.
	private static String DEFAULT_EFFECT_SPRITESHEET = "images/effects/" + DEFAULT_EFFECT_NAME + ".png";
	
	// Duration
	private static float DEFAULT_ANIMATION_DURATION = 0.5f;
	
	// Effect sound
	protected String effectSound2 = "sounds/effects/combat/poisonExplode.wav";
	protected static float DEFAULT_VOLUME = 0.8f;
	
	// Damage
	protected int DEFAULT_DAMAGE = 7;
	
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
	private boolean allied = false;
	
	///////////////
	/// METHODS ///
	///////////////
	// Constructor
	public poisonExplode(int newX, int newY, boolean isAllied) {
		super(theEffectType, newX, newY);
		
		// Allied?
		allied = isAllied;
		
		// Set sound.
		sound s = new sound(effectSound2);
		s.setPosition(getX(), getY(), sound.DEFAULT_SOUND_RADIUS);
		s.start();
		
		// Make adjustments on hitbox if we're in topDown.
		setHeight(getDefaultHeight());
		setWidth(getDefaultWidth());
		setHitBoxAdjustmentY(getDefaultHitBoxAdjustmentY());

	}
	
	// Respond to ending
	@Override
	public void respondToFrame(int j) {
		if(j >= 2) {
			// If someone is in the explosion radius, hurt.
			ArrayList<unit> hurtUnits = unit.getUnitsInBox(getX(), getY(), getX() + getWidth(), getY() + getHeight());
			if(!alreadyHurt && hurtUnits != null) {
				alreadyHurt = true;
				for(int i = 0; i < hurtUnits.size(); i++) {
					if(hurtUnits.get(i) instanceof player && !allied) {
						hurtUnits.get(i).knockBack(getX()+getWidth()/2,getY()+getWidth()/2, getWidth()/2, .2f, 5);
						hurtUnits.get(i).hurt(DEFAULT_DAMAGE, 1f);
					}
					else if(allied) {
						hurtUnits.get(i).knockBack(getX()+getWidth()/2,getY()+getWidth()/2, getWidth()/2, .2f, 5);
						hurtUnits.get(i).hurt(DEFAULT_DAMAGE, 1f);
					}
				}
			}
		}
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
