package sounds;
import java.io.File; 
import java.io.IOException; 
import javax.sound.sampled.AudioFormat; 
import javax.sound.sampled.AudioInputStream; 
import javax.sound.sampled.AudioSystem; 
import javax.sound.sampled.DataLine; 
import javax.sound.sampled.FloatControl; 
import javax.sound.sampled.LineUnavailableException; 
import javax.sound.sampled.SourceDataLine; 
import javax.sound.sampled.UnsupportedAudioFileException;

import drawing.gameCanvas;
import units.player; 

public class sound extends Thread { 

    private String filename;

    private Position curPosition;

    private final int EXTERNAL_BUFFER_SIZE = 524288/100; // 128Kb DEFAULT
    
    // Where to play and at what radius.
    private int radius = 0;
    private int x = 0;
    private int y = 0;
    
    // Volume
    public static float DEFAULT_SOUND_VOLUME = 1f;
    private float soundVolume = DEFAULT_SOUND_VOLUME;
    private float volume = 1f;
    
    // Default sound radius
    public static int DEFAULT_SOUND_RADIUS = 1800;

    enum Position { 
        LEFT, RIGHT, NORMAL
    };

    public sound(String wavfile) { 
        filename = wavfile;
        curPosition = Position.NORMAL;
    } 

    public sound(String wavfile, Position p) { 
        filename = wavfile;
        curPosition = p;
    } 

    public void run() { 

        File soundFile = new File(filename);
        if (!soundFile.exists()) { 
            System.err.println("Wave file not found: " + filename);
            return;
        } 

        AudioInputStream audioInputStream = null;
        try { 
            audioInputStream = AudioSystem.getAudioInputStream(soundFile);
        } catch (UnsupportedAudioFileException e1) { 
            e1.printStackTrace();
            return;
        } catch (IOException e1) { 
            e1.printStackTrace();
            return;
        } 

        AudioFormat format = audioInputStream.getFormat();
        SourceDataLine auline = null;
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);

        try { 
            auline = (SourceDataLine) AudioSystem.getLine(info);
            auline.open(format);
        } catch (LineUnavailableException e) { 
            e.printStackTrace();
            return;
        } catch (Exception e) { 
            e.printStackTrace();
            return;
        } 

        if (auline.isControlSupported(FloatControl.Type.PAN)) { 
            FloatControl pan = (FloatControl) auline
                    .getControl(FloatControl.Type.PAN);
            if (curPosition == Position.RIGHT) 
                pan.setValue(1.0f);
            else if (curPosition == Position.LEFT) 
                pan.setValue(-1.0f);
        } 

        auline.start();
        int nBytesRead = 0;
        byte[] abData = new byte[EXTERNAL_BUFFER_SIZE];

        try { 
            while (nBytesRead != -1) { 
            	
    		    // Get player position
    		    int playerX = player.getCurrentPlayer().getIntX();
    		    int playerY = player.getCurrentPlayer().getIntY();
    		   
    		    // Calculate how close we are.
    		    float howClose = (float) Math.sqrt((playerX - x)*(playerX - x) + (playerY - y)*(playerY - y));
    		    
    		    // How close are you to the sound?
    		    float howClosePercentage;
            	if(/*howClose > gameCanvas.getDefaultWidth()/3 &&*/ radius > 0) {
            		howClosePercentage = ((float)radius - howClose)/(float)radius;
            	}
            	else {
            		howClosePercentage = 1f;
            	}
    		   
    		    // Adjust volume based on radius.
    		    FloatControl control = (FloatControl) auline.getControl(FloatControl.Type.MASTER_GAIN);
    	        float max = control.getMaximum();
    	        float min = control.getMinimum(); // negative values all seem to be zero?
    	        float range = max - min;
    	        if(howClosePercentage>0) {
    	        	control.setValue(min + (range * howClosePercentage * volume * soundVolume));
    	        }
    	        else { 
    	        	control.setValue(min);
    	        }
                nBytesRead = audioInputStream.read(abData, 0, abData.length);
                if (nBytesRead >= 0) 
                    auline.write(abData, 0, nBytesRead);
            } 
        } catch (IOException e) { 
            e.printStackTrace();
            return;
        } finally { 
            auline.drain();
            auline.close();
        } 

    }
    
    public void setVolume(float v) {
    	volume = v;
    }
    
    public void setPosition(int newX, int newY, int newRadius) {
    	radius = newRadius;
    	x = newX;
    	y = newY;
    }
}