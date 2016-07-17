package effects;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import doodads.general.questMark;
import drawing.camera;
import drawing.drawnObject;
import drawing.gameCanvas;
import drawing.spriteSheet;
import drawing.animation.animation;
import drawing.animation.animationPack;
import modes.mode;
import sounds.sound;
import terrain.chunk;
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
	protected animationPack animations;
	private animation currentAnimation = null;
	
	// Animation duration
	protected long timeStarted = 0;
	protected float animationDuration = 0;
	protected boolean hasATimer = true;
	
	///////////////
	/// METHODS ///
	///////////////

	// Constructor
	public effect(effectType e, int newX, int newY) {
		super(e.getEffectTypeSpriteSheet(), newX, newY, e.getWidth(), e.getHeight());	
	
		// Set timer.
		timeStarted = time.getTime();
		animationDuration = e.getAnimationDuration();
		
		// Set animations
		typeOfEffect = e;
		setAnimations(false);
		
	}
	
	// Constructor
	public effect(effectType e, int newX, int newY, boolean entireSpriteSheetIsAnimation) {
		super(e.getEffectTypeSpriteSheet(), newX, newY, e.getWidth(), e.getHeight());	
	
		// Set timer.
		timeStarted = time.getTime();
		animationDuration = e.getAnimationDuration();
		
		// Set animations
		typeOfEffect = e;
		setAnimations(entireSpriteSheetIsAnimation);
		
	}
	
	// Set animations
	public void setAnimations(boolean b) {
		if(typeOfEffect.getEffectTypeSpriteSheet() != null) {
			// Set-up animations.
			animationPack newAnimationPack =  new animationPack();
			
			// Is the entire spritesheet not the animation?
			if(!b) {
				// Set each animation in the spritesheet to be +1 of eachother.
				for(int i = 0; i < typeOfEffect.getEffectTypeSpriteSheet().getSprites().size(); i++) {
					animation newAnimation = new animation(typeOfEffect.getName() + i, 
							typeOfEffect.getEffectTypeSpriteSheet().getAnimation(i), 
							0, 
							typeOfEffect.getEffectTypeSpriteSheet().getSprites().get(i).size()-1, 
							typeOfEffect.getAnimationDuration()); //TODO: plays over 1 second by defualt
					newAnimationPack.addAnimation(newAnimation);
				}
			}
			
			// Okay then bud, it is. Do it up bud.
			else {
				animation newAnimation = new animation(typeOfEffect.getName(), 
						typeOfEffect.getEffectTypeSpriteSheet().getAnimation(), 
						0, 
						typeOfEffect.getEffectTypeSpriteSheet().getAnimation().size() - 1,
						typeOfEffect.getAnimationDuration()); 
				newAnimationPack.addAnimation(newAnimation);
			}
			animations = newAnimationPack;
			animation a = animations.selectRandomAnimation();
			
			// Set the animation.
			setCurrentAnimation(a);
		}
	}
	
	// Update unit
	@Override
	public void update() {
		if(getCurrentAnimation() != null) { 
			getCurrentAnimation().playAnimation();
			respondToFrame(getCurrentAnimation().getCurrentSprite());
		}
		if(hasATimer && time.getTime() - timeStarted >= animationDuration*1000) {
			this.destroy();
		}
	}

	
	// Respond to ending
	public void respondToFrame(int j) {
		
	}
	
	// Deal with movement animations.
	public void dealWithAnimations(int moveX, int moveY) {}

	// Draw the effect
	@Override
	public void drawObject(Graphics g) {
		
		// Of course only draw if the animation is not null.
		if(getCurrentAnimation() != null) {
			g.drawImage(getCurrentAnimation().getCurrentFrame(), 
					getDrawX(), 
					getDrawY(), 
					(int)(gameCanvas.getScaleX()*getObjectSpriteSheet().getSpriteWidth() + 1), 
					(int)(gameCanvas.getScaleY()*getObjectSpriteSheet().getSpriteHeight() + 1), 
					null);
		}
		
		// Draw the hitbox of the image in green.
		if(showHitBox) {
			g.setColor(Color.green);
			g.drawRect(getDrawX() - (int)(gameCanvas.getScaleX()*(- (getObjectSpriteSheet().getSpriteWidth()/2 - getWidth()/2) - getHitBoxAdjustmentX())),
					   getDrawY() - (int)(gameCanvas.getScaleY()*(- (getObjectSpriteSheet().getSpriteHeight()/2 - getHeight()/2) - getHitBoxAdjustmentY())), 
					   (int)(gameCanvas.getScaleX()*getWidth() + 1), 
					   (int)(gameCanvas.getScaleY()*getHeight() + 1));
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

	public animation getCurrentAnimation() {
		return currentAnimation;
	}

	public void setCurrentAnimation(animation currentAnimation) {
		this.currentAnimation = currentAnimation;
	}
}