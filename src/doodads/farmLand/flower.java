package doodads.farmLand;

import java.util.Random;

import drawing.camera;
import interactions.event;
import interactions.interactBox;
import interactions.textSeries;
import modes.mode;
import sounds.sound;
import terrain.chunk;
import terrain.chunkType;
import terrain.generalChunkType;
import units.humanType;
import units.unit;
import units.unitType;
import utilities.stringUtils;
import utilities.time;
import zones.zone;

public class flower extends chunk {
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default name.
	private static String DEFAULT_CHUNK_NAME = "flower";
	
	// Sprite stuff.
	private static String DEFAULT_CHUNK_SPRITESHEET = "images/doodads/farmLand/flower.png";
	
	// Dimensions
	public static int DEFAULT_SPRITE_WIDTH = 18;
	public static int DEFAULT_SPRITE_HEIGHT = 17;

	// Topdown
	private static int DEFAULT_TOPDOWN_ADJUSTMENT_Y = 0;
	private static int DEFAULT_TOPDOWN_WIDTH = DEFAULT_SPRITE_WIDTH;
	private static int DEFAULT_TOPDOWN_HEIGHT = DEFAULT_SPRITE_HEIGHT;

	// Platformer.
	private static int DEFAULT_PLATFORMER_ADJUSTMENT_Y = 0;
	private static int DEFAULT_PLATFORMER_HEIGHT = DEFAULT_SPRITE_WIDTH;
	private static int DEFAULT_PLATFORMER_WIDTH = DEFAULT_SPRITE_HEIGHT;
	
	// Flower type.
	private int flowerType = 0;
	
	// The actual type.
	private static generalChunkType typeReference = new generalChunkType(DEFAULT_CHUNK_NAME, DEFAULT_CHUNK_SPRITESHEET, DEFAULT_SPRITE_WIDTH, DEFAULT_SPRITE_HEIGHT);  
	
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
		
		if(flowerType == 0) {
			startOfConversation = new textSeries(null, "A freshly bloomed dandelion. It's a dazzling hue of yellow.");
			startOfConversation.setEnd();
		}
		if(flowerType == 1) {
			startOfConversation = new textSeries(null, "A rose. Red, like the blood of your enemies.");
			startOfConversation.setEnd();
		}
		if(flowerType == 2) {
			startOfConversation = new textSeries(null, "An old, withered dandelion. It's ready to die.");
			startOfConversation.setEnd();
		}
		if(flowerType == 3) {
			startOfConversation = new textSeries(null, "A rose. Blue.");
			startOfConversation.setEnd();
		}
		if(flowerType == 4) {
			startOfConversation = new textSeries(null, "A bright red prickly weed.");
			s = startOfConversation.addChild("Pull it out", "You pull out the weed by the root.");
			s = s.addChild(null, "One less weed to step on in the world.");
			s.setEnd();
			s = startOfConversation.addChild("Leave it alone", "The fate of the plant will be decided by nature.");
			s.setEnd();
		}
		
		return new interactBox(startOfConversation, stringUtils.toTitleCase(DEFAULT_CHUNK_NAME));
	}
	
	// Interact stuff.
	public void doInteractStuff() {
		if(flowerType == 4 && interactSequence != null && interactSequence.getTheText().isEnd() &&
				interactSequence.getTheText().getTextOnPress() != null &&
				interactSequence.getTheText().getTextOnPress().equals("One less weed to step on in the world.")) {
			
			// Play sound
			sound s = new sound(bush.clearBush);
			s.start();
			
			// Destroy
			this.destroy();
		}
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
	public flower(int newX, int newY, int i) {
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
		
		// Set fields.
		flowerType = i;
		interactable = true;
		setPassable(true);
	}
}
