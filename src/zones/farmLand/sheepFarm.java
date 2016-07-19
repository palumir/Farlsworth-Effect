package zones.farmLand;
import java.util.ArrayList;

import doodads.cave.firePit;
import doodads.cave.skullSign;
import doodads.general.invisibleDoodad;
import doodads.sheepFarm.barn;
import doodads.sheepFarm.blackSmith;
import doodads.sheepFarm.bone;
import doodads.sheepFarm.bridge;
import doodads.sheepFarm.bridgePole;
import doodads.sheepFarm.bush;
import doodads.sheepFarm.caveEnterance;
import doodads.sheepFarm.farmHouse;
import doodads.sheepFarm.fenceBars;
import doodads.sheepFarm.fenceBarsSmall;
import doodads.sheepFarm.fencePost;
import doodads.sheepFarm.flower;
import doodads.sheepFarm.grave;
import doodads.sheepFarm.haystack;
import doodads.sheepFarm.horizontalGate;
import doodads.sheepFarm.rock;
import doodads.sheepFarm.statue;
import doodads.sheepFarm.tomb;
import doodads.sheepFarm.tree;
import doodads.sheepFarm.verticalFence;
import doodads.sheepFarm.well;
import doodads.sheepFarm.woolPiece;
import drawing.background;
import drawing.userInterface.tooltipString;
import interactions.event;
import items.bottle;
import items.item;
import items.bottles.normalBottle;
import items.weapons.dagger;
import modes.topDown;
import sounds.music;
import terrain.chunk;
import terrain.atmosphericEffects.fog;
import terrain.atmosphericEffects.storm;
import terrain.chunkTypes.cave;
import terrain.chunkTypes.grass;
import terrain.chunkTypes.water;
import terrain.chunkTypes.wood;
import units.player;
import units.unit;
import units.bosses.denmother;
import units.bosses.farlsworth;
import units.unitTypes.farmLand.sheepFarm.farmer;
import units.unitTypes.farmLand.sheepFarm.redWolf;
import units.unitTypes.farmLand.sheepFarm.sheep;
import units.unitTypes.farmLand.sheepFarm.blackWolf;
import units.unitTypes.farmLand.sheepFarm.yellowWolf;
import units.unitTypes.farmLand.spiderCave.poisonSpider;
import units.unitTypes.farmLand.spiderCave.spider;
import utilities.intTuple;
import utilities.saveState;
import utilities.time;
import utilities.utility;
import zones.zone;

public class sheepFarm extends zone {
	
	//////////////////////////////////
	// FIELDS, GLOBALS, CONSTRUCTOR //
	//////////////////////////////////
	
	// Static caller of the zone.
	private static zone zoneReference;
	
	// Zone music.
	private static music zoneMusic = new music("sounds/music/farmLand/sheepFarm/forest.wav");
	private static music graveYardMusic = new music("sounds/music/farmLand/spiderCave/spiderCave.wav");
	
	// Static fence so farlsworth can be attached to it.
	public static ArrayList<chunk> farlsworthFence;
	
	// References we will use throughout.
	static unit u;
	static chunk c;
	
	// Forest gate
	public static horizontalGate forestGate;
	
	// Zone events.
	public static event wellTooltipLoaded;
	public static event attackTooltipLoaded;
	public static event gameSavedForIdiots;
	public static event uCanSaveAtWater;
	public static event stormInProgress;
	
	// Storm booleans
	public static boolean stormStarted = false;
	
	// Zone fog
	private static fog zoneFog;
	private float stormFogLevel = 0.15f;
	
	// Defaults
	public static intTuple DEFAULT_SPAWN_TUPLE = new intTuple(-9,11);
	
	// Constructor
	public sheepFarm() {
		super("sheepFarm", "farmLand");
	}
	
	///////////////////////////////
	// SPAWN PATTERNS/GENERATORS //
	///////////////////////////////
	
	// Spawn grass from x to y.
	public void spawnForestMeta(int x1, int y1, int x2, int y2) {	
		for(int i = x1; i < x2; i = i += flower.DEFAULT_SPRITE_WIDTH) {
			for(int j = y1; j < y2; j += flower.DEFAULT_SPRITE_HEIGHT) {
				int spawnTree = utility.RNG.nextInt(200);
				int diffX = utility.RNG.nextInt(60);
				int diffY = utility.RNG.nextInt(60);
				int t = utility.RNG.nextInt(5);
				if(spawnTree<1) {
					System.out.println("c = new flower(" + (i+diffX) + "," + (j+diffY) + "," + t + ");");
					c = new flower(i+diffX,j+diffY,t);
				}
			}
		}
	}
	
	// Spawn grass from x to y.
	public void spawnGrassRect(int x1, int y1, int x2, int y2) {
		int numX = (x2 - x1)/grass.DEFAULT_CHUNK_WIDTH;
		int numY = (y2 - y1)/grass.DEFAULT_CHUNK_HEIGHT;
		for(int i = 0; i < numX; i++) {
			for(int j = 0; j < numY; j++) {
				c = new grass(i*grass.DEFAULT_CHUNK_WIDTH + x1, j*grass.DEFAULT_CHUNK_HEIGHT + y1);
			}
		}
	}
	
	// Spawn grass from x to y.
	public void spawnMountainRect(int x1, int y1, int x2, int y2) {
		int numX = (x2 - x1)/cave.DEFAULT_CHUNK_WIDTH;
		int numY = (y2 - y1)/cave.DEFAULT_CHUNK_HEIGHT;
		for(int i = 0; i < numX; i++) {
			for(int j = 0; j < numY; j++) {
				c = new cave(i*cave.DEFAULT_CHUNK_WIDTH + x1, j*cave.DEFAULT_CHUNK_HEIGHT + y1);
				c.setPassable(false);
			}
		}
	}
	
	// Spawn water from x to y.
	public void spawnWaterRect(int x1, int y1, int x2, int y2) {
		int numX = (x2 - x1)/water.DEFAULT_CHUNK_WIDTH;
		int numY = (y2 - y1)/water.DEFAULT_CHUNK_HEIGHT;
		for(int i = 0; i < numX; i++) {
			for(int j = 0; j < numY; j++) {
				c = new water(i*water.DEFAULT_CHUNK_WIDTH + x1, j*water.DEFAULT_CHUNK_HEIGHT + y1);
			}
		}
	}
	
	// Spawn water from x to y.
	public void spawnPassableWaterRect(int x1, int y1, int x2, int y2) {
		int numX = (x2 - x1)/water.DEFAULT_CHUNK_WIDTH;
		int numY = (y2 - y1)/water.DEFAULT_CHUNK_HEIGHT;
		for(int i = 0; i < numX; i++) {
			for(int j = 0; j < numY; j++) {
				c = new water(i*water.DEFAULT_CHUNK_WIDTH + x1, j*water.DEFAULT_CHUNK_HEIGHT + y1);
				c.setPassable(true);
			}
		}
	}
	
	// Spawn wood from x to y.
	public void spawnFlowerRect(int x1, int y1, int x2, int y2, int r) {
		int numX = (x2 - x1)/flower.DEFAULT_SPRITE_WIDTH;
		int numY = (y2 - y1)/flower.DEFAULT_SPRITE_HEIGHT;
		for(int i = 0; i < numX; i++) {
			for(int j = 0; j < numY; j++) {
				//int rand = utility.RNG.nextInt(r+1);
				c = new flower(i*flower.DEFAULT_SPRITE_WIDTH + x1, j*flower.DEFAULT_SPRITE_WIDTH + y1, r);
			}
		}
	}
	
	// Spawn wood from x to y.
	public void spawnWoodRect(int x1, int y1, int x2, int y2) {
		int numX = (x2 - x1)/wood.DEFAULT_CHUNK_WIDTH;
		int numY = (y2 - y1)/wood.DEFAULT_CHUNK_HEIGHT;
		for(int i = 0; i < numX; i++) {
			for(int j = 0; j < numY; j++) {
				c = new wood(i*wood.DEFAULT_CHUNK_WIDTH + x1, j*wood.DEFAULT_CHUNK_HEIGHT + y1);
			}
		}
	}
	
	// Spawn wood from x to y. TODO: JUST WORKS FOR VERTICAL
	public static ArrayList<chunk> spawnFence(ArrayList<chunk> chunkList, int x1, int y1, int x2, int y2) {
		
		// Arraylist for return
		if(chunkList == null) chunkList = new ArrayList<chunk>();
		
		// The fence is vertical.
		if(x2==x1) {
			int numY = (y2 - y1)/verticalFence.DEFAULT_CHUNK_HEIGHT;
			for(int j = 0; j < numY; j++) {
					
				// Bottom of fence.
				if(j==numY-1) {
					c = new verticalFence(verticalFence.DEFAULT_CHUNK_WIDTH + x1, j*60 + y1, 1);
					chunkList.add(c);
				}
					
				// Anything in between.
				else {
					c = new verticalFence(verticalFence.DEFAULT_CHUNK_WIDTH + x1, j*60 + y1, 0);
					chunkList.add(c);
				}
			}
		}
		if(y2==y1) {
				int numX = (x2 - x1)/(fencePost.DEFAULT_CHUNK_WIDTH + fenceBars.DEFAULT_CHUNK_WIDTH);
				for(int j = 0; j < numX; j++) {
					// Far left of fence.
					if(j==0) {
						c = new fencePost(j*fencePost.DEFAULT_CHUNK_WIDTH + x1, fencePost.DEFAULT_CHUNK_HEIGHT + y1, 0);
						chunkList.add(c);
					}
						
					// Middle fence
					else {
						c = new fenceBars((j-1)*fenceBars.DEFAULT_CHUNK_WIDTH + j*fencePost.DEFAULT_CHUNK_WIDTH + x1,fenceBars.DEFAULT_CHUNK_HEIGHT + y1,0);
						chunkList.add(c);
						c = new fencePost(j*fenceBars.DEFAULT_CHUNK_WIDTH + j*fencePost.DEFAULT_CHUNK_WIDTH + x1, fencePost.DEFAULT_CHUNK_HEIGHT + y1, 0);
						chunkList.add(c);
					}
				}
		}
		return chunkList;
	}
	
	/////////////////
	// ZONE LOADER //
	/////////////////
	// Load the zone.
	public void loadSpecificZoneStuff() {
		
		// Set the mode of the zone of course.
		topDown.setMode();
		
		// Set background
		background.setGameBackground(null);
		
		// Load zone events.
		loadZoneEvents();
		
		// Storming?
		if(stormInProgress.isCompleted()) {
			zoneFog = new fog();
			zoneFog.setTo(stormFogLevel);
			storm s = new storm();
		}
		
		// Spawn area.
		createSpawnArea();
		
		// Create forest above spawn
		createForestAboveSpawn();
		
		// Create flower farm
		createFlowerFarm();
		
		// Create area above flower farm
		createAreaAboveFlowerFarm();
		
		// Create graveyard
		createGraveYard();
		
		// Create final area
		createFinalArea();
		
		
		// Spawn units
		spawnUnits();
		
		// Sort chunks.
		chunk.sortChunks();
		
		// Play zone music.
		zoneMusic.loopMusic();
		
	}
	
	//////////////////////
	// INDIVIDUAL AREAS //
	//////////////////////
	
