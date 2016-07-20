package drawing.animation;

import java.util.ArrayList;

import utilities.utility;

public class animationPack {
	////////////////
	//// FIELDS ////
	////////////////
	
	// The animations and their names.
	private ArrayList<animation> animations;
	
	///////////////
	/// METHODS ///
	///////////////
	public animationPack() {
		
		// Initialize
		setAnimations(new ArrayList<animation>());
	}
	
	public animationPack(animationPack animationsToSet) {
		// Initialize
		setAnimations(new ArrayList<animation>());
		for(int i = 0; i < animationsToSet.getAnimations().size(); i++) {
			addAnimation(new animation(animationsToSet.getAnimations().get(i)));
		}
	}

	///////////////////////////
	/// GETTERS AND SETTERS ///
	///////////////////////////
	public void addAnimation(animation a) {
		getAnimations().add(a);
	}
	
	// Select random animation
	public animation selectRandomAnimation() {
		int randomNum = utility.RNG.nextInt(getAnimations().size());
		return getAnimations().get(randomNum);
	}
	
	public animation getAnimation(String name) {
		// Search for it in our animations
		for(int i = 0; i < getAnimations().size(); i++) {
			if(name.equals(getAnimations().get(i).getName())) return getAnimations().get(i);
		}
		return null;
	}

	public boolean contains(animation trailRight) {
		// TODO Auto-generated method stub
		return false;
	}

	public ArrayList<animation> getAnimations() {
		return animations;
	}

	public void setAnimations(ArrayList<animation> animations) {
		this.animations = animations;
	}
	
}