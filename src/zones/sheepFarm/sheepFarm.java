package zones.sheepFarm;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;

import UI.tooltipString;
import doodads.cave.firePit;
import doodads.general.well;
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
import drawing.background;
import drawing.spriteSheet;
import drawing.backgrounds.rotatingBackground;
import effects.effectTypes.fire;
import interactions.event;
import items.bottle;
import items.item;
import items.bottles.saveBottle;
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
import units.characters.farlsworth.farlsworth;
import units.characters.farlsworth.cinematics.farmFenceCinematic;
import units.characters.farlsworth.cinematics.farmIntroCinematic;
import units.characters.farlsworth.cinematics.flowerFarmCinematic;
import units.characters.farmer.farmer;
import units.unitCommands.commandList;
import units.unitCommands.commands.moveCommand;
import units.unitCommands.commands.slashCommand;
import units.unitCommands.commands.waitCommand;
import units.unitTypes.sheepFarm.redWolf;
import units.unitTypes.sheepFarm.sheep;
import units.unitTypes.sheepFarm.wolf;
import units.unitTypes.sheepFarm.yellowWolf;
import utilities.intTuple;
import utilities.levelSave;
import utilities.saveState;
import utilities.time;
import utilities.utility;
import zones.zone;
import zones.farmTomb.farmTomb;

public class sheepFarm extends zone {
	
	//////////////////////////////////
	// FIELDS, GLOBALS, CONSTRUCTOR //
	//////////////////////////////////
	
	// Static caller of the zone.
	private static zone zoneReference;
	
	// Zone music.
	private static String zoneMusic = "sounds/music/farmLand/sheepFarm/forest.wav";
	private static String zoneMusicDistorted = "sounds/music/farmLand/sheepFarm/forestDistorted.wav";
	
	// Static fence so farlsworth can be attached to it.
	public static ArrayList<chunk> farlsworthFence;
	
	// References we will use throughout.
	static unit u;
	static chunk c;
	
	// Forest gate
	public static horizontalGate forestGate;
	
	// Zone events.
	public static event wellTooltipLoaded;
	public static event stormInProgress;
	public static event isOnFire;
	public static event distortedMusicPlaying;
	public static event talkingGateJokeExperienced = new event("sheepFarmGateJokeExperienced");
	
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
	
	private static BufferedImage DEFAULT_ZONE_BACKGROUND = spriteSheet.getSpriteFromFilePath("images/terrain/backgrounds/spinningFarmBackground.png");
	
	/////////////////
	// ZONE LOADER //
	/////////////////
	// Load the zone.
	public void loadSpecificZoneStuff() {
		
		// Set the mode of the zone of course.
		topDown.setMode();
		
		// Set background
		new rotatingBackground(DEFAULT_ZONE_BACKGROUND);
		
		// Load zone events.
		loadZoneEvents();
		
		// Storming?
		if(stormInProgress.isCompleted()) {
			zoneFog = new fog();
			zoneFog.setTo(stormFogLevel);
			storm s = new storm();
		}
		
		// Load the level save.
		sheepFarmZoneLoader loader = new sheepFarmZoneLoader();
		loader.loadSegments();
		
		// Spawn special stuff
		spawnSpecialStuff();
		
		// Create items
		createItems();
		
		// Create graveyard
		createGraveYard();
		
		// Play fire sound
		if(isOnFire != null && isOnFire.isCompleted()) {
			sound s = new sound(fire.forestFire);
			s.start();
		}
		
		// Spawn units
		//spawnUnits();
		
		if(distortedMusicPlaying.isCompleted()) {
			music.startMusic(zoneMusicDistorted); 
		}
		else {
			
			// Deal with possible saving issue
			if(music.currMusic != null && !music.currMusic.getFileName().contains(zoneMusic)) {
				music.endAll();
			}
			
			// Play regular zone music. 
			music.startMusic(zoneMusic); 
		}
		
	}
	
	// Load from save
	public void loadFromSaveForTesting() {
		levelSave.loadSaveState("sheepFarmLevel.save");
		spawnFarlsworthAndFence();
	}
	
	//////////////////////
	// INDIVIDUAL AREAS //
	//////////////////////
	
	// Spawn farlsworth and fence.
	public void spawnFarlsworthAndFence() {
		farlsworth sheepBoss = new farlsworth(411,-394);
		if(!farlsworth.isFenceAttached.isCompleted()) {
			farlsworthFence = makeFarlsworthFence(5,-406);
		}
		else {
			farlsworthFence = null;
		}
	}
	
