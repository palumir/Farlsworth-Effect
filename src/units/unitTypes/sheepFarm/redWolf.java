package units.unitTypes.sheepFarm;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import doodads.sheepFarm.clawMarkRed;
import doodads.sheepFarm.clawMarkYellow;
import drawing.drawnObject;
import drawing.gameCanvas;
import drawing.spriteSheet;
import drawing.spriteSheet.spriteSheetInfo;
import items.item;
import drawing.animation.animation;
import units.player;
import units.unit;
import units.unitType;
import utilities.intTuple;
import utilities.mathUtils;
import utilities.time;
import utilities.utility;

public class redWolf extends wolf {
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default name.
	private static String DEFAULT_UNIT_NAME = "Red Wolf";
	
	// Health.
	private int DEFAULT_HP = 6;
	
	// Default jump speed
	private static int DEFAULT_UNIT_JUMPSPEED = 8;

	// Spawn claw stuff
	protected int DEFAULT_SLASH_DAMAGE = 1;
	
	// Beta stats
	private static float DEFAULT_MOVESPEED_BETA = 2f;
	private static float DEFAULT_CLAW_ATTACK_EVERY_BASE_BETA = 2.5f;
	private static float DEFAULT_SPAWN_CLAW_PHASE_TIME_BETA = 1f;
	private static int DEFAULT_FOLLOW_UNTIL_RANGE_BASE_BETA = 90;
	private static int DEFAULT_FOLLOW_UNTIL_RANGE_RANDOM_BETA = 15;
	
	////////////////
	/// FIELDS ///
	////////////////
	
	// Unit sprite stuff.
	private static spriteSheet DEFAULT_UPDOWN_SPRITESHEET = new spriteSheet(new spriteSheetInfo(
			"images/units/animals/redWolfUpDown.png", 
			32, 
			64,
			0,
			DEFAULT_TOPDOWN_ADJUSTMENT_Y
			));
	private static spriteSheet DEFAULT_LEFTRIGHT_SPRITESHEET = new spriteSheet(new spriteSheetInfo(
			"images/units/animals/redWolfLeftRight.png",
			64, 
			32,
			0,
			DEFAULT_TOPDOWN_ADJUSTMENT_Y
			));
	
	// The actual type.
	private static unitType unitTypeRef =
			new unitType(DEFAULT_UNIT_NAME,  // Name of unitType 
					     null,
					     null,
					     DEFAULT_TOPDOWN_WIDTH,
					     DEFAULT_TOPDOWN_HEIGHT,
					     DEFAULT_MOVESPEED_BETA, // Movespeed
					     DEFAULT_UNIT_JUMPSPEED // Jump speed
						);	
	
	// Alpha type
	private static unitType alphaUnitTypeRef =
			new unitType(DEFAULT_UNIT_NAME,  // Name of unitType 
					     null,
					     null,
					     DEFAULT_TOPDOWN_WIDTH,
					     DEFAULT_TOPDOWN_HEIGHT,
					     DEFAULT_MOVESPEED_BETA, // Movespeed
					     DEFAULT_UNIT_JUMPSPEED // Jump speed
						);	
	
	///////////////
	/// METHODS ///
	///////////////
	// Constructor
	public redWolf(int newX, int newY) {
		super(unitTypeRef, newX, newY);
		
		// Set wolf combat stuff.
		setCombatStuff();
		
		// Attacking left animation.
		animation trailLeft = new animation("trailLeft", leftRightSpriteSheet.getAnimation(8), 0, 0, 1);
		getAnimations().addAnimation(trailLeft);
		
		// Attacking right animation.
		animation trailRight = new animation("trailRight", leftRightSpriteSheet.getAnimation(9), 0, 0, 1);
		getAnimations().addAnimation(trailRight);
		
		// Attacking up animation.
		animation trailUp = new animation("trailUp", upDownSpriteSheet.getAnimation(6), 0, 0, 1);
		getAnimations().addAnimation(trailUp);
		
		// Attacking down animation.
		animation trailDown = new animation("trailDown", upDownSpriteSheet.getAnimation(5), 0, 0, 1);
		getAnimations().addAnimation(trailDown);
		
		changeCombat();
		
		setCanSlash(true);
		
	}
	
