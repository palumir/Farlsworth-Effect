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

public class torch extends weapon {
	////////////////
	/// DEFAULTS ///
	////////////////
	// Weapon name
	public static String DEFAULT_WEAPON_NAME = "torch";
	
	// Weapon stats.
	static private int DEFAULT_ATTACK_DAMAGE = 7;
	static private float DEFAULT_BAT = 0.35f;
	static private float DEFAULT_ATTACK_TIME = 0.45f;
	static private int DEFAULT_ATTACK_WIDTH = 55;
	static private int DEFAULT_ATTACK_LENGTH = 30;
	static private float DEFAULT_CRIT_CHANCE = .17f;
	static private float DEFAULT_CRIT_DAMAGE = 1.5f;
	static private float DEFAULT_VARIABILITY = 0.1f;
	
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
	
	// Is the torch lit?
	private boolean lit = false;
	
	///////////////
	/// METHODS ///
	///////////////
	
	// In inventory.
	public torch() {
		super(DEFAULT_WEAPON_NAME,weaponSpriteSheet);
		
		// Weapon stats.
		setStats();
	}
	
	// On floor.
	public torch(int x, int y) {
		super(DEFAULT_WEAPON_NAME,x,y);
		
		// Weapon stats.
		setStats();
	}
	
	// React to being picked up.
	@Override
	public void reactToPickup() {
		player currPlayer = player.getCurrentPlayer();
		if(currPlayer != null) {
			// TODO:
			//if(!sheepFarm.attackTooltipLoaded.isCompleted()) sheepFarm.attackTooltipLoaded.setCompleted(true);
			//tooltipString t = new tooltipString("Light a torch at a fire.");
		}
	}
	
	// Set stats
	public void setStats() {
		// Weapon stats.
		setAttackDamage(DEFAULT_ATTACK_DAMAGE);
		attackTime = DEFAULT_ATTACK_TIME;
		baseAttackTime = DEFAULT_BAT;
		attackWidth = DEFAULT_ATTACK_WIDTH;
		attackLength = DEFAULT_ATTACK_LENGTH;
		critChance = DEFAULT_CRIT_CHANCE;
		critDamage = DEFAULT_CRIT_DAMAGE;
		attackVariability = DEFAULT_VARIABILITY;
		setRange("medium");
		setSpeed("medium");
	}
	
	// Equip item
	public void equip() {
		
		// Equip the weapon.
		player.getCurrentPlayer().setEquippedWeapon(this);
		
		// Change the player's stats based on the weapon's strength and their
		// level.
		player.getCurrentPlayer().setAttackDamage(attackDamage);
		player.getCurrentPlayer().setAttackTime(attackTime);
		player.getCurrentPlayer().setBaseAttackTime(baseAttackTime);
		player.getCurrentPlayer().setAttackWidth(attackWidth);
		player.getCurrentPlayer().setAttackLength(attackLength);
		player.getCurrentPlayer().setCritChance(critChance);
		player.getCurrentPlayer().setCritDamage(critDamage);
		player.getCurrentPlayer().setAttackVariability(attackVariability);
		
		// Deal with animations
		animationPack unitTypeAnimations = new animationPack();
		
		// Attacking left animation.
		animation attackingLeft = new animation("attackingLeft", weaponSpriteSheet.getAnimation(13), 0, 5, DEFAULT_ATTACK_TIME);
		unitTypeAnimations.addAnimation(attackingLeft);
		
		// Attacking left animation.
		animation attackingRight = new animation("attackingRight", weaponSpriteSheet.getAnimation(15), 0, 5, DEFAULT_ATTACK_TIME);
		unitTypeAnimations.addAnimation(attackingRight);
		
		// Attacking left animation.
		animation attackingUp = new animation("attackingUp", weaponSpriteSheet.getAnimation(12), 0, 5, DEFAULT_ATTACK_TIME);
		unitTypeAnimations.addAnimation(attackingUp);
		
		// Attacking left animation.
		animation attackingDown = new animation("attackingDown", weaponSpriteSheet.getAnimation(14), 0, 5, DEFAULT_ATTACK_TIME);
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
	
	// Light torch
	public void light() {
		lit = true;
	}
	
	// Unlight torch
	public void goOut() {
		lit = false;
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