package units.unitTypes.tomb;

import java.awt.Color;
import java.awt.Graphics;

import doodads.general.invisibleLightSource;
import doodads.general.lightSource;
import drawing.gameCanvas;
import modes.mode;
import units.humanType;
import units.unit;
import units.unitType;

public class lightDude extends unit {
	
	// Default dimensions.
	private static int DEFAULT_PLATFORMER_HEIGHT = 46;
	private static int DEFAULT_PLATFORMER_WIDTH = humanType.DEFAULT_UNIT_WIDTH;
	private static int DEFAULT_TOPDOWN_HEIGHT = 20;
	private static int DEFAULT_TOPDOWN_WIDTH = humanType.DEFAULT_UNIT_WIDTH;
	
	// Platformer and topdown default adjustment
	private static int DEFAULT_PLATFORMER_ADJUSTMENT_Y = 6;
	private static int DEFAULT_TOPDOWN_ADJUSTMENT_Y = 20;
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default name.
	private static String unitName = "lightDude";
	
	// Default movespeed.
	private static int DEFAULT_UNIT_MOVESPEED = 3;
	
	// Default jump speed
	private static int DEFAULT_UNIT_JUMPSPEED = 10;
	
	// farmer sprite stuff.
	private static String DEFAULT_UNIT_SPRITESHEET = "images/units/player/female/light.png";
	
	// The actual type.
	private static unitType lightType =
			new humanType( "light",  // Name of unitType 
						 DEFAULT_UNIT_SPRITESHEET,
					     DEFAULT_UNIT_MOVESPEED, // Movespeed
					     DEFAULT_UNIT_JUMPSPEED // Jump speed
						);	    
	///////////////
	/// FIELDS ///
	///////////////
	private lightSource light;
	
	///////////////
	/// METHODS ///
	///////////////
	// Constructor
	public lightDude(int newX, int newY) {
		super(lightType, newX, newY);
		
		// Make unkillable
		setKillable(false);
		setTargetable(false);
		setStuck(true);
		collisionOn = false;
		
		// Make adjustments on hitbox if we're in topDown.
		// Set dimensions
		setHeight(getDefaultHeight());
		setWidth(getDefaultWidth());
		platformerHeight = DEFAULT_PLATFORMER_HEIGHT;
		platformerWidth = DEFAULT_PLATFORMER_WIDTH;
		topDownHeight = DEFAULT_TOPDOWN_HEIGHT;
		topDownWidth = DEFAULT_TOPDOWN_WIDTH;
		setHitBoxAdjustmentY(getDefaultHitBoxAdjustmentY());
		
		// Make the light source.
		light = new invisibleLightSource(newX + getWidth()/2 - invisibleLightSource.DEFAULT_SPRITE_WIDTH/2, 
				newY + getHeight()/2 - invisibleLightSource.DEFAULT_SPRITE_HEIGHT/2 - getHitBoxAdjustmentY());
		light.setLightRadius(50);
		
	}
	
	// Do unit specific movement.
	@Override
	public void unitSpecificMovement(double moveX, double moveY) {
		if(light != null) {
			light.setDoubleX(getDoubleX() + moveX);
			light.setDoubleY(light.getDoubleY() + moveY);
		}
	}
	
	// React to pain.
	public void reactToPain() {
	}

	// Does nothing yet.
	public void updateUnit() {
	}
	
