package units.unitTypes.farmLand.sheepFarm;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

import doodads.sheepFarm.clawMarkYellow;
import doodads.sheepFarm.clawMarkRed;
import doodads.sheepFarm.rock;
import drawing.camera;
import drawing.drawnObject;
import drawing.gameCanvas;
import drawing.spriteSheet;
import drawing.animation.animation;
import drawing.spriteSheet.spriteSheetInfo;
import effects.effect;
import effects.effectTypes.bloodSquirt;
import effects.effectTypes.darkHole;
import effects.effectTypes.poisonExplode;
import effects.effectTypes.explodingRock;
import modes.mode;
import sounds.sound;
import terrain.chunk;
import units.humanType;
import units.player;
import units.unit;
import units.unitType;
import utilities.intTuple;
import utilities.mathUtils;
import utilities.pathFindingNode;
import utilities.time;
import utilities.utility;
import zones.zone;

public class yellowWolf extends wolf {
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default name.
	private static String DEFAULT_UNIT_NAME = "yellowWolf";
	
	// Health.
	private int DEFAULT_HP = 10;
	
	// Default movespeed.
	private static int DEFAULT_UNIT_MOVESPEED = 2;
	
	// Default jump speed
	private static int DEFAULT_UNIT_JUMPSPEED = 12;
	
	// wolf sprite stuff.
	private static String DEFAULT_UNIT_SPRITESHEET = "images/units/animals/yellowWolf.png";
	
	// Unit sprite stuff.
	private static spriteSheet DEFAULT_UPDOWN_SPRITESHEET = new spriteSheet(new spriteSheetInfo(
			"images/units/animals/yellowWolfUpDown.png", 
			32, 
			64,
			0,
			DEFAULT_TOPDOWN_ADJUSTMENT_Y
			));
	private static spriteSheet DEFAULT_LEFTRIGHT_SPRITESHEET = new spriteSheet(new spriteSheetInfo(
			"images/units/animals/yellowWolfLeftRight.png",
			64, 
			32,
			0,
			DEFAULT_TOPDOWN_ADJUSTMENT_Y
			));
	
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
	
	// Alpha type
	private static unitType alphaUnitTypeRef =
			new unitType(DEFAULT_UNIT_NAME,  // Name of unitType 
					     null,
					     null,
					     DEFAULT_TOPDOWN_WIDTH,
					     DEFAULT_TOPDOWN_HEIGHT,
					     DEFAULT_UNIT_MOVESPEED, // Movespeed
					     DEFAULT_UNIT_JUMPSPEED // Jump speed
						);	
	
	
	// Spawn rock every
	protected float spawnRockEvery = 0.075f/2f;
	protected long lastSpawnRock = 0;
	
	// Rockpile
	private static float DEFAULT_ROCK_DURATION = 2.5f;
	private static int DEFAULT_ROCK_RADIUS = 25;
	private static int DEFAULT_ROCK_DAMAGE = 2;
	
	// How far back do we put the claw?
	private int howFarBackBase = 80;
	private int howFarBackRandom = 20;
	private int randomRadius = 1;

	
	//////////////
	/// FIELDS ///
	//////////////
	
	
	///////////////
	/// METHODS ///
	///////////////
	// Constructor
	public yellowWolf(int newX, int newY) {
		super(unitTypeRef, newX, newY);
		
		// Set wolf combat stuff.
		setCombatStuff();
	}
	
	// Combat defaults.
	@Override
	public void setCombatStuff() {
		// Set to be attackable.
		this.setKillable(true);
		
		// Wolf damage.
		setAttackFrameStart(2);
		setAttackFrameEnd(3);
		setAttackDamage(DEFAULT_ATTACK_DAMAGE);
		setAttackTime(DEFAULT_ATTACK_TIME);
		setAttackWidth(DEFAULT_ATTACK_WIDTH);
		setAttackLength(DEFAULT_ATTACK_LENGTH);
		setCritDamage(DEFAULT_CRIT_DAMAGE);
		setCritChance(DEFAULT_CRIT_CHANCE);
		
		// HP
		setMaxHealthPoints(DEFAULT_HP);
		setHealthPoints(DEFAULT_HP);
		
	}

