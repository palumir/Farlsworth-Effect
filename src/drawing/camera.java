package drawing;

import units.unit;

public class camera {
	
	// Global camera
	private static camera current = null;
	
	// Fields
	private int x;
	private int y;
	private unit attachedUnit = null;
	private int zoom; // TODO does nothing.
	
	// Constructor
	public camera(int newX, int newY, int newZoom) {
		setX(newX);
		setY(newY);
		zoom = newZoom;
	}
	
	// Constructor for attaching to player.
	public camera(unit u, int newZoom) {
		attachedUnit = u;
		setX(u.x);
		setY(u.y);
		zoom = newZoom;
	}

	public static camera getCurrent() {
		return current;
	}

	public static void setCurrent(camera current) {
		camera.current = current;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
}