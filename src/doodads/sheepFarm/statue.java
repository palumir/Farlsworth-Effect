package doodads.sheepFarm;

import java.util.Random;

import drawing.camera;
import drawing.gameCanvas;
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

public class statue extends chunk {
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default name.
	private static String DEFAULT_CHUNK_NAME = "statue";
	
	// Sprite stuff.
	private static String DEFAULT_CHUNK_SPRITESHEET = "images/doodads/farmLand/sheepFarm/"+ DEFAULT_CHUNK_NAME + ".png";
	
	// Dimensions
	public static int DEFAULT_CHUNK_WIDTH = 64;
	public static int DEFAULT_CHUNK_HEIGHT = 96;
	
	// The actual type.
	private static generalChunkType typeReference = new generalChunkType(DEFAULT_CHUNK_NAME, DEFAULT_CHUNK_SPRITESHEET, DEFAULT_CHUNK_WIDTH, DEFAULT_CHUNK_HEIGHT);
	
	////////////////
	/// FIELDS /////
	////////////////
	
	// Sequence
	private interactBox interactSequence;
	
	// Type
	private int type = 0;
	
	///////////////
	/// METHODS ///
	///////////////
	
	// Create interact sequence
	public interactBox makeNormalInteractSequence() {
		
		// Placeholder for each individual textSeries.
		textSeries s;
					
		textSeries startOfConversation = null;
		
		// Statue of weeping woman
		if(type == 1) {
			startOfConversation = new textSeries(null, "A statue of a cloaked woman.");
			s = startOfConversation.addChild(null, "She has no face, but it's clear she's mourning.");
			s.setEnd();
		}
		else {
			startOfConversation = new textSeries(null, "A statue of a woman carrying a basket of flowers.");
			s = startOfConversation.addChild(null, "Well, the statue isn't carrying a real basket of flowers ...");
			s = s.addChild(null, "The basket is part of the statue.");
			s = s.addChild(null, "And so are the flowers.");
			s = s.addChild(null, "So, I guess it's ...");
			s = s.addChild(null, "A statue of a woman carrying a statue of a basket of flowers.");
			s.setEnd();
		}
		
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
	
	///////////////
	/// METHODS ///
	///////////////
	// Constructor
	public statue(int newX, int newY, int i) {
		super(typeReference, newX, newY, i, 0);
		type = i;
		if(mode.getCurrentMode().equals("topDown")) {
			setHitBoxAdjustmentY(40);
			setHeight(20);
			setWidth(20);
		}
		else {
			setHitBoxAdjustmentY(0);
			setHeight(DEFAULT_CHUNK_HEIGHT);
			setWidth(DEFAULT_CHUNK_WIDTH);
		}
		setInteractable(true);
		setPassable(false);
	}
}
