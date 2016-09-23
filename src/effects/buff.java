package effects;

import java.util.ArrayList;

import units.unit;

public abstract class buff extends effect {

	// Fields
	protected unit onUnit = null;
	
	// Constructor
	public buff(effectType e, unit u, float d) {
		super(e, u.getIntX(), u.getIntY());
		setDrawObject(false);
		onUnit = u;
		setAnimationDuration(d);
		applyEffect();
	}
	
	// Respond to destruction
	@Override
	public void respondToDestroy() {
		if(onUnit.getBuffs()!=null) onUnit.getBuffs().remove(this);
		removeEffect();
	}
	
	// Apply
	public void apply() {
		applyEffect();
		if(onUnit.getBuffs() == null) onUnit.setBuffs(new ArrayList<buff>());
		onUnit.getBuffs().add(this);
	}
	
	// Remove
	public void remove() {
		destroy();
	}
	
	// Apply effect.
	public abstract void applyEffect();
	
	// Remove effect.
	public abstract void removeEffect();
	
}