	// Spawn creeps
	public void spawnUnits() {
		
		// First wolf
		u = new redWolf(-2,-747);
		u.setFacingDirection("Down");
		
		// Second pack of wolves after spawn
		u = new redWolf(179,-1240);
		u.setFacingDirection("Down");
		u = new redWolf(145,-1243);
		u.setFacingDirection("Down");
		
		// First yellow wolf
		u = new yellowWolf(699,-1344);
		u.setFacingDirection("Left");
		
		// Two yellow wolves
		u = new redWolf(1420,-768);
		u.setFacingDirection("Left");
		u = new yellowWolf(1394,-763);
		u.setFacingDirection("Left");
		
		// First black wolf, at flower farm
		u = new blackWolf(1983,-276);
		u.setFacingDirection("Left");
		
		// Black and red wolf at the end of the flower patch
		u = new redWolf(2655,-137);
		u.setFacingDirection("Left");
		u = new blackWolf(2655,-121);
		u.setFacingDirection("Left");
		
		// Two blacks
		u = new blackWolf(2646,-955);
		u.setFacingDirection("Down");
		u = new blackWolf(2678,-957);
		u.setFacingDirection("Down");
		
		// Two reds and black, before graveyard well
		u = new blackWolf(2645,-1387);
		u.setFacingDirection("Down");
		u = new redWolf(2683,-1397);
		u.setFacingDirection("Down");
		u = new redWolf(2717,-1398);
		u.setFacingDirection("Down");
		
		// Two reds and black, before graveyard well
		u = new blackWolf(2771,-2566);
		u.setFacingDirection("Down");
		u = new blackWolf(2804,-2568);
		u.setFacingDirection("Down");
		u = new yellowWolf(2745,-2563);
		u.setFacingDirection("Down");
		
		// Pack of all 3 wolves in graveyard
		u = new yellowWolf(2168,-3046);
		u.setFacingDirection("Right");
		u = new redWolf(2163,-3067);
		u.setFacingDirection("Right");
		u = new blackWolf(2155,-3079);
		u.setFacingDirection("Right");


	}
	
	// Final area with all the fire.
	public void createFinalArea() {
		
		// Lightning tree
		tree lightningTree = new tree(-758,-3937,0);
		
		// Trees
		c = new tree(-291,-3931,2);
		c = new tree(-291,-3904,2);
		c = new tree(-311,-3866,1);
		c = new tree(-278,-3880,2);
		c = new tree(-283,-3840,1);
		c = new tree(-305,-3814,0);
		c = new tree(-274,-3785,0);
		c = new tree(-309,-3759,0);
		c = new tree(-293,-3732,0);
		c = new tree(-293,-3708,1);
		c = new tree(-293,-3687,2);
		c = new tree(-311,-3666,2);
		c = new tree(-296,-3645,0);
		c = new tree(-317,-3624,2);
		c = new tree(-302,-3597,1);
		c = new tree(-298,-3948,1);
		c = new tree(-300,-3964,0);
		c = new tree(-318,-3988,1);
		c = new tree(-334,-4014,1);
		c = new tree(-364,-4038,1);
		c = new tree(-342,-3657,2);
		c = new tree(-383,-3639,2);
		c = new tree(-433,-3643,1);
		c = new tree(-406,-3646,2);
		c = new tree(-465,-3656,1);
		c = new tree(-503,-3681,0);
		c = new tree(-386,-4052,0);
		c = new tree(-422,-4048,2);
		c = new tree(-463,-4063,2);
		c = new tree(-488,-4077,0);
		c = new tree(-518,-4082,0);
		c = new tree(-539,-3690,2);
		c = new tree(-565,-3683,1);
		c = new tree(-590,-3699,1);
		c = new tree(-614,-3682,0);
		c = new tree(-642,-3698,0);
		c = new tree(-664,-3691,0);
		c = new tree(-549,-4087,1);
		c = new tree(-592,-4087,1);
		c = new tree(-641,-4075,1);
		c = new tree(-623,-4087,1);
		c = new tree(-678,-4068,1);
	}
	
	// Flower farm
	public void createFlowerFarm() {
		
		// Farmhouse
		c = new farmHouse(1892,-626-100,0);
		c = new bush(1811,-664,1);
		c = new bush(2101,-668,0);
		c = new bush(2085,-702,1);
		c = new tree(1848,-713,0);
		c = new tree(1806,-695,2);
		c = new tree(1770,-677,0);
		c = new tree(1721,-652,1);
		c = new tree(1743,-615,2);
		c = new tree(1736,-568,1);
		c = new tree(1711,-552,1);
		c = new tree(2071,-737,1);
		c = new tree(2123,-721,2);
		c = new tree(2156,-676,1);
		c = new tree(2202,-651,1);
		c = new tree(2202,-682,2);
		c = new tree(2249,-652,0);
		c = new tree(2269,-635,1);
		c = new tree(2287,-602,0);
		c = new tree(2309,-565,0);
		c = new tree(2348,-542,0);
		c = new tree(2368,-509,1);
		c = new tree(2413,-532,2);
		c = new tree(2424,-507,2);
		c = new tree(1904,-776,0);
		c = new tree(1995,-759,0);
		c = new flower(2094,-631,0);
		c = new tree(1791,-540,2);
		c = new tree(1765,-509,2);
		c = new flower(1712,-534,2);
		c = new flower(2224,-626,0);
		c = new flower(2258,-564,2);
		c = new bush(1660,-510,1);
		c = new tree(1741,-539,1);
		c = new bush(1731,-528,1);
		c = new firePit(2150,-565);
		
		// Fences
		spawnFence(null, 1617,-56,2900,-56); // Top horizontal fence
		spawnFence(null, 2848-4,-400+5+7, 2848-4,150); // End fence, vertical
		spawnFence(null, 1662+10,-500+14, 1662+10,0); // Start fence, vertical
		spawnFence(null, 2049+15,-400+5+7, 2049+15,150); // Second fence, vertical
		spawnFence(null, 2428,-500+14, 2428,0); // Third fence, vertical
		spawnFence(null,1677,-512,2900,-512); // Bottom horizontal fence
		
		// First flower patch
		spawnFlowerRect(1684,-470,1800,-150,7); // yellow
		spawnFlowerRect(1684,-150,1800,-30,3); // blue
		spawnFlowerRect(1800-7,-252-10+4, 1684+280,-30,5); // pink
		spawnFlowerRect(1783+10,-468-2, 2041+30,-280+16,1); // orange 
		spawnFlowerRect(1954,-266+8, 2040+36,-30,6); // green
		
		//Second flower patch
		spawnFlowerRect(1684+400,-470,2039+40+350,-400-7,8);
		spawnFlowerRect(1684+400,-470+50,2039+40+350,-400+40,1);
		spawnFlowerRect(1684+400,-470+100,2039+40+350,-400+90,7);
		spawnFlowerRect(1684+400,-470+150,2039+40+350,-400+140,6);
		spawnFlowerRect(1684+400,-470+200,2039+40+350,-400+190,3);
		spawnFlowerRect(1684+400,-470+250,2039+40+350,-400+240,9);
		spawnFlowerRect(1684+400,-470+300,2039+40+350,-400+290,5);
		spawnFlowerRect(1684+400,-470+350,2039+40+350,-400+340,8);
		spawnFlowerRect(1684+400,-470+400,2039+40+350,-400+370,1);
		
		// Last patch
		spawnFlowerRect(1684+400+355,-470,2039+40+350+400+20+16,-46,10);
		
		// Trees below
		c = new tree(2849,-295,2);
		c = new tree(2880,-267,0);
		c = new tree(2907,-279,1);
		c = new tree(2934,-261,0);

	}
	
