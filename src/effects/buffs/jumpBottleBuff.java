package effects.buffs;

import effects.buff;
import effects.effectType;
import effects.effectTypes.jumpBottleSplash;
import items.bottles.jumpBottle;
import modes.mode;
import terrain.chunkTypes.mud;
import units.player;
import units.unit;
import utilities.time;
import utilities.utility;

public class jumpBottleBuff extends buff {
	
	// Defaults
	private static String DEFAULT_EFFECT_NAME = "jumpBottleBuff";
	public static float DEFAULT_ANIMATION_DURATION = 10f;
	
	// The actual type.
	private static effectType theEffectType =
			new effectType(DEFAULT_EFFECT_NAME,
							null,
							DEFAULT_ANIMATION_DURATION);	
	
	
	@Override
	public void applyEffect() {
	}

	@Override
	public void removeEffect() {
	}
	
	// Animate buff
	public void animateBuff() {
		
		// Put particles at the bottom of the buff.
		int randomX = onUnit.getIntX() + utility.RNG.nextInt(onUnit.getWidth());
		if(mode.getCurrentMode().equals("platformer")) {
			jumpBottleSplash e = new jumpBottleSplash(randomX - jumpBottleSplash.DEFAULT_SPRITE_WIDTH/2, onUnit.getIntY() + onUnit.getHeight()-jumpBottleSplash.DEFAULT_SPRITE_HEIGHT/2);
		}
		else { 
			jumpBottleSplash e = new jumpBottleSplash(onUnit.getIntX() + onUnit.getWidth()/2 - jumpBottleSplash.DEFAULT_SPRITE_WIDTH/2,-5 + onUnit.getIntY() + onUnit.getHeight()-jumpBottleSplash.DEFAULT_SPRITE_HEIGHT/2);
		}
	}
	
	// Methods
	public jumpBottleBuff(unit u) {
		super(theEffectType, u, DEFAULT_ANIMATION_DURATION);
		setHasATimer(false);
		
		// Check if we don't have to animate again.
		boolean alreadyEffected = false;
		if(u.getBuffs() != null) {
			for(int i = 0; i < u.getBuffs().size(); i++) {
				if(u.getBuffs().get(i) instanceof jumpBottleBuff) {
					alreadyEffected = true;
					break;
				}
			}
		}
		
		// But allow the jump
		if(mode.getCurrentMode().equals("platformer")) {
			u.touchDown();
			u.setFallSpeed(-u.getJumpSpeed());
			u.setDoubleJumping(true);
		}
		
		// Topdown
		else {
			
			// Top down.
			if(mode.getCurrentMode().equals("topDown")) {
				if(u.isMovingLeft() && u.isMovingUp()) 
					u.slashTo((int) (u.getIntX()-jumpBottle.DEFAULT_JUMP_DISTANCE_TOPDOWN/1.4f),
							(int) (u.getIntY()-jumpBottle.DEFAULT_JUMP_DISTANCE_TOPDOWN/1.4f));
				else if(u.isMovingRight() && u.isMovingUp()) 
					u.slashTo((int) (u.getIntX()+jumpBottle.DEFAULT_JUMP_DISTANCE_TOPDOWN/1.4f),
							(int) (u.getIntY()-jumpBottle.DEFAULT_JUMP_DISTANCE_TOPDOWN/1.4f));
				else if(u.isMovingRight() && u.isMovingDown()) 
					u.slashTo((int) (u.getIntX()+jumpBottle.DEFAULT_JUMP_DISTANCE_TOPDOWN/1.4f),
							(int) (u.getIntY()+jumpBottle.DEFAULT_JUMP_DISTANCE_TOPDOWN/1.4f));
				else if(u.isMovingLeft() && u.isMovingDown()) 
					u.slashTo((int) (u.getIntX()-jumpBottle.DEFAULT_JUMP_DISTANCE_TOPDOWN/1.4f),
							(int) (u.getIntY()+jumpBottle.DEFAULT_JUMP_DISTANCE_TOPDOWN/1.4f));
				else if(u.getFacingDirection().equals("Left")) 
					u.slashTo(u.getIntX()-jumpBottle.DEFAULT_JUMP_DISTANCE_TOPDOWN,u.getIntY());
				else if(u.getFacingDirection().equals("Right")) 
					u.slashTo(u.getIntX()+jumpBottle.DEFAULT_JUMP_DISTANCE_TOPDOWN,u.getIntY());
				else if(u.getFacingDirection().equals("Up")) 
					u.slashTo(u.getIntX(),u.getIntY()-jumpBottle.DEFAULT_JUMP_DISTANCE_TOPDOWN);
				else if(u.getFacingDirection().equals("Down")) 
					u.slashTo(u.getIntX(),u.getIntY()+jumpBottle.DEFAULT_JUMP_DISTANCE_TOPDOWN);
			}
		}
		
		// Destroy so we don't animate twice.
		if(alreadyEffected) this.destroy();
	}
	
	// Remove effect if off wood.
	@Override
	public void update() {
		
		// Do the buff animation
		animateBuff();
		if(mode.getCurrentMode().equals("platformer") && (onUnit.getFallSpeed() > 0 || !onUnit.isDoubleJumping() || onUnit.isUnitIsDead())) {
			destroy();
		}
		if(mode.getCurrentMode().equals("topDown") && (!onUnit.isJumping() || onUnit.isUnitIsDead())) {
			destroy();
		}
	}
	
	
}