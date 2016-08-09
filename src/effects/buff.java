package effects;

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
		removeEffect();
	}
	
	// Apply effect.
	public abstract void applyEffect();
	
	// Remove effect.
	public abstract void removeEffect();
	
}