	// Combat defaults.
	@Override
	public void setCombatStuff() {
		
		// Set to be attackable.
		this.setKillable(true);
		
		// Wolf damage.
		setAttackFrameStart(2);
		setAttackFrameEnd(3);
		setAttackDamage(DEFAULT_SLASH_DAMAGE);
		setAttackTime(DEFAULT_ATTACK_TIME);
		setAttackWidth(DEFAULT_ATTACK_WIDTH);
		setAttackLength(DEFAULT_ATTACK_LENGTH);
		setCritDamage(DEFAULT_CRIT_DAMAGE);
		setCritChance(DEFAULT_CRIT_CHANCE);
		
		// HP
		setMaxHealthPoints(DEFAULT_HP);
		setHealthPoints(DEFAULT_HP);
	}
		
	// Remove claw.
	@Override
	public void reactToDeath() {
		if(currClaw != null) currClaw.destroy();
	}
	
	// Trail stuff.
	private float trailInterval = 0.0125f;
	private long lastTrail = 0;
	private int trailLength = 10;
	private ArrayList<intTuple> trail;
	private ArrayList<BufferedImage> trailImage;
	
	@Override
	public void drawUnitSpecialStuff(Graphics g) {
		// Of course only draw if the animation is not null.
		if(getCurrentAnimation() != null) {
			if(isJumping()) {
				
				// If it's empty, make it.
				if(trail == null) trail = new ArrayList<intTuple>();
				if(trailImage == null) trailImage = new ArrayList<BufferedImage>();
				// If we've passed trailInterval seconds. Add a new intTuple to trail and remove oldest one.
				if(time.getTime() - lastTrail > trailInterval*1000) {
					lastTrail = time.getTime();
					if(trail.size() >= trailLength) {
						trailImage.remove(0);
						trail.remove(0);
					}
					trailImage.add(getAnimations().getAnimation("trail" + getFacingDirection()).getSprites().get(0));
					trail.add(new intTuple(getIntX(),getIntY()));
				}
				
				g.drawImage(getCurrentAnimation().getCurrentFrame(), 
						getDrawX(), 
						getDrawY(), 
						(int)(gameCanvas.getScaleX()*getCurrentAnimation().getCurrentFrame().getWidth()), 
						(int)(gameCanvas.getScaleY()*getCurrentAnimation().getCurrentFrame().getHeight()), 
						null);
				
				// Draw trail.
				for(int i = 0; i < trail.size(); i++) {
					float alpha = ((float)i)/(float)trail.size();
					Graphics2D g2d = (Graphics2D) g.create();
					g2d.setComposite(AlphaComposite.SrcOver.derive(alpha));
					g2d.drawImage(trailImage.get(i), 
							drawnObject.calculateDrawX(this, trail.get(i).x), 
							drawnObject.calculateDrawY(this, trail.get(i).y), 
							(int)(gameCanvas.getScaleX()*trailImage.get(i).getWidth()), 
							(int)(gameCanvas.getScaleY()*trailImage.get(i).getHeight()), 
							null);
				}
			}
			else {
				if(trail != null) {
					
					// If we've passed trailInterval seconds. Add a new intTuple to trail and remove oldest one.
					if(time.getTime() - lastTrail > trailInterval*1000 && trail.size() > 0) {
						lastTrail = time.getTime();
						trail.remove(0);
						trailImage.remove(0);
					}
				}
				g.drawImage(getCurrentAnimation().getCurrentFrame(), 
						getDrawX(), 
						getDrawY(), 
						(int)(gameCanvas.getScaleX()*getCurrentAnimation().getCurrentFrame().getWidth()), 
						(int)(gameCanvas.getScaleY()*getCurrentAnimation().getCurrentFrame().getHeight()), 
						null);
				
				if(trail != null) {
					// Draw trail.
					for(int i = 0; i < trail.size(); i++) {
						float alpha = ((float)i)/(float)trail.size();
						Graphics2D g2d = (Graphics2D) g.create();
						g2d.setComposite(AlphaComposite.SrcOver.derive(alpha));
						g2d.drawImage(trailImage.get(i), 
								drawnObject.calculateDrawX(this, trail.get(i).x), 
								drawnObject.calculateDrawY(this, trail.get(i).y), 
								(int)(gameCanvas.getScaleX()*trailImage.get(i).getWidth()), 
								(int)(gameCanvas.getScaleY()*trailImage.get(i).getHeight()), 
								null);
					}
				}
			}
			// Of course only draw if the animation is not null.
			g.drawImage(getCurrentAnimation().getCurrentFrame(), 
					getDrawX(), 
					getDrawY(), 
					(int)(gameCanvas.getScaleX()*getCurrentAnimation().getCurrentFrame().getWidth()), 
					(int)(gameCanvas.getScaleY()*getCurrentAnimation().getCurrentFrame().getHeight()), 
					null);
		}
	}

