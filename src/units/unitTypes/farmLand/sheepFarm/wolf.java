package units.unitTypes.farmLand.sheepFarm;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

import doodads.sheepFarm.clawMarkYellow;
import doodads.sheepFarm.clawMarkRed;
import doodads.sheepFarm.rock;
import drawing.camera;
import drawing.drawnObject;
import drawing.gameCanvas;
import drawing.animation.animation;
import effects.effect;
import effects.effectTypes.bloodSquirt;
import effects.effectTypes.darkExplode;
import effects.effectTypes.poisonExplode;
import effects.effectTypes.explodingRock;
import modes.mode;
import sounds.sound;
import terrain.chunk;
import units.animalType;
import units.humanType;
import units.player;
import units.unit;
import units.unitType;
import utilities.intTuple;
import utilities.mathUtils;
import utilities.pathFindingNode;
import utilities.time;
import utilities.utility;
import zones.zone;

public abstract class wolf extends unit {
		
		////////////////
		/// DEFAULTS ///
		////////////////
		
		// Platformer real dimensions
		public static int DEFAULT_PLATFORMER_HEIGHT = 32;
		public static int DEFAULT_PLATFORMER_WIDTH = 32;
		public static int DEFAULT_PLATFORMER_ADJUSTMENT_Y = 0;
		
		// TopDown real dimensions
		public static int DEFAULT_TOPDOWN_HEIGHT = 18;
		public static int DEFAULT_TOPDOWN_WIDTH = 30;
		public static int DEFAULT_TOPDOWN_ADJUSTMENT_Y = 5;
		
		// Follow until range
		protected int followUntilRange = 20 + utility.RNG.nextInt(15);
		
		// How close to attack?
		protected int DEFAULT_ATTACK_RADIUS = 300;
		protected int DEFAULT_DEAGGRO_RADIUS = 400;
		
		// Damage stats
		static  int DEFAULT_ATTACK_DAMAGE = 2;
		static protected float DEFAULT_ATTACK_TIME = 2f;
		static protected int DEFAULT_ATTACK_WIDTH = 500;
		static protected int DEFAULT_ATTACK_LENGTH = 500;
		static protected float DEFAULT_CRIT_CHANCE = .15f;
		static protected float DEFAULT_CRIT_DAMAGE = 1.6f;
		
		// Sounds
		protected static String howl = "sounds/effects/animals/wolfHowl.wav";
		protected static String growl = "sounds/effects/animals/wolfGrowl.wav";
		protected static String bark1 = "sounds/effects/animals/wolfBark1.wav";
		protected static String bark2 = "sounds/effects/animals/wolfBark2.wav";
		protected static String wolfAttack = "sounds/effects/player/combat/swingWeapon.wav";
		protected long lastHowl = 0;
		protected float randomHowl = 0;
		
		//////////////
		/// FIELDS ///
		//////////////
		
		// Is the wolf dosile?
		protected boolean dosile = false;
		
		// Is the wolf aggrod?
		protected boolean aggrod = false;
		
		// Claw attacking?
		protected boolean clawAttacking = false;
		protected boolean hasClawSpawned = false;
		protected boolean slashing = false;
		protected long startOfClawAttack = 0;
		protected float spawnClawPhaseTime = .5f;
		protected float clawAttackEveryBase = 3f;
		protected float clawAttackEvery = 0f;
		protected long lastClawAttack = 0;
		
		// Claw
		protected chunk currClaw = null;
		protected boolean hasStartedJumping = false;
		
		// Start rise and run
		protected int startX = 0;
		protected int startY = 0;
		
		// Jumping stuff
		protected int jumpingToX = 0;
		protected int jumpingToY = 0;
		protected boolean hasSlashed = false;
		protected boolean riseRunSet = false;
		protected int rise = 0;
		protected int run = 0;
		
