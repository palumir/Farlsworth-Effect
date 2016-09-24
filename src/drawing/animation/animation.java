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
	private double timeToComplete; // in seconds
	private long startTime; // in milliseconds
	
	// Repeats?
	private boolean repeats = false;
	
	///////////////
	/// METHODS ///
	///////////////
	
	// Constructor.
	public animation(String newName, ArrayList<BufferedImage> newSprites, int newStartFrame, int newEndFrame, float newTimeToComplete) {
		
		// Set the fields.
		setTimeToComplete(newTimeToComplete);
		setStartFrame(newStartFrame);
		endFrame = newEndFrame;
		setName(newName);
		setSprites(newSprites);
	}
	
	// Copy constructor
	public animation(animation animation) {
		
		// Set the fields.
		setTimeToComplete(animation.getTimeToComplete());
		this.setStartFrame(animation.getStartFrame());
		repeats = animation.repeats;
		endFrame = animation.endFrame;
		setName(animation.name);
		setSprites(animation.sprites);
	}

	// Start animation
	public void startAnimation() {
		setStartTime(0);
	}
	
	// Play animation.
	public void playAnimation() { 
		
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
		
		// Get the correct current frame.
		if(getStartTime() == 0) setStartTime(time.getTime());
		double howMuchTimeHasElapsed = (double) (time.getTime() - getStartTime());
		double howMuchTimePerFrame = (double) (getTimeToComplete()*1000/((endFrame + 1) - getStartFrame()));
		double correctFrame = getStartFrame() + ((double)howMuchTimeHasElapsed)/((double)howMuchTimePerFrame);
		if(correctFrame >= endFrame+1) {

			// It's over but hasn't been destroyed yet by worker thread.
			if(time.getTime() - getStartTime() >= timeToComplete*1000 && !repeats) correctFrame = endFrame;
			
			// Otherwise, just return the correct frame.
			else {
				setStartTime(time.getTime());
				correctFrame = getStartFrame();
			}
		}
		
		return (int)correctFrame;
	}
	
	public void setRepeats(boolean b) {
		repeats = b;
	}
	
	public boolean isRepeats() {
		return repeats;
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

	public double getTimeToComplete() {
		return timeToComplete;
	}

	public void setTimeToComplete(double timeToComplete) {
		this.timeToComplete = timeToComplete;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
}