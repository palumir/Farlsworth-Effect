package units.unitCommands;

import units.unitCommand;

public class slashCommand extends unitCommand {
	
	// Where do we slash to?
	private double x;
	private double y;
	
	// Constructor
	public slashCommand(double x, double y) {
		super("slash");
		this.setX(x);
		this.setY(y);
	}

	public slashCommand(slashCommand unitCommand) {
		super("slash");
		this.setX(unitCommand.x);
		this.setY(unitCommand.y);
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}
}

