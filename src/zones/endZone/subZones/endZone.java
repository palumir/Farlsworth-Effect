package zones.endZone.subZones;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import doodads.sheepFarm.tomb;
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
import zones.farmTomb.farmTombZoneLoader;
import zones.farmTomb.subZones.farmTomb;
import zones.sheepFarm.subZones.sheepFarm;

public class endZone extends zone {
	
	//////////////////////////////////
	// FIELDS, GLOBALS, CONSTRUCTOR //
	//////////////////////////////////
	
	// Static caller of the zone.
	private static zone zoneReference;
	
	// Default zone mode
	private static String DEFAULT_ZONE_MODE = "topDown";
	
	// References we will use throughout.
	static unit u;
	static chunk c;
	static commandList commands;
	
	// Some defaults.
	public static int BACKGROUND_Z = -100;

	// Defaults
	public static intTuple DEFAULT_SPAWN_TUPLE = new intTuple(0,-50);
	
	
	// Constructor
	public endZone() {
		super("endZone", "forest");
	}
		
	/////////////////
	// ZONE LOADER //
	/////////////////
	// Load the zone.
	public void loadSpecificZoneStuff() {
		
		// Set the mode of the zone of course.
		setMode(DEFAULT_ZONE_MODE);
		
		new tomb(-1726, -3292, 0, farmTomb.getZone(),6730,1798,"Left");
		
		// Load stuff so the zone doesn't lag
		preLoadStuff();
		
		// Load zone events.
		loadZoneEvents();
		
		// Endzone
		//levelSave.loadSaveState("endZoneLevel.save");
		
		new background(null);
		background.setColor(Color.BLACK);

		// Load zone items
		loadItems();
		
		// Load units
		loadUnits();
		
	}
	
	// PreloadStuff
	public void preLoadStuff() {
	}
	
	// Load items
	public void loadItems() {
	}
	
	// Load units
	public void loadUnits() {
	}
	
	// Load zone events.
	public void loadZoneEvents() {
	}
	
	// Do zone specific tasks that aren't monitored by
	// zone specific units. 
	@Override
	public void update() {
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