package drawing;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.CopyOnWriteArrayList;

import UI.interfaceObject;
import UI.tooltipString;
import effects.effect;
import effects.projectile;
import effects.interfaceEffects.floatingString;
import effects.interfaceEffects.interactBlurb;
import items.item;
import modes.mode;
import terrain.chunk;
import terrain.groundTile;
import units.unit;
import utilities.mathUtils;

// A class for any object that is drawn in the
// canvas.
public abstract class drawnObject {
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Are chunks important?
	public static boolean reloadTheFollowingObjects = true;

	// Font
	public static String DEFAULT_FONT_NAME = "SansSerif";
	public static int DEFAULT_FONT_SIZE = 11;
	public static Font DEFAULT_FONT = null;
	protected static Font DEFAULT_FONT_BOLD = null;
	
	// Comparator for platformer.
	private static Comparator<drawnObject> platformerComparator =  new Comparator<drawnObject>() {
		@Override
	    public int compare(drawnObject d1, drawnObject d2) {
			
			if(d1.attachedToObject != null) d1 = d1.attachedToObject;
			if(d2.attachedToObject != null) d2 = d2.attachedToObject;
	    	
	    	// Draw floating numbers over ...
	    	if(d1 instanceof floatingString && !(d2 instanceof floatingString)) return 10;
	    	else if(d2 instanceof floatingString && !(d1 instanceof floatingString)) return -10;
	    	else {
	    	
		    	// Draw interface objects over ...
		    	if(d1 instanceof interfaceObject && !(d2 instanceof interfaceObject)) return 9;
		    	else if(d2 instanceof interfaceObject && !(d1 instanceof interfaceObject)) return -9;
		    	else {
			    	if(d1.isBackgroundDoodad() && !d2.isBackgroundDoodad()) return -8;
			    	else if(!d1.isBackgroundDoodad() && d2.isBackgroundDoodad()) return 8;
			    	else {
				    	// Prioritize units walking over chunks
				    	// and units walking in front of other units.
				    	if(!(d1 instanceof groundTile) && d2 instanceof groundTile) return 7;
				    	else if(d1 instanceof groundTile && !(d2 instanceof groundTile)) return -7;
					    else {	
				    		if(d1.isForceInFront() && !d2.isForceInFront()) return 6;
				    		else if(!d1.isForceInFront() && d2.isForceInFront()) return -6;
					    	else {
					    		// Different comparator for drawing effects over ...
					    		if(d1 instanceof effect && !(d2 instanceof effect) /*&& d1.getY()+d1.getHeight() <= d2.getY()*/) return 5;
					    		else if(d2 instanceof effect && !(d1 instanceof effect) /*&& d2.getY()+d2.getHeight() <= d1.getY()*/) return -5;
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
		}
	};
	
	// Comparator for topDown
	private static Comparator<drawnObject> topDownComparator = new Comparator<drawnObject>() {
		
	    @Override
	    public int compare(drawnObject d1, drawnObject d2) {
	    	
			if(d1.attachedToObject != null) d1 = d1.attachedToObject;
			if(d2.attachedToObject != null) d2 = d2.attachedToObject;
	    	
	    	// Draw floating numbers over ...
	    	if(d1 instanceof floatingString && !(d2 instanceof floatingString)) return 10;
	    	else if(d2 instanceof floatingString && !(d1 instanceof floatingString)) return -10;
	    	else {
	    	
		    	// Draw interface objects over ...
		    	if(d1 instanceof interfaceObject && !(d2 instanceof interfaceObject)) return 9;
		    	else if(d2 instanceof interfaceObject && !(d1 instanceof interfaceObject)) return -9;
		    	else {
		    		// Prioritize units walking over chunks
			    	// and units walking in front of other units.
			    	if(!(d1 instanceof groundTile) && d2 instanceof groundTile) return 8;
			    	else if(d1 instanceof groundTile && !(d2 instanceof groundTile)) return -8;
			    	else {
			    		if(d1.isForceInFront() && !d2.isForceInFront()) return 7;
			    		else if(!d1.isForceInFront() && d2.isForceInFront()) return -7;
				    	else {
				    		if(d1 instanceof interfaceObject && d2 instanceof interfaceObject) {
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
				    		else {
						    	if(d1.isBackgroundDoodad() && !d2.isBackgroundDoodad()) return -5;
						    	else if(!d1.isBackgroundDoodad() && d2.isBackgroundDoodad()) return 5;
							    else {	
							    		
							    		
								    // Draw units closer to the camera first.
								    if(d1.getIntY() + d1.getHeight() > d2.getIntY() + d2.getHeight()) return 4;
								    else if(d1.getIntY() + d1.getHeight() < d2.getIntY() + d2.getHeight()) return -4;
								    else return 0;
							    }
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
	protected double doubleX;
	protected double doubleY;
	
	// Draw sprite?
	private boolean drawSprite = true;
	
	// Name of object
	private String name;
	
	// Attached to a unit?
	private drawnObject attachedToObject;
	private double relativeX;
	private double relativeY;
	
	// Is the object grabbable?
	private boolean smallObject = false;
	
	// Z axis for platformer.
	private Integer z;
	
	// Draw in front?
	protected boolean forceInFront = false;
	
	// Does this object exist?
	private boolean exists = true;
	
	// Where was it spawned
	private int spawnedAtX;
	private int spawnedAtY;
	
	// Width and height.
	private int width;
	private int height;
	
	// Do we actually draw the object?
	private boolean drawObject = true;
	
	// Is it drawn in the background? But above text tiles.
	protected boolean backgroundDoodad = false;
	
	// Can we interact with the object?
	private boolean interactable = false;
	private boolean showInteractable = true;
	
	// Attached objects to unit
	protected ArrayList<drawnObject> attachedObjects;
	
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
	
	// Is the chunk important enough to reload?
	protected boolean reloadObject = true;

	// Is the object being interacted with?
	private boolean beingInteracted;
	
	// Unimportant objects
	public static ArrayList<drawnObject> dontReloadTheseObjects = new ArrayList<drawnObject>();
	
	// A list of things that we need to draw in general.
	public static CopyOnWriteArrayList<drawnObject> objects;
	
	///////////////
	/// METHODS ///
	///////////////
	// drawnObject constructor
	public drawnObject(spriteSheet newSpriteSheet, String newName, int newX, int newY, int newWidth, int newHeight) {
		objectSpriteSheet = newSpriteSheet;
		if(objectSpriteSheet!=null) {
			setHitBoxAdjustmentX(objectSpriteSheet.getHitBoxAdjustmentX());
			setHitBoxAdjustmentY(objectSpriteSheet.getHitBoxAdjustmentY());
		}
		setName(newName);
		setDoubleX(newX);
		setDoubleY(newY);
		setSpawnedAtX(newX);
		setSpawnedAtY(newY);
		setWidth(newWidth);
		setHeight(newHeight);
		addObject(this);
		setReloadObject(reloadTheFollowingObjects);
	}
	
	// Get drawnObjects in box.
	public static ArrayList<drawnObject> getObjectsInBox(int x1, int y1, int x2, int y2) {
		ArrayList<drawnObject> returnList = new ArrayList<drawnObject>();
		for(int i = 0; i < objects.size(); i++) {
			drawnObject o = objects.get(i);
			if(o.getIntX() < x2 && 
					 o.getIntX() + o.getWidth() > x1 && 
					 o.getIntY() < y2 && 
					 o.getIntY() + o.getHeight() > y1) {
				returnList.add(o);
			}
		}
		if(returnList.size()==0) return null;
		return returnList;
	}
	
	// Make copy to be overrided.
	public drawnObject makeCopy() {
		return null;
	}
	
	// Every thing needs to update itself in some way.
	public void update() {
		// Do nothing for basic objects.
	}
	
	// Get objects in box.
	public static ArrayList<drawnObject> getObjectsInRadius(int x, int y, int radius) {
		ArrayList<drawnObject> returnList = new ArrayList<drawnObject>();
		for(int i = 0; i < objects.size(); i++) {
			drawnObject u = objects.get(i);
			if(u.isWithinRadius(x, y, radius)) {
				returnList.add(u);
			}
		}
		if(returnList.size()==0) return null;
		return returnList;
	}
	
	// Check if a drawn object contains a point
	public boolean contains(int x, int y) {
		return x > this.getIntX() && x < this.getIntX() + this.getWidth() && y > this.getIntY() && y < this.getIntY() + this.getHeight();
	}
	
	// Get whether a object is within radius
	public boolean isWithinRadius(int x, int y, int radius) {
	    int circleDistanceX = Math.abs(x - (this.getIntX() + this.getWidth()/2));
	    int circleDistanceY = Math.abs(y - (this.getIntY() + this.getHeight()/2));

	    if (circleDistanceX > (this.getWidth()/2 + radius)) { return false; }
	    if (circleDistanceY > (this.getHeight()/2 + radius)) { return false; }

	    if (circleDistanceX <= (this.getWidth()/2)) { return true; } 
	    if (circleDistanceY <= (this.getHeight()/2)) { return true; }

	    int cornerDistanceSQ = (int) (Math.pow(circleDistanceX - this.getWidth()/2,2) +
	                         Math.pow(circleDistanceY - this.getHeight()/2,2));

	    return (cornerDistanceSQ <= Math.pow(radius,2));
	}
	
	
	// Convert drawn point to in game position.
	public static Point toInGamePos(Point p) {
		Point inGamePointCurrent = new Point(p.x + camera.getCurrent().getX() + camera.getCurrent().getAttachedUnit().getWidth()/2 - gameCanvas.getDefaultWidth()/2, 
			      p.y + camera.getCurrent().getY() + camera.getCurrent().getAttachedUnit().getHeight()/2 - gameCanvas.getDefaultHeight()/2);
		return inGamePointCurrent;
	}
	
	// Convert point to draw position based on camera position.
	public static Point toDrawPos(Point p) {
		Point inGamePointCurrent = new Point(p.x - (camera.getCurrent().getX() + camera.getCurrent().getAttachedUnit().getWidth()/2 - gameCanvas.getDefaultWidth()/2), 
			      p.y - (camera.getCurrent().getY() + camera.getCurrent().getAttachedUnit().getHeight()/2 - gameCanvas.getDefaultHeight()/2));
		return inGamePointCurrent;
	}
	
	// Get closest to
	public drawnObject getClosestToFrom(ArrayList<drawnObject> checkObjects) {
		drawnObject closestTo = null;
		int howCloseBest = Integer.MAX_VALUE;
		for(int i = 0; i < checkObjects.size(); i++) {
			drawnObject currObj = checkObjects.get(i);
			int howClose = (int) Math.sqrt((currObj.getIntX() + currObj.getWidth()/2 - getIntX() - getWidth()/2)*(currObj.getIntX() + currObj.getWidth()/2 - getIntX() - getWidth()/2) + (currObj.getIntY() + currObj.getHeight()/2 - getIntY() - getHeight()/2)*(currObj.getIntY() + currObj.getHeight()/2 - getIntY() - getHeight()/2));
			if(howClose < howCloseBest) {
				howCloseBest = howClose;
				closestTo = currObj;
			}
		}
		return closestTo;
	}
	
	// Get closest to
	public static drawnObject getClosestToFrom(int x, int y, ArrayList<drawnObject> checkObjects) {
		drawnObject closestTo = null;
		int howCloseBest = Integer.MAX_VALUE;
		for(int i = 0; i < checkObjects.size(); i++) {
			drawnObject currObj = checkObjects.get(i);
			int howClose = (int) Math.sqrt((currObj.getIntX() + currObj.getWidth()/2 - x)*(currObj.getIntX() + currObj.getWidth()/2 - x) + (currObj.getIntY() + currObj.getHeight()/2 - y)*(currObj.getIntY() + currObj.getHeight()/2 - y));
			if(howClose < howCloseBest) {
				howCloseBest = howClose;
				closestTo = currObj;
			}
		}
		return closestTo;
	}
	
	// Get top left out of
	public static drawnObject getTopLeftFrom(ArrayList<drawnObject> checkObjects) {
		int minX = Integer.MAX_VALUE;
		int minY = Integer.MAX_VALUE;
		for(int i = 0; i < checkObjects.size(); i++) {
			drawnObject currObj = checkObjects.get(i);
			if(currObj.getIntX() < minX) minX = currObj.getIntX();
			if(currObj.getIntY() < minY) minY = currObj.getIntY();
		}
		return getClosestToFrom(minX,minY,checkObjects);
	}
	
	// Get angle between (in degrees) 
	public int getAngleBetween(drawnObject d) {
		double currentDegree = mathUtils.getAngleBetween(d.getIntX()+d.getWidth()/2, d.getIntY() + d.getHeight()/2, 
				getIntX()+getWidth()/2, getIntY() + getHeight()/2);
		if(currentDegree < 0) currentDegree += 360 + currentDegree;
		return (int)currentDegree;
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
			DEFAULT_FONT_BOLD = new Font(DEFAULT_FONT_NAME, Font.BOLD, DEFAULT_FONT_SIZE); 
		}
		g.setFont(DEFAULT_FONT);

		
		if(objects != null) {
			sortObjects();
			for(int i = 0; i < objects.size(); i++) {
				drawnObject d = objects.get(i);
				
				if(d.isDrawObject()) {

					// Draw the object if it's on the screen.
					if(d instanceof tooltipString ||
						d instanceof interfaceObject ||
					   d.isOnScreen()) {
						d.drawObject(g);
					}
				}
			}
		}
	}
	
	// Is on screen?
	public boolean isOnScreen() {
		// Get the correct sprite width and height.
		int spriteWidth = 0;
		int spriteHeight = 0;
		if(this instanceof unit && ((unit)this).getCurrentAnimation() != null) {
			spriteWidth = ((unit)this).getCurrentAnimation().getCurrentFrame().getWidth();
			spriteHeight = ((unit)this).getCurrentAnimation().getCurrentFrame().getHeight();
		}
		else if(this.getObjectSpriteSheet() != null) {
			spriteWidth = this.getObjectSpriteSheet().getSpriteWidth();
			spriteHeight = this.getObjectSpriteSheet().getSpriteHeight();
		}
		else if(this.getObjectImage() != null) {
			spriteWidth = this.getObjectImage().getWidth();
			spriteHeight = this.getObjectImage().getHeight();
		}
		else if(this instanceof item && ((item)this).getImage() != null) {
			spriteWidth =((item)this).getImage().getWidth();
			spriteHeight =((item)this).getImage().getHeight();
		}
		return (this.getDrawX() + gameCanvas.getScaleX()*spriteWidth > 0 && 
				   this.getDrawY() + gameCanvas.getScaleY()*spriteHeight > 0 && 
				   this.getDrawX() < gameCanvas.getActualWidth() && 
				   this.getDrawY() < gameCanvas.getActualHeight());
	}
	
	// Returns a tuple containing pertaining to how much the object would
	// be inside checkObject if moved to newX and newY. 0,0 otherwise.
	public boolean collides(int newX, int newY, drawnObject checkObject) {
		// Check each side.
		boolean intercepts = false;
		if(checkObject !=null) {
			intercepts = newX < checkObject.getIntX() + checkObject.getWidth() && 
								 newX + getWidth() > checkObject.getIntX() && 
								 newY < checkObject.getIntY() + checkObject.getHeight() && 
								 newY + getHeight() > checkObject.getIntY();
		}
		return intercepts;
	}
	
	// Initiate drawnObjects
	public static void initiate() {
		objects = new CopyOnWriteArrayList<drawnObject>();
		if(dontReloadTheseObjects!=null && dontReloadTheseObjects.size() > 0) {
			objects = new CopyOnWriteArrayList<drawnObject>(dontReloadTheseObjects);
			chunk.impassableChunks = new CopyOnWriteArrayList<chunk>();
			for(int i = 0; i < objects.size(); i++) {
				drawnObject d = objects.get(i);
				if(d instanceof chunk) {
					if(!((chunk)d).isPassable())  chunk.impassableChunks.add((chunk)d);
				}
			}
		}
		else {
			chunk.impassableChunks = new CopyOnWriteArrayList<chunk>();
		}
	}
	
	// Draw object.
	public abstract void drawObject(Graphics g);
	
	// Destroy an object.
	public void destroy() {
		setExists(false);
		drawnObject.removeObject(this);
		
		// If it's attached, remove it
		if(attachedToObject!=null) {
			attachedToObject.unnattachFromObject(this);
		}
		
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
		
		// Respond to destruction
		respondToDestroy();
	}
	
	// Respond to destruction
	public void respondToDestroy() {
		
	}
	
	// Destroy all
	public static void destroyAll(ArrayList<drawnObject> d) {
		if(d != null) {
			for(int i = 0; i < d.size(); i++) {
				d.get(i).destroy();
			}
		}
	}
	
	// Destroy all
	public static void destroyAll() {
		if(objects !=null) {
			for(; 0 < drawnObject.objects.size();) {
				objects.get(0).destroy();
			}
		}
	}
	
	// Interact with object. Should be over-ridden.
	public void interactWith() {
		
	}
	
	///////////////////////////
	/// Getters and Setters ///
	///////////////////////////
	
	// Attach u to this.
	public void attachToObject(drawnObject u) {
		setAttachedObject(u);
		u.setAttached(this);
		setRelativeX(this.getDoubleX() - u.getDoubleX());
		setRelativeY(this.getDoubleY() - u.getDoubleY());
	}
	
	// Attach d to current unit
	public void setAttached(drawnObject d) {
		if(attachedObjects == null) attachedObjects = new ArrayList<drawnObject>();
		if(!attachedObjects.contains(d)) attachedObjects.add(d);
	}
	
	// Unnattach
	public void unnattachFromObject(drawnObject d) {
		if(attachedObjects != null && attachedObjects.contains(d)) attachedObjects.remove(d);
	}
	
	public boolean canInteract() {
		return isInteractable();
	}
	
	public void showHitBox() {
		showHitBox = true;
	}
	
	public void dontShowHitBox() {
		showHitBox =false;
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

	public int getIntY() {
		return (int)doubleY;
	}
	
	public double getDoubleY() {
		return doubleY;
	}

	public void setDoubleY(double y) {
		this.doubleY = y;
	}
	
	public double getDoubleX() {
		return doubleX;
	}

	public int getIntX() {
		return (int)doubleX;
	}

	public void setDoubleX(double x) {
		this.doubleX = x;
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

	public int getDrawX() {
		return calculateDrawX(this, this.getIntX());
	}

	public int getDrawY() {
		return calculateDrawY(this, this.getIntY());
	}

	public boolean isDrawSprite() {
		return drawSprite;
	}

	public void setDrawSprite(boolean drawSprite) {
		this.drawSprite = drawSprite;
	}

	public boolean isInteractable() {
		return interactable;
	}

	public void setInteractable(boolean interactable) {
		this.interactable = interactable;
	}

	public boolean isForceInFront() {
		return forceInFront;
	}

	public void setForceInFront(boolean forceInFront) {
		this.forceInFront = forceInFront;
	}

	public boolean isExists() {
		return exists;
	}

	public void setExists(boolean exists) {
		this.exists = exists;
	}
	
	public void setReloadObject(boolean importantEnoughToReload) {
		this.reloadObject = importantEnoughToReload;
		if(importantEnoughToReload && dontReloadTheseObjects.contains(this)) dontReloadTheseObjects.remove(this);
		if(!importantEnoughToReload && !dontReloadTheseObjects.contains(this)) dontReloadTheseObjects.add(this);
	}

	public double getRelativeX() {
		return relativeX;
	}

	public void setRelativeX(double relativeX) {
		this.relativeX = relativeX;
	}

	public double getRelativeY() {
		return relativeY;
	}

	public void setRelativeY(double relativeY) {
		this.relativeY = relativeY;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public interactBlurb getAttachedInteractBlurb() {
		if(attachedObjects!=null) {
			for(int i = 0; i < attachedObjects.size(); i++) {
				if(attachedObjects.get(i) instanceof interactBlurb) return (interactBlurb)attachedObjects.get(i);
			}
		}
		return null;
	}

	public boolean isBeingInteracted() {
		return beingInteracted;
	}

	public void setBeingInteracted(boolean beingInteracted) {
		this.beingInteracted = beingInteracted;
	}

	public boolean isShowInteractable() {
		return showInteractable;
	}

	public void setShowInteractable(boolean showInteractable) {
		this.showInteractable = showInteractable;
	}

	public drawnObject getAttachedObject() {
		return attachedToObject;
	}

	public void setAttachedObject(drawnObject attachedObject) {
		this.attachedToObject = attachedObject;
	}

	public boolean isSmallObject() {
		return smallObject;
	}

	public void setSmallObject(boolean smallObject) {
		this.smallObject = smallObject;
	}

	public int getSpawnedAtX() {
		return spawnedAtX;
	}

	public void setSpawnedAtX(int spawnedAtX) {
		this.spawnedAtX = spawnedAtX;
	}

	public int getSpawnedAtY() {
		return spawnedAtY;
	}

	public void setSpawnedAtY(int spawnedAtY) {
		this.spawnedAtY = spawnedAtY;
	}
	
}