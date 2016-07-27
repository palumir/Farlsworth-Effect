package units.unitTypes.farmLand.sheepFarm;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

import doodads.sheepFarm.clawMarkRed;
import doodads.sheepFarm.clawMarkYellow;
import drawing.camera;
import drawing.drawnObject;
import drawing.gameCanvas;
import drawing.spriteSheet;
import drawing.animation.animation;
import drawing.spriteSheet.spriteSheetInfo;
import effects.effect;
import effects.effectTypes.bloodSquirt;
import modes.mode;
import sounds.sound;
import units.humanType;
import units.player;
import units.unit;
import units.unitType;
import utilities.intTuple;
import utilities.mathUtils;
import utilities.time;
import utilities.utility;
import zones.zone;

public class redWolf extends wolf {
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default name.
	private static String DEFAULT_UNIT_NAME = "redWolf";
	
	// Health.
	private int DEFAULT_HP = 6;
	
	// Default jump speed
	private static int DEFAULT_UNIT_JUMPSPEED = 14;

	// Spawn claw stuff
	protected int DEFAULT_SLASH_DAMAGE = 1;
	
	// Beta stats
	private static float DEFAULT_MOVESPEED_BETA = 3.5f;
	private static float DEFAULT_CLAW_ATTACK_EVERY_BASE_BETA = 2.5f;
	private static float DEFAULT_SPAWN_CLAW_PHASE_TIME_BETA = 2.2f;
	private static int DEFAULT_HOW_FAR_BACK_BASE_BETA = 60;
	private static int DEFAULT_HOW_FAR_BACK_RANDOM_BETA = 20;
	private static int DEFAULT_RANDOM_DEGREE_BETA = 10;
	private static int DEFAULT_FOLLOW_UNTIL_RANGE_BASE_BETA = 90;
	private static int DEFAULT_FOLLOW_UNTIL_RANGE_RANDOM_BETA = 15;

	// Alpha stats
	private static float DEFAULT_MOVESPEED_ALPHA = 3.5f;
	private static float DEFAULT_CLAW_ATTACK_EVERY_BASE_ALPHA = 2f;
	private static float DEFAULT_SPAWN_CLAW_PHASE_TIME_ALPHA = 1.5f;
	private static int DEFAULT_HOW_FAR_BACK_BASE_ALPHA = 30;
	private static int DEFAULT_HOW_FAR_BACK_RANDOM_ALPHA = 5;
	private static int DEFAULT_RANDOM_DEGREE_ALPHA = 1; 
	private static int DEFAULT_FOLLOW_UNTIL_RANGE_BASE_ALPHA = 20;
	private static int DEFAULT_FOLLOW_UNTIL_RANGE_RANDOM_ALPHA = 0;
	
	////////////////
	/// FIELDS ///
	////////////////
	// How far back do we put the claw?
	private int howFarBackBase = 80;
	private int howFarBackRandom = 20;
	private int randomDegree = 0;
	
	// Charged units
	private ArrayList<unit> chargeUnits;
	
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
	
	// Already hurt
	private boolean alreadyHurt = false;
	
	// Trail stuff.
	private float trailInterval = 0.0125f/2f;
	private long lastTrail = 0;
	private int trailLength = 40;
	private ArrayList<intTuple> trail;
	private ArrayList<BufferedImage> trailImage;
	
