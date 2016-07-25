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
	
	// Lightning tree
	public static tree lightningTree;
	
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
				c = grass.createChunk(i*grass.DEFAULT_CHUNK_WIDTH + x1, j*grass.DEFAULT_CHUNK_HEIGHT + y1);
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
				c = water.createChunk(i*water.DEFAULT_CHUNK_WIDTH + x1, j*water.DEFAULT_CHUNK_HEIGHT + y1);
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
				c = new flower(i*flower.DEFAULT_SPRITE_WIDTH + x1, j*flower.DEFAULT_SPRITE_WIDTH + y1, r);
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
		
		// Denmother area
		createDenmotherArea();
		
		// Spawn units
		spawnUnits();
		
		// Sort chunks.
		chunk.sortChunks();
		
		// Play zone music.
		 music m = new music(zoneMusic); 
		
	}
	
	//////////////////////
	// INDIVIDUAL AREAS //
	//////////////////////
	
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
		
		// First wolf
		u = new yellowWolf(748,-790);
		u.setFacingDirection("Down");
		u = new redWolf(788,-790);
		u.setFacingDirection("Down");
		u = new blackWolf(828,-790);
		u.setFacingDirection("Down");

	}
	
	// Denmother area
	public void createDenmotherArea() {
		spawnGrassRect(-4954+2,-5588+12,-3115,63);
		spawnGrassRect(-3155,-548,-2900,60);
		c = new bone(-3172,-326,1);
		c = new bone(-3139,-135,2);
		c = new bone(-2866,-240,0);
		c = new bone(-2778,-352,2);
		c = new bone(-2630,-151,3);
		c = new bone(-2728,-17,0);
		c = new bone(-2978,-82,1);
		c = new bone(-3235,-17,1);
		c = new bone(-3421,-117,4);
		c = new bone(-3592,-36,1);
		c = new bone(-3757,-114,1);
		c = new bone(-3623,-219,3);
		c = new bone(-3479,-335,0);
		c = new bone(-3307,-402,3);
		c = new bone(-3447,-501,0);
		c = new bone(-3236,-567,3);
		c = new bone(-3215,-689,0);
		c = new bone(-3389,-786,3);
		c = new bone(-3319,-892,1);
		c = new bone(-3203,-1028,3);
		c = new bone(-3372,-1119,2);
		c = new bone(-3558,-1079,0);
		c = new bone(-3713,-1143,2);
		c = new bone(-3621,-980,1);
		c = new bone(-3451,-957,2);
		c = new bone(-3564,-879,4);
		c = new bone(-3698,-790,1);
		c = new bone(-3639,-665,0);
		c = new bone(-3692,-587,0);
		c = new bone(-3641,-443,4);
		c = new bone(-3813,-422,1);
		c = new bone(-3928,-303,0);
		c = new bone(-3976,-181,1);
		c = new bone(-4034,-79,0);
		c = new bone(-4195,-44,1);
		c = new bone(-4343,-17,3);
		c = new bone(-4340,-128,4);
		c = new bone(-4190,-236,0);
		c = new bone(-4058,-316,2);
		c = new bone(-4107,-454,1);
		c = new bone(-4296,-386,4);
		c = new bone(-4194,-568,4);
		c = new bone(-3943,-533,2);
		c = new bone(-3795,-661,4);
		c = new bone(-3974,-741,2);
		c = new bone(-4064,-855,2);
		c = new bone(-3901,-893,4);
		c = new bone(-3761,-912,4);
		c = new bone(-3791,-1039,4);
		c = new bone(-3936,-1031,0);
		c = new bone(-4077,-1135,2);
		c = new bone(-4188,-1205,1);
		c = new bone(-4250,-1083,1);
		c = new bone(-4325,-962,0);
		c = new bone(-4317,-836,1);
		c = new bone(-4213,-787,2);
		c = new bone(-4070,-943,0);
		c = new bone(-3303,-1229,0);
		c = new bone(-3489,-1313,1);
		c = new bone(-3664,-1343,1);
		c = new bone(-3852,-1386,1);
		c = new bone(-3878,-1280,2);
		c = new bone(-4088,-1289,4);
		c = new bone(-4283,-1308,3);
		c = new bone(-4457,-1261,1);
		c = new bone(-4457,-1023,3);
		c = new bone(-4421,-882,0);
		c = new bone(-4504,-763,3);
		c = new bone(-4320,-642,0);
		c = new bone(-4476,-461,1);
		c = new bone(-4368,-293,1);
		c = new bone(-4484,-183,2);
		c = new bone(-4496,-33,2);
		c = new bone(-3937,18,0);
		c = new bone(-3461,24,4);
		c = new bone(-3303,-258,1);
	}
	
	// Final area with all the fire.
	public void createFinalArea() {
		
		// Lightning tree
		lightningTree = tree.createTree(-758,-3937,0);
		
		// Trees
		c = tree.createTree(-703,-4069,1);
		c = tree.createTree(-735,-4081,1);
		c = tree.createTree(-759,-4098,1);
		c = tree.createTree(-787,-4119,1);
		c = tree.createTree(-809,-4141,2);
		c = tree.createTree(-831,-4160,1);
		c = tree.createTree(-862,-4187,1);
		c = tree.createTree(-879,-4208,1);
		c = tree.createTree(-909,-4227,2);
		c = tree.createTree(-699,-3685,1);
		c = tree.createTree(-737,-3685,1);
		c = tree.createTree(-771,-3700,1);
		c = tree.createTree(-805,-3693,1);
		c = tree.createTree(-842,-3691,2);
		c = tree.createTree(-876,-3703,1);
		c = tree.createTree(-916,-3712,2);
		c = tree.createTree(-950,-3725,2);
		c = tree.createTree(-983,-3724,0);
		c = tree.createTree(-1023,-3711,2);
		c = tree.createTree(-1053,-3706,2);
		c = tree.createTree(-1098,-3706,1);
		c = tree.createTree(-1137,-3706,0);
		c = tree.createTree(-1182,-3698,2);
		c = tree.createTree(-1207,-3709,0);
		c = tree.createTree(-1240,-3696,0);
		c = tree.createTree(-939,-4237,0);
		c = tree.createTree(-977,-4224,2);
		c = tree.createTree(-998,-4238,2);
		c = tree.createTree(-1013,-4223,2);
		c = tree.createTree(-1038,-4223,0);
		c = tree.createTree(-1078,-4246,2);
		c = tree.createTree(-1101,-4227,0);
		c = tree.createTree(-1144,-4246,1);
		c = tree.createTree(-1160,-4220,2);
		c = tree.createTree(-1197,-4239,0);
		c = tree.createTree(-1218,-4215,2);
		c = tree.createTree(-1259,-4229,2);
		c = tree.createTree(-1279,-3695,0);
		c = tree.createTree(-1317,-3688,0);
		c = tree.createTree(-1355,-3702,1);
		c = tree.createTree(-1390,-3677,2);
		c = tree.createTree(-1424,-3697,0);
		c = tree.createTree(-1287,-4226,1);
		c = tree.createTree(-1319,-4241,1);
		c = tree.createTree(-1353,-4246,0);
		c = tree.createTree(-1388,-4236,0);
		c = tree.createTree(-1427,-4239,0);
		c = tree.createTree(-1468,-4239,0);
		c = tree.createTree(-1506,-4239,0);
		c = tree.createTree(-1539,-4237,1);
		c = tree.createTree(-1452,-3689,0);
		c = tree.createTree(-1500,-3694,0);
		c = tree.createTree(-1527,-3675,1);
		c = tree.createTree(-1555,-3657,1);
		c = tree.createTree(-1584,-3655,0);
		c = tree.createTree(-1618,-3628,1);
		c = tree.createTree(-1640,-3604,0);
		c = tree.createTree(-1659,-3578,2);
		c = tree.createTree(-1686,-3551,2);
		c = tree.createTree(-1708,-3534,1);
		c = tree.createTree(-1719,-3503,1);
		c = tree.createTree(-1761,-3486,1);
		c = tree.createTree(-1752,-3452,0);
		c = tree.createTree(-1726,-3402,1);
		c = tree.createTree(-1724,-3424,0);
		c = tree.createTree(-1723,-3378,2);
		c = tree.createTree(-1575,-4239,2);
		c = tree.createTree(-1614,-4218,1);
		c = tree.createTree(-1647,-4224,0);
		c = tree.createTree(-1682,-4214,0);
		c = tree.createTree(-1725,-4229,1);
		c = tree.createTree(-1748,-4196,1);
		c = tree.createTree(-1806,-4183,0);
		c = tree.createTree(-1742,-4219,0);
		c = tree.createTree(-1772,-4200,2);
		c = tree.createTree(-1833,-4180,0);
		c = tree.createTree(-1862,-4165,1);
		c = tree.createTree(-1885,-4142,0);
		c = tree.createTree(-1907,-4150,2);
		c = tree.createTree(-1934,-4123,1);
		c = tree.createTree(-1978,-4119,2);
		c = tree.createTree(-1996,-4087,1);
		c = tree.createTree(-2027,-4073,2);
		c = tree.createTree(-2061,-4043,1);
		c = tree.createTree(-2053,-4066,1);
		c = tree.createTree(-2091,-4031,0);
		c = tree.createTree(-2107,-4005,1);
		c = tree.createTree(-2130,-3989,1);
		c = tree.createTree(-2175,-3969,0);
		c = tree.createTree(-2145,-3950,0);
		c = tree.createTree(-2190,-3926,2);
		c = tree.createTree(-2239,-3899,0);
		c = tree.createTree(-2211,-3913,0);
		c = tree.createTree(-2175,-3938,2);
		c = tree.createTree(-2255,-3872,0);
		c = tree.createTree(-2283,-3837,2);
		c = tree.createTree(-2314,-3816,0);
		c = tree.createTree(-2291,-3786,0);
		c = tree.createTree(-2314,-3763,0);
		c = tree.createTree(-2338,-3742,0);
		c = tree.createTree(-2321,-3694,2);
		c = tree.createTree(-2334,-3649,1);
		c = tree.createTree(-2324,-3674,0);
		c = tree.createTree(-2337,-3722,2);
		c = tree.createTree(-2334,-3631,1);
		c = tree.createTree(-2347,-3611,1);
		c = tree.createTree(-2353,-3579,0);
		c = tree.createTree(-2323,-3592,0);
		c = tree.createTree(-2344,-3554,2);
		c = tree.createTree(-2367,-3538,1);
		c = tree.createTree(-2340,-3515,1);
		c = tree.createTree(-2359,-3493,1);
		c = tree.createTree(-2318,-3469,2);
		c = tree.createTree(-2350,-3452,0);
		c = tree.createTree(-2330,-3426,2);
		c = tree.createTree(-2361,-3395,0);
		c = tree.createTree(-2356,-3373,2);
		c = tree.createTree(-2336,-3350,1);
		c = tree.createTree(-2355,-3342,2);
		c = tree.createTree(-2337,-3314,0);
		c = tree.createTree(-2360,-3287,2);
		c = tree.createTree(-2337,-3264,1);
		c = tree.createTree(-2351,-3244,1);
		c = tree.createTree(-2322,-3226,0);
		c = tree.createTree(-2341,-3193,0);
		c = tree.createTree(-2321,-3176,0);
		c = tree.createTree(-2348,-3148,0);
		c = tree.createTree(-2323,-3126,2);
		c = tree.createTree(-2347,-3098,1);
		c = tree.createTree(-2325,-3076,0);
		c = tree.createTree(-2352,-3052,0);
		c = tree.createTree(-2321,-3027,0);
		c = tree.createTree(-2355,-3000,2);
		c = tree.createTree(-2323,-2979,1);
		c = tree.createTree(-1723,-3362,2);
		c = tree.createTree(-1723,-3345,2);
		c = tree.createTree(-1696,-3329,1);
		c = tree.createTree(-1726,-3288,1);
		c = tree.createTree(-1698,-3301,2);
		c = tree.createTree(-1696,-3271,1);
		c = tree.createTree(-1713,-3254,0);
		c = tree.createTree(-1690,-3234,0);
		c = tree.createTree(-1712,-3205,1);
		c = tree.createTree(-1690,-3186,0);
		c = tree.createTree(-1720,-3163,2);
		c = tree.createTree(-1692,-3141,1);
		c = tree.createTree(-1718,-3107,2);
		c = tree.createTree(-1687,-3122,2);
		c = tree.createTree(-1681,-3077,2);
		c = tree.createTree(-1711,-3054,2);
		c = tree.createTree(-1679,-3032,0);
		c = tree.createTree(-1711,-3089,2);
		c = tree.createTree(-1709,-3003,2);
		c = tree.createTree(-1662,-3002,0);
		c = tree.createTree(-1689,-2971,0);
		c = tree.createTree(-1702,-2939,0);
		c = tree.createTree(-1696,-2984,2);
		c = tree.createTree(-2357,-2946,2);
		c = tree.createTree(-2357,-2960,0);
		c = tree.createTree(-2328,-2944,1);
		c = tree.createTree(-2345,-2920,2);
		c = tree.createTree(-2322,-2901,1);
		c = tree.createTree(-2351,-2878,2);
		c = tree.createTree(-2322,-2860,0);
		c = tree.createTree(-2342,-2840,2);
		c = tree.createTree(-2310,-2818,1);
		c = tree.createTree(-2353,-2789,1);
		c = tree.createTree(-2337,-2769,2);
		c = tree.createTree(-2315,-2750,0);
		c = tree.createTree(-2336,-2725,0);
		c = tree.createTree(-2350,-2681,0);
		c = tree.createTree(-2333,-2700,2);
		c = tree.createTree(-2318,-2796,1);
		c = tree.createTree(-1678,-2952,2);
		c = tree.createTree(-1678,-2920,0);
		c = tree.createTree(-1654,-2896,1);
		c = tree.createTree(-1679,-2881,0);
		c = tree.createTree(-1647,-2864,1);
		c = tree.createTree(-1661,-2846,0);
		c = tree.createTree(-1678,-2829,1);
		c = tree.createTree(-1671,-2790,0);
		c = tree.createTree(-1688,-2805,2);
		c = tree.createTree(-1683,-2769,0);
		c = tree.createTree(-1663,-2752,0);
		c = tree.createTree(-1687,-2732,1);
		c = tree.createTree(-1670,-2718,1);
		c = tree.createTree(-1696,-2696,2);
		c = tree.createTree(-1674,-2677,1);
		c = tree.createTree(-1681,-2643,1);
		c = tree.createTree(-1662,-2624,1);
		c = tree.createTree(-1677,-2653,2);
		c = tree.createTree(-1757,-3469,2);
		c = tree.createTree(-2263,-3921,0);
		c = tree.createTree(-2346,-3828,2);
		c = tree.createTree(-2371,-3842,1);
		c = tree.createTree(-2294,-3937,2);
		c = tree.createTree(-2307,-3961,1);
		c = tree.createTree(-2326,-3976,2);
		c = tree.createTree(-2398,-3851,1);
		c = tree.createTree(-2443,-3876,1);
		c = tree.createTree(-2435,-3860,0);
		c = tree.createTree(-2347,-3993,1);
		c = tree.createTree(-2382,-4011,0);
		c = tree.createTree(-2416,-4024,1);
		c = tree.createTree(-2447,-4017,1);
		c = tree.createTree(-2479,-4021,0);
		c = tree.createTree(-2504,-4043,0);
		c = tree.createTree(-2521,-4029,2);
		c = tree.createTree(-2548,-4049,1);
		c = tree.createTree(-2568,-4032,1);
		c = tree.createTree(-2596,-4043,2);
		c = tree.createTree(-2631,-4043,0);
		c = tree.createTree(-2661,-4035,2);
		c = tree.createTree(-2698,-4050,2);
		c = tree.createTree(-2726,-4040,0);
		c = tree.createTree(-2755,-4059,1);
		c = tree.createTree(-2776,-4045,0);
		c = tree.createTree(-2813,-4061,0);
		c = tree.createTree(-2843,-4042,1);
		c = tree.createTree(-2872,-4061,1);
		c = tree.createTree(-2890,-4040,1);
		c = tree.createTree(-2918,-4040,1);
		c = tree.createTree(-2938,-4059,0);
		c = tree.createTree(-2957,-4043,2);
		c = tree.createTree(-3000,-4051,0);
		c = tree.createTree(-2999,-3842,2);
		c = tree.createTree(-2967,-3860,0);
		c = tree.createTree(-2941,-3840,2);
		c = tree.createTree(-2917,-3857,0);
		c = tree.createTree(-2881,-3835,2);
		c = tree.createTree(-2826,-3839,0);
		c = tree.createTree(-2845,-3823,0);
		c = tree.createTree(-2769,-3839,2);
		c = tree.createTree(-2801,-3815,0);
		c = tree.createTree(-2736,-3834,1);
		c = tree.createTree(-2714,-3815,2);
		c = tree.createTree(-2675,-3836,2);
		c = tree.createTree(-2639,-3815,1);
		c = tree.createTree(-2608,-3839,1);
		c = tree.createTree(-2582,-3820,0);
		c = tree.createTree(-2547,-3842,2);
		c = tree.createTree(-2523,-3825,2);
		c = tree.createTree(-2495,-3849,1);
		c = tree.createTree(-2467,-3849,2);
		c = tree.createTree(-2351,-2660,1);
		c = tree.createTree(-2344,-2636,0);
		c = tree.createTree(-2360,-2619,0);
		c = tree.createTree(-2330,-2600,0);
		c = tree.createTree(-2361,-2576,1);
		c = tree.createTree(-1663,-2598,1);
		c = tree.createTree(-1695,-2584,0);
		c = tree.createTree(-1691,-2556,2);
		c = tree.createTree(-1710,-2537,0);
		c = tree.createTree(-1730,-2521,0);
		c = tree.createTree(-1749,-2498,0);
		c = tree.createTree(-1752,-2459,1);
		c = tree.createTree(-1752,-2480,0);
		c = tree.createTree(-1761,-2433,1);
		c = tree.createTree(-1780,-2417,1);
		c = tree.createTree(-1801,-2403,1);
		c = tree.createTree(-1801,-2372,1);
		c = tree.createTree(-1832,-2353,2);
		c = tree.createTree(-1797,-2384,1);
		c = tree.createTree(-2398,-2576,0);
		c = tree.createTree(-2412,-2547,1);
		c = tree.createTree(-2442,-2528,1);
		c = tree.createTree(-2474,-2544,1);
		c = tree.createTree(-2495,-2519,0);
		c = tree.createTree(-2551,-2512,2);
		c = tree.createTree(-2527,-2526,2);
		c = tree.createTree(-2582,-2512,1);
		c = tree.createTree(-2608,-2516,2);
		c = tree.createTree(-2646,-2530,1);
		c = tree.createTree(-2664,-2512,2);
		c = tree.createTree(-2692,-2526,0);
		c = tree.createTree(-2716,-2526,0);
		c = tree.createTree(-2765,-2509,1);
		c = tree.createTree(-2755,-2517,2);
		c = tree.createTree(-2822,-2517,2);
		c = tree.createTree(-2802,-2494,0);
		c = tree.createTree(-2868,-2511,1);
		c = tree.createTree(-2896,-2504,2);
		c = tree.createTree(-2918,-2489,0);
		c = tree.createTree(-2950,-2467,2);
		c = tree.createTree(-2992,-2460,2);
		c = tree.createTree(-3006,-2446,1);
		c = tree.createTree(-1835,-2338,2);
		c = tree.createTree(-1848,-2314,2);
		c = tree.createTree(-1848,-2289,1);
		c = tree.createTree(-1865,-2276,0);
		c = tree.createTree(-1888,-2259,1);
		c = tree.createTree(-1890,-2234,0);
		c = tree.createTree(-1912,-2213,0);
		c = tree.createTree(-1908,-2192,2);
		c = tree.createTree(-1940,-2181,0);
		c = tree.createTree(-1954,-2165,1);
		c = tree.createTree(-1967,-2141,1);
		c = tree.createTree(-1987,-2125,0);
		c = tree.createTree(-2007,-2108,0);
		c = tree.createTree(-2052,-2101,1);
		c = tree.createTree(-2063,-2077,0);
		c = tree.createTree(-2102,-2053,2);
		c = tree.createTree(-2092,-2069,0);
		c = tree.createTree(-2143,-2029,0);
		c = tree.createTree(-2120,-2049,0);
		c = tree.createTree(-2170,-2005,1);
		c = tree.createTree(-2218,-1990,1);
		c = tree.createTree(-2258,-1964,0);
		c = tree.createTree(-2253,-1984,1);
		c = tree.createTree(-2287,-1955,1);
		c = tree.createTree(-2339,-1955,2);
		c = tree.createTree(-2313,-1928,1);
		c = tree.createTree(-2351,-1932,1);
		c = tree.createTree(-2379,-1908,1);
		c = tree.createTree(-2405,-1926,1);
		c = tree.createTree(-2414,-1901,1);
		c = tree.createTree(-2448,-1906,1);
		c = tree.createTree(-2462,-1888,1);
		c = tree.createTree(-2478,-1904,1);
		c = tree.createTree(-2496,-1889,2);
		c = tree.createTree(-2518,-1864,1);
		c = tree.createTree(-2479,-1858,2);
		c = tree.createTree(-2491,-1832,1);
		c = tree.createTree(-2480,-1808,1);
		c = tree.createTree(-2499,-1793,2);
		c = tree.createTree(-2472,-1770,0);
		c = tree.createTree(-2497,-1748,1);
		c = tree.createTree(-2469,-1727,0);
		c = tree.createTree(-2495,-1711,0);
		c = tree.createTree(-2467,-1683,1);
		c = tree.createTree(-2467,-1648,2);
		c = tree.createTree(-2486,-1622,1);
		c = tree.createTree(-2448,-1667,2);
		c = tree.createTree(-2457,-1601,1);
		c = tree.createTree(-2485,-1570,1);
		c = tree.createTree(-2454,-1581,1);
		c = tree.createTree(-2473,-1539,2);
		c = tree.createTree(-2456,-1556,2);
		c = tree.createTree(-2456,-1518,1);
		c = tree.createTree(-3004,-2430,2);
		c = tree.createTree(-3004,-2409,1);
		c = tree.createTree(-2995,-2394,2);
		c = tree.createTree(-3004,-2366,1);
		c = tree.createTree(-2981,-2347,0);
		c = tree.createTree(-2999,-2315,2);
		c = tree.createTree(-2981,-2290,1);
		c = tree.createTree(-2995,-2262,0);
		c = tree.createTree(-2971,-2249,1);
		c = tree.createTree(-3001,-2219,1);
		c = tree.createTree(-2981,-2187,2);
		c = tree.createTree(-3005,-2161,2);
		c = tree.createTree(-2984,-2136,1);
		c = tree.createTree(-3011,-2122,2);
		c = tree.createTree(-2987,-2095,0);
		c = tree.createTree(-3001,-2074,0);
		c = tree.createTree(-2977,-2053,1);
		c = tree.createTree(-3005,-2003,0);
		c = tree.createTree(-2997,-2023,1);
		c = tree.createTree(-3001,-1982,2);
		c = tree.createTree(-3009,-1956,2);
		c = tree.createTree(-2992,-1939,1);
		c = tree.createTree(-2988,-1911,2);
		c = tree.createTree(-3010,-1894,1);
		c = tree.createTree(-2987,-1870,1);
		c = tree.createTree(-3005,-1577,2);
		c = tree.createTree(-2994,-1565,1);
		c = tree.createTree(-2971,-1547,0);
		c = tree.createTree(-2966,-1517,1);
		c = tree.createTree(-2950,-1497,0);
		c = tree.createTree(-2936,-1473,1);
		c = tree.createTree(-2893,-1483,2);
		c = tree.createTree(-2889,-1441,1);
		c = tree.createTree(-2857,-1416,2);
		c = tree.createTree(-2900,-1466,2);
		c = tree.createTree(-2804,-1402,2);
		c = tree.createTree(-2832,-1388,1);
		c = tree.createTree(-2800,-1360,1);
		c = tree.createTree(-2781,-1330,0);
		c = tree.createTree(-2417,-1536,1);
		c = tree.createTree(-2411,-1515,0);
		c = tree.createTree(-2372,-1494,2);
		c = tree.createTree(-2344,-1512,0);
		c = tree.createTree(-2322,-1486,2);
		c = tree.createTree(-2274,-1487,2);
		c = tree.createTree(-2244,-1457,0);
		c = tree.createTree(-2222,-1476,0);
		c = tree.createTree(-2177,-1452,1);
		c = tree.createTree(-2147,-1471,0);
		c = tree.createTree(-2120,-1430,0);
		c = tree.createTree(-2088,-1452,1);
		c = tree.createTree(-2066,-1419,1);
		c = tree.createTree(-2143,-1447,2);
		c = tree.createTree(-2040,-1404,0);
		c = tree.createTree(-1997,-1416,2);
		c = tree.createTree(-2011,-1395,2);
		c = tree.createTree(-1973,-1399,1);
		c = tree.createTree(-2777,-1308,0);
		c = tree.createTree(-2770,-1278,0);
		c = tree.createTree(-2744,-1261,1);
		c = tree.createTree(-2721,-1238,0);
		c = tree.createTree(-2690,-1246,1);
		c = tree.createTree(-2665,-1211,1);
		c = tree.createTree(-2627,-1218,1);
		c = tree.createTree(-2607,-1192,1);
		c = tree.createTree(-2558,-1194,0);
		c = tree.createTree(-2532,-1150,2);
		c = tree.createTree(-2550,-1173,2);
		c = tree.createTree(-2494,-1137,0);
		c = tree.createTree(-2465,-1113,1);
		c = tree.createTree(-2446,-1087,1);
		c = tree.createTree(-2431,-1060,1);
		c = tree.createTree(-2397,-1039,2);
		c = tree.createTree(-2371,-1020,1);
		c = tree.createTree(-2344,-993,0);
		c = tree.createTree(-1942,-1371,2);
		c = tree.createTree(-1938,-1392,0);
		c = tree.createTree(-1911,-1358,2);
		c = tree.createTree(-1889,-1336,2);
		c = tree.createTree(-1860,-1333,2);
		c = tree.createTree(-1825,-1323,1);
		c = tree.createTree(-1800,-1295,1);
		c = tree.createTree(-2313,-971,0);
		c = tree.createTree(-2296,-940,2);
		c = tree.createTree(-2260,-938,1);
		c = tree.createTree(-2295,-946,0);
		c = tree.createTree(-2226,-908,0);
		c = tree.createTree(-2197,-870,0);
		c = tree.createTree(-2197,-898,1);
		c = tree.createTree(-2241,-918,2);
		c = tree.createTree(-2164,-872,1);
		c = tree.createTree(-2134,-855,1);
		c = tree.createTree(-1802,-1313,0);
		c = tree.createTree(-2099,-845,0);
		c = tree.createTree(-2077,-823,2);
		c = tree.createTree(-2051,-811,2);
		c = tree.createTree(-2023,-794,1);
		c = tree.createTree(-2003,-767,1);
		c = tree.createTree(-1983,-750,2);
		c = tree.createTree(-1961,-734,2);
		c = tree.createTree(-1941,-714,1);
		c = tree.createTree(-1758,-1287,0);
		c = tree.createTree(-1735,-1261,2);
		c = tree.createTree(-1710,-1243,1);
		c = tree.createTree(-1682,-1256,2);
		c = tree.createTree(-1661,-1235,0);
		c = tree.createTree(-1620,-1205,2);
		c = tree.createTree(-1593,-1225,1);
		c = tree.createTree(-1625,-1225,0);
		c = tree.createTree(-1565,-1190,0);
		c = tree.createTree(-1582,-1208,0);
		c = tree.createTree(-1902,-713,1);
		c = tree.createTree(-1883,-683,0);
		c = tree.createTree(-1883,-701,1);
		c = tree.createTree(-1852,-673,0);
		c = tree.createTree(-1823,-671,0);
		c = tree.createTree(-1801,-642,0);
		c = tree.createTree(-1774,-658,1);
		c = tree.createTree(-1756,-630,0);
		c = tree.createTree(-1732,-609,1);
		c = tree.createTree(-1704,-606,1);
		c = tree.createTree(-1665,-574,0);
		c = tree.createTree(-1698,-588,2);
		c = tree.createTree(-1641,-562,1);
		c = tree.createTree(-1622,-542,1);
		c = tree.createTree(-1536,-1170,2);
		c = tree.createTree(-1511,-1145,2);
		c = tree.createTree(-1473,-1145,0);
		c = tree.createTree(-1457,-1126,1);
		c = tree.createTree(-1410,-1130,2);
		c = tree.createTree(-1404,-1100,1);
		c = tree.createTree(-1372,-1064,1);
		c = tree.createTree(-1393,-1078,2);
		c = tree.createTree(-1341,-1036,1);
		c = tree.createTree(-1358,-1012,1);
		c = tree.createTree(-1339,-993,1);
		c = tree.createTree(-1347,-954,1);
		c = tree.createTree(-1334,-976,2);
		c = tree.createTree(-1327,-921,2);
		c = tree.createTree(-1344,-934,2);
		c = tree.createTree(-1326,-898,1);
		c = tree.createTree(-1316,-867,0);
		c = tree.createTree(-1314,-886,1);
		c = tree.createTree(-1310,-840,2);
		c = tree.createTree(-1291,-817,1);
		c = tree.createTree(-1278,-792,1);
		c = tree.createTree(-1262,-769,2);
		c = tree.createTree(-1279,-742,0);
		c = tree.createTree(-1259,-715,2);
		c = tree.createTree(-1273,-693,0);
		c = tree.createTree(-1276,-673,1);
		c = tree.createTree(-1276,-645,2);
		c = tree.createTree(-1268,-616,2);
		c = tree.createTree(-1286,-582,1);
		c = tree.createTree(-1262,-595,1);
		c = tree.createTree(-1272,-555,0);
		c = tree.createTree(-1266,-533,2);
		c = tree.createTree(-1266,-495,2);
		c = tree.createTree(-1246,-511,1);
		c = tree.createTree(-1269,-478,0);
		c = tree.createTree(-1283,-460,1);
		c = tree.createTree(-1283,-434,0);
		c = tree.createTree(-1283,-416,2);
		c = tree.createTree(-1284,-392,0);
		c = tree.createTree(-1290,-365,1);
		c = tree.createTree(-1296,-343,1);
		c = tree.createTree(-1300,-308,1);
		c = tree.createTree(-1301,-281,2);
		c = tree.createTree(-1305,-256,2);
		c = tree.createTree(-1296,-316,0);
		c = tree.createTree(-1321,-238,2);
		c = tree.createTree(-1343,-204,1);
		c = tree.createTree(-1346,-223,1);
		c = tree.createTree(-1382,-177,1);
		c = tree.createTree(-1362,-193,2);
		c = tree.createTree(-1411,-147,2);
		c = tree.createTree(-1402,-166,2);
		c = tree.createTree(-1432,-126,1);
		c = tree.createTree(-1436,-94,0);
		c = tree.createTree(-1463,-110,0);
		c = tree.createTree(-1463,-55,0);
		c = tree.createTree(-1483,-72,2);
		c = tree.createTree(-1487,-29,2);
		c = tree.createTree(-1519,-7,2);
		c = tree.createTree(-1510,19,0);
		c = tree.createTree(-1545,41,0);
		c = tree.createTree(-1494,-55,0);
		c = tree.createTree(-1646,-536,0);
		c = tree.createTree(-1673,-516,1);
		c = tree.createTree(-1702,-512,2);
		c = tree.createTree(-1718,-478,0);
		c = tree.createTree(-1743,-489,2);
		c = tree.createTree(-1718,-506,0);
		c = tree.createTree(-1721,-447,2);
		c = tree.createTree(-1746,-465,2);
		c = tree.createTree(-1771,-432,2);
		c = tree.createTree(-1768,-449,1);
		c = tree.createTree(-1801,-413,0);
		c = tree.createTree(-1805,-385,2);
		c = tree.createTree(-1832,-361,2);
		c = tree.createTree(-1847,-423,2);
		c = tree.createTree(-1852,-401,2);
		c = tree.createTree(-1892,-417,0);
		c = tree.createTree(-1913,-389,1);
		c = tree.createTree(-1941,-414,0);
		c = tree.createTree(-1965,-398,0);
		c = tree.createTree(-1987,-417,1);
		c = tree.createTree(-2026,-406,0);
		c = tree.createTree(-2043,-437,1);
		c = tree.createTree(-2066,-429,0);
		c = tree.createTree(-2089,-410,1);
		c = tree.createTree(-2120,-434,2);
		c = tree.createTree(-2140,-411,0);
		c = tree.createTree(-2178,-423,0);
		c = tree.createTree(-2207,-415,0);
		c = tree.createTree(-2231,-436,2);
		c = tree.createTree(-2259,-422,0);
		c = tree.createTree(-2291,-441,1);
		c = tree.createTree(-2324,-418,1);
		c = tree.createTree(-2352,-431,0);
		c = tree.createTree(-2399,-432,0);
		c = tree.createTree(-2382,-408,2);
		c = tree.createTree(-2435,-430,2);
		c = tree.createTree(-2455,-413,2);
		c = tree.createTree(-2491,-434,2);
		c = tree.createTree(-2517,-416,2);
		c = tree.createTree(-2550,-434,1);
		c = tree.createTree(-2576,-418,2);
		c = tree.createTree(-2616,-437,1);
		c = tree.createTree(-2638,-415,0);
		c = tree.createTree(-2670,-436,1);
		c = tree.createTree(-2720,-458,1);
		c = tree.createTree(-2740,-438,1);
		c = tree.createTree(-2774,-452,2);
		c = tree.createTree(-2813,-424,0);
		c = tree.createTree(-2843,-443,2);
		c = tree.createTree(-2878,-447,0);
		c = tree.createTree(-2901,-427,1);
		c = tree.createTree(-2936,-427,1);
		c = tree.createTree(-2960,-441,2);
		c = tree.createTree(-2980,-427,2);
		c = tree.createTree(-2687,-1228,0);
		c = tree.createTree(-291,-3931,2);
		c = tree.createTree(-291,-3904,2);
		c = tree.createTree(-311,-3866,1);
		c = tree.createTree(-278,-3880,2);
		c = tree.createTree(-283,-3840,1);
		c = tree.createTree(-305,-3814,0);
		c = tree.createTree(-274,-3785,0);
		c = tree.createTree(-309,-3759,0);
		c = tree.createTree(-293,-3732,0);
		c = tree.createTree(-293,-3708,1);
		c = tree.createTree(-293,-3687,2);
		c = tree.createTree(-311,-3666,2);
		c = tree.createTree(-296,-3645,0);
		c = tree.createTree(-317,-3624,2);
		c = tree.createTree(-302,-3597,1);
		c = tree.createTree(-298,-3948,1);
		c = tree.createTree(-300,-3964,0);
		c = tree.createTree(-318,-3988,1);
		c = tree.createTree(-334,-4014,1);
		c = tree.createTree(-364,-4038,1);
		c = tree.createTree(-342,-3657,2);
		c = tree.createTree(-383,-3639,2);
		c = tree.createTree(-433,-3643,1);
		c = tree.createTree(-406,-3646,2);
		c = tree.createTree(-465,-3656,1);
		c = tree.createTree(-503,-3681,0);
		c = tree.createTree(-386,-4052,0);
		c = tree.createTree(-422,-4048,2);
		c = tree.createTree(-463,-4063,2);
		c = tree.createTree(-488,-4077,0);
		c = tree.createTree(-518,-4082,0);
		c = tree.createTree(-539,-3690,2);
		c = tree.createTree(-565,-3683,1);
		c = tree.createTree(-590,-3699,1);
		c = tree.createTree(-614,-3682,0);
		c = tree.createTree(-642,-3698,0);
		c = tree.createTree(-664,-3691,0);
		c = tree.createTree(-549,-4087,1);
		c = tree.createTree(-592,-4087,1);
		c = tree.createTree(-641,-4075,1);
		c = tree.createTree(-623,-4087,1);
		c = tree.createTree(-678,-4068,1);
	}
	
	// Flower farm
	public void createFlowerFarm()  {
		
		// Farmhouse
		c = new farmHouse(1892,-626-100,0);
		c = new bush(1811,-664,1);
		c = new bush(2101,-668,0);
		c = new bush(2085,-702,1);
		c = tree.createTree(1848,-713,0);
		c = tree.createTree(1806,-695,2);
		c = tree.createTree(1770,-677,0);
		c = tree.createTree(1721,-652,1);
		c = tree.createTree(1743,-615,2);
		c = tree.createTree(1736,-568,1);
		c = tree.createTree(1711,-552,1);
		c = tree.createTree(2071,-737,1);
		c = tree.createTree(2123,-721,2);
		c = tree.createTree(2156,-676,1);
		c = tree.createTree(2202,-651,1);
		c = tree.createTree(2202,-682,2);
		c = tree.createTree(2249,-652,0);
		c = tree.createTree(2269,-635,1);
		c = tree.createTree(2287,-602,0);
		c = tree.createTree(2309,-565,0);
		c = tree.createTree(2348,-542,0);
		c = tree.createTree(2368,-509,1);
		c = tree.createTree(2413,-532,2);
		c = tree.createTree(2424,-507,2);
		c = tree.createTree(1904,-776,0);
		c = tree.createTree(1995,-759,0);
		c = new flower(2094,-631,0);
		c = tree.createTree(1791,-540,2);
		c = tree.createTree(1765,-509,2);
		c = new flower(1712,-534,2);
		c = new flower(2224,-626,0);
		c = new flower(2258,-564,2);
		c = new bush(1660,-510,1);
		c = tree.createTree(1741,-539,1);
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
		c = tree.createTree(2849,-295,2);
		c = tree.createTree(2880,-267,0);
		c = tree.createTree(2907,-279,1);
		c = tree.createTree(2934,-261,0);

	}
	
	// Create area above flower farm.
	public void createAreaAboveFlowerFarm()  {
		
		// Well
		c = new well(2685,-1877-125,0);
		
		// Trees
		c = tree.createTree(2440,-1663,1);
		c = tree.createTree(2416,-1686,1);
		c = tree.createTree(2391,-1714,0);
		c = tree.createTree(2365,-1732,2);
		c = tree.createTree(2333,-1709,1);
		c = tree.createTree(2303,-1733,0);
		c = tree.createTree(2274,-1720,1);
		c = tree.createTree(2235,-1735,2);
		c = tree.createTree(2192,-1729,1);
		c = tree.createTree(2170,-1754,0);
		c = tree.createTree(2133,-1740,0);
		c = tree.createTree(2105,-1758,2);
		c = tree.createTree(2078,-1738,2);
		c = tree.createTree(2039,-1728,2);
		c = tree.createTree(2027,-1755,2);
		c = tree.createTree(2001,-1741,0);
		c = tree.createTree(1955,-1745,2);
		c = tree.createTree(1935,-1731,1);
		c = tree.createTree(1905,-1741,1);
		c = tree.createTree(1866,-1723,1);
		c = tree.createTree(1848,-1745,2);
		c = tree.createTree(1809,-1726,2);
		c = tree.createTree(1788,-1756,2);
		c = tree.createTree(1770,-1741,0);
		c = tree.createTree(1742,-1725,1);
		c = tree.createTree(1715,-1740,1);
		c = tree.createTree(1661,-1739,0);
		c = tree.createTree(1688,-1739,1);
		c = tree.createTree(1656,-1720,2);
		c = tree.createTree(1627,-1731,1);
		c = tree.createTree(1610,-1714,0);
		c = tree.createTree(1583,-1724,2);
		c = tree.createTree(1556,-1709,1);
		c = tree.createTree(1532,-1724,2);
		c = tree.createTree(1502,-1724,1);
		c = tree.createTree(1457,-1724,2);
		c = tree.createTree(1427,-1712,2);
		c = tree.createTree(1394,-1723,0);
		c = tree.createTree(1368,-1703,1);
		c = tree.createTree(1344,-1730,1);
		c = tree.createTree(1338,-1707,2);
		c = tree.createTree(1302,-1728,0);
		c = tree.createTree(1292,-1708,0);
		c = tree.createTree(1240,-1730,2);
		c = tree.createTree(1247,-1708,0);
		c = tree.createTree(1202,-1706,2);
		c = tree.createTree(1207,-1732,1);
		c = tree.createTree(1184,-1715,1);
		c = tree.createTree(1136,-1715,1);
		c = tree.createTree(1152,-1734,1);
		c = tree.createTree(1097,-1715,2);
		c = tree.createTree(1112,-1736,1);
		c = tree.createTree(1068,-1719,0);
		c = tree.createTree(1033,-1728,1);
		c = tree.createTree(992,-1711,2);
		c = tree.createTree(990,-1739,0);
		c = tree.createTree(963,-1715,2);
		c = tree.createTree(941,-1737,0);
		c = tree.createTree(910,-1715,2);
		c = tree.createTree(885,-1735,0);
		c = tree.createTree(868,-1712,0);
		c = tree.createTree(841,-1736,0);
		c = tree.createTree(826,-1714,0);
		c = tree.createTree(793,-1732,0);
		c = tree.createTree(777,-1710,1);
		c = tree.createTree(750,-1732,2);
		c = tree.createTree(736,-1718,1);
		c = tree.createTree(706,-1700,2);
		c = tree.createTree(687,-1716,1);
		c = tree.createTree(660,-1698,2);
		c = tree.createTree(639,-1713,1);
		c = tree.createTree(618,-1703,2);
		c = tree.createTree(572,-1715,0);
		c = tree.createTree(564,-1696,0);
		c = tree.createTree(530,-1715,1);
		c = tree.createTree(530,-1691,0);
		c = tree.createTree(487,-1690,2);
		c = tree.createTree(484,-1711,1);
		c = tree.createTree(451,-1711,1);
		c = tree.createTree(442,-1687,0);
		c = tree.createTree(409,-1687,1);
		c = tree.createTree(392,-1670,2);
		c = tree.createTree(357,-1675,0);
		c = tree.createTree(342,-1654,2);
		c = tree.createTree(315,-1675,2);
		c = tree.createTree(294,-1650,1);
		c = tree.createTree(279,-1627,0);
		c = tree.createTree(249,-1627,2);
		c = tree.createTree(210,-1609,1);
		c = tree.createTree(192,-1591,2);
		c = tree.createTree(167,-1574,2);
		c = tree.createTree(128,-1574,1);
		c = tree.createTree(97,-1549,0);
		c = tree.createTree(62,-1536,0);
		c = tree.createTree(35,-1508,0);
		c = tree.createTree(0,-1487,2);
		c = tree.createTree(67,-1512,2);
		c = tree.createTree(-20,-1475,0);
		c = tree.createTree(-47,-1454,1);
		c = tree.createTree(-74,-1454,0);
		c = tree.createTree(-101,-1440,1);
		c = tree.createTree(-136,-1428,2);
		c = tree.createTree(-162,-1403,1);
		c = tree.createTree(-183,-1367,2);
		c = tree.createTree(-186,-1385,2);
		c = tree.createTree(-212,-1337,2);
		c = tree.createTree(-222,-1353,0);
		c = tree.createTree(-244,-1325,0);
		c = tree.createTree(-283,-1313,0);
		c = tree.createTree(-283,-1283,0);
		c = tree.createTree(-307,-1283,2);
		c = tree.createTree(-321,-1242,1);
		c = tree.createTree(-310,-1262,2);
		c = tree.createTree(-248,-1300,1);
		c = tree.createTree(-342,-1217,2);
		c = tree.createTree(-364,-1190,2);
		c = tree.createTree(-404,-1170,0);
		c = tree.createTree(-381,-1144,1);
		c = tree.createTree(-402,-1119,2);
		c = tree.createTree(-366,-1155,1);
		c = tree.createTree(-435,-1110,2);
		c = tree.createTree(-421,-1075,2);
		c = tree.createTree(-439,-1085,0);
		c = tree.createTree(-452,-1053,0);
		c = tree.createTree(-452,-1001,1);
		c = tree.createTree(-457,-988,1);
		c = tree.createTree(-457,-961,1);
		c = tree.createTree(-470,-940,0);
		c = tree.createTree(-469,-913,0);
		c = tree.createTree(-483,-887,2);
		c = tree.createTree(-471,-863,0);
		c = tree.createTree(-483,-836,1);
		c = tree.createTree(-483,-806,1);
		c = tree.createTree(-483,-785,0);
		c = tree.createTree(-489,-773,1);
		c = tree.createTree(-489,-746,0);
		c = tree.createTree(-493,-724,2);
		c = tree.createTree(-493,-694,1);
		c = tree.createTree(-493,-667,2);
		c = tree.createTree(-496,-633,2);
		c = tree.createTree(-496,-603,2);
		c = tree.createTree(-496,-652,0);
		c = tree.createTree(-495,-571,1);
		c = tree.createTree(-522,-586,0);
		c = tree.createTree(-498,-532,0);
		c = tree.createTree(-510,-550,2);
		c = tree.createTree(-512,-526,1);
		c = tree.createTree(-496,-504,1);
		c = tree.createTree(-519,-475,0);
		c = tree.createTree(-524,-454,1);
		c = tree.createTree(-1086,-452,1);
		c = tree.createTree(-1086,-479,2);
		c = tree.createTree(-1062,-503,2);
		c = tree.createTree(-1084,-522,2);
		c = tree.createTree(-1069,-553,2);
		c = tree.createTree(-1095,-579,0);
		c = tree.createTree(-1069,-605,2);
		c = tree.createTree(-1088,-625,0);
		c = tree.createTree(-1102,-647,0);
		c = tree.createTree(-1077,-681,1);
		c = tree.createTree(-1094,-671,2);
		c = tree.createTree(-1094,-704,1);
		c = tree.createTree(-1082,-722,0);
		c = tree.createTree(-1103,-743,1);
		c = tree.createTree(-1079,-771,1);
		c = tree.createTree(-1105,-784,2);
		c = tree.createTree(-1081,-814,0);
		c = tree.createTree(-1105,-835,0);
		c = tree.createTree(-1082,-867,0);
		c = tree.createTree(-1105,-893,1);
		c = tree.createTree(-1088,-916,0);
		c = tree.createTree(-1117,-945,2);
		c = tree.createTree(-1097,-966,0);
		c = tree.createTree(-1077,-1003,2);
		c = tree.createTree(-1097,-1025,0);
		c = tree.createTree(-1070,-1045,0);
		c = tree.createTree(-1093,-1078,2);
		c = tree.createTree(-1064,-1107,2);
		c = tree.createTree(-1092,-1123,2);
		c = tree.createTree(-1071,-1153,1);
		c = tree.createTree(-1031,-600,1);
		c = tree.createTree(-991,-586,2);
		c = tree.createTree(-971,-594,2);
		c = tree.createTree(-928,-595,0);
		c = tree.createTree(-905,-581,2);
		c = tree.createTree(-876,-604,1);
		c = tree.createTree(-838,-581,2);
		c = tree.createTree(-805,-591,0);
		c = tree.createTree(-772,-591,1);
		c = tree.createTree(-746,-571,2);
		c = tree.createTree(-714,-592,0);
		c = tree.createTree(-690,-568,0);
		c = tree.createTree(-654,-589,1);
		c = tree.createTree(-633,-562,1);
		c = tree.createTree(-604,-583,0);
		c = tree.createTree(-582,-561,1);
		c = tree.createTree(-556,-584,1);
		c = tree.createTree(-538,-610,1);
		c = tree.createTree(-443,-618,0);
		c = tree.createTree(-1065,-1184,0);
		c = tree.createTree(-1056,-1202,2);
		c = tree.createTree(-1042,-1173,0);
		c = tree.createTree(-1025,-1228,0);
		c = tree.createTree(-1037,-1252,0);
		c = tree.createTree(-1010,-1270,0);
		c = tree.createTree(-985,-1298,0);
		c = tree.createTree(-992,-1321,0);
		c = tree.createTree(-949,-1346,2);
		c = tree.createTree(-942,-1373,2);
		c = tree.createTree(-919,-1390,0);
		c = tree.createTree(-890,-1413,0);
		c = tree.createTree(-861,-1442,0);
		c = tree.createTree(-848,-1463,2);
		c = tree.createTree(-823,-1435,2);
		c = tree.createTree(-782,-1460,2);
		c = tree.createTree(-767,-1486,1);
		c = tree.createTree(-726,-1493,0);
		c = tree.createTree(-703,-1522,0);
		c = tree.createTree(-648,-1520,2);
		c = tree.createTree(-687,-1541,1);
		c = tree.createTree(-655,-1556,0);
		c = tree.createTree(-634,-1582,0);
		c = tree.createTree(-610,-1609,2);
		c = tree.createTree(-580,-1594,1);
		c = tree.createTree(-557,-1612,2);
		c = tree.createTree(-531,-1652,2);
		c = tree.createTree(-523,-1635,0);
		c = tree.createTree(-494,-1658,2);
		c = tree.createTree(-472,-1686,2);
		c = tree.createTree(-470,-1713,2);
		c = tree.createTree(-437,-1732,2);
		c = tree.createTree(-395,-1748,1);
		c = tree.createTree(-362,-1772,2);
		c = tree.createTree(-335,-1805,0);
		c = tree.createTree(-332,-1787,0);
		c = tree.createTree(-284,-1813,0);
		c = tree.createTree(-257,-1834,1);
		c = tree.createTree(-214,-1830,0);
		c = tree.createTree(-192,-1859,0);
		c = tree.createTree(-176,-1885,1);
		c = tree.createTree(-150,-1901,0);
		c = tree.createTree(-119,-1927,2);
		c = tree.createTree(-99,-1956,0);
		c = tree.createTree(-77,-1979,0);
		c = tree.createTree(-44,-1997,1);
		c = tree.createTree(-25,-2022,1);
		c = tree.createTree(12,-2040,1);
		c = tree.createTree(62,-2058,0);
		c = tree.createTree(86,-2069,0);
		c = tree.createTree(119,-2093,2);
		c = tree.createTree(161,-2112,1);
		c = tree.createTree(183,-2096,2);
		c = tree.createTree(230,-2102,2);
		c = tree.createTree(256,-2122,1);
		c = tree.createTree(289,-2131,1);
		c = tree.createTree(318,-2160,2);
		c = tree.createTree(340,-2135,1);
		c = tree.createTree(372,-2155,1);
		c = tree.createTree(404,-2169,1);
		c = tree.createTree(431,-2190,1);
		c = tree.createTree(462,-2165,0);
		c = tree.createTree(495,-2185,1);
		c = tree.createTree(522,-2165,0);
		c = tree.createTree(555,-2185,0);
		c = tree.createTree(575,-2162,1);
		c = tree.createTree(612,-2180,0);
		c = tree.createTree(633,-2204,2);
		c = tree.createTree(655,-2186,0);
		c = tree.createTree(694,-2207,2);
		c = tree.createTree(713,-2188,0);
		c = tree.createTree(754,-2201,2);
		c = tree.createTree(776,-2183,2);
		c = tree.createTree(812,-2192,1);
		c = tree.createTree(841,-2169,1);
		c = tree.createTree(870,-2196,0);
		c = tree.createTree(906,-2166,2);
		c = tree.createTree(933,-2184,1);
		c = tree.createTree(992,-2195,2);
		c = tree.createTree(961,-2172,1);
		c = tree.createTree(1040,-2188,0);
		c = tree.createTree(1059,-2159,0);
		c = tree.createTree(1080,-2192,1);
		c = tree.createTree(1011,-2174,2);
		c = tree.createTree(1113,-2177,1);
		c = tree.createTree(1143,-2156,2);
		c = tree.createTree(1171,-2174,0);
		c = tree.createTree(1204,-2174,0);
		c = tree.createTree(1230,-2148,2);
		c = tree.createTree(1255,-2166,2);
		c = tree.createTree(1281,-2140,2);
		c = tree.createTree(1287,-2177,0);
		c = tree.createTree(1324,-2193,2);
		c = tree.createTree(1353,-2167,1);
		c = tree.createTree(1383,-2191,0);
		c = tree.createTree(1409,-2162,2);
		c = tree.createTree(1439,-2189,2);
		c = tree.createTree(1464,-2164,0);
		c = tree.createTree(1494,-2189,1);
		c = tree.createTree(1510,-2170,1);
		c = tree.createTree(1537,-2187,2);
		c = tree.createTree(1562,-2156,1);
		c = tree.createTree(1593,-2177,1);
		c = tree.createTree(1616,-2154,2);
		c = tree.createTree(1663,-2175,0);
		c = tree.createTree(1689,-2156,1);
		c = tree.createTree(1722,-2156,1);
		c = tree.createTree(1762,-2166,1);
		c = tree.createTree(1781,-2186,2);
		c = tree.createTree(1803,-2174,2);
		c = tree.createTree(1827,-2192,1);
		c = tree.createTree(1850,-2182,1);
		c = tree.createTree(1883,-2200,2);
		c = tree.createTree(1934,-2179,1);
		c = tree.createTree(1904,-2164,2);
		c = tree.createTree(1968,-2192,2);
		c = tree.createTree(1991,-2206,2);
		c = tree.createTree(2022,-2176,0);
		c = tree.createTree(2050,-2192,0);
		c = tree.createTree(2077,-2164,2);
		c = tree.createTree(2101,-2185,2);
		c = tree.createTree(2120,-2161,0);
		c = tree.createTree(2148,-2189,2);
		c = tree.createTree(2168,-2206,1);
		c = tree.createTree(2201,-2206,1);
		c = tree.createTree(2234,-2185,0);
		c = tree.createTree(2257,-2209,1);
		c = tree.createTree(2278,-2192,2);
		c = tree.createTree(2317,-2222,0);
		c = tree.createTree(2344,-2195,1);
		c = tree.createTree(2378,-2217,1);
		c = tree.createTree(2402,-2198,1);
		c = tree.createTree(2433,-2208,0);
		c = tree.createTree(2459,-2182,1);
		c = tree.createTree(2488,-2214,2);
		c = tree.createTree(2460,-2240,0);
		c = tree.createTree(2475,-2258,2);
		c = tree.createTree(2499,-2234,0);
		c = tree.createTree(2474,-2301,1);
		c = tree.createTree(2501,-2280,0);
		c = tree.createTree(2473,-2332,0);
		c = tree.createTree(2498,-2315,0);
		c = tree.createTree(2947,-1656,2);
		c = tree.createTree(2947,-1701,1);
		c = tree.createTree(2921,-1733,2);
		c = tree.createTree(2965,-1757,0);
		c = tree.createTree(2942,-1795,1);
		c = tree.createTree(2942,-1768,2);
		c = tree.createTree(2946,-1814,1);
		c = tree.createTree(2931,-1841,2);
		c = tree.createTree(2956,-1868,1);
		c = tree.createTree(2939,-1893,0);
		c = tree.createTree(2956,-1919,1);
		c = tree.createTree(2939,-1950,1);
		c = tree.createTree(2955,-1976,0);
		c = tree.createTree(2926,-2013,0);
		c = tree.createTree(2951,-1996,0);
		c = tree.createTree(2946,-2027,1);
		c = tree.createTree(2931,-2051,2);
		c = tree.createTree(2949,-2075,0);
		c = tree.createTree(2931,-2099,1);
		c = tree.createTree(2961,-2114,2);
		c = tree.createTree(2937,-2143,2);
		c = tree.createTree(2953,-2171,2);
		c = tree.createTree(2932,-2198,0);
		c = tree.createTree(2945,-2213,2);
		c = tree.createTree(2945,-2243,1);
		c = tree.createTree(2936,-2228,0);
		c = tree.createTree(2942,-2271,0);
		c = tree.createTree(2940,-2288,1);
		c = tree.createTree(2940,-2315,2);
		c = tree.createTree(2959,-2329,0);
		c = tree.createTree(2930,-2348,2);
		c = tree.createTree(2951,-2372,2);
		c = tree.createTree(2489,-2349,0);
		c = tree.createTree(-1079,-983,0);
		c = tree.createTree(2959,-605,2);
		c = tree.createTree(2930,-623,1);
		c = tree.createTree(2942,-661,2);
		c = tree.createTree(2933,-688,0);
		c = tree.createTree(2951,-728,1);
		c = tree.createTree(2924,-740,0);
		c = tree.createTree(2942,-782,2);
		c = tree.createTree(2931,-818,1);
		c = tree.createTree(2926,-852,2);
		c = tree.createTree(2921,-878,0);
		c = tree.createTree(2942,-902,2);
		c = tree.createTree(2952,-945,2);
		c = tree.createTree(2925,-932,1);
		c = tree.createTree(2942,-969,0);
		c = tree.createTree(2924,-999,0);
		c = tree.createTree(2945,-1025,0);
		c = tree.createTree(2932,-1047,2);
		c = tree.createTree(2945,-1076,2);
		c = tree.createTree(2918,-1076,1);
		c = tree.createTree(2931,-1103,1);
		c = tree.createTree(2943,-1134,0);
		c = tree.createTree(2918,-1156,2);
		c = tree.createTree(2932,-1179,1);
		c = tree.createTree(2928,-1212,2);
		c = tree.createTree(2947,-1237,2);
		c = tree.createTree(2924,-1265,0);
		c = tree.createTree(2928,-1296,2);
		c = tree.createTree(2951,-1328,0);
		c = tree.createTree(2938,-1350,0);
		c = tree.createTree(2924,-1392,0);
		c = tree.createTree(2948,-1371,1);
		c = tree.createTree(2934,-1429,0);
		c = tree.createTree(2934,-1402,0);
		c = tree.createTree(2925,-1468,1);
		c = tree.createTree(2943,-1490,2);
		c = tree.createTree(2943,-1454,2);
		c = tree.createTree(2943,-1508,0);
		c = tree.createTree(2924,-1536,1);
		c = tree.createTree(2945,-1565,1);
		c = tree.createTree(2913,-1576,0);
		c = tree.createTree(2937,-1599,0);
		c = tree.createTree(2917,-1622,0);
		c = tree.createTree(2935,-1640,2);
		c = tree.createTree(2396,-575,0);
		c = tree.createTree(2414,-599,1);
		c = tree.createTree(2382,-629,1);
		c = tree.createTree(2407,-661,2);
		c = tree.createTree(2434,-679,0);
		c = tree.createTree(2409,-699,2);
		c = tree.createTree(2434,-718,0);
		c = tree.createTree(2409,-736,1);
		c = tree.createTree(2431,-770,0);
		c = tree.createTree(2440,-791,2);
		c = tree.createTree(2421,-819,2);
		c = tree.createTree(2437,-841,2);
		c = tree.createTree(2410,-865,2);
		c = tree.createTree(2430,-894,1);
		c = tree.createTree(2411,-919,1);
		c = tree.createTree(2440,-944,2);
		c = tree.createTree(2414,-967,1);
		c = tree.createTree(2430,-989,2);
		c = tree.createTree(2409,-1014,0);
		c = tree.createTree(2427,-1035,0);
		c = tree.createTree(2434,-1060,2);
		c = tree.createTree(2428,-1090,0);
		c = tree.createTree(2409,-1106,2);
		c = tree.createTree(2427,-1127,0);
		c = tree.createTree(2409,-1167,1);
		c = tree.createTree(2435,-1184,2);
		c = tree.createTree(2435,-1160,0);
		c = tree.createTree(2423,-1199,0);
		c = tree.createTree(2417,-1226,2);
		c = tree.createTree(2438,-1244,0);
		c = tree.createTree(2417,-1264,0);
		c = tree.createTree(2439,-1289,2);
		c = tree.createTree(2417,-1311,2);
		c = tree.createTree(2441,-1333,0);
		c = tree.createTree(2422,-1349,2);
		c = tree.createTree(2449,-1379,1);
		c = tree.createTree(2413,-1364,1);
		c = tree.createTree(2431,-1403,2);
		c = tree.createTree(2427,-1417,1);
		c = tree.createTree(2440,-1449,0);
		c = tree.createTree(2419,-1434,0);
		c = tree.createTree(2436,-1481,1);
		c = tree.createTree(2415,-1463,0);
		c = tree.createTree(2415,-1511,1);
		c = tree.createTree(2430,-1535,2);
		c = tree.createTree(2411,-1561,0);
		c = tree.createTree(2431,-1593,1);
		c = tree.createTree(2431,-1569,0);
		c = tree.createTree(2437,-1607,0);
		c = tree.createTree(2422,-1622,2);
		c = tree.createTree(2453,-1647,1);
		c = tree.createTree(2419,-1143,0);
		c = tree.createTree(2412,-752,2);
		c = tree.createTree(2398,-556,2);
		c = tree.createTree(2939,-801,1);
		c = tree.createTree(2950,-757,1);
		c = tree.createTree(2930,-638,2);
		c = tree.createTree(2936,-705,2);
		c = tree.createTree(2924,-834,1);
	}
	
	// Forest area above spawn.
	public void createForestAboveSpawn()  {
		
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
		spawnWaterRect(-2128-1000,-2900-1000-12,-2000-1000,-500);
		
		// Spawn river to right of mountain
		spawnWaterRect(2000+1000-16,-4466+18-1000,2128+1000,200);
		
		// Cave to top left.
		caveEnterance spiderCaveEnterance = new caveEnterance(-1762+20+30,-4070+6+14-1000,0, spiderCave.getZone(),100,-6,"Right");
		
		// Bottle
		item b = new normalBottle(15,-862);
		
		// Well.
		c = new well(1142,-1353,0);
		
		// Trees and stuff (Generated by map maker)
		 c = tree.createTree(-1067,-436,1);
		 c = tree.createTree(-1088,-419,1);
		 c = tree.createTree(-1073,-402,2);
		 c = tree.createTree(-1094,-384,2);
		 c = tree.createTree(-1067,-369,0);
		 c = tree.createTree(-1092,-338,1);
		 c = tree.createTree(-1080,-314,0);
		 c = tree.createTree(-1096,-292,0);
		 c = tree.createTree(-1076,-271,0);
		 c = tree.createTree(-1103,-238,1);
		 c = tree.createTree(-1106,-257,2);
		 c = tree.createTree(-1115,-358,1);
		 c = tree.createTree(-1106,-198,0);
		 c = tree.createTree(-1141,-222,1);
		 c = tree.createTree(-1099,-171,0);
		 c = tree.createTree(-1120,-141,0);
		 c = tree.createTree(-1147,-164,0);
		 c = tree.createTree(-1112,-120,2);
		 c = tree.createTree(-1130,-96,2);
		 c = tree.createTree(-1097,-72,1);
		 c = tree.createTree(-1097,-42,0);
		 c = tree.createTree(-1139,-60,0);
		 c = tree.createTree(-1139,-9,2);
		 c = tree.createTree(-1092,-14,0);
		 c = tree.createTree(-1092,15,2);
		 c = tree.createTree(-1077,30,1);
		 c = tree.createTree(471,-439,1);
		 c = tree.createTree(498,-427,2);
		 c = tree.createTree(474,-388,2);
		 c = tree.createTree(504,-397,0);
		 c = tree.createTree(504,-358,1);
		 c = tree.createTree(489,-340,1);
		 c = tree.createTree(483,-316,0);
		 c = tree.createTree(510,-298,1);
		 c = tree.createTree(529,-264,2);
		 c = tree.createTree(490,-237,0);
		 c = tree.createTree(514,-209,0);
		 c = tree.createTree(481,-185,1);
		 c = tree.createTree(510,-157,2);
		 c = tree.createTree(526,-120,0);
		 c = tree.createTree(493,-96,2);
		 c = tree.createTree(521,-82,2);
		 c = tree.createTree(521,-52,2);
		 c = tree.createTree(488,-25,0);
		 c = tree.createTree(518,7,1);
		 c = tree.createTree(485,28,2);
		 c = tree.createTree(528,-137,1);
		 c = tree.createTree(523,-281,2);
		 c = tree.createTree(-311,-457,1);
		 c = tree.createTree(-311,-481,1);
		 c = tree.createTree(-323,-499,1);
		 c = tree.createTree(-307,-518,0);
		 c = tree.createTree(-328,-545,1);
		 c = tree.createTree(-313,-571,1);
		 c = tree.createTree(-338,-592,1);
		 c = tree.createTree(-295,-611,1);
		 c = tree.createTree(-315,-630,2);
		 c = tree.createTree(-296,-651,0);
		 c = tree.createTree(-307,-674,0);
		 c = tree.createTree(-280,-695,0);
		 c = tree.createTree(-309,-708,1);
		 c = tree.createTree(-318,-729,2);
		 c = tree.createTree(-336,-747,0);
		 c = tree.createTree(-318,-767,1);
		 c = tree.createTree(-306,-794,1);
		 c = tree.createTree(-335,-817,0);
		 c = tree.createTree(-305,-836,1);
		 c = tree.createTree(-335,-856,1);
		 c = tree.createTree(-304,-872,1);
		 c = tree.createTree(282,-457,0);
		 c = tree.createTree(266,-475,1);
		 c = tree.createTree(287,-496,0);
		 c = tree.createTree(268,-518,0);
		 c = tree.createTree(300,-529,1);
		 c = tree.createTree(267,-550,2);
		 c = tree.createTree(285,-568,2);
		 c = tree.createTree(276,-589,2);
		 c = tree.createTree(276,-610,1);
		 c = tree.createTree(269,-629,0);
		 c = tree.createTree(269,-653,0);
		 c = tree.createTree(290,-667,2);
		 c = tree.createTree(272,-688,2);
		 c = tree.createTree(272,-715,2);
		 c = tree.createTree(257,-742,2);
		 c = tree.createTree(279,-772,0);
		 c = tree.createTree(287,-750,2);
		 c = tree.createTree(265,-789,2);
		 c = tree.createTree(284,-813,0);
		 c = tree.createTree(270,-846,1);
		 c = tree.createTree(276,-831,2);
		 c = tree.createTree(282,-864,2);
		 c = tree.createTree(-308,-888,1);
		 c = tree.createTree(-292,-922,2);
		 c = tree.createTree(-277,-955,0);
		 c = tree.createTree(-281,-978,0);
		 c = tree.createTree(-265,-1000,2);
		 c = tree.createTree(-241,-1019,2);
		 c = tree.createTree(-250,-1034,1);
		 c = tree.createTree(-240,-1053,0);
		 c = tree.createTree(-215,-1075,2);
		 c = tree.createTree(-214,-1095,2);
		 c = tree.createTree(-187,-1122,1);
		 c = tree.createTree(-187,-1104,2);
		 c = tree.createTree(-183,-1141,2);
		 c = tree.createTree(-162,-1159,1);
		 c = tree.createTree(-149,-1180,2);
		 c = tree.createTree(-118,-1197,1);
		 c = tree.createTree(-103,-1224,1);
		 c = tree.createTree(-96,-1208,1);
		 c = tree.createTree(-94,-1240,2);
		 c = tree.createTree(-76,-1264,1);
		 c = tree.createTree(-50,-1290,1);
		 c = tree.createTree(-17,-1311,1);
		 c = tree.createTree(282,-880,1);
		 c = tree.createTree(316,-923,1);
		 c = tree.createTree(335,-947,2);
		 c = tree.createTree(355,-971,2);
		 c = tree.createTree(371,-982,0);
		 c = tree.createTree(391,-1002,2);
		 c = tree.createTree(420,-1003,0);
		 c = tree.createTree(439,-1027,1);
		 c = tree.createTree(460,-1015,1);
		 c = tree.createTree(481,-1039,0);
		 c = tree.createTree(513,-1059,0);
		 c = tree.createTree(546,-1035,1);
		 c = tree.createTree(540,-1056,0);
		 c = tree.createTree(19,-1324,2);
		 c = tree.createTree(41,-1346,1);
		 c = tree.createTree(74,-1361,0);
		 c = tree.createTree(106,-1367,1);
		 c = tree.createTree(133,-1388,1);
		 c = tree.createTree(164,-1366,0);
		 c = tree.createTree(174,-1390,1);
		 c = tree.createTree(201,-1411,2);
		 c = tree.createTree(238,-1420,0);
		 c = tree.createTree(265,-1402,2);
		 c = tree.createTree(297,-1422,0);
		 c = tree.createTree(326,-1438,0);
		 c = tree.createTree(358,-1459,1);
		 c = tree.createTree(403,-1460,1);
		 c = tree.createTree(379,-1445,1);
		 c = tree.createTree(590,-1044,1);
		 c = tree.createTree(602,-1044,0);
		 c = tree.createTree(632,-1044,1);
		 c = tree.createTree(654,-1051,0);
		 c = tree.createTree(683,-1071,2);
		 c = tree.createTree(700,-1054,2);
		 c = tree.createTree(739,-1028,0);
		 c = tree.createTree(778,-1043,1);
		 c = tree.createTree(697,-1025,0);
		 c = tree.createTree(438,-1470,2);
		 c = tree.createTree(478,-1471,2);
		 c = tree.createTree(512,-1480,0);
		 c = tree.createTree(532,-1465,0);
		 c = tree.createTree(556,-1483,0);
		 c = tree.createTree(583,-1459,0);
		 c = tree.createTree(627,-1473,1);
		 c = tree.createTree(612,-1458,2);
		 c = tree.createTree(657,-1479,2);
		 c = tree.createTree(678,-1458,0);
		 c = tree.createTree(714,-1510,2);
		 c = tree.createTree(699,-1489,0);
		 c = tree.createTree(741,-1489,0);
		 c = tree.createTree(756,-1504,2);
		 c = tree.createTree(797,-1484,1);
		 c = tree.createTree(795,-1500,2);
		 c = tree.createTree(849,-1500,0);
		 c = tree.createTree(834,-1485,0);
		 c = tree.createTree(894,-1497,2);
		 c = tree.createTree(876,-1479,2);
		 c = tree.createTree(918,-1512,0);
		 c = tree.createTree(939,-1500,0);
		 c = tree.createTree(987,-1500,1);
		 c = tree.createTree(1023,-1516,2);
		 c = tree.createTree(817,-1056,0);
		 c = tree.createTree(848,-1075,2);
		 c = tree.createTree(886,-1057,0);
		 c = tree.createTree(918,-1068,1);
		 c = tree.createTree(1059,-1510,0);
		 c = tree.createTree(1096,-1507,2);
		 c = tree.createTree(1121,-1521,0);
		 c = tree.createTree(1156,-1506,2);
		 c = tree.createTree(1195,-1506,2);
		 c = tree.createTree(1224,-1494,1);
		 c = tree.createTree(1262,-1477,0);
		 c = tree.createTree(1296,-1493,1);
		 c = tree.createTree(1319,-1468,2);
		 c = tree.createTree(1366,-1450,0);
		 c = tree.createTree(1352,-1468,2);
		 c = tree.createTree(1391,-1426,1);
		 c = tree.createTree(1414,-1394,0);
		 c = tree.createTree(1441,-1412,2);
		 c = tree.createTree(1422,-1377,1);
		 c = tree.createTree(1420,-1429,2);
		 c = tree.createTree(1465,-1371,0);
		 c = tree.createTree(1447,-1347,0);
		 c = tree.createTree(1476,-1333,2);
		 c = tree.createTree(961,-1084,0);
		 c = tree.createTree(961,-1063,0);
		 c = tree.createTree(1022,-1077,0);
		 c = tree.createTree(1004,-1065,1);
		 c = tree.createTree(1043,-1063,1);
		 c = tree.createTree(1065,-1040,2);
		 c = tree.createTree(1101,-1050,0);
		 c = tree.createTree(1127,-1024,1);
		 c = tree.createTree(1118,-1005,2);
		 c = tree.createTree(1157,-978,2);
		 c = tree.createTree(1147,-992,1);
		 c = tree.createTree(1521,-1321,2);
		 c = tree.createTree(1524,-1306,1);
		 c = tree.createTree(1488,-1318,1);
		 c = tree.createTree(1559,-1309,2);
		 c = tree.createTree(1581,-1287,1);
		 c = tree.createTree(1547,-1282,0);
		 c = tree.createTree(1572,-1240,2);
		 c = tree.createTree(1565,-1260,2);
		 c = tree.createTree(1586,-1210,1);
		 c = tree.createTree(1586,-1188,2);
		 c = tree.createTree(1604,-1165,0);
		 c = tree.createTree(1591,-1143,0);
		 c = tree.createTree(1153,-963,1);
		 c = tree.createTree(1162,-944,2);
		 c = tree.createTree(1144,-924,1);
		 c = tree.createTree(1168,-903,2);
		 c = tree.createTree(1136,-882,1);
		 c = tree.createTree(1166,-858,2);
		 c = tree.createTree(1131,-844,1);
		 c = tree.createTree(1169,-827,0);
		 c = tree.createTree(1139,-804,0);
		 c = tree.createTree(1161,-785,0);
		 c = tree.createTree(1161,-755,1);
		 c = tree.createTree(1137,-725,1);
		 c = tree.createTree(1606,-1124,1);
		 c = tree.createTree(1606,-1124,2);
		 c = tree.createTree(1617,-1101,1);
		 c = tree.createTree(1602,-1069,1);
		 c = tree.createTree(1627,-1088,1);
		 c = tree.createTree(1616,-1038,0);
		 c = tree.createTree(1620,-1057,0);
		 c = tree.createTree(1606,-1008,1);
		 c = tree.createTree(1621,-1017,2);
		 c = tree.createTree(1618,-987,1);
		 c = tree.createTree(1606,-957,0);
		 c = tree.createTree(1631,-925,2);
		 c = tree.createTree(1641,-971,2);
		 c = tree.createTree(1629,-947,0);
		 c = tree.createTree(1598,-896,2);
		 c = tree.createTree(1634,-890,0);
		 c = tree.createTree(1120,-703,1);
		 c = tree.createTree(1103,-678,0);
		 c = tree.createTree(1132,-683,1);
		 c = tree.createTree(1131,-656,0);
		 c = tree.createTree(1114,-630,2);
		 c = tree.createTree(1113,-601,0);
		 c = tree.createTree(1132,-615,1);
		 c = tree.createTree(1615,-859,0);
		 c = tree.createTree(1636,-841,2);
		 c = tree.createTree(1621,-822,1);
		 c = tree.createTree(1647,-862,0);
		 c = tree.createTree(1620,-787,2);
		 c = tree.createTree(1659,-783,1);
		 c = tree.createTree(1650,-798,2);
		 c = tree.createTree(1629,-743,1);
		 c = tree.createTree(1642,-762,0);
		 c = tree.createTree(1653,-730,2);
		 c = tree.createTree(1636,-707,2);
		 c = tree.createTree(1658,-688,1);
		 c = tree.createTree(1631,-661,1);
		 c = tree.createTree(1122,-572,2);
		 c = tree.createTree(1123,-484,1);
		 c = tree.createTree(1123,-511,1);
		 c = tree.createTree(1108,-520,2);
		 c = tree.createTree(1117,-536,0);
		 c = tree.createTree(1131,-556,0);
		 c = tree.createTree(1650,-637,0);
		 c = tree.createTree(1650,-604,0);
		 c = tree.createTree(1626,-625,2);
		 c = tree.createTree(1644,-588,2);
		 c = tree.createTree(1623,-561,2);
		 c = tree.createTree(1656,-540,2);
		 c = tree.createTree(1630,-514,2);
		 c = tree.createTree(1123,-466,1);
		 c = tree.createTree(1123,-430,1);
		 c = tree.createTree(1126,-443,2);
		 c = tree.createTree(1093,-416,0);
		 c = tree.createTree(1113,-398,2);
		 c = tree.createTree(1112,-369,0);
		 c = tree.createTree(1096,-335,2);
		 c = tree.createTree(1114,-387,2);
		 c = tree.createTree(1108,-347,0);
		 c = tree.createTree(1120,-315,2);
		 c = tree.createTree(1104,-295,2);
		 c = tree.createTree(1116,-271,2);
		 c = tree.createTree(1109,-237,2);
		 c = tree.createTree(1114,-252,1);
		 c = tree.createTree(1114,-209,1);
		 c = tree.createTree(1105,-218,0);
		 c = tree.createTree(1639,-525,0);
		 c = tree.createTree(1105,-189,1);
		 c = tree.createTree(1121,-170,2);
		 c = tree.createTree(1153,-150,1);
		 c = tree.createTree(1144,-163,1);
		 c = tree.createTree(1161,-129,0);
		 c = tree.createTree(1183,-139,0);
		 c = tree.createTree(1203,-116,1);
		 c = tree.createTree(1211,-89,2);
		 c = tree.createTree(1229,-98,0);
		 c = tree.createTree(1235,-69,2);
		 c = tree.createTree(1263,-60,0);
		 c = tree.createTree(1282,-39,1);
		 c = tree.createTree(1298,-58,2);
		 c = tree.createTree(1304,-40,0);
		 c = tree.createTree(1315,-32,1);
		 c = tree.createTree(1309,-14,1);
		 c = tree.createTree(1289,5,1);
		 c = tree.createTree(1307,23,1);
		 c = tree.createTree(1321,37,0);
		 c = tree.createTree(1600,-61,2);
		 c = tree.createTree(1576,-52,1);
		 c = tree.createTree(1543,-60,2);
		 c = tree.createTree(1517,-46,0);
		 c = tree.createTree(1483,-59,2);
		 c = tree.createTree(1440,-40,0);
		 c = tree.createTree(1404,-54,2);
		 c = tree.createTree(1353,-39,0);
		 c = tree.createTree(1368,-54,0);
		 c = tree.createTree(308,-896,2);
		
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
	
	public void createGraveYard()  {
		
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
		c = tree.createTree(818,-3104,1);
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
		c = tree.createTree(1156,-2532,2);
		c = tree.createTree(1162,-2511,0);
		c = tree.createTree(1143,-2497,1);
		c = tree.createTree(1159,-2466,2);
		c = tree.createTree(1134,-2449,0);
		c = tree.createTree(1158,-2428,2);
		c = tree.createTree(1128,-2410,0);
		c = tree.createTree(1144,-2381,0);
		c = tree.createTree(1017,-3099,0);
		c = tree.createTree(892,-2872,1);
		c = tree.createTree(1001,-2741,0);
		c = tree.createTree(827,-2651,0);
		c = tree.createTree(661,-2586,2);
		c = tree.createTree(596,-2758,0);
		c = tree.createTree(488,-2656,1);
		c = tree.createTree(349,-2867,0);
		c = tree.createTree(322,-2611,2);
		c = tree.createTree(595,-3052,2);
		c = tree.createTree(409,-3166,1);
		c = tree.createTree(732,-3228,2);
		c = tree.createTree(898,-3316,1);
		c = tree.createTree(1039,-3424,0);
		c = tree.createTree(1004,-3593,2);
		c = tree.createTree(743,-3616,1);
		c = tree.createTree(674,-3501,0);
		c = tree.createTree(462,-3594,1);
		c = tree.createTree(338,-3508,0);
		c = tree.createTree(206,-3589,0);
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
		c = tree.createTree(1166,-3176,1);
		c = tree.createTree(1181,-3197,1);
		c = tree.createTree(1166,-3221,2);
		c = tree.createTree(1189,-3242,2);
		c = tree.createTree(1168,-3278,1);
		c = tree.createTree(1192,-3262,1);
		c = tree.createTree(1191,-3304,1);
		c = tree.createTree(1193,-3331,1);
		c = tree.createTree(1191,-3356,2);
		c = tree.createTree(1203,-3383,0);
		c = tree.createTree(1178,-3403,2);
		c = tree.createTree(1204,-3426,2);
		c = tree.createTree(1184,-3462,0);
		c = tree.createTree(1206,-3444,0);
		c = tree.createTree(1210,-3478,2);
		c = tree.createTree(1194,-3503,0);
		c = tree.createTree(1213,-3525,2);
		c = tree.createTree(1191,-3546,2);
		c = tree.createTree(1218,-3567,1);
		c = tree.createTree(1197,-3595,1);
		c = tree.createTree(1220,-3621,1);
		c = tree.createTree(1243,-3661,2);
		c = tree.createTree(1219,-3643,1);
		c = tree.createTree(1215,-3693,1);
		c = tree.createTree(1248,-3672,2);
		
		// Trees
		c = tree.createTree(483,-4060,1);
		c = tree.createTree(460,-4070,1);
		c = tree.createTree(444,-4062,1);
		c = tree.createTree(417,-4062,2);
		c = tree.createTree(393,-4062,1);
		c = tree.createTree(351,-4057,1);
		c = tree.createTree(321,-4057,0);
		c = tree.createTree(273,-4075,1);
		c = tree.createTree(240,-4075,2);
		c = tree.createTree(225,-4045,2);
		c = tree.createTree(276,-4043,2);
		c = tree.createTree(340,-4041,1);
		c = tree.createTree(432,-4126,0);
		c = tree.createTree(251,-4126,0);
		c = tree.createTree(205,-4061,2);
		c = tree.createTree(1254,-3707,1);
		c = tree.createTree(1275,-3707,1);
		c = tree.createTree(1301,-3696,1);
		c = tree.createTree(1316,-3676,1);
		c = tree.createTree(1357,-3670,2);
		c = tree.createTree(1383,-3645,1);
		c = tree.createTree(1419,-3645,1);
		c = tree.createTree(1413,-3622,0);
		c = tree.createTree(1464,-3652,2);
		c = tree.createTree(1454,-3626,1);
		c = tree.createTree(1498,-3647,0);
		c = tree.createTree(1507,-3638,1);
		c = tree.createTree(1537,-3635,2);
		c = tree.createTree(1563,-3655,2);
		c = tree.createTree(1599,-3670,0);
		c = tree.createTree(1622,-3647,0);
		c = tree.createTree(1647,-3651,1);
		c = tree.createTree(1671,-3669,0);
		c = tree.createTree(1695,-3660,1);
		c = tree.createTree(1737,-3666,1);
		c = tree.createTree(1768,-3650,0);
		c = tree.createTree(1792,-3672,2);
		c = tree.createTree(1816,-3648,1);
		c = tree.createTree(1861,-3667,2);
		c = tree.createTree(1837,-3646,0);
		c = tree.createTree(1897,-3672,2);
		c = tree.createTree(1900,-3647,0);
		c = tree.createTree(1936,-3632,1);
		c = tree.createTree(1966,-3614,0);
		c = tree.createTree(1991,-3592,0);
		c = tree.createTree(2024,-3592,2);
		c = tree.createTree(2055,-3569,2);
		c = tree.createTree(2081,-3543,2);
		c = tree.createTree(2106,-3563,2);
		c = tree.createTree(2130,-3533,0);
		c = tree.createTree(2152,-3511,0);
		c = tree.createTree(2182,-3532,0);
		c = tree.createTree(2192,-3507,2);
		c = tree.createTree(2213,-3474,0);
		c = tree.createTree(2232,-3481,0);
		c = tree.createTree(2251,-3500,1);
		c = tree.createTree(2261,-3478,2);
		c = tree.createTree(2309,-3505,1);
		c = tree.createTree(2300,-3484,2);
		c = tree.createTree(2351,-3526,1);
		c = tree.createTree(2351,-3511,1);
		c = tree.createTree(2367,-3539,1);
		c = tree.createTree(2387,-3559,0);
		c = tree.createTree(2396,-3580,1);
		c = tree.createTree(2412,-3601,1);
		c = tree.createTree(2432,-3610,0);
		c = tree.createTree(2432,-3634,1);
		c = tree.createTree(2444,-3654,1);
		c = tree.createTree(2464,-3666,1);
		c = tree.createTree(2461,-3693,1);
		c = tree.createTree(2485,-3714,2);
		c = tree.createTree(2498,-3739,1);
		c = tree.createTree(2480,-3755,0);
		c = tree.createTree(2498,-3778,2);
		c = tree.createTree(2487,-3795,2);
		c = tree.createTree(2506,-3815,2);
		c = tree.createTree(2476,-3836,1);
		c = tree.createTree(2498,-3861,0);
		c = tree.createTree(2515,-3872,2);
		c = tree.createTree(2498,-3894,2);
		c = tree.createTree(2520,-3892,2);
		c = tree.createTree(2520,-3910,2);
		c = tree.createTree(2496,-3930,2);
		c = tree.createTree(2512,-3958,1);
		c = tree.createTree(2529,-3932,1);
		c = tree.createTree(2532,-3989,2);
		c = tree.createTree(2532,-3983,1);
		c = tree.createTree(2923,-2561,1);
		c = tree.createTree(2951,-2545,1);
		c = tree.createTree(2956,-2513,0);
		c = tree.createTree(2938,-2477,0);
		c = tree.createTree(2950,-2441,0);
		c = tree.createTree(2923,-2422,1);
		c = tree.createTree(2406,-2402,1);
		c = tree.createTree(2397,-2435,0);
		c = tree.createTree(2370,-2459,1);
		c = tree.createTree(2397,-2495,1);
		c = tree.createTree(2445,-2534,2);
		c = tree.createTree(2461,-2509,1);
		c = tree.createTree(2482,-2476,2);
		c = tree.createTree(2450,-2462,0);
		c = tree.createTree(2465,-2420,2);
		c = tree.createTree(2492,-2366,1);
		c = tree.createTree(2456,-2366,0);
		c = tree.createTree(2417,-2375,2);
		c = tree.createTree(2375,-2375,2);
		c = tree.createTree(2348,-2375,1);
		c = tree.createTree(2312,-2381,1);
		c = tree.createTree(2286,-2395,2);
		c = tree.createTree(2258,-2397,2);
		c = tree.createTree(2231,-2385,2);
		c = tree.createTree(2187,-2386,2);
		c = tree.createTree(2154,-2386,2);
		c = tree.createTree(2124,-2386,1);
		c = tree.createTree(2091,-2386,0);
		c = tree.createTree(2091,-2398,0);
		c = tree.createTree(2064,-2398,2);
		c = tree.createTree(2026,-2393,1);
		c = tree.createTree(1993,-2390,0);
		c = tree.createTree(1968,-2390,2);
		c = tree.createTree(1920,-2399,1);
		c = tree.createTree(1884,-2411,2);
		c = tree.createTree(1856,-2395,1);
		c = tree.createTree(1810,-2387,2);
		c = tree.createTree(1820,-2407,0);
		c = tree.createTree(1781,-2383,0);
		c = tree.createTree(1767,-2391,0);
		c = tree.createTree(1732,-2392,2);
		c = tree.createTree(1709,-2406,1);
		c = tree.createTree(1682,-2391,2);
		c = tree.createTree(1653,-2409,0);
		c = tree.createTree(1625,-2399,1);
		c = tree.createTree(1587,-2419,2);
		c = tree.createTree(1539,-2398,1);
		c = tree.createTree(1509,-2390,0);
		c = tree.createTree(1474,-2376,2);
		c = tree.createTree(1447,-2388,1);
		c = tree.createTree(1424,-2365,2);
		c = tree.createTree(1389,-2380,1);
		c = tree.createTree(1362,-2380,1);
		c = tree.createTree(1338,-2359,0);
		c = tree.createTree(1296,-2382,2);
		c = tree.createTree(1258,-2370,1);
		c = tree.createTree(1216,-2370,0);
		c = tree.createTree(1189,-2370,0);
		c = tree.createTree(1159,-2388,2);
		c = tree.createTree(1127,-2365,1);
		c = tree.createTree(1085,-2365,2);
		c = tree.createTree(1054,-2375,2);
		c = tree.createTree(1026,-2362,0);
		c = tree.createTree(993,-2386,0);
		c = tree.createTree(965,-2364,2);
		c = tree.createTree(937,-2383,2);
		c = tree.createTree(905,-2366,2);
		c = tree.createTree(875,-2380,0);
		c = tree.createTree(842,-2359,0);
		c = tree.createTree(816,-2381,0);
		c = tree.createTree(789,-2365,1);
		c = tree.createTree(762,-2383,1);
		c = tree.createTree(732,-2369,1);
		c = tree.createTree(702,-2381,2);
		c = tree.createTree(669,-2366,0);
		c = tree.createTree(643,-2380,2);
		c = tree.createTree(618,-2365,0);
		c = tree.createTree(589,-2378,1);
		c = tree.createTree(562,-2366,1);
		c = tree.createTree(523,-2384,1);
		c = tree.createTree(493,-2375,2);
		c = tree.createTree(461,-2381,0);
		c = tree.createTree(434,-2381,1);
		c = tree.createTree(404,-2383,0);
		c = tree.createTree(377,-2383,0);
		c = tree.createTree(335,-2356,2);
		c = tree.createTree(335,-2389,0);
		c = tree.createTree(308,-2374,1);
		c = tree.createTree(263,-2359,2);
		c = tree.createTree(263,-2383,1);
		c = tree.createTree(234,-2367,1);
		c = tree.createTree(195,-2367,2);
		c = tree.createTree(174,-2382,1);
		c = tree.createTree(156,-2406,2);
		c = tree.createTree(145,-2430,2);
		c = tree.createTree(148,-2451,0);
		c = tree.createTree(134,-2469,2);
		c = tree.createTree(167,-2494,2);
		c = tree.createTree(146,-2515,2);
		c = tree.createTree(158,-2536,1);
		c = tree.createTree(132,-2553,1);
		c = tree.createTree(158,-2570,1);
		c = tree.createTree(121,-2583,0);
		c = tree.createTree(147,-2597,0);
		c = tree.createTree(122,-2616,0);
		c = tree.createTree(152,-2634,0);
		c = tree.createTree(128,-2649,0);
		c = tree.createTree(158,-2670,2);
		c = tree.createTree(134,-2694,2);
		c = tree.createTree(120,-2713,1);
		c = tree.createTree(137,-2734,2);
		c = tree.createTree(128,-2755,1);
		c = tree.createTree(146,-2773,1);
		c = tree.createTree(128,-2800,0);
		c = tree.createTree(154,-2814,1);
		c = tree.createTree(133,-2838,0);
		c = tree.createTree(154,-2861,1);
		c = tree.createTree(2954,-3978,2);
		c = tree.createTree(2922,-3983,2);
		c = tree.createTree(2880,-3990,0);
		c = tree.createTree(2840,-3983,0);
		c = tree.createTree(2815,-4007,1);
		c = tree.createTree(2772,-3988,0);
		c = tree.createTree(2744,-3995,2);
		c = tree.createTree(2713,-4006,1);
		c = tree.createTree(2685,-3986,0);
		c = tree.createTree(2658,-4007,0);
		c = tree.createTree(2605,-3990,1);
		c = tree.createTree(2622,-4025,2);
		c = tree.createTree(2595,-4010,2);
		c = tree.createTree(2559,-4004,2);
		c = tree.createTree(2534,-4024,0);
		c = tree.createTree(2522,-4006,0);
		c = tree.createTree(2491,-4022,1);
		c = tree.createTree(2477,-4005,2);
		c = tree.createTree(2455,-4019,1);
		c = tree.createTree(2432,-4003,1);
		c = tree.createTree(2405,-4025,2);
		c = tree.createTree(2383,-4002,0);
		c = tree.createTree(2355,-4019,1);
		c = tree.createTree(2341,-3999,2);
		c = tree.createTree(2299,-4017,2);
		c = tree.createTree(2262,-4018,0);
		c = tree.createTree(2294,-3998,0);
		c = tree.createTree(2240,-4028,0);
		c = tree.createTree(2216,-4016,1);
		c = tree.createTree(2184,-4029,0);
		c = tree.createTree(2150,-4011,2);
		c = tree.createTree(2130,-4028,1);
		c = tree.createTree(2112,-4013,2);
		c = tree.createTree(2076,-4028,0);
		c = tree.createTree(2057,-4009,0);
		c = tree.createTree(2020,-4025,2);
		c = tree.createTree(2020,-4004,0);
		c = tree.createTree(1978,-4019,1);
		c = tree.createTree(1960,-4019,1);
		c = tree.createTree(1921,-4019,1);
		c = tree.createTree(1882,-4013,2);
		c = tree.createTree(1859,-4033,0);
		c = tree.createTree(1823,-4009,1);
		c = tree.createTree(1797,-4026,0);
		c = tree.createTree(1777,-4003,0);
		c = tree.createTree(1737,-4021,1);
		c = tree.createTree(1716,-4030,0);
		c = tree.createTree(1680,-4030,2);
		c = tree.createTree(1659,-4048,1);
		c = tree.createTree(1634,-4024,1);
		c = tree.createTree(1619,-4034,0);
		c = tree.createTree(161,-2884,0);
		c = tree.createTree(128,-2900,1);
		c = tree.createTree(151,-2924,1);
		c = tree.createTree(136,-2945,1);
		c = tree.createTree(152,-2961,0);
		c = tree.createTree(125,-2985,2);
		c = tree.createTree(144,-3012,2);
		c = tree.createTree(117,-3024,2);
		c = tree.createTree(134,-3041,2);
		c = tree.createTree(160,-3060,2);
		c = tree.createTree(138,-3085,2);
		c = tree.createTree(157,-3107,0);
		c = tree.createTree(134,-3120,2);
		c = tree.createTree(151,-3143,1);
		c = tree.createTree(171,-3178,2);
		c = tree.createTree(144,-3157,2);
		c = tree.createTree(138,-3203,1);
		c = tree.createTree(159,-3227,2);
		c = tree.createTree(164,-3249,2);
		c = tree.createTree(137,-3264,1);
		c = tree.createTree(155,-3288,2);
		c = tree.createTree(127,-3311,0);
		c = tree.createTree(164,-3327,1);
		c = tree.createTree(145,-3349,1);
		c = tree.createTree(160,-3369,2);
		c = tree.createTree(170,-3395,2);
		c = tree.createTree(146,-3422,1);
		c = tree.createTree(162,-3440,2);
		c = tree.createTree(148,-3463,0);
		c = tree.createTree(166,-3487,1);
		c = tree.createTree(190,-3499,2);
		c = tree.createTree(153,-3521,0);
		c = tree.createTree(177,-3551,2);
		c = tree.createTree(153,-3575,1);
		c = tree.createTree(184,-3522,2);
		c = tree.createTree(167,-3596,2);
		c = tree.createTree(189,-3624,1);
		c = tree.createTree(153,-3641,2);
		c = tree.createTree(153,-3611,1);
		c = tree.createTree(180,-3650,0);
		c = tree.createTree(167,-3664,0);
		c = tree.createTree(149,-3673,2);
		c = tree.createTree(149,-3697,0);
		c = tree.createTree(167,-3716,2);
		c = tree.createTree(147,-3737,2);
		c = tree.createTree(169,-3756,0);
		c = tree.createTree(149,-3770,0);
		c = tree.createTree(165,-3792,1);
		c = tree.createTree(146,-3814,2);
		c = tree.createTree(170,-3837,2);
		c = tree.createTree(149,-3859,0);
		c = tree.createTree(167,-3889,1);
		c = tree.createTree(149,-3907,1);
		c = tree.createTree(173,-3931,1);
		c = tree.createTree(174,-3878,0);
		c = tree.createTree(1589,-4028,0);
		c = tree.createTree(1556,-4040,0);
		c = tree.createTree(1530,-4026,1);
		c = tree.createTree(1513,-4037,0);
		c = tree.createTree(1477,-4035,2);
		c = tree.createTree(1433,-4050,2);
		c = tree.createTree(1450,-4028,0);
		c = tree.createTree(1395,-4042,2);
		c = tree.createTree(1362,-4040,0);
		c = tree.createTree(1329,-4051,1);
		c = tree.createTree(1313,-4030,1);
		c = tree.createTree(1290,-4043,2);
		c = tree.createTree(1268,-4039,0);
		c = tree.createTree(1247,-4050,1);
		c = tree.createTree(1228,-4034,2);
		c = tree.createTree(1206,-4050,0);
		c = tree.createTree(1188,-4041,1);
		c = tree.createTree(1160,-4057,2);
		c = tree.createTree(1138,-4030,1);
		c = tree.createTree(1108,-4051,1);
		c = tree.createTree(1086,-4034,2);
		c = tree.createTree(1058,-4052,1);
		c = tree.createTree(1039,-4033,1);
		c = tree.createTree(1005,-4049,1);
		c = tree.createTree(984,-4034,2);
		c = tree.createTree(956,-4043,2);
		c = tree.createTree(917,-4067,1);
		c = tree.createTree(917,-4046,2);
		c = tree.createTree(886,-4053,0);
		c = tree.createTree(861,-4031,2);
		c = tree.createTree(843,-4053,0);
		c = tree.createTree(821,-4043,1);
		c = tree.createTree(801,-4069,0);
		c = tree.createTree(771,-4047,0);
		c = tree.createTree(733,-4040,0);
		c = tree.createTree(707,-4063,1);
		c = tree.createTree(675,-4042,0);
		c = tree.createTree(636,-4063,0);
		c = tree.createTree(624,-4048,1);
		c = tree.createTree(585,-4069,0);
		c = tree.createTree(587,-4049,2);
		c = tree.createTree(551,-4059,0);
		c = tree.createTree(531,-4039,2);
		c = tree.createTree(510,-4052,1);
		c = tree.createTree(158,-3953,1);
		c = tree.createTree(163,-3972,0);
		c = tree.createTree(157,-3996,1);
		c = tree.createTree(166,-4032,0);
		c = tree.createTree(166,-4002,1);
		c = tree.createTree(148,-4041,0);
		c = tree.createTree(167,-4067,2);
		c = tree.createTree(147,-4086,0);
		c = tree.createTree(163,-4105,2);
		c = tree.createTree(144,-4146,2);
		c = tree.createTree(163,-4123,0);
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
	public void createSpawnArea()  {
		
		//////////////////////////
		//// ENTIRE ZONE STUFF ///
		//////////////////////////
		// Draw the grass around spawn.
		spawnGrassRect(-2-1000-1000-1000,-1000,2000+1000,64);
		
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
		spawnWaterRect(-2100-3000+10-6-5,56,-167,200);	
		
		// Draw the water to right of bridge spawn.
		spawnWaterRect(-168+20+15-5-6+5,56,2032+1000,200);
		
		// Draw rocks behind spawn.
		c = new rock(-24,75,0);
		c = new rock(21,111,1);
		c = new rock(-6,147,0);
		
		////////////////////////////
		//// FARMHOUSE AREA  ///////
		////////////////////////////
		c = tree.createTree(-720,-325,0);
		c = new farmHouse(-650,-420,0);
		c = new barn(-950,-420,0);
		farmer theFarmer = new farmer(-711,-267);
		theFarmer.setFacingDirection("Down");
		c = tree.createTree(-1017, -414, 1);
		c = tree.createTree(-1011, 0, 0);
		c = new haystack(-960,-351,1);
		c = new haystack(-875,-351,1);
		c = new bush(-1025,-130,0);
		c = new bush(-909,-9,1);
		c = new bush(-510,-330,1);
		
		/////////////////////////////////////
		//// ARMORY/SHED/ACROSS RIVER ///////
		/////////////////////////////////////
		c = new blackSmith(-600,200,0);
		c = tree.createTree(-135,186,1);
		c = tree.createTree(-70,220,2);
		c = new bush(-110,250,0);
		c = tree.createTree(-120,280,1);
		c = tree.createTree(-135,310,2);
		c = tree.createTree(-70,340,0);
		c = new bush(-90,360,1);
		c = tree.createTree(-90,380,1);
		c = tree.createTree(-65,410,2);
		c = tree.createTree(-40,450,1);
		c = tree.createTree(-99,212,0);
		c = tree.createTree(-120,323,1);
		c = tree.createTree(-58,425,2);
		c = tree.createTree(-774,428,2);
		c = tree.createTree(-693,314,1);
		c = tree.createTree(-627,260,1);
		
		// Dagger.
		item daggerSpawn = new dagger(-516,387);
		
		// Corner
		c = tree.createTree(-60,480,2);
		c = tree.createTree(-20,500,1);
		c = tree.createTree(-55,525,2);
		c = tree.createTree(-80,545,1);
		c = tree.createTree(-100,570,1);
		c = new bush(-142,360,2);
		c = new bush(-250,465,0);
		
		// The bottom trees.
		c = tree.createTree(309,-464,1);
		c = tree.createTree(338,-450,1);
		c = tree.createTree(366,-454,2);
		c = tree.createTree(393,-448,2);
		c = tree.createTree(414,-467,1);
		c = tree.createTree(436,-448,1);
		c = tree.createTree(461,-450,1);
		c = tree.createTree(483,-429,1);
		c = tree.createTree(-331,-462,0);
		c = tree.createTree(-361,-462,2);
		c = tree.createTree(-397,-468,2);
		c = tree.createTree(-422,-452,0);
		c = tree.createTree(-439,-469,1);
		c = tree.createTree(-460,-447,1);
		c = tree.createTree(-487,-465,2);
		c = tree.createTree(-1003,-567,0);
		c = tree.createTree(-993,-536,1);
		c = tree.createTree(-1012,-520,0);
		c = tree.createTree(-120,600,2);
		c = tree.createTree(-145,580,0);
		c = tree.createTree(-165,605,2);
		c = tree.createTree(-195,585,1);
		c = tree.createTree(-225,585,1);
		c = tree.createTree(-240,610,0);
		c = tree.createTree(-260,600,1);
		c = tree.createTree(-285,575,2);
		c = tree.createTree(-310,610,2);
		c = tree.createTree(-330,565,2);
		c = tree.createTree(-345,585,1);
		c = tree.createTree(-375,615,0);
		c = tree.createTree(-415,600,2);
		c = tree.createTree(-445,575,0);
		c = tree.createTree(-465,605,0);
		c = tree.createTree(-490,585,1);
		c = tree.createTree(-525,580,1);
		c = tree.createTree(-550,615,2);
		c = tree.createTree(-580,610,2);
		c = tree.createTree(-610,580,1);
		c = tree.createTree(-635,610,2);
		c = tree.createTree(-665,580,1);
		c = tree.createTree(-700,580,0);
		c = tree.createTree(-715,615,0);
		
		// The left trees.
		c = tree.createTree(-730,590,0);
		c = tree.createTree(-700,560,1);
		c = tree.createTree(-660,555,2);
		c = tree.createTree(-685,530,0);
		c = tree.createTree(-705,499,2);
		c = tree.createTree(-735,476,1);
		c = tree.createTree(-755,454,2);
		c = tree.createTree(-785,415,0);
		c = tree.createTree(-745,390,1);
		c = tree.createTree(-710,360,0);
		c = tree.createTree(-685,335,2);
		c = tree.createTree(-655,300,1);
		c = tree.createTree(-774,234,1);
		c = tree.createTree(-670,275,2);
		c = tree.createTree(-680,260,1);
		c = tree.createTree(-690,235,0);
		c = tree.createTree(-730,200,1);
		c = new bush(-741,240,2);
		c = new bush(-642,393,1);
		
		// Misc left trees beyond the line of trees that block you
		c = tree.createTree(-850,590,0);
		c = tree.createTree(-775,560,2);
		c = tree.createTree(-1025,555,1);
		c = tree.createTree(-855,530,2);
		c = tree.createTree(-1200,499,1);
		c = tree.createTree(-1100,476,1);
		c = tree.createTree(-929,454,0);
		c = tree.createTree(-828,415,0);
		c = tree.createTree(-979,390,2);
		c = tree.createTree(-1202,360,0);
		c = tree.createTree(-827,335,2);
		c = tree.createTree(-1209,300,1);
		c = tree.createTree(-919,275,0);
		c = tree.createTree(-929,260,2);
		c = tree.createTree(-890,235,0);
		c = tree.createTree(-950,200,2);
		c = new bush(-921,510,1);
		c = new bush(-771,636,2);
		c = new bush(-795,201,0);
		c = new rock(-891,312,1);
		c = tree.createTree(-804,675,1);
		
		// Trees below bottom tree wall.
		c = tree.createTree(-699,672,2);
		c = tree.createTree(-660,770,1);
		c = tree.createTree(-635,890,2);
		c = tree.createTree(-580,840,1);
		c = tree.createTree(-520,780,2);
		c = tree.createTree(-480,827,1);
		c = tree.createTree(-410,820,0);
		c = tree.createTree(-385,890,0);
		c = tree.createTree(-360,760,1);
		c = tree.createTree(-519, 660, 0);
		c = tree.createTree(-310,700,1);
		c = tree.createTree(-280,792,0);
		c = tree.createTree(-265,700,2);
		c = tree.createTree(-200,679,0);
		c = tree.createTree(-160,740,1);
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
		
		// Is the zone on fire?
		isOnFire = new event("sheepFarmIsOnFire");
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
				storm s = new storm(15f);
			}
		}
	}
	
	
	// Do zone specific tasks that aren't monitored by
	// zone specific units. 
	@Override
	public void update() {
		startStormFromFog();
		doForestFireStuff();
		doFinalLightningStuff();
		dealWithRegionStuff();
	}
	
	// First lightning area.
	int firstAreaX1 = -2200;
	int firstAreaY1 = -4200;
	int firstAreaX2 = -1000;
	int firstAreaY2 = -3800;
	float firstAreaLightningEvery = 1f;
	boolean firstAreaPreLightningSoundPlayed = false;
	boolean firstAreaLightningStrikeSoundPlayed = false;
	long firstAreaLastStrike = 0;
	
	// Second lightning area.
	int secondAreaX1 = -2354;
	int secondAreaY1 = -3527;
	int secondAreaX2 = -1661;
	int secondAreaY2 = -2600;
	ArrayList<drawnObject> secondAreaLightning;
	float secondAreaLightningEvery = lightningStrike.preLightningLastsFor + 0.5f;
	boolean secondAreaPreLightningSoundPlayed = false;
	boolean secondAreaLightningStrikeSoundPlayed = false;
	long secondAreaLastStrike = 0;
	
	// Third lightning area.
	int thirdAreaX1 = -2937;
	int thirdAreaY1 = -2467;
	int thirdAreaX2 = -2488;
	int thirdAreaY2 = -1188;
	int thirdAreaX3 = -2480;
	int thirdAreaY3 = -1515;
	int thirdAreaX4 = -1267;
	int thirdAreaY4 = -539;
	int thirdAreaX5 = -2463;
	int thirdAreaY5 = -2498;
	int thirdAreaX6 = -1814;
	int thirdAreaY6 = -1989;
	ArrayList<drawnObject> thirdAreaLightning;
	float thirdAreaLightningEvery = lightningStrike.preLightningLastsFor + 0.5f;
	boolean thirdAreaPreLightningSoundPlayed = false;
	boolean thirdAreaLightningStrikeSoundPlayed = false;
	long thirdAreaLastStrike = 0;
	
	// First lightning area.
	int fourthAreaX1 = -3012;
	int fourthAreaY1 = -450;
	int fourthAreaX2 = -1531;
	int fourthAreaY2 = 80;
	float fourthAreaLightningEvery = 0.05f;
	boolean fourthAreaPreLightningSoundPlayed = false;
	boolean fourthAreaLightningStrikeSoundPlayed = false;
	long fourthAreaLastStrike = 0;
	
	
	// Do final lightning stuff
	public void doFinalLightningStuff() {
		
		// Current player
		player currPlayer = player.getCurrentPlayer();
		
		// Only do this if the player is actually past the part. TODO: needs to be changed to only
		// be done if they are not on the fire path.
		if(farlsworth.pastTombExit != null && farlsworth.pastTombExit.isCompleted()) {
			
			///////////////////////////
			// FIRST LIGHTNING AREA  //
			///////////////////////////
			if(firstAreaLastStrike == 0 || time.getTime() - firstAreaLastStrike > firstAreaLightningEvery*1000) {
				
				int i = firstAreaX1 + utility.RNG.nextInt(firstAreaX2 - firstAreaX1);
				int j = firstAreaY1 + utility.RNG.nextInt(firstAreaY2 - firstAreaY1);
				lightningStrike l = new lightningStrike(i,j); 
		
				firstAreaLastStrike = time.getTime();
				firstAreaLightningStrikeSoundPlayed = false;
			}
			
			///////////////////////////
			// SECOND LIGHTNING AREA //
			///////////////////////////
			if(secondAreaLastStrike == 0 || time.getTime() - secondAreaLastStrike > secondAreaLightningEvery*1000) {
				
				// Efficiency.
				secondAreaLightning = new ArrayList<drawnObject>();
				
				for(int j = secondAreaY1; j < secondAreaY2; j += lightningStrike.DEFAULT_LIGHTNING_RADIUS*2) {
					for(int i = secondAreaX1; i < secondAreaX2; i += lightningStrike.DEFAULT_LIGHTNING_RADIUS*2) {
							if(utility.RNG.nextInt(100) > 95) {
								lightningStrike l = new lightningStrike(i,j, true); 
								secondAreaLightning.add(l);
							}
					}
				}
				
				secondAreaLastStrike = time.getTime();
				secondAreaLightningStrikeSoundPlayed = false;
			}
		
			if(!secondAreaLightningStrikeSoundPlayed && time.getTime() - secondAreaLastStrike > lightningStrike.preLightningLastsFor*1000) {
				if(secondAreaLightning!=null && secondAreaLightning.size() > 0) {
					drawnObject l = currPlayer.getClosestToFrom(secondAreaLightning);
					if(l.isOnScreen()) {
						sound s = new sound(lightningStrike.lightningSound);
						s.setPosition(l.getIntX(), l.getIntY(), sound.DEFAULT_SOUND_RADIUS);
						s.start();
					}
				}
				secondAreaLightningStrikeSoundPlayed = true;
			}
			
			///////////////////////////
			// THIRD LIGHTNING AREA //
			///////////////////////////
			if(thirdAreaLastStrike == 0 || time.getTime() - thirdAreaLastStrike > thirdAreaLightningEvery*1000) {
				
				// Efficiency.
				thirdAreaLightning = new ArrayList<drawnObject>();
				
				for(int j = thirdAreaY1; j < thirdAreaY2; j += lightningStrike.DEFAULT_LIGHTNING_RADIUS*2) {
					for(int i = thirdAreaX1; i < thirdAreaX2; i += lightningStrike.DEFAULT_LIGHTNING_RADIUS*2) {
							if(utility.RNG.nextInt(100) > 90) {
								lightningStrike l = new lightningStrike(i,j, true); 
								thirdAreaLightning.add(l);
							}
					}
				}
				for(int j = thirdAreaY3; j < thirdAreaY4; j += lightningStrike.DEFAULT_LIGHTNING_RADIUS*2) {
					for(int i = thirdAreaX3; i < thirdAreaX4; i += lightningStrike.DEFAULT_LIGHTNING_RADIUS*2) {
							if(utility.RNG.nextInt(100) > 90) {
								lightningStrike l = new lightningStrike(i,j, true); 
								thirdAreaLightning.add(l);
							}
					}
				}
				for(int j = thirdAreaY5; j < thirdAreaY6; j += lightningStrike.DEFAULT_LIGHTNING_RADIUS*2) {
					for(int i = thirdAreaX5; i < thirdAreaX6; i += lightningStrike.DEFAULT_LIGHTNING_RADIUS*2) {
							if(utility.RNG.nextInt(100) > 90) {
								lightningStrike l = new lightningStrike(i,j, true); 
								thirdAreaLightning.add(l);
							}
					}
				}
				
				thirdAreaLastStrike = time.getTime();
				thirdAreaLightningStrikeSoundPlayed = false;
			}
			
			if(!thirdAreaLightningStrikeSoundPlayed && time.getTime() - thirdAreaLastStrike > lightningStrike.preLightningLastsFor*1000) {
				if(thirdAreaLightning!=null && thirdAreaLightning.size() > 0) {
					drawnObject l = currPlayer.getClosestToFrom(thirdAreaLightning);
					if(l.isOnScreen()) {
						sound s = new sound(lightningStrike.lightningSound);
						s.setPosition(l.getIntX(), l.getIntY(), sound.DEFAULT_SOUND_RADIUS);
						s.start();
					}
				}
				thirdAreaLightningStrikeSoundPlayed = true;
			}
			
			///////////////////////////
			// FOURTH LIGHTNING AREA  //
			///////////////////////////
			if(fourthAreaLastStrike == 0 || time.getTime() - fourthAreaLastStrike > fourthAreaLightningEvery*1000) {
				
				int i = fourthAreaX1 + utility.RNG.nextInt(fourthAreaX2 - fourthAreaX1);
				int j = fourthAreaY1 + utility.RNG.nextInt(fourthAreaY2 - fourthAreaY1);
				lightningStrike l = new lightningStrike(i,j); 
		
				fourthAreaLastStrike = time.getTime();
				fourthAreaLightningStrikeSoundPlayed = false;
			}

		}
		
	}
	
	long lastFireSound = 0;
	float playEvery = 6f;
	
	// Do forest fire stuff.
	public void doForestFireStuff() {
		
		if(isOnFire != null && isOnFire.isCompleted()) {
			// Play fire sound
			if(lastFireSound == 0) {
				lastFireSound = time.getTime();
				sound s = new sound(fire.forestFire);
				s.start();
			}
			
			else if(time.getTime() - lastFireSound > playEvery*1000) {
				lastFireSound = time.getTime();
				sound s = new sound(fire.forestFire);
				s.start();
			}
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