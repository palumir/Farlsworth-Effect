package sounds;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import main.main;

public class sound {
	
	// Audiostream for the file.
	private AudioInputStream inputStream;
	
	// Constructor
	public sound(String soundFile) {
		try {
			inputStream = AudioSystem.getAudioInputStream(
				this.getClass().getResourceAsStream(soundFile));
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	// Play sound
	public synchronized void playSound() {
		  // The wrapper thread is unnecessary, unless it blocks on the
		  // Clip finishing; see comments.
		      try {
		        Clip clip = AudioSystem.getClip();
		        clip.open(inputStream);
		        clip.start(); 
		      } catch (Exception e) {
		        //e.printStackTrace();
		      }
	}
}