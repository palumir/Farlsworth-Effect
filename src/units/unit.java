package units;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import doodads.general.lightSource;
import doodads.general.questMark;
import doodads.sheepFarm.clawMarkYellow;
import drawing.camera;
import drawing.drawnObject;
import drawing.gameCanvas;
import drawing.spriteSheet;
import drawing.animation.animation;
import drawing.animation.animationPack;
import drawing.userInterface.playerHealthBar;
import effects.effect;
import effects.projectile;
import effects.buffs.movementBuff;
import effects.effectTypes.bloodSquirt;
import effects.effectTypes.critBloodSquirt;
import effects.effectTypes.floatingString;
import modes.mode;
import sounds.sound;
import terrain.chunk;
import terrain.region;
import units.bosses.denmother;
import units.bosses.farlsworth;
import units.unitTypes.farmLand.sheepFarm.yellowWolf;
import utilities.intTuple;
import utilities.mathUtils;
import utilities.stringUtils;
import utilities.time;
import utilities.utility;

public abstract class unit extends drawnObject  { 
	
	/////////////////////////
	////// DEFAULTS /////////
	/////////////////////////
	
	// List of all units
	private static ArrayList<unit> allUnits;
	
	// Default movespeed.
	private static int DEFAULT_UNIT_MOVESPEED = 1;
	public static int DEFAULT_MAX_MOVESPEED = 10;
	
	// Gravity defaults.
	private static boolean DEFAULT_GRAVITY_STATE = false;
	private static float DEFAULT_GRAVITY_ACCELERATION = 0.455f;
	private static float DEFAULT_GRAVITY_MAX_VELOCITY = 20;
	protected static float DEFAULT_JUMPSPEED = 11f;
	
	// Animation defaults.
	private String DEFAULT_FACING_DIRECTION = "Right";
	
	// List of units we are in combat with
	private ArrayList<unit> inCombatWith = new ArrayList<unit>();
	
	// Unit defaults.
	protected static int DEFAULT_ATTACK_DAMAGE = 1;
	protected static float DEFAULT_ATTACK_TIME = 0.44f;
	protected static int DEFAULT_ATTACK_WIDTH = 35;
	protected static int DEFAULT_ATTACK_LENGTH = 11;
	protected static float DEFAULT_BACKSWING = 0.1f;
	protected static float DEFAULT_CRIT_CHANCE = 0f;
	protected float DEFAULT_CRIT_DAMAGE = 4f;
	protected static float DEFAULT_ATTACK_VARIABILITY = 0f; // How much the range of hits is. 5% both ways.
	
	// Combat defaults.
	protected static int DEFAULT_HP = 10;
	protected boolean showAttackRange = false;
	
	// Default healthbarsize
	protected int DEFAULT_HEALTHBAR_HEIGHT = 6;
	protected int DEFAULT_HEALTHBAR_WIDTH = 40;
	
	// Colors for combat.
	protected Color DEFAULT_DAMAGE_COLOR = Color.white;
	protected Color DEFAULT_CRIT_COLOR = Color.yellow;
	protected Color DEFAULT_HEAL_COLOR = Color.green;
	
	// Sounds
	protected static int DEFAULT_ATTACK_SOUND_RADIUS = 1000;
	
	///////////////
	/// GLOBALS ///
	///////////////
	private static boolean gravity = DEFAULT_GRAVITY_STATE;

	////////////////
	//// FIELDS ////
	////////////////
	
	// The actual unit type.
	private unitType typeOfUnit;
	
	// Width and height for topDown and platformer
	protected int topDownWidth = 0;
	protected int topDownHeight = 0;
	protected int platformerWidth = 0;
	protected int platformerHeight = 0;
	
	// Are we stuck in something sticky?
	private boolean stuck = false;
	
	// Check if something is stuck in pathfinding
	protected boolean pathFindingStuck = false;
	
	// Combat
	// Has the unit
	protected boolean unitIsDead = false;
	protected long unitDiedAt = 0;
	protected float deathAnimationLasts = 1f;
	
	// Health points
	protected int maxHealthPoints = DEFAULT_HP;
	protected int healthPoints = DEFAULT_HP;
	
	// Damage
	private int attackDamage = DEFAULT_ATTACK_DAMAGE;
	
	// Attack time
	private int attackFrameStart = 0;
	private int attackFrameEnd = 0;
	private float attackTime = DEFAULT_ATTACK_TIME;
	protected float backSwing = DEFAULT_BACKSWING;
	
	// Attack range.
	private int attackWidth = DEFAULT_ATTACK_WIDTH;
	private int attackLength = DEFAULT_ATTACK_LENGTH;
	
	// Unit stats.
	protected float attackMultiplier = 1f;
	private float attackVariability = DEFAULT_ATTACK_VARIABILITY; // Percentage
	private float critChance = DEFAULT_CRIT_CHANCE;
	private float critDamage = DEFAULT_CRIT_DAMAGE;
	
	// Attacking/getting attacked mechanics
	protected boolean canAttack = true; // backswing stuff.
	private boolean killable = false;
	private boolean targetable = true;
	private boolean attacking = false;
	private boolean alreadyAttacked = false;
	protected double startAttackTime = 0;
	
	// Combat sounds
	private String attackSound;
	
	// Gravity
	protected float jumpSpeed = DEFAULT_JUMPSPEED;
	private float fallSpeed = 0;
	protected boolean jumping = false;
	private boolean tryJump = false;
	private boolean touchingGround = false;
	private boolean inAir = true;
	
	// Movement buffs/debuffs
	private ArrayList<movementBuff> movementBuffs = new ArrayList<movementBuff>();
	
	// Movement
	protected float moveSpeed = DEFAULT_UNIT_MOVESPEED;
	protected float oldMoveSpeed = moveSpeed;
	protected float baseMoveSpeed = DEFAULT_UNIT_MOVESPEED;
	protected float baseMoveDuration = 0.75f;
	private boolean movingLeft = false;
	private boolean movingRight = false;
	private boolean movingDown = false;
	public boolean movingUp = false;
	protected String facingDirection = DEFAULT_FACING_DIRECTION;
	protected boolean collisionOn = true;
	
	// Quests
	private chunk questIcon = null;
	
	// Sprite stuff.
	private animationPack animations;
	private animation currentAnimation = null;
	
	// Followed unit
	protected unit followedUnit = null;
	protected boolean followingUnit = false;
	
	// Where are we moving to?
	protected int moveToX = 0;
	protected int moveToY = 0;
	
	// Moving to a point?
	protected boolean movingToAPoint = false;
	
	// Following a path?
	private boolean followingAPath = false;
	
	// Units in attack range.
	protected ArrayList<unit> unitsInAttackRange;
	
	// Path to follow
	protected ArrayList<intTuple> path;
	
	// Next point.
	private intTuple currPoint;
	
	// Knockbacks
	private long knockBackStart = 0;
	private boolean gettingKnockedBack = false;
	private int knockToX;
	private int knockToY;
	private int knockSpeed;
	private float knockTime;
	
