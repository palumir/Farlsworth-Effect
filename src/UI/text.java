package UI;

import java.awt.Color;
import java.awt.Graphics;

import drawing.drawnObject;

public class text extends interfaceObject  {
	
	///////////////////////
	////// FIELDS /////////
	///////////////////////
	private String theText;
	private Color theColor;
	protected float size = 1f;
	
	///////////////
	/// METHODS ///
	///////////////

	// Constructor
	public text(String newText, int newX, int newY, Color newColor) {
		super(null, newX, newY, 1, 1);	
		
		// Set fields.
		setTheText(newText);
		setTheColor(newColor);
	}
	
	// Constructor
	public text(String newText, int newX, int newY, Color newColor, float fontSize) {
		super(null, newX, newY, 1, 1);	
		
		// Set fields.
		size = fontSize;
		setTheText(newText);
		setTheColor(newColor);
	}
	
	// Draw the unit. 
	@Override
	public void drawObject(Graphics g) {
		// Set font.
		g.setFont(drawnObject.DEFAULT_FONT.deriveFont(drawnObject.DEFAULT_FONT.getSize()*size));
		
		// Color and string.
		g.setColor(getTheColor());
		g.drawString(getTheText(),
				   getIntX(),
				   getIntY());
	}
	
	// Update unit
	@Override
	public void update() {
	}

	public String getTheText() {
		return theText;
	}

	public void setTheText(String theText) {
		this.theText = theText;
	}

	public Color getTheColor() {
		return theColor;
	}

	public void setTheColor(Color theColor) {
		this.theColor = theColor;
	}
	
}
