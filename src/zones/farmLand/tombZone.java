package zones.farmLand;

import java.awt.image.BufferedImage;

import doodads.sheepFarm.caveEnterance;
import drawing.background;
import drawing.spriteSheet;
import interactions.event;
import items.weapons.torch;
import modes.platformer;
import modes.topDown;
import sounds.music;
import terrain.chunk;
import terrain.atmosphericEffects.fog;
import terrain.chunkTypes.dirt;
import terrain.chunkTypes.tombBackground;
import terrain.chunkTypes.tombDirt;
import units.unit;
import utilities.intTuple;
import zones.zone;

public class tombZone extends zone {
	
	//////////////////////////////////
	// FIELDS, GLOBALS, CONSTRUCTOR //
	//////////////////////////////////
	
	// Static caller of the zone.
	private static zone zoneReference;
	
	// References we will use throughout.
	unit u;
	chunk c;
	
	// Default background.
	//private static BufferedImage DEFAULT_ZONE_BACKGROUND = spriteSheet.getSpriteFromFilePath("images/terrain/backgrounds/dirtBackground.png");
	
	// Some defaults.
	public static int BACKGROUND_Z = -100;
	
	// Zone events.
	public static event enteredtombZoneBefore;
	
	// Defaults
	public static intTuple DEFAULT_SPAWN_TUPLE = new intTuple(0,-50);
	
	// Constructor
	public tombZone() {
		super("tombZone", "farmLand");
	}
	
	///////////////////////////////
	// SPAWN PATTERNS/GENERATORS //
	///////////////////////////////
	
	// Spawn grass dirt x to y.
	public void spawnDirtRect(int x1, int y1, int x2, int y2, String type) {
		int numX = (x2 - x1)/dirt.DEFAULT_CHUNK_WIDTH;
		int numY = (y2 - y1)/dirt.DEFAULT_CHUNK_HEIGHT;
		for(int i = 0; i < numX; i++) {
			for(int j = 0; j < numY; j++) {
				if((i == numX-1 || i == 0 || j == 0 || j == numY-1)) {
					if(j==0 && type.equals("ground")) {
						c = new tombDirt(i*dirt.DEFAULT_CHUNK_WIDTH + x1, j*dirt.DEFAULT_CHUNK_HEIGHT + y1, 0);
					}
					else if(i == numX - 1 && type.equals("leftWall")) {
						c = new tombDirt(i*dirt.DEFAULT_CHUNK_WIDTH + x1, j*dirt.DEFAULT_CHUNK_HEIGHT + y1, 1);
					}
					else if(i==0 && type.equals("rightWall")) {
						c = new tombDirt(i*dirt.DEFAULT_CHUNK_WIDTH + x1, j*dirt.DEFAULT_CHUNK_HEIGHT + y1, 3);
					}
					else if(j==numY - 1 && type.equals("roof")) {
						c = new tombDirt(i*dirt.DEFAULT_CHUNK_WIDTH + x1, j*dirt.DEFAULT_CHUNK_HEIGHT + y1, 2);
					}
					else {
						c = new dirt(i*dirt.DEFAULT_CHUNK_WIDTH + x1, j*dirt.DEFAULT_CHUNK_HEIGHT + y1);
					}
				}
				else { 
					 c = new dirt(i*dirt.DEFAULT_CHUNK_WIDTH + x1, j*dirt.DEFAULT_CHUNK_HEIGHT + y1);
					 c.setPassable(true);
				}
			}
		}
	}
	
	// Spawn dirt  from x to y.
	public void spawnPassableDirtRect(int x1, int y1, int x2, int y2) {
		int numX = (x2 - x1)/dirt.DEFAULT_CHUNK_WIDTH;
		int numY = (y2 - y1)/dirt.DEFAULT_CHUNK_HEIGHT;
		for(int i = 0; i < numX; i++) {
			for(int j = 0; j < numY; j++) {
				c = new dirt(i*dirt.DEFAULT_CHUNK_WIDTH + x1, j*dirt.DEFAULT_CHUNK_HEIGHT + y1);
				c.setPassable(true);
			}
		}
	}
	
	// Spawn background  from x to y.
	public void spawnBackgroundRect(int x1, int y1, int x2, int y2) {
		int numX = (x2 - x1)/dirt.DEFAULT_CHUNK_WIDTH;
		int numY = (y2 - y1)/dirt.DEFAULT_CHUNK_HEIGHT;
		for(int i = 0; i < numX; i++) {
			for(int j = 0; j < numY; j++) {
				c = new tombBackground(i*dirt.DEFAULT_CHUNK_WIDTH + x1, j*dirt.DEFAULT_CHUNK_HEIGHT + y1);
				c.setZ(BACKGROUND_Z);
				c.setBackgroundDoodad(true);
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
		topDown.setMode();
		//platformer.setMode();
		
		// Set the darkness.
		//fog.setTo(0.5f);
		
		// Background
		//background.setGameBackground(DEFAULT_ZONE_BACKGROUND);
		
		// Load zone events.
		loadZoneEvents();
		
		// Create surrounding dirt.
		createSurroundingdirt();
		
		// Spawn area.
		createSpawnArea();
		
		// Sort chunks.
		chunk.sortChunks();
		
		// Zone is loaded.
		setZoneLoaded(true);
		
		// Play zone music.
		//zoneMusic.loopMusic();
		music.endAll();
	}
	
	// Load zone events.
	public void loadZoneEvents() {
		
		// Have we entered the dirt before?
		enteredtombZoneBefore = new event("enteredtombZoneBefore");
	}
	
	//////////////////////
	// INDIVIDUAL AREAS //
	//////////////////////
	
	// Surrounding dirt.
	public void createSurroundingdirt() {
		
		// Background 
		spawnBackgroundRect(-65,-269, 3230,787);
		
		// Roof
		spawnDirtRect(-18, -747, 4000,-220,"roof"); // the roof
		spawnDirtRect(-500+2, -747, 0,-220,"none"); // roof dirt top left
	
		// Floor
		spawnDirtRect(-18,40,300,40+32*100,"ground");
		spawnDirtRect(-500+2,46,0,500,"none");
		
		// Left wall
		spawnDirtRect(-338,-242,7,60,"leftWall");
		
		// Right wall
		spawnDirtRect(3976+32,-750,4300,1000,"rightWall");
		
		// Torches
	}
	
	// Spawn area.
	public void createSpawnArea() {
		
		////////////////
		// FIRST Area //
		////////////////
		
		// Entrance
		caveEnterance tombZoneEnterance = new caveEnterance(30,-15,0, sheepFarm.getZone(),2320,-3896,"Down");
		tombZoneEnterance.setZ(BACKGROUND_Z);
		
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