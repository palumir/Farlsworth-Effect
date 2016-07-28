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
import drawing.drawnObject;
import drawing.userInterface.tooltipString;
import effects.effectTypes.fire;
import effects.effectTypes.lightningAboutToStrike;
import effects.effectTypes.lightningStrike;
import interactions.event;
import items.bottle;
import items.item;
import items.bottles.normalBottle;
import items.weapons.dagger;
import modes.topDown;
import sounds.music;
import sounds.sound;
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
	private static String zoneMusic = "sounds/music/farmLand/sheepFarm/forest.wav";
	
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
	public static event isOnFire;
	
	// Storm booleans
	public static boolean stormStarted = false;
	
	// Zone fog
	private static fog zoneFog;
	private float stormFogLevel = 0.15f;
	
	// Defaults
	public static intTuple DEFAULT_SPAWN_TUPLE = new intTuple(-1115,391);
	
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
					System.out.println("new flower(" + (i+diffX) + "," + (j+diffY) + "," + t + ");");
					new flower(i+diffX,j+diffY,t);
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
				grass.createChunk(i*grass.DEFAULT_CHUNK_WIDTH + x1, j*grass.DEFAULT_CHUNK_HEIGHT + y1);
			}
		}
	}
	
	// Spawn grass from x to y.
	public void spawnMountainRect(int x1, int y1, int x2, int y2) {
		int numX = (x2 - x1)/cave.DEFAULT_CHUNK_WIDTH;
		int numY = (y2 - y1)/cave.DEFAULT_CHUNK_HEIGHT;
		for(int i = 0; i < numX; i++) {
			for(int j = 0; j < numY; j++) {
				c = cave.createChunk(i*cave.DEFAULT_CHUNK_WIDTH + x1, j*cave.DEFAULT_CHUNK_HEIGHT + y1);
				if(c!=null) c.setPassable(false);
			}
		}
	}
	
	// Spawn water from x to y.
	public void spawnWaterRect(int x1, int y1, int x2, int y2) {
		int numX = (x2 - x1)/water.DEFAULT_CHUNK_WIDTH;
		int numY = (y2 - y1)/water.DEFAULT_CHUNK_HEIGHT;
		for(int i = 0; i < numX; i++) {
			for(int j = 0; j < numY; j++) {
				water.createChunk(i*water.DEFAULT_CHUNK_WIDTH + x1, j*water.DEFAULT_CHUNK_HEIGHT + y1);
			}
		}
	}
	
	// Spawn water from x to y.
	public void spawnPassableWaterRect(int x1, int y1, int x2, int y2) {
		int numX = (x2 - x1)/water.DEFAULT_CHUNK_WIDTH;
		int numY = (y2 - y1)/water.DEFAULT_CHUNK_HEIGHT;
		for(int i = 0; i < numX; i++) {
			for(int j = 0; j < numY; j++) {
				c = water.createChunk(i*water.DEFAULT_CHUNK_WIDTH + x1, j*water.DEFAULT_CHUNK_HEIGHT + y1);
				if(c!=null) c.setPassable(true);
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
				new flower(i*flower.DEFAULT_SPRITE_WIDTH + x1, j*flower.DEFAULT_SPRITE_WIDTH + y1, r);
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
		
		// Create terrain
		createTerrain();
		
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
		
		// Play fire sound
		if(isOnFire != null && isOnFire.isCompleted()) {
			sound s = new sound(fire.forestFire);
			s.start();
		}
		
		// Spawn units
		spawnUnits();
		
		// Sort chunks.
		chunk.sortChunks();
		
		// Play zone music.
		 music.startMusic(zoneMusic); 
		
	}
	
	//////////////////////
	// INDIVIDUAL AREAS //
	//////////////////////
	
	// Create terrain
	public void createTerrain() {
		// Draw the grass around spawn.
		spawnGrassRect(-1000-1000,-1000,2000+1000,64);
		
		// Spawn some grass on the other side of the bridge.
		spawnGrassRect(-2000-550,184,2000-550,1000);
		
		// Spawn forest grass
		spawnGrassRect(-1500+5,-4000-1000,2000+1000,-440);
		
		// Draw the bridge.
		spawnPassableWaterRect(-208-580,56,-101-580,200);
		
		// Draw the water to left of bridge spawn.
		spawnWaterRect(-2100-3000-10-580,56,-167-580,200);	
		
		// Draw the water to right of bridge spawn.
		spawnWaterRect(-168+20+15-5-6+5-580,56,2032+1000-580,200);
		
		// Spawn river to right of mountain
		spawnWaterRect(2000+1000-16,-4466+18-1000,2128+1000,200);
	}
	
	// Spawn creeps
	public void spawnUnits() {
		
		// Denmother
		denmother den = new denmother(-3841,-856);
		
		// Farlsworth
		farlsworth sheepBoss = new farlsworth(411,-394);
		if(!farlsworth.isFenceAttached.isCompleted()) {
			farlsworthFence = makeFarlsworthFence(5,-406);
		}
		else {
			farlsworthFence = null;
		}
		
		// First wolf pack
		u = new redWolf(-8,-846);
		u.setFacingDirection("Down");
		
		// Second wolf pack
		u = new blackWolf(430,-1262);
		u.setFacingDirection("Left");
		
		// Third wolf pack
		u = new yellowWolf(1045,-1262);
		u.setFacingDirection("Left");
		
		// Fourth wolf pack
		u = new redWolf(1400,-700);
		u.setFacingDirection("Up");
		
		// Well.
		c = new well(1400,-300,0);
		
		// Fifth wolf pack
		u = new blackWolf(2335,-210);
		u.setFacingDirection("Left");
		u = new yellowWolf(2385,-110);
		u.setFacingDirection("Left");
		
		// Sixth wolf pack
		u = new blackWolf(2785,-210);
		u.setFacingDirection("Left");
		u = new redWolf(2760,-110);
		u.setFacingDirection("Left");
		
		// Seventh wolf pack
		u = new yellowWolf(2582+50,-1317);
		u.setFacingDirection("Down");
		u = new redWolf(2812-50,-1317);
		u.setFacingDirection("Down");
		
		// Eighth wolf pack
		u = new redWolf(1405,-2786);
		u.setFacingDirection("Right");
		u = new yellowWolf(1405,-2921);
		u.setFacingDirection("Right");
		u = new blackWolf(1405,-3056);
		u.setFacingDirection("Right");
		
		// Ninth wolf pack
		u = new yellowWolf(452,-3013);
		u.setFacingDirection("Right");
		u = new blackWolf(422,-3910);
		u.setFacingDirection("Right");
		u = new redWolf(285,-3470);
		u.setFacingDirection("Down");

	}
	
	// Flower farm
	public void createFlowerFarm()  {
	
		// Bottle before forest farm
		item b = new normalBottle(1293,-1300);
		
		// Forest farm stuff
		new farmHouse(1892,-626-100,0);
		new bush(1811,-664,1);
		new bush(2101,-668,0);
		new bush(2085,-702,1);
		tree.createTree(1848,-713,0);
		tree.createTree(1806,-695,2);
		tree.createTree(1770,-677,0);
		tree.createTree(1721,-652,1);
		tree.createTree(1743,-615,2);
		tree.createTree(1736,-568,1);
		tree.createTree(1711,-552,1);
		tree.createTree(2071,-737,1);
		tree.createTree(2123,-721,2);
		tree.createTree(2156,-676,1);
		tree.createTree(2202,-651,1);
		tree.createTree(2202,-682,2);
		tree.createTree(2249,-652,0);
		tree.createTree(2269,-635,1);
		tree.createTree(2287,-602,0);
		tree.createTree(2309,-565,0);
		tree.createTree(2348,-542,0);
		tree.createTree(2368,-509,1);
		tree.createTree(2413,-532,2);
		tree.createTree(2424,-507,2);
		tree.createTree(1904,-776,0);
		tree.createTree(1995,-759,0);
		new flower(2094,-631,0);
		tree.createTree(1791,-540,2);
		tree.createTree(1765,-509,2);
		new flower(1712,-534,2);
		new flower(2224,-626,0);
		new flower(2258,-564,2);
		new bush(1660,-510,1);
		tree.createTree(1741,-539,1);
		new bush(1731,-528,1);
		new firePit(2150,-565);
		
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
		tree.createTree(2849,-295,2);
		tree.createTree(2880,-267,0);
		tree.createTree(2907,-279,1);
		tree.createTree(2934,-261,0);

	}
	
	// Create area above flower farm.
	public void createAreaAboveFlowerFarm()  {
		
		// Well
		new well(2685,-1877-125,0);
		
		// Trees
		tree.createTree(2440,-1663,1);
		tree.createTree(2416,-1686,1);
		tree.createTree(2391,-1714,0);
		tree.createTree(2365,-1732,2);
		tree.createTree(2333,-1709,1);
		tree.createTree(2303,-1733,0);
		tree.createTree(2274,-1720,1);
		tree.createTree(2235,-1735,2);
		tree.createTree(2192,-1729,1);
		tree.createTree(2170,-1754,0);
		tree.createTree(2133,-1740,0);
		tree.createTree(2105,-1758,2);
		tree.createTree(2078,-1738,2);
		tree.createTree(2039,-1728,2);
		tree.createTree(2027,-1755,2);
		tree.createTree(2001,-1741,0);
		tree.createTree(1955,-1745,2);
		tree.createTree(1935,-1731,1);
		tree.createTree(1905,-1741,1);
		tree.createTree(1866,-1723,1);
		tree.createTree(1848,-1745,2);
		tree.createTree(1809,-1726,2);
		tree.createTree(1788,-1756,2);
		tree.createTree(1770,-1741,0);
		tree.createTree(1742,-1725,1);
		tree.createTree(1715,-1740,1);
		tree.createTree(1661,-1739,0);
		tree.createTree(1688,-1739,1);
		tree.createTree(1656,-1720,2);
		tree.createTree(1627,-1731,1);
		tree.createTree(1610,-1714,0);
		tree.createTree(1583,-1724,2);
		tree.createTree(1556,-1709,1);
		tree.createTree(1532,-1724,2);
		tree.createTree(1502,-1724,1);
		tree.createTree(1457,-1724,2);
		tree.createTree(1427,-1712,2);
		tree.createTree(1394,-1723,0);
		tree.createTree(1368,-1703,1);
		tree.createTree(1344,-1730,1);
		tree.createTree(1338,-1707,2);
		tree.createTree(1302,-1728,0);
		tree.createTree(1292,-1708,0);
		tree.createTree(1240,-1730,2);
		tree.createTree(1247,-1708,0);
		tree.createTree(1202,-1706,2);
		tree.createTree(1207,-1732,1);
		tree.createTree(1184,-1715,1);
		tree.createTree(1136,-1715,1);
		tree.createTree(1152,-1734,1);
		tree.createTree(1097,-1715,2);
		tree.createTree(1112,-1736,1);
		tree.createTree(1068,-1719,0);
		tree.createTree(1033,-1728,1);
		tree.createTree(992,-1711,2);
		tree.createTree(990,-1739,0);
		tree.createTree(963,-1715,2);
		tree.createTree(941,-1737,0);
		tree.createTree(910,-1715,2);
		tree.createTree(885,-1735,0);
		tree.createTree(868,-1712,0);
		tree.createTree(841,-1736,0);
		tree.createTree(826,-1714,0);
		tree.createTree(793,-1732,0);
		tree.createTree(777,-1710,1);
		tree.createTree(750,-1732,2);
		tree.createTree(736,-1718,1);
		tree.createTree(706,-1700,2);
		tree.createTree(687,-1716,1);
		tree.createTree(660,-1698,2);
		tree.createTree(639,-1713,1);
		tree.createTree(618,-1703,2);
		tree.createTree(572,-1715,0);
		tree.createTree(564,-1696,0);
		tree.createTree(530,-1715,1);
		tree.createTree(530,-1691,0);
		tree.createTree(487,-1690,2);
		tree.createTree(484,-1711,1);
		tree.createTree(451,-1711,1);
		tree.createTree(442,-1687,0);
		tree.createTree(409,-1687,1);
		tree.createTree(392,-1670,2);
		tree.createTree(357,-1675,0);
		tree.createTree(342,-1654,2);
		tree.createTree(315,-1675,2);
		tree.createTree(294,-1650,1);
		tree.createTree(279,-1627,0);
		tree.createTree(249,-1627,2);
		tree.createTree(210,-1609,1);
		tree.createTree(192,-1591,2);
		tree.createTree(167,-1574,2);
		tree.createTree(128,-1574,1);
		tree.createTree(97,-1549,0);
		tree.createTree(62,-1536,0);
		tree.createTree(35,-1508,0);
		tree.createTree(0,-1487,2);
		tree.createTree(67,-1512,2);
		tree.createTree(-20,-1475,0);
		tree.createTree(-47,-1454,1);
		tree.createTree(-74,-1454,0);
		tree.createTree(-101,-1440,1);
		tree.createTree(-136,-1428,2);
		tree.createTree(-162,-1403,1);
		tree.createTree(-183,-1367,2);
		tree.createTree(-186,-1385,2);
		tree.createTree(-212,-1337,2);
		tree.createTree(-222,-1353,0);
		tree.createTree(-244,-1325,0);
		tree.createTree(-283,-1313,0);
		tree.createTree(-283,-1283,0);
		tree.createTree(-307,-1283,2);
		tree.createTree(-321,-1242,1);
		tree.createTree(-310,-1262,2);
		tree.createTree(-248,-1300,1);
		tree.createTree(-342,-1217,2);
		tree.createTree(-364,-1190,2);
		tree.createTree(-404,-1170,0);
		tree.createTree(-381,-1144,1);
		tree.createTree(-402,-1119,2);
		tree.createTree(-366,-1155,1);
		tree.createTree(-435,-1110,2);
		tree.createTree(-421,-1075,2);
		tree.createTree(-439,-1085,0);
		tree.createTree(-452,-1053,0);
		tree.createTree(-452,-1001,1);
		tree.createTree(-457,-988,1);
		tree.createTree(-457,-961,1);
		tree.createTree(-470,-940,0);
		tree.createTree(-469,-913,0);
		tree.createTree(-483,-887,2);
		tree.createTree(-471,-863,0);
		tree.createTree(-483,-836,1);
		tree.createTree(-483,-806,1);
		tree.createTree(-483,-785,0);
		tree.createTree(-489,-773,1);
		tree.createTree(-489,-746,0);
		tree.createTree(-493,-724,2);
		tree.createTree(-493,-694,1);
		tree.createTree(-493,-667,2);
		tree.createTree(-496,-633,2);
		tree.createTree(-496,-603,2);
		tree.createTree(-496,-652,0);
		tree.createTree(-495,-571,1);
		tree.createTree(-522,-586,0);
		tree.createTree(-498,-532,0);
		tree.createTree(-510,-550,2);
		tree.createTree(-512,-526,1);
		tree.createTree(-496,-504,1);
		tree.createTree(-519,-475,0);
		tree.createTree(-524,-454,1);
		tree.createTree(-1086,-452,1);
		tree.createTree(-1086,-479,2);
		tree.createTree(-1062,-503,2);
		tree.createTree(-1084,-522,2);
		tree.createTree(-1069,-553,2);
		tree.createTree(-1095,-579,0);
		tree.createTree(-1069,-605,2);
		tree.createTree(-1088,-625,0);
		tree.createTree(-1102,-647,0);
		tree.createTree(-1077,-681,1);
		tree.createTree(-1094,-671,2);
		tree.createTree(-1094,-704,1);
		tree.createTree(-1082,-722,0);
		tree.createTree(-1103,-743,1);
		tree.createTree(-1079,-771,1);
		tree.createTree(-1105,-784,2);
		tree.createTree(-1081,-814,0);
		tree.createTree(-1105,-835,0);
		tree.createTree(-1082,-867,0);
		tree.createTree(-1105,-893,1);
		tree.createTree(-1088,-916,0);
		tree.createTree(-1117,-945,2);
		tree.createTree(-1097,-966,0);
		tree.createTree(-1077,-1003,2);
		tree.createTree(-1097,-1025,0);
		tree.createTree(-1070,-1045,0);
		tree.createTree(-1093,-1078,2);
		tree.createTree(-1064,-1107,2);
		tree.createTree(-1092,-1123,2);
		tree.createTree(-1071,-1153,1);
		tree.createTree(-1031,-600,1);
		tree.createTree(-991,-586,2);
		tree.createTree(-971,-594,2);
		tree.createTree(-928,-595,0);
		tree.createTree(-905,-581,2);
		tree.createTree(-876,-604,1);
		tree.createTree(-838,-581,2);
		tree.createTree(-805,-591,0);
		tree.createTree(-772,-591,1);
		tree.createTree(-746,-571,2);
		tree.createTree(-714,-592,0);
		tree.createTree(-690,-568,0);
		tree.createTree(-654,-589,1);
		tree.createTree(-633,-562,1);
		tree.createTree(-604,-583,0);
		tree.createTree(-582,-561,1);
		tree.createTree(-556,-584,1);
		tree.createTree(-538,-610,1);
		tree.createTree(-443,-618,0);
		tree.createTree(-1065,-1184,0);
		tree.createTree(-1056,-1202,2);
		tree.createTree(-1042,-1173,0);
		tree.createTree(-1025,-1228,0);
		tree.createTree(-1037,-1252,0);
		tree.createTree(-1010,-1270,0);
		tree.createTree(-985,-1298,0);
		tree.createTree(-992,-1321,0);
		tree.createTree(-949,-1346,2);
		tree.createTree(-942,-1373,2);
		tree.createTree(-919,-1390,0);
		tree.createTree(-890,-1413,0);
		tree.createTree(-861,-1442,0);
		tree.createTree(-848,-1463,2);
		tree.createTree(-823,-1435,2);
		tree.createTree(-782,-1460,2);
		tree.createTree(-767,-1486,1);
		tree.createTree(-726,-1493,0);
		tree.createTree(-703,-1522,0);
		tree.createTree(-648,-1520,2);
		tree.createTree(-687,-1541,1);
		tree.createTree(-655,-1556,0);
		tree.createTree(-634,-1582,0);
		tree.createTree(-610,-1609,2);
		tree.createTree(-580,-1594,1);
		tree.createTree(-557,-1612,2);
		tree.createTree(-531,-1652,2);
		tree.createTree(-523,-1635,0);
		tree.createTree(-494,-1658,2);
		tree.createTree(-472,-1686,2);
		tree.createTree(-470,-1713,2);
		tree.createTree(-437,-1732,2);
		tree.createTree(-395,-1748,1);
		tree.createTree(-362,-1772,2);
		tree.createTree(-335,-1805,0);
		tree.createTree(-332,-1787,0);
		tree.createTree(-284,-1813,0);
		tree.createTree(-257,-1834,1);
		tree.createTree(-214,-1830,0);
		tree.createTree(-192,-1859,0);
		tree.createTree(-176,-1885,1);
		tree.createTree(-150,-1901,0);
		tree.createTree(-119,-1927,2);
		tree.createTree(-99,-1956,0);
		tree.createTree(-77,-1979,0);
		tree.createTree(-44,-1997,1);
		tree.createTree(-25,-2022,1);
		tree.createTree(12,-2040,1);
		tree.createTree(62,-2058,0);
		tree.createTree(86,-2069,0);
		tree.createTree(119,-2093,2);
		tree.createTree(161,-2112,1);
		tree.createTree(183,-2096,2);
		tree.createTree(230,-2102,2);
		tree.createTree(256,-2122,1);
		tree.createTree(289,-2131,1);
		tree.createTree(318,-2160,2);
		tree.createTree(340,-2135,1);
		tree.createTree(372,-2155,1);
		tree.createTree(404,-2169,1);
		tree.createTree(431,-2190,1);
		tree.createTree(462,-2165,0);
		tree.createTree(495,-2185,1);
		tree.createTree(522,-2165,0);
		tree.createTree(555,-2185,0);
		tree.createTree(575,-2162,1);
		tree.createTree(612,-2180,0);
		tree.createTree(633,-2204,2);
		tree.createTree(655,-2186,0);
		tree.createTree(694,-2207,2);
		tree.createTree(713,-2188,0);
		tree.createTree(754,-2201,2);
		tree.createTree(776,-2183,2);
		tree.createTree(812,-2192,1);
		tree.createTree(841,-2169,1);
		tree.createTree(870,-2196,0);
		tree.createTree(906,-2166,2);
		tree.createTree(933,-2184,1);
		tree.createTree(992,-2195,2);
		tree.createTree(961,-2172,1);
		tree.createTree(1040,-2188,0);
		tree.createTree(1059,-2159,0);
		tree.createTree(1080,-2192,1);
		tree.createTree(1011,-2174,2);
		tree.createTree(1113,-2177,1);
		tree.createTree(1143,-2156,2);
		tree.createTree(1171,-2174,0);
		tree.createTree(1204,-2174,0);
		tree.createTree(1230,-2148,2);
		tree.createTree(1255,-2166,2);
		tree.createTree(1281,-2140,2);
		tree.createTree(1287,-2177,0);
		tree.createTree(1324,-2193,2);
		tree.createTree(1353,-2167,1);
		tree.createTree(1383,-2191,0);
		tree.createTree(1409,-2162,2);
		tree.createTree(1439,-2189,2);
		tree.createTree(1464,-2164,0);
		tree.createTree(1494,-2189,1);
		tree.createTree(1510,-2170,1);
		tree.createTree(1537,-2187,2);
		tree.createTree(1562,-2156,1);
		tree.createTree(1593,-2177,1);
		tree.createTree(1616,-2154,2);
		tree.createTree(1663,-2175,0);
		tree.createTree(1689,-2156,1);
		tree.createTree(1722,-2156,1);
		tree.createTree(1762,-2166,1);
		tree.createTree(1781,-2186,2);
		tree.createTree(1803,-2174,2);
		tree.createTree(1827,-2192,1);
		tree.createTree(1850,-2182,1);
		tree.createTree(1883,-2200,2);
		tree.createTree(1934,-2179,1);
		tree.createTree(1904,-2164,2);
		tree.createTree(1968,-2192,2);
		tree.createTree(1991,-2206,2);
		tree.createTree(2022,-2176,0);
		tree.createTree(2050,-2192,0);
		tree.createTree(2077,-2164,2);
		tree.createTree(2101,-2185,2);
		tree.createTree(2120,-2161,0);
		tree.createTree(2148,-2189,2);
		tree.createTree(2168,-2206,1);
		tree.createTree(2201,-2206,1);
		tree.createTree(2234,-2185,0);
		tree.createTree(2257,-2209,1);
		tree.createTree(2278,-2192,2);
		tree.createTree(2317,-2222,0);
		tree.createTree(2344,-2195,1);
		tree.createTree(2378,-2217,1);
		tree.createTree(2402,-2198,1);
		tree.createTree(2433,-2208,0);
		tree.createTree(2459,-2182,1);
		tree.createTree(2488,-2214,2);
		tree.createTree(2460,-2240,0);
		tree.createTree(2475,-2258,2);
		tree.createTree(2499,-2234,0);
		tree.createTree(2474,-2301,1);
		tree.createTree(2501,-2280,0);
		tree.createTree(2473,-2332,0);
		tree.createTree(2498,-2315,0);
		tree.createTree(2947,-1656,2);
		tree.createTree(2947,-1701,1);
		tree.createTree(2921,-1733,2);
		tree.createTree(2965,-1757,0);
		tree.createTree(2942,-1795,1);
		tree.createTree(2942,-1768,2);
		tree.createTree(2946,-1814,1);
		tree.createTree(2931,-1841,2);
		tree.createTree(2956,-1868,1);
		tree.createTree(2939,-1893,0);
		tree.createTree(2956,-1919,1);
		tree.createTree(2939,-1950,1);
		tree.createTree(2955,-1976,0);
		tree.createTree(2926,-2013,0);
		tree.createTree(2951,-1996,0);
		tree.createTree(2946,-2027,1);
		tree.createTree(2931,-2051,2);
		tree.createTree(2949,-2075,0);
		tree.createTree(2931,-2099,1);
		tree.createTree(2961,-2114,2);
		tree.createTree(2937,-2143,2);
		tree.createTree(2953,-2171,2);
		tree.createTree(2932,-2198,0);
		tree.createTree(2945,-2213,2);
		tree.createTree(2945,-2243,1);
		tree.createTree(2936,-2228,0);
		tree.createTree(2942,-2271,0);
		tree.createTree(2940,-2288,1);
		tree.createTree(2940,-2315,2);
		tree.createTree(2959,-2329,0);
		tree.createTree(2930,-2348,2);
		tree.createTree(2951,-2372,2);
		tree.createTree(2489,-2349,0);
		tree.createTree(-1079,-983,0);
		tree.createTree(2959,-605,2);
		tree.createTree(2930,-623,1);
		tree.createTree(2942,-661,2);
		tree.createTree(2933,-688,0);
		tree.createTree(2951,-728,1);
		tree.createTree(2924,-740,0);
		tree.createTree(2942,-782,2);
		tree.createTree(2931,-818,1);
		tree.createTree(2926,-852,2);
		tree.createTree(2921,-878,0);
		tree.createTree(2942,-902,2);
		tree.createTree(2952,-945,2);
		tree.createTree(2925,-932,1);
		tree.createTree(2942,-969,0);
		tree.createTree(2924,-999,0);
		tree.createTree(2945,-1025,0);
		tree.createTree(2932,-1047,2);
		tree.createTree(2945,-1076,2);
		tree.createTree(2918,-1076,1);
		tree.createTree(2931,-1103,1);
		tree.createTree(2943,-1134,0);
		tree.createTree(2918,-1156,2);
		tree.createTree(2932,-1179,1);
		tree.createTree(2928,-1212,2);
		tree.createTree(2947,-1237,2);
		tree.createTree(2924,-1265,0);
		tree.createTree(2928,-1296,2);
		tree.createTree(2951,-1328,0);
		tree.createTree(2938,-1350,0);
		tree.createTree(2924,-1392,0);
		tree.createTree(2948,-1371,1);
		tree.createTree(2934,-1429,0);
		tree.createTree(2934,-1402,0);
		tree.createTree(2925,-1468,1);
		tree.createTree(2943,-1490,2);
		tree.createTree(2943,-1454,2);
		tree.createTree(2943,-1508,0);
		tree.createTree(2924,-1536,1);
		tree.createTree(2945,-1565,1);
		tree.createTree(2913,-1576,0);
		tree.createTree(2937,-1599,0);
		tree.createTree(2917,-1622,0);
		tree.createTree(2935,-1640,2);
		tree.createTree(2396,-575,0);
		tree.createTree(2414,-599,1);
		tree.createTree(2382,-629,1);
		tree.createTree(2407,-661,2);
		tree.createTree(2434,-679,0);
		tree.createTree(2409,-699,2);
		tree.createTree(2434,-718,0);
		tree.createTree(2409,-736,1);
		tree.createTree(2431,-770,0);
		tree.createTree(2440,-791,2);
		tree.createTree(2421,-819,2);
		tree.createTree(2437,-841,2);
		tree.createTree(2410,-865,2);
		tree.createTree(2430,-894,1);
		tree.createTree(2411,-919,1);
		tree.createTree(2440,-944,2);
		tree.createTree(2414,-967,1);
		tree.createTree(2430,-989,2);
		tree.createTree(2409,-1014,0);
		tree.createTree(2427,-1035,0);
		tree.createTree(2434,-1060,2);
		tree.createTree(2428,-1090,0);
		tree.createTree(2409,-1106,2);
		tree.createTree(2427,-1127,0);
		tree.createTree(2409,-1167,1);
		tree.createTree(2435,-1184,2);
		tree.createTree(2435,-1160,0);
		tree.createTree(2423,-1199,0);
		tree.createTree(2417,-1226,2);
		tree.createTree(2438,-1244,0);
		tree.createTree(2417,-1264,0);
		tree.createTree(2439,-1289,2);
		tree.createTree(2417,-1311,2);
		tree.createTree(2441,-1333,0);
		tree.createTree(2422,-1349,2);
		tree.createTree(2449,-1379,1);
		tree.createTree(2413,-1364,1);
		tree.createTree(2431,-1403,2);
		tree.createTree(2427,-1417,1);
		tree.createTree(2440,-1449,0);
		tree.createTree(2419,-1434,0);
		tree.createTree(2436,-1481,1);
		tree.createTree(2415,-1463,0);
		tree.createTree(2415,-1511,1);
		tree.createTree(2430,-1535,2);
		tree.createTree(2411,-1561,0);
		tree.createTree(2431,-1593,1);
		tree.createTree(2431,-1569,0);
		tree.createTree(2437,-1607,0);
		tree.createTree(2422,-1622,2);
		tree.createTree(2453,-1647,1);
		tree.createTree(2419,-1143,0);
		tree.createTree(2412,-752,2);
		tree.createTree(2398,-556,2);
		tree.createTree(2939,-801,1);
		tree.createTree(2950,-757,1);
		tree.createTree(2930,-638,2);
		tree.createTree(2936,-705,2);
		tree.createTree(2924,-834,1);
	}
	
	// Forest area above spawn.
	public void createForestAboveSpawn()  {
		
		///////////////////
		//// FOREST ///////
		///////////////////
		
		// Trees and stuff (Generated by map maker)
		 tree.createTree(-1067,-436,1);
		 tree.createTree(-1088,-419,1);
		 tree.createTree(-1073,-402,2);
		 tree.createTree(-1094,-384,2);
		 tree.createTree(-1067,-369,0);
		 tree.createTree(-1092,-338,1);
		 tree.createTree(-1080,-314,0);
		 tree.createTree(-1096,-292,0);
		 tree.createTree(-1076,-271,0);
		 tree.createTree(-1103,-238,1);
		 tree.createTree(-1106,-257,2);
		 tree.createTree(-1115,-358,1);
		 tree.createTree(-1106,-198,0);
		 tree.createTree(-1141,-222,1);
		 tree.createTree(-1099,-171,0);
		 tree.createTree(-1120,-141,0);
		 tree.createTree(-1147,-164,0);
		 tree.createTree(-1112,-120,2);
		 tree.createTree(-1130,-96,2);
		 tree.createTree(-1097,-72,1);
		 tree.createTree(-1097,-42,0);
		 tree.createTree(-1139,-60,0);
		 tree.createTree(-1139,-9,2);
		 tree.createTree(-1092,-14,0);
		 tree.createTree(-1092,15,2);
		 tree.createTree(-1077,30,1);
		 tree.createTree(471,-439,1);
		 tree.createTree(498,-427,2);
		 tree.createTree(474,-388,2);
		 tree.createTree(504,-397,0);
		 tree.createTree(504,-358,1);
		 tree.createTree(489,-340,1);
		 tree.createTree(483,-316,0);
		 tree.createTree(510,-298,1);
		 tree.createTree(529,-264,2);
		 tree.createTree(490,-237,0);
		 tree.createTree(514,-209,0);
		 tree.createTree(481,-185,1);
		 tree.createTree(510,-157,2);
		 tree.createTree(526,-120,0);
		 tree.createTree(493,-96,2);
		 tree.createTree(521,-82,2);
		 tree.createTree(521,-52,2);
		 tree.createTree(488,-25,0);
		 tree.createTree(518,7,1);
		 tree.createTree(485,28,2);
		 tree.createTree(528,-137,1);
		 tree.createTree(523,-281,2);
		 tree.createTree(-311,-457,1);
		 tree.createTree(-311,-481,1);
		 tree.createTree(-323,-499,1);
		 tree.createTree(-307,-518,0);
		 tree.createTree(-328,-545,1);
		 tree.createTree(-313,-571,1);
		 tree.createTree(-338,-592,1);
		 tree.createTree(-295,-611,1);
		 tree.createTree(-315,-630,2);
		 tree.createTree(-296,-651,0);
		 tree.createTree(-307,-674,0);
		 tree.createTree(-280,-695,0);
		 tree.createTree(-309,-708,1);
		 tree.createTree(-318,-729,2);
		 tree.createTree(-336,-747,0);
		 tree.createTree(-318,-767,1);
		 tree.createTree(-306,-794,1);
		 tree.createTree(-335,-817,0);
		 tree.createTree(-305,-836,1);
		 tree.createTree(-335,-856,1);
		 tree.createTree(-304,-872,1);
		 tree.createTree(282,-457,0);
		 tree.createTree(266,-475,1);
		 tree.createTree(287,-496,0);
		 tree.createTree(268,-518,0);
		 tree.createTree(300,-529,1);
		 tree.createTree(267,-550,2);
		 tree.createTree(285,-568,2);
		 tree.createTree(276,-589,2);
		 tree.createTree(276,-610,1);
		 tree.createTree(269,-629,0);
		 tree.createTree(269,-653,0);
		 tree.createTree(290,-667,2);
		 tree.createTree(272,-688,2);
		 tree.createTree(272,-715,2);
		 tree.createTree(257,-742,2);
		 tree.createTree(279,-772,0);
		 tree.createTree(287,-750,2);
		 tree.createTree(265,-789,2);
		 tree.createTree(284,-813,0);
		 tree.createTree(270,-846,1);
		 tree.createTree(276,-831,2);
		 tree.createTree(282,-864,2);
		 tree.createTree(-308,-888,1);
		 tree.createTree(-292,-922,2);
		 tree.createTree(-277,-955,0);
		 tree.createTree(-281,-978,0);
		 tree.createTree(-265,-1000,2);
		 tree.createTree(-241,-1019,2);
		 tree.createTree(-250,-1034,1);
		 tree.createTree(-240,-1053,0);
		 tree.createTree(-215,-1075,2);
		 tree.createTree(-214,-1095,2);
		 tree.createTree(-187,-1122,1);
		 tree.createTree(-187,-1104,2);
		 tree.createTree(-183,-1141,2);
		 tree.createTree(-162,-1159,1);
		 tree.createTree(-149,-1180,2);
		 tree.createTree(-118,-1197,1);
		 tree.createTree(-103,-1224,1);
		 tree.createTree(-96,-1208,1);
		 tree.createTree(-94,-1240,2);
		 tree.createTree(-76,-1264,1);
		 tree.createTree(-50,-1290,1);
		 tree.createTree(-17,-1311,1);
		 tree.createTree(282,-880,1);
		 tree.createTree(316,-923,1);
		 tree.createTree(335,-947,2);
		 tree.createTree(355,-971,2);
		 tree.createTree(371,-982,0);
		 tree.createTree(391,-1002,2);
		 tree.createTree(420,-1003,0);
		 tree.createTree(439,-1027,1);
		 tree.createTree(460,-1015,1);
		 tree.createTree(481,-1039,0);
		 tree.createTree(513,-1059,0);
		 tree.createTree(546,-1035,1);
		 tree.createTree(540,-1056,0);
		 tree.createTree(19,-1324,2);
		 tree.createTree(41,-1346,1);
		 tree.createTree(74,-1361,0);
		 tree.createTree(106,-1367,1);
		 tree.createTree(133,-1388,1);
		 tree.createTree(164,-1366,0);
		 tree.createTree(174,-1390,1);
		 tree.createTree(201,-1411,2);
		 tree.createTree(238,-1420,0);
		 tree.createTree(265,-1402,2);
		 tree.createTree(297,-1422,0);
		 tree.createTree(326,-1438,0);
		 tree.createTree(358,-1459,1);
		 tree.createTree(403,-1460,1);
		 tree.createTree(379,-1445,1);
		 tree.createTree(590,-1044,1);
		 tree.createTree(602,-1044,0);
		 tree.createTree(632,-1044,1);
		 tree.createTree(654,-1051,0);
		 tree.createTree(683,-1071,2);
		 tree.createTree(700,-1054,2);
		 tree.createTree(739,-1028,0);
		 tree.createTree(778,-1043,1);
		 tree.createTree(697,-1025,0);
		 tree.createTree(438,-1470,2);
		 tree.createTree(478,-1471,2);
		 tree.createTree(512,-1480,0);
		 tree.createTree(532,-1465,0);
		 tree.createTree(556,-1483,0);
		 tree.createTree(583,-1459,0);
		 tree.createTree(627,-1473,1);
		 tree.createTree(612,-1458,2);
		 tree.createTree(657,-1479,2);
		 tree.createTree(678,-1458,0);
		 tree.createTree(714,-1510,2);
		 tree.createTree(699,-1489,0);
		 tree.createTree(741,-1489,0);
		 tree.createTree(756,-1504,2);
		 tree.createTree(797,-1484,1);
		 tree.createTree(795,-1500,2);
		 tree.createTree(849,-1500,0);
		 tree.createTree(834,-1485,0);
		 tree.createTree(894,-1497,2);
		 tree.createTree(876,-1479,2);
		 tree.createTree(918,-1512,0);
		 tree.createTree(939,-1500,0);
		 tree.createTree(987,-1500,1);
		 tree.createTree(1023,-1516,2);
		 tree.createTree(817,-1056,0);
		 tree.createTree(848,-1075,2);
		 tree.createTree(886,-1057,0);
		 tree.createTree(918,-1068,1);
		 tree.createTree(1059,-1510,0);
		 tree.createTree(1096,-1507,2);
		 tree.createTree(1121,-1521,0);
		 tree.createTree(1156,-1506,2);
		 tree.createTree(1195,-1506,2);
		 tree.createTree(1224,-1494,1);
		 tree.createTree(1262,-1477,0);
		 tree.createTree(1296,-1493,1);
		 tree.createTree(1319,-1468,2);
		 tree.createTree(1366,-1450,0);
		 tree.createTree(1352,-1468,2);
		 tree.createTree(1391,-1426,1);
		 tree.createTree(1414,-1394,0);
		 tree.createTree(1441,-1412,2);
		 tree.createTree(1422,-1377,1);
		 tree.createTree(1420,-1429,2);
		 tree.createTree(1465,-1371,0);
		 tree.createTree(1447,-1347,0);
		 tree.createTree(1476,-1333,2);
		 tree.createTree(961,-1084,0);
		 tree.createTree(961,-1063,0);
		 tree.createTree(1022,-1077,0);
		 tree.createTree(1004,-1065,1);
		 tree.createTree(1043,-1063,1);
		 tree.createTree(1065,-1040,2);
		 tree.createTree(1101,-1050,0);
		 tree.createTree(1127,-1024,1);
		 tree.createTree(1118,-1005,2);
		 tree.createTree(1157,-978,2);
		 tree.createTree(1147,-992,1);
		 tree.createTree(1521,-1321,2);
		 tree.createTree(1524,-1306,1);
		 tree.createTree(1488,-1318,1);
		 tree.createTree(1559,-1309,2);
		 tree.createTree(1581,-1287,1);
		 tree.createTree(1547,-1282,0);
		 tree.createTree(1572,-1240,2);
		 tree.createTree(1565,-1260,2);
		 tree.createTree(1586,-1210,1);
		 tree.createTree(1586,-1188,2);
		 tree.createTree(1604,-1165,0);
		 tree.createTree(1591,-1143,0);
		 tree.createTree(1153,-963,1);
		 tree.createTree(1162,-944,2);
		 tree.createTree(1144,-924,1);
		 tree.createTree(1168,-903,2);
		 tree.createTree(1136,-882,1);
		 tree.createTree(1166,-858,2);
		 tree.createTree(1131,-844,1);
		 tree.createTree(1169,-827,0);
		 tree.createTree(1139,-804,0);
		 tree.createTree(1161,-785,0);
		 tree.createTree(1161,-755,1);
		 tree.createTree(1137,-725,1);
		 tree.createTree(1606,-1124,1);
		 tree.createTree(1606,-1124,2);
		 tree.createTree(1617,-1101,1);
		 tree.createTree(1602,-1069,1);
		 tree.createTree(1627,-1088,1);
		 tree.createTree(1616,-1038,0);
		 tree.createTree(1620,-1057,0);
		 tree.createTree(1606,-1008,1);
		 tree.createTree(1621,-1017,2);
		 tree.createTree(1618,-987,1);
		 tree.createTree(1606,-957,0);
		 tree.createTree(1631,-925,2);
		 tree.createTree(1641,-971,2);
		 tree.createTree(1629,-947,0);
		 tree.createTree(1598,-896,2);
		 tree.createTree(1634,-890,0);
		 tree.createTree(1120,-703,1);
		 tree.createTree(1103,-678,0);
		 tree.createTree(1132,-683,1);
		 tree.createTree(1131,-656,0);
		 tree.createTree(1114,-630,2);
		 tree.createTree(1113,-601,0);
		 tree.createTree(1132,-615,1);
		 tree.createTree(1615,-859,0);
		 tree.createTree(1636,-841,2);
		 tree.createTree(1621,-822,1);
		 tree.createTree(1647,-862,0);
		 tree.createTree(1620,-787,2);
		 tree.createTree(1659,-783,1);
		 tree.createTree(1650,-798,2);
		 tree.createTree(1629,-743,1);
		 tree.createTree(1642,-762,0);
		 tree.createTree(1653,-730,2);
		 tree.createTree(1636,-707,2);
		 tree.createTree(1658,-688,1);
		 tree.createTree(1631,-661,1);
		 tree.createTree(1122,-572,2);
		 tree.createTree(1123,-484,1);
		 tree.createTree(1123,-511,1);
		 tree.createTree(1108,-520,2);
		 tree.createTree(1117,-536,0);
		 tree.createTree(1131,-556,0);
		 tree.createTree(1650,-637,0);
		 tree.createTree(1650,-604,0);
		 tree.createTree(1626,-625,2);
		 tree.createTree(1644,-588,2);
		 tree.createTree(1623,-561,2);
		 tree.createTree(1656,-540,2);
		 tree.createTree(1630,-514,2);
		 tree.createTree(1123,-466,1);
		 tree.createTree(1123,-430,1);
		 tree.createTree(1126,-443,2);
		 tree.createTree(1093,-416,0);
		 tree.createTree(1113,-398,2);
		 tree.createTree(1112,-369,0);
		 tree.createTree(1096,-335,2);
		 tree.createTree(1114,-387,2);
		 tree.createTree(1108,-347,0);
		 tree.createTree(1120,-315,2);
		 tree.createTree(1104,-295,2);
		 tree.createTree(1116,-271,2);
		 tree.createTree(1109,-237,2);
		 tree.createTree(1114,-252,1);
		 tree.createTree(1114,-209,1);
		 tree.createTree(1105,-218,0);
		 tree.createTree(1639,-525,0);
		 tree.createTree(1105,-189,1);
		 tree.createTree(1121,-170,2);
		 tree.createTree(1153,-150,1);
		 tree.createTree(1144,-163,1);
		 tree.createTree(1161,-129,0);
		 tree.createTree(1183,-139,0);
		 tree.createTree(1203,-116,1);
		 tree.createTree(1211,-89,2);
		 tree.createTree(1229,-98,0);
		 tree.createTree(1235,-69,2);
		 tree.createTree(1263,-60,0);
		 tree.createTree(1282,-39,1);
		 tree.createTree(1298,-58,2);
		 tree.createTree(1304,-40,0);
		 tree.createTree(1315,-32,1);
		 tree.createTree(1309,-14,1);
		 tree.createTree(1289,5,1);
		 tree.createTree(1307,23,1);
		 tree.createTree(1321,37,0);
		 tree.createTree(1600,-61,2);
		 tree.createTree(1576,-52,1);
		 tree.createTree(1543,-60,2);
		 tree.createTree(1517,-46,0);
		 tree.createTree(1483,-59,2);
		 tree.createTree(1440,-40,0);
		 tree.createTree(1404,-54,2);
		 tree.createTree(1353,-39,0);
		 tree.createTree(1368,-54,0);
		 tree.createTree(308,-896,2);
	}
	
	public void spawnMetaGraves() {
		int numRows = 9;
		int numCols = 9;
		for(int i = 0; i <= numRows; i++) {
			for(int j = 2; j <= numCols; j++) {
				System.out.println("new grave(" + (1196+ i*((2120-1196)/numCols)) + "," + (-3237 + j*((-2628+3237)/numRows)) + "," + 4 + ");");
			}
		}
	}
	
	public void createGraveYard()  {
		
		// Tomb
		new tomb(2305+2,-3944-85,0, farmTombEasy.getZone(),57,-6,"Right");
		
		// Fence around tomb
		spawnFence(null, 2216,-3990,2216,-3820); // Left fence
		spawnFence(null, 2216+5,-3890-8,2330,-3890-8); // Left bottom
		spawnFence(null, 2216+220,-3990,2216+220,-3820); // Right fence
		spawnFence(null, 2216+5+166,-3890-8,2330+166,-3890-8); // Right bottom
		new statue(2253,-3907-37,0);
		new statue(2397,-3907-37,1);
		c = new flower(2249,-3902-20,10);
		c.setInteractable(false);
		c = new flower(2396,-3902-20,7);
		c.setInteractable(false);
		
		// First grave area.
		new grave(2225,-3116,1);
		new grave(2225,-2995,2);
		new grave(2225,-2874,2);
		new grave(2225,-2753,2);
		new grave(2225,-2632,2);
		new grave(2382,-3116,0);
		new grave(2382,-2995,2);
		new grave(2382,-2874,2);
		new grave(2382,-2753,0);
		new grave(2382,-2632,2);
		new grave(2539,-3116,2);
		new grave(2539,-2995,2);
		new grave(2539,-2874,2);
		new grave(2539,-2753,2);
		new grave(2539,-2632,2);
		new grave(2696,-3116,2);
		new grave(2696,-2995,2);
		new grave(2696,-2874,2);
		new grave(2696,-2753,2);
		new grave(2696,-2632,2);
		new grave(2853,-3116,1);
		new grave(2853,-2995,2);
		new grave(2853,-2874,2);
		new grave(2853,-2753,2);
		new grave(2853,-2632,2);
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
		new grave(1298,-3103,2);
		new grave(1298,-2969,2);
		new grave(1298,-2835,2);
		new grave(1298,-2701,2);
		new grave(1502,-3103,2);
		new grave(1502,-2969,1);
		new grave(1502,-2835,2);
		new grave(1502,-2701,2);
		new grave(1706,-3103,2);
		new grave(1706,-2969,1);
		new grave(1706,-2835,2);
		new grave(1706,-2701,2);
		new grave(1910,-3103,2);
		new grave(1910,-2969,2);
		new grave(1910,-2835,2);
		new grave(1910,-2701,2);
		new grave(2114,-3103,2);
		new grave(2114,-2969,2);
		new grave(2114,-2835,2);
		new grave(2114,-2701,2);
		
		// Far left area.
		tree.createTree(818,-3104,1);
		new grave(628,-2941,0);
		new grave(731,-2850,2);
		new grave(814,-2939,2);
		new grave(950,-2985,1);
		new grave(1046,-2895,2);
		new grave(1092,-2789,2);
		new grave(1061,-2688,2);
		new grave(1033,-2597,2);
		new grave(945,-2634,1);
		new grave(884,-2561,2);
		new grave(732,-2712,0);
		new grave(586,-2581,2);
		new grave(450,-2562,0);
		new grave(342,-2739,2);
		new grave(451,-2838,2);
		new grave(279,-3016,2);
		new grave(454,-3095,2);
		new grave(699,-3143,2);
		new grave(895,-3219,2);
		new grave(1029,-3270,2);
		new grave(1092,-3351,2);
		new grave(882,-3474,1);
		new grave(752,-3413,2);
		new grave(568,-3387,2);
		new grave(314,-3383,2);
		new grave(259,-3262,2);
		new grave(594,-3608,1);
		new grave(462,-3685,2);
		new grave(369,-3776,1);
		new grave(350,-3896,0);
		new grave(581,-3801,2);
		new grave(678,-3862,0);
		new grave(752,-3903,0);
		new grave(816,-3951,0);
		new grave(640,-3929,2);
		tree.createTree(1156,-2532,2);
		tree.createTree(1162,-2511,0);
		tree.createTree(1143,-2497,1);
		tree.createTree(1159,-2466,2);
		tree.createTree(1134,-2449,0);
		tree.createTree(1158,-2428,2);
		tree.createTree(1128,-2410,0);
		tree.createTree(1144,-2381,0);
		tree.createTree(1017,-3099,0);
		tree.createTree(892,-2872,1);
		tree.createTree(1001,-2741,0);
		tree.createTree(827,-2651,0);
		tree.createTree(661,-2586,2);
		tree.createTree(596,-2758,0);
		tree.createTree(488,-2656,1);
		tree.createTree(349,-2867,0);
		tree.createTree(322,-2611,2);
		tree.createTree(595,-3052,2);
		tree.createTree(409,-3166,1);
		tree.createTree(732,-3228,2);
		tree.createTree(898,-3316,1);
		tree.createTree(1039,-3424,0);
		tree.createTree(1004,-3593,2);
		tree.createTree(743,-3616,1);
		tree.createTree(674,-3501,0);
		tree.createTree(462,-3594,1);
		tree.createTree(338,-3508,0);
		tree.createTree(206,-3589,0);
		new bush(456,-3570,0);
		new bush(704,-3484,0);
		new bush(1022,-3589,0);
		new bush(1005,-3397,1);
		new bush(693,-3220,2);
		new bush(718,-3249,0);
		new bush(594,-3073,0);
		new bush(570,-3037,0);
		new bush(595,-2789,2);
		new bush(615,-2731,0);
		new bush(893,-2843,1);
		new bush(1030,-3114,0);
		new bush(1003,-3082,0);
		new bush(892,-3300,2);
		new grave(698,-3965,0);
		new grave(821,-3816,2);
		new grave(903,-3776,2);
		new grave(946,-3861,2);
		new grave(914,-3945,2);
		new grave(1019,-3729,0);
		new grave(1131,-3749,2);
		new grave(1085,-3879,2);
		new grave(1174,-3968,2);
		new grave(1225,-3917,2);
		new grave(1075,-3965,2);
		new grave(1201,-3836,2);
		new grave(1301,-3901,1);
		new grave(1378,-3954,1);
		new grave(1417,-3774,2);
		new grave(1490,-3829,2);
		new grave(1518,-3921,0);
		new grave(1596,-3954,1);
		new grave(1415,-3887,0);
		new grave(1628,-3857,1);
		new grave(1603,-3778,2);
		new grave(1704,-3942,1);
		new grave(1746,-3840,0);
		new grave(1849,-3779,2);
		new grave(1881,-3868,2);
		new grave(1828,-3931,1);
		new grave(1961,-3931,2);
		new grave(1965,-3823,1);
		new grave(2002,-3744,2);
		new grave(2065,-3917,2);
		new grave(2100,-3968,1);
		new grave(2114,-3714,2);
		new grave(2140,-3779,2);
		new grave(2163,-3883,2);
		new grave(2210,-3705,0);
		new grave(2275,-3667,2);
		new grave(2333,-3707,2);
		new grave(2293,-3753,0);
		new grave(1011,-3964,2);
		new grave(748,-3787,1);
		new grave(544,-3876,2);
		new grave(479,-3944,2);
		new grave(370,-3991,2);
		new grave(256,-3949,0);
		new grave(279,-3794,2);
		new grave(471,-3794,2);
		new grave(879,-3628,2);
		new grave(466,-3407,2);
		new grave(274,-2537,2);
		new flower(542,-3340,2);
		new flower(943,-2857,4);
		new flower(949,-3139,8);
		new flower(1099,-3676,10);
		new flower(856,-3880,7);
		new flower(591,-3859,7);
		new flower(417,-3940,2);
		tree.createTree(1166,-3176,1);
		tree.createTree(1181,-3197,1);
		tree.createTree(1166,-3221,2);
		tree.createTree(1189,-3242,2);
		tree.createTree(1168,-3278,1);
		tree.createTree(1192,-3262,1);
		tree.createTree(1191,-3304,1);
		tree.createTree(1193,-3331,1);
		tree.createTree(1191,-3356,2);
		tree.createTree(1203,-3383,0);
		tree.createTree(1178,-3403,2);
		tree.createTree(1204,-3426,2);
		tree.createTree(1184,-3462,0);
		tree.createTree(1206,-3444,0);
		tree.createTree(1210,-3478,2);
		tree.createTree(1194,-3503,0);
		tree.createTree(1213,-3525,2);
		tree.createTree(1191,-3546,2);
		tree.createTree(1218,-3567,1);
		tree.createTree(1197,-3595,1);
		tree.createTree(1220,-3621,1);
		tree.createTree(1243,-3661,2);
		tree.createTree(1219,-3643,1);
		tree.createTree(1215,-3693,1);
		tree.createTree(1248,-3672,2);
		
		// Trees
		tree.createTree(483,-4060,1);
		tree.createTree(460,-4070,1);
		tree.createTree(444,-4062,1);
		tree.createTree(417,-4062,2);
		tree.createTree(393,-4062,1);
		tree.createTree(351,-4057,1);
		tree.createTree(321,-4057,0);
		tree.createTree(273,-4075,1);
		tree.createTree(240,-4075,2);
		tree.createTree(225,-4045,2);
		tree.createTree(276,-4043,2);
		tree.createTree(340,-4041,1);
		tree.createTree(432,-4126,0);
		tree.createTree(251,-4126,0);
		tree.createTree(205,-4061,2);
		tree.createTree(1254,-3707,1);
		tree.createTree(1275,-3707,1);
		tree.createTree(1301,-3696,1);
		tree.createTree(1316,-3676,1);
		tree.createTree(1357,-3670,2);
		tree.createTree(1383,-3645,1);
		tree.createTree(1419,-3645,1);
		tree.createTree(1413,-3622,0);
		tree.createTree(1464,-3652,2);
		tree.createTree(1454,-3626,1);
		tree.createTree(1498,-3647,0);
		tree.createTree(1507,-3638,1);
		tree.createTree(1537,-3635,2);
		tree.createTree(1563,-3655,2);
		tree.createTree(1599,-3670,0);
		tree.createTree(1622,-3647,0);
		tree.createTree(1647,-3651,1);
		tree.createTree(1671,-3669,0);
		tree.createTree(1695,-3660,1);
		tree.createTree(1737,-3666,1);
		tree.createTree(1768,-3650,0);
		tree.createTree(1792,-3672,2);
		tree.createTree(1816,-3648,1);
		tree.createTree(1861,-3667,2);
		tree.createTree(1837,-3646,0);
		tree.createTree(1897,-3672,2);
		tree.createTree(1900,-3647,0);
		tree.createTree(1936,-3632,1);
		tree.createTree(1966,-3614,0);
		tree.createTree(1991,-3592,0);
		tree.createTree(2024,-3592,2);
		tree.createTree(2055,-3569,2);
		tree.createTree(2081,-3543,2);
		tree.createTree(2106,-3563,2);
		tree.createTree(2130,-3533,0);
		tree.createTree(2152,-3511,0);
		tree.createTree(2182,-3532,0);
		tree.createTree(2192,-3507,2);
		tree.createTree(2213,-3474,0);
		tree.createTree(2232,-3481,0);
		tree.createTree(2251,-3500,1);
		tree.createTree(2261,-3478,2);
		tree.createTree(2309,-3505,1);
		tree.createTree(2300,-3484,2);
		tree.createTree(2351,-3526,1);
		tree.createTree(2351,-3511,1);
		tree.createTree(2367,-3539,1);
		tree.createTree(2387,-3559,0);
		tree.createTree(2396,-3580,1);
		tree.createTree(2412,-3601,1);
		tree.createTree(2432,-3610,0);
		tree.createTree(2432,-3634,1);
		tree.createTree(2444,-3654,1);
		tree.createTree(2464,-3666,1);
		tree.createTree(2461,-3693,1);
		tree.createTree(2485,-3714,2);
		tree.createTree(2498,-3739,1);
		tree.createTree(2480,-3755,0);
		tree.createTree(2498,-3778,2);
		tree.createTree(2487,-3795,2);
		tree.createTree(2506,-3815,2);
		tree.createTree(2476,-3836,1);
		tree.createTree(2498,-3861,0);
		tree.createTree(2515,-3872,2);
		tree.createTree(2498,-3894,2);
		tree.createTree(2520,-3892,2);
		tree.createTree(2520,-3910,2);
		tree.createTree(2496,-3930,2);
		tree.createTree(2512,-3958,1);
		tree.createTree(2529,-3932,1);
		tree.createTree(2532,-3989,2);
		tree.createTree(2532,-3983,1);
		tree.createTree(2923,-2561,1);
		tree.createTree(2951,-2545,1);
		tree.createTree(2956,-2513,0);
		tree.createTree(2938,-2477,0);
		tree.createTree(2950,-2441,0);
		tree.createTree(2923,-2422,1);
		tree.createTree(2406,-2402,1);
		tree.createTree(2397,-2435,0);
		tree.createTree(2370,-2459,1);
		tree.createTree(2397,-2495,1);
		tree.createTree(2445,-2534,2);
		tree.createTree(2461,-2509,1);
		tree.createTree(2482,-2476,2);
		tree.createTree(2450,-2462,0);
		tree.createTree(2465,-2420,2);
		tree.createTree(2492,-2366,1);
		tree.createTree(2456,-2366,0);
		tree.createTree(2417,-2375,2);
		tree.createTree(2375,-2375,2);
		tree.createTree(2348,-2375,1);
		tree.createTree(2312,-2381,1);
		tree.createTree(2286,-2395,2);
		tree.createTree(2258,-2397,2);
		tree.createTree(2231,-2385,2);
		tree.createTree(2187,-2386,2);
		tree.createTree(2154,-2386,2);
		tree.createTree(2124,-2386,1);
		tree.createTree(2091,-2386,0);
		tree.createTree(2091,-2398,0);
		tree.createTree(2064,-2398,2);
		tree.createTree(2026,-2393,1);
		tree.createTree(1993,-2390,0);
		tree.createTree(1968,-2390,2);
		tree.createTree(1920,-2399,1);
		tree.createTree(1884,-2411,2);
		tree.createTree(1856,-2395,1);
		tree.createTree(1810,-2387,2);
		tree.createTree(1820,-2407,0);
		tree.createTree(1781,-2383,0);
		tree.createTree(1767,-2391,0);
		tree.createTree(1732,-2392,2);
		tree.createTree(1709,-2406,1);
		tree.createTree(1682,-2391,2);
		tree.createTree(1653,-2409,0);
		tree.createTree(1625,-2399,1);
		tree.createTree(1587,-2419,2);
		tree.createTree(1539,-2398,1);
		tree.createTree(1509,-2390,0);
		tree.createTree(1474,-2376,2);
		tree.createTree(1447,-2388,1);
		tree.createTree(1424,-2365,2);
		tree.createTree(1389,-2380,1);
		tree.createTree(1362,-2380,1);
		tree.createTree(1338,-2359,0);
		tree.createTree(1296,-2382,2);
		tree.createTree(1258,-2370,1);
		tree.createTree(1216,-2370,0);
		tree.createTree(1189,-2370,0);
		tree.createTree(1159,-2388,2);
		tree.createTree(1127,-2365,1);
		tree.createTree(1085,-2365,2);
		tree.createTree(1054,-2375,2);
		tree.createTree(1026,-2362,0);
		tree.createTree(993,-2386,0);
		tree.createTree(965,-2364,2);
		tree.createTree(937,-2383,2);
		tree.createTree(905,-2366,2);
		tree.createTree(875,-2380,0);
		tree.createTree(842,-2359,0);
		tree.createTree(816,-2381,0);
		tree.createTree(789,-2365,1);
		tree.createTree(762,-2383,1);
		tree.createTree(732,-2369,1);
		tree.createTree(702,-2381,2);
		tree.createTree(669,-2366,0);
		tree.createTree(643,-2380,2);
		tree.createTree(618,-2365,0);
		tree.createTree(589,-2378,1);
		tree.createTree(562,-2366,1);
		tree.createTree(523,-2384,1);
		tree.createTree(493,-2375,2);
		tree.createTree(461,-2381,0);
		tree.createTree(434,-2381,1);
		tree.createTree(404,-2383,0);
		tree.createTree(377,-2383,0);
		tree.createTree(335,-2356,2);
		tree.createTree(335,-2389,0);
		tree.createTree(308,-2374,1);
		tree.createTree(263,-2359,2);
		tree.createTree(263,-2383,1);
		tree.createTree(234,-2367,1);
		tree.createTree(195,-2367,2);
		tree.createTree(174,-2382,1);
		tree.createTree(156,-2406,2);
		tree.createTree(145,-2430,2);
		tree.createTree(148,-2451,0);
		tree.createTree(134,-2469,2);
		tree.createTree(167,-2494,2);
		tree.createTree(146,-2515,2);
		tree.createTree(158,-2536,1);
		tree.createTree(132,-2553,1);
		tree.createTree(158,-2570,1);
		tree.createTree(121,-2583,0);
		tree.createTree(147,-2597,0);
		tree.createTree(122,-2616,0);
		tree.createTree(152,-2634,0);
		tree.createTree(128,-2649,0);
		tree.createTree(158,-2670,2);
		tree.createTree(134,-2694,2);
		tree.createTree(120,-2713,1);
		tree.createTree(137,-2734,2);
		tree.createTree(128,-2755,1);
		tree.createTree(146,-2773,1);
		tree.createTree(128,-2800,0);
		tree.createTree(154,-2814,1);
		tree.createTree(133,-2838,0);
		tree.createTree(154,-2861,1);
		tree.createTree(2954,-3978,2);
		tree.createTree(2922,-3983,2);
		tree.createTree(2880,-3990,0);
		tree.createTree(2840,-3983,0);
		tree.createTree(2815,-4007,1);
		tree.createTree(2772,-3988,0);
		tree.createTree(2744,-3995,2);
		tree.createTree(2713,-4006,1);
		tree.createTree(2685,-3986,0);
		tree.createTree(2658,-4007,0);
		tree.createTree(2605,-3990,1);
		tree.createTree(2622,-4025,2);
		tree.createTree(2595,-4010,2);
		tree.createTree(2559,-4004,2);
		tree.createTree(2534,-4024,0);
		tree.createTree(2522,-4006,0);
		tree.createTree(2491,-4022,1);
		tree.createTree(2477,-4005,2);
		tree.createTree(2455,-4019,1);
		tree.createTree(2432,-4003,1);
		tree.createTree(2405,-4025,2);
		tree.createTree(2383,-4002,0);
		tree.createTree(2355,-4019,1);
		tree.createTree(2341,-3999,2);
		tree.createTree(2299,-4017,2);
		tree.createTree(2262,-4018,0);
		tree.createTree(2294,-3998,0);
		tree.createTree(2240,-4028,0);
		tree.createTree(2216,-4016,1);
		tree.createTree(2184,-4029,0);
		tree.createTree(2150,-4011,2);
		tree.createTree(2130,-4028,1);
		tree.createTree(2112,-4013,2);
		tree.createTree(2076,-4028,0);
		tree.createTree(2057,-4009,0);
		tree.createTree(2020,-4025,2);
		tree.createTree(2020,-4004,0);
		tree.createTree(1978,-4019,1);
		tree.createTree(1960,-4019,1);
		tree.createTree(1921,-4019,1);
		tree.createTree(1882,-4013,2);
		tree.createTree(1859,-4033,0);
		tree.createTree(1823,-4009,1);
		tree.createTree(1797,-4026,0);
		tree.createTree(1777,-4003,0);
		tree.createTree(1737,-4021,1);
		tree.createTree(1716,-4030,0);
		tree.createTree(1680,-4030,2);
		tree.createTree(1659,-4048,1);
		tree.createTree(1634,-4024,1);
		tree.createTree(1619,-4034,0);
		tree.createTree(161,-2884,0);
		tree.createTree(128,-2900,1);
		tree.createTree(151,-2924,1);
		tree.createTree(136,-2945,1);
		tree.createTree(152,-2961,0);
		tree.createTree(125,-2985,2);
		tree.createTree(144,-3012,2);
		tree.createTree(117,-3024,2);
		tree.createTree(134,-3041,2);
		tree.createTree(160,-3060,2);
		tree.createTree(138,-3085,2);
		tree.createTree(157,-3107,0);
		tree.createTree(134,-3120,2);
		tree.createTree(151,-3143,1);
		tree.createTree(171,-3178,2);
		tree.createTree(144,-3157,2);
		tree.createTree(138,-3203,1);
		tree.createTree(159,-3227,2);
		tree.createTree(164,-3249,2);
		tree.createTree(137,-3264,1);
		tree.createTree(155,-3288,2);
		tree.createTree(127,-3311,0);
		tree.createTree(164,-3327,1);
		tree.createTree(145,-3349,1);
		tree.createTree(160,-3369,2);
		tree.createTree(170,-3395,2);
		tree.createTree(146,-3422,1);
		tree.createTree(162,-3440,2);
		tree.createTree(148,-3463,0);
		tree.createTree(166,-3487,1);
		tree.createTree(190,-3499,2);
		tree.createTree(153,-3521,0);
		tree.createTree(177,-3551,2);
		tree.createTree(153,-3575,1);
		tree.createTree(184,-3522,2);
		tree.createTree(167,-3596,2);
		tree.createTree(189,-3624,1);
		tree.createTree(153,-3641,2);
		tree.createTree(153,-3611,1);
		tree.createTree(180,-3650,0);
		tree.createTree(167,-3664,0);
		tree.createTree(149,-3673,2);
		tree.createTree(149,-3697,0);
		tree.createTree(167,-3716,2);
		tree.createTree(147,-3737,2);
		tree.createTree(169,-3756,0);
		tree.createTree(149,-3770,0);
		tree.createTree(165,-3792,1);
		tree.createTree(146,-3814,2);
		tree.createTree(170,-3837,2);
		tree.createTree(149,-3859,0);
		tree.createTree(167,-3889,1);
		tree.createTree(149,-3907,1);
		tree.createTree(173,-3931,1);
		tree.createTree(174,-3878,0);
		tree.createTree(1589,-4028,0);
		tree.createTree(1556,-4040,0);
		tree.createTree(1530,-4026,1);
		tree.createTree(1513,-4037,0);
		tree.createTree(1477,-4035,2);
		tree.createTree(1433,-4050,2);
		tree.createTree(1450,-4028,0);
		tree.createTree(1395,-4042,2);
		tree.createTree(1362,-4040,0);
		tree.createTree(1329,-4051,1);
		tree.createTree(1313,-4030,1);
		tree.createTree(1290,-4043,2);
		tree.createTree(1268,-4039,0);
		tree.createTree(1247,-4050,1);
		tree.createTree(1228,-4034,2);
		tree.createTree(1206,-4050,0);
		tree.createTree(1188,-4041,1);
		tree.createTree(1160,-4057,2);
		tree.createTree(1138,-4030,1);
		tree.createTree(1108,-4051,1);
		tree.createTree(1086,-4034,2);
		tree.createTree(1058,-4052,1);
		tree.createTree(1039,-4033,1);
		tree.createTree(1005,-4049,1);
		tree.createTree(984,-4034,2);
		tree.createTree(956,-4043,2);
		tree.createTree(917,-4067,1);
		tree.createTree(917,-4046,2);
		tree.createTree(886,-4053,0);
		tree.createTree(861,-4031,2);
		tree.createTree(843,-4053,0);
		tree.createTree(821,-4043,1);
		tree.createTree(801,-4069,0);
		tree.createTree(771,-4047,0);
		tree.createTree(733,-4040,0);
		tree.createTree(707,-4063,1);
		tree.createTree(675,-4042,0);
		tree.createTree(636,-4063,0);
		tree.createTree(624,-4048,1);
		tree.createTree(585,-4069,0);
		tree.createTree(587,-4049,2);
		tree.createTree(551,-4059,0);
		tree.createTree(531,-4039,2);
		tree.createTree(510,-4052,1);
		tree.createTree(158,-3953,1);
		tree.createTree(163,-3972,0);
		tree.createTree(157,-3996,1);
		tree.createTree(166,-4032,0);
		tree.createTree(166,-4002,1);
		tree.createTree(148,-4041,0);
		tree.createTree(167,-4067,2);
		tree.createTree(147,-4086,0);
		tree.createTree(163,-4105,2);
		tree.createTree(144,-4146,2);
		tree.createTree(163,-4123,0);
	}
	
	public static ArrayList<chunk> makeFarlsworthFence(float atX, float atY) {
		
		// Where Farlsworth stands for the fence to spawn normally at the farm.
		float defaultX = 5;
		float defaultY = -406;
		
		// Adjust X and Y by:
		int adjustX = (int) (atX - defaultX);
		int adjustY = (int) (atY - defaultY);
		
		// Arraylist that will contain fence pieces.
		ArrayList<chunk> farlsworthFence = new ArrayList<chunk>();
		int fenceAdjustX = -6;
		farlsworthFence = spawnFence(farlsworthFence, adjustX + -30+fenceAdjustX,adjustY -435+2,adjustX + -30+fenceAdjustX,adjustY + 200); // Vertical, right
		farlsworthFence = spawnFence(farlsworthFence, adjustX + -1050+fenceAdjustX+17,adjustY + -462,adjustX + 10+fenceAdjustX,adjustY + -462); // Horizontal, top of field
		farlsworthFence = spawnFence(farlsworthFence, adjustX + -168+40 - 580,adjustY + 17,adjustX + 70+420,adjustY + 17); // Horizontal, right of bridge.
		farlsworthFence = spawnFence(farlsworthFence, adjustX + -168+40 - 900,adjustY + 17,adjustX + -168 - 580,adjustY + 17); // Horizontal, left of bridge.
		farlsworthFence = spawnFence(farlsworthFence, adjustX + -450+fenceAdjustX,adjustY + -436+2,adjustX + -450+fenceAdjustX,adjustY + 200); // Vertical, far left
		c = new fenceBarsSmall(adjustX + 70+375,adjustY + 43,0);
		farlsworthFence.add(c);
		c = new fenceBarsSmall(adjustX + 70+375+3,adjustY + 43,0);
		farlsworthFence.add(c);
		c = new fenceBarsSmall(adjustX + 70+375+6,adjustY + 43,0);
		farlsworthFence.add(c);
		c = new fenceBarsSmall(adjustX + 70+375+9,adjustY + 43,0);
		farlsworthFence.add(c);
		c = new fenceBarsSmall(adjustX + 70+375+12,adjustY + 43,0);
		farlsworthFence.add(c);
		
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
		farlsworthFence = spawnFence(farlsworthFence, adjustX + 35,adjustY + 2 -435,adjustX + 35,adjustY + 200); // Vertical, left
		farlsworthFence = spawnFence(farlsworthFence, adjustX + 455,adjustY + 3 + -436,adjustX + 455,adjustY + 300); // Vertical, right
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
	public void createSpawnArea()  {
		
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
		new haystack(-195,-165,0);
		
		// Make bridge
		new bridgePole(-192+6-580,35-11,0);
		new bridgePole(-140-2-580,35-10,0);
		new bridge(-170-16-580,56-33,0);
		
		// Draw rocks behind spawn.
		new rock(-24-550,75,0);
		new rock(21-550,111,1);
		new rock(-6-550,147,0);
		
		////////////////////////////
		//// FARMHOUSE AREA  ///////
		////////////////////////////
		tree.createTree(-720,-325,0);
		new farmHouse(-650,-420,0);
		new barn(-950,-420,0);
		farmer theFarmer = new farmer(-711,-247);
		theFarmer.setFacingDirection("Down");
		tree.createTree(-1017, -414, 1);
		tree.createTree(-1011, 0, 0);
		new haystack(-960,-351,1);
		new haystack(-875,-351,1);
		new bush(-1025,-130,0);
		new bush(-909,-9,1);
		new bush(-510,-330,1);
		
		/////////////////////////////////////
		//// ARMORY/SHED/ACROSS RIVER ///////
		/////////////////////////////////////
		new blackSmith(-600-550,200,0);
		tree.createTree(-135-550,186,1);
		tree.createTree(-70-550,220,2);
		new bush(-110-550,250,0);
		tree.createTree(-120-550,280,1);
		tree.createTree(-135-550,310,2);
		tree.createTree(-70-550,340,0);
		new bush(-90-550,360,1);
		tree.createTree(-90-550,380,1);
		tree.createTree(-65-550,410,2);
		tree.createTree(-40-550,450,1);
		tree.createTree(-99-550,212,0);
		tree.createTree(-120-550,323,1);
		tree.createTree(-58-550,425,2);
		tree.createTree(-774-550,428,2);
		tree.createTree(-693-550,314,1);
		tree.createTree(-627-550,260,1);
		
		// Dagger.
		item daggerSpawn = new dagger(-800,387);
		
		// Corner
		tree.createTree(-60-550,480,2);
		tree.createTree(-20-550,500,1);
		tree.createTree(-55-550,525,2);
		tree.createTree(-80-550,545,1);
		tree.createTree(-100-550,570,1);
		new bush(-142-550,360,2);
		new bush(-250-550,465,0);
		
		// The bottom trees.
		tree.createTree(-331-550,-462,0);
		tree.createTree(-361-550,-462,2);
		tree.createTree(-397-550,-468,2);
		tree.createTree(-422-550,-452,0);
		tree.createTree(-439-550,-469,1);
		tree.createTree(-460-550,-447,1);
		tree.createTree(-487-550,-465,2);
		tree.createTree(-1003-550,-567,0);
		tree.createTree(-993-550,-536,1);
		tree.createTree(-1012-550,-520,0);
		tree.createTree(-120-550,600,2);
		tree.createTree(-145-550,580,0);
		tree.createTree(-165-550,605,2);
		tree.createTree(-195-550,585,1);
		tree.createTree(-225-550,585,1);
		tree.createTree(-240-550,610,0);
		tree.createTree(-260-550,600,1);
		tree.createTree(-285-550,575,2);
		tree.createTree(-310-550,610,2);
		tree.createTree(-330-550,565,2);
		tree.createTree(-345-550,585,1);
		tree.createTree(-375-550,615,0);
		tree.createTree(-415-550,600,2);
		tree.createTree(-445-550,575,0);
		tree.createTree(-465-550,605,0);
		tree.createTree(-490-550,585,1);
		tree.createTree(-525-550,580,1);
		tree.createTree(-550-550,615,2);
		tree.createTree(-580-550,610,2);
		tree.createTree(-610-550,580,1);
		tree.createTree(-635-550,610,2);
		tree.createTree(-665-550,580,1);
		tree.createTree(-700-550,580,0);
		tree.createTree(-715-550,615,0);
		
		// The left trees.
		tree.createTree(-730-550,590,0);
		tree.createTree(-700-550,560,1);
		tree.createTree(-660-550,555,2);
		tree.createTree(-685-550,530,0);
		tree.createTree(-705-550,499,2);
		tree.createTree(-735-550,476,1);
		tree.createTree(-755-550,454,2);
		tree.createTree(-785-550,415,0);
		tree.createTree(-745-550,390,1);
		tree.createTree(-710-550,360,0);
		tree.createTree(-685-550,335,2);
		tree.createTree(-655-550,300,1);
		tree.createTree(-774-550,234,1);
		tree.createTree(-670-550,275,2);
		tree.createTree(-680-550,260,1);
		tree.createTree(-690-550,235,0);
		tree.createTree(-730-550,200,1);
		new bush(-741-550,240,2);
		new bush(-642-550,393,1);
		
		// Misc left trees beyond the line of trees that block you
		tree.createTree(-850-550,590,0);
		tree.createTree(-775-550,560,2);
		tree.createTree(-1025-550,555,1);
		tree.createTree(-855-550,530,2);
		tree.createTree(-1200-550,499,1);
		tree.createTree(-1100-550,476,1);
		tree.createTree(-929-550,454,0);
		tree.createTree(-828-550,415,0);
		tree.createTree(-979-550,390,2);
		tree.createTree(-1202-550,360,0);
		tree.createTree(-827-550,335,2);
		tree.createTree(-1209-550,300,1);
		tree.createTree(-919-550,275,0);
		tree.createTree(-929-550,260,2);
		tree.createTree(-890-550,235,0);
		tree.createTree(-950-550,200,2);
		new bush(-921-550,510,1);
		new bush(-771-550,636,2);
		new bush(-795-550,201,0);
		new rock(-891-550,312,1);
		tree.createTree(-804-550,675,1);
		
		// Trees below bottom tree wall.
		tree.createTree(-699-550,672,2);
		tree.createTree(-660-550,770,1);
		tree.createTree(-635-550,890,2);
		tree.createTree(-580-550,840,1);
		tree.createTree(-520-550,780,2);
		tree.createTree(-480-550,827,1);
		tree.createTree(-410-550,820,0);
		tree.createTree(-385-550,890,0);
		tree.createTree(-360-550,760,1);
		tree.createTree(-519-550, 660, 0);
		tree.createTree(-310-550,700,1);
		tree.createTree(-280-550,792,0);
		tree.createTree(-265-550,700,2);
		tree.createTree(-200-550,679,0);
		tree.createTree(-160-550,740,1);
		new rock(-612-550,669,1);
		new bush(-429-550,636,1);
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
		
		// Is the zone on fire?
		isOnFire = new event("forestIsOnFire");
	}
	
	// Deal with the first well we encounters.
	public void dealWithRegionStuff() {
		player currPlayer = player.getPlayer();
		if(currPlayer != null && currPlayer.isWithin(1138,-484,1666,-46) && wellTooltipLoaded != null && !wellTooltipLoaded.isCompleted()) {
			wellTooltipLoaded.setCompleted(true);
			tooltipString t = new tooltipString("Press 'e' on a well or river to save and heal.");
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
		if(stormInProgress != null && !stormInProgress.isCompleted() && currPlayer != null && currPlayer.isWithin(2439,-918,2914,-790)) {
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
				storm s = new storm(7f);
			}
		}
	}
	
	
	// Do zone specific tasks that aren't monitored by
	// zone specific units. 
	@Override
	public void update() {
		startStormFromFog();
		doForestFireStuff();
		dealWithRegionStuff();
	}
	
	// Do forest fire stuff.
	public void doForestFireStuff() {
		
		if(isOnFire != null && isOnFire.isCompleted()) {
		}
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