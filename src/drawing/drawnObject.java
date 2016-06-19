package drawing;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.CopyOnWriteArrayList;

import drawing.userInterface.interfaceObject;
import effects.effect;
import effects.effectTypes.floatingString;
import effects.effectTypes.tooltipString;
import units.player;
import units.unit;
import modes.mode;
import terrain.chunk;
import terrain.groundTile;
import terrain.doodads.general.questMark;
import utilities.intTuple;

// A class for any object that is drawn in the
// canvas.
public abstract class drawnObject {
	
	////////////////
	/// DEFAULTS ///
	////////////////

	// Font
	public static String DEFAULT_FONT_NAME = "TimesRoman";
	public static int DEFAULT_FONT_SIZE = 12;
	protected static Font DEFAULT_FONT = null;
	
	// Comparator
	private static Comparator<drawnObject> yComparator = new Comparator<drawnObject>() {
		
	    @Override
	    public int compare(drawnObject d1, drawnObject d2) {
	    	
	    	// Draw floating numbers over ...
	    	if(d1 instanceof floatingString && !(d2 instanceof floatingString)) return 10;
	    	else if(d2 instanceof floatingString && !(d1 instanceof floatingString)) return -10;
	    	else {
	    	
		    	// Draw interface objects over ...
		    	if(d1 instanceof interfaceObject && !(d2 instanceof interfaceObject)) return 9;
		    	else if(d2 instanceof interfaceObject && !(d1 instanceof interfaceObject)) return -9;
		    	else {
		    	
			    	// Different comparator for drawing effects over ...
			    	if(d1 instanceof effect && !(d2 instanceof effect) /*&& d1.getY()+d1.getHeight() <= d2.getY()*/) return 8;
			    	else if(d2 instanceof effect && !(d1 instanceof effect) /*&& d2.getY()+d2.getHeight() <= d1.getY()*/) return -8;
			    	
			    	else {
				    	// Prioritize units walking over chunks
				    	// and units walking in front of other units.
				    	if(!(d1 instanceof groundTile) && d2 instanceof groundTile) return 7;
				    	else if(d1 instanceof groundTile && !(d2 instanceof groundTile)) return -7;
					    else {	
					        // Draw units closer to the camera first.
					    	if(d1.y + d1.getHeight() > d2.y + d2.getHeight()) return 6;
					    	else if(d1.y + d1.getHeight() < d2.y + d2.getHeight()) return -6;
					    	else return 0;
					    }
			    	}
		    	}
	    	}
	    }
	};
	
	//////////////
	/// FIELDS ///
	//////////////
	private int x;
	private int y;
	protected int drawX;
	protected int drawY;
	private int width;
	private int height;
	
	// Do we actually draw the object?
	private boolean drawObject = true;
	
	// Can we interact with the object?
	protected boolean interactable = false;
	
	// Developer stuff
	protected boolean showHitBox = false;
	protected boolean showSpriteBox = false;
	protected boolean showUnitPosition = false;
	
	// Sprite stuff.
	private spriteSheet objectSpriteSheet;
	private BufferedImage objectImage;
	private int hitBoxAdjustmentX;
	private int hitBoxAdjustmentY;
	
	// Camera attached to the object,
	protected camera attachedCamera = null;
	
	// A list of things that we need to draw in general.
	public static CopyOnWriteArrayList<drawnObject> objects;
	
	///////////////
	/// METHODS ///
	///////////////
	// drawnObject constructor
	public drawnObject(spriteSheet newSpriteSheet, int newX, int newY, int newWidth, int newHeight) {
		objectSpriteSheet = newSpriteSheet;
		if(objectSpriteSheet!=null) {
			setHitBoxAdjustmentX(objectSpriteSheet.getHitBoxAdjustmentX());
			setHitBoxAdjustmentY(objectSpriteSheet.getHitBoxAdjustmentY());
		}
		setX(newX);
		setY(newY);
		setWidth(newWidth);
		setHeight(newHeight);
		addObject(this);
	}
	
	// Get units in box.
	public static ArrayList<drawnObject> getObjectsInBox(int x1, int y1, int x2, int y2) {
		ArrayList<drawnObject> returnList = new ArrayList<drawnObject>();
		for(int i = 0; i < objects.size(); i++) {
			drawnObject o = objects.get(i);
			if(o.getX() < x2 && 
					 o.getX() + o.getWidth() > x1 && 
					 o.getY() < y2 && 
					 o.getY() + o.getHeight() > y1) {
				returnList.add(o);
			}
		}
		if(returnList.size()==0) return null;
		return returnList;
	}
	
	// Every thing needs to update itself in some way.
	public void update() {
		// Do nothing for basic objects.
	}
	