	// Create area above flower farm.
	public void createAreaAboveFlowerFarm() {
		
		// Well
		c = new well(2685,-1877-125,0);
		
		// Trees
		c = new tree(2440,-1663,1);
		c = new tree(2416,-1686,1);
		c = new tree(2391,-1714,0);
		c = new tree(2365,-1732,2);
		c = new tree(2333,-1709,1);
		c = new tree(2303,-1733,0);
		c = new tree(2274,-1720,1);
		c = new tree(2235,-1735,2);
		c = new tree(2192,-1729,1);
		c = new tree(2170,-1754,0);
		c = new tree(2133,-1740,0);
		c = new tree(2105,-1758,2);
		c = new tree(2078,-1738,2);
		c = new tree(2039,-1728,2);
		c = new tree(2027,-1755,2);
		c = new tree(2001,-1741,0);
		c = new tree(1955,-1745,2);
		c = new tree(1935,-1731,1);
		c = new tree(1905,-1741,1);
		c = new tree(1866,-1723,1);
		c = new tree(1848,-1745,2);
		c = new tree(1809,-1726,2);
		c = new tree(1788,-1756,2);
		c = new tree(1770,-1741,0);
		c = new tree(1742,-1725,1);
		c = new tree(1715,-1740,1);
		c = new tree(1661,-1739,0);
		c = new tree(1688,-1739,1);
		c = new tree(1656,-1720,2);
		c = new tree(1627,-1731,1);
		c = new tree(1610,-1714,0);
		c = new tree(1583,-1724,2);
		c = new tree(1556,-1709,1);
		c = new tree(1532,-1724,2);
		c = new tree(1502,-1724,1);
		c = new tree(1457,-1724,2);
		c = new tree(1427,-1712,2);
		c = new tree(1394,-1723,0);
		c = new tree(1368,-1703,1);
		c = new tree(1344,-1730,1);
		c = new tree(1338,-1707,2);
		c = new tree(1302,-1728,0);
		c = new tree(1292,-1708,0);
		c = new tree(1240,-1730,2);
		c = new tree(1247,-1708,0);
		c = new tree(1202,-1706,2);
		c = new tree(1207,-1732,1);
		c = new tree(1184,-1715,1);
		c = new tree(1136,-1715,1);
		c = new tree(1152,-1734,1);
		c = new tree(1097,-1715,2);
		c = new tree(1112,-1736,1);
		c = new tree(1068,-1719,0);
		c = new tree(1033,-1728,1);
		c = new tree(992,-1711,2);
		c = new tree(990,-1739,0);
		c = new tree(963,-1715,2);
		c = new tree(941,-1737,0);
		c = new tree(910,-1715,2);
		c = new tree(885,-1735,0);
		c = new tree(868,-1712,0);
		c = new tree(841,-1736,0);
		c = new tree(826,-1714,0);
		c = new tree(793,-1732,0);
		c = new tree(777,-1710,1);
		c = new tree(750,-1732,2);
		c = new tree(736,-1718,1);
		c = new tree(706,-1700,2);
		c = new tree(687,-1716,1);
		c = new tree(660,-1698,2);
		c = new tree(639,-1713,1);
		c = new tree(618,-1703,2);
		c = new tree(572,-1715,0);
		c = new tree(564,-1696,0);
		c = new tree(530,-1715,1);
		c = new tree(530,-1691,0);
		c = new tree(487,-1690,2);
		c = new tree(484,-1711,1);
		c = new tree(451,-1711,1);
		c = new tree(442,-1687,0);
		c = new tree(409,-1687,1);
		c = new tree(392,-1670,2);
		c = new tree(357,-1675,0);
		c = new tree(342,-1654,2);
		c = new tree(315,-1675,2);
		c = new tree(294,-1650,1);
		c = new tree(279,-1627,0);
		c = new tree(249,-1627,2);
		c = new tree(210,-1609,1);
		c = new tree(192,-1591,2);
		c = new tree(167,-1574,2);
		c = new tree(128,-1574,1);
		c = new tree(97,-1549,0);
		c = new tree(62,-1536,0);
		c = new tree(35,-1508,0);
		c = new tree(0,-1487,2);
		c = new tree(67,-1512,2);
		c = new tree(-20,-1475,0);
		c = new tree(-47,-1454,1);
		c = new tree(-74,-1454,0);
		c = new tree(-101,-1440,1);
		c = new tree(-136,-1428,2);
		c = new tree(-162,-1403,1);
		c = new tree(-183,-1367,2);
		c = new tree(-186,-1385,2);
		c = new tree(-212,-1337,2);
		c = new tree(-222,-1353,0);
		c = new tree(-244,-1325,0);
		c = new tree(-283,-1313,0);
		c = new tree(-283,-1283,0);
		c = new tree(-307,-1283,2);
		c = new tree(-321,-1242,1);
		c = new tree(-310,-1262,2);
		c = new tree(-248,-1300,1);
		c = new tree(-342,-1217,2);
		c = new tree(-364,-1190,2);
		c = new tree(-404,-1170,0);
		c = new tree(-381,-1144,1);
		c = new tree(-402,-1119,2);
		c = new tree(-366,-1155,1);
		c = new tree(-435,-1110,2);
		c = new tree(-421,-1075,2);
		c = new tree(-439,-1085,0);
		c = new tree(-452,-1053,0);
		c = new tree(-452,-1001,1);
		c = new tree(-457,-988,1);
		c = new tree(-457,-961,1);
		c = new tree(-470,-940,0);
		c = new tree(-469,-913,0);
		c = new tree(-483,-887,2);
		c = new tree(-471,-863,0);
		c = new tree(-483,-836,1);
		c = new tree(-483,-806,1);
		c = new tree(-483,-785,0);
		c = new tree(-489,-773,1);
		c = new tree(-489,-746,0);
		c = new tree(-493,-724,2);
		c = new tree(-493,-694,1);
		c = new tree(-493,-667,2);
		c = new tree(-496,-633,2);
		c = new tree(-496,-603,2);
		c = new tree(-496,-652,0);
		c = new tree(-495,-571,1);
		c = new tree(-522,-586,0);
		c = new tree(-498,-532,0);
		c = new tree(-510,-550,2);
		c = new tree(-512,-526,1);
		c = new tree(-496,-504,1);
		c = new tree(-519,-475,0);
		c = new tree(-524,-454,1);
		c = new tree(-1086,-452,1);
		c = new tree(-1086,-479,2);
		c = new tree(-1062,-503,2);
		c = new tree(-1084,-522,2);
		c = new tree(-1069,-553,2);
		c = new tree(-1095,-579,0);
		c = new tree(-1069,-605,2);
		c = new tree(-1088,-625,0);
		c = new tree(-1102,-647,0);
		c = new tree(-1077,-681,1);
		c = new tree(-1094,-671,2);
		c = new tree(-1094,-704,1);
		c = new tree(-1082,-722,0);
		c = new tree(-1103,-743,1);
		c = new tree(-1079,-771,1);
		c = new tree(-1105,-784,2);
		c = new tree(-1081,-814,0);
		c = new tree(-1105,-835,0);
		c = new tree(-1082,-867,0);
		c = new tree(-1105,-893,1);
		c = new tree(-1088,-916,0);
		c = new tree(-1117,-945,2);
		c = new tree(-1097,-966,0);
		c = new tree(-1077,-1003,2);
		c = new tree(-1097,-1025,0);
		c = new tree(-1070,-1045,0);
		c = new tree(-1093,-1078,2);
		c = new tree(-1064,-1107,2);
		c = new tree(-1092,-1123,2);
		c = new tree(-1071,-1153,1);
		c = new tree(-1031,-600,1);
		c = new tree(-991,-586,2);
		c = new tree(-971,-594,2);
		c = new tree(-928,-595,0);
		c = new tree(-905,-581,2);
		c = new tree(-876,-604,1);
		c = new tree(-838,-581,2);
		c = new tree(-805,-591,0);
		c = new tree(-772,-591,1);
		c = new tree(-746,-571,2);
		c = new tree(-714,-592,0);
		c = new tree(-690,-568,0);
		c = new tree(-654,-589,1);
		c = new tree(-633,-562,1);
		c = new tree(-604,-583,0);
		c = new tree(-582,-561,1);
		c = new tree(-556,-584,1);
		c = new tree(-538,-610,1);
		c = new tree(-443,-618,0);
		c = new tree(-1065,-1184,0);
		c = new tree(-1056,-1202,2);
		c = new tree(-1042,-1173,0);
		c = new tree(-1025,-1228,0);
		c = new tree(-1037,-1252,0);
		c = new tree(-1010,-1270,0);
		c = new tree(-985,-1298,0);
		c = new tree(-992,-1321,0);
		c = new tree(-949,-1346,2);
		c = new tree(-942,-1373,2);
		c = new tree(-919,-1390,0);
		c = new tree(-890,-1413,0);
		c = new tree(-861,-1442,0);
		c = new tree(-848,-1463,2);
		c = new tree(-823,-1435,2);
		c = new tree(-782,-1460,2);
		c = new tree(-767,-1486,1);
		c = new tree(-726,-1493,0);
		c = new tree(-703,-1522,0);
		c = new tree(-648,-1520,2);
		c = new tree(-687,-1541,1);
		c = new tree(-655,-1556,0);
		c = new tree(-634,-1582,0);
		c = new tree(-610,-1609,2);
		c = new tree(-580,-1594,1);
		c = new tree(-557,-1612,2);
		c = new tree(-531,-1652,2);
		c = new tree(-523,-1635,0);
		c = new tree(-494,-1658,2);
		c = new tree(-472,-1686,2);
		c = new tree(-470,-1713,2);
		c = new tree(-437,-1732,2);
		c = new tree(-395,-1748,1);
		c = new tree(-362,-1772,2);
		c = new tree(-335,-1805,0);
		c = new tree(-332,-1787,0);
		c = new tree(-284,-1813,0);
		c = new tree(-257,-1834,1);
		c = new tree(-214,-1830,0);
		c = new tree(-192,-1859,0);
		c = new tree(-176,-1885,1);
		c = new tree(-150,-1901,0);
		c = new tree(-119,-1927,2);
		c = new tree(-99,-1956,0);
		c = new tree(-77,-1979,0);
		c = new tree(-44,-1997,1);
		c = new tree(-25,-2022,1);
		c = new tree(12,-2040,1);
		c = new tree(62,-2058,0);
		c = new tree(86,-2069,0);
		c = new tree(119,-2093,2);
		c = new tree(161,-2112,1);
		c = new tree(183,-2096,2);
		c = new tree(230,-2102,2);
		c = new tree(256,-2122,1);
		c = new tree(289,-2131,1);
		c = new tree(318,-2160,2);
		c = new tree(340,-2135,1);
		c = new tree(372,-2155,1);
		c = new tree(404,-2169,1);
		c = new tree(431,-2190,1);
		c = new tree(462,-2165,0);
		c = new tree(495,-2185,1);
		c = new tree(522,-2165,0);
		c = new tree(555,-2185,0);
		c = new tree(575,-2162,1);
		c = new tree(612,-2180,0);
		c = new tree(633,-2204,2);
		c = new tree(655,-2186,0);
		c = new tree(694,-2207,2);
		c = new tree(713,-2188,0);
		c = new tree(754,-2201,2);
		c = new tree(776,-2183,2);
		c = new tree(812,-2192,1);
		c = new tree(841,-2169,1);
		c = new tree(870,-2196,0);
		c = new tree(906,-2166,2);
		c = new tree(933,-2184,1);
		c = new tree(992,-2195,2);
		c = new tree(961,-2172,1);
		c = new tree(1040,-2188,0);
		c = new tree(1059,-2159,0);
		c = new tree(1080,-2192,1);
		c = new tree(1011,-2174,2);
		c = new tree(1113,-2177,1);
		c = new tree(1143,-2156,2);
		c = new tree(1171,-2174,0);
		c = new tree(1204,-2174,0);
		c = new tree(1230,-2148,2);
		c = new tree(1255,-2166,2);
		c = new tree(1281,-2140,2);
		c = new tree(1287,-2177,0);
		c = new tree(1324,-2193,2);
		c = new tree(1353,-2167,1);
		c = new tree(1383,-2191,0);
		c = new tree(1409,-2162,2);
		c = new tree(1439,-2189,2);
		c = new tree(1464,-2164,0);
		c = new tree(1494,-2189,1);
		c = new tree(1510,-2170,1);
		c = new tree(1537,-2187,2);
		c = new tree(1562,-2156,1);
		c = new tree(1593,-2177,1);
		c = new tree(1616,-2154,2);
		c = new tree(1663,-2175,0);
		c = new tree(1689,-2156,1);
		c = new tree(1722,-2156,1);
		c = new tree(1762,-2166,1);
		c = new tree(1781,-2186,2);
		c = new tree(1803,-2174,2);
		c = new tree(1827,-2192,1);
		c = new tree(1850,-2182,1);
		c = new tree(1883,-2200,2);
		c = new tree(1934,-2179,1);
		c = new tree(1904,-2164,2);
		c = new tree(1968,-2192,2);
		c = new tree(1991,-2206,2);
		c = new tree(2022,-2176,0);
		c = new tree(2050,-2192,0);
		c = new tree(2077,-2164,2);
		c = new tree(2101,-2185,2);
		c = new tree(2120,-2161,0);
		c = new tree(2148,-2189,2);
		c = new tree(2168,-2206,1);
		c = new tree(2201,-2206,1);
		c = new tree(2234,-2185,0);
		c = new tree(2257,-2209,1);
		c = new tree(2278,-2192,2);
		c = new tree(2317,-2222,0);
		c = new tree(2344,-2195,1);
		c = new tree(2378,-2217,1);
		c = new tree(2402,-2198,1);
		c = new tree(2433,-2208,0);
		c = new tree(2459,-2182,1);
		c = new tree(2488,-2214,2);
		c = new tree(2460,-2240,0);
		c = new tree(2475,-2258,2);
		c = new tree(2499,-2234,0);
		c = new tree(2474,-2301,1);
		c = new tree(2501,-2280,0);
		c = new tree(2473,-2332,0);
		c = new tree(2498,-2315,0);
		c = new tree(2947,-1656,2);
		c = new tree(2947,-1701,1);
		c = new tree(2921,-1733,2);
		c = new tree(2965,-1757,0);
		c = new tree(2942,-1795,1);
		c = new tree(2942,-1768,2);
		c = new tree(2946,-1814,1);
		c = new tree(2931,-1841,2);
		c = new tree(2956,-1868,1);
		c = new tree(2939,-1893,0);
		c = new tree(2956,-1919,1);
		c = new tree(2939,-1950,1);
		c = new tree(2955,-1976,0);
		c = new tree(2926,-2013,0);
		c = new tree(2951,-1996,0);
		c = new tree(2946,-2027,1);
		c = new tree(2931,-2051,2);
		c = new tree(2949,-2075,0);
		c = new tree(2931,-2099,1);
		c = new tree(2961,-2114,2);
		c = new tree(2937,-2143,2);
		c = new tree(2953,-2171,2);
		c = new tree(2932,-2198,0);
		c = new tree(2945,-2213,2);
		c = new tree(2945,-2243,1);
		c = new tree(2936,-2228,0);
		c = new tree(2942,-2271,0);
		c = new tree(2940,-2288,1);
		c = new tree(2940,-2315,2);
		c = new tree(2959,-2329,0);
		c = new tree(2930,-2348,2);
		c = new tree(2951,-2372,2);
		c = new tree(2489,-2349,0);
		c = new tree(-1079,-983,0);
		c = new tree(2959,-605,2);
		c = new tree(2930,-623,1);
		c = new tree(2942,-661,2);
		c = new tree(2933,-688,0);
		c = new tree(2951,-728,1);
		c = new tree(2924,-740,0);
		c = new tree(2942,-782,2);
		c = new tree(2931,-818,1);
		c = new tree(2926,-852,2);
		c = new tree(2921,-878,0);
		c = new tree(2942,-902,2);
		c = new tree(2952,-945,2);
		c = new tree(2925,-932,1);
		c = new tree(2942,-969,0);
		c = new tree(2924,-999,0);
		c = new tree(2945,-1025,0);
		c = new tree(2932,-1047,2);
		c = new tree(2945,-1076,2);
		c = new tree(2918,-1076,1);
		c = new tree(2931,-1103,1);
		c = new tree(2943,-1134,0);
		c = new tree(2918,-1156,2);
		c = new tree(2932,-1179,1);
		c = new tree(2928,-1212,2);
		c = new tree(2947,-1237,2);
		c = new tree(2924,-1265,0);
		c = new tree(2928,-1296,2);
		c = new tree(2951,-1328,0);
		c = new tree(2938,-1350,0);
		c = new tree(2924,-1392,0);
		c = new tree(2948,-1371,1);
		c = new tree(2934,-1429,0);
		c = new tree(2934,-1402,0);
		c = new tree(2925,-1468,1);
		c = new tree(2943,-1490,2);
		c = new tree(2943,-1454,2);
		c = new tree(2943,-1508,0);
		c = new tree(2924,-1536,1);
		c = new tree(2945,-1565,1);
		c = new tree(2913,-1576,0);
		c = new tree(2937,-1599,0);
		c = new tree(2917,-1622,0);
		c = new tree(2935,-1640,2);
		c = new tree(2396,-575,0);
		c = new tree(2414,-599,1);
		c = new tree(2382,-629,1);
		c = new tree(2407,-661,2);
		c = new tree(2434,-679,0);
		c = new tree(2409,-699,2);
		c = new tree(2434,-718,0);
		c = new tree(2409,-736,1);
		c = new tree(2431,-770,0);
		c = new tree(2440,-791,2);
		c = new tree(2421,-819,2);
		c = new tree(2437,-841,2);
		c = new tree(2410,-865,2);
		c = new tree(2430,-894,1);
		c = new tree(2411,-919,1);
		c = new tree(2440,-944,2);
		c = new tree(2414,-967,1);
		c = new tree(2430,-989,2);
		c = new tree(2409,-1014,0);
		c = new tree(2427,-1035,0);
		c = new tree(2434,-1060,2);
		c = new tree(2428,-1090,0);
		c = new tree(2409,-1106,2);
		c = new tree(2427,-1127,0);
		c = new tree(2409,-1167,1);
		c = new tree(2435,-1184,2);
		c = new tree(2435,-1160,0);
		c = new tree(2423,-1199,0);
		c = new tree(2417,-1226,2);
		c = new tree(2438,-1244,0);
		c = new tree(2417,-1264,0);
		c = new tree(2439,-1289,2);
		c = new tree(2417,-1311,2);
		c = new tree(2441,-1333,0);
		c = new tree(2422,-1349,2);
		c = new tree(2449,-1379,1);
		c = new tree(2413,-1364,1);
		c = new tree(2431,-1403,2);
		c = new tree(2427,-1417,1);
		c = new tree(2440,-1449,0);
		c = new tree(2419,-1434,0);
		c = new tree(2436,-1481,1);
		c = new tree(2415,-1463,0);
		c = new tree(2415,-1511,1);
		c = new tree(2430,-1535,2);
		c = new tree(2411,-1561,0);
		c = new tree(2431,-1593,1);
		c = new tree(2431,-1569,0);
		c = new tree(2437,-1607,0);
		c = new tree(2422,-1622,2);
		c = new tree(2453,-1647,1);
		c = new tree(2419,-1143,0);
		c = new tree(2412,-752,2);
		c = new tree(2398,-556,2);
		c = new tree(2939,-801,1);
		c = new tree(2950,-757,1);
		c = new tree(2930,-638,2);
		c = new tree(2936,-705,2);
		c = new tree(2924,-834,1);
	}
	
