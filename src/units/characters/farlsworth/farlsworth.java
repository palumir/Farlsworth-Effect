package units.characters.farlsworth;

import java.awt.Color;
import java.util.ArrayList;

import doodads.sheepFarm.fireLog;
import drawing.spriteSheet;
import drawing.spriteSheet.spriteSheetInfo;
import drawing.animation.animation;
import drawing.animation.animationPack;
import effects.projectiles.spinningFireLog;
import interactions.event;
import interactions.interactBox;
import interactions.textSeries;
import modes.mode;
import terrain.chunk;
import units.boss;
import units.player;
import units.unitType;
import units.characters.farlsworth.cinematics.beforeTombCinematic;
import units.characters.farlsworth.cinematics.farmFenceCinematic;
import units.characters.farlsworth.cinematics.farmIntroCinematic;
import units.characters.farlsworth.cinematics.flowerFarmCinematic;
import units.characters.farmer.cinematics.farmerIntroCinematic;
import units.unitTypes.sheepFarm.sheep;
import utilities.intTuple;
import zones.zone;
import zones.sheepFarm.sheepFarm;

public class farlsworth extends boss {
	
	// Platformer real dimensions
	public static int DEFAULT_PLATFORMER_HEIGHT = 18;
	public static int DEFAULT_PLATFORMER_WIDTH = 20;
	public static int DEFAULT_PLATFORMER_ADJUSTMENT_Y = 0;
	
	// TopDown real dimensions
	public static int DEFAULT_TOPDOWN_HEIGHT = 18;
	public static int DEFAULT_TOPDOWN_WIDTH = 20;
	public static int DEFAULT_TOPDOWN_ADJUSTMENT_Y = 4;
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Global
	public static farlsworth farlsworth;
	
	// Default name.
	private static String DEFAULT_UNIT_NAME = "Farlsworth";
	
	// Default movespeed.
	private static float DEFAULT_UNIT_MOVESPEED = 4.2f;
	
	// Default jump speed
	private static int DEFAULT_UNIT_JUMPSPEED = 10;
	
	// Name color
	private static Color DEFAULT_NAME_COLOR = new Color(51, 51, 51);
	
	// FARLSWORTH sprite stuff.
	private static String DEFAULT_FARLSWORTH_SPRITESHEET = "images/units/animals/sheep.png";
	
	// The actual type.
	private static unitType sheepType  =
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
	
	//////////////
	/// FIELDS ///
	//////////////
	
	// Interaction
	private interactBox interactSequence;
	
	// Path to follow on interaction.
	private ArrayList<intTuple> p;
	
	// Sound.
	public static String bleet = "sounds/effects/animals/sheep2.wav";
	private static String otherBleet = "sounds/effects/animals/sheep1.wav";
	
	// Are we in boss fight mode?
	private boolean bossFight = false;
	
	// Interact times.
	private int interactTimes = 0;
	private boolean interactMoved = false;
	
	// What part of the sequence are we at?
	private int sequencePart = 0;
	
	// Fence attached
	private static ArrayList<chunk> attachedFence = null;
	private static chunk attachedLog = null;
	private static spinningFireLog projectileLog = null;
	
	// Events
	public static event isFenceAttached;
	public static event pastSpawnFarm;
	private static event pastFlowerPatch;
	private static event pastTombEntrance;
	public static event pastTombExit;
	private static event pastDenmother;
	
	///////////////
	/// METHODS ///
	///////////////
	
	// Create interact sequence
	public interactBox makeNormalInteractSequence() {
	
		// Placeholder for each individual textSeries.
		textSeries s = null;
		
		// Start.
		textSeries startOfConversation = null;
		return new interactBox(startOfConversation, this, true);
	}
	
	// Booleans
	private boolean movedFromFence = false;
	private long waitStart = 0;
	private float waitFor = 0;
	private boolean waiting = false;
	private boolean didYouDodgeTheLog = false;
	
	// Ben.
	sheep ben;
	
	// Do interact stuff.
	public void doInteractStuff() {
		
		// Load player.
		player currPlayer = player.getPlayer();
	
	}
	
	// Interact with object. 
	public void interactWith() { 
		if(interactSequence == null || (interactSequence != null && !interactSequence.isUnescapable())) {
			interactMoved = false;
			faceTowardPlayer();
			interactSequence = makeNormalInteractSequence();
			interactSequence.toggleDisplay();
		}
	}

