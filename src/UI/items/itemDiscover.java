package UI.items;

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
import effects.effectType;
import items.item;
import modes.mode;
import utilities.time;

public class itemDiscover extends absolutePositionedEffect {
	// Default dimensions.
	public static int DEFAULT_SPRITE_WIDTH = 128;
	public static int DEFAULT_SPRITE_HEIGHT = 128;
	
	// Platformer real dimensions
	public static int DEFAULT_PLATFORMER_HEIGHT = DEFAULT_SPRITE_HEIGHT;
	public static int DEFAULT_PLATFORMER_WIDTH = DEFAULT_SPRITE_WIDTH;
	public static int DEFAULT_PLATFORMER_ADJUSTMENT_Y = 0;
	
	// TopDown real dimensions
	public static int DEFAULT_TOPDOWN_HEIGHT = DEFAULT_SPRITE_HEIGHT;
	public static int DEFAULT_TOPDOWN_WIDTH = DEFAULT_SPRITE_WIDTH;
	public static int DEFAULT_TOPDOWN_ADJUSTMENT_Y = 0;
	
	// Default text color
	public static Color DEFAULT_COLOR = Color.black;
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default name.
	public static String DEFAULT_EFFECT_NAME = "itemDiscover";
	
	// Effect sprite stuff.
	private static String DEFAULT_EFFECT_SPRITESHEET_COMMON = "images/effects/interface/" + DEFAULT_EFFECT_NAME + "Common.png";
	private static String DEFAULT_EFFECT_SPRITESHEET_RARE = "images/effects/interface/" + DEFAULT_EFFECT_NAME + "Rare.png";
	private static String DEFAULT_EFFECT_SPRITESHEET_LEGENDARY = "images/effects/interface/" + DEFAULT_EFFECT_NAME + "Legendary.png";
	
	// Duration
	private static float DEFAULT_ANIMATION_DURATION = 4f; // multiple of 0.25f
	
	// Item
	private item item;
	
