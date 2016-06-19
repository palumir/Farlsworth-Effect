package terrain;

import drawing.drawnObject;
import units.player;
import units.unit;
import utilities.intTuple;

public class region {
	
	// Statics
	public static region playerTrappedWithin = null;
	
	// Fields
	private int x;
	private int y;
	private int radius;
	
	// Circular
	public region(int newX, int newY, int newRadius) {
		setX(newX);
		setY(newY);
		setRadius(newRadius);
	}
	
	// Did something moving to newX and newY leave region?
	public static intTuple leftRegion(unit u, int newX, int newY) {
		if(playerTrappedWithin != null && u instanceof player) {
			intTuple i = new intTuple(0,0);
			float howCloseX = (float) Math.sqrt((newX - playerTrappedWithin.getX())*(newX - playerTrappedWithin.getX()) + (u.getY() - playerTrappedWithin.getY())*(u.getY() - playerTrappedWithin.getY()));
			float howCloseY = (float) Math.sqrt((u.getX() - playerTrappedWithin.getX())*(u.getX() - playerTrappedWithin.getX()) + (newY - playerTrappedWithin.getY())*(newY - playerTrappedWithin.getY()));
			if(howCloseX > playerTrappedWithin.getRadius()) i.x = 1;
			if(howCloseY > playerTrappedWithin.getRadius()) i.y = 1;
			return i;
		}
		return intTuple.emptyTuple;
	}
	
	// Is within
	public boolean contains(unit u) {
		float howClose = (float) Math.sqrt((u.getX() - getX())*(u.getX() - getX()) + (u.getY() - getY())*(u.getY() - getY()));
		return howClose < getRadius();
	}
	
	// Trap player within region.
	public void trapPlayerWithin() {
		playerTrappedWithin = this;
	}
	
	// Destroy region.
	public void destroy() {
		if(this == playerTrappedWithin) playerTrappedWithin = null;
	}

	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
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