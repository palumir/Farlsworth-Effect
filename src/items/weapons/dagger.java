package items.weapons;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import drawing.spriteSheet;
import drawing.animation.animation;
import drawing.animation.animationPack;
import drawing.spriteSheet.spriteSheetInfo;
import drawing.userInterface.tooltipString;
import interactions.event;
import items.item;
import items.weapon;
import units.player;
import utilities.saveState;
import zones.farmLand.sheepFarm;

public class dagger extends weapon {
	////////////////
	/// DEFAULTS ///
	////////////////
	// Weapon name
	public static String DEFAULT_WEAPON_NAME = "Dagger";
	
	// Weapon stats.
	static private int DEFAULT_ATTACK_DAMAGE = 2; //2;
	static private float DEFAULT_ATTACK_TIME = 0.30f; //0.30f;
	static private float DEFAULT_BACKSWING = 0.1f;
	static private int DEFAULT_ATTACK_WIDTH = 50;
	static private int DEFAULT_ATTACK_LENGTH = 18;
	static private float DEFAULT_CRIT_CHANCE = .2f;
	static private float DEFAULT_CRIT_DAMAGE = 2f;
	static private float DEFAULT_VARIABILITY = 0f;
	
	// Event for telling the player to press 'i' to exit inventory.
	static private event pressIToExit;
	
	// Attack sound
	static private String DEFAULT_ATTACK_SOUND = "sounds/effects/combat/knifeSlash.wav";
	
	//////////////
	/// FIELDS ///
	//////////////
	public static BufferedImage itemImage = spriteSheet.getSpriteFromFilePath("images/doodads/items/"+ DEFAULT_WEAPON_NAME + ".png");
	public static spriteSheet weaponSpriteSheet = new spriteSheet(new spriteSheetInfo(
			"images/units/player/" + player.DEFAULT_PLAYER_GENDER + "/"+ DEFAULT_WEAPON_NAME + ".png", 
			weapon.DEFAULT_SPRITE_WIDTH, 
			weapon.DEFAULT_SPRITE_HEIGHT,
			weapon.DEFAULT_SPRITE_ADJUSTMENT_X,
			weapon.DEFAULT_SPRITE_ADJUSTMENT_Y
			));
	
	public static weapon weaponRef;
	
	///////////////
	/// METHODS ///
	///////////////
	
	// In inventory.
	public dagger() {
		super(DEFAULT_WEAPON_NAME,weaponSpriteSheet);
		
		// Create event.
		pressIToExit = new event("daggerPressIToExit");
		
		// Weapon stats.
		setStats();
	}
	
	// On floor.
	public dagger(int x, int y) {
		super(DEFAULT_WEAPON_NAME,x,y);
		
		// Create event.
		pressIToExit = new event("daggerPressIToExit");
		
		// Weapon stats.
		setStats();
	}
	
	// React to being picked up.
	@Override
	public void reactToPickup() {
		player currPlayer = player.getCurrentPlayer();
		if(currPlayer != null) {
			if(!sheepFarm.attackTooltipLoaded.isCompleted()) sheepFarm.attackTooltipLoaded.setCompleted(true);
			tooltipString t = new tooltipString("Press 'i' to open inventory.");
		}
	}
	
	// Set stats
	public void setStats() {
		// Weapon stats.
		setAttackDamage(DEFAULT_ATTACK_DAMAGE);
		attackSound = DEFAULT_ATTACK_SOUND;
		attackTime = DEFAULT_ATTACK_TIME;
		attackWidth = DEFAULT_ATTACK_WIDTH;
		attackLength = DEFAULT_ATTACK_LENGTH;
		critChance = DEFAULT_CRIT_CHANCE;
		critDamage = DEFAULT_CRIT_DAMAGE;
		attackVariability = DEFAULT_VARIABILITY;
		backSwing = DEFAULT_BACKSWING;
		setRange("short");
		setSpeed("fast");
	}
	
	// Get item ref.
	public item getItemRef() {
		return weaponRef;
	}
	
