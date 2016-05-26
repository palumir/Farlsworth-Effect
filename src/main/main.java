package main;

import drawing.gameCanvas;
import drawing.shape;
import modes.mode;
import modes.platformer;
import modes.topDown;
import units.player;
import units.unit;
import units.unitType;
import utilities.utility;

// The class that initiates the program.
public class main {
	
	// The actual function that initiates the program.
	public static void main(String[] args) {
		
		// Initiate the utilities we may need.
		utility.initiateAll();
		
		// Create the game canvas.
		gameCanvas gameCanvas = new gameCanvas();
		
		// Do stuff for testing.
		player thePlayer = new player(10, 10);
		unit bigUnit1 = new unit(unitType.bigUnit, 50, 50);
		unit bigUnit2 = new unit(unitType.bigUnit, 70, 70);
		unit smallUnit1 = new unit(unitType.smallUnit, -10, -10);
		topDown.setMode();
	}
}