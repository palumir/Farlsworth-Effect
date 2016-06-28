package units.unitTypes.farmLand.spiderCave;

import java.util.Random;

import drawing.camera;
import drawing.spriteSheet;
import drawing.animation.animation;
import drawing.animation.animationPack;
import drawing.spriteSheet.spriteSheetInfo;
import effects.effect;
import effects.effectTypes.bloodSquirt;
import modes.mode;
import sounds.sound;
import units.animalType;
import units.humanType;
import units.player;
import units.unit;
import units.unitType;
import utilities.time;
import utilities.utility;
import zones.zone;

public class spider extends unit {
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Unit name
	public static String DEFAULT_UNIT_NAME = "spider";
	
	// Sprite.
	public static int DEFAULT_SPRITE_HEIGHT = 64;
	public static int DEFAULT_SPRITE_WIDTH = 64;
	
	// Platformer real dimensions
	public static int DEFAULT_PLATFORMER_HEIGHT = 32;
	public static int DEFAULT_PLATFORMER_WIDTH = 32;
	public static int DEFAULT_PLATFORMER_ADJUSTMENT_Y = 0;
	
	// TopDown real dimensions
	public static int DEFAULT_TOPDOWN_HEIGHT = 32;
	public static int DEFAULT_TOPDOWN_WIDTH = 32;
	public static int DEFAULT_TOPDOWN_ADJUSTMENT_Y = 0;
	
	// How close to attack?
	private int DEFAULT_ATTACK_RADIUS = 220;
	private int DEFAULT_DEAGGRO_RADIUS = 300;
	
	// Damage stats
	private int DEFAULT_ATTACK_DIFFERENTIAL = 7; // the range within the attackrange the unit will attack.
	private int DEFAULT_ATTACK_DAMAGE = 6;
	private float DEFAULT_BAT = 0.4f;
	private float DEFAULT_ATTACK_TIME = 0.5f;
	private float DEFAULT_BACKSWING = 0.5f;
	private int DEFAULT_ATTACK_WIDTH = 20;
	private int DEFAULT_ATTACK_LENGTH = 12;
	static private float DEFAULT_CRIT_CHANCE = .05f;
	static private float DEFAULT_CRIT_DAMAGE = 1.9f;
	
	// Default exp given.
	private int DEFAULT_EXP_GIVEN = 25;
	
	// Health.
	private int DEFAULT_HP = 25;
	
	// Default movespeed.
	private static int DEFAULT_UNIT_MOVESPEED = 1;
	
	// Default jump speed
	private static int DEFAULT_UNIT_JUMPSPEED = 10;
	
	// wolf sprite stuff.
	private static String DEFAULT_UNIT_SPRITESHEET = "images/units/animals/spider.png";
	
	// The actual type.
	private static unitType typeRef =
			new unitType(DEFAULT_UNIT_NAME,  // Name of unitType 
					new spriteSheet(new spriteSheetInfo(
							DEFAULT_UNIT_SPRITESHEET, 
							DEFAULT_SPRITE_WIDTH, 
							DEFAULT_SPRITE_HEIGHT,
							0,
							0
							)),
				     null,
				     DEFAULT_TOPDOWN_WIDTH,
				     DEFAULT_TOPDOWN_HEIGHT,
				     DEFAULT_UNIT_MOVESPEED, // Movespeed
				     DEFAULT_UNIT_JUMPSPEED // Jump speed
					);	
	
	// Sounds
	private static String spiderSound1 = "sounds/effects/animals/wolfBark1.wav";
	private static String spiderSound2 = "sounds/effects/animals/wolfBark2.wav";
	private static String unitAttack = "sounds/effects/player/combat/swingWeapon.wav";
	private int lastSpiderSound = 0;
	private int soundRadius = 1200;
	
	//////////////
	/// FIELDS ///
	//////////////
	private boolean aggrod = false;

