package UI;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import drawing.drawnObject;
import drawing.gameCanvas;
import interactions.quest;
import items.bottle;
import units.player;
import units.developer.developer;
import utilities.imageUtils;
import utilities.stringUtils;

public class propertyEditBox extends interfaceObject  {
	
	/////////////////////////
	////// DEFAULTS /////////
	/////////////////////////
	
	// Colors
	public static Color DEFAULT_TEXT_COLOR = Color.white;
	public static Color DEFAULT_BACKGROUND_COLOR = Color.BLUE;
	
	// Current box
	public static propertyEditBox currentBox;
	
	// Default width/height
	public static int DEFAULT_WIDTH = 150;
	public static int DEFAULT_HEIGHT = 100;
	
	///////////////////////
	////// FIELDS /////////
	///////////////////////
	
	// Box ID
	private String boxID;
	
	// Children
	private button okay;
	private button cancel;

	///////////////
	/// METHODS ///
	///////////////

	// Constructor
	public propertyEditBox(String boxName, String boxID, String value) {
		super(null, gameCanvas.getDefaultWidth()/2 - DEFAULT_WIDTH/2, gameCanvas.getDefaultHeight()/2 - DEFAULT_HEIGHT/2, DEFAULT_WIDTH, DEFAULT_HEIGHT);	
		
		// Add button
		okay = new button("Okay",boxID + ".okay", getIntX(), getIntY() + getHeight() - 40,50,40);
		okay.setZ(2);
		cancel = new button("Cancel",boxID + ".cancel", getIntX() + getWidth() - 50, getIntY() + getHeight() - 40,50,40);
		cancel.setZ(2);
		
		// Set things.
		setName(boxName);
		this.boxID = boxID;
		setCurrentValue(value);
		setZ(1);
		
		// Set current box.
		setCurrentBox(this);
	}
	
	// Current value
	private String currentValue;
	
	// Respond to key press.
	public void respondToKeyPress(KeyEvent k) {
		
		// Number entered
		if(Character.isDigit(k.getKeyChar()) 
				|| k.getKeyCode() == KeyEvent.VK_MINUS || 
				Character.isAlphabetic((k.getKeyChar()))) {
			setCurrentValue(getCurrentValue() + k.getKeyChar());
		}
		
		// Dot
		else if(k.getKeyChar() == KeyEvent.VK_PERIOD) {
			setCurrentValue(getCurrentValue() + k.getKeyChar());
		}
		
		// Backspace
		else if(k.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
			if(getCurrentValue() != null && getCurrentValue().length() > 0) setCurrentValue(getCurrentValue().substring(0,getCurrentValue().length()-1));
		}
		
		// Enter
		else if(k.getKeyChar() == KeyEvent.VK_ENTER) {
			developer.selectButton(okay);
		}
		
		// Esc
		else if(k.getKeyChar() == KeyEvent.VK_ESCAPE) {
			developer.selectButton(cancel);
		}
	}
	
	// Draw the unit. 
	@Override
	public void drawObject(Graphics g) {
		
		// Set font.
		g.setFont(drawnObject.DEFAULT_FONT);
		
		// Draw background
		g.setColor(Color.GRAY);
		g.fillRect((int)(getIntX()*gameCanvas.getScaleX()),
				   (int)(getIntY()*gameCanvas.getScaleY()),
				   (int)(gameCanvas.getScaleX()*DEFAULT_WIDTH),
				   (int)(gameCanvas.getScaleY()*DEFAULT_HEIGHT));
		
		// Draw button name.
		g.setColor(DEFAULT_TEXT_COLOR);
		String cutName = stringUtils.toTitleCase(getName().substring(getName().indexOf('.')+1, getName().length())) + " Value: ";
		g.drawString(cutName, 
				(int)(gameCanvas.getScaleX()*(getIntX() + getWidth()/2) - (g.getFontMetrics().stringWidth(cutName)/2)), 
				(int)(gameCanvas.getScaleY()*(getIntY()+20)));
		g.drawString(getCurrentValue(), (int)(gameCanvas.getScaleX()*(getIntX() + getWidth()/2) - (g.getFontMetrics().stringWidth(getCurrentValue())/2)), 
				(int)(gameCanvas.getScaleY()*(getIntY()+40)));
		
	}
	
	// Respond to destroy
	@Override
	public void respondToDestroy() {
		okay.destroy();
		cancel.destroy();
		if(getCurrentBox() == this) setCurrentBox(null);
	}
	
	
	
	// Update unit
	@Override
	public void update() {
	}

	public static propertyEditBox getCurrentBox() {
		return currentBox;
	}

	public void setCurrentBox(propertyEditBox currentBox) {
		this.currentBox = currentBox;
	}

	public String getCurrentValue() {
		return currentValue;
	}

	public void setCurrentValue(String currentValue) {
		this.currentValue = currentValue;
	}
	
}
