package units.unitTypes.farmLand.sheepFarm;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

import doodads.sheepFarm.clawMarkRed;
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
import utilities.time;
import utilities.utility;
import zones.zone;

public class redWolf extends unit {
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default name.
	private static String DEFAULT_WOLF_NAME = "redWolf";
	
	// Platformer real dimensions
	public static int DEFAULT_PLATFORMER_HEIGHT = 32;
	public static int DEFAULT_PLATFORMER_WIDTH = 32;
	public static int DEFAULT_PLATFORMER_ADJUSTMENT_Y = 0;
	
	// TopDown real dimensions
	public static int DEFAULT_TOPDOWN_HEIGHT = 18;
	public static int DEFAULT_TOPDOWN_WIDTH = 30;
	public static int DEFAULT_TOPDOWN_ADJUSTMENT_Y = 5;
	
	// How close to attack?
	static private int DEFAULT_ATTACK_RADIUS = 300;
	static private int DEFAULT_DEAGGRO_RADIUS = 300;
	
	// Damage stats
	static private int DEFAULT_ATTACK_DIFFERENTIAL = 6; // the range within the attackrange the unit will attack.
	static private int DEFAULT_ATTACK_DAMAGE = 2;
	static private float DEFAULT_BAT = 0.30f;
	static private float DEFAULT_ATTACK_TIME = 2f;
	static private int DEFAULT_ATTACK_WIDTH = 300;
	static private int DEFAULT_ATTACK_LENGTH = 300;
	static private float DEFAULT_CRIT_CHANCE = .15f;
	static private float DEFAULT_CRIT_DAMAGE = 1.6f;
	
	// Dosile?
	private boolean dosile = false;
	
	// Health.
	private int DEFAULT_HP = 10;
	
	// Default movespeed.
	private static int DEFAULT_WOLF_MOVESPEED = 2;
	
	// Default jump speed
	private static int DEFAULT_WOLF_JUMPSPEED = 13;
	
	// wolf sprite stuff.
	private static String DEFAULT_WOLF_SPRITESHEET = "images/units/animals/redWolf.png";
	
	// The actual type.
	private static unitType wolfType =
			new animalType( "jumpingWolf",  // Name of unitType 
						 DEFAULT_WOLF_SPRITESHEET,
					     DEFAULT_WOLF_MOVESPEED, // Movespeed
					     DEFAULT_WOLF_JUMPSPEED // Jump speed
						);	
	
	// Sounds
	private static String howl = "sounds/effects/animals/wolfHowl.wav";
	private static String growl = "sounds/effects/animals/wolfGrowl.wav";
	private static String bark1 = "sounds/effects/animals/wolfBark1.wav";
	private static String bark2 = "sounds/effects/animals/wolfBark2.wav";
	private static String wolfAttack = "sounds/effects/player/combat/swingWeapon.wav";
	private int lastBarkSound = 0;
	private long lastHowl = 0;
	private float randomHowl = 0;
	private int soundRadius = 1200;
	
	// Sound volumes.
	private static float DEFAULT_HOWL_VOLUME = 0.8f;
	private static float DEFAULT_GROWL_VOLUME = 0.8f;
	private static float DEFAULT_BARK_VOLUME = 0.8f;
	
	//////////////
	/// FIELDS ///
	//////////////
	private boolean aggrod = false;
	
	// Claw attacking?
	private boolean clawAttacking = false;
	private boolean hasClawSpawned = false;
	private boolean slashing = false;
	private long startOfClawAttack = 0;
	private float spawnClawPhaseTime = 1f;
	private float clawAttackEvery = 0.3f;
	private long lastClawAttack = 0;
	
	// Claw
	private clawMarkRed currClaw = null;
	private boolean hasStartedJumping = false;
	
	// Jumping stuff
	private int jumpingToX = 0;
	private int jumpingToY = 0;
	private boolean hasSlashed = false;
	private boolean riseRunSet = false;
	private int rise = 0;
	private int run = 0;
	
