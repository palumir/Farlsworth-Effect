package doodads.general;

import java.util.ArrayList;

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
}