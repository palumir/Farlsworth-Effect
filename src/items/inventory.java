package items;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import UI.interfaceObject;
import UI.tooltipString;
import drawing.gameCanvas;
import drawing.spriteSheet;
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
	public static int DEFAULT_INVENTORY_START_X = (int)(gameCanvas.getScaleX()*20);
	public static int DEFAULT_INVENTORY_START_Y = (int)(gameCanvas.getScaleY()*gameCanvas.getDefaultHeight()-420);
	
	// Inventory size.
	public static int DEFAULT_INVENTORY_SIZE = 16; // total number of slots. must be a perfect root of 2
	public static int DEFAULT_SLOT_SIZE = 30; // size in terms of the actual physical size on the screen.
	
	// Strings.
	public static String DEFAULT_EMPTY_SLOT = "Empty";
	public static String DEFAULT_EQUIP_TEXT = "Press \'e\' to equip";
	public static String DEFAULT_UNEQUIP_TEXT = "Press \'e\' to unequip";
	
	// Colors
	public static Color DEFAULT_SLOT_COLOR = new Color(52,41,36);
	public static Color DEFAULT_SLOT_BACKGROUND_COLOR = new Color(64,48,38);
	public static Color DEFAULT_TEXT_COLOR = Color.white;
	public static Color DEFAULT_SELECTED_SLOT_COLOR = new Color(122,96,84);
	public static Color DEFAULT_DESC_COLOR = Color.black;
	
	//////////////
	/// FIELDS ///
	//////////////
	
	// List of active inventory slots
	public static ArrayList<Integer> activeSlotKeys = new ArrayList<Integer>() {{
		add(KeyEvent.VK_ENTER);
		add(KeyEvent.VK_SPACE);
		add(KeyEvent.VK_SHIFT);
		add(KeyEvent.VK_1);
		add(KeyEvent.VK_2);
		add(KeyEvent.VK_3);
		add(KeyEvent.VK_4);
		add(KeyEvent.VK_5);
		add(KeyEvent.VK_6);
		add(KeyEvent.VK_7);
		add(KeyEvent.VK_8);
		add(KeyEvent.VK_9);
		add(KeyEvent.VK_0);
	}};
	
	public ArrayList<item> activeSlots = new ArrayList<item>();
	
	// The actual items
	private ArrayList<item> items;
	
	// Selected slot
	private int selectedSlot = 0;
	
	// Display inventory on screen?
	private boolean displayOn = false;
	
	// Sounds for inventory.
	private String openInventory;
	private String closeInventory;
	private String equipWeapon;
	private String unequipWeapon;
	private String UIMove;
	
	///////////////
	/// METHODS ///
	///////////////
	public inventory() {
		super(null, DEFAULT_INVENTORY_START_X, DEFAULT_INVENTORY_START_Y, 0, 0);
		setItems(new ArrayList<item>());
		
		// Set sounds.
		openInventory = "sounds/effects/player/UI/openInventory.wav";
		closeInventory = "sounds/effects/player/UI/closeInventory.wav";
		equipWeapon = "sounds/effects/player/UI/equipItem.wav";
		unequipWeapon = "sounds/effects/player/UI/unequipItem.wav";
		UIMove = "sounds/effects/player/UI/UIMove.wav";
	}
	
	// Toggle inventory display.
	public void toggleDisplay() {
		setDisplayOn(!isDisplayOn());
		if(displayOn) {
			
			// Stop the player
			player.getPlayer().stop();
			
			sound s = new sound(openInventory);
			s.start();
		}
		else {
			
			sound s = new sound(closeInventory);
			s.start();
		}
	}
	
	// Search for key.
	public boolean hasKey(String s) {
		if(getItems() != null) {
			for(int j = 0; j < getItems().size(); j++) {
				if(getItems().get(j) != null 
					&& getItems().get(j) instanceof key 
					&& ((key)getItems().get(j)).getName().equals(s)) return true;
			}
		}
		return false;
	}
	
	// Pickup an item into inventory.
	public void pickUp(item i) {
		if(!hasItem(i)) {
			getItems().add(i);
		}
	}
	
	// Drop an item from inventory.
	public void drop(item i) {
		if(hasItem(i)) {
			getItems().remove(i);
		}
	}
	
	// Check if inventory has item with the same name.
	public boolean hasItem(item i) {
		if(getItems() != null) {
			for(int j = 0; j < getItems().size(); j++) {
				if((getItems().get(j) != null  && 
						getItems().get(j).getName().equals(i.getName()) &&
						getItems().get(j).getIntX() == i.getIntX() &&
						getItems().get(j).getIntY() == i.getIntY() &&
						getItems().get(j).discoverZone.equals(i.discoverZone))) return true;
			}
		}
		if((i instanceof bottleShard && 
		containsBottleType(((bottleShard)i).getBottleType()))) return true;
		return false;
	}
	
	// Contains bottle type?
	public boolean containsBottleType(Class c) {
		for(int i = 0; i < getItems().size(); i++) {
			if(items.get(i).getClass().equals(c)) return true;
		}
		return false;
	}
	
	// Waiting to equip item?
	private boolean waitingToEquipItem = false;
	private item itemToEquip;
	
	// Interact with the current selected item.
	public void equipSelectedItem() {
		
		// We aren't trying to equip nothing.
		if(selectedSlot < getItems().size()) {
			
			// Make sure it's equippable.
			if(getItems().get(selectedSlot).equippable) {
				
				// Get the item.
				item i = getItems().get(selectedSlot);
				
				// If the weapon is currently unequipped, equip it.
				if(i.slot == KeyEvent.VK_WINDOWS) {
					
					// Display a tooltip.
					new tooltipString("Enter a slot to equip the item (enter, space, shift, 1-9).");
					waitingToEquipItem = true;
					itemToEquip = i;
				}
				else {
					unequipItem(i);
				}
				
			}
			else {
				// TODO: Play unequippable sound?
			}
		}
	}
	
	// Unequip item
	public void unequipItem(item i) {
		
		// Play equip sound.
		sound s = new sound(unequipWeapon);
		s.start();
		
		// Actually unequip it.
		i.slot = KeyEvent.VK_WINDOWS;
		activeSlots.remove(i);
		
		// Tooltip.
		//new tooltipString("Item unequipped.");
	}
	
	// Equip item to slot
	public void equipItemToSlot(int key) {
		
		// Play equip sound.
		sound s = new sound(equipWeapon);
		s.start();
		
		// Equip it.
		equipItem(itemToEquip,key);
		
		// Stop waiting.
		waitingToEquipItem = false;
	}
	
	// Actually equip item
	public void equipItem(item i, int key) {
		i.slot = key;
		activeSlots.add(i);
	}
	
	// Move the select around.
	public void moveSelect(String direction) {
		
		// Left
		if(direction=="left") {
			if(selectedSlot==0 ||
			(selectedSlot)/Math.sqrt(DEFAULT_INVENTORY_SIZE)==(int)((selectedSlot+1)/Math.sqrt(DEFAULT_INVENTORY_SIZE)));
			else {
				sound s = new sound(UIMove);
				s.start();
				selectedSlot--;
			}
		}
		
		// Right
		if(direction=="right") {
			if((selectedSlot+1)/Math.sqrt(DEFAULT_INVENTORY_SIZE)==(int)((selectedSlot+1)/Math.sqrt(DEFAULT_INVENTORY_SIZE)));
			else {
				sound s = new sound(UIMove);
				s.start();
				selectedSlot++;
			}
		}
		
		// Up
		if(direction=="up") {
			if(selectedSlot-Math.sqrt(DEFAULT_INVENTORY_SIZE) < 0);
			else {
				sound s = new sound(UIMove);
				s.start();
				selectedSlot -= Math.sqrt(DEFAULT_INVENTORY_SIZE);
			}
		}
		
		// Down
		if(direction=="down") {
			if(selectedSlot + Math.sqrt(DEFAULT_INVENTORY_SIZE) >= DEFAULT_INVENTORY_SIZE);
			else {
				sound s = new sound(UIMove);
				s.start();
				selectedSlot += Math.sqrt(DEFAULT_INVENTORY_SIZE);
			}
		}
	}
	
	// Respond to key press.
	public void respondToKeyPress(KeyEvent k) {
		
		if(waitingToEquipItem) {
			
			// We're good, equip the item.
			if(activeSlotKeys.contains(k.getKeyCode())) {
				equipItemToSlot(k.getKeyCode());
			}
			else {
				new tooltipString("You didn't enter a correct slot. Quit joshing me hard.");
				waitingToEquipItem = false;
			}
		}
		else { 
			// Player presses i (inventory) key.
			if(k.getKeyCode() == KeyEvent.VK_ESCAPE) { 
				toggleDisplay();
			}
			
			// Player presses left key.
			if(k.getKeyCode() == KeyEvent.VK_A) { 
				moveSelect("left");
			}
			
			// Player presses right key.
			if(k.getKeyCode() == KeyEvent.VK_D) { 
				moveSelect("right");
			}
			
			// Player presses up key
			if(k.getKeyCode() == KeyEvent.VK_W) { 
				moveSelect("up");
			}
			
			// Player presses down key
			if(k.getKeyCode() == KeyEvent.VK_S) { 
				moveSelect("down");
			}
			
			// Player presses e key.
			if(k.getKeyCode() == KeyEvent.VK_E || k.getKeyCode() == KeyEvent.VK_SPACE || k.getKeyCode() == KeyEvent.VK_ENTER) { 
				equipSelectedItem();
			}
		}
	}

	// Draw the inventory.
	@Override
	public void drawObject(Graphics g) {
		if(isDisplayOn()) {
			
			Font currentFont = g.getFont();
			Font DEFAULT_FONT = currentFont;
			Font DEFAULT_FONT_TITLE = currentFont.deriveFont(currentFont.getSize() * 1.15F);
			
			// Set font.
			g.setFont(DEFAULT_FONT);
			
			// Draw the inventory background.
			g.drawImage(inventoryBackground, 
					(int)(gameCanvas.getScaleX()*getIntX()), 
					(int)(gameCanvas.getScaleY()*getIntY()), 
					(int)(gameCanvas.getScaleX()*inventoryBackground.getWidth()), 
					(int)(gameCanvas.getScaleY()*inventoryBackground.getHeight()), 
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
					g.drawRect((int)(gameCanvas.getScaleX()*(getIntX() + i*DEFAULT_SLOT_SIZE + adjustX)), 
							(int)(gameCanvas.getScaleY()*(getIntY() + j*DEFAULT_SLOT_SIZE + adjustY)), 
							(int)(gameCanvas.getScaleX()*DEFAULT_SLOT_SIZE), 
							(int)(gameCanvas.getScaleY()*DEFAULT_SLOT_SIZE));
					
					// Draw the slot background.
					g.setColor(DEFAULT_SLOT_BACKGROUND_COLOR);
					g.fillRect((int)(gameCanvas.getScaleX()*(getIntX() + j*DEFAULT_SLOT_SIZE + adjustX) + 1), 
							(int)(gameCanvas.getScaleY()*(getIntY() + i*DEFAULT_SLOT_SIZE + adjustY) + 1), 
							(int)(gameCanvas.getScaleX()*(DEFAULT_SLOT_SIZE)-1), 
							(int)(gameCanvas.getScaleY()*(DEFAULT_SLOT_SIZE))-1);
					
					// If the slot is selected, then mark it.
					if(selectedSlot == i*Math.sqrt(DEFAULT_INVENTORY_SIZE) + j) {
						
						// Draw the yellow background for the selected slot.
						g.setColor(DEFAULT_SELECTED_SLOT_COLOR);
						g.fillRect((int)(gameCanvas.getScaleX()*(getIntX() + j*DEFAULT_SLOT_SIZE + adjustX) +1), 
								(int)(gameCanvas.getScaleY()*(getIntY() + i*DEFAULT_SLOT_SIZE + adjustY) +1), 
								(int)(gameCanvas.getScaleX()*(DEFAULT_SLOT_SIZE)-1), 
								(int)(gameCanvas.getScaleY()*(DEFAULT_SLOT_SIZE)-1));
						
					}
					
					// Draw the item, if it exists.
					if(x < getItems().size()) {
						
						// Draw the item, if it exists.
						item currentItem = getItems().get(x);
						g.setColor(DEFAULT_TEXT_COLOR);
						g.drawImage(currentItem.getImage(), 
								(int)(gameCanvas.getScaleX()*(getIntX() + j*DEFAULT_SLOT_SIZE + DEFAULT_SLOT_SIZE/2 - currentItem.getImage().getWidth()/2 + adjustX)), 
								(int)(gameCanvas.getScaleY()*(getIntY() + i*DEFAULT_SLOT_SIZE + DEFAULT_SLOT_SIZE/2 - currentItem.getImage().getHeight()/2 + adjustY)), 
								(int)(gameCanvas.getScaleX()*(currentItem.getImage().getWidth())), 
								(int)(gameCanvas.getScaleY()*(currentItem.getImage().getHeight())), 
								null);
						
						// Set color
						g.setColor(DEFAULT_SLOT_COLOR);
						
						
						if(selectedSlot == i*Math.sqrt(DEFAULT_INVENTORY_SIZE) + j) {
							
							// Selected slot text adjustment
							int selectedSlotTextAdjustX = 45;

							// Draw the item information on the right
							g.setFont(DEFAULT_FONT_TITLE);
							String itemName = stringUtils.toTitleCase(currentItem.getName());
							g.drawString(itemName, (int)(gameCanvas.getScaleX()*(getIntX() + (int) (Math.sqrt(DEFAULT_INVENTORY_SIZE)*DEFAULT_SLOT_SIZE) + selectedSlotTextAdjustX + adjustX)) - g.getFontMetrics().stringWidth(itemName)/2, 
									(int)(gameCanvas.getScaleY()*(getIntY()+ 34 + adjustY)));
							
							// Draw weapon information.
							g.setFont(DEFAULT_FONT);
							
							// Draw bottle information.
							if(currentItem instanceof bottle) {
								bottle currentBottle = (bottle)currentItem;
								g.drawString("Charges: " + currentBottle.getChargesLeft(), 
										(int)(gameCanvas.getScaleX()*(getIntX() + (int) (Math.sqrt(DEFAULT_INVENTORY_SIZE)*DEFAULT_SLOT_SIZE) + selectedSlotTextAdjustX + adjustX)) - g.getFontMetrics().stringWidth("Charges: " + currentBottle.getChargesLeft())/2, 
										(int)(gameCanvas.getScaleY()*(getIntY()+ 34 + adjustY + 20)));
								g.drawString("Max Charges: " + currentBottle.getMaxCharges(), 
										(int)(gameCanvas.getScaleX()*(getIntX() + (int) (Math.sqrt(DEFAULT_INVENTORY_SIZE)*DEFAULT_SLOT_SIZE) + selectedSlotTextAdjustX + adjustX)) - g.getFontMetrics().stringWidth("Max Charges: " + currentBottle.getMaxCharges())/2, 
										(int)(gameCanvas.getScaleY()*(getIntY()+ 34 + adjustY + 34)));
								if(currentBottle.description!=null) g.drawString(currentBottle.description, 
										(int)(gameCanvas.getScaleX()*(getIntX() + (int) (Math.sqrt(DEFAULT_INVENTORY_SIZE)*DEFAULT_SLOT_SIZE) + selectedSlotTextAdjustX + adjustX)) - g.getFontMetrics().stringWidth(currentBottle.description)/2, 
										(int)(gameCanvas.getScaleY()*(getIntY()+ 34 + adjustY + 48)));
							}
							
							// Press e to equip/dequip.
							if(currentItem.equippable && 
								!activeSlots.contains(currentItem)) {
								g.drawString(DEFAULT_EQUIP_TEXT, 
										(int)(gameCanvas.getScaleX()*(getIntX() + selectedSlotTextAdjustX + (int) (Math.sqrt(DEFAULT_INVENTORY_SIZE)*DEFAULT_SLOT_SIZE) + adjustX)) - g.getFontMetrics().stringWidth(DEFAULT_EQUIP_TEXT)/2, 
										(int)(gameCanvas.getScaleY()*(getIntY()+ 34 + adjustY + 140)));
							}
							else if(currentItem.equippable && activeSlots.contains(currentItem)){
								g.drawString(DEFAULT_UNEQUIP_TEXT, 
										(int)(gameCanvas.getScaleX()*(getIntX() + selectedSlotTextAdjustX + (int) (Math.sqrt(DEFAULT_INVENTORY_SIZE)*DEFAULT_SLOT_SIZE) + adjustX)) - g.getFontMetrics().stringWidth(DEFAULT_UNEQUIP_TEXT)/2, 
										(int)(gameCanvas.getScaleY()*(getIntY()+ 34 + adjustY + 140)));
							}
						}
						x++;
					}
				}
			}
			
			
			// Draw the equipped potion.
			g.setColor(DEFAULT_TEXT_COLOR);
			g.drawString("Bottle",
					   (int)(gameCanvas.getScaleX()*(getIntX() + adjustX)),
					   (int)(gameCanvas.getScaleY()*(getIntY() + 56 + adjustY + (int) (DEFAULT_SLOT_SIZE*Math.sqrt(DEFAULT_INVENTORY_SIZE)))));
			
			// Draw the slot.
			g.setColor(DEFAULT_SLOT_COLOR);
			g.drawRect((int)(gameCanvas.getScaleX()*(getIntX() + DEFAULT_SLOT_SIZE/2 + 35 + adjustX)), 
					   (int)(gameCanvas.getScaleY()*(getIntY() + (int) (DEFAULT_SLOT_SIZE*Math.sqrt(DEFAULT_INVENTORY_SIZE)+53 - DEFAULT_SLOT_SIZE/2) + adjustY)), 
					   (int)(gameCanvas.getScaleX()*(DEFAULT_SLOT_SIZE)), 
					   (int)(gameCanvas.getScaleY()*(DEFAULT_SLOT_SIZE)));
			
			// Draw slot background.
			g.setColor(DEFAULT_SLOT_BACKGROUND_COLOR);
			g.fillRect((int)(gameCanvas.getScaleX()*(getIntX() + DEFAULT_SLOT_SIZE/2 + 35 + adjustX)+1), 
					   (int)(gameCanvas.getScaleY()*(getIntY() + (int) (DEFAULT_SLOT_SIZE*Math.sqrt(DEFAULT_INVENTORY_SIZE)+53 - DEFAULT_SLOT_SIZE/2) + adjustY)+1), 
					   (int)(gameCanvas.getScaleX()*(DEFAULT_SLOT_SIZE)-1), 
					   (int)(gameCanvas.getScaleY()*(DEFAULT_SLOT_SIZE)-1));
			
			// Draw the potion
			if(player.getPlayer().getEquippedBottle() != null) {
				g.drawImage(player.getPlayer().getEquippedBottle().getImage(), 
						(int)(gameCanvas.getScaleX()*(getIntX() + DEFAULT_SLOT_SIZE + 35 - player.getPlayer().getEquippedBottle().getImage().getWidth()/2 + adjustX)), 
						(int)(gameCanvas.getScaleY()*(getIntY() + (int) (DEFAULT_SLOT_SIZE*Math.sqrt(DEFAULT_INVENTORY_SIZE)+53) - player.getPlayer().getEquippedBottle().getImage().getHeight()/2 + adjustY)), 
						(int)(gameCanvas.getScaleX()*(player.getPlayer().getEquippedBottle().getImage().getWidth())), 
						(int)(gameCanvas.getScaleY()*(player.getPlayer().getEquippedBottle().getImage().getHeight())), 
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
	
	public item get(String i) {
		if(getItems() != null) {
			for(int j = 0; j < getItems().size(); j++) {
				if(getItems().get(j) != null  && getItems().get(j).getName().equals(i)) return getItems().get(j);
			}
		}
		return null;
	}
	
	public item get(int i) {
		return getItems().get(i);
	}
	
	public int size() {
		return getItems().size();
	}
	
	public void add(item i) {
		i.setUpItem();
		getItems().add(i);
	}
	
	public void remove(int i) {
		getItems().remove(i);
	}

	public ArrayList<item> getItems() {
		return items;
	}

	public void setItems(ArrayList<item> items) {
		this.items = items;
	}
}