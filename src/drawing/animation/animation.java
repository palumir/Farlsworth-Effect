package drawing.animation;

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
	private long startTime; // in milliseconds
	
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
		setStartFrame(newStartFrame);
		endFrame = newEndFrame;
		setName(newName);
		setSprites(newSprites);
	}
	
	// Start animation
	public void startAnimation() {
		startTime = 0;
	}
	
	// Play animation.
	public void playAnimation() { 
			
		// Get the correct current frame.
		if(startTime == 0) startTime = time.getTime();
		int howMuchTimeHasElapsed = (int) (time.getTime() - startTime);
		int howMuchTimePerFrame = (int) (timeToComplete*1000/((endFrame + 1) - getStartFrame()));
		int correctFrame = getStartFrame() + howMuchTimeHasElapsed/howMuchTimePerFrame;
		if(correctFrame>endFrame) {
			startTime = time.getTime();
			correctFrame = getStartFrame();
		}
		
		// Set animation
		setCurrentSprite(correctFrame);
	}
	
	///////////////////////////
	/// GETTERS AND SETTERS ///
	///////////////////////////
	public BufferedImage getCurrentFrame() {
		return getSprites().get(getCurrentSprite());
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

	public int getStartFrame() {
		return startFrame;
	}

	public void setStartFrame(int startFrame) {
		this.startFrame = startFrame;
	}

	public ArrayList<BufferedImage> getSprites() {
		return sprites;
	}

	public void setSprites(ArrayList<BufferedImage> sprites) {
		this.sprites = sprites;
	}
}