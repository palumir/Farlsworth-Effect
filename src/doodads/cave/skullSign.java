package doodads.cave;

import java.util.Random;

import drawing.camera;
import drawing.userInterface.interactBox;
import interactions.event;
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

public class skullSign extends chunk {
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default name.
	private static String DEFAULT_CHUNK_NAME = "Danger Sign";
	
	// Sprite stuff.
	private static String DEFAULT_CHUNK_SPRITESHEET = "images/doodads/farmLand/skullSign.png";
	
	// Dimensions
	private static int DEFAULT_SPRITE_WIDTH = 33;
	private static int DEFAULT_SPRITE_HEIGHT = 29;

	// Topdown
	private static int DEFAULT_TOPDOWN_ADJUSTMENT_Y = 0;
	private static int DEFAULT_TOPDOWN_WIDTH = 33;
	private static int DEFAULT_TOPDOWN_HEIGHT = 29;

	// Platformer.
	private static int DEFAULT_PLATFORMER_ADJUSTMENT_Y = 0;
	private static int DEFAULT_PLATFORMER_HEIGHT = 33;
	private static int DEFAULT_PLATFORMER_WIDTH = 29;
	
	// The actual type.
	private static generalChunkType typeReference = new generalChunkType(DEFAULT_CHUNK_NAME, DEFAULT_CHUNK_SPRITESHEET, DEFAULT_SPRITE_WIDTH, DEFAULT_SPRITE_HEIGHT);  
	
	////////////////
	/// FIELDS /////
	////////////////
	
	// Sequence for haystacks.
	private interactBox interactSequence;
	
	///////////////
	/// METHODS ///
	///////////////
	
	// Create interact sequence
	public interactBox makeNormalInteractSequence() {
		
		// Placeholder for each individual textSeries.
		textSeries s;
					
		// Start of conversation.
		textSeries startOfConversation = null;
			
		// Start of conversation.
		startOfConversation = new textSeries(null, "Careful of spiders.");
			
		s = startOfConversation.addChild("Heed warning", "You will be careful of spiders.");
		s.setEnd();
		
		s = startOfConversation.addChild("Ignore warning", "You will not be careful of spiders.");
		s.setEnd();
			
		return new interactBox(startOfConversation, stringUtils.toTitleCase(DEFAULT_CHUNK_NAME));
	}
	
	// Interact stuff.
	public void doInteractStuff() {
	}
	
	// Update
	@Override
	public void update() {
		doInteractStuff();
	}
	
	// Interact with object. Should be over-ridden.
	public void interactWith() { 
		interactSequence = makeNormalInteractSequence();
		interactSequence.toggleDisplay();
	}
	
	// Constructor
	public skullSign(int newX, int newY, int i) {
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
		
		// Interactable.
		interactable = true;
		interactSequence = makeNormalInteractSequence();
		
		// Passable.
		setPassable(false);
	}
}
