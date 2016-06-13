package zones.farmLand;
import items.item;
import items.weapons.dagger;
import modes.topDown;
import terrain.chunk;
import terrain.chunkTypes.cave;
import terrain.chunkTypes.grass;
import terrain.chunkTypes.water;
import terrain.chunkTypes.wood;
import terrain.doodads.farmLand.barn;
import terrain.doodads.farmLand.blackSmith;
import terrain.doodads.farmLand.bush;
import terrain.doodads.farmLand.caveEnterance;
import terrain.doodads.farmLand.farmHouse;
import terrain.doodads.farmLand.fenceBars;
import terrain.doodads.farmLand.fenceBarsSmall;
import terrain.doodads.farmLand.fencePost;
import terrain.doodads.farmLand.hay;
import terrain.doodads.farmLand.horizontalGate;
import terrain.doodads.farmLand.rock;
import terrain.doodads.farmLand.tree;
import terrain.doodads.farmLand.verticalFence;
import units.unit;
import units.unitTypes.farmLand.farmer;
import units.unitTypes.farmLand.sheep;
import utilities.intTuple;
import utilities.utility;
import zones.zone;

public class sheepFarm extends zone {
	
	//////////////////////////////////
	// FIELDS, GLOBALS, CONSTRUCTOR //
	//////////////////////////////////
	
	// Static caller of the zone.
	private static zone zoneReference;
	
	// References we will use throughout.
	unit u;
	chunk c;
	
	// Defaults
	public static intTuple DEFAULT_SPAWN_TUPLE = new intTuple(-3,33);
	
	// Constructor
	public sheepFarm() {
		super("sheepFarm", "farmLand");
	}
	
	///////////////////////////////
	// SPAWN PATTERNS/GENERATORS //
	///////////////////////////////
	