	// The actual type.
	private static effectType commonEffectType =
			new effectType(DEFAULT_EFFECT_NAME,
					new spriteSheet(new spriteSheetInfo(
							DEFAULT_EFFECT_SPRITESHEET_COMMON, 
							DEFAULT_SPRITE_WIDTH, 
							DEFAULT_SPRITE_HEIGHT,
							0,
							0
							)),
							DEFAULT_ANIMATION_DURATION);	
	private static effectType rareEffectType =
			new effectType(DEFAULT_EFFECT_NAME,
					new spriteSheet(new spriteSheetInfo(
							DEFAULT_EFFECT_SPRITESHEET_RARE, 
							DEFAULT_SPRITE_WIDTH, 
							DEFAULT_SPRITE_HEIGHT,
							0,
							0
							)),
							DEFAULT_ANIMATION_DURATION);	
	private static effectType legendaryEffectType =
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
	public itemDiscover(item i, int newX, int newY) {
		super(getTypeOfEffect(i), newX, newY);
		
		// The item.
		item = i;
		
		// Deal with animations.
		// Set-up animations.
		animationPack newAnimationPack =  new animationPack();
		animation spawnAnimation = new animation("spawnAnimation", 
				getTypeOfEffect(i).getEffectTypeSpriteSheet().getAnimation(0), 
				0, 
				4, 
				0.15f); 
		newAnimationPack.addAnimation(spawnAnimation);
		animation pulseAnimation = new animation("pulseAnimation", 
				getTypeOfEffect(i).getEffectTypeSpriteSheet().getAnimation(0), 
				5, 
				14, 
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
		
		// Draw above everything.
		forceInFront = true;
	}
	
	// Get type of effect
	public static effectType getTypeOfEffect(item i) {
		if(i.rarity.equals("Legendary")) {
			return legendaryEffectType;
		}
		else if(i.rarity.equals("Rare")) {
			return rareEffectType;
		}
		else {
			return commonEffectType;
		}
	}
	
	private boolean fadeOut = false;
	
	// Respond to ending
	@Override
	public void respondToFrame(int j) {
		
		// Fadeout.
		if(time.getTime() - timeStarted > DEFAULT_ANIMATION_DURATION*1000) {
			fadeOut();
		}
		
		// Destroy if completed.
		if(fadeOut && time.getTime() - timeStarted > getAnimations().getAnimation("spawnAnimation").getTimeToComplete()*1000) {
			destroy();
		}
		
	}
	
	// Respond to destroy
	@Override
	public void respondToDestroy() {
	}
	
	// Fadeout
	public void fadeOut() {
		fadeOut = true;
		timeStarted = time.getTime();
	}

	// Draw the effect
	@Override
	public void drawObject(Graphics g) {
		
		// Of course only draw if the animation is not null.
		if(getCurrentAnimation() != null) {
			
			float sizeMultiplier = (time.getTime() - timeStarted)/(getAnimations().getAnimation("spawnAnimation").getTimeToComplete()*1000);
			if(sizeMultiplier > 1) sizeMultiplier = 1;
			if(fadeOut) sizeMultiplier = 1 - (time.getTime() - timeStarted)/(getAnimations().getAnimation("spawnAnimation").getTimeToComplete()*1000);
			if(sizeMultiplier < 0) sizeMultiplier = 0;
			
			g.drawImage(getCurrentAnimation().getCurrentFrame(), 
					(int)((getIntX() + getCurrentAnimation().getCurrentFrame().getWidth()/2 - sizeMultiplier*getCurrentAnimation().getCurrentFrame().getWidth()/2)*gameCanvas.getScaleX()), 
					(int)((getIntY() + getCurrentAnimation().getCurrentFrame().getHeight()/2 - sizeMultiplier*getCurrentAnimation().getCurrentFrame().getHeight()/2)*gameCanvas.getScaleY()) , 
					(int)(gameCanvas.getScaleX()*getObjectSpriteSheet().getSpriteWidth()*sizeMultiplier + 1), 
					(int)(gameCanvas.getScaleY()*getObjectSpriteSheet().getSpriteHeight()*sizeMultiplier + 1), 
					null);
			
			g.drawImage(item.getImage(), 
					(int)((getIntX() + getCurrentAnimation().getCurrentFrame().getWidth()/2 - sizeMultiplier*item.getImage().getWidth()*1.5f/2)*gameCanvas.getScaleX()), 
					(int)((getIntY() + getCurrentAnimation().getCurrentFrame().getHeight()/2 - sizeMultiplier*item.getImage().getHeight()*1.5f/2 - 7)*gameCanvas.getScaleY()), 
					(int)(gameCanvas.getScaleX()*sizeMultiplier*item.getImage().getWidth()*1.5f + 1), 
					(int)(gameCanvas.getScaleY()*sizeMultiplier*item.getImage().getHeight()*1.5f + 1), 
					null);
			
			Font font = drawnObject.DEFAULT_FONT_BOLD.deriveFont(drawnObject.DEFAULT_FONT_BOLD.getSize()*1.1f*sizeMultiplier);
			g.setFont(font);
			g.setColor(DEFAULT_COLOR);
			g.drawString(item.getName(), 
					(int)((getIntX() 
							+ getCurrentAnimation().getCurrentFrame().getWidth()/2)*gameCanvas.getScaleX()
							- g.getFontMetrics().stringWidth(item.getName())/2), 
					(int)((getIntY()
							+ getCurrentAnimation().getCurrentFrame().getHeight()/2 
							+ item.getImage().getHeight()*1.5f/2+3)*gameCanvas.getScaleY()));
			
			font = drawnObject.DEFAULT_FONT.deriveFont(drawnObject.DEFAULT_FONT.getSize()*1f*sizeMultiplier);
			g.setFont(font);
			g.setColor(DEFAULT_COLOR);
			g.drawString(item.rarity, 
					(int)((getIntX() 
							+ getCurrentAnimation().getCurrentFrame().getWidth()/2)*gameCanvas.getScaleX()
							- g.getFontMetrics().stringWidth(item.rarity)/2), 
					(int)((getIntY()
							+ getCurrentAnimation().getCurrentFrame().getHeight()/2 
							+ item.getImage().getHeight()*1.5f/2 + 14)*gameCanvas.getScaleY()));
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