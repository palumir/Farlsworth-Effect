package terrain.chunkTypes;

import java.util.Random;

import drawing.camera;
import modes.mode;
import terrain.chunk;
import terrain.chunkType;
import terrain.generalChunkType;
import terrain.groundTile;
import units.humanType;
import units.player;
import units.unit;
import units.unitType;
import utilities.saveState;
import utilities.time;
import zones.zone;

public class water extends groundTile {
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default name.
	private static String DEFAULT_CHUNK_NAME = "water";
	
	// Tile sprite stuff
	private static String DEFAULT_CHUNK_SPRITESHEET = "images/terrain/" + DEFAULT_CHUNK_NAME + ".png";
	
	// Dimensions
	public static int DEFAULT_CHUNK_WIDTH = 32;
	public static int DEFAULT_CHUNK_HEIGHT = 32;
	
	// The actual type.
	private static generalChunkType typeReference = new generalChunkType(DEFAULT_CHUNK_NAME, DEFAULT_CHUNK_SPRITESHEET, DEFAULT_CHUNK_WIDTH, DEFAULT_CHUNK_HEIGHT);  
	
	///////////////
	/// METHODS ///
	///////////////
	// Constructor
	public water(int newX, int newY) {
		super(typeReference, newX, newY);
		interactable = true;
		this.setPassable(false);
	}
	
	// Interacting with heals you and saves.
	@Override
	public void interactWith() {
		player.getCurrentPlayer().setHealthPoints(player.getCurrentPlayer().getMaxHealthPoints());
		saveState.createSaveState();
	}
}
