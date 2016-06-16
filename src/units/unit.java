package units;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import drawing.camera;
import drawing.drawnObject;
import drawing.spriteSheet;
import drawing.animation.animation;
import drawing.animation.animationPack;
import drawing.userInterface.playerHealthBar;
import effects.effect;
import effects.effectTypes.bloodSquirt;
import effects.effectTypes.critBloodSquirt;
import effects.effectTypes.floatingNumber;
import modes.mode;
import sounds.sound;
import terrain.chunk;
import terrain.doodads.general.questMark;
import utilities.intTuple;
import utilities.time;
import utilities.utility;

public abstract class unit extends drawnObject  { // shape for now sprite later
	
	/////////////////////////
	////// DEFAULTS /////////
	/////////////////////////
	
	// List of all units
	private static ArrayList<unit> allUnits = new ArrayList<unit>();
	
	// Default movespeed.
	private static int DEFAULT_UNIT_MOVESPEED = 1;
	public static int DEFAULT_MAX_MOVESPEED = 10;
	
	// Gravity defaults.
	private static boolean DEFAULT_GRAVITY_STATE = false;
	private static float DEFAULT_GRAVITY_ACCELERATION = 0.4f;
	private static float DEFAULT_GRAVITY_MAX_VELOCITY = 20;
	private static float DEFAULT_JUMPSPEED = 8;
	
	// Animation defaults.
	private String DEFAULT_FACING_DIRECTION = "Down";
	
	// Combat defaults.
	private int DEFAULT_HP = 10;
	private int DEFAULT_ATTACK_DAMAGE = 1;
	private float DEFAULT_BAT = 1f;
	private float DEFAULT_ATTACK_TIME = 1.5f;
	private int DEFAULT_ATTACK_RANGE = 0;
	private float DEFAULT_ATTACK_VARIABILITY = 0.2f; // How much the range of hits is. 20% both ways.
	private float DEFAULT_CRIT_CHANCE = .15f;
	private float DEFAULT_CRIT_DAMAGE = 1.5f;
	protected boolean showAttackRange = false;
	
	// Default healthbarsize
	private int DEFAULT_HEALTHBAR_HEIGHT = 6;
	private int DEFAULT_HEALTHBAR_WIDTH = 40;
	
	// Colors for combat.
	private Color DEFAULT_DAMAGE_COLOR = Color.white;
	private Color DEFAULT_CRIT_COLOR = Color.yellow;
	
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
	protected unitType typeOfUnit;
	
	// Width and height for topDown and platformer
	protected int topDownWidth = 0;
	protected int topDownHeight = 0;
	protected int platformerWidth = 0;
	protected int platformerHeight = 0;
	
	// Combat
	// Health points
	protected int maxHealthPoints = DEFAULT_HP;
	protected int healthPoints = DEFAULT_HP;
	
	// Damage
	private int attackDamage = DEFAULT_ATTACK_DAMAGE;
	
	// Attack time.
	private float baseAttackTime = DEFAULT_BAT;
	private float attackTime = DEFAULT_ATTACK_TIME;
	
	// Attack range.
	private int attackWidth = DEFAULT_ATTACK_RANGE;
	private int attackLength = DEFAULT_ATTACK_RANGE;
	
	// Unit stats.
	protected float attackMultiplier = 1f;
	protected float attackVariability = DEFAULT_ATTACK_VARIABILITY; // Percentage
	protected float critChance = DEFAULT_CRIT_CHANCE;
	protected float critDamage = DEFAULT_CRIT_DAMAGE;
	
	// Attacking/getting attacked mechanics
	private boolean attackable = false;
	private boolean attacking = false;
	private boolean alreadyAttacked = false;
	private double startAttackTime = 0;
	
	// Combat sounds
	protected sound attackSound = null;
	
	// Gravity
	private float jumpSpeed = DEFAULT_JUMPSPEED;
	private float fallSpeed = 0;
	private boolean jumping = false;
	private boolean tryJump = false;
	private boolean touchingGround = false;
	private boolean inAir = true;
	
	// Movement
	private int moveSpeed = DEFAULT_UNIT_MOVESPEED;
	private boolean movingLeft = false;
	private boolean movingRight = false;
	private boolean movingDown = false;
	private boolean movingUp = false;
	private String facingDirection = DEFAULT_FACING_DIRECTION;
	private boolean collisionOn = true;
	
	// Quests
	private chunk questIcon = null;
	
