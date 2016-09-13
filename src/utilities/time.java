package utilities;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.Timer;

import sounds.sound;

public class time extends utility {
	
	// Start time in Unix.
	private static Long startTime = 0l;
	
	// The actual timer we will use.
	public static Timer gameTimer; 
	public static Timer drawTimer;
	
	// Paused?
	public static boolean paused = false;
	
	// Game time
	public static long gameTime = 0;
	
	// Slow time
	public static float timeSpeed = 1f;
	
	// Default tick rate.
	public static float DEFAULT_TICK_RATE = 12;
	
	// Last tick
	private static long lastTick = 0;
	
	// Sound
	private static String heartbeat = "sounds/effects/quicktime/heartbeat.wav";
	
	// Initiate timer.
	public static void initiate() {
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
	
	// All sounds
	static ArrayList<sound> soundsToPlay;
	
	// Last heartbeat
	static long lastHeartBeat = 0;
	static float playEvery = 3.6f;
	
	// Does what is says.
	public static void potentiallyPlayHeartBeat() {
		if(timeSpeed < 1 && (lastHeartBeat == 0 || getCurrentUnixTime() - lastHeartBeat > playEvery*1000)) {
			lastHeartBeat = getCurrentUnixTime();
			sound s = new sound(heartbeat);
			s.start();
		}
	}
	
	// Slow time
	public static void setTimeSpeed(float f) {
		if(f < 1) {
			soundsToPlay = sound.getAllPlaying();
			sound.stopAllSounds();
		}
		if(f == 1) {
			sound.stopAllSounds();
			if(soundsToPlay!=null) {
				for(int i = 0; i < soundsToPlay.size(); i++) {
					sound currSound = soundsToPlay.get(i);
					sound s = new sound(currSound);
					s.start();
				}
			}
		}
		gameTimer.setDelay((int) (DEFAULT_TICK_RATE/f));
		timeSpeed = f;
	}
	
	// Initiate the timer (for repainting)
	public static void initiateDrawTimer(int fps, ActionListener a) {
		drawTimer = new Timer(fps, a);
		drawTimer.setInitialDelay(190);
		drawTimer.start();
	}
	
	// Initiate the timer (for repainting)
	public static void initiateGameTimer(ActionListener a) {
		gameTimer = new Timer((int) DEFAULT_TICK_RATE, a);
		gameTimer.setInitialDelay(190);
		gameTimer.start();
	}
	
	// Pause time
	public static void pause() {
		paused = true;
	}
	
	// Unpause time
	public static void unpause() {
		paused = false;
	}
	
	// Tick timer.
	public static void tickTimer() {
		if(lastTick==0) lastTick = time.getCurrentUnixTime();
		if(!paused) gameTime += (time.getCurrentUnixTime() - lastTick)*timeSpeed;
		lastTick = time.getCurrentUnixTime();
		potentiallyPlayHeartBeat();
	}
	
	// Get the current game time in milliseconds.
	public static Long getTime() {
		return gameTime;
	}
	
	// Get the current game time in seconds.
	public static Long getTimeInSeconds() {
		return getTime()*1000;
	}
}