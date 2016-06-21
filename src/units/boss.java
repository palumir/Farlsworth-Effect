package units;

import java.util.ArrayList;

import utilities.saveState;

public class boss extends unit {
	
	// All gags
	public static ArrayList<boss> loadedBosses = new ArrayList<boss>();
	
	// Is the boss done?
	private boolean completed = false;

	// Don't load boss if it's dead (and saved).
	public boss(unitType u, int newX, int newY) {
		super(u, newX, newY);
		
		// If the boss is loaded, set it's data.
		int i = 0;
		if(loadedBosses != null) {
			// Go through the list and return the gag with the same name.
			while(i < loadedBosses.size()) {
				if(loadedBosses.get(i).getTypeOfUnit().getName().equals(u.getName())) {
					completed = loadedBosses.get(i).completed;
					loadedBosses.remove(i);
				}
				else {
					i++;
				}
			}
		}
		loadedBosses.add(this);
		
		if(isCompleted()) this.destroy();
	}
	
	// Load boss data.
	public static void loadBossData() {
		
		// Load the savestate
		saveState s = player.getCurrentPlayer().playerSaveState;
		
		// Populate allGags with quests from the saveState.
		if(s != null) {
			loadedBosses = s.getAllBosses();
		}
	}


	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

	@Override
	public void updateUnit() {
	}

	@Override
	public void reactToPain() {	
	}
	
}