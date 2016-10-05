package effects.effectTypes;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import drawing.drawnObject;
import drawing.gameCanvas;
import drawing.spriteSheet;
import drawing.spriteSheet.spriteSheetInfo;
import drawing.animation.animation;
import drawing.animation.animationPack;
import effects.absolutePositionedEffect;
import effects.effect;
import effects.effectType;
import items.item;
import modes.mode;
import utilities.time;

public class itemGlow extends effect {
	// Default dimensions.
	public static int DEFAULT_SPRITE_WIDTH = 64;
	public static int DEFAULT_SPRITE_HEIGHT = 64;
	
	// Platformer real dimensions
	public static int DEFAULT_HEIGHT = DEFAULT_SPRITE_HEIGHT;
	public static int DEFAULT_WIDTH = DEFAULT_SPRITE_WIDTH;
	
	// Default text color
	public static Color DEFAULT_COLOR = Color.black;
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default name.
	public static String DEFAULT_EFFECT_NAME = "itemGlow";
	
	// Effect sprite stuff.
	private static String DEFAULT_EFFECT_SPRITESHEET_COMMON = "images/effects/itemEffects/" + DEFAULT_EFFECT_NAME + "Common.png";
	private static String DEFAULT_EFFECT_SPRITESHEET_RARE = "images/effects/itemEffects/" + DEFAULT_EFFECT_NAME + "Rare.png";
	private static String DEFAULT_EFFECT_SPRITESHEET_LEGENDARY = "images/effects/itemEffects/" + DEFAULT_EFFECT_NAME + "Legendary.png";
	
	// Duration
	private static float DEFAULT_ANIMATION_DURATION = 4f; // multiple of 0.25f
	
	// The actual type.
	public static effectType commonEffectType =
			new effectType(DEFAULT_EFFECT_NAME,
					new spriteSheet(new spriteSheetInfo(
							DEFAULT_EFFECT_SPRITESHEET_COMMON, 
							DEFAULT_SPRITE_WIDTH, 
							DEFAULT_SPRITE_HEIGHT,
							0,
							0
							)),
							DEFAULT_ANIMATION_DURATION);	
	public static effectType rareEffectType =
			new effectType(DEFAULT_EFFECT_NAME,
					new spriteSheet(new spriteSheetInfo(
							DEFAULT_EFFECT_SPRITESHEET_RARE, 
							DEFAULT_SPRITE_WIDTH, 
							DEFAULT_SPRITE_HEIGHT,
							0,
							0
							)),
							DEFAULT_ANIMATION_DURATION);	
	public static effectType legendaryEffectType =
			new effectType(DEFAULT_EFFECT_NAME,
					new spriteSheet(new spriteSheetInfo(
							DEFAULT_EFFECT_SPRITESHEET_LEGENDARY, 
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
	public itemGlow(item i, int newX, int newY) {
		super(getTypeOfEffect(i), newX, newY);
		
		// Deal with animations.
		// Set-up animations.
		animationPack newAnimationPack =  new animationPack();
		animation pulseAnimation = new animation("pulseAnimation", 
				getTypeOfEffect(i).getEffectTypeSpriteSheet().getAnimation(0), 
				5, 
				14, 
				0.9f){{
					setRepeats(true);
				}}; 
		newAnimationPack.addAnimation(pulseAnimation);
		animations = newAnimationPack;
		
		// Set the animation.
		setCurrentAnimation(pulseAnimation);
		
		// Make adjustments on hitbox if we're in topDown.
		setHeight(getDefaultHeight());
		setWidth(getDefaultWidth());
		setHitBoxAdjustmentY(getDefaultHitBoxAdjustmentY());
		setHasATimer(false);
	}
	
	// Get type of effect
	public static effectType getTypeOfEffect(item i) {
		if(i.quality.equals("Legit")) {
			return legendaryEffectType;
		}
		else if(i.quality.equals("Good")) {
			return rareEffectType;
		}
		else {
			return commonEffectType;
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
			return DEFAULT_WIDTH;
	}
	
	// Get default height.
	public static int getDefaultHeight() {
			return DEFAULT_HEIGHT;
	}
	
	// Get default hitbox adjustment Y.
	public static int getDefaultHitBoxAdjustmentY() {
		return 0;
	}
}