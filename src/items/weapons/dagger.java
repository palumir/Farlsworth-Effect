package items.weapons;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import animation.animation;
import animation.animationPack;
import drawing.spriteSheet;
import drawing.spriteSheet.spriteSheetInfo;
import items.item;
import items.weapon;
import units.player;
import utilities.saveState;

public class dagger extends weapon {
	////////////////
	/// DEFAULTS ///
	////////////////
	// Weapon name
	public static String DEFAULT_WEAPON_NAME = "dagger";
	
	// Weapon stats.
	private int DEFAULT_ATTACK_DAMAGE = 2;
	private float DEFAULT_BAT = 0.3f;
	private float DEFAULT_ATTACK_TIME = 0.4f;
	private int DEFAULT_ATTACK_WIDTH = 50;
	private int DEFAULT_ATTACK_LENGTH = 20;
	
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
		
		// Weapon stats.
		setStats();
	}
	
	// On floor.
	public dagger(int x, int y) {
		super(DEFAULT_WEAPON_NAME,x,y);
		
		// Weapon stats.
		setStats();
	}
	
	// Set stats
	public void setStats() {
		// Weapon stats.
		attackDamage = DEFAULT_ATTACK_DAMAGE;
		attackTime = DEFAULT_ATTACK_TIME;
		baseAttackTime = DEFAULT_BAT;
		attackWidth = DEFAULT_ATTACK_WIDTH;
		attackLength = DEFAULT_ATTACK_LENGTH;
		range = "short";
		speed = "fast";
	}
	
	// Pickup item.
	public void pickUp() {
		if(player.getCurrentPlayer() != null) {
			// Equip the item if it's a weapon and we don't have one equipped.
			if(player.getCurrentPlayer().getEquippedWeapon() == null
					&& this instanceof weapon) {
				equip();
			}
		
			// At least add the item to the player's inventory.
			player.getCurrentPlayer().getPlayerInventory().pickUp(this);
			
		}
		
		// Stop drawing the weapon on the ground.
		setDrawObject(false);
		inInventory = true;
	}
	
	// Equip item
	public void equip() {
		
		// Equip the weapon.
		player.getCurrentPlayer().setEquippedWeapon(this);
		
		// Change the player's stats based on the weapon's strength and their
		// level.
		player.getCurrentPlayer().setAttackDamage(DEFAULT_ATTACK_DAMAGE);
		player.getCurrentPlayer().setAttackTime(DEFAULT_ATTACK_TIME);
		player.getCurrentPlayer().setBaseAttackTime(DEFAULT_BAT);
		player.getCurrentPlayer().setAttackWidth(DEFAULT_ATTACK_WIDTH);
		player.getCurrentPlayer().setAttackLength(DEFAULT_ATTACK_LENGTH);
		
		// Deal with animations
		animationPack unitTypeAnimations = new animationPack();
		
		// Attacking left animation.
		animation attackingLeft = new animation("attackingLeft", weaponSpriteSheet.getAnimation(13), 0, 5, DEFAULT_BAT);
		unitTypeAnimations.addAnimation(attackingLeft);
		
		// Attacking left animation.
		animation attackingRight = new animation("attackingRight", weaponSpriteSheet.getAnimation(15), 0, 5, DEFAULT_BAT);
		unitTypeAnimations.addAnimation(attackingRight);
		
		// Attacking left animation.
		animation attackingUp = new animation("attackingUp", weaponSpriteSheet.getAnimation(12), 0, 5, DEFAULT_BAT);
		unitTypeAnimations.addAnimation(attackingUp);
		
		// Attacking left animation.
		animation attackingDown = new animation("attackingDown", weaponSpriteSheet.getAnimation(14), 0, 5, DEFAULT_BAT);
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
		animation runningLeft = new animation("runningLeft", weaponSpriteSheet.getAnimation(9), 0, 8, 1);
		unitTypeAnimations.addAnimation(runningLeft);		
		
		// Running up animation.
		animation runningUp = new animation("runningUp", weaponSpriteSheet.getAnimation(8), 0, 8, 1);
		unitTypeAnimations.addAnimation(runningUp);
		
		// Running right animation.
		animation runningRight = new animation("runningRight", weaponSpriteSheet.getAnimation(11), 0, 8, 1);
		unitTypeAnimations.addAnimation(runningRight);
		
		// Running down animation.
		animation runningDown = new animation("runningDown", weaponSpriteSheet.getAnimation(10), 0, 8, 1);
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