	// Patrol stuff
	boolean patrolling = false;
	boolean movingBack = false;
	int patrolX;
	int patrolY;
	int startX;
	int startY;
	boolean patrollingPath = false;
	ArrayList<intTuple> patrolPath;
	
	// No collision knockback
	boolean oldCollision = collisionOn;
	
	// Shielding?
	private boolean shielding = false;
	
	// What we are stuck on?
	private drawnObject stuckOn = null;
	
	// If unit is locked, prohibit movement.
	private boolean unitLocked = false;
	
	///////////////
	/// METHODS ///
	///////////////

	// Constructor
	public unit(unitType u, int newX, int newY) {
		super(u.getUnitTypeSpriteSheet(), newX, newY, u.getWidth(), u.getHeight());	
		if(u.getAnimations()!=null) setAnimations(new animationPack(u.getAnimations()));
		moveSpeed = u.getMoveSpeed();
		baseMoveSpeed = u.getMoveSpeed();
		jumpSpeed = u.getJumpSpeed();
		setTypeOfUnit(u);
		
		// Add to list
		getAllUnits().add(this);
	}
	
	// Update unit
	@Override
	public void update() {
		
		if(!unitIsDead) {
			gravity();
			jump();
			moveUnit();
			dealWithMetaMovement();
			combat();
			aliveOrDead();
		}
		updateUnit();
		if(getCurrentAnimation() != null) getCurrentAnimation().playAnimation();
	}
	
	// Check if united is illuminated
	public boolean isIlluminated() {
		for(int i = 0; i < lightSource.lightSources.size(); i++) {
			lightSource l = lightSource.lightSources.get(i);
			if(this.isWithinRadius(l.getIntX() + l.getWidth()/2, l.getIntY() + l.getHeight()/2, l.getLightRadius())) {
				return true;
			}
		}
		return false;
	}
	
	// Move in place.
	public void moveInPlace() {
		oldMoveSpeed = moveSpeed;
		moveSpeed = 0.001f;
	}
	
	// Face toward player
	public void faceTowardPlayer() {
		int angle = this.getAngleBetween(player.getPlayer());
		if(angle >= 45 && angle <= 45+90) {
			this.facingDirection = "Right";
		}
		else if(angle >= 45+90 && angle <= 45+90+90) {
			this.facingDirection = "Down";
		}
		else if(angle >= 45+90+90 && angle <= 45+90+90+90) {
			this.facingDirection = "Left";
		}
		else {
			this.facingDirection = "Up";
		}
	}
	
	// Move in place.
	public void stopMoveInPlace() {
		moveSpeed = oldMoveSpeed;
	}
	
	// Deal with patrolling
	public void dealWithPatrolling() {
		
		// If we are patrolling, patrol
		if(patrolling && !patrollingPath) {
			if(!movingBack) {
				if(Math.abs(startX - getIntX()) <= moveSpeed + 1 && Math.abs(startY - getIntY()) <= moveSpeed + 1) {
					moveTo(patrolX, patrolY);
					movingBack = true;
				}
			}
			else {
				if(Math.abs(patrolX - getIntX()) <= moveSpeed + 1 && Math.abs(patrolY - getIntY()) <= moveSpeed + 1) {
					moveTo(startX, startY);
					movingBack = false;
				}
			}
		}
		
		// If we are patrolling a path.
		if(patrolling && patrollingPath ) {
			if(path == null || path.size() == 0) {
				if(patrolPath != null) {
					ArrayList <intTuple> pathToPatrol = new ArrayList<intTuple>(patrolPath);
					followPath(pathToPatrol);
				}
			}
		}
	}
	
	// Patrol to
	public void patrolTo(int patrolToX, int patrolToY) {
		patrolling = true;
		patrolX = patrolToX;
		patrolY = patrolToY;
		startX = this.getIntX();
		startY = this.getIntY();
	}
	
	// Patrol path
	public void patrolPath(ArrayList<intTuple> p) {
		patrolling = true;
		patrollingPath = true;
		startX = this.getIntX();
		startY = this.getIntY();
		p.add(new intTuple(startX, startY));
		patrolPath = p;
	}
	
	// Stop patrolling
	public void stopPatrol() {
		patrolling = false;
		patrollingPath = false;
	}
	
	// Is the unit alive or dead
	public void aliveOrDead() {

		
		// Kill unit if we're dead.
		if(healthPoints <= 0) {
			die();
		}
	}
	
	// Destroy all units
	public static void destroyAll() {
		for(int i = 0; i < allUnits.size(); i++) {
			allUnits.get(0).destroy();
			allUnits.remove(0);
		}
	}
	
	// Respond to destroy
	@Override
	public void respondToDestroy() {
		for(; 0 < getInCombatWith().size();) {
			exitCombatWith(getInCombatWith().get(0));
		}
	}
	
	// Enter combat with this unit
	public void enterCombatWith(unit u) {
		if(!getInCombatWith().contains(u)) {
			getInCombatWith().add(u);
		}
		if(!u.getInCombatWith().contains(this)) {
			u.getInCombatWith().add(this);
		}
	}
	
	// Exit combat
	public void exitCombatWith(unit u) {
		if(u.getInCombatWith().contains(this)){
			for(int i = 0; i < u.getInCombatWith().size(); i++) {
				if(u.getInCombatWith().get(i).equals(this)) {
					u.getInCombatWith().remove(i);
					break;
				}
			}
		}
		if(getInCombatWith().contains(u)) {
			for(int i = 0; i < getInCombatWith().size(); i++) {
				if(getInCombatWith().get(i).equals(u)) {
					getInCombatWith().remove(i);
					break;
				}
			}
		}
	}
	
	// Kill unit
	public void die() {
		
		// Remove from game.
		destroy();
		
		if(!(this instanceof player)) {
			// Do stuff for player?
		}
		
		// Do a huge blood squirt.
		effect blood = new critBloodSquirt(getIntX() - critBloodSquirt.getDefaultWidth()/2 + topDownWidth/2,
				   getIntY() - critBloodSquirt.getDefaultHeight()/2);
		
		// React to death.
		reactToDeath();
	}
	
	// React to death.
	public void reactToDeath() {
	}
	
	// Heal unit.
	public void heal(int i) {
		if(healthPoints + i > maxHealthPoints) {
			i = maxHealthPoints - healthPoints;
			healthPoints = maxHealthPoints;
		}
		else healthPoints = healthPoints + i;
		effect e = new floatingString("+" + i, DEFAULT_HEAL_COLOR, getIntX() + getWidth()/2, getIntY() + getHeight()/2, 1f);
	}
	
	// Require units to have some sort of AI.
	public abstract void updateUnit();
	
	// Set gravity on or off.
	public static void setGravity(boolean b) {
		gravity = b;
	}
	
	// Provide gravity
	public void gravity() {
		if(gravity && !isStuck()) {
			
			// Accelerate
			if(fallSpeed < DEFAULT_GRAVITY_MAX_VELOCITY){
				fallSpeed += DEFAULT_GRAVITY_ACCELERATION;
			}
			
			move(0,(int)fallSpeed);
		}
	}
	
