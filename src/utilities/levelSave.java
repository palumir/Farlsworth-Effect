package utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import UI.tooltipString;
import drawing.drawnObject;
import interactions.event;
import interactions.quest;
import items.bottle;
import items.inventory;
import items.item;
import items.weapon;
import terrain.chunk;
import terrain.groundTile;
import units.player;
import units.unit;

public class levelSave implements Serializable {
	
	/////////////////////////
	////// DEFAULTS /////////
	/////////////////////////
	
	// Game saved text.
	public static String DEFAULT_GAME_SAVED_TEXT = "Level saved.";
	
	// Default folder name
	private static String DEFAULT_FOLDER_NAME = "customLevels";
	
	// Default saveFile
	private static String DEFAULT_TEMP_SAVE_FILENAME = "tempLevelSave.save";
	
	// Custom level to code file
	private static String DEFAULT_TO_CODE_FILENAME = "levelSaveCode.txt";

	///////////////////////
	////// FIELDS /////////
	///////////////////////
	
	/////////////////////////////////
	////// GLOBAL SAVE FIELDS////////
	/////////////////////////////////
	
	// Save quietly?
	private static boolean quiet = false;
	
	////////////////////////
	////// METHODS /////////
	////////////////////////
	
	// Constructor
	public levelSave() {
		// Does nothing.
	}
	
	public static ArrayList<String> listOfTypes = new ArrayList<String>() {{
				add("unit");
				add("groundTile");
				add("chunk");
				}};
				
	public static ArrayList<String> dontSaveTheseThings = new ArrayList<String>() {{
		add("doodads.tomb.stairsUp");
		add("doodads.sheepFarm.caveEnterance");
		add("doodads.sheepFarm.tomb");
		add("doodads.sheepFarm.horizontalGate");
		add("units.developer");
		add("units.player");
	}};

