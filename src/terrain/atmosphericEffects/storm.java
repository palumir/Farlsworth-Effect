package terrain.atmosphericEffects;

import java.awt.Color;
import java.awt.Graphics;

import drawing.gameCanvas;
import drawing.animation.animation;
import effects.effectTypes.rainFall;
import sounds.sound;
import units.player;
import utilities.time;
import utilities.utility;

public class storm extends atmosphericEffect {
	
	// All fog effects in place.
	public static storm currentStorm;
	
	// Animation
	private static animation rainAnimation;
	
	// Sprite stuff.
	private static String DEFAULT_CHUNK_SPRITESHEET = "images/effects/rain.png";
	
	// Fade over.
	private float fadeOver = 0;
	private long stormStart = 0;
	
	public storm() {
		super();
		currentStorm = this;
		
		// Storm start time.
		stormStart = time.getTime();
		
		// Play rain.
		sound s = new sound(rainSound);
		s.setLoop(true);
		s.start();
		
		// Be behind fog.
		setZ(-5);
	}
	
	public storm(float fadeOver) {
		super();
		currentStorm = this;
		
		// Storm start time.
		stormStart = time.getTime();
		this.fadeOver = fadeOver;
		
		// Play rain
		sound s = new sound(rainSound);
		s.setLoop(true);
		s.fadeIn(fadeOver);
		s.start();
		
		// Be behind fog.
		setZ(-5);
	}
	
	// Paint the fog
	@Override
	public void drawObject(Graphics g) {
		
		// Draw the fog.
		g.setColor(Color.white);
		
	}
	
	// Update.
	public void update() {
		if(rainAnimation != null) rainAnimation.playAnimation();
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
		if(lastLightning == 0) lastLightning = time.getTime();
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
	public static String rainSound = "sounds/effects/weather/rain.wav";
	
	// How often to make rainsplash
	float howOftenToRainSplash = 0.015f;
	long lastRainSplash = 0;
	
	// Make rain splashes
	public void makeRainSplashes() {
		
		// Fade in.
		float fadePercent = ((time.getTime() - stormStart + 1)/(fadeOver*1000));
		if(fadePercent > 1) fadePercent = 1;
		if(fadeOver == 0) fadePercent = 1;
	
		// Do rain drops.
		for(int i = 0; i < 6*fadePercent; i++) {
			player currPlayer = player.getPlayer();
			int middleX = currPlayer.getIntX() + getWidth()/2 - rainFall.getDefaultWidth()/2;
			int middleY = currPlayer.getIntY() + getHeight()/2- rainFall.getDefaultHeight()/2;
			int randomX = middleX - (utility.RNG.nextInt(gameCanvas.getDefaultWidth()*2)) + gameCanvas.getDefaultWidth()/2;
			int randomY = middleY - (utility.RNG.nextInt(gameCanvas.getDefaultHeight()*2)) + gameCanvas.getDefaultHeight()/2;
			rainFall f = new rainFall(randomX,randomY);
		}
	}
	
	// Is raining?
	public static boolean isRaining() {
		return currentStorm != null;
	}
	
	// Delete current fog
	@Override
	public void respondToDestroy() {
	}

	public static void initiate() {
	}
}