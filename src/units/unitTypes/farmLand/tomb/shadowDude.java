package units.unitTypes.farmLand.tomb;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

import drawing.gameCanvas;
import drawing.animation.animation;
import effects.buffs.darkSlow;
import modes.mode;
import units.humanType;
import units.player;
import units.unit;
import units.unitType;
import utilities.intTuple;
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
	private static int DEFAULT_UNIT_MOVESPEED = 3;
	
	// Default jump speed
	private static int DEFAULT_UNIT_JUMPSPEED = 10;
	
	// Entered light time
	private long lastInLightTime= 0;
	private long lastInShadowsTime = 0;
	private float fadeTime = .25f;
	
	// farmer sprite stuff.
	private static String DEFAULT_UNIT_SPRITESHEET = "images/units/player/female/shadow.png";
	private static String DEFAULT_FADED_SPRITESHEET = "images/units/player/female/shadowEyes.png";
	
	// The actual type.
	private static unitType shadowType =
			new humanType( "shadowDude",  // Name of unitType 
						 DEFAULT_UNIT_SPRITESHEET,
					     DEFAULT_UNIT_MOVESPEED, // Movespeed
					     DEFAULT_UNIT_JUMPSPEED // Jump speed
						);	   
	
	private static unitType shadowFadedType =
			new humanType( "shadowFaded",  // Name of unitType 
						 DEFAULT_FADED_SPRITESHEET,
					     DEFAULT_UNIT_MOVESPEED, // Movespeed
					     DEFAULT_UNIT_JUMPSPEED // Jump speed
						);	   
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
	int damage = 6;
	float slowTo = 0.1f;
	float hurtEvery = 0.05f;
	
	public void hurtPeople() {
		// If someone is in the explosion radius, hurt.
		ArrayList<unit> hurtUnits = unit.getUnitsInBox(getIntX(), getIntY(), getIntX() + getWidth(), getIntY()+getHeight());
		if(hurtUnits != null && time.getTime() - lastHurt > hurtEvery*1000) {
			lastHurt = time.getTime();
			for(int i = 0; i < hurtUnits.size(); i++) {
				if(hurtUnits.get(i) instanceof player && !hurtUnits.get(i).isIlluminated() && !illuminated) {
					hurtUnits.get(i).hurt(damage, 1f);
					darkSlow d = new darkSlow(hurtUnits.get(i), hurtEvery, slowTo);
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
	
	// Trail stuff.
	private float trailInterval = 0.0125f/2f;
	private long lastTrail = 0;
	private int trailLength = 40;
	private ArrayList<intTuple> trail;
	private ArrayList<BufferedImage> trailImage;
	
	// Draw the unit. 
	@Override
	public void drawObject(Graphics g) {
		// Of course only draw if the animation is not null.
		if(getCurrentAnimation() != null) {
			
			// Draw the unit.
			float alpha = 0;
			float minFade = 0.2f;
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
			alpha = 1;
			g2d.setComposite(AlphaComposite.SrcOver.derive(alpha));
			// 9 or 11
			animation faded = shadowFadedType.getAnimations().getAnimation("running" + getFacingDirection());
			g2d.drawImage(faded.getCurrentFrame(), 
					getDrawX(), 
					getDrawY(), 
					(int)(gameCanvas.getScaleX()*faded.getCurrentFrame().getWidth()), 
					(int)(gameCanvas.getScaleY()*faded.getCurrentFrame().getHeight()), 
					null);
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
