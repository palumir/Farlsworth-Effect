package units.unitTypes.sheepFarm;

import java.util.ArrayList;

import drawing.spriteSheet;
import drawing.spriteSheet.spriteSheetInfo;
import units.unitType;
import utilities.utility;

public class yellowWolf extends wolf {
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default name.
	private static String DEFAULT_UNIT_NAME = "Yellow Wolf";
	
	// Health.
	private int DEFAULT_HP = 6;
	
	// Default movespeed.
	private static int DEFAULT_UNIT_MOVESPEED = 2;
	
	// Default jump speed
	private static int DEFAULT_UNIT_JUMPSPEED = 9;
	
	// Beta stats
	private static float DEFAULT_MOVESPEED_BETA = 2f;
	private static float DEFAULT_CLAW_ATTACK_EVERY_BASE_BETA = 3.5f;
	private static float DEFAULT_SPAWN_CLAW_PHASE_TIME_BETA = 0.5f;
	private static int DEFAULT_FOLLOW_UNTIL_RANGE_BASE_BETA = 90;
	private static int DEFAULT_FOLLOW_UNTIL_RANGE_RANDOM_BETA = 15;

	// Alpha stats
	private static float DEFAULT_MOVESPEED_ALPHA = 3.5f;
	private static float DEFAULT_CLAW_ATTACK_EVERY_BASE_ALPHA = 1.75f;
	private static float DEFAULT_SPAWN_CLAW_PHASE_TIME_ALPHA = 1.25f;
	private static int DEFAULT_FOLLOW_UNTIL_RANGE_BASE_ALPHA = 15;
	
	// How often to spawn a rock.
	private static float DEFAULT_SPAWN_ROCK_EVERY = 0.15f;
	
	// Rockpile
	private static int DEFAULT_HOW_MANY_ROCK_PIECES_SPAWN = 4;
	private static float DEFAULT_ROCK_PIECE_MOVESPEED = 1.2f;
	private static float DEFAULT_ROCK_DURATION = 0.25f;
	private static int DEFAULT_ROCK_RADIUS = 0;
	private static int DEFAULT_ROCK_DAMAGE = 1;
	
	//////////////
	/// FIELDS ///
	//////////////
	
	// Spawn rock every
	private float spawnRockEvery = DEFAULT_SPAWN_ROCK_EVERY;
	protected int howManyRockPiecesSpawn = DEFAULT_HOW_MANY_ROCK_PIECES_SPAWN;
	protected long lastSpawnRock = 0;
	protected boolean spawnUp = true;
	protected boolean spawnDown = true;
	protected boolean spawnLeft = true;
	protected boolean spawnRight = true;
	
	// List of angles that the rocks will go
	private ArrayList<Integer> rockAngles;
	
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
	
	
	///////////////
	/// METHODS ///
	///////////////
	// Constructor
	public yellowWolf(int newX, int newY) {
		super(unitTypeRef, newX, newY);
		
		// Set wolf combat stuff.
		setCombatStuff();
		
		changeCombat();
		
		setCanSlash(true);
		setCanSlashSummon(true);
	}
	
	// Combat defaults.
	@Override
	public void setCombatStuff() {
		// Set to be attackable.
		this.setKillable(true);
		setHealthPoints(DEFAULT_HP);
		
	}

	// Charge units
	@Override
	public void chargeUnits() {
	}
	
	// Spawn claw
	public void spawnClaw(int x, int y) {
	}

	@Override
	public void spawnTrail() {
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
				"images/units/animals/yellowWolfUpDownAlpha.png", 
				32, 
				64,
				0,
				DEFAULT_TOPDOWN_ADJUSTMENT_Y
				));
		
		leftRightSpriteSheet = new spriteSheet(new spriteSheetInfo(
				"images/units/animals/yellowWolfLeftRightAlpha.png", 
				64, 
				32,
				0,
				DEFAULT_TOPDOWN_ADJUSTMENT_Y
				));
		addAnimations();
	}

	public float getSpawnRockEvery() {
		return spawnRockEvery;
	}

	public void setSpawnRockEvery(float spawnRockEvery) {
		this.spawnRockEvery = spawnRockEvery;
	}

	public ArrayList<Integer> getRockAngles() {
		return rockAngles;
	}

	public void setRockAngles(ArrayList<Integer> rockAngles) {
		this.rockAngles = rockAngles;
	}
}