	// Draw the unit. 
	@Override
	public void drawObject(Graphics g) {
		// Of course only draw if the animation is not null.
		if(getCurrentAnimation() != null) {
			g.drawImage(getCurrentAnimation().getCurrentFrame(), 
					getDrawX(), 
					getDrawY(), 
					(int)(gameCanvas.getScaleX()*getCurrentAnimation().getCurrentFrame().getWidth()), 
					(int)(gameCanvas.getScaleY()*getCurrentAnimation().getCurrentFrame().getHeight()), 
					null);
		}
		
		// Draw the outskirts of the sprite.
		if(showSpriteBox && getCurrentAnimation() != null) {
			g.setColor(Color.red);
			g.drawRect(getDrawX(),
					   getDrawY(), 
					   (int)(gameCanvas.getScaleX()*getCurrentAnimation().getCurrentFrame().getWidth()), 
					   (int)(gameCanvas.getScaleY()*getCurrentAnimation().getCurrentFrame().getHeight()));
			
		}
		
		// Draw the x,y coordinates of the unit.
		if(showUnitPosition) {
			g.setColor(Color.white);
			g.drawString(getIntX() + "," + getIntY(),
					   getDrawX(),
					   getDrawY());
		}
		
		// Show attack range.
		if(showAttackRange && getCurrentAnimation() != null) {
			int x1 = 0;
			int x2 = 0;
			int y1 = 0;
			int y2 = 0;
			
			// Get the x and y of hitbox.
			int hitBoxX = getDrawX() - (- (getCurrentAnimation().getCurrentFrame().getWidth()/2 - getWidth()/2) - getHitBoxAdjustmentX());
			int hitBoxY = getDrawY() - (- (getCurrentAnimation().getCurrentFrame().getHeight()/2 - getHeight()/2) - getHitBoxAdjustmentY());
			
			// Get the box we will attack in if facing left.
			if(facingDirection.equals("Left")) {
				int heightMidPoint = hitBoxY + getHeight()/2;
				y1 = heightMidPoint - getAttackWidth()/2;
				y2 = heightMidPoint + getAttackWidth()/2;
				x1 = hitBoxX - getAttackLength();
				x2 = hitBoxX + getWidth() + 5;
			}
			
			// Get the box we will attack in if facing right.
			if(facingDirection.equals("Right")) {
				int heightMidPoint = hitBoxY + getHeight()/2;
				y1 = heightMidPoint - getAttackWidth()/2;
				y2 = heightMidPoint + getAttackWidth()/2;
				x1 = hitBoxX - 5;
				x2 = hitBoxX + getWidth() + getAttackLength();
			}
			
			// Get the box we will attack in facing up.
			if(facingDirection.equals("Up")) {
				int widthMidPoint = hitBoxX + getWidth()/2;
				x1 = widthMidPoint - getAttackWidth()/2;
				x2 = widthMidPoint + getAttackWidth()/2;
				y1 = hitBoxY - getAttackLength();
				y2 = hitBoxY + getHeight() + 5;
			}
			
			// Get the box we will attack in facing down.
			if(facingDirection.equals("Down")) {
				int widthMidPoint = hitBoxX + getWidth()/2;
				x1 = widthMidPoint - getAttackWidth()/2;
				x2 = widthMidPoint + getAttackWidth()/2;
				y1 = hitBoxY - 5;
				y2 = hitBoxY + getHeight() + getAttackLength();
			}
			g.setColor(Color.blue);
			g.drawRect((int)(gameCanvas.getScaleX()*x1),(int)(gameCanvas.getScaleY()*y1),(int)(gameCanvas.getScaleX()*x2-x1),(int)(gameCanvas.getScaleY()*y2-y1));
		}
		
		// Draw the hitbox of the image in green.
		if(showHitBox && getCurrentAnimation() != null) {
			g.setColor(Color.green);
			g.drawRect(getDrawX() - (int)(gameCanvas.getScaleX()*(- (getCurrentAnimation().getCurrentFrame().getWidth()/2 - getWidth()/2) - getHitBoxAdjustmentX())),
					   getDrawY() - (int)(gameCanvas.getScaleY()*(- (getCurrentAnimation().getCurrentFrame().getHeight()/2 - getHeight()/2) - getHitBoxAdjustmentY())), 
					   (int)(gameCanvas.getScaleX()*getWidth()), 
					   (int)(gameCanvas.getScaleY()*getHeight()));
		}
		
		// Draw special stuff
		drawUnitSpecialStuff(g);
	}
	
	///////////////////////////
	/// GETTERS AND SETTERS ///
	///////////////////////////
	
	// Get default width.
	public static int getDefaultWidth() {
		if(mode.getCurrentMode().equals("topDown")) {
			return DEFAULT_TOPDOWN_WIDTH;
		}
		else {
			return DEFAULT_PLATFORMER_WIDTH;
		}
	}
	
	// Get default height.
	public static int getDefaultHeight() {
		if(mode.getCurrentMode().equals("topDown")) {
			return DEFAULT_TOPDOWN_HEIGHT;
		}
		else {
			return DEFAULT_PLATFORMER_HEIGHT;
		}
	}
	
	// Get default hitbox adjustment Y.
	public static int getDefaultHitBoxAdjustmentY() {
		if(mode.getCurrentMode().equals("topDown")) {
			return DEFAULT_TOPDOWN_ADJUSTMENT_Y;
		}
		else {
			return DEFAULT_PLATFORMER_ADJUSTMENT_Y;
		}
	}
}
