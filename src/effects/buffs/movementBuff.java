package effects.buffs;

import effects.buff;
import effects.effectType;
import units.unit;

public class movementBuff extends buff {
	
	// Fields
	private float movementPercentage;
	
	// Methods
	public movementBuff(effectType e, unit u, float percent, float duration) {
		super(e, u, duration);
		setMovementPercentage(percent);
	}

	@Override
	public void applyEffect() {
		if(!onUnit.getMovementBuffs().contains(this)) {
			onUnit.getMovementBuffs().add(this);
		}
	}

	@Override
	public void removeEffect() {
		if(onUnit.getMovementBuffs().contains(this)) {
			onUnit.getMovementBuffs().remove(this);
		}
	}

	public float getMovementPercentage() {
		return movementPercentage;
	}

	public void setMovementPercentage(float movementPercentage) {
		this.movementPercentage = movementPercentage;
	}
	
}