	// Knockback.
	public void knockBackNoCollision(int knockX, int knockY, int knockRadius, float overTime, int knockSpeed) {
		if(!gettingKnockedBack) {
			oldCollision = collisionOn;
			setCollisionOn(false);
			setUnitLocked(true);
			knockBackStart = time.getTime();
			gettingKnockedBack = true;
			this.knockTime = overTime;	
			this.knockSpeed = knockSpeed;
			
			// Calculate the new X and Y we need to knock them to, based off radius.
			double currentDegree = mathUtils.angleBetweenTwoPointsWithFixedPoint(getIntX()+getWidth()/2, getIntY() + getHeight()/2, knockX, knockY, knockX, knockY);
			knockToX = (int) (getIntX() + (knockSpeed*overTime*1000)*Math.cos(Math.toRadians(currentDegree))); 
			knockToY = (int) (getIntY() + (knockSpeed*overTime*1000)*Math.sin(Math.toRadians(currentDegree)));
		}
	}
	
	// Knockback.
	public void knockBack(int knockX, int knockY, int knockRadius, float overTime, int knockSpeed) {
		if(!gettingKnockedBack) {
			setUnitLocked(true);
			knockBackStart = time.getTime();
			gettingKnockedBack = true;
			this.knockTime = overTime;	
			this.knockSpeed = knockSpeed;
			
			// Calculate the new X and Y we need to knock them to, based off radius.
			double currentDegree = mathUtils.angleBetweenTwoPointsWithFixedPoint(getIntX()+getWidth()/2, getIntY() + getHeight()/2, knockX, knockY, knockX, knockY);
			knockToX = (int) (getIntX() + (knockSpeed*overTime*1000)*Math.cos(Math.toRadians(currentDegree))); 
			knockToY = (int) (getIntY() + (knockSpeed*overTime*1000)*Math.sin(Math.toRadians(currentDegree)));
		}
	}
	
	// Deal with knockbacks
	public void dealWithKnockBacks() {
		if(gettingKnockedBack) {
			
			// Knock them there over the duration.
			float yDistance = (knockToY - getIntY());
			float xDistance = (knockToX - getIntX());
			float distanceXY = (float) Math.sqrt(yDistance * yDistance
						+ xDistance * xDistance);
			int rise = (int) ((yDistance/distanceXY)*knockSpeed);
			int run = (int) ((xDistance/distanceXY)*knockSpeed);
			
			// Reset rise and run if we're close.
			if(Math.abs(moveToX - getIntX()) <  run) {
				run = 0;
			}
			if(Math.abs(moveToY - getIntY()) < rise) {
				rise = 0;
			}
			
			// Move
			move(run,rise);
			
			// If we are done, stop being knocked back.
			if(time.getTime() - knockBackStart > knockTime*1000) {
				collisionOn = oldCollision;
				gettingKnockedBack = false;
				setUnitLocked(false);
			}
		}
		
	}
	
	// Already attacked units
	private ArrayList<unit> alreadyAttackedUnits = new ArrayList<unit>();
	
	// Do combat mechanics.
	public void combat() {
		// Deal with knock backs
		dealWithKnockBacks();
		
		// Attack if we are attacking.
		if(isAttacking() && getCurrentAnimation()!=null) {
			// Do the attack if our BAT is over.
			if(getCurrentAnimation().getCurrentSprite() >= getAttackFrameStart() &&
			   getCurrentAnimation().getCurrentSprite() <= getAttackFrameEnd()) {
				int x1 = 0;
				int x2 = 0;
				int y1 = 0;
				int y2 = 0;
				
				// Get the box we will attack in if facing left.
				if(facingDirection.equals("Left")) {
					int heightMidPoint = getIntY() + getHeight()/2;
					y1 = heightMidPoint - getAttackWidth()/2;
					y2 = heightMidPoint + getAttackWidth()/2;
					x1 = getIntX() - getAttackLength();
					x2 = getIntX() + getWidth() + 5;
				}
				
				// Get the box we will attack in if facing right.
				if(facingDirection.equals("Right")) {
					int heightMidPoint = getIntY() + getHeight()/2;
					y1 = heightMidPoint - getAttackWidth()/2;
					y2 = heightMidPoint + getAttackWidth()/2;
					x1 = getIntX() - 5;
					x2 = getIntX() + getWidth() + getAttackLength();
				}
				
				// Get the box we will attack in facing up.
				if(facingDirection.equals("Up")) {
					int widthMidPoint = getIntX() + getWidth()/2;
					x1 = widthMidPoint - getAttackWidth()/2;
					x2 = widthMidPoint + getAttackWidth()/2;
					y1 = getIntY() - getAttackLength();
					y2 = getIntY() + getHeight() + 5;
				}
				
				// Get the box we will attack in facing down.
				if(facingDirection.equals("Down")) {
					int widthMidPoint = getIntX() + getWidth()/2;
					x1 = widthMidPoint - getAttackWidth()/2;
					x2 = widthMidPoint + getAttackWidth()/2;
					y1 = getIntY() - 5;
					y2 = getIntY() + getHeight() + getAttackLength();
				}
				
				// Attack units in array.
				unitsInAttackRange = getUnitsInBox(x1,y1,x2,y2);
				attackUnits();
				
				// Knock back projectiles for player.
				if(this instanceof player) {

					ArrayList<projectile> projs = projectile.getProjectilesInBox(x1, y1, x2, y2);
					if(projs != null) {
						for(int i = 0; i < projs.size(); i++) {
							if(!projs.get(i).isAllied()) {
								if(projs.get(i).isReflectable()) projs.get(i).sendBack();
							}
						}
					}
				}
			}
			if(time.getTime() - startAttackTime > (getAttackTime())*1000) {
				attackOver();
				setAttacking(false);
				canAttack = false;
			}
		}
		else if(time.getTime() - startAttackTime > (getAttackTime() + backSwing)*1000) {
			canAttack = true;
		}
	}
	
	// Attack is over
	public void attackOver() {
		alreadyAttackedUnits = new ArrayList<unit>();
	}
	
	// Initiate
	public static void initiate() {
		allUnits = new ArrayList<unit>();
	}
	
