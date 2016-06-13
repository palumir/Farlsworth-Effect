package userInterface;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import drawing.drawnObject;

public class text extends interfaceObject  {
	
	///////////////////////
	////// FIELDS /////////
	///////////////////////
	private String theText;
	protected Color theColor;
	
	///////////////
	/// METHODS ///
	///////////////

	// Constructor
	public text(String newText, int newX, int newY, Color newColor) {
		super(null, newX, newY, 1, 1);	
		
		// Set fields.
		setTheText(newText);
		theColor = newColor;
	}
	
	// Draw the unit. 
	@Override
	public void drawObject(Graphics g) {
		// Set font.
		g.setFont(drawnObject.DEFAULT_FONT);
		
		// Color and string.
		g.setColor(theColor);
		g.drawString(getTheText(),
				   getX(),
				   getY());
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
	
}
