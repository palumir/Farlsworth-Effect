package units.unitTypes.farmLand.sheepFarm;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

import drawing.camera;
import drawing.drawnObject;
import drawing.gameCanvas;
import drawing.animation.animation;
import effects.effect;
import effects.effectTypes.bloodSquirt;
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

public class wolfOld extends unit {
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Platformer real dimensions
	public static int DEFAULT_PLATFORMER_HEIGHT = 32;
	public static int DEFAULT_PLATFORMER_WIDTH = 32;
	public static int DEFAULT_PLATFORMER_ADJUSTMENT_Y = 0;
	
	// TopDown real dimensions
	public static int DEFAULT_TOPDOWN_HEIGHT = 18;
	public static int DEFAULT_TOPDOWN_WIDTH = 30;
	public static int DEFAULT_TOPDOWN_ADJUSTMENT_Y = 5;
	
	// How close to attack?
	private int DEFAULT_ATTACK_RADIUS = 220;
	private int DEFAULT_DEAGGRO_RADIUS = 300;
	
	// Damage stats
	private int DEFAULT_ATTACK_DIFFERENTIAL = 7; // the range within the attackrange the unit will attack.
	private int DEFAULT_ATTACK_DAMAGE = 1;
	private float DEFAULT_ATTACK_TIME = 1f;
	private float DEFAULT_BACKSWING = 0.3f;
	private int DEFAULT_ATTACK_WIDTH = 30;
	private int DEFAULT_ATTACK_LENGTH = 15;
	static private float DEFAULT_CRIT_CHANCE = .15f;
	static private float DEFAULT_CRIT_DAMAGE = 1.6f;
	
	// Dosile?
	private boolean dosile = false;
	
	// Health.
	private int DEFAULT_HP = 10;
	
	// Default movespeed.
	private static int DEFAULT_WOLF_MOVESPEED = 2;
	
	// Default jump speed
	private static int DEFAULT_WOLF_JUMPSPEED = 10;
	
	// wolf sprite stuff.
	private static String DEFAULT_WOLF_SPRITESHEET = "images/units/animals/yellowWolf.png";
	
	// The actual type.
	private static unitType wolfType =
			new animalType( "wolf",  // Name of unitType 
						 DEFAULT_WOLF_SPRITESHEET,
					     DEFAULT_WOLF_MOVESPEED, // Movespeed
					     DEFAULT_WOLF_JUMPSPEED // Jump speed
						);	
	
	// Sounds
	private static String howl = "sounds/effects/animals/wolfHowl.wav";
	private static String growl = "sounds/effects/animals/wolfGrowl.wav";
	private static String bark1 = "sounds/effects/animals/wolfBark1.wav";
	private static String bark2 = "sounds/effects/animals/wolfBark2.wav";
	private static String wolfAttack = "sounds/effects/player/combat/swingWeapon.wav";
	private int lastBarkSound = 0;
	private static long lastHowl = 0;
	private float randomHowl = 0;
	private int soundRadius = 1200;
	
	//////////////
	/// FIELDS ///
	//////////////
	private boolean aggrod = false;

	///////////////
	/// METHODS ///
	///////////////
	// Constructor
	public wolfOld(int newX, int newY) {
		super(wolfType, newX, newY);
		
		// Set wolf combat stuff.
		setCombatStuff();
		attackSound = wolfAttack;
		
		// Add attack animations.
		// Attacking left animation.
		animation attackingLeft = new animation("attackingLeft", wolfType.getUnitTypeSpriteSheet().getAnimation(5), 0, 5, DEFAULT_ATTACK_TIME);
		getAnimations().addAnimation(attackingLeft);
		
		// Attacking left animation.
		animation attackingRight = new animation("attackingRight", wolfType.getUnitTypeSpriteSheet().getAnimation(6), 0, 5, DEFAULT_ATTACK_TIME);
		getAnimations().addAnimation(attackingRight);
		
		// Attacking left animation.
		animation attackingUp = new animation("attackingUp", wolfType.getUnitTypeSpriteSheet().getAnimation(7), 0, 5, DEFAULT_ATTACK_TIME);
		getAnimations().addAnimation(attackingUp);
		
		// Attacking left animation.
		animation attackingDown = new animation("attackingDown", wolfType.getUnitTypeSpriteSheet().getAnimation(4), 0, 5, DEFAULT_ATTACK_TIME);
		getAnimations().addAnimation(attackingDown);
		
		// Set dimensions
		setHeight(getDefaultHeight());
		setWidth(getDefaultWidth());
		platformerHeight = DEFAULT_PLATFORMER_HEIGHT;
		platformerWidth = DEFAULT_PLATFORMER_WIDTH;
		topDownHeight = DEFAULT_TOPDOWN_HEIGHT;
		topDownWidth = DEFAULT_TOPDOWN_WIDTH;
		setHitBoxAdjustmentY(getDefaultHitBoxAdjustmentY());
	}
	