	// Is in attack range?
	public boolean isInAttackRange(unit u, int differential) {
		int x1 = 0;
		int x2 = 0;
		int y1 = 0;
		int y2 = 0;
		
		// Get the box we will attack in if facing left.
		if(facingDirection.equals("Left")) {
			int heightMidPoint = getIntY() + getHeight()/2;
			y1 = heightMidPoint - getAttackWidth()/2 + differential/2;
			y2 = heightMidPoint + getAttackWidth()/2 - differential/2;
			x1 = getIntX() - getAttackLength() + differential;
			x2 = getIntX() + getWidth();
		}
		
		// Get the box we will attack in if facing right.
		if(facingDirection.equals("Right")) {
			int heightMidPoint = getIntY() + getHeight()/2;
			y1 = heightMidPoint - getAttackWidth()/2 + differential/2;
			y2 = heightMidPoint + getAttackWidth()/2 - differential/2;
			x1 = getIntX();
			x2 = getIntX() + getWidth() + getAttackLength() - differential;
		}
		
		// Get the box we will attack in facing up.
		if(facingDirection.equals("Up")) {
			int widthMidPoint = getIntX() + getWidth()/2;
			x1 = widthMidPoint - getAttackWidth()/2 + differential/2;
			x2 = widthMidPoint + getAttackWidth()/2 - differential/2;
			y1 = getIntY() - getAttackLength() + differential;
			y2 = getIntY() + getHeight();
		}
		
		// Get the box we will attack in facing down.
		if(facingDirection.equals("Down")) {
			int widthMidPoint = getIntX() + getWidth()/2;
			x1 = widthMidPoint - getAttackWidth()/2 + differential/2;
			x2 = widthMidPoint + getAttackWidth()/2 - differential/2;
			y1 = getIntY();
			y2 = getIntY() + getHeight() + getAttackLength() - differential;
		}
		
		ArrayList<unit> unitsInBox = getUnitsInBox(x1,y1,x2,y2);
		if(unitsInBox == null) return false;
		return unitsInBox.contains(u);
	}
	
	// Get units in box.
	public static ArrayList<unit> getUnitsInBox(int x1, int y1, int x2, int y2) {
		ArrayList<unit> returnList = new ArrayList<unit>();
		for(int i = 0; i < getAllUnits().size(); i++) {
			unit u = getAllUnits().get(i);
			if(u.isWithin(x1, y1, x2, y2)) {
				returnList.add(u);
			}
		}
		if(returnList.size()==0) return null;
		return returnList;
	}
	
	// Get units in box.
	public static ArrayList<unit> getUnitsInRadius(int x, int y, int radius) {
		ArrayList<unit> returnList = new ArrayList<unit>();
		for(int i = 0; i < getAllUnits().size(); i++) {
			unit u = getAllUnits().get(i);
			if(u.isWithinRadius(x, y, radius)) {
				returnList.add(u);
			}
		}
		if(returnList.size()==0) return null;
		return returnList;
	}
	
	// Check if a unit is within 
	public boolean isWithin(int x1, int y1, int x2, int y2) {
		if(this instanceof developer) return false;
		return getIntX() < x2 && 
		 getIntX() + getWidth() > x1 && 
		 getIntY() + getHitBoxAdjustmentY() < y2 && 
		 getIntY() + + getHitBoxAdjustmentY() + getHeight() > y1;
	}
	
	// Get whether a unit is within radius
	public boolean isWithinRadius(int x, int y, int radius) {
	    int circleDistanceX = Math.abs(x - (this.getIntX() + this.getWidth()/2));
	    int circleDistanceY = Math.abs(y - (this.getIntY() + this.getHeight()/2));

	    if (circleDistanceX > (this.getWidth()/2 + radius)) { return false; }
	    if (circleDistanceY > (this.getHeight()/2 + radius)) { return false; }

	    if (circleDistanceX <= (this.getWidth()/2)) { return true; } 
	    if (circleDistanceY <= (this.getHeight()/2)) { return true; }

	    int cornerDistanceSQ = (int) (Math.pow(circleDistanceX - this.getWidth()/2,2) +
	                         Math.pow(circleDistanceY - this.getHeight()/2,2));

	    return (cornerDistanceSQ <= Math.pow(radius,2));
	}
	
	// Attack units
	public void attackUnits() {
		ArrayList<unit> unitsToAttack = unitsInAttackRange;
		if(unitsToAttack!=null) {
			for(int i = 0; i < unitsToAttack.size(); i++) {
				unit currentUnit = unitsToAttack.get(i);
				
				// Don't hit something we already have.
				if(alreadyAttackedUnits.contains(currentUnit)) {
				}
				else {
					// Don't hit yourself.
					if(this!=currentUnit) {
					
						// Hit for their damage times their multiplier.
						float variabilityMult = 1;
						if(getAttackVariability() == 0) variabilityMult = 1;
						else variabilityMult = 1f + getAttackVariability() - ((float)utility.RNG.nextInt((int)(2*getAttackVariability()*100))/100f);
						int actualDamageDone = (int) (this.getAttackDamage()*attackMultiplier*variabilityMult);
						
						// Did we crit?
						float crit = 1f;
						if(getCritChance()*100 >= utility.RNG.nextInt(100)) crit = getCritDamage();
						
						// Players can hit anything.
						if(this instanceof player) {
							if(currentUnit.isTargetable()) {
								alreadyAttackedUnits.add(currentUnit);
								currentUnit.hurt(actualDamageDone, crit);
							}
						}
						
						// Enemies can't hit eachother.
						else if(!(this instanceof player) && currentUnit instanceof player) {
							if(currentUnit.isTargetable()) {
								alreadyAttackedUnits.add(currentUnit);
								currentUnit.hurt(actualDamageDone, crit);
							}
						}
						
						// Kill unit?
						if(currentUnit.healthPoints <= 0) currentUnit.die();
					}
				}
			}
		}
	}
	
	// Force to take damage.
	public boolean forceHurt(int damage, float crit) {
		if(damage != 0) {
			
			if(killable) {
				if(healthPoints - crit*damage < 0) healthPoints = 0;
				else healthPoints -= crit*damage;
			}
		
			// Crit
			if(crit != 1f) {
				effect e = new floatingString("" + (int)(crit*damage), DEFAULT_CRIT_COLOR, getIntX() + getWidth()/2, getIntY() + getHeight()/2, 1f, 3f);
			}
			
			// Non crit.
			else {
				effect e = new floatingString("" + damage, DEFAULT_DAMAGE_COLOR, getIntX() + getWidth()/2, getIntY() + getHeight()/2, 1f);
			}
			
			// Squirt blood
			int randomX = 0;
			int randomY = -platformerHeight/3 + utility.RNG.nextInt(platformerHeight/3 + 1);
			if(healthPoints > 0) {
				effect blood = new bloodSquirt(getIntX() - bloodSquirt.getDefaultWidth()/2 + topDownWidth/2 + randomX ,
						getIntY() - bloodSquirt.getDefaultHeight()/2 + platformerHeight/2 + randomY);
			}
			reactToPain();
		}
		return true;
	}
	
	// Take damage. Ouch!
	public boolean hurt(int damage, float crit) {
		if(targetable) {
			if(shielding && ((player)this).getEnergy() > 0) {
				if(this instanceof player) {
					float hitDamage = 1;
					if(((player)this).getEnergy() <= 0) {
						return forceHurt(damage,crit);
					}
					else if(((player)this).getEnergy() - hitDamage < 0) {
						float difference = (float) ((hitDamage - ((player)this).getEnergy())/hitDamage);
						((player)this).setShielding(false);
						((player)this).setEnergy(0);
						forceHurt((int) (damage*difference),crit);
						return false;
					}
					else ((player)this).setEnergy((((player)this).getEnergy() - hitDamage));
					return false;
				}
				return false;
			}
			else {
				return forceHurt(damage,crit);
			}
		}
		else {
			return false;
		}
	}
	
	// React to pain.
	public abstract void reactToPain();
	
