package doodads.sheepFarm;

import java.awt.image.BufferedImage;
import java.util.Random;

import drawing.camera;
import drawing.gameCanvas;
import drawing.animation.animation;
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

public class fireTree extends chunk {
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default name.
	private static String DEFAULT_CHUNK_NAME = "fireTree";
	
	// Sprite stuff.
	private static String DEFAULT_CHUNK_SPRITESHEET = "images/doodads/farmLand/sheepFarm/fireTree1.png";
	
	// Dimensions
	public static int DEFAULT_CHUNK_WIDTH = 125;
	public static int DEFAULT_CHUNK_HEIGHT = 133;
	
	// The actual type.
	private static generalChunkType typeReference = new generalChunkType(DEFAULT_CHUNK_NAME, DEFAULT_CHUNK_SPRITESHEET, DEFAULT_CHUNK_WIDTH, DEFAULT_CHUNK_HEIGHT); 
	
	////////////////
	/// FIELDS /////
	////////////////
	
	// Sequence
	private interactBox interactSequence;
	
	// Firepit sound
	private static String firePitSound = "sounds/effects/doodads/firePit.wav";
	private static long lastFireSound = 0;
	private static float playEvery = 28.6f;
	
	///////////////
	/// METHODS ///
	///////////////
	
	// Create interact sequence
	public interactBox makeNormalInteractSequence() {
		
		// Placeholder for each individual textSeries.
		textSeries s;
					
		// Start of conversation.
		textSeries startOfConversation = null;
		startOfConversation = new textSeries(null, "It's an ordinary tree.");
		s = startOfConversation.addChild(null, "Except it's also on fire.");
		s.setEnd();
		
		return new interactBox(startOfConversation, stringUtils.toTitleCase(DEFAULT_CHUNK_NAME));
	}
	
	// Interact stuff.
	public void doInteractStuff() {
	}
	
	// Interact with object. Should be over-ridden.
	public void interactWith() { 
		interactSequence = makeNormalInteractSequence();
		interactSequence.toggleDisplay();
	}
	
	// Animation
	private animation fireAnimation;
	
	///////////////
	/// METHODS ///
	///////////////
	// Constructor
	public fireTree(int newX, int newY, int i) {
		super(typeReference, newX, newY, i, 0);
		if(mode.getCurrentMode().equals("topDown")) {
			setHitBoxAdjustmentY(58);
			setWidth(30);
			setHeight(13);
		}
		else {
			setHitBoxAdjustmentY(0);
			setHeight(DEFAULT_CHUNK_HEIGHT);
			setWidth(DEFAULT_CHUNK_WIDTH);
		}
		setInteractable(true);
		setPassable(false);
		
		// Add animation
		fireAnimation = new animation("fire", typeReference.getChunkTypeSpriteSheet().getAnimation(0), 0, 2, 0.3f);
	}
	
	// Update
	@Override
	public void update() {
		doInteractStuff();
		if(fireAnimation != null) fireAnimation.playAnimation();
		playFireSound();
	}
	
	// Play fire sound
	public void playFireSound() {
		if(lastFireSound == 0) {
			lastFireSound = time.getTime();
			sound s = new sound(firePitSound);
			s.start();
		}
		else if(time.getTime() - lastFireSound > playEvery*1000) {
			lastFireSound = time.getTime();
			sound s = new sound(firePitSound);
			s.start();
		}
	}
	
	// Override chunkImage so we can do an animation
	@Override
	public BufferedImage getChunkImage() {
		return fireAnimation.getCurrentFrame();
	}
}
