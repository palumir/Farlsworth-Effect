package units;

public abstract class unitCommand {
	
	// The name of the command (or type).
	private String name; 
	
	// Has the command been issued?
	private boolean issued;
	
	// Constructor
	public unitCommand(String n) {
		setName(n);
		setIssued(false);
	}
	
	// Make copy
	public abstract unitCommand makeCopy();

	public boolean isIssued() {
		return issued;
	}

	public void setIssued(boolean issued) {
		this.issued = issued;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public abstract void execute(unit u);
}
