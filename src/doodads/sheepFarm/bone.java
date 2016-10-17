package doodads.sheepFarm;


import java.util.ArrayList;

import interactions.interactBox;
import interactions.textSeries;
import modes.mode;
import terrain.chunk;
import terrain.generalChunkType;
import utilities.utility;

public class bone extends chunk {
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default name.
	private static String DEFAULT_CHUNK_NAME = "bone";
	
	// Sprite stuff.
	private static String DEFAULT_CHUNK_SPRITESHEET = "images/doodads/farmLand/sheepFarm/" + DEFAULT_CHUNK_NAME + ".png";
	
	// Dimensions
	public static int DEFAULT_CHUNK_WIDTH = 32;
	public static int DEFAULT_CHUNK_HEIGHT = 32;
	
	// Interactsequence.
	private interactBox interactSequence;
	
	// The actual type.
	private static generalChunkType typeReference = new generalChunkType(DEFAULT_CHUNK_NAME, DEFAULT_CHUNK_SPRITESHEET, DEFAULT_CHUNK_WIDTH, DEFAULT_CHUNK_HEIGHT); 
	
	// Bone list
	public static ArrayList<bone> bones;
	
	// Num bones
	public static int numBones = 0;
	public int boneNumber;
	
	// Initiate bones
	public static void initiate() {
		numBones = 0;
		bones = new ArrayList<bone>();
	}
	
	///////////////
	/// METHODS ///
	///////////////
	// Constructor
	public bone(int newX, int newY, int i) {
		super(typeReference, newX, newY, i, 0);
		if(mode.getCurrentMode().equals("topDown")) {
			setHitBoxAdjustmentY(0);
			setWidth(28);
			setHeight(5);
		}
		else {
			setHitBoxAdjustmentY(0);
			setHeight(DEFAULT_CHUNK_HEIGHT);
			setWidth(DEFAULT_CHUNK_WIDTH);
		}
		// Do a random interaction.
		setInteractable(true);
		setPassable(true);
		bones.add(this);
		boneNumber = numBones;
		numBones++;
		if(numBones>2) numBones = 0;
	}
	
	// Create interact sequence
		public interactBox makeNormalInteractSequence() {
						
			// Start of conversation.
			textSeries startOfConversation = null;
			
			if(boneNumber == 0) {
				startOfConversation = new textSeries(null, "10/10, would bone.");
				startOfConversation.setEnd();
			}
			
			else if(boneNumber == 1) {
				startOfConversation = new textSeries(null, "This guy met a ruff end.");
				startOfConversation.setEnd();
			}
			
			else {
				startOfConversation = new textSeries(null, "Bone appetit.");
				startOfConversation.setEnd();
			}
			
			return new interactBox(startOfConversation, this);
		}
		
		// Interact with object. Should be over-ridden.
		public void interactWith() { 
			interactSequence = makeNormalInteractSequence();
			interactSequence.toggleDisplay();
		}
}