	// Forest area above spawn.
	public void createForestAboveSpawn() {
		
		///////////////////
		//// FOREST ///////
		///////////////////
		
		// Spawn forest grass
		spawnGrassRect(-2000-1000,-4000-1000,2000+1000,-440);
		
		// Spawn mountain above grass with a little hole for the
		// cave enterance
		spawnMountainRect(-2000-1000,-4460+12-1000,-1712,-4000-1000);
		spawnMountainRect(-1706-14+1,-4460+12-1000,2000+1000,-4000-32-1000);
		spawnMountainRect(-1712-30+35+20,-4035-1000+3,2016+1000,-4000-1000);
		
		// Spawn river to left of mountain
		spawnWaterRect(-2128-1000,-4466+2-1000-16,-2000-1000,-2926-1000);
		spawnWaterRect(-2128-1000,-2900-1000-12,-2000-1000,200);
		
		// Spawn river to right of mountain
		spawnWaterRect(2000+1000-16,-4466+18-1000,2128+1000,200);
		
		// Cave to top left.
		caveEnterance spiderCaveEnterance = new caveEnterance(-1762+20+30,-4070+6+14-1000,0, spiderCave.getZone(),100,-6,"Right");
		
		// Bottle
		item b = new normalBottle(15,-862);
		
		// Well.
		c = new well(1142,-1353,0);
		
		// Trees and stuff (Generated by map maker)
		 c = new tree(-1067,-436,1);
		 c = new tree(-1088,-419,1);
		 c = new tree(-1073,-402,2);
		 c = new tree(-1094,-384,2);
		 c = new tree(-1067,-369,0);
		 c = new tree(-1092,-338,1);
		 c = new tree(-1080,-314,0);
		 c = new tree(-1096,-292,0);
		 c = new tree(-1076,-271,0);
		 c = new tree(-1103,-238,1);
		 c = new tree(-1106,-257,2);
		 c = new tree(-1115,-358,1);
		 c = new tree(-1106,-198,0);
		 c = new tree(-1141,-222,1);
		 c = new tree(-1099,-171,0);
		 c = new tree(-1120,-141,0);
		 c = new tree(-1147,-164,0);
		 c = new tree(-1112,-120,2);
		 c = new tree(-1130,-96,2);
		 c = new tree(-1097,-72,1);
		 c = new tree(-1097,-42,0);
		 c = new tree(-1139,-60,0);
		 c = new tree(-1139,-9,2);
		 c = new tree(-1092,-14,0);
		 c = new tree(-1092,15,2);
		 c = new tree(-1077,30,1);
		 c = new tree(471,-439,1);
		 c = new tree(498,-427,2);
		 c = new tree(474,-388,2);
		 c = new tree(504,-397,0);
		 c = new tree(504,-358,1);
		 c = new tree(489,-340,1);
		 c = new tree(483,-316,0);
		 c = new tree(510,-298,1);
		 c = new tree(529,-264,2);
		 c = new tree(490,-237,0);
		 c = new tree(514,-209,0);
		 c = new tree(481,-185,1);
		 c = new tree(510,-157,2);
		 c = new tree(526,-120,0);
		 c = new tree(493,-96,2);
		 c = new tree(521,-82,2);
		 c = new tree(521,-52,2);
		 c = new tree(488,-25,0);
		 c = new tree(518,7,1);
		 c = new tree(485,28,2);
		 c = new tree(528,-137,1);
		 c = new tree(523,-281,2);
		 c = new tree(-311,-457,1);
		 c = new tree(-311,-481,1);
		 c = new tree(-323,-499,1);
		 c = new tree(-307,-518,0);
		 c = new tree(-328,-545,1);
		 c = new tree(-313,-571,1);
		 c = new tree(-338,-592,1);
		 c = new tree(-295,-611,1);
		 c = new tree(-315,-630,2);
		 c = new tree(-296,-651,0);
		 c = new tree(-307,-674,0);
		 c = new tree(-280,-695,0);
		 c = new tree(-309,-708,1);
		 c = new tree(-318,-729,2);
		 c = new tree(-336,-747,0);
		 c = new tree(-318,-767,1);
		 c = new tree(-306,-794,1);
		 c = new tree(-335,-817,0);
		 c = new tree(-305,-836,1);
		 c = new tree(-335,-856,1);
		 c = new tree(-304,-872,1);
		 c = new tree(282,-457,0);
		 c = new tree(266,-475,1);
		 c = new tree(287,-496,0);
		 c = new tree(268,-518,0);
		 c = new tree(300,-529,1);
		 c = new tree(267,-550,2);
		 c = new tree(285,-568,2);
		 c = new tree(276,-589,2);
		 c = new tree(276,-610,1);
		 c = new tree(269,-629,0);
		 c = new tree(269,-653,0);
		 c = new tree(290,-667,2);
		 c = new tree(272,-688,2);
		 c = new tree(272,-715,2);
		 c = new tree(257,-742,2);
		 c = new tree(279,-772,0);
		 c = new tree(287,-750,2);
		 c = new tree(265,-789,2);
		 c = new tree(284,-813,0);
		 c = new tree(270,-846,1);
		 c = new tree(276,-831,2);
		 c = new tree(282,-864,2);
		 c = new tree(-308,-888,1);
		 c = new tree(-292,-922,2);
		 c = new tree(-277,-955,0);
		 c = new tree(-281,-978,0);
		 c = new tree(-265,-1000,2);
		 c = new tree(-241,-1019,2);
		 c = new tree(-250,-1034,1);
		 c = new tree(-240,-1053,0);
		 c = new tree(-215,-1075,2);
		 c = new tree(-214,-1095,2);
		 c = new tree(-187,-1122,1);
		 c = new tree(-187,-1104,2);
		 c = new tree(-183,-1141,2);
		 c = new tree(-162,-1159,1);
		 c = new tree(-149,-1180,2);
		 c = new tree(-118,-1197,1);
		 c = new tree(-103,-1224,1);
		 c = new tree(-96,-1208,1);
		 c = new tree(-94,-1240,2);
		 c = new tree(-76,-1264,1);
		 c = new tree(-50,-1290,1);
		 c = new tree(-17,-1311,1);
		 c = new tree(282,-880,1);
		 c = new tree(316,-923,1);
		 c = new tree(335,-947,2);
		 c = new tree(355,-971,2);
		 c = new tree(371,-982,0);
		 c = new tree(391,-1002,2);
		 c = new tree(420,-1003,0);
		 c = new tree(439,-1027,1);
		 c = new tree(460,-1015,1);
		 c = new tree(481,-1039,0);
		 c = new tree(513,-1059,0);
		 c = new tree(546,-1035,1);
		 c = new tree(540,-1056,0);
		 c = new tree(19,-1324,2);
		 c = new tree(41,-1346,1);
		 c = new tree(74,-1361,0);
		 c = new tree(106,-1367,1);
		 c = new tree(133,-1388,1);
		 c = new tree(164,-1366,0);
		 c = new tree(174,-1390,1);
		 c = new tree(201,-1411,2);
		 c = new tree(238,-1420,0);
		 c = new tree(265,-1402,2);
		 c = new tree(297,-1422,0);
		 c = new tree(326,-1438,0);
		 c = new tree(358,-1459,1);
		 c = new tree(403,-1460,1);
		 c = new tree(379,-1445,1);
		 c = new tree(590,-1044,1);
		 c = new tree(602,-1044,0);
		 c = new tree(632,-1044,1);
		 c = new tree(654,-1051,0);
		 c = new tree(683,-1071,2);
		 c = new tree(700,-1054,2);
		 c = new tree(739,-1028,0);
		 c = new tree(778,-1043,1);
		 c = new tree(697,-1025,0);
		 c = new tree(438,-1470,2);
		 c = new tree(478,-1471,2);
		 c = new tree(512,-1480,0);
		 c = new tree(532,-1465,0);
		 c = new tree(556,-1483,0);
		 c = new tree(583,-1459,0);
		 c = new tree(627,-1473,1);
		 c = new tree(612,-1458,2);
		 c = new tree(657,-1479,2);
		 c = new tree(678,-1458,0);
		 c = new tree(714,-1510,2);
		 c = new tree(699,-1489,0);
		 c = new tree(741,-1489,0);
		 c = new tree(756,-1504,2);
		 c = new tree(797,-1484,1);
		 c = new tree(795,-1500,2);
		 c = new tree(849,-1500,0);
		 c = new tree(834,-1485,0);
		 c = new tree(894,-1497,2);
		 c = new tree(876,-1479,2);
		 c = new tree(918,-1512,0);
		 c = new tree(939,-1500,0);
		 c = new tree(987,-1500,1);
		 c = new tree(1023,-1516,2);
		 c = new tree(817,-1056,0);
		 c = new tree(848,-1075,2);
		 c = new tree(886,-1057,0);
		 c = new tree(918,-1068,1);
		 c = new tree(1059,-1510,0);
		 c = new tree(1096,-1507,2);
		 c = new tree(1121,-1521,0);
		 c = new tree(1156,-1506,2);
		 c = new tree(1195,-1506,2);
		 c = new tree(1224,-1494,1);
		 c = new tree(1262,-1477,0);
		 c = new tree(1296,-1493,1);
		 c = new tree(1319,-1468,2);
		 c = new tree(1366,-1450,0);
		 c = new tree(1352,-1468,2);
		 c = new tree(1391,-1426,1);
		 c = new tree(1414,-1394,0);
		 c = new tree(1441,-1412,2);
		 c = new tree(1422,-1377,1);
		 c = new tree(1420,-1429,2);
		 c = new tree(1465,-1371,0);
		 c = new tree(1447,-1347,0);
		 c = new tree(1476,-1333,2);
		 c = new tree(961,-1084,0);
		 c = new tree(961,-1063,0);
		 c = new tree(1022,-1077,0);
		 c = new tree(1004,-1065,1);
		 c = new tree(1043,-1063,1);
		 c = new tree(1065,-1040,2);
		 c = new tree(1101,-1050,0);
		 c = new tree(1127,-1024,1);
		 c = new tree(1118,-1005,2);
		 c = new tree(1157,-978,2);
		 c = new tree(1147,-992,1);
		 c = new tree(1521,-1321,2);
		 c = new tree(1524,-1306,1);
		 c = new tree(1488,-1318,1);
		 c = new tree(1559,-1309,2);
		 c = new tree(1581,-1287,1);
		 c = new tree(1547,-1282,0);
		 c = new tree(1572,-1240,2);
		 c = new tree(1565,-1260,2);
		 c = new tree(1586,-1210,1);
		 c = new tree(1586,-1188,2);
		 c = new tree(1604,-1165,0);
		 c = new tree(1591,-1143,0);
		 c = new tree(1153,-963,1);
		 c = new tree(1162,-944,2);
		 c = new tree(1144,-924,1);
		 c = new tree(1168,-903,2);
		 c = new tree(1136,-882,1);
		 c = new tree(1166,-858,2);
		 c = new tree(1131,-844,1);
		 c = new tree(1169,-827,0);
		 c = new tree(1139,-804,0);
		 c = new tree(1161,-785,0);
		 c = new tree(1161,-755,1);
		 c = new tree(1137,-725,1);
		 c = new tree(1606,-1124,1);
		 c = new tree(1606,-1124,2);
		 c = new tree(1617,-1101,1);
		 c = new tree(1602,-1069,1);
		 c = new tree(1627,-1088,1);
		 c = new tree(1616,-1038,0);
		 c = new tree(1620,-1057,0);
		 c = new tree(1606,-1008,1);
		 c = new tree(1621,-1017,2);
		 c = new tree(1618,-987,1);
		 c = new tree(1606,-957,0);
		 c = new tree(1631,-925,2);
		 c = new tree(1641,-971,2);
		 c = new tree(1629,-947,0);
		 c = new tree(1598,-896,2);
		 c = new tree(1634,-890,0);
		 c = new tree(1120,-703,1);
		 c = new tree(1103,-678,0);
		 c = new tree(1132,-683,1);
		 c = new tree(1131,-656,0);
		 c = new tree(1114,-630,2);
		 c = new tree(1113,-601,0);
		 c = new tree(1132,-615,1);
		 c = new tree(1615,-859,0);
		 c = new tree(1636,-841,2);
		 c = new tree(1621,-822,1);
		 c = new tree(1647,-862,0);
		 c = new tree(1620,-787,2);
		 c = new tree(1659,-783,1);
		 c = new tree(1650,-798,2);
		 c = new tree(1629,-743,1);
		 c = new tree(1642,-762,0);
		 c = new tree(1653,-730,2);
		 c = new tree(1636,-707,2);
		 c = new tree(1658,-688,1);
		 c = new tree(1631,-661,1);
		 c = new tree(1122,-572,2);
		 c = new tree(1123,-484,1);
		 c = new tree(1123,-511,1);
		 c = new tree(1108,-520,2);
		 c = new tree(1117,-536,0);
		 c = new tree(1131,-556,0);
		 c = new tree(1650,-637,0);
		 c = new tree(1650,-604,0);
		 c = new tree(1626,-625,2);
		 c = new tree(1644,-588,2);
		 c = new tree(1623,-561,2);
		 c = new tree(1656,-540,2);
		 c = new tree(1630,-514,2);
		 c = new tree(1123,-466,1);
		 c = new tree(1123,-430,1);
		 c = new tree(1126,-443,2);
		 c = new tree(1093,-416,0);
		 c = new tree(1113,-398,2);
		 c = new tree(1112,-369,0);
		 c = new tree(1096,-335,2);
		 c = new tree(1114,-387,2);
		 c = new tree(1108,-347,0);
		 c = new tree(1120,-315,2);
		 c = new tree(1104,-295,2);
		 c = new tree(1116,-271,2);
		 c = new tree(1109,-237,2);
		 c = new tree(1114,-252,1);
		 c = new tree(1114,-209,1);
		 c = new tree(1105,-218,0);
		 c = new tree(1639,-525,0);
		 c = new tree(1105,-189,1);
		 c = new tree(1121,-170,2);
		 c = new tree(1153,-150,1);
		 c = new tree(1144,-163,1);
		 c = new tree(1161,-129,0);
		 c = new tree(1183,-139,0);
		 c = new tree(1203,-116,1);
		 c = new tree(1211,-89,2);
		 c = new tree(1229,-98,0);
		 c = new tree(1235,-69,2);
		 c = new tree(1263,-60,0);
		 c = new tree(1282,-39,1);
		 c = new tree(1298,-58,2);
		 c = new tree(1304,-40,0);
		 c = new tree(1315,-32,1);
		 c = new tree(1309,-14,1);
		 c = new tree(1289,5,1);
		 c = new tree(1307,23,1);
		 c = new tree(1321,37,0);
		 c = new tree(1600,-61,2);
		 c = new tree(1576,-52,1);
		 c = new tree(1543,-60,2);
		 c = new tree(1517,-46,0);
		 c = new tree(1483,-59,2);
		 c = new tree(1440,-40,0);
		 c = new tree(1404,-54,2);
		 c = new tree(1353,-39,0);
		 c = new tree(1368,-54,0);
		 c = new tree(308,-896,2);
		
	}
	
