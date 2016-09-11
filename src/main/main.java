package main;

import UI.tooltipString;
import doodads.general.well;
import drawing.gameCanvas;
import sounds.music;
import units.player;
import units.developer.developer;
import utilities.levelSave;
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
		
		// Create the player at the last bottle?
		if(s!= null && s.equals("respawnAtWell")) {
			player p = player.loadPlayer(null,null,0,0,"Up");
			p.lastSaveBottle = null;
			p.setDoubleX(p.lastWell.getX());
			p.setDoubleY(p.lastWell.getY());
			well.refreshPlayer("respawnAtWell");
		}
		else {
			player p = player.loadPlayer(null,null,0,0,"Up");
		}
		
		// Saved game?
		if(s!=null) {
			
			// Restart due to saving.
			if(s.equals("Save")) {
				tooltipString t = new tooltipString(saveState.DEFAULT_GAME_SAVED_TEXT);
			}
		
			if(s.equals("respawnAtSaveBottle") || s.equals("respawnAtWell")) {
				
				// Load zone if we're a developer
				if(player.isDeveloper() && developer.levelName != null) {
					levelSave.loadSaveState(developer.levelName);
				}
			}
		}
	}
}