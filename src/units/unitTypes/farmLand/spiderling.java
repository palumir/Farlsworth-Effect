package units.unitTypes.farmLand;

import java.util.Random;

import drawing.camera;
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
import utilities.time;
import utilities.utility;
import zones.zone;

public class spiderling extends unit {
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default name.
	private static String DEFAULT_spiderling_NAME = "spiderling";
	
	// Platformer real dimensions
	public static int DEFAULT_PLATFORMER_HEIGHT = 32;
	public static int DEFAULT_PLATFORMER_WIDTH = 32;
	public static int DEFAULT_PLATFORMER_ADJUSTMENT_Y = 0;
	
	// TopDown real dimensions
	public static int DEFAULT_TOPDOWN_HEIGHT = 18;
	public static int DEFAULT_TOPDOWN_WIDTH = 30;
	public static int DEFAULT_TOPDOWN_ADJUSTMENT_Y = 5;
	
	// How close to attack?
	private int DEFAULT_ATTACK_RADIUS = 200;
	private int DEFAULT_DEAGGRO_RADIUS = 300;
	
	// Damage stats
	private int DEFAULT_ATTACK_DIFFERENTIAL = 6; // the range within the attackrange the unit will attack.
	private int DEFAULT_ATTACK_DAMAGE = 4;
	private float DEFAULT_BAT = 0.35f;
	private float DEFAULT_ATTACK_TIME = 0.9f;
	private int DEFAULT_ATTACK_WIDTH = 30;
	private int DEFAULT_ATTACK_LENGTH = 15;
	
	// Health.
	private int DEFAULT_HP = 15;
	
	// Default movespeed.
	private static int DEFAULT_spiderling_MOVESPEED = 2;
	
	// Default jump speed
	private static int DEFAULT_spiderling_JUMPSPEED = 10;
	
	// spiderling sprite stuff.
	private static String DEFAULT_spiderling_SPRITESHEET = "images/units/animals/spiderling.png";
	
	// The actual type.
	private static unitType spiderlingType =
			new animalType( "spiderling",  // Name of unitType 
						 DEFAULT_spiderling_SPRITESHEET,
					     DEFAULT_spiderling_MOVESPEED, // Movespeed
					     DEFAULT_spiderling_JUMPSPEED // Jump speed
						);	
	
	// Sounds
	//private sound howl = new sound("sounds/effects/animals/spiderlingHowl.wav");
	private sound spiderlingAttack = new sound("sounds/effects/player/combat/swingWeapon.wav");
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
	
	///////////////
	/// METHODS ///
	///////////////
	// Constructor
	public spiderling(int newX, int newY) {
		super(spiderlingType, newX, newY);
		
		//showAttackRange();
		// Set spiderling combat stuff.
		setCombatStuff();
		attackSound = spiderlingAttack;
		
		// Add attack animations.
		// Attacking left animation.
		animation attackingLeft = new animation("attackingLeft", spiderlingType.getUnitTypeSpriteSheet().getAnimation(5), 0, 5, DEFAULT_BAT);
		getAnimations().addAnimation(attackingLeft);
		
		// Attacking left animation.
		animation attackingRight = new animation("attackingRight", spiderlingType.getUnitTypeSpriteSheet().getAnimation(6), 0, 5, DEFAULT_BAT);
		getAnimations().addAnimation(attackingRight);
		
		// Attacking left animation.
		animation attackingUp = new animation("attackingUp", spiderlingType.getUnitTypeSpriteSheet().getAnimation(7), 0, 5, DEFAULT_BAT);
		getAnimations().addAnimation(attackingUp);
		
		// Attacking left animation.
		animation attackingDown = new animation("attackingDown", spiderlingType.getUnitTypeSpriteSheet().getAnimation(4), 0, 5, DEFAULT_BAT);
		getAnimations().addAnimation(attackingDown);
		
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
		this.setAttackable(true);
		
		// spiderling damage.
		setAttackDamage(DEFAULT_ATTACK_DAMAGE);
		setAttackTime(DEFAULT_ATTACK_TIME);
		setBaseAttackTime(DEFAULT_BAT);
		setAttackWidth(DEFAULT_ATTACK_WIDTH);
		setAttackLength(DEFAULT_ATTACK_LENGTH);
		
		// HP
		setMaxHealthPoints(DEFAULT_HP);
		setHealthPoints(DEFAULT_HP);
		
	}
	
	// React to pain.
	public void reactToPain() {
		// Play a bark on pain.
		if(lastBarkSound == 0) {
			lastBarkSound = 1;
			//bark1.playSound(this.getX(), this.getY(), soundRadius, DEFAULT_BARK_VOLUME);
		}
		else if(lastBarkSound == 1) {
			lastBarkSound = 0;
			//bark2.playSound(this.getX(), this.getY(), soundRadius, DEFAULT_BARK_VOLUME);
		}
	}
	
	// spiderling random noises
	public void makeSounds() {
		
			// Create a new random growl interval
			float newRandomHowlInterval = 10f + utility.RNG.nextInt(10);
			
			// Make the spiderling howl
			if(randomHowl == 0f) {
				randomHowl = newRandomHowlInterval;
			}
			if(!aggrod && time.getTime() - lastHowl > randomHowl*1000) {
				
				// Set the last time they howled
				lastHowl = time.getTime();
				randomHowl = newRandomHowlInterval;
				//howl.playSound(this.getX(), this.getY(), soundRadius, DEFAULT_HOWL_VOLUME);
			}
	}
	
	// spiderling AI moves spiderling around for now.
	public void updateUnit() {
		
		// If player is in radius, follow player, attacking.
		player currPlayer = player.getCurrentPlayer();
		int playerX = currPlayer.getX();
		int playerY = currPlayer.getY();
		float howClose = (float) Math.sqrt((playerX - getX())*(playerX - getX()) + (playerY - getY())*(playerY - getY()));
		
		// Make sounds.
		makeSounds();
		
		// Attack if we're in radius.
		if(howClose < DEFAULT_ATTACK_RADIUS) {
			
			// If we're in attack range, attack.
			if(isInAttackRange(currPlayer, DEFAULT_ATTACK_DIFFERENTIAL)) {
				stopMove("all");
				attack();
			}
			else {
				if(!aggrod) //growl.playSound(this.getX(), this.getY(), soundRadius, DEFAULT_GROWL_VOLUME);
				aggrod = true;
				follow(currPlayer);
			}
		}
		else if(aggrod && howClose > DEFAULT_DEAGGRO_RADIUS) {
			stopMove("all");
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
}