package doodads.tomb;

import java.awt.image.BufferedImage;
import java.util.Random;

import drawing.camera;
import drawing.animation.animation;
import modes.mode;
import sounds.sound;
import terrain.chunk;
import terrain.chunkType;
import terrain.generalChunkType;
import units.humanType;
import units.unit;
import units.unitType;
import utilities.time;
import zones.zone;

public class wallTorch extends lightSource {
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default name.
	private static String DEFAULT_CHUNK_NAME = "wallTorch";
	
	// Sprite stuff.
	private static String DEFAULT_CHUNK_SPRITESHEET = "images/doodads/farmLand/tomb/"+ DEFAULT_CHUNK_NAME + ".png";
	
	// Dimensions
	private static int DEFAULT_CHUNK_WIDTH = 5;
	private static int DEFAULT_CHUNK_HEIGHT = 30;
	
	// The actual type.
	private static generalChunkType typeReference = new generalChunkType(DEFAULT_CHUNK_NAME, DEFAULT_CHUNK_SPRITESHEET, DEFAULT_CHUNK_WIDTH, DEFAULT_CHUNK_HEIGHT); 
	
	///////////////
	/// FIELDS ////
	///////////////
	private animation fireAnimation;
	
	///////////////
	/// METHODS ///
	///////////////
	// Constructor
	public wallTorch(int newX, int newY) {
		super(typeReference, newX, newY);
		setPassable(true);
		if(mode.getCurrentMode().equals("topDown")) {
			setHitBoxAdjustmentY(0);
			setHeight(DEFAULT_CHUNK_HEIGHT);
			setWidth(DEFAULT_CHUNK_WIDTH);
		}
		else {
			setHitBoxAdjustmentY(0);
			setHeight(DEFAULT_CHUNK_HEIGHT);
			setWidth(DEFAULT_CHUNK_WIDTH);
		}
		
		// Add animation
		fireAnimation = new animation("fire", typeReference.getChunkTypeSpriteSheet().getAnimation(0), 0, 4, 0.43f);
	}
	
	// Update
	@Override
	public void update() {
		if(fireAnimation != null) fireAnimation.playAnimation();
	}
	
	// Override chunkImage so we can do an animation
	@Override
	public BufferedImage getChunkImage() {
		return fireAnimation.getCurrentFrame();
	}
}
