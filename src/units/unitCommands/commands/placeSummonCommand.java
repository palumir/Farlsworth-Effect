package units.unitCommands.commands;

import units.unitCommand;
import units.unitCommands.positionedCommand;

public class placeSummonCommand extends positionedCommand {
	
	// Constructor
	public placeSummonCommand(double x, double y) {
		super("placeSummon",x,y);
	}

	public placeSummonCommand(placeSummonCommand unitCommand) {
		super("placeSummon",unitCommand.getX(),unitCommand.getY());
	}
	
	@Override
	public unitCommand makeCopy() {
		return new placeSummonCommand(getX(), getY());
	}
}

