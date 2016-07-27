package items;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import drawing.drawnObject;
import drawing.gameCanvas;
import drawing.spriteSheet;
import drawing.userInterface.interfaceObject;
import drawing.userInterface.tooltipString;
import interactions.event;
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
	private String openInventory;
	private String closeInventory;
	private String equipWeapon;
	private String unequipWeapon;
	private String UIMove;
	
	// Have they been given the pressSpaceToAttack message?
	private static event pressSpaceToAttack;
	
	///////////////
	/// METHODS ///
	///////////////
	public inventory() {
		super(null, DEFAULT_INVENTORY_START_X, DEFAULT_INVENTORY_START_Y, 0, 0);
		setItems(new ArrayList<item>());
		
		// Create the event
		pressSpaceToAttack = new event("inventoryPressSpaceToAttack");
		
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
			sound s = new sound(openInventory);
			s.start();
		}
		else { 
			
			// Set pressSpaceToAttack to be true.
			if(!pressSpaceToAttack.isCompleted() && player.getPlayer().getEquippedWeapon().name.equals("Dagger")) {
				pressSpaceToAttack.setCompleted(true);
				tooltipString t = new tooltipString("Press or hold 'space' to attack.");
			}
			
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
					&& ((key)getItems().get(j)).name.equals(s)) return true;
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
	
	// Check if inventory has item with the same name.
	public boolean hasItem(item i) {
		if(getItems() != null) {
			for(int j = 0; j < getItems().size(); j++) {
				if(getItems().get(j) != null  && getItems().get(j).name.equals(i.name)) return true;
			}
		}
		return false;
	}
	
	// Interact with the current selected item.
	public void equipSelectedItem() {
		
		// We aren't trying to equip nothing.
		if(selectedSlot < getItems().size()) {
			
			// Make sure it's equippable.
			if(getItems().get(selectedSlot).equippable) {
				
				// Get the item.
				item i = getItems().get(selectedSlot);
				player currPlayer = player.getPlayer();
				
				// Deal with weapons.
				if(i instanceof weapon) {
					
					// If the weapon is currently equipped, unequip it.
					if(currPlayer.getEquippedWeapon() != null && currPlayer.getEquippedWeapon().name.equals(i.name)) {
						// Unequip item
						currPlayer.unequipWeapon();
						
						// Play equip sound.
						sound s = new sound(unequipWeapon);
						s.start();
					}
					else {
						// Equip item
						i.getItemRef().equip();
						
						// Play equip sound.
						sound s = new sound(equipWeapon);
						s.start();
					}
				}
				
				// Deal with bottles.
				if(i instanceof bottle) {
					
					// If the weapon is currently equipped, unequip it.
					if(currPlayer.getEquippedBottle() != null && currPlayer.getEquippedBottle().name.equals(i.name)) {
						// Unequip item
						currPlayer.unequipBottle();
						
						// Play equip sound.
						sound s = new sound(unequipWeapon);
						s.start();
					}
					else {
						// Equip item
						i.getItemRef().equip();
						
						// Play equip sound.
						sound s = new sound(equipWeapon);
						s.start();
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
						item currentItem = getItems().get(x).getItemRef();
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
							String itemName = stringUtils.toTitleCase(currentItem.name);
							g.drawString(itemName, (int)(gameCanvas.getScaleX()*(getIntX() + (int) (Math.sqrt(DEFAULT_INVENTORY_SIZE)*DEFAULT_SLOT_SIZE) + selectedSlotTextAdjustX + adjustX)) - g.getFontMetrics().stringWidth(itemName)/2, 
									(int)(gameCanvas.getScaleY()*(getIntY()+ 34 + adjustY)));
							
							// Draw weapon information.
							g.setFont(DEFAULT_FONT);
							if(currentItem instanceof weapon) {
								g.setColor(DEFAULT_DESC_COLOR);
								weapon currentWeapon = (weapon)currentItem;
								g.drawString("Damage: " + currentWeapon.getAttackDamage(), (int)(gameCanvas.getScaleX()*(getIntX() + (int) (Math.sqrt(DEFAULT_INVENTORY_SIZE)*DEFAULT_SLOT_SIZE) + selectedSlotTextAdjustX + adjustX)) - g.getFontMetrics().stringWidth("Damage: " + currentWeapon.getAttackDamage())/2, 
										(int)(gameCanvas.getScaleY()*(getIntY()+ 34 + adjustY + 20)));
								g.drawString("Speed: " + currentWeapon.getSpeed(), (int)(gameCanvas.getScaleX()*(getIntX() + (int) (Math.sqrt(DEFAULT_INVENTORY_SIZE)*DEFAULT_SLOT_SIZE) + selectedSlotTextAdjustX + adjustX)) - g.getFontMetrics().stringWidth("Speed: " + currentWeapon.getSpeed())/2, 
										(int)(gameCanvas.getScaleY()*(getIntY()+ 34 + adjustY + 34)));
								g.drawString("Range: " + currentWeapon.getRange(), (int)(gameCanvas.getScaleX()*(getIntX() + (int) (Math.sqrt(DEFAULT_INVENTORY_SIZE)*DEFAULT_SLOT_SIZE) + selectedSlotTextAdjustX + adjustX)) - g.getFontMetrics().stringWidth("Range: " + currentWeapon.getRange())/2, 
										(int)(gameCanvas.getScaleY()*(getIntY()+ 34 + adjustY + 48)));
								g.drawString("Crit: " + ((int)(currentWeapon.critChance*100)) + "%", (int)(gameCanvas.getScaleX()*(getIntX() + (int) (Math.sqrt(DEFAULT_INVENTORY_SIZE)*DEFAULT_SLOT_SIZE) + selectedSlotTextAdjustX + adjustX)) - g.getFontMetrics().stringWidth("Crit: " + ((int)(currentWeapon.critChance*100)) + "%")/2, 
										(int)(gameCanvas.getScaleY()*(getIntY()+ 34 + adjustY + 48+14)));
								
								// Draw weapon properties
								int startY = getIntY()+ 34 + adjustY + 48 + 18;
								if(currentItem.properties != null) {
									g.drawString("Properties:", (int)(gameCanvas.getScaleX()*(getIntX() + (int) (Math.sqrt(DEFAULT_INVENTORY_SIZE)*DEFAULT_SLOT_SIZE) + selectedSlotTextAdjustX + adjustX)) - g.getFontMetrics().stringWidth("Properties:")/2, 
											(int)(gameCanvas.getScaleY()*(startY)));
									for(int n = 0; n < currentItem.properties.size(); n++) {
										startY += 14;
										g.drawString(currentItem.properties.get(n), (int)(gameCanvas.getScaleX()*(getIntX() + (int) (Math.sqrt(DEFAULT_INVENTORY_SIZE)*DEFAULT_SLOT_SIZE) + selectedSlotTextAdjustX + adjustX)) - g.getFontMetrics().stringWidth(currentItem.properties.get(n))/2, 
												(int)(gameCanvas.getScaleY()*(startY)));
									}
								}
								
								// Press e to equip.
								g.drawString(DEFAULT_BOTTOM_TEXT, (int)(gameCanvas.getScaleX()*(getIntX() + selectedSlotTextAdjustX + (int)(Math.sqrt(DEFAULT_INVENTORY_SIZE)*DEFAULT_SLOT_SIZE) + adjustX) - g.getFontMetrics().stringWidth(DEFAULT_BOTTOM_TEXT)/2), 
										(int)(gameCanvas.getScaleY()*(getIntY()+ 34 + adjustY + 140)));
							}
							
							// Draw bottle information.
							if(currentItem instanceof bottle) {
								bottle currentBottle = (bottle)currentItem;
								g.drawString("Charges: " + currentBottle.getChargesLeft(), 
										(int)(gameCanvas.getScaleX()*(getIntX() + (int) (Math.sqrt(DEFAULT_INVENTORY_SIZE)*DEFAULT_SLOT_SIZE) + selectedSlotTextAdjustX + adjustX)) - g.getFontMetrics().stringWidth("Charges: " + currentBottle.getChargesLeft())/2, 
										(int)(gameCanvas.getScaleY()*(getIntY()+ 34 + adjustY + 20)));
								g.drawString("Max Charges: " + currentBottle.getMaxCharges(), 
										(int)(gameCanvas.getScaleX()*(getIntX() + (int) (Math.sqrt(DEFAULT_INVENTORY_SIZE)*DEFAULT_SLOT_SIZE) + selectedSlotTextAdjustX + adjustX)) - g.getFontMetrics().stringWidth("Max Charges: " + currentBottle.getMaxCharges())/2, 
										(int)(gameCanvas.getScaleY()*(getIntY()+ 34 + adjustY + 34)));
								g.drawString("Heal: " + (int)(currentBottle.getHealPercent()*100) + "%", 
										(int)(gameCanvas.getScaleX()*(getIntX() + (int) (Math.sqrt(DEFAULT_INVENTORY_SIZE)*DEFAULT_SLOT_SIZE) + selectedSlotTextAdjustX + adjustX)) - g.getFontMetrics().stringWidth("Heal: " + (int)(currentBottle.getHealPercent()*100) + "%")/2, 
										(int)(gameCanvas.getScaleY()*(getIntY()+ 34 + adjustY + 48)));
								
								// Press e to equip.
								g.drawString(DEFAULT_BOTTOM_TEXT, 
										(int)(gameCanvas.getScaleX()*(getIntX() + selectedSlotTextAdjustX + (int) (Math.sqrt(DEFAULT_INVENTORY_SIZE)*DEFAULT_SLOT_SIZE) + adjustX)) - g.getFontMetrics().stringWidth(DEFAULT_BOTTOM_TEXT)/2, 
										(int)(gameCanvas.getScaleY()*(getIntY()+ 34 + adjustY + 140)));
							}
						
						}
						x++;
					}
				}
			}
			
			// Draw the equipped weapon.
			g.setColor(DEFAULT_TEXT_COLOR);
			g.drawString("Weapon",
					(int)(gameCanvas.getScaleX()*(getIntX() + adjustX)),
					(int)(gameCanvas.getScaleY()*(getIntY()  + adjustY + (int) (DEFAULT_SLOT_SIZE*Math.sqrt(DEFAULT_INVENTORY_SIZE)) +23)));
			
			// Draw the slot.
			g.setColor(DEFAULT_SLOT_COLOR);
			g.drawRect((int)(gameCanvas.getScaleX()*(getIntX() + DEFAULT_SLOT_SIZE/2 + 35 + adjustX)), 
					   (int)(gameCanvas.getScaleY()*(getIntY() + (int) (DEFAULT_SLOT_SIZE*Math.sqrt(DEFAULT_INVENTORY_SIZE)+19 - DEFAULT_SLOT_SIZE/2) + adjustY)), 
					   (int)(gameCanvas.getScaleX()*(DEFAULT_SLOT_SIZE)), 
					   (int)(gameCanvas.getScaleY()*(DEFAULT_SLOT_SIZE)));
			
			// Draw slot background.
			g.setColor(DEFAULT_SLOT_BACKGROUND_COLOR);
			g.fillRect((int)(gameCanvas.getScaleX()*(getIntX() + DEFAULT_SLOT_SIZE/2 + 35 + adjustX)+1), 
					   (int)(gameCanvas.getScaleY()*(getIntY() + (int) (DEFAULT_SLOT_SIZE*Math.sqrt(DEFAULT_INVENTORY_SIZE)+19 - DEFAULT_SLOT_SIZE/2) + adjustY)+1), 
					   (int)(gameCanvas.getScaleX()*(DEFAULT_SLOT_SIZE)-1), 
					   (int)(gameCanvas.getScaleY()*(DEFAULT_SLOT_SIZE)-1));
			
			// Draw the weapon.
			if(player.getPlayer().getEquippedWeapon() != null) {
				g.drawImage(player.getPlayer().getEquippedWeapon().getImage(), 
						(int)(gameCanvas.getScaleX()*(getIntX() + DEFAULT_SLOT_SIZE + 35 - player.getPlayer().getEquippedWeapon().getImage().getWidth()/2 + adjustX)), 
						(int)(gameCanvas.getScaleY()*(getIntY() + (int) (DEFAULT_SLOT_SIZE*Math.sqrt(DEFAULT_INVENTORY_SIZE)+19) - player.getPlayer().getEquippedWeapon().getImage().getHeight()/2 + adjustY)), 
						(int)(gameCanvas.getScaleX()*(player.getPlayer().getEquippedWeapon().getImage().getWidth())), 
						(int)(gameCanvas.getScaleY()*(player.getPlayer().getEquippedWeapon().getImage().getHeight())), 
						null);
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
	
	public item get(int i) {
		return getItems().get(i).getItemRef();
	}
	
	public int size() {
		return getItems().size();
	}
	
	public void add(item i) {
		i.setUpItem();
		getItems().add(i);
	}

	public ArrayList<item> getItems() {
		return items;
	}

	public void setItems(ArrayList<item> items) {
		this.items = items;
	}
}