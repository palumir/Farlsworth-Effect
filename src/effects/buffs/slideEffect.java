package effects.buffs;

import effects.buff;
import effects.effectType;
import terrain.groundTile;
import terrain.atmosphericEffects.fog;
import terrain.chunkTypes.wood;
import units.player;
import units.unit;

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
	
	// Save the moveSpeed
	private static float unitMoveSpeed = 0;
	
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
			unitMoveSpeed = onUnit.moveSpeed;
			onUnit.moveSpeed = onUnit.moveSpeed + onUnit.moveSpeed*3/12;
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
			onUnit.moveSpeed = unitMoveSpeed;
		}
	}
	
	// Methods
	public slideEffect(unit u) {
		super(theEffectType, u, 1, DEFAULT_ANIMATION_DURATION);
		hasATimer = false;
	}
	
	// Remove effect if off wood.
	@Override
	public void update() {
		if(!wood.isOnWood(player.getPlayer())) {
			removeEffect();
		}
	}
	
	
}