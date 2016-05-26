package drawing;

import java.awt.Graphics;
import java.util.ArrayList;

// A class for any object that is drawn in the
// canvas.
public abstract class drawnObject {
	
	// Fields
	protected int x;
	protected int y;
	protected int drawX;
	protected int drawY;
	protected int width;
	protected int height;
	
	// A list of things that we need to draw in general.
	public static ArrayList<drawnObject> objects = new ArrayList<drawnObject>();
	
	// drawnObject constructor
	public drawnObject(int newX, int newY, int newHeight, int newWidth) {
		x = newX;
		y = newY;
		width = newWidth;
		height = newHeight;
		objects.add(this);
	}
	
	// Every thing needs to update itself in some way.
	public void update() {
		// Do nothing for basic objects.
	}
	
	// Draw all objects.
	public static void drawObjects(Graphics g) {	
		for(int i = 0; i < objects.size(); i++) {
			drawnObject d = objects.get(i);
			
			// If there's a camera, adjust units drawn to the camera pos.
			if(camera.getCurrent() != null) {
				// TODO: possible issues with the screen being resized.
				d.drawX = d.x - camera.getCurrent().getX() - d.width/2 + gameCanvas.getDefaultWidth()/2;
				d.drawY = d.y - camera.getCurrent().getY() - d.height/2 + gameCanvas.getDefaultHeight()/2;
			}
			else {
				d.drawX = d.x;
				d.drawY = d.y;
			}
			
			// Draw the object.
			d.drawObject(g);
		}
	}
	
	// Draw an object.
	public abstract void drawObject(Graphics g);
	
}