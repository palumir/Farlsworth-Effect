package effects.effectTypes;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

import drawing.camera;
import drawing.drawnObject;
import drawing.gameCanvas;
import drawing.spriteSheet;
import drawing.spriteSheet.spriteSheetInfo;
import effects.effect;
import effects.effectType;
import effects.buffs.darkSlow;
import modes.mode;
import sounds.sound;
import terrain.atmosphericEffects.lightning;
import units.humanType;
import units.player;
import units.unit;
import units.unitType;
import utilities.time;
import utilities.utility;
import zones.zone;

public class lightningStrike extends effect {
	
	// Default dimensions.
	public static int DEFAULT_SPRITE_WIDTH = 72*2;
	public static int DEFAULT_SPRITE_HEIGHT = 651;
	
	// Platformer real dimensions
	public static int DEFAULT_PLATFORMER_HEIGHT = DEFAULT_SPRITE_HEIGHT;
	public static int DEFAULT_PLATFORMER_WIDTH = DEFAULT_SPRITE_WIDTH;
	public static int DEFAULT_PLATFORMER_ADJUSTMENT_Y = 0;
	
	// TopDown real dimensions
	public static int DEFAULT_TOPDOWN_HEIGHT = DEFAULT_SPRITE_HEIGHT;
	public static int DEFAULT_TOPDOWN_WIDTH = DEFAULT_SPRITE_WIDTH;
	public static int DEFAULT_TOPDOWN_ADJUSTMENT_Y = 0;
	
	// effect
	public static String lightningSound = "sounds/effects/weather/lightningStrike.wav";
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default name.
	private static String DEFAULT_EFFECT_NAME = "lightningStrike";
	
	// Effect sprite stuff.
	private static String DEFAULT_EFFECT_SPRITESHEET = "images/effects/lightning.png";
	
	// Duration
	private static float DEFAULT_ANIMATION_DURATION = 1f;
	
	// Default lightning damage.
	static int DEFAULT_LIGHTNING_DAMAGE = 6;
	public static int DEFAULT_LIGHTNING_RADIUS = 20;
	
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
	
	// Timing and lightning animation stuff.
	long startLightning = 0;
	float lightningEvery = 0f;
	long lastMiniStrike = 0;
	float miniStrikeEvery = .03f;
	float miniStrikeLastsFor = .03f;
	int howManyStrikes = 0;
	int howManyStrikesTotal = 0;
	float lastsFor = 0.5f;
	int maxStrikes = 4;
	boolean strikingCurrently = false;
	effect preLightning = null;
	long startPreLightning = 0;
	float preLightningLastsFor = 1.6f;
	
	// Lightning odd color
	Color lightningOddColor = new Color(211,228,248);
	Color lightningEvenColor = Color.white;
	
	// Are we allied?
	private boolean allied = false;
	
	///////////////
	/// METHODS ///
	///////////////
	// Constructor
	public lightningStrike(int newX, int newY) {
		super(theEffectType, newX-DEFAULT_SPRITE_WIDTH/2, newY-DEFAULT_SPRITE_HEIGHT);
		
		// Don't draw at first.
		setDrawObject(false);
		
		// Has no timer.
		hasATimer = false;
		
		// Force in front
		setForceInFront(true);
		
		// Make adjustments on hitbox if we're in topDown.
		setHeight(getDefaultHeight());
		setWidth(getDefaultWidth());
		setHitBoxAdjustmentY(getDefaultHitBoxAdjustmentY());

	}
	
	public lightningStrike(int newX, int newY, boolean muted) {
		super(theEffectType, newX-DEFAULT_SPRITE_WIDTH/2, newY-DEFAULT_SPRITE_HEIGHT);
		
		// Don't draw at first.
		setDrawObject(false);
		
		// Has no timer.
		hasATimer = false;
		
		// Muted.
		this.muted = muted;
		
		// Force in front
		setForceInFront(true);
		
		// Make adjustments on hitbox if we're in topDown.
		setHeight(getDefaultHeight());
		setWidth(getDefaultWidth());
		setHitBoxAdjustmentY(getDefaultHitBoxAdjustmentY());

	}
	
	// Update
	@Override 
	public void update() {
		doLightning();
	}

	public void hurtPeople() {
		// If someone is in the explosion radius, hurt.
		ArrayList<unit> hurtUnits = unit.getUnitsInRadius(getIntX()+DEFAULT_SPRITE_WIDTH/2, getIntY()+DEFAULT_SPRITE_HEIGHT, DEFAULT_LIGHTNING_RADIUS);
		if(hurtUnits!=null) {
			for(int i = 0; i < hurtUnits.size(); i++) {
				if(hurtUnits.get(i) instanceof player && !allied) {
					hurtUnits.get(i).hurt(DEFAULT_LIGHTNING_DAMAGE, 1f);
				}
				if(!(hurtUnits.get(i) instanceof player) && allied) {
					hurtUnits.get(i).hurt(DEFAULT_LIGHTNING_DAMAGE, 1f);
				}
			}
		}
	}
	
	boolean muted = false;
	// Do lightning
	public void doLightning() {	
		
		// Spawn prelightning
		if(preLightning == null && !strikingCurrently) {
			preLightning = new lightningAboutToStrike(getIntX()+DEFAULT_SPRITE_WIDTH/2-lightningAboutToStrike.DEFAULT_SPRITE_WIDTH/2, 
					getIntY()+DEFAULT_SPRITE_HEIGHT-lightningAboutToStrike.DEFAULT_SPRITE_HEIGHT/2, true);
			startPreLightning = time.getTime();
		}
		
		// Strike!
		if(!strikingCurrently && time.getTime() - startPreLightning > preLightningLastsFor*1000) {
			strikingCurrently = true;
			preLightning.destroy();
			preLightning = null;
			startLightning = time.getTime();
			
			// Draw the lightning
			setDrawObject(true);
			
			if(!muted) {
				// Play sound
				sound s = new sound(lightningSound);
				s.setPosition(getIntX()+DEFAULT_SPRITE_WIDTH/2, getIntY()+DEFAULT_SPRITE_HEIGHT, sound.DEFAULT_SOUND_RADIUS);
				s.start();
			}
			
			// Hurt units in the area.
			hurtPeople();
		}
		
		// Once we're done. Destroy.
		if(strikingCurrently && time.getTime() - startLightning > lastsFor*1000) {
			this.destroy();
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
	
	// Draw the unit. 
	@Override
	public void drawObject(Graphics g) {
		
		// Set the alpha depending on how close the animation is to over.
		float timeThatHasPassed = (time.getTime() - startLightning)/1000f; // in seconds
		float alpha = 1f - timeThatHasPassed/animationDuration;
		if(alpha < 0) alpha = 0;
		if(alpha > 1) alpha = 1;
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setComposite(AlphaComposite.SrcOver.derive(alpha));
		g2d.drawImage(getCurrentAnimation().getCurrentFrame(), 
				getDrawX(), 
				getDrawY(), 
				(int)(gameCanvas.getScaleX()*DEFAULT_SPRITE_WIDTH), 
				(int)(gameCanvas.getScaleY()*DEFAULT_SPRITE_HEIGHT), 
				null);
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
