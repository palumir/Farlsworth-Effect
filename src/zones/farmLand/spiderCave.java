package zones.farmLand;

import java.awt.Color;

import modes.platformer;
import modes.topDown;
import terrain.chunk;
import terrain.chunkType;
import terrain.generalChunkType;
import terrain.chunkTypes.cave;
import terrain.chunkTypes.grass;
import terrain.chunkTypes.water;
import terrain.chunkTypes.wood;
import terrain.doodads.farmLand.bush;
import terrain.doodads.farmLand.fenceBars;
import terrain.doodads.farmLand.fenceBarsSmall;
import terrain.doodads.farmLand.fencePost;
import terrain.doodads.farmLand.horizontalGate;
import terrain.doodads.farmLand.rock;
import terrain.doodads.farmLand.tree;
import terrain.doodads.farmLand.verticalFence;
import units.player;
import units.unit;
import units.unitType;
import units.unitTypes.farmLand.knight;
import utilities.intTuple;
import utilities.utility;
import zones.zone;

public class spiderCave extends zone {
	
	//////////////////////////////////
	// FIELDS, GLOBALS, CONSTRUCTOR //
	//////////////////////////////////
	
	// Static caller of the zone.
	private static zone zoneReference;
	
	// References we will use throughout.
	unit u;
	chunk c;
	
	// Defaults
	public static intTuple DEFAULT_SPAWN_TUPLE = new intTuple(0,-50);
	
	// Constructor
	public spiderCave() {
		super("spiderCave", "farmLand");
	}
	
	///////////////////////////////
	// SPAWN PATTERNS/GENERATORS //
	///////////////////////////////
	
	/////////////////
	// ZONE LOADER //
	/////////////////
	// Load the zone.
	public void loadZone() {
		
		// Set the mode of the zone of course.
		platformer.setMode();
		
		// Spawn area.
		createSpawnArea();
		
	}
	
	//////////////////////
	// INDIVIDUAL AREAS //
	//////////////////////
	
	// Spawn area.
	public void createSpawnArea() {
		// References we will use throughout.
		unit u;
		chunk c;
		int max = 0;
		int iteratorX = 0;
		
		////////////////
		// Spawn Area //
		////////////////
		
		u = new knight(120, -100);
		
		// First platform
		
		max = 10;
		for(int i = 0; i < max; i++) {
			c = new cave(32*i, 40);
		}		
		iteratorX += max*32;
		
		max = 4;
		for(int i = 0; i < max; i++) {
			c = new cave(iteratorX + 32*i, 40+32);
		}
		iteratorX += max*32;
		
		max = 8;
		for(int i = 0; i < max; i++) {
			c = new cave(iteratorX + 32*i, 40+32-32);
		}
		iteratorX += max*32;
		
		max = 2;
		for(int i = 0; i < max; i++) {
			c = new cave(iteratorX + 32*i, 40+32-32+32);
		}
		iteratorX += max*32;
		
		max = 4;
		for(int i = 0; i < max; i++) {
			c = new cave(iteratorX + 32*i, 40+32-32+32+32);
		}
		iteratorX += max*32;
		
		max = 3;
		for(int i = 0; i < max; i++) {
			c = new cave(iteratorX + 32*i, 40+32-32+32+32+32);
		}
		iteratorX += max*32;
		
		max = 3;
		for(int i = 0; i < max; i++) {
			c = new cave(iteratorX + 32*i + 64, 40+32-32+32+32+32-32-32);
		}
		iteratorX += max*32 + 64;
		
		max = 10;
		for(int i = 0; i < max; i++) {
			c = new cave(iteratorX + 32*i + 64, 40+32-32+32+32+32-32-32+32);
		}
		iteratorX += max*32 + 64;
		
		// Zone loaded.
		setZoneLoaded(true);
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