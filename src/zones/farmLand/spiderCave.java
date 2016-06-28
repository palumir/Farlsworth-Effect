package zones.farmLand;

import java.awt.image.BufferedImage;

import doodads.cave.webMedium;
import doodads.cave.webSmall;
import doodads.sheepFarm.caveEnterance;
import drawing.background;
import drawing.spriteSheet;
import interactions.event;
import modes.platformer;
import modes.topDown;
import sounds.music;
import terrain.chunk;
import terrain.chunkTypes.cave;
import units.unit;
import units.unitTypes.farmLand.spiderCave.spider;
import utilities.intTuple;
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
	
	// Zone music.
	private static music zoneMusic = new music("sounds/music/farmLand/spiderCave/spiderCave.wav");
	
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
		// topDown.setMode();
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
		zoneMusic.loopMusic();
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
		
		// Left wall
		spawnCaveRect(-338,-235,7,1000);
		
		// Right wall
		spawnCaveRect(3976+32,-750,4300,1000);
	}
	
	// Spawn area.
	public void createSpawnArea() {
		int max = 0;
		
		////////////////
		// FIRST Area //
		////////////////
		
		// Entrance
		caveEnterance spiderCaveEnterance = new caveEnterance(30,-15,0, sheepFarm.getZone(),-1762+52,-4070+90,"Down");
		spiderCaveEnterance.setZ(BACKGROUND_Z);
		
		// First patch of webs.
		webSmall w = new webSmall(200,-81,1);
		c = new webSmall(w.getX()+110,w.getY(),0);
		c = new webSmall(c.getX()+220,-290,1);
		c = new webSmall(c.getX()+230,-81,3);
		c = new webSmall(c.getX()+110,-81,2);
		c = new webSmall(w.getX()+110,w.getY()+110,0);
		c = new webMedium(w.getX() + 150, w.getY()+200,0);
		c = new webMedium(c.getX() + 260, c.getY()+150,0);
		c = new webSmall(c.getX() - 450, c.getY()+80,0);
		
		// First patch of spiders
		u = new spider(535,231);
		u = new spider(535+50,231+50);
		u.setMoveSpeed(2);
		u = new spider(397,339);
		u.setMoveSpeed(3);
		
		// Floor below entrance
		spawnCaveRect(-311,8+500,300,40+32*3+500);
		
		// Spawn lower floor
		spawnCaveRect(-311,40+500+32*3,1000,40+32*6+500);
		
		// Spawn right wall.
		spawnCaveRect(1000-32,-70+2,1100+32,707+32);
		spawnCaveRect(1100-4+32,-250,1100+150,707+32);
		
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