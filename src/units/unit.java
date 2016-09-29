package units;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.lang.reflect.Constructor;
import java.util.ArrayList;

import UI.playerActionBar;
import doodads.general.lightSource;
import doodads.general.questMark;
import drawing.drawnObject;
import drawing.gameCanvas;
import drawing.animation.animation;
import drawing.animation.animationPack;
import effects.buff;
import effects.effect;
import effects.buffs.movementBuff;
import effects.effectTypes.bloodSquirt;
import effects.effectTypes.critBloodSquirt;
import effects.interfaceEffects.floatingString;
import modes.mode;
import sounds.sound;
import terrain.chunk;
import terrain.groundTile;
import terrain.region;
import units.developer.developer;
import units.unitCommands.commandList;
import units.unitCommands.positionedCommand;
import units.unitCommands.positionedMovementCommand;
import units.unitCommands.commands.moveCommand;
import units.unitCommands.commands.slashCommand;
import units.unitCommands.commands.waitCommand;
import utilities.intTuple;
import utilities.mathUtils;
import utilities.time;
import utilities.utility;
import zones.zone;

public class unit extends drawnObject  { 
	
	/////////////////////////
	////// DEFAULTS /////////
	/////////////////////////
	
	// List of all units
	private static ArrayList<unit> allUnits;
	
	// Default movespeed.
	private static int DEFAULT_UNIT_MOVESPEED = 1;
	
	// Gravity defaults.
	private static boolean DEFAULT_GRAVITY_STATE = false;
	private static float DEFAULT_SHORT_JUMP_ACCEL = 0.42f + .37f;
	private static float DEFAULT_LONG_JUMP_ACCEL = 0.43f;
	private static float DEFAULT_GRAVITY_MAX_VELOCITY = 18f;
	protected static float DEFAULT_JUMPSPEED = 11f;
	
	// Animation defaults.
	private static String DEFAULT_FACING_DIRECTION = "Right";
	
	// Colors for combat.
	protected static Color DEFAULT_HEAL_COLOR = Color.green;

	////////////////
	//// FIELDS ////
	////////////////
	
	// State of gravity
	private static boolean gravity = DEFAULT_GRAVITY_STATE;
	
	// The actual unit type.
	private unitType typeOfUnit;
	
	// Color of name
	private Color nameColor = Color.BLACK;
	
	// Default textBox.
	private BufferedImage dialogueBox = null;
	
	// Width and height for topDown and platformer
	protected int topDownWidth = 0;
	protected int topDownHeight = 0;
	protected int platformerWidth = 0;
	protected int platformerHeight = 0;
	
	// Are we stuck in something sticky?
	private boolean stuck = false;
	
	// Check if something is stuck in pathfinding
	protected boolean pathFindingStuck = false;
	
	// Destroy timer
	private float destroyTimer = 0;
	
	// Health
	private int healthPoints = 1;
	
	// Does the unit kill the player?
	protected boolean killsPlayer = false;

	// Has the unit
	private boolean unitIsDead = false;
	protected long unitDiedAt = 0;
	protected float deathAnimationLasts = 1f;
	
	// Attacking/getting attacked mechanics
	private boolean killable = false;
	protected boolean targetable = true;
	
	// Gravity
	public float jumpSpeed = DEFAULT_JUMPSPEED;
	private float fallSpeed = 0;
	private boolean jumping = false;
	private boolean tryJump = false;
	private boolean touchingGround = false;
	private boolean inAir = true;
	
	// Double jumping?
	private boolean doubleJumping = false;
	
	// Buffs
	private ArrayList<buff> buffs;
	
	// Movement buffs/debuffs
	private ArrayList<movementBuff> movementBuffs;
	
	// When did we spawn?
	private long spawnTime = 0;
	
	// Movement
	public float moveSpeed = DEFAULT_UNIT_MOVESPEED;
	protected float oldMoveSpeed = moveSpeed;
	protected float baseMoveSpeed = DEFAULT_UNIT_MOVESPEED;
	protected float baseMoveDuration = 0.75f;
	private boolean movingLeft = false;
	private boolean movingRight = false;
	private boolean movingDown = false;
	public boolean movingUp = false;
	protected String facingDirection = DEFAULT_FACING_DIRECTION;
	protected boolean collisionOn = true;
	
	// Slow movement
	private boolean allowSlowMovement = false;
	private int moveFrame = 0;
	
	// Movement for slippery stuff
	private float movementAcceleration = 0;
	private float momentumX = 0;
	private float momentumY = 0;
	
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
	private boolean movingToAPoint = false;
	