	@Override
	public void drawUnitSpecialStuff(Graphics g) {
		// Of course only draw if the animation is not null.
		if(getCurrentAnimation() != null) {
			if(jumping) {
				
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
					if(getCurrentAnimation().getName().contains("Left")) trailImage.add(getAnimations().getAnimation("trailLeft").getSprites().get(0));
					else trailImage.add(getAnimations().getAnimation("trailRight").getSprites().get(0));
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
	public void chargeUnits() {	
		int chargeStartX = this.getIntX() + this.getWidth()/2;
		int chargeStartY = this.getIntY() + this.getHeight()/2;
		int radius = 30;
		if(chargeUnits!=null) {
			for(int i = 0; i < chargeUnits.size(); i++) {
				if(chargeUnits.get(i) instanceof player) {
					chargeUnits.get(i).setUnitLocked(false);
				}
			}
		}
		chargeUnits = unit.getUnitsInRadius(chargeStartX, chargeStartY, radius);
		if(chargeUnits != null) {
			for(int i = 0; i < chargeUnits.size(); i++) {
				if(chargeUnits.get(i) instanceof player) {
					chargeUnits.get(i).stopMove("all");
					chargeUnits.get(i).move(run,rise);
					chargeUnits.get(i).setUnitLocked(true);
				}
			}
		}
	}

	@Override
	public void spawnTrail() {
	}

	@Override
	public void jumpingFinished() {
		if(chargeUnits != null && chargeUnits.size() >= 1) {
			for(int i = 0; i < chargeUnits.size(); i++) {
				if(chargeUnits.get(i) instanceof player) {
					chargeUnits.get(i).stopMove("all");
					if(!alreadyHurt) {
						alreadyHurt = true;
						chargeUnits.get(i).hurt(DEFAULT_SLASH_DAMAGE, 1f);
					}
					chargeUnits.get(i).move(run,rise);
					chargeUnits.get(i).setUnitLocked(false);
				}
			}
			chargeUnits = new ArrayList<unit>();
		}
		alreadyHurt = false;
	}

	@Override
	public void spawnClaw() {	
		int howFarBack = howFarBackBase + utility.RNG.nextInt(howFarBackRandom);
		int spawnX = player.getPlayer().getIntX()+player.getPlayer().getWidth()/2;
		int spawnY = player.getPlayer().getIntY()+player.getPlayer().getHeight()/2;
		int degree = (int) mathUtils.angleBetweenTwoPointsWithFixedPoint(
				spawnX, spawnY,
				this.getIntX()+this.getWidth()/2, this.getIntY()+this.getHeight()/2, 
				this.getIntX()+this.getWidth()/2, this.getIntY()+this.getHeight()/2) -
				randomDegree + 2*utility.RNG.nextInt(randomDegree + 1);
		int distance = (int) Math.sqrt(Math.pow(spawnX - (this.getIntX()+this.getWidth()/2),2) + Math.pow(spawnY - (this.getIntY()+this.getHeight()/2),2));
		int newX = (int) (getIntX() + (distance+howFarBack)*Math.cos(Math.toRadians(degree))); 
		int newY = (int) (getIntY() + (distance+howFarBack)*Math.sin(Math.toRadians(degree)));
		currClaw = new clawMarkRed(newX - clawMarkYellow.DEFAULT_CHUNK_WIDTH/2, 
									 newY - clawMarkYellow.DEFAULT_CHUNK_HEIGHT/2,
			
									 0);
	}
	
	@Override
	public void changeCombat() {
		
		// Beta wolf
		if(!alpha) {
			clawAttackEveryBase = DEFAULT_CLAW_ATTACK_EVERY_BASE_BETA;
			spawnClawPhaseTime = DEFAULT_SPAWN_CLAW_PHASE_TIME_BETA;
			howFarBackBase = DEFAULT_HOW_FAR_BACK_BASE_BETA;
			howFarBackRandom = DEFAULT_HOW_FAR_BACK_RANDOM_BETA;
			randomDegree = DEFAULT_RANDOM_DEGREE_BETA;
			followUntilRange = DEFAULT_FOLLOW_UNTIL_RANGE_BASE_BETA + utility.RNG.nextInt(DEFAULT_FOLLOW_UNTIL_RANGE_RANDOM_BETA);
			setMoveSpeed(DEFAULT_MOVESPEED_BETA);
		}

		// Alpha
		else {
			
			// Claw attack stuff.
			clawAttackEveryBase = DEFAULT_CLAW_ATTACK_EVERY_BASE_ALPHA;
			clawAttackEvery = clawAttackEveryBase;
			spawnClawPhaseTime = DEFAULT_SPAWN_CLAW_PHASE_TIME_ALPHA;
			howFarBackBase = DEFAULT_HOW_FAR_BACK_BASE_ALPHA;
			howFarBackRandom = DEFAULT_HOW_FAR_BACK_RANDOM_ALPHA;
			randomDegree = DEFAULT_RANDOM_DEGREE_ALPHA;
			setMoveSpeed(DEFAULT_MOVESPEED_ALPHA);
			followUntilRange = DEFAULT_FOLLOW_UNTIL_RANGE_BASE_ALPHA + DEFAULT_FOLLOW_UNTIL_RANGE_RANDOM_ALPHA;
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
				"images/units/animals/" + DEFAULT_UNIT_NAME + "UpDownAlpha.png", 
				32, 
				64,
				0,
				DEFAULT_TOPDOWN_ADJUSTMENT_Y
				));
		
		leftRightSpriteSheet = new spriteSheet(new spriteSheetInfo(
				"images/units/animals/" + DEFAULT_UNIT_NAME + "LeftRightAlpha.png", 
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
	}
}