	// Start attacking.
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
	
	// Jump only once.
	boolean alreadyJumped = false;
	
	// Start trying to jump.
	public void startJump() {
		tryJump = true;
	}
	
	// Stop trying to jump
	public void stopJump() {
		alreadyJumped = false;
		tryJump = false;
	}
	
	// Jump unit
	public void jump() {
		if(!alreadyJumped && !isStuck() && gravity && !jumping && tryJump && touchingGround) {
			// Accelerate upward.
			alreadyJumped = true;
			jumping = true;
			fallSpeed = -jumpSpeed;
		}
	}
	
	// Follow a unit.
	public void follow(unit u) {
		
		// Set following
		followingUnit = true;
		followedUnit = u;
	}
	
	// Unfollow
	public void unfollow() {
		stopMove("all");
		followingUnit = false;
		followedUnit = null;
	}
	
	// Move to
	public void moveTo(int newX, int newY) {
		movingToAPoint = true;
		moveToX = newX;
		moveToY = newY;
		
		// Set rise/run
		setRiseRun();
	}
	
	// Move towards a point
	public void moveTowards() {
		if(movingToAPoint && Math.abs(moveToX - getDoubleX()) < getMoveSpeed() && Math.abs(moveToY - getDoubleY()) < getMoveSpeed()) {
			setDoubleX(moveToX);
			setDoubleY(moveToY);
			movingToAPoint = false;
		}
		else {
			// Set facing direction.
			if(run < -0.5f) {
				setFacingDirection("Left");
			}
			else if(run > 0.5f) {
				setFacingDirection("Right");
			}
			else if(rise < 0) {
				setFacingDirection("Up");
			}
			else {
				setFacingDirection("Down");
			}
			move(run,rise);
		}
	}
	
	// Moving rise/run.
	private double rise = 0;
	private double run = 0;
	
	// Set rise run. 
	public void setRiseRun() {
		double yDistance = (moveToY - getIntY());
		double xDistance = (moveToX - getIntX());
		double distanceXY = (float) Math.sqrt(yDistance * yDistance
					+ xDistance * xDistance);
		
		// Calculate rise values.
		double floatRise = ((yDistance/distanceXY)*(float)getMoveSpeed());
		rise = floatRise;
		
		// Calculate run values.
		double floatRun = ((xDistance/distanceXY)*(float)getMoveSpeed());
		run = floatRun;
	}
	
	// Deal with meta movement. Moving toward a point, following, pathing, etc.
	public void dealWithMetaMovement() {
		
		///////////////////////////////
		/// Following unit ///
		///////////////////////////////	
		if(followingUnit) {
			
			// Set where we need to move to.
			moveToX = followedUnit.getIntX();
			moveToY = followedUnit.getIntY();
			
			// Set rise/run
			setRiseRun();
			
			// Move there.
			moveTowards();
		}
		
		///////////////////////////////
		/// PATROLLING ///
		///////////////////////////////	
		dealWithPatrolling();
		
		///////////////////////////////
		/// MOVEMENT TOWARD A POINT ///
		///////////////////////////////	
		if(movingToAPoint) {
			moveTowards();
		}
		
		///////////////////////////////
		/// FOLLOWING A PATH        ///
		///////////////////////////////	
		if(isFollowingAPath()) {
			if(path != null && path.size() > 0) {
				if(currPoint == null) {
					moveTo(path.get(0).x, path.get(0).y);
					currPoint = path.get(0);
					path.remove(0);
				}
				else if(!(Math.abs(currPoint.x - getIntX()) > moveSpeed + 1 || Math.abs(currPoint.y - getIntY()) > moveSpeed+1)) {
					setDoubleX(moveToX);
					setDoubleY(moveToY);
					moveTo(path.get(0).x, path.get(0).y);
					currPoint = path.get(0);
					path.remove(0);
				}
			}
			else {
				currPoint = null;
				path = null;
				setFollowingAPath(false);
			}
		}
	}
	
	// Follow path.
	public void followPath(ArrayList<intTuple> p) {
		path = p;
		setFollowingAPath(true);
	}
	
