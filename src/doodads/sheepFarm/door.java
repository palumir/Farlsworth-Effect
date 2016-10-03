package doodads.sheepFarm;

import doodads.openable;
import interactions.event;
import interactions.interactBox;
import interactions.textSeries;
import modes.mode;
import sounds.sound;
import terrain.chunk;
import terrain.generalChunkType;
import units.player;
import units.characters.farlsworth.farlsworth;
import zones.zone;
import zones.sheepFarm.sheepFarm;

public class door extends openable {
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default name.
	private static String DEFAULT_CHUNK_NAME = "door";
	
	// Sprite stuff.
	private static String DEFAULT_CHUNK_SPRITESHEET = "images/doodads/farmLand/sheepFarm/"+ DEFAULT_CHUNK_NAME + ".png";
	
	// Dimensions
	public static int DEFAULT_CHUNK_WIDTH = 33;
	public static int DEFAULT_CHUNK_HEIGHT = 55;
	
	// The actual type.
	private static generalChunkType typeReference = new generalChunkType(DEFAULT_CHUNK_NAME, DEFAULT_CHUNK_SPRITESHEET, DEFAULT_CHUNK_WIDTH, DEFAULT_CHUNK_HEIGHT);  
	
	// FIELDS
	
	private zone toZone;
	private int spawnX;
	private int spawnY;
	private String spawnDirection;
	
	///////////////
	/// METHODS ///
	///////////////
	// Constructor
	public door(String newKeyName, int newX, int newY, zone newZone, int newSpawnX, int newSpawnY, String direction) {
		super(typeReference, newKeyName, newX, newY);
		
		toZone = newZone;
		spawnDirection = direction;
		spawnX = newSpawnX;
		spawnY = newSpawnY;
		
		// Key name.
		setKeyName(newKeyName);
		
		// Check if we have save data on the gate.
		if(isOpen.isCompleted()) {
			forceOpen();
		}
		else {
			setPassable(true);
		}
		
		if(mode.getCurrentMode().equals("topDown")) {
			setHeight(DEFAULT_CHUNK_HEIGHT);
			setWidth(DEFAULT_CHUNK_WIDTH);
		}
		else {
			setHeight(DEFAULT_CHUNK_HEIGHT);
			setWidth(DEFAULT_CHUNK_WIDTH);
		}
		setInteractable(true);
	}
	
	// Update.
	@Override
	public void update() {
		if(this.collides(this.getIntX(), this.getIntY(), player.getPlayer()) && isOpen()) {
			enter();
		}
		doInteractStuff();
	}
	
	// Enter the cave
	public void enter() {
		zone.switchZones(player.getPlayer(), player.getPlayer().getCurrentZone(), toZone, spawnX, spawnY, spawnDirection);
	}

	@Override
	public generalChunkType getTypeReference() {
		// TODO Auto-generated method stub
		return typeReference;
	}
}
