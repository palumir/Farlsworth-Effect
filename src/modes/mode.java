package modes;

// A mode of the game that sets some presets for
// said mode in the game files. For example, some
// modes will have gravity, others won't.
public abstract class mode {
	
	// Current mode.
	private static String currentMode;
	
	// Set the mode.
	public static void setMode() {
		// Do nothing for the basic one.
	}
	
	// Print mode.
	public static void printMode() {
		System.out.println(getCurrentMode());
	}

	public static String getCurrentMode() {
		if(currentMode==null) return "Error: No Current Mode";
		return currentMode;
	}

	public static void setCurrentMode(String currentMode) {
		mode.currentMode = currentMode;
	}

}