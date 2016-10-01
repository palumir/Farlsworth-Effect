package units.unitCommands.commands;

import units.unit;
import units.unitCommand;

public class jumpCommand extends unitCommand {
	
	// Constructor
	public jumpCommand() {
		super("jump");
	}

	public jumpCommand(moveCommand unitCommand) {
		super("jump");
	}
	
	@Override
	public unitCommand makeCopy() {
		return new jumpCommand();
	}

	@Override
	public void execute(unit u) {
		u.touchDown();
		u.setFallSpeed(-u.getJumpSpeed());
		u.setDoubleJumping(true);
		u.getAllCommands().remove(0);
		u.doCommands();
	}
}




