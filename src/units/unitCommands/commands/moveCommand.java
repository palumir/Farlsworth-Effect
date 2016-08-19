package units.unitCommands.commands;

import units.unitCommand;
import units.unitCommands.positionedCommand;
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
}

