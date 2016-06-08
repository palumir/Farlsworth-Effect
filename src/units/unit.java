package units;

import java.awt.Color;
import java.awt.Graphics;

import drawing.camera;
import drawing.drawnObject;
import drawing.sprites.animation;
import drawing.sprites.animationPack;
import drawing.sprites.spriteSheet;
import modes.mode;
import terrain.chunk;
import terrain.doodads.general.questMark;
import utilities.intTuple;
import utilities.utility;

public abstract class unit extends drawnObject  { // shape for now sprite later
	
	/////////////////////////
	////// DEFAULTS /////////
	/////////////////////////
	
	// Default movespeed.
	private int DEFAULT_UNIT_MOVESPEED = 1;
	
	// Gravity defaults.
	private static boolean DEFAULT_GRAVITY_STATE = false;
	private static float DEFAULT_GRAVITY_ACCELERATION = 0.4f;
	private static float DEFAULT_GRAVITY_MAX_VELOCITY = 20;
	private static float DEFAULT_JUMPSPEED = 8;
	
	// Animation defaults.
	private String DEFAULT_FACING_DIRECTION = "Down";
	
	///////////////
	/// GLOBALS ///
	///////////////
	private static boolean gravity = DEFAULT_GRAVITY_STATE;

	////////////////
	//// FIELDS ////
	////////////////
	
	// The actual unit type.
	private unitType typeOfUnit;
	
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
		animations = u.getAnimations();
		moveSpeed = u.getMoveSpeed();
		jumpSpeed = u.getJumpSpeed();
		typeOfUnit = u;
	}
	
	// Update unit
	@Override
	public void update() {
		if(currentAnimation != null) currentAnimation.playAnimation();
		gravity();
		jump();
		moveUnit();
		AI();
	}
	
	// Require units to have some sort of AI.
	public abstract void AI();
	
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
	
	// Move unit
	public void moveUnit() {
		int moveX = 0;
		int moveY = 0;
		
		// Actual movement.
		if(movingLeft) moveX -= moveSpeed;
		if(movingRight) moveX += moveSpeed;
		
		// Only do these ones if we're in topDown mode.
		if(mode.getCurrentMode() == "topDown") {
			if(movingUp) moveY -= moveSpeed;
			if(movingDown) moveY += moveSpeed;
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
				if(Math.abs(actualMoveY) > moveSpeed) {
					inAir = true;
				}
			}
			
			// Deal with animations.
			movementAnimation(actualMoveX,actualMoveY);
	
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
	public void movementAnimation(int moveX, int moveY) {
		
		// topDown mode movement animations.
		if(mode.getCurrentMode() == "topDown") {
			if(moveX != 0 || moveY != 0) {
				animate("running" + getFacingDirection());
			}
			else {
				animate("standing" + getFacingDirection());
			}
		}
		
		// platformer movement animations.
		if(mode.getCurrentMode() == "platformer") {
			if(inAir) {
				animate("jumping" + getFacingDirection());
			}
			else if(moveX != 0 || moveY != 0) {
				// If we are running.
				animate("running" + getFacingDirection());
			}
			else if(moveX == 0 && moveY == 0){
				animate("standing" + getFacingDirection());
			}
		}
	}
	
	// Cause unit to perform an animation.
	public void animate(String animationName) {
		currentAnimation = animations.getAnimation(animationName);
	}
	
	// Draw the unit. TODO: JUST DRAWS ONE SPRITE LOL
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
		
		// Draw the outskirts of the sprite.
		if(showSpriteBox) {
			g.setColor(Color.red);
			g.drawRect(drawX,
					   drawY, 
					   getObjectSpriteSheet().getSpriteWidth(), 
					   getObjectSpriteSheet().getSpriteHeight());
		}
		
		// Draw the hitbox of the image in green.
		if(showHitBox) {
			g.setColor(Color.green);
			g.drawRect(drawX - (- (getObjectSpriteSheet().getSpriteWidth()/2 - width/2) - getHitBoxAdjustmentX()),
					   drawY - (- (getObjectSpriteSheet().getSpriteHeight()/2 - height/2) - getHitBoxAdjustmentY()), 
				       width, 
				       height);
		}
		
		// Draw the x,y coordinates of the unit.
		if(showUnitPosition) {
			g.setColor(Color.white);
			g.drawString(getX() + "," + getY(),
					   drawX,
					   drawY);
		}
	}
	
	// Set a unit to have a quest.
	public void hasQuest() {
		int spawnX = (getX() + width/2) - questMark.DEFAULT_CHUNK_WIDTH/2;
		int spawnY = (int)(getY() - 2.5f*questMark.DEFAULT_CHUNK_HEIGHT);
		questIcon = new questMark(spawnX, spawnY, 0);
	}
	
	/////////////////////////
	// Getters and setters //
	/////////////////////////
	public boolean isMoving() {
		return movingLeft || movingRight || movingUp || movingDown;
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
	
}