package sounds;

import java.io.File;
import java.util.ArrayList;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

import units.player;

public class music extends sound {
	
	// Defaults.
	private static float DEFAULT_MUSIC_VOLUME = 0.9f;
	
	// List of all music.
	private static ArrayList<music> allMusic = new ArrayList<music>();
	
	// Last song and volume
	private static music lastMusic;
	
	// Current music.
	public static music currMusic;
	
	// Restart on death?
	public boolean stopOnDeath = false;

	// Music constructor.
	public music(String soundFile) {
		super(soundFile);
		musicConstruct();
	}
	
	// Copy constructor.
	public music(music music) {
		super(music);
		musicConstruct();
	}
	
	// Called in both constructors
	public void musicConstruct() {
		
		// Set volume.
		this.setVolume(DEFAULT_MUSIC_VOLUME);
		
		// Last music.
		lastMusic = currMusic;
		
		// Replace current music.
		currMusic = this;
		
		// Loop all music.
		this.loop = true;
		
		// Start music.
		this.start();
		
		// Add to all music.
		allMusic.add(this);
	}
	
	// Factory constructor
	public static music startMusic(String s) {
		if(currMusic != null && currMusic.filename.equals(s)) {
			// Don't play the same music twice, dumby!
			return null;
		}
		else {
			if(currMusic!=null) {
				System.out.println(currMusic.filename + " and " + s);
			}
			// Play the music
			music m = new music(s);
			return m;
		}
	}
	
	// Init.
	public static void endAll() {
		if(allMusic != null) {
			for(int i = 0; i < allMusic.size(); i++) {
				allMusic.get(i).stopRequested = true;
			}
			currMusic = null;
		}
	}
	
	// Player died
	public static void playerDied() {
		if(currMusic != null && currMusic.stopOnDeath) {
			currMusic.stopRequested = true;
			currMusic = null;
		}
	}
	
	// Play last song played before current.
	public static void playLast() {
		if(lastMusic != null) {
			music s = new music(lastMusic);
		}
	}
}