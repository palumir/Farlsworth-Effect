package units;

import java.util.ArrayList;

public class commandList extends ArrayList<unitCommand> {
	
	// Constructor
	public commandList() {
		super();
	}
	
	// Copy Constructor
	public commandList(commandList c) {
		super(c);
		for(int i = 0; i < c.size(); i++) {
			this.get(i).setIssued(false);
		}
	}
}