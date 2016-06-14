package units;

import java.awt.Color;

import drawing.spriteSheet;
import drawing.animation.animationPack;
import drawing.spriteSheet.spriteSheetInfo;

public class unitType {
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Movespeed
	public static int DEFAULT_MOVESPEED = 1;
	
	// Color TODO: irrelevant?
	public static Color DEFAULT_COLOR = Color.white;
	
	///////////////
	/// GLOBALS ///
	///////////////
	
	// Initiate the pre-defined unit types.
	public static void initiate() {
		
	}
	
	////////////////
	//// FIELDS ////
	////////////////
	
	// Name
	private String name;
	
	// Dimensions
	private int width;
	private int height;
	
	// Spritesheet
	private spriteSheet unitTypeSpriteSheet;
	private animationPack animations;
	private int hitBoxAdjustmentX;
	private int hitBoxAdjustmentY;
	
	// Movespeed
	private int moveSpeed;
	
	// Jumpspeed;
	private int jumpSpeed;

	///////////////
	/// METHODS ///
	///////////////
	
	// Constructor
	public unitType(String newName, spriteSheet newSpriteSheet, animationPack newAnimationPack, int newWidth, int newHeight, int newMoveSpeed, int newJumpSpeed) {
		
		// Make the sprite sheet and animations.
		setUnitTypeSpriteSheet(newSpriteSheet);
		animations = newAnimationPack;
		
		// Set specifications.
		setName(newName);
		setWidth(newWidth);
		setHeight(newHeight);
		setMoveSpeed(newMoveSpeed);
		setJumpSpeed(newJumpSpeed);
	}

	/////////////////////////
	// Getters and Setters //
	/////////////////////////
	public void setMoveSpeed(int i) {
		moveSpeed = i;
	}
	
	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getMoveSpeed() {
		return moveSpeed;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public spriteSheet getUnitTypeSpriteSheet() {
		return unitTypeSpriteSheet;
	}

	public void setUnitTypeSpriteSheet(spriteSheet unitTypeSpriteSheet) {
		this.unitTypeSpriteSheet = unitTypeSpriteSheet;
	}

	public int getJumpSpeed() {
		return jumpSpeed;
	}

	public void setJumpSpeed(int jumpSpeed) {
		this.jumpSpeed = jumpSpeed;
	}

	public animationPack getAnimations() {
		return animations;
	}

	public void setAnimations(animationPack animations) {
		this.animations = animations;
	}

	public int getHitBoxAdjustmentY() {
		return hitBoxAdjustmentY;
	}

	public void setHitBoxAdjustmentY(int hitBoxAdjustmentY) {
		this.hitBoxAdjustmentY = hitBoxAdjustmentY;
	}
}