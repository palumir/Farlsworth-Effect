package zones.farmTomb.subZones;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import doodads.tomb.stairsUp;
import drawing.background;
import drawing.spriteSheet;
import interactions.event;
import items.bottleShards.jumpBottleShard;
import items.bottles.pushBottle;
import items.other.bottleExpander;
import sounds.music;
import terrain.chunk;
import terrain.atmosphericEffects.fog;
import terrain.chunkTypes.tomb;
import terrain.chunkTypes.tombEdge;
import units.player;
import units.unit;
import units.bosses.graveKeeper.cinematics.wolflessFightCinematic;
import units.bosses.wolfless.wolfless;
import units.characters.farlsworth.farlsworth;
import units.unitCommands.commandList;
import units.unitCommands.commands.moveCommand;
import units.unitTypes.tomb.lightDude;
import units.unitTypes.tomb.shadowDude;
import utilities.intTuple;
import utilities.levelSave;
import utilities.saveState;
import zones.zone;
import zones.sheepFarm.subZones.sheepFarm;

public class bossFightZone extends zone {
	
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
	public static boolean shadowBossFightFirstTime;
	
	// Initiated?
	public boolean shadowBossFightInitiated = false;
	
	// Defaults
	public static intTuple DEFAULT_SPAWN_TUPLE = new intTuple(0,-50);
	
	 // BossFight loaded?
	static boolean bossFightLoaded = false;

	// Constructor
	public bossFightZone() {
		super("bossFightZone", "farmLand");
		zoneMusics.add(zoneMusic);
		zoneMusics.add(zoneMusicFrantic);
	}
		
	///////////////////////////////
	// SPAWN PATTERNS/GENERATORS //
	///////////////////////////////
	
	
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
		farmTomb.zoneFog = new fog();
		farmTomb.zoneFog.setTo(0.3f);
		
		// Events
		loadZoneEvents();
		
		// BossFight not initiated
		shadowBossFightInitiated = false;
		
		// Load stuff so the zone doesn't lag
		preLoadStuff();
				
		// Background
		background.setGameBackground(DEFAULT_ZONE_BACKGROUND);
		
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
				// Load bossFight
				//createShadowBossFightAroundPlayer();
			}
		}
		
	}
	
	// PreloadStuff
	public void preLoadStuff() {
		int holder = wolfless.leniency;
	}
	
	// Load zone events.
	public void loadZoneEvents() {
		
		// Has the BossFight started?
		shadowBossFightStarted = event.createEvent("tombZoneShadowBossFightStarted");
	}
	
	//////////////////////
	// INDIVIDUAL AREAS //
	//////////////////////
	public void createTerrain() {
		
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
	
	
	// Deal with shadow bossFight stuff
	public void dealWithShadowBossFightStuff() {
		
		// It's game time, bro!! turner!! wooo!
		if(!shadowBossFightInitiated && zoneLoaded) {
				wolflessFightCinematic w = new wolflessFightCinematic();
				w.startBossImmediately = true;
				w.start();
				
				shadowBossFightInitiated = true;
		}
		
	}
	
	// Do zone specific tasks that aren't monitored by
	// zone specific units. 
	@Override
	public void update() {
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