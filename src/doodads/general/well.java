package doodads.general;

import java.awt.Point;

import effects.effectTypes.items.savePoint;
import interactions.interactBox;
import interactions.textSeries;
import items.bottle;
import items.item;
import items.bottles.saveBottle;
import main.main;
import modes.mode;
import sounds.sound;
import terrain.chunk;
import terrain.generalChunkType;
import units.player;
import utilities.saveState;

public class well extends chunk {
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default name.
	private static String DEFAULT_CHUNK_NAME = "Water Well";
	
	// Sprite stuff.
	private static String DEFAULT_CHUNK_SPRITESHEET = "images/doodads/farmLand/sheepFarm/well.png";
	
	// Dimensions
	private static int DEFAULT_CHUNK_WIDTH = 65;
	private static int DEFAULT_CHUNK_HEIGHT = 39;
	
	// The actual type.
	private static generalChunkType typeReference = new generalChunkType(DEFAULT_CHUNK_NAME, DEFAULT_CHUNK_SPRITESHEET, DEFAULT_CHUNK_WIDTH, DEFAULT_CHUNK_HEIGHT);  
	
	// Sound
	public static String waterSplash = "sounds/effects/doodads/waterSplash.wav";
	
	// Interact sequence.
	private static interactBox interactSequence;
	
	// Have saved?
	private boolean haveSaved = true;
		
	// Constructor
	public well(int newX, int newY, int i) {
		super(typeReference, newX, newY);
		setPassable(false);
		setInteractable(true);
		if(mode.getCurrentMode().equals("topDown")) {
			setHitBoxAdjustmentY(10);
			setWidth(35);
			setHeight(18);
		}
		else {
			setHitBoxAdjustmentY(0);
			setHeight(DEFAULT_CHUNK_HEIGHT);
			setWidth(43);
		}
	}
	
	// Create interact sequence
	public interactBox makeInteractSequence() {
		
		// Placeholder for each individual textSeries.
		textSeries s;
		
		// Start of conversation.
		textSeries startOfConversation = new textSeries("StartWithButtons", "StartWithButtons");
		
		// Save and reset.
		textSeries saveGame = null;
		if(!player.getPlayer().getPlayerInventory().containsBottleType(saveBottle.class)) saveGame = startOfConversation.addChild("Save game", "Are you sure you want to save?");
		else {
			if(player.getPlayer().getPlayerInventory().countBottles() == 1) {
				saveGame = startOfConversation.addChild("Save and refill Save Bottle", "Are you sure you want to save and refill your Save Bottle?");
			}
			else {
				saveGame = startOfConversation.addChild("Save and refill ALL bottles", "Are you sure you want to save and refill ALL bottles?");
			}
		}
		
		// Cancel
		textSeries cancel = startOfConversation.addChild("Cancel", "The game was not saved.");
		cancel.setEnd();
		
		// Warning
		s = saveGame.addChild("Yes", "The game was saved.");
		s.setEnd();
		
		s = saveGame.addChild("No", "The game was not saved.");
		s.setEnd();

		return new interactBox(startOfConversation, this);
	}
	
	// Interact stuff.
	public void doInteractStuff() {
		
		if(interactSequence != null) {
			// Save
			if(!haveSaved && interactSequence.getTextSeries().getButtonText().equals("Yes")) {
				
				// Set the well to be the last well the player used.
				player.getPlayer().lastWell = new Point(player.getPlayer().getIntX(), player.getPlayer().getIntY());
				
				// Save and refresh.
				haveSaved = true;
				interactSequence.toggleDisplay();
				
				// Destroy last bottle charge indicator because we saved at a well!
				if(player.getPlayer().lastSaveBottles != null) player.getPlayer().lastSaveBottles.clear();
				if(player.getPlayer().lastSaveBottleChargeIndicator!=null) {
					player.getPlayer().lastSaveBottleChargeIndicator = null;
				}
				
				// Refresh, of course
				refreshPlayer("Save");
			}
			
			// Don't save.
			if(interactSequence.getTextSeries().getButtonText().equals("No")) {
				
			}
		}
	}
	
	
	// Refresh player.
	public static void refreshPlayer(String reason) {
		
		// Refill all bottles in inventory.
		if(player.getPlayer().getPlayerInventory()!=null) {
			for(int i = 0; i < player.getPlayer().getPlayerInventory().size(); i++) {
				item currItem = player.getPlayer().getPlayerInventory().get(i);
				if(currItem instanceof bottle) ((bottle)currItem).refill();
			}
		}
		
		if(reason.equals("respawnAtWell")) {
			
			// Clear last save.
			player.getPlayer().lastSaveBottles.clear();
			if(player.getPlayer().lastSaveBottleChargeIndicator!=null) {
				player.getPlayer().lastSaveBottleChargeIndicator.destroy();
				player.getPlayer().lastSaveBottleChargeIndicator = null;
			}
			
			// Comment this in to make black wolves respawn at their original spot
			// on respawn at well.
			/*
			saveState.setQuiet(true);
			saveState.createSaveState();
			saveState.setQuiet(false);
			*/
		}
		
		// Saving at well.
		else {
			saveState.createSaveState();
			main.restartGame(reason);
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
