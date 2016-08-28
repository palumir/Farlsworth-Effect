package sounds;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import units.player;
import utilities.time; 

public class sound extends Thread { 
	
	private static ArrayList<sound> allSounds;

    private String fileName;

    private Position curPosition;

    private final int EXTERNAL_BUFFER_SIZE = 1024; // 128Kb DEFAULT
    
    // Where to play and at what radius.
    private int radius = 0; 
    private int x = 0;
    private int y = 0;
    
    // Volume
    public static float DEFAULT_SOUND_VOLUME = 1f;
    private float soundVolume = DEFAULT_SOUND_VOLUME;
    private float volume = 1f;
    protected boolean stopRequested = false;
    private long soundStart = 0;
    private float fadeOver = 0;
    private long fadeOutStart = 0;
    
    // Loop?
    private boolean loop = false;
    
    // Default sound radius
    public static int DEFAULT_SOUND_RADIUS = 1800;

    enum Position { 
        LEFT, RIGHT, NORMAL
    };
    
    // Copy constructor
    public sound(sound s) {
    	setFileName(s.getFileName());
    	curPosition = Position.NORMAL;
    	allSounds.add(this);
    	this.x = s.x;
    	this.y = s.y;
    	this.radius = s.radius;
    	this.volume = s.volume;
    }

    public sound(String wavfile) { 
        setFileName(wavfile);
        curPosition = Position.NORMAL;
        allSounds.add(this);
    }
 
    // Get all playing
    public static ArrayList<sound> getAllPlaying() {
    	ArrayList<sound> retList = new ArrayList<sound>();
    	for(int i = 0; i < allSounds.size(); i++) {
    		retList.add(allSounds.get(i));
    	}
    	return retList;
    }
    
    // Fade out
    public void fadeOut(float f) {
    	setLoop(false);
    	fadeOver = f;
    	fadeOutStart = time.getTime();
    }
    
    // Fade out
    public void fadeIn(float f) {
    	fadeOver = f;
    }

    public void run() { 

        File soundFile = new File(getFileName());
        if (!soundFile.exists()) { 
            System.err.println("Wave file not found: " + getFileName());
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
        
        // Get the control. 
	    FloatControl control = (FloatControl) auline.getControl(FloatControl.Type.MASTER_GAIN);
        float max = control.getMaximum();
        float min = control.getMinimum(); // negative values all seem to be zero?
        
        try { 
            while (!stopRequested && nBytesRead != -1) { 
            	
    		    // Get player position
    		    int playerX = player.getPlayer().getIntX();
    		    int playerY = player.getPlayer().getIntY();
    		   
    		    // Calculate how close we are.
    		    float howClose = (float) Math.sqrt((playerX - x)*(playerX - x) + (playerY - y)*(playerY - y));
    		    
    		    // How close are you to the sound?
    		    float howClosePercentage;
            	if(radius > 0) {
            		howClosePercentage = ((float)radius - howClose)/(float)radius;
            	}
            	else {
            		howClosePercentage = 1f;
            	}
            	
        		// Fade in.
            	float fadePercent = 1;
        		if(fadeOver != 0) fadePercent = ((time.getTime() - soundStart + 1)/(fadeOver*1000));
        		if(fadePercent > 1) fadePercent = 1;
        		
        		// Fade out
        		if(fadeOutStart != 0) {
        			fadePercent = (1 - (time.getTime() - fadeOutStart + 1)/(fadeOver*1000));
            		if(fadePercent > 1) fadePercent = 1;
            		if(fadePercent < 0) {
            			stopRequested = true;
            			fadePercent = 0;
            		}
        		}
    		   
    		    // Adjust volume based on radius.
    	        float range = max - min;
    	        if(howClosePercentage>0) {	   
    	        	control.setValue(min + (range * howClosePercentage * volume * soundVolume * fadePercent));
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
            if(isLoop() && !stopRequested) {
            	if(this instanceof music) {
            		music m = new music((music)this);
            		auline.stop();
            		m.setLoop(true);
            	}
            	else { 
	            	sound s = new sound(this);
	            	auline.stop();
	            	s.setLoop(true);
	            	s.start();
            	}
            }
            auline.drain();
            auline.close();
            allSounds.remove(this);
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
    
    public static void stopAllSounds() {
		for(int i = 0; i < allSounds.size(); i++) {
				allSounds.get(0).stopRequested = true;
				allSounds.remove(0);
		}
		if(allSounds == null) allSounds = new ArrayList<sound>();
    }
    
    public static void initiate() {
    	if(allSounds != null) {
    		for(int i = 0; i < allSounds.size(); i++) {
    			if(!(allSounds.get(i) instanceof music)) {
    				if(allSounds.get(i) != null) allSounds.get(i).stopRequested = true;
    				allSounds.remove(i);
    				i--;
    			}
    		}
    	}
    	if(allSounds == null) allSounds = new ArrayList<sound>();
    }

	public boolean isLoop() {
		return loop;
	}

	public void setLoop(boolean loop) {
		this.loop = loop;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String filename) {
		this.fileName = filename;
	}
}