	public void spawnMetaGraves() {
		int numRows = 9;
		int numCols = 9;
		for(int i = 0; i <= numRows; i++) {
			for(int j = 2; j <= numCols; j++) {
				System.out.println("c = new grave(" + (1196+ i*((2120-1196)/numCols)) + "," + (-3237 + j*((-2628+3237)/numRows)) + "," + 4 + ");");
			}
		}
	}
	
	public void createGraveYard() {
		
		// Tomb
		c = new tomb(2305+2,-3944-85,0, tombZone.getZone(),57,-6,"Right");
		
		// Fence around tomb
		spawnFence(null, 2216,-3990,2216,-3820); // Left fence
		spawnFence(null, 2216+5,-3890-8,2330,-3890-8); // Left bottom
		spawnFence(null, 2216+220,-3990,2216+220,-3820); // Right fence
		spawnFence(null, 2216+5+166,-3890-8,2330+166,-3890-8); // Right bottom
		c = new statue(2253,-3907-37,0);
		c = new statue(2397,-3907-37,1);
		c = new flower(2249,-3902-20,10);
		c.setInteractable(false);
		c = new flower(2396,-3902-20,7);
		c.setInteractable(false);
		
		// First grave area.
		c = new grave(2225,-3116,1);
		c = new grave(2225,-2995,2);
		c = new grave(2225,-2874,2);
		c = new grave(2225,-2753,2);
		c = new grave(2225,-2632,2);
		c = new grave(2382,-3116,0);
		c = new grave(2382,-2995,2);
		c = new grave(2382,-2874,2);
		c = new grave(2382,-2753,0);
		c = new grave(2382,-2632,2);
		c = new grave(2539,-3116,2);
		c = new grave(2539,-2995,2);
		c = new grave(2539,-2874,2);
		c = new grave(2539,-2753,2);
		c = new grave(2539,-2632,2);
		c = new grave(2696,-3116,2);
		c = new grave(2696,-2995,2);
		c = new grave(2696,-2874,2);
		c = new grave(2696,-2753,2);
		c = new grave(2696,-2632,2);
		c = new grave(2853,-3116,1);
		c = new grave(2853,-2995,2);
		c = new grave(2853,-2874,2);
		c = new grave(2853,-2753,2);
		c = new grave(2853,-2632,2);
		c = new flower(2390,-2729,10);
		c.setInteractable(false);
		c = new flower(2359,-2731,3);
		c.setInteractable(false);
		c = new flower(2410,-2731,5);
		c.setInteractable(false);
		c = new flower(2412,-2735,4);
		c.setInteractable(false);
		c = new flower(2421,-2719,8);
		c.setInteractable(false);
		c = new flower(2349,-2719,6);
		c.setInteractable(false);
		c = new flower(2371,-2721,3);
		c.setInteractable(false);
		c = new flower(2404,-2721,7);
		c.setInteractable(false);
		c = new flower(2842,-2633,7);
		c.setInteractable(false);
		c = new flower(2869,-2633,8);
		c.setInteractable(false);
		c = new flower(2677,-2622,2);
		c.setInteractable(false);
		c = new flower(2728,-2622,6);
		c.setInteractable(false);
		spawnFence(null, 2927-8,-3183+26,2927-8,-2544+300); // Vertical, right
		spawnFence(null, 2168-1000-8,-3183,2927+50,-3183); // Top
		spawnFence(null, 2168+3,-3183+225,2168+3,-2565+200); // First left fence
		spawnFence(null, 2168-1000-5,-3183+225+1,2168-1000-5,-2565+200); // Second left fence
		spawnFence(null, 2168-1000,-2565,2929-200,-2565); // Bottom
		
		// Flowers in first grave area.
		c = new flower(2688,-2749,0);
		c.setInteractable(false);
		c = new flower(2712,-2749,2);
		c.setInteractable(false);
		c = new flower(2372,-2873,7);
		c.setInteractable(false);
		c = new flower(2402,-2873,8);
		c.setInteractable(false);
		c = new flower(2376,-2747,6);
		c.setInteractable(false);
		c = new flower(2397,-2747,3);
		c.setInteractable(false);
		c = new flower(2387,-2751,8);
		c.setInteractable(false);
		c = new flower(2690,-3119,6);
		c.setInteractable(false);
		c = new flower(2705,-3116,5);
		c.setInteractable(false);
		c = new flower(2208,-3114,9);
		c.setInteractable(false);
		c = new flower(2208,-3099,3);
		c.setInteractable(false);
		c = new flower(2208,-3081,8);
		c.setInteractable(false);
		c = new flower(2208+43,-3114,9);
		c.setInteractable(false);
		c = new flower(2208+43,-3099,3);
		c.setInteractable(false);
		c = new flower(2208+43,-3081,8);
		c.setInteractable(false);
		
		// Second grave area.
		c = new grave(1196,-3103,1);
		c = new grave(1196,-3036,2);
		c = new grave(1196,-2969,2);
		c = new grave(1196,-2902,2);
		c = new grave(1196,-2835,2);
		c = new grave(1196,-2768,2);
		c = new grave(1196,-2701,1);
		c = new grave(1196,-2634,2);
		c = new grave(1298,-3103,2);
		c = new grave(1298,-3036,2);
		c = new grave(1298,-2969,2);
		c = new grave(1298,-2902,2);
		c = new grave(1298,-2835,2);
		c = new grave(1298,-2768,2);
		c = new grave(1298,-2701,2);
		c = new grave(1298,-2634,1);
		c = new grave(1400,-3103,2);
		c = new grave(1400,-3036,2);
		c = new grave(1400,-2969,2);
		c = new grave(1400,-2902,2);
		c = new grave(1400,-2835,2);
		c = new grave(1400,-2768,2);
		c = new grave(1400,-2701,2);
		c = new grave(1400,-2634,2);
		c = new grave(1502,-3103,2);
		c = new grave(1502,-3036,1);
		c = new grave(1502,-2969,1);
		c = new grave(1502,-2902,2);
		c = new grave(1502,-2835,2);
		c = new grave(1502,-2768,2);
		c = new grave(1502,-2701,2);
		c = new grave(1502,-2634,2);
		c = new grave(1604,-3103,2);
		c = new grave(1604,-3036,2);
		c = new grave(1604,-2969,2);
		c = new grave(1604,-2902,2);
		c = new grave(1604,-2835,2);
		c = new grave(1604,-2768,2);
		c = new grave(1604,-2701,2);
		c = new grave(1604,-2634,2);
		c = new grave(1706,-3103,2);
		c = new grave(1706,-3036,2);
		c = new grave(1706,-2969,1);
		c = new grave(1706,-2902,2);
		c = new grave(1706,-2835,2);
		c = new grave(1706,-2768,2);
		c = new grave(1706,-2701,2);
		c = new grave(1706,-2634,2);
		c = new grave(1808,-3103,2);
		c = new grave(1808,-3036,2);
		c = new grave(1808,-2969,1);
		c = new grave(1808,-2902,2);
		c = new grave(1808,-2835,2);
		c = new grave(1808,-2768,2);
		c = new grave(1808,-2701,2);
		c = new grave(1808,-2634,2);
		c = new grave(1910,-3103,2);
		c = new grave(1910,-3036,2);
		c = new grave(1910,-2969,2);
		c = new grave(1910,-2902,2);
		c = new grave(1910,-2835,2);
		c = new grave(1910,-2768,2);
		c = new grave(1910,-2701,2);
		c = new grave(1910,-2634,2);
		c = new grave(2012,-3103,2);
		c = new grave(2012,-3036,2);
		c = new grave(2012,-2969,2);
		c = new grave(2012,-2902,2);
		c = new grave(2012,-2835,1);
		c = new grave(2012,-2768,2);
		c = new grave(2012,-2701,2);
		c = new grave(2012,-2634,2);
		c = new grave(2114,-3103,2);
		c = new grave(2114,-3036,1);
		c = new grave(2114,-2969,2);
		c = new grave(2114,-2902,2);
		c = new grave(2114,-2835,2);
		c = new grave(2114,-2768,1);
		c = new grave(2114,-2701,2);
		c = new grave(2114,-2634,2);
		
		// Far left area.
		c = new tree(818,-3104,1);
		c = new grave(628,-2941,0);
		c = new grave(731,-2850,2);
		c = new grave(814,-2939,2);
		c = new grave(950,-2985,1);
		c = new grave(1046,-2895,2);
		c = new grave(1092,-2789,2);
		c = new grave(1061,-2688,2);
		c = new grave(1033,-2597,2);
		c = new grave(945,-2634,1);
		c = new grave(884,-2561,2);
		c = new grave(732,-2712,0);
		c = new grave(586,-2581,2);
		c = new grave(450,-2562,0);
		c = new grave(342,-2739,2);
		c = new grave(451,-2838,2);
		c = new grave(279,-3016,2);
		c = new grave(454,-3095,2);
		c = new grave(699,-3143,2);
		c = new grave(895,-3219,2);
		c = new grave(1029,-3270,2);
		c = new grave(1092,-3351,2);
		c = new grave(882,-3474,1);
		c = new grave(752,-3413,2);
		c = new grave(568,-3387,2);
		c = new grave(314,-3383,2);
		c = new grave(259,-3262,2);
		c = new grave(594,-3608,1);
		c = new grave(462,-3685,2);
		c = new grave(369,-3776,1);
		c = new grave(350,-3896,0);
		c = new grave(581,-3801,2);
		c = new grave(678,-3862,0);
		c = new grave(752,-3903,0);
		c = new grave(816,-3951,0);
		c = new grave(640,-3929,2);
		c = new tree(1156,-2532,2);
		c = new tree(1162,-2511,0);
		c = new tree(1143,-2497,1);
		c = new tree(1159,-2466,2);
		c = new tree(1134,-2449,0);
		c = new tree(1158,-2428,2);
		c = new tree(1128,-2410,0);
		c = new tree(1144,-2381,0);
		c = new tree(1017,-3099,0);
		c = new tree(892,-2872,1);
		c = new tree(1001,-2741,0);
		c = new tree(827,-2651,0);
		c = new tree(661,-2586,2);
		c = new tree(596,-2758,0);
		c = new tree(488,-2656,1);
		c = new tree(349,-2867,0);
		c = new tree(322,-2611,2);
		c = new tree(595,-3052,2);
		c = new tree(409,-3166,1);
		c = new tree(732,-3228,2);
		c = new tree(898,-3316,1);
		c = new tree(1039,-3424,0);
		c = new tree(1004,-3593,2);
		c = new tree(743,-3616,1);
		c = new tree(674,-3501,0);
		c = new tree(462,-3594,1);
		c = new tree(338,-3508,0);
		c = new tree(206,-3589,0);
		c = new bush(456,-3570,0);
		c = new bush(704,-3484,0);
		c = new bush(1022,-3589,0);
		c = new bush(1005,-3397,1);
		c = new bush(693,-3220,2);
		c = new bush(718,-3249,0);
		c = new bush(594,-3073,0);
		c = new bush(570,-3037,0);
		c = new bush(595,-2789,2);
		c = new bush(615,-2731,0);
		c = new bush(893,-2843,1);
		c = new bush(1030,-3114,0);
		c = new bush(1003,-3082,0);
		c = new bush(892,-3300,2);
		c = new grave(698,-3965,0);
		c = new grave(821,-3816,2);
		c = new grave(903,-3776,2);
		c = new grave(946,-3861,2);
		c = new grave(914,-3945,2);
		c = new grave(1019,-3729,0);
		c = new grave(1131,-3749,2);
		c = new grave(1085,-3879,2);
		c = new grave(1174,-3968,2);
		c = new grave(1225,-3917,2);
		c = new grave(1075,-3965,2);
		c = new grave(1201,-3836,2);
		c = new grave(1301,-3901,1);
		c = new grave(1378,-3954,1);
		c = new grave(1417,-3774,2);
		c = new grave(1490,-3829,2);
		c = new grave(1518,-3921,0);
		c = new grave(1596,-3954,1);
		c = new grave(1415,-3887,0);
		c = new grave(1628,-3857,1);
		c = new grave(1603,-3778,2);
		c = new grave(1704,-3942,1);
		c = new grave(1746,-3840,0);
		c = new grave(1849,-3779,2);
		c = new grave(1881,-3868,2);
		c = new grave(1828,-3931,1);
		c = new grave(1961,-3931,2);
		c = new grave(1965,-3823,1);
		c = new grave(2002,-3744,2);
		c = new grave(2065,-3917,2);
		c = new grave(2100,-3968,1);
		c = new grave(2114,-3714,2);
		c = new grave(2140,-3779,2);
		c = new grave(2163,-3883,2);
		c = new grave(2210,-3705,0);
		c = new grave(2275,-3667,2);
		c = new grave(2333,-3707,2);
		c = new grave(2293,-3753,0);
		c = new grave(1011,-3964,2);
		c = new grave(748,-3787,1);
		c = new grave(544,-3876,2);
		c = new grave(479,-3944,2);
		c = new grave(370,-3991,2);
		c = new grave(256,-3949,0);
		c = new grave(279,-3794,2);
		c = new grave(471,-3794,2);
		c = new grave(879,-3628,2);
		c = new grave(466,-3407,2);
		c = new grave(274,-2537,2);
		c = new flower(542,-3340,2);
		c = new flower(943,-2857,4);
		c = new flower(949,-3139,8);
		c = new flower(1099,-3676,10);
		c = new flower(856,-3880,7);
		c = new flower(591,-3859,7);
		c = new flower(417,-3940,2);
		c = new tree(1166,-3176,1);
		c = new tree(1181,-3197,1);
		c = new tree(1166,-3221,2);
		c = new tree(1189,-3242,2);
		c = new tree(1168,-3278,1);
		c = new tree(1192,-3262,1);
		c = new tree(1191,-3304,1);
		c = new tree(1193,-3331,1);
		c = new tree(1191,-3356,2);
		c = new tree(1203,-3383,0);
		c = new tree(1178,-3403,2);
		c = new tree(1204,-3426,2);
		c = new tree(1184,-3462,0);
		c = new tree(1206,-3444,0);
		c = new tree(1210,-3478,2);
		c = new tree(1194,-3503,0);
		c = new tree(1213,-3525,2);
		c = new tree(1191,-3546,2);
		c = new tree(1218,-3567,1);
		c = new tree(1197,-3595,1);
		c = new tree(1220,-3621,1);
		c = new tree(1243,-3661,2);
		c = new tree(1219,-3643,1);
		c = new tree(1215,-3693,1);
		c = new tree(1248,-3672,2);
		
		// Trees
		c = new tree(483,-4060,1);
		c = new tree(460,-4070,1);
		c = new tree(444,-4062,1);
		c = new tree(417,-4062,2);
		c = new tree(393,-4062,1);
		c = new tree(351,-4057,1);
		c = new tree(321,-4057,0);
		c = new tree(273,-4075,1);
		c = new tree(240,-4075,2);
		c = new tree(225,-4045,2);
		c = new tree(276,-4043,2);
		c = new tree(340,-4041,1);
		c = new tree(432,-4126,0);
		c = new tree(251,-4126,0);
		c = new tree(205,-4061,2);
		c = new tree(1254,-3707,1);
		c = new tree(1275,-3707,1);
		c = new tree(1301,-3696,1);
		c = new tree(1316,-3676,1);
		c = new tree(1357,-3670,2);
		c = new tree(1383,-3645,1);
		c = new tree(1419,-3645,1);
		c = new tree(1413,-3622,0);
		c = new tree(1464,-3652,2);
		c = new tree(1454,-3626,1);
		c = new tree(1498,-3647,0);
		c = new tree(1507,-3638,1);
		c = new tree(1537,-3635,2);
		c = new tree(1563,-3655,2);
		c = new tree(1599,-3670,0);
		c = new tree(1622,-3647,0);
		c = new tree(1647,-3651,1);
		c = new tree(1671,-3669,0);
		c = new tree(1695,-3660,1);
		c = new tree(1737,-3666,1);
		c = new tree(1768,-3650,0);
		c = new tree(1792,-3672,2);
		c = new tree(1816,-3648,1);
		c = new tree(1861,-3667,2);
		c = new tree(1837,-3646,0);
		c = new tree(1897,-3672,2);
		c = new tree(1900,-3647,0);
		c = new tree(1936,-3632,1);
		c = new tree(1966,-3614,0);
		c = new tree(1991,-3592,0);
		c = new tree(2024,-3592,2);
		c = new tree(2055,-3569,2);
		c = new tree(2081,-3543,2);
		c = new tree(2106,-3563,2);
		c = new tree(2130,-3533,0);
		c = new tree(2152,-3511,0);
		c = new tree(2182,-3532,0);
		c = new tree(2192,-3507,2);
		c = new tree(2213,-3474,0);
		c = new tree(2232,-3481,0);
		c = new tree(2251,-3500,1);
		c = new tree(2261,-3478,2);
		c = new tree(2309,-3505,1);
		c = new tree(2300,-3484,2);
		c = new tree(2351,-3526,1);
		c = new tree(2351,-3511,1);
		c = new tree(2367,-3539,1);
		c = new tree(2387,-3559,0);
		c = new tree(2396,-3580,1);
		c = new tree(2412,-3601,1);
		c = new tree(2432,-3610,0);
		c = new tree(2432,-3634,1);
		c = new tree(2444,-3654,1);
		c = new tree(2464,-3666,1);
		c = new tree(2461,-3693,1);
		c = new tree(2485,-3714,2);
		c = new tree(2498,-3739,1);
		c = new tree(2480,-3755,0);
		c = new tree(2498,-3778,2);
		c = new tree(2487,-3795,2);
		c = new tree(2506,-3815,2);
		c = new tree(2476,-3836,1);
		c = new tree(2498,-3861,0);
		c = new tree(2515,-3872,2);
		c = new tree(2498,-3894,2);
		c = new tree(2520,-3892,2);
		c = new tree(2520,-3910,2);
		c = new tree(2496,-3930,2);
		c = new tree(2512,-3958,1);
		c = new tree(2529,-3932,1);
		c = new tree(2532,-3989,2);
		c = new tree(2532,-3983,1);
		c = new tree(2923,-2561,1);
		c = new tree(2951,-2545,1);
		c = new tree(2956,-2513,0);
		c = new tree(2938,-2477,0);
		c = new tree(2950,-2441,0);
		c = new tree(2923,-2422,1);
		c = new tree(2406,-2402,1);
		c = new tree(2397,-2435,0);
		c = new tree(2370,-2459,1);
		c = new tree(2397,-2495,1);
		c = new tree(2445,-2534,2);
		c = new tree(2461,-2509,1);
		c = new tree(2482,-2476,2);
		c = new tree(2450,-2462,0);
		c = new tree(2465,-2420,2);
		c = new tree(2492,-2366,1);
		c = new tree(2456,-2366,0);
		c = new tree(2417,-2375,2);
		c = new tree(2375,-2375,2);
		c = new tree(2348,-2375,1);
		c = new tree(2312,-2381,1);
		c = new tree(2286,-2395,2);
		c = new tree(2258,-2397,2);
		c = new tree(2231,-2385,2);
		c = new tree(2187,-2386,2);
		c = new tree(2154,-2386,2);
		c = new tree(2124,-2386,1);
		c = new tree(2091,-2386,0);
		c = new tree(2091,-2398,0);
		c = new tree(2064,-2398,2);
		c = new tree(2026,-2393,1);
		c = new tree(1993,-2390,0);
		c = new tree(1968,-2390,2);
		c = new tree(1920,-2399,1);
		c = new tree(1884,-2411,2);
		c = new tree(1856,-2395,1);
		c = new tree(1810,-2387,2);
		c = new tree(1820,-2407,0);
		c = new tree(1781,-2383,0);
		c = new tree(1767,-2391,0);
		c = new tree(1732,-2392,2);
		c = new tree(1709,-2406,1);
		c = new tree(1682,-2391,2);
		c = new tree(1653,-2409,0);
		c = new tree(1625,-2399,1);
		c = new tree(1587,-2419,2);
		c = new tree(1539,-2398,1);
		c = new tree(1509,-2390,0);
		c = new tree(1474,-2376,2);
		c = new tree(1447,-2388,1);
		c = new tree(1424,-2365,2);
		c = new tree(1389,-2380,1);
		c = new tree(1362,-2380,1);
		c = new tree(1338,-2359,0);
		c = new tree(1296,-2382,2);
		c = new tree(1258,-2370,1);
		c = new tree(1216,-2370,0);
		c = new tree(1189,-2370,0);
		c = new tree(1159,-2388,2);
		c = new tree(1127,-2365,1);
		c = new tree(1085,-2365,2);
		c = new tree(1054,-2375,2);
		c = new tree(1026,-2362,0);
		c = new tree(993,-2386,0);
		c = new tree(965,-2364,2);
		c = new tree(937,-2383,2);
		c = new tree(905,-2366,2);
		c = new tree(875,-2380,0);
		c = new tree(842,-2359,0);
		c = new tree(816,-2381,0);
		c = new tree(789,-2365,1);
		c = new tree(762,-2383,1);
		c = new tree(732,-2369,1);
		c = new tree(702,-2381,2);
		c = new tree(669,-2366,0);
		c = new tree(643,-2380,2);
		c = new tree(618,-2365,0);
		c = new tree(589,-2378,1);
		c = new tree(562,-2366,1);
		c = new tree(523,-2384,1);
		c = new tree(493,-2375,2);
		c = new tree(461,-2381,0);
		c = new tree(434,-2381,1);
		c = new tree(404,-2383,0);
		c = new tree(377,-2383,0);
		c = new tree(335,-2356,2);
		c = new tree(335,-2389,0);
		c = new tree(308,-2374,1);
		c = new tree(263,-2359,2);
		c = new tree(263,-2383,1);
		c = new tree(234,-2367,1);
		c = new tree(195,-2367,2);
		c = new tree(174,-2382,1);
		c = new tree(156,-2406,2);
		c = new tree(145,-2430,2);
		c = new tree(148,-2451,0);
		c = new tree(134,-2469,2);
		c = new tree(167,-2494,2);
		c = new tree(146,-2515,2);
		c = new tree(158,-2536,1);
		c = new tree(132,-2553,1);
		c = new tree(158,-2570,1);
		c = new tree(121,-2583,0);
		c = new tree(147,-2597,0);
		c = new tree(122,-2616,0);
		c = new tree(152,-2634,0);
		c = new tree(128,-2649,0);
		c = new tree(158,-2670,2);
		c = new tree(134,-2694,2);
		c = new tree(120,-2713,1);
		c = new tree(137,-2734,2);
		c = new tree(128,-2755,1);
		c = new tree(146,-2773,1);
		c = new tree(128,-2800,0);
		c = new tree(154,-2814,1);
		c = new tree(133,-2838,0);
		c = new tree(154,-2861,1);
		c = new tree(2954,-3978,2);
		c = new tree(2922,-3983,2);
		c = new tree(2880,-3990,0);
		c = new tree(2840,-3983,0);
		c = new tree(2815,-4007,1);
		c = new tree(2772,-3988,0);
		c = new tree(2744,-3995,2);
		c = new tree(2713,-4006,1);
		c = new tree(2685,-3986,0);
		c = new tree(2658,-4007,0);
		c = new tree(2605,-3990,1);
		c = new tree(2622,-4025,2);
		c = new tree(2595,-4010,2);
		c = new tree(2559,-4004,2);
		c = new tree(2534,-4024,0);
		c = new tree(2522,-4006,0);
		c = new tree(2491,-4022,1);
		c = new tree(2477,-4005,2);
		c = new tree(2455,-4019,1);
		c = new tree(2432,-4003,1);
		c = new tree(2405,-4025,2);
		c = new tree(2383,-4002,0);
		c = new tree(2355,-4019,1);
		c = new tree(2341,-3999,2);
		c = new tree(2299,-4017,2);
		c = new tree(2262,-4018,0);
		c = new tree(2294,-3998,0);
		c = new tree(2240,-4028,0);
		c = new tree(2216,-4016,1);
		c = new tree(2184,-4029,0);
		c = new tree(2150,-4011,2);
		c = new tree(2130,-4028,1);
		c = new tree(2112,-4013,2);
		c = new tree(2076,-4028,0);
		c = new tree(2057,-4009,0);
		c = new tree(2020,-4025,2);
		c = new tree(2020,-4004,0);
		c = new tree(1978,-4019,1);
		c = new tree(1960,-4019,1);
		c = new tree(1921,-4019,1);
		c = new tree(1882,-4013,2);
		c = new tree(1859,-4033,0);
		c = new tree(1823,-4009,1);
		c = new tree(1797,-4026,0);
		c = new tree(1777,-4003,0);
		c = new tree(1737,-4021,1);
		c = new tree(1716,-4030,0);
		c = new tree(1680,-4030,2);
		c = new tree(1659,-4048,1);
		c = new tree(1634,-4024,1);
		c = new tree(1619,-4034,0);
		c = new tree(161,-2884,0);
		c = new tree(128,-2900,1);
		c = new tree(151,-2924,1);
		c = new tree(136,-2945,1);
		c = new tree(152,-2961,0);
		c = new tree(125,-2985,2);
		c = new tree(144,-3012,2);
		c = new tree(117,-3024,2);
		c = new tree(134,-3041,2);
		c = new tree(160,-3060,2);
		c = new tree(138,-3085,2);
		c = new tree(157,-3107,0);
		c = new tree(134,-3120,2);
		c = new tree(151,-3143,1);
		c = new tree(171,-3178,2);
		c = new tree(144,-3157,2);
		c = new tree(138,-3203,1);
		c = new tree(159,-3227,2);
		c = new tree(164,-3249,2);
		c = new tree(137,-3264,1);
		c = new tree(155,-3288,2);
		c = new tree(127,-3311,0);
		c = new tree(164,-3327,1);
		c = new tree(145,-3349,1);
		c = new tree(160,-3369,2);
		c = new tree(170,-3395,2);
		c = new tree(146,-3422,1);
		c = new tree(162,-3440,2);
		c = new tree(148,-3463,0);
		c = new tree(166,-3487,1);
		c = new tree(190,-3499,2);
		c = new tree(153,-3521,0);
		c = new tree(177,-3551,2);
		c = new tree(153,-3575,1);
		c = new tree(184,-3522,2);
		c = new tree(167,-3596,2);
		c = new tree(189,-3624,1);
		c = new tree(153,-3641,2);
		c = new tree(153,-3611,1);
		c = new tree(180,-3650,0);
		c = new tree(167,-3664,0);
		c = new tree(149,-3673,2);
		c = new tree(149,-3697,0);
		c = new tree(167,-3716,2);
		c = new tree(147,-3737,2);
		c = new tree(169,-3756,0);
		c = new tree(149,-3770,0);
		c = new tree(165,-3792,1);
		c = new tree(146,-3814,2);
		c = new tree(170,-3837,2);
		c = new tree(149,-3859,0);
		c = new tree(167,-3889,1);
		c = new tree(149,-3907,1);
		c = new tree(173,-3931,1);
		c = new tree(174,-3878,0);
		c = new tree(1589,-4028,0);
		c = new tree(1556,-4040,0);
		c = new tree(1530,-4026,1);
		c = new tree(1513,-4037,0);
		c = new tree(1477,-4035,2);
		c = new tree(1433,-4050,2);
		c = new tree(1450,-4028,0);
		c = new tree(1395,-4042,2);
		c = new tree(1362,-4040,0);
		c = new tree(1329,-4051,1);
		c = new tree(1313,-4030,1);
		c = new tree(1290,-4043,2);
		c = new tree(1268,-4039,0);
		c = new tree(1247,-4050,1);
		c = new tree(1228,-4034,2);
		c = new tree(1206,-4050,0);
		c = new tree(1188,-4041,1);
		c = new tree(1160,-4057,2);
		c = new tree(1138,-4030,1);
		c = new tree(1108,-4051,1);
		c = new tree(1086,-4034,2);
		c = new tree(1058,-4052,1);
		c = new tree(1039,-4033,1);
		c = new tree(1005,-4049,1);
		c = new tree(984,-4034,2);
		c = new tree(956,-4043,2);
		c = new tree(917,-4067,1);
		c = new tree(917,-4046,2);
		c = new tree(886,-4053,0);
		c = new tree(861,-4031,2);
		c = new tree(843,-4053,0);
		c = new tree(821,-4043,1);
		c = new tree(801,-4069,0);
		c = new tree(771,-4047,0);
		c = new tree(733,-4040,0);
		c = new tree(707,-4063,1);
		c = new tree(675,-4042,0);
		c = new tree(636,-4063,0);
		c = new tree(624,-4048,1);
		c = new tree(585,-4069,0);
		c = new tree(587,-4049,2);
		c = new tree(551,-4059,0);
		c = new tree(531,-4039,2);
		c = new tree(510,-4052,1);
		c = new tree(158,-3953,1);
		c = new tree(163,-3972,0);
		c = new tree(157,-3996,1);
		c = new tree(166,-4032,0);
		c = new tree(166,-4002,1);
		c = new tree(148,-4041,0);
		c = new tree(167,-4067,2);
		c = new tree(147,-4086,0);
		c = new tree(163,-4105,2);
		c = new tree(144,-4146,2);
		c = new tree(163,-4123,0);
	}
	
