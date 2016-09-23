package effects.buffs;

import effects.buff;
import effects.effectType;
import effects.effectTypes.jumpBottleSplash;
import terrain.chunkTypes.wood;
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
	
	// Last animation
	public long lastAnimation = 0;
	public double animationEvery = .04;
	
	// Animate buff
	public void animateBuff() {
		
		// Put particles at the bottom of the buff.
			int randomX = onUnit.getIntX() + utility.RNG.nextInt(onUnit.getWidth());
			jumpBottleSplash e = new jumpBottleSplash(randomX - jumpBottleSplash.DEFAULT_SPRITE_WIDTH/2,onUnit.getIntY() + onUnit.getHeight()-jumpBottleSplash.DEFAULT_SPRITE_HEIGHT/2);
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
		u.touchDown();
		u.setFallSpeed(-u.getJumpSpeed());
		u.setDoubleJumping(true);
		
		// Destroy so we don't animate twice.
		if(alreadyEffected) this.destroy();
	}
	
	// Remove effect if off wood.
	@Override
	public void update() {
		
		// Do the buff animation
		animateBuff();
		if(onUnit.getFallSpeed() > 0 || !onUnit.isDoubleJumping() || onUnit.isUnitIsDead()) {
			destroy();
		}
	}
	
	
}