	// Units in attack range.
	//protected ArrayList<unit> unitsInAttackRange;
	
	// Next point.
	private intTuple currPoint;
	
	// Knockbacks
	private long knockBackStart = 0;
	private boolean gettingKnockedBack = false;
	private int knockToX;
	private int knockToY;
	private int knockSpeed;
	private float knockTime;
	
	// No collision knockback
	boolean oldCollision = collisionOn;
	
	// Shielding?
	private boolean shielding = false;
	
	// What we are stuck on?
	private drawnObject stuckOn = null;
	
	// What commands do we do?
	private boolean canSlash = false;
	private boolean canSlashSummon = false;
	
	///////////////
	/// METHODS ///
	///////////////

	// Constructor
	public unit(unitType u, int newX, int newY) {
		super(u.getUnitTypeSpriteSheet(), u.getName(), newX, newY, u.getWidth(), u.getHeight());	
		if(u.getAnimations()!=null) setAnimations(new animationPack(u.getAnimations()));
		moveSpeed = u.getMoveSpeed();
		oldMoveSpeed = moveSpeed;
		baseMoveSpeed = u.getMoveSpeed();
		setJumpSpeed(u.getJumpSpeed());
		setTypeOfUnit(u);
		
		// Add to list
		getAllUnits().add(this);
		
		// Add animation
		move(0,0);
		
		spawnTime = time.getTime();
	}
	
	// Copy Constructor
	public unit(unit u) {
		super(u.getObjectSpriteSheet(), u.getName(), u.getIntX(), u.getIntX(), u.getWidth(), u.getHeight());	
		if(u.getAnimations()!=null) setAnimations(new animationPack(u.getAnimations()));
		moveSpeed = u.getMoveSpeed();
		oldMoveSpeed = moveSpeed;
		baseMoveSpeed = u.getMoveSpeed();
		setJumpSpeed(u.getJumpSpeed());
		setTypeOfUnit(u.getTypeOfUnit());
		
		// Copy the repeatCommands
		repeatCommands(new commandList(u.getRepeatCommands()));
		
		// Add to list
		getAllUnits().add(this);
		
		// Add animation
		move(0,0);
		
		spawnTime = time.getTime();
	}
	
	// Make copy
	@Override
	public drawnObject makeCopy() {
		
		try {
			Class<?> clazz = Class.forName(this.getClass().getName());
			Constructor<?> ctor = clazz.getConstructor(int.class, int.class);
			Object object = ctor.newInstance(new Object[] { this.getIntX(),
					this.getIntY()});
			
			unit d = (unit)object;
			d.setMoveSpeed(this.getMoveSpeed());
			
			// Copy the repeatCommands
			if(this.getRepeatCommands() != null) d.repeatCommands(new commandList(this.getRepeatCommands()));
			
			return d;
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}	
	}
	
	// Possibly destroy
	public void possiblyDestroy() {
		
		if(getDestroyTimer() != 0 && time.getTime() - spawnTime > getDestroyTimer()*1000) {
			destroy();
		}
		
	}

	// Update unit
	@Override
	public void update() {	
		if(!isUnitIsDead()) {
			possiblyDestroy();
			hurtPeople(leniency);
			doCommands();
			gravity();
			jump();
			moveUnit();
			dealWithMetaMovement();
			//combat();
			aliveOrDead();
		}
		updateUnit();
		if(getCurrentAnimation() != null) getCurrentAnimation().playAnimation();
	}
	
	// Hurting people leniency
	public static int leniency = 7;
	
	// Hurt people, if we do.
	public void hurtPeople(int leniency) {
		if(killsPlayer) {
			player currPlayer = player.getPlayer();
			if(currPlayer.playerLoaded && currPlayer.isWithin(this.getIntX() + leniency, this.getIntY() + leniency, this.getIntX() + this.getWidth() - leniency, this.getIntY() + this.getHeight() - leniency)) {
				currPlayer.hurt(1, 1);
			}
		}
	}
	
