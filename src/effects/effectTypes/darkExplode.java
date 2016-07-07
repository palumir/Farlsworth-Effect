package effects.effectTypes;

import java.util.ArrayList;
import java.util.Random;

import drawing.camera;
import drawing.spriteSheet;
import drawing.animation.animation;
import drawing.animation.animationPack;
import drawing.spriteSheet.spriteSheetInfo;
import effects.effect;
import effects.effectType;
import effects.buffs.darkSlow;
import modes.mode;
import sounds.sound;
import units.animalType;
import units.humanType;
import units.player;
import units.unit;
import units.unitType;
import units.unitTypes.farmLand.spiderCave.poisonSpider;
import utilities.time;
import utilities.utility;
import zones.zone;

public class darkExplode extends effect {
	
	// Default dimensions.
	public static int DEFAULT_SPRITE_WIDTH = 150;
	public static int DEFAULT_SPRITE_HEIGHT = 150;
	
	// Platformer real dimensions
	public static int DEFAULT_PLATFORMER_HEIGHT = DEFAULT_SPRITE_WIDTH;
	public static int DEFAULT_PLATFORMER_WIDTH = DEFAULT_SPRITE_HEIGHT;
	public static int DEFAULT_PLATFORMER_ADJUSTMENT_Y = 0;
	
	// TopDown real dimensions
	public static int DEFAULT_TOPDOWN_HEIGHT = 120;
	public static int DEFAULT_TOPDOWN_WIDTH = 120;
	public static int DEFAULT_TOPDOWN_ADJUSTMENT_Y = 0;
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default name.
	private static String DEFAULT_EFFECT_NAME = "darkExplode";
	
	// Effect sprite stuff.
	private static String DEFAULT_EFFECT_SPRITESHEET = "images/effects/" + DEFAULT_EFFECT_NAME + ".png";
	
	// Duration
	private static float DEFAULT_ANIMATION_DURATION = 3f; // multiple of 0.25f
	
	// Effect sound
	protected String effectSound2 = "sounds/effects/combat/darkExplode.wav";
	
	// Damage
	protected int DEFAULT_DAMAGE = 1;
	
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
	private int damage = DEFAULT_DAMAGE;
	private boolean playedOnce = false;
	private long lastHurt = 0;
	private float hurtEvery = 0.5f;
	
	///////////////
	/// METHODS ///
	///////////////
	// Constructor
	public darkExplode(int newX, int newY, boolean isAllied, int damage, float duration) {
		super(theEffectType, newX, newY);
		
		// Allied?
		allied = isAllied;
		
		// Damage.
		this.damage = damage;
		
		// Duration
		this.animationDuration = duration;
		
		// Set background
		setBackgroundDoodad(true);
		
		// Deal with animations.
		// Set-up animations.
		animationPack newAnimationPack =  new animationPack();
		animation startAnimation = new animation("startAnimation", 
					theEffectType.getEffectTypeSpriteSheet().getAnimation(0), 
					0, 
					3, 
					0.25f); 
		
		animation pulseAnimation = new animation("pulseAnimation", 
				theEffectType.getEffectTypeSpriteSheet().getAnimation(0), 
				4, 
				7, 
				0.25f); 
		newAnimationPack.addAnimation(startAnimation);
		newAnimationPack.addAnimation(pulseAnimation);
		animations = newAnimationPack;
		
		// Set the animation.
		setCurrentAnimation(startAnimation);
		
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
		// We've played the start already.
		if(playedOnce && j == 0 && !getCurrentAnimation().getName().equals("pulseAnimation")) {
			setCurrentAnimation(animations.getAnimation("pulseAnimation"));
		}
		
		// Set played once.
		if(j >= 3 && !getCurrentAnimation().getName().equals("pulseAnimation")) {
			playedOnce = true;
		}
		
		// Play the second half of the animation.
		if(j>=4) {
			
			// If someone is in the explosion radius, hurt.
			ArrayList<unit> hurtUnits = unit.getUnitsInRadius(getX() + getWidth()/2, getY() + getHeight()/2, getWidth()/2);
			if(hurtUnits != null && time.getTime() - lastHurt > hurtEvery*1000) {
				lastHurt = time.getTime();
				for(int i = 0; i < hurtUnits.size(); i++) {
					if(hurtUnits.get(i) instanceof player && !allied) {
						hurtUnits.get(i).hurt(damage, 1f);
						darkSlow d = new darkSlow(hurtUnits.get(i), hurtEvery);
					}
					else if(allied) {
						hurtUnits.get(i).hurt(damage, 1f);
						darkSlow d = new darkSlow(hurtUnits.get(i), hurtEvery);
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
