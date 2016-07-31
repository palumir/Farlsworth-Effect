package doodads.tomb;

import java.awt.image.BufferedImage;

import doodads.general.lightSource;
import drawing.animation.animation;
import modes.mode;
import terrain.generalChunkType;

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
		
		// Z axis
		setZ(-1);
		
		// Set radius
		setLightRadius(75);
		
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
