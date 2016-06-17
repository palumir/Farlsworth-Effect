package drawing.userInterface;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import drawing.drawnObject;
import drawing.gameCanvas;
import drawing.spriteSheet;
import items.bottle;
import items.item;
import items.weapon;
import sounds.sound;
import units.player;
import utilities.stringUtils;

public class inventory extends interfaceObject {
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Inventory background.
	public static BufferedImage inventoryBackground = spriteSheet.getSpriteFromFilePath("images/interface/inventory.png");
	
	// Draw position
	public static int DEFAULT_INVENTORY_START_X = 20;
	public static int DEFAULT_INVENTORY_START_Y = gameCanvas.getDefaultHeight()-320;
	
	// Inventory size.
	public static int DEFAULT_INVENTORY_SIZE = 16; // total number of slots. must be a perfect root of 2
	public static int DEFAULT_SLOT_SIZE = 30; // size in terms of the actual physical size on the screen.
	
	// Strings.
	public static String DEFAULT_EMPTY_SLOT = "Empty";
	public static String DEFAULT_BOTTOM_TEXT = "Press \'e\' to equip";
	
	// Colors
	public static Color DEFAULT_SLOT_COLOR = new Color(52,41,36);
	public static Color DEFAULT_SLOT_BACKGROUND_COLOR = new Color(64,48,38);
	public static Color DEFAULT_TEXT_COLOR = Color.white;
	public static Color DEFAULT_SELECTED_SLOT_COLOR = new Color(122,96,84);
	public static Color DEFAULT_DESC_COLOR = Color.black;
	
	//////////////
	/// FIELDS ///
	//////////////
	
	// The actual items
	private ArrayList<item> items;
	
	// Selected slot
	private int selectedSlot = 0;
	
	// Display inventory on screen?
	private boolean displayOn = false;
	
	// Sounds for inventory.
	private sound openInventory;
	private sound closeInventory;
	private sound equipWeapon;
	private sound unequipWeapon;
	private sound UIMove;
	
	///////////////
	/// METHODS ///
	///////////////
	public inventory() {
		super(null, DEFAULT_INVENTORY_START_X, DEFAULT_INVENTORY_START_Y, 0, 0);
		items = new ArrayList<item>();
		
		// Set sounds.
		openInventory = new sound("sounds/effects/player/UI/openInventory.wav");
		closeInventory = new sound("sounds/effects/player/UI/closeInventory.wav");
		equipWeapon = new sound("sounds/effects/player/UI/equipItem.wav");
		unequipWeapon = new sound("sounds/effects/player/UI/unequipItem.wav");
		UIMove = new sound("sounds/effects/player/UI/UIMove.wav");
	}
	
	// Toggle inventory display.
	public void toggleDisplay() {
		setDisplayOn(!isDisplayOn());
		if(displayOn) openInventory.playSound(0.7f);
		else { closeInventory.playSound(0.7f); }
	}
	
	// Pickup an item into inventory.
	public void pickUp(item i) {
		if(!hasItem(i)) {
			items.add(i);
		}
	}
	
	// Check if inventory has item with the same name.
	public boolean hasItem(item i) {
		if(items != null) {
			for(int j = 0; j < items.size(); j++) {
				if(items.get(j) != null  && items.get(j).name.equals(i.name)) return true;
			}
		}
		return false;
	}
	
