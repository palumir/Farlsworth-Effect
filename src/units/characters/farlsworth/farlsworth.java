package units.characters.farlsworth;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import drawing.gameCanvas;
import drawing.spriteSheet;
import drawing.spriteSheet.spriteSheetInfo;
import drawing.animation.animation;
import drawing.animation.animationPack;
import effects.effectTypes.itemGlow;
import effects.projectiles.spinningFireLog;
import interactions.event;
import interactions.interactBox;
import interactions.textSeries;
import items.item;
import items.bottles.saveBottle;
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
import zones.sheepFarm.subZones.sheepFarm;

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
	
	// Farlsworth dialogue box
	private static BufferedImage DEFAULT_DIALOGUE_BOX = spriteSheet.getSpriteFromFilePath("images/units/dialogueBoxes/farlsworthBox.png");
	
	// Give bottle back eventually joke (can't figure out where to put this)
	public static event giveBottleBackEventually = event.createEvent("tombGiveBottleBackEventuallyJoke");
	
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
	
	// Fence attached
	private static ArrayList<chunk> attachedFence = null;
	private static chunk attachedLog = null;
	private static spinningFireLog projectileLog = null;
	
	// Events
	public static event isFenceAttached;
	
	///////////////
	/// METHODS ///
	///////////////
	
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
	}
	
	// Death counter
	private static int deathCounter = 0;

	///////////////
	/// METHODS ///
	///////////////
	// Constructor
	public farlsworth(int newX, int newY) {
		super(sheepType, "Farlsworth", newX, newY);
		
		// Death counter
		if(zone.getCurrentZone().getName().equals("farmTomb") && giveBottleBackEventually.isCompleted()) {
			saveBottle bottle = (saveBottle)player.getPlayer().getPlayerInventory().get("Save Bottle");
			
			if(!bottle.inInventory) {
				if(bottle!=null) {
					if(deathCounter < 3) {
						bottle.setDoubleX(2564+15);
						bottle.setDoubleY(1582+13);
					}
					else {

						bottle.setDoubleX(370);
						bottle.setDoubleY(215);
					}
					bottle.setForceInFront(true);
					bottle.undestroy();
					bottle.setGlow(new itemGlow(bottle, getIntX() + (int)bottle.getImage().getWidth()/2 - itemGlow.DEFAULT_WIDTH/2, getIntY() + (int)bottle.getImage().getHeight()/2 - itemGlow.DEFAULT_HEIGHT/2));
					bottle.attachFarlsworthNote();
					
				}
			}
			deathCounter++;
		}
		
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
		isFenceAttached = event.createEvent("farlsworthFenceAttached");
		
		// Set dialogue box
		setDialogueBox(DEFAULT_DIALOGUE_BOX);
		
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
	
	// Attached item
	public item attachedItem;
	
	// Put in mouth
	public void putItemInMouth(item i) {
		
		if(i!=null) {
			attachedItem = i;
			
			// Drop it obviously.
			i.destroy();
			i.setDrawObject(false);
			i.dropSilent();
			player.getPlayer().getPlayerInventory().unequipItem(i);
		}
	}
	
	// Place item on ground
	public void placeItemOnGround() {
		if(attachedItem != null) {
			objects.add(attachedItem);
			attachedItem.setExists(true);
			if(farlsworth.getFacingDirection().equals("Left")) {
				attachedItem.setDoubleX(getIntX() - 9 - attachedItem.getImage().getWidth()/2);
				attachedItem.setDoubleY(getIntY() - attachedItem.getImage().getHeight()/2+3);
			}
			else if(farlsworth.getFacingDirection().equals("Right")) {
				attachedItem.setDoubleX(this.getWidth() + getIntX() - attachedItem.getImage().getWidth()/2+9);
				attachedItem.setDoubleY(getIntY() - attachedItem.getImage().getHeight()/2+3);
			}
			else if(farlsworth.getFacingDirection().equals("Down")) {
				attachedItem.setDoubleX(this.getWidth()/2 + getIntX() - attachedItem.getImage().getWidth()/2);
				attachedItem.setDoubleY(getIntY() - attachedItem.getImage().getHeight()/2 +12);
			}
			else {
				attachedItem.setDoubleX(this.getWidth()/2 + getIntX() - attachedItem.getImage().getWidth()/2);
				attachedItem.setDoubleY(getIntY() - attachedItem.getImage().getHeight()/2 - 12);
			}
			attachedItem.setDrawObject(true);
			attachedItem = null;
		}
	}
	
	// Draw unit specific stuff
	@Override
	public void drawUnitSpecialStuff(Graphics g) {
		if(attachedItem != null) {
			if(farlsworth.getFacingDirection().equals("Left")) {
				g.drawImage(attachedItem.getImage(), 
						calculateDrawX(attachedItem,getIntX() - 9 - attachedItem.getImage().getWidth()/2), 
						calculateDrawY(attachedItem,getIntY() - attachedItem.getImage().getHeight()/2+3), 
						(int)(gameCanvas.getScaleX()*attachedItem.getImage().getWidth()), 
						(int)(gameCanvas.getScaleY()*attachedItem.getImage().getHeight()), 
						null);
			}
			if(farlsworth.getFacingDirection().equals("Right")) {
				g.drawImage(attachedItem.getImage(), 
						calculateDrawX(attachedItem,this.getWidth() + getIntX() - attachedItem.getImage().getWidth()/2+9), 
						calculateDrawY(attachedItem,getIntY() - attachedItem.getImage().getHeight()/2+3), 
						(int)(gameCanvas.getScaleX()*attachedItem.getImage().getWidth()), 
						(int)(gameCanvas.getScaleY()*attachedItem.getImage().getHeight()), 
						null);
			}
			if(farlsworth.getFacingDirection().equals("Down")) {
				g.drawImage(attachedItem.getImage(), 
						calculateDrawX(attachedItem,this.getWidth()/2 + getIntX() - attachedItem.getImage().getWidth()/2), 
						calculateDrawY(attachedItem,getIntY() - attachedItem.getImage().getHeight()/2 +12), 
						(int)(gameCanvas.getScaleX()*attachedItem.getImage().getWidth()), 
						(int)(gameCanvas.getScaleY()*attachedItem.getImage().getHeight()), 
						null);
			}
		}
		
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
