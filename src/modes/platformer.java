package modes;

import units.unit;

public class platformer extends mode {

	// Fields
	public static String name = "platformer";
	
	// Set the mode.
	public static void setMode() {
		mode.setCurrentMode(name);
	}
}