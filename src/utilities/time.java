package utilities;

import java.awt.event.ActionListener;
import java.util.Date;

import javax.swing.Timer;

public class time extends utility {
	
	// Start time in Unix.
	private static Long startTime = 0l;
	
	// The actual timer we will use.
	public static Timer timer; 
	
	// Initiate timer.
	static void initiate() {
		// Set the start time so we can figure
		// out what the current game time is.
		startTime = getCurrentUnixTime();
	}
	
	// Get the system Unix time.
	private static Long getCurrentUnixTime() {
		Date now = new Date();
		Long longTime = new Long(now.getTime());
		return longTime;
	}
	
	// Initiate the timer (for repainting)
	public static void initiateTimer(int fps, ActionListener a) {
		timer = new Timer(fps, a);
		timer.setInitialDelay(190);
		timer.start();
	}
	
	// Get the current game time in milliseconds.
	public static Long getTime() {
		Long currentTime = getCurrentUnixTime();
		return currentTime - startTime;
	}
	
	// Get the current game time in seconds.
	public static Long getTimeInSeconds() {
		Long currentTime = getCurrentUnixTime()/1000;
		return currentTime - startTime/1000;
	}
}