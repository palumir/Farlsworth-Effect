package items;

import drawing.sprites.spriteSheet;

public abstract class weapon extends item {
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default sprite stuff
	public static int DEFAULT_SPRITE_WIDTH = 64;
	public static int DEFAULT_SPRITE_HEIGHT = 64;
	protected static int DEFAULT_SPRITE_ADJUSTMENT_X = 0;
	protected static int DEFAULT_SPRITE_ADJUSTMENT_Y = 6;
	
	//////////////
	/// FIELDS ///
	//////////////
	protected spriteSheet weaponSpriteSheet;
	
	///////////////
	/// METHODS ///
	///////////////
	public weapon(String newName, spriteSheet newSpriteSheet) {
		super(newName);
		
		// Break up the spriteSheet. Assumed to be regular human character size, for now.
		weaponSpriteSheet = newSpriteSheet;
	}
}