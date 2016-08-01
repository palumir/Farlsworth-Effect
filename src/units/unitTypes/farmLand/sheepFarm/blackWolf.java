package units.unitTypes.farmLand.sheepFarm;

import doodads.sheepFarm.clawMarkBlack;
import drawing.spriteSheet;
import drawing.spriteSheet.spriteSheetInfo;
import effects.effectTypes.darkHole;
import units.player;
import units.unitType;
import utilities.mathUtils;
import utilities.time;
import utilities.utility;

public class blackWolf extends wolf {
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default name.
	private static String DEFAULT_UNIT_NAME = "Black Wolf";
	
	// Health.
	private int DEFAULT_HP = 6;
	
	// Default movespeed.
	private static int DEFAULT_UNIT_MOVESPEED = 2;
	
	// Default jump speed
	private static int DEFAULT_UNIT_JUMPSPEED = 9;
	
	// Beta stats
	private static float DEFAULT_MOVESPEED_BETA = 3.5f;
	private static float DEFAULT_CLAW_ATTACK_EVERY_BASE_BETA = 3.5f;
	private static float DEFAULT_SPAWN_CLAW_PHASE_TIME_BETA = 2.5f;
	private static int DEFAULT_FOLLOW_UNTIL_RANGE_BASE_BETA = 90;
	private static int DEFAULT_FOLLOW_UNTIL_RANGE_RANDOM_BETA = 15;

	// Alpha stats
	private static float DEFAULT_MOVESPEED_ALPHA = 3.5f;
	private static float DEFAULT_CLAW_ATTACK_EVERY_BASE_ALPHA = 1.75f;
	private static float DEFAULT_SPAWN_CLAW_PHASE_TIME_ALPHA = 1.25f;
	private static int DEFAULT_FOLLOW_UNTIL_RANGE_BASE_ALPHA = 15;
	
	// How often to spawn a darkHole.
	private static float DEFAULT_SPAWN_DARKHOLE_EVERY = 0.05f;
	
	// DarkHole
	
	//////////////
	/// FIELDS ///
	//////////////
	
	// Spawn darkHole every
	protected float spawnDarkHoleEvery = DEFAULT_SPAWN_DARKHOLE_EVERY;
	protected long lastSpawnDarkHole = 0;
	
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
	
	
	///////////////
	/// METHODS ///
	///////////////
	// Constructor
	public blackWolf(int newX, int newY) {
		super(unitTypeRef, newX, newY);
		
		// Set wolf combat stuff.
		setCombatStuff();
		
		changeCombat();
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
	public void spawnClaw(int x, int y) {
		int spawnX = x;
		int spawnY = y;
		currClaw = new clawMarkBlack(spawnX,spawnY,0);
	}

	@Override
	public void spawnTrail() {
		// Spawn darkHoles
		int stopAt = 10;
		int howClose = (int) Math.sqrt(Math.pow(this.getIntX() - currClaw.getIntX(),2) + Math.pow(this.getIntY() - currClaw.getIntY(), 2));
		if(time.getTime() - lastSpawnDarkHole > spawnDarkHoleEvery*1000 && (howClose > stopAt)) {
			lastSpawnDarkHole = time.getTime();
			darkHole h = new darkHole(this.getIntX() + this.getWidth()/2 - darkHole.getDefaultWidth()/2,
					  this.getIntY() + this.getHeight()/2 - darkHole.getDefaultHeight()/2,
					  false,
					  1,
					  5);
		}
	}

	@Override
	public void jumpingFinished() {
	}

	@Override
	public void changeCombat() {
		
		// Beta wolf
		if(!isAlpha()) {
			setMoveSpeed(DEFAULT_MOVESPEED_BETA);
			clawAttackEveryBase = DEFAULT_CLAW_ATTACK_EVERY_BASE_BETA;
			setSpawnClawPhaseTime(DEFAULT_SPAWN_CLAW_PHASE_TIME_BETA);
			followUntilRange = DEFAULT_FOLLOW_UNTIL_RANGE_BASE_BETA + utility.RNG.nextInt(DEFAULT_FOLLOW_UNTIL_RANGE_RANDOM_BETA);
		}
		
		// Alpha wolf
		else {
			// Claw attack stuff.
			setMoveSpeed(DEFAULT_MOVESPEED_ALPHA);
			clawAttackEveryBase = DEFAULT_CLAW_ATTACK_EVERY_BASE_ALPHA;
			setSpawnClawPhaseTime(DEFAULT_SPAWN_CLAW_PHASE_TIME_ALPHA);
			followUntilRange = DEFAULT_FOLLOW_UNTIL_RANGE_BASE_ALPHA;
			setClawAttackEvery(clawAttackEveryBase);
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
