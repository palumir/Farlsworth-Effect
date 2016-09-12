package utilities;

import java.awt.Point;

import drawing.camera;
import drawing.gameCanvas;

public class userMouseTracker {
	
	// Last mouse position
	public static Point lastMousePos;
	
	// Is left click held?
	public static Point leftClickStartPoint;
	
	
	// Convert drawn point to in game position.
	public static Point toInGamePos(Point p) {
		if(camera.getCurrent() != null) {
			return new Point(p.x + camera.getCurrent().getX() + camera.getCurrent().getAttachedUnit().getWidth()/2 - gameCanvas.getDefaultWidth()/2, 
			      p.y + camera.getCurrent().getY() + camera.getCurrent().getAttachedUnit().getHeight()/2 - gameCanvas.getDefaultHeight()/2);
		}
		return null;
	}
	
	// Convert point to draw position based on camera position.
	public static Point toDrawPos(Point p) {
		Point inGamePointCurrent = new Point(p.x - (camera.getCurrent().getX() + camera.getCurrent().getAttachedUnit().getWidth()/2 - gameCanvas.getDefaultWidth()/2), 
			      p.y - (camera.getCurrent().getY() + camera.getCurrent().getAttachedUnit().getHeight()/2 - gameCanvas.getDefaultHeight()/2));
		return inGamePointCurrent;
	}
	
	// Update
	public static void update() {
		
		// Record mouse position.
		Point p = gameCanvas.getGameCanvas().getMousePosition();
		if(p!=null) lastMousePos = toInGamePos(p);
	}
	
}