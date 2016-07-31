package units;

public abstract class unitCommand {
	
	// The name of the command (or type).
	private String name; 
	
	// Has the command been issued?
	private boolean issued;
	
	// Constructor
	public unitCommand(String n) {
		name = n;
		setIssued(false);
	}

	public boolean isIssued() {
		return issued;
	}

	public void setIssued(boolean issued) {
		this.issued = issued;
	}
}
