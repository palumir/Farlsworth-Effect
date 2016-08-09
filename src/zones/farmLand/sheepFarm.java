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
		
		// Wolf holder
		wolf w;
		ArrayList<intTuple> spawnList;
		ArrayList<Integer> angleList;

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
		
		/////////////////////////////
		/// Wolf Section I guess ///
		///////////////////////////
		
		//FUCKING TREES FOR NOW!!!
		
		/*
		w = new blackWolf(-1068,-1200);
		spawnList = new ArrayList<intTuple>();
		spawnList.add(new intTuple(-1068,-1550));
		spawnList.add(new intTuple(-1068,-1450));
		spawnList.add(new intTuple(-1068,-1350));
		spawnList.add(new intTuple(-1068,-1250));
		w.setTrailSpawns(spawnList);
		commands = new commandList();
		commands.add(new slashCommand(-1068,-1650));
		commands.add(new slashCommand(-1068,-1200));
		w.repeatCommands(commands);
		*/
		
		// Section 1
		
		w = new redWolf (93+slashAdjustX,-720+slashAdjustY);
		commands = new commandList();
		commands.add(new slashCommand (-107+slashAdjustX, -720+slashAdjustY));
		commands.add(new slashCommand (93+slashAdjustX, -720+slashAdjustY));
		w.repeatCommands(commands);
		w.setJumpSpeed (11f);
		w.setSpawnClawPhaseTime(0.75f);
		
		w = new redWolf (-107+slashAdjustX,-820+slashAdjustY);
		commands = new commandList();
		commands.add(new slashCommand (93+slashAdjustX, -820+slashAdjustY));
		commands.add(new slashCommand (-107+slashAdjustX, -820+slashAdjustY));
		w.repeatCommands(commands);
		w.setJumpSpeed (11f);
		w.setSpawnClawPhaseTime(0.75f);
		
		w = new redWolf (93+slashAdjustX,-920+slashAdjustY);
		commands = new commandList();
		commands.add(new slashCommand (-107+slashAdjustX, -920+slashAdjustY));
		commands.add(new slashCommand (93+slashAdjustX, -920+slashAdjustY));
		w.repeatCommands(commands);
		w.setJumpSpeed (11f);
		w.setSpawnClawPhaseTime(0.6f);
		
		w = new redWolf (-107+slashAdjustX,-995+slashAdjustY);
		commands = new commandList();
		commands.add(new slashCommand (93+slashAdjustX, -995+slashAdjustY));
		commands.add(new slashCommand (-107+slashAdjustX, -995+slashAdjustY));
		w.repeatCommands(commands);
		w.setJumpSpeed (11f);
		w.setSpawnClawPhaseTime(0.6f);
		
		w = new redWolf (93+slashAdjustX,-1060+slashAdjustY);
		commands = new commandList();
		commands.add(new slashCommand (-107+slashAdjustX, -1060+slashAdjustY));
		commands.add(new slashCommand (93+slashAdjustX, -1060+slashAdjustY));
		w.repeatCommands(commands);
		w.setJumpSpeed (11f);
		w.setSpawnClawPhaseTime(0.6f);
		
		// Bottle
		
		bottle okBottle = new normalBottle(-110,-1192);
		
		// Section 2 (patrolling intro)
		
			// Jumping
		
		w = new redWolf (-457+slashAdjustX,-1360+slashAdjustY);
		commands = new commandList();
		commands.add(new slashCommand (-157+slashAdjustX, -1360+slashAdjustY));
		commands.add(new slashCommand (-457+slashAdjustX, -1360+slashAdjustY));
		w.repeatCommands(commands);
		w.setJumpSpeed (11f);
		w.setSpawnClawPhaseTime(0.4f);
		
		w = new redWolf (-157+slashAdjustX,-1460+slashAdjustY);
		commands = new commandList();
		commands.add(new slashCommand (-457+slashAdjustX, -1460+slashAdjustY));
		commands.add(new slashCommand (-157+slashAdjustX, -1460+slashAdjustY));
		w.repeatCommands(commands);
		w.setJumpSpeed (11f);
		w.setSpawnClawPhaseTime(0.4f);
		
		w = new redWolf (-457+slashAdjustX,-1560+slashAdjustY);
		commands = new commandList();
		commands.add(new slashCommand (-157+slashAdjustX, -1560+slashAdjustY));
		commands.add(new slashCommand (-457+slashAdjustX, -1560+slashAdjustY));
		w.repeatCommands(commands);
		w.setJumpSpeed (11f);
		w.setSpawnClawPhaseTime(0.4f);
		
		w = new redWolf (-157+slashAdjustX,-1660+slashAdjustY);
		commands = new commandList();
		commands.add(new slashCommand (-457+slashAdjustX, -1660+slashAdjustY));
		commands.add(new slashCommand (-157+slashAdjustX, -1660+slashAdjustY));
		w.repeatCommands(commands);
		w.setJumpSpeed (11f);
		w.setSpawnClawPhaseTime(0.4f);
		
			// Patrolling
		
		w = new yellowWolf (-367+slashAdjustX,-1410+slashAdjustY);
		commands = new commandList();
		commands.add(new moveCommand (-367+slashAdjustX, -1610+slashAdjustY));
		commands.add(new moveCommand (-227+slashAdjustX, -1610+slashAdjustY));
		commands.add(new moveCommand (-227+slashAdjustX, -1410+slashAdjustY));
		commands.add(new moveCommand (-367+slashAdjustX, -1410+slashAdjustY));
		w.repeatCommands(commands);
		w.setJumpSpeed (5f);
		
		// Section 3
		
			// Jumping wolves
		
		w = new redWolf (-722+slashAdjustX,-1890+slashAdjustY);
		commands = new commandList();
		commands.add(new slashCommand (-522+slashAdjustX, -2090+slashAdjustY));
		commands.add(new slashCommand (-722+slashAdjustX, -1890+slashAdjustY));
		w.repeatCommands(commands);
		w.setJumpSpeed (11f);
		w.setSpawnClawPhaseTime(1.3f);
		
		w = new redWolf (-992+slashAdjustX, -2090+slashAdjustY);
		commands = new commandList();
		commands.add(new slashCommand (-792+slashAdjustX, -1890+slashAdjustY));
		commands.add(new slashCommand (-992+slashAdjustX, -2090+slashAdjustY));
		w.repeatCommands(commands);
		w.setJumpSpeed (11f);
		w.setSpawnClawPhaseTime(1.3f);
		
		w = new redWolf (-1262+slashAdjustX, -1890+slashAdjustY);
		commands = new commandList();
		commands.add(new slashCommand (-1062+slashAdjustX, -2090+slashAdjustY));
		commands.add(new slashCommand (-1262+slashAdjustX, -1890+slashAdjustY));
		w.repeatCommands(commands);
		w.setJumpSpeed (11f);
		w.setSpawnClawPhaseTime(1.3f);
		
			// Patrolling Wolves
		
		w = new yellowWolf (-622+slashAdjustX,-1957+slashAdjustY);
		commands = new commandList();
		commands.add(new moveCommand (-872+slashAdjustX, -1957+slashAdjustY));
		commands.add(new moveCommand (-872+slashAdjustX, -2023+slashAdjustY));
		commands.add(new moveCommand (-622+slashAdjustX,-2023+slashAdjustY));
		commands.add(new moveCommand (-622+slashAdjustX,-1957+slashAdjustY));
		w.repeatCommands(commands);
		w.setMoveSpeed (3f);
		
		w = new yellowWolf (-912+slashAdjustX, -2023+slashAdjustY);
		commands = new commandList();
		commands.add(new moveCommand (-1162+slashAdjustX,-2023+slashAdjustY));
		commands.add(new moveCommand (-1162+slashAdjustX,-1957+slashAdjustY));
		commands.add(new moveCommand (-912+slashAdjustX, -1957+slashAdjustY));
		commands.add(new moveCommand (-912+slashAdjustX, -2023+slashAdjustY));
		w.repeatCommands(commands);
		w.setMoveSpeed (3f);
		
		// Section 4 (rock intro)
		
			// Patrolling
		
		w = new yellowWolf (-1234+slashAdjustX, -2377+slashAdjustY);
		commands = new commandList();
		commands.add(new moveCommand (-994+slashAdjustX,-2467+slashAdjustY));
		commands.add(new moveCommand (-1234+slashAdjustX, -2377+slashAdjustY));
		w.repeatCommands(commands);
		w.setMoveSpeed (3f);
		
		w = new yellowWolf (-994+slashAdjustX, -2477+slashAdjustY);
		commands = new commandList();
		commands.add(new moveCommand (-1234+slashAdjustX,-2567+slashAdjustY));
		commands.add(new moveCommand (-994+slashAdjustX, -2477+slashAdjustY));
		w.repeatCommands(commands);
		w.setMoveSpeed (3f);
		
		w = new yellowWolf (-1234+slashAdjustX, -2577+slashAdjustY);
		commands = new commandList();
		commands.add(new moveCommand (-994+slashAdjustX,-2667+slashAdjustY));
		commands.add(new moveCommand (-1234+slashAdjustX, -2577+slashAdjustY));
		w.repeatCommands(commands);
		w.setMoveSpeed (3f);
		
			// Rock spawn Left and Right
		
		w = new yellowWolf(-904+slashAdjustX,-2327+slashAdjustY);
		spawnList = new ArrayList<intTuple>();
		spawnList.add(new intTuple(-904+slashAdjustX,-2577+slashAdjustY));
		spawnList.add(new intTuple(-904+slashAdjustX,-2377+slashAdjustY));
		w.setTrailSpawns(spawnList);
		angleList = new ArrayList<Integer>();
		angleList.add(270);
		((yellowWolf)w).setRockAngles(angleList);
		commands = new commandList();
		commands.add(new waitCommand(2.5f));
		commands.add(new slashCommand(-904+slashAdjustX,-2627+slashAdjustY));
		commands.add(new waitCommand(2.5f));
		commands.add(new slashCommand(-904+slashAdjustX,-2327+slashAdjustY));
		w.repeatCommands(commands);
		
		w = new yellowWolf(-1324+slashAdjustX,-2727+slashAdjustY);
		spawnList = new ArrayList<intTuple>();
		spawnList.add(new intTuple(-1324+slashAdjustX,-2477+slashAdjustY));
		spawnList.add(new intTuple(-1324+slashAdjustX,-2677+slashAdjustY));
		w.setTrailSpawns(spawnList);
		angleList = new ArrayList<Integer>();
		angleList.add(90);
		((yellowWolf)w).setRockAngles(angleList);
		commands = new commandList();
		commands.add(new waitCommand(2.5f));
		commands.add(new slashCommand(-1324+slashAdjustX,-2427+slashAdjustY));
		commands.add(new waitCommand(2.5f));
		commands.add(new slashCommand(-1324+slashAdjustX,-2727+slashAdjustY));
		w.repeatCommands(commands);
		
		c = new well (-1282, -3089, 1);
		
		// Section 5 (stone, jumping, patrolling wolves)
		
			// Rock spawn top
		
		w = new yellowWolf(-984+slashAdjustX,-3253+slashAdjustY);
		w.setJumpSpeed(8f);
		commands = new commandList();
		commands.add(new waitCommand(0.5f));
		commands.add(new slashCommand(-384+slashAdjustX,-3253+slashAdjustY));
		commands.add(new waitCommand(0.5f));
		commands.add(new slashCommand(-984+slashAdjustX,-3253+slashAdjustY));
		w.repeatCommands(commands);
		spawnList = new ArrayList<intTuple>();
		spawnList.add(new intTuple(-784+slashAdjustX,-3253+slashAdjustY));
		spawnList.add(new intTuple(-634+slashAdjustX,-3253+slashAdjustY));
		spawnList.add(new intTuple(-484+slashAdjustX,-3253+slashAdjustY));
		w.setTrailSpawns(spawnList);
		angleList = new ArrayList<Integer>();
		angleList.add(180);
		((yellowWolf)w).setRockAngles(angleList);
		
			// Patrolling
		
		w = new yellowWolf (-844+slashAdjustX, -3153+slashAdjustY);
		commands = new commandList();
		commands.add(new moveCommand (-687+slashAdjustX,-2956+slashAdjustY));
		commands.add(new moveCommand (-844+slashAdjustX, -3153+slashAdjustY));
		w.repeatCommands(commands);
		w.setMoveSpeed (3f);
		
		w = new yellowWolf (-687+slashAdjustX,-2956+slashAdjustY);
		commands = new commandList();
		commands.add(new moveCommand (-530+slashAdjustX,-3153+slashAdjustY));
		commands.add(new moveCommand (-687+slashAdjustX,-2956+slashAdjustY));
		w.repeatCommands(commands);
		w.setMoveSpeed (3f);
		
			// Jumping Wolf
		
		w = new redWolf (-904+slashAdjustX,-3205+slashAdjustY);
		commands = new commandList();
		commands.add(new slashCommand (-550+slashAdjustX, -3205+slashAdjustY));
		commands.add(new slashCommand (-904+slashAdjustX, -3205+slashAdjustY));
		w.repeatCommands(commands);
		w.setJumpSpeed (11f);
		w.setSpawnClawPhaseTime(1.6f);
		
		w = new redWolf (-904+slashAdjustX,-3005+slashAdjustY);
		commands = new commandList();
		commands.add(new slashCommand (-550+slashAdjustX, -3005+slashAdjustY));
		commands.add(new slashCommand (-904+slashAdjustX, -3005+slashAdjustY));
		w.repeatCommands(commands);
		w.setJumpSpeed (11f);
		w.setSpawnClawPhaseTime(1.6f);
		
		// Section 6 (black hole intro)
		
		// (i)
		
			// Black hole dogs
		
		w = new blackWolf(-43+slashAdjustX,-3250+slashAdjustY);
		spawnList = new ArrayList<intTuple>();
		spawnList.add(new intTuple(-43+slashAdjustX,-3175+slashAdjustY));
		spawnList.add(new intTuple(-43+slashAdjustX,-3025+slashAdjustY));
		w.setTrailSpawns(spawnList);
		commands = new commandList();
		commands.add(new slashCommand(-43+slashAdjustX,-2950+slashAdjustY));
		commands.add(new slashCommand(-43+slashAdjustX,-3250+slashAdjustY));
		w.repeatCommands(commands);
		
		w = new blackWolf(67+slashAdjustX,-2950+slashAdjustY);
		spawnList = new ArrayList<intTuple>();
		spawnList.add(new intTuple(67+slashAdjustX,-3100+slashAdjustY));
		spawnList.add(new intTuple(67+slashAdjustX,-3025+slashAdjustY));
		w.setTrailSpawns(spawnList);
		commands = new commandList();
		commands.add(new slashCommand(67+slashAdjustX,-3250+slashAdjustY));
		commands.add(new slashCommand(67+slashAdjustX,-2950+slashAdjustY));
		w.repeatCommands(commands);
		
		w = new blackWolf(177+slashAdjustX,-3250+slashAdjustY);
		spawnList = new ArrayList<intTuple>();
		spawnList.add(new intTuple(177+slashAdjustX,-3175+slashAdjustY));
		spawnList.add(new intTuple(177+slashAdjustX,-3100+slashAdjustY));
		w.setTrailSpawns(spawnList);
		commands = new commandList();
		commands.add(new slashCommand(177+slashAdjustX,-2950+slashAdjustY));
		commands.add(new slashCommand(177+slashAdjustX,-3250+slashAdjustY));
		w.repeatCommands(commands);
		
		w = new blackWolf(287+slashAdjustX,-2950+slashAdjustY);
		spawnList = new ArrayList<intTuple>();
		spawnList.add(new intTuple(287+slashAdjustX,-3175+slashAdjustY));
		spawnList.add(new intTuple(287+slashAdjustX,-3025+slashAdjustY));
		w.setTrailSpawns(spawnList);
		commands = new commandList();
		commands.add(new slashCommand(287+slashAdjustX,-3250+slashAdjustY));
		commands.add(new slashCommand(287+slashAdjustX,-2950+slashAdjustY));
		w.repeatCommands(commands);
		
		// (ii)
		
			// Black hole dogs
		
		w = new blackWolf(382+slashAdjustX,-3100+slashAdjustY);
		spawnList = new ArrayList<intTuple>();
		spawnList.add(new intTuple(452+slashAdjustX,-3100+slashAdjustY));
		spawnList.add(new intTuple(527+slashAdjustX,-3100+slashAdjustY));
		spawnList.add(new intTuple(602+slashAdjustX,-3100+slashAdjustY));
		spawnList.add(new intTuple(677+slashAdjustX,-3100+slashAdjustY));
		spawnList.add(new intTuple(752+slashAdjustX,-3100+slashAdjustY));
		spawnList.add(new intTuple(827+slashAdjustX,-3100+slashAdjustY));
		w.setTrailSpawns(spawnList);
		commands = new commandList();
		commands.add(new slashCommand(902+slashAdjustX,-3100+slashAdjustY));
		commands.add(new slashCommand(382+slashAdjustX,-3100+slashAdjustY));
		w.repeatCommands(commands);
		
			// Jumping
		
		w = new redWolf (452+slashAdjustX,-2950+slashAdjustY);
		commands = new commandList();
		commands.add(new slashCommand (452+slashAdjustX, -3250+slashAdjustY));
		commands.add(new slashCommand (452+slashAdjustX, -2950+slashAdjustY));
		w.repeatCommands(commands);
		w.setJumpSpeed (8f);
		w.setSpawnClawPhaseTime(0.9f);
		
		w = new redWolf (527+slashAdjustX,-3250+slashAdjustY);
		commands = new commandList();
		commands.add(new slashCommand (527+slashAdjustX, -2950+slashAdjustY));
		commands.add(new slashCommand (527+slashAdjustX, -3250+slashAdjustY));
		w.repeatCommands(commands);
		w.setJumpSpeed (8f);
		w.setSpawnClawPhaseTime(0.9f);
		
		w = new redWolf (602+slashAdjustX,-2950+slashAdjustY);
		commands = new commandList();
		commands.add(new slashCommand (602+slashAdjustX, -3250+slashAdjustY));
		commands.add(new slashCommand (602+slashAdjustX, -2950+slashAdjustY));
		w.repeatCommands(commands);
		w.setJumpSpeed (8f);
		w.setSpawnClawPhaseTime(0.9f);
		
		w = new redWolf (677+slashAdjustX,-3250+slashAdjustY);
		commands = new commandList();
		commands.add(new slashCommand (677+slashAdjustX, -2950+slashAdjustY));
		commands.add(new slashCommand (677+slashAdjustX, -3250+slashAdjustY));
		w.repeatCommands(commands);
		w.setJumpSpeed (8f);
		w.setSpawnClawPhaseTime(0.9f);
		
		w = new redWolf (752+slashAdjustX,-2950+slashAdjustY);
		commands = new commandList();
		commands.add(new slashCommand (752+slashAdjustX, -3250+slashAdjustY));
		commands.add(new slashCommand (752+slashAdjustX, -2950+slashAdjustY));
		w.repeatCommands(commands);
		w.setJumpSpeed (8f);
		w.setSpawnClawPhaseTime(0.9f);
		
		w = new redWolf (827+slashAdjustX,-3250+slashAdjustY);
		commands = new commandList();
		commands.add(new slashCommand (827+slashAdjustX, -2950+slashAdjustY));
		commands.add(new slashCommand (827+slashAdjustX, -3250+slashAdjustY));
		w.repeatCommands(commands);
		w.setJumpSpeed (8f);
		w.setSpawnClawPhaseTime(0.9f);
		
		// Section 7 (up)
		
			// Rock wolf bottom
		
		w = new yellowWolf(952+slashAdjustX,-2950+slashAdjustY);
		spawnList = new ArrayList<intTuple>();
		spawnList.add(new intTuple(1012+slashAdjustX,-2950+slashAdjustY));
		spawnList.add(new intTuple(1072+slashAdjustX,-2950+slashAdjustY));
		spawnList.add(new intTuple(1132+slashAdjustX,-2950+slashAdjustY));
		spawnList.add(new intTuple(1192+slashAdjustX,-2950+slashAdjustY));
		w.setTrailSpawns(spawnList);
		commands = new commandList();
		commands.add(new waitCommand (1f) );
		commands.add(new slashCommand(1252+slashAdjustX,-2950+slashAdjustY));
		commands.add(new waitCommand (1f) );
		commands.add(new slashCommand(902+slashAdjustX,-2950+slashAdjustY));
		w.repeatCommands(commands);
		angleList = new ArrayList<Integer>();
		angleList.add(0);
		((yellowWolf)w).setRockAngles(angleList);
		
			// Patrolling
		
		w = new yellowWolf (1192+slashAdjustX, -3100+slashAdjustY);
		commands = new commandList();
		commands.add(new moveCommand (952+slashAdjustX,-3000+slashAdjustY));
		commands.add(new moveCommand (1192+slashAdjustX, -3100+slashAdjustY));
		commands.add(new moveCommand (952+slashAdjustX, -3200+slashAdjustY));
		commands.add(new moveCommand (1192+slashAdjustX, -3100+slashAdjustY));
		w.repeatCommands(commands);
		w.setMoveSpeed (3f);
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