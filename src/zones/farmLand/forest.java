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

public class forest extends zone {
	
	//////////////////////////////////
	// FIELDS, GLOBALS, CONSTRUCTOR //
	//////////////////////////////////
	
	// Static caller of the zone.
	private static zone zoneReference;
	
	// Zone music.
	private static String zoneMusic = "sounds/music/farmLand/sheepFarm/forest.wav";
	
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
	public forest() {
		super("forest", "farmLand");
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
		
		// Load units
		loadUnits();
		
		// Create terrain
		createTerrain();
		
		// Create final area
		createFinalArea();
		
		// Denmother area
		createDenmotherArea();
		
		// Sort chunks.
		chunk.sortChunks();
		
		// Play zone music.
		 music.startMusic(zoneMusic); 
		
	}
	
	//////////////////////
	// INDIVIDUAL AREAS //
	//////////////////////
	
	// Load units
	public void loadUnits() {
		
		// Farlsworth
		farlsworth sheepBoss = new farlsworth(411,-394);
	}
	
	// Create terrain
	public void createTerrain() {
		
		// Rivers
		spawnWaterRect(-3152+20,-5705+7,-2975,-538+150); // Vertical river
		spawnWaterRect(-4984,61,318,184); // Horizontal river
		
		// Denmother area
		spawnGrassRect(-4954+2,-5588+12+6,-3115,63); // Denmother's area on left.
		spawnGrassRect(-3155,-548-31,-2900,60+32); // Grass bridge over river
		
		// Grass on right side (Final area)
		spawnGrassRect(-2985-20,-5598+28,280,64); // Right
	}
	
	// Denmother area
	public void createDenmotherArea() {
		new bone(-3172,-326,1);
		new bone(-3139,-135,2);
		new bone(-2866,-240,0);
		new bone(-2778,-352,2);
		new bone(-2630,-151,3);
		new bone(-2728,-17,0);
		new bone(-2978,-82,1);
		new bone(-3235,-17,1);
		new bone(-3421,-117,4);
		new bone(-3592,-36,1);
		new bone(-3757,-114,1);
		new bone(-3623,-219,3);
		new bone(-3479,-335,0);
		new bone(-3307,-402,3);
		new bone(-3447,-501,0);
		new bone(-3236,-567,3);
		new bone(-3215,-689,0);
		new bone(-3389,-786,3);
		new bone(-3319,-892,1);
		new bone(-3203,-1028,3);
		new bone(-3372,-1119,2);
		new bone(-3558,-1079,0);
		new bone(-3713,-1143,2);
		new bone(-3621,-980,1);
		new bone(-3451,-957,2);
		new bone(-3564,-879,4);
		new bone(-3698,-790,1);
		new bone(-3639,-665,0);
		new bone(-3692,-587,0);
		new bone(-3641,-443,4);
		new bone(-3813,-422,1);
		new bone(-3928,-303,0);
		new bone(-3976,-181,1);
		new bone(-4034,-79,0);
		new bone(-4195,-44,1);
		new bone(-4343,-17,3);
		new bone(-4340,-128,4);
		new bone(-4190,-236,0);
		new bone(-4058,-316,2);
		new bone(-4107,-454,1);
		new bone(-4296,-386,4);
		new bone(-4194,-568,4);
		new bone(-3943,-533,2);
		new bone(-3795,-661,4);
		new bone(-3974,-741,2);
		new bone(-4064,-855,2);
		new bone(-3901,-893,4);
		new bone(-3761,-912,4);
		new bone(-3791,-1039,4);
		new bone(-3936,-1031,0);
		new bone(-4077,-1135,2);
		new bone(-4188,-1205,1);
		new bone(-4250,-1083,1);
		new bone(-4325,-962,0);
		new bone(-4317,-836,1);
		new bone(-4213,-787,2);
		new bone(-4070,-943,0);
		new bone(-3303,-1229,0);
		new bone(-3489,-1313,1);
		new bone(-3664,-1343,1);
		new bone(-3852,-1386,1);
		new bone(-3878,-1280,2);
		new bone(-4088,-1289,4);
		new bone(-4283,-1308,3);
		new bone(-4457,-1261,1);
		new bone(-4457,-1023,3);
		new bone(-4421,-882,0);
		new bone(-4504,-763,3);
		new bone(-4320,-642,0);
		new bone(-4476,-461,1);
		new bone(-4368,-293,1);
		new bone(-4484,-183,2);
		new bone(-4496,-33,2);
		new bone(-3937,18,0);
		new bone(-3461,24,4);
		new bone(-3303,-258,1);
	}
	
