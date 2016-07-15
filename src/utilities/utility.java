package utilities;

import java.util.Random;

import doodads.general.lightSource;
import drawing.drawnObject;
import interactions.event;
import interactions.interactBox;
import interactions.quest;
import items.item;
import sounds.music;
import terrain.chunk;
import terrain.region;
import terrain.atmosphericEffects.fog;
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
	
	// Update the actual game.
	// No where else to put this.
	public static void updateGame() {
		
		// Update units.
		if(unit.getAllUnits() != null) {
			for(int i = 0; i < unit.getAllUnits().size(); i++) {
				drawnObject d = unit.getAllUnits().get(i);
				d.update();
			}
		}
		
		// Update other drawn objects.
		if(drawnObject.objects != null) {
			for(int i = 0; i < drawnObject.objects.size(); i++) {
				drawnObject d = drawnObject.objects.get(i);
				if(!(d instanceof unit)) d.update();
			}
		}
		
		// Update the current zone.
		player currPlayer = player.getCurrentPlayer();
		if(currPlayer != null && currPlayer.getCurrentZone() != null) currPlayer.getCurrentZone().update();
		
		// Update fog.
		fog.update();
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
		drawnObject.initiate();
		interactBox.initiate();
		region.initiate();
		lightSource.initiate();
		fog.initiate();
		unit.initiate();
		unitType.initiate();
		chunk.initiate();
		zone.initiate(); 
		
		// Load save state and return
		saveState s = saveState.loadSaveState();
		
		return s;
	}
}