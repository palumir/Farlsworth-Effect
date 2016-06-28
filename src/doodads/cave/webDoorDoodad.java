package doodads.cave;

import java.util.ArrayList;
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

public class webDoorDoodad extends chunk {
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default name.
	private static String DEFAULT_CHUNK_NAME = "Web Door";
	
	// Sprite stuff.
	private static String DEFAULT_CHUNK_SPRITESHEET = "images/doodads/farmLand/spiderCave/webDoor.png";
	
	// Dimensions
	public static int DEFAULT_SPRITE_WIDTH = 77;
	public static int DEFAULT_SPRITE_HEIGHT = 77;

	// Topdown
	public static int DEFAULT_TOPDOWN_ADJUSTMENT_Y = 0;
	public static int DEFAULT_TOPDOWN_HEIGHT = 50;
	public static int DEFAULT_TOPDOWN_WIDTH = 50;

	// Platformer.
	public static int DEFAULT_PLATFORMER_ADJUSTMENT_Y = 0;
	public static int DEFAULT_PLATFORMER_HEIGHT = 50;
	public static int DEFAULT_PLATFORMER_WIDTH = 50;
	
	// Default Z
	private static int DEFAULT_Z = -50;
	
	// The actual type.
	private static generalChunkType typeReference = new generalChunkType(DEFAULT_CHUNK_NAME, DEFAULT_CHUNK_SPRITESHEET, DEFAULT_SPRITE_WIDTH, DEFAULT_SPRITE_HEIGHT); 
	
	// Interact sequence.
	private interactBox interactSequence;
	
	////////////////
	/// FIELDS /////
	////////////////
	
	// Create interact sequence
	public interactBox makeNormalInteractSequence() {
		
		// Placeholder for each individual textSeries.
		textSeries s;
					
		// Start of conversation.
		textSeries startOfConversation = null;
		startOfConversation = new textSeries(null, "It's some sort of super strong web.");
		s = startOfConversation .addChild(null, "Hacking and slashing won't get you through this.");
		s = s .addChild(null, "You'll need something else.");
		s.setEnd();
		
		return new interactBox(startOfConversation, "Web Door");
	}
	
	// Interact with object. Should be over-ridden.
	public void interactWith() { 
		interactSequence = makeNormalInteractSequence();
		interactSequence.toggleDisplay();
	}

	// Update
	@Override
	public void update() {
	}
	
	// Constructor
	public webDoorDoodad(int newX, int newY, int i) {
		super(typeReference, newX, newY, i, 0);
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
		
		// Set z.
		setZ(DEFAULT_Z);
		
		// Passable and interactable.
		interactable = true;
		setPassable(false);
	}
}
