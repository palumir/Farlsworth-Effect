package sounds;

import java.util.ArrayList;

public class music extends sound {
	
	// List of all music.
	private static ArrayList<music> allMusic = new ArrayList<music>();

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

}