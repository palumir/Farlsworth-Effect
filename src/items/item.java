package items;

public abstract class item {
	////////////////
	/// DEFAULTS ///
	////////////////
	
	//////////////
	/// FIELDS ///
	//////////////
	public String name;
	
	///////////////
	/// METHODS ///
	///////////////
	public item(String newName) {
		name = newName;
	}
	
	// Equip the item.
	public abstract void equip();
}