	///////////////
	/// METHODS ///
	///////////////
	// Constructor
	public redWolf(int newX, int newY) {
		super(wolfType, newX, newY);
		//showAttackRange();
		// Set wolf combat stuff.
		setCombatStuff();
		attackSound = wolfAttack;
		
		// Add attack animations.
		// Attacking left animation.
		animation slashingLeft = new animation("slashingLeft", wolfType.getUnitTypeSpriteSheet().getAnimation(5), 2, 2, DEFAULT_BAT);
		getAnimations().addAnimation(slashingLeft);
		
		// Attacking left animation.
		animation slashingRight = new animation("slashingRight", wolfType.getUnitTypeSpriteSheet().getAnimation(6), 2, 2, DEFAULT_BAT);
		getAnimations().addAnimation(slashingRight);
		
		// Attacking left animation.
		animation attackingLeft = new animation("attackingLeft", wolfType.getUnitTypeSpriteSheet().getAnimation(5), 0, 5, DEFAULT_BAT);
		getAnimations().addAnimation(attackingLeft);
		
		// Attacking left animation.
		animation attackingRight = new animation("attackingRight", wolfType.getUnitTypeSpriteSheet().getAnimation(6), 0, 5, DEFAULT_BAT);
		getAnimations().addAnimation(attackingRight);
		
		// Attacking left animation.
		animation trailLeft = new animation("trailLeft", wolfType.getUnitTypeSpriteSheet().getAnimation(8), 0, 0, DEFAULT_BAT);
		getAnimations().addAnimation(trailLeft);
		
		// Attacking right animation.
		animation trailRight = new animation("trailRight", wolfType.getUnitTypeSpriteSheet().getAnimation(8), 0, 0, DEFAULT_BAT);
		getAnimations().addAnimation(trailRight);
		
		// Set dimensions
		setHeight(getDefaultHeight());
		setWidth(getDefaultWidth());
		platformerHeight = DEFAULT_PLATFORMER_HEIGHT;
		platformerWidth = DEFAULT_PLATFORMER_WIDTH;
		topDownHeight = DEFAULT_TOPDOWN_HEIGHT;
		topDownWidth = DEFAULT_TOPDOWN_WIDTH;
		setHitBoxAdjustmentY(getDefaultHitBoxAdjustmentY());
	}
	
	// Combat defaults.
	public void setCombatStuff() {
		// Set to be attackable.
		this.setKillable(true);
		
		// Wolf damage.
		setAttackFrameStart(2);
		setAttackFrameEnd(3);
		setAttackDamage(DEFAULT_ATTACK_DAMAGE);
		setAttackTime(DEFAULT_ATTACK_TIME);
		setAttackWidth(DEFAULT_ATTACK_WIDTH);
		setAttackLength(DEFAULT_ATTACK_LENGTH);
		setCritDamage(DEFAULT_CRIT_DAMAGE);
		setCritChance(DEFAULT_CRIT_CHANCE);
		
		// HP
		setMaxHealthPoints(DEFAULT_HP);
		setHealthPoints(DEFAULT_HP);
		
	}
	
	// React to pain.
	public void reactToPain() {
		// Play a bark on pain.
		sound s = new sound(bark1);
		s.setPosition(getX(), getY(), soundRadius);
		s.start();
	}
	
	// Wolf random noises
	public void makeSounds() {
		
			// Create a new random growl interval
			float newRandomHowlInterval = 10f + utility.RNG.nextInt(10);
			
			// Make the wolf howl
			if(randomHowl == 0f) {
				randomHowl = newRandomHowlInterval;
			}
			if(!dosile && !aggrod && time.getTime() - lastHowl > randomHowl*1000) {
				
				// Set the last time they howled
				lastHowl = time.getTime();
				randomHowl = newRandomHowlInterval;
				sound s = new sound(howl);
				s.setPosition(getX(), getY(), soundRadius);
				s.start();
			}
	}

	// Claw attack.
	public void clawAttack(unit u) {
		clawAttacking = true;
		hasClawSpawned = false;
		slashing = false;
		player currPlayer = player.getCurrentPlayer();
		
		// Start of claw attack.
		startOfClawAttack = time.getTime();
	}
	
	// Deal with jumping.
		public void dealWithJumping() {
			
			if(clawAttacking) {
				// Get current player.
				player currPlayer = player.getCurrentPlayer();
				
				// Reset rise and run if we're close.
				if(Math.abs(jumpingToX - getX()) < jumpSpeed) {
					run = 0;
				}
				if(Math.abs(jumpingToY - getY()) < jumpSpeed) {
					rise = 0;
				}
				
				// Jump to the location.
				if(jumping) {
					
					// Set rise/run
					if(!riseRunSet) {
						riseRunSet = true;
						float yDistance = (jumpingToY - getY());
						float xDistance = (jumpingToX - getX());
						float distanceXY = (float) Math.sqrt(yDistance * yDistance
								+ xDistance * xDistance);
						rise = (int) ((yDistance/distanceXY)*jumpSpeed);
						run = (int) ((xDistance/distanceXY)*jumpSpeed);
					}
					
					setX(getX() + run);
					setY(getY() + rise);
					
					// Don't let him not move at all or leave region.
					if((run == 0 && rise == 0) || ((Math.abs(jumpingToX - getX()) < 1 && Math.abs(jumpingToY - getY()) < 1))) {
						if(currClaw != null) {
							currClaw.destroy();
						}
						jumping = false;
						clawAttacking = false;
						hasStartedJumping = false;
					}
					
					// If slashing, hurt the player.
					if(slashing && !hasSlashed && currPlayer.isWithin(getX(), getY(), getX()+getWidth(), getY() + getHeight())) {
						currPlayer.hurt(getAttackDamage(), 1f);
						hasSlashed = true;
						slashing = false;
					}
				}
			}
		}
		

