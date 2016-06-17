package terrain.chunkTypes;

import java.util.Random;

import drawing.camera;
import drawing.userInterface.interactBox;
import interactions.textSeries;
import main.main;
import modes.mode;
import terrain.chunk;
import terrain.chunkType;
import terrain.generalChunkType;
import terrain.groundTile;
import terrain.doodads.farmLand.needlestack;
import units.humanType;
import units.player;
import units.unit;
import units.unitType;
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
	
	// Have healed?
	private boolean haveHealed = true;
	
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
	public static interactBox makeInteractSequence() {
		
		// Placeholder for each individual textSeries.
		textSeries s;
		
		// Start of conversation.
		textSeries startOfConversation = new textSeries("StartWithButtons", "StartWithButtons");

		// Heal.
		s = startOfConversation.addChild("Refill bottle and heal", "Equipped bottle filled and health restored.");
		s.setEnd();
		
		// Save and reset.
		textSeries saveGame = startOfConversation.addChild("Save game", "Saving the game will reset mobs. Are you sure?");
		
		// Warning
		s = saveGame.addChild("Yes", "Game saved and mobs reset.");
		s.setEnd();
		
		s = saveGame.addChild("No", "Game has not been saved.");
		s.setEnd();

		return new interactBox(startOfConversation, stringUtils.toTitleCase(DEFAULT_CHUNK_NAME));
	}
	
	// Interact stuff.
	public void doInteractStuff() {
		
		if(interactSequence != null) {
			// Save
			if(!haveSaved && interactSequence.getTheText().getButtonText().equals("Yes")) {
				if(player.getCurrentPlayer().getEquippedBottle()!=null) player.getCurrentPlayer().getEquippedBottle().refill();
				saveState.createSaveState();
				haveSaved = true;
				interactSequence.toggleDisplay();
				main.restartGame();
			}
			
			// Don't save.
			if(interactSequence.getTheText().getButtonText().equals("No")) {
				
			}
			
			// Heal
			if(!haveHealed && interactSequence.getTheText().getButtonText().equals("Refill bottle and heal")) {
				player.getCurrentPlayer().setHealthPoints(player.getCurrentPlayer().getMaxHealthPoints());
				if(player.getCurrentPlayer().getEquippedBottle()!=null) player.getCurrentPlayer().getEquippedBottle().refill();
				haveHealed = true;
			}
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
		
		// Restart sequence.
		interactSequence = makeInteractSequence();
		
		// Reset booleans
		haveSaved = false;
		haveHealed = false;
		
		// Toggle display.
		interactSequence.toggleDisplay();
	}
}