	// Interact with the current selected item.
	public void equipSelectedItem() {
		
		// We aren't trying to equip nothing.
		if(selectedSlot < items.size()) {
			
			// Make sure it's equippable.
			if(items.get(selectedSlot).equippable) {
				
				// Get the item.
				item i = items.get(selectedSlot);
				player currPlayer = player.getCurrentPlayer();
				
				// Deal with weapons.
				if(i instanceof weapon) {
					
					// If the weapon is currently equipped, unequip it.
					if(currPlayer.getEquippedWeapon() != null && currPlayer.getEquippedWeapon().name.equals(i.name)) {
						// Unequip item
						currPlayer.unequipWeapon();
						
						// Play equip sound.
						unequipWeapon.playSound(0.7f);
					}
					else {
						// Equip item
						i.equip();
						
						// Play equip sound.
						equipWeapon.playSound(0.7f);
					}
				}
				
				// Deal with bottles.
				if(i instanceof bottle) {
					
					// If the weapon is currently equipped, unequip it.
					if(currPlayer.getEquippedBottle() != null && currPlayer.getEquippedBottle().name.equals(i.name)) {
						// Unequip item
						currPlayer.unequipBottle();
						
						// Play equip sound.
						unequipWeapon.playSound(0.7f);
					}
					else {
						// Equip item
						i.equip();
						
						// Play equip sound.
						equipWeapon.playSound(0.7f);
					}
				}
				
			}
			else {
				// TODO: Play unequippable sound?
			}
		}
	}
	
	// Move the select around.
	public void moveSelect(String direction) {
		
		// Left
		if(direction=="left") {
			if(selectedSlot==0 ||
			(selectedSlot)/Math.sqrt(DEFAULT_INVENTORY_SIZE)==(int)((selectedSlot+1)/Math.sqrt(DEFAULT_INVENTORY_SIZE)));
			else {
				UIMove.playSound(0.7f);
				selectedSlot--;
			}
		}
		
		// Right
		if(direction=="right") {
			if((selectedSlot+1)/Math.sqrt(DEFAULT_INVENTORY_SIZE)==(int)((selectedSlot+1)/Math.sqrt(DEFAULT_INVENTORY_SIZE)));
			else {
				UIMove.playSound(0.7f);
				selectedSlot++;
			}
		}
		
		// Up
		if(direction=="up") {
			if(selectedSlot-Math.sqrt(DEFAULT_INVENTORY_SIZE) < 0);
			else {
				UIMove.playSound(0.7f);
				selectedSlot -= Math.sqrt(DEFAULT_INVENTORY_SIZE);
			}
		}
		
		// Down
		if(direction=="down") {
			if(selectedSlot + Math.sqrt(DEFAULT_INVENTORY_SIZE) >= DEFAULT_INVENTORY_SIZE);
			else {
				UIMove.playSound(0.7f);
				selectedSlot += Math.sqrt(DEFAULT_INVENTORY_SIZE);
			}
		}
	}
	
	// Respond to key press.
	public void respondToKeyPress(KeyEvent k) {
		// Player presses i (inventory) key.
		if(k.getKeyCode() == KeyEvent.VK_ESCAPE) { 
			toggleDisplay();
		}
		
		// Player presses left key.
		if(k.getKeyCode() == KeyEvent.VK_LEFT || k.getKeyCode() == KeyEvent.VK_A) { 
			moveSelect("left");
		}
		
		// Player presses right key.
		if(k.getKeyCode() == KeyEvent.VK_RIGHT || k.getKeyCode() == KeyEvent.VK_D) { 
			moveSelect("right");
		}
		
		// Player presses up key
		if(k.getKeyCode() == KeyEvent.VK_UP || k.getKeyCode() == KeyEvent.VK_W) { 
			moveSelect("up");
		}
		
		// Player presses down key
		if(k.getKeyCode() == KeyEvent.VK_DOWN || k.getKeyCode() == KeyEvent.VK_S) { 
			moveSelect("down");
		}
		
		// Player presses e key.
		if(k.getKeyCode() == KeyEvent.VK_E || k.getKeyCode() == KeyEvent.VK_SPACE) { 
			equipSelectedItem();
		}
	}

