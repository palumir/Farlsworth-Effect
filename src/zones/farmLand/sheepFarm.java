package zones.farmLand;
import java.util.ArrayList;
import java.util.Arrays;

import doodads.cave.firePit;
import doodads.sheepFarm.barn;
import doodads.sheepFarm.blackSmith;
import doodads.sheepFarm.bridge;
import doodads.sheepFarm.bridgePole;
import doodads.sheepFarm.bush;
import doodads.sheepFarm.clawMarkRed;
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
import drawing.background;
import drawing.userInterface.tooltipString;
import effects.effectTypes.fire;
import interactions.event;
import items.bottle;
import items.item;
import items.bottles.normalBottle;
import items.weapons.dagger;
import items.weapons.sword;
import modes.topDown;
import sounds.music;
import sounds.sound;
import terrain.chunk;
import terrain.atmosphericEffects.fog;
import terrain.atmosphericEffects.storm;
import terrain.chunkTypes.cave;
import terrain.chunkTypes.grass;
import terrain.chunkTypes.water;
import units.player;
import units.unit;
import units.unitCommand;
import units.bosses.denmother;
import units.bosses.farlsworth;
import units.unitCommands.commandList;
import units.unitCommands.moveCommand;
import units.unitCommands.slashCommand;
import units.unitCommands.waitCommand;
import units.unitTypes.farmLand.sheepFarm.blackWolf;
import units.unitTypes.farmLand.sheepFarm.farmer;
import units.unitTypes.farmLand.sheepFarm.redWolf;
import units.unitTypes.farmLand.sheepFarm.sheep;
import units.unitTypes.farmLand.sheepFarm.wolf;
import units.unitTypes.farmLand.sheepFarm.yellowWolf;
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
				if(c!=null) {
					c.setInteractable(false);
					c.setPassable(true);
				}
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
	
	// Commands
	commandList commands;
	
	// Create a move list of wolves
	public void createChain(commandList c, String commandType, String wolfColor, float waitFor, float spawnClawPhaseTime, float speed, ArrayList<Integer> missingLinks) {		
		if(commandType.equals("move")) {
			for(int i = 0; i < c.size(); i++) {
				if(missingLinks.contains(i)) {
					// Do nothing
				}
				else {
					if(wolfColor.equals("red"))	u = new redWolf((int)((moveCommand) c.get(i)).getX(), (int)((moveCommand) c.get(i)).getY());
					if(wolfColor.equals("yellow"))	u = new yellowWolf((int)((moveCommand) c.get(i)).getX(), (int)((moveCommand) c.get(i)).getY());
					if(wolfColor.equals("black"))	u = new blackWolf((int)((moveCommand) c.get(i)).getX(), (int)((moveCommand) c.get(i)).getY());
					((wolf) u).setSpawnClawPhaseTime(spawnClawPhaseTime);
					((wolf) u).setMoveSpeed(speed);
					int n = i+1;
					commands = new commandList();
					for(int j = 0; j < c.size(); j++) {
						if(n >= c.size())  n = 0;
						if(waitFor!=0) commands.add(new waitCommand(waitFor));
						commands.add(new moveCommand((moveCommand)c.get(n)));
						n++;
					}
					u.repeatCommands(commands);
				}
			}
		}
		else if(commandType.equals("slash")) {
			for(int i = 0; i < c.size(); i++) {
				if(missingLinks.contains(i)) {
					// Do nothing
				}
				else {
					if(wolfColor.equals("red"))	u = new redWolf((int)((slashCommand) c.get(i)).getX(), (int)((slashCommand) c.get(i)).getY());
					if(wolfColor.equals("yellow"))	u = new yellowWolf((int)((slashCommand) c.get(i)).getX(), (int)((slashCommand) c.get(i)).getY());
					if(wolfColor.equals("black"))	u = new blackWolf((int)((slashCommand) c.get(i)).getX(), (int)((slashCommand) c.get(i)).getY());
					((wolf) u).setSpawnClawPhaseTime(spawnClawPhaseTime);
					((wolf) u).setJumpSpeed(speed);
					int n = i+1;
					commands = new commandList();
					for(int j = 0; j < c.size(); j++) {
						if(n >= c.size())  n = 0;
						if(waitFor!=0) commands.add(new waitCommand(waitFor));
						commands.add(new slashCommand((slashCommand)c.get(n)));
						n++;
					}
					u.repeatCommands(commands);
				}
			}
		}
	}
	
	// Spawn creeps
	public void spawnUnits() {
		
		// One over root 2
		double oneOverRoot2 = 1/(Math.sqrt(2));
		
		// Farlsworth
		farlsworth sheepBoss = new farlsworth(411,-394);
		if(!farlsworth.isFenceAttached.isCompleted()) {
			farlsworthFence = makeFarlsworthFence(5,-406);
		}
		else {
			farlsworthFence = null;
		}
		
		// Wolf height and width
		int slashAdjustX = wolf.getDefaultWidth()/2/* - clawMarkRed.DEFAULT_CHUNK_WIDTH/2*/;
		int slashAdjustY = wolf.getDefaultHeight()/2/* - clawMarkRed.DEFAULT_CHUNK_HEIGHT/2*/;
		
		// Wolf holder
		wolf w;
		
		/////////////////////////////
		/// Wolf Section I guess ///
		///////////////////////////
		
		//FUCKING TREES FOR NOW!!!
		
		c = new tree(-233,-448,2);
		c = new tree(-233,-458,1);
		c = new tree(-233,-518,1);
		c = new tree(-233,-568,1);
		c = new tree(-233,-598,1);
		c = new tree(-233,-638,1);
		c = new tree(-233,-688,0);
		c = new tree(-233,-738,2);
		c = new tree(-233,-798,2);
		c = new tree(-233,-848,2);
		c = new tree(-233,-888,1);
		c = new tree(-233,-938,1);
		c = new tree(-271,-976,1);
		c = new tree(-325,-1030,1);
		c = new tree(-340,-1065,1);
		c = new tree(-340,-1135,1);
		c = new tree(-340,-1195,2);
		c = new tree(-340,-1275,2);
		c = new tree(-340,-1345,2);
		c = new tree(-340,-1395,0);
		c = new tree(-340,-1435,1);
		c = new tree(-340,-1495,1);
		c = new tree(-340,-1555,0);
		c = new tree(-340,-1605,2);
		c = new tree(-340,-1685,1);
		c = new tree(-340,-1715,1);
		c = new tree(-340,-1785,1);
		c = new tree(-340,-1845,0);
		c = new tree(-340,-1895,1);
		c = new tree(-340,-1945,0);
		c = new tree(259,-445,1);
		c = new tree(259,-505,0);
		c = new tree(259,-555,0);
		c = new tree(259,-605,0);
		c = new tree(259,-645,1);
		c = new tree(259,-695,2);
		c = new tree(259,-745,0);
		c = new tree(259,-795,1);
		c = new tree(259,-845,1);
		c = new tree(299,-885,1);
		c = new tree(353,-959,0);
		c = new tree(353,-889,2);
		c = new tree(353,-929,1);
		c = new tree(353,-919,2);
		c = new tree(323,-919,2);
		c = new tree(377,-1023,2);
		c = new tree(397,-1083,0);
		c = new tree(373,-1166,2);
		c = new tree(373,-1096,1);
		c = new tree(323,-1096,2);
		c = new tree(323,-1016,1);
		c = new tree(323,-1106,0);
		c = new tree(323,-1176,0);
		c = new tree(303,-1236,1);
		c = new tree(320,-1299,2);
		c = new tree(351,-1370,1);
		c = new tree(351,-1430,1);
		c = new tree(351,-1490,1);
		c = new tree(351,-1560,1);
		c = new tree(351,-1610,2);
		c = new tree(351,-1680,0);
		c = new tree(351,-1760,2);
		c = new tree(351,-1840,0);
		c = new tree(351,-1920,2);
		c = new tree(351,-2000,0);
		c = new tree(-308,-2020,2);
		c = new tree(-258,-2020,1);
		c = new tree(-212,-2116,0);
		c = new tree(-212,-2076,0);
		c = new tree(-262,-2076,2);
		c = new tree(-262,-2036,2);
		c = new tree(-200,-2157,0);
		c = new tree(-200,-2157,2);
		c = new tree(-142,-2196,2);
		c = new tree(-73,-2294,1);
		c = new tree(-126,-2241,1);
		c = new tree(-86,-2241,1);
		c = new tree(-86,-2291,1);
		c = new tree(-33,-2354,2);
		c = new tree(-33,-2354,0);
		c = new tree(16,-2354,1);
		c = new tree(46,-2354,0);
		c = new tree(46,-2354,0);
		c = new tree(106,-2354,2);
		c = new tree(106,-2354,0);
		c = new tree(136,-2354,0);
		c = new tree(166,-2354,2);
		c = new tree(186,-2354,1);
		c = new tree(236,-2354,0);
		c = new tree(276,-2354,2);
		c = new tree(336,-2354,0);
		c = new tree(336,-2354,0);
		c = new tree(376,-2354,2);
		c = new tree(376,-2354,2);
		c = new tree(436,-2354,2);
		c = new tree(456,-2354,0);
		c = new tree(476,-2354,1);
		c = new tree(546,-2354,2);
		c = new tree(546,-2354,0);
		c = new tree(496,-2354,1);
		c = new tree(556,-2354,2);
		c = new tree(616,-2354,0);
		c = new tree(576,-2354,2);
		c = new tree(576,-2294,1);
		c = new tree(576,-2294,0);
		c = new tree(576,-2254,1);
		c = new tree(576,-2254,0);
		c = new tree(576,-2204,2);
		c = new tree(576,-2184,2);
		c = new tree(576,-2184,0);
		c = new tree(576,-2134,2);
		c = new tree(576,-2134,1);
		c = new tree(576,-2104,0);
		c = new tree(576,-2104,2);
		c = new tree(576,-2054,2);
		c = new tree(576,-2104,1);
		c = new tree(576,-2054,0);
		c = new tree(576,-2054,1);
		c = new tree(576,-2004,1);
		c = new tree(576,-2074,2);
		c = new tree(576,-2014,0);
		c = new tree(576,-1974,0);
		c = new tree(576,-1934,0);
		c = new tree(576,-1904,0);
		c = new tree(376,-1884,2);
		c = new tree(343,-1841,2);
		c = new tree(380,-1754,2);
		c = new tree(380,-1754,2);
		c = new tree(380,-1724,2);
		c = new tree(380,-1644,1);
		c = new tree(380,-1604,2);
		c = new tree(380,-1544,2);
		c = new tree(380,-1494,1);
		c = new tree(380,-1444,0);
		c = new tree(380,-1494,2);
		c = new tree(380,-1424,0);
		c = new tree(380,-1264,1);
		c = new tree(380,-1174,2);
		c = new tree(380,-1224,1);
		c = new tree(411,-1304,2);
		c = new tree(411,-1304,2);
		c = new tree(341,-1334,0);
		c = new tree(403,-1496,0);
		c = new tree(403,-1536,2);
		c = new tree(403,-1636,1);
		c = new tree(403,-1706,0);
		c = new tree(403,-1756,2);
		c = new tree(403,-1796,2);
		c = new tree(403,-1846,1);
		c = new tree(403,-1926,1);
		c = new tree(403,-1996,1);
		c = new tree(403,-2046,0);
		c = new tree(23,-2343,2);
		c = new tree(143,-2343,1);
		c = new tree(203,-2343,0);
		c = new tree(203,-2343,0);
		c = new tree(283,-2343,2);
		c = new tree(353,-2343,1);
		c = new tree(403,-2343,2);
		c = new tree(453,-2343,2);
		c = new tree(483,-2343,2);
		c = new tree(503,-2343,2);
		c = new tree(393,-2343,1);
		c = new tree(263,-2343,2);
		c = new tree(193,-2343,0);
		c = new tree(283,-2343,0);
		c = new tree(333,-2343,0);
		c = new tree(333,-2343,1);
		c = new tree(313,-2343,1);
		c = new tree(416,-1289,0);
		c = new tree(476,-1289,0);
		c = new tree(476,-1289,0);
		c = new tree(506,-1289,2);
		c = new tree(546,-1289,2);
		c = new tree(566,-1289,1);
		c = new tree(606,-1289,0);
		c = new tree(656,-1289,0);
		c = new tree(656,-1289,2);
		c = new tree(736,-1289,2);
		c = new tree(796,-1289,0);
		c = new tree(836,-1289,1);
		c = new tree(866,-1289,1);
		c = new tree(896,-1289,2);
		c = new tree(936,-1289,1);
		c = new tree(986,-1289,0);
		c = new tree(996,-1289,2);
		c = new tree(1026,-1289,0);
		c = new tree(1076,-1289,1);
		c = new tree(1116,-1289,0);
		c = new tree(1166,-1289,0);
		c = new tree(1186,-1289,0);
		c = new tree(1206,-1289,0);
		c = new tree(1206,-1289,1);
		c = new tree(1246,-1289,2);
		c = new tree(1246,-1289,1);
		c = new tree(1246,-1289,0);
		c = new tree(1296,-1289,0);
		c = new tree(1296,-1289,1);
		c = new tree(587,-2101,1);
		c = new tree(597,-2101,0);
		c = new tree(637,-2101,1);
		c = new tree(687,-2101,2);
		c = new tree(727,-2101,1);
		c = new tree(727,-2101,0);
		c = new tree(677,-2101,1);
		c = new tree(677,-2101,1);
		c = new tree(677,-2101,1);
		c = new tree(607,-2101,1);
		c = new tree(677,-2101,0);
		c = new tree(677,-2101,1);
		c = new tree(717,-2101,0);
		c = new tree(737,-2101,2);
		c = new tree(777,-2101,0);
		c = new tree(777,-2101,1);
		c = new tree(777,-2101,0);
		c = new tree(827,-2101,0);
		c = new tree(827,-2101,1);
		c = new tree(827,-2101,1);
		c = new tree(827,-2101,0);
		c = new tree(877,-2101,2);
		c = new tree(877,-2101,0);
		c = new tree(877,-2101,2);
		c = new tree(927,-2101,2);
		c = new tree(927,-2101,1);
		c = new tree(927,-2101,1);
		c = new tree(977,-2101,0);
		c = new tree(977,-2101,2);
		c = new tree(1007,-2101,1);
		c = new tree(1007,-2101,2);
		c = new tree(1007,-2101,1);
		c = new tree(1037,-2101,2);
		c = new tree(1057,-2101,1);
		c = new tree(1077,-2101,0);
		c = new tree(1097,-2101,0);
		c = new tree(1127,-2101,2);
		c = new tree(1167,-2101,1);
		c = new tree(1167,-2101,0);
		c = new tree(1217,-2101,1);
		c = new tree(1267,-2101,2);
		c = new tree(1267,-2101,1);
		c = new tree(1317,-2101,0);
		c = new tree(1367,-2101,1);
		c = new tree(1377,-2101,1);
		c = new tree(1417,-2101,0);
		c = new tree(1337,-2101,0);
		c = new tree(1297,-2101,2);
		c = new tree(1257,-2101,0);
		c = new tree(1177,-2101,0);
		c = new tree(1147,-2101,0);
		c = new tree(1097,-2101,2);
		c = new tree(1077,-2101,2);
		c = new tree(997,-2101,0);
		c = new tree(997,-2101,2);
		c = new tree(927,-2101,0);
		c = new tree(927,-2101,1);
		c = new tree(857,-2101,0);
		c = new tree(857,-2101,0);
		c = new tree(787,-2101,0);
		c = new tree(767,-2101,2);
		c = new tree(847,-2101,2);
		c = new tree(947,-2101,1);
		c = new tree(1017,-2101,1);
		c = new tree(1283,-1290,1);
		c = new tree(1153,-1290,1);
		c = new tree(1143,-1290,2);
		c = new tree(1033,-1290,0);
		c = new tree(993,-1290,1);
		c = new tree(943,-1290,2);
		c = new tree(943,-1290,0);
		c = new tree(883,-1290,2);
		c = new tree(863,-1290,0);
		c = new tree(823,-1290,2);
		c = new tree(793,-1290,1);
		c = new tree(753,-1290,1);
		c = new tree(723,-1290,1);
		c = new tree(653,-1290,2);
		c = new tree(603,-1290,1);
		c = new tree(563,-1290,1);
		c = new tree(633,-1290,1);
		c = new tree(723,-1290,2);
		c = new tree(723,-1290,2);
		c = new tree(663,-1290,1);
		c = new tree(663,-1290,1);
		c = new tree(703,-1290,2);
		c = new tree(703,-1290,1);
		c = new tree(693,-1290,2);
		c = new tree(623,-1290,2);
		c = new tree(693,-1290,2);
		c = new tree(693,-1290,0);
		c = new tree(553,-1290,1);
		c = new tree(443,-1290,1);
		c = new tree(573,-1290,1);
		c = new tree(723,-1290,0);
		c = new tree(853,-1290,1);
		c = new tree(923,-1290,0);
		c = new tree(923,-1290,0);
		c = new tree(973,-1290,0);
		c = new tree(1023,-1290,1);
		c = new tree(1063,-1290,1);
		c = new tree(1103,-1290,2);
		c = new tree(1153,-1290,1);
		c = new tree(1193,-1290,2);
		c = new tree(1213,-1290,0);
		c = new tree(1243,-1290,0);
		c = new tree(1243,-1290,0);
		c = new tree(1173,-1290,0);
		c = new tree(1123,-1290,0);
		c = new tree(1073,-1290,2);
		c = new tree(1023,-1290,1);
		c = new tree(953,-1290,0);
		c = new tree(893,-1290,0);
		c = new tree(823,-1290,0);
		
		// Section 1 (outside farm)
		
		w = new redWolf(-161+slashAdjustX,-673+slashAdjustY);
		commands = new commandList();
		commands.add(new slashCommand(139+slashAdjustX,-673+slashAdjustY));
		commands.add(new slashCommand(-161+slashAdjustX,-673+slashAdjustY));
		w.repeatCommands(commands);
		w.setJumpSpeed (10);
		w.setSpawnClawPhaseTime(1.5f);
		
		w = new redWolf(139+slashAdjustX,-773+slashAdjustY);
		commands = new commandList();
		commands.add(new slashCommand(-161+slashAdjustX,-773+slashAdjustY));
		commands.add(new slashCommand(139+slashAdjustX,-773+slashAdjustY));
		w.repeatCommands(commands);
		w.setJumpSpeed (17.5f);
		w.setSpawnClawPhaseTime(1f);
		
		w = new redWolf(-161+slashAdjustX,-873+slashAdjustY);
		commands = new commandList();
		commands.add(new slashCommand(139+slashAdjustX,-873+slashAdjustY));
		commands.add(new slashCommand(-161+slashAdjustX,-873+slashAdjustY));
		w.repeatCommands(commands);
		w.setJumpSpeed (25);
		w.setSpawnClawPhaseTime(0.5f);
		
		// Section 2 (different pattern)
		
		w = new redWolf(-261+slashAdjustX,-973+slashAdjustY);
		commands = new commandList();
		commands.add(new slashCommand(-11+slashAdjustX,-1173+slashAdjustY));
		commands.add(new slashCommand(239+slashAdjustX,-973+slashAdjustY));
		commands.add(new slashCommand(-11+slashAdjustX,-1173+slashAdjustY));
		commands.add(new slashCommand(-261+slashAdjustX,-973+slashAdjustY));
		w.repeatCommands(commands);
		w.setJumpSpeed (10f);
		w.setSpawnClawPhaseTime(1f);
		
		w = new redWolf(-261+slashAdjustX,-1173+slashAdjustY);
		commands = new commandList();
		commands.add(new slashCommand(-11+slashAdjustX,-973+slashAdjustY));
		commands.add(new slashCommand(239+slashAdjustX,-1173+slashAdjustY));
		commands.add(new slashCommand(-11+slashAdjustX,-973+slashAdjustY));
		commands.add(new slashCommand(-261+slashAdjustX,-1173+slashAdjustY));
		w.repeatCommands(commands);
		w.setJumpSpeed (10f);
		w.setSpawnClawPhaseTime(1f);
		
		w = new redWolf(-186+slashAdjustX,-1073+slashAdjustY);
		//Middle jumping wolf
		commands = new commandList();
		commands.add(new slashCommand(164+slashAdjustX,-1073+slashAdjustY));
		commands.add(new slashCommand(-186+slashAdjustX,-1073+slashAdjustY));
		w.repeatCommands(commands);
		w.setJumpSpeed (13f);
		w.setSpawnClawPhaseTime(1f);
		
		// Section 3 (same pattern, more wolves)
		
		w = new redWolf(-261+slashAdjustX,-1273+slashAdjustY);
		commands = new commandList();
		commands.add(new slashCommand(-11+slashAdjustX,-1473+slashAdjustY));
		commands.add(new slashCommand(239+slashAdjustX,-1273+slashAdjustY));
		commands.add(new slashCommand(-11+slashAdjustX,-1473+slashAdjustY));
		commands.add(new slashCommand(-261+slashAdjustX,-1273+slashAdjustY));
		w.repeatCommands(commands);
		w.setJumpSpeed (10f);
		w.setSpawnClawPhaseTime(1f);
		
		w = new redWolf(-11+slashAdjustX,-1473+slashAdjustY);
		commands = new commandList();
		commands.add(new slashCommand(239+slashAdjustX,-1273+slashAdjustY));
		commands.add(new slashCommand(-11+slashAdjustX,-1473+slashAdjustY));
		commands.add(new slashCommand(-261+slashAdjustX,-1273+slashAdjustY));
		commands.add(new slashCommand(-11+slashAdjustX,-1473+slashAdjustY));
		w.repeatCommands(commands);
		w.setJumpSpeed (10f);
		w.setSpawnClawPhaseTime(1f);
		
		w = new redWolf(-261+slashAdjustX,-1473+slashAdjustY);
		commands = new commandList();
		commands.add(new slashCommand(-11+slashAdjustX,-1273+slashAdjustY));
		commands.add(new slashCommand(239+slashAdjustX,-1473+slashAdjustY));
		commands.add(new slashCommand(-11+slashAdjustX,-1273+slashAdjustY));
		commands.add(new slashCommand(-261+slashAdjustX,-1473+slashAdjustY));
		w.repeatCommands(commands);
		w.setJumpSpeed (10f);
		w.setSpawnClawPhaseTime(1f);
		
		w = new redWolf(-11+slashAdjustX,-1273+slashAdjustY);
		commands = new commandList();
		commands.add(new slashCommand(239+slashAdjustX,-1473+slashAdjustY));
		commands.add(new slashCommand(-11+slashAdjustX,-1273+slashAdjustY));
		commands.add(new slashCommand(-261+slashAdjustX,-1473+slashAdjustY));
		commands.add(new slashCommand(-11+slashAdjustX,-1273+slashAdjustY));
		w.repeatCommands(commands);
		w.setJumpSpeed (10f);
		w.setSpawnClawPhaseTime(1f);
		
		w = new redWolf(-186+slashAdjustX,-1373+slashAdjustY);
		//Middle jumping wolf
		commands = new commandList();
		commands.add(new slashCommand(164+slashAdjustX,-1373+slashAdjustY));
		commands.add(new slashCommand(-186+slashAdjustX,-1373+slashAdjustY));
		w.repeatCommands(commands);
		w.setJumpSpeed (13f);
		w.setSpawnClawPhaseTime(1f);
		
		// Section 4
		
			//First half
		
		w = new redWolf(-261+slashAdjustX,-1773+slashAdjustY);
		commands = new commandList();
		commands.add(new slashCommand(-261+slashAdjustX,-1573+slashAdjustY));
		commands.add(new slashCommand(-261+slashAdjustX,-1773+slashAdjustY));
		w.repeatCommands(commands);
		w.setJumpSpeed (10f);
		w.setSpawnClawPhaseTime(1f);
		
		w = new redWolf(-11+slashAdjustX,-1573+slashAdjustY);
		commands = new commandList();
		commands.add(new slashCommand(-11+slashAdjustX,-1773+slashAdjustY));
		commands.add(new slashCommand(-11+slashAdjustX,-1573+slashAdjustY));
		w.repeatCommands(commands);
		w.setJumpSpeed (10f);
		w.setSpawnClawPhaseTime(1f);
		
		w = new redWolf(239+slashAdjustX,-1773+slashAdjustY);
		commands = new commandList();
		commands.add(new slashCommand(239+slashAdjustX,-1573+slashAdjustY));
		commands.add(new slashCommand(239+slashAdjustX,-1773+slashAdjustY));
		w.repeatCommands(commands);
		w.setJumpSpeed (10f);
		w.setSpawnClawPhaseTime(1f);
		
			// Second half
		
		w = new redWolf(-261+slashAdjustX,-1973+slashAdjustY);
		commands = new commandList();
		commands.add(new slashCommand(-261+slashAdjustX,-1773+slashAdjustY));
		commands.add(new slashCommand(-261+slashAdjustX,-1973+slashAdjustY));
		w.repeatCommands(commands);
		w.setJumpSpeed (10f);
		w.setSpawnClawPhaseTime(1f);
		
		w = new redWolf(-11+slashAdjustX,-1773+slashAdjustY);
		commands = new commandList();
		commands.add(new slashCommand(-11+slashAdjustX,-1973+slashAdjustY));
		commands.add(new slashCommand(-11+slashAdjustX,-1773+slashAdjustY));
		w.repeatCommands(commands);
		w.setJumpSpeed (10f);
		w.setSpawnClawPhaseTime(1f);
		
		w = new redWolf(239+slashAdjustX,-1973+slashAdjustY);
		commands = new commandList();
		commands.add(new slashCommand(239+slashAdjustX,-1773+slashAdjustY));
		commands.add(new slashCommand(239+slashAdjustX,-1973+slashAdjustY));
		w.repeatCommands(commands);
		w.setJumpSpeed (10f);
		w.setSpawnClawPhaseTime(1f);
		
			// Middle yellow wolf
		
		w = new yellowWolf(-136+slashAdjustX,-1873+slashAdjustY);
		commands = new commandList();
		commands.add(new slashCommand(114+slashAdjustX,-1873+slashAdjustY));
		commands.add(new slashCommand(114+slashAdjustX,-1673+slashAdjustY));
		commands.add(new slashCommand(-136+slashAdjustX,-1673+slashAdjustY));
		commands.add(new slashCommand(-136+slashAdjustX,-1873+slashAdjustY));
		w.repeatCommands(commands);
		w.setJumpSpeed (10f);
		w.setSpawnClawPhaseTime(1f);
		
		// Section 5 (to the right)
		
			// Black wolves
		
		w = new blackWolf(620+slashAdjustX,-1973+slashAdjustY);
		commands = new commandList();
		commands.add(new slashCommand(620+slashAdjustX,-1573+slashAdjustY));
		commands.add(new slashCommand(620+slashAdjustX,-1973+slashAdjustY));
		w.repeatCommands(commands);
		w.setJumpSpeed (10f);
		w.setSpawnClawPhaseTime(1f);
		
		w = new blackWolf(770+slashAdjustX,-1573+slashAdjustY);
		commands = new commandList();
		commands.add(new slashCommand(770+slashAdjustX,-1973+slashAdjustY));
		commands.add(new slashCommand(770+slashAdjustX,-1573+slashAdjustY));
		w.repeatCommands(commands);
		w.setJumpSpeed (10f);
		w.setSpawnClawPhaseTime(1f);
		
		w = new blackWolf(920+slashAdjustX,-1973+slashAdjustY);
		commands = new commandList();
		commands.add(new slashCommand(920+slashAdjustX,-1573+slashAdjustY));
		commands.add(new slashCommand(920+slashAdjustX,-1973+slashAdjustY));
		w.repeatCommands(commands);
		w.setJumpSpeed (10f);
		w.setSpawnClawPhaseTime(1f);
		
		w = new blackWolf(1070+slashAdjustX,-1573+slashAdjustY);
		commands = new commandList();
		commands.add(new slashCommand(1070+slashAdjustX,-1973+slashAdjustY));
		commands.add(new slashCommand(1070+slashAdjustX,-1573+slashAdjustY));
		w.repeatCommands(commands);
		w.setJumpSpeed (10f);
		w.setSpawnClawPhaseTime(1f);
		
			// Red wolves
		
		w = new redWolf(700+slashAdjustX,-1923+slashAdjustY);
		commands = new commandList();
		commands.add(new slashCommand(1000+slashAdjustX,-1923+slashAdjustY));
		commands.add(new slashCommand(700+slashAdjustX,-1923+slashAdjustY));
		w.repeatCommands(commands);
		w.setJumpSpeed (10f);
		w.setSpawnClawPhaseTime(1f);
		
		w = new redWolf(1000+slashAdjustX,-1773+slashAdjustY);
		commands = new commandList();
		commands.add(new slashCommand(700+slashAdjustX,-1773+slashAdjustY));
		commands.add(new slashCommand(1000+slashAdjustX,-1773+slashAdjustY));
		w.repeatCommands(commands);
		w.setJumpSpeed (10f);
		w.setSpawnClawPhaseTime(1f);
		
		w = new redWolf(700+slashAdjustX,-1673+slashAdjustY);
		commands = new commandList();
		commands.add(new slashCommand(1000+slashAdjustX,-1623+slashAdjustY));
		commands.add(new slashCommand(700+slashAdjustX,-1623+slashAdjustY));
		w.repeatCommands(commands);
		w.setJumpSpeed (10f);
		w.setSpawnClawPhaseTime(1f);
		
			// Yellow wolves
		
		w = new yellowWolf(1070+slashAdjustX,-2073+slashAdjustY);
		commands = new commandList();
		commands.add(new slashCommand(620+slashAdjustX,-2073+slashAdjustY));
		commands.add(new slashCommand(1070+slashAdjustX,-2073+slashAdjustY));
		w.repeatCommands(commands);
		w.setJumpSpeed (10f);
		w.setSpawnClawPhaseTime(1f);
		
		w = new yellowWolf(620+slashAdjustX,-1473+slashAdjustY);
		commands = new commandList();
		commands.add(new slashCommand(1070+slashAdjustX,-1473+slashAdjustY));
		commands.add(new slashCommand(620+slashAdjustX,-1473+slashAdjustY));
		w.repeatCommands(commands);
		w.setJumpSpeed (10f);
		w.setSpawnClawPhaseTime(1f);

	}
	
	// Flower farm
	public void createFlowerFarm()  {
		
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
		
	}
	
	// Forest area above spawn.
	public void createForestAboveSpawn()  {
		
		///////////////////
		//// FOREST ///////
		///////////////////
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
		new grave(2114,-2835,2);
		new grave(2114,-2701,2);
		
		// Far left area.
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
		forestGate = new horizontalGate("Forest Gate", "Farm Key But Not Farmer's Barn, House, or Fridge Key", adjustX + -13+fenceAdjustX/2,adjustY + -434,0);
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
		horizontalGate farlsworthGate = new horizontalGate("Sheep Gate", "Farm Key But Not Farmer's Barn, House, or Fridge Key", adjustX + 412,adjustY + -15,0);
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
		bottle okBottle = new normalBottle(-800,387);
		
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