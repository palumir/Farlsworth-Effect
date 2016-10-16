package effects.buffs;

import effects.buff;
import effects.effectTypes.fire;
import effects.effectType;
import effects.effectTypes.mudSplash;
import effects.effectTypes.mudSplash;
import modes.mode;
import sounds.sound;
import terrain.chunkTypes.mud;
import units.player;
import units.unit;
import utilities.time;
import utilities.utility;

public class onFireEffect extends movementBuff {
	
	// Defaults
	private static String DEFAULT_EFFECT_NAME = "onFireEffect";
	public static float DEFAULT_ANIMATION_DURATION = 10f;
	public static float DEFAULT_SPEED_INCREASE = 2f;
	public static float TIME_UNTIL_DEATH = 5f;
	
	// Set on fire.
	private static String fireGulf = "sounds/effects/doodads/startFire.wav";
	
	// The actual type.
	private static effectType theEffectType =
			new effectType(DEFAULT_EFFECT_NAME,
							null,
							DEFAULT_ANIMATION_DURATION);
	
	// Save the moveSpeed
	private static float unitMoveSpeed = 0;
	
	@Override
	public void applyEffect() {
		boolean noFireBuffs = true;
		for(int i = 0; i < onUnit.getMovementBuffs().size(); i++) {
			buff currentBuff = onUnit.getMovementBuffs().get(i);
			if(currentBuff instanceof onFireEffect) {
				noFireBuffs = false;
				break;
			}
		}
		if(noFireBuffs) {
			unitMoveSpeed = onUnit.moveSpeed;
			onUnit.moveSpeed = unitMoveSpeed + DEFAULT_SPEED_INCREASE;
		}
	}

	@Override
	public void removeEffect() {
		if(onUnit.getMovementBuffs().contains(this)) {
			onUnit.getMovementBuffs().remove(this);
		}
		
		// If it's the last of it's kind, reset player movement.
		boolean noFireBuffs = true;
		for(int i = 0; i < onUnit.getMovementBuffs().size(); i++) {
			buff currentBuff = onUnit.getMovementBuffs().get(i);
			if(currentBuff instanceof onFireEffect) {
				noFireBuffs = false;
				break;
			}
		}
		if(noFireBuffs) {
			onUnit.moveSpeed = unitMoveSpeed;
		}
		
	}
	
	// Methods
	public onFireEffect(unit u) {
		super(theEffectType, u, 1, DEFAULT_ANIMATION_DURATION);
		setHasATimer(false);
	}
	
	// Fire effect.
	fire fire;
	
	public void animateBuff() {
		int x = onUnit.getIntX() + onUnit.getWidth()/2 - fire.getDefaultWidth()/2;
		int y = onUnit.getIntY() + onUnit.getHeight() - fire.DEFAULT_SPRITE_HEIGHT*6/14;
		if(fire==null) {
			sound s = new sound(fireGulf);
			s.setPosition(getIntX(),getIntY(), sound.DEFAULT_SOUND_RADIUS);
			s.start();
			fire = new fire(x, y);
			fire.attachToObject(onUnit);
		}
	}
	
	public void killPlayerEventually() {
		if(time.getTime() - timeStarted > TIME_UNTIL_DEATH*1000) {
			onUnit.hurt(1, 1);
		}
	}
	
	// Remove effect only on death at the moment. TODO: water?
	@Override
	public void update() {
		animateBuff();
		killPlayerEventually();
		if(onUnit.isUnitIsDead()) {
			destroy();
		}
	}
	
	
}