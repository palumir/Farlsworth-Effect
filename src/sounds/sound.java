package sounds;

import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

import units.player;

public class sound {
	
	// Audiostream for the file.
	private AudioInputStream audioIn;
	private Clip clip;
	
	// Constructor
	public sound(String soundFile) {
		try {
			File f = new File("./" + soundFile);
			audioIn = AudioSystem.getAudioInputStream(f.toURI().toURL());
			clip = AudioSystem.getClip();
			clip.open(audioIn);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	// Play sound anywhere
	public void playSound(float v) {
		  // The wrapper thread is unnecessary, unless it blocks on the
		  // Clip finishing; see comments.
		   try {
			   // Adjust volume based on radius.
			   FloatControl control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
		       float max = control.getMaximum();
		       float min = control.getMinimum(); // negative values all seem to be zero?
		       float range = max - min;
		       control.setValue(min + (range*v));
				   
			   // Play the clip.
			   clip.setFramePosition(0);
			   clip.stop();
			   clip.start();
		   }
		   catch (Exception e) {
		   }
	}
	
	// Play sound at a volume
	public void playSound(int x, int y, int radius, float v) {
		  // The wrapper thread is unnecessary, unless it blocks on the
		  // Clip finishing; see comments.
		   try {
			   if(player.getCurrentPlayer()!=null) {
				   // Get player position
				   int playerX = player.getCurrentPlayer().getX();
				   int playerY = player.getCurrentPlayer().getY();
				   
				   // Calculate how close we are.
				   float howClose = (float) Math.sqrt((playerX - x)*(playerX - x) + (playerY - y)*(playerY - y));
				   float howClosePercentage = (radius - howClose)/radius;
				   
				   // Adjust volume based on radius.
				   FloatControl control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
			       float max = control.getMaximum();
			       float min = control.getMinimum(); // negative values all seem to be zero?
			       float range = max - min;
			       if(howClosePercentage>0) {
			    	   control.setValue(min + (range * howClosePercentage*v));
					   
					   // Play the clip.
					   clip.setFramePosition(0);
					   clip.stop();
					   clip.start();
			       }
			   }
		   } 
		   catch (Exception e) {
		   }
	}
}