	// Spawn grass from x to y.
	public void spawnForestMeta(int x1, int y1, int x2, int y2) {	
		for(int i = x1; i < x2; i = i += tree.DEFAULT_CHUNK_WIDTH) {
			for(int j = y1; j < y2; j += tree.DEFAULT_CHUNK_HEIGHT) {
				int spawnTree = utility.RNG.nextInt(100);
				int diffX = utility.RNG.nextInt(60);
				int diffY = utility.RNG.nextInt(60);
				int t = utility.RNG.nextInt(6);
				if(t>=3) t = 0;
				else if(t>=1) t = 1;
				else t=2;
				if(spawnTree<50) {
					System.out.println("c = new tree(" + (i+diffX) + "," + (j+diffY) + "," + t + ");");
					c = new tree(i+diffX,j+diffY,t);
				}
				else if(spawnTree<=60) {
					System.out.println("c = new bush(" + (i+diffX) + "," + (j+diffY) + "," + t + ");");
					c = new bush(i+diffX,j+diffY,t);
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
	public void spawnFence(int x1, int y1, int x2, int y2) {
		// The fence is vertical.
		if(x2==x1) {
			int numY = (y2 - y1)/verticalFence.DEFAULT_CHUNK_HEIGHT;
			for(int j = 0; j < numY; j++) {
					
				// Bottom of fence.
				if(j==numY-1) c = new verticalFence(verticalFence.DEFAULT_CHUNK_WIDTH + x1, j*60 + y1, 1);
					
				// Anything in between.
				else c = new verticalFence(verticalFence.DEFAULT_CHUNK_WIDTH + x1, j*60 + y1, 0);
			}
		}
		if(y2==y1) {
				int numX = (x2 - x1)/(fencePost.DEFAULT_CHUNK_WIDTH + fenceBars.DEFAULT_CHUNK_WIDTH);
				for(int j = 0; j < numX; j++) {
					// Far left of fence.
					if(j==0) c = new fencePost(j*fencePost.DEFAULT_CHUNK_WIDTH + x1, fencePost.DEFAULT_CHUNK_HEIGHT + y1, 0);
						
					// Middle fence
					else {
						c = new fenceBars((j-1)*fenceBars.DEFAULT_CHUNK_WIDTH + j*fencePost.DEFAULT_CHUNK_WIDTH + x1,fenceBars.DEFAULT_CHUNK_HEIGHT + y1,0);
						c = new fencePost(j*fenceBars.DEFAULT_CHUNK_WIDTH + j*fencePost.DEFAULT_CHUNK_WIDTH + x1, fencePost.DEFAULT_CHUNK_HEIGHT + y1, 0);
					}
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
		
		// Spawn area.
		createSpawnArea();
		
		// Create forest above spawn
		createForest();
		
		// Zone is loaded.
		setZoneLoaded(true);
		
	}
	
	//////////////////////
	// INDIVIDUAL AREAS //
	//////////////////////
	
	// Forest area above spawn.
	public void createForest() {
		///////////////////
		//// FOREST ///////
		///////////////////
		
		// Spawn forest grass
		spawnGrassRect(-2000,-4000,2000,-440);
		
		// Spawn mountain above grass with a little hole for the
		// cave enterance
		spawnMountainRect(-2000,-4460+12,-1712,-4000);
		spawnMountainRect(-1700-12,-4460+12,2016,-4000-32);
		spawnMountainRect(-1712-30+35+36-5-4,-4035+3,2016,-4000);
		
		// Spawn river to left of mountain
		spawnWaterRect(-2128,-4466+10,-2000,200);
		
		// Spawn river to right of mountain
		spawnWaterRect(2000,-4466+10,2128,200);
		
		// Cave to top left.
		caveEnterance spiderCaveEnterance = new caveEnterance(-1762+20+30,-4070+6+14,0, spiderCave.getZone(),100,-6,"Right");
		
		// Spawn forest trees.
		c = new tree(-2000,-3946,0);
		c = new tree(-1958,-3725,1);
		c = new tree(-1967,-3415,0);
		c = new tree(-2000,-3166,1);
		c = new bush(-1966,-2900,0);
		c = new tree(-1953,-2647,0);
		c = new tree(-1975,-2516,0);
		c = new bush(-1967,-1990,1);
		c = new tree(-1967,-1859,1);
		c = new tree(-1964,-1017,0);
		c = new tree(-1942,-915,0);
		c = new tree(-1995,-495,0);
		c = new tree(-1874,-3983,1);
		c = new tree(-1848,-3817,0);
		c = new tree(-1848,-3547,2);
		c = new bush(-1818,-3466,2);
		c = new tree(-1829,-3044,1);
		c = new tree(-1853,-2880,1);
		c = new tree(-1843,-2795,0);
		c = new tree(-1847,-2358,1);
		c = new tree(-1829,-2098,2);
		c = new tree(-1862,-1818,1);
		c = new tree(-1859,-1720,0);
		c = new bush(-1829,-1553,0);
		c = new tree(-1824,-1462,0);
		c = new bush(-1866,-1286,2);
		c = new tree(-1844,-1035,0);
		c = new tree(-1851,-798,2);
		c = new tree(-1842,-516,0);
		c = new tree(-1708,-3299,2);
		c = new tree(-1724,-2775,1);
		c = new tree(-1750,-2668,0);
		c = new tree(-1744,-2486,0);
		c = new tree(-1731,-2218,2);
		c = new tree(-1699,-2081,1);
		c = new tree(-1705,-1689,0);
		c = new tree(-1736,-1599,1);
		c = new bush(-1702,-1303,0);
		c = new tree(-1744,-1038,2);
		c = new bush(-1744,-917,0);
		c = new tree(-1706,-769,1);
		c = new tree(-1744,-659,1);
		c = new tree(-1712,-526,0);
		c = new tree(-1593,-3860,0);
		c = new tree(-1609,-3558,2);
		c = new tree(-1598,-3333,0);
		c = new tree(-1585,-3184,2);
		c = new tree(-1607,-2767,1);
		c = new bush(-1568,-2401,2);
		c = new tree(-1599,-2224,0);
		c = new tree(-1614,-2101,1);
		c = new tree(-1590,-1730,1);
		c = new tree(-1581,-1601,0);
		c = new tree(-1602,-1420,2);
		c = new tree(-1566,-1025,0);
		c = new bush(-1594,-930,1);
		c = new tree(-1614,-764,0);
		c = new bush(-1444,-3953,0);
		c = new bush(-1477,-3816,0);
		c = new tree(-1498,-3692,1);
		c = new tree(-1468,-3553,1);
		c = new tree(-1475,-3421,2);
		c = new bush(-1464,-3320,1);
		c = new tree(-1475,-3147,0);
		c = new bush(-1477,-3023,1);
		c = new tree(-1500,-2918,0);
		c = new tree(-1453,-2666,1);
		c = new tree(-1455,-2398,0);
		c = new bush(-1450,-2082,2);
		c = new tree(-1499,-1958,1);
		c = new tree(-1492,-1834,0);
		c = new tree(-1470,-1707,1);
		c = new tree(-1458,-1336,1);
		c = new tree(-1492,-1194,1);
		c = new bush(-1455,-1065,1);
		c = new tree(-1446,-930,1);
		c = new bush(-1445,-769,2);
		c = new tree(-1447,-675,0);
		c = new tree(-1477,-483,0);
		c = new tree(-1354,-3986,0);
		c = new tree(-1318,-3866,0);
		c = new tree(-1360,-3682,0);
		c = new tree(-1337,-3410,0);
		c = new tree(-1333,-3314,0);
		c = new tree(-1354,-3178,2);
		c = new tree(-1370,-3058,1);
		c = new bush(-1368,-2479,0);
		c = new tree(-1322,-2260,1);
		c = new bush(-1375,-2128,0);
		c = new tree(-1363,-1835,1);
		c = new tree(-1316,-1724,0);
		c = new tree(-1356,-1583,0);
		c = new bush(-1354,-1438,0);
		c = new bush(-1368,-1168,0);
		c = new tree(-1359,-1030,2);
		c = new tree(-1324,-753,0);
		c = new tree(-1367,-484,1);
		c = new tree(-1235,-3997,2);
		c = new tree(-1238,-3835,2);
		c = new tree(-1246,-3727,2);
		c = new tree(-1235,-3579,1);
		c = new tree(-1243,-3153,2);
		c = new tree(-1247,-2925,1);
		c = new tree(-1198,-2762,0);
		c = new tree(-1203,-2518,1);
		c = new tree(-1194,-2227,2);
		c = new tree(-1205,-2125,1);
		c = new bush(-1234,-1976,1);
		c = new bush(-1217,-1868,0);
		c = new tree(-1204,-1555,1);
		c = new tree(-1245,-1308,0);
		c = new tree(-1213,-1023,0);
		c = new tree(-1236,-667,0);
		c = new tree(-1240,-520,2);
		c = new tree(-1086,-3811,0);
		c = new bush(-1095,-3561,2);
		c = new tree(-1099,-3022,2);
		c = new bush(-1091,-2918,1);
		c = new tree(-1112,-2509,1);
		c = new tree(-1090,-2223,0);
		c = new tree(-1118,-2109,2);
		c = new bush(-1112,-1835,1);
		c = new tree(-1074,-1554,0);
		c = new tree(-1091,-1422,2);
		c = new tree(-1099,-1036,0);
		c = new tree(-1117,-923,1);
		c = new tree(-1080,-797,0);
		c = new tree(-1070,-625,1);
		c = new bush(-976,-3838,1);
		c = new bush(-979,-3565,0);
		c = new tree(-947,-3331,1);
		c = new tree(-961,-3144,2);
		c = new tree(-979,-3039,1);
		c = new tree(-977,-2930,1);
		c = new tree(-955,-2760,0);
		c = new tree(-960,-2643,0);
		c = new tree(-972,-2368,1);
		c = new tree(-980,-2222,1);
		c = new tree(-947,-1949,0);
		c = new tree(-964,-1716,1);
		c = new tree(-972,-1587,1);
		c = new tree(-950,-1424,0);
		c = new tree(-987,-1335,1);
		c = new tree(-964,-1162,0);
		c = new bush(-980,-1052,1);
		c = new bush(-957,-931,2);
		c = new tree(-967,-674,1);
		c = new tree(-842,-3558,1);
		c = new bush(-822,-3455,1);
		c = new tree(-851,-3031,0);
		c = new tree(-871,-2917,2);
		c = new tree(-847,-2634,0);
		c = new tree(-820,-2532,1);
		c = new tree(-822,-2380,1);
		c = new tree(-870,-2249,0);
		c = new bush(-817,-2094,0);
		c = new tree(-824,-1996,1);
		c = new bush(-847,-1596,1);
		c = new tree(-858,-1325,0);
		c = new tree(-873,-757,0);
		c = new tree(-829,-630,0);
		c = new tree(-698,-3726,0);
		c = new tree(-716,-3465,1);
		c = new tree(-748,-3313,0);
		c = new bush(-729,-3144,1);
		c = new tree(-746,-3048,1);
		c = new tree(-730,-2931,0);
		c = new tree(-707,-2665,2);
		c = new tree(-741,-2235,0);
		c = new bush(-739,-2134,0);
		c = new tree(-712,-1194,1);
		c = new tree(-730,-919,2);
		c = new tree(-714,-788,1);
		c = new tree(-574,-3942,0);
		c = new tree(-585,-3851,0);
		c = new tree(-570,-3691,0);
		c = new tree(-570,-3574,1);
		c = new tree(-593,-3451,1);
		c = new bush(-587,-3292,1);
		c = new tree(-566,-3019,1);
		c = new tree(-598,-2642,0);
		c = new tree(-573,-2497,0);
		c = new tree(-613,-2384,1);
		c = new tree(-598,-2268,0);
		c = new tree(-607,-2130,0);
		c = new bush(-589,-1706,2);
		c = new tree(-621,-1444,2);
		c = new tree(-581,-1282,1);
		c = new bush(-604,-1158,0);
		c = new tree(-592,-891,1);
		c = new tree(-583,-788,0);
		c = new tree(-569,-667,1);
		c = new bush(-606,-536,0);
		c = new tree(-484,-3839,1);
		c = new tree(-472,-3721,1);
		c = new tree(-454,-3592,0);
		c = new tree(-484,-3435,0);
		c = new bush(-470,-3278,0);
		c = new tree(-500,-3015,0);
		c = new tree(-500,-2928,1);
		c = new tree(-464,-2778,0);
		c = new tree(-441,-2643,0);
		c = new tree(-456,-2517,0);
		c = new tree(-495,-2381,0);
		c = new bush(-481,-2228,2);
		c = new bush(-445,-2121,1);
		c = new tree(-484,-1860,0);
		c = new tree(-441,-1579,1);
		c = new bush(-492,-1333,1);
		c = new bush(-449,-1148,1);
		c = new tree(-445,-1035,1);
		c = new bush(-458,-907,0);
		c = new tree(-442,-754,0);
		c = new bush(-496,-661,2);
		c = new bush(-445,-522,0);
		c = new tree(-327,-3825,0);
		c = new tree(-375,-3434,1);
		c = new bush(-318,-3295,0);
		c = new tree(-330,-2648,1);
		c = new tree(-370,-2502,2);
		c = new bush(-357,-2264,2);
		c = new tree(-374,-2082,1);
		c = new tree(-354,-1992,1);
		c = new tree(-353,-1828,0);
		c = new bush(-351,-1701,2);
		c = new tree(-343,-1452,1);
		c = new bush(-370,-1312,1);
		c = new tree(-338,-803,1);
		c = new bush(-374,-665,2);
		c = new tree(-357,-502,0);
		c = new tree(-234,-3820,1);
		c = new bush(-237,-3714,0);
		c = new tree(-240,-3546,0);
		c = new tree(-236,-3466,0);
		c = new bush(-215,-3149,1);
		c = new tree(-226,-3011,2);
		c = new tree(-208,-2887,1);
		c = new tree(-202,-2379,0);
		c = new bush(-206,-1978,2);
		c = new tree(-200,-1417,1);
		c = new tree(-202,-1309,2);
		c = new tree(-222,-1196,0);
		c = new tree(-202,-1047,0);
		c = new tree(-200,-929,2);
		c = new bush(-200,-792,1);
		c = new bush(-74,-3952,0);
		c = new tree(-73,-3841,0);
		c = new tree(-118,-3566,1);
		c = new tree(-111,-3425,0);
		c = new tree(-74,-2933,2);
		c = new tree(-94,-2536,0);
		c = new tree(-97,-2345,1);
		c = new tree(-68,-1726,2);
		c = new bush(-109,-1558,0);
		c = new bush(-72,-1438,0);
		c = new tree(-69,-1303,1);
		c = new bush(-88,-1159,0);
		c = new tree(-95,-1016,1);
		c = new tree(-73,-804,0);
		c = new tree(-98,-665,0);
		c = new bush(24,-3969,0);
		c = new tree(28,-3720,0);
		c = new tree(38,-3543,0);
		c = new tree(18,-3460,0);
		c = new bush(53,-3185,1);
		c = new bush(57,-3044,0);
		c = new tree(50,-2746,0);
		c = new tree(35,-2363,2);
		c = new tree(54,-2269,0);
		c = new tree(55,-1827,0);
		c = new tree(52,-1684,0);
		c = new tree(43,-1334,1);
		c = new tree(6,-1189,1);
		c = new tree(27,-1066,0);
		c = new tree(27,-774,0);
		c = new bush(52,-657,0);
		c = new tree(5,-533,1);
		c = new tree(142,-3993,0);
		c = new tree(129,-3827,1);
		c = new tree(148,-3589,0);
		c = new tree(158,-3416,0);
		c = new tree(166,-3325,2);
		c = new tree(183,-3143,0);
		c = new tree(182,-2888,0);
		c = new tree(136,-2780,1);
		c = new tree(149,-2621,1);
		c = new bush(152,-2502,0);
		c = new tree(125,-2354,2);
		c = new tree(128,-1955,1);
		c = new tree(173,-1551,1);
		c = new tree(137,-1446,1);
		c = new tree(154,-1284,0);
		c = new tree(177,-1180,0);
		c = new bush(158,-1065,0);
		c = new tree(166,-930,1);
		c = new bush(132,-794,1);
		c = new tree(138,-628,0);
		c = new tree(154,-521,1);
		c = new tree(287,-3999,1);
		c = new tree(260,-3675,0);
		c = new tree(271,-3567,0);
		c = new tree(268,-3444,0);
		c = new tree(302,-3309,0);
		c = new tree(280,-3039,0);
		c = new tree(300,-2659,1);
		c = new tree(274,-2517,0);
		c = new bush(268,-2388,0);
		c = new tree(270,-2266,2);
		c = new tree(252,-2106,0);
		c = new bush(279,-1845,1);
		c = new tree(290,-1693,0);
		c = new tree(270,-1304,1);
		c = new tree(301,-1027,0);
		c = new tree(298,-898,2);
		c = new bush(266,-749,0);
		c = new tree(264,-535,1);
		c = new bush(433,-3955,0);
		c = new bush(380,-3866,1);
		c = new tree(389,-3700,0);
		c = new tree(429,-3144,0);
		c = new tree(385,-3062,1);
		c = new tree(402,-2880,1);
		c = new tree(379,-2782,2);
		c = new tree(391,-2484,0);
		c = new tree(384,-2351,1);
		c = new bush(418,-2227,2);
		c = new bush(384,-2083,2);
		c = new tree(427,-1962,1);
		c = new tree(377,-1289,0);
		c = new tree(391,-1026,0);
		c = new bush(377,-642,0);
		c = new tree(508,-3942,0);
		c = new tree(519,-3858,0);
		c = new tree(501,-3701,0);
		c = new tree(517,-3599,0);
		c = new tree(513,-3145,2);
		c = new tree(527,-3020,2);
		c = new tree(558,-2627,0);
		c = new bush(506,-2525,0);
		c = new tree(541,-2247,1);
		c = new tree(544,-1948,0);
		c = new tree(501,-1841,0);
		c = new bush(532,-1580,0);
		c = new tree(541,-1419,0);
		c = new tree(546,-1295,1);
		c = new tree(531,-1165,0);
		c = new tree(534,-1033,1);
		c = new tree(513,-886,1);
		c = new bush(559,-783,0);
		c = new tree(634,-3943,0);
		c = new tree(640,-3833,0);
		c = new tree(641,-3577,0);
		c = new tree(650,-3291,1);
		c = new tree(660,-3201,0);
		c = new tree(651,-3059,0);
		c = new tree(658,-2920,0);
		c = new tree(631,-2645,2);
		c = new tree(648,-1860,2);
		c = new bush(683,-1024,1);
		c = new tree(639,-927,0);
		c = new tree(631,-772,0);
		c = new tree(673,-634,0);
		c = new tree(643,-507,1);
		c = new bush(781,-3981,2);
		c = new tree(809,-3729,1);
		c = new tree(785,-3598,2);
		c = new tree(801,-3409,1);
		c = new tree(776,-3330,0);
		c = new tree(762,-3191,0);
		c = new tree(776,-3010,2);
		c = new tree(752,-2785,2);
		c = new tree(785,-2262,2);
		c = new tree(791,-2123,0);
		c = new bush(755,-1968,2);
		c = new tree(776,-1851,0);
		c = new tree(795,-1685,0);
		c = new tree(755,-511,0);
		c = new tree(906,-3968,2);
		c = new tree(918,-3545,0);
		c = new tree(912,-3410,0);
		c = new tree(907,-3159,2);
		c = new tree(879,-2889,0);
		c = new tree(906,-2774,1);
		c = new tree(900,-2520,1);
		c = new tree(908,-2364,0);
		c = new tree(893,-2237,1);
		c = new tree(911,-2116,0);
		c = new tree(892,-1963,0);
		c = new bush(920,-1866,2);
		c = new tree(877,-1683,1);
		c = new tree(927,-1597,1);
		c = new tree(915,-1464,0);
		c = new tree(918,-1206,2);
		c = new tree(882,-1037,0);
		c = new tree(882,-892,2);
		c = new tree(890,-756,0);
		c = new tree(893,-655,1);
		c = new tree(925,-490,0);
		c = new bush(1001,-3822,0);
		c = new tree(1035,-3691,2);
		c = new tree(1012,-3467,0);
		c = new tree(1031,-3184,2);
		c = new tree(1035,-2930,0);
		c = new bush(1020,-2792,0);
		c = new tree(1013,-2660,0);
		c = new tree(1016,-2402,0);
		c = new tree(1014,-2002,2);
		c = new tree(1013,-1680,1);
		c = new tree(1030,-1589,1);
		c = new tree(1029,-1042,0);
		c = new tree(1019,-914,2);
		c = new tree(1036,-802,1);
		c = new tree(1016,-642,1);
		c = new tree(1045,-491,1);
		c = new bush(1162,-3728,2);
		c = new tree(1180,-3549,0);
		c = new tree(1154,-3457,1);
		c = new tree(1140,-3326,0);
		c = new tree(1128,-3036,0);
		c = new tree(1143,-2527,1);
		c = new tree(1161,-2352,1);
		c = new tree(1160,-2229,1);
		c = new tree(1149,-1844,0);
		c = new tree(1158,-1693,0);
		c = new tree(1133,-1599,1);
		c = new bush(1181,-1322,0);
		c = new tree(1182,-1193,0);
		c = new bush(1183,-883,1);
		c = new tree(1168,-640,1);
		c = new tree(1267,-3844,1);
		c = new tree(1282,-3681,0);
		c = new tree(1270,-3585,1);
		c = new tree(1282,-3432,0);
		c = new tree(1255,-3185,1);
		c = new tree(1277,-3047,1);
		c = new tree(1263,-2878,1);
		c = new tree(1272,-2489,1);
		c = new tree(1300,-2254,0);
		c = new tree(1303,-1979,2);
		c = new tree(1299,-1836,2);
		c = new tree(1269,-1699,0);
		c = new tree(1278,-1561,2);
		c = new bush(1295,-1423,2);
		c = new tree(1289,-1164,0);
		c = new tree(1273,-1058,1);
		c = new tree(1268,-883,0);
		c = new tree(1281,-644,0);
		c = new tree(1274,-538,0);
		c = new tree(1405,-3943,2);
		c = new bush(1402,-3842,0);
		c = new tree(1399,-3727,0);
		c = new tree(1420,-2878,2);
		c = new tree(1434,-2800,2);
		c = new bush(1406,-2627,2);
		c = new bush(1421,-2388,2);
		c = new tree(1390,-2136,1);
		c = new tree(1407,-1992,1);
		c = new bush(1432,-1570,2);
		c = new tree(1385,-1452,0);
		c = new tree(1387,-1333,0);
		c = new tree(1409,-1060,0);
		c = new tree(1432,-891,1);
		c = new tree(1377,-793,0);
		c = new tree(1388,-637,0);
		c = new tree(1434,-498,0);
		c = new tree(1541,-3987,1);
		c = new tree(1549,-3703,0);
		c = new bush(1539,-3594,1);
		c = new tree(1511,-3411,1);
		c = new bush(1527,-3335,0);
		c = new tree(1503,-2931,2);
		c = new tree(1525,-2622,1);
		c = new tree(1512,-2485,0);
		c = new tree(1526,-2249,0);
		c = new tree(1522,-2129,0);
		c = new tree(1545,-1583,1);
		c = new tree(1540,-1291,1);
		c = new tree(1535,-669,0);
		c = new bush(1652,-3826,0);
		c = new tree(1658,-3157,1);
		c = new tree(1673,-3011,0);
		c = new tree(1668,-2907,0);
		c = new bush(1652,-2779,2);
		c = new tree(1635,-2525,0);
		c = new tree(1652,-2095,1);
		c = new tree(1631,-1946,2);
		c = new tree(1678,-1864,0);
		c = new tree(1678,-1693,0);
		c = new tree(1659,-1416,0);
		c = new tree(1636,-1314,1);
		c = new tree(1650,-1065,2);
		c = new tree(1640,-757,2);
		c = new tree(1633,-636,2);
		c = new tree(1762,-3998,0);
		c = new bush(1781,-3325,0);
		c = new tree(1755,-3175,0);
		c = new tree(1791,-3048,1);
		c = new bush(1765,-2766,1);
		c = new tree(1779,-2533,0);
		c = new tree(1801,-2372,0);
		c = new tree(1801,-2108,1);
		c = new tree(1760,-1552,0);
		c = new tree(1793,-1473,1);
		c = new tree(1793,-1288,0);
		c = new tree(1771,-1205,0);
		c = new tree(1753,-1046,0);
		c = new tree(1780,-772,0);
		c = new bush(1806,-636,1);
		c = new tree(1774,-536,2);
		c = new bush(1904,-3996,0);
		c = new bush(1927,-3849,1);
		c = new tree(1899,-3707,0);
		c = new tree(1907,-3430,1);
		c = new tree(1928,-3013,0);
		c = new tree(1896,-2761,0);
		c = new bush(1905,-2263,0);
		c = new bush(1911,-2092,0);
		c = new tree(1906,-2003,1);
		c = new tree(1893,-1815,1);
		c = new tree(1914,-1680,0);
		c = new tree(1900,-1285,2);
		c = new tree(1934,-1166,1);
		c = new tree(1892,-1045,0);
		c = new tree(1898,-887,2);
		c = new tree(1902,-515,0);
	}
	
	// Spawn area.
	public void createSpawnArea() {
		
		//////////////////////////
		//// ENTIRE ZONE STUFF ///
		//////////////////////////
		// Draw the grass around spawn.
		spawnGrassRect(-2000,-1000,2000,64);
		
		// Spawn some grass on the other side of the bridge.
		spawnGrassRect(-2000,184,2000,1000);
		
		//////////////////
		//// PENS  ///////
		//////////////////

		// Draw field on the left of spawn.
		int fenceAdjustX = -6;
		spawnFence(-30+fenceAdjustX,-435,-30+fenceAdjustX,200); // Vertical, right
		spawnFence(-1050+fenceAdjustX+17,-462,10+fenceAdjustX,-462); // Horizontal, top of field
		spawnFence(-168-3+28,17,20-3,17); // Horizontal, right of bridge.
		spawnFence(-1050+fenceAdjustX+17,17,-150,17); // Horizontal, left of bridge.
		spawnFence(-450+fenceAdjustX,-436,-450+fenceAdjustX,200); // Vertical, far left
		u = new sheep(-378,-369);
		u = new sheep(-150,-372);
		u = new sheep(-129,-60);
		u = new sheep(-372,-36);
		c = new hay(-294,-315,0);
		c = new hay(-195,-165,0);
		
		// Draw the bridge.
		spawnWoodRect(-180-5,56,-140,200);
		
		// Draw the water to left of bridge spawn.
		spawnWaterRect(-2000-9,56,-170,200);	
		
		// Draw the water to right of bridge spawn.
		spawnWaterRect(-168+20-5,56,2032,200);
		
		// Draw fence on the right.
		spawnFence(40,-462,500,-462); // Horizontal, top of field
		spawnFence(40,17,500,17); // Horizontal, bottom
		spawnFence(35,-436,35,200); // Vertical, left
		spawnFence(455,-436,455,300); // Vertical, right
		
		// Draw the fence gate above the fields.
		// Fencebars to left of gate.
		c = new fenceBarsSmall(-21+fenceAdjustX,-436,0);
		c = new fenceBarsSmall(-18+fenceAdjustX,-436,0);
		c = new fenceBarsSmall(-15+fenceAdjustX,-436,0);
		c = new fenceBarsSmall(-15+fenceAdjustX+3,-436,0);
		
		// Fencebars to right of gate
		c = new fenceBarsSmall(32-3,-436,0);
		c = new fenceBarsSmall(32,-436,0);
		c = new fenceBarsSmall(35,-436,0);
		c = new fenceBarsSmall(37,-436,0);
		horizontalGate forestGate = new horizontalGate(-13+fenceAdjustX/2,-434,0);
		forestGate.open();
		
		////////////////////////////
		//// FARMHOUSE AREA  ///////
		////////////////////////////
		c = new tree(-720,-325,0);
		c = new farmHouse(-650,-420,0);
		c = new barn(-950,-420,0);
		spawnFence(-1038-6,-436,-1038-6,200+50); // Vertical, far left
		farmer theFarmer = new farmer(-711,-267);
		theFarmer.setFacingDirection("Down");
		c = new tree(-1017, -414, 1);
		c = new tree(-1011, 0, 0);
		c = new hay(-960,-351,1);
		c = new hay(-875,-351,1);
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