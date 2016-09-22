package zones;

import java.awt.Point;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import UI.tooltipString;
import drawing.drawnObject;
import modes.platformer;
import modes.topDown;
import sounds.music;
import terrain.chunk;
import terrain.groundTile;
import units.player;
import utilities.intTuple;
import utilities.saveState;
import zones.farmLand.forest;
import zones.farmLand.spiderCave;
import zones.farmTomb.farmTomb;
import zones.sheepFarm.sheepFarm;

public abstract class zone {
	
	// List of all zones.
	private static ArrayList<zone> allZones = new ArrayList<zone>();
	
	// Current zone
	private static zone currentZone;
	
	// Fields
	private String parentName;
	private String name;
	
	// Has the current zone been loaded once before?
	public static boolean loadedOnce = false;
	
	// Zone loaded?
	protected boolean zoneLoaded = false;
	
	// Mode
	private String mode;
	
	// Constructor
	public zone(String newName, String parentName) {
		
		// Set the name.
		setName(newName);
		
		// Set parent name.
		setParentName(parentName);
		
		// Add it to our list.
		allZones.add(this);
	}
	
	// Return the start zone.
	public static zone getStartZone() {
		return sheepFarm.getZone();
	}
	
	// Load the zone.
	public void loadZone() {
		currentZone = this;
		loadSpecificZoneStuff();
		
		// Sort chunks.
		chunk.sortChunks();
		groundTile.sortGroundTiles();
		
		loadedOnce = true;
		zoneLoaded = true;
	}
	
	// Load specific zone stuff
	public abstract void loadSpecificZoneStuff();
	
	// Switch zones.
	public static void switchZones(player p, zone a, zone b, int x, int y, String direction) {	
		
		// If we are in the same zone, just move the player.
		if(currentZone != null && currentZone.getName().equals(b.getName())) {
			p.setDoubleX(x);
			p.setDoubleY(y);
			p.setFacingDirection(direction);
		}
		
		// If we are leaving a zone into a new one
		else {
			// Save the player's progress before switching zones..
			saveState.createSaveState();
			
			// Re-create the player in the new zone.
			drawnObject.dontReloadTheseObjects = new ArrayList<drawnObject>();
			music.endAll();
			groundTile.groundTiles = new CopyOnWriteArrayList<chunk>();
			loadedOnce = false;
			player.loadPlayer(player.getPlayer(), b, x, y, direction);
			
			// If we are going into the cave for the first time, display tooltip.
			if(spiderCave.enteredSpiderCaveBefore != null && !spiderCave.enteredSpiderCaveBefore.isCompleted()) {
				tooltipString t = new tooltipString("In platformer mode, press 'w' to jump.");
				spiderCave.enteredSpiderCaveBefore.setCompleted(true);
				saveState.setQuiet(true);
				saveState.createSaveState();
				saveState.setQuiet(false);
			}
			else {
				// Save the player in the new zone.
				player.getPlayer().lastWell = new Point(x,y);
				saveState.createSaveState();
			}
		}
	}
	
	// Get zone by name.
	public static zone getZoneByName(String s) {
		for(int i=0; i < allZones.size(); i++) {
			zone currentZone = allZones.get(i);
			if(currentZone.getName().equals(s)) return currentZone; // Found the zone!
		}
		return null; // Didn't find the zone.
	}
	
	// TODO: Figure out a better way to initiate these variables so we
	// don't have to keep coming back and adding them here.
	// Initiate zones.
	public static void initiate() {
		
		// Sheep Farms
		sheepFarm.setZone(new sheepFarm());
		farmTomb.setZone(new farmTomb());
		forest.setZone(new forest());
		spiderCave.setZone(new spiderCave());
	}
	
	// Get the player location in the zone.
	public abstract intTuple getDefaultLocation();
	
	// Update.
	public void update() {
		
	}

	/////////////////////////
	// Getters and setters //
	/////////////////////////
	
	// Set mode
	public void setMode(String s) {
		if(s.equals("topDown")) {
			topDown.setMode();
		}
		if(s.equals("platformer")) {
			platformer.setMode();
		}
		
		mode = s;
	}
	public static zone getCurrentZone() {
		return currentZone;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isZoneLoaded() {
		return zoneLoaded;
	}

	public void setZoneLoaded(boolean zoneLoaded) {
		this.zoneLoaded = zoneLoaded;
	}

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	public abstract String getMode();
}