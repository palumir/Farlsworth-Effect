package effects.effectTypes;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import drawing.gameCanvas;
import drawing.spriteSheet;
import drawing.spriteSheet.spriteSheetInfo;
import drawing.animation.animation;
import drawing.animation.animationPack;
import effects.effect;
import effects.effectType;
import modes.mode;
import units.player;
import units.unit;
import utilities.time;

public class darkHole extends effect {
	
	// Default dimensions.
	public static int DEFAULT_SPRITE_WIDTH = 150/2;
	public static int DEFAULT_SPRITE_HEIGHT = 150/2;
	
	// Platformer real dimensions
	public static int DEFAULT_PLATFORMER_HEIGHT = DEFAULT_SPRITE_WIDTH;
	public static int DEFAULT_PLATFORMER_WIDTH = DEFAULT_SPRITE_HEIGHT;
	public static int DEFAULT_PLATFORMER_ADJUSTMENT_Y = 0;
	
	// TopDown real dimensions
	public static int DEFAULT_TOPDOWN_HEIGHT = 120/2;
	public static int DEFAULT_TOPDOWN_WIDTH = 120/2;
	public static int DEFAULT_TOPDOWN_ADJUSTMENT_Y = 0;
	
	// Leniency
	public static int leniency = 13;
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default name.
	private static String DEFAULT_EFFECT_NAME = "darkHole";
	
	// Effect sprite stuff.
	private static String DEFAULT_EFFECT_SPRITESHEET = "images/effects/unused/" + DEFAULT_EFFECT_NAME + ".png";
	
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
	
	// List of all darkholes
	public static ArrayList<darkHole> allHoles = new ArrayList<darkHole>();
	
	//////////////
	/// FIELDS ///
	//////////////
	private boolean allied = false;
	private int damage = DEFAULT_DAMAGE;
	private boolean playedOnce = false;
	private long lastHurt = 0;
	private float hurtEvery = 0.25f;
	
	///////////////
	/// METHODS ///
	///////////////
	// Constructor
	public darkHole(int newX, int newY, boolean isAllied, int damage, float duration) {
		super(theEffectType, newX, newY);
		
		// Allied?
		allied = isAllied;
		
		// Damage.
		this.damage = damage;
		
		// Duration
		this.setAnimationDuration(duration);
		
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
		/*sound s = new sound(effectSound2);
		s.setPosition(getIntX(), getIntY(), sound.DEFAULT_SOUND_RADIUS);
		s.start();*/
		
		// Make adjustments on hitbox if we're in topDown.
		setHeight(getDefaultHeight());
		setWidth(getDefaultWidth());
		setHitBoxAdjustmentY(getDefaultHitBoxAdjustmentY());
		
		// Add to all holes.
		allHoles.add(this);

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
			ArrayList<unit> hurtUnits = unit.getUnitsInRadius(getIntX() + getWidth()/2, getIntY() + getHeight()/2, getWidth()/2 - leniency);
			if(hurtUnits != null && time.getTime() - lastHurt > hurtEvery*1000) {
				lastHurt = time.getTime();
				for(int i = 0; i < hurtUnits.size(); i++) {
					if(hurtUnits.get(i) instanceof player && !allied) {
						hurtUnits.get(i).hurt(damage, 1f);
					}
					else if(allied) {
						hurtUnits.get(i).hurt(damage, 1f);
					}
				}
			}
		}
	}
	
	// Respond to destroy
	@Override
	public void respondToDestroy() {
		if(allHoles.contains(this)) allHoles.remove(this);
	}
	
	// Draw the effect
	@Override
	public void drawObject(Graphics g) {
		
		// Of course only draw if the animation is not null.
		if(getCurrentAnimation() != null) {
			float alpha = 1;//1f - fog.fogLevel;
			if(alpha<.5f) alpha = 0f;
			Graphics2D g2d = (Graphics2D) g.create();
			g2d.setComposite(AlphaComposite.SrcOver.derive(alpha));
			g2d.drawImage(getCurrentAnimation().getCurrentFrame(), 
					getDrawX(), 
					getDrawY(), 
					(int)(gameCanvas.getScaleX()*getObjectSpriteSheet().getSpriteWidth() + 1), 
					(int)(gameCanvas.getScaleY()*getObjectSpriteSheet().getSpriteHeight() + 1), 
					null);
		}
		
		// Draw the hitbox of the image in green.
		if(showHitBox) {
			g.setColor(Color.green);
			g.drawRect(getDrawX() - (int)(gameCanvas.getScaleX()*(- (getObjectSpriteSheet().getSpriteWidth()/2 - getWidth()/2) - getHitBoxAdjustmentX())),
					   getDrawY() - (int)(gameCanvas.getScaleY()*(- (getObjectSpriteSheet().getSpriteHeight()/2 - getHeight()/2) - getHitBoxAdjustmentY())), 
					   (int)(gameCanvas.getScaleX()*getWidth() + 1), 
					   (int)(gameCanvas.getScaleY()*getHeight() + 1));
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
