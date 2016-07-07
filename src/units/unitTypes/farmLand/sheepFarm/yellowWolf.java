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
import drawing.animation.animation;
import effects.effect;
import effects.effectTypes.bloodSquirt;
import effects.effectTypes.darkExplode;
import effects.effectTypes.poisonExplode;
import effects.effectTypes.explodingRock;
import modes.mode;
import sounds.sound;
import terrain.chunk;
import units.animalType;
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
	private static String DEFAULT_WOLF_NAME = "yellowWolf";
	
	// Health.
	private int DEFAULT_HP = 11;
	
	// Default movespeed.
	private static int DEFAULT_WOLF_MOVESPEED = 2;
	
	// Default jump speed
	private static int DEFAULT_WOLF_JUMPSPEED = 12;
	
	// wolf sprite stuff.
	private static String DEFAULT_WOLF_SPRITESHEET = "images/units/animals/yellowWolf.png";
	
	// The actual type.
	private static unitType wolfType =
			new animalType( "yellowWolf",  // Name of unitType 
						 DEFAULT_WOLF_SPRITESHEET,
					     DEFAULT_WOLF_MOVESPEED, // Movespeed
					     DEFAULT_WOLF_JUMPSPEED // Jump speed
						);	
	
	// Rockpile
	private static float DEFAULT_ROCK_DURATION = 2f;
	private static int DEFAULT_ROCK_RADIUS = 35;
	private static int DEFAULT_ROCK_DAMAGE = 2;
	
	// Spawn claw stuff
	protected float DEFAULT_CLAW_ATTACK_EVERY = 3f;
	protected float DEFAULT_SPAWN_CLAW_PHASE_TIME = 1f;
	
	//////////////
	/// FIELDS ///
	//////////////
	
	// Charged units
	private ArrayList<unit> chargeUnits;
	
	
	///////////////
	/// METHODS ///
	///////////////
	// Constructor
	public yellowWolf(int newX, int newY) {
		super(wolfType, newX, newY);
		
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

	// Charge units
	@Override
	public void chargeUnits() {
		int chargeStartX = this.getX() + this.getWidth()/2;
		int chargeStartY = this.getY() + this.getHeight()/2;
		int radius = 30;
		chargeUnits = unit.getUnitsInRadius(chargeStartX, chargeStartY, radius);
		if(chargeUnits != null) {
			for(int i = 0; i < chargeUnits.size(); i++) {
				if(chargeUnits.get(i)!=this) {
					stopMove("all");
					chargeUnits.get(i).move(run,rise);
					chargeUnits.get(i).setUnitLocked(true);
				}
			}
		}
	}
	
	// Spawn claw
	public void spawnClaw() {
		int howFarBack = 60;
		int spawnX = player.getCurrentPlayer().getX()+player.getCurrentPlayer().getWidth()/2;
		int spawnY = player.getCurrentPlayer().getY()+player.getCurrentPlayer().getHeight()/2;
		int degree = (int) mathUtils.angleBetweenTwoPointsWithFixedPoint(
				spawnX, spawnY,
				this.getX()+this.getWidth()/2, this.getY()+this.getHeight()/2, 
				this.getX()+this.getWidth()/2, this.getY()+this.getHeight()/2);
		int distance = (int) Math.sqrt(Math.pow(spawnX - (this.getX()+this.getWidth()/2),2) + Math.pow(spawnY - (this.getY()+this.getHeight()/2),2));
		int newX = (int) (getX() + (distance+howFarBack)*Math.cos(Math.toRadians(degree))); 
		int newY = (int) (getY() + (distance+howFarBack)*Math.sin(Math.toRadians(degree)));
		currClaw = new clawMarkYellow(newX - clawMarkYellow.DEFAULT_CHUNK_WIDTH/2, 
									 newY - clawMarkYellow.DEFAULT_CHUNK_HEIGHT/2,
									 0);
	}

	@Override
	public void spawnTrail() {
		// Spawn rocks
		int stopAt = 30;
		int howClose = (int) Math.sqrt(Math.pow(this.getX() - currClaw.getX(),2) + Math.pow(this.getY() - currClaw.getY(), 2));
		if(time.getTime() - lastSpawnRock > spawnRockEvery*1000 && (howClose > stopAt)) {
			lastSpawnRock = time.getTime();
			explodingRock r = new explodingRock(this.getX() + this.getWidth()/2,
					  this.getY() + this.getHeight()/2,
					  false,
					  DEFAULT_ROCK_RADIUS,
					  DEFAULT_ROCK_DAMAGE,
					  DEFAULT_ROCK_DURATION);
		}
	}

	@Override
	public void jumpingFinished() {
		if(chargeUnits != null && chargeUnits.size() >= 1) {
			for(int i = 0; i < chargeUnits.size(); i++) {
				chargeUnits.get(i).setUnitLocked(false);
			}
			chargeUnits = new ArrayList<unit>();
		}
	}
}
