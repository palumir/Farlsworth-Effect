package terrain.atmosphericEffects;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import doodads.general.lightSource;
import drawing.drawnObject;
import drawing.gameCanvas;
import utilities.time;

public class fog extends atmosphericEffect {
	
	// All fog effects in place.
	public static ArrayList<fog> allFog;
	
	public fog() {
		super();
		
		// Set z
		setZ(-2);
	}

	// Fog percentage.
	public float fogLevel = 0; // between 0.0 and 1.0
	public float fogLevelMax = 0;
	public float oldLevel = 0;
	
	// Timer stuff.
	private long startFade = 0;
	private float fadeTime = 0;
	
	// Image we will draw on for the fog.
	BufferedImage img;
	
	// Paint the fog
	@Override
	public void drawObject(Graphics g) {
		if(this == getMaxFog()) {
			
			// Create an image the size of the screen to draw on.
			if(img == null || (gameCanvas.getActualWidth() != img.getWidth()) || (gameCanvas.getActualHeight() != img.getHeight())) {
				img = new BufferedImage(gameCanvas.getActualWidth(),gameCanvas.getActualHeight(), BufferedImage.TYPE_INT_ARGB);
			}
			Graphics2D g2 = img.createGraphics();
			
			// Draw the fog.
			float alpha = fogLevel;
			Color black = new Color(0, 0, 0, alpha); //Black 
			g2.setComposite(AlphaComposite.Src);
			g2.setPaint(black);
			g2.fillRect(0,0,gameCanvas.getActualWidth(),gameCanvas.getActualHeight());
			
			lightSource.drawLightSources(g2);
			
			g.drawImage(img,0,0,null);
		}
	}
	
	// Update.
	public void update() {
		if(fogLevel < fogLevelMax) {
			if((time.getTime() - startFade)/(fadeTime*1000) >= 1) fogLevel = fogLevelMax;
			else fogLevel = oldLevel + (fogLevelMax - oldLevel)*((time.getTime() - startFade + 1)/(fadeTime*1000));
		}
		else if(fogLevel > fogLevelMax) {
			 if((time.getTime() - startFade)/(fadeTime*1000) >= 1) fogLevel = fogLevelMax;
			 else fogLevel = fogLevelMax + (oldLevel - fogLevelMax)*(1 - ((time.getTime() - startFade + 1)/(fadeTime*1000)));

		}
	}
	
	// Set to
	public void setTo(float level) {	
		fogLevelMax = level;
		fogLevel = level;
		oldLevel = level;
		allFog = new ArrayList<fog>();
		allFog.add(this);
	}
	
	// Delete current fog
	@Override
	public void respondToDestroy() {
		if(allFog.contains(this)) allFog.remove(this);
	}
	
	// Get max fog
	public fog getMaxFog() {
		fog maxFog = null;
		for(int i=0; i < allFog.size(); i++) {
			if(maxFog == null || allFog.get(i).fogLevel > maxFog.fogLevel) maxFog = allFog.get(i);
		}
		return maxFog;
	}
	
	// Fade to a certain alpha over time.
	public void fadeTo(float level, float newTime) {
			startFade = time.getTime();
			fadeTime = newTime;
			oldLevel = fogLevel;
			fogLevelMax = level;
			if(!allFog.contains(this)) allFog.add(this);
			if(!drawnObject.objects.contains(this)) drawnObject.objects.add(this);
	}

	public static void initiate() {
		allFog = new ArrayList<fog>();
	}
}