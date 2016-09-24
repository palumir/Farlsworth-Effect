package utilities;

import java.awt.Point;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.ArrayList;

import UI.tooltipString;
import interactions.event;
import interactions.quest;
import items.bottle;
import items.inventory;
import items.item;
import units.player;

public class saveState implements Serializable {
	
	/////////////////////////
	////// DEFAULTS /////////
	/////////////////////////
	
	// Game saved text.
	public static String DEFAULT_GAME_SAVED_TEXT = "Game saved.";
	
	// Default saveFile
	private static String DEFAULT_SAVE_FILENAME = "save/gameData.save";

	///////////////////////
	////// FIELDS /////////
	///////////////////////
	
	/////////////////////////////////
	////// GLOBAL SAVE FIELDS////////
	/////////////////////////////////
	
	// Current zone name
	private String zoneName;
	
	// Position in current zone.
	private int playerX;
	private int playerY;
	
	// Choices
	private int chaosChoices;
	private int orderChoices;
	
	// Facing position
	private String facingDirection;
	
	// Player inventory
	private inventory playerInventory;
	private bottle equippedBottle;
	
	// Last well coordinates
	public Point lastWell;
	
	// Last save bottle coordinates
	public Point lastSaveBottle;
	
	// Save quietly?
	private static boolean quiet = false;
	
	// Events
	private ArrayList<event> allEvents;
	
	// Quests
	private ArrayList<String> currentQuests;

	////////////////////////
	////// METHODS /////////
	////////////////////////
	
	// Constructor
	public saveState() {
		// Does nothing.
	}
	
