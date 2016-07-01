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
	public static int DEFAULT_PLATFORMER_HEIGHT = 25;
	public static int DEFAULT_PLATFORMER_WIDTH = 25;
	public static int DEFAULT_PLATFORMER_ADJUSTMENT_Y = 0;
	
	// TopDown real dimensions
	public static int DEFAULT_TOPDOWN_HEIGHT = 25;
	public static int DEFAULT_TOPDOWN_WIDTH = 25;
	public static int DEFAULT_TOPDOWN_ADJUSTMENT_Y = 0;
	
	// Default patrol radius
	private int DEFAULT_PATROL_RADIUS = 10;
	
	// How close to attack?
	private int DEFAULT_ATTACK_RADIUS = 220;
	private int DEFAULT_DEAGGRO_RADIUS = 350;
	
	// Damage stats
	private int DEFAULT_ATTACK_DIFFERENTIAL = 7; // the range within the attackrange the unit will attack.
	private int DEFAULT_ATTACK_DAMAGE = 6;
	private float DEFAULT_BAT = 0.4f;
	private float DEFAULT_ATTACK_TIME = 0.5f;
	private float DEFAULT_BACKSWING = 0.5f;
	private int DEFAULT_ATTACK_WIDTH = 20;
	private int DEFAULT_ATTACK_LENGTH = 12;
	static private float DEFAULT_CRIT_CHANCE = .15f;
	static private float DEFAULT_CRIT_DAMAGE = 1.9f;
	
	// AI movement.
	private long AILastCheck = 0l; // milliseconds
	private float randomMove = 1f; // seconds
	private float randomStop = 0.5f;
	private int startX = 0;
	private int startY = 0;
	private int patrolRadius = DEFAULT_PATROL_RADIUS;
	
	// Default exp given.
	private int DEFAULT_EXP_GIVEN = 25;
	
	// Health.
	private int DEFAULT_HP = 55;
	
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
	private static String spiderHurt = "sounds/effects/animals/spider1.wav";
	private static String spiderAttack = "sounds/effects/animals/spider2.wav";
	private static String spiderAggro= "sounds/effects/animals/spider3.wav";
	private int soundRadius = 1200;
	
	//////////////
	/// FIELDS ///
	//////////////
	
	// Aggrod?
	private boolean aggrod = false;
	
	// Wanders?
	private boolean wanders = true;

	///////////////
	/// METHODS ///
	///////////////
	// Constructor
	public spider(int newX, int newY) {
		super(typeRef, newX, newY);
		
		//showAttackRange();
		// Set combat stuff.
		setCombatStuff();
		attackSound = spiderAttack;
		
		
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
		
		// Set facing direction
		this.setFacingDirection("random");
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
	
	// Make sure the movement is within a certain radius.
	public void checkMovement(String direction) {
		if(getX() < startX - patrolRadius) moveUnit("right");
		else if(getX() + getWidth() > startX + patrolRadius)  moveUnit("left");
		else if(getY() < startY - patrolRadius) moveUnit("down");
		else if(getY() + getHeight() > startY + patrolRadius) moveUnit("up");
		else moveUnit(direction);
	}
	
	// React to pain.
	public void reactToPain() {
		// Play a bark on pain.
		sound s = new sound(spiderHurt);
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
		if(howClose < DEFAULT_ATTACK_RADIUS || aggrod) {
			
			// If we're in attack range, attack.
			if(isInAttackRange(currPlayer, DEFAULT_ATTACK_DIFFERENTIAL)) {
				stopMove("all");
				attack();
				aggrod = true;
			}
			else {
				if(!aggrod) {
					sound s = new sound(spiderAggro);
					s.setPosition(getX(), getY(), soundRadius);
					s.start();
				}
				aggrod = true;
				follow(currPlayer);
			}
		}
		
		// Deagro
		if(howClose > DEFAULT_DEAGGRO_RADIUS) {
			aggrod = false;
		}
		
		// Do movement if we're not aggrod.
		if(!aggrod && isWanders()) {
			// Move in a random direction every interval.
			if(time.getTime() - AILastCheck > randomMove*1000) {
				AILastCheck = time.getTime();
				int random = utility.RNG.nextInt(4);
				if(random==0) checkMovement("left");
				if(random==1) checkMovement("right");
				if(random==2) checkMovement("down");
				if(random==3) checkMovement("up");
				randomStop = 0.35f + utility.RNG.nextInt(4)*0.125f;
			}
			
			// Stop after a fraction of a second
			if(isMoving() && time.getTime() - AILastCheck > randomStop*1000) {
				randomMove = 2.4f + utility.RNG.nextInt(9)*0.2f;
				AILastCheck = time.getTime();
				stopMove("all");
			}
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

	public boolean isWanders() {
		return wanders;
	}

	public void setWanders(boolean wanders) {
		this.wanders = wanders;
	}
}
