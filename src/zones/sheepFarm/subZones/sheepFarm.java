package zones.sheepFarm.subZones;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;

import doodads.cave.firePit;
import doodads.general.well;
import doodads.sheepFarm.barn;
import doodads.sheepFarm.blackSmith;
import doodads.sheepFarm.bridge;
import doodads.sheepFarm.bridgePole;
import doodads.sheepFarm.bush;
import doodads.sheepFarm.clawMarkRed;
import doodads.sheepFarm.door;
import doodads.sheepFarm.farmHouse;
import doodads.sheepFarm.fenceBars;
import doodads.sheepFarm.fenceBarsSmall;
import doodads.sheepFarm.fencePost;
import doodads.sheepFarm.flower;
import doodads.sheepFarm.grave;
import doodads.sheepFarm.haystack;
import doodads.sheepFarm.gate;
import doodads.sheepFarm.rock;
import doodads.sheepFarm.statue;
import doodads.sheepFarm.tomb;
import doodads.sheepFarm.tree;
import doodads.sheepFarm.verticalFence;
import drawing.background;
import drawing.spriteSheet;
import drawing.backgrounds.rotatingBackground;
import effects.effectTypes.fire;
import effects.interfaceEffects.tooltipString;
import interactions.event;
import items.bottle;
import items.item;
import items.bottles.saveBottle;
import items.other.bottleExpander;
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
import units.characters.farlsworth.cinematics.beforeTombCinematic;
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
import zones.farmTomb.subZones.farmTomb;
import zones.sheepFarm.sheepFarmZoneLoader;

public class sheepFarm extends zone {
	
	//////////////////////////////////
	// FIELDS, GLOBALS, CONSTRUCTOR //
	//////////////////////////////////
	
	// Static caller of the zone.
	private static zone zoneReference;
	
	// Default zone mode
	private String DEFAULT_ZONE_MODE = "topDown";
	
	// Zone music.
	public static String forestMusic = "sounds/music/farmLand/sheepFarm/forest.wav";
	public static String farmMusic = "sounds/music/farmLand/sheepFarm/farm.wav";
	
	// Static fence so farlsworth can be attached to it.
	public static ArrayList<chunk> farlsworthFence;
	// References we will use throughout.
	static unit u;
	static chunk c;
	
	// Forest gate
	public static gate forestGate;
	public static gate farlsworthGate;
	
	// Zone events.
	public static event eToInteract;
	public static event useSaveBottle;
	public static event wellTooltipLoaded;
	public static event stormInProgress;
	public static event isOnFire;
	public static event talkingGateJokeExperienced = event.createEvent("sheepFarmGateJokeExperienced");
	public static event musicOff = event.createEvent("sheepFarmMusicOff");
	
	// Storm booleans
	public static boolean stormStarted = false;
	
	// Zone fog
	private static fog zoneFog;
	private float stormFogLevel = 0.15f;
	
	// Defaults
	public static intTuple DEFAULT_SPAWN_TUPLE = new intTuple(-1115,391);
	
	// Constructor
	public sheepFarm() {
		super("sheepFarm", "sheepFarm");
		zoneMusics.add(forestMusic);
		zoneMusics.add(farmMusic);
	}
	
	// Spawn wood from x to y.
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
		setMode(DEFAULT_ZONE_MODE);
		
		// Set background
		new rotatingBackground(DEFAULT_ZONE_BACKGROUND);
		
		// Load zone events.
		loadZoneEvents();
		
		// Load the level save.
		levelSave.loadSaveState("sheepFarmLevel.save");
		//sheepFarmZoneLoader loader = new sheepFarmZoneLoader();
		//loader.loadSegments();
		
		// Storming?
		if(stormInProgress.isCompleted()) {
			zoneFog = new fog();
			zoneFog.setTo(stormFogLevel);
			storm s = new storm();  
		}
		
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
		
