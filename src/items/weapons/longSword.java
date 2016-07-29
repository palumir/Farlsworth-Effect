package items.weapons;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import drawing.spriteSheet;
import drawing.animation.animation;
import drawing.animation.animationPack;
import drawing.spriteSheet.spriteSheetInfo;
import drawing.userInterface.tooltipString;
import items.item;
import items.weapon;
import units.player;
import utilities.saveState;
import zones.farmLand.sheepFarm;

public class longSword extends weapon {
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Weapon name
	public static String DEFAULT_WEAPON_NAME = "Long Sword";
	
	// Weapon stats.
	static private int DEFAULT_ATTACK_DAMAGE = 4; //4;
	static private float DEFAULT_ATTACK_TIME = 0.35f; //0.41f;
	static private float DEFAULT_BACKSWING = 0.22f;
	static private int DEFAULT_ATTACK_WIDTH = 75;
	static private int DEFAULT_ATTACK_LENGTH = 35;
	static private float DEFAULT_CRIT_CHANCE = .05f;
	static private float DEFAULT_CRIT_DAMAGE = 2f;
	static private float DEFAULT_VARIABILITY = 0f;
	static private float DPS = (1/(DEFAULT_ATTACK_TIME + DEFAULT_BACKSWING))*(DEFAULT_ATTACK_DAMAGE + DEFAULT_ATTACK_DAMAGE*DEFAULT_CRIT_DAMAGE*DEFAULT_CRIT_CHANCE);
	
	// Attack sound
	static private String DEFAULT_ATTACK_SOUND = "sounds/effects/combat/knifeSlash.wav";
	
	//////////////
	/// FIELDS ///
	//////////////
	public static BufferedImage itemImage = spriteSheet.getSpriteFromFilePath("images/doodads/items/longSword.png");
	public static spriteSheet weaponMovingSpriteSheet = new spriteSheet(new spriteSheetInfo(
			"images/units/player/" + player.DEFAULT_PLAYER_GENDER + "/longSwordMoving.png", 
			weapon.DEFAULT_SPRITE_WIDTH, 
			weapon.DEFAULT_SPRITE_HEIGHT,
			weapon.DEFAULT_SPRITE_ADJUSTMENT_X,
			weapon.DEFAULT_SPRITE_ADJUSTMENT_Y
			));
	public static spriteSheet weaponAttackingSpriteSheet = new spriteSheet(new spriteSheetInfo(
			"images/units/player/" + player.DEFAULT_PLAYER_GENDER + "/longSwordAttacking.png", 
			weapon.DEFAULT_SPRITE_WIDTH*2, 
			weapon.DEFAULT_SPRITE_HEIGHT,
			weapon.DEFAULT_SPRITE_ADJUSTMENT_X,
			weapon.DEFAULT_SPRITE_ADJUSTMENT_Y
			));
	
	public static weapon weaponRef;
	
	///////////////
	/// METHODS ///
	///////////////
	
	// In inventory.
	public longSword() {
		super(DEFAULT_WEAPON_NAME,null);
		
		// Weapon stats.
		setStats();
	}
	
	// On floor.
	public longSword(int x, int y) {
		super(DEFAULT_WEAPON_NAME,x,y);
		System.out.println("Long Sword DPS: " + DPS);
		
		// Weapon stats.
		setStats();
	}
	
