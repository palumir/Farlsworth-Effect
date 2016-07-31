package terrain.chunkTypes;

import drawing.userInterface.tooltipString;
import interactions.interactBox;
import interactions.textSeries;
import main.main;
import sounds.sound;
import terrain.chunk;
import terrain.generalChunkType;
import units.player;
import utilities.saveState;
import zones.zone;

public class water extends chunk {
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default name.
	private static String DEFAULT_CHUNK_NAME = "Water";
	
	// Tile sprite stuff
	private static String DEFAULT_CHUNK_SPRITESHEET = "images/terrain/water.png";
	
	// Sound
	public static String waterSplash = "sounds/effects/doodads/waterSplash.wav";
	
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
	
	// A tile of water.
	public water(int newX, int newY) {
		super(typeReference, newX, newY);
		setInteractable(true);
		this.setPassable(false);
		backgroundDoodad = true;
	}
	
	// A water source.
	public water(generalChunkType typeReference, int newX, int newY) {
		super(typeReference, newX, newY);
		setInteractable(true);
		this.setPassable(false);
	}
	
	// Create function
	public static chunk createChunk(int newX, int newY) {
		if(!zone.loadedOnce) {
			chunk t = new water(newX,newY);
			t.setReloadObject(false);
			return t;
		}
		else {
			return null;
		}
	}

	// Create interact sequence
	public interactBox makeInteractSequence() {
		
		// Placeholder for each individual textSeries.
		textSeries s;
		
		// Start of conversation.
		textSeries startOfConversation = new textSeries("StartWithButtons", "StartWithButtons");
		
		// Save and reset.
		textSeries saveGame = startOfConversation.addChild("Save game", "Saving the game will reset mobs. Are you sure?");
		
		// Cancel
		textSeries cancel = startOfConversation.addChild("Cancel", "The game was not saved.");
		cancel.setEnd();
		
		// Warning
		s = saveGame.addChild("Yes", "Game saved and mobs reset.");
		s.setEnd();
		
		s = saveGame.addChild("No", "The game was not saved.");
		s.setEnd();

		return new interactBox(startOfConversation, this);
	}
	
	// Interact stuff.
	public void doInteractStuff() {
		
		if(interactSequence != null) {
			// Save
			if(!haveSaved && interactSequence.getTheText().getButtonText().equals("Yes")) {
				if(player.getPlayer().getEquippedBottle()!=null) player.getPlayer().getEquippedBottle().refill();
				saveState.createSaveState();
				haveSaved = true;
				interactSequence.toggleDisplay();
				main.restartGame("Save");
			}
			
			// Don't save.
			if(interactSequence.getTheText().getButtonText().equals("No")) {
				
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
		
		// Play sound
		sound s = new sound(waterSplash);
		s.start();
		
		// Restart sequence.
		interactSequence = makeInteractSequence();
		
		// Reset booleans
		haveSaved = false;
		
		// Toggle display.
		interactSequence.toggleDisplay();

	}
}
