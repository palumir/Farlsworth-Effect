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
import units.unitCommand;
import units.unitCommands.commandList;
import units.unitCommands.positionedCommand;
import units.unitCommands.commands.waitCommand;

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
		add("new units.bosses.playerOne");
		add("doodads.tomb.stairsUp");
		add("doodads.sheepFarm.caveEnterance");
		add("doodads.sheepFarm.tomb");
		add("doodads.sheepFarm.horizontalGate");
		add("units.developer");
		add("units.player");
		add("doodads.general.invisibleLightSource");
		add("terrain.chunkTypes.water");
		add("units.developer.developer");
		add("doodads.sheepFarm.clawMarkBlack");
		add("doodads.sheepFarm.clawMarkRed");
		add("doodads.sheepFarm.clawMarkYellow");
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
								
								unit u = (unit)object;
								
								// Write class name.
								objectStream.writeObject(object.getClass().getName());
								
								// Position TODO: figure out where to put the unit?
								objectStream.writeObject(object.getIntX());
								objectStream.writeObject(object.getIntY());
								
								// Command List
								if(u.getRepeatCommands() == null) objectStream.writeObject(0);
								else {
									
									// Write the length of commands
									objectStream.writeObject(u.getRepeatCommands().size());
									
									// Write the commands
									for(int j = 0; j < u.getRepeatCommands().size(); j++) {
										
										// Unit command
										unitCommand c = u.getRepeatCommands().get(j);
										
										// Write the command details
										objectStream.writeObject(c.getClass().getName());
										
										// Types of commands
										if(c instanceof positionedCommand) {
											positionedCommand p = (positionedCommand)c;
											objectStream.writeObject(p.getX());
											objectStream.writeObject(p.getY());
											
										}
										else if(c instanceof waitCommand) {
											waitCommand w = (waitCommand)c;
											objectStream.writeObject(w.getHowLong());
											objectStream.writeObject(0); // Dumby object.
										}
										else {
											objectStream.writeObject(0); // Dumby object.
											objectStream.writeObject(0); // Dumby object.
											System.err.println("Unknown unit command saved to file.");
										}
									}
								}
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
				
				// Read class name.
				String objectClass = (String)objectStream.readObject();
				
				// Ground tiles.
				if(typeOfObject.equals("groundTile")) {
					
					// Properties
					int x = (int)objectStream.readObject();
					int y = (int)objectStream.readObject();
					int j = (int)objectStream.readObject();
					
					if(!dontSaveTheseThings.contains(objectClass)) {
						Class<?> clazz = Class.forName(objectClass);
						Constructor<?> ctor = clazz.getConstructor(int.class, int.class, int.class);
						Object object = ctor.newInstance(new Object[] { x,
								y,
								j});
						
						if(toCode) {
							out.println("new " + objectClass + "(" + x + "," + y + "," + j + ");");
						}
					}
				}
				
				// Chunk
				else if(typeOfObject.equals("chunk")) {
					
					// Properties
					int x = (int)objectStream.readObject();
					int y = (int)objectStream.readObject();
					int j = (int)objectStream.readObject();
					
					if(!dontSaveTheseThings.contains(objectClass)) {
						Class<?> clazz = Class.forName(objectClass);
						Constructor<?> ctor = clazz.getConstructor(int.class, int.class, int.class);
						Object object = ctor.newInstance(new Object[] { x,
								y,
								j});
						
						if(toCode) {
							out.println("new " + objectClass + "(" + x + "," + y + "," + j + ");");
						}
					}
				}
				
				// Unit
				else if(typeOfObject.equals("unit")) {
					
					// Properties
					int x = (int)objectStream.readObject();
					int y = (int)objectStream.readObject();
					
					// Get the unit commands length
					int howManyCommands = (int)objectStream.readObject();
					
					if(!dontSaveTheseThings.contains(objectClass)) {
						Class<?> clazz = Class.forName(objectClass);
					
						Constructor<?> ctor = clazz.getConstructor(int.class, int.class);
						Object object = ctor.newInstance(new Object[] { x,
								y});
						
						
						if(toCode) {
							out.println("u = new " + objectClass + "(" + x + "," + y + ");");
						}
						
						// Cast to unit
						unit u = (unit)object;
						
						if(howManyCommands>0 && toCode) out.println("commands = new commandList();");
						
						// Make unit commands
						u.setRepeatCommands(new commandList());
						for(int j = 0; j < howManyCommands; j++) {
							
							// Read 
							String commandClass = (String)objectStream.readObject();
							
							// Properties
							double commandX = (double)objectStream.readObject();
							double commandY = (double)objectStream.readObject();
							
							Class<?> commandClazz = Class.forName(commandClass);
						
							Constructor<?> commandCtor = commandClazz.getConstructor(double.class, double.class);
							Object commandObject = commandCtor.newInstance(new Object[] { commandX,
									commandY});
							u.getRepeatCommands().add((unitCommand)commandObject);
							
							if(toCode) {
								out.println("commands.add(new " + commandClass + "(" + commandX + "," + commandY + "));");
							}
						}
						
						if(howManyCommands>0 && toCode) out.println("u.setRepeatCommands(commands);");
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