package units;

import java.awt.Graphics;

import drawing.camera;
import drawing.drawnObject;
import drawing.shape;

public class unit extends shape  { // shape for now sprite later
	
	// Fields
	private unitType type;
	protected camera attachedCamera = null;
	
	// Movement
	private int moveSpeed = 1;
	private boolean movingLeft = false;
	private boolean movingRight = false;
	private boolean movingDown = false;
	private boolean movingUp = false;

	// Constructor
	public unit(unitType u, int newX, int newY) {
		super(u.getShape(), newX, newY, u.getHeight(), u.getWidth());
		moveSpeed = u.getMoveSpeed();
		type = u;
	}
	
	// Update unit
	@Override
	public void update() {
		moveUnit();
	}
	
	// Move unit
	public void moveUnit() {
		int moveX = 0;
		int moveY = 0;
		if(movingLeft) moveX -= moveSpeed; // TODO: include movespeed.
		if(movingRight) moveX += moveSpeed;
		if(movingUp) moveY -= moveSpeed;
		if(movingDown) moveY += moveSpeed;
		move(moveX, moveY);
	}
	
	// Move function
	public void move(int moveX, int moveY) {
		
		// Collision stuff. TODO: Does nothing.
		int actualMoveX = moveX;
		int actualMoveY = moveY;
		
		// Move the camera if it's there.
		if(attachedCamera != null) {
			attachedCamera.setX(attachedCamera.getX() + actualMoveX);
			attachedCamera.setY(attachedCamera.getY() + actualMoveY);
		}
		
		// Move the unit.
		x = x + actualMoveX;
		y = y + actualMoveY;
	}
	
	// Start moving
	public void startMove(String direction) {
		if(direction=="right") movingRight=true;
		if(direction=="left") movingLeft=true;
		if(direction=="up") movingUp=true;
		if(direction=="down") movingDown=true;
	}
	
	// Stop moving
	public void stopMove(String direction) {
		if(direction=="right") movingRight=false;
		if(direction=="left") movingLeft=false;
		if(direction=="up") movingUp=false;
		if(direction=="down") movingDown=false;
	}
}