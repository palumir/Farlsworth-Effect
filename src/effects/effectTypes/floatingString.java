package effects.effectTypes;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Random;

import drawing.camera;
import drawing.drawnObject;
import drawing.gameCanvas;
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

public class floatingString extends effect {
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default name.
	private static String DEFAULT_EFFECT_NAME = "floatingString";
	 
	// Duration
	private static float DEFAULT_ANIMATION_DURATION = 2.2f;
	
	// The actual type.
	private static effectType theEffectType =
			new effectType(DEFAULT_EFFECT_NAME,
					null, DEFAULT_ANIMATION_DURATION);	
	
	//////////////
	/// FIELDS ///
	//////////////
	public String text;
	public Color color;
	public float startSize;
	public float currSize;
	public float endSize;
	
	///////////////
	/// METHODS ///
	///////////////
	// Constructor
	public floatingString(String newText, Color newColor, int newX, int newY, float newSize) {
		super(theEffectType, newX, newY);
		text = newText;
		color = newColor;
		startSize = newSize;
		endSize = newSize;
		currSize = newSize;
		
		// So it displays over everything.
		setHeight(100);
		setWidth(1);
	}
	
	// Constructor
	public floatingString(String newText, Color newColor, int newX, int newY, float newSize, float newEndSize) {
		super(theEffectType, newX, newY);
		text = newText;
		color = newColor;
		startSize = newSize;
		endSize = newEndSize;
		currSize = newSize;
		
		// So it displays over everything.
		setHeight(100);
		setWidth(1);
	}
	
	// Draw the unit. 
	@Override
	public void drawObject(Graphics g2) {
		
		BufferedImage img = new BufferedImage(gameCanvas.getActualWidth(),gameCanvas.getActualHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = img.createGraphics();
		
		// Make font
		Font font = drawnObject.DEFAULT_FONT.deriveFont(drawnObject.DEFAULT_FONT.getSize()*currSize);
		if(currSize < endSize) {
			currSize += (endSize - startSize)/(DEFAULT_ANIMATION_DURATION*gameCanvas.getFPS());
		}
		
		// Set the alpha depending on how close the animation is to over.
		float timeThatHasPassed = (time.getTime() - timeStarted)/1000f; // in seconds
		float alpha = 1f - timeThatHasPassed/animationDuration;
		if(alpha < 0) alpha = 0;
		if(alpha > 1) alpha = 1;
		Color newColor = new Color(color.getRed()/255f, color.getGreen()/255f, color.getBlue()/255f, alpha); //Black 
		g.setFont(font);
		g.setComposite(AlphaComposite.Src);
		g.setPaint(newColor);
		
		// Draw.
		g.drawString(text,drawX - g.getFontMetrics().stringWidth(text)/2, drawY - (int)(gameCanvas.getScaleY()*getHeight()/2));
		g2.drawImage(img,0,0,null);
		setY(getY() - 1);
	}

}