	// Move unit
	public void moveUnit() {
		
		// Apply movement debuffs/buffs
		float buffedMoveSpeed = moveSpeed;
		ArrayList<Class> appliedEffects = new ArrayList<Class>();
		for(int i = 0; i < movementBuffs.size(); i++) {
			if(!appliedEffects.contains(movementBuffs.get(i).getClass())) {
				buffedMoveSpeed *= movementBuffs.get(i).getMovementPercentage();
				appliedEffects.add(movementBuffs.get(i).getClass());
			}
		}
		
		float moveByX = buffedMoveSpeed;
		float moveByY = buffedMoveSpeed;
	
		// Basic movement.
		float moveX = 0;
		float moveY = 0;
		
		// Actual movement.
		if(isMovingLeft()) moveX -= moveByX;
		if(isMovingRight()) moveX += moveByX;
		
		// Only do these ones if we're in topDown mode.
		if(mode.getCurrentMode() == "topDown" || isStuck()) {
			if(isMovingUp()) moveY -= moveByY;
			if(isMovingDown()) moveY += moveByY;
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

		if(!isUnitLocked()) {
			// Move the unit
			if(movingDiagonally()) {
				move(moveX*(1f/1.3f), moveY*(1f/1.3f));
			}
			else {
				move(moveX, moveY);
			}
		}

	}
	
	// Do unit specific movement.
	public void unitSpecificMovement(double actualMoveX, double actualMoveY) {
	}
	
	// Moving diagonally?
	public boolean movingDiagonally() {
		boolean movingLeftAndRight = isMovingLeft() && isMovingRight();
		boolean movingUpAndDown = isMovingUp() && isMovingDown();
		boolean movingHorizontally = (isMovingLeft() || isMovingRight()) && !movingLeftAndRight;
		boolean movingVertically = (isMovingUp() || isMovingDown()) && !movingUpAndDown;
		return movingVertically && movingHorizontally;
	}
	
	// Move the unit in a specific direction.
	public void moveUnit(String direction) {
		
		// Reset everything.
		setMovingLeft(false);
		setMovingRight(false);
		setMovingUp(false);
		setMovingDown(false);
		
		// Move them in said direction.
		if(direction.equals("upLeft")) {
			setMovingLeft(true);
			setMovingUp(true);
		}
		if(direction.equals("upRight")) {
			setMovingRight(true);
			setMovingUp(true);
		}
		if(direction.equals("downLeft")) {
			setMovingLeft(true);
			setMovingDown(true);
		}
		if(direction.equals("downRight")) {
			setMovingRight(true);
			setMovingDown(true);
		}
		if(direction.equals("left")) {
			setMovingLeft(true);
		}
		if(direction.equals("right")) {
			setMovingRight(true);
		}
		if(direction.equals("up")) {
			setMovingUp(true);
		}
		if(direction.equals("down")) {
			setMovingDown(true);
		}
	}
	
	// Unit has touched up
	public void touchUp() {
		
		// Essentially, bonk unit's head of roof.
		fallSpeed = 0;
	}
	
	// Unit has touched down.
	public void touchDown() {
		
		// Hold the fall speed but set the current to be 0.
		float oldFallSpeed = fallSpeed;
		fallSpeed = 0;
		
		// If they've touched down, place them closer to the ground.
		chunk ground = chunk.getGroundChunk(this, (int)getDoubleX(), (int)(getDoubleY() + oldFallSpeed));
		if(ground != null) {
			fallSpeed = ground.getIntY() - (this.getIntY() + this.getHeight());
		}
		
		// They can jump again if they've touched down.
		jumping = false;
		touchingGround = true;
		inAir = (fallSpeed != 0);
	}
	
	// Move function
	public void move(double moveX, double moveY) {
		
		if(player.getPlayer() != null && 
			player.getPlayer().getCurrentZone()!=null && 
			player.getPlayer().getCurrentZone().isZoneLoaded()) {
			
			// Actual move x and y when all is said and done.
			double actualMoveX = moveX;
			double actualMoveY = moveY;
	
			if(isCollisionOn() && (moveX != 0 || moveY != 0)) {
				
				// Set to not be stuck.
				pathFindingStuck = false;
				
				// Check if it collides with a chunk in the x or y plane.
				intTuple xyCollide = chunk.collidesWith(this, (int)(getDoubleX() + moveX), (int)(getDoubleY() + moveY));
				intTuple leftRegion = region.leftRegion(this, (int)(getDoubleX() + moveX),(int)(getDoubleY() + moveY));
				if((xyCollide.x != 0 || leftRegion.x != 0)) {
					pathFindingStuck = true;
					if(xyCollide.x!=0) actualMoveX = 0;
					if(leftRegion.x!=0) actualMoveX = 0;
				}
				
				// Lots more to check for platformer mode.
				if((xyCollide.y != 0 || leftRegion.y != 0)) {
					
					// Yes, we're stuck.
					pathFindingStuck = true;
				
					// If gravity is on
					if (gravity) { 
						
						// We touch down
						if(moveY >= 0) {
							touchDown();
						}
						
						// We touch up
						if(moveY <= 0) {
							touchUp();
						}
					}
					
					// Don't move the object.
					if(xyCollide.y!=0) actualMoveY = 0;
					if(leftRegion.y!=0) actualMoveY = 0;
				}
				
				// If we are moving in the y direction, but are not touching down.
				else if(moveY > 0 && touchingGround) {
					touchingGround = false;
				}
				
				// Are we entering the air (by a significant amount?)
				if(Math.abs(actualMoveY) > getMoveSpeed()) {
					inAir = true;
				}
			}
			
			// Deal with animations.
			dealWithAnimations((int)actualMoveX,(int)actualMoveY);
	
			// Move the camera if it's there.
			if(attachedCamera != null) {
				attachedCamera.setX((int)(getDoubleX() + actualMoveX));
				attachedCamera.setY((int)(getDoubleY() + actualMoveY));
			}
			
			// Move the unit.
			setDoubleX(getDoubleX() + actualMoveX);
			setDoubleY(getDoubleY() + actualMoveY);
			
			// Specific movement
			unitSpecificMovement(actualMoveX,actualMoveY);
		}
	}
	
	// Start moving
	public void startMove(String direction) {
		
		// Start moving right.
		if(direction=="right") { 
			setMovingRight(true);
		}
		
		// Start moving left.
		if(direction=="left") { 
			setMovingLeft(true);
		}
		
		// Start moving up.
		if(direction=="up") {
			setMovingUp(true);
		}
		
		// Start moving down..
		if(direction=="down") {
			setMovingDown(true);
		}
	}
	
	// Stop moving
	public void stopMove(String direction) {
		
		// Stop moving in any direction.
		if(direction=="all") {
			setMovingRight(false);
			setMovingLeft(false);
			setMovingUp(false);
			setMovingDown(false);
		}
		
		// Stop moving right.
		if(direction=="right") { 
			setMovingRight(false);
		}
		
		// Stop moving left.
		if(direction=="left") {
			setMovingLeft(false);
		}
		
		// Stop moving up.
		if(direction=="up") {
			setMovingUp(false);
		}
		
		// Stop moving down.
		if(direction=="down") {
			setMovingDown(false);
		}
		
		if(direction=="horizontal"){
			setMovingLeft(false);
			setMovingRight(false);
		}
		
		if(direction=="vertical"){
			setMovingUp(false);
			setMovingDown(false);
		}
	}
	
	// Deal with movement animations.
	public void dealWithAnimations(int moveX, int moveY) {
		
		// topDown mode movement animations.
		if(mode.getCurrentMode().equals("topDown")) {
			if(isAttacking() && !isAlreadyAttacked()) {
				// Play animation.
				animate("attacking" + facingDirection);
			}
			else if(isMoving()) {
				animate("running" + getFacingDirection());
			}
			else {
				animate("standing" + getFacingDirection());
			}
		}
		
		// platformer movement animations.
		if(mode.getCurrentMode().equals("platformer")) {
			if(isAttacking() && !isAlreadyAttacked()) {
				// Play animation.
				animate("attacking" + facingDirection);
			}
			else if(inAir && !isStuck()) {
				String face;
				if(getFacingDirection().equals("Up") || getFacingDirection().equals("Down")) {
					face = "Right";
				}
				else {
					face = getFacingDirection();
				}
				animate("jumping" + face);
			}
			else if(!(alreadyJumped && tryJump && !movingHorizontally()) && ((isMoving() && isStuck()) || (!isStuck() && (!isMovingDown() || (isMovingLeft() || isMovingRight())) && isMoving()))) {
				
				// If we are running.
				animate("running" + getFacingDirection());
			}
			else {
			    animate("standing" + getFacingDirection());
			}
		}
	}
	
	// Cause unit to perform an animation.
	public void animate(String animationName) {
		if(getAnimations()!=null) {
			animation a = getAnimations().getAnimation(animationName);
			if(a != null) {
				
				// Reset the frame if it's a new animation.
				if(getCurrentAnimation() != null) {
					if(!getCurrentAnimation().getName().equals(a.getName())) { 
						a.startAnimation();
						//if(this instanceof shadowDude) 
					}
					
					// Set the animation.
					setCurrentAnimation(a);
				}
				
				// No animation yet.
				else if(getCurrentAnimation()==null) {
					setCurrentAnimation(a);
				}
			}
		}
	}
	
	// Special case drawing stuff.
	public void drawUnitSpecialStuff(Graphics g) {
	}
	
	// Draw the unit. 
	@Override
	public void drawObject(Graphics g) {
		// Of course only draw if the animation is not null.
		if(getCurrentAnimation() != null) {
			g.drawImage(getCurrentAnimation().getCurrentFrame(), 
					getDrawX(), 
					getDrawY(), 
					(int)(gameCanvas.getScaleX()*getCurrentAnimation().getCurrentFrame().getWidth()), 
					(int)(gameCanvas.getScaleY()*getCurrentAnimation().getCurrentFrame().getHeight()), 
					null);
		}
		
		// Draw healthbar is hp is low.
		if(healthPoints < maxHealthPoints && !(this instanceof player)) {
			// % of HP left.
			int healthChunkSize = (int)(((float)getHealthPoints()/(float)getMaxHealthPoints())*DEFAULT_HEALTHBAR_WIDTH);
			
			// Adjustment
			int hpAdjustX;
			int hpAdjustY;
			if(getCurrentAnimation()!=null) {
				hpAdjustX = (int) (gameCanvas.getScaleX()*(getCurrentAnimation().getCurrentFrame().getWidth()/2 - DEFAULT_HEALTHBAR_WIDTH/2));
				hpAdjustY = -(int)(gameCanvas.getScaleY()*getCurrentAnimation().getCurrentFrame().getHeight()/2/3);
			}
			else {
				hpAdjustX = (int) (gameCanvas.getScaleX()*(getObjectSpriteSheet().getSpriteWidth()/2 - DEFAULT_HEALTHBAR_WIDTH/2));
				hpAdjustY = -(int)(gameCanvas.getScaleY()*getHeight()/3);
			}
			
			// Draw the red.
			g.setColor(playerHealthBar.DEFAULT_HEART_COLOR);
			g.fillRect(getDrawX() + hpAdjustX,
					   getDrawY() + hpAdjustY,
					   (int)(gameCanvas.getScaleX()*DEFAULT_HEALTHBAR_WIDTH),
					   (int)(gameCanvas.getScaleY()*DEFAULT_HEALTHBAR_HEIGHT));
			
			// Draw the green chunks.
			g.setColor(DEFAULT_HEAL_COLOR);
			g.fillRect(getDrawX() + hpAdjustX,
					   getDrawY() + hpAdjustY,
					   (int)(gameCanvas.getScaleX()*healthChunkSize),
					   (int)(gameCanvas.getScaleY()*DEFAULT_HEALTHBAR_HEIGHT));

			// Draw border.
			g.setColor(playerHealthBar.DEFAULT_BORDER_COLOR);
			g.drawRect(getDrawX() + hpAdjustX,
					   getDrawY() + hpAdjustY,
					   (int)(gameCanvas.getScaleX()*DEFAULT_HEALTHBAR_WIDTH),
					   (int)(gameCanvas.getScaleY()*DEFAULT_HEALTHBAR_HEIGHT));
		}
		
		// Draw the outskirts of the sprite.
		if(showSpriteBox && getCurrentAnimation() != null) {
			g.setColor(Color.red);
			g.drawRect(getDrawX(),
					   getDrawY(), 
					   (int)(gameCanvas.getScaleX()*getCurrentAnimation().getCurrentFrame().getWidth()), 
					   (int)(gameCanvas.getScaleY()*getCurrentAnimation().getCurrentFrame().getHeight()));
		}
		
		// Draw the x,y coordinates of the unit.
		if(showUnitPosition) {
			g.setColor(Color.white);
			g.drawString(getIntX() + "," + getIntY(),
					   getDrawX(),
					   getDrawY());
		}
		
		// Show attack range.
		if(showAttackRange && getCurrentAnimation() != null) {
			int x1 = 0;
			int x2 = 0;
			int y1 = 0;
			int y2 = 0;
			
			// Get the x and y of hitbox.
			int hitBoxX = getDrawX() - (- (getCurrentAnimation().getCurrentFrame().getWidth()/2 - getWidth()/2) - getHitBoxAdjustmentX());
			int hitBoxY = getDrawY() - (- (getCurrentAnimation().getCurrentFrame().getHeight()/2 - getHeight()/2) - getHitBoxAdjustmentY());
			
			// Get the box we will attack in if facing left.
			if(facingDirection.equals("Left")) {
				int heightMidPoint = hitBoxY + getHeight()/2;
				y1 = heightMidPoint - getAttackWidth()/2;
				y2 = heightMidPoint + getAttackWidth()/2;
				x1 = hitBoxX - getAttackLength();
				x2 = hitBoxX + getWidth() + 5;
			}
			
			// Get the box we will attack in if facing right.
			if(facingDirection.equals("Right")) {
				int heightMidPoint = hitBoxY + getHeight()/2;
				y1 = heightMidPoint - getAttackWidth()/2;
				y2 = heightMidPoint + getAttackWidth()/2;
				x1 = hitBoxX - 5;
				x2 = hitBoxX + getWidth() + getAttackLength();
			}
			
			// Get the box we will attack in facing up.
			if(facingDirection.equals("Up")) {
				int widthMidPoint = hitBoxX + getWidth()/2;
				x1 = widthMidPoint - getAttackWidth()/2;
				x2 = widthMidPoint + getAttackWidth()/2;
				y1 = hitBoxY - getAttackLength();
				y2 = hitBoxY + getHeight() + 5;
			}
			
			// Get the box we will attack in facing down.
			if(facingDirection.equals("Down")) {
				int widthMidPoint = hitBoxX + getWidth()/2;
				x1 = widthMidPoint - getAttackWidth()/2;
				x2 = widthMidPoint + getAttackWidth()/2;
				y1 = hitBoxY - 5;
				y2 = hitBoxY + getHeight() + getAttackLength();
			}
			g.setColor(Color.blue);
			g.drawRect((int)(gameCanvas.getScaleX()*x1),(int)(gameCanvas.getScaleY()*y1),(int)(gameCanvas.getScaleX()*x2-x1),(int)(gameCanvas.getScaleY()*y2-y1));
		}
		
		// Draw the hitbox of the image in green.
		if(showHitBox && getCurrentAnimation() != null) {
			g.setColor(Color.green);
			g.drawRect(getDrawX() - (int)(gameCanvas.getScaleX()*(- (getCurrentAnimation().getCurrentFrame().getWidth()/2 - getWidth()/2) - getHitBoxAdjustmentX())),
					   getDrawY() - (int)(gameCanvas.getScaleY()*(- (getCurrentAnimation().getCurrentFrame().getHeight()/2 - getHeight()/2) - getHitBoxAdjustmentY())), 
					   (int)(gameCanvas.getScaleX()*getWidth()), 
					   (int)(gameCanvas.getScaleY()*getHeight()));
		}
		
		// Draw special stuff
		drawUnitSpecialStuff(g);
	}
	
	// Set a unit to have a quest.
	public void hasQuest() {
		int spawnX = (getIntX() + getWidth()/2) - questMark.DEFAULT_CHUNK_WIDTH/2;
		int spawnY = (int)(getIntY() - 2.5f*questMark.DEFAULT_CHUNK_HEIGHT);
		questIcon = new questMark(spawnX, spawnY, 0);
	}
	
	// Set a unit to have a quest.
	public void noQuest() {
		if(questIcon != null) questIcon.destroy();
	}
	
	/////////////////////////
	// Getters and setters //
	/////////////////////////
	public boolean movingHorizontally() {
		boolean movingLeftAndRight = isMovingLeft() && isMovingRight();
		return (isMovingLeft() || isMovingRight()) && !movingLeftAndRight;
	}
	
	public boolean movingVertically() {
		boolean movingUpAndDown = isMovingUp() && isMovingDown();
		return (isMovingUp() || isMovingDown()) && !movingUpAndDown;
	}
	
	public boolean isMoving() {
		return movingVertically() || movingHorizontally() || movingToAPoint || followingUnit || followingAPath || patrolling;
	}
	
	public void setCollision(boolean b) {
		setCollisionOn(b);
	}
	public String getFacingDirection() {
		return facingDirection;
	}

	public void setFacingDirection(String facingDirection) {
		if(facingDirection=="random") {
			int r = utility.RNG.nextInt(3);
			if(r==0) facingDirection = "Up";
			if(r==1) facingDirection = "Down";
			if(r==2) facingDirection = "Right";
			if(r==3) facingDirection = "Left";
		}
		this.facingDirection = facingDirection;
	}

	public animationPack getAnimations() {
		return animations;
	}

	public void setAnimations(animationPack animations) {
		this.animations = animations;
	}

	public boolean isKillable() {
		return killable;
	}

	public void setKillable(boolean killable) {
		this.killable = killable;
	}

	public int getHealthPoints() {
		return healthPoints;
	}

	public void setHealthPoints(int healthPoints) {
		this.healthPoints = healthPoints;
	}

	public int getAttackDamage() {
		return attackDamage;
	}

	public void setAttackDamage(int attackDamage) {
		this.attackDamage = attackDamage;
	}
	
	
	public void showAttackRange() {
		showAttackRange = true;
	}

	public float getAttackTime() {
		return attackTime;
	}

	public void setAttackTime(float attackTime) {
		this.attackTime = attackTime;
	}

	public int getAttackWidth() {
		return attackWidth;
	}

	public void setAttackWidth(int attackWidth) {
		this.attackWidth = attackWidth;
	}

	public int getAttackLength() {
		return attackLength;
	}

	public void setAttackLength(int attackLength) {
		this.attackLength = attackLength;
	}

	public int getMaxHealthPoints() {
		return maxHealthPoints;
	}

	public void setMaxHealthPoints(int maxHealthPoints) {
		this.maxHealthPoints = maxHealthPoints;
	}

	public float getMoveSpeed() {
		return moveSpeed;
	}

	public void setMoveSpeed(float f) {
		fixAnimationsBasedOnMoveSpeed(f);
		this.moveSpeed = f;
	}
	
	public void fixAnimationsBasedOnMoveSpeed(float newSpeed) {
		if(animations!=null) {
			for(int i = 0; i < animations.getAnimations().size(); i++) {
				animation currentAnimation = animations.getAnimations().get(i);
				if(currentAnimation.getName().contains("running")) {
					currentAnimation.setTimeToComplete((moveSpeed/newSpeed)*currentAnimation.getTimeToComplete());
				}
			}
		}
	}

	public static ArrayList<unit> getAllUnits() {
		return allUnits;
	}

	public static void setAllUnits(ArrayList<unit> allUnits) {
		unit.allUnits = allUnits;
	}

	public animation getCurrentAnimation() {
		return currentAnimation;
	}

	public void setCurrentAnimation(animation currentAnimation) {
		this.currentAnimation = currentAnimation;
	}

	public boolean isAttacking() {
		return attacking;
	}

	public void setAttacking(boolean attacking) {
		this.attacking = attacking;
	}

	public boolean isAlreadyAttacked() {
		return alreadyAttacked;
	}

	public void setAlreadyAttacked(boolean alreadyAttacked) {
		this.alreadyAttacked = alreadyAttacked;
	}

	public boolean isTargetable() {
		return targetable;
	}

	public void setTargetable(boolean targetable) {
		this.targetable = targetable;
	}

	public boolean isCollisionOn() {
		return collisionOn;
	}

	public void setCollisionOn(boolean collisionOn) {
		this.collisionOn = collisionOn;
	}

	public unitType getTypeOfUnit() {
		return typeOfUnit;
	}

	public void setTypeOfUnit(unitType typeOfUnit) {
		this.typeOfUnit = typeOfUnit;
	}

	public float getBackSwing() {
		return backSwing;
	}

	public void setBackSwing(float backSwing) {
		this.backSwing = backSwing;
	}

	public float getCritChance() {
		return critChance;
	}

	public void setCritChance(float critChance) {
		this.critChance = critChance;
	}

	public float getCritDamage() {
		return critDamage;
	}

	public void setCritDamage(float critDamage) {
		this.critDamage = critDamage;
	}

	public float getAttackVariability() {
		return attackVariability;
	}

	public void setAttackVariability(float attackVariability) {
		this.attackVariability = attackVariability;
	}

	public boolean isStuck() {
		return stuck;
	}

	public void setStuck(boolean b, drawnObject d) {
		this.stuck = b;
		if(b==true) {
			setStuckOn(d);
		}
	}

	public drawnObject getStuckOn() {
		return stuckOn;
	}

	public void setStuckOn(drawnObject stuckOn) {
		this.stuckOn = stuckOn;
	}

	public boolean isShielding() {
		return shielding;
	}

	public void setShielding(boolean shielding) {
		this.shielding = shielding;
	}

	public int getAttackFrameEnd() {
		return attackFrameEnd;
	}

	public void setAttackFrameEnd(int attackFrameEnd) {
		this.attackFrameEnd = attackFrameEnd;
	}

	public int getAttackFrameStart() {
		return attackFrameStart;
	}

	public void setAttackFrameStart(int attackFrameStart) {
		this.attackFrameStart = attackFrameStart;
	}

	public ArrayList<movementBuff> getMovementBuffs() {
		return movementBuffs;
	}

	public void setMovementBuffs(ArrayList<movementBuff> movementBuffs) {
		this.movementBuffs = movementBuffs;
	}

	public boolean isUnitLocked() {
		return unitLocked;
	}

	public void setUnitLocked(boolean unitLocked) {
		this.unitLocked = unitLocked;
	}

	public String getAttackSound() {
		return attackSound;
	}

	public void setAttackSound(String attackSound) {
		this.attackSound = attackSound;
	}

	public ArrayList<unit> getInCombatWith() {
		return inCombatWith;
	}

	public void setInCombatWith(ArrayList<unit> inCombatWith) {
		this.inCombatWith = inCombatWith;
	}

	public boolean isMovingLeft() {
		return movingLeft;
	}

	public void setMovingLeft(boolean movingLeft) {
		this.movingLeft = movingLeft;
	}

	public boolean isMovingRight() {
		return movingRight;
	}

	public void setMovingRight(boolean movingRight) {
		this.movingRight = movingRight;
	}

	public boolean isMovingDown() {
		return movingDown;
	}

	public void setMovingDown(boolean movingDown) {
		this.movingDown = movingDown;
	}

	public boolean isMovingUp() {
		return movingUp;
	}

	public void setMovingUp(boolean movingUp) {
		this.movingUp = movingUp;
	}

	public void setStuck(boolean stuck) {
		this.stuck = stuck;
	}

	public boolean isFollowingAPath() {
		return followingAPath;
	}

	public void setFollowingAPath(boolean followingAPath) {
		this.followingAPath = followingAPath;
	}
	
}