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
			float howCloseX = (float) Math.sqrt((newX + u.getWidth()/2 - playerTrappedWithin.getIntX())*(newX + u.getWidth()/2 - playerTrappedWithin.getIntX()) + (u.getIntY() + u.getHeight()/2 - playerTrappedWithin.getIntY())*(u.getIntY() + u.getHeight()/2 - playerTrappedWithin.getIntY()));
			float howCloseY = (float) Math.sqrt((u.getIntX() + u.getWidth()/2 - playerTrappedWithin.getIntX())*(u.getIntX() + u.getWidth()/2 - playerTrappedWithin.getIntX()) + (newY + u.getHeight()/2 - playerTrappedWithin.getIntY())*(newY + u.getHeight()/2 - playerTrappedWithin.getIntY()));
			if(howCloseX > playerTrappedWithin.getRadius()) i.x = 1;
			if(howCloseY > playerTrappedWithin.getRadius()) i.y = 1;
			return i;
		}
		return intTuple.emptyTuple;
	}
	
	// Is within
	public boolean contains(unit u) {
		float howClose = (float) Math.sqrt((u.getIntX() + u.getWidth()/2 - getIntX())*(u.getIntX() + u.getWidth()/2 - getIntX()) + (u.getIntY() + u.getHeight()/2 - getIntY())*(u.getIntY() + u.getHeight()/2 - getIntY()));
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

	public int getIntX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getIntY() {
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