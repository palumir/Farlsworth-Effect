package units.unitTypes.farmLand;

import java.util.Random;

import drawing.camera;
import effects.effect;
import effects.effectTypes.bloodSquirt;
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
		
		// Interactable.
		interactable = true;
		
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
	
	// Interact with object. Should be over-ridden.
	public void interactWith() {
		System.out.println("Hello I'm farmer fart");
	}
	
	// React to pain.
	public void reactToPain() {
		// Squirt blood
		int randomX = -width/3 + utility.RNG.nextInt(width/3);
		int randomY = -height/2 + utility.RNG.nextInt(height/2);
		effect e = new bloodSquirt(getX() - bloodSquirt.getDefaultWidth()/2 + width/2 + randomX,
				   getY() - bloodSquirt.getDefaultHeight()/2 + height/2 + randomY);
	}
	
	// Does nothing yet.
	public void updateUnit() {
	}
}
