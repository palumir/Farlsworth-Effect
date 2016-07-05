package effects.buffs;

import drawing.spriteSheet;
import drawing.spriteSheet.spriteSheetInfo;
import effects.buff;
import effects.effectType;
import units.unit;

public class darkSlow extends movementBuff {
	
	// Defaults
	private static String DEFAULT_EFFECT_NAME = "darkSlow";
	private static float DEFAULT_ANIMATION_DURATION = 0.25f;
	
	// The actual type.
	private static effectType theEffectType =
			new effectType(DEFAULT_EFFECT_NAME,
							null,
							DEFAULT_ANIMATION_DURATION);	
	
	// Fields
	private static float DEFAULT_SLOW_PERCENTAGE = 0.5f;
	
	// Methods
	public darkSlow(unit u, float duration) {
		super(theEffectType, u, DEFAULT_SLOW_PERCENTAGE, duration);
	}
	
}