package terrain.doodads.farmLand;

import java.util.Random;

import drawing.camera;
import drawing.userInterface.interactBox;
import interactions.gag;
import interactions.textSeries;
import main.main;
import modes.mode;
import terrain.chunk;
import terrain.chunkType;
import terrain.generalChunkType;
import units.humanType;
import units.player;
import units.unit;
import units.unitType;
import utilities.saveState;
import utilities.stringUtils;
import utilities.time;
import zones.zone;

public class well extends chunk {
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default name.
	private static String DEFAULT_CHUNK_NAME = "Water Well";
	
	// Sprite stuff.
	private static String DEFAULT_CHUNK_SPRITESHEET = "images/doodads/farmLand/well.png";
	
	// Dimensions
	private static int DEFAULT_CHUNK_WIDTH = 65;
	private static int DEFAULT_CHUNK_HEIGHT = 39;
	
	// The actual type.
	private static generalChunkType typeReference = new generalChunkType(DEFAULT_CHUNK_NAME, DEFAULT_CHUNK_SPRITESHEET, DEFAULT_CHUNK_WIDTH, DEFAULT_CHUNK_HEIGHT);  
	
	// Have saved?
	private boolean haveSaved = true;
	
	// Have healed?
	private boolean haveHealed = true;
	
	////////////////
	/// FIELDS /////
	////////////////
	
	// Sequence for haystacks.
	private interactBox interactSequence;
	
	///////////////
	/// METHODS ///
	///////////////
	
	// Create interact sequence
		public interactBox makeInteractSequence() {
			
			// Placeholder for each individual textSeries.
			textSeries s;
			
			// Start of conversation.
			textSeries startOfConversation = new textSeries("StartWithButtons", "StartWithButtons");
			
			// Save and reset.
			textSeries saveGame = startOfConversation.addChild("Save game", "Saving the game will reset mobs. Are you sure?");
			
			// Warning
			s = saveGame.addChild("Yes", "Game saved and mobs reset.");
			s.setEnd();
			
			s = saveGame.addChild("No", "Game has not been saved.");
			s.setEnd();
			
			// Heal.
			s = startOfConversation.addChild("Heal", "Potions filled and health restored.");
			s.setEnd();

			return new interactBox(startOfConversation, stringUtils.toTitleCase(DEFAULT_CHUNK_NAME));
		}
		
		// Interact stuff.
		public void doInteractStuff() {
			
			if(interactSequence != null) {
				// Save
				if(!haveSaved && interactSequence.getTheText().getButtonText().equals("Yes")) {
					saveState.createSaveState();
					haveSaved = true;
					interactSequence.toggleDisplay();
					main.restartGame();
				}
				
				// Don't save.
				if(interactSequence.getTheText().getButtonText().equals("No")) {
					
				}
				
				// Heal
				if(!haveHealed && interactSequence.getTheText().getButtonText().equals("Heal")) {
					player.getCurrentPlayer().setHealthPoints(player.getCurrentPlayer().getMaxHealthPoints());
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
	
	// Constructor
	public well(int newX, int newY, int i) {
		super(typeReference, newX, newY, i, 0);
		if(mode.getCurrentMode().equals("topDown")) {
			setHitBoxAdjustmentY(10);
			setWidth(35);
			setHeight(18);
		}
		else {
			setHitBoxAdjustmentY(0);
			setHeight(DEFAULT_CHUNK_HEIGHT);
			setWidth(DEFAULT_CHUNK_WIDTH);
		}
		
		// Interactable.
		interactable = true;
		interactSequence = makeInteractSequence();
		
		// Passable.
		setPassable(false);
	}
}
