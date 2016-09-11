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
	
	// Boolean
	public static boolean actionsPaused = false;
	
	// Update the actual game.
	// No where else to put this.
	public static void updateGame() {
		
		// Tick timer
		time.tickTimer();
		
		// Update the mouse tracker.
		userMouseTracker.update();
		
		// Update interface objects (but not atmospheric effects)
		if(interfaceObject.interfaceObjects != null) {
			for(int i = 0; i < interfaceObject.interfaceObjects.size(); i++) {
				interfaceObject d = interfaceObject.interfaceObjects.get(i);
				if(!(d instanceof atmosphericEffect)) {
					d.update();
				}
			}
		}
		
		// If timer is going
		if(!time.paused && !actionsPaused) {
			// Update units.
			if(unit.getAllUnits() != null) {
				for(int i = 0; i < unit.getAllUnits().size(); i++) {
					drawnObject d = unit.getAllUnits().get(i);
					if(!(d instanceof player)) d.update();
				}
			}
			
			// Update other drawn objects.
			if(drawnObject.objects != null) {
				for(int i = 0; i < drawnObject.objects.size(); i++) {
					drawnObject d = drawnObject.objects.get(i);
					if(!(d instanceof unit) && (!(d instanceof interfaceObject) || d instanceof atmosphericEffect)) d.update();
				}
			}
			
			// Update the current zone.
			player currPlayer = player.getPlayer();
			if(currPlayer != null) currPlayer.update();
			if(currPlayer != null && currPlayer.getCurrentZone() != null) currPlayer.getCurrentZone().update();
		}
		
		if(actionsPaused) {
			player currPlayer = player.getPlayer();
			if(currPlayer != null) currPlayer.update();
		}
	
	}
	
	// Pause actions
	public static void toggleActions() {
		actionsPaused = !actionsPaused;
	}
	
	// Initiate the utility. Does nothing
	// if the utility does not need to be
	// set-up.
	static void initiate() {
		// Base-line utilities do nothing.
	}
	
	// Set-up all the utilities.
	public static saveState initiateAll() {
		
		// Initiate everything
		time.initiate();
		sound.initiate();
		drawnObject.initiate();
		interfaceObject.initiate();
		fog.initiate();
		storm.initiate();
		interactBox.initiate();
		region.initiate();
		lightSource.initiate();
		unit.initiate();
		unitType.initiate();
		chunk.initiate();
		zone.initiate(); 		
		// Load save state and return
		saveState s = saveState.loadSaveState();
		
		return s;
	}
}