	// Special chunks
	public void spawnSpecialStuff() {
		
		// Spawn Farlsworth and fence
		spawnFarlsworthAndFence();
		
		// Farmer
		new farmer(-710,-256);
		
		// The silly haystack.
		c = new doodads.sheepFarm.haystack(-294,-315,0);
		c.setPassable(false);
		((haystack)c).setStrange();
	}
	
	// Spawn items
	public void createItems() {
		
		// Spawn bottle.
		bottle saveBottle = new saveBottle(-665,-3102);
		//bottle saveBottle2 = new saveBottle(-665,-3140);
		
	}

	
	public void createGraveYard()  {
		
		// Tomb
		new tomb(-3460, -5803, 0, farmTomb.getZone(),70,-6,"Right");
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
		forestGate = new horizontalGate("Forest Gate", "Not Fridge Key", adjustX + -13+fenceAdjustX/2,adjustY + -434,0);
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
		horizontalGate farlsworthGate = new horizontalGate("Sheep Gate", "Not Fridge Key", adjustX + 412,adjustY + -15,0);
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
		
		// Storm stuff
		stormInProgress = new event("sheepFarmStormInProgress");
		
		// Is the zone on fire?
		isOnFire = new event("forestIsOnFire");
		
		// Is distorted music playing?
		distortedMusicPlaying = new event("sheepFarmIsDistortedMusicPlaying");
	}
	
	// Zone cinematics.
	farmIntroCinematic farlsworthIntro;
	farmFenceCinematic farlsworthFenceCinematic;
	flowerFarmCinematic farlsworthFlowerCinematic;
	
	// Deal with the first well we encounters.
	public void dealWithRegionStuff() {
		player currPlayer = player.getPlayer();
		
		// First Farlsworth cinematic (intro)
		if(currPlayer != null && currPlayer.isWithin(230,-458,433,-250) 
				&& !farlsworthIntro.isCompleted.isCompleted()
				&& (farlsworthIntro == null || !farlsworthIntro.isInProgress())) {
			farlsworthIntro = new farmIntroCinematic();
			farlsworthIntro.start();
		}
		
		// Second Farlsworth cinematic (at the fence)
		if(currPlayer != null && currPlayer.isWithin(-50, -453, 20, -300) 
				&& !farlsworthFenceCinematic.isCompleted.isCompleted()
				&& (farlsworthFenceCinematic == null || !farlsworthFenceCinematic.isInProgress())
				&& farmIntroCinematic.isCompleted.isCompleted()) {
			farlsworthFenceCinematic = new farmFenceCinematic();
			farlsworthFenceCinematic.start();
		}
		
		// Flower patch cinematic
		if(currPlayer != null && currPlayer.isWithin(-1695,-5258,-1335,-4830)
				&& !farlsworthFlowerCinematic.isCompleted.isCompleted()
				&& (farlsworthFlowerCinematic == null || !farlsworthFlowerCinematic.isInProgress())
				&& farmFenceCinematic.isCompleted.isCompleted()) {
			farlsworthFlowerCinematic = new flowerFarmCinematic();
			farlsworthFlowerCinematic.start();
		}
		
		if(currPlayer != null && currPlayer.isWithin(-220,-2401,228,-2049) && wellTooltipLoaded != null && !wellTooltipLoaded.isCompleted()) {
			wellTooltipLoaded.setCompleted(true);
			tooltipString t = new tooltipString("Interact with a well to save the game.");
		}
		
		// Fog at black flower area
		if(stormInProgress != null && !stormInProgress.isCompleted() && currPlayer != null && currPlayer.isWithin(-1719,-5298,-1314,-4818)) {
			if(zoneFog == null) zoneFog = new fog();
			zoneFog.fadeTo(stormFogLevel, 1f);
			stormInProgress.setCompleted(true);
			stormStartTime = time.getTime();
			startStormFromFog = true;
			saveState.setQuiet(true);
			saveState.createSaveState();
			saveState.setQuiet(false);
		}
		
		// Distorted revealed?
		if(distortedMusicPlaying != null && !distortedMusicPlaying.isCompleted()&& currPlayer != null && currPlayer.isWithin(-863,-3191,-460,-2953)) {
			
			// Change music to distorted
			music.endAll();
			music.startMusic(zoneMusicDistorted);
			distortedMusicPlaying.setCompleted(true);
		}
		
		
	}
	
	// Storm stuff
	long stormStartTime = 0;
	boolean startStormFromFog = false;
	int howManySecondsUntilStorm = 2;
	
	// Start storm from fog
	public void startStormFromFog() {
		if(startStormFromFog) {
			if(time.getTime() - stormStartTime > howManySecondsUntilStorm*1000) {
				startStormFromFog = false;
				stormStarted = true;
				storm s = new storm(3f);
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