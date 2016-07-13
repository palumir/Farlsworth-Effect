package units.unitTypes.farmLand.sheepFarm;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

import doodads.sheepFarm.clawMarkBlack;
import doodads.sheepFarm.clawMarkRed;
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
import modes.mode;
import sounds.sound;
import units.humanType;
import units.player;
import units.unit;
import units.unitType;
import utilities.intTuple;
import utilities.time;
import utilities.utility;
import zones.zone;

public class blackWolf extends wolf {
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default name.
	private static String DEFAULT_UNIT_NAME = "blackWolf";
	
	// Spawn claw stuff
	protected float DEFAULT_CLAW_ATTACK_EVERY = 1.5f;
	protected float DEFAULT_SPAWN_CLAW_PHASE_TIME = 1f;
	
	// Unit sprite stuff.
	private static spriteSheet DEFAULT_UPDOWN_SPRITESHEET = new spriteSheet(new spriteSheetInfo(
			"images/units/animals/blackWolfUpDown.png", 
			32, 
			64,
			0,
			DEFAULT_TOPDOWN_ADJUSTMENT_Y
			));
	private static spriteSheet DEFAULT_LEFTRIGHT_SPRITESHEET = new spriteSheet(new spriteSheetInfo(
			"images/units/animals/blackWolfLeftRight.png",
			64, 
			32,
			0,
			DEFAULT_TOPDOWN_ADJUSTMENT_Y
			));
	
	// Health.
	private int DEFAULT_HP = 10;
	
	// Default movespeed.
	private static int DEFAULT_UNIT_MOVESPEED = 2;
	
	// Default jump speed
	private static int DEFAULT_UNIT_JUMPSPEED = 12;
	
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
	
	
	//////////////
	/// FIELDS ///
	//////////////
	
	private int moveClawBy = 3*(1000/12)/2;
	
	// Dark attacking
	private float darkAttackFor = 10f;
	
	///////////////
	/// METHODS ///
	///////////////
	// Constructor
	public blackWolf(int newX, int newY) {
		super(unitTypeRef, newX, newY);
		// Set wolf combat stuff.
		setCombatStuff();
	}
	
	// Combat defaults.
	@Override
	public void setCombatStuff() {
		// Set to be attackable.
		this.setKillable(true);
		
		// Claw attack stuff.
		clawAttackEveryBase = DEFAULT_CLAW_ATTACK_EVERY;
		spawnClawPhaseTime = DEFAULT_SPAWN_CLAW_PHASE_TIME;
		
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

	// Claw destroy
	@Override
	public void clawDestroy() {
		darkHole b = new darkHole(currClaw.getIntX() + currClaw.getWidth()/2 - darkHole.getDefaultWidth()/2,
				currClaw.getIntY() + currClaw.getHeight()/2  - darkHole.getDefaultHeight()/2,
				false,
				1,
				darkAttackFor);
		currClaw.destroy();
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
	
	public boolean isDosile() {
		return dosile;
	}

	public void setDosile(boolean dosile) {
		this.dosile = dosile;
	}

	@Override
	public void chargeUnits() {
	}

	@Override
	public void spawnTrail() {	
	}

	@Override
	public void jumpingFinished() {
	}

	@Override
	public void spawnClaw() {
		int spawnX = player.getCurrentPlayer().getIntX()+player.getCurrentPlayer().getWidth()/2;
		int spawnY = player.getCurrentPlayer().getIntY()+player.getCurrentPlayer().getHeight()/2;
		
		// Change X and Y by
		int changeX = moveClawBy - 2*utility.RNG.nextInt(moveClawBy);
		int changeY = moveClawBy - 2*utility.RNG.nextInt(moveClawBy);
		
		// Move based on where the player is moving
		spawnX += changeX;
		spawnX += changeY;
		
		// Spawn claw
		currClaw = new clawMarkBlack(spawnX - clawMarkBlack.DEFAULT_CHUNK_WIDTH/2, 
									 spawnY - clawMarkBlack.DEFAULT_CHUNK_HEIGHT/2,
									 0);
	}
	
	@Override
	public void changeCombat() {
		
		// Beta wolf
		if(!alpha) {
			clawAttackEveryBase = 4f;
			spawnClawPhaseTime = 2f;
			followUntilRange = 90 + utility.RNG.nextInt(15);
			moveClawBy = 3*(1000/12)/4;
		}
		
		// Alpha wolf
		else {
			clawAttackEveryBase = 1.5f;
			spawnClawPhaseTime = 0.75f;
			moveSpeed = 3;
			followUntilRange = 15;
			moveClawBy = 1;
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
