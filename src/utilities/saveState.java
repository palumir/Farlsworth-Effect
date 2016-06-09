package utilities;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import units.player;
import zones.zone;

public class saveState implements Serializable {
	
	/////////////////////////
	////// DEFAULTS /////////
	/////////////////////////
	
	// Default saveFile
	private static String DEFAULT_SAVE_FILENAME = "save/gameData.save";
	
	// How often do we save?
	private static Float DEFAULT_SAVE_INTERVAL = .2f; // in seconds
	
	// Last save time.
	private static Long lastSaveTime = 0l; 

	///////////////////////
	////// FIELDS /////////
	///////////////////////
	
	/////////////////////////////////
	////// GLOBAL SAVE ITEMS/////////
	/////////////////////////////////
	
	// Current zone name
	private String zoneName;
	
	// Position in current zone.
	private int playerX;
	private int playerY;
	
	// Facing position
	private String facingDirection;
	
	//////////////////////////////////////////
	////// INDIVIDUAL ZONE SAVE DATA /////////
	//////////////////////////////////////////
	private boolean forestGateOpen; // TODO: doesn't work, but is an example.
	
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
			// Create new saveState.
			saveState s = new saveState();
			s.setZoneName(player.getCurrentPlayer().getPlayerZone().getName());
			s.setPlayerX(player.getCurrentPlayer().getX());
			s.setPlayerY(player.getCurrentPlayer().getY());
			s.setFacingDirection(player.getCurrentPlayer().getFacingDirection());
			
			// Open the streams.
			FileOutputStream fileStream = new FileOutputStream(DEFAULT_SAVE_FILENAME);   
			ObjectOutputStream objectStream = new ObjectOutputStream(fileStream);  
			
			// Write the save state to the file.
			objectStream.writeObject(s.getZoneName());
			objectStream.writeObject(s.getPlayerX());
			objectStream.writeObject(s.getPlayerY());
			objectStream.writeObject(s.getFacingDirection());
			
			// Close the streams.
		    objectStream.close();   
		    fileStream.close();   
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
	
	// Save the game every second
	public static void saveGame() {
		if(time.getTime() - lastSaveTime >= DEFAULT_SAVE_INTERVAL*1000) {
			lastSaveTime = time.getTime();
			createSaveState();
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
	
}