		///////////////
		/// METHODS ///
		///////////////
		// Constructor
		public wolf(unitType wolfType, int newX, int newY) {
			super(wolfType, newX, newY);
			//showAttackRange();
			// Set wolf combat stuff.
			setCombatStuff();
			attackSound = wolfAttack;
			
			// Add attack animations.
			// Attacking left animation.
			animation slashingLeft = new animation("slashingLeft", wolfType.getUnitTypeSpriteSheet().getAnimation(5), 2, 2, 1);
			getAnimations().addAnimation(slashingLeft);
			
			// Attacking left animation.1
			animation slashingRight = new animation("slashingRight", wolfType.getUnitTypeSpriteSheet().getAnimation(6), 2, 2, 1);
			getAnimations().addAnimation(slashingRight);
			
			// Attacking left animation.
			animation attackingLeft = new animation("attackingLeft", wolfType.getUnitTypeSpriteSheet().getAnimation(5), 0, 5, 1);
			getAnimations().addAnimation(attackingLeft);
			
			// Attacking left animation.
			animation attackingRight = new animation("attackingRight", wolfType.getUnitTypeSpriteSheet().getAnimation(6), 0, 5, 1);
			getAnimations().addAnimation(attackingRight);
			
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
			s.setPosition(getX(), getY(), sound.DEFAULT_SOUND_RADIUS);
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
					s.setPosition(getX(), getY(), sound.DEFAULT_SOUND_RADIUS);
					s.start();
				}
		}

		// Claw attack.
		public void clawAttack(unit u) {
			clawAttacking = true;
			hasClawSpawned = false;
			slashing = false;
			
			// Start of claw attack.
			startOfClawAttack = time.getTime();
		}
		
		// Spawn rock every
		protected float spawnRockEvery = 0.05f;
		protected long lastSpawnRock = 0;
		
		// Charge units?
		public abstract void chargeUnits();
		
		// Spawn a trail?
		public abstract void spawnTrail();
		
		// Jumping finished
		public abstract void jumpingFinished();
		
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
						startX = getX();
						startY = getY();
					}
					
					// Charge units to position.
					chargeUnits();
					
					// Spawn rocks
					spawnTrail();
					
					setX(getX() + run);
					setY(getY() + rise);
					
					// Don't let him not move at all or leave region.
					if((run == 0 && rise == 0) || ((Math.abs(jumpingToX - getX()) < 1 && Math.abs(jumpingToY - getY()) < 1))) {
						if(currClaw != null) {
							clawDestroy();
						}
						jumping = false;
						clawAttacking = false;
						hasStartedJumping = false;
					}
					
					// If slashing, hurt the player.
					if(slashing && !hasSlashed && currPlayer.isWithin(getX(), getY(), getX() + getWidth(), getY() + getHeight())) {
						hasSlashed = true;
						slashing = false;
					}
				}
			}
			else {
				jumpingFinished();
			}
		}
		
		// Claw destroy
		public void clawDestroy() {
			currClaw.destroy();
		}

		// Remove claw.
		@Override
		public void reactToDeath() {
			if(currClaw != null) currClaw.destroy();
		}
		
		// Jump
		public void slashTo(chunk c) {
			stopMove("all");
			sound s = new sound(bark1);
			s.setPosition(getX(), getY(), sound.DEFAULT_SOUND_RADIUS);
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
		
		// Spawn claw
		public abstract void spawnClaw();
		
		// Deal with claw attacks.
		public void dealWithClawAttacks() {
			if(clawAttacking) {
				
				// Spawn claw phase.
				if(!hasClawSpawned && time.getTime() - startOfClawAttack < spawnClawPhaseTime*1000) {
					hasClawSpawned = true;
					spawnClaw();
				}
				
				// Slashing phase.
				else if(hasClawSpawned && time.getTime() - startOfClawAttack > spawnClawPhaseTime*1000 && !hasStartedJumping) {
					hasStartedJumping = true;
					slashTo((chunk)currClaw);
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
			if(!clawAttacking && !isDosile() && (howClose < DEFAULT_ATTACK_RADIUS || (aggrod && howClose < DEFAULT_DEAGGRO_RADIUS))) {
				
				// If we're in attack range, attack.
				if(isInAttackRange(currPlayer, 0) && time.getTime() - lastClawAttack > clawAttackEvery*1000) {
						clawAttackEvery = clawAttackEveryBase + 0.1f*(float)utility.RNG.nextInt(5);
						lastClawAttack = time.getTime();
						stopMove("all");
						clawAttack(currPlayer);
				}
				else {
					if(!aggrod) {
						sound s = new sound(growl);
						s.setPosition(getX(), getY(), sound.DEFAULT_SOUND_RADIUS);
						s.start();
					}
					aggrod = true;
					if(howClose > followUntilRange) {
						follow(currPlayer);
					}
					else {
						stopMove("all");
					}
				}
			}
			else if(aggrod && howClose > DEFAULT_DEAGGRO_RADIUS) {
				stopMove("all");
			}
			
			// Even dosile wolves attack if provoked.
			if(dosile && isInAttackRange(currPlayer, 0)) {
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