	public static ArrayList<chunk> makeFarlsworthFence(float atX, float atY) {
		
		// Where Farlsworth stands for the fence to spawn normally at the farm.
		float defaultX = 5;
		float defaultY = -406;
		
		// Adjust X and Y by:
		int adjustX = (int) (atX - defaultX);
		int adjustY = (int) (atY - defaultY);
		
		// Arraylist that will contain fence pices.
		ArrayList<chunk> farlsworthFence = new ArrayList<chunk>();
		int fenceAdjustX = -6;
		farlsworthFence = spawnFence(farlsworthFence, adjustX + -30+fenceAdjustX,adjustY -435,adjustX + -30+fenceAdjustX,adjustY + 200); // Vertical, right
		farlsworthFence = spawnFence(farlsworthFence, adjustX + -1050+fenceAdjustX+17,adjustY + -462,adjustX + 10+fenceAdjustX,adjustY + -462); // Horizontal, top of field
		farlsworthFence = spawnFence(farlsworthFence, adjustX + -168+40,adjustY + 17,adjustX + 70,adjustY + 17); // Horizontal, right of bridge.
		farlsworthFence = spawnFence(farlsworthFence, adjustX + -1050+fenceAdjustX+17,adjustY + 17,adjustX + -150,adjustY + 17); // Horizontal, left of bridge.
		farlsworthFence = spawnFence(farlsworthFence, adjustX + -450+fenceAdjustX,adjustY + -436,adjustX + -450+fenceAdjustX,adjustY + 200); // Vertical, far left
		
		// Draw the fence gate above the fields.
		// Fencebars to left of gate.
		c = new fenceBarsSmall(adjustX + -21+fenceAdjustX,adjustY + -436,0);
		farlsworthFence.add(c);
		c = new fenceBarsSmall(adjustX + -18+fenceAdjustX,adjustY + -436,0);
		farlsworthFence.add(c);
		c = new fenceBarsSmall(adjustX + -15+fenceAdjustX,adjustY + -436,0);
		farlsworthFence.add(c);
		c = new fenceBarsSmall(adjustX + -15+fenceAdjustX+3,adjustY + -436,0);
		farlsworthFence.add(c);
		
		// Fencebars to right of gate
		c = new fenceBarsSmall(adjustX + 32-3,adjustY + -436,0);
		farlsworthFence.add(c);
		c = new fenceBarsSmall(adjustX + 32,adjustY + -436,0);
		farlsworthFence.add(c);
		c = new fenceBarsSmall(adjustX + 35,adjustY + -436,0);
		farlsworthFence.add(c);
		c = new fenceBarsSmall(adjustX + 37,adjustY + -436,0);
		farlsworthFence.add(c);
		forestGate = new horizontalGate("Forest Gate", "Farm Key", adjustX + -13+fenceAdjustX/2,adjustY + -434,0);
		farlsworthFence.add(forestGate);
		
		///////////////////////////////
		//// FARLSWORTH'S AREA  ///////
		///////////////////////////////
		farlsworthFence = spawnFence(farlsworthFence, adjustX + 40,adjustY + -462,adjustX + 500,adjustY + -462); // Horizontal, top of field
		farlsworthFence = spawnFence(farlsworthFence, adjustX + 40,adjustY + 17,adjustX + 500,adjustY + 17); // Horizontal, bottom
		farlsworthFence = spawnFence(farlsworthFence, adjustX + 35,adjustY + -435,adjustX + 35,adjustY + 200); // Vertical, left
		farlsworthFence = spawnFence(farlsworthFence, adjustX + 455,adjustY + 1 + -436,adjustX + 455,adjustY + 300); // Vertical, right
		farlsworthFence = spawnFence(farlsworthFence, adjustX + 40,adjustY + -43,adjustX + 440,adjustY + -43); // Bottom middle area.
		
		// Left of gate.
		c = new fenceBarsSmall(adjustX + 409,adjustY + -17,0); 
		farlsworthFence.add(c);
		
		// Gate.
		horizontalGate farlsworthGate = new horizontalGate("Sheep Gate", "Farm Key", adjustX + 412,adjustY + -15,0);
		farlsworthFence.add(farlsworthGate);
		
		// Right of gate
		c = new fenceBarsSmall(adjustX + 457,adjustY + -17,0); 
		farlsworthFence.add(c);
		
		return farlsworthFence;
	}
	
