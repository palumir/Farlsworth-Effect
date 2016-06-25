package interactions;

import java.util.ArrayList;

import units.player;
import utilities.saveState;

public class event {
	
	// All events
	public static ArrayList<event> loadedEvents = new ArrayList<event>();
	
	// Is the event done?
	private boolean completed = false;
	
	// Name of the event.
	private String name; // unique
	
	// Constructor
	public event(String newName) {
		
		// Initialize fields.
		name = newName;
		
		// If the gag is loaded, set it's data.
		int i = 0;
		if(loadedEvents != null) {
			
			// Go through the list and return the gag with the same name.
			while(i < loadedEvents.size()) {
				if(loadedEvents.get(i).getName().equals(newName)) {
					setCompleted(loadedEvents.get(i).isCompleted());
					loadedEvents.remove(i);
				}
				else {
					i++;
				}
			}
		}
		
		loadedEvents.add(this);
	}
	
	// Load gag data.
	public static void loadEventData() {
		
		// Load the savestate
		saveState s = player.getCurrentPlayer().playerSaveState;
		
		// Populate allGags with quests from the saveState.
		if(s != null) {
			loadedEvents = s.getAllEvents();
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