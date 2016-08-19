package units.unitCommands;

import java.util.ArrayList;

import units.unitCommand;

public class commandList extends ArrayList<unitCommand> {
	
	// Constructor
	public commandList() {
		super();
	}
	
	// Copy Constructor
	public commandList(commandList c) {
		for(int i = 0; i < c.size(); i++) {
			unitCommand newCommand = c.get(i).makeCopy();
			this.add(newCommand);
			newCommand.setIssued(false);
		}
	}
}