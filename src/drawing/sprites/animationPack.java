package drawing.sprites;

import java.util.ArrayList;

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
	
	public animation getAnimation(String name) {
		// Search for it in our animations
		for(int i = 0; i < animations.size(); i++) {
			if(name.equals(animations.get(i).getName())) return animations.get(i);
		}
		return null;
	}
	
}