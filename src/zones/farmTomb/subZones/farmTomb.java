package zones.farmTomb.subZones;

import java.awt.image.BufferedImage;

import doodads.tomb.stairsUp;
import drawing.background;
import drawing.spriteSheet;
import interactions.event;
import items.bottles.pushBottle;
import items.other.bottleExpander;
import sounds.music;
import terrain.chunk;
import terrain.atmosphericEffects.fog;
import units.player;
import units.unit;
import units.bosses.graveKeeper.cinematics.wolflessFightCinematic;
import units.bosses.wolfless.wolfless;
import units.characters.farlsworth.farlsworth;
import units.unitCommands.commandList;
import units.unitTypes.tomb.shadowDude;
import utilities.intTuple;
import utilities.saveState;
import zones.zone;
import zones.endZone.subZones.endZone;
import zones.farmTomb.farmTombZoneLoader;
import zones.sheepFarm.subZones.sheepFarm;

public class farmTomb extends zone {
	
	//////////////////////////////////
	// FIELDS, GLOBALS, CONSTRUCTOR //
	//////////////////////////////////
	
	// Static caller of the zone.
	private static zone zoneReference;
	
	// Default background.
	private static BufferedImage DEFAULT_ZONE_BACKGROUND = spriteSheet.getSpriteFromFilePath("images/terrain/backgrounds/tombBackground.png");
	
	// Zone music.
	public static String zoneMusic = "sounds/music/farmLand/tomb/tomb.wav";
	public static String zoneMusicFrantic = "sounds/music/farmLand/tomb/tombBossFight.wav";
	
	// Default zone mode
	private static String DEFAULT_ZONE_MODE = "platformer";
	
	// References we will use throughout.
	static unit u;
	static chunk c;
	static commandList commands;
	
	// Some defaults.
	public static int BACKGROUND_Z = -100;
	
	// Zone events.
	public static event shadowBossFightStarted;
	public static event shadowBossFightFinished;
	public static boolean shadowBossFightFirstTime;
	
	// Initiated?
	public boolean shadowBossFightInitiated = false;
	
	// Defaults
	public static intTuple DEFAULT_SPAWN_TUPLE = new intTuple(0,-50);
	
	 // BossFight loaded?
	static boolean bossFightLoaded = false;
	
	// Zone fog
	public static fog zoneFog;
	
	// Constructor
	public farmTomb() {
		super("farmTomb", "farmTomb");
		zoneMusics.add(zoneMusic);
		zoneMusics.add(zoneMusicFrantic);
	}
		
	/////////////////
	// ZONE LOADER //
	/////////////////
	// Load the zone.
	public void loadSpecificZoneStuff() {
		
		// Shadow bossFight not loaded
		bossFightLoaded = false;
		
		// Set the mode of the zone of course.
		setMode(DEFAULT_ZONE_MODE);
		
		// Set the darkness.
		zoneFog = new fog();
		zoneFog.setTo(0.3f);
		
		// BossFight not initiated
		shadowBossFightInitiated = false;
		
		// Load stuff so the zone doesn't lag
		preLoadStuff();
		
		// Load zone events.
		loadZoneEvents();
		
		// Don't load!
		boolean loadStuff = true;
		
		// Play zone music.
		if(!shadowBossFightStarted.isCompleted()) { music.startMusic(zoneMusic);  }
		else {
			
			if(player.getPlayer().isWithin((int)player.getPlayer().lastWell.getX()-200, 
					(int)player.getPlayer().lastWell.getY()-200, 
					(int)player.getPlayer().lastWell.getX()+250, 
					(int)player.getPlayer().lastWell.getY()+250)) {
				shadowBossFightStarted.setCompleted(false);
				shadowBossFightFirstTime = true;
				music.startMusic(zoneMusic); 
				saveState.setQuiet(true);
				saveState.createSaveState();
				saveState.setQuiet(false);
			}
			else {
				// Switch to boss fight zone.
				//loadStuff = false;
				//zone.switchZones(player.getPlayer(), player.getPlayer().getCurrentZone(), bossFightZone.getZone(), 0, 0, "Right");
				
				createShadowBossFightAroundPlayer(); 
			}
		}
		
		if(loadStuff) {
			//levelSave.loadSaveState("actualTombLevel.save");
			
			// Load the level save.
			farmTombZoneLoader loader = new farmTombZoneLoader();
			loader.loadSegments();
	
			// Load zone items
			loadItems();
			
			// Load units
			loadUnits();
					
			// Background
			background.setGameBackground(DEFAULT_ZONE_BACKGROUND);
			
			// Spawn area.
			createNonEditorChunks();
		}
		
	}
	
