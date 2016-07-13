package doodads.sheepFarm;

import java.util.Random;

import drawing.camera;
import modes.mode;
import terrain.chunk;
import terrain.chunkType;
import terrain.generalChunkType;
import units.humanType;
import units.player;
import units.unit;
import units.unitType;
import utilities.time;
import zones.zone;
import zones.farmLand.spiderCave;

public class caveEnterance extends chunk {
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default name.
	private static String DEFAULT_CHUNK_NAME = "caveEnterance";
	
	// Sprite stuff.
	private static String DEFAULT_CHUNK_SPRITESHEET = "images/doodads/farmLand/sheepFarm/"+ DEFAULT_CHUNK_NAME + ".png";
	
	// Dimensions
	private static int DEFAULT_CHUNK_WIDTH = 92;
	private static int DEFAULT_CHUNK_HEIGHT = 82;
	
	// The actual type.
	private static generalChunkType typeReference = new generalChunkType(DEFAULT_CHUNK_NAME, DEFAULT_CHUNK_SPRITESHEET, DEFAULT_CHUNK_WIDTH, DEFAULT_CHUNK_HEIGHT);  
	
	//////////////
	/// FIELDS ///
	//////////////
	private zone toZone;
	private int spawnX;
	private int spawnY;
	private String spawnDirection;
	
	///////////////
	/// METHODS ///
	///////////////
	
	// Constructor
	public caveEnterance(int newX, int newY, int i, zone newZone, int newSpawnX, int newSpawnY, String direction) {
		super(typeReference, newX, newY, i, 0);
		toZone = newZone;
		spawnDirection = direction;
		spawnX = newSpawnX;
		spawnY = newSpawnY;
		if(mode.getCurrentMode().equals("topDown")) {
			setHitBoxAdjustmentY(12);
			setHeight(55);
			setWidth(20);
		}
		else {
			setHitBoxAdjustmentY(12);
			setHeight(55);
			setWidth(20);
		}
		setPassable(true);
	}
	
	// Update.
	@Override
	public void update() {
		if(this.collides(this.getIntX(), this.getIntY(), player.getCurrentPlayer())) {
			enter();
		}
	}
	
	// Enter the cave
	public void enter() {
		zone.switchZones(player.getCurrentPlayer(), player.getCurrentPlayer().getCurrentZone(), toZone, spawnX, spawnY, spawnDirection);
	}
}