	// Remove claw.
	@Override
	public void reactToDeath() {
		if(currClaw != null) currClaw.destroy();
	}
	
	// Jump
	public void slashTo(clawMarkRed c) {
		stopMove("all");
		sound s = new sound(bark2);
		s.setPosition(getX(), getY(), soundRadius);
		s.start();
		
		// Set facing direction.
		if(this.getX() - c.getX() < 0) {
			setFacingDirection("Right");
		}
		else {
			setFacingDirection("Left");
		}
		
		// Jump there
		jumpingToX = c.getX() + c.getWidth()/2 - getWidth()/2;
		jumpingToY = c.getY() + c.getHeight()/2 - getHeight()/2;
		jumping = true;
		slashing = true;
		hasSlashed = false;
		riseRunSet = false;
	}
	
	// Deal with claw attacks.
	public void dealWithClawAttacks() {
		if(clawAttacking) {
			
			// Spawn claw phase.
			if(!hasClawSpawned && time.getTime() - startOfClawAttack < spawnClawPhaseTime*1000) {
				hasClawSpawned = true;
				currClaw = new clawMarkRed(player.getCurrentPlayer().getX()+player.getCurrentPlayer().getWidth()/2-clawMarkRed.DEFAULT_CHUNK_WIDTH/2, 
						player.getCurrentPlayer().getY()+player.getCurrentPlayer().getHeight()/2-clawMarkRed.DEFAULT_CHUNK_HEIGHT/2,0);
			}
			
			
			// Slashing phase.
			else if(hasClawSpawned && time.getTime() - startOfClawAttack > spawnClawPhaseTime*1000 && !hasStartedJumping) {
				hasStartedJumping = true;
				slashTo(currClaw);
			}
		}
	}
	
	// wolf AI moves wolf around for now.
	public void updateUnit() {
		
		// If player is in radius, follow player, attacking.
		player currPlayer = player.getCurrentPlayer();
		int playerX = currPlayer.getX();
		int playerY = currPlayer.getY();
		float howClose = (float) Math.sqrt((playerX - getX())*(playerX - getX()) + (playerY - getY())*(playerY - getY()));
		
		// Make sounds.
		makeSounds();
		
		// Claw attack
		dealWithClawAttacks();
		dealWithJumping();
		
		// Attack if we're in radius.
		if(!clawAttacking && !isDosile() && howClose < DEFAULT_ATTACK_RADIUS) {
			
			// If we're in attack range, attack.
			if(isInAttackRange(currPlayer, DEFAULT_ATTACK_DIFFERENTIAL)) {
				if(time.getTime() - lastClawAttack > clawAttackEvery*1000) {
					clawAttackEvery = 0.1f + 0.1f*(float)utility.RNG.nextInt(3);
					lastClawAttack = time.getTime();
					stopMove("all");
					clawAttack(currPlayer);
				}
			}
			else {
				if(!aggrod) {
					sound s = new sound(growl);
					s.setPosition(getX(), getY(), soundRadius);
					s.start();
				}
				aggrod = true;
				follow(currPlayer);
			}
		}
		else if(aggrod && howClose > DEFAULT_DEAGGRO_RADIUS) {
			stopMove("all");
		}
		
		// Even dosile wolves attack if provoked.
		if(dosile && isInAttackRange(currPlayer, DEFAULT_ATTACK_DIFFERENTIAL)) {
			attack();
		}
	}
	
	// Deal with movement animations.
	public void dealWithAnimations(int moveX, int moveY) {
		if(jumping) {
			animate("slashing" + facingDirection);
		}
		else if(isAttacking() && !isAlreadyAttacked()) {
			// Play animation.
			animate("attacking" + facingDirection);
		}
		else if(isMoving()) {
			animate("running" + getFacingDirection());
		}
		else {
			animate("standing" + getFacingDirection());
		}
	}
	
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
	
	public boolean isDosile() {
		return dosile;
	}

	public void setDosile(boolean dosile) {
		this.dosile = dosile;
	}
}