	// Combat defaults.
	public void setCombatStuff() {
		// Set to be attackable.
		this.setKillable(true);
		
		// Wolf damage.
		setAttackDamage(DEFAULT_ATTACK_DAMAGE);
		setAttackTime(DEFAULT_ATTACK_TIME);
		setAttackFrameStart(4);
		setAttackFrameEnd(4);
		setAttackWidth(DEFAULT_ATTACK_WIDTH);
		setAttackLength(DEFAULT_ATTACK_LENGTH);
		setBackSwing(DEFAULT_BACKSWING);
		setCritChance(DEFAULT_CRIT_CHANCE);
		setCritDamage(DEFAULT_CRIT_DAMAGE);
		
		// HP
		setMaxHealthPoints(DEFAULT_HP);
		setHealthPoints(DEFAULT_HP);
		
	}
	
	// React to pain.
	public void reactToPain() {
		// Play a bark on pain.
		if(lastBarkSound == 0) {
			lastBarkSound = 1;
			sound s = new sound(bark1);
			s.setPosition(getX(), getY(), soundRadius);
			s.start();
		}
		else if(lastBarkSound == 1) {
			lastBarkSound = 0;
			sound s = new sound(bark2);
			s.setPosition(getX(), getY(), soundRadius);
			s.start();
		}
	}
	
	// Wolf random noises
	public void makeSounds() {
		
			// Create a new random growl interval
			float newRandomHowlInterval = 10f + utility.RNG.nextInt(10);
			
			// Make the wolf howl
			if(randomHowl == 0f) {
				randomHowl = newRandomHowlInterval;
			}
			if(!dosile && !aggrod && time.getTime() - lastHowl > randomHowl*1000) {
				
				// Set the last time they howled
				lastHowl = time.getTime();
				randomHowl = newRandomHowlInterval;
				sound s = new sound(howl);
				s.setPosition(getX(), getY(), soundRadius);
				s.start();
			}
	}
	
	// wolf AI moves wolf around for now.
	public void updateUnit() {
		
		// If player is in radius, follow player, attacking.
		player currPlayer = player.getCurrentPlayer();
		int playerX = currPlayer.getX();
		int playerY = currPlayer.getY();
		float howClose = (float) Math.sqrt((playerX - getX())*(playerX - getX()) + (playerY - getY())*(playerY - getY()));
		
		// Make sounds.
		makeSounds();
		
		// Attack if we're in radius.
		if(!isDosile() && howClose < DEFAULT_ATTACK_RADIUS) {
			
			// If we're in attack range, attack.
			if(isInAttackRange(currPlayer, DEFAULT_ATTACK_DIFFERENTIAL)) {
				stopMove("all");
				attack();
			}
			else {
				if(!aggrod) {
					sound s = new sound(growl);
					s.setPosition(getX(), getY(), soundRadius);
					s.start();
				}
				aggrod = true;
				follow(currPlayer);
			}
		}
		else if(aggrod && howClose > DEFAULT_DEAGGRO_RADIUS) {
			stopMove("all");
		}
		
		// Even dosile wolves attack if provoked.
		if(dosile && isInAttackRange(currPlayer, DEFAULT_ATTACK_DIFFERENTIAL)) {
			attack();
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
	
	public boolean isDosile() {
		return dosile;
	}

	public void setDosile(boolean dosile) {
		this.dosile = dosile;
	}
}
