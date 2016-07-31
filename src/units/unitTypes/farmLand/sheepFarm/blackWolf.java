package units.unitTypes.farmLand.sheepFarm;

import doodads.sheepFarm.clawMarkBlack;
import drawing.spriteSheet;
import drawing.spriteSheet.spriteSheetInfo;
import effects.effectTypes.darkHole;
import modes.mode;
import units.player;
import units.unitType;
import utilities.utility;

public class blackWolf extends wolf {
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default name.
	private static String DEFAULT_UNIT_NAME = "Black Wolf";
	
	// Health.
	private int DEFAULT_HP = 6;
	
	// Default jump speed
	private static int DEFAULT_UNIT_JUMPSPEED = 9;
	
	// Darkhole
	private static float DEFAULT_DARKHOLE_FOR = 5;
	
	// Beta stats
	private static float DEFAULT_MOVESPEED_BETA = 3.5f;
	private static float DEFAULT_CLAW_ATTACK_EVERY_BETA = 4f;
	private static float DEFAULT_SPAWN_CLAW_PHASE_TIME_BETA = 3f;
	private static int DEFAULT_HOW_FAR_IN_A_DIRECTION_BETA = 90;
	private static int DEFAULT_FOLLOW_UNTIL_RANGE_BASE_BETA = 90;
	private static int DEFAULT_FOLLOW_UNTIL_RANGE_RANDOM_BETA = 15;

	// Alpha stats
	private static float DEFAULT_MOVESPEED_ALPHA = 3.5f;
	private static float DEFAULT_CLAW_ATTACK_EVERY_ALPHA = 2f;
	private static float DEFAULT_SPAWN_CLAW_PHASE_TIME_ALPHA = 1.5f;
	private static int DEFAULT_HOW_FAR_IN_A_DIRECTION_ALPHA = 30;
	private static int DEFAULT_FOLLOW_UNTIL_RANGE_BASE_ALPHA = 15;
	
	//////////////
	/// FIELDS ///
	//////////////
	
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
	
	// The actual type.
	private static unitType unitTypeRef =
			new unitType(DEFAULT_UNIT_NAME,  // Name of unitType 
					     null,
					     null,
					     DEFAULT_TOPDOWN_WIDTH,
					     DEFAULT_TOPDOWN_HEIGHT,
					     DEFAULT_MOVESPEED_BETA, // Movespeed
					     DEFAULT_UNIT_JUMPSPEED // Jump speed
						);	
	
	// Alpha type
	private static unitType alphaUnitTypeRef =
			new unitType(DEFAULT_UNIT_NAME,  // Name of unitType 
					     null,
					     null,
					     DEFAULT_TOPDOWN_WIDTH,
					     DEFAULT_TOPDOWN_HEIGHT,
					     DEFAULT_MOVESPEED_BETA, // Movespeed
					     DEFAULT_UNIT_JUMPSPEED // Jump speed
						);	
	
	private int moveClawBy = 3*(1000/12)/2;
	
	// Dark attacking
	private float darkAttackFor = DEFAULT_DARKHOLE_FOR;
	
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
		clawAttackEveryBase = DEFAULT_CLAW_ATTACK_EVERY_BETA;
		spawnClawPhaseTime = DEFAULT_SPAWN_CLAW_PHASE_TIME_BETA;
		
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
		int spawnX = player.getPlayer().getIntX()+player.getPlayer().getWidth()/2;
		int spawnY = player.getPlayer().getIntY()+player.getPlayer().getHeight()/2;
		
		// Change X and Y by
		int changeX = moveClawBy - 2*utility.RNG.nextInt(moveClawBy+1);
		int changeY = moveClawBy - 2*utility.RNG.nextInt(moveClawBy+1);
		
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
			setMoveSpeed(DEFAULT_MOVESPEED_BETA);
			clawAttackEveryBase = DEFAULT_CLAW_ATTACK_EVERY_BETA;
			spawnClawPhaseTime = DEFAULT_SPAWN_CLAW_PHASE_TIME_BETA;
			followUntilRange = DEFAULT_FOLLOW_UNTIL_RANGE_BASE_BETA + utility.RNG.nextInt(DEFAULT_FOLLOW_UNTIL_RANGE_RANDOM_BETA);
			moveClawBy = DEFAULT_HOW_FAR_IN_A_DIRECTION_BETA;
		}
		
		// Alpha wolf
		else {
			setMoveSpeed(DEFAULT_MOVESPEED_ALPHA);
			clawAttackEveryBase =DEFAULT_CLAW_ATTACK_EVERY_ALPHA;
			spawnClawPhaseTime = DEFAULT_SPAWN_CLAW_PHASE_TIME_ALPHA;
			followUntilRange = DEFAULT_FOLLOW_UNTIL_RANGE_BASE_ALPHA;
			moveClawBy = DEFAULT_HOW_FAR_IN_A_DIRECTION_ALPHA;
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
				"images/units/animals/blackWolfUpDownAlpha.png", 
				32, 
				64,
				0,
				DEFAULT_TOPDOWN_ADJUSTMENT_Y
				));
		
		leftRightSpriteSheet = new spriteSheet(new spriteSheetInfo(
				"images/units/animals/blackWolfLeftRightAlpha.png", 
				64, 
				32,
				0,
				DEFAULT_TOPDOWN_ADJUSTMENT_Y
				));
		addAnimations();
	}
}