	// Draw the inventory.
	@Override
	public void drawObject(Graphics g) {
		if(isDisplayOn()) {
			// Set font.
			g.setFont(drawnObject.DEFAULT_FONT);
			
			// Draw the inventory background.
			g.drawImage(inventoryBackground, 
					getX(), 
					getY(), 
					inventoryBackground.getWidth(), 
					inventoryBackground.getHeight(), 
					null);
			
			// Adjustment for inventory background.
			int adjustX = 36;
			int adjustY = 50;
			
			// Draw each slot and an item in it if we have one.
			int x = 0;
			for(int i = 0; i < Math.sqrt(DEFAULT_INVENTORY_SIZE); i++) {
				for(int j = 0; j < Math.sqrt(DEFAULT_INVENTORY_SIZE); j++) {
					
					// Draw the slot.
					g.setColor(DEFAULT_SLOT_COLOR);
					g.drawRect(getX() + i*DEFAULT_SLOT_SIZE + adjustX, 
							getY() + j*DEFAULT_SLOT_SIZE + adjustY, 
							   DEFAULT_SLOT_SIZE, 
							   DEFAULT_SLOT_SIZE);
					
					// Draw the slot background.
					g.setColor(DEFAULT_SLOT_BACKGROUND_COLOR);
					g.fillRect(getX() + j*DEFAULT_SLOT_SIZE+1 + adjustX, 
							   getY() + i*DEFAULT_SLOT_SIZE+1 + adjustY, 
							   DEFAULT_SLOT_SIZE-1, 
							   DEFAULT_SLOT_SIZE-1);
					
					// If the slot is selected, then mark it.
					if(selectedSlot == i*Math.sqrt(DEFAULT_INVENTORY_SIZE) + j) {
						
						// Draw the yellow background for the selected slot.
						g.setColor(DEFAULT_SELECTED_SLOT_COLOR);
						g.fillRect(getX() + j*DEFAULT_SLOT_SIZE+1 + adjustX, 
								   getY() + i*DEFAULT_SLOT_SIZE+1 + adjustY, 
								   DEFAULT_SLOT_SIZE-1, 
								   DEFAULT_SLOT_SIZE-1);
						
					}
					
					// Draw the item, if it exists.
					if(x < items.size()) {
						
						// Draw the item, if it exists.
						item currentItem = items.get(x);
						g.setColor(DEFAULT_TEXT_COLOR);
						g.drawImage(currentItem.getImage(), 
								getX() + j*DEFAULT_SLOT_SIZE + DEFAULT_SLOT_SIZE/2 - currentItem.getImage().getWidth()/2 + adjustX, 
								getY() + i*DEFAULT_SLOT_SIZE + DEFAULT_SLOT_SIZE/2 - currentItem.getImage().getHeight()/2 + adjustY, 
								currentItem.getImage().getWidth(), 
								currentItem.getImage().getHeight(), 
								null);
						
						// Set color
						g.setColor(DEFAULT_SLOT_COLOR);
						
						
						if(selectedSlot == i*Math.sqrt(DEFAULT_INVENTORY_SIZE) + j) {

							// Draw the item information on the right
							String weaponName = stringUtils.toTitleCase(currentItem.name);
							g.drawString(weaponName, getX() + (int) (Math.sqrt(DEFAULT_INVENTORY_SIZE)*DEFAULT_SLOT_SIZE) + 38 + adjustX - g.getFontMetrics().stringWidth(weaponName)/2, getY()+ 34 + adjustY);
							
							// Draw weapon information.
							if(currentItem instanceof weapon) {
								g.setColor(DEFAULT_DESC_COLOR);
								weapon currentWeapon = (weapon)currentItem;
								g.drawString("Damage: " + currentWeapon.getAttackDamage(), getX() + (int) (Math.sqrt(DEFAULT_INVENTORY_SIZE)*DEFAULT_SLOT_SIZE) + 38 + adjustX - g.getFontMetrics().stringWidth("Damage: " + currentWeapon.getAttackDamage())/2, getY()+ 34 + adjustY + 20);
								g.drawString("Speed: " + currentWeapon.getSpeed(), getX() + (int) (Math.sqrt(DEFAULT_INVENTORY_SIZE)*DEFAULT_SLOT_SIZE) + 38 + adjustX - g.getFontMetrics().stringWidth("Speed: " + currentWeapon.getSpeed())/2, getY()+ 34 + adjustY + 34);
								g.drawString("Range: " + currentWeapon.getRange(), getX() + (int) (Math.sqrt(DEFAULT_INVENTORY_SIZE)*DEFAULT_SLOT_SIZE) + 38 + adjustX - g.getFontMetrics().stringWidth("Range: " + currentWeapon.getRange())/2, getY()+ 34 + adjustY + 48);
								
								// Press e to equip.
								g.drawString(DEFAULT_BOTTOM_TEXT, getX() + 38 - g.getFontMetrics().stringWidth(DEFAULT_BOTTOM_TEXT)/2 + (int) (Math.sqrt(DEFAULT_INVENTORY_SIZE)*DEFAULT_SLOT_SIZE) + adjustX, getY()+ 34 + adjustY + 140);
							}
							
							// Draw bottle information.
							if(currentItem instanceof bottle) {
								g.setColor(DEFAULT_DESC_COLOR);
								bottle currentBottle = (bottle)currentItem;
								g.drawString("Charges: " + currentBottle.getChargesLeft(), getX() + (int) (Math.sqrt(DEFAULT_INVENTORY_SIZE)*DEFAULT_SLOT_SIZE) + 38 + adjustX - g.getFontMetrics().stringWidth("Charges: " + currentBottle.getChargesLeft())/2, getY()+ 34 + adjustY + 20);
								g.drawString("Max Charges: " + currentBottle.getMaxCharges(), getX() + (int) (Math.sqrt(DEFAULT_INVENTORY_SIZE)*DEFAULT_SLOT_SIZE) + 38 + adjustX - g.getFontMetrics().stringWidth("Max Charges: " + currentBottle.getMaxCharges())/2, getY()+ 34 + adjustY + 34);
								g.drawString("Heal: " + (int)(currentBottle.getHealPercent()*100) + "%", getX() + (int) (Math.sqrt(DEFAULT_INVENTORY_SIZE)*DEFAULT_SLOT_SIZE) + 38 + adjustX - g.getFontMetrics().stringWidth("Heal: " + (int)(currentBottle.getHealPercent()*100) + "%")/2, getY()+ 34 + adjustY + 48);
								
								// Press e to equip.
								g.drawString(DEFAULT_BOTTOM_TEXT, getX() + 38 - g.getFontMetrics().stringWidth(DEFAULT_BOTTOM_TEXT)/2 + (int) (Math.sqrt(DEFAULT_INVENTORY_SIZE)*DEFAULT_SLOT_SIZE) + adjustX, getY()+ 34 + adjustY + 140);
							}
						
						}
						x++;
					}
				}
			}
			
			// Draw the equipped weapon.
			g.setColor(DEFAULT_TEXT_COLOR);
			g.drawString("Weapon",
					   getX() + adjustX,
					   getY()  + adjustY + (int) (DEFAULT_SLOT_SIZE*Math.sqrt(DEFAULT_INVENTORY_SIZE)+23));
			
			// Draw the slot.
			g.setColor(DEFAULT_SLOT_COLOR);
			g.drawRect(getX() + DEFAULT_SLOT_SIZE/2 + 35 + adjustX, 
					   getY() + (int) (DEFAULT_SLOT_SIZE*Math.sqrt(DEFAULT_INVENTORY_SIZE)+19 - DEFAULT_SLOT_SIZE/2) + adjustY, 
					   DEFAULT_SLOT_SIZE, 
					   DEFAULT_SLOT_SIZE);
			
			// Draw slot background.
			g.setColor(DEFAULT_SLOT_BACKGROUND_COLOR);
			g.fillRect(getX() + DEFAULT_SLOT_SIZE/2 + 35 + adjustX+1, 
						getY() + (int) (DEFAULT_SLOT_SIZE*Math.sqrt(DEFAULT_INVENTORY_SIZE)+19 - DEFAULT_SLOT_SIZE/2) + adjustY + 1, 
					   DEFAULT_SLOT_SIZE-1, 
					   DEFAULT_SLOT_SIZE-1);
			
			// Draw the weapon.
			if(player.getCurrentPlayer().getEquippedWeapon() != null) {
				g.drawImage(player.getCurrentPlayer().getEquippedWeapon().getImage(), 
						getX() + DEFAULT_SLOT_SIZE + 35 - player.getCurrentPlayer().getEquippedWeapon().getImage().getWidth()/2 + adjustX, 
						getY() + (int) (DEFAULT_SLOT_SIZE*Math.sqrt(DEFAULT_INVENTORY_SIZE)+19) - player.getCurrentPlayer().getEquippedWeapon().getImage().getHeight()/2 + adjustY, 
						player.getCurrentPlayer().getEquippedWeapon().getImage().getWidth(), 
						player.getCurrentPlayer().getEquippedWeapon().getImage().getHeight(), 
						null);
			}
			
			// Draw the equipped potion.
			g.setColor(DEFAULT_TEXT_COLOR);
			g.drawString("Bottle",
					   getX() + adjustX,
					   getY() + (int) (DEFAULT_SLOT_SIZE*Math.sqrt(DEFAULT_INVENTORY_SIZE)+56) + adjustY);
			
			// Draw the slot.
			g.setColor(DEFAULT_SLOT_COLOR);
			g.drawRect(getX() + DEFAULT_SLOT_SIZE/2 + 35 + adjustX, 
					   getY() + (int) (DEFAULT_SLOT_SIZE*Math.sqrt(DEFAULT_INVENTORY_SIZE)+53 - DEFAULT_SLOT_SIZE/2) + adjustY, 
					   DEFAULT_SLOT_SIZE, 
					   DEFAULT_SLOT_SIZE);
			
			// Draw slot background.
			g.setColor(DEFAULT_SLOT_BACKGROUND_COLOR);
			g.fillRect(getX() + DEFAULT_SLOT_SIZE/2 + 35 + adjustX+1, 
						getY() + (int) (DEFAULT_SLOT_SIZE*Math.sqrt(DEFAULT_INVENTORY_SIZE)+53 - DEFAULT_SLOT_SIZE/2) + adjustY + 1, 
					   DEFAULT_SLOT_SIZE-1, 
					   DEFAULT_SLOT_SIZE-1);
			
			// Draw the potion
			if(player.getCurrentPlayer().getEquippedBottle() != null) {
				g.drawImage(player.getCurrentPlayer().getEquippedBottle().getImage(), 
						getX() + DEFAULT_SLOT_SIZE + 35 - player.getCurrentPlayer().getEquippedBottle().getImage().getWidth()/2 + adjustX, 
						getY() + (int) (DEFAULT_SLOT_SIZE*Math.sqrt(DEFAULT_INVENTORY_SIZE)+53) - player.getCurrentPlayer().getEquippedBottle().getImage().getHeight()/2 + adjustY, 
						player.getCurrentPlayer().getEquippedBottle().getImage().getWidth(), 
						player.getCurrentPlayer().getEquippedBottle().getImage().getHeight(), 
						null);
			}
		}
	}
	
	////////////////////////////
	/// GETTERS AND SETTERS ////
	////////////////////////////

	public boolean isDisplayOn() {
		return displayOn;
	}

	public void setDisplayOn(boolean displayOn) {
		this.displayOn = displayOn;
	}
	
	public item get(int i) {
		return items.get(i);
	}
	
	public int size() {
		return items.size();
	}
	
	public void add(item i) {
		items.add(i);
	}
}