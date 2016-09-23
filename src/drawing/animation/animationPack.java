package drawing.animation;

import java.util.ArrayList;

import utilities.utility;

public class animationPack extends ArrayList<animation> {
	///////////////
	/// METHODS ///
	///////////////
	public animationPack() {
		super();
	}
	
	public animationPack(animationPack animationsToSet) {
		super();
		// Initialize
		for(int i = 0; i < animationsToSet.size(); i++) {
			addAnimation(new animation(animationsToSet.get(i)));
		}
	}

	///////////////////////////
	/// GETTERS AND SETTERS ///
	///////////////////////////
	public void addAnimation(animation a) {
		this.add(a);
	}
	
	// Select random animation
	public animation selectRandomAnimation() {
		int randomNum = utility.RNG.nextInt(this.size());
		return this.get(randomNum);
	}
	
	public animation getAnimation(String name) {
		// Search for it in our animations
		for(int i = 0; i < this.size(); i++) {
			if(name.equals(this.get(i).getName())) return this.get(i);
		}
		return null;
	}


	
}