package userInterface;

import java.awt.image.BufferedImage;

import drawing.drawnObject;

public abstract class interfaceObject extends drawnObject  {
	
	/////////////////////////
	////// DEFAULTS /////////
	/////////////////////////
	
	///////////////
	/// METHODS ///
	///////////////

	// Constructor
	public interfaceObject(BufferedImage newImage, int newX, int newY, int newWidth, int newHeight) {
		super(null, newX, newY, newWidth, newHeight);	
		setObjectImage(newImage);
	}
	
	// Update unit
	@Override
	public void update() {
	}
	
}