package items;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import drawing.drawnObject;
import drawing.spriteSheet;
import items.bottles.normalBottle;
import items.weapons.dagger;
import zones.farmLand.sheepFarm;
import zones.farmLand.spiderCave;

public abstract class item extends drawnObject {
	////////////////
	/// DEFAULTS ///
	////////////////
	public static ArrayList<item> allItems = new ArrayList<item>();
	
	//////////////
	/// FIELDS ///
	//////////////
	public String name;
	
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
	public abstract void pickUp();
	
	// Initiate so we actually have a list of items.
	public static void initiate() {
		
		// Weapons,
		dagger.weaponRef = new dagger();
		
		// Bottles.
		normalBottle.bottleRef = new normalBottle();
		
	}
}