package utilities;

import java.util.Random;

import UI.interfaceObject;
import doodads.general.lightSource;
import drawing.drawnObject;
import interactions.interactBox;
import sounds.sound;
import terrain.chunk;
import terrain.region;
import terrain.atmosphericEffects.atmosphericEffect;
import terrain.atmosphericEffects.fog;
import terrain.atmosphericEffects.storm;
import units.player;
import units.unit;
import units.unitType;
import zones.zone;

// A general class for all utilities so we may
// call this parent class to set-up and run
// all the utilities at once.
public abstract class utility {
	
	// Random number generator.
	public static Random RNG = new Random();
	

}