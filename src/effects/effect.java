package effects;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import animation.animation;
import animation.animationPack;
import drawing.camera;
import drawing.drawnObject;
import drawing.spriteSheet;
import modes.mode;
import sounds.sound;
import terrain.chunk;
import terrain.doodads.general.questMark;
import utilities.intTuple;
import utilities.time;
import utilities.utility;

public abstract class effect extends drawnObject  { 
	
	////////////////////////
	////// DEFAULTS ////////
	////////////////////////
	
	///////////////
	/// GLOBALS ///
	///////////////

	////////////////
	//// FIELDS ////
	////////////////
	
	// The actual unit type.
	private effectType typeOfEffect;
	
	// Movement
	private boolean collisionOn = false;
	
	// Sprite stuff.
	private animationPack animations;
	private animation currentAnimation = null;
	
	// Animation duration
	private long timeStarted = 0;
	private float animationDuration = 0;
	
	///////////////
	/// METHODS ///
	///////////////

	// Constructor
	public effect(effectType e, int newX, int newY) {
		super(e.getEffectTypeSpriteSheet(), newX, newY, e.getWidth(), e.getHeight());	
	
		// Set timer.
		timeStarted = time.getTime();
		animationDuration = e.getAnimationDuration();
		
		// Set-up animations.
		animationPack newAnimationPack =  new animationPack();
		
		// Set each animation in the spritesheet to be +1 of eachother.
		for(int i = 0; i < e.getEffectTypeSpriteSheet().getSprites().size(); i++) {
			animation newAnimation = new animation(e.getName() + i, 
					e.getEffectTypeSpriteSheet().getAnimation(i), 
					0, 
					e.getEffectTypeSpriteSheet().getSprites().get(i).size()-1, 
					e.getAnimationDuration()); //TODO: plays over 1 second by defualt
			newAnimationPack.addAnimation(newAnimation);
		}
		animations = newAnimationPack;
		animation a = animations.selectRandomAnimation();
		// Set the animation.
		currentAnimation = a;
		typeOfEffect = e;
	}
	
	// Update unit
	@Override
	public void update() {
		if(currentAnimation != null) currentAnimation.playAnimation();
		if(time.getTime() - timeStarted >= animationDuration*1000) {
			this.destroy();
		}
	}
	
	// Deal with movement animations.
	public void dealWithAnimations(int moveX, int moveY) {
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
		
		// Draw the hitbox of the image in green.
		if(showHitBox) {
			g.setColor(Color.green);
			g.drawRect(drawX - (- (getObjectSpriteSheet().getSpriteWidth()/2 - width/2) - getHitBoxAdjustmentX()),
					   drawY - (- (getObjectSpriteSheet().getSpriteHeight()/2 - height/2) - getHitBoxAdjustmentY()), 
				       width, 
				       height);
		}
	}
	
	/////////////////////////
	// Getters and setters //
	/////////////////////////
	
	public void setCollision(boolean b) {
		collisionOn = b;
	}

	public animationPack getAnimations() {
		return animations;
	}

	public void setAnimations(animationPack animations) {
		this.animations = animations;
	}
}