package units.unitCommands.commands;

import units.unit;
import units.unitCommand;
import units.unitCommands.positionedMovementCommand;

public class moveCommand extends positionedMovementCommand {
	
	// Constructor
	public moveCommand(double x, double y) {
		super("move",x,y);
	}

	public moveCommand(moveCommand unitCommand) {
		super("move",unitCommand.getX(),unitCommand.getY());
	}
	
	@Override
	public unitCommand makeCopy() {
		return new moveCommand(getX(), getY());
	}
	
	@Override
	public void execute(unit u) {
		// Only move to the next command when applicable.
		if(isIssued()) {
		
			// If we have stopped moving. This command is done.
			if(!u.isMoving()) {
				u.getAllCommands().remove(0);
				u.doCommands();
			}
		}
		
		// Issue the command if it hasn't yet been issued.
		else {
			setIssued(true);
			moveCommand moveCommand = (moveCommand) this;
			u.moveTo((int)moveCommand.getX(), (int)moveCommand.getY());
		}
	}
}

