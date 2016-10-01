package doodads.tomb;

import modes.mode;
import terrain.chunk;
import terrain.generalChunkType;
import units.player;
import zones.zone;

public class stairsUp extends chunk {
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default name.
	private static String DEFAULT_CHUNK_NAME = "stairsUp";
	
	// Sprite stuff.
	private static String DEFAULT_CHUNK_SPRITESHEET = "images/doodads/farmLand/tomb/"+ DEFAULT_CHUNK_NAME + ".png";
	
	// Dimensions
	private static int DEFAULT_CHUNK_WIDTH = 43;
	private static int DEFAULT_CHUNK_HEIGHT = 57;
	
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
	public stairsUp(int newX, int newY, zone newZone, int newSpawnX, int newSpawnY, String direction) {
		super(typeReference, newX, newY, 0, 0);
		toZone = newZone;
		spawnDirection = direction;
		spawnX = newSpawnX;
		spawnY = newSpawnY;
		if(mode.getCurrentMode().equals("topDown")) {
			setHitBoxAdjustmentY(0);
			setHeight(10);
			setWidth(20);
		}
		else {
			setHitBoxAdjustmentY(0);
			setHeight(43);
			setWidth(10);
		}
		setPassable(true);
	}
	
	// Update.
	@Override
	public void update() {
		if(this.collides(this.getIntX(), this.getIntY(), player.getPlayer())) {
			enter();
		}
	}
	
	// Enter the cave
	public void enter() {
		zone.switchZones(player.getPlayer(), player.getPlayer().getCurrentZone(), toZone, spawnX, spawnY, spawnDirection);
	}
}
