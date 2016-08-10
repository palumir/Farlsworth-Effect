package units.unitCommands.commands;

import units.unitCommand;
import units.unitCommands.positionedCommand;

public class slashCommand extends positionedCommand {
	
	// Constructor
	public slashCommand(double x, double y) {
		super("slash",x,y);
	}

	public slashCommand(slashCommand unitCommand) {
		super("slash",unitCommand.getX(),unitCommand.getY());
	}
}

