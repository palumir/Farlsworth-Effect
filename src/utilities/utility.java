package utilities;

import drawing.drawnObject;
import units.player;
import units.unit;
import units.unitType;

// A general class for all utilities so we may
// call this parent class to set-up and run
// all the utilities at once.
public abstract class utility {
	
	// Update the actual game.
	// No where else to put this.
	public static void updateGame() {
		for(int i = 0; i < drawnObject.objects.size(); i++) {
			drawnObject d = drawnObject.objects.get(i);
			d.update();
		}
	}
	
	// Initiate the utility. Does nothing
	// if the utility does not need to be
	// set-up.
	static void initiate() {
		// Base-line utilities do nothing.
	}
	
	// Set-up all the utilities.
	public static void initiateAll() {
		time.initiate();
		unitType.initiate();
		player.initiate();
	}
}