	// Spawn area.
	public void createSpawnArea() {
		
		//////////////////////////
		//// ENTIRE ZONE STUFF ///
		//////////////////////////
		// Draw the grass around spawn.
		spawnGrassRect(-2-1000-1000,-1000,2000+1000,64);
		
		// Spawn some grass on the other side of the bridge.
		spawnGrassRect(-2000,184,2000,1000);
		
		//////////////////
		//// PENS  ///////
		//////////////////

		// Draw field on the left of spawn.
		u = new sheep(-378,-369);
		u = new sheep(-150,-372);
		u = new sheep(-129,-60);
		u = new sheep(-372,-36);
		c = new haystack(-294,-315,0);
		((haystack)c).setStrange(); // Add the funny interact sequence.
		c = new haystack(-195,-165,0);
		
		// Draw the bridge.
		spawnPassableWaterRect(-170-6-32,56,-165+32+32,200);
		c = new bridgePole(-192+6,35-11,0);
		c = new bridgePole(-140-2,35-10,0);
		c = new bridge(-170-16,56-33,0);
		
		// Draw the water to left of bridge spawn.
		spawnWaterRect(-2100-1000+10-6-5,56,-167,200);	
		
		// Draw the water to right of bridge spawn.
		spawnWaterRect(-168+20+15-5-6+5,56,2032+1000,200);
		
		// Draw rocks behind spawn.
		c = new rock(-24,75,0);
		c = new rock(21,111,1);
		c = new rock(-6,147,0);
	
		// Farlsworth stuff
		farlsworth sheepBoss = new farlsworth(411,-394);
		if(!farlsworth.isFenceAttached.isCompleted()) {
			farlsworthFence = makeFarlsworthFence(5,-406);
		}
		else {
			farlsworthFence = null;
		}
		
		////////////////////////////
		//// FARMHOUSE AREA  ///////
		////////////////////////////
		c = new tree(-720,-325,0);
		c = new farmHouse(-650,-420,0);
		c = new barn(-950,-420,0);
		farmer theFarmer = new farmer(-711,-267);
		theFarmer.setFacingDirection("Down");
		c = new tree(-1017, -414, 1);
		c = new tree(-1011, 0, 0);
		c = new haystack(-960,-351,1);
		c = new haystack(-875,-351,1);
		c = new bush(-1025,-130,0);
		c = new bush(-909,-9,1);
		c = new bush(-510,-330,1);
		
		/////////////////////////////////////
		//// ARMORY/SHED/ACROSS RIVER ///////
		/////////////////////////////////////
		c = new blackSmith(-600,200,0);
		c = new tree(-135,186,1);
		c = new tree(-70,220,2);
		c = new bush(-110,250,0);
		c = new tree(-120,280,1);
		c = new tree(-135,310,2);
		c = new tree(-70,340,0);
		c = new bush(-90,360,1);
		c = new tree(-90,380,1);
		c = new tree(-65,410,2);
		c = new tree(-40,450,1);
		c = new tree(-99,212,0);
		c = new tree(-120,323,1);
		c = new tree(-58,425,2);
		c = new tree(-774,428,2);
		c = new tree(-693,314,1);
		c = new tree(-627,260,1);
		
		// Dagger.
		item daggerSpawn = new dagger(-516,387);
		
		// Corner
		c = new tree(-60,480,2);
		c = new tree(-20,500,1);
		c = new tree(-55,525,2);
		c = new tree(-80,545,1);
		c = new tree(-100,570,1);
		c = new bush(-142,360,2);
		c = new bush(-250,465,0);
		
		// The bottom trees.
		c = new tree(309,-464,1);
		c = new tree(338,-450,1);
		c = new tree(366,-454,2);
		c = new tree(393,-448,2);
		c = new tree(414,-467,1);
		c = new tree(436,-448,1);
		c = new tree(461,-450,1);
		c = new tree(483,-429,1);
		c = new tree(-331,-462,0);
		c = new tree(-361,-462,2);
		c = new tree(-397,-468,2);
		c = new tree(-422,-452,0);
		c = new tree(-439,-469,1);
		c = new tree(-460,-447,1);
		c = new tree(-487,-465,2);
		c = new tree(-1003,-567,0);
		c = new tree(-993,-536,1);
		c = new tree(-1012,-520,0);
		c = new tree(-120,600,2);
		c = new tree(-145,580,0);
		c = new tree(-165,605,2);
		c = new tree(-195,585,1);
		c = new tree(-225,585,1);
		c = new tree(-240,610,0);
		c = new tree(-260,600,1);
		c = new tree(-285,575,2);
		c = new tree(-310,610,2);
		c = new tree(-330,565,2);
		c = new tree(-345,585,1);
		c = new tree(-375,615,0);
		c = new tree(-415,600,2);
		c = new tree(-445,575,0);
		c = new tree(-465,605,0);
		c = new tree(-490,585,1);
		c = new tree(-525,580,1);
		c = new tree(-550,615,2);
		c = new tree(-580,610,2);
		c = new tree(-610,580,1);
		c = new tree(-635,610,2);
		c = new tree(-665,580,1);
		c = new tree(-700,580,0);
		c = new tree(-715,615,0);
		
		// The left trees.
		c = new tree(-730,590,0);
		c = new tree(-700,560,1);
		c = new tree(-660,555,2);
		c = new tree(-685,530,0);
		c = new tree(-705,499,2);
		c = new tree(-735,476,1);
		c = new tree(-755,454,2);
		c = new tree(-785,415,0);
		c = new tree(-745,390,1);
		c = new tree(-710,360,0);
		c = new tree(-685,335,2);
		c = new tree(-655,300,1);
		c = new tree(-774,234,1);
		c = new tree(-670,275,2);
		c = new tree(-680,260,1);
		c = new tree(-690,235,0);
		c = new tree(-730,200,1);
		c = new bush(-741,240,2);
		c = new bush(-642,393,1);
		
		// Misc left trees beyond the line of trees that block you
		c = new tree(-850,590,0);
		c = new tree(-775,560,2);
		c = new tree(-1025,555,1);
		c = new tree(-855,530,2);
		c = new tree(-1200,499,1);
		c = new tree(-1100,476,1);
		c = new tree(-929,454,0);
		c = new tree(-828,415,0);
		c = new tree(-979,390,2);
		c = new tree(-1202,360,0);
		c = new tree(-827,335,2);
		c = new tree(-1209,300,1);
		c = new tree(-919,275,0);
		c = new tree(-929,260,2);
		c = new tree(-890,235,0);
		c = new tree(-950,200,2);
		c = new bush(-921,510,1);
		c = new bush(-771,636,2);
		c = new bush(-795,201,0);
		c = new rock(-891,312,1);
		c = new tree(-804,675,1);
		
		// Trees below bottom tree wall.
		c = new tree(-699,672,2);
		c = new tree(-660,770,1);
		c = new tree(-635,890,2);
		c = new tree(-580,840,1);
		c = new tree(-520,780,2);
		c = new tree(-480,827,1);
		c = new tree(-410,820,0);
		c = new tree(-385,890,0);
		c = new tree(-360,760,1);
		c = new tree(-519, 660, 0);
		c = new tree(-310,700,1);
		c = new tree(-280,792,0);
		c = new tree(-265,700,2);
		c = new tree(-200,679,0);
		c = new tree(-160,740,1);
		c = new rock(-612,669,1);
		c = new bush(-429,636,1);
	}
	