	// Sprite stuff.
	private animationPack animations;
	private animation currentAnimation = null;
	
	///////////////
	/// METHODS ///
	///////////////

	// Constructor
	public unit(unitType u, int newX, int newY) {
		super(u.getUnitTypeSpriteSheet(), newX, newY, u.getWidth(), u.getHeight());	
		setAnimations(u.getAnimations());
		setMoveSpeed(u.getMoveSpeed());
		jumpSpeed = u.getJumpSpeed();
		typeOfUnit = u;
		
		// Add to list
		getAllUnits().add(this);
	}
	
	// Update unit
	@Override
	public void update() {
		if(currentAnimation != null) currentAnimation.playAnimation();
		gravity();
		jump();
		moveUnit();
		combat();
		aliveOrDead();
		updateUnit();
	}
	
	// Is the unit alive or dead
	public void aliveOrDead() {
		
		// Kill unit if we're dead.
		if(healthPoints <= 0) {
			die();
		}
	}
	
	// Kill unit
	public void die() {
		
		// Remove from game.
		destroy();
		
		// Do a huge blood squirt.
		effect blood = new critBloodSquirt(getX() - critBloodSquirt.getDefaultWidth()/2 + topDownWidth/2,
				   getY() - critBloodSquirt.getDefaultHeight()/2);
	}
	
	// Require units to have some sort of AI.
	public abstract void updateUnit();
	
	// Set gravity on or off.
	public static void setGravity(boolean b) {
		gravity = b;
	}
	
	// Provide gravity
	public void gravity() {
		if(gravity) {
			
			// Accelerate
			if(fallSpeed < DEFAULT_GRAVITY_MAX_VELOCITY){
				fallSpeed += DEFAULT_GRAVITY_ACCELERATION;
			}
			
			move(0,(int)fallSpeed);
		}
	}
	
	// Do combat mechanics.
	public void combat() {
		
		// Attack if we are attacking.
		if(attacking) {
			// Do the attack if our BAT is over.
			if(!alreadyAttacked && time.getTime() - startAttackTime > baseAttackTime*1000) {
				int x1 = 0;
				int x2 = 0;
				int y1 = 0;
				int y2 = 0;
				
				// Get the box we will attack in if facing left.
				if(facingDirection.equals("Left")) {
					int heightMidPoint = getY() + getHeight()/2;
					y1 = heightMidPoint - getAttackWidth()/2;
					y2 = heightMidPoint + getAttackWidth()/2;
					x1 = getX() - getAttackLength();
					x2 = getX() + getWidth();
				}
				
				// Get the box we will attack in if facing right.
				if(facingDirection.equals("Right")) {
					int heightMidPoint = getY() + getHeight()/2;
					y1 = heightMidPoint - getAttackWidth()/2;
					y2 = heightMidPoint + getAttackWidth()/2;
					x1 = getX();
					x2 = getX() + getWidth() + getAttackLength();
				}
				
				// Get the box we will attack in facing up.
				if(facingDirection.equals("Up")) {
					int widthMidPoint = getX() + getWidth()/2;
					x1 = widthMidPoint - getAttackWidth()/2;
					x2 = widthMidPoint + getAttackWidth()/2;
					y1 = getY() - getAttackLength();
					y2 = getY() + getHeight();
				}
				
				// Get the box we will attack in facing down.
				if(facingDirection.equals("Down")) {
					int widthMidPoint = getX() + getWidth()/2;
					x1 = widthMidPoint - getAttackWidth()/2;
					x2 = widthMidPoint + getAttackWidth()/2;
					y1 = getY();
					y2 = getY() + getHeight() + getAttackLength();
				}
				attackUnits(getUnitsInBox(x1,y1,x2,y2));
				alreadyAttacked = true;
			}
			if(time.getTime() - startAttackTime > getAttackTime()*1000) {
				alreadyAttacked = false;
				attacking = false;
			}
		}
	}
	
