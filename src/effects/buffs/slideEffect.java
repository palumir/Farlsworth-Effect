package effects.buffs;

import effects.buff;
import effects.effectType;
import effects.effectTypes.mudSplash;
import modes.mode;
import terrain.chunkTypes.mud;
import units.unit;
import utilities.time;
import utilities.utility;

public class slideEffect extends movementBuff {
	
	// Defaults
	private static String DEFAULT_EFFECT_NAME = "slideEffect";
	public static float DEFAULT_ANIMATION_DURATION = 10f;
	
	// The actual type.
	private static effectType theEffectType =
			new effectType(DEFAULT_EFFECT_NAME,
							null,
							DEFAULT_ANIMATION_DURATION);	
	
	// Fields
	private static float DEFAULT_SLIDE_ACCELERATION = 0.1f;
	
	@Override
	public void applyEffect() {
		boolean noSlideBuffs = true;
		for(int i = 0; i < onUnit.getMovementBuffs().size(); i++) {
			buff currentBuff = onUnit.getMovementBuffs().get(i);
			if(currentBuff instanceof slideEffect) {
				noSlideBuffs = false;
				break;
			}
		}
		if(noSlideBuffs) {
			onUnit.getMovementBuffs().add(this);
			onUnit.setMovementAcceleration(DEFAULT_SLIDE_ACCELERATION);
			onUnit.moveSpeed = onUnit.moveSpeed + onUnit.moveSpeed*6/12;
			onUnit.setMomentumX(onUnit.getMomentumX()*3/8);
			onUnit.setMomentumY(onUnit.getMomentumY()*3/8);
		}
	}

	@Override
	public void removeEffect() {
		if(onUnit.getMovementBuffs().contains(this)) {
			onUnit.getMovementBuffs().remove(this);
		}
		
		// If it's the last of it's kind, reset player movement.
		boolean noSlideBuffs = true;
		for(int i = 0; i < onUnit.getMovementBuffs().size(); i++) {
			buff currentBuff = onUnit.getMovementBuffs().get(i);
			if(currentBuff instanceof slideEffect) {
				noSlideBuffs = false;
				break;
			}
		}
		if(noSlideBuffs) {
			onUnit.setMovementAcceleration(0);
			onUnit.moveSpeed = onUnit.getBaseMoveSpeed();
		}
		
	}
	
	// Methods
	public slideEffect(unit u) {
		super(theEffectType, u, 1, DEFAULT_ANIMATION_DURATION);
		setHasATimer(false);
	}
	
	// Don't do it that often
	long lastMud = 0;
	float mudEvery = 0.04f;
	
	public void animateBuff() {
		
		if(time.getTime() - lastMud > mudEvery*1000) {
			lastMud = time.getTime();
			if(Math.abs(onUnit.getMomentumX()) > 0.2f || Math.abs(onUnit.getMomentumY()) > 0.2f) {
				// Put particles at the bottom of the buff.
				int randomX = onUnit.getIntX() + utility.RNG.nextInt(onUnit.getWidth());
				if(mode.getCurrentMode().equals("platformer")) {
					//mudSplash e = new mudSplash(randomX - mudSplash.DEFAULT_SPRITE_WIDTH/2, onUnit.getIntY() + onUnit.getHeight()-mudSplash.DEFAULT_SPRITE_HEIGHT/2);
				}
				else { 
					mudSplash e = new mudSplash(onUnit.getIntX() + onUnit.getWidth()/2 - mudSplash.DEFAULT_SPRITE_WIDTH/2 + 1 - utility.RNG.nextInt(3),-5 + onUnit.getIntY() + onUnit.getHeight()-mudSplash.DEFAULT_SPRITE_HEIGHT/2 + 1 - utility.RNG.nextInt(3));
				}
			}
		}
	}
	
	// Remove effect if off wood.
	@Override
	public void update() {
		boolean onMud = mud.isOnMud(onUnit);
		if(onMud && !mud.isOnNonMud(onUnit)) animateBuff();
		if(onUnit.isUnitIsDead() || (!onMud && (!onUnit.isInAir() ||  mode.getCurrentMode().equals("topDown")))) {
			destroy();
		}
	}
	
	
}