	///////////////
	/// METHODS ///
	///////////////
	// Constructor
	public spider(int newX, int newY) {
		super(typeRef, newX, newY);
		
		//showAttackRange();
		// Set combat stuff.
		setCombatStuff();
		attackSound = unitAttack;
		
		
		// Deal with animations
		animationPack unitTypeAnimations = new animationPack();
		
		// Attacking left animation.
		animation attackingLeft = new animation("attackingLeft", typeRef.getUnitTypeSpriteSheet().getAnimation(1), 0, 4, DEFAULT_ATTACK_TIME);
		unitTypeAnimations.addAnimation(attackingLeft);
		
		// Attacking right animation.
		animation attackingRight = new animation("attackingRight", typeRef.getUnitTypeSpriteSheet().getAnimation(3), 0, 4, DEFAULT_ATTACK_TIME);
		unitTypeAnimations.addAnimation(attackingRight);
		
		// Attacking up animation.
		animation attackingUp = new animation("attackingUp", typeRef.getUnitTypeSpriteSheet().getAnimation(0), 0, 4, DEFAULT_ATTACK_TIME);
		unitTypeAnimations.addAnimation(attackingUp);
		
		// Attacking right animation.
		animation attackingDown = new animation("attackingDown", typeRef.getUnitTypeSpriteSheet().getAnimation(2), 0, 4, DEFAULT_ATTACK_TIME);
		unitTypeAnimations.addAnimation(attackingDown);
		
		// Jumping left animation.
		animation jumpingLeft = new animation("jumpingLeft", typeRef.getUnitTypeSpriteSheet().getAnimation(1), 2, 2, 1);
		unitTypeAnimations.addAnimation(jumpingLeft);
		
		// Jumping right animation.
		animation jumpingRight = new animation("jumpingRight", typeRef.getUnitTypeSpriteSheet().getAnimation(3), 2, 2, 1);
		unitTypeAnimations.addAnimation(jumpingRight);
		
		// Standing left animation.
		animation standingLeft = new animation("standingLeft", typeRef.getUnitTypeSpriteSheet().getAnimation(1), 0, 0, 1);
		unitTypeAnimations.addAnimation(standingLeft);
		
		// Standing right animation.
		animation standingRight = new animation("standingRight", typeRef.getUnitTypeSpriteSheet().getAnimation(3), 0, 0, 1);
		unitTypeAnimations.addAnimation(standingRight);
		
		// Running left animation.
		animation runningLeft = new animation("runningLeft", typeRef.getUnitTypeSpriteSheet().getAnimation(1), 4, 9, 1f);
		unitTypeAnimations.addAnimation(runningLeft);		
		
		// Running right animation.
		animation runningRight = new animation("runningRight", typeRef.getUnitTypeSpriteSheet().getAnimation(3), 4, 9, 1f);
		unitTypeAnimations.addAnimation(runningRight);
		
		// Standing up animation.
		animation standingUp = new animation("standingUp", typeRef.getUnitTypeSpriteSheet().getAnimation(0), 0, 0, 1);
		unitTypeAnimations.addAnimation(standingUp);
		
		// Standing down animation.
		animation standingDown = new animation("standingDown", typeRef.getUnitTypeSpriteSheet().getAnimation(2), 0, 0, 1);
		unitTypeAnimations.addAnimation(standingDown);
		
		// Running up animation.
		animation runningUp = new animation("runningUp", typeRef.getUnitTypeSpriteSheet().getAnimation(0), 4, 9, 1f);
		unitTypeAnimations.addAnimation(runningUp);
		
		// Running down animation.
		animation runningDown = new animation("runningDown", typeRef.getUnitTypeSpriteSheet().getAnimation(2), 4, 9, 1f);
		unitTypeAnimations.addAnimation(runningDown);
		
		// Set animations.
		setAnimations(unitTypeAnimations);

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
		
		// Set exp given.
		exp = DEFAULT_EXP_GIVEN;
		
		// Wolf damage.
		setAttackDamage(DEFAULT_ATTACK_DAMAGE);
		setAttackTime(DEFAULT_ATTACK_TIME);
		setBaseAttackTime(DEFAULT_BAT);
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
		lastSpiderSound = 1;
		sound s = new sound(spiderSound1);
		s.setPosition(getX(), getY(), soundRadius);
		s.start();
	}
	
	// Wolf random noises
	public void makeSounds() {
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
		if(howClose < DEFAULT_ATTACK_RADIUS) {
			
			// If we're in attack range, attack.
			if(isInAttackRange(currPlayer, DEFAULT_ATTACK_DIFFERENTIAL)) {
				stopMove("all");
				attack();
			}
			else {
				if(!aggrod) {
					sound s = new sound(spiderSound2);
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
}
