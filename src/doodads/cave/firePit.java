package doodads.cave;

import java.awt.image.BufferedImage;
import java.util.Random;

import drawing.camera;
import drawing.animation.animation;
import interactions.event;
import interactions.interactBox;
import interactions.textSeries;
import items.weapons.torch;
import modes.mode;
import sounds.sound;
import terrain.chunk;
import terrain.chunkType;
import terrain.generalChunkType;
import units.humanType;
import units.player;
import units.unit;
import units.unitType;
import utilities.stringUtils;
import utilities.time;
import zones.zone;

public class firePit extends chunk {
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default name.
	private static String DEFAULT_CHUNK_NAME = "Fire Pit";
	
	// Sprite stuff.
	private static String DEFAULT_CHUNK_SPRITESHEET = "images/doodads/farmLand/spiderCave/firePit.png";
	
	// Dimensions
	private static int DEFAULT_SPRITE_WIDTH = 44;
	private static int DEFAULT_SPRITE_HEIGHT = 42;

	// Topdown
	private static int DEFAULT_TOPDOWN_ADJUSTMENT_Y = 15;
	private static int DEFAULT_TOPDOWN_WIDTH = 44;
	private static int DEFAULT_TOPDOWN_HEIGHT = 8;

	// Platformer.
	private static int DEFAULT_PLATFORMER_ADJUSTMENT_Y = 15;
	private static int DEFAULT_PLATFORMER_HEIGHT = 8;
	private static int DEFAULT_PLATFORMER_WIDTH = 42;
	
	// The actual type.
	private static generalChunkType typeReference = new generalChunkType(DEFAULT_CHUNK_NAME, DEFAULT_CHUNK_SPRITESHEET, DEFAULT_SPRITE_WIDTH, DEFAULT_SPRITE_HEIGHT);  
	
	// Firepit sound
	private static String firePitSound = "sounds/effects/doodads/firePit.wav";
	
	////////////////
	/// FIELDS /////
	////////////////
	
	// Sequence.
	private interactBox interactSequence;
	
	// Animation
	private animation fireAnimation;
	
	///////////////
	/// METHODS ///
	///////////////
	
	// Create interact sequence
	public interactBox makeNormalInteractSequence() {
		
		// Placeholder for each individual textSeries.
		textSeries s;
					
		// Start of conversation.
		textSeries startOfConversation = null;
			
		// Start of conversation.
		startOfConversation = new textSeries(null, "A fire pit.");
		
		// If player doesn't have torch.
		if(!player.getPlayer().getPlayerInventory().hasItem("Torch")) {
			s = startOfConversation.addChild(null, "You have nothing to light.");
			s = s.addChild(null, "You'd probably just set everything on fire anyway.");
			s.setEnd();
		}
		else {
			s = startOfConversation.addChild("Light torch", "You light the torch on fire.");
			s.setEnd();
			s = startOfConversation.addChild("Walk away", "It's for the best. Fire is dangerous.");
			s.setEnd();
		}
			
		return new interactBox(startOfConversation, stringUtils.toTitleCase(DEFAULT_CHUNK_NAME));
	}
	
	// Interact stuff.
	public void doInteractStuff() {
		if(interactSequence != null 
				&& interactSequence.getTheText().getButtonText() != null
				&& interactSequence.getTheText().getButtonText().equals("Light torch")
				&& interactSequence.getTheText().isEnd()
				&& interactSequence.isDisplayOn()
				&& !((torch)torch.weaponRef).isLit()) {
			((torch)torch.weaponRef).light();
		}
	}
	
	// Update
	@Override
	public void update() {
		doInteractStuff();
		if(fireAnimation != null) fireAnimation.playAnimation();
	}
	
	// Play fire sound
	public void playFireSound() {
		sound s = new sound(firePitSound);
		s.setLoop(true);
		s.setPosition(getIntX(),getIntY(), sound.DEFAULT_SOUND_RADIUS);
		s.start();
	}
	
	// Interact with object. Should be over-ridden.
	public void interactWith() { 
		interactSequence = makeNormalInteractSequence();
		interactSequence.toggleDisplay();
	}
	
	// Constructor
	public firePit(int newX, int newY) {
		super(typeReference, newX, newY);
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
		
		// Play fire sound.
		playFireSound();
		
		// Add animation
		fireAnimation = new animation("fire", typeReference.getChunkTypeSpriteSheet().getAnimation(0), 0, 3, 0.43f);
		
		// Interactable.
		setInteractable(true);
		interactSequence = makeNormalInteractSequence();
		
		// Passable.
		setPassable(false);
	}
	
	// Override chunkImage so we can do an animation
	@Override
	public BufferedImage getChunkImage() {
		return fireAnimation.getCurrentFrame();
	}
}
