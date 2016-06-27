package drawing.userInterface;

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

public class tooltipString extends effect {
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default name.
	private static String DEFAULT_EFFECT_NAME = "tooltipString";
	 
	// Duration
	private static float DEFAULT_ANIMATION_DURATION = 5f;
	
	// Size
	private static float DEFAULT_TEXT_SIZE = 1.7f;
	
	// Color 
	private static Color DEFAULT_COLOR = Color.white;
	
	// Default X and Y
	private static int DEFAULT_X = 275;
	private static int DEFAULT_Y = 420;
	
	// Current tooltipstring.
	private static tooltipString currTooltipString = null;
	
	// The actual type.
	private static effectType theEffectType =
			new effectType(DEFAULT_EFFECT_NAME,
					null, DEFAULT_ANIMATION_DURATION);	
	
	//////////////
	/// FIELDS ///
	//////////////
	public String text;
	public Color color;
	
	///////////////
	/// METHODS ///
	///////////////
	// Constructor
	public tooltipString(String newText) {
		super(theEffectType, DEFAULT_X, DEFAULT_Y);
		text = newText;
		color = DEFAULT_COLOR;
		
		// Set current tooltipstring and fade the old one out.
		if(currTooltipString != null) currTooltipString.destroy();
		currTooltipString = this;
		
		// So it displays over everything.
		setHeight(1);
		setWidth(1);
	}
	
	// Draw the object.
	@Override
	public void drawObject(Graphics g2) {
		
		BufferedImage img = new BufferedImage(gameCanvas.getActualWidth(),
				gameCanvas.getActualHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = img.createGraphics();
		
		// Set the alpha depending on how close the animation is to over.
		float timeThatHasPassed = (time.getTime() - timeStarted)/1000f; // in seconds
		float alpha = 1f;
		if(timeThatHasPassed/animationDuration < 0.05f) {
			alpha = 20f*(timeThatHasPassed/animationDuration);
		}
		if(timeThatHasPassed/animationDuration > 0.95f) {
			alpha = 20f*(1f - (timeThatHasPassed/animationDuration));
		}
		if(alpha < 0) alpha = 0;
		if(alpha > 1) alpha = 1;
		Color newColor = new Color(DEFAULT_COLOR.getRed()/255f, DEFAULT_COLOR.getGreen()/255f, DEFAULT_COLOR.getBlue()/255f, alpha); //Black 
		Font font = drawnObject.DEFAULT_FONT.deriveFont(drawnObject.DEFAULT_FONT.getSize()*DEFAULT_TEXT_SIZE);
		g.setFont(font);
		g.setComposite(AlphaComposite.Src);
		g.setPaint(newColor);
	
		// Draw.)
		g.drawString(text,(int)(gameCanvas.getScaleX()*(getX()) - g.getFontMetrics().stringWidth(text)/2), (int)(gameCanvas.getScaleY()*getY()));
		g2.drawImage(img,0,0,null);
	}

}
