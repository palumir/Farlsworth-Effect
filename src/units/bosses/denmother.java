package units.bosses;

import java.util.ArrayList;
import java.util.Random;

import drawing.camera;
import drawing.drawnObject;
import drawing.gameCanvas;
import drawing.spriteSheet;
import drawing.animation.animation;
import drawing.animation.animationPack;
import drawing.spriteSheet.spriteSheetInfo;
import drawing.userInterface.interactBox;
import effects.effect;
import effects.effectTypes.bloodSquirt;
import interactions.textSeries;
import modes.mode;
import sounds.sound;
import terrain.region;
import units.animalType;
import units.humanType;
import units.player;
import units.unit;
import units.unitType;
import units.unitTypes.farmLand.farmer;
import units.unitTypes.farmLand.wolf;
import utilities.stringUtils;
import utilities.time;
import utilities.utility;
import zones.zone;

public class denmother extends unit {
	
	// Platformer real dimensions
	public static int DEFAULT_PLATFORMER_HEIGHT = 32;
	public static int DEFAULT_PLATFORMER_WIDTH = 32;
	public static int DEFAULT_PLATFORMER_ADJUSTMENT_Y = 0;
	public static int DEFAULT_SPRITE_ADJUSTMENT_X = 0;
	
	// TopDown real dimensions
	public static int DEFAULT_TOPDOWN_HEIGHT = 32;
	public static int DEFAULT_TOPDOWN_WIDTH = 32;
	public static int DEFAULT_TOPDOWN_ADJUSTMENT_Y = 0;
	
	// How far do the sheep patrol
	private static int DEFAULT_PATROL_RADIUS = 100;
	
	// Damage stats
	private int DEFAULT_ATTACK_DIFFERENTIAL = 6; // the range within the attackrange the unit will attack.
	private int DEFAULT_ATTACK_DAMAGE = 5;
	private float DEFAULT_BAT = 0.30f;
	private float DEFAULT_ATTACK_TIME = 0.9f;
	private int DEFAULT_ATTACK_WIDTH = 30;
	private int DEFAULT_ATTACK_LENGTH = 17;
	
	// Health
	private int DEFAULT_HP = 600;
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default name.
	private static String DEFAULT_UNIT_NAME = "denmother";
	
	// Default movespeed.
	private static int DEFAULT_UNIT_MOVESPEED = 1;
	
	// Default jump speed
	private static int DEFAULT_UNIT_JUMPSPEED = 10;
	
	// Default arena size.
	private static int DEFAULT_ARENA_RADIUS = 200;
	
	// Unit sprite stuff.
	private static String DEFAULT_HOWL_SPRITESHEET = "images/units/bosses/denmother/denmotherHowl.png";
	private static String DEFAULT_UPDOWN_SPRITESHEET = "images/units/bosses/denmother/denmotherUpDown.png";
	private static String DEFAULT_LEFTRIGHT_SPRITESHEET = "images/units/bosses/denmother/denmotherLeftRight.png";
	
	// The actual type.
	private static unitType unitTypeRef =
			new unitType(DEFAULT_UNIT_NAME,  // Name of unitType 
					     null,
					     null,
					     DEFAULT_TOPDOWN_WIDTH,
					     DEFAULT_TOPDOWN_HEIGHT,
					     DEFAULT_UNIT_MOVESPEED, // Movespeed
					     DEFAULT_UNIT_JUMPSPEED // Jump speed
						);	
	
	// Sounds
	//private sound bleet1 = new sound("sounds/effects/animals/sheep1.wav");
	//private int bleetRadius = 1200;
	
	//////////////
	/// FIELDS ///
	//////////////
	
	// Spritesheets.
	private spriteSheet howlSpriteSheet;
	private spriteSheet upDownSpriteSheet;
	private spriteSheet leftRightSpriteSheet;
	
	// Region.
	private region fightRegion;
	
	// Interaction
	private interactBox interactSequence;
	
