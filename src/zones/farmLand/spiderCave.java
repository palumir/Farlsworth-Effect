package zones.farmLand;

import java.awt.Color;
import java.awt.image.BufferedImage;

import doodads.farmLand.bush;
import doodads.farmLand.caveEnterance;
import doodads.farmLand.fenceBars;
import doodads.farmLand.fenceBarsSmall;
import doodads.farmLand.fencePost;
import doodads.farmLand.horizontalGate;
import doodads.farmLand.rock;
import doodads.farmLand.tree;
import doodads.farmLand.verticalFence;
import drawing.background;
import drawing.spriteSheet;
import interactions.event;
import modes.platformer;
import modes.topDown;
import sounds.music;
import terrain.chunk;
import terrain.chunkType;
import terrain.generalChunkType;
import terrain.chunkTypes.cave;
import terrain.chunkTypes.grass;
import terrain.chunkTypes.water;
import terrain.chunkTypes.wood;
import units.player;
import units.unit;
import units.unitType;
import utilities.intTuple;
import utilities.utility;
import zones.zone;

public class spiderCave extends zone {
	
	//////////////////////////////////
	// FIELDS, GLOBALS, CONSTRUCTOR //
	//////////////////////////////////
	
	// Static caller of the zone.
	private static zone zoneReference;
	
	// References we will use throughout.
	unit u;
	chunk c;
	
	// Default background.
	private static BufferedImage DEFAULT_ZONE_BACKGROUND = spriteSheet.getSpriteFromFilePath("images/terrain/backgrounds/caveBackground.png");
	
	// Some defaults.
	public static int BACKGROUND_Z = -100;
	
	// Zone events.
	public static event enteredSpiderCaveBefore;
	
	// Defaults
	public static intTuple DEFAULT_SPAWN_TUPLE = new intTuple(0,-50);
	
	// Constructor
	public spiderCave() {
		super("spiderCave", "farmLand");
	}
	
	///////////////////////////////
	// SPAWN PATTERNS/GENERATORS //
	///////////////////////////////
	
	// Spawn grass cave x to y.
	public void spawnCaveRect(int x1, int y1, int x2, int y2) {
		int numX = (x2 - x1)/cave.DEFAULT_CHUNK_WIDTH;
		int numY = (y2 - y1)/cave.DEFAULT_CHUNK_HEIGHT;
		for(int i = 0; i < numX; i++) {
			for(int j = 0; j < numY; j++) {
				c = new cave(i*cave.DEFAULT_CHUNK_WIDTH + x1, j*cave.DEFAULT_CHUNK_HEIGHT + y1);
				if((i == numX-1 || i == 0 || j == 0 || j == numY-1));
				else { 
					 c.setPassable(true);
				}
			}
		}
	}
	
	// Spawn cave  from x to y.
	public void spawnPassableCaveRect(int x1, int y1, int x2, int y2) {
		int numX = (x2 - x1)/cave.DEFAULT_CHUNK_WIDTH;
		int numY = (y2 - y1)/cave.DEFAULT_CHUNK_HEIGHT;
		for(int i = 0; i < numX; i++) {
			for(int j = 0; j < numY; j++) {
				c = new cave(i*cave.DEFAULT_CHUNK_WIDTH + x1, j*cave.DEFAULT_CHUNK_HEIGHT + y1);
				c.setPassable(true);
			}
		}
	}
	
	/////////////////
	// ZONE LOADER //
	/////////////////
	// Load the zone.
	public void loadZone() {
		
		// Set the mode of the zone of course.
		platformer.setMode();
		
		// Background
		background.setGameBackground(DEFAULT_ZONE_BACKGROUND);
		
		// Load zone events.
		loadZoneEvents();
		
		// Create surrounding cave.
		createSurroundingCave();
		
		// Spawn area.
		createSpawnArea();
		
		// Sort chunks.
		chunk.sortChunks();
		
		// Zone is loaded.
		setZoneLoaded(true);
		
		// Play zone music.
		music.endAll();
		//zoneMusic.loopMusic();
		
	}
	
	// Load zone events.
	public void loadZoneEvents() {
		
		// Have we entered the cave before?
		enteredSpiderCaveBefore = new event("enteredSpiderCaveBefore");
	}
	
	//////////////////////
	// INDIVIDUAL AREAS //
	//////////////////////
	
	// Surrounding cave.
	public void createSurroundingCave() {
		
		// Roof
		spawnCaveRect(-311, -747, 4000,-220);
	
		// Floor
		spawnCaveRect(-311,40,300,40+32*3);
		//spawnCaveRect(-311,40+32,4000,500);
		
		// Left wall
		spawnCaveRect(-338,-235,7,1000);
		
		// Right wall
		spawnCaveRect(3976+32,-750,4300,1000);
	}
	
	// Spawn area.
	public void createSpawnArea() {
		int max = 0;
		
		////////////////
		// Spawn Area //
		////////////////
		
		caveEnterance spiderCaveEnterance = new caveEnterance(30,-15,0, sheepFarm.getZone(),-1762+52,-4070+90,"Down");
		spiderCaveEnterance.setZ(BACKGROUND_Z);
		
		// Zone loaded.
		setZoneLoaded(true);
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
	
}