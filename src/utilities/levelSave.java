package utilities;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;

import UI.tooltipString;
import drawing.drawnObject;
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
		add("units.unitTypes.sheepFarm.farmer");
		add("units.bosses.fernando");
		add("units.bosses.rodriguez");
		add("units.bosses.farlsworth");
		add("units.bosses.shadowOfTheDenmother");
		add("doodads.tomb.stairsUp");
		add("doodads.sheepFarm.caveEnterance");
		add("doodads.general.questMark");
		add("doodads.sheepFarm.tomb");
		add("doodads.sheepFarm.horizontalGate");
		add("units.developer");
		add("units.player");
		add("doodads.general.invisibleLightSource");
		add("units.developer.developer");
		add("doodads.sheepFarm.clawMarkBlack");
		add("doodads.sheepFarm.clawMarkRed");
		add("doodads.sheepFarm.clawMarkYellow");
	}};
	
	// Save fields for units
	public static ArrayList<String> saveTheseUnitFields = new ArrayList<String>() {{
		add("moveSpeed");
		add("jumpSpeed");
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
								
								// Passable?
								objectStream.writeObject(((chunk)object).isPassable());
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
								
								// Passable?
								objectStream.writeObject(((chunk)object).isPassable());
							}
							
							///////////////////////////////
							//////// Unit /////////
							///////////////////////////////
							
							else if(object instanceof unit) {
								
								unit u = (unit)object;
								
								// Write class name.
								objectStream.writeObject(object.getClass().getName());
								
								// If there's repeat commands, spawn unit on last positioned command.
								if(u.getRepeatCommands() != null && u.getRepeatCommands().size() > 0) {
									
									positionedCommand p = u.getPreviousPosCommand(u.getRepeatCommands().size()-1);
									
									// If there is no positioned command, spawn where it is in editor.
									if(p == null) {
										objectStream.writeObject(object.getIntX());
										objectStream.writeObject(object.getIntY());
									}
									else {
										objectStream.writeObject((int)p.getX());
										objectStream.writeObject((int)p.getY());
									}
								}
								else {
									// Otherwise, spawn where it currently is in editor.
									objectStream.writeObject(object.getIntX());
									objectStream.writeObject(object.getIntY());
								}
								
								// Save fields.
								Class<?> clazz = Class.forName("units.unit");
								
								// Save fields.
								objectStream.writeObject(saveTheseUnitFields.size()); // Write number of things fields to file.
								
								// Save each field. If it doesn't exist, save a dummy.
								for(int j = 0; j < saveTheseUnitFields.size(); j++) {
									String currFieldName = saveTheseUnitFields.get(j);
									
									try {
										Field f = clazz.getDeclaredField(currFieldName);
										f.setAccessible(true);
										objectStream.writeObject(currFieldName);
										objectStream.writeObject(f.get(u));
									}
									
									// Field doesn't exist, just add a dumby variable.
									catch(Exception e) {
										objectStream.writeObject("<dne>");
										objectStream.writeObject("<dne>");
									}
								}
								
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
											objectStream.writeObject(0.0); // Dumby object.
										}
										else {
											objectStream.writeObject(0.0); // Dumby object.
											objectStream.writeObject(0.0); // Dumby object.
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
	private static int newFunctionEvery = 50;
	
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
			
			// Num objects
			int numObjects = 0;
			int numFunctions = 0;
			
			if(toCode) {
				out.println("public void addSegment" + numFunctions + "() {");
			}
			
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
					boolean passable = (boolean)objectStream.readObject();
					
					if(!dontSaveTheseThings.contains(objectClass)) {
						try {
							Class<?> clazz = Class.forName(objectClass);
							Constructor<?> ctor = clazz.getConstructor(int.class, int.class, int.class);
							Object object = ctor.newInstance(new Object[] { x,
									y,
									j});
							numObjects++;
							((chunk)object).setPassable(passable);
							
							if(toCode) {
								out.println("c = new " + objectClass + "(" + x + "," + y + "," + j + ");");
								out.println("c.setPassable(" + passable + ");");
							}
						}
						catch(Exception e) {
							e.printStackTrace();
						}
					}
				}
				
				// Chunk
				else if(typeOfObject.equals("chunk")) {
					
					// Properties
					int x = (int)objectStream.readObject();
					int y = (int)objectStream.readObject();
					int j = (int)objectStream.readObject();
					boolean passable = (boolean)objectStream.readObject();
					
					try {
						if(!dontSaveTheseThings.contains(objectClass)) {
							Class<?> clazz = Class.forName(objectClass);
							Constructor<?> ctor = clazz.getConstructor(int.class, int.class, int.class);
							Object object = ctor.newInstance(new Object[] { x,
									y,
									j});
							numObjects++;
							((chunk)object).setPassable(passable);
							
							if(toCode) {
								out.println("c = new " + objectClass + "(" + x + "," + y + "," + j + ");");
								out.println("c.setPassable(" + passable + ");");
							}
						}
					}
					catch(Exception e) {
						e.printStackTrace();
					}
				}
				
				// Unit
				else if(typeOfObject.equals("unit")) {
					
					if(!dontSaveTheseThings.contains(objectClass)) {
						
						// Properties
						int x = (int)objectStream.readObject();
						int y = (int)objectStream.readObject();
						
						// Create the object.
						Class<?> clazz = Class.forName(objectClass);
						Constructor<?> ctor = clazz.getConstructor(int.class, int.class);
						Object object = ctor.newInstance(new Object[] { x,
								y});
						numObjects++;
						
						// Cast to unit
						unit u = (unit)object;
						
						if(toCode) {
							out.println("u = new " + objectClass + "(" + x + "," + y + ");");
						}
						
						// Load unit class
						clazz = Class.forName("units.unit");
						
						// How many fields
						int howManyFields = (int)objectStream.readObject();
						
						// Load the fields.
						for(int j = 0; j < howManyFields; j++) {
							String currFieldName = (String)objectStream.readObject();
							
							try {
								Field f = clazz.getDeclaredField(currFieldName);
								f.setAccessible(true);
								Object val = objectStream.readObject();
								
								// If it's movement, do the constructor. Special case.
								if(currFieldName.equals("moveSpeed")) {
									u.setMoveSpeed((float)val);
									if(toCode) {
										out.println("u.setMoveSpeed(" + "(float)" + val + ");");
									}
								}
								
								// Otherwise, straight up set the value.
								else {
									f.set(u,val);
									if(toCode) {
										out.println("u." + f.getName() + " = " + "(" + f.getType().getName() + ")" + val + ";");
									}
								}
								
							}
							
							// Field doesn't exist. Leave to default.
							catch(Exception e) {
								e.printStackTrace();
								Object val = objectStream.readObject();
							}
						}
						
						// Get the unit commands length
						int howManyCommands = (int)objectStream.readObject();
						
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
					else {
						objectStream.readObject();
						objectStream.readObject();
						// How many fields
						int howManyFields = (int)objectStream.readObject();
						
						// Load the fields.
						for(int j = 0; j < howManyFields; j++) {
							objectStream.readObject();
							objectStream.readObject();
						}
						
						howManyFields = (int)objectStream.readObject();
						// Load the fields.
						for(int j = 0; j < howManyFields; j++) {
							objectStream.readObject();
							objectStream.readObject();
						}
					}
				}
				
				// Move to next function?
				if(numObjects >= newFunctionEvery) {
					numObjects = 0;
					numFunctions++;
					if(toCode) {
						out.println("}");
						out.println("");
						out.println("public void addSegment" + numFunctions + "() {");
					}
				}
			}
			
			// End code
			if(toCode) {
				out.println("}");
				out.println("");
				out.println("////////////////////////");
				out.println("////////SEGMENTS ARE:///");
				out.println("////////////////////////");
				for(int i = 0; i <= numFunctions; i++) {
					out.println("addSegment" + i + "();");
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