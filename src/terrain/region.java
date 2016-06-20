package terrain;

import java.awt.Color;
import java.awt.Graphics;

import drawing.drawnObject;
import units.player;
import units.unit;
import utilities.intTuple;

public class region extends drawnObject {
	
	// Statics
	public static region playerTrappedWithin = null;
	
	// Fields
	private int x;
	private int y;
	private int radius;
	
	// Circular
	public region(int newX, int newY, int newRadius) {
		super(null,newX,newY,newRadius, newRadius);
		setX(newX);
		setY(newY);
		setRadius(newRadius);
	}
	
	// Did something moving to newX and newY leave region?
	public static intTuple leftRegion(unit u, int newX, int newY) {
		if(playerTrappedWithin != null && u instanceof player) {
			intTuple i = new intTuple(0,0);
			float howCloseX = (float) Math.sqrt((newX + u.getWidth()/2 - playerTrappedWithin.getX())*(newX + u.getWidth()/2 - playerTrappedWithin.getX()) + (u.getY() + u.getHeight()/2 - playerTrappedWithin.getY())*(u.getY() + u.getHeight()/2 - playerTrappedWithin.getY()));
			float howCloseY = (float) Math.sqrt((u.getX() + u.getWidth()/2 - playerTrappedWithin.getX())*(u.getX() + u.getWidth()/2 - playerTrappedWithin.getX()) + (newY + u.getHeight()/2 - playerTrappedWithin.getY())*(newY + u.getHeight()/2 - playerTrappedWithin.getY()));
			if(howCloseX > playerTrappedWithin.getRadius()) i.x = 1;
			if(howCloseY > playerTrappedWithin.getRadius()) i.y = 1;
			return i;
		}
		return intTuple.emptyTuple;
	}
	
	// Is within
	public boolean contains(unit u) {
		float howClose = (float) Math.sqrt((u.getX() + u.getWidth()/2 - getX())*(u.getX() + u.getWidth()/2 - getX()) + (u.getY() + u.getHeight()/2 - getY())*(u.getY() + u.getHeight()/2 - getY()));
		return howClose < getRadius();
	}
	
	// Trap player within region.
	public void trapPlayerWithin() {
		playerTrappedWithin = this;
	}
	
	// Trap player within region.
	public void untrapPlayer() {
		if(this == playerTrappedWithin) playerTrappedWithin = null;
	}
	
	// Initiate
	public static void initiate() {
		playerTrappedWithin = null;
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

	@Override
	public void drawObject(Graphics g) {
		// Draw region
		//g.setColor(Color.red);
		//g.drawOval(drawX-getRadius()-getWidth()/2,drawY-getRadius()-getWidth()/2,getRadius()*2,getRadius()*2);
	}
}