package doodads.general;

import java.util.Random;

import drawing.camera;
import interactions.event;
import interactions.interactBox;
import interactions.textSeries;
import modes.mode;
import terrain.chunk;
import terrain.chunkType;
import terrain.generalChunkType;
import units.humanType;
import units.unit;
import units.unitType;
import utilities.stringUtils;
import utilities.time;
import zones.zone;

public class invisibleLightSource extends lightSource {
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default name.
	private static String DEFAULT_CHUNK_NAME = "Invisible Lightsource";
	
	// Dimensions
	public static int DEFAULT_SPRITE_WIDTH = 10;
	public static int DEFAULT_SPRITE_HEIGHT = 10;

	// Topdown
	private static int DEFAULT_TOPDOWN_ADJUSTMENT_Y = 0;
	private static int DEFAULT_TOPDOWN_WIDTH = 10;
	private static int DEFAULT_TOPDOWN_HEIGHT = 10;

	// Platformer.
	private static int DEFAULT_PLATFORMER_ADJUSTMENT_Y = 0;
	private static int DEFAULT_PLATFORMER_HEIGHT = 10;
	private static int DEFAULT_PLATFORMER_WIDTH = 10;
	
	// The actual type.
	private static generalChunkType typeReference = new generalChunkType(DEFAULT_CHUNK_NAME, DEFAULT_SPRITE_WIDTH, DEFAULT_SPRITE_HEIGHT);  
	
	
	// Update
	@Override
	public void update() {
	}
	
	
	// Constructor
	public invisibleLightSource(int newX, int newY) {
		super(typeReference, newX, newY);
		if(mode.getCurrentMode().equals("topDown")) {
			setHitBoxAdjustmentY(DEFAULT_TOPDOWN_ADJUSTMENT_Y);
			setWidth(DEFAULT_TOPDOWN_WIDTH);
			setHeight(DEFAULT_TOPDOWN_HEIGHT);
		}
		else {
			setHitBoxAdjustmentY(DEFAULT_PLATFORMER_ADJUSTMENT_Y);
			setHeight(DEFAULT_PLATFORMER_HEIGHT);
			setWidth(DEFAULT_PLATFORMER_WIDTH);
		}
		
		// Passable
		setPassable(true);
	}
}
