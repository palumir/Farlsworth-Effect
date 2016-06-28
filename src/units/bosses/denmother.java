package units.bosses;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import doodads.sheepFarm.claw;
import drawing.drawnObject;
import drawing.gameCanvas;
import drawing.spriteSheet;
import drawing.animation.animation;
import drawing.animation.animationPack;
import drawing.spriteSheet.spriteSheetInfo;
import drawing.userInterface.bossHealthBar;
import drawing.userInterface.playerHealthBar;
import effects.effect;
import effects.effectTypes.critBloodSquirt;
import interactions.interactBox;
import interactions.textSeries;
import modes.mode;
import sounds.music;
import sounds.sound;
import terrain.region;
import terrain.atmosphericEffects.fog;
import units.boss;
import units.player;
import units.unit;
import units.unitType;
import units.unitTypes.farmLand.sheepFarm.jumpingWolf;
import units.unitTypes.farmLand.sheepFarm.slowWolf;
import units.unitTypes.farmLand.sheepFarm.wolf;
import utilities.stringUtils;
import utilities.time;
import utilities.intTuple;
import utilities.utility;

public class denmother extends boss {
	
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
	private int DEFAULT_ATTACK_DAMAGE = 5;
	private float DEFAULT_BAT = 0.30f;
	private float DEFAULT_ATTACK_TIME = 0.9f;
	private int DEFAULT_ATTACK_WIDTH = 30;
	private int DEFAULT_ATTACK_LENGTH = 17;
	
	// Default display name
	private static String DEFAULT_DISPLAY_NAME = "The Denmother";
	
	// Health
	private int DEFAULT_HP = 400; // 600-800
	
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
	private static String howl = "sounds/effects/animals/wolfHowl.wav";
	private static String growl = "sounds/effects/animals/wolfGrowl.wav";
	private static String bark1 = "sounds/effects/animals/wolfBark1.wav";
	private static String bark2 = "sounds/effects/animals/wolfBark2.wav";
	private static String snore = "sounds/effects/animals/snore.wav";
	
	// Music
	private static music bossMusic = new music("sounds/music/farmLand/sheepFarm/denmother/fight.wav");
	private static music bossIntro = new music("sounds/music/farmLand/sheepFarm/denmother/intro.wav");
	
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
	private boolean combatStarted = false;
	
	// Breakpoints
	private float howlTime = 0f;
	private float spawnWolvesTime = howlTime + 2f;
	private float startMusicTime = spawnWolvesTime + 1f;
	private float startCombatTime = startMusicTime + 4f;
	
	// How long after getting hit do we wake howl?
	private long wakeUpTime = 0;
	private float growlLength = 2.5f;
	
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
	private boolean hasSlashed = false;
	private float clawAttackEvery = 4f;
	private int SLASH_DAMAGE = 10;
	
	// Dog border movement below 50% hp
	private long lastMove = 0;
	private float moveEvery = 1f;
	
	// Are we doing a special attack?
	private boolean doingSpecialAttack = false;
	
	// Dying?
	private boolean dying = false;
	private long dieStart = 0;
	private float dieTime = 4f;
	
	// Wolf movespeed.
	private int wolfMoveSpeed = 1;
	
	// Clawattack stuff
	private long lastClawAttackTime = 0;
	private boolean clawAttacking = false;
	private long clawStart = 0;
	private long lastClaw = 0;
	private long lastClawSpawn = 0;
	private float clawSpawnTime = 1f;
	private int numClaws = 0;
	private float clawDelay = 0.75f;
	private float initialClawDelay = 0.5f;
	private int numClawsToSpawn = 4;
	
	// Has she jumped towards the middle already?
	private boolean jumpingToMiddle = false;
	
	// Snoring
	private float snoreEvery = 5f;
	private long lastSnore = 0;
	
	// Moved
	private boolean movedUp = false;
	private boolean movedDown = false;
	private boolean movedRight = false;
	private boolean movedLeft = false;
	
	// List of claws.
	private ArrayList<claw> claws;
	private ArrayList<intTuple> clawsMoveToward;
	
