package units.unitTypes.spiderCave;

import UI.tooltipString;
import drawing.spriteSheet;
import drawing.spriteSheet.spriteSheetInfo;
import drawing.animation.animation;
import drawing.animation.animationPack;
import effects.projectiles.poisonBall;
import interactions.event;
import modes.mode;
import sounds.sound;
import units.player;
import units.unit;
import units.unitType;
import utilities.time;
import utilities.utility;

public class poisonSpider extends unit {
	
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
	private float DEFAULT_BACKSWING = 1.5f;
	private int DEFAULT_ATTACK_WIDTH = 300*2;
	private int DEFAULT_ATTACK_LENGTH = 300;
	static private float DEFAULT_CRIT_CHANCE = 0f;
	static private float DEFAULT_CRIT_DAMAGE = 0f;
	
	// Default exp given.
	private int DEFAULT_EXP_GIVEN = 25;
	
	// Health.
	private int DEFAULT_HP = 9;
	
	// Event for tooltip
	private static event projectileToolTipDisplayed;
	
	// Default movespeed.
	private static int DEFAULT_UNIT_MOVESPEED = 2;
	
	// Default jump speed
	private static int DEFAULT_UNIT_JUMPSPEED = 10;
	
	// Wolf sprite stuff.
	private static String DEFAULT_UNIT_SPRITESHEET = "images/units/animals/poisonSpider.png";
	
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
	
	// Damage
	public static int DEFAULT_PROJECTILE_DAMAGE = 3;
	
	// Aggrod?
	private boolean aggrod = false;
	
	// Wanders?
	private boolean wanders = true;
	
	// Follows?
	private boolean follows = true;
	
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
	public poisonSpider(int newX, int newY) {
		super(typeRef, newX, newY);
		
		// Set AI start X and Y
		startX = newX;
		startY = newY;
		
		// Set combat stuff.
		setCombatStuff();
		setAttackSound(spiderAttack);
		
		// Event load.
		projectileToolTipDisplayed = new event("Unit: projectileToolTipLoaded");
		
		// Deal with animations
		animationPack unitTypeAnimations = new animationPack();
		
		// Attacking left animation.
		animation attackingLeft = new animation("attackingLeft", typeRef.getUnitTypeSpriteSheet().getAnimation(1), 0, 3, DEFAULT_ATTACK_TIME);
		unitTypeAnimations.addAnimation(attackingLeft);
		
		// Attacking right animation.
		animation attackingRight = new animation("attackingRight", typeRef.getUnitTypeSpriteSheet().getAnimation(3), 0, 3, DEFAULT_ATTACK_TIME);
		unitTypeAnimations.addAnimation(attackingRight);
		
		// Attacking up animation.
		animation attackingUp = new animation("attackingUp", typeRef.getUnitTypeSpriteSheet().getAnimation(0), 0, 3, DEFAULT_ATTACK_TIME);
		unitTypeAnimations.addAnimation(attackingUp);
		
		// Attacking right animation.
		animation attackingDown = new animation("attackingDown", typeRef.getUnitTypeSpriteSheet().getAnimation(2), 0, 3, DEFAULT_ATTACK_TIME);
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
		
		// Wolf damage.
		setAttackDamage(DEFAULT_ATTACK_DAMAGE);
		setAttackTime(DEFAULT_ATTACK_TIME);
		setAttackFrameStart(2);
		setAttackFrameEnd(2);
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
			if(getIntX() < startX - patrolRadius) moveUnit("right");
			else if(getIntX() + getWidth() > startX + patrolRadius)  moveUnit("left");
			else if(getIntY() < startY - patrolRadius) moveUnit("down");
			else if(getIntY() + getHeight() > startY + patrolRadius) moveUnit("up");
			else moveUnit(direction);
	}
	
	// Shoot poison.
	public void shootPoison() {
		// Spawn the poison ball.
		poisonBall p = new poisonBall(getIntX()+getWidth()/2,getIntY()+getHeight()/2,
				player.getPlayer().getIntX()+player.getPlayer().getWidth()/2,
				player.getPlayer().getIntY()+player.getPlayer().getHeight()/2,
				DEFAULT_ATTACK_DAMAGE);
	}
	
