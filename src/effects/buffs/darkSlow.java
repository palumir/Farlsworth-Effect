package effects.buffs;

import drawing.spriteSheet;
import drawing.spriteSheet.spriteSheetInfo;
import effects.buff;
import effects.effectType;
import terrain.atmosphericEffects.fog;
import units.unit;

public class darkSlow extends movementBuff {
	
	// Defaults
	private static String DEFAULT_EFFECT_NAME = "darkSlow";
	private static float DEFAULT_ANIMATION_DURATION = 0.5f;
	
	// Fog percentage
	private static float FOG_PERCENT = 0.8f;
	private static float FOG_OVER = 2f;
	private static float FOG_BACK_OVER = 0.15f;
	
	// The actual type.
	private static effectType theEffectType =
			new effectType(DEFAULT_EFFECT_NAME,
							null,
							DEFAULT_ANIMATION_DURATION);	
	
	// Fields
	private static float DEFAULT_SLOW_PERCENTAGE = 0.7f;
	
	@Override
	public void applyEffect() {
		if(!onUnit.getMovementBuffs().contains(this)) {
			onUnit.getMovementBuffs().add(this);
			//fog.fadeTo(FOG_PERCENT, FOG_OVER);
		}
	}

	@Override
	public void removeEffect() {
		if(onUnit.getMovementBuffs().contains(this)) {
			onUnit.getMovementBuffs().remove(this);
			
			// Check if there's another of the same effect.
			boolean isAnother = false;
			for(int i = 0; i < onUnit.getMovementBuffs().size(); i++) {
				if(onUnit.getMovementBuffs().get(i).getClass().equals(this.getClass())) isAnother = true;
			}
			//if(!isAnother) fog.fadeTo(0, FOG_BACK_OVER);
		}
	}
	
	// Methods
	public darkSlow(unit u, float duration) {
		super(theEffectType, u, DEFAULT_SLOW_PERCENTAGE, duration + 0.05f);
	}
	
}