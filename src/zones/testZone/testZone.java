package zones.testZone;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import UI.tooltipString;
import doodads.sheepFarm.barn;
import doodads.sheepFarm.blackSmith;
import doodads.sheepFarm.bridge;
import doodads.sheepFarm.bridgePole;
import doodads.sheepFarm.bush;
import doodads.sheepFarm.farmHouse;
import doodads.sheepFarm.fenceBars;
import doodads.sheepFarm.fenceBarsSmall;
import doodads.sheepFarm.fencePost;
import doodads.sheepFarm.flower;
import doodads.sheepFarm.haystack;
import doodads.sheepFarm.horizontalGate;
import doodads.sheepFarm.rock;
import doodads.sheepFarm.tomb;
import doodads.sheepFarm.tree;
import doodads.sheepFarm.verticalFence;
import drawing.spriteSheet;
import drawing.backgrounds.rotatingBackground;
import effects.effectTypes.fire;
import interactions.event;
import items.bottle;
import items.bottles.saveBottle;
import sounds.music;
import sounds.sound;
import terrain.chunk;
import terrain.atmosphericEffects.fog;
import terrain.atmosphericEffects.storm;
import terrain.chunkTypes.cave;
import terrain.chunkTypes.water;
import units.player;
import units.unit;
import units.characters.farlsworth.farlsworth;
import units.characters.farlsworth.cinematics.beforeTombCinematic;
import units.characters.farlsworth.cinematics.farmFenceCinematic;
import units.characters.farlsworth.cinematics.farmIntroCinematic;
import units.characters.farlsworth.cinematics.flowerFarmCinematic;
import units.characters.farmer.farmer;
import units.unitTypes.sheepFarm.sheep;
import utilities.intTuple;
import utilities.levelSave;
import utilities.saveState;
import utilities.time;
import utilities.utility;
import zones.zone;
import zones.farmTomb.farmTomb;

public class testZone extends zone {
	
	//////////////////////////////////
	// FIELDS, GLOBALS, CONSTRUCTOR //
	//////////////////////////////////
	
	// Static caller of the zone.
	private static zone zoneReference;
	
	// Default zone mode
	private String DEFAULT_ZONE_MODE = "topDown";
	
	// References we will use throughout.
	static unit u;
	static chunk c;
	
	// Defaults
	public static intTuple DEFAULT_SPAWN_TUPLE = new intTuple(-1115,391);
	
	// Constructor
	public testZone() {
		super("testZone", "farmLand");
	}
	
	///////////////////////////////
	// SPAWN PATTERNS/GENERATORS //
	///////////////////////////////
	
	// Spawn water from x to y.
	public void spawnPassableWaterRect(int x1, int y1, int x2, int y2) {
		int numX = (x2 - x1)/water.DEFAULT_CHUNK_WIDTH;
		int numY = (y2 - y1)/water.DEFAULT_CHUNK_HEIGHT;
		for(int i = 0; i < numX; i++) {
			for(int j = 0; j < numY; j++) {
				c = water.createChunk(i*water.DEFAULT_CHUNK_WIDTH + x1, j*water.DEFAULT_CHUNK_HEIGHT + y1);
				if(c!=null) {
					c.setInteractable(false);
					c.setPassable(true);
				}
			}
		}
	}

	
	private static BufferedImage DEFAULT_ZONE_BACKGROUND = spriteSheet.getSpriteFromFilePath("images/terrain/backgrounds/spinningFarmBackground.png");
	
	/////////////////
	// ZONE LOADER //
	/////////////////
	// Load the zone.
	public void loadSpecificZoneStuff() {
		
		// Set the mode of the zone of course.
		setMode(DEFAULT_ZONE_MODE);
		
		levelSave.loadSaveState("sheepFarmLevel.save");
		
		// Set background
		new rotatingBackground(DEFAULT_ZONE_BACKGROUND);

	}

	//////////////////////
	// INDIVIDUAL AREAS //
	//////////////////////
	



	

	
	// Do zone specific tasks that aren't monitored by
	// zone specific units. 
	@Override
	public void update() {
	}
	

	// Get the player location in the zone.
	public intTuple getDefaultLocation() {
		return DEFAULT_SPAWN_TUPLE;
	}

	/////////////////////////
	// Getters and setters //
	/////////////////////////
	public static zone getZone() {
		return zoneReference;
	}

	public static void setZone(zone z) {
		zoneReference = z;
	}
	
	public String getMode() {
		return DEFAULT_ZONE_MODE;
	}
	
}