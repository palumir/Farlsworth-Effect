package zones;

import java.awt.Point;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import drawing.drawnObject;
import effects.interfaceEffects.tooltipString;
import main.main;
import modes.platformer;
import modes.topDown;
import sounds.music;
import sounds.sound;
import terrain.chunk;
import terrain.groundTile;
import units.player;
import utilities.intTuple;
import utilities.saveState;
import zones.farmTomb.subZones.farmTomb;
import zones.sheepFarm.subZones.farmerHouse;
import zones.sheepFarm.subZones.sheepFarm;
import zones.unused.forest;
import zones.unused.spiderCave;

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
	
	
	// Zone musics
	public ArrayList<String> zoneMusics = new ArrayList<String>();
	
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
			
			// Deal with sounds
			sound.stopSounds();
			sound.stopAmbience();
			if(music.currMusic != null && !b.zoneMusics.contains(music.currMusic.getFileName())) music.endAll();
			groundTile.groundTiles = new CopyOnWriteArrayList<chunk>();
			loadedOnce = false;
			player.loadPlayer(player.getPlayer(), b, x, y, direction, "spawnAnywhere");
			
			// Save the player in the new zone.
			player.getPlayer().lastWell = new Point(x,y);
			saveState.setQuiet(true);
			saveState.createSaveState();
			saveState.setQuiet(false);
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
		
		// Create a zone reference in every zone.
		File folder = new File("src/zones");
		File[] listOfFiles = folder.listFiles();
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isDirectory()) {
				File subFolder = new File("src/zones/" + listOfFiles[i].getName());
				File[] subListOfFiles = subFolder.listFiles();
				for (int j = 0; j < subListOfFiles.length; j++) {
					if (subListOfFiles[j].isDirectory() && subListOfFiles[j].getName().equals("subZones")) {
						File subZones = new File("src/zones/" + listOfFiles[i].getName() + "/subZones");
						File[] subZoneFiles = subZones.listFiles();
						if (subZoneFiles != null) {
							for (int b = 0; b < subZoneFiles.length; b++) {
								File currZone = subZoneFiles[b];
								if (currZone.isFile()) {

									// Create zone reference.
									Class<?> clazz;
									try {
										clazz = Class.forName("zones." + listOfFiles[i].getName() + ".subZones."
												+ currZone.getName().replace(".java", ""));
										Constructor<?> ctor = clazz.getConstructor();
										zone object = (zone) ctor.newInstance(new Object[] {});
										Method method = clazz.getMethod("setZone", zone.class);
										method.invoke(null, object);
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}
						}
					}
				}
			}
		}

		// Sheep Farms
		/*sheepFarm.setZone(new sheepFarm());
		farmerHouse.setZone(new farmerHouse());
		
		// Farm tomb
		farmTomb.setZone(new farmTomb());*/
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