	// React to being picked up.
	@Override
	public void reactToPickup() {
		player currPlayer = player.getPlayer();
		if(currPlayer != null) {
			if(!sheepFarm.attackTooltipLoaded.isCompleted()) sheepFarm.attackTooltipLoaded.setCompleted(true);
			tooltipString t = new tooltipString("Press or hold 'space' to attack.");
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
		type = "Melee Weapon";
		setRange("medium");
		setSpeed("medium");
	}
	
	// Get item ref.
	public item getItemRef() {
		return weaponRef;
	}
	
	// Equip item
	public void equip() {
		
		// Equip the weapon.
		player.getPlayer().setEquippedWeapon(this);
		
		// Change the player's stats based on the weapon's strength and their
		// level.
		player.getPlayer().setAttackSound(attackSound);
		player.getPlayer().setAttackDamage(attackDamage);
		player.getPlayer().setAttackTime(attackTime);
		player.getPlayer().setAttackFrameStart(4);
		player.getPlayer().setAttackFrameEnd(8);
		player.getPlayer().setAttackWidth(attackWidth);
		player.getPlayer().setAttackLength(attackLength);
		player.getPlayer().setCritChance(critChance);
		player.getPlayer().setCritDamage(critDamage);
		player.getPlayer().setAttackVariability(attackVariability);
		player.getPlayer().setBackSwing(backSwing);
		
		// Deal with animations
		animationPack unitTypeAnimations = new animationPack();
		
		// Attacking left animation.
		animation attackingLeft = new animation("attackingLeft", weaponAttackingSpriteSheet.getAnimation(1), 0, 8, DEFAULT_ATTACK_TIME);
		unitTypeAnimations.addAnimation(attackingLeft);
		
		// Attacking left animation.
		animation attackingRight = new animation("attackingRight", weaponAttackingSpriteSheet.getAnimation(3), 0, 8, DEFAULT_ATTACK_TIME);
		unitTypeAnimations.addAnimation(attackingRight);
		
		// Attacking left animation.
		animation attackingUp = new animation("attackingUp", weaponAttackingSpriteSheet.getAnimation(0), 0, 8, DEFAULT_ATTACK_TIME);
		unitTypeAnimations.addAnimation(attackingUp);
		
		// Attacking left animation.
		animation attackingDown = new animation("attackingDown", weaponAttackingSpriteSheet.getAnimation(2), 0, 8, DEFAULT_ATTACK_TIME);
		unitTypeAnimations.addAnimation(attackingDown);
		
		// Jumping left animation.
		animation jumpingLeft = new animation("jumpingLeft", weaponMovingSpriteSheet.getAnimation(1), 5, 5, 1);
		unitTypeAnimations.addAnimation(jumpingLeft);
		
		// Jumping right animation.
		animation jumpingRight = new animation("jumpingRight", weaponMovingSpriteSheet.getAnimation(3), 5, 5, 1);
		unitTypeAnimations.addAnimation(jumpingRight);
		
		// Standing left animation.
		animation standingLeft = new animation("standingLeft", weaponMovingSpriteSheet.getAnimation(9), 0, 0, 1);
		unitTypeAnimations.addAnimation(standingLeft);
		
		// Standing up animation.
		animation standingUp = new animation("standingUp", weaponMovingSpriteSheet.getAnimation(8), 0, 0, 1);
		unitTypeAnimations.addAnimation(standingUp);
		
		// Standing right animation.
		animation standingRight = new animation("standingRight", weaponMovingSpriteSheet.getAnimation(11), 0, 0, 1);
		unitTypeAnimations.addAnimation(standingRight);
		
		// Standing down animation.
		animation standingDown = new animation("standingDown", weaponMovingSpriteSheet.getAnimation(10), 0, 0, 1);
		unitTypeAnimations.addAnimation(standingDown);
		
		// Running left animation.
		animation runningLeft = new animation("runningLeft", weaponMovingSpriteSheet.getAnimation(9), 1, 8, 0.75f);
		unitTypeAnimations.addAnimation(runningLeft);		
		
		// Running up animation.
		animation runningUp = new animation("runningUp", weaponMovingSpriteSheet.getAnimation(8), 1, 8, 0.75f);
		unitTypeAnimations.addAnimation(runningUp);
		
		// Running right animation.
		animation runningRight = new animation("runningRight", weaponMovingSpriteSheet.getAnimation(11), 1, 8, 0.75f);
		unitTypeAnimations.addAnimation(runningRight);
		
		// Running down animation.
		animation runningDown = new animation("runningDown", weaponMovingSpriteSheet.getAnimation(10), 1, 8, 0.75f);
		unitTypeAnimations.addAnimation(runningDown);
		
		// Set animations.
		player.getPlayer().setAnimations(unitTypeAnimations);
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