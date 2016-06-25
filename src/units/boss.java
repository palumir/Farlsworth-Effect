package units;

import java.util.ArrayList;

import interactions.event;
import utilities.saveState;

public class boss extends unit {
	
	// All gags
	public static ArrayList<boss> loadedBosses = new ArrayList<boss>();
	
	// Is the boss done?
	private event bossCompleted;
	
	// Name to display on healthbar
	private String displayName;

	// Don't load boss if it's dead (and saved).
	public boss(unitType u, String newDisplayName, int newX, int newY) {
		super(u, newX, newY);
		
		// Set display name.
		setDisplayName(newDisplayName);
		
		// Load the event
		bossCompleted = new event(newDisplayName + "bossCompleted");
		
		if(bossCompleted.isCompleted()) this.destroy();
	}
	
	// Defeat
	public void defeat() {
		setCompleted(true);
		saveState.createSaveState();
	}


	public boolean isCompleted() {
		return bossCompleted.isCompleted();
	}

	public void setCompleted(boolean completed) {
		bossCompleted.setCompleted(completed);
	}

	@Override
	public void updateUnit() {
	}

	@Override
	public void reactToPain() {	
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
}