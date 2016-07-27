package items;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import drawing.drawnObject;
import drawing.gameCanvas;
import drawing.spriteSheet;
import effects.effect;
import effects.effectTypes.floatingString;
import items.bottles.normalBottle;
import items.keys.farmKey;
import items.weapons.dagger;
import items.weapons.longSword;
import items.weapons.torch;
import sounds.sound;
import units.player;
import utilities.saveBooleanList;
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
	public static String pickUpSound = "sounds/effects/player/items/itemPickup.wav";
	private static float volume = .7f;
	
	//////////////
	/// FIELDS ///
	//////////////
	
	// List of booleans we will save for the item.
	private saveBooleanList saveBooleans = new saveBooleanList();
	
	// Item name
	public String name;
	
	// Properties
	public ArrayList<String> properties;
	
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
			if(s.equals(allItems.get(i).name)) return allItems.get(i).getItemRef();
		}
		if(!s.equals("None!")) System.err.println("Item " + s + " has not been initialized in item.initiate()!");
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
				this.getItemRef().equip();
				if(this instanceof bottle) ((bottle)this.getItemRef()).setChargesLeft(((bottle)this.getItemRef()).getMaxCharges());
			}
		
			// Display text. 
			player currPlayer = player.getCurrentPlayer();
			effect e = new floatingString("+" + stringUtils.toTitleCase(name), DEFAULT_PICKUP_COLOR, currPlayer.getIntX() + currPlayer.getWidth()/2, currPlayer.getIntY() + currPlayer.getHeight()/2, 1.2f);
			
			// At least add the item to the player's inventory.
			player.getCurrentPlayer().getPlayerInventory().pickUp(this.getItemRef());
			
		}
		
		// Play sound.
		sound s = new sound(pickUpSound);
		s.start();
		
		// React to pick-up
		reactToPickup();
		
		// Stop drawing the weapon on the ground.
		setDrawObject(false);
		inInventory = true;
		destroy();
	}
	
	// Get item ref.
	public abstract item getItemRef();
	
	// React to pickup
	public void reactToPickup() {
	}
	
	// Draw the item
	@Override
	public void drawObject(Graphics g) {
		g.drawImage(getImage(), 
				getDrawX(), 
				getDrawY(), 
				(int)(gameCanvas.getScaleX()*getImage().getWidth()), 
				(int)(gameCanvas.getScaleY()*getImage().getHeight()), 
				null);
	}
	
	// Initiate events.
	public static void initiateEvents() {
	}
	
	// Initiate so we actually have a list of items.
	public static void initiate() {
		
		// Weapons
		if(dagger.weaponRef == null) dagger.weaponRef = new dagger();
		if(longSword.weaponRef == null) longSword.weaponRef = new longSword();
		if(torch.weaponRef == null) torch.weaponRef = new torch();

		// Bottles.
		if(normalBottle.bottleRef == null) normalBottle.bottleRef = new normalBottle();
		
		// Keys.
		if(farmKey.keyRef == null) farmKey.keyRef = new farmKey();
		
	}

	public saveBooleanList getSaveBooleans() {
		return saveBooleans;
	}

	public void setSaveBooleans(saveBooleanList saveBooleans) {
		this.saveBooleans = saveBooleans;
	}

	public void setUpItem() {
	}
}