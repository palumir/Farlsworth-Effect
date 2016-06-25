package doodads.farmLand;

import java.util.Random;

import drawing.camera;
import drawing.userInterface.interactBox;
import interactions.textSeries;
import modes.mode;
import terrain.chunk;
import terrain.chunkType;
import terrain.generalChunkType;
import units.humanType;
import units.player;
import units.unit;
import units.unitType;
import utilities.stringUtils;
import utilities.time;
import utilities.utility;
import zones.zone;

public class lever extends chunk {
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default name.
	private static String DEFAULT_CHUNK_NAME = "lever";
	
	// Sprite stuff.
	private static String DEFAULT_CHUNK_SPRITESHEET = "images/doodads/farmLand/"+ DEFAULT_CHUNK_NAME + ".png";
	
	// Bushtype
	private int bushType = 0;
	
	// Dimensions
	private static int DEFAULT_CHUNK_WIDTH = 26;
	private static int DEFAULT_CHUNK_HEIGHT = 25;
	
	// The actual type.
	private static generalChunkType typeReference = new generalChunkType(DEFAULT_CHUNK_NAME, DEFAULT_CHUNK_SPRITESHEET, DEFAULT_CHUNK_WIDTH, DEFAULT_CHUNK_HEIGHT);
	
	////////////
	// FIELDS //
	////////////
	private interactBox interactSequence;
	
	// Is it the bush lever?
	private boolean bushLever = false;
	
	// On or off
	private boolean on = false;
	
	///////////////
	/// METHODS ///
	///////////////
	// Constructor
	public lever(int newX, int newY, int i) {
		super(typeReference, newX, newY, i, 0);
		bushType = i;
		if(mode.getCurrentMode().equals("topDown")) {
			setHitBoxAdjustmentY(5);
			setWidth(50);
			setHeight(15);
		}
		else {
			setHitBoxAdjustmentY(0);
			setHeight(DEFAULT_CHUNK_HEIGHT);
			setWidth(DEFAULT_CHUNK_WIDTH);
		}
		
		// Set interactable.
		interactable = true;
		
		// Set not passable.
		setPassable(false);
	}
	
	// Create interact sequence
	public interactBox makeNormalInteractSequence() {
		
		// Placeholder for each individual textSeries.
		textSeries s;
					
		// Start of conversation.
		textSeries startOfConversation = null;
		if(isBushLever()) {
			// Start of conversation.
			startOfConversation = new textSeries("Start with buttons", "A lever.");
				
			if(!on) {
				s = startOfConversation.addChild("Switch lever on", "The lever was switched on.");
				s.setEnd();
			}
			else {
				s = startOfConversation.addChild("Switch lever off", "The lever was switched off.");
				s.setEnd();
			}
		}	
		return new interactBox(startOfConversation, stringUtils.toTitleCase(DEFAULT_CHUNK_NAME));
	}
	
	// Interact stuff.
	public void doInteractStuff() {
		
		// If they choose to eat a berry.
		if(bushLever)  {
			
			// On.
			if(!on &&
				interactSequence != null 
				&& interactSequence.getTheText().isEnd() 
				&& interactSequence.getTheText().getButtonText() != null
				&& interactSequence.getTheText().getButtonText().equals("Switch lever on")) {
				
				// TODO: OPEN SECRET PASSAGE.
				System.out.println("Open secret passage TODO:");
				setOn();
			}
			
			// Off.
			if(on &&
				interactSequence != null 
				&& interactSequence.getTheText().isEnd() 
				&& interactSequence.getTheText().getButtonText() != null
				&& interactSequence.getTheText().getButtonText().equals("Switch lever off")) {
					
				// TODO: CLOSE SECRET PASSAGE.
				setOff();
			}
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
	
	// Open the gate
	public void setOn() {
		on = true;
		chunkImage = typeReference.getChunkImage(1, 0);
	}
	
	// Close the gate
	public void setOff() {
		on = false;
		chunkImage = typeReference.getChunkImage(0, 0);
	}

	public boolean isBushLever() {
		return bushLever;
	}

	public void setBushLever(boolean bushLever) {
		this.bushLever = bushLever;
	}
}
