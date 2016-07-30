package doodads.sheepFarm;

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

public class woolPiece extends chunk {
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default name.
	private static String DEFAULT_CHUNK_NAME = "A piece of wool";
	
	// Sprite stuff.
	private static String DEFAULT_CHUNK_SPRITESHEET = "images/doodads/farmLand/sheepFarm/woolPiece.png";
	
	// Dimensions
	private static int DEFAULT_CHUNK_WIDTH = 18;
	private static int DEFAULT_CHUNK_HEIGHT = 7;
	
	// The actual type.
	private static generalChunkType typeReference = new generalChunkType(DEFAULT_CHUNK_NAME, DEFAULT_CHUNK_SPRITESHEET, DEFAULT_CHUNK_WIDTH, DEFAULT_CHUNK_HEIGHT);  
	
	////////////////
	/// FIELDS /////
	////////////////
	
	// Sequence
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
		startOfConversation = new textSeries(null, "A piece of wool. The sheep went this way.");
		startOfConversation.setEnd();
		
		return new interactBox(startOfConversation, this);
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
	public woolPiece(int newX, int newY, int i) {
		super(typeReference, newX, newY, i, 0);
		if(mode.getCurrentMode().equals("topDown")) {
			setHitBoxAdjustmentY(0);
			setWidth(DEFAULT_CHUNK_WIDTH);
			setHeight(DEFAULT_CHUNK_HEIGHT);
		}
		else {
			setHitBoxAdjustmentY(0);
			setHeight(DEFAULT_CHUNK_HEIGHT);
			setWidth(DEFAULT_CHUNK_WIDTH);
		}
		
		// Interactable.
		setInteractable(true);
		interactSequence = makeNormalInteractSequence();
		
		// Passable.
		setPassable(true);
	}
}
