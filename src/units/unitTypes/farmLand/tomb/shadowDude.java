package units.unitTypes.farmLand.tomb;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Random;

import drawing.camera;
import drawing.drawnObject;
import drawing.gameCanvas;
import drawing.animation.animation;
import drawing.userInterface.playerHealthBar;
import drawing.userInterface.tooltipString;
import effects.effect;
import effects.buffs.darkSlow;
import effects.effectTypes.bloodSquirt;
import interactions.interactBox;
import interactions.quest;
import interactions.textSeries;
import items.keys.farmKey;
import modes.mode;
import units.humanType;
import units.player;
import units.unit;
import units.unitType;
import utilities.stringUtils;
import utilities.time;
import utilities.utility;
import zones.zone;

public class shadowDude extends unit {
	
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
	private static String unitName = "shadowDude";
	
	// Default movespeed.
	private static int DEFAULT_UNIT_MOVESPEED = 1;
	
	// Default jump speed
	private static int DEFAULT_UNIT_JUMPSPEED = 10;
	
	// Entered light time
	private long lastInLightTime= 0;
	private long lastInShadowsTime = 0;
	private float fadeTime = .3f;
	
	// farmer sprite stuff.
	private static String DEFAULT_UNIT_SPRITESHEET = "images/units/player/female/shadow.png";
	//private static String DEFAULT_FADED_SPRITESHEET = "images/units/player/female/shadowFaded.png";
	
	// The actual type.
	private static unitType shadowType =
			new humanType( "shadow",  // Name of unitType 
						 DEFAULT_UNIT_SPRITESHEET,
					     DEFAULT_UNIT_MOVESPEED, // Movespeed
					     DEFAULT_UNIT_JUMPSPEED // Jump speed
						);	   
	
	/*private static unitType shadowFadedType =
			new humanType( "shadow",  // Name of unitType 
						 DEFAULT_FADED_SPRITESHEET,
					     DEFAULT_UNIT_MOVESPEED, // Movespeed
					     DEFAULT_UNIT_JUMPSPEED // Jump speed
						);	   */
	///////////////
	/// METHODS ///
	///////////////
	// Constructor
	public shadowDude(int newX, int newY) {
		super(shadowType, newX, newY);
		
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
		
	}
	
	// React to pain.
	public void reactToPain() {
	}
	
	boolean illuminated = false;
	long lastHurt = 0;
	int damage = 10;
	float hurtEvery = 0.10f;
	
	public void hurtPeople() {
		// If someone is in the explosion radius, hurt.
		ArrayList<unit> hurtUnits = unit.getUnitsInRadius(getIntX() + getWidth()/2, getIntY() + getHeight()/2, getWidth()/2);
		if(hurtUnits != null && time.getTime() - lastHurt > hurtEvery*1000) {
			lastHurt = time.getTime();
			for(int i = 0; i < hurtUnits.size(); i++) {
				if(hurtUnits.get(i) instanceof player && !hurtUnits.get(i).isIlluminated() && !illuminated) {
					hurtUnits.get(i).hurt(damage, 1f);
					darkSlow d = new darkSlow(hurtUnits.get(i), hurtEvery);
				}
			}
		}
	}

	// Does nothing yet.
	public void updateUnit() {
		
		hurtPeople();
		
		// Illumination stuff
		illuminated = isIlluminated();
		if(illuminated) {
			lastInLightTime = time.getTime();
		}
		if(!illuminated) {
			lastInShadowsTime = time.getTime();
		}
		
	}
	
	// Draw the unit. 
	@Override
	public void drawObject(Graphics g) {
		// Of course only draw if the animation is not null.
		if(getCurrentAnimation() != null) {
			float alpha = 0;
			float minFade = 0.1f;
			if(illuminated) {
				alpha = (1 - (time.getTime() - lastInShadowsTime)/(fadeTime*1000))/(1f-minFade);
				if(alpha < minFade) alpha = minFade;
				if(alpha > 1) alpha = 1;
			}
			else {
				alpha = ((time.getTime() - lastInLightTime)/(fadeTime*1000))/(1f-minFade);
				if(alpha < minFade) alpha = minFade;
				if(alpha > 1) alpha = 1;
			}
			Graphics2D g2d = (Graphics2D) g.create();
			g2d.setComposite(AlphaComposite.SrcOver.derive(alpha));
			g2d.drawImage(getCurrentAnimation().getCurrentFrame(), 
					getDrawX(), 
					getDrawY(), 
					(int)(gameCanvas.getScaleX()*getCurrentAnimation().getCurrentFrame().getWidth()), 
					(int)(gameCanvas.getScaleY()*getCurrentAnimation().getCurrentFrame().getHeight()), 
					null);
			
			// Draw eyes.
			//alpha = 1;
			//g2d.setComposite(AlphaComposite.SrcOver.derive(alpha));
			// 9 or 11
			//animation faded = shadowFadedType.getAnimations().getAnimation("running" + getFacingDirection());
			/*g2d.drawImage(faded.getCurrentFrame(), 
					getDrawX(), 
					getDrawY(), 
					(int)(gameCanvas.getScaleX()*faded.getCurrentFrame().getWidth()), 
					(int)(gameCanvas.getScaleY()*faded.getCurrentFrame().getHeight()), 
					null);*/
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