	// Save the game.
	public static void createSaveState() {
		try {
			if(player.playerLoaded && player.getPlayer()!= null) {
				
				// Display that we made a new savestate.
				if(!quiet) {
					tooltipString t = new tooltipString(DEFAULT_GAME_SAVED_TEXT);
				}
				
				// Create new saveState.
				saveState s = new saveState();
				
				// Load our player. 
				player currPlayer = player.getPlayer();
				s.setZoneName(currPlayer.getCurrentZone().getName());
				s.setPlayerX(currPlayer.getIntX());
				s.setPlayerY(currPlayer.getIntY());
				s.setFacingDirection(currPlayer.getFacingDirection());
				s.setPlayerInventory(currPlayer.getPlayerInventory());
				s.setEquippedBottle(currPlayer.getEquippedBottle());
				s.setCurrentQuests(quest.getCurrentQuests());
				
				// Save jokes.
				s.setAllEvents(event.loadedEvents);
				
				// Open the streams.
				FileOutputStream fileStream = new FileOutputStream(DEFAULT_SAVE_FILENAME);   
				ObjectOutputStream objectStream = new ObjectOutputStream(fileStream);  
				
				// Write the save state to the file.
				objectStream.writeObject(s.getZoneName());
				objectStream.writeObject(s.getPlayerX());
				objectStream.writeObject(s.getPlayerY());
				objectStream.writeObject(s.getFacingDirection());
				
				// Choices	
				objectStream.writeObject(player.getPlayer().chaosChoices);
				objectStream.writeObject(player.getPlayer().orderChoices);
				
				// Last well position
				Point lastWell = player.getPlayer().lastWell;
				
				if(lastWell == null) {
					objectStream.writeObject(false);
					objectStream.writeObject(0);
					objectStream.writeObject(0);
				}
				else {
					objectStream.writeObject(true);
					objectStream.writeObject((int)lastWell.getX());
					objectStream.writeObject((int)lastWell.getY());
				}
				
				// Last well position
				Point lastSaveBottle = player.getPlayer().lastSaveBottle;
				
				if(lastSaveBottle == null) {
					objectStream.writeObject(false);
					objectStream.writeObject(0);
					objectStream.writeObject(0);
				}
				else {
					objectStream.writeObject(true);
					objectStream.writeObject((int)lastSaveBottle.getX());
					objectStream.writeObject((int)lastSaveBottle.getY());
				}
				
				
				////////////////
				/// EVENTS   ///
				////////////////
				// Write the length of the coming array.
				int gagsSize = 0;
				if(s.getAllEvents() != null) gagsSize = s.getAllEvents().size();
				objectStream.writeObject(gagsSize); 
				
				// Write the inventory (names of items) to save file.
				for(int i = 0; i < gagsSize; i++) {
					objectStream.writeObject(s.getAllEvents().get(i).getName());
					objectStream.writeObject(s.getAllEvents().get(i).isCompleted());
				}
				
				/////////////////
				/// INVENTORY ///
				/////////////////
				// Write the length of the coming array.
				int inventorySize = 0;
				if(s.getPlayerInventory() != null) inventorySize = s.getPlayerInventory().size();
				objectStream.writeObject(inventorySize); 
				
				// Write the inventory (names of items) to save file.
				for(int i = 0; i < inventorySize; i++) {
					item currItem = s.getPlayerInventory().get(i);
					
					// Write the item name.
					objectStream.writeObject(currItem.getClass().getName());
					
					// Save zone
					objectStream.writeObject(currItem.discoverZone);
					
					// Save position.
					objectStream.writeObject(currItem.getIntX());
					objectStream.writeObject(currItem.getIntY());
					
					// Save the slot.
					objectStream.writeObject(currItem.slot);
					
					// For bottles, save the charges.
					if(currItem instanceof bottle) objectStream.writeObject(((bottle)currItem).getChargesLeft());
					
				}
				
				//////////////////////
				/// CURRENT QUESTS ///
				//////////////////////
				// Write the length of the coming array.
				int questsSize = 0;
				if(s.getCurrentQuests() != null) questsSize = s.getCurrentQuests().size();
				objectStream.writeObject(questsSize); 
				
				// Write the inventory (names of items) to save file.
				for(int i = 0; i < questsSize; i++) {
					objectStream.writeObject(s.getCurrentQuests().get(i));
				}
				
				// Close the streams.
			    objectStream.close();   
			    fileStream.close(); 
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			// Failed to save state.
		}
	}
	
	// Load the game
	public static saveState loadSaveState() {
		try {
			// Open the streams.
			FileInputStream fileStream = new FileInputStream(DEFAULT_SAVE_FILENAME);   
			ObjectInputStream objectStream = new ObjectInputStream(fileStream);
			
			// Create a new saveState
			saveState s = new saveState();
			
			// Write the objects to our fields.
			s.setZoneName((String) objectStream.readObject());
			s.setPlayerX((int) objectStream.readObject());
			s.setPlayerY((int) objectStream.readObject());
			s.setFacingDirection((String)objectStream.readObject());
			s.setChaosChoices(((int)objectStream.readObject()));
			s.setOrderChoices(((int)objectStream.readObject()));
			
			// Get the last well.
			boolean isThereAWell = (boolean) objectStream.readObject();
			if(isThereAWell) {
				s.lastWell = new Point((int)objectStream.readObject(),(int)objectStream.readObject());
			}
			else {
				objectStream.readObject();
				objectStream.readObject();
			}
			
			// Get the last bottle charge
			boolean isThereASaveBottle = (boolean) objectStream.readObject();
			if(isThereASaveBottle) {
				s.lastSaveBottle = new Point((int)objectStream.readObject(),(int)objectStream.readObject());
			}
			else {
				objectStream.readObject();
				objectStream.readObject();
			}
			
			//////////////
			/// EVENTS ///
			//////////////
			// Write the length of the coming array.
			int eventsSize = (int)objectStream.readObject(); 
			
			// Create new array.
			ArrayList<event> newEvents = new ArrayList<event>();
			
			// Read the gags from save file.
			for(int i = 0; i < eventsSize; i++) {
				String theName = (String)objectStream.readObject();
				boolean completed = (boolean)objectStream.readObject();
				event g = new event(theName);
				g.setCompleted(completed);
				newEvents.add(g);
			}
			
			// Initiate items
			item.initiate();
			
			//////////////////
			/// INVENTORY ///
			//////////////////
			// Read the length of the coming array.
			int j = (int)objectStream.readObject();
			
			// Get the item pertaining to each name and add it to an array list.
			inventory newInventory = new inventory();
			for(int i = 0; i < j; i++) {
				
				// Write the item name.
				String itemName = (String)objectStream.readObject();
				
				// Save zone
				String discoverZone = (String)objectStream.readObject();
				
				// x and y
				int x = (int)objectStream.readObject();
				int y = (int)objectStream.readObject();
			
				Class<?> clazz = Class.forName(itemName);
				Constructor<?> ctor = clazz.getConstructor(int.class, int.class);
				Object object = ctor.newInstance(new Object[] { x,
						y});
				
				// Save the slot.
				int slot = (int)objectStream.readObject();
				
				// Add the slot and discover zone to the item.
				item newItem = (item)object;
				newItem.setDrawObject(false); // Don't draw objects on the floor if they're in our inventory.
				newItem.inInventory = true; // Of course, we're loading it from the inventory.
				newItem.slot = slot;
				newItem.discoverZone = discoverZone;
				
				// Equip equipped items.
				newInventory.equipItem(newItem, slot);
				
				// For bottles, save the charges.
				if(newItem instanceof bottle) {
					((bottle) newItem).setChargesLeft((int)(objectStream.readObject()));
				}
				
				newInventory.add(newItem);
				
			}
			s.setPlayerInventory(newInventory);
			
			//////////////
			/// QUESTS ///
			//////////////
			// Write the length of the coming array.
			int questsSize = (int)objectStream.readObject(); 
			
			// Create new array.
			ArrayList<String> newQuests = new ArrayList<String>();
			
			// Read the quests from save file.
			for(int i = 0; i < questsSize; i++) {
				String theName = (String)objectStream.readObject();
				newQuests.add(theName);
			}
			
			// Load the quests
			s.setCurrentQuests(newQuests);
			quest.setCurrentQuests(s.getCurrentQuests());
			
			// Close the streams.
		    objectStream.close();   
		    fileStream.close();   
			
		    // Return the state.
		    return s;
		}
		catch(Exception e) {
			// Initiate items
			item.initiate();
			
			e.printStackTrace();
			// Failed to load game.
			return null;
		}
	}
	
	public String getZoneName() {
		return zoneName;
	}

	public void setZoneName(String zoneName) {
		this.zoneName = zoneName;
	}

	public int getPlayerX() {
		return playerX;
	}

	public void setPlayerX(int playerX) {
		this.playerX = playerX;
	}

	public int getPlayerY() {
		return playerY;
	}

	public void setPlayerY(int playerY) {
		this.playerY = playerY;
	}

	public String getFacingDirection() {
		return facingDirection;
	}

	public void setFacingDirection(String facingDirection) {
		this.facingDirection = facingDirection;
	}

	public inventory getPlayerInventory() {
		return playerInventory;
	}

	public void setPlayerInventory(inventory playerInventory) {
		this.playerInventory = playerInventory;
	}

	public bottle getEquippedBottle() {
		return equippedBottle;
	}

	public void setEquippedBottle(bottle equippedBottle) {
		this.equippedBottle = equippedBottle;
	}

	public ArrayList<event> getAllEvents() {
		return allEvents;
	}

	public void setAllEvents(ArrayList<event> allEvents) {
		this.allEvents = allEvents;
	}

	public static boolean isQuiet() {
		return quiet;
	}

	public static void setQuiet(boolean quiet) {
		saveState.quiet = quiet;
	}

	public ArrayList<String> getCurrentQuests() {
		return currentQuests;
	}

	public void setCurrentQuests(ArrayList<String> currentQuests) {
		this.currentQuests = currentQuests;
	}

	public int getChaosChoices() {
		return chaosChoices;
	}

	public void setChaosChoices(int chaosChoices) {
		this.chaosChoices = chaosChoices;
	}

	public int getOrderChoices() {
		return orderChoices;
	}

	public void setOrderChoices(int orderChoices) {
		this.orderChoices = orderChoices;
	}
	
}