	// Is in attack range?
	public boolean isInAttackRange(unit u, int differential) {
		int x1 = 0;
		int x2 = 0;
		int y1 = 0;
		int y2 = 0;
		
		// Get the box we will attack in if facing left.
		if(facingDirection.equals("Left")) {
			int heightMidPoint = getY() + getHeight()/2;
			y1 = heightMidPoint - getAttackWidth()/2 + differential/2;
			y2 = heightMidPoint + getAttackWidth()/2 - differential/2;
			x1 = getX() - getAttackLength() + differential;
			x2 = getX() + getWidth();
		}
		
		// Get the box we will attack in if facing right.
		if(facingDirection.equals("Right")) {
			int heightMidPoint = getY() + getHeight()/2;
			y1 = heightMidPoint - getAttackWidth()/2 + differential/2;
			y2 = heightMidPoint + getAttackWidth()/2 - differential/2;
			x1 = getX();
			x2 = getX() + getWidth() + getAttackLength() - differential;
		}
		
		// Get the box we will attack in facing up.
		if(facingDirection.equals("Up")) {
			int widthMidPoint = getX() + getWidth()/2;
			x1 = widthMidPoint - getAttackWidth()/2 + differential/2;
			x2 = widthMidPoint + getAttackWidth()/2 - differential/2;
			y1 = getY() - getAttackLength() + differential;
			y2 = getY() + getHeight();
		}
		
		// Get the box we will attack in facing down.
		if(facingDirection.equals("Down")) {
			int widthMidPoint = getX() + getWidth()/2;
			x1 = widthMidPoint - getAttackWidth()/2 + differential/2;
			x2 = widthMidPoint + getAttackWidth()/2 - differential/2;
			y1 = getY();
			y2 = getY() + getHeight() + getAttackLength() - differential;
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
			if(u.getX() < x2 && 
					 u.getX() + u.getWidth() > x1 && 
					 u.getY() < y2 && 
					 u.getY() + u.getHeight() > y1) {
				returnList.add(u);
			}
		}
		if(returnList.size()==0) return null;
		return returnList;
	}
	
	// Attack units
	public void attackUnits(ArrayList<unit> unitsToAttack) {
		if(unitsToAttack!=null) {
			for(int i = 0; i < unitsToAttack.size(); i++) {
				unit currentUnit = unitsToAttack.get(i);
				
				// Don't hit yourself.
				if(this!=currentUnit) {
					
					// Hit for their damage times their multiplier.
					float variabilityMult = 1;
					if(attackVariability == 0) variabilityMult = 1;
					else variabilityMult = 1f + attackVariability - ((float)utility.RNG.nextInt((int)(2*attackVariability*100))/100f);
					int actualDamageDone = (int) (this.getAttackDamage()*attackMultiplier*variabilityMult);
					
					// Did we crit?
					float crit = 1f;
					if(critChance*100 >= utility.RNG.nextInt(100)) crit = critDamage;
					
					// Players can hit anything.
					if(this instanceof player) {
						currentUnit.hurt(actualDamageDone, crit);
					}
					
					// Enemies can't hit eachother.
					else if(!(this instanceof player) && currentUnit instanceof player) {
						currentUnit.hurt(actualDamageDone, crit);
					}
				}
			}
		}
	}
	
	// Take damage. Ouch!
	public void hurt(int damage, float crit) {
		if(attackable) {
			healthPoints -= crit*damage;
		}
		
		// Crit
		if(crit != 1f) {
			effect e = new floatingNumber((int) (crit*damage), DEFAULT_CRIT_COLOR, getX() + getWidth()/2, getY() + getHeight()/2);
		}
		// Non crit.
		else {
			effect e = new floatingNumber(damage, DEFAULT_DAMAGE_COLOR, getX() + getWidth()/2, getY() + getHeight()/2);
		}
		
		// Squirt blood
		int randomX = 0;
		int randomY = -platformerHeight/3 + utility.RNG.nextInt(platformerHeight/3);
		effect blood = new bloodSquirt(getX() - bloodSquirt.getDefaultWidth()/2 + topDownWidth/2 + randomX ,
				   getY() - bloodSquirt.getDefaultHeight()/2 + platformerHeight/2 + randomY);
		reactToPain();
	}
	
	// React to pain.
	public abstract void reactToPain();
	
	// Start attacking.
	public void attack() {
		if(!attacking) {
			
			// We are attacking of course.
			if(attackSound != null) attackSound.playSound(getX(), getY(), DEFAULT_ATTACK_SOUND_RADIUS, 0.8f);
			attacking = true;
			startAttackTime = time.getTime();
		}
	}
	
	// Start trying to jump.
	public void startJump() {
		tryJump = true;
	}
	
	// Stop trying to jump
	public void stopJump() {
		tryJump = false;
	}
	
	// Jump unit
	public void jump() {
		if(gravity && !jumping && tryJump && touchingGround) {
			// Accelerate upward.
			jumping = true;
			fallSpeed = -jumpSpeed;
		}
	}
	
