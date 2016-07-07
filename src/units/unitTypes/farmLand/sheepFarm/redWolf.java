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
import drawing.animation.animation;
import effects.effect;
import effects.effectTypes.bloodSquirt;
import modes.mode;
import sounds.sound;
import units.animalType;
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
	private static String DEFAULT_WOLF_NAME = "redWolf";
	
	// Health.
	private int DEFAULT_HP = 11;
	
	// Default movespeed.
	private static int DEFAULT_WOLF_MOVESPEED = 2;
	
	// Default jump speed
	private static int DEFAULT_WOLF_JUMPSPEED = 13;
	
	// wolf sprite stuff.
	private static String DEFAULT_WOLF_SPRITESHEET = "images/units/animals/redWolf.png";
	
	// The actual type.
	private static unitType wolfType =
			new animalType( "redWolf",  // Name of unitType 
						 DEFAULT_WOLF_SPRITESHEET,
					     DEFAULT_WOLF_MOVESPEED, // Movespeed
					     DEFAULT_WOLF_JUMPSPEED // Jump speed
						);	

	// Spawn claw stuff
	protected float DEFAULT_CLAW_ATTACK_EVERY = 1f;
	protected float DEFAULT_SPAWN_CLAW_PHASE_TIME = 1f;
	protected int SLASH_DAMAGE = 1;
	
	///////////////
	/// METHODS ///
	///////////////
	// Constructor
	public redWolf(int newX, int newY) {
		super(wolfType, newX, newY);
		
		// Set wolf combat stuff.
		setCombatStuff();
		
		// Attacking left animation.
		animation trailLeft = new animation("trailLeft", wolfType.getUnitTypeSpriteSheet().getAnimation(8), 0, 0, 1);
		getAnimations().addAnimation(trailLeft);
		
		// Attacking right animation.
		animation trailRight = new animation("trailRight", wolfType.getUnitTypeSpriteSheet().getAnimation(8), 0, 0, 1);
		getAnimations().addAnimation(trailRight);
	}
	
	// Combat defaults.
	@Override
	public void setCombatStuff() {
		// Set to be attackable.
		this.setKillable(true);
		
		// Claw attack stuff.
		clawAttackEveryBase = DEFAULT_CLAW_ATTACK_EVERY;
		spawnClawPhaseTime = DEFAULT_SPAWN_CLAW_PHASE_TIME;
		
		// Wolf damage.
		setAttackFrameStart(2);
		setAttackFrameEnd(3);
		setAttackDamage(SLASH_DAMAGE);
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
	private int trailLength = 30;
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
					trail.add(new intTuple(getX(),getY()));
				}
				
				g.drawImage(getCurrentAnimation().getCurrentFrame(), 
						drawX, 
						drawY, 
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
						drawX, 
						drawY, 
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
					drawX, 
					drawY, 
					(int)(gameCanvas.getScaleX()*getCurrentAnimation().getCurrentFrame().getWidth()), 
					(int)(gameCanvas.getScaleY()*getCurrentAnimation().getCurrentFrame().getHeight()), 
					null);
		}
	}

	@Override
	public void chargeUnits() {	
		if(!alreadyHurt) {
			ArrayList<unit> hurtUnits = unit.getUnitsInBox(getX(), getY(), getX() + getWidth(), getY() + getHeight());
			if(hurtUnits!=null)
			for(int i = 0; i < hurtUnits.size(); i++) {
				if(hurtUnits.get(i) instanceof player) {
					alreadyHurt = true;
					hurtUnits.get(i).hurt(SLASH_DAMAGE, 1f);
				}
			}
		}
	}

	@Override
	public void spawnTrail() {
	}

	@Override
	public void jumpingFinished() {
		alreadyHurt = false;
	}

	@Override
	public void spawnClaw() {	
		int spawnX = player.getCurrentPlayer().getX()+player.getCurrentPlayer().getWidth()/2;
		int spawnY = player.getCurrentPlayer().getY()+player.getCurrentPlayer().getHeight()/2;
		currClaw = new clawMarkRed(spawnX - clawMarkRed.DEFAULT_CHUNK_WIDTH/2, 
									 spawnY - clawMarkRed.DEFAULT_CHUNK_HEIGHT/2,
									 0);
	}
}
