package main;

import UI.interfaceObject;
import UI.tooltipString;
import cinematics.cinematic;
import doodads.general.lightSource;
import doodads.general.well;
import drawing.drawnObject;
import drawing.gameCanvas;
import interactions.interactBox;
import sounds.music;
import sounds.sound;
import terrain.chunk;
import terrain.region;
import terrain.atmosphericEffects.atmosphericEffect;
import terrain.atmosphericEffects.fog;
import terrain.atmosphericEffects.storm;
import units.player;
import units.unit;
import units.unitType;
import units.developer.developer;
import utilities.levelSave;
import utilities.saveState;
import utilities.time;
import utilities.userMouseTracker;
import zones.zone;

// The class that initiates the program.
public class main {
	
	// The actual function that initiates the program.
	public static void main(String[] args) {
		
		// Create the game canvas.
		gameCanvas gameCanvas = new gameCanvas();
		
		// Start the game for the first time.
		restartGame(null);
		
	}
	
	// Restart game
	public static void restartGame(String s) {
		
		// Create the player
		//player.setDeveloper(true);
		
		// Create the player at the last bottle?
		if(s!= null && s.equals("respawnAtWell")) {
			player p = player.loadPlayer(null,null,0,0,"Up");
			p.lastSaveBottle = null;
			p.setDoubleX(p.lastWell.getX());
			p.setDoubleY(p.lastWell.getY());
			well.refreshPlayer("respawnAtWell");
		}
		else {
			player p = player.loadPlayer(null,null,0,0,"Up");
		}
		
		// Saved game?
		if(s!=null) {
			
			// Restart due to saving.
			if(s.equals("Save")) {
				tooltipString t = new tooltipString(saveState.DEFAULT_GAME_SAVED_TEXT);
			}
		
			if(s.equals("respawnAtSaveBottle") || s.equals("respawnAtWell")) {
				
				// Load zone if we're a developer
				if(player.isDeveloper() && developer.levelName != null) {
					levelSave.loadSaveState(developer.levelName);
				}
			}
		}
	}
	
	// Boolean
	public static boolean actionsPaused = false;
	
	// Game loop.
	public static void updateGame() {
		
		// Tick timer
		time.tickTimer();
		
		// Update the mouse tracker.
		userMouseTracker.update();
		
		// Update the cinematic if one is in progress.
		cinematic.updateCurrentCinematic();
		
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