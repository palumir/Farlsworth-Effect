package units.unitCommands.commands;

import units.unit;
import units.unitCommand;
import units.unitCommands.positionedMovementCommand;

public class slashCommand extends positionedMovementCommand {
	
	// Constructor
	public slashCommand(double x, double y) {
		super("slash",x,y);
	}

	public slashCommand(slashCommand unitCommand) {
		super("slash",unitCommand.getX(),unitCommand.getY());
	}
	
	@Override
	public unitCommand makeCopy() {
		return new slashCommand(getX(), getY());
	}

	@Override
	public void execute(unit u) {
		
		// Only move to the next command when applicable.
		if(isIssued()) {
			
			// Slash is over. This command is done.
			if(u.isCurrentCommandComplete()) { 
				u.getAllCommands().remove(0);
				u.doCommands();
			}
		}
		
		// Issue the command if it hasn't yet been issued.
		else {
			setIssued(true);
			u.setCurrentCommandComplete(false);
			slashCommand slashCommand = (slashCommand)this;
			u.slashTo((int)slashCommand.getX(), (int)slashCommand.getY());
		}
	}
}

