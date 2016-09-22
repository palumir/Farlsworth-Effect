package UI;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import drawing.drawnObject;
import drawing.gameCanvas;

public class button extends interfaceObject  {
	
	/////////////////////////
	////// DEFAULTS /////////
	/////////////////////////
	
	// Colors
	public static Color DEFAULT_TEXT_COLOR = Color.white;
	public static Color DEFAULT_BACKGROUND_COLOR = Color.BLUE;
	
	///////////////////////
	////// FIELDS /////////
	///////////////////////
	
	// Button ID
	private String buttonID;
	
	// Children
	private ArrayList<button> children;
	private button parent;
	
	// For children
	private int moveOverNumber = 0;
	private int rowNumber = 0;

	///////////////
	/// METHODS ///
	///////////////

	// Constructor
	public button(String buttonName, String buttonID, int newX, int newY, int width, int height) {
		super(null, newX, newY, width, height);	
		setName(buttonName);
		this.setButtonID(buttonID);
		setZ(1);
	}
	
	// Draw the unit. 
	@Override
	public void drawObject(Graphics g) {
		
		// Set font.
		g.setFont(drawnObject.DEFAULT_FONT);
		
		// Draw button background
		g.setColor(DEFAULT_BACKGROUND_COLOR);
		g.fillRect((int)(gameCanvas.getScaleX()*getIntX()), 
				(int)(gameCanvas.getScaleY()*getIntY()), 
				(int)(gameCanvas.getScaleX()*getWidth()), 
				(int)(gameCanvas.getScaleY()*getHeight()));
		
		// Draw button name.
		g.setColor(DEFAULT_TEXT_COLOR);
		g.drawString(getName(), 
				(int)(gameCanvas.getScaleX()*(getIntX() + getWidth()/2) - (g.getFontMetrics().stringWidth(getName())/2)), 
				(int)(gameCanvas.getScaleY()*(getIntY() + getHeight()/2) +  + (g.getFontMetrics().getHeight())/4));
	}
	
	// 
	public boolean isButtonWithinRadius(int x, int y, int radius) {
	    int circleDistanceX = (int) Math.abs(x - gameCanvas.getScaleX()*(this.getIntX() + this.getWidth()/2));
	    int circleDistanceY = (int) Math.abs(y - gameCanvas.getScaleY()*(this.getIntY() + this.getHeight()/2));

	    if (circleDistanceX > gameCanvas.getScaleX()*(this.getWidth()/2 + radius)) { return false; }
	    if (circleDistanceY > gameCanvas.getScaleY()*(this.getHeight()/2 + radius)) { return false; }

	    if (circleDistanceX <= gameCanvas.getScaleX()*(this.getWidth()/2)) { return true; } 
	    if (circleDistanceY <= gameCanvas.getScaleY()*(this.getHeight()/2)) { return true; }

	    int cornerDistanceSQ = (int) (Math.pow(circleDistanceX - gameCanvas.getScaleX()*this.getWidth()/2,2) +
	                         Math.pow(circleDistanceY - gameCanvas.getScaleY()*this.getHeight()/2,2));

	    return (cornerDistanceSQ <= Math.pow(radius,2));
	}
	
	// Button touch radius
	private static int touchRadius = 2;
	
	// Get button at
	public static button getButtonAt(int x, int y) {
		for(int i = 0; i < interfaceObjects.size(); i++) {
			drawnObject d = interfaceObjects.get(i);
			if(d instanceof button && ((button)d).isButtonWithinRadius(x,y, touchRadius) && d.isDrawObject()) {
				return (button)d;
			}
		}
		return null;
	}
	
	// Respond to destroy
	@Override
	public void respondToDestroy() {
		if(interfaceObjects.contains(this)) interfaceObjects.remove(this);
		if(children != null) {
			recursivelyDestroy(children);
		}
	}
	
	// Hide all child buttons
	public static void hideAllChildButtons() {
		for(int i = 0; i < interfaceObjects.size(); i++) {
			drawnObject d = interfaceObjects.get(i);
			if(d instanceof button && d.isDrawObject() && ((button) d).parent != null) {
				((button)d).setDrawObject(false);
			}
		}
	}
	
	// Add child
	public void addChild(button b) {
		if(getChildren() == null) setChildren(new ArrayList<button>());
		if(!getChildren().contains(b)) {
			b.setDrawObject(false);
			getChildren().add(b);

			// If we go off the screen, move over.
			if(this.getDoubleY() + (rowNumber)*b.getHeight()*4/3 + b.getHeight() > gameCanvas.getDefaultHeight()) {
				moveOverNumber++;
				rowNumber = 0;
			}
			b.setDoubleX(this.getDoubleX() + 100*(moveOverNumber+1));
			b.setDoubleY(this.getDoubleY() + rowNumber*b.getHeight()*4/3);
			rowNumber++;
			b.setParent(this);
		}
	}
	
	// Select default shows children.
	public void select() {
		if(getChildren() != null) {
			for(int i = 0; i < getChildren().size(); i++) {
				getChildren().get(i).setDrawObject(true);
			}
		}
		else if(getParent() != null) {
			for(int i = 0; i < getParent().getChildren().size(); i++) {
				getParent().getChildren().get(i).setDrawObject(false);
			}
		}
	}
	
	public static void recursivelyDestroy(ArrayList<button> buttons) {
		for(int i = 0; i < buttons.size(); i++) {
			if(buttons.get(i).getChildren() != null && buttons.get(i).getChildren().size() > 0) {
				recursivelyDestroy(buttons.get(i).getChildren());
			}
			buttons.get(i).destroy();
		}
	}
	
	public button getTopParent() {
		if(getParent() == null) return this;
		return getParent().getTopParent();
	}
	
	public static void recursivelyHideChildren(ArrayList<button> buttons) {
		if(buttons != null) {
			for(int i = 0; i < buttons.size(); i++) {
				if(buttons.get(i).getChildren() != null && buttons.get(i).getChildren().size() > 0) {
					recursivelyHide(buttons.get(i).getChildren());
				}
			}
		}
	}
	
	public static void recursivelyHide(ArrayList<button> buttons) {
		if(buttons != null) {
			for(int i = 0; i < buttons.size(); i++) {
				if(buttons.get(i).getChildren() != null && buttons.get(i).getChildren().size() > 0) {
					recursivelyHide(buttons.get(i).getChildren());
				}
				buttons.get(i).setDrawObject(false);
			}
		}
	}
	
	
	// Update unit
	@Override
	public void update() {
	}

	public String getButtonID() {
		return buttonID;
	}

	public void setButtonID(String buttonID) {
		this.buttonID = buttonID;
	}

	public ArrayList<button> getChildren() {
		return children;
	}

	public void setChildren(ArrayList<button> children) {
		this.children = children;
	}

	public button getParent() {
		return parent;
	}

	public void setParent(button parent) {
		this.parent = parent;
	}
	
}