	// PreloadStuff
	public void preLoadStuff() {
		int holder = wolfless.leniency;
		holder = shadowDude.getDefaultHeight();
	}
	
	// Load items
	public void loadItems() {
		pushBottle b = new pushBottle(70, 815);
		bottleExpander exp = new bottleExpander(6526,2325);
		exp.quality = "Alright";
		bottleExpander exp2 = new bottleExpander(6640,1817);
		exp2.quality = "Good";
	}
	
	// Load units
	public void loadUnits() {
		farlsworth f = new farlsworth(Integer.MIN_VALUE, Integer.MIN_VALUE);
		f.setGravity(false);
	}
	
	// Load zone events.
	public void loadZoneEvents() {
		
		// Has the BossFight started?
		shadowBossFightStarted = event.createEvent("tombZoneShadowBossFightStarted");
		
		// Has the BossFight ended?
		shadowBossFightFinished = event.createEvent("tombZoneShadowBossFightFinished");
	}
	
	
	//////////////////////
	// INDIVIDUAL AREAS //
	//////////////////////
	
	public void createNonEditorChunks() {
		
		/////////////////////////
		//////// ENTRANCE ///////
		/////////////////////////
		
		// Entrance
		stairsUp tombZoneEnterance = new stairsUp(30,-8,sheepFarm.getZone(),-1529, -3108,"Down");
		tombZoneEnterance.setZ(BACKGROUND_Z);
		
		// Secret chest stairs up to well.
		stairsUp secretStairs = new stairsUp(6677,2315,farmTomb.getZone(),2656,1797,"Right");
		secretStairs.setZ(BACKGROUND_Z);
		
		// Stairs to entrance.
		stairsUp entranceStairs = new stairsUp(6136,2007,farmTomb.getZone(),65+30,-414+2,"Right");
		entranceStairs.setZ(BACKGROUND_Z);
		
		// Stairs to end
		stairsUp toEndStairs = new stairsUp(65,-414,farmTomb.getZone(),6136-40,2007+1,"Left");
		toEndStairs.setZ(BACKGROUND_Z);
		
		// Exit
		stairsUp bossExit = new stairsUp(6769,
				1797,
				endZone.getZone(),
				-1706, 
				-3183,
				"Right");
		bossExit.setZ(-100);
		
	}
	
	
/////////////////////////////
//////SHADOW ELEVATOR //////
///////////////////////////
	
	// Move bossFight up
	public static void startBossFight() {
	
		// Play frantic music.
		music m = music.startMusic(zoneMusicFrantic);
		if(m!=null) m.stopOnDeath = true;
		
		bossFight.startFight();
	}
	
	static wolfless bossFight;
	
	// Create shadow dude bossFight
	public static void createShadowBossFightAroundPlayer() {
		
		if(!bossFightLoaded) {
			bossFightLoaded = true;
			bossFight = new wolfless();
		}
	}
	
	// Deal with the first well we encounters.
	public void dealWithRegionStuff() {
		player currPlayer = player.getPlayer();
		if(currPlayer != null && currPlayer.isWithin(3026,1359,3522,1845) && shadowBossFightStarted!=null && !shadowBossFightStarted.isCompleted() && !shadowBossFightFinished.isCompleted()) {
			shadowBossFightStarted.setCompleted(true);
			shadowBossFightFirstTime = true;
		}
	}
	
	
	// Deal with shadow bossFight stuff
	public void dealWithShadowBossFightStuff() {
		
		// It's game time, bro!! turner!! wooo!
		if(shadowBossFightStarted!=null && shadowBossFightStarted.isCompleted()) {

			if(!shadowBossFightInitiated && zoneLoaded) {
				
				// If it's the first time.
				if(shadowBossFightFirstTime) {
					music.currMusic.fadeOut(5f);
					shadowBossFightFirstTime = false;
					wolflessFightCinematic w = new wolflessFightCinematic();
					w.start();
					shadowBossFightInitiated = true;
				}
				else {
					wolflessFightCinematic w = new wolflessFightCinematic();
					w.startBossImmediately = true;
					w.start();
					
					shadowBossFightInitiated = true;
				}
			}
		}
		
	}
	
	// Do zone specific tasks that aren't monitored by
	// zone specific units. 
	@Override
	public void update() {
		dealWithRegionStuff();
		dealWithShadowBossFightStuff();
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