	// Follow a unit.
	public void follow(unit u) {
		moveTowards(u.getX(), u.getY());
	}
	
	// Move towards a spot.
	public void moveTowards(int moveX, int moveY) {
		
		// Reset movement.
		stopMove("all");
		
		// If we are there, stop.
		if(Math.abs(getX() - moveX) <= 3*moveSpeed &&  Math.abs(getY() - moveY) <= moveSpeed + 1) {
			// Don't move.
		}
		else {
			// Move towards unit.
			if(getX() - moveX < 0 && Math.abs(getX() - moveX) > 3*moveSpeed) movingRight = true;
			if(getX() - moveX > 0 && Math.abs(getX() - moveX) > 3*moveSpeed) movingLeft = true;
			if(getY() - moveY < 0 && Math.abs(getY() - moveY) > 3*moveSpeed) movingDown = true;
			if(getY() - moveY > 0 && Math.abs(getY() - moveY) > 3*moveSpeed) movingUp = true;
		}
	}
	
	// Move unit
	public void moveUnit() {
		int moveX = 0;
		int moveY = 0;
		
		// Actual movement.
		if(movingLeft) moveX -= getMoveSpeed();
		if(movingRight) moveX += getMoveSpeed();
		
		// Only do these ones if we're in topDown mode.
		if(mode.getCurrentMode() == "topDown") {
			if(movingUp) moveY -= getMoveSpeed();
			if(movingDown) moveY += getMoveSpeed();
		}
		
		// Deal with direction facing.
		if(movingLeft && movingUp) setFacingDirection("Left");
		else if(movingRight && movingUp) setFacingDirection("Right");
		else if(movingLeft && movingDown) setFacingDirection("Left");
		else if(movingRight && movingDown) setFacingDirection("Right");
		else if(movingDown && mode.getCurrentMode() != "platformer") setFacingDirection("Down");
		else if(movingUp && mode.getCurrentMode() != "platformer") setFacingDirection("Up");
		else if(movingRight) setFacingDirection("Right");
		else if(movingLeft) setFacingDirection("Left");
		
		// Actually move the unit.
		move(moveX, moveY);
	}
	
	// Move the unit in a specific direction.
	public void moveUnit(String direction) {
		
		// Reset everything.
		movingLeft = false;
		movingRight = false;
		movingUp = false;
		movingDown = false;
		
		// Move them in said direction.
		if(direction.equals("upLeft")) {
			movingLeft = true;
			movingUp = true;
		}
		if(direction.equals("upRight")) {
			movingRight = true;
			movingUp = true;
		}
		if(direction.equals("downLeft")) {
			movingLeft = true;
			movingDown = true;
		}
		if(direction.equals("downRight")) {
			movingRight = true;
			movingDown = true;
		}
		if(direction.equals("left")) {
			movingLeft = true;
		}
		if(direction.equals("right")) {
			movingRight = true;
		}
		if(direction.equals("up")) {
			movingUp = true;
		}
		if(direction.equals("down")) {
			movingDown = true;
		}
	}
	
	// Unit has touched up
	public void touchUp() {
		
		// Essentially, bonk unit's head of roof.
		fallSpeed = 0;
	}
	
	// Unit has touched down.
	public void touchDown() {
		
		// They can jump again if they've touched down.
		fallSpeed = 0;
		jumping = false;
		touchingGround = true;
		inAir = false;
	}
	
