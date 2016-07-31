package units.unitCommands;

import units.unitCommand;
import utilities.time;

public class waitCommand extends unitCommand {
	
	// How long to wait for?
	private float howLong;
	
	// Constructor
	public waitCommand(float howLong) {
		super("wait");
		this.setHowLong(howLong);
	}

	public float getHowLong() {
		return howLong;
	}

	public void setHowLong(float howLong) {
		this.howLong = howLong;
	}
}

