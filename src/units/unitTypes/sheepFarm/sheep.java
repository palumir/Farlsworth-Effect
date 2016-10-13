package units.unitTypes.sheepFarm;

import drawing.spriteSheet;
import drawing.spriteSheet.spriteSheetInfo;
import drawing.animation.animation;
import drawing.animation.animationPack;
import interactions.event;
import interactions.interactBox;
import interactions.textSeries;
import modes.mode;
import sounds.sound;
import units.unit;
import units.unitType;
import utilities.time;
import utilities.utility;

public class sheep extends unit {
	
	// Platformer real dimensions
	public static int DEFAULT_PLATFORMER_HEIGHT = 25;
	public static int DEFAULT_PLATFORMER_WIDTH = 25;
	public static int DEFAULT_PLATFORMER_ADJUSTMENT_Y = 0;
	
	// TopDown real dimensions
	public static int DEFAULT_TOPDOWN_HEIGHT = 25;
	public static int DEFAULT_TOPDOWN_WIDTH = 25;
	public static int DEFAULT_TOPDOWN_ADJUSTMENT_Y = 0;
	
	// How far do the sheep patrol
	private static int DEFAULT_PATROL_RADIUS = 100;
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default name.
	private static String DEFAULT_UNIT_NAME = "Sheep";
	
	// Default movespeed.
	private static int DEFAULT_UNIT_MOVESPEED = 1;
	
	// Interact times
	private static int interactTimes = 0;
	
	// Default jump speed
	private static int DEFAULT_UNIT_JUMPSPEED = 10;
	
	// SHEEP sprite stuff.
	private static String DEFAULT_UNIT_SPRITESHEET = "images/units/animals/sheep.png";
	
	// The actual type.
	public static unitType sheepType =
			new unitType(DEFAULT_UNIT_NAME,  // Name of unitType 
					  new spriteSheet(new spriteSheetInfo(
							"images/units/animals/sheep.png", 
							90, 
							90,
							0,
							DEFAULT_TOPDOWN_ADJUSTMENT_Y
							)),
					     null,
					     DEFAULT_TOPDOWN_WIDTH,
					     DEFAULT_TOPDOWN_HEIGHT,
					     DEFAULT_UNIT_MOVESPEED, // Movespeed
					     DEFAULT_UNIT_JUMPSPEED // Jump speed
						);	
	
	
	// Sounds
	private static String bleet1 = "sounds/effects/animals/sheep1.wav";
	private int bleetRadius = 1200;
	
	//////////////
	/// FIELDS ///
	//////////////
	
	// How many times has a sheep been hit?
	private int hitTimes = 0;
	
	// Has the sheep been hit 10 times joke been done?
	public static event sheepHitABunchJoke;
	
	// AI movement.
	private long AILastCheck = 0l; // milliseconds
	private float randomMove = 2f; // seconds
	private float randomStop = 0.5f;
	private int startX = 0;
	private int startY = 0;
	private int patrolRadius = DEFAULT_PATROL_RADIUS;
	private boolean meanders = false;
	
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
		
		// Bah
		sound bleet = new sound(bleet1);
		bleet.setPosition(getIntX(), getIntY(), bleetRadius);
		bleet.start();
		
		// Default
		textSeries startOfConversation = new textSeries(null, "Bah.");
		startOfConversation.setEnd();
		
		// Sheep getting hit joke.
		if(hitTimes >= 10 && !sheepHitABunchJoke.isCompleted()) {
			startOfConversation = new textSeries(null, "Dude.");
			s = startOfConversation.addChild(null, "I'm unkillable.");
			s = s.addChild(null, "Frig off.");
			s = s.addChild(null, "I mean... uh...  baah.");
			s.setEnd();
		}
		
		// Funny interactions
		int random = utility.RNG.nextInt(7);
		if(interactTimes > 5 && random == 0) {
			startOfConversation = new textSeries(null, "Frig off.");
			startOfConversation.setEnd();
		}
		interactTimes++;
			
