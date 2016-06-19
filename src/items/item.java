package items;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import drawing.drawnObject;
import drawing.gameCanvas;
import drawing.spriteSheet;
import effects.effect;
import effects.effectTypes.floatingString;
import items.bottles.normalBottle;
import items.weapons.dagger;
import sounds.sound;
import units.player;
import utilities.stringUtils;
import zones.farmLand.sheepFarm;
import zones.farmLand.spiderCave;

public abstract class item extends drawnObject {
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// All items.
	public static ArrayList<item> allItems = new ArrayList<item>();
	
	// Display
	public static Color DEFAULT_PICKUP_COLOR = new Color(103,238,245);
	
	// Volume and sound
	public static sound pickUpSound = new sound("sounds/effects/player/items/itemPickup.wav");
	private static float volume = .7f;
	
	//////////////
	/// FIELDS ///
	//////////////
	
	// Item name
	public String name;
	
	// Does the player actually own the item?
	public static boolean inInventory = false;
	
	// Is it equippable?
	public boolean equippable = false;
	
	///////////////
	/// METHODS ///
	///////////////
	public item(String newName, spriteSheet newSpriteSheet, int newX, int newY, int newWidth, int newHeight) {
		super(newSpriteSheet,newX,newY,newWidth,newHeight);
		name = newName;
		allItems.add(this);
	}
	
	// Get the item's image.
	public abstract BufferedImage getImage();
	
	// Get item by name.
	public static item getItemByName(String s) {
		for(int i = 0; i < allItems.size(); i++) {
			if(s.equals(allItems.get(i).name)) return allItems.get(i);
		}
		return null;
	}
	
	// Equip the item.
	public abstract void equip();
	
	// Pickup the item.
	public void pickUp() {
		if(player.getCurrentPlayer() != null) {
			// Equip the item if it's a weapon or bottle and we don't have one equipped.
			if((player.getCurrentPlayer().getEquippedWeapon() == null && this instanceof weapon) ||
					(player.getCurrentPlayer().getEquippedBottle() == null && this instanceof bottle)) {
				equip();
			}
		
			// Display text. 
			player currPlayer = player.getCurrentPlayer();
			effect e = new floatingString("+" + stringUtils.toTitleCase(name), DEFAULT_PICKUP_COLOR, currPlayer.getX() + currPlayer.getWidth()/2, currPlayer.getY() + currPlayer.getHeight()/2, 1.2f);
			
			// At least add the item to the player's inventory.
			player.getCurrentPlayer().getPlayerInventory().pickUp(this);
			
		}
		
		// Play sound.
		pickUpSound.playSound(volume);
		
		// React to pick-up
		reactToPickup();
		
		// Stop drawing the weapon on the ground.
		setDrawObject(false);
		inInventory = true;
	}
	
	// React to pickup
	public void reactToPickup() {
	}
	
	// Draw the item
	@Override
	public void drawObject(Graphics g) {
		g.drawImage(getImage(), 
				drawX, 
				drawY, 
				(int)(gameCanvas.getScaleX()*getImage().getWidth()), 
				(int)(gameCanvas.getScaleY()*getImage().getHeight()), 
				null);
	}
	
	// Initiate so we actually have a list of items.
	public static void initiate() {
		
		// Weapons,
		dagger.weaponRef = new dagger();
		
		// Bottles.
		normalBottle.bottleRef = new normalBottle();
		
	}
}