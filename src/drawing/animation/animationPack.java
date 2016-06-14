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
		animations = new ArrayList<animation>();
	}
	
	///////////////////////////
	/// GETTERS AND SETTERS ///
	///////////////////////////
	public void addAnimation(animation a) {
		animations.add(a);
	}
	
	// Select random animation
	public animation selectRandomAnimation() {
		int randomNum = utility.RNG.nextInt(animations.size());
		return animations.get(randomNum);
	}
	
	public animation getAnimation(String name) {
		// Search for it in our animations
		for(int i = 0; i < animations.size(); i++) {
			if(name.equals(animations.get(i).getName())) return animations.get(i);
		}
		return null;
	}
	
}