package terrain.chunkTypes;

import java.util.Random;

import drawing.camera;
import modes.mode;
import quests.textSeries;
import terrain.chunk;
import terrain.chunkType;
import terrain.generalChunkType;
import terrain.groundTile;
import terrain.doodads.farmLand.needlestack;
import units.humanType;
import units.player;
import units.unit;
import units.unitType;
import userInterface.interactBox;
import utilities.saveState;
import utilities.stringUtils;
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
	
	// Interact sequence.
	private static interactBox interactSequence;
	
	// Have saved?
	private boolean haveSaved = true;
	
	///////////////
	/// METHODS ///
	///////////////
	// Constructor
	public water(int newX, int newY) {
		super(typeReference, newX, newY);
		interactable = true;
		this.setPassable(false);
	}
	
	// Create interact sequence
	public interactBox makeInteractSequence() {
		
		// Placeholder for each individual textSeries.
		textSeries s;
		
		// Start of conversation.
		textSeries startOfConversation = new textSeries("StartWithButtons", "StartWithButtons");
		s = startOfConversation.addChild("Save game and heal.", "Game saved, potion filled, and healed to full health.");
		s.setEnd();
		
		return new interactBox(startOfConversation, stringUtils.toTitleCase(DEFAULT_CHUNK_NAME));
	}
	
	// Interact stuff.
	public void doInteractStuff() {
		// If we have reached the end of our quest conversation (and they clicked yes, of course, since it's all they can do.)
		if(!haveSaved && interactSequence != null && interactSequence.getTheText().isEnd()) {
			saveState.createSaveState();
			haveSaved = true;
		}
	}
	
	// Update
	@Override
	public void update() {
		doInteractStuff();
	}
	
	// Interacting with heals you and saves.
	@Override
	public void interactWith() {
		if(interactSequence == null || interactSequence.getTheText().isEnd()) {
			haveSaved = false;
			interactSequence = makeInteractSequence();
		}
		interactSequence.toggleDisplay();
	}
}