	// Has the unit left the map?
	public boolean hasLeftMap() {
		return targetable && zone.getCurrentZone() != null && zone.getCurrentZone().isZoneLoaded() && (!groundTile.isOnGroundTile(this) && mode.getCurrentMode().equals("topDown"));
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
	
	// Face toward player
	public void faceTowardPlayer() {
		faceTowardThing(player.getPlayer());
	}
	
	// Face toward thing
	public void faceTowardThing(drawnObject d) {
		int angle = this.getAngleBetween(d);
		
		if(mode.getCurrentMode().equals("topDown") || stuck) {
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
		else {
			if(angle >= 0 && angle <= 180) {
				this.facingDirection = "Right";
			}
			else {
				this.facingDirection = "Left";
			}
		}
	}
	
	//////////////////////////
	//// ISSUING COMMANDS ////
	//////////////////////////
	
	// Wait command
	private float waitFor = 0;
	private long waitStart = 0;
	
	// Store the commands we need to issue.
	private commandList allCommands; // Current commands we will issue. This list gets slowly reduced as commands get issued.
	private commandList repeatCommands; // List of commands we will repeat. List is never reduced, since it needs to repeat.
	
	// Is the current command complete?
	protected boolean currentCommandComplete = false;
	
	// Repeat commands
	public void repeatCommands(commandList c) {
		setRepeatCommands(c);
	}
	
	// Do commands once
	public void doCommandsOnce(commandList c) {
		allCommands = c;
	}

	// Do commands
	public void doCommands() {
		
		// If we have commands in our allCommands queue left to issue.
		if(getAllCommands() != null && getAllCommands().size() > 0) {
			
			
			// Get the current command because we have to do shit with it.
			unitCommand currentCommand = getAllCommands().get(0);
			
			// Deal with each command type.
			// AKA, don't move to the next command on a move command
			// if we are still moving, etc.
			if(currentCommand instanceof moveCommand) {
				
				// Only move to the next command when applicable.
				if(currentCommand.isIssued()) {
				
					// If we have stopped moving. This command is done.
					if(!isMoving()) {
						getAllCommands().remove(0);
						doCommands();
					}
				}
				
				// Issue the command if it hasn't yet been issued.
				else {
					currentCommand.setIssued(true);
					moveCommand moveCommand = (moveCommand) currentCommand;
					moveTo((int)moveCommand.getX(), (int)moveCommand.getY());
				}
			}
			
			else if(currentCommand instanceof waitCommand) {
				
				// Only move to the next command when applicable.
				if(currentCommand.isIssued()) {
					
					// Time has elapsed. This command is done.
					if(waitStart!=0 && time.getTime() - waitStart > waitFor*1000) { 
						waitStart = 0;
						getAllCommands().remove(0);
						doCommands();
					}
				}
				
				// Issue the command if it hasn't yet been issued.
				else {
					currentCommand.setIssued(true);
					waitCommand waitCommand = (waitCommand) currentCommand;
					waitFor = (float)waitCommand.getHowLong();
					waitStart = time.getTime();
				}
				
			}
			
			else if(currentCommand instanceof slashCommand) {
				
				// Only move to the next command when applicable.
				if(currentCommand.isIssued()) {
					
					// Slash is over. This command is done.
					if(currentCommandComplete) { 
						getAllCommands().remove(0);
						doCommands();
					}
				}
				
				// Issue the command if it hasn't yet been issued.
				else {
					currentCommand.setIssued(true);
					currentCommandComplete = false;
					slashCommand slashCommand = (slashCommand)currentCommand;
					slashTo((int)slashCommand.getX(), (int)slashCommand.getY());
				}

			}
			
			else {
				// Unknown command issued. Skip it.
				currentCommand.setIssued(true);
				currentCommandComplete = true;
				getAllCommands().remove(0);
				doCommands();
			}
		}
		
		// Otherwise, the allCommands queue is empty.
		else {
			
			// If we have repeat commands, repeat them.
			if(getRepeatCommands()!=null && getRepeatCommands().size() > 0) {
				setAllCommands(new commandList(getRepeatCommands()));
				doCommands();
			}
			
			// Otherwise, do nothing.
			else {
				
			}
			
		}
	}
	
	// Regular units do not slash.
	public void slashTo(int x, int y) {
		currentCommandComplete = true;
	}
	
	// Move in place.
	public void stopMoveInPlace() {
		moveSpeed = oldMoveSpeed;
	}
	
	// Stop repaet commands
	public void stopRepeatCommands() {
		stopMove("all");
		setRepeatCommands(null);
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
	
	// Require units to have some sort of AI.
	public void updateUnit() {
	}
	
	// Set gravity on or off.
	public static void setGravity(boolean b) {
		gravity = b;
	}
	
	// Provide gravity
	public void gravity() {
		if(gravity && !isStuck()) {
			
			// Set touching ground.
			touchingGround = chunk.impassableChunks != null && 
					chunk.impassableChunks.size() > 0 &&
					fallSpeed >= -1
					&& chunk.getGroundChunk(this, (int)getDoubleX(), (int)(getDoubleY()+fallSpeed+1)) != null;
			
			// Accelerate
			if(getFallSpeed() < DEFAULT_GRAVITY_MAX_VELOCITY &&
					!touchingGround) {
				if(!tryJump && !doubleJumping) {
					setFallSpeed(getFallSpeed() + DEFAULT_SHORT_JUMP_ACCEL);
				}
				else {
					// If we are jump shortening.
					setFallSpeed(getFallSpeed() + DEFAULT_LONG_JUMP_ACCEL);
				}
				
				// Correct
				if(getFallSpeed() > DEFAULT_GRAVITY_MAX_VELOCITY) {
					setFallSpeed(DEFAULT_GRAVITY_MAX_VELOCITY);
				}
			}
			
			move(0,(int)getFallSpeed());
		}
	}
	
	// Knockback.
	public void knockBackNoCollision(int knockX, int knockY, int knockRadius, float overTime, int knockSpeed) {
		if(!gettingKnockedBack) {
			oldCollision = collisionOn;
			setCollisionOn(false);
			knockBackStart = time.getTime();
			gettingKnockedBack = true;
			this.knockTime = overTime;	
			this.knockSpeed = knockSpeed;
			
			// Calculate the new X and Y we need to knock them to, based off radius.
			double currentDegree = mathUtils.getAngleBetween(getIntX()+getWidth()/2, getIntY() + getHeight()/2, knockX, knockY);
			knockToX = (int) (getIntX() + (knockSpeed*overTime*1000)*Math.cos(Math.toRadians(currentDegree))); 
			knockToY = (int) (getIntY() + (knockSpeed*overTime*1000)*Math.sin(Math.toRadians(currentDegree)));
		}
	}
	
	// Knockback.
	public void knockBack(int knockX, int knockY, int knockRadius, float overTime, int knockSpeed) {
		if(!gettingKnockedBack) {
			knockBackStart = time.getTime();
			gettingKnockedBack = true;
			this.knockTime = overTime;	
			this.knockSpeed = knockSpeed;
			
			// Calculate the new X and Y we need to knock them to, based off radius.
			double currentDegree = mathUtils.getAngleBetween(getIntX()+getWidth()/2, getIntY() + getHeight()/2, knockX, knockY);
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
			}
		}
		
	}
	
	// Already attacked units
	private ArrayList<unit> alreadyAttackedUnits = new ArrayList<unit>();
	
	// Do combat mechanics.
/*	public void combat() {
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
	}*/
	
	// Attack is over
	public void attackOver() {
		alreadyAttackedUnits = new ArrayList<unit>();
	}
	
	// Initiate
	public static void initiate() {
		allUnits = new ArrayList<unit>();
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
		 getIntY() < y2 && 
		 getIntY() + + getHeight() > y1;
	}
	
	// Attack units
/*	public void attackUnits() {
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
	}*/
	
	// Force to take damage.
	public boolean forceHurt(int damage, float crit) {
		if(damage != 0) {
			
			if(killable) {
				if(healthPoints - crit*damage < 0) healthPoints = 0;
				else healthPoints -= crit*damage;
			}
			
			// Squirt blood
			int randomX = 0;
			int randomY = -platformerHeight/3 + utility.RNG.nextInt(platformerHeight/3 + 1);
			if(healthPoints > 0) {
				effect blood = new bloodSquirt(getIntX() - bloodSquirt.getDefaultWidth()/2 + topDownWidth/2 + randomX ,
						getIntY() - bloodSquirt.getDefaultHeight()/2 + platformerHeight/2 + randomY);
				blood.attachToObject(this);
			}
			reactToPain();
		}
		return true;
	}
	
	// Take damage. Ouch!
	public boolean hurt(int damage, float crit) {
		if(targetable) {
			return forceHurt(damage,crit);
		}
		else {
			return false;
		}
	}
	
	// React to pain.
	public void reactToPain() {
		
	}
	
	// Jump only once.
	private boolean alreadyJumped = false;
	
	// Start trying to jump.
	public void startJump() {
		tryJump = true;
	}
	
	// Stop trying to jump
	public void stopJump() {
		setAlreadyJumped(false);
		tryJump = false;
	}
	
	// Jump unit
	public void jump() {
		if(!isAlreadyJumped() && !isStuck() && gravity && !isJumping() && tryJump && touchingGround) {
			// Accelerate upward.
			setAlreadyJumped(true);
			setJumping(true);
			setFallSpeed(-getJumpSpeed());
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
		setMovingToAPoint(true);
		moveToX = newX;
		moveToY = newY;
		
		// Set rise/run
		setRiseRun();
	}
	
	// Check if next comamnd is movement
	public boolean nextCommandIsMovement() {
		return (allCommands!=null && allCommands.size() > 0) && 
				((allCommands.size() > 1 && allCommands.get(1) instanceof moveCommand) ||
				(allCommands.size() == 1 && getRepeatCommands() != null && getRepeatCommands().size() > 0 && getRepeatCommands().get(0) instanceof moveCommand));
	}
	
	// Get next command
	public unitCommand getNextCommand() {
		if(allCommands!=null && allCommands.size() > 0) {
			
			if(allCommands.size() > 1) return allCommands.get(1);
			else if(allCommands.size() == 1 && getRepeatCommands() != null && getRepeatCommands().size() > 0) return getRepeatCommands().get(0);
			
		}
		return null;
	}
	
	// Move towards a point
	public void moveTowards() {
		if(isMovingToAPoint()) {
			if(Math.abs(moveToX - getDoubleX()) < getMoveSpeed() && Math.abs(moveToY - getDoubleY()) < getMoveSpeed()) {
				
				// If the next command is movement, move in that direction by
				// whatever we overshoot our point by.
				
				/*if(nextCommandIsMovement()) {
					
					// Get the differences
					double diffX = moveToX - getDoubleX();
					double diffY = moveToY - getDoubleY();
					double distanceBetween = (float) Math.sqrt(diffY * diffY
							+ diffX * diffX);
					
					// Check if we overshot
					boolean overShot = (diffX > 0 && run > 0) || (diffX < 0 && run < 0) || (diffY > 0 && rise > 0) || (diffY < 0 && rise < 0);
					
					// If we overshot, move in the direction we are going to go
					// next by how much we overshot.
					if(overShot) {
						
						System.out.println("Overshot");
						
						setDoubleX(moveToX);
						setDoubleY(moveToY);
						
						moveCommand command = (moveCommand)getNextCommand();
						
						double yDistance = (command.getY() - getIntY());
						double xDistance = (command.getX() - getIntX());
						double distanceXY = (float) Math.sqrt(yDistance * yDistance
									+ xDistance * xDistance);
						
						// Calculate rise values.
						double floatRise = ((yDistance/distanceXY)*(float)distanceBetween);
						
						// Calculate run values.
						double floatRun = ((xDistance/distanceXY)*(float)distanceBetween);
						
						move(floatRun,floatRise);
					}
				}*/
				
				setDoubleX(moveToX);
				setDoubleY(moveToY);
	
				// Done moving to this point.
				setMovingToAPoint(false);
			}
			else {
				// Set facing direction.
				if(run < -0.49f) {
					setFacingDirection("Left");
				}
				else if(run > 0.49f) {
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
	}
	
	// Moving rise/run.
	protected double rise = 0;
	protected double run = 0;
	
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
	
	public boolean track = false;
	
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
		/// MOVEMENT TOWARD A POINT ///
		///////////////////////////////	
		moveTowards();
	}
	
	// Disabled movement?
	private boolean stunned = false;
	
	// Move unit
	public void moveUnit() {
		
		// Apply movement debuffs/buffs
		float buffedMoveSpeed = moveSpeed;
		//currentMoveSpeed += movementAcceleration;
		//if(currentMoveSpeed > moveSpeed || movementAcceleration == 0) currentMoveSpeed = moveSpeed;
		
		if(movementBuffs != null) {
			ArrayList<Class> appliedEffects = new ArrayList<Class>();
			
			for(int i = 0; i < movementBuffs.size(); i++) {
				if(!appliedEffects.contains(movementBuffs.get(i).getClass())) {
					buffedMoveSpeed *= movementBuffs.get(i).getMovementPercentage();
					appliedEffects.add(movementBuffs.get(i).getClass());
				}
			}
		}
		
		// Actual movement.
		if(isMovingLeft()) {
			
			// If we have no acceleration, use perfect movespeed.
			if(getMovementAcceleration() == 0) setMomentumX(-buffedMoveSpeed);
			
			// Otherwise, increment momentum.
			else setMomentumX(getMomentumX() - getMovementAcceleration());
			
			// Disallow surpassing minimum momentum.
			if(getMomentumX() < -buffedMoveSpeed) setMomentumX(-buffedMoveSpeed);
		}
		else if(isMovingRight()) {
			
			// If we have no acceleration, perfect movespeed.
			if(getMovementAcceleration() == 0) setMomentumX(buffedMoveSpeed);
			
			// Otherwise, increment momentum.
			else setMomentumX(getMomentumX() + getMovementAcceleration());
			
			// Set it above a certain threshold.
			if(getMomentumX() > buffedMoveSpeed) setMomentumX(+buffedMoveSpeed);
		}
		else {
			
			// If we have no acceleration, set their movespeed immediately to zero.
			if(getMovementAcceleration() == 0)  setMomentumX(0);
			
			else {
				// Reset x momentum slowly (make them slide).
				if(getMomentumX() < 0) setMomentumX(getMomentumX() + getMovementAcceleration()*5/8);
				if(getMomentumX() > 0) setMomentumX(getMomentumX() - getMovementAcceleration()*5/8);
				if(Math.abs(getMomentumX()) < getMovementAcceleration()*5/8) setMomentumX(0);
			}
		}
		
		// Only do these ones if we're in topDown mode.
		if(mode.getCurrentMode() == "topDown" || isStuck()) {
			if(isMovingUp()) {
				
				// Set momentum on no acceleration.
				if(getMovementAcceleration() == 0) setMomentumY(-buffedMoveSpeed);
				
				// Increment momentum.
				else setMomentumY(getMomentumY() - getMovementAcceleration());
				
				// Set momentum below a threshold.
				if(getMomentumY() < -buffedMoveSpeed) setMomentumY(-buffedMoveSpeed);
			}
			else if(isMovingDown()) {
				
				// Set movespeed if we have no acceleration.
				if(getMovementAcceleration() == 0) setMomentumY(buffedMoveSpeed);
				
				// Otherwise, increment momentum.
				else setMomentumY(getMomentumY() + getMovementAcceleration());
				
				// Set momentum if it's too high.
				if(getMomentumY() > buffedMoveSpeed) setMomentumY(+buffedMoveSpeed);
			}
			else {
				// If we have no acceleration, set their movespeed immediately to zero.
				if(getMovementAcceleration() == 0)  setMomentumY(0);
				
				else {
					
					// Make them slide on the Y axis.
					if(getMomentumY() < 0) setMomentumY(getMomentumY() + getMovementAcceleration()*5/8);
					if(getMomentumY() > 0) setMomentumY(getMomentumY() - getMovementAcceleration()*5/8);
					if(Math.abs(getMomentumY()) < getMovementAcceleration()*5/8) setMomentumY(0);
				}
			}
		}
		
		// Basic movement.
		float moveX = getMomentumX();
		float moveY = getMomentumY();
		
		// Deal with direction facing.
		if(isMovingLeft() && isMovingUp()) setFacingDirection("Left");
		else if(isMovingRight() && isMovingUp()) setFacingDirection("Right");
		else if(isMovingLeft() && isMovingDown()) setFacingDirection("Left");
		else if(isMovingRight() && isMovingDown()) setFacingDirection("Right");
		else if(isMovingDown() && (mode.getCurrentMode() != "platformer" || isStuck())) setFacingDirection("Down");
		else if(isMovingUp() && (mode.getCurrentMode() != "platformer" || isStuck())) setFacingDirection("Up");
		else if(isMovingRight()) setFacingDirection("Right");
		else if(isMovingLeft()) setFacingDirection("Left");

		if(!isStunned()) {
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
	
	// Set next fall speed
	boolean zeroNextFallSpeed = false;
	
	// Unit has touched up
	public void touchUp() {
		
		// If they've touched up, place them closer to the ground.
		if(!zeroNextFallSpeed && getFallSpeed() < 0) {
			chunk ground = chunk.getGroundChunk(this, (int)getDoubleX(), (int)(getDoubleY() + getFallSpeed()));
			if(ground != null) {
				setFallSpeed(-(this.getIntY() - ground.getIntY() - ground.getHeight()));
				zeroNextFallSpeed = true;
			}
		}
		else {
			setFallSpeed(getFallSpeed()/2);
			zeroNextFallSpeed = false;
		}
	
	}
	
	// Unit has touched down.
	public void touchDown() {
		
		if(mode.getCurrentMode().equals("platformer")) {
			// Hold the fall speed but set the current to be 0.
			float oldFallSpeed = getFallSpeed();
			setFallSpeed(0);
			
			// If they've touched down, place them closer to the ground.
			chunk ground = chunk.getGroundChunk(this, (int)getDoubleX(), (int)(getDoubleY() + oldFallSpeed));
			if(ground != null) {
				setFallSpeed(ground.getIntY() - (this.getIntY() + this.getHeight()));
			}
			
			// They can jump again if they've touched down.
			//if(doubleJumping) alreadyJumped = false;
			doubleJumping = false;
			setInAir((getFallSpeed() != 0));
			if(!inAir) setJumping(false);
			respondToTouchDown();
		}
	}
	
	// Respond to touchdown
	public void respondToTouchDown() {
		
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
				if(xyCollide.x != 0 || leftRegion.x != 0 /*|| leftMap.x != 0*/) {
					pathFindingStuck = true;
					if(xyCollide.x!=0) actualMoveX = 0;
					if(leftRegion.x!=0) actualMoveX = 0;
					
					// If we hit something stop momentum
					if(actualMoveX == 0) setMomentumX(0);
				}
				
				// Lots more to check for platformer mode.
				if(xyCollide.y != 0 || leftRegion.y != 0 /*|| leftMap.y != 0*/) {
					
					// Yes, we're stuck.
					pathFindingStuck = true;
				
					// If gravity is on and is the terrain loaded?
					if (gravity && zone.getCurrentZone() != null && zone.getCurrentZone().isZoneLoaded()) { 
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
					
					// If we hit something stop momentum
					if(actualMoveX == 0) setMomentumY(0);
				}
				
				// Are we entering the air (by a significant amount?)
				if(Math.abs(actualMoveY) > getMoveSpeed()) {
					setInAir(true);
				}
			}
			
			// Slow movement
			if(allowSlowMovement && moveFrame<2 && (actualMoveY!=0||actualMoveX!=0)) {
				moveFrame++;
				if(mode.getCurrentMode().equals("topDown")) actualMoveY /= (3-moveFrame);
				actualMoveX /= (3-moveFrame);
			}
			
			// Deal with animations.
			dealWithAnimations((int)actualMoveX,(int)actualMoveY);
	
			// Move the camera if it's there.
			if(attachedCamera != null) {
				attachedCamera.setX((int)(getDoubleX() + actualMoveX));
				attachedCamera.setY((int)(getDoubleY() + actualMoveY));
			}
			
			// Move attached objects
			if(attachedObjects != null) {
				for(int i = 0; i < attachedObjects.size(); i++) {
					attachedObjects.get(i).setDoubleX(this.getDoubleX() + actualMoveX + attachedObjects.get(i).getRelativeX());
					attachedObjects.get(i).setDoubleY(this.getDoubleY() + actualMoveY + attachedObjects.get(i).getRelativeY());
				}
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
		
		// First move frame is true since we stopped
		if(!isMoving()) {
			moveFrame = 0;
		}
	}
	
	// Deal with movement animations.
	public void dealWithAnimations(int moveX, int moveY) {
		
		// topDown mode movement animations.
		if(mode.getCurrentMode().equals("topDown")) {
			if(isJumping()) {
				animate("jumping" + getFacingDirection());
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
			 if(!touchingGround && !isStuck()) {
				String face;
				if(getFacingDirection().equals("Up") || getFacingDirection().equals("Down")) {
					face = "Right";
				}
				else {
					face = getFacingDirection();
				}
				animate("jumping" + face);
			}
			else if(!(isAlreadyJumped() && tryJump && !movingHorizontally()) && ((isMoving() && isStuck()) || (!isStuck() && (!isMovingDown() || (isMovingLeft() || isMovingRight())) && isMoving()))) {
				
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
		//questIcon = new questMark(spawnX, spawnY, 0);
		//questIcon.attachToObject(this);
	}
	
	// Set a unit to have a quest.
	public void noQuest() {
		if(questIcon != null) {
		//	questIcon.destroy();
		}
	}
	
	/////////////////////////
	// Getters and setters //
	/////////////////////////
	
	// Moving horizontally boolean
	public boolean movingHorizontally() {
		boolean movingLeftAndRight = isMovingLeft() && isMovingRight();
		return (isMovingLeft() || isMovingRight()) && !movingLeftAndRight;
	}
	
	// Moving horizontally boolean
	public boolean movingVertically() {
		boolean movingUpAndDown = isMovingUp() && isMovingDown();
		return (isMovingUp() || isMovingDown()) && !movingUpAndDown;
	}
	
	// Combination of all movement functions boolean
	public boolean isMoving() {
		return movingVertically() || movingHorizontally() || isMovingToAPoint() || followingUnit;
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


	public float getMoveSpeed() {
		return moveSpeed;
	}

	public void setMoveSpeed(float f) {
		fixAnimationsBasedOnMoveSpeed(f);
		this.moveSpeed = f;
	}
	
	public void fixAnimationsBasedOnMoveSpeed(float newSpeed) {
		if(animations!=null) {
			for(int i = 0; i < animations.size(); i++) {
				animation currentAnimation = animations.get(i);
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

	public ArrayList<movementBuff> getMovementBuffs() {
		if(movementBuffs == null) movementBuffs = new ArrayList<movementBuff>();
		return movementBuffs;
	}

	public void setMovementBuffs(ArrayList<movementBuff> movementBuffs) {
		this.movementBuffs = movementBuffs;
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

	public commandList getAllCommands() {
		return allCommands;
	}
	
	// Get previous move command
	public positionedCommand getPreviousPosCommand(int j) {
		
		if(repeatCommands == null) return null;
		
		// Get the previous command that is positioned
		int x = 0;
		for(int n = j; ; n--) {
			if(x >= repeatCommands.size()) break;
			if(n < 0) n = repeatCommands.size() - 1;
			if(repeatCommands.get(n) instanceof positionedMovementCommand) {
				return (positionedCommand)repeatCommands.get(n);
			}
			x++;
		}
		return null;
	}

	public void setAllCommands(commandList allCommands) {
		this.allCommands = allCommands;
	}

	public float getJumpSpeed() {
		return jumpSpeed;
	}

	public void setJumpSpeed(float jumpSpeed) {
		this.jumpSpeed = jumpSpeed;
	}

	public commandList getRepeatCommands() {
		return repeatCommands;
	}

	public void setRepeatCommands(commandList repeatCommands) {
		this.repeatCommands = repeatCommands;
	}

	public boolean isCanSlash() {
		return canSlash;
	}

	public void setCanSlash(boolean canSlash) {
		this.canSlash = canSlash;
	}

	public boolean isCanSlashSummon() {
		return canSlashSummon;
	}

	public void setCanSlashSummon(boolean canSlashSummon) {
		this.canSlashSummon = canSlashSummon;
	}

	public float getMovementAcceleration() {
		return movementAcceleration;
	}

	public void setMovementAcceleration(float movementAcceleration) {
		this.movementAcceleration = movementAcceleration;
	}

	public float getMomentumX() {
		return momentumX;
	}

	public void setMomentumX(float momentumX) {
		this.momentumX = momentumX;
	}

	public float getMomentumY() {
		return momentumY;
	}

	public void setMomentumY(float momentumY) {
		this.momentumY = momentumY;
	}

	public float getDestroyTimer() {
		return destroyTimer;
	}

	public void setDestroyTimer(float destroyTimer) {
		this.destroyTimer = destroyTimer;
	}

	public boolean isMovingToAPoint() {
		return movingToAPoint;
	}

	public void setMovingToAPoint(boolean movingToAPoint) {
		this.movingToAPoint = movingToAPoint;
	}

	public Color getNameColor() {
		return nameColor;
	}

	public void setNameColor(Color nameColor) {
		this.nameColor = nameColor;
	}

	public boolean isAlreadyJumped() {
		return alreadyJumped;
	}

	public void setAlreadyJumped(boolean alreadyJumped) {
		this.alreadyJumped = alreadyJumped;
	}

	public boolean isJumping() {
		return jumping;
	}

	public void setJumping(boolean jumping) {
		this.jumping = jumping;
	}

	public float getFallSpeed() {
		return fallSpeed;
	}

	public void setFallSpeed(float fallSpeed) {
		this.fallSpeed = fallSpeed;
	}

	public boolean isDoubleJumping() {
		return doubleJumping;
	}

	public void setDoubleJumping(boolean doubleJumping) {
		this.doubleJumping = doubleJumping;
	}

	public boolean isAllowSlowMovement() {
		return allowSlowMovement;
	}

	public void setAllowSlowMovement(boolean allowSlowMovement) {
		this.allowSlowMovement = allowSlowMovement;
	}

	public boolean isUnitIsDead() {
		return unitIsDead;
	}

	public void setUnitIsDead(boolean unitIsDead) {
		this.unitIsDead = unitIsDead;
	}

	public ArrayList<buff> getBuffs() {
		return buffs;
	}

	public void setBuffs(ArrayList<buff> buffs) {
		this.buffs = buffs;
	}

	public boolean isInAir() {
		return inAir;
	}

	public void setInAir(boolean inAir) {
		this.inAir = inAir;
	}

	public boolean isStunned() {
		return stunned;
	}

	public void setStunned(boolean stunned) {
		this.stunned = stunned;
	}

	public BufferedImage getDialogueBox() {
		return dialogueBox;
	}

	public void setDialogueBox(BufferedImage dialogueBox) {
		this.dialogueBox = dialogueBox;
	}
	
}