	// Pack of wolves
	private ArrayList<unit> wolfPack = null;
	private ArrayList<intTuple> wolfPackPoints = null;
	
	// Boss health bar
	private bossHealthBar bossHealth;
	
	// Direction wolves are moving
	private String moveDir;
	
	///////////////
	/// METHODS ///
	///////////////
	
	// Create interact sequence
	public interactBox makeNormalInteractSequence() {
	
		// Placeholder for each individual textSeries.
		textSeries s;
		
		// Start of conversation.
		textSeries startOfConversation = new textSeries(null, "Zzzzzz ...");
		s = startOfConversation.addChild("Shake violently", "You shake the dog violently ...");
		s = s.addChild(null, "It's fast asleep.");
		s = s.addChild("Pet", "You pet the dog ...");
		startOfConversation.addChild(s);
		textSeries warning = s.addChild(null, "It growls in it's sleep. Boy, better not do that again.");
		textSeries walkAway = warning.addChild("Walk away", "It's probably for the best.");
		s = warning.addChild("Give belly rub", "Doesn't seem like the best idea. Are you sure?");
		s.addChild(walkAway);
		walkAway.setEnd();
		s = s.addChild("Give belly rub", "Alright, here goes ...");
		s.setEnd();
		
		return new interactBox(startOfConversation, stringUtils.toTitleCase(DEFAULT_UNIT_NAME), true);
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
		super(unitTypeRef, DEFAULT_DISPLAY_NAME, newX, newY);
		
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
		
		// Exp given.
		exp = 600;
		
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
		animation standingUp = new animation("standingUp", upDownSpriteSheet.getAnimation(2), 5, 5, 1);
		unitTypeAnimations.addAnimation(standingUp);
		
		// Standing down animation.
		animation standingDown = new animation("standingDown", upDownSpriteSheet.getAnimation(2), 0, 0, 1);
		unitTypeAnimations.addAnimation(standingDown);
		
		// Running up animation.
		animation runningUp = new animation("runningUp", upDownSpriteSheet.getAnimation(2), 5, 8, 1f);
		unitTypeAnimations.addAnimation(runningUp);
		
		// Running down animation.
		animation runningDown = new animation("runningDown", upDownSpriteSheet.getAnimation(2), 0, 3, 1f);
		unitTypeAnimations.addAnimation(runningDown);
		
		// Howling starting left.
		animation howlingStartLeft = new animation("howlingStartLeft", howlSpriteSheet.getAnimation(0), 0, 2, 1f);
		unitTypeAnimations.addAnimation(howlingStartLeft);
		
		// Howling middle left.
		animation howlingLeft = new animation("howlingLeft", howlSpriteSheet.getAnimation(0), 3, 3, 4f);
		unitTypeAnimations.addAnimation(howlingLeft);
		
		// Howling end animation
		animation howlingEndLeft = new animation("howlingEndLeft", howlSpriteSheet.getAnimation(0), 4, 6, 0.5f);
		unitTypeAnimations.addAnimation(howlingEndLeft);
		
		// Howling starting right
		animation howlingStartRight = new animation("howlingStartRight", howlSpriteSheet.getAnimation(1), 0, 2, 1f);
		unitTypeAnimations.addAnimation(howlingStartRight);
		
		// Howling middle left.
		animation howlingRight = new animation("howlingRight", howlSpriteSheet.getAnimation(1), 3, 3, 4f);
		unitTypeAnimations.addAnimation(howlingRight);
		
		// Howling end animation
		animation howlingEndRight = new animation("howlingEndRight", howlSpriteSheet.getAnimation(1), 4, 6, 0.5f);
		unitTypeAnimations.addAnimation(howlingEndRight);
		
		// Sleeping animation
		animation sleepingLeft = new animation("sleepingLeft", leftRightSpriteSheet.getAnimation(4), 3, 3, 0.5f);
		unitTypeAnimations.addAnimation(sleepingLeft);
		
		// Set animations.
		setAnimations(unitTypeAnimations);
		
		// Set default facing direction.
		setFacingDirection("Left");
	}
	
