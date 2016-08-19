package units.unitCommands;

import units.unitCommand;

public abstract class positionedMovementCommand extends positionedCommand {
	
	// Constructor
	public positionedMovementCommand(String s, double x, double y) {
		super(s,x,y);
	}

}