		playMusic();
	}
	
	// Play music
	public static void playMusic() {
		if(!musicOff.isCompleted()) {
			if(farmIntroCinematic.isCompleted.isCompleted()) {
				music.startMusic(forestMusic); 
			}
			else {
				music.startMusic(farmMusic);
			}
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
		
		// Sheep!
		u = new sheep(-378,-369);
		((sheep)u).setMeanders(true);
		u = new sheep(-150,-372);
		((sheep)u).setMeanders(true);
		u = new sheep(-129,-60);
		((sheep)u).setMeanders(true);
		u = new sheep(-372,-36);
		((sheep)u).setMeanders(true);
		
		// Farmer
		farmer farmer = new farmer(-710,-200);
		
		// The silly haystack.
		c = new doodads.sheepFarm.haystack(-195,-165,0);
		c.setPassable(false);
		((haystack)c).setStrange();
		
		// Farmer house
		c = new door("No Key Yet", -599,-373, farmerHouse.getZone(), -93+20,957+5, "Right");
		
		// Barn
		c = new door("No Key Yet", -793,-370, farmerHouse.getZone(), -93,957, "Right");
		
		// Flower farm
		c = new door("No Key Yet", -1747,-4424, farmerHouse.getZone(), -93,957, "Right");
	}
	
	// Spawn items
	public void createItems() {
		// Spawn bottle.
		bottle saveBottle = new saveBottle(0,-1293);
		
		// Spawn bottle expander
		bottleExpander b = new bottleExpander(-1530+8, -5514+15);
		b.quality = "Alright";
	}

	
	public void createGraveYard()  {
		
		// Tomb
		new tomb(-1548, -3235, 0, farmTomb.getZone(),70,-6,"Right");
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
		forestGate = new gate("Farm Key", adjustX + -13+fenceAdjustX/2,adjustY + -434);
		forestGate.setDialogueBox(gate.DEFAULT_DIALOGUE_BOX);
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
		farlsworthGate = new gate("Farm Key", adjustX + 412,adjustY + -15);
		farlsworthFence.add(farlsworthGate);
		
		// Right of gate
		c = new fenceBarsSmall(adjustX + 457,adjustY + -17,0); 
		farlsworthFence.add(c);
		
		return farlsworthFence;
	}
	
	// Create zone events.
	public void loadZoneEvents() {
		
		// Use WASD
		eToInteract = event.createEvent("useWASDToMoveToolTip");
		
		// Use save bottle
		useSaveBottle = event.createEvent("rememberToUseSaveBottleYouDinky");
		
		// Well and attack tooltips.
		wellTooltipLoaded = event.createEvent("sheepFarmWellTooltipLoaded");
		
		// Storm stuff
		stormInProgress = event.createEvent("sheepFarmStormInProgress");
		
		// Is the zone on fire?
		isOnFire = event.createEvent("forestIsOnFire");
	}
	
	// Zone cinematics.
	farmIntroCinematic farlsworthIntro;
	farmFenceCinematic farlsworthFenceCinematic;
	flowerFarmCinematic farlsworthFlowerCinematic;
	beforeTombCinematic farlsworthTombCinematic;
	
	// Deal with the first well we encounters.
	public void dealWithRegionStuff() {
		player currPlayer = player.getPlayer();
		
		if(farlsworth.farlsworth != null && zoneLoaded) {
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
			if(currPlayer != null && currPlayer.isWithin(-2028,-4262,-1619,-3782)
					&& !farlsworthFlowerCinematic.isCompleted.isCompleted()
					&& (farlsworthFlowerCinematic == null || !farlsworthFlowerCinematic.isInProgress())
					&& farmFenceCinematic.isCompleted.isCompleted()) {
				farlsworthFlowerCinematic = new flowerFarmCinematic();
				farlsworthFlowerCinematic.start();
			}
			
			// Tomb patch cinematic
			if(currPlayer != null && currPlayer.isWithin(-1670,-3259,-1418,-2825)
					&& !farlsworthTombCinematic.isCompleted.isCompleted()
					&& (farlsworthTombCinematic == null || !farlsworthTombCinematic.isInProgress())
					&& flowerFarmCinematic.isCompleted.isCompleted()) {
				farlsworthTombCinematic = new beforeTombCinematic();
				farlsworthTombCinematic.start();
			}
		}
		
		if(currPlayer != null && currPlayer.isWithin(-863,-2175,-460,-1937) && wellTooltipLoaded != null && !wellTooltipLoaded.isCompleted()) {
			wellTooltipLoaded.setCompleted(true);
			tooltipString t = new tooltipString("Interact with a well to fill bottles and the save game.");
		}
		
		/*if(currPlayer != null && currPlayer.isWithin(-718,-3959,-630,-3800) && useSaveBottle != null && !useSaveBottle.isCompleted()
				&& player.getPlayer().lastSaveBottles!=null && player.getPlayer().lastSaveBottles.size() < 2) {
			useSaveBottle.setCompleted(true);
			tooltipString t = new tooltipString("Use the Save Bottle to avoid having to re-do difficult areas.");
		}*/
		
		if(currPlayer != null && currPlayer.isWithin(-1116,-490,-444,54) && eToInteract != null && !eToInteract.isCompleted()) {
			eToInteract.setCompleted(true);
			tooltipString t = new tooltipString("Press 'e' to interact with something.");
		}
		
		// Fog at black flower area
		if(stormInProgress != null && !stormInProgress.isCompleted() && currPlayer != null && currPlayer.isWithin(-863,-2175,-460,-1937)) {
			if(zoneFog == null) zoneFog = new fog();
			zoneFog.fadeTo(stormFogLevel, 1f);
			stormInProgress.setCompleted(true);
			stormStartTime = time.getTime();
			startStormFromFog = true;
			saveState.setQuiet(true);
			saveState.createSaveState();
			saveState.setQuiet(false);
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
				storm s = new storm(5f);
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
	
	public String getMode() {
		return DEFAULT_ZONE_MODE;
	}
	
}