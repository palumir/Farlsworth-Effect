package units.unitCommands.commands;

import units.unit;
import units.unitCommand;
import utilities.time;

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
	
	// Wait command
	private float waitFor = 0;
	private long waitStart = 0;

	@Override
	public void execute(unit u) {
		// Only move to the next command when applicable.
		if(isIssued()) {
			
			// Time has elapsed. This command is done.
			if(waitStart!=0 && time.getTime() - waitStart > waitFor*1000) { 
				waitStart = 0;
				u.getAllCommands().remove(0);
				u.doCommands();
			}
		}
		
		// Issue the command if it hasn't yet been issued.
		else {
			setIssued(true);
			waitCommand waitCommand = (waitCommand) this;
			waitFor = (float)waitCommand.getHowLong();
			waitStart = time.getTime();
		}
	}
}