	// Drop loot.
	public void dropLoot() {
		
	}
	
	// What to do when we are dyign?
	public void potentiallyDie() {
		if(dying == true) {
			// Run wolves away.
			for(int i = 0; i < wolfPack.size(); i++) {
				wolfPack.get(i).moveTowards(originalPoints.get(i).x,originalPoints.get(i).y);
			}
			
			// Remove claws.
			if(claws != null) {
				for(int i = 0; i < claws.size(); i++) {
					claws.get(i).destroy();
				}
				claws = null;
			}
			
			// Fade back
			fog.fadeTo(0f,2f);
			
			// Destroy the fight region.
			fightRegion.destroy();
			
			// Remove wolves.
			if(time.getTime() - dieStart > dieTime*1000) {
				
				// Remove wolves
				for(int i = 0; i < wolfPack.size(); i++) {
					wolfPack.get(i).destroy();
				}
				wolfPack = null;
				
				// Do a huge blood squirt.
				effect blood = new critBloodSquirt(getX() - critBloodSquirt.getDefaultWidth()/2 + topDownWidth/2,
						   getY() - critBloodSquirt.getDefaultHeight()/2);
				
				// Give exp.
				if(player.getCurrentPlayer()!=null) player.getCurrentPlayer().giveExp(exp);
				
				// Drop loot.
				dropLoot();
				
				// Remove from game.
				destroy();
				
				// Music play last.
				music.playLast();
				
				// Complete the boss fight.
				defeat();
			}
		}
	}
	
	
	// Kill unit
	@Override
	public void die() {
		
		if(!dying) {
			// Dying = true.
			dying = true;
			combatStarted = false;
			clawAttacking = false;
			jumping = false;
			slashing = false;
			dieStart = time.getTime();
			stopMove("all");
			
			// Howl.
			getAnimations().getAnimation("howlingStartLeft").setCurrentSprite(0);
			getAnimations().getAnimation("howlingStartRight").setCurrentSprite(0);
			startHowl();
		}
	}
	
	// React to pain.
	public void reactToPain() {
		if(sleeping) {
			wakeUp();
		}
	}
	
	// Wakeup
	public void wakeUp() {
		music.endAll();
		sleeping = false;
		sound s = new sound(growl);
		s.start();
		wakeUpTime = time.getTime();
	}
	
	// AI
	public void updateUnit() {
		
		if(!sleeping && time.getTime() - wakeUpTime > growlLength*1000) {
			
			// Potentially die.
			potentiallyDie();
			potentiallyHowl();
			
			if(!dying) {
				// Setphase
				setPhase();
				
				// Order of events.
				potentiallyStartFight();
				potentiallySpawnWolves();
				potentiallyMoveWolves();
				potentiallyStartCombat();
			}
		}
		else {
			
			// Play snoring.
			if(time.getTime() - lastSnore > snoreEvery*1000) {
				lastSnore = time.getTime();
				sound s = new sound(snore);
				s.setPosition(getX(),getY(),sound.DEFAULT_SOUND_RADIUS);
				s.start();
			}
			
			// Wake up?
			if(interactSequence != null  && 
					interactSequence.getTheText().isEnd() && 
					interactSequence.getTheText().getButtonText().equals("Give belly rub")
					&& sleeping) {
				wakeUp();
			}
		}
	}
	
