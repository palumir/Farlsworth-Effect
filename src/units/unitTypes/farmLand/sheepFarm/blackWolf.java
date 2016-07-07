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
import drawing.animation.animation;
import effects.effect;
import effects.effectTypes.bloodSquirt;
import effects.effectTypes.darkExplode;
import effects.effectTypes.poisonExplode;
import modes.mode;
import sounds.sound;
import units.animalType;
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
	private static String DEFAULT_WOLF_NAME = "blackWolf";
	
	// Spawn claw stuff
	protected float DEFAULT_CLAW_ATTACK_EVERY = 2f;
	protected float DEFAULT_SPAWN_CLAW_PHASE_TIME = 1f;
	
	// Health.
	private int DEFAULT_HP = 11;
	
	// Default movespeed.
	private static int DEFAULT_WOLF_MOVESPEED = 2;
	
	// Default jump speed
	private static int DEFAULT_WOLF_JUMPSPEED = 9;
	
	// wolf sprite stuff.
	private static String DEFAULT_WOLF_SPRITESHEET = "images/units/animals/blackWolf.png";
	
	// The actual type.
	private static unitType wolfType =
			new animalType( "blackWolf",  // Name of unitType 
						 DEFAULT_WOLF_SPRITESHEET,
					     DEFAULT_WOLF_MOVESPEED, // Movespeed
					     DEFAULT_WOLF_JUMPSPEED // Jump speed
						);	
	
	
	//////////////
	/// FIELDS ///
	//////////////
	
	// Dark attacing
	private float darkAttackFor = 10f;
	
	///////////////
	/// METHODS ///
	///////////////
	// Constructor
	public blackWolf(int newX, int newY) {
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

	// Claw destroy
	@Override
	public void clawDestroy() {
		darkExplode b = new darkExplode(currClaw.getX() + currClaw.getWidth()/2 - darkExplode.getDefaultWidth()/2,
				currClaw.getY() + currClaw.getHeight()/2  - darkExplode.getDefaultHeight()/2,
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
		int spawnX = player.getCurrentPlayer().getX()+player.getCurrentPlayer().getWidth()/2;
		int spawnY = player.getCurrentPlayer().getY()+player.getCurrentPlayer().getHeight()/2;
		currClaw = new clawMarkBlack(spawnX - clawMarkBlack.DEFAULT_CHUNK_WIDTH/2, 
									 spawnY - clawMarkBlack.DEFAULT_CHUNK_HEIGHT/2,
									 0);
	}
}
