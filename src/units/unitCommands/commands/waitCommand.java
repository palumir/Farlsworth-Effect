package units.unitCommands.commands;

import units.unitCommand;

public class waitCommand extends unitCommand {
	
	// How long to wait for?
	private double howLong;
	
	// Constructor
	public waitCommand(double howLong) {
		super("wait");
		this.setHowLong(howLong);
	}
	
	// Constructor
	public waitCommand(double howLong, double dumbyValueForSaving) {
		super("wait");
		this.setHowLong(howLong);
	}
	
	@Override
	public unitCommand makeCopy() {
		return new waitCommand(howLong);
	}

	public double getHowLong() {
		return howLong;
	}

	public void setHowLong(double howLong) {
		this.howLong = howLong;
	}
}

