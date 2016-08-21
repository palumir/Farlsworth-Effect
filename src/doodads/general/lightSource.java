package doodads.general;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.util.ArrayList;

import drawing.gameCanvas;
import terrain.chunk;
import terrain.generalChunkType;

public class lightSource extends chunk {
	
	// List of all lightsources
	public static ArrayList<lightSource> lightSources;
	
	///////////////
	/// FIELDS ////
	///////////////
	private int lightRadius = 100;

	///////////////
	/// METHODS ///
	///////////////
	// Constructor
	public lightSource(generalChunkType typeReference, int newX, int newY) {
		super(typeReference, newX, newY, 0, 0);
		lightSources.add(this);
	}
	
	// Destroy from list of lightsources
	@Override
	public void respondToDestroy() {
		if(lightSources.contains(this)) lightSources.remove(this);
	}

	public int getLightRadius() {
		return lightRadius;
	}

	public void setLightRadius(int lightRadius) {
		this.lightRadius = lightRadius;
	}
	
	// Initiate
	public static void initiate() {
		lightSources = new ArrayList<lightSource>();
	}
	
	// Draw light sources
	public static void drawLightSources(Graphics2D g2) {
		
		// Draw the lightsources.
		g2.setComposite(AlphaComposite.Clear);
		for(int i = 0; i < lightSource.lightSources.size(); i++) {
			lightSource l = lightSource.lightSources.get(i);
			g2.fillOval(l.getDrawX() + (int)(gameCanvas.getScaleX()*(l.getWidth()/2 - l.getLightRadius())), 
					l.getDrawY() + (int)(gameCanvas.getScaleY()*(l.getHeight()/2 - l.getLightRadius())), 
					(int)(gameCanvas.getScaleX()*(l.getLightRadius()*2)), 
					(int)( gameCanvas.getScaleY()*(l.getLightRadius()*2)));
		}
	}
}