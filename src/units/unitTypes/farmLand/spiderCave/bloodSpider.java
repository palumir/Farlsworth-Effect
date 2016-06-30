package units.unitTypes.farmLand.spiderCave;

import java.util.Random;

import drawing.camera;
import drawing.spriteSheet;
import drawing.animation.animation;
import drawing.animation.animationPack;
import drawing.spriteSheet.spriteSheetInfo;
import effects.effect;
import effects.effectTypes.bloodBall;
import effects.effectTypes.bloodSquirt;
import effects.effectTypes.poisonBall;
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

public class bloodSpider extends unit {
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Unit name
	public static String DEFAULT_UNIT_NAME = "spittingSpider";
	
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
	
	// How close to attack?
	private int DEFAULT_ATTACK_RADIUS = 250;
	private int DEFAULT_DEAGGRO_RADIUS = 350;
	
	// Damage stats
	private int DEFAULT_ATTACK_DIFFERENTIAL = 7; // the range within the attackrange the unit will attack.
	private int DEFAULT_ATTACK_DAMAGE = 6;
	private float DEFAULT_BAT = 0.2f;
	private float DEFAULT_ATTACK_TIME = 0.2f;
	private float DEFAULT_BACKSWING = 1f;
	private int DEFAULT_ATTACK_WIDTH = 300*2;
	private int DEFAULT_ATTACK_LENGTH = 300;
	static private float DEFAULT_CRIT_CHANCE = 0f;
	static private float DEFAULT_CRIT_DAMAGE = 0f;
	
	// Default exp given.
	private int DEFAULT_EXP_GIVEN = 25;
	
	// Health.
	private int DEFAULT_HP = 45;
	
	// Default movespeed.
	private static int DEFAULT_UNIT_MOVESPEED = 2;
	
	// Default jump speed
	private static int DEFAULT_UNIT_JUMPSPEED = 10;
	
	// Wolf sprite stuff.
	private static String DEFAULT_UNIT_SPRITESHEET = "images/units/animals/bloodSpider.png";
	
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
	private static String spiderAggro = "sounds/effects/animals/spider3.wav";
	
	// Default patrol radius
	private int DEFAULT_PATROL_RADIUS = 10;
	
	//////////////
	/// FIELDS ///
	//////////////
	private boolean aggrod = false;
	
	// AI movement.
	private long AILastCheck = 0l; // milliseconds
	private float randomMove = 1f; // seconds
	private float randomStop = 0.5f;
	private int startX = 0;
	private int startY = 0;
	private int patrolRadius = DEFAULT_PATROL_RADIUS;

	///////////////
	/// METHODS ///
	///////////////
	// Constructor
	public bloodSpider(int newX, int newY) {
		super(typeRef, newX, newY);
		
		// Set AI start X and Y
		startX = newX;
		startY = newY;
		
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
	
	// Shoot poison.
	public void shootBlood() {
		// Create poison and move to player location
		bloodBall b = new bloodBall(getX()+getWidth()/2,getY()+getHeight()/2,
				player.getCurrentPlayer().getX()+player.getCurrentPlayer().getWidth()/2,
				player.getCurrentPlayer().getY()+player.getCurrentPlayer().getHeight()/2);
	}
	
	// Start attacking.
	@Override
	public void attack() {
		if(!isAttacking() && canAttack) {
			
			// Attack sound.
			if(attackSound!=null) {
				sound s = new sound(attackSound);
				s.setPosition(getX(), getY(), sound.DEFAULT_SOUND_RADIUS);
				s.start();
			}
			setAttacking(true);
			startAttackTime = time.getTime();
		}
	}
	
	// Do combat mechanics.
	@Override
	public void combat() {
		
		// Attack if we are attacking.
		if(isAttacking()) {
			// Do the attack if our BAT is over.
			if(time.getTime() - startAttackTime > baseAttackTime*1000) {
				shootBlood();
				attackOver();
			}
			if(time.getTime() - startAttackTime > (getAttackTime())*1000) {
			}
		}
		else if(time.getTime() - startAttackTime > (getAttackTime() + backSwing)*1000) {
			canAttack = true;
		}
	}
	
	// React to pain.
	public void reactToPain() {
		// Play a bark on pain.
		sound s = new sound(spiderHurt);
		s.setPosition(getX(), getY(), sound.DEFAULT_SOUND_RADIUS);
		s.start();
	}

	// Move unit
	public void moveUnit() {
		
			// Basic movement.
			int moveX = 0;
			int moveY = 0;
			
			// Actual movement.
			if(movingLeft) moveX -= moveSpeed;
			if(movingRight) moveX += moveSpeed;
			
			// Only do these ones if we're in topDown mode.
			if(mode.getCurrentMode() == "topDown" || isStuck()) {
				if(movingUp) moveY -= moveSpeed;
				if(movingDown) moveY += moveSpeed;
			}
			
			// Deal with direction facing.
			if(movingLeft && movingUp) setFacingDirection("Left");
			else if(movingRight && movingUp) setFacingDirection("Right");
			else if(movingLeft && movingDown) setFacingDirection("Left");
			else if(movingRight && movingDown) setFacingDirection("Right");
			else if(movingDown && (mode.getCurrentMode() != "platformer" || isStuck())) setFacingDirection("Down");
			else if(movingUp && (mode.getCurrentMode() != "platformer" || isStuck())) setFacingDirection("Up");
			else if(movingRight) setFacingDirection("Right");
			else if(movingLeft) setFacingDirection("Left");
			
			// Actually move the unit.
			int oldX = getX();
			int oldY = getY();
			move(moveX, moveY);
			
			// Check if we aren't stuck anymore by doing this.
			if(getStuckOn() != null &&
			!isWithinRadius(getStuckOn().getX()+getStuckOn().getWidth()/2,getStuckOn().getY()+getStuckOn().getHeight()/2,getStuckOn().getWidth()/2)) {
				move(oldX - getX(), oldY - getY());
				stopMove("all");
			}
		//}
	}
	
	// wolf AI moves wolf around for now.
	public void updateUnit() {
		
		// If player is in radius, follow player, attacking.
		player currPlayer = player.getCurrentPlayer();
		int playerX = currPlayer.getX();
		int playerY = currPlayer.getY();
		float howClose = (float) Math.sqrt((playerX - getX())*(playerX - getX()) + (playerY - getY())*(playerY - getY()));
		
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
					s.setPosition(getX(), getY(), sound.DEFAULT_SOUND_RADIUS);
					s.start();
				}
				aggrod = true;
				follow(currPlayer);
			}
		}
		else if(howClose > DEFAULT_DEAGGRO_RADIUS) {
			aggrod = false;
		}
		
		// Do movement if we're not aggrod.
		if(!aggrod) {
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

}
