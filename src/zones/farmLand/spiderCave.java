package zones.farmLand;

import java.awt.image.BufferedImage;

import doodads.cave.caveBone;
import doodads.cave.firePit;
import doodads.cave.webMedium;
import doodads.cave.webSmall;
import doodads.cave.webTiny;
import doodads.sheepFarm.bone;
import doodads.sheepFarm.caveEnterance;
import drawing.background;
import drawing.spriteSheet;
import interactions.event;
import items.weapons.torch;
import modes.platformer;
import modes.topDown;
import sounds.music;
import terrain.chunk;
import terrain.chunkTypes.cave;
import units.unit;
import units.unitTypes.farmLand.spiderCave.poisonSpider;
import units.unitTypes.farmLand.spiderCave.spider;
import units.unitTypes.farmLand.spiderCave.webDoor;
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
		//topDown.setMode();
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
		
		////////////////
		// FIRST Area //
		////////////////
		
		// Entrance
		caveEnterance spiderCaveEnterance = new caveEnterance(30,-15,0, sheepFarm.getZone(),-1762+52,-4070+90,"Down");
		spiderCaveEnterance.setZ(BACKGROUND_Z);
		
		// First patch of webs.
		c = new webSmall(200+110,-81,0);
		c = new webMedium(493,-186,0);
		c = new webSmall(826,-27,2);
		c = new webTiny(360,140,2);
		c = new webMedium(511,456,0);
		c = new webSmall(472,273,2);
		c = new webSmall(640,225,1);
		c = new webSmall(727,402,3);
		
		// Spawn the torch.
		torch t = new torch(1036,-100);
		
		// First patch of spiders
		u = new poisonSpider(601,-99);
		u = new spider(676,237);
		u = new poisonSpider(589,530);
		
		// Floor below entrance
		spawnCaveRect(32,8+500,300,40+32*3+500);
		
		// Spawn lower floor
		spawnCaveRect(32,40+500+32*3,1000,707+180);
		
		// Spawn right wall.
		spawnCaveRect(1000-32,-70+2,1100+32,600);
		spawnCaveRect(1100-4+32,-250-10,1100+600,2200);
		
		// Spawn lower room left wall
		spawnCaveRect(361,860,754,2200);
		
		// Spawn lower room floor
		spawnCaveRect(361,2000,1100+600,2400);
		
		// Webs in lower room
		c = new webTiny(1050,700,0);
		c = new webTiny(1102,821,0);
		c = new webTiny(1039, 1769,1);
		c = new webTiny(868, 1649, 2);
		c = new webTiny(745, 1526, 2);
		c = new webTiny(745, 1295, 1);
		c = new webTiny(856,900,3);
		c = new webTiny(750,1100,3);
		c = new webTiny(919,1430,0);
		c = new webTiny(958, 941, 1);
		c = new webTiny(946, 1882, 2);
		
		// Spiders in lower room
		c = new webTiny(979,1187,2);
		u = new poisonSpider(979+20,1187+15);
		((poisonSpider)u).setWanders(false);
		((poisonSpider)u).setFollows(false);
		u.setFacingDirection("Left");
		c = new webTiny(1078,1580,2);
		u = new poisonSpider(1078+20,1580+15);
		((poisonSpider)u).setWanders(false);
		((poisonSpider)u).setFollows(false);
		u.setFacingDirection("Left");
		
		// Fire in lower room.
		c = new firePit(784,1994);
		c = new caveBone(750,1983,1);
		c = new caveBone(1102, 1983, 0);
		c = new caveBone(1017, 1984, 3);
		
		// Web door
		u = new webDoor(-15,485);
		
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