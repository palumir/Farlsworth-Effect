package main;

import drawing.gameCanvas;
import modes.mode;
import modes.platformer;
import modes.topDown;
import units.player;
import units.unit;
import units.unitType;
import utilities.utility;
import zones.*;

// The class that initiates the program.
public class main {
	
	// The actual function that initiates the program.
	public static void main(String[] args) {
		
		// Create the game canvas.
		gameCanvas gameCanvas = new gameCanvas();
		
		// Start the game for the first time.
		restartGame();
		
	}
	
	// Restart game
	public static void restartGame() {
		// Initiate.
		utility.initiateAll();
		
		// Create the player.
		player p = player.loadPlayer(null,0,0,"Up");
	}
}