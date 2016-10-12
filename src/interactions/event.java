package interactions;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;

public class event {
	
	// All events
	public static CopyOnWriteArrayList<event> loadedEvents = new CopyOnWriteArrayList<event>();
	
	// Is the event done?
	private boolean completed;
	
	// Name of the event.
	private String name; // unique
	
	// Constructor
	private event(String newName) {
		
		// Initialize fields.
		name = newName;
		completed = false;
		loadedEvents.add(this);

	}
	
	// Create an event
	public static event createEvent(String newName) {	
		
		// Go through the list and return the event with the same name.
		for(int i = 0; i < loadedEvents.size(); i++) {
			if(loadedEvents.get(i).getName().equals(newName)) {
				return loadedEvents.get(i);
			}
		}
		
		return new event(newName);
	}
	
	public static void printAllEvents() {
		if(loadedEvents != null)
		System.out.println("=====================================================");
		for(int i = 0; i < loadedEvents.size(); i++) {
			System.out.println("Event: " + loadedEvents.get(i).getName() + " is set to: " + loadedEvents.get(i).completed);
		}
		System.out.println("=====================================================");
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