	// Start attacking.
	@Override
	public void attack() {
		if(!isAttacking() && canAttack) {
			
			// Attack sound.
			if(getAttackSound()!=null) {
				sound s = new sound(getAttackSound());
				s.setPosition(getIntX(), getIntY(), sound.DEFAULT_SOUND_RADIUS);
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
			if(getCurrentAnimation().getCurrentSprite() >= getAttackFrameStart() &&
					   getCurrentAnimation().getCurrentSprite() <= getAttackFrameEnd()) {
				shootPoison();
				attackOver();
				setAttacking(false);
				canAttack = false;
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
		s.setPosition(getIntX(), getIntY(), sound.DEFAULT_SOUND_RADIUS);
		s.start();
	}

	// Move unit
	public void moveUnit() {
		
			// Basic movement.
			int moveX = 0;
			int moveY = 0;
			
			// Actual movement.
			if(isMovingLeft()) moveX -= moveSpeed;
			if(isMovingRight()) moveX += moveSpeed;
			
			// Only do these ones if we're in topDown mode.
			if(mode.getCurrentMode() == "topDown" || isStuck()) {
				if(isMovingUp()) moveY -= moveSpeed;
				if(isMovingDown()) moveY += moveSpeed;
			}
			
			// Deal with direction facing.
			if(isMovingLeft() && isMovingUp()) setFacingDirection("Left");
			else if(isMovingRight() && isMovingUp()) setFacingDirection("Right");
			else if(isMovingLeft() && isMovingDown()) setFacingDirection("Left");
			else if(isMovingRight() && isMovingDown()) setFacingDirection("Right");
			else if(isMovingDown() && (mode.getCurrentMode() != "platformer" || isStuck())) setFacingDirection("Down");
			else if(isMovingUp() && (mode.getCurrentMode() != "platformer" || isStuck())) setFacingDirection("Up");
			else if(isMovingRight()) setFacingDirection("Right");
			else if(isMovingLeft()) setFacingDirection("Left");
			
			// Actually move the unit.
			int oldX = getIntX();
			int oldY = getIntY();
			move(moveX, moveY);
			
			// Check if we aren't stuck anymore by doing this.
			if(getStuckOn() != null &&
			!isWithinRadius(getStuckOn().getIntX()+getStuckOn().getWidth()/2,getStuckOn().getIntY()+getStuckOn().getHeight()/2,getStuckOn().getWidth()/2)) {
				move(oldX - getIntX(), oldY - getIntY());
				stopMove("all");
			}
		//}
	}
	
	// wolf AI moves wolf around for now.
	public void updateUnit() {
		
		// If player is in radius, follow player, attacking.
		player currPlayer = player.getPlayer();
		int playerX = currPlayer.getIntX();
		int playerY = currPlayer.getIntY();
		float howClose = (float) Math.sqrt((playerX - getIntX())*(playerX - getIntX()) + (playerY - getIntY())*(playerY - getIntY()));
		
		// Attack if we're in radius.
		if(howClose < DEFAULT_ATTACK_RADIUS || aggrod) {
			
			// If we aggro
			if(aggrod && !projectileToolTipDisplayed.isCompleted()) {
				tooltipString t = new tooltipString("Time your attacks to reflect projectiles.");
				projectileToolTipDisplayed.setCompleted(true);
			}
			
			// If we're in attack range, attack.
			if(isInAttackRange(currPlayer, DEFAULT_ATTACK_DIFFERENTIAL)) {
				stopMove("all");
				attack();
				aggrod = true;
			}
			else {
				
				// Play aggrod sound on aggro.
				if(!aggrod) {
					sound s = new sound(spiderAggro);
					s.setPosition(getIntX(), getIntY(), sound.DEFAULT_SOUND_RADIUS);
					s.start();
				}
				
				// Follow current player, set to aggro
				aggrod = true;
				if(isFollows()) follow(currPlayer);
			}
		}
		else if(howClose > DEFAULT_DEAGGRO_RADIUS) {
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

	public boolean isFollows() {
		return follows;
	}

	public void setFollows(boolean follows) {
		this.follows = follows;
	}

}
