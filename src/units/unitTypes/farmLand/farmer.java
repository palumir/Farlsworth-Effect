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

public class farmer extends unit {
	
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
	private static String FARMER = "farmer";
	
	// Default movespeed.
	private static int DEFAULT_FARMER_MOVESPEED = 1;
	
	// Default jump speed
	private static int DEFAULT_FARMER_JUMPSPEED = 10;
	
	// farmer sprite stuff.
	private static String DEFAULT_FARMER_SPRITESHEET = "images/units/humanoid/farmer.png";
	
	// The actual type.
	private static unitType farmerType =
			new humanType( "farmer",  // Name of unitType 
						 DEFAULT_FARMER_SPRITESHEET,
					     DEFAULT_FARMER_MOVESPEED, // Movespeed
					     DEFAULT_FARMER_JUMPSPEED // Jump speed
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
	public farmer(int newX, int newY) {
		super(farmerType, newX, newY);
		
		// Make adjustments on hitbox if we're in topDown.
		if(mode.getCurrentMode().equals("topDown")) {
			height = DEFAULT_TOPDOWN_HEIGHT;
			setHitBoxAdjustmentY(DEFAULT_TOPDOWN_ADJUSTMENT_Y);
		}
		else {
			height = DEFAULT_PLATFORMER_HEIGHT;
			setHitBoxAdjustmentY(DEFAULT_PLATFORMER_ADJUSTMENT_Y);
		}
		setFacingDirection("Down");
	}
	
	// farmer AI moves farmer around for now.
	public void AI() {
	}
}
