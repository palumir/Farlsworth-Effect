package units.unitTypes.farmLand;

import java.util.Random;

import drawing.camera;
import effects.effect;
import effects.effectTypes.bloodSquirt;
import interactions.interactBox;
import interactions.textSeries;
import modes.mode;
import sounds.sound;
import units.animalType;
import units.humanType;
import units.unit;
import units.unitType;
import utilities.stringUtils;
import utilities.time;
import utilities.utility;
import zones.zone;

public class sheep extends unit {
	
	// Platformer real dimensions
	public static int DEFAULT_PLATFORMER_HEIGHT = 32;
	public static int DEFAULT_PLATFORMER_WIDTH = 32;
	public static int DEFAULT_PLATFORMER_ADJUSTMENT_Y = 0;
	
	// TopDown real dimensions
	public static int DEFAULT_TOPDOWN_HEIGHT = 18;
	public static int DEFAULT_TOPDOWN_WIDTH = 24;
	public static int DEFAULT_TOPDOWN_ADJUSTMENT_Y = 4;
	
	// How far do the sheep patrol
	private static int DEFAULT_PATROL_RADIUS = 100;
	
	// Sheep volume.
	private static float DEFAULT_BLEET_VOLUME = 0.8f;
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default name.
	private static String DEFAULT_SHEEP_NAME = "sheep";
	
	// Default movespeed.
	private static int DEFAULT_SHEEP_MOVESPEED = 1;
	
	// Default jump speed
	private static int DEFAULT_SHEEP_JUMPSPEED = 10;
	
	// SHEEP sprite stuff.
	private static String DEFAULT_SHEEP_SPRITESHEET = "images/units/animals/sheep.png";
	
	// The actual type.
	private static unitType sheepType =
			new animalType( "sheep",  // Name of unitType 
						 DEFAULT_SHEEP_SPRITESHEET,
					     DEFAULT_SHEEP_MOVESPEED, // Movespeed
					     DEFAULT_SHEEP_JUMPSPEED // Jump speed
						);	
	
	// Sounds
	private static String bleet1 = "sounds/effects/animals/sheep1.wav";
	private static String bleet2 = "sounds/effects/animals/sheep2.wav";
	private int bleetRadius = 1200;
	
	//////////////
	/// FIELDS ///
	//////////////
	
	// AI movement.
	private long AILastCheck = 0l; // milliseconds
	private float randomMove = 1f; // seconds
	private float randomStop = 0.5f;
	private int startX = 0;
	private int startY = 0;
	private int patrolRadius = DEFAULT_PATROL_RADIUS;
	
	// AI sounds.
	private float randomBleet = 0f;
	private static float lastBleet = 0f;
	
	// Interaction
	private interactBox interactSequence;
	
	///////////////
	/// METHODS ///
	///////////////
	
	// Create interact sequence
	public interactBox makeNormalInteractSequence() {
	
		// Placeholder for each individual textSeries.
		textSeries s;
		
		// Start of conversation.
		textSeries startOfConversation = new textSeries(null, "Bah.");
		startOfConversation.setEnd();
		
		return new interactBox(startOfConversation, stringUtils.toTitleCase(DEFAULT_SHEEP_NAME), true);
	}
	
	// Interact with object. 
	public void interactWith() { 
		interactSequence = makeNormalInteractSequence();
		interactSequence.toggleDisplay();
	}

	///////////////
	/// METHODS ///
	///////////////
	// Constructor
	public sheep(int newX, int newY) {
		super(sheepType, newX, newY);
		
		// Set AI start X and Y
		startX = newX;
		startY = newY;
		
		// Set interactable.
		interactable = true;
		
		// Set dimensions
		setHeight(getDefaultHeight());
		setWidth(getDefaultWidth());
		platformerHeight = DEFAULT_PLATFORMER_HEIGHT;
		platformerWidth = DEFAULT_PLATFORMER_WIDTH;
		topDownHeight = DEFAULT_TOPDOWN_HEIGHT;
		topDownWidth = DEFAULT_TOPDOWN_WIDTH;
		setHitBoxAdjustmentY(getDefaultHitBoxAdjustmentY());

	}
	
	// Make sure the movement is within a certain radius.
	public void checkMovement(String direction) {
			if(getX() < startX - patrolRadius) moveUnit("right");
			else if(getX() + getWidth() > startX + patrolRadius)  moveUnit("left");
			else if(getY() < startY - patrolRadius) moveUnit("down");
			else if(getY() + getHeight() > startY + patrolRadius) moveUnit("up");
			else moveUnit(direction);
	}
	
	// React to pain.
	public void reactToPain() {
	}
	
	// SHEEP AI moves SHEEP around for now.
	public void updateUnit() {
		
		// Create a new random bleet interval
		float newRandomBleetInterval = 2.5f + utility.RNG.nextInt(18);
		
		// Sheep make sounds
		if(randomBleet == 0f) {
			randomBleet = newRandomBleetInterval;
		}
		if(time.getTime() - lastBleet > randomBleet*1000) {
			
			// Set the last time they bleeted.
			lastBleet = time.getTime();
			randomBleet = newRandomBleetInterval;
			
			// Play a random baaaah
			int random = utility.RNG.nextInt(2);
			if(random==0) {
				sound s = new sound(bleet1);
				s.setPosition(getX(), getY(), bleetRadius);
				s.start();
			}
			if(random==1) {
				sound s = new sound(bleet2);
				s.setPosition(getX(), getY(), bleetRadius);
				s.start();
			}
		}
		
		// Move SHEEP in a random direction every interval.
		if(time.getTime() - AILastCheck > randomMove*1000) {
			AILastCheck = time.getTime();
			int random = utility.RNG.nextInt(4);
			if(random==0) checkMovement("left");
			if(random==1) checkMovement("right");
			if(random==2) checkMovement("down");
			if(random==3) checkMovement("up");
			randomStop = 0.5f + utility.RNG.nextInt(8)*0.25f;
		}
		
		// Stop sheep after a fraction of a second
		if(isMoving() && time.getTime() - AILastCheck > randomStop*1000) {
			randomMove = 2f + utility.RNG.nextInt(9)*0.5f;
			AILastCheck = time.getTime();
			stopMove("all");
		}
	}
	
	///////////////////////////
	/// GETTERS AND SETTERS ///
	///////////////////////////
	
	// Get default width.
	public static int getDefaultWidth() {
		if(mode.getCurrentMode().equals("topDown")) {
			return DEFAULT_TOPDOWN_WIDTH;
		}
		else {
			return DEFAULT_PLATFORMER_WIDTH;
		}
	}
	
	// Get default height.
	public static int getDefaultHeight() {
		if(mode.getCurrentMode().equals("topDown")) {
			return DEFAULT_TOPDOWN_HEIGHT;
		}
		else {
			return DEFAULT_PLATFORMER_HEIGHT;
		}
	}
	
	// Get default hitbox adjustment Y.
	public static int getDefaultHitBoxAdjustmentY() {
		if(mode.getCurrentMode().equals("topDown")) {
			return DEFAULT_TOPDOWN_ADJUSTMENT_Y;
		}
		else {
			return DEFAULT_PLATFORMER_ADJUSTMENT_Y;
		}
	}
}
