package effects.buffs;

import effects.buff;
import effects.effectType;
import effects.effectTypes.items.pushBottleSplash;
import items.bottles.pushBottle;
import modes.mode;
import terrain.chunkTypes.mud;
import units.player;
import units.unit;
import utilities.time;
import utilities.utility;

public class pushBottleBuff extends buff {
	
	// Defaults
	private static String DEFAULT_EFFECT_NAME = "pushBottleBuff";
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
			pushBottleSplash e = new pushBottleSplash(randomX - pushBottleSplash.DEFAULT_SPRITE_WIDTH/2, onUnit.getIntY() + onUnit.getHeight()-pushBottleSplash.DEFAULT_SPRITE_HEIGHT/2);
		}
		else { 
			pushBottleSplash e = new pushBottleSplash(onUnit.getIntX() + onUnit.getWidth()/2 - pushBottleSplash.DEFAULT_SPRITE_WIDTH/2,-5 + onUnit.getIntY() + onUnit.getHeight()-pushBottleSplash.DEFAULT_SPRITE_HEIGHT/2);
		}
	}
	
	// Methods
	public pushBottleBuff(player u) {
		super(theEffectType, u, DEFAULT_ANIMATION_DURATION);
		setHasATimer(false);
		
		// Check if we don't have to animate again.
		boolean alreadyEffected = false;
		if(u.getBuffs() != null) {
			for(int i = 0; i < u.getBuffs().size(); i++) {
				if(u.getBuffs().get(i) instanceof pushBottleBuff) {
					alreadyEffected = true;
					break;
				}
			}
		}
		
		// But allow the jump
		if(mode.getCurrentMode().equals("topDown") && u.isMovingLeft() && u.isMovingUp()) 
			u.slashTo((int) (u.getIntX()-pushBottle.DEFAULT_JUMP_DISTANCE_TOPDOWN/1.4f),
					(int) (u.getIntY()-pushBottle.DEFAULT_JUMP_DISTANCE_TOPDOWN/1.4f));
		else if(mode.getCurrentMode().equals("topDown") && u.isMovingRight() && u.isMovingUp()) 
			u.slashTo((int) (u.getIntX()+pushBottle.DEFAULT_JUMP_DISTANCE_TOPDOWN/1.4f),
					(int) (u.getIntY()-pushBottle.DEFAULT_JUMP_DISTANCE_TOPDOWN/1.4f));
		else if(mode.getCurrentMode().equals("topDown") && u.isMovingRight() && u.isMovingDown()) 
			u.slashTo((int) (u.getIntX()+pushBottle.DEFAULT_JUMP_DISTANCE_TOPDOWN/1.4f),
					(int) (u.getIntY()+pushBottle.DEFAULT_JUMP_DISTANCE_TOPDOWN/1.4f));
		else if(mode.getCurrentMode().equals("topDown") && u.isMovingLeft() && u.isMovingDown()) 
			u.slashTo((int) (u.getIntX()-pushBottle.DEFAULT_JUMP_DISTANCE_TOPDOWN/1.4f),
					(int) (u.getIntY()+pushBottle.DEFAULT_JUMP_DISTANCE_TOPDOWN/1.4f));
		else if(u.getFacingDirection().equals("Left")) 
			u.slashTo(u.getIntX()-pushBottle.DEFAULT_JUMP_DISTANCE_TOPDOWN,u.getIntY());
		else if(u.getFacingDirection().equals("Right")) 
			u.slashTo(u.getIntX()+pushBottle.DEFAULT_JUMP_DISTANCE_TOPDOWN,u.getIntY());
		else if(u.getFacingDirection().equals("Up")) 
			u.slashTo(u.getIntX(),u.getIntY()-pushBottle.DEFAULT_JUMP_DISTANCE_TOPDOWN);
		else if(u.getFacingDirection().equals("Down")) 
			u.slashTo(u.getIntX(),u.getIntY()+pushBottle.DEFAULT_JUMP_DISTANCE_TOPDOWN);
		
		// Destroy so we don't animate twice.
		if(alreadyEffected) this.destroy();
	}
	
	// Remove effect if off wood.
	@Override
	public void update() {
		
		// Do the buff animation
		animateBuff();
		if((!((player)onUnit).isPushing() || ((player)onUnit).isUnitIsDead())) {
			destroy();
		}
	}
	
	
}