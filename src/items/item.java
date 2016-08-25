package items;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import drawing.drawnObject;
import drawing.gameCanvas;
import drawing.spriteSheet;
import effects.effect;
import effects.interfaceEffects.floatingString;
import items.bottles.saveBottle;
import items.keys.farmGateKey;
import items.keys.farmKey;
import sounds.sound;
import units.player;
import utilities.saveBooleanList;
import utilities.stringUtils;

public abstract class item extends drawnObject {
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// All items.
	public static ArrayList<item> allItems = new ArrayList<item>();
	
	// Display
	public static Color DEFAULT_PICKUP_COLOR = new Color(103,238,245);
	public static Color DEFAULT_DROP_COLOR = Color.red;
	
	// Volume and sound
	public static String pickUpSound = "sounds/effects/player/items/itemPickup.wav";
	private static float volume = .7f;
	
	//////////////
	/// FIELDS ///
	//////////////
	
	// List of booleans we will save for the item.
	private saveBooleanList saveBooleans = new saveBooleanList();

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
		super(newSpriteSheet,newName, newX,newY,newWidth,newHeight);
		allItems.add(this);
	}
	
	// Get the item's image.
	public abstract BufferedImage getImage();
	
	// Get item by name.
	public static item getItemByName(String s) {
		for(int i = 0; i < allItems.size(); i++) {
			if(s.equals(allItems.get(i).getName())) return allItems.get(i).getItemRef();
		}
		if(!s.equals("None!")) System.err.println("Item " + s + " has not been initialized in item.initiate()!");
		return null;
	}
	
	// Equip the item.
	public abstract void equip();
	
	// Pickup the item.
	public void drop() {
		if(player.getPlayer() != null) {
		
			// Display text. 
			player currPlayer = player.getPlayer();
			effect e = new floatingString("-" + stringUtils.toTitleCase(getName()), DEFAULT_DROP_COLOR, currPlayer.getIntX() + currPlayer.getWidth()/2, currPlayer.getIntY() + currPlayer.getHeight()/2, 1.3f);
			e.setAnimationDuration(3f);
			
			// At least add the item to the player's inventory.
			player.getPlayer().getPlayerInventory().drop(this.getItemRef());
			
		}
		
		// Play sound.
		sound s = new sound(pickUpSound);
		s.start();
		
		// Stop drawing the weapon on the ground.
		inInventory = false;
		destroy();
	}
	
	// Pickup the item.
	public void pickUp() {
		if(player.getPlayer() != null) {
		
			// Display text. 
			player currPlayer = player.getPlayer();
			effect e = new floatingString("+" + stringUtils.toTitleCase(getName()), DEFAULT_PICKUP_COLOR, currPlayer.getIntX() + currPlayer.getWidth()/2, currPlayer.getIntY() + currPlayer.getHeight()/2, 1.3f);
			e.setAnimationDuration(3f);
			
			// At least add the item to the player's inventory.
			player.getPlayer().getPlayerInventory().pickUp(this.getItemRef());
			
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

		// Bottles.
		if(saveBottle.bottleRef == null) saveBottle.bottleRef = new saveBottle();
		
		// Keys.
		if(farmKey.keyRef == null) farmKey.keyRef = new farmKey();
		if(farmGateKey.keyRef == null) farmGateKey.keyRef = new farmGateKey();
		
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