package main;

import drawing.gameCanvas;
import drawing.userInterface.tooltipString;
import units.player;
import utilities.saveState;

// The class that initiates the program.
public class main {
	
	// The actual function that initiates the program.
	public static void main(String[] args) {
		
		// Create the game canvas.
		gameCanvas gameCanvas = new gameCanvas();
		
		// Start the game for the first time.
		restartGame(null);
		
	}
	
	// Restart game
	public static void restartGame(String s) {
		
		// Create the player
		//player.setDeveloper(true);
		player p = player.loadPlayer(null,null,0,0,"Up");
		
		// Saved game?
		if(s!=null) {
			
			// Restart due to saving.
			if(s.equals("Save")) {
				tooltipString t = new tooltipString(saveState.DEFAULT_GAME_SAVED_TEXT);
			}
			
			// Restart due to death.
			if(s.equals("Death")) {
			}
		}
	}
}