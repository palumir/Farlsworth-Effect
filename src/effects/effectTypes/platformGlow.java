package effects.effectTypes;

import drawing.spriteSheet;
import drawing.spriteSheet.spriteSheetInfo;
import drawing.animation.animation;
import drawing.animation.animationPack;
import effects.effect;
import effects.effectType;
import modes.mode;
import units.player;

public class platformGlow extends effect {
	
	// Default dimensions.
	public static int DEFAULT_SPRITE_WIDTH = 96;
	public static int DEFAULT_SPRITE_HEIGHT = 32;
	
	// Platformer real dimensions
	public static int DEFAULT_PLATFORMER_HEIGHT = DEFAULT_SPRITE_HEIGHT;
	public static int DEFAULT_PLATFORMER_WIDTH = DEFAULT_SPRITE_WIDTH;
	public static int DEFAULT_PLATFORMER_ADJUSTMENT_Y = 0;
	
	// TopDown real dimensions
	public static int DEFAULT_TOPDOWN_HEIGHT = 32;
	public static int DEFAULT_TOPDOWN_WIDTH = 96;
	public static int DEFAULT_TOPDOWN_ADJUSTMENT_Y = 0;
	
	// Leniency
	public static int leniency = 0;
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default name.
	private static String DEFAULT_EFFECT_NAME = "platformGlow";
	
	// Effect sprite stuff.
	private static String DEFAULT_EFFECT_SPRITESHEET = "images/effects/bosses/wolfless/" + DEFAULT_EFFECT_NAME + ".png";
	
	// Duration
	private static float DEFAULT_ANIMATION_DURATION = 10f; // multiple of 0.25f
	
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
	private int damage = DEFAULT_DAMAGE;
	
	///////////////
	/// METHODS ///
	///////////////
	// Constructor
	public platformGlow(int newX, int newY, float duration) {
		super(theEffectType, newX, newY);
		
		// Damage.
		this.damage = 1;
		
		// Duration
		this.setAnimationDuration(duration);
		
		// Set background
		//setBackgroundDoodad(true);
		
		// Deal with animations.
		// Set-up animations.
		animationPack newAnimationPack =  new animationPack();
		animation pulseAnimation = new animation("pulseAnimation", 
					theEffectType.getEffectTypeSpriteSheet().getAnimation(0), 
					0, 
					7, 
					0.5f); 
		
		newAnimationPack.addAnimation(pulseAnimation);
		animations = newAnimationPack;
		
		// Set the animation.
		setCurrentAnimation(pulseAnimation);
		
		// Make adjustments on hitbox if we're in topDown.
		setHeight(getDefaultHeight());
		setWidth(getDefaultWidth());
		setHitBoxAdjustmentY(getDefaultHitBoxAdjustmentY());
		
	}
	
	// Respond to ending
	@Override
	public void respondToFrame(int j) {
		// If someone is in the explosion radius, hurt.
		if(player.getPlayer().isWithin(getIntX()+5, getIntY()-1, getIntX()+getWidth()-5, getIntY()+getHeight()-5)) {
			player.getPlayer().hurt(damage, 1f);
		}
	}
	
	// Respond to destroy
	@Override
	public void respondToDestroy() {
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
