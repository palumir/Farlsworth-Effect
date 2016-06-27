package terrain.atmosphericEffects;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.Random;

import drawing.gameCanvas;
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
			BufferedImage img = new BufferedImage(gameCanvas.getActualWidth(),gameCanvas.getActualHeight(), BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = img.createGraphics();
			float alpha = fogLevel;
			Color color = new Color(0, 0, 0, alpha); //Black 
			g.setComposite(AlphaComposite.Src);
			g.setPaint(color);
			g.fillRect(0,0,gameCanvas.getActualWidth(),gameCanvas.getActualHeight());
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
	
	// Fade to a certain alpha over time.
	public static void fadeTo(float level, float newTime) {
		startFade = time.getTime();
		fadeTime = newTime;
		if(level < fogLevel) oldLevel = fogLevel;
		fogLevelMax = level;
	}

	public static void initiate() {
		fogLevelMax = 0;
		fogLevel = 0;
	}
}