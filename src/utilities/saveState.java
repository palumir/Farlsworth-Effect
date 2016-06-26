package utilities;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import doodads.farmLand.haystack;
import drawing.userInterface.inventory;
import drawing.userInterface.tooltipString;
import interactions.event;
import interactions.quest;
import items.bottle;
import items.item;
import items.weapon;
import units.boss;
import units.player;
import units.unitType;
import zones.zone;

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
	
	// Facing position
	private String facingDirection;
	
	// Player inventory
	private inventory playerInventory;
	private weapon equippedWeapon;
	private bottle equippedBottle;
	
	// Level and exp
	private int playerLevel;
	private int expIntoLevel;
	
	// Save quietly?
	private static boolean quiet = false;
	
	// Events
	private ArrayList<event> allEvents;

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
			if(player.playerLoaded && player.getCurrentPlayer()!= null) {
				
				// Display that we made a new savestate.
				if(!quiet) {
					tooltipString t = new tooltipString(DEFAULT_GAME_SAVED_TEXT);
				}
				
				// Create new saveState.
				saveState s = new saveState();
				
				// Load our player. 
				player currPlayer = player.getCurrentPlayer();
				s.setZoneName(currPlayer.getCurrentZone().getName());
				s.setPlayerX(currPlayer.getX());
				s.setPlayerY(currPlayer.getY());
				s.setFacingDirection(currPlayer.getFacingDirection());
				s.setPlayerInventory(currPlayer.getPlayerInventory());
				s.setEquippedWeapon(currPlayer.getEquippedWeapon());
				s.setPlayerLevel(currPlayer.getPlayerLevel());
				s.setExpIntoLevel(currPlayer.getExpIntoLevel());
				s.setEquippedBottle(currPlayer.getEquippedBottle());
				
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
				
				/////////////////
				/// INVENTORY ///
				/////////////////
				// Write the length of the coming array.
				int inventorySize = 0;
				if(s.getPlayerInventory() != null) inventorySize = s.getPlayerInventory().size();
				objectStream.writeObject(inventorySize); 
				
				// Write the inventory (names of items) to save file.
				for(int i = 0; i < inventorySize; i++) objectStream.writeObject(s.getPlayerInventory().get(i).name);
				
				// Write the equipped items to save file.
				if(s.getEquippedWeapon() == null) objectStream.writeObject("None!");
				else objectStream.writeObject(s.getEquippedWeapon().name);
				
				// Write equipped bottle to file.
				if(s.getEquippedBottle() == null) {
					objectStream.writeObject("None!");
					objectStream.writeObject(0);
				}
				else {
					objectStream.writeObject(s.getEquippedBottle().name);
					objectStream.writeObject(s.getEquippedBottle().getChargesLeft());
				}
				
				// Write the level and exp into level.
				objectStream.writeObject(s.getPlayerLevel());
				objectStream.writeObject(s.getExpIntoLevel());
				
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
				
				// Close the streams.
			    objectStream.close();   
			    fileStream.close(); 
			}
		}
		catch(Exception e) {
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
			
			//////////////////
			/// INVENTORTY ///
			//////////////////
			// Read the length of the coming array.
			int j = (int)objectStream.readObject();
			
			// Read the inventory (names of items) to save file.
			ArrayList<String> itemNames = new ArrayList<String>();
			for(int i = 0; i < j; i++) {
				itemNames.add((String)objectStream.readObject());
			}
			
			// Get the item pertaining to each name and add it to an array list.
			inventory newInventory = new inventory();
			for(int i = 0; i < itemNames.size(); i++) {
				newInventory.add(item.getItemByName(itemNames.get(i)));
			}
			s.setPlayerInventory(newInventory);
			
			// Write the equipped items to save file.
			String equippedWeaponName = (String)objectStream.readObject();
			s.setEquippedWeapon((weapon)item.getItemByName(equippedWeaponName));
			
			// Write equipped bottle to file.
			String equippedBottleName = (String)objectStream.readObject();
			s.setEquippedBottle(((bottle)item.getItemByName(equippedBottleName)));
			
			// Set charges.
			if(s.getEquippedBottle() != null) s.getEquippedBottle().setChargesLeft((int)objectStream.readObject());
			else {
				int placeHolder = (int)objectStream.readObject();
			}
			
			// Get level and exp
			s.setPlayerLevel((int)objectStream.readObject());
			s.setExpIntoLevel((int)objectStream.readObject());
			
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
			
			// Load the gags
			s.setAllEvents(newEvents);
			
			// Close the streams.
		    objectStream.close();   
		    fileStream.close();   
			
		    // Return the state.
		    return s;
		}
		catch(Exception e) {
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

	public weapon getEquippedWeapon() {
		return equippedWeapon;
	}

	public void setEquippedWeapon(weapon equippedWeapon) {
		this.equippedWeapon = equippedWeapon;
	}

	public inventory getPlayerInventory() {
		return playerInventory;
	}

	public void setPlayerInventory(inventory playerInventory) {
		this.playerInventory = playerInventory;
	}

	public int getPlayerLevel() {
		return playerLevel;
	}

	public void setPlayerLevel(int playerLevel) {
		this.playerLevel = playerLevel;
	}

	public int getExpIntoLevel() {
		return expIntoLevel;
	}

	public void setExpIntoLevel(int expIntoLevel) {
		this.expIntoLevel = expIntoLevel;
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
	
}