	///////////////
	/// METHODS ///
	///////////////
	// Constructor
	public farlsworth(int newX, int newY) {
		super(sheepType, "Farlsworth", newX, newY);
		
		farlsworth = this;
		
		setNameColor(DEFAULT_NAME_COLOR);
		
		// Facing direction.
		facingDirection = "Up";
		
		// He has no collision
		collisionOn = false;
		
		// Set interactable.
		setInteractable(true);
		
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
		animation runningLeft = new animation("runningLeft", getObjectSpriteSheet().getAnimation(1), 0, 3, 0.75f/5){{
			setRepeats(true);
		}};
		unitTypeAnimations.addAnimation(runningLeft);		
		
		// Running right animation.
		animation runningRight = new animation("runningRight", getObjectSpriteSheet().getAnimation(3), 0, 3, 0.75f/5){{
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
		animation runningUp = new animation("runningUp", getObjectSpriteSheet().getAnimation(0), 0, 3, 0.75f/5){{
			setRepeats(true);
		}};
		unitTypeAnimations.addAnimation(runningUp);
		
		// Running down animation.
		animation runningDown = new animation("runningDown", getObjectSpriteSheet().getAnimation(2), 0, 3, 0.75f/5){{
			setRepeats(true);
		}};
		unitTypeAnimations.addAnimation(runningDown);
		
		// Set animations.
		setAnimations(unitTypeAnimations);
		
		// Get whether or not he's lost.
		isFenceAttached = new event("farlsworthFenceAttached");
		pastSpawnFarm = new event("farlsworthRan");
		pastDenmother = new event("farlsworthPastDenmother");
		pastFlowerPatch = new event("farlsworthPastFlowerPatch");
		pastTombEntrance = new event("farlsworthPastTombEntrance");
		pastTombExit = new event("farlsworthPastTombExit");
		
		// If he's lost, don't spawn him in the farm.
		if(pastSpawnFarm.isCompleted() && 
		   player.getPlayer().getCurrentZone().getName().equals("sheepFarm")) {
			
			// If we aren't past Denmother, spawn Farlsworth there.
			if(!pastDenmother.isCompleted()) {
			}
			
			// Despawn, we've done all the Farlsworth stuff for the zone.
			else {
				this.destroy(); // TODO: Destroy for now, but should move to next location.
			}
		}
		
		// Set dimensions
		setHeight(getDefaultHeight());
		setWidth(getDefaultWidth());
		platformerHeight = DEFAULT_PLATFORMER_HEIGHT;
		platformerWidth = DEFAULT_PLATFORMER_WIDTH;
		topDownHeight = DEFAULT_TOPDOWN_HEIGHT;
		topDownWidth = DEFAULT_TOPDOWN_WIDTH;
		setHitBoxAdjustmentY(getDefaultHitBoxAdjustmentY());
		
		// If fence is attached, attach it
		if(isFenceAttached.isCompleted()) {
			attachFence();
		}
		
		// Relocate Farlsworth
		relocate();
	}
	
	// Relocate
	public void relocate() {
		if(zone.getCurrentZone().getName().equals("sheepFarm")) {
			if(!farmIntroCinematic.isCompleted.isCompleted()) {
				// Leave him in his spawn spot.
				if(farmerIntroCinematic.playerPressedNoABunch.isCompleted()) {
					setFacingDirection("Left");
				}
			}
			else if(!farmFenceCinematic.isCompleted.isCompleted()) {
				setDoubleX(5);
				setDoubleY(-420);
			}
			else if(!flowerFarmCinematic.isCompleted.isCompleted()) {
				setDoubleX(-1550);
				setDoubleY(-5258+30);
			}
			else if(!beforeTombCinematic.isCompleted.isCompleted()) {
				setDoubleX(-3463);
				setDoubleY(-5550);
			}
			else {
				destroy();
			}
		}
	}
	
	// React to pain.
	public void reactToPain() {
	}
	
	// Farlsworth AI
	public void updateUnit() {
		//printFarlsworthEvents();
		
		// Stuff to do in non-boss fight mode.
		if(!bossFight) {
			doInteractStuff();
		}
	}
	
	// Attach fence.
	public void attachFence() {
		isFenceAttached.setCompleted(true);
		
		// Only add the sheepFarm fence if it already exists, otherwise
		// we need to create the fence.
		if(sheepFarm.farlsworthFence != null) {
			attachedFence = sheepFarm.farlsworthFence;
		}
		
		// Make the fence passable.
		if(attachedFence != null) {
			for(int i = 0; i < attachedFence.size(); i++) {
				attachedFence.get(i).setPassable(true);
			}
		}
	}
	
	// Destroy fence
	public void destroyFence() {
		if(attachedFence != null) {
			for(int i = 0; i < attachedFence.size(); i++) {
				attachedFence.get(i).destroy();
			}
			attachedFence = null;
		}
	}
	
	// Do unit specific movement.
	@Override
	public void unitSpecificMovement(double moveX, double moveY) {
		
		// Move the fence for the joke.
		if(attachedFence != null) {
			for(int i = 0; i < attachedFence.size(); i++) {
				attachedFence.get(i).setDoubleX(attachedFence.get(i).getDoubleX() + moveX);
				attachedFence.get(i).setDoubleY(attachedFence.get(i).getDoubleY() + moveY);
			}
		}
		
		// Move the log.
		if(attachedLog != null) {
			attachedLog.setForceInFront(false);
			if(facingDirection.equals("Left")) {
				attachedLog.setDoubleX((int)getDoubleX() - fireLog.DEFAULT_CHUNK_WIDTH/2-5);
				attachedLog.setDoubleY((int)getDoubleY()-7);
			}
			if(facingDirection.equals("Right")) {
				attachedLog.setDoubleX((int)getDoubleX() + getWidth() - fireLog.DEFAULT_CHUNK_WIDTH/2+5);
				attachedLog.setDoubleY((int)getDoubleY()-7);
			}
			if(facingDirection.equals("Down")) {
				attachedLog.setDoubleX((int)getDoubleX() - fireLog.DEFAULT_CHUNK_WIDTH/2+10);
				attachedLog.setDoubleY(getDoubleY()+1);
				attachedLog.setForceInFront(true);
			}
			if(facingDirection.equals("Up")) {
				attachedLog.setDoubleX((int)getDoubleX() - fireLog.DEFAULT_CHUNK_WIDTH/2+10);
				attachedLog.setDoubleY((int)getDoubleY() - fireLog.DEFAULT_CHUNK_HEIGHT);
			}
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
