package units.unitCommands.commands;

import units.unitCommand;
import units.unitCommands.positionedCommand;
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
}

