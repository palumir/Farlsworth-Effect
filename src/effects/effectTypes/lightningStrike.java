package effects.effectTypes;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Random;

import drawing.camera;
import drawing.drawnObject;
import drawing.gameCanvas;
import drawing.spriteSheet;
import drawing.spriteSheet.spriteSheetInfo;
import effects.effect;
import effects.effectType;
import modes.mode;
import sounds.sound;
import terrain.atmosphericEffects.lightning;
import units.humanType;
import units.unit;
import units.unitType;
import utilities.time;
import utilities.utility;
import zones.zone;

public class lightningStrike extends effect {
	
	// Default dimensions.
	public static int DEFAULT_SPRITE_WIDTH = 110;
	public static int DEFAULT_SPRITE_HEIGHT = 651;
	
	// Platformer real dimensions
	public static int DEFAULT_PLATFORMER_HEIGHT = DEFAULT_SPRITE_WIDTH;
	public static int DEFAULT_PLATFORMER_WIDTH = DEFAULT_SPRITE_HEIGHT;
	public static int DEFAULT_PLATFORMER_ADJUSTMENT_Y = 0;
	
	// TopDown real dimensions
	public static int DEFAULT_TOPDOWN_HEIGHT = 651;
	public static int DEFAULT_TOPDOWN_WIDTH = 110;
	public static int DEFAULT_TOPDOWN_ADJUSTMENT_Y = 0;
	
	// effect
	static String lightningSound = "sounds/effects/weather/lightningStrike.wav";
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default name.
	private static String DEFAULT_EFFECT_NAME = "lightningStrike";
	
	// Effect sprite stuff.
	private static String DEFAULT_EFFECT_SPRITESHEET = "images/effects/lightning.png";
	
	// Duration
	private static float DEFAULT_ANIMATION_DURATION = 1f;
	
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
	public lightningStrike(int newX, int newY) {
		super(theEffectType, newX, newY);
		
		// Force in front
		forceInFront = true;
		lastLightning = time.getTime();
		
		// Make adjustments on hitbox if we're in topDown.
		setHeight(getDefaultHeight());
		setWidth(getDefaultWidth());
		setHitBoxAdjustmentY(getDefaultHitBoxAdjustmentY());

	}
	
	// Update
	@Override 
	public void doSpecificEffectStuff() {
		doLightning();
	}
	
	long lastLightning = 0;
	float lightningEvery = 0f;
	long lastMiniStrike = 0;
	float miniStrikeEvery = .03f;
	float miniStrikeLastsFor = .03f;
	int howManyStrikes = 0;
	int howManyStrikesTotal = 0;
	float lastsFor = 0.5f;
	int maxStrikes = 4;
	boolean strikingCurrently = false;
	
	// Lightning odd color
	Color lightningOddColor = new Color(211,228,248);
	Color lightningEvenColor = Color.white;
	
	// Do lightning
	public void doLightning() {	
		// Mini strike
		if(time.getTime() - lastLightning < lastsFor*1000 && time.getTime() - lastMiniStrike > miniStrikeEvery*1000) {
			lastMiniStrike = time.getTime();
			Color c = lightningEvenColor;
			if(howManyStrikes%2 != 0) c = lightningOddColor;
			lightning l = new lightning(miniStrikeLastsFor, c);
			howManyStrikes++;
		}
	}
	
	// Strike lightning at:
	public static void strikeAt(int x, int y) {
		new lightningStrike(x-DEFAULT_SPRITE_WIDTH/2, y-DEFAULT_SPRITE_HEIGHT);
		
		// Play sound
		sound s = new sound(lightningSound);
		s.setPosition(x, y, sound.DEFAULT_SOUND_RADIUS);
		s.start();
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
		float timeThatHasPassed = (time.getTime() - timeStarted)/1000f; // in seconds
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
