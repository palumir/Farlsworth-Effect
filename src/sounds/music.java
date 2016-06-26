package sounds;

import java.io.File;
import java.util.ArrayList;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

import units.player;

public class music {
	
	// Defaults.
	private static float DEFAULT_MUSIC_VOLUME = 0.8f;
	
	// List of all music.
	private static ArrayList<music> allMusic = new ArrayList<music>();
	
	// Last song and volume
	private static music lastMusic;
	private static float lastVolume;
	
	// Current music.
	private static music currMusic;
	private static float currVolume = DEFAULT_MUSIC_VOLUME;

	// Music constructor.
	public music(String soundFile) {
		try {
			File f = new File("./" + soundFile);
			audioIn = AudioSystem.getAudioInputStream(f.toURI().toURL());
			setClip(AudioSystem.getClip());
			getClip().open(audioIn);
		}
		catch(Exception e) {
			
		}
		allMusic.add(this);
	}
	
	// Init.
	public static void endAll() {
		if(allMusic != null) {
			for(int i = 0; i < allMusic.size(); i++) {
				allMusic.get(i).getClip().stop();
				allMusic.get(i).getClip().setMicrosecondPosition(0l);
			}
		}
	}
	
	// Play last song played before current.
	public static void playLast() {
		if(lastMusic != null) lastMusic.loopMusic();
	}

	// Loop a song.
	public void loopMusic() {
		// Set last music.
		lastMusic = currMusic;
		lastVolume = currVolume;
		
		// Stop all music.
		music.endAll();
		
		// Loop this song.
		currMusic = this;
		this.setVolume(currVolume);
		this.getClip().setFramePosition(0);
		this.getClip().loop(-1);
		
	}
	
	// Audiostream for the file.
	private AudioInputStream audioIn;
	private Clip clip;
	
	// Set volume
	public void setVolume(float newVol) {
		 FloatControl control = (FloatControl) getClip().getControl(FloatControl.Type.MASTER_GAIN);
		 currVolume = newVol;
	     float max = control.getMaximum();
	     float min = control.getMinimum(); // negative values all seem to be zero?
	     float range = max - min;
	     control.setValue(min + (range *newVol));
	}
	
	// Play sound at a volume
	public void playSound() {
		  // The wrapper thread is unnecessary, unless it blocks on the
		  // Clip finishing; see comments.
		   try {
			   getClip().stop();
	    	   getClip().setFramePosition(0);
			   getClip().start();
		   } 
		   catch (Exception e) {
		   }
	}

	public Clip getClip() {
		return clip;
	}

	public void setClip(Clip clip) {
		this.clip = clip;
	}

}