	// Final area with all the fire.
	public void createFinalArea() {
		
		// Lightning tree
		lightningTree = tree.createTree(-758,-3937,0);
		
		// Trees
		tree.createTree(-703,-4069,1);
		tree.createTree(-735,-4081,1);
		tree.createTree(-759,-4098,1);
		tree.createTree(-787,-4119,1);
		tree.createTree(-809,-4141,2);
		tree.createTree(-831,-4160,1);
		tree.createTree(-862,-4187,1);
		tree.createTree(-879,-4208,1);
		tree.createTree(-909,-4227,2);
		tree.createTree(-699,-3685,1);
		tree.createTree(-737,-3685,1);
		tree.createTree(-771,-3700,1);
		tree.createTree(-805,-3693,1);
		tree.createTree(-842,-3691,2);
		tree.createTree(-876,-3703,1);
		tree.createTree(-916,-3712,2);
		tree.createTree(-950,-3725,2);
		tree.createTree(-983,-3724,0);
		tree.createTree(-1023,-3711,2);
		tree.createTree(-1053,-3706,2);
		tree.createTree(-1098,-3706,1);
		tree.createTree(-1137,-3706,0);
		tree.createTree(-1182,-3698,2);
		tree.createTree(-1207,-3709,0);
		tree.createTree(-1240,-3696,0);
		tree.createTree(-939,-4237,0);
		tree.createTree(-977,-4224,2);
		tree.createTree(-998,-4238,2);
		tree.createTree(-1013,-4223,2);
		tree.createTree(-1038,-4223,0);
		tree.createTree(-1078,-4246,2);
		tree.createTree(-1101,-4227,0);
		tree.createTree(-1144,-4246,1);
		tree.createTree(-1160,-4220,2);
		tree.createTree(-1197,-4239,0);
		tree.createTree(-1218,-4215,2);
		tree.createTree(-1259,-4229,2);
		tree.createTree(-1279,-3695,0);
		tree.createTree(-1317,-3688,0);
		tree.createTree(-1355,-3702,1);
		tree.createTree(-1390,-3677,2);
		tree.createTree(-1424,-3697,0);
		tree.createTree(-1287,-4226,1);
		tree.createTree(-1319,-4241,1);
		tree.createTree(-1353,-4246,0);
		tree.createTree(-1388,-4236,0);
		tree.createTree(-1427,-4239,0);
		tree.createTree(-1468,-4239,0);
		tree.createTree(-1506,-4239,0);
		tree.createTree(-1539,-4237,1);
		tree.createTree(-1452,-3689,0);
		tree.createTree(-1500,-3694,0);
		tree.createTree(-1527,-3675,1);
		tree.createTree(-1555,-3657,1);
		tree.createTree(-1584,-3655,0);
		tree.createTree(-1618,-3628,1);
		tree.createTree(-1640,-3604,0);
		tree.createTree(-1659,-3578,2);
		tree.createTree(-1686,-3551,2);
		tree.createTree(-1708,-3534,1);
		tree.createTree(-1719,-3503,1);
		tree.createTree(-1761,-3486,1);
		tree.createTree(-1752,-3452,0);
		tree.createTree(-1726,-3402,1);
		tree.createTree(-1724,-3424,0);
		tree.createTree(-1723,-3378,2);
		tree.createTree(-1575,-4239,2);
		tree.createTree(-1614,-4218,1);
		tree.createTree(-1647,-4224,0);
		tree.createTree(-1682,-4214,0);
		tree.createTree(-1725,-4229,1);
		tree.createTree(-1748,-4196,1);
		tree.createTree(-1806,-4183,0);
		tree.createTree(-1742,-4219,0);
		tree.createTree(-1772,-4200,2);
		tree.createTree(-1833,-4180,0);
		tree.createTree(-1862,-4165,1);
		tree.createTree(-1885,-4142,0);
		tree.createTree(-1907,-4150,2);
		tree.createTree(-1934,-4123,1);
		tree.createTree(-1978,-4119,2);
		tree.createTree(-1996,-4087,1);
		tree.createTree(-2027,-4073,2);
		tree.createTree(-2061,-4043,1);
		tree.createTree(-2053,-4066,1);
		tree.createTree(-2091,-4031,0);
		tree.createTree(-2107,-4005,1);
		tree.createTree(-2130,-3989,1);
		tree.createTree(-2175,-3969,0);
		tree.createTree(-2145,-3950,0);
		tree.createTree(-2190,-3926,2);
		tree.createTree(-2239,-3899,0);
		tree.createTree(-2211,-3913,0);
		tree.createTree(-2175,-3938,2);
		tree.createTree(-2255,-3872,0);
		tree.createTree(-2283,-3837,2);
		tree.createTree(-2314,-3816,0);
		tree.createTree(-2291,-3786,0);
		tree.createTree(-2314,-3763,0);
		tree.createTree(-2338,-3742,0);
		tree.createTree(-2321,-3694,2);
		tree.createTree(-2334,-3649,1);
		tree.createTree(-2324,-3674,0);
		tree.createTree(-2337,-3722,2);
		tree.createTree(-2334,-3631,1);
		tree.createTree(-2347,-3611,1);
		tree.createTree(-2353,-3579,0);
		tree.createTree(-2323,-3592,0);
		tree.createTree(-2344,-3554,2);
		tree.createTree(-2367,-3538,1);
		tree.createTree(-2340,-3515,1);
		tree.createTree(-2359,-3493,1);
		tree.createTree(-2318,-3469,2);
		tree.createTree(-2350,-3452,0);
		tree.createTree(-2330,-3426,2);
		tree.createTree(-2361,-3395,0);
		tree.createTree(-2356,-3373,2);
		tree.createTree(-2336,-3350,1);
		tree.createTree(-2355,-3342,2);
		tree.createTree(-2337,-3314,0);
		tree.createTree(-2360,-3287,2);
		tree.createTree(-2337,-3264,1);
		tree.createTree(-2351,-3244,1);
		tree.createTree(-2322,-3226,0);
		tree.createTree(-2341,-3193,0);
		tree.createTree(-2321,-3176,0);
		tree.createTree(-2348,-3148,0);
		tree.createTree(-2323,-3126,2);
		tree.createTree(-2347,-3098,1);
		tree.createTree(-2325,-3076,0);
		tree.createTree(-2352,-3052,0);
		tree.createTree(-2321,-3027,0);
		tree.createTree(-2355,-3000,2);
		tree.createTree(-2323,-2979,1);
		tree.createTree(-1723,-3362,2);
		tree.createTree(-1723,-3345,2);
		tree.createTree(-1696,-3329,1);
		tree.createTree(-1726,-3288,1);
		tree.createTree(-1698,-3301,2);
		tree.createTree(-1696,-3271,1);
		tree.createTree(-1713,-3254,0);
		tree.createTree(-1690,-3234,0);
		tree.createTree(-1712,-3205,1);
		tree.createTree(-1690,-3186,0);
		tree.createTree(-1720,-3163,2);
		tree.createTree(-1692,-3141,1);
		tree.createTree(-1718,-3107,2);
		tree.createTree(-1687,-3122,2);
		tree.createTree(-1681,-3077,2);
		tree.createTree(-1711,-3054,2);
		tree.createTree(-1679,-3032,0);
		tree.createTree(-1711,-3089,2);
		tree.createTree(-1709,-3003,2);
		tree.createTree(-1662,-3002,0);
		tree.createTree(-1689,-2971,0);
		tree.createTree(-1702,-2939,0);
		tree.createTree(-1696,-2984,2);
		tree.createTree(-2357,-2946,2);
		tree.createTree(-2357,-2960,0);
		tree.createTree(-2328,-2944,1);
		tree.createTree(-2345,-2920,2);
		tree.createTree(-2322,-2901,1);
		tree.createTree(-2351,-2878,2);
		tree.createTree(-2322,-2860,0);
		tree.createTree(-2342,-2840,2);
		tree.createTree(-2310,-2818,1);
		tree.createTree(-2353,-2789,1);
		tree.createTree(-2337,-2769,2);
		tree.createTree(-2315,-2750,0);
		tree.createTree(-2336,-2725,0);
		tree.createTree(-2350,-2681,0);
		tree.createTree(-2333,-2700,2);
		tree.createTree(-2318,-2796,1);
		tree.createTree(-1678,-2952,2);
		tree.createTree(-1678,-2920,0);
		tree.createTree(-1654,-2896,1);
		tree.createTree(-1679,-2881,0);
		tree.createTree(-1647,-2864,1);
		tree.createTree(-1661,-2846,0);
		tree.createTree(-1678,-2829,1);
		tree.createTree(-1671,-2790,0);
		tree.createTree(-1688,-2805,2);
		tree.createTree(-1683,-2769,0);
		tree.createTree(-1663,-2752,0);
		tree.createTree(-1687,-2732,1);
		tree.createTree(-1670,-2718,1);
		tree.createTree(-1696,-2696,2);
		tree.createTree(-1674,-2677,1);
		tree.createTree(-1681,-2643,1);
		tree.createTree(-1662,-2624,1);
		tree.createTree(-1677,-2653,2);
		tree.createTree(-1757,-3469,2);
		tree.createTree(-2263,-3921,0);
		tree.createTree(-2346,-3828,2);
		tree.createTree(-2371,-3842,1);
		tree.createTree(-2294,-3937,2);
		tree.createTree(-2307,-3961,1);
		tree.createTree(-2326,-3976,2);
		tree.createTree(-2398,-3851,1);
		tree.createTree(-2443,-3876,1);
		tree.createTree(-2435,-3860,0);
		tree.createTree(-2347,-3993,1);
		tree.createTree(-2382,-4011,0);
		tree.createTree(-2416,-4024,1);
		tree.createTree(-2447,-4017,1);
		tree.createTree(-2479,-4021,0);
		tree.createTree(-2504,-4043,0);
		tree.createTree(-2521,-4029,2);
		tree.createTree(-2548,-4049,1);
		tree.createTree(-2568,-4032,1);
		tree.createTree(-2596,-4043,2);
		tree.createTree(-2631,-4043,0);
		tree.createTree(-2661,-4035,2);
		tree.createTree(-2698,-4050,2);
		tree.createTree(-2726,-4040,0);
		tree.createTree(-2755,-4059,1);
		tree.createTree(-2776,-4045,0);
		tree.createTree(-2813,-4061,0);
		tree.createTree(-2843,-4042,1);
		tree.createTree(-2872,-4061,1);
		tree.createTree(-2890,-4040,1);
		tree.createTree(-2918,-4040,1);
		tree.createTree(-2938,-4059,0);
		tree.createTree(-2957,-4043,2);
		tree.createTree(-3000,-4051,0);
		tree.createTree(-2999,-3842,2);
		tree.createTree(-2967,-3860,0);
		tree.createTree(-2941,-3840,2);
		tree.createTree(-2917,-3857,0);
		tree.createTree(-2881,-3835,2);
		tree.createTree(-2826,-3839,0);
		tree.createTree(-2845,-3823,0);
		tree.createTree(-2769,-3839,2);
		tree.createTree(-2801,-3815,0);
		tree.createTree(-2736,-3834,1);
		tree.createTree(-2714,-3815,2);
		tree.createTree(-2675,-3836,2);
		tree.createTree(-2639,-3815,1);
		tree.createTree(-2608,-3839,1);
		tree.createTree(-2582,-3820,0);
		tree.createTree(-2547,-3842,2);
		tree.createTree(-2523,-3825,2);
		tree.createTree(-2495,-3849,1);
		tree.createTree(-2467,-3849,2);
		tree.createTree(-2351,-2660,1);
		tree.createTree(-2344,-2636,0);
		tree.createTree(-2360,-2619,0);
		tree.createTree(-2330,-2600,0);
		tree.createTree(-2361,-2576,1);
		tree.createTree(-1663,-2598,1);
		tree.createTree(-1695,-2584,0);
		tree.createTree(-1691,-2556,2);
		tree.createTree(-1710,-2537,0);
		tree.createTree(-1730,-2521,0);
		tree.createTree(-1749,-2498,0);
		tree.createTree(-1752,-2459,1);
		tree.createTree(-1752,-2480,0);
		tree.createTree(-1761,-2433,1);
		tree.createTree(-1780,-2417,1);
		tree.createTree(-1801,-2403,1);
		tree.createTree(-1801,-2372,1);
		tree.createTree(-1832,-2353,2);
		tree.createTree(-1797,-2384,1);
		tree.createTree(-2398,-2576,0);
		tree.createTree(-2412,-2547,1);
		tree.createTree(-2442,-2528,1);
		tree.createTree(-2474,-2544,1);
		tree.createTree(-2495,-2519,0);
		tree.createTree(-2551,-2512,2);
		tree.createTree(-2527,-2526,2);
		tree.createTree(-2582,-2512,1);
		tree.createTree(-2608,-2516,2);
		tree.createTree(-2646,-2530,1);
		tree.createTree(-2664,-2512,2);
		tree.createTree(-2692,-2526,0);
		tree.createTree(-2716,-2526,0);
		tree.createTree(-2765,-2509,1);
		tree.createTree(-2755,-2517,2);
		tree.createTree(-2822,-2517,2);
		tree.createTree(-2802,-2494,0);
		tree.createTree(-2868,-2511,1);
		tree.createTree(-2896,-2504,2);
		tree.createTree(-2918,-2489,0);
		tree.createTree(-2950,-2467,2);
		tree.createTree(-2992,-2460,2);
		tree.createTree(-3006,-2446,1);
		tree.createTree(-1835,-2338,2);
		tree.createTree(-1848,-2314,2);
		tree.createTree(-1848,-2289,1);
		tree.createTree(-1865,-2276,0);
		tree.createTree(-1888,-2259,1);
		tree.createTree(-1890,-2234,0);
		tree.createTree(-1912,-2213,0);
		tree.createTree(-1908,-2192,2);
		tree.createTree(-1940,-2181,0);
		tree.createTree(-1954,-2165,1);
		tree.createTree(-1967,-2141,1);
		tree.createTree(-1987,-2125,0);
		tree.createTree(-2007,-2108,0);
		tree.createTree(-2052,-2101,1);
		tree.createTree(-2063,-2077,0);
		tree.createTree(-2102,-2053,2);
		tree.createTree(-2092,-2069,0);
		tree.createTree(-2143,-2029,0);
		tree.createTree(-2120,-2049,0);
		tree.createTree(-2170,-2005,1);
		tree.createTree(-2218,-1990,1);
		tree.createTree(-2258,-1964,0);
		tree.createTree(-2253,-1984,1);
		tree.createTree(-2287,-1955,1);
		tree.createTree(-2339,-1955,2);
		tree.createTree(-2313,-1928,1);
		tree.createTree(-2351,-1932,1);
		tree.createTree(-2379,-1908,1);
		tree.createTree(-2405,-1926,1);
		tree.createTree(-2414,-1901,1);
		tree.createTree(-2448,-1906,1);
		tree.createTree(-2462,-1888,1);
		tree.createTree(-2478,-1904,1);
		tree.createTree(-2496,-1889,2);
		tree.createTree(-2518,-1864,1);
		tree.createTree(-2479,-1858,2);
		tree.createTree(-2491,-1832,1);
		tree.createTree(-2480,-1808,1);
		tree.createTree(-2499,-1793,2);
		tree.createTree(-2472,-1770,0);
		tree.createTree(-2497,-1748,1);
		tree.createTree(-2469,-1727,0);
		tree.createTree(-2495,-1711,0);
		tree.createTree(-2467,-1683,1);
		tree.createTree(-2467,-1648,2);
		tree.createTree(-2486,-1622,1);
		tree.createTree(-2448,-1667,2);
		tree.createTree(-2457,-1601,1);
		tree.createTree(-2485,-1570,1);
		tree.createTree(-2454,-1581,1);
		tree.createTree(-2473,-1539,2);
		tree.createTree(-2456,-1556,2);
		tree.createTree(-2456,-1518,1);
		tree.createTree(-3004,-2430,2);
		tree.createTree(-3004,-2409,1);
		tree.createTree(-2995,-2394,2);
		tree.createTree(-3004,-2366,1);
		tree.createTree(-2981,-2347,0);
		tree.createTree(-2999,-2315,2);
		tree.createTree(-2981,-2290,1);
		tree.createTree(-2995,-2262,0);
		tree.createTree(-2971,-2249,1);
		tree.createTree(-3001,-2219,1);
		tree.createTree(-2981,-2187,2);
		tree.createTree(-3005,-2161,2);
		tree.createTree(-2984,-2136,1);
		tree.createTree(-3011,-2122,2);
		tree.createTree(-2987,-2095,0);
		tree.createTree(-3001,-2074,0);
		tree.createTree(-2977,-2053,1);
		tree.createTree(-3005,-2003,0);
		tree.createTree(-2997,-2023,1);
		tree.createTree(-3001,-1982,2);
		tree.createTree(-3009,-1956,2);
		tree.createTree(-2992,-1939,1);
		tree.createTree(-2988,-1911,2);
		tree.createTree(-3010,-1894,1);
		tree.createTree(-2987,-1870,1);
		tree.createTree(-3005,-1577,2);
		tree.createTree(-2994,-1565,1);
		tree.createTree(-2971,-1547,0);
		tree.createTree(-2966,-1517,1);
		tree.createTree(-2950,-1497,0);
		tree.createTree(-2936,-1473,1);
		tree.createTree(-2893,-1483,2);
		tree.createTree(-2889,-1441,1);
		tree.createTree(-2857,-1416,2);
		tree.createTree(-2900,-1466,2);
		tree.createTree(-2804,-1402,2);
		tree.createTree(-2832,-1388,1);
		tree.createTree(-2800,-1360,1);
		tree.createTree(-2781,-1330,0);
		tree.createTree(-2417,-1536,1);
		tree.createTree(-2411,-1515,0);
		tree.createTree(-2372,-1494,2);
		tree.createTree(-2344,-1512,0);
		tree.createTree(-2322,-1486,2);
		tree.createTree(-2274,-1487,2);
		tree.createTree(-2244,-1457,0);
		tree.createTree(-2222,-1476,0);
		tree.createTree(-2177,-1452,1);
		tree.createTree(-2147,-1471,0);
		tree.createTree(-2120,-1430,0);
		tree.createTree(-2088,-1452,1);
		tree.createTree(-2066,-1419,1);
		tree.createTree(-2143,-1447,2);
		tree.createTree(-2040,-1404,0);
		tree.createTree(-1997,-1416,2);
		tree.createTree(-2011,-1395,2);
		tree.createTree(-1973,-1399,1);
		tree.createTree(-2777,-1308,0);
		tree.createTree(-2770,-1278,0);
		tree.createTree(-2744,-1261,1);
		tree.createTree(-2721,-1238,0);
		tree.createTree(-2690,-1246,1);
		tree.createTree(-2665,-1211,1);
		tree.createTree(-2627,-1218,1);
		tree.createTree(-2607,-1192,1);
		tree.createTree(-2558,-1194,0);
		tree.createTree(-2532,-1150,2);
		tree.createTree(-2550,-1173,2);
		tree.createTree(-2494,-1137,0);
		tree.createTree(-2465,-1113,1);
		tree.createTree(-2446,-1087,1);
		tree.createTree(-2431,-1060,1);
		tree.createTree(-2397,-1039,2);
		tree.createTree(-2371,-1020,1);
		tree.createTree(-2344,-993,0);
		tree.createTree(-1942,-1371,2);
		tree.createTree(-1938,-1392,0);
		tree.createTree(-1911,-1358,2);
		tree.createTree(-1889,-1336,2);
		tree.createTree(-1860,-1333,2);
		tree.createTree(-1825,-1323,1);
		tree.createTree(-1800,-1295,1);
		tree.createTree(-2313,-971,0);
		tree.createTree(-2296,-940,2);
		tree.createTree(-2260,-938,1);
		tree.createTree(-2295,-946,0);
		tree.createTree(-2226,-908,0);
		tree.createTree(-2197,-870,0);
		tree.createTree(-2197,-898,1);
		tree.createTree(-2241,-918,2);
		tree.createTree(-2164,-872,1);
		tree.createTree(-2134,-855,1);
		tree.createTree(-1802,-1313,0);
		tree.createTree(-2099,-845,0);
		tree.createTree(-2077,-823,2);
		tree.createTree(-2051,-811,2);
		tree.createTree(-2023,-794,1);
		tree.createTree(-2003,-767,1);
		tree.createTree(-1983,-750,2);
		tree.createTree(-1961,-734,2);
		tree.createTree(-1941,-714,1);
		tree.createTree(-1758,-1287,0);
		tree.createTree(-1735,-1261,2);
		tree.createTree(-1710,-1243,1);
		tree.createTree(-1682,-1256,2);
		tree.createTree(-1661,-1235,0);
		tree.createTree(-1620,-1205,2);
		tree.createTree(-1593,-1225,1);
		tree.createTree(-1625,-1225,0);
		tree.createTree(-1565,-1190,0);
		tree.createTree(-1582,-1208,0);
		tree.createTree(-1902,-713,1);
		tree.createTree(-1883,-683,0);
		tree.createTree(-1883,-701,1);
		tree.createTree(-1852,-673,0);
		tree.createTree(-1823,-671,0);
		tree.createTree(-1801,-642,0);
		tree.createTree(-1774,-658,1);
		tree.createTree(-1756,-630,0);
		tree.createTree(-1732,-609,1);
		tree.createTree(-1704,-606,1);
		tree.createTree(-1665,-574,0);
		tree.createTree(-1698,-588,2);
		tree.createTree(-1641,-562,1);
		tree.createTree(-1622,-542,1);
		tree.createTree(-1536,-1170,2);
		tree.createTree(-1511,-1145,2);
		tree.createTree(-1473,-1145,0);
		tree.createTree(-1457,-1126,1);
		tree.createTree(-1410,-1130,2);
		tree.createTree(-1404,-1100,1);
		tree.createTree(-1372,-1064,1);
		tree.createTree(-1393,-1078,2);
		tree.createTree(-1341,-1036,1);
		tree.createTree(-1358,-1012,1);
		tree.createTree(-1339,-993,1);
		tree.createTree(-1347,-954,1);
		tree.createTree(-1334,-976,2);
		tree.createTree(-1327,-921,2);
		tree.createTree(-1344,-934,2);
		tree.createTree(-1326,-898,1);
		tree.createTree(-1316,-867,0);
		tree.createTree(-1314,-886,1);
		tree.createTree(-1310,-840,2);
		tree.createTree(-1291,-817,1);
		tree.createTree(-1278,-792,1);
		tree.createTree(-1262,-769,2);
		tree.createTree(-1279,-742,0);
		tree.createTree(-1259,-715,2);
		tree.createTree(-1273,-693,0);
		tree.createTree(-1276,-673,1);
		tree.createTree(-1276,-645,2);
		tree.createTree(-1268,-616,2);
		tree.createTree(-1286,-582,1);
		tree.createTree(-1262,-595,1);
		tree.createTree(-1272,-555,0);
		tree.createTree(-1266,-533,2);
		tree.createTree(-1266,-495,2);
		tree.createTree(-1246,-511,1);
		tree.createTree(-1269,-478,0);
		tree.createTree(-1283,-460,1);
		tree.createTree(-1283,-434,0);
		tree.createTree(-1283,-416,2);
		tree.createTree(-1284,-392,0);
		tree.createTree(-1290,-365,1);
		tree.createTree(-1296,-343,1);
		tree.createTree(-1300,-308,1);
		tree.createTree(-1301,-281,2);
		tree.createTree(-1305,-256,2);
		tree.createTree(-1296,-316,0);
		tree.createTree(-1321,-238,2);
		tree.createTree(-1343,-204,1);
		tree.createTree(-1346,-223,1);
		tree.createTree(-1382,-177,1);
		tree.createTree(-1362,-193,2);
		tree.createTree(-1411,-147,2);
		tree.createTree(-1402,-166,2);
		tree.createTree(-1432,-126,1);
		tree.createTree(-1436,-94,0);
		tree.createTree(-1463,-110,0);
		tree.createTree(-1463,-55,0);
		tree.createTree(-1483,-72,2);
		tree.createTree(-1487,-29,2);
		tree.createTree(-1519,-7,2);
		tree.createTree(-1510,19,0);
		tree.createTree(-1545,41,0);
		tree.createTree(-1494,-55,0);
		tree.createTree(-1646,-536,0);
		tree.createTree(-1673,-516,1);
		tree.createTree(-1702,-512,2);
		tree.createTree(-1718,-478,0);
		tree.createTree(-1743,-489,2);
		tree.createTree(-1718,-506,0);
		tree.createTree(-1721,-447,2);
		tree.createTree(-1746,-465,2);
		tree.createTree(-1771,-432,2);
		tree.createTree(-1768,-449,1);
		tree.createTree(-1801,-413,0);
		tree.createTree(-1805,-385,2);
		tree.createTree(-1832,-361,2);
		tree.createTree(-1847,-423,2);
		tree.createTree(-1852,-401,2);
		tree.createTree(-1892,-417,0);
		tree.createTree(-1913,-389,1);
		tree.createTree(-1941,-414,0);
		tree.createTree(-1965,-398,0);
		tree.createTree(-1987,-417,1);
		tree.createTree(-2026,-406,0);
		tree.createTree(-2043,-437,1);
		tree.createTree(-2066,-429,0);
		tree.createTree(-2089,-410,1);
		tree.createTree(-2120,-434,2);
		tree.createTree(-2140,-411,0);
		tree.createTree(-2178,-423,0);
		tree.createTree(-2207,-415,0);
		tree.createTree(-2231,-436,2);
		tree.createTree(-2259,-422,0);
		tree.createTree(-2291,-441,1);
		tree.createTree(-2324,-418,1);
		tree.createTree(-2352,-431,0);
		tree.createTree(-2399,-432,0);
		tree.createTree(-2382,-408,2);
		tree.createTree(-2435,-430,2);
		tree.createTree(-2455,-413,2);
		tree.createTree(-2491,-434,2);
		tree.createTree(-2517,-416,2);
		tree.createTree(-2550,-434,1);
		tree.createTree(-2576,-418,2);
		tree.createTree(-2616,-437,1);
		tree.createTree(-2638,-415,0);
		tree.createTree(-2670,-436,1);
		tree.createTree(-2720,-458,1);
		tree.createTree(-2740,-438,1);
		tree.createTree(-2774,-452,2);
		tree.createTree(-2813,-424,0);
		tree.createTree(-2843,-443,2);
		tree.createTree(-2878,-447,0);
		tree.createTree(-2901,-427,1);
		tree.createTree(-2936,-427,1);
		tree.createTree(-2960,-441,2);
		tree.createTree(-2980,-427,2);
		tree.createTree(-2687,-1228,0);
		tree.createTree(-291,-3931,2);
		tree.createTree(-291,-3904,2);
		tree.createTree(-311,-3866,1);
		tree.createTree(-278,-3880,2);
		tree.createTree(-283,-3840,1);
		tree.createTree(-305,-3814,0);
		tree.createTree(-274,-3785,0);
		tree.createTree(-309,-3759,0);
		tree.createTree(-293,-3732,0);
		tree.createTree(-293,-3708,1);
		tree.createTree(-293,-3687,2);
		tree.createTree(-311,-3666,2);
		tree.createTree(-296,-3645,0);
		tree.createTree(-317,-3624,2);
		tree.createTree(-302,-3597,1);
		tree.createTree(-298,-3948,1);
		tree.createTree(-300,-3964,0);
		tree.createTree(-318,-3988,1);
		tree.createTree(-334,-4014,1);
		tree.createTree(-364,-4038,1);
		tree.createTree(-342,-3657,2);
		tree.createTree(-383,-3639,2);
		tree.createTree(-433,-3643,1);
		tree.createTree(-406,-3646,2);
		tree.createTree(-465,-3656,1);
		tree.createTree(-503,-3681,0);
		tree.createTree(-386,-4052,0);
		tree.createTree(-422,-4048,2);
		tree.createTree(-463,-4063,2);
		tree.createTree(-488,-4077,0);
		tree.createTree(-518,-4082,0);
		tree.createTree(-539,-3690,2);
		tree.createTree(-565,-3683,1);
		tree.createTree(-590,-3699,1);
		tree.createTree(-614,-3682,0);
		tree.createTree(-642,-3698,0);
		tree.createTree(-664,-3691,0);
		tree.createTree(-549,-4087,1);
		tree.createTree(-592,-4087,1);
		tree.createTree(-641,-4075,1);
		tree.createTree(-623,-4087,1);
		tree.createTree(-678,-4068,1);
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
	
	// Create zone events.
	public void loadZoneEvents() {
		
		// Storm stuff
		stormInProgress = new event("sheepFarmStormInProgress");
		
		// Is the zone on fire?
		isOnFire = new event("sheepFarmIsOnFire");
	}
	
	// Deal with the first well we encounters.
	public void dealWithRegionStuff() {
		player currPlayer = player.getCurrentPlayer();
	}
	
	// Storm stuff
	long stormStartTime = 0;
	boolean startStormFromFog = false;
	int howManySecondsUntilStorm = 5;
	
	
	// Do zone specific tasks that aren't monitored by
	// zone specific units. 
	@Override
	public void update() {
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