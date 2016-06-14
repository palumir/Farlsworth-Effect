package terrain.doodads.farmLand;

import java.util.Random;

import drawing.camera;
import drawing.userInterface.interactBox;
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

public class horizontalGate extends chunk {
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default name.
	private static String DEFAULT_CHUNK_NAME = "horizontalGate";
	
	// Sprite stuff.
	private static String DEFAULT_CHUNK_SPRITESHEET = "images/doodads/farmLand/"+ DEFAULT_CHUNK_NAME + ".png";
	
	// Sequence for fences.
	private interactBox interactSequence;
	
	// Dimensions
	public static int DEFAULT_CHUNK_WIDTH = 46;
	public static int DEFAULT_CHUNK_HEIGHT = 34;
	
	// The actual type.
	private static generalChunkType typeReference = new generalChunkType(DEFAULT_CHUNK_NAME, DEFAULT_CHUNK_SPRITESHEET, DEFAULT_CHUNK_WIDTH, DEFAULT_CHUNK_HEIGHT);  
	
	///////////////
	/// METHODS ///
	///////////////
	// Constructor
	public horizontalGate(int newX, int newY, int i) {
		super(typeReference, newX, newY, i, 0);
		if(mode.getCurrentMode().equals("topDown")) {
			setHitBoxAdjustmentY(14);
			height = 6;
		}
		else {
			setHitBoxAdjustmentY(0);
			height = DEFAULT_CHUNK_HEIGHT;
			width = DEFAULT_CHUNK_WIDTH;
		}
		setPassable(false);
		if(i==1) setPassable(true);
		interactable = true;
	}
	
	// Create interact sequence
	public interactBox makeNormalInteractSequence() {
		
		// Placeholder for each individual textSeries.
		textSeries s;
					
		// Start of conversation.
		textSeries startOfConversation = null;
			
		// Start of conversation.
		startOfConversation = new textSeries("StartWithButtons", "StartWithButtons");
		s = startOfConversation.addChild("Open", "You don't have the key.");
		s.setEnd();
		
		return new interactBox(startOfConversation, "Gate");
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
	
	// Open the gate
	public void open() {
		setPassable(true);
		chunkImage = typeReference.getChunkImage(1, 0);
	}
	
	// Close the gate
	public void close() {
		setPassable(false);
		chunkImage = typeReference.getChunkImage(0, 0);
	}
}
