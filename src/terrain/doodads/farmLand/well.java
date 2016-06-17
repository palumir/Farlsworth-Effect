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
import terrain.chunkTypes.water;
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
			interactSequence = water.makeInteractSequence();
			
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
		interactSequence = water.makeInteractSequence();
		
		// Passable.
		setPassable(false);
	}
}
