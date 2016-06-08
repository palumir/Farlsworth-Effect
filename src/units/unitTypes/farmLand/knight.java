package units.unitTypes.farmLand;

import java.util.Random;

import drawing.camera;
import modes.mode;
import units.humanType;
import units.unit;
import units.unitType;
import utilities.time;
import utilities.utility;
import zones.zone;

public class knight extends unit {
	
	// Default dimensions.
	private static int DEFAULT_PLATFORMER_HEIGHT = 46;
	private static int DEFAULT_TOPDOWN_HEIGHT = 20;
	
	// Platformer and topdown default adjustment
	private static int DEFAULT_PLATFORMER_ADJUSTMENT_Y = 6;
	private static int DEFAULT_TOPDOWN_ADJUSTMENT_Y = 20;
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default name.
	private static String DEFAULT_KNIGHT_NAME = "knight";
	
	// Default movespeed.
	private static int DEFAULT_KNIGHT_MOVESPEED = 1;
	
	// Default jump speed
	private static int DEFAULT_KNIGHT_JUMPSPEED = 10;
	
	// KNIGHT sprite stuff.
	private static String DEFAULT_KNIGHT_SPRITESHEET = "images/units/humanoid/knight.png";
	
	// The actual type.
	private static unitType knightType =
			new humanType( "KNIGHT",  // Name of unitType 
						 DEFAULT_KNIGHT_SPRITESHEET,
					     DEFAULT_KNIGHT_MOVESPEED, // Movespeed
					     DEFAULT_KNIGHT_JUMPSPEED // Jump speed
						);	   
	
	//////////////
	/// FIELDS ///
	//////////////
	
	// AI stuff.
	private Long AILastCheck = 0l; // milliseconds
	private Float AICheckInterval = 1f; // seconds
	
	///////////////
	/// METHODS ///
	///////////////
	// Constructor
	public knight(int newX, int newY) {
		super(knightType, newX, newY);
		
		// Make adjustments on hitbox if we're in topDown.
		if(mode.getCurrentMode().equals("topDown")) {
			height = DEFAULT_TOPDOWN_HEIGHT;
			setHitBoxAdjustmentY(DEFAULT_TOPDOWN_ADJUSTMENT_Y);
		}
		else {
			height = DEFAULT_PLATFORMER_HEIGHT;
			setHitBoxAdjustmentY(DEFAULT_PLATFORMER_ADJUSTMENT_Y);
		}
	}
	
	// Knight AI moves knight around for now.
	public void AI() {
		
		// Move knight in a random direction every interval.
		if(time.getTime() - AILastCheck > AICheckInterval*1000) {
			AILastCheck = time.getTime();
			int random = utility.RNG.nextInt(4);
			if(random==0) moveUnit("left");
			if(random==1) moveUnit("right");
			if(random==2) moveUnit("down");
			if(random==3) moveUnit("up");
		}
	}
}