	// Move function
	public void move(int moveX, int moveY) {
		
		if(player.getCurrentPlayer() != null && 
			player.getCurrentPlayer().getPlayerZone()!=null && 
			player.getCurrentPlayer().getPlayerZone().isZoneLoaded()) {
			// Actual move x and y when all is said and done.
			int actualMoveX = moveX;
			int actualMoveY = moveY;
	
			if(collisionOn) {
				// Check if it collides with a chunk in the x or y plane.
				intTuple xyCollide = chunk.collidesWith(this, getX() + moveX, getY() + moveY);
				if(xyCollide.x == 1) actualMoveX = 0;
				
				// Lots more to check for platformer mode.
				if(xyCollide.y == 1) {
				
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
					actualMoveY = 0;
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
			dealWithAnimations(actualMoveX,actualMoveY);
	
			// Move the camera if it's there.
			if(attachedCamera != null) {
				attachedCamera.setX(attachedCamera.getX() + actualMoveX);
				attachedCamera.setY(attachedCamera.getY() + actualMoveY);
			}
			
			// Move the unit.
			setX(getX() + actualMoveX);
			setY(getY() + actualMoveY);
		}
	}
	
	// Start moving
	public void startMove(String direction) {
		
		// Start moving right.
		if(direction=="right") { 
			movingRight=true;
		}
		
		// Start moving left.
		if(direction=="left") { 
			movingLeft=true;
		}
		
		// Start moving up.
		if(direction=="up") {
			movingUp=true;
		}
		
		// Start moving down..
		if(direction=="down") {
			movingDown=true;
		}
	}
	
	// Stop moving
	public void stopMove(String direction) {
		
		// Stop moving in any direction.
		if(direction=="all") {
			movingRight=false;
			movingLeft=false;
			movingUp=false;
			movingDown=false;
		}
		
		// Stop moving right.
		if(direction=="right") { 
			movingRight=false;
		}
		
		// Stop moving left.
		if(direction=="left") {
			movingLeft=false;
		}
		
		// Stop moving up.
		if(direction=="up") {
			movingUp=false;
		}
		
		// Stop moving down.
		if(direction=="down") {
			movingDown=false;
		}
	}
	
	// Deal with movement animations.
	public void dealWithAnimations(int moveX, int moveY) {
		
		// topDown mode movement animations.
		if(mode.getCurrentMode() == "topDown") {
			if(attacking && !alreadyAttacked) {
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
		if(mode.getCurrentMode() == "platformer") {
			if(attacking && !alreadyAttacked) {
				// Play animation.
				animate("attacking" + facingDirection);
			}
			else if(inAir) {
				animate("jumping" + getFacingDirection());
			}
			else if(isMoving()) {
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
		animation a = getAnimations().getAnimation(animationName);
		if(a != null) {
			
			// Reset the frame if it's a new animation.
			if(currentAnimation != null && currentAnimation != a) {
				currentAnimation.setCurrentSprite(currentAnimation.getStartFrame());
			}
			
			// Set the animation.
			currentAnimation = a;
		}
	}
	
	// Draw the unit. 
	@Override
	public void drawObject(Graphics g) {
		// Of course only draw if the animation is not null.
		if(currentAnimation != null) {
			g.drawImage(currentAnimation.getCurrentFrame(), 
					drawX, 
					drawY, 
					getObjectSpriteSheet().getSpriteWidth(), 
					getObjectSpriteSheet().getSpriteHeight(), 
					null);
		}
		
		// Draw healthbar is hp is low.
		if(healthPoints < maxHealthPoints && !(this instanceof player)) {
			
			// % of HP left.
			int healthChunkSize = (int)(((float)getHealthPoints()/(float)getMaxHealthPoints())*DEFAULT_HEALTHBAR_WIDTH);
			
			// Adjustment
			int hpAdjustX = -DEFAULT_HEALTHBAR_WIDTH/2+getWidth()/2 ;
			int hpAdjustY = 0;
			
			// Draw the red.
			g.setColor(playerHealthBar.DEFAULT_LOST_HEALTH_COLOR);
			g.fillRect(drawX + hpAdjustX,
					   drawY + hpAdjustY,
					   DEFAULT_HEALTHBAR_WIDTH,
					   DEFAULT_HEALTHBAR_HEIGHT);
			
			// Draw the green chunks.
			g.setColor(playerHealthBar.DEFAULT_HEALTH_COLOR);
			g.fillRect(drawX + hpAdjustX,
					   drawY + hpAdjustY,
					   healthChunkSize,
					  DEFAULT_HEALTHBAR_HEIGHT);

			// Draw border.
			g.setColor(playerHealthBar.DEFAULT_BORDER_COLOR);
			g.drawRect(drawX + hpAdjustX,
					   drawY + hpAdjustY,
					   DEFAULT_HEALTHBAR_WIDTH,
					   DEFAULT_HEALTHBAR_HEIGHT);
		}
		
		// Draw the outskirts of the sprite.
		if(showSpriteBox) {
			g.setColor(Color.red);
			g.drawRect(drawX,
					   drawY, 
					   getObjectSpriteSheet().getSpriteWidth(), 
					   getObjectSpriteSheet().getSpriteHeight());
		}
		
		// Draw the x,y coordinates of the unit.
		if(showUnitPosition) {
			g.setColor(Color.white);
			g.drawString(getX() + "," + getY(),
					   drawX,
					   drawY);
		}
		
		// Show attack range.
		if(showAttackRange) {
			int x1 = 0;
			int x2 = 0;
			int y1 = 0;
			int y2 = 0;
			
			// Get the x and y of hitbox.
			int hitBoxX = drawX - (- (getObjectSpriteSheet().getSpriteWidth()/2 - getWidth()/2) - getHitBoxAdjustmentX());
			int hitBoxY = drawY - (- (getObjectSpriteSheet().getSpriteHeight()/2 - getHeight()/2) - getHitBoxAdjustmentY());
			
			// Get the box we will attack in if facing left.
			if(facingDirection.equals("Left")) {
				int heightMidPoint = hitBoxY + getHeight()/2;
				y1 = heightMidPoint - getAttackWidth()/2;
				y2 = heightMidPoint + getAttackWidth()/2;
				x1 = hitBoxX - getAttackLength();
				x2 = hitBoxX + getWidth();
			}
			
			// Get the box we will attack in if facing right.
			if(facingDirection.equals("Right")) {
				int heightMidPoint = hitBoxY + getHeight()/2;
				y1 = heightMidPoint - getAttackWidth()/2;
				y2 = heightMidPoint + getAttackWidth()/2;
				x1 = hitBoxX;
				x2 = hitBoxX + getWidth() + getAttackLength();
			}
			
			// Get the box we will attack in facing up.
			if(facingDirection.equals("Up")) {
				int widthMidPoint = hitBoxX + getWidth()/2;
				x1 = widthMidPoint - getAttackWidth()/2;
				x2 = widthMidPoint + getAttackWidth()/2;
				y1 = hitBoxY - getAttackLength();
				y2 = hitBoxY + getHeight();
			}
			
			// Get the box we will attack in facing down.
			if(facingDirection.equals("Down")) {
				int widthMidPoint = hitBoxX + getWidth()/2;
				x1 = widthMidPoint - getAttackWidth()/2;
				x2 = widthMidPoint + getAttackWidth()/2;
				y1 = hitBoxY;
				y2 = hitBoxY + getHeight() + getAttackLength();
			}
			g.setColor(Color.blue);
			g.drawRect(x1,y1,x2-x1,y2-y1);
		}
		
		// Draw the hitbox of the image in green.
		if(showHitBox) {
			g.setColor(Color.green);
			g.drawRect(drawX - (- (getObjectSpriteSheet().getSpriteWidth()/2 - getWidth()/2) - getHitBoxAdjustmentX()),
					   drawY - (- (getObjectSpriteSheet().getSpriteHeight()/2 - getHeight()/2) - getHitBoxAdjustmentY()), 
				       getWidth(), 
				       getHeight());
		}
	}
	
	// Set a unit to have a quest.
	public void hasQuest() {
		int spawnX = (getX() + getWidth()/2) - questMark.DEFAULT_CHUNK_WIDTH/2;
		int spawnY = (int)(getY() - 2.5f*questMark.DEFAULT_CHUNK_HEIGHT);
		questIcon = new questMark(spawnX, spawnY, 0);
	}
	
	// Set a unit to have a quest.
	public void noQuest() {
		if(questIcon != null) questIcon.destroy();
	}
	
	/////////////////////////
	// Getters and setters //
	/////////////////////////
	public boolean isMoving() {
		boolean movingLeftAndRight = movingLeft && movingRight;
		boolean movingUpAndDown = movingUp && movingDown;
		boolean movingHorizontally = (movingLeft || movingRight) && !movingLeftAndRight;
		boolean movingVertically = (movingUp || movingDown) && !movingUpAndDown;
		return movingVertically || movingHorizontally;
	}
	
	public void setCollision(boolean b) {
		collisionOn = b;
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

	public boolean isAttackable() {
		return attackable;
	}

	public void setAttackable(boolean attackable) {
		this.attackable = attackable;
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

	public float getBaseAttackTime() {
		return baseAttackTime;
	}

	public void setBaseAttackTime(float baseAttackTime) {
		this.baseAttackTime = baseAttackTime;
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

	public int getMoveSpeed() {
		return moveSpeed;
	}

	public void setMoveSpeed(int moveSpeed) {
		this.moveSpeed = moveSpeed;
	}

	public static ArrayList<unit> getAllUnits() {
		return allUnits;
	}

	public static void setAllUnits(ArrayList<unit> allUnits) {
		unit.allUnits = allUnits;
	}
	
}