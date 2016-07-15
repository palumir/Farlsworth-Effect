package terrain.atmosphericEffects;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.Random;

import doodads.general.lightSource;
import drawing.gameCanvas;
import effects.effectTypes.darkHole;
import utilities.imageUtils;
import utilities.time;

public class fog {
	
	// Fog percentage.
	public static float fogLevel = 0; // between 0.0 and 1.0
	public static float fogLevelMax = 0;
	public static float oldLevel = 0;
	
	// Timer stuff.
	private static long startFade = 0;
	private static float fadeTime = 0;
	
	// Paint the fog
	public static void paintFog(Graphics2D g2) {
		if(fogLevel != 0f) {
			
			// Create an image the size of the screen to draw on.
			BufferedImage img = new BufferedImage(gameCanvas.getActualWidth(),gameCanvas.getActualHeight(), BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = img.createGraphics();
			
			// Regular composite
			Composite oldComposite = g.getComposite();
			
			// Draw the fog.
			float alpha = fogLevel;
			Color black = new Color(0, 0, 0, alpha); //Black 
			g.setComposite(AlphaComposite.Src);
			g.setPaint(black);
			g.fillRect(0,0,gameCanvas.getActualWidth(),gameCanvas.getActualHeight());
			
			// Draw the lightsources.
			g.setComposite(AlphaComposite.Clear);
			for(int i = 0; i < lightSource.lightSources.size(); i++) {
				lightSource l = lightSource.lightSources.get(i);
				g.fillOval(l.getDrawX() + l.getWidth()/2 - l.getLightRadius(), l.getDrawY() + l.getHeight()/2 - l.getLightRadius(), l.getLightRadius()*2, l.getLightRadius()*2);
			}
			
			g2.drawImage(img,0,0,null);
		}
	}
	
	// Update.
	public static void update() {
		if(fogLevel < fogLevelMax) {
			fogLevel = fogLevelMax*((time.getTime() - startFade)/(fadeTime*1000));
			if((time.getTime() - startFade)/(fadeTime*1000) >= 1) fogLevel = fogLevelMax;
		}
		else if(fogLevel > fogLevelMax) {
			 fogLevel = oldLevel*(1 - ((time.getTime() - startFade)/(fadeTime*1000)));
			 if((time.getTime() - startFade)/(fadeTime*1000) >= 1) fogLevel = fogLevelMax;
		}
	}
	
	// Set to
	public static void setTo(float level) {
		fogLevelMax = level;
		fogLevel = level;
		oldLevel = level;
	}
	
	// Fade to a certain alpha over time.
	public static void fadeTo(float level, float newTime) {
		if(level != fogLevelMax || fadeTime != newTime) {
			startFade = time.getTime();
			fadeTime = newTime;
			if(level < fogLevel) oldLevel = fogLevel;
			fogLevelMax = level;
		}
	}

	public static void initiate() {
		fogLevelMax = 0;
		fogLevel = 0;
	}
}