		return new interactBox(startOfConversation, this);
	}
	
	// Interact with object. 
	public void interactWith() { 
		stopMove("all");
		faceTowardPlayer();
		interactSequence = makeNormalInteractSequence();
		interactSequence.toggleDisplay();
	}

	///////////////
	/// METHODS ///
	///////////////
	// Constructor
	public sheep(int newX, int newY) {
		super(sheepType, newX, newY);
		
		// Small object
		setSmallObject(true);
		
		// Sheep joke
		sheepHitABunchJoke = event.createEvent("sheepHitABunchOfTimesJoke");
	
		// Deal with animations
		animationPack unitTypeAnimations = new animationPack();
		
		// Standing left animation.
		animation standingLeft = new animation("standingLeft", getObjectSpriteSheet().getAnimation(1), 3, 3, 1){{
			setRepeats(true);
		}};
		unitTypeAnimations.addAnimation(standingLeft);
		
		// Standing right animation.
		animation standingRight = new animation("standingRight", getObjectSpriteSheet().getAnimation(3), 3, 3, 1){{
			setRepeats(true);
		}};
		unitTypeAnimations.addAnimation(standingRight);
		
		// Running left animation.
		animation runningLeft = new animation("runningLeft", getObjectSpriteSheet().getAnimation(1), 0, 3, 0.75f){{
			setRepeats(true);
		}};
		unitTypeAnimations.addAnimation(runningLeft);		
		
		// Running right animation.
		animation runningRight = new animation("runningRight", getObjectSpriteSheet().getAnimation(3), 0, 3, 0.75f){{
			setRepeats(true);
		}};
		unitTypeAnimations.addAnimation(runningRight);
		
		// Standing up animation.
		animation standingUp = new animation("standingUp", getObjectSpriteSheet().getAnimation(0), 3, 3, 1){{
			setRepeats(true);
		}};
		unitTypeAnimations.addAnimation(standingUp);
		
		// Standing down animation.
		animation standingDown = new animation("standingDown", getObjectSpriteSheet().getAnimation(2), 3, 3, 1){{
			setRepeats(true);
		}};
		unitTypeAnimations.addAnimation(standingDown);
		
		// Running up animation.
		animation runningUp = new animation("runningUp", getObjectSpriteSheet().getAnimation(0), 0, 3, 0.75f){{
			setRepeats(true);
		}};
		unitTypeAnimations.addAnimation(runningUp);
		
		// Running down animation.
		animation runningDown = new animation("runningDown", getObjectSpriteSheet().getAnimation(2), 0, 3, 0.75f){{
			setRepeats(true);
		}};
		unitTypeAnimations.addAnimation(runningDown);
		
		// Set animations.
		setAnimations(unitTypeAnimations);
		
		// Set AI start X and Y
		startX = newX;
		startY = newY;
		
		// Set interactable.
		setInteractable(true);
		
		// Set dimensions
		setHeight(getDefaultHeight());
		setWidth(getDefaultWidth());
		platformerHeight = DEFAULT_PLATFORMER_HEIGHT;
		platformerWidth = DEFAULT_PLATFORMER_WIDTH;
		topDownHeight = DEFAULT_TOPDOWN_HEIGHT;
		topDownWidth = DEFAULT_TOPDOWN_WIDTH;
		setHitBoxAdjustmentY(getDefaultHitBoxAdjustmentY());

	}
	
	// Set Ben's animations.
	public void setBenAnimations() {
		
		// Deal with animations	
		animationPack unitTypeAnimations = new animationPack();
		
		// Set his movespeed really funnilily slow.
		setMoveSpeed(1);
		
		// Standing left animation.
		animation standingLeft = new animation("standingLeft", getObjectSpriteSheet().getAnimation(1), 3, 3, 1);
		unitTypeAnimations.addAnimation(standingLeft);
		
		// Standing right animation.
		animation standingRight = new animation("standingRight", getObjectSpriteSheet().getAnimation(3), 3, 3, 1);
		unitTypeAnimations.addAnimation(standingRight);
		
		// Running left animation.
		animation runningLeft = new animation("runningLeft", getObjectSpriteSheet().getAnimation(1), 0, 3, 0.075f);
		unitTypeAnimations.addAnimation(runningLeft);		
		
		// Running right animation.
		animation runningRight = new animation("runningRight", getObjectSpriteSheet().getAnimation(3), 0, 3, 0.075f);
		unitTypeAnimations.addAnimation(runningRight);
		
		// Standing up animation.
		animation standingUp = new animation("standingUp", getObjectSpriteSheet().getAnimation(0), 3, 3, 1);
		unitTypeAnimations.addAnimation(standingUp);
		
		// Standing down animation.
		animation standingDown = new animation("standingDown", getObjectSpriteSheet().getAnimation(2), 3, 3, 1);
		unitTypeAnimations.addAnimation(standingDown);
		
		// Running up animation.
		animation runningUp = new animation("runningUp", getObjectSpriteSheet().getAnimation(0), 0, 3, 0.075f);
		unitTypeAnimations.addAnimation(runningUp);
		
		// Running down animation.
		animation runningDown = new animation("runningDown", getObjectSpriteSheet().getAnimation(2), 0, 3, 0.075f);
		unitTypeAnimations.addAnimation(runningDown);
		
		// Set animations.
		setAnimations(unitTypeAnimations);
	}
	
	// Make sure the movement is within a certain radius.
	public void checkMovement(String direction) {
			if(getIntX() < startX - patrolRadius) moveUnit("right");
			else if(getIntX() + getWidth() > startX + patrolRadius)  moveUnit("left");
			else if(getIntY() < startY - patrolRadius) moveUnit("down");
			else if(getIntY() + getHeight() > startY + patrolRadius) moveUnit("up");
			else moveUnit(direction);
	}
	
	// React to pain.
	public void reactToPain() {
		hitTimes++;
	}
	
	// Deal with getting hit joke.
	public void dealWithGettingHitJoke() {
		if(hitTimes >= 10 && !sheepHitABunchJoke.isCompleted()) {
			
			// Open interactbox.
			interactWith();
			
			// Set the joke to be completed.
			sheepHitABunchJoke.setCompleted(true);
		}
	}
	
	// Meander the sheep
	public void meander() {
		if(isMeanders() && (interactSequence == null || (interactSequence != null && !interactSequence.isDisplayOn()))) {
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
					sound s = new sound(bleet1);
					s.setPosition(getIntX(), getIntY(), bleetRadius);
					s.start();
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
				randomMove = 3f + utility.RNG.nextInt(9)*0.5f;
				AILastCheck = time.getTime();
				stopMove("all");
			}
		}
	}
	
	// SHEEP AI moves SHEEP around for now.
	public void updateUnit() {
		dealWithGettingHitJoke();
		meander();
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

	public boolean isMeanders() {
		return meanders;
	}

	public void setMeanders(boolean meanders) {
		this.meanders = meanders;
	}
}