	// Fight periods
	private long introStartTime = 0;
	
	// Has the fight started?
	private boolean fightInProgress = false;
	
	// Sleeping?
	private boolean sleeping = true;
	
	// Pack of wolves
	private ArrayList<unit> wolfPack = null;
	
	///////////////
	/// METHODS ///
	///////////////
	
	// Create interact sequence
	public interactBox makeNormalInteractSequence() {
	
		// Placeholder for each individual textSeries.
		textSeries s;
		
		// Start of conversation.
		textSeries startOfConversation = new textSeries(null, "Zzzzzz ...");
		startOfConversation.setEnd();
		
		return new interactBox(startOfConversation, stringUtils.toTitleCase(DEFAULT_UNIT_NAME));
	}
	
	// Interact with object. 
	public void interactWith() { 
		if(sleeping) {
			interactSequence = makeNormalInteractSequence();
			interactSequence.toggleDisplay();
		}
	}

	///////////////
	/// METHODS ///
	///////////////
	// Constructor
	public denmother(int newX, int newY) {
		super(unitTypeRef, newX, newY);
		
		// Set interactable.
		interactable = true;
		
		// Spritesheets.
		howlSpriteSheet = new spriteSheet(new spriteSheetInfo(
				DEFAULT_HOWL_SPRITESHEET, 
				64, 
				41,
				DEFAULT_SPRITE_ADJUSTMENT_X,
				DEFAULT_TOPDOWN_ADJUSTMENT_Y
				));
		upDownSpriteSheet = new spriteSheet(new spriteSheetInfo(
				DEFAULT_UPDOWN_SPRITESHEET, 
				32, 
				64,
				DEFAULT_SPRITE_ADJUSTMENT_X,
				DEFAULT_TOPDOWN_ADJUSTMENT_Y
				));
		
		leftRightSpriteSheet = new spriteSheet(new spriteSheetInfo(
				DEFAULT_LEFTRIGHT_SPRITESHEET, 
				64, 
				32,
				DEFAULT_SPRITE_ADJUSTMENT_X,
				DEFAULT_TOPDOWN_ADJUSTMENT_Y
				));
		
		// Add animations.
		addBossAnimations();
		
		// Combat things.
		setCombatStuff();
		
		// Set dimensions
		setHeight(getDefaultHeight());
		setWidth(getDefaultWidth());
		platformerHeight = DEFAULT_PLATFORMER_HEIGHT;
		platformerWidth = DEFAULT_PLATFORMER_WIDTH;
		topDownHeight = DEFAULT_TOPDOWN_HEIGHT;
		topDownWidth = DEFAULT_TOPDOWN_WIDTH;
		setHitBoxAdjustmentY(getDefaultHitBoxAdjustmentY());
		
		// Setup region.
		fightRegion = new region(this.getX() + this.getWidth()/2, this.getY() + this.getHeight()/2, DEFAULT_ARENA_RADIUS);

	}
	
	// Set combat stuff.
	public void setCombatStuff() {
		// Set to be attackable.
		this.setAttackable(true);
		
		// Wolf damage.
		setAttackDamage(DEFAULT_ATTACK_DAMAGE);
		setAttackTime(DEFAULT_ATTACK_TIME);
		setBaseAttackTime(DEFAULT_BAT);
		setAttackWidth(DEFAULT_ATTACK_WIDTH);
		setAttackLength(DEFAULT_ATTACK_LENGTH);
		
		// HP
		setMaxHealthPoints(DEFAULT_HP);
		setHealthPoints(DEFAULT_HP);
	}
	