	@Override
	public void spawnTrail() {
	}
	
	@Override
	// Is on screen?
	public boolean isOnScreen() {
		// Get the correct sprite width and height.
		int spriteWidth = 0;
		int spriteHeight = 0;
		if(this instanceof unit && ((unit)this).getCurrentAnimation() != null) {
			spriteWidth = ((unit)this).getCurrentAnimation().getCurrentFrame().getWidth();
			spriteHeight = ((unit)this).getCurrentAnimation().getCurrentFrame().getHeight();
		}
		if(trail!=null) {
			for(int i = 0; i < trail.size(); i++) {
				int trailDrawX = drawnObject.calculateDrawX(this, trail.get(i).x);
				int trailDrawY = drawnObject.calculateDrawY(this, trail.get(i).y);
				if(trailDrawX + gameCanvas.getScaleX()*spriteWidth > 0 && 
						trailDrawY + gameCanvas.getScaleY()*spriteHeight > 0 && 
						   trailDrawX < gameCanvas.getActualWidth() && 
						   trailDrawY < gameCanvas.getActualHeight()) return true;
			}
		}
		return (this.getDrawX() + gameCanvas.getScaleX()*spriteWidth > 0 && 
				   this.getDrawY() + gameCanvas.getScaleY()*spriteHeight > 0 && 
				   this.getDrawX() < gameCanvas.getActualWidth() && 
				   this.getDrawY() < gameCanvas.getActualHeight());
	}

	@Override
	public void jumpingFinished() {
	}
	
	@Override
	public void respondToDestroy() {
	}

	@Override
	public void spawnClaw(int x, int y) {	
		int spawnX = x;
		int spawnY = y;
		currClaw = new clawMarkRed(spawnX,spawnY,0);
		faceTowardThing(currClaw);
	}
	
	@Override
	public void changeCombat() {
		
		// Beta wolf
		if(!isAlpha()) {
			clawAttackEveryBase = DEFAULT_CLAW_ATTACK_EVERY_BASE_BETA;
			setSpawnClawPhaseTime(DEFAULT_SPAWN_CLAW_PHASE_TIME_BETA);
			followUntilRange = DEFAULT_FOLLOW_UNTIL_RANGE_BASE_BETA + utility.RNG.nextInt(DEFAULT_FOLLOW_UNTIL_RANGE_RANDOM_BETA);
			setMoveSpeed(DEFAULT_MOVESPEED_BETA);
		}

		// Alpha
		else {
		}
	}

	// Get updown spritesheet
	@Override
	public spriteSheet getUpDownSpriteSheet() {
		return DEFAULT_UPDOWN_SPRITESHEET;
	}
	
	// Get leftRight spritesheet
	@Override
	public spriteSheet getLeftRightSpriteSheet() {
		return DEFAULT_LEFTRIGHT_SPRITESHEET;
	}
	
	// Set animations for alpha
	@Override
	public void setAlphaAnimations() {
		upDownSpriteSheet = new spriteSheet(new spriteSheetInfo(
				"images/units/animals/redWolfUpDownAlpha.png", 
				32, 
				64,
				0,
				DEFAULT_TOPDOWN_ADJUSTMENT_Y
				));
		leftRightSpriteSheet = new spriteSheet(new spriteSheetInfo(
				"images/units/animals/redWolfLeftRightAlpha.png", 
				64, 
				32,
				0,
				DEFAULT_TOPDOWN_ADJUSTMENT_Y
				));
		addAnimations();
		
		// Attacking left animation.
		animation trailLeft = new animation("trailLeft", leftRightSpriteSheet.getAnimation(8), 0, 0, 1);
		getAnimations().addAnimation(trailLeft);
		
		// Attacking right animation.
		animation trailRight = new animation("trailRight", leftRightSpriteSheet.getAnimation(9), 0, 0, 1);
		getAnimations().addAnimation(trailRight);
	
		// Attacking up animation.
		animation trailUp = new animation("trailUp", upDownSpriteSheet.getAnimation(6), 0, 0, 1);
		getAnimations().addAnimation(trailUp);
		
		// Attacking down animation.
		animation trailDown = new animation("trailDown", upDownSpriteSheet.getAnimation(5), 0, 0, 1);
		getAnimations().addAnimation(trailDown);
	}

	@Override
	public void chargeUnits() {
		// TODO Auto-generated method stub
		
	}
}
