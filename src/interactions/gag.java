package interactions;

import java.util.ArrayList;

import units.player;
import utilities.saveState;

public class gag {
	
	// All gags
	public static ArrayList<gag> loadedGags = new ArrayList<gag>();
	
	// Is the gag done?
	private boolean completed = false;
	
	// Name of the gag.
	private String name; // unique
	
	// Constructor
	public gag(String newName) {
		
		// Initialize fields.
		name = newName;
		
		// If the gag is loaded, set it's data.
		int i = 0;
		if(loadedGags != null) {
			
			// Go through the list and return the gag with the same name.
			while(i < loadedGags.size()) {
				if(loadedGags.get(i).getName().equals(newName)) {
					setCompleted(loadedGags.get(i).isCompleted());
					loadedGags.remove(i);
				}
				else {
					i++;
				}
			}
		}
		
		loadedGags.add(this);
	}
	
	// Load quest data.
	public static void loadGagData() {
		
		// Load the savestate
		saveState s = player.getCurrentPlayer().playerSaveState;
		
		// Populate allGags with quests from the saveState.
		if(s != null) {
			loadedGags = s.getAllGags();
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}
}