	// Set phases,
	public void setPhase() {
		
		///////////////
		/// PHASE 1 ///
		///////////////
		if((float)this.getHealthPoints()/(float)this.getMaxHealthPoints() >= .75f) {
			phase = 1;
			clawDelay = .60f;
			clawAttackEvery = 2f;
			numClawsToSpawn = 4;
			clawSpawnTime = 0.5f;
		}
		
		///////////////
		/// PHASE 2 ///
		///////////////
		else if(phase < 2 && (float)this.getHealthPoints()/(float)this.getMaxHealthPoints() <= .75f) {
			phase = 2;
			fightRegion.untrapPlayer();
			moveSpeed = phase - 1;
			numClawsToSpawn = 5;
			sound s = new sound(howl);
			s.start();
			clawDelay = .50f;
			jumpSpeed = jumpSpeed + 3;
			wolfMoveSpeed = moveSpeed;
			clawAttackEvery = 2f;
			clawSpawnTime = 0.3f;
		}
		
		///////////////
		/// PHASE 3 ///
		///////////////
		else if(phase < 3 && (float)this.getHealthPoints()/(float)this.getMaxHealthPoints() <= .50f) {
			phase = 3;
			moveSpeed = phase - 1;
			sound s = new sound(howl);
			s.start();
			numClawsToSpawn = 6;
			clawDelay = .40f;
			jumpSpeed = jumpSpeed + 3;
			wolfMoveSpeed = moveSpeed;
			clawAttackEvery = 1.9f;
			clawSpawnTime = 0.2f;
		}
		
		///////////////
		/// PHASE 4 ///
		///////////////
		else if(phase < 4 && (float)this.getHealthPoints()/(float)this.getMaxHealthPoints() <= .25f) {
			phase = 4;
			numClawsToSpawn = 12;
			sound s = new sound(howl);
			s.start();
			clawAttackEvery = 1.8f;
			clawDelay = .13f;
			jumpSpeed = jumpSpeed + 7;
			clawSpawnTime = 0.1f;
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
		bossIntro.getClip().setFramePosition(0);
		bossIntro.playSound();
		fightInProgress = true;
		fightStartTime = time.getTime();
		fightRegion.trapPlayerWithin();
		fog.fadeTo(0.3f,2f);
		startHowl();
	}
	
	// Starthowl.
	public void startHowl() {
		// Howl.
		howling = true;
		startOfHowl = time.getTime();
	}
	
	// Howl.
	public void potentiallyHowl() {
		if(howling) {
			// If they're facing up or down.
			if(facingDirection=="Up") facingDirection = "Left";
			if(facingDirection=="Down") facingDirection = "Right";
			
			// Start howl animation.
			if(!startHowl && time.getTime() - startOfHowl < 0.5f*1000) {
				sound s = new sound(howl);
				s.start();
				startHowl = true;
			}
			
			// Middle of howl.
			else if(!middleHowl && time.getTime() - startOfHowl >= 0.5f*1000 && time.getTime() - startOfHowl < (1f + 4f)*1000) {
				startHowl = false;
				middleHowl = true;
			}
			
			else if(middleHowl && time.getTime() - startOfHowl > (1f + 4f)*1000 && time.getTime() - startOfHowl < (0.5f + 5f)*1000){
				middleHowl = false;
				endHowl = true;
			}
			
			else if(endHowl && time.getTime() - startOfHowl >= (0.5f + 5f)*1000){
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
	
	// Original points wolves move from
	private ArrayList<intTuple> originalPoints;
	
	// Spawn trap wolves.
	public void spawnTrapWolves(int n) {
			
			// Initiate
			wolfPack = new ArrayList<unit>();
			wolfPackPoints = new ArrayList<intTuple>();
			originalPoints = new ArrayList<intTuple>();
			
			// Spawn points the wolves will walk to.
			int radius = (int) (fightRegion.getRadius());
			double currentDegree = 0;
			double degreeChange = (double) 360/n;
			for(int i = 0; i < n; i++){
				int newX = (int) (getX() + radius*Math.cos(Math.toRadians(currentDegree))); 
				int newY = (int) (getY() + radius*Math.sin(Math.toRadians(currentDegree)));
				currentDegree += degreeChange;
				wolfPackPoints.add(new intTuple(newX, newY));
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
				originalPoints.add(new intTuple(newX, newY));
				currentDegree += degreeChange;
				int randomInt = utility.RNG.nextInt(3);
				unit u;
				if(randomInt == 1) {
					u = new wolf(newX, newY);
					((wolf)u).setDosile(true);
				}
				else if (randomInt == 2){
					 u = new slowWolf(newX, newY);
					((slowWolf)u).setDosile(true);
				}
				else {
					u = new jumpingWolf(newX, newY);
					((jumpingWolf)u).setDosile(true);
				}
				randomInt = 2 + utility.RNG.nextInt(2);
				u.setMoveSpeed(randomInt);
				u.setAttackDamage(3) ;
				u.setAttackLength(7);
				u.setAttackWidth(30);
				u.setBaseAttackTime(0.2f);
				u.setAttackTime(0.25f);
				u.setKillable(false);
				u.setTargetable(false);
				u.ignoreCollision();
				wolfPack.add(u);
			}
	}
	
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
					moveDir = "left";
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
				
				// Move jumping to X and Y if dog is jumping.
				if(jumpingToMiddle) {
					jumpingToX = fightRegion.getX() - getWidth()/2 + xMove*wolfPack.get(0).getMoveSpeed();
					jumpingToY = fightRegion.getY() - getHeight()/2 + yMove*wolfPack.get(0).getMoveSpeed();
				}
				
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
	
	// Potentially start combat
	public void potentiallyStartCombat() {
		if(fightInProgress && !combatStarted && time.getTime() - fightStartTime > startCombatTime*1000) {
			
			// Deal with music.
			bossIntro.getClip().stop();
			bossMusic.loopMusic();
			
			// Start combat and make boss attackable.
			combatStarted = true;
			setKillable(true);
			
			// Create healthbar.
			bossHealth = new bossHealthBar(this);
		}
	}
	
	private boolean clawDeleted = true;
	
	// Deal with jumping.
	public void dealWithJumping() {
		if(doingSpecialAttack) {
			// Get current player.
			player currPlayer = player.getCurrentPlayer();
			
			// Jump to the location.
			if(jumping) {
				
				// set rise/run
				float yDistance = (jumpingToY - getY());
				float xDistance = (jumpingToX - getX());
				float distanceXY = (float) Math.sqrt(yDistance * yDistance
						+ xDistance * xDistance);
				rise = (int) ((yDistance/distanceXY)*jumpSpeed);
				run = (int) ((xDistance/distanceXY)*jumpSpeed);
				
				// Don't let him not move at all or leave region.
				if(Math.abs(jumpingToX - getX()) <= jumpSpeed && Math.abs(jumpingToY - getY()) <= jumpSpeed) {
					if(claws != null && claws.size() > 0) {
						claws.get(0).destroy();
						claws.remove(claws.get(0));
						clawDeleted = true;
					}
					jumping = false;
					slashing = false;
				}
				
				if(jumping) {
					setX(getX() + run);
					setY(getY() + rise);
					
					// If slashing, hurt the player.
					if(slashing && !hasSlashed && currPlayer.isWithin(getX(), getY(), getX()+getWidth(), getY() + getHeight())) {
						currPlayer.hurt(SLASH_DAMAGE, 1f);
						hasSlashed = true;
					}
				}
			}
		}
	}
	
	// Jump
	public void jumpTo(int newX, int newY) {
		stopMove("all");
		sound s = new sound(bark1);
		s.start();
		
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
	public void slashTo(claw c) {
		stopMove("all");
		sound s = new sound(bark2);
		s.start();
		
		// Set facing direction.
		if(this.getX() - c.getX() < 0) {
			setFacingDirection("Right");
		}
		else {
			setFacingDirection("Left");
		}
		
		// Jump there
		jumpingToX = c.getX() + c.getWidth()/2 - getWidth()/2;
		jumpingToY = c.getY() + c.getHeight()/2 - getHeight()/2;
		jumping = true;
		slashing = true;
		hasSlashed = false;
		riseRunSet = false;
	}
	
	// Claw attack.
	public void launchClawAttack() {
		currentDegree += 90 + utility.RNG.nextInt(90);
		int newX = (int) (fightRegion.getX() + (fightRegion.getRadius()-10)*Math.cos(Math.toRadians(currentDegree))); 
		int newY = (int) (fightRegion.getY() + (fightRegion.getRadius()-10)*Math.sin(Math.toRadians(currentDegree)));
		jumpTo(newX, newY);
		
		// Start slash attack.
		doingSpecialAttack = true;
		clawStart = time.getTime();
		clawAttacking = true;
		spawnClaws(numClawsToSpawn);
	}
	
	// Potentially claw attack.
	public void potentiallyClawAttack() {
		if(clawAttacking) {
			
			// Spawn claws.
			if(numClaws > 0 && time.getTime() - lastClawSpawn > clawSpawnTime*1000 && time.getTime() - clawStart > initialClawDelay*1000) {
					currentDegree += 90 + utility.RNG.nextInt(150);
					int newX = (int) (fightRegion.getX() + (fightRegion.getRadius()-10)*Math.cos(Math.toRadians(currentDegree))); 
					int newY = (int) (fightRegion.getY() + (fightRegion.getRadius()-10)*Math.sin(Math.toRadians(currentDegree)));
					if(phase > 1) {
						newX = (int) (fightRegion.getX() + (fightRegion.getRadius() - utility.RNG.nextInt(fightRegion.getRadius()/2))*Math.cos(Math.toRadians(currentDegree))); 
						newY = (int) (fightRegion.getY() + (fightRegion.getRadius() - utility.RNG.nextInt(fightRegion.getRadius()/2))*Math.sin(Math.toRadians(currentDegree)));
					}
					if(phase == 4) {
						newX = (int) (fightRegion.getX() + (fightRegion.getRadius() - utility.RNG.nextInt(fightRegion.getRadius()))*Math.cos(Math.toRadians(currentDegree))); 
						newY = (int) (fightRegion.getY() + (fightRegion.getRadius() - utility.RNG.nextInt(fightRegion.getRadius()))*Math.sin(Math.toRadians(currentDegree)));
					}
					int r = utility.RNG.nextInt(2);
					clawsMoveToward.add(new intTuple(newX, newY));
					claws.add(new claw(newX-32, newY-32, r));
					lastClawSpawn = time.getTime();
					numClaws--;
			}
			
			// Jump to claws, doing damage.
			else if(time.getTime() - lastClawSpawn > clawSpawnTime*1000 && numClaws <= 0 && time.getTime() - clawStart > initialClawDelay*1000 && claws.size() > 0) {
				if(clawDeleted && time.getTime() - lastClaw > clawDelay*1000) {
					lastClaw = time.getTime();
					clawDeleted = false;
					slashTo(claws.get(0));
				}
				else {
					jumpingToX = claws.get(0).getX();
					jumpingToY = claws.get(0).getY();
				}
			}
			
			// Jump to middle.
			else if(!jumpingToMiddle && numClaws <= 0 && claws.size() <= 0 && time.getTime() - lastClaw > clawDelay*1000) {
				jumpTo(fightRegion.getX()-getWidth()/2, fightRegion.getY()-getHeight()/2);
				jumpingToMiddle = true;
			}
			
			// We're done the special attack.
			else if(!jumping && jumpingToMiddle && numClaws <= 0 && claws.size() <= 0 && time.getTime() - lastClaw > clawDelay*1000) {
				clawAttacking = false;
				slashing = false;
				doingSpecialAttack = false;
				jumpingToMiddle = false;
				lastClawAttackTime = time.getTime();
			}
		}
	}
	
	// Spawn claws.
	public void spawnClaws(int i) {
		claws = new ArrayList<claw>();
		clawsMoveToward = new ArrayList<intTuple>();
		numClaws = i;
	}
	
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
					if(moveDir != null) {
						moveUnit(moveDir);
					}
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
		if(sleeping) {
			animate("sleepingLeft");
		}
		else if(jumping || clawAttacking) {
			if(clawAttacking && !jumping) {
				animate("standing" + getFacingDirection());
			}
			else {
				animate("jumping" + getFacingDirection());
			}
		}
		else if(startHowl) {
			setHitBoxAdjustmentY(DEFAULT_PLATFORMER_ADJUSTMENT_Y+6);
			setHitBoxAdjustmentX(0);
			animate("howlingStart" + facingDirection);
		}
		else if(middleHowl) {
			setHitBoxAdjustmentY(DEFAULT_PLATFORMER_ADJUSTMENT_Y+6);
			setHitBoxAdjustmentX(0);
			animate("howling" + facingDirection);
		}
		else if(endHowl) {
			setHitBoxAdjustmentY(DEFAULT_PLATFORMER_ADJUSTMENT_Y+6);
			setHitBoxAdjustmentX(0);
			animate("howlingEnd" + facingDirection);
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
	
	// Trail stuff.
	private float trailInterval = 0.0125f;
	private long lastTrail = 0;
	private int trailLength = 15;
	private ArrayList<intTuple> trail;
	private ArrayList<BufferedImage> trailImage;
	
	@Override
	public void drawObject(Graphics g) {
		// Of course only draw if the animation is not null.
		if(getCurrentAnimation() != null) {
			if(slashing) {
				
				// If it's empty, make it.
				if(trail == null) trail = new ArrayList<intTuple>();
				if(trailImage == null) trailImage = new ArrayList<BufferedImage>();
				
				// If we've passed trailInterval seconds. Add a new intTuple to trail and remove oldest one.
				if(time.getTime() - lastTrail > trailInterval*1000) {
					lastTrail = time.getTime();
					if(trail.size() >= trailLength) {
						trailImage.remove(0);
						trail.remove(0);
					}
					trailImage.add(getCurrentAnimation().getCurrentFrame());
					trail.add(new intTuple(getX(),getY()));
				}
				
				g.drawImage(getCurrentAnimation().getCurrentFrame(), 
						drawX, 
						drawY, 
						(int)(gameCanvas.getScaleX()*getCurrentAnimation().getCurrentFrame().getWidth()), 
						(int)(gameCanvas.getScaleY()*getCurrentAnimation().getCurrentFrame().getHeight()), 
						null);
				
				// Draw trail.
				for(int i = 0; i < trail.size(); i++) {
					float alpha = ((float)i)/(float)trail.size();
					Graphics2D g2d = (Graphics2D) g.create();
					g2d.setComposite(AlphaComposite.SrcOver.derive(alpha));
					g2d.drawImage(trailImage.get(i), 
							drawnObject.calculateDrawX(this, trail.get(i).x), 
							drawnObject.calculateDrawY(this, trail.get(i).y), 
							(int)(gameCanvas.getScaleX()*trailImage.get(i).getWidth()), 
							(int)(gameCanvas.getScaleY()*trailImage.get(i).getHeight()), 
							null);
				}
			}
			else {
				if(trail != null) {
					
					// If we've passed trailInterval seconds. Add a new intTuple to trail and remove oldest one.
					if(time.getTime() - lastTrail > trailInterval*1000 && trail.size() > 0) {
						lastTrail = time.getTime();
						trail.remove(0);
						trailImage.remove(0);
					}
				}
				g.drawImage(getCurrentAnimation().getCurrentFrame(), 
						drawX, 
						drawY, 
						(int)(gameCanvas.getScaleX()*getCurrentAnimation().getCurrentFrame().getWidth()), 
						(int)(gameCanvas.getScaleY()*getCurrentAnimation().getCurrentFrame().getHeight()), 
						null);
				
				if(trail != null) {
					// Draw trail.
					for(int i = 0; i < trail.size(); i++) {
						float alpha = ((float)i)/(float)trail.size();
						Graphics2D g2d = (Graphics2D) g.create();
						g2d.setComposite(AlphaComposite.SrcOver.derive(alpha));
						g2d.drawImage(trailImage.get(i), 
								drawnObject.calculateDrawX(this, trail.get(i).x), 
								drawnObject.calculateDrawY(this, trail.get(i).y), 
								(int)(gameCanvas.getScaleX()*trailImage.get(i).getWidth()), 
								(int)(gameCanvas.getScaleY()*trailImage.get(i).getHeight()), 
								null);
					}
				}
			}
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