	// Save the game.
	public static void createSaveState(String fileName) {
		try {
			if(player.playerLoaded && player.getPlayer()!= null) {
				
				// Display that we made a new savestate.
				if(!quiet) {
					tooltipString t = new tooltipString(DEFAULT_GAME_SAVED_TEXT);
				}
				
				// Create new saveState.
				levelSave s = new levelSave();
				
				// Open the streams.
				FileOutputStream fileStream = new FileOutputStream(DEFAULT_FOLDER_NAME + "/" + fileName);   
				ObjectOutputStream objectStream = new ObjectOutputStream(fileStream);  
				
				
				// Go through drawn objects and save each unit the way we should.
				if(drawnObject.objects != null) {
					
					ArrayList<drawnObject> objects = new ArrayList<drawnObject>();
					for(int i = 0; i < drawnObject.objects.size(); i++) {
						drawnObject currObj = drawnObject.objects.get(i);
						if((currObj instanceof groundTile ||
								currObj instanceof chunk  ||
								currObj instanceof unit) &&
								!dontSaveTheseThings.contains(currObj.getClass().getName())) {
							objects.add(currObj);
						}
					}
					
					// Output length of objects.
					objectStream.writeObject(objects.size());
					
					for(int i = 0; i < objects.size(); i++) {
						
						// Get current object.
						drawnObject object = objects.get(i);
						
						// Get type of object.
						String type = "None";
						if(object instanceof groundTile) type = "groundTile";
						else if(object instanceof chunk) type = "chunk";
						else if(object instanceof unit) type = "unit";
						
						if(listOfTypes.contains(type)) {
							
							// Put in the type.
							objectStream.writeObject(type);
						
							///////////////////////////////
							//////// Ground Tiles /////////
							///////////////////////////////
							
							if(object instanceof groundTile) {
								
								// Write class name.
								objectStream.writeObject(object.getClass().getName());
								
								// Position
								objectStream.writeObject(object.getIntX());
								objectStream.writeObject(object.getIntY());
								
								// Variations
								objectStream.writeObject(((groundTile)object).getVariationI());
							}
							
							///////////////////////////////
							//////// CHUNKS /////////
							///////////////////////////////
							
							else if(object instanceof chunk) {
								
								// Write class name.
								objectStream.writeObject(object.getClass().getName());
								
								// Position
								objectStream.writeObject(object.getIntX());
								objectStream.writeObject(object.getIntY());
								
								// Variations
								objectStream.writeObject(((chunk)object).getVariationI());
							}
							
							///////////////////////////////
							//////// Unit /////////
							///////////////////////////////
							
							else if(object instanceof unit) {
								
								// Write class name.
								objectStream.writeObject(object.getClass().getName());
								
								// Position
								objectStream.writeObject(object.getIntX());
								objectStream.writeObject(object.getIntY());
								
								// TODO: commandlist
							}
						}
						
						// Do nothing for non-saveable objects.
						else {
							
						}
					}
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
	
	// To code?
	private static boolean toCode = true;
	
	// Load the game
	public static levelSave loadSaveState(String fileName) {
		try {
			// File we will print toCode to
			PrintWriter out = new PrintWriter(DEFAULT_FOLDER_NAME + "/" + DEFAULT_TO_CODE_FILENAME);
			
			// Open the streams.
			FileInputStream fileStream = new FileInputStream(DEFAULT_FOLDER_NAME + "/" + fileName);   
			ObjectInputStream objectStream = new ObjectInputStream(fileStream);
			
			// Create a new saveState
			levelSave s = new levelSave();
			
			// Run through the list of objects to load.
			int lengthToCome = ((int)(objectStream.readObject()));
			
			// Run through our list of objects and create them. 
			for(int i = 0; i < lengthToCome; i++) {
				
				// Type of object
				String typeOfObject = ((String)(objectStream.readObject()));
				
				// Ground tiles.
				if(typeOfObject.equals("groundTile")) {
					
					// Write class name.
					String objectClass = (String)objectStream.readObject();
					
					// Properties
					int x = (int)objectStream.readObject();
					int y = (int)objectStream.readObject();
					int j = (int)objectStream.readObject();
					
					Class<?> clazz = Class.forName(objectClass);
					Constructor<?> ctor = clazz.getConstructor(int.class, int.class, int.class);
					Object object = ctor.newInstance(new Object[] { x,
							y,
							j});
					
					if(toCode) {
						out.println("new " + objectClass + "(" + x + "," + y + "," + j + ");");
					}
				}
				
				// Chunk
				else if(typeOfObject.equals("chunk")) {
					
					// Write class name.
					String objectClass = (String)objectStream.readObject();
					
					// Properties
					int x = (int)objectStream.readObject();
					int y = (int)objectStream.readObject();
					int j = (int)objectStream.readObject();
					
					Class<?> clazz = Class.forName(objectClass);
					Constructor<?> ctor = clazz.getConstructor(int.class, int.class, int.class);
					Object object = ctor.newInstance(new Object[] { x,
							y,
							j});
					
					if(toCode) {
						out.println("new " + objectClass + "(" + x + "," + y + "," + j + ");");
					}
				}
				
				// Unit
				else if(typeOfObject.equals("unit")) {
					
					// Write class name.
					String objectClass = (String)objectStream.readObject();
					
					// Properties
					int x = (int)objectStream.readObject();
					int y = (int)objectStream.readObject();
					
					Class<?> clazz = Class.forName(objectClass);
				
					Constructor<?> ctor = clazz.getConstructor(int.class, int.class);
					Object object = ctor.newInstance(new Object[] { x,
							y});
					
					if(toCode) {
						out.println("new " + objectClass + "(" + x + "," + y + ");");
					}
				}
			}
			
			// Close the streams.
			out.close();
		    objectStream.close();   
		    fileStream.close();   
			
		    // Return the state.
		    return s;
		}
		catch(Exception e) {
			
			e.printStackTrace();
			// Failed to load game.
			return null;
		}
	}

	public static boolean isQuiet() {
		return quiet;
	}

	public static void setQuiet(boolean quiet) {
		levelSave.quiet = quiet;
	}

}