	// Equip item
	public void equip() {
		
		// Set pressIToExit to be true.
		if(!pressIToExit.isCompleted()) {
			pressIToExit.setCompleted(true);
			tooltipString t = new tooltipString("Press 'i' or 'esc' to exit inventory.");
		}
		
		// Equip the weapon.
		player.getCurrentPlayer().setEquippedWeapon(this);
		
		// Change the player's stats based on the weapon's strength and their
		// level.
		player.getCurrentPlayer().setAttackSound(attackSound);
		player.getCurrentPlayer().setAttackDamage(attackDamage);
		player.getCurrentPlayer().setAttackTime(attackTime);
		player.getCurrentPlayer().setAttackFrameStart(4);
		player.getCurrentPlayer().setAttackFrameEnd(8);
		player.getCurrentPlayer().setAttackWidth(attackWidth);
		player.getCurrentPlayer().setAttackLength(attackLength);
		player.getCurrentPlayer().setCritChance(critChance);
		player.getCurrentPlayer().setCritDamage(critDamage);
		player.getCurrentPlayer().setAttackVariability(attackVariability);
		player.getCurrentPlayer().setBackSwing(backSwing);
		
		// Deal with animations
		animationPack unitTypeAnimations = new animationPack();
		
		// Attacking left animation.
		animation attackingLeft = new animation("attackingLeft", weaponSpriteSheet.getAnimation(13), 0, 8, DEFAULT_ATTACK_TIME);
		unitTypeAnimations.addAnimation(attackingLeft);
		
		// Attacking left animation.
		animation attackingRight = new animation("attackingRight", weaponSpriteSheet.getAnimation(15), 0, 8, DEFAULT_ATTACK_TIME);
		unitTypeAnimations.addAnimation(attackingRight);
		
		// Attacking left animation.
		animation attackingUp = new animation("attackingUp", weaponSpriteSheet.getAnimation(12), 0, 8, DEFAULT_ATTACK_TIME);
		unitTypeAnimations.addAnimation(attackingUp);
		
		// Attacking left animation.
		animation attackingDown = new animation("attackingDown", weaponSpriteSheet.getAnimation(14), 0, 8, DEFAULT_ATTACK_TIME);
		unitTypeAnimations.addAnimation(attackingDown);
		
		// Jumping left animation.
		animation jumpingLeft = new animation("jumpingLeft", weaponSpriteSheet.getAnimation(1), 5, 5, 1);
		unitTypeAnimations.addAnimation(jumpingLeft);
		
		// Jumping right animation.
		animation jumpingRight = new animation("jumpingRight", weaponSpriteSheet.getAnimation(3), 5, 5, 1);
		unitTypeAnimations.addAnimation(jumpingRight);
		
		// Standing left animation.
		animation standingLeft = new animation("standingLeft", weaponSpriteSheet.getAnimation(9), 0, 0, 1);
		unitTypeAnimations.addAnimation(standingLeft);
		
		// Standing up animation.
		animation standingUp = new animation("standingUp", weaponSpriteSheet.getAnimation(8), 0, 0, 1);
		unitTypeAnimations.addAnimation(standingUp);
		
		// Standing right animation.
		animation standingRight = new animation("standingRight", weaponSpriteSheet.getAnimation(11), 0, 0, 1);
		unitTypeAnimations.addAnimation(standingRight);
		
		// Standing down animation.
		animation standingDown = new animation("standingDown", weaponSpriteSheet.getAnimation(10), 0, 0, 1);
		unitTypeAnimations.addAnimation(standingDown);
		
		// Running left animation.
		animation runningLeft = new animation("runningLeft", weaponSpriteSheet.getAnimation(9), 1, 8, 0.75f);
		unitTypeAnimations.addAnimation(runningLeft);		
		
		// Running up animation.
		animation runningUp = new animation("runningUp", weaponSpriteSheet.getAnimation(8), 1, 8, 0.75f);
		unitTypeAnimations.addAnimation(runningUp);
		
		// Running right animation.
		animation runningRight = new animation("runningRight", weaponSpriteSheet.getAnimation(11), 1, 8, 0.75f);
		unitTypeAnimations.addAnimation(runningRight);
		
		// Running down animation.
		animation runningDown = new animation("runningDown", weaponSpriteSheet.getAnimation(10), 1, 8, 0.75f);
		unitTypeAnimations.addAnimation(runningDown);
		
		// Set animations.
		player.getCurrentPlayer().setAnimations(unitTypeAnimations);
	}
	
	// Get the item image.
	public BufferedImage getImage() {
		return itemImage;
	}
	
	// Get weapon reference.
	public static weapon getWeapon() {
		return weaponRef;
	}
}