	// Add animations.
	public void addBossAnimations() {
		// Deal with animations
		animationPack unitTypeAnimations = new animationPack();
		
		// Jumping left animation.
		animation jumpingLeft = new animation("jumpingLeft", leftRightSpriteSheet.getAnimation(6), 4, 4, 1);
		unitTypeAnimations.addAnimation(jumpingLeft);
		
		// Jumping right animation.
		animation jumpingRight = new animation("jumpingRight", leftRightSpriteSheet.getAnimation(2), 4, 4, 1);
		unitTypeAnimations.addAnimation(jumpingRight);
		
		// Standing left animation.
		animation standingLeft = new animation("standingLeft", leftRightSpriteSheet.getAnimation(5), 0, 0, 1);
		unitTypeAnimations.addAnimation(standingLeft);
		
		// Standing right animation.
		animation standingRight = new animation("standingRight", leftRightSpriteSheet.getAnimation(3), 0, 0, 1);
		unitTypeAnimations.addAnimation(standingRight);
		
		// Running left animation.
		animation runningLeft = new animation("runningLeft", leftRightSpriteSheet.getAnimation(5), 0, 4, 1f);
		unitTypeAnimations.addAnimation(runningLeft);		
		
		// Running right animation.
		animation runningRight = new animation("runningRight", leftRightSpriteSheet.getAnimation(1), 0, 4, 1f);
		unitTypeAnimations.addAnimation(runningRight);
		
		// Standing up animation.
		animation standingUp = new animation("standingUp", upDownSpriteSheet.getAnimation(4), 5, 5, 1);
		unitTypeAnimations.addAnimation(standingUp);
		
		// Standing down animation.
		animation standingDown = new animation("standingDown", upDownSpriteSheet.getAnimation(4), 0, 0, 1);
		unitTypeAnimations.addAnimation(standingDown);
		
		// Running up animation.
		animation runningUp = new animation("runningUp", upDownSpriteSheet.getAnimation(4), 5, 8, 1f);
		unitTypeAnimations.addAnimation(runningUp);
		
		// Running down animation.
		animation runningDown = new animation("runningDown", upDownSpriteSheet.getAnimation(4), 0, 3, 1f);
		unitTypeAnimations.addAnimation(runningDown);
		
		// Set animations.
		setAnimations(unitTypeAnimations);
	}
	
	// React to pain.
	public void reactToPain() {
	}
	
	// AI
	public void updateUnit() {
		potentiallyStartFight();
		potentiallyMoveWolves();
	}
	
	// Move wolfpack.
	public void potentiallyMoveWolves() {
		
		// If the wolfpack exists.
		if(wolfPack != null) {
			
			// Move inward until at the outskirts of the region.
			for(int i = 0; i < wolfPack.size(); i++) {
				if(!fightRegion.contains(wolfPack.get(i))) {
					wolfPack.get(i).moveTowards(fightRegion.getX(), fightRegion.getY());
				}
				else {
					wolfPack.get(i).stopMove("all");
				}
			}
		}
	}
	
	// Start fight?
	public void potentiallyStartFight() {
		
		// Player	
		player currPlayer = player.getCurrentPlayer();
		
		// Start the fight.
		if(!fightInProgress && currPlayer != null && fightRegion != null && fightRegion.contains(currPlayer)) {
			fightInProgress = true;
			introStartTime = time.getTime();
			fightRegion.trapPlayerWithin();
			wolfPack = spawnTrapWolves(50);
		}
	}
	
	// Spawn trap wolves.
	public ArrayList<unit> spawnTrapWolves(int n) {
			int radius = (int) (fightRegion.getRadius()*1.5f);
			ArrayList<unit> w = new ArrayList<unit>();
			double currentDegree = 0;
			double degreeChange = (double) 360/n;
			for(int i = 0; i < n; i++){
				int newX = (int) (getX() + radius*Math.cos(Math.toRadians(currentDegree))); 
				int newY = (int) (getY() + radius*Math.sin(Math.toRadians(currentDegree)));
				currentDegree += degreeChange;
				wolf u = new wolf(newX, newY);
				u.setDosile(true);
				u.setAttackable(false);
				u.ignoreCollision();
				w.add(u);
			}
			return w;
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
