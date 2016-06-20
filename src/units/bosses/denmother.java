package units.bosses;

import java.awt.Color;
import java.awt.Graphics;
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
import drawing.userInterface.playerHealthBar;
import effects.effect;
import effects.effectTypes.bloodSquirt;
import interactions.textSeries;
import modes.mode;
import sounds.sound;
import terrain.region;
import terrain.atmosphericEffects.fog;
import terrain.doodads.farmLand.bush;
import terrain.doodads.farmLand.claw;
import units.animalType;
import units.humanType;
import units.player;
import units.unit;
import units.unitType;
import units.unitTypes.farmLand.farmer;
import units.unitTypes.farmLand.fastWolf;
import units.unitTypes.farmLand.wolf;
import utilities.stringUtils;
import utilities.time;
import utilities.intTuple;
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

	// Damage stats
	private int DEFAULT_ATTACK_DIFFERENTIAL = 6; // the range within the attackrange the unit will attack.
	private int DEFAULT_ATTACK_DAMAGE = 5;
	private float DEFAULT_BAT = 0.30f;
	private float DEFAULT_ATTACK_TIME = 0.9f;
	private int DEFAULT_ATTACK_WIDTH = 30;
	private int DEFAULT_ATTACK_LENGTH = 17;
	
	// Health
	private int DEFAULT_HP = 500;
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default name.
	private static String DEFAULT_UNIT_NAME = "denmother";
	
	// Default movespeed.
	private static int DEFAULT_UNIT_MOVESPEED = 1;
	
	// Default jump speed
	private static int DEFAULT_UNIT_JUMPSPEED = 12;
	
	// Default arena size.
	private static int DEFAULT_ARENA_RADIUS = 215;
	
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
	private static sound howl = new sound("sounds/effects/animals/wolfHowl.wav");
	private static sound growl = new sound("sounds/effects/animals/wolfGrowl.wav");
	private static sound bark1 = new sound("sounds/effects/animals/wolfBark1.wav");
	private static sound bark2 = new sound("sounds/effects/animals/wolfBark2.wav");
	
	// Music
	private static sound music = new sound("sounds/music/fightOn.wav");
	
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
	
	// Fight period
	private long fightStartTime = 0;
	
	// Sleeping?
	private boolean sleeping = true;
	
	// Howling?
	private boolean howling = false;
	private long startOfHowl = 0;
	private boolean startHowl = false;
	private boolean middleHowl = false;
	private boolean endHowl = false;
	
	// Order of events of starting boss fight:
	private boolean fightInProgress = false;
	private boolean spawnedWolves = false;
	private boolean musicStarted = false;
	private boolean combatStarted = false;
	
	// Breakpoints
	private float wakeUpTime = 0f;
	private float howlTime = wakeUpTime + 0f;
	private float spawnWolvesTime = howlTime + 2f;
	private float startMusicTime = spawnWolvesTime + 1f;
	private float startCombatTime = startMusicTime + 3f;
	
	// Pack of wolves
	private ArrayList<unit> wolfPack = null;
	private ArrayList<intTuple> wolfPackPoints = null;
	
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
		setCollisionOn(false);
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
		
		// Howling starting left.
		animation howlingStartLeft = new animation("howlingStartLeft", howlSpriteSheet.getAnimation(0), 0, 2, 1f);
		unitTypeAnimations.addAnimation(howlingStartLeft);
		
		// Howling middle left.
		animation howlingLeft = new animation("howlingLeft", howlSpriteSheet.getAnimation(0), 3, 3, 4f);
		unitTypeAnimations.addAnimation(howlingLeft);
		
		// Howling end animation
		animation howlingEndLeft = new animation("howlingEndLeft", howlSpriteSheet.getAnimation(0), 4, 6, 1f);
		unitTypeAnimations.addAnimation(howlingEndLeft);
		
		// Set animations.
		setAnimations(unitTypeAnimations);
		
		// Set default facing direction.
		setFacingDirection("Left");
	}
	
	// React to pain.
	public void reactToPain() {
	}
	
	// AI
	public void updateUnit() {
		
		// Setphase
		setPhase();
		
		// Order of events.
		potentiallyStartFight();
		potentiallyHowl();
		potentiallySpawnWolves();
		potentiallyStartMusic();
		potentiallyMoveWolves();
		potentiallyStartCombat();
		
		// Stop music if player dies.
		if(player.getCurrentPlayer().getHealthPoints() <= 0) {
			music.getClip().stop();
		}
	}
	
	// Wolf movespeed.
	private int wolfMoveSpeed = 1;
	
	// Set phases,
	public void setPhase() {
		
		///////////////
		/// PHASE 1 ///
		///////////////
		if((float)this.getHealthPoints()/(float)this.getMaxHealthPoints() >= .75f) {
			clawDelay = .60f;
			clawAttackEvery = 3f;
			phase = 1;
		}
		
		///////////////
		/// PHASE 2 ///
		///////////////
		else if(phase < 2 && (float)this.getHealthPoints()/(float)this.getMaxHealthPoints() <= .75f) {
			fightRegion.untrapPlayer();
			moveSpeed += 1;
			howl.playSound(.9f);
			clawDelay = .50f;
			jumpSpeed = jumpSpeed + 3;
			phase = 2;
			wolfMoveSpeed = phase - 1;
			clawAttackEvery = 2f;
		}
		
		///////////////
		/// PHASE 3 ///
		///////////////
		else if(phase < 3 && (float)this.getHealthPoints()/(float)this.getMaxHealthPoints() <= .50f) {
			moveSpeed += 1;
			howl.playSound(.9f);
			clawDelay = .40f;
			jumpSpeed = jumpSpeed + 3;
			phase = 3;
			wolfMoveSpeed = phase - 1;
			clawAttackEvery = 2f;
		}
		
		///////////////
		/// PHASE 4 ///
		///////////////
		else if(phase < 4 && (float)this.getHealthPoints()/(float)this.getMaxHealthPoints() <= .25f) {
			howl.playSound(.9f);
			clawAttackEvery = 2f;
			clawDelay = .2f;
			jumpSpeed = jumpSpeed + 6;
			phase = 4;
		}
	}
	
	// Start fight?
	public void potentiallyStartFight() {
		
		// Player	
		player currPlayer = player.getCurrentPlayer();
		
		// Start the fight.
		if(!fightInProgress && currPlayer != null && fightRegion != null && fightRegion.contains(currPlayer)) {
			startFight();
		}
	}
	
	// Start fight.
	public void startFight() {
		fightInProgress = true;
		fightStartTime = time.getTime();
		fightRegion.trapPlayerWithin();
		startHowl();
	}
	
	// Starthowl.
	public void startHowl() {
		// Howl.
		howling = true;
		startOfHowl = time.getTime();
		fog.fadeTo(0.3f,2f);
	}
	
	// Howl.
	public void potentiallyHowl() {
		if(howling) {
			//System.out.println(time.getTime() - startOfHowl);
			// Start howl animation.
			if(!startHowl && time.getTime() - startOfHowl < 1f*1000) {
				howl.playSound(0.8f);
				startHowl = true;
			}
			
			// Middle of howl.
			else if(!middleHowl && time.getTime() - startOfHowl > 1f*1000 && time.getTime() - startOfHowl < (1f + 4f)*1000) {
				startHowl = false;
				middleHowl = true;
			}
			
			else if(middleHowl && time.getTime() - startOfHowl > (1f + 4f)*1000 && time.getTime() - startOfHowl < (1f + 5f)*1000){
				middleHowl = false;
				endHowl = true;
			}
			
			else if(endHowl && time.getTime() - startOfHowl >= (1f + 5f)*1000){
				endHowl = false;
				howling = false;
			}
		}
	}
	
	// Spawn wolves to trap.
	public void potentiallySpawnWolves() {
		if(fightInProgress && !spawnedWolves && time.getTime() - fightStartTime > spawnWolvesTime*1000) {
		    spawnTrapWolves(50);
			spawnedWolves = true;
		}
	}
	
	// Spawn trap wolves.
	public void spawnTrapWolves(int n) {
			
			// Initiate
			wolfPack = new ArrayList<unit>();
			wolfPackPoints = new ArrayList<intTuple>();
			
			// Spawn points the wolves will walk to.
			int radius = (int) (fightRegion.getRadius());
			double currentDegree = 0;
			double degreeChange = (double) 360/n;
			for(int i = 0; i < n; i++){
				int newX = (int) (getX() + radius*Math.cos(Math.toRadians(currentDegree))); 
				int newY = (int) (getY() + radius*Math.sin(Math.toRadians(currentDegree)));
				currentDegree += degreeChange;
				intTuple t = new intTuple(newX, newY);
				wolfPackPoints.add(t);
			}
			
			// Spawn wolves.
			radius = (int) (fightRegion.getRadius()*3f);
			currentDegree = 0;
			degreeChange = (double) 360/n;
			for(int i = 0; i < n; i++){
				int randomY = utility.RNG.nextInt(76);
				int randomX = utility.RNG.nextInt(72);
				int newX = (int) (getX() + (randomX + radius)*Math.cos(Math.toRadians(currentDegree))); 
				int newY = (int) (getY() + (randomY + radius)*Math.sin(Math.toRadians(currentDegree)));
				currentDegree += degreeChange;
				int randomInt = utility.RNG.nextInt(2);
				unit u;
				if(randomInt != 1) {
					u = new wolf(newX, newY);
					((wolf)u).setDosile(true);
				}
				else {
					 u = new fastWolf(newX, newY);
					((fastWolf)u).setDosile(true);
				}
				randomInt = 2 + utility.RNG.nextInt(2);
				u.setBaseAttackTime(.1f);
				u.setAttackTime(.2f);
				u.setAttackDamage(6);
				u.setMoveSpeed(randomInt);
				u.setAttackLength(u.getAttackLength()-10);
				u.setAttackable(false);
				u.setTargetable(false);
				u.ignoreCollision();
				wolfPack.add(u);
			}
	}
	
	// Moved
	private boolean movedUp = false;
	private boolean movedDown = false;
	private boolean movedRight = false;
	private boolean movedLeft = false;
	
	// Move wolfpack.
	public void potentiallyMoveWolves() {
		
		// If the wolfpack exists.
		if(wolfPack != null) {
			
			if(phase == 1) {
				// Move inward until at the outskirts of the region.
				for(int i = 0; i < wolfPack.size(); i++) {
					if(!fightRegion.contains(wolfPack.get(i))) {
						wolfPack.get(i).moveTowards(wolfPackPoints.get(i).x, wolfPackPoints.get(i).y);
					}
					else {
						wolfPack.get(i).stopMove("all");
					}
				}
			}
			else if(phase > 1) { 
				// If the dog is below 50%, special movement mechanics.
				if(time.getTime() - lastMove > moveEvery*1000) {
					lastMove = time.getTime();
					int random = utility.RNG.nextInt(4);
					String moveDir = "left";
					if(random==0)  {
						if(movedLeft == true) {
							moveDir = "right";
							movedLeft = false;
						}
						else {
							moveDir = "left";
							movedLeft = true;			
						}
					}
					if(random==1) {
						if(movedRight == true) {
							moveDir = "left";
							movedRight = false;
						}
						else {
							moveDir = "right";
							movedRight = true;			
						}
					}
					if(random==2)  {
						if(movedUp == true) {
							moveDir = "down";
							movedUp = false;
						}
						else {
							moveDir = "up";
							movedUp = true;			
						}
					}
					if(random==3) {
						if(movedDown == true) {
							moveDir = "up";
							movedDown = false;
						}
						else {
							moveDir = "down";
							movedDown = true;			
						}
					}
					
					// Set new position.	
					for(int i = 0; i < wolfPack.size(); i++) {
							wolfPack.get(i).setMoveSpeed(wolfMoveSpeed);
							
							// Move in the direction.
							if(moveDir.equals("left")) {
								wolfPackPoints.get(i).x = wolfPack.get(i).getX() - 1000;
								wolfPackPoints.get(i).y = wolfPack.get(i).getY();
							}
							if(moveDir.equals("right")) {
								wolfPackPoints.get(i).x = wolfPack.get(i).getX() + 1000;
								wolfPackPoints.get(i).y = wolfPack.get(i).getY();
							}
							if(moveDir.equals("up")) {
								wolfPackPoints.get(i).y = wolfPack.get(i).getY() - 1000;
								wolfPackPoints.get(i).x = wolfPack.get(i).getX();
							}
							if(moveDir.equals("down")){
								wolfPackPoints.get(i).y = wolfPack.get(i).getY() + 1000;
								wolfPackPoints.get(i).x = wolfPack.get(i).getX();
							}
					}
				}
				
				// Move region.
				int xMove = 0;
				int yMove = 0;
				
				// Move region in x?
				if(wolfPackPoints.get(0).x - wolfPack.get(0).getX() > 3) {
					xMove = 1;
				}
				else if (wolfPackPoints.get(0).x - wolfPack.get(0).getX() < -3) {
					xMove = -1;
				}
				
				// Move region in y?
				if(wolfPackPoints.get(0).y - wolfPack.get(0).getY() > 3) {
					yMove = 1;
				}
				else if (wolfPackPoints.get(0).y - wolfPack.get(0).getY() < -3) {
					yMove = -1;
				}
				
				// Move region.
				fightRegion.setX(fightRegion.getX() + xMove*wolfPack.get(0).getMoveSpeed());
				fightRegion.setY(fightRegion.getY() + yMove*wolfPack.get(0).getMoveSpeed());
				
				// Move claws.
				if(claws != null) {
					for(int i = 0; i < claws.size(); i++) {
						claws.get(i).setX(claws.get(i).getX() + xMove*wolfPack.get(0).getMoveSpeed());
						claws.get(i).setY(claws.get(i).getY() + yMove*wolfPack.get(0).getMoveSpeed());
					}
				}
				
				// Move wolves.
				for(int i = 0; i < wolfPack.size(); i++) {
					wolfPack.get(i).moveTowards(wolfPackPoints.get(i).x, wolfPackPoints.get(i).y);
				}
			}
		}
	}
	
	// Potentially start music.
	public void potentiallyStartMusic() {
		if(fightInProgress && !musicStarted && time.getTime() - fightStartTime > startMusicTime*1000) {
			music.setVolume(0.9f);
			music.getClip().setFramePosition(0);
			music.getClip().loop(-1);
			musicStarted = true;
		}
	}
	
	// Potentially start combat
	public void potentiallyStartCombat() {
		if(fightInProgress && !combatStarted && time.getTime() - fightStartTime > startCombatTime*1000) {
			combatStarted = true;
			setAttackable(true);
		}
	}
	
	/////////////////////////
	///// COMBAT FIELDS /////
	/////////////////////////
	
	// Phase.
	private int phase = 1;
	
	// Region stuff.
	private long outOfRegionStart = 0;
	private float outOfRegionKillTimer = 1f;
	
	// Jumping.
	private boolean jumping = false;
	private int jumpingToX = 0;
	private int jumpingToY = 0;
	private int jumpSpeed = 10;
	private int rise = 0;
	private int run = 0;
	private boolean riseRunSet = false;
	private double currentDegree = 0;
	
	// Slash attack.
	private boolean slashing = false;
	private float clawAttackEvery = 4f;
	private int SLASH_DAMAGE = 4;
	
	// Dog border movement below 50% hp
	private long lastMove = 0;
	private float moveEvery = 1f;
	
	// Are we doing a special attack?
	private boolean doingSpecialAttack = false;

	
	// Deal with jumping.
	public void dealWithJumping() {
		
		if(doingSpecialAttack) {
			// Get current player.
			player currPlayer = player.getCurrentPlayer();
			
			// Reset rise and run if we're close.
			if(Math.abs(jumpingToX - getX()) < jumpSpeed) {
				run = 0;
			}
			if(Math.abs(jumpingToY - getY()) < jumpSpeed) {
				rise = 0;
			}
			
			// Jump to the location.
			if(jumping && (Math.abs(jumpingToX - getX()) > jumpSpeed || Math.abs(jumpingToY - getY()) > jumpSpeed)) {
				
				// set rise/run
				if(!riseRunSet) {
					riseRunSet = true;
					float yDistance = (jumpingToY - getY());
					float xDistance = (jumpingToX - getX());
					float distanceXY = (float) Math.sqrt(yDistance * yDistance
							+ xDistance * xDistance);
					rise = (int) ((yDistance/distanceXY)*jumpSpeed);
					run = (int) ((xDistance/distanceXY)*jumpSpeed);
				}
				
				setX(getX() + run);
				setY(getY() + rise);
				
				// Don't let him not move at all or leave region.
				if(run == 0 && rise == 0) {
					jumping = false;
				}
				
				// If slashing, hurt the player.
				if(slashing && currPlayer.isWithin(getX(), getY(), getX()+getWidth(), getY() + getHeight())) {
					currPlayer.hurt(SLASH_DAMAGE, 2f);
					slashing = false;
				}
			}
			else {
				stopMove("all");
				jumping = false;
				slashing = false;
			}
		}
	}
	
	// Jump
	public void jumpTo(int newX, int newY) {
		stopMove("all");
		bark1.playSound(0.9f);
		
		// Set facing direction.
		if(this.getX() - newX < 0) {
			setFacingDirection("Right");
		}
		else {
			setFacingDirection("Left");
		}
		
		// Jump there
		slashing = false;
		jumpingToX = newX;
		jumpingToY = newY;
		jumping = true;
		riseRunSet = false;
	}
	
	// Jump
	public void slashTo(int newX, int newY) {
		stopMove("all");
		bark2.playSound(0.9f);
		
		// Set facing direction.
		if(this.getX() - newX < 0) {
			setFacingDirection("Right");
		}
		else {
			setFacingDirection("Left");
		}
		
		// Jump there
		jumpingToX = newX;
		jumpingToY = newY;
		jumping = true;
		slashing = true;
		riseRunSet = false;
	}
	
	// Claw attack.
	public void launchClawAttack() {
		currentDegree += 90 + utility.RNG.nextInt(90);
		int newX = (int) (fightRegion.getX() + (fightRegion.getRadius())*Math.cos(Math.toRadians(currentDegree))); 
		int newY = (int) (fightRegion.getY() + (fightRegion.getRadius())*Math.sin(Math.toRadians(currentDegree)));
		if(phase > 1) {
			newX = (int) (fightRegion.getX() + (utility.RNG.nextInt(fightRegion.getRadius()))*Math.cos(Math.toRadians(currentDegree))); 
			newY = (int) (fightRegion.getY() + (utility.RNG.nextInt(fightRegion.getRadius()))*Math.sin(Math.toRadians(currentDegree)));
		}
		jumpTo(newX, newY);
		
		// Start slash attack.
		doingSpecialAttack = true;
		clawStart = time.getTime();
		clawsNum = phase*2 + 1;
		clawAttacking = true;
		spawnClaws(clawsNum);
	}
	
	// Clawattack stuff
	private long lastClawAttackTime = 0;
	private boolean clawAttacking = false;
	private long clawStart = 0;
	private long lastClaw = 0;
	private int clawsNum = 0;
	private float clawDelay = 0.75f;
	private float initialClawDelay = 1f;
	
	// Potentially claw attack.
	public void potentiallyClawAttack() {
		if(clawAttacking) {
			if(claws.size() > 0 && time.getTime() - clawStart > initialClawDelay*1000) {
				if(time.getTime() - lastClaw > clawDelay*1000) {
					lastClaw = time.getTime();
					slashTo(claws.get(0).getX(),claws.get(0).getY());
					claws.get(0).destroy();
					claws.remove(claws.get(0));
				}
			}
			else if(claws.size() <= 0 && time.getTime() - lastClaw > clawDelay*1000) {
				clawAttacking = false;
				jumping = false;
				slashing = false;
				doingSpecialAttack = false;
				lastClawAttackTime = time.getTime();
			}
		}
	}
	
	// List of claws.
	private ArrayList<claw> claws;
	private ArrayList<intTuple> clawsMoveToward;
	
	// Spawn claws.
	public void spawnClaws(int i) {
		claws = new ArrayList<claw>();
		clawsMoveToward = new ArrayList<intTuple>();
		for(int j = 0; j < i; j++) {
			currentDegree += 90 + utility.RNG.nextInt(150);
			int newX = (int) (fightRegion.getX() + (fightRegion.getRadius()+10)*Math.cos(Math.toRadians(currentDegree))); 
			int newY = (int) (fightRegion.getY() + (fightRegion.getRadius()+10)*Math.sin(Math.toRadians(currentDegree)));
			if(phase > 1) {
				newX = (int) (fightRegion.getX() + (utility.RNG.nextInt(fightRegion.getRadius() + 10))*Math.cos(Math.toRadians(currentDegree))); 
				newY = (int) (fightRegion.getY() + (utility.RNG.nextInt(fightRegion.getRadius() + 10))*Math.sin(Math.toRadians(currentDegree)));
			}
			int r = utility.RNG.nextInt(5);
			clawsMoveToward.add(new intTuple(newX, newY));
			claws.add(new claw(newX-32, newY-32, r));
		}
	}
	
	// AI movement.
	private long lastMoveTime = 0l; // milliseconds
	private float moveTime = 1f; // seconds
	
	// Kill player if out of region.
	public void killPlayerIfOutOfRegion() {
		if(!fightRegion.contains(player.getCurrentPlayer()) && outOfRegionStart == 0) {
			outOfRegionStart = time.getTime();
		}
		else if(fightRegion.contains(player.getCurrentPlayer())) {
			outOfRegionStart = 0;
		}
		else if(outOfRegionStart != 0 && time.getTime() - outOfRegionStart > outOfRegionKillTimer*1000) {
			player.getCurrentPlayer().hurt(player.getCurrentPlayer().getHealthPoints(), 1f);
		}
		
	}
	
	// Combat
	public void combat() {
		
		if(combatStarted) {
		
			// Update stuff contantly.
			potentiallyClawAttack();
			dealWithJumping();
			killPlayerIfOutOfRegion();
			
			// Launch claw attack.
			if(!doingSpecialAttack) {
				if(time.getTime() - lastClawAttackTime > clawAttackEvery*1000) {
					currentDegree += 90 + utility.RNG.nextInt(90);
					launchClawAttack();
				}
				
				// Meander
				else {
					moveTowards(fightRegion.getX(), fightRegion.getY());
				}
			}
		}
	}
	
	// Deal with animations.
	@Override
	public void dealWithAnimations(int moveX, int moveY) {
			
		// No hitboxadjustment.
		setHitBoxAdjustmentY(DEFAULT_PLATFORMER_ADJUSTMENT_Y);
		setHitBoxAdjustmentX(0);
		if(jumping || clawAttacking) {
			if(clawAttacking && !jumping) {
				animate("standing" + getFacingDirection());
			}
			else {
				animate("jumping" + getFacingDirection());
			}
		}
		else if(startHowl) {
			setHitBoxAdjustmentY(DEFAULT_PLATFORMER_ADJUSTMENT_Y+6);
			setHitBoxAdjustmentX(1);
			animate("howlingStartLeft");
		}
		else if(middleHowl) {
			setHitBoxAdjustmentY(DEFAULT_PLATFORMER_ADJUSTMENT_Y+6);
			setHitBoxAdjustmentX(1);
			animate("howlingLeft");
		}
		else if(endHowl) {
			setHitBoxAdjustmentY(DEFAULT_PLATFORMER_ADJUSTMENT_Y+6);
			setHitBoxAdjustmentX(1);
			animate("howlingEndLeft");
		}
		else if(isMoving() || phase > 1) {
			animate("running" + getFacingDirection());
		}
		else {
			animate("standing" + getFacingDirection());
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
	
	@Override
	public void drawObject(Graphics g) {
		// Of course only draw if the animation is not null.
		if(getCurrentAnimation() != null) {
			g.drawImage(getCurrentAnimation().getCurrentFrame(), 
					drawX, 
					drawY, 
					(int)(gameCanvas.getScaleX()*getCurrentAnimation().getCurrentFrame().getWidth()), 
					(int)(gameCanvas.getScaleY()*getCurrentAnimation().getCurrentFrame().getHeight()), 
					null);
		}
		
		// Draw healthbar is hp is low.
		if(healthPoints < maxHealthPoints) {
			// % of HP left.
			int healthChunkSize = (int)(((float)getHealthPoints()/(float)getMaxHealthPoints())*DEFAULT_HEALTHBAR_WIDTH);
			
			// Adjustment
			int hpAdjustX = (int) (gameCanvas.getScaleX()*getCurrentAnimation().getCurrentFrame().getWidth()/2 - DEFAULT_HEALTHBAR_WIDTH/2);
			int hpAdjustY = -(int)(gameCanvas.getScaleY()*getCurrentAnimation().getCurrentFrame().getHeight()/3);
			
			// Draw the red.
			g.setColor(playerHealthBar.DEFAULT_LOST_HEALTH_COLOR);
			g.fillRect(drawX + hpAdjustX,
					   drawY + hpAdjustY,
					   (int)(gameCanvas.getScaleX()*DEFAULT_HEALTHBAR_WIDTH),
					   (int)(gameCanvas.getScaleY()*DEFAULT_HEALTHBAR_HEIGHT));
			
			// Draw the green chunks.
			g.setColor(playerHealthBar.DEFAULT_HEALTH_COLOR);
			g.fillRect(drawX + hpAdjustX,
					   drawY + hpAdjustY,
					   (int)(gameCanvas.getScaleX()*healthChunkSize),
					   (int)(gameCanvas.getScaleY()*DEFAULT_HEALTHBAR_HEIGHT));

			// Draw border.
			g.setColor(playerHealthBar.DEFAULT_BORDER_COLOR);
			g.drawRect(drawX + hpAdjustX,
					   drawY + hpAdjustY,
					   (int)(gameCanvas.getScaleX()*DEFAULT_HEALTHBAR_WIDTH),
					   (int)(gameCanvas.getScaleY()*DEFAULT_HEALTHBAR_HEIGHT));
		}
		
		// Draw the outskirts of the sprite.
		if(showSpriteBox && getCurrentAnimation() != null) {
			g.setColor(Color.red);
			g.drawRect(drawX,
					   drawY, 
					   (int)(gameCanvas.getScaleX()*getCurrentAnimation().getCurrentFrame().getWidth()), 
					   (int)(gameCanvas.getScaleY()*getCurrentAnimation().getCurrentFrame().getHeight()));
		}
		
		// Draw the x,y coordinates of the unit.
		if(showUnitPosition) {
			g.setColor(Color.white);
			g.drawString(getX() + "," + getY(),
					   drawX,
					   drawY);
		}
		
		// Show attack range.
		if(showAttackRange && getCurrentAnimation() != null) {
			int x1 = 0;
			int x2 = 0;
			int y1 = 0;
			int y2 = 0;
			
			// Get the x and y of hitbox.
			int hitBoxX = drawX - (- (getCurrentAnimation().getCurrentFrame().getWidth()/2 - getWidth()/2) - getHitBoxAdjustmentX());
			int hitBoxY = drawY - (- (getCurrentAnimation().getCurrentFrame().getHeight()/2 - getHeight()/2) - getHitBoxAdjustmentY());
			
			// Get the box we will attack in if facing left.
			if(getFacingDirection().equals("Left")) {
				int heightMidPoint = hitBoxY + getHeight()/2;
				y1 = heightMidPoint - getAttackWidth()/2;
				y2 = heightMidPoint + getAttackWidth()/2;
				x1 = hitBoxX - getAttackLength();
				x2 = hitBoxX + getWidth();
			}
			
			// Get the box we will attack in if facing right.
			if(getFacingDirection().equals("Right")) {
				int heightMidPoint = hitBoxY + getHeight()/2;
				y1 = heightMidPoint - getAttackWidth()/2;
				y2 = heightMidPoint + getAttackWidth()/2;
				x1 = hitBoxX;
				x2 = hitBoxX + getWidth() + getAttackLength();
			}
			
			// Get the box we will attack in facing up.
			if(getFacingDirection().equals("Up")) {
				int widthMidPoint = hitBoxX + getWidth()/2;
				x1 = widthMidPoint - getAttackWidth()/2;
				x2 = widthMidPoint + getAttackWidth()/2;
				y1 = hitBoxY - getAttackLength();
				y2 = hitBoxY + getHeight();
			}
			
			// Get the box we will attack in facing down.
			if(getFacingDirection().equals("Down")) {
				int widthMidPoint = hitBoxX + getWidth()/2;
				x1 = widthMidPoint - getAttackWidth()/2;
				x2 = widthMidPoint + getAttackWidth()/2;
				y1 = hitBoxY;
				y2 = hitBoxY + getHeight() + getAttackLength();
			}
			g.setColor(Color.blue);
			g.drawRect((int)(gameCanvas.getScaleX()*x1),(int)(gameCanvas.getScaleY()*y1),(int)(gameCanvas.getScaleX()*x2-x1),(int)(gameCanvas.getScaleY()*y2-y1));
		}
		
		// Draw the hitbox of the image in green.
		if(showHitBox && getCurrentAnimation() != null) {
			g.setColor(Color.green);
			g.drawRect(drawX - (int)(gameCanvas.getScaleX()*(- (getCurrentAnimation().getCurrentFrame().getWidth()/2 - getWidth()/2) - getHitBoxAdjustmentX())),
					   drawY - (int)(gameCanvas.getScaleY()*(- (getCurrentAnimation().getCurrentFrame().getHeight()/2 - getHeight()/2) - getHitBoxAdjustmentY())), 
					   (int)(gameCanvas.getScaleX()*getWidth()), 
					   (int)(gameCanvas.getScaleY()*getHeight()));
		}
	}
}