	// Charge units
	@Override
	public void chargeUnits() {
	}
	
	// Spawn claw
	public void spawnClaw() {
		int howFarBack = howFarBackBase + utility.RNG.nextInt(howFarBackRandom);
		int spawnX = player.getCurrentPlayer().getIntX()+player.getCurrentPlayer().getWidth()/2;
		int spawnY = player.getCurrentPlayer().getIntY()+player.getCurrentPlayer().getHeight()/2;
		int degree = (int) mathUtils.angleBetweenTwoPointsWithFixedPoint(
				spawnX, spawnY,
				this.getIntX()+this.getWidth()/2, this.getIntY()+this.getHeight()/2, 
				this.getIntX()+this.getWidth()/2, this.getIntY()+this.getHeight()/2) - randomRadius + 2*(utility.RNG.nextInt(randomRadius));
		int distance = (int) Math.sqrt(Math.pow(spawnX - (this.getIntX()+this.getWidth()/2),2) + Math.pow(spawnY - (this.getIntY()+this.getHeight()/2),2));
		int newX = (int) (getIntX() + (distance+howFarBack)*Math.cos(Math.toRadians(degree))); 
		int newY = (int) (getIntY() + (distance+howFarBack)*Math.sin(Math.toRadians(degree)));
		currClaw = new clawMarkYellow(newX - clawMarkYellow.DEFAULT_CHUNK_WIDTH/2, 
									 newY - clawMarkYellow.DEFAULT_CHUNK_HEIGHT/2,
									 0);
	}

	@Override
	public void spawnTrail() {
		// Spawn rocks
		int stopAt = 10;
		int howClose = (int) Math.sqrt(Math.pow(this.getIntX() - currClaw.getIntX(),2) + Math.pow(this.getIntY() - currClaw.getIntY(), 2));
		if(time.getTime() - lastSpawnRock > spawnRockEvery*1000 && (howClose > stopAt)) {
			lastSpawnRock = time.getTime();
			explodingRock r = new explodingRock(this.getIntX() + this.getWidth()/2,
					  this.getIntY() + this.getHeight()/2,
					  false,
					  DEFAULT_ROCK_RADIUS,
					  DEFAULT_ROCK_DAMAGE,
					  DEFAULT_ROCK_DURATION);
		}
	}

	@Override
	public void jumpingFinished() {
	}

	@Override
	public void changeCombat() {
		
		// Beta wolf
		if(!alpha) {
			clawAttackEveryBase = 4f;
			spawnClawPhaseTime = 2f;
			howFarBackBase = 55;
			howFarBackRandom = 20;
			randomRadius = 40;
			followUntilRange = 90 + utility.RNG.nextInt(15);
		}
		
		// Alpha wolf
		else {
			// Claw attack stuff.
			clawAttackEveryBase = 1f;
			spawnClawPhaseTime = 0.75f;
			howFarBackBase = 15;
			howFarBackRandom = 1;
			randomRadius = 1;
			followUntilRange = 15;
			moveSpeed = 3;
			clawAttackEvery = clawAttackEveryBase;
		}
	}
	
	// Get updown spritesheet
	@Override
	public spriteSheet getUpDownSpriteSheet() {
		return DEFAULT_UPDOWN_SPRITESHEET;
	}
	
	// Get leftRight spritesheet
	@Override
	public spriteSheet getLeftRightSpriteSheet() {
		return DEFAULT_LEFTRIGHT_SPRITESHEET;
	}
	
	// Set animations for alpha
	@Override
	public void setAlphaAnimations() {
		upDownSpriteSheet = new spriteSheet(new spriteSheetInfo(
				"images/units/animals/" + DEFAULT_UNIT_NAME + "UpDownAlpha.png", 
				32, 
				64,
				0,
				DEFAULT_TOPDOWN_ADJUSTMENT_Y
				));
		
		leftRightSpriteSheet = new spriteSheet(new spriteSheetInfo(
				"images/units/animals/" + DEFAULT_UNIT_NAME + "LeftRightAlpha.png", 
				64, 
				32,
				0,
				DEFAULT_TOPDOWN_ADJUSTMENT_Y
				));
		addAnimations();
	}
}
