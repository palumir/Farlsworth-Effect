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
			
		// Advance animation if the correct amount of time has elapsed.
		if(currentFrameTime == 0) currentFrameTime = time.getTime();
		else if(time.getTime() - currentFrameTime >= timeToComplete*1000/(endFrame - startFrame + 1)) {
			
			// Advance the timer and the sprite.
			currentFrameTime = time.getTime();
		
			// Have we reached the end?
			if(getCurrentSprite() + 1 > endFrame) {
					setCurrentSprite(startFrame);
			}
			else {
				setCurrentSprite(getCurrentSprite() + 1);
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

	public int getCurrentSprite() {
		return currentSprite;
	}

	public void setCurrentSprite(int currentSprite) {
		this.currentSprite = currentSprite;
	}
}