package UI;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import drawing.drawnObject;

public abstract class interfaceObject extends drawnObject  {
	
	/////////////////////////
	////// DEFAULTS /////////
	/////////////////////////
	
	public static ArrayList<interfaceObject> interfaceObjects;
	
	///////////////
	/// METHODS ///
	///////////////

	// Constructor
	public interfaceObject(BufferedImage newImage, int newX, int newY, int newWidth, int newHeight) {
		super(null, "Interface Object" , newX, newY, newWidth, newHeight);	
		setObjectImage(newImage);
		interfaceObjects.add(this);
	}
	
	// Respond to destroy
	@Override
	public void respondToDestroy() {
		if(interfaceObjects.contains(this)) interfaceObjects.remove(this);
	}
	
	// Update unit
	@Override
	public void update() {
	}
	
	// Initiate
	public static void initiate() {
		interfaceObjects = new ArrayList<interfaceObject>();
	}
	
}