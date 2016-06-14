package utilities;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import items.item;
import items.weapon;
import quests.quest;
import terrain.doodads.farmLand.haystack;
import units.player;
import userInterface.inventory;
import zones.zone;

public class saveState implements Serializable {
	
	/////////////////////////
	////// DEFAULTS /////////
	/////////////////////////
	
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
	
	// Level and exp
	private int playerLevel;
	private int expIntoLevel;
	
	// Quests
	private ArrayList<quest> allQuests;

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
				// Create new saveState.
				saveState s = new saveState();
				
				// Load our player. 
				player currPlayer = player.getCurrentPlayer();
				s.setZoneName(currPlayer.getPlayerZone().getName());
				s.setPlayerX(currPlayer.getX());
				s.setPlayerY(currPlayer.getY());
				s.setFacingDirection(currPlayer.getFacingDirection());
				s.setPlayerInventory(currPlayer.getPlayerInventory());
				s.setEquippedWeapon(currPlayer.getEquippedWeapon());
				s.setPlayerLevel(currPlayer.getPlayerLevel());
				s.setExpIntoLevel(currPlayer.getExpIntoLevel());
			
				// Save quests.
				s.setAllQuests(quest.loadedQuests);
				
				// Save jokes.
				
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
				
				// Write the level and exp into level.
				objectStream.writeObject(s.getPlayerLevel());
				objectStream.writeObject(s.getExpIntoLevel());
				
				//////////////
				/// QUESTS ///
				//////////////
				// Write the length of the coming array.
				int questsSize = 0;
				if(s.getAllQuests() != null) questsSize = s.getAllQuests().size();
				objectStream.writeObject(questsSize); 
				
				// Write the inventory (names of items) to save file.
				for(int i = 0; i < questsSize; i++) {
					objectStream.writeObject(s.getAllQuests().get(i).getTheText());
					objectStream.writeObject(s.getAllQuests().get(i).isStarted());
					objectStream.writeObject(s.getAllQuests().get(i).isCompleted());
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
			
			// Get level and exp
			s.setPlayerLevel((int)objectStream.readObject());
			s.setExpIntoLevel((int)objectStream.readObject());
			
			//////////////
			/// QUESTS ///
			//////////////
			// Write the length of the coming array.
			int questsSize = (int)objectStream.readObject(); 
			
			// Create new array.
			ArrayList<quest> newQuests = new ArrayList<quest>();
			
			// Write the inventory (names of items) to save file.
			for(int i = 0; i < questsSize; i++) {
				String theText = (String)objectStream.readObject();
				boolean started = (boolean)objectStream.readObject();
				boolean completed = (boolean)objectStream.readObject();
				quest q = new quest(theText, null, null);
				q.setStarted(started);
				q.setCompleted(completed);
				newQuests.add(q);
			}
			
			// Load the quests.
			s.setAllQuests(newQuests);
			
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

	public ArrayList<quest> getAllQuests() {
		return allQuests;
	}

	public void setAllQuests(ArrayList<quest> allQuests) {
		this.allQuests = allQuests;
	}
	
}