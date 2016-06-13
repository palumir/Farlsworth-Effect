package effects.effectTypes;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;

import drawing.camera;
import drawing.spriteSheet;
import drawing.spriteSheet.spriteSheetInfo;
import effects.effect;
import effects.effectType;
import modes.mode;
import sounds.sound;
import units.animalType;
import units.humanType;
import units.unit;
import units.unitType;
import utilities.time;
import utilities.utility;
import zones.zone;

public class floatingNumber extends effect {
	
	// Default dimensions.
	public static int DEFAULT_SPRITE_WIDTH = 24;
	public static int DEFAULT_SPRITE_HEIGHT = 24;
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default name.
	private static String DEFAULT_EFFECT_NAME = "floatingNumber";
	
	// Duration
	private static float DEFAULT_ANIMATION_DURATION = 1f;
	
	// The actual type.
	private static effectType theEffectType =
			new effectType(DEFAULT_EFFECT_NAME,
					null, DEFAULT_ANIMATION_DURATION);	
	
	
	
	//////////////
	/// FIELDS ///
	//////////////
	public int number = 0;
	public Color color;
	
	///////////////
	/// METHODS ///
	///////////////
	// Constructor
	public floatingNumber(int newNumber, Color newColor, int newX, int newY) {
		super(theEffectType, newX, newY);
		number = newNumber;
		color = newColor;
		
		// So it displays over everything.
		height = 100;
		width = 1;
	}
	
	// Draw the unit. 
	@Override
	public void drawObject(Graphics g) {
		g.setColor(color);
		g.drawString("" + number,drawX - g.getFontMetrics().stringWidth(number+"")/2, drawY - height/2);
		setY(getY() - 1);
	}

}
