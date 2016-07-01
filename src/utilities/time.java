package utilities;

import java.awt.event.ActionListener;
import java.util.Date;

import javax.swing.Timer;

public class time extends utility {
	
	// Start time in Unix.
	private static Long startTime = 0l;
	
	// The actual timer we will use.
	public static Timer gameTimer; 
	public static Timer drawTimer;
	
	// Initiate timer.
	static void initiate() {
		// Set the start time so we can figure
		// out what the current game time is.
		if(startTime == 0l) startTime = getCurrentUnixTime();
	}
	
	// Get the system Unix time.
	private static Long getCurrentUnixTime() {
		Date now = new Date();
		Long longTime = new Long(now.getTime());
		return longTime;
	}
	
	// Initiate the timer (for repainting)
	public static void initiateDrawTimer(int fps, ActionListener a) {
		drawTimer = new Timer(fps, a);
		drawTimer.setInitialDelay(190);
		drawTimer.start();
	}
	
	// Initiate the timer (for repainting)
	public static void initiateGameTimer(int fps, ActionListener a) {
		gameTimer = new Timer(fps, a);
		gameTimer.setInitialDelay(190);
		gameTimer.start();
	}
	
	// Get the current game time in milliseconds.
	public static Long getTime() {
		Long currentTime = getCurrentUnixTime();
		if(startTime == 0l) startTime = getCurrentUnixTime(); 
		return currentTime - startTime;
	}
	
	// Get the current game time in seconds.
	public static Long getTimeInSeconds() {
		return getTime()*1000;
	}
}