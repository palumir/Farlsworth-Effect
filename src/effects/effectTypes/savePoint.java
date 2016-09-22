package effects.effectTypes;

import drawing.spriteSheet;
import drawing.spriteSheet.spriteSheetInfo;
import drawing.animation.animation;
import drawing.animation.animationPack;
import effects.effect;
import effects.effectType;
import modes.mode;
import units.player;

public class savePoint extends effect {
	
	// Default dimensions.
	public static int DEFAULT_SPRITE_WIDTH = 25;
	public static int DEFAULT_SPRITE_HEIGHT = 35;
	
	// Platformer real dimensions
	public static int DEFAULT_PLATFORMER_HEIGHT = 35;
	public static int DEFAULT_PLATFORMER_WIDTH = 25;
	public static int DEFAULT_PLATFORMER_ADJUSTMENT_Y = 0;
	
	// TopDown real dimensions
	public static int DEFAULT_TOPDOWN_HEIGHT = 35;
	public static int DEFAULT_TOPDOWN_WIDTH = 25;
	public static int DEFAULT_TOPDOWN_ADJUSTMENT_Y = 0;
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default name.
	private static String DEFAULT_EFFECT_NAME = "savePoint";
	
	// Effect sprite stuff.
	private static String DEFAULT_EFFECT_SPRITESHEET = "images/effects/bottleEffects/" + DEFAULT_EFFECT_NAME + ".png";
	
	// Duration
	private static float DEFAULT_ANIMATION_DURATION = 3f; // multiple of 0.25f
	
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
	public savePoint(int newX, int newY) {
		super(theEffectType, newX, newY);
		
		// Deal with animations.
		// Set-up animations.
		animationPack newAnimationPack =  new animationPack();
		
		animation pulseAnimation = new animation("pulseAnimation", 
				theEffectType.getEffectTypeSpriteSheet().getAnimation(0), 
				0, 
				9, 
				0.9f); 
		newAnimationPack.addAnimation(pulseAnimation);
		animations = newAnimationPack;
		
		// Set the animation.
		setCurrentAnimation(pulseAnimation);
		
		// Make adjustments on hitbox if we're in topDown.
		setHeight(getDefaultHeight());
		setWidth(getDefaultWidth());
		setHitBoxAdjustmentY(getDefaultHitBoxAdjustmentY());
		hasATimer = false;

	}
	
	// Respond to ending
	@Override
	public void respondToFrame(int j) {
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

	public static void createSavePoint() {
		player.getPlayer().lastSaveBottleChargeIndicator = new savePoint(
				(int)player.getPlayer().lastSaveBottle.getX(),
				player.getPlayer().getIntY() - 
				(
				((int)player.getPlayer().lastSaveBottle.getY() + savePoint.DEFAULT_SPRITE_HEIGHT)
				- (player.getPlayer().getIntY() + player.getPlayer().getHeight()))
				);
	}

}