	// Create zone events.
	public void loadZoneEvents() {
		
		// Well and attack tooltips.
		wellTooltipLoaded = new event("sheepFarmWellTooltipLoaded");
		attackTooltipLoaded = new event("sheepFarmWttackTooltipLoaded");
		
		// Load well tooltip event.
		gameSavedForIdiots = new event("sheepFarmGameSavedForIdiots");
		
		// Tell them u can save at water
		uCanSaveAtWater = new event("sheepFarmuCanSaveAtWater");
		
		// Storm stuff
		stormInProgress = new event("sheepFarmStormInProgress");
	}
	
	// Deal with the first well we encounters.
	public void dealWithRegionStuff() {
		player currPlayer = player.getCurrentPlayer();
		if(currPlayer != null && currPlayer.isWithin(998,-1483,1290,-1034) && wellTooltipLoaded != null && !wellTooltipLoaded.isCompleted()) {
			wellTooltipLoaded.setCompleted(true);
			tooltipString t = new tooltipString("Use any water source to save and heal.");
		}
		if(currPlayer != null && currPlayer.isWithin(-261,-736,249,-570) && attackTooltipLoaded != null && !attackTooltipLoaded.isCompleted()) {
			attackTooltipLoaded.setCompleted(true);
			tooltipString t = new tooltipString("Press or hold 'space' to attack.");
		}
		if(currPlayer != null && currPlayer.isWithin(-324,-684,69,-456) && gameSavedForIdiots != null && !gameSavedForIdiots.isCompleted()) {
			gameSavedForIdiots.setCompleted(true);
			saveState.createSaveState();
		}
		if(currPlayer != null && currPlayer.isWithin(2834,-473,2972,-363) && uCanSaveAtWater != null && !uCanSaveAtWater.isCompleted()) {
			uCanSaveAtWater.setCompleted(true);
			tooltipString t = new tooltipString("u no u can save at water right bud");
		}
		
		// Fog at black flower area
		if(stormInProgress != null && !stormInProgress.isCompleted() && currPlayer != null && currPlayer.isWithin(2421,-489,2843,-20)) {
			if(zoneFog == null) zoneFog = new fog();
			zoneFog.fadeTo(stormFogLevel, 2f);
			stormInProgress.setCompleted(true);
			stormStartTime = time.getTime();
			startStormFromFog = true;
		}
	}
	
	// Storm stuff
	long stormStartTime = 0;
	boolean startStormFromFog = false;
	int howManySecondsUntilStorm = 5;
	
	// Start storm from fog
	public void startStormFromFog() {
		if(startStormFromFog) {
			if(time.getTime() - stormStartTime > howManySecondsUntilStorm*1000) {
				startStormFromFog = false;
				stormStarted = true;
				storm s = new storm();
			}
		}
	}
	
	
	// Do zone specific tasks that aren't monitored by
	// zone specific units. 
	@Override
	public void update() {
		startStormFromFog();
		dealWithRegionStuff();
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