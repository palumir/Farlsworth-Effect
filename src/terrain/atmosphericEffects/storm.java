package terrain.atmosphericEffects;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

import doodads.general.lightSource;
import drawing.drawnObject;
import drawing.gameCanvas;
import drawing.spriteSheet;
import drawing.animation.animation;
import drawing.spriteSheet.spriteSheetInfo;
import drawing.userInterface.interfaceObject;
import effects.effectTypes.darkHole;
import effects.effectTypes.rainFall;
import effects.effectTypes.rainSplash;
import sounds.sound;
import terrain.generalChunkType;
import units.player;
import utilities.imageUtils;
import utilities.time;
import utilities.utility;

public class storm extends interfaceObject {
	
	// All fog effects in place.
	public static storm currentStorm;
	
	// Animation
	private animation rainAnimation;
	
	// Sprite stuff.
	private static String DEFAULT_CHUNK_SPRITESHEET = "images/effects/rain.png";
	
	// Dimensions
	private static int DEFAULT_SPRITE_WIDTH = gameCanvas.getDefaultWidth();
	private static int DEFAULT_SPRITE_HEIGHT = gameCanvas.getDefaultHeight();

	// Topdown
	private static int DEFAULT_TOPDOWN_WIDTH = gameCanvas.getDefaultWidth();
	private static int DEFAULT_TOPDOWN_HEIGHT = gameCanvas.getDefaultHeight();

	// Platformer.
	private static int DEFAULT_PLATFORMER_HEIGHT = gameCanvas.getDefaultHeight();
	private static int DEFAULT_PLATFORMER_WIDTH = gameCanvas.getDefaultWidth();
	
	// The actual type.
	private static spriteSheet rainSheet = new spriteSheet(new spriteSheetInfo(
			DEFAULT_CHUNK_SPRITESHEET, 
			DEFAULT_SPRITE_WIDTH, 
			DEFAULT_SPRITE_HEIGHT,
			0,
			0
			));
	
	public storm() {
		super(null, 0, 0, gameCanvas.getDefaultWidth(), gameCanvas.getDefaultHeight());
		currentStorm = this;
		
		// Be behind fog.
		setZ(-5);
		
		// Add rain animation
		rainAnimation = new animation("rain",rainSheet.getAnimation(0), 0, 5, 0.4f);
	}
	
	// Paint the fog
	@Override
	public void drawObject(Graphics g) {
			
			// Draw the fog.
			//g.drawImage(rainAnimation.getCurrentFrame(),0,0,gameCanvas.getActualWidth(),gameCanvas.getActualHeight(),null);
			g.setColor(Color.white);
		
	}
	
	// Update.
	public void update() {
		if(rainAnimation != null) rainAnimation.playAnimation();
		playSound();
		makeRainSplashes();
		doLightning();
	}
	
	long lastLightning = 0;
	float lightningEvery = 10f;
	long lastMiniStrike = 0;
	float miniStrikeEvery = .03f;
	float miniStrikeLastsFor = .03f;
	int howManyStrikes = 0;
	int howManyStrikesTotal = 0;
	int maxStrikes = 4;
	boolean strikingCurrently = false;
	
	// Lightning odd color
	Color lightningOddColor = new Color(211,228,248);
	Color lightningEvenColor = Color.white;
	
	// Do lightning
	public void doLightning() {
		
		// Start lightning
		if(!strikingCurrently && time.getTime() - lastLightning > lightningEvery*1000) {
			strikingCurrently = true;
			
			// Last lightning
			lastLightning = time.getTime();
			
			// Set amount of strikes
			howManyStrikesTotal = maxStrikes;
			howManyStrikes = 0;
			lastMiniStrike = 0;
		}
		
		// Actually do the lightning
		if(strikingCurrently) {
			
			// Mini strike
			if(howManyStrikes <= howManyStrikesTotal && time.getTime() - lastMiniStrike > miniStrikeEvery*1000) {
				lastMiniStrike = time.getTime();
				Color c = lightningEvenColor;
				if(howManyStrikes%2 != 0) c = lightningOddColor;
				lightning l = new lightning(miniStrikeLastsFor, c);
				howManyStrikes++;
			}
			else if(howManyStrikes > howManyStrikesTotal) { 
				strikingCurrently = false;
			}
			
		}
		
	}
	
	long lastRainSound = 0;
	String rainSound = "sounds/effects/weather/rain.wav";
	float playEvery = 16f;
	
	// Play sound.
	public void playSound() {
		
		// Play rain sound
		if(lastRainSound == 0) {
			lastRainSound = time.getTime();
			sound s = new sound(rainSound);
			s.start();
		}
		
		else if(time.getTime() - lastRainSound > playEvery*1000) {
			lastRainSound = time.getTime();
			sound s = new sound(rainSound);
			s.start();
		}
	}
	
	// How often to make rainsplash
	float howOftenToRainSplash = 0.015f;
	long lastRainSplash = 0;
	
	// Make rain splashes
	public void makeRainSplashes() {
		
		// Splashes
		if(time.getTime() - lastRainSplash > howOftenToRainSplash*1000) {
			lastRainSplash = time.getTime();
			player currPlayer = player.getCurrentPlayer();
			int middleX = currPlayer.getIntX() + getWidth()/2 - rainSplash.getDefaultWidth()/2;
			int middleY = currPlayer.getIntY() + getHeight()/2 - rainSplash.getDefaultHeight()/2;
			int randomX = middleX - (utility.RNG.nextInt(gameCanvas.getDefaultWidth()));
			int randomY = middleY - (utility.RNG.nextInt(gameCanvas.getDefaultHeight()));
			rainSplash r = new rainSplash(randomX,randomY);
		}
		
		
		// Do rain drops
		for(int i = 0; i < 7; i++) {
			player currPlayer = player.getCurrentPlayer();
			int middleX = currPlayer.getIntX() + getWidth()/2 - rainFall.getDefaultWidth()/2;
			int middleY = currPlayer.getIntY() + getHeight()/2- rainFall.getDefaultHeight()/2;
			int randomX = middleX - (utility.RNG.nextInt(gameCanvas.getDefaultWidth()));
			int randomY = middleY - (utility.RNG.nextInt(gameCanvas.getDefaultHeight()));
			rainFall f = new rainFall(randomX,randomY);
		}
	}
	
	// Delete current fog
	@Override
	public void respondToDestroy() {
	}

	public static void initiate() {
	}
}