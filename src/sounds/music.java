package sounds;

import java.util.ArrayList;

public class music extends sound {
	
	// List of all music.
	private static ArrayList<music> allMusic = new ArrayList<music>();
	
	// Last song and volume
	private static music lastMusic;
	private static float lastVolume;
	
	// Current music.
	private static music currMusic;
	private static float currVolume;

	// Music constructor.
	public music(String soundFile) {
		super(soundFile);
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
		if(lastMusic != null) lastMusic.loopMusic(lastVolume);
	}

	// Loop a song.
	public void loopMusic(float volume) {
		// Set last music.
		lastMusic = currMusic;
		lastVolume = currVolume;
		
		// Stop all music.
		music.endAll();
		
		// Loop this song.
		currMusic = this;
		currVolume = volume;
		this.setVolume(volume);
		this.getClip().setFramePosition(0);
		this.getClip().loop(-1);
		
	}

}