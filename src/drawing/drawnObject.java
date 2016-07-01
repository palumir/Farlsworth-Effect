package drawing;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.CopyOnWriteArrayList;

import doodads.general.questMark;
import drawing.userInterface.interfaceObject;
import drawing.userInterface.tooltipString;
import effects.effect;
import effects.projectile;
import effects.effectTypes.floatingString;
import items.item;
import units.player;
import units.unit;
import modes.mode;
import terrain.chunk;
import terrain.groundTile;
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
	
	// Comparator for platformer.
	private static Comparator<drawnObject> platformerComparator =  new Comparator<drawnObject>() {
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
					    	if(d1.isBackgroundDoodad() && !d2.isBackgroundDoodad()) return -6;
					    	else if(!d1.isBackgroundDoodad() && d2.isBackgroundDoodad()) return 6;
					    	else {
								int z1;
								int z2;
								
								// Get z1, assume 0 if not set.
								if(d1.getZ() == null) z1 = 0;
								else z1 = d1.getZ();
								
								// Get z2, assume 0 if not set.
								if(d2.getZ() == null) z2 = 0;
								else z2 = d2.getZ();
								
								// Return the comparison between the two.
								if(z1 - z2 > 0) return 1;
								if(z1 - z2 < 0) return -1;
								else return 0;
					    	}
					    }
			    	}
		    	}
	    	}
		}
	};
	
	// Comparator for topDown
	private static Comparator<drawnObject> topDownComparator = new Comparator<drawnObject>() {
		
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
			    	if(d1 instanceof effect && !(d1 instanceof projectile) && !(d2 instanceof effect) /*&& d1.getY()+d1.getHeight() <= d2.getY()*/) return 8;
			    	else if(d2 instanceof effect  && !(d2 instanceof projectile) && !(d1 instanceof effect) /*&& d2.getY()+d2.getHeight() <= d1.getY()*/) return -8;
			    	
			    	else {
				    	// Prioritize units walking over chunks
				    	// and units walking in front of other units.
				    	if(!(d1 instanceof groundTile) && d2 instanceof groundTile) return 7;
				    	else if(d1 instanceof groundTile && !(d2 instanceof groundTile)) return -7;
					    else {	
					    	if(d1.isBackgroundDoodad() && !d2.isBackgroundDoodad()) return -6;
					    	else if(!d1.isBackgroundDoodad() && d2.isBackgroundDoodad()) return 6;
					    	else {
						        // Draw units closer to the camera first.
						    	if(d1.y + d1.getHeight() > d2.y + d2.getHeight()) return 5;
						    	else if(d1.y + d1.getHeight() < d2.y + d2.getHeight()) return -5;
						    	else return 0;
					    	}
					    }
			    	}
		    	}
	    	}
	    }
	};
	
	//////////////
	/// FIELDS ///
	//////////////
	
	// X and Y
	private int x;
	private int y;
	
	// Z axis for platformer.
	private Integer z;
	
	// Draw X and Y, different from X and Y.
	protected int drawX;
	protected int drawY;
	
	// Width and height.
	private int width;
	private int height;
	
	// Do we actually draw the object?
	private boolean drawObject = true;
	
	// Is it drawn in the background? But above text tiles.
	private boolean backgroundDoodad = false;
	
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
	
	// Calculate drawX
	public static int calculateDrawX(drawnObject d, int getX) {
		int newDrawX = 0;
		// If there's a camera, adjust units drawn to the camera pos.
		if(camera.getCurrent() != null) {
			// TODO: possible issues with the screen being resized.
			newDrawX = getX - 
					camera.getCurrent().getX() - 
					camera.getCurrent().getAttachedUnit().getWidth()/2 + 
					gameCanvas.getDefaultWidth()/2;
		}
		else {
			newDrawX = getX;
		}
		
		// Get the correct sprite width and height.
		int spriteWidth = 0;
		if(d instanceof unit && ((unit)d).getCurrentAnimation() != null) {
			spriteWidth = ((unit)d).getCurrentAnimation().getCurrentFrame().getWidth();
		}
		else if(d.getObjectSpriteSheet() != null) {
			spriteWidth = d.getObjectSpriteSheet().getSpriteWidth();
		}
		else if(d.getObjectImage() != null) {
			spriteWidth = d.getObjectImage().getWidth();
		}
		else if(d instanceof item && ((item)d).getImage() != null) {
			spriteWidth =((item)d).getImage().getWidth();
		}
		
		// Adjust for hitboxes.
		 newDrawX += - (spriteWidth/2 - d.getWidth()/2) - d.getHitBoxAdjustmentX();
		 
		 // Adjust for scaling.
		 newDrawX = (int) (gameCanvas.getScaleX()*newDrawX);
		 return newDrawX;
	}
	
	// Calculate drawY
	public static int calculateDrawY(drawnObject d, int getY) {
		int newDrawY = 0;
		// If there's a camera, adjust units drawn to the camera pos.
		if(camera.getCurrent() != null) {
			newDrawY = getY - 
					camera.getCurrent().getY() - 
					camera.getCurrent().getAttachedUnit().getHeight()/2 + 
					gameCanvas.getDefaultHeight()/2;
		}
		else {
			newDrawY = getY;
		}
		
		// Get the correct sprite width and height.
		int spriteHeight = 0;
		if(d instanceof unit && ((unit)d).getCurrentAnimation() != null) {
			spriteHeight = ((unit)d).getCurrentAnimation().getCurrentFrame().getHeight();
		}
		else if(d.getObjectSpriteSheet() != null) {
			spriteHeight = d.getObjectSpriteSheet().getSpriteHeight();
		}
		else if(d.getObjectImage() != null) {
			spriteHeight = d.getObjectImage().getHeight();
		}
		else if(d instanceof item && ((item)d).getImage() != null) {
			spriteHeight =((item)d).getImage().getHeight();
		}
		
		// Adjust for hitboxes.
		 newDrawY += - (spriteHeight/2 - d.getHeight()/2) - d.getHitBoxAdjustmentY();
		 
		 // Adjust for scaling.
		newDrawY = (int) (gameCanvas.getScaleY()*newDrawY);
		return newDrawY;
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
					else if(d instanceof item && ((item)d).getImage() != null) {
						spriteWidth =((item)d).getImage().getWidth();
						spriteHeight =((item)d).getImage().getHeight();
					}
					
					 // Adjust.
					 d.drawX = calculateDrawX(d, d.getX());
					 d.drawY = calculateDrawY(d, d.getY());
					
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
		
		// If it's a projectile, remove it from proj list.
		if(this instanceof projectile) {
			projectile.allProjectiles.remove((projectile)this);
		}
		
		// If it's a chunk
		if(this instanceof chunk) {
			chunk.allChunks.remove((chunk)this);
			if(!((chunk)this).isPassable()) chunk.impassableChunks.remove((chunk)this);
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
		if(mode.getCurrentMode() == "topDown") Collections.sort(objects, topDownComparator);
		if(mode.getCurrentMode() == "platformer") Collections.sort(objects, platformerComparator);
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

	public boolean isBackgroundDoodad() {
		return backgroundDoodad;
	}

	public void setBackgroundDoodad(boolean backgroundDoodad) {
		this.backgroundDoodad = backgroundDoodad;
	}

	public Integer getZ() {
		return z;
	}

	public void setZ(Integer z) {
		this.z = z;
	}
	
}