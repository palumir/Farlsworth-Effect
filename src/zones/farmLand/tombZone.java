package zones.farmLand;

import java.awt.image.BufferedImage;

import doodads.sheepFarm.caveEnterance;
import doodads.tomb.stairsUp;
import doodads.tomb.wallTorch;
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
import units.unitTypes.farmLand.tomb.lightDude;
import units.unitTypes.farmLand.tomb.shadowDude;
import utilities.intTuple;
import zones.zone;

public class tombZone extends zone {
	
	//////////////////////////////////
	// FIELDS, GLOBALS, CONSTRUCTOR //
	//////////////////////////////////
	
	// Static caller of the zone.
	private static zone zoneReference;
	
	// Zone music.
	private static music zoneMusic = new music("sounds/music/farmLand/spiderCave/spiderCave.wav");
	
	// References we will use throughout.
	unit u;
	chunk c;
	
	// Some defaults.
	public static int BACKGROUND_Z = -100;
	
	// Zone events.
	public static event enteredtombZoneBefore;
	
	// Defaults
	public static intTuple DEFAULT_SPAWN_TUPLE = new intTuple(0,-50);
	
	// Zone fog
	public static fog zoneFog;
	
	// Constructor
	public tombZone() {
		super("tombZone", "farmLand");
	}
	
	///////////////////////////////
	// SPAWN PATTERNS/GENERATORS //
	///////////////////////////////
	
	// Spawn grass dirt x to y.
	public void spawnTombRect(int x1, int y1, int x2, int y2, String type) {
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
	public void loadSpecificZoneStuff() {
		
		// Set the mode of the zone of course.
		//topDown.setMode();
		platformer.setMode();
		
		// Set the darkness.
		zoneFog = new fog();
		zoneFog.setTo(0.15f);//fog.setTo(0.75f);
		
		// Load zone events.
		loadZoneEvents();
		
		// Create surrounding dirt.
		createTomb();
		
		// Spawn area.
		createSpawnArea();
		
		// Sort chunks.
		chunk.sortChunks();
		
		// Play zone music.
		zoneMusic.loopMusic();
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
	public void createTomb() {
		
		// Background 
		spawnBackgroundRect(-65,-269, 3230,787);
		
		// Roof
		spawnTombRect(-18, -747, 4000,-220,"roof"); // the roof
		spawnTombRect(-500+2, -747, 0,-220,"none"); // roof dirt top left
	
		// Floor
		spawnTombRect(-18,40,300,820,"ground");
		spawnTombRect(-500+2,46,0,820,"none");
		
		// Left wall
		spawnTombRect(-338,-242,7,60,"leftWall");
		
		// Right wall
		spawnTombRect(3976+32,-750,4300,1000,"rightWall");
		
		// First floor
		spawnTombRect(337,40,410,820,"ground");
		
		// Torches
		c = new wallTorch(209,-40);
		c = new wallTorch(767,-76);
		c = new wallTorch(435,-72);
		
		// Second floor
		spawnTombRect(508,0,1000,820,"ground");
		u = new shadowDude(974,-47);
		u.patrolTo(500,-47);
		
		// Third floor
		spawnTombRect(1073,63,1195,785,"ground");
		c = new wallTorch(1126,-2);
		
		// Fourth floor
		spawnTombRect(1209,85,1701,736, "ground");
		u = new lightDude(1209,38);
		u.patrolTo(1670, 38);
		
		// Spawn 6 shadow dudes above eachother for fourth floor.
		for(int i = 0; i < 6; i++) {
			u = new shadowDude(1670, 38-50*i);
			u.patrolTo(1209,38-50*i);
		}
		
	}
	
	// Spawn area.
	public void createSpawnArea() {
		
		////////////////
		// FIRST Area //
		////////////////
		
		// Entrance
		stairsUp tombZoneEnterance = new stairsUp(30,-8,0, sheepFarm.getZone(),2320,-3896,"Down");
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