	// Draw all objects.
	public static void drawObjects(Graphics g) {
		
		// Set default font.
		if(DEFAULT_FONT == null) {
			DEFAULT_FONT = new Font(DEFAULT_FONT_NAME, Font.PLAIN, DEFAULT_FONT_SIZE); 
		}
		g.setFont(DEFAULT_FONT);

		
		if(objects != null) {
			sortObjects();
			for(int i = 0; i < objects.size(); i++) {
				drawnObject d = objects.get(i);
				
				if(d.isDrawObject()) {
					// If there's a camera, adjust units drawn to the camera pos.
					if(camera.getCurrent() != null) {
						// TODO: possible issues with the screen being resized.
						d.drawX = d.getX() - 
								camera.getCurrent().getX() - 
								camera.getCurrent().getAttachedUnit().getWidth()/2 + 
								gameCanvas.getDefaultWidth()/2;
						d.drawY = d.getY() - 
								camera.getCurrent().getY() - 
								camera.getCurrent().getAttachedUnit().getHeight()/2 + 
								gameCanvas.getDefaultHeight()/2;
					}
					else {
						d.drawX = d.getX();
						d.drawY = d.getY();
					}
					
					// Get the correct sprite width and height.
					int spriteWidth = 0;
					int spriteHeight = 0;
					if(d instanceof unit && ((unit)d).getCurrentAnimation() != null) {
						spriteWidth = ((unit)d).getCurrentAnimation().getCurrentFrame().getWidth();
						spriteHeight = ((unit)d).getCurrentAnimation().getCurrentFrame().getHeight();
					}
					else if(d.getObjectSpriteSheet() != null) {
						spriteWidth = d.getObjectSpriteSheet().getSpriteWidth();
						spriteHeight = d.getObjectSpriteSheet().getSpriteHeight();
					}
					else if(d.getObjectImage() != null) {
						spriteWidth = d.getObjectImage().getWidth();
						spriteHeight = d.getObjectImage().getHeight();
					}
					
					// Adjust for hitboxes.
					 d.drawX += - (spriteWidth/2 - d.getWidth()/2) - d.getHitBoxAdjustmentX();
					 d.drawY += - (spriteHeight/2 - d.getHeight()/2) - d.getHitBoxAdjustmentY();
					 
					 // Adjust for scaling.
					 d.drawX = (int) (gameCanvas.getScaleX()*d.drawX);
					 d.drawY = (int) (gameCanvas.getScaleY()*d.drawY);
					
					// Draw the object if it's on the screen.
					if(d instanceof tooltipString ||
						d instanceof interfaceObject ||
					   (d.drawX + gameCanvas.getScaleX()*spriteWidth > 0 && 
					   d.drawY + gameCanvas.getScaleY()*spriteHeight > 0 && 
					   d.drawX < gameCanvas.getActualWidth() && 
					   d.drawY < gameCanvas.getActualHeight())) {
						d.drawObject(g);
					}
				}
			}
		}
	}
	
	// Returns a tuple containing pertaining to how much the object would
	// be inside checkObject if moved to newX and newY. 0,0 otherwise.
	public boolean collides(int newX, int newY, drawnObject checkObject) {
		// Check each side.
		boolean intercepts = false;
		if(checkObject !=null) {
			intercepts = newX < checkObject.getX() + checkObject.getWidth() && 
								 newX + getWidth() > checkObject.getX() && 
								 newY < checkObject.getY() + checkObject.getHeight() && 
								 newY + getHeight() > checkObject.getY();
		}
		return intercepts;
	}
	
	// Initiate drawnObjects
	public static void initiate() {
		objects = new CopyOnWriteArrayList<drawnObject>();
	}
	
	// Draw object.
	public abstract void drawObject(Graphics g);
	
	// Destroy an object.
	public void destroy() {
		drawnObject.removeObject(this);
		
		// If it's a unit, remove it from list.
		if(this instanceof unit) {
			unit.getAllUnits().remove((unit)this);
		}
	}
	
	// Interact with object. Should be over-ridden.
	public void interactWith() {
		
	}
	
	///////////////////////////
	/// Getters and Setters ///
	///////////////////////////
	public boolean canInteract() {
		return interactable;
	}
	
	public void showHitBox() {
		showHitBox = true;
	}
	
	public void showSpriteBox() {
		showSpriteBox = true;
	}
	
	public void showUnitPosition() {
		showUnitPosition = true;
	}
	
	public static void addObject(drawnObject d) {
		objects.add(d);
	}
	
	public static void removeObject(drawnObject d) {
		objects.remove(d);
	}
	
	public static void sortObjects() {
		if(mode.getCurrentMode() == "topDown") Collections.sort(objects, yComparator);
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public spriteSheet getObjectSpriteSheet() {
		return objectSpriteSheet;
	}

	public void setObjectSpriteSheet(spriteSheet objectSpriteSheet) {
		this.objectSpriteSheet = objectSpriteSheet;
	}

	public int getHitBoxAdjustmentY() {
		return hitBoxAdjustmentY;
	}

	public void setHitBoxAdjustmentY(int hitBoxAdjustmentY) {
		this.hitBoxAdjustmentY = hitBoxAdjustmentY;
	}

	public int getHitBoxAdjustmentX() {
		return hitBoxAdjustmentX;
	}

	public void setHitBoxAdjustmentX(int hitBoxAdjustmentX) {
		this.hitBoxAdjustmentX = hitBoxAdjustmentX;
	}

	public boolean isDrawObject() {
		return drawObject;
	}

	public void setDrawObject(boolean drawObject) {
		this.drawObject = drawObject;
	}

	public BufferedImage getObjectImage() {
		return objectImage;
	}

	public void setObjectImage(BufferedImage objectImage) {
		this.objectImage = objectImage;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}
	
}