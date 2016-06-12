package effects;

import java.awt.Color;

import animation.animation;
import animation.animationPack;
import drawing.spriteSheet;
import drawing.spriteSheet.spriteSheetInfo;

public class effectType {
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
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
	private spriteSheet effectTypeSpriteSheet;
	private animationPack animations;
	private int hitBoxAdjustmentX;
	private int hitBoxAdjustmentY;
	
	// Movespeed
	private int moveSpeed;
	
	// Jumpspeed;
	private int jumpSpeed;
	
	// Duration
	private float animationDuration;

	///////////////
	/// METHODS ///
	///////////////
	
	// Constructor
	public effectType(String newName, spriteSheet newSpriteSheet, float newDuration) {
		
		// Make the sprite sheet and animations.
		setEffectTypeSpriteSheet(newSpriteSheet);
		
		// Set specifications.
		setAnimationDuration(newDuration);
		setName(newName);
		setWidth(newSpriteSheet.getSpriteWidth());
		setHeight(newSpriteSheet.getSpriteHeight());
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

	public spriteSheet getEffectTypeSpriteSheet() {
		return effectTypeSpriteSheet;
	}

	public void setEffectTypeSpriteSheet(spriteSheet effectTypeSpriteSheet) {
		this.effectTypeSpriteSheet = effectTypeSpriteSheet;
	}

	public float getAnimationDuration() {
		return animationDuration;
	}

	public void setAnimationDuration(float animationDuration) {
		this.animationDuration = animationDuration;
	}
}