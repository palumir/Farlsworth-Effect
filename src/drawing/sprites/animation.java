package drawing.sprites;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import utilities.time;

public class animation {
	
	////////////////
	//// FIELDS ////
	////////////////
	
	// Animation name
	private String name;
	
	// The actual sprites in the animation.
	private ArrayList<BufferedImage> sprites;
	private int startFrame;
	private int endFrame;
	
	// The time it takes to complete the animation.
	private float timeToComplete; // in seconds
	private long currentFrameTime = 0; // in milliseconds
	
	// Where are we in the animation?
	private int currentSprite;
	private boolean animationComplete = false;
	private boolean repeatAnimation = false;
	
	///////////////
	/// METHODS ///
	///////////////
	
	// Constructor.
	public animation(String newName, ArrayList<BufferedImage> newSprites, int newStartFrame, int newEndFrame, float newTimeToComplete) {
		
		// Set the fields.
		currentSprite = newStartFrame;
		timeToComplete = newTimeToComplete;
		startFrame = newStartFrame;
		endFrame = newEndFrame;
		setName(newName);
		sprites = newSprites;
	}
	
	// Play animation.
	public void playAnimation() {
		
		// Only play if the animation isn't complete yet.
		if(!animationComplete) {
			
			// Advance animation if the correct amount of time has elapsed.
			if(currentFrameTime == 0) currentFrameTime = time.getTime();
			else if(time.getTime() - currentFrameTime > timeToComplete*1000/(endFrame - startFrame + 1)) {
				
				// Advance the timer and the sprite.
				currentFrameTime = time.getTime();
				setCurrentSprite(getCurrentSprite() + 1);
			
				// Have we reached the end?
				if(getCurrentSprite() >= sprites.size() || getCurrentSprite() >= endFrame) {
				
					// Do we want to repeat?
					if(doesRepeat()) {
						setCurrentSprite(startFrame);
					}
				
					// No? End it.
					else {
						animationComplete = true;
					}
				}
			}
		}
	}
	
	///////////////////////////
	/// GETTERS AND SETTERS ///
	///////////////////////////
	public BufferedImage getCurrentFrame() {
		return sprites.get(getCurrentSprite());
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean doesRepeat() {
		return repeatAnimation;
	}

	public void repeat(boolean repeatAnimation) {
		this.repeatAnimation = repeatAnimation;
	}

	public int getCurrentSprite() {
		return currentSprite;
	}

	public void setCurrentSprite(int currentSprite) {
		this.currentSprite = currentSprite;
	}
	
}