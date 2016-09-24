package effects;

import java.awt.Color;
import java.awt.Graphics;

import drawing.drawnObject;
import drawing.gameCanvas;
import drawing.animation.animation;
import drawing.animation.animationPack;
import effects.effectTypes.jumpBottleSplash;
import utilities.time;

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
	
	// Sprite stuff.
	protected animationPack animations;
	private animation currentAnimation = null;
	
	// Animation duration
	protected long timeStarted = 0;
	private float animationDuration = 0;
	private boolean hasATimer = true;
	
	///////////////
	/// METHODS ///
	///////////////

	// Constructor
	public effect(effectType e, int newX, int newY) {
		super(e.getEffectTypeSpriteSheet(), e.getName(), newX, newY, e.getWidth(), e.getHeight());	
	
		// Set timer.
		timeStarted = time.getTime();
		setAnimationDuration(e.getAnimationDuration());
		
		// Set animations
		typeOfEffect = e;
		setAnimations(false);
		
		// Dont reload these
		reloadObject = false;
	}
	
	// Constructor
	public effect(effectType e, int newX, int newY, boolean entireSpriteSheetIsAnimation) {
		super(e.getEffectTypeSpriteSheet(), e.getName(), newX, newY, e.getWidth(), e.getHeight());	
	
		// Set timer.
		timeStarted = time.getTime();
		setAnimationDuration(e.getAnimationDuration());
		
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
							typeOfEffect.getAnimationDuration()); 
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
		
			
		// Play animation.
		if(getCurrentAnimation() != null) { 
			// Destroy.
			if(isHasATimer() && (time.getTime() - timeStarted >= getAnimationDuration()*1000)) {
				this.destroy();
			}
			respondToFrame(getCurrentAnimation().getCurrentSprite());
		}
		
		doSpecificEffectStuff();
	}
	
	// Do specific effect stuff
	public void doSpecificEffectStuff() {
		
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
			
			if(isExists()) g.drawImage(getCurrentAnimation().getCurrentFrame(), 
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

	public float getAnimationDuration() {
		return animationDuration;
	}

	public void setAnimationDuration(float animationDuration) {
		this.animationDuration = animationDuration;
	}

	public boolean isHasATimer() {
		return hasATimer;
	}

	public void setHasATimer(boolean hasATimer) {
		if(hasATimer == false) {
			// Go through the animations and set them to repeat.
			if(animations!=null) {
				for(int i = 0; i < animations.size(); i++) {
					animations.get(i).setRepeats(true);
				}
			}
		}
		this.hasATimer = hasATimer;
	}
}