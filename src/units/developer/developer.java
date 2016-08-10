package units.developer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;

import UI.button;
import UI.interfaceObject;
import UI.text;
import UI.tooltipString;
import doodads.sheepFarm.bone;
import doodads.sheepFarm.bush;
import doodads.sheepFarm.flower;
import doodads.sheepFarm.grave;
import doodads.sheepFarm.haystack;
import doodads.sheepFarm.log;
import doodads.sheepFarm.rock;
import doodads.sheepFarm.tree;
import doodads.sheepFarm.well;
import doodads.tomb.wallTorch;
import drawing.camera;
import drawing.drawnObject;
import drawing.gameCanvas;
import modes.topDown;
import terrain.chunk;
import terrain.groundTile;
import units.player;
import units.unit;
import units.unitCommand;
import units.unitCommands.commandIndicator;
import units.unitCommands.commandList;
import units.unitCommands.positionedCommand;
import utilities.levelSave;
import utilities.mathUtils;
import utilities.saveState;
import utilities.utility;
import zones.zone;

public class developer extends player {
	
	// Interface
	public text instructionOne;
	public text instructionTwo;
	public text currChunkPre;
	public text currChunkType;
	public text instructionThree;
	public text hitBoxOnOrOff;
	public text instructionFour;
	public text collisionOnOrOff;
	public text instructionFive;
	public text visibilityOnOrOff;
	public text instructionSix;
	
	// Show hitboxes?
	public boolean showHitBoxes = false;
	
	// Show visibility
	public boolean visible = true;
	
	// Lists of our stuff.
	public static ArrayList<String> listOfChunks;
	
	public static ArrayList<String> listOfUnits;
	public static ArrayList<String> listOfUnitCommands;
	
	public static ArrayList<String> listOfGroundTiles;
	
	// Selected units
	private static ArrayList<drawnObject> selectedThings;
	private static drawnObject selectedThing;
	
	// Unit commands displayed
	public static ArrayList<commandIndicator> unitCommands;
	
	// Current object class
	public static String currentObjectClass;
	
	// Is left click held?
	private static Point leftClickStartPoint;
	private static Point lastMousePos;
	
	// Are we selecting?
	private static boolean selecting = false;
	private static boolean movingObject = false;
	
	// Click radius
	private static int DEFAULT_CLICK_RADIUS = 10;
	
	// Developer mode.
	public developer(int newX, int newY, zone z) {
		super(newX, newY, z);
		collisionOn = false;
		setStuck(true);
		
		// Give a million hp
		healthPoints = 100000;
		moveSpeed = 10f;
		
		// Create our lists of stuff.
		createLists();
		
		// Destroy healthbar.
		getHealthBar().destroy();
		
		// Create dev interface
		createDevInterface();
	}
	
	// Adjust X and Y
	int adjustX = -150;
	int adjustY = -150;
	
	// Default button stuff
	static int DEFAULT_BUTTON_WIDTH = 80;
	static int DEFAULT_BUTTON_HEIGHT = 30;
	
	// File button.
	static button file;
	text displayFileName;
	public static String levelName;
	static ArrayList<interfaceObject> areYouSureSave;
	
	// Editor mode.
	static button editorMode;
	text displayEditorMode;
	public String[] editorTypeList = {"Unit",
									  "Chunk",
									  "Ground Tile"
	};
	private static String editorType = "";
	
	// Display the selected unit
	static text selectedThingDisplay;
	
	// Buttons for units
	static ArrayList<button> unitButtons;
	
	// Buttons for chunks
	static ArrayList<button> chunkButtons;
	
	// Buttons for groundTiles
	static ArrayList<button> groundTileButtons;
	
	// Pause button
	static button pauseButton;
	
	// Create lists
	public void createLists() {
		// Chunks
		listOfChunks =  new ArrayList<String>();
		File folder = new File("src/doodads");
		File[] listOfFiles = folder.listFiles();
	    for (int i = 0; i < listOfFiles.length; i++) {
	      if (listOfFiles[i].isFile()) {
	    	  System.err.println("There's a file in the base doodads folder!");
	      } else if (listOfFiles[i].isDirectory()) {
	    	  File subFolder = new File("src/doodads/" + listOfFiles[i].getName());
	    	  File[] subListOfFiles = subFolder.listFiles();
	    	  for(int j = 0; j < subListOfFiles.length; j++) {
	    		  listOfChunks.add("doodads." + listOfFiles[i].getName() + "." + subListOfFiles[j].getName().substring(0,subListOfFiles[j].getName().length() - 5));
	    	  }
	      }
	    }
		
		// Units
		listOfUnits =  new ArrayList<String>();
		folder = new File("src/units/unitTypes");
		listOfFiles = folder.listFiles();
	    for (int i = 0; i < listOfFiles.length; i++) {
	      if (listOfFiles[i].isFile()) {
	    	  System.err.println("There's a file in the base unitTypes folder!");
	      } else if (listOfFiles[i].isDirectory()) {
	    	  File subFolder = new File("src/units/unitTypes/" + listOfFiles[i].getName());
	    	  File[] subListOfFiles = subFolder.listFiles();
	    	  for(int j = 0; j < subListOfFiles.length; j++) {
	    		  listOfUnits.add("units.unitTypes." + listOfFiles[i].getName() + "." + subListOfFiles[j].getName().substring(0,subListOfFiles[j].getName().length() - 5));
	    	  }
	      }
	    }
	    
		// Unit commands
		listOfUnitCommands =  new ArrayList<String>();
		folder = new File("src/units/unitCommands/commands");
		listOfFiles = folder.listFiles();
	    for (int i = 0; i < listOfFiles.length; i++) {
	      if (listOfFiles[i].isFile()) {
	    	  listOfUnitCommands.add("units.unitCommands.commands." + listOfFiles[i].getName().substring(0,listOfFiles[i].getName().length() - 5));
	      } else if (listOfFiles[i].isDirectory()) {
	      }
	    }
		
		// Ground tiles.
		listOfGroundTiles = new ArrayList<String>();
		folder = new File("src/terrain/chunkTypes");
		listOfFiles = folder.listFiles();
	    for (int i = 0; i < listOfFiles.length; i++) {
	      if (listOfFiles[i].isFile()) {
	    	  listOfGroundTiles .add("terrain.chunkTypes." + listOfFiles[i].getName().substring(0,listOfFiles[i].getName().length() - 5));
	      } else if (listOfFiles[i].isDirectory()) {
	      }
	    }
	}
	
	// Delete selected.
	public void deleteSelected() {
		if(selectedThings!=null) {
			
			// Special case for unit commands.
			if(unitSelected() && commandSelected()) {
				for(int i = 0;  i < selectedThings.size(); i++) {
					if(selectedThings.get(i) instanceof commandIndicator) {
						
						// Remove from unit's repeatCommands
						for(int j = 0; j < ((commandIndicator)selectedThings.get(i)).getUnit().getRepeatCommands().size(); j++) {
							if(((commandIndicator)selectedThings.get(i)).getUnit().getRepeatCommands().get(j) == ((commandIndicator)selectedThings.get(i)).getCommand()) {
								((commandIndicator)selectedThings.get(i)).getUnit().getRepeatCommands().remove(j);
								break;
							}
						}
						
						// Remove from unitCommands list.
						unitCommands.remove(unitCommands.indexOf((commandIndicator)(selectedThings.get(i))));
						
						// Remove from drawing
						selectedThings.get(i).destroy();
					}
				}
			}
			
			// Just delete everything selected otherwise.
			else {
				for(int i = 0;  i < selectedThings.size(); i++) {
					selectedThings.get(i).destroy();
				}
			}
		}
	}

	// Create dev interface
	public void createDevInterface() {
		
		////////////////////
		/////////Pause//////
		////////////////////
		pauseButton = new button("Pause Actions","pause",gameCanvas.getDefaultWidth() - 150, 50, DEFAULT_BUTTON_WIDTH, DEFAULT_BUTTON_HEIGHT);
		
		//////////////////////
		///////// File ///////
		//////////////////////
		
		file = new button("File","file",50,50,DEFAULT_BUTTON_WIDTH,DEFAULT_BUTTON_HEIGHT);
		
		displayFileName = new text("No level loaded", 150, 70, Color.green, 1.3f);
			
		// Save
		button b = new button("Save", "Save", 150, file.getIntY() + 0*DEFAULT_BUTTON_HEIGHT*4/3, DEFAULT_BUTTON_WIDTH, DEFAULT_BUTTON_HEIGHT);
		file.addChild(b);
		
		// Save As
		//b = new button("Save As", "Save As", 150, file.getIntY() + 1*DEFAULT_BUTTON_HEIGHT*4/3, DEFAULT_BUTTON_WIDTH, DEFAULT_BUTTON_HEIGHT);
		//file.addChild(b);
		
		// Load
		b = new button("Load", "Load", 150, file.getIntY() + 1*DEFAULT_BUTTON_HEIGHT*4/3, DEFAULT_BUTTON_WIDTH, DEFAULT_BUTTON_HEIGHT);
		file.addChild(b);
		
		// Put levels in the load button.
		File folder = new File("customLevels");
		File[] listOfFiles = folder.listFiles();
		int numButtons = 0;
	    for (int i = 0; i < listOfFiles.length; i++) {
	    	
	      // Make sure it's a .save file.
	      String extension = listOfFiles[i].getName().substring(listOfFiles[i].getName().indexOf('.') + 1, listOfFiles[i].getName().length());
	      String fileWithoutExtension = listOfFiles[i].getName().substring(0, listOfFiles[i].getName().indexOf('.'));
	    	
	      if (listOfFiles[i].isFile() && extension.equals("save")) {
	    	  button fileButton = new button(fileWithoutExtension, b.getName() + "." + listOfFiles[i].getName(), b.getIntX() + 100, b.getIntY() + numButtons*DEFAULT_BUTTON_HEIGHT*4/3,DEFAULT_BUTTON_WIDTH, DEFAULT_BUTTON_HEIGHT);
	    	  b.addChild(fileButton);
		      numButtons++;
	      } else if (listOfFiles[i].isDirectory()) {
	      }
	    }
		
		// Test
		b = new button("Test Level", "Test", 150, file.getIntY() + 2*DEFAULT_BUTTON_HEIGHT*4/3, DEFAULT_BUTTON_WIDTH, DEFAULT_BUTTON_HEIGHT);
		file.addChild(b);

		//////////////////////
		/// EDITOR MODE ///
		//////////////////////
		
		editorMode = new button("Editor Mode","editorMode",50,50 + 1*DEFAULT_BUTTON_HEIGHT*4/3,DEFAULT_BUTTON_WIDTH,DEFAULT_BUTTON_HEIGHT);
		
		displayEditorMode = new text("",150,70 + 1*DEFAULT_BUTTON_HEIGHT*4/3, Color.green, 1.3f);
		
		int selectionNumber = 0;
		for(String s : editorTypeList) {
			
			// editorModeUnit, editorModeChunk, or editorModeGround Tile
			b = new button(s, editorMode.getButtonID() + s, 150, editorMode.getIntY() + selectionNumber*DEFAULT_BUTTON_HEIGHT*4/3, DEFAULT_BUTTON_WIDTH, DEFAULT_BUTTON_HEIGHT);
			editorMode.addChild(b);
			selectionNumber++;
		}
	}
	
	// Close prompts
	public static void closePrompts() {
		if(areYouSureSave != null) {
			for(;0 < areYouSureSave.size();) {
				areYouSureSave.get(0).destroy();
				areYouSureSave.remove(0);
			}
		}
	}
	
	// Reset stuff
	public static void resetEditorStuff() {
		
		// Destroy all old buttons
		button.recursivelyHide(groundTileButtons);
		button.recursivelyHide(chunkButtons);
		button.recursivelyHide(unitButtons);
		button.recursivelyHide(editorMode.getChildren());
		button.recursivelyHide(file.getChildren());
		
		// Set current placeable object to be null.
		currentObjectClass = null;
	}
	
	// Select a button on the dev interface
	public static void selectButton(button b) {
		
		// Destroy children of buttons on same level.
		if(b.getParent() != null) {
			button.recursivelyHideChildren(b.getParent().getChildren());
		}
		
		// If there's no more kids
		if(b.getChildren() == null || b.getChildren().size() == 0) {
			button.recursivelyHide(b.getTopParent().getChildren());
		}
		
		// Initiate these
		if(groundTileButtons==null) groundTileButtons = new ArrayList<button>();
		if(chunkButtons==null) chunkButtons = new ArrayList<button>();
		if(unitButtons==null) unitButtons = new ArrayList<button>();
		
		b.select();
		
		/////////////////////
		///////// PAUSE /////
		/////////////////////
		if(b.getButtonID().contains("pause")) {
			
			// Do nothing.
			utility.toggleActions();
		}
		
		/////////////////////
		///// FILE STUFF ////
		/////////////////////
		
		// Quick save button.
		if(b.getButtonID().equals("Save")) {
			
			// We are in a file.
			if(developer.levelName != null) {
				String levelName = b.getButtonID().substring(b.getButtonID().indexOf('.')+1,b.getButtonID().length());
				
				String buttonText = "Are you sure you want to save over the current level?";
				int textWidth = gameCanvas.getGameCanvas().getFontMetrics(drawnObject.DEFAULT_FONT).stringWidth(buttonText);
				
				areYouSureSave = new ArrayList<interfaceObject>();
				
				text textPart = new text(
						buttonText,
						gameCanvas.getActualWidth()/2 - textWidth/2,
						gameCanvas.getActualHeight()*3/4,
						Color.white
						);
				areYouSureSave.add(textPart);
				
				areYouSureSave.add(new button(
						"Yes",
						"SaveSave." + levelName, 
						gameCanvas.getActualWidth()/2 - textWidth/2 + textWidth*1/4 - 70/2,
						gameCanvas.getActualHeight()*3/4 + 30,
						70,
						50
						));
				
				areYouSureSave.add(new button(
						"No",
						"SaveNotSave." + levelName,
						gameCanvas.getActualWidth()/2 - textWidth/2 + textWidth*3/4 - 70/2,
						gameCanvas.getActualHeight()*3/4 + 30,
						70,
						50
						));
			}
			
			// We aren't in a file.
			else {
				// TODO: make this prompt saving to a new file.
				tooltipString t = new tooltipString("We aren't in a loaded level. Create or load a level.");
			}
		}
		
		if(b.getButtonID().contains("SaveNotSave.")) {
			
			// Do nothing.
			closePrompts();
		}
		
		if(b.getButtonID().contains("SaveSave.")) {
			
			// Save
			levelSave.createSaveState(developer.levelName);
			closePrompts();
		}
		
		// Load a level
		if(b.getButtonID().contains("Load.")) {
			
			// Loaded level after loading another.
			if(developer.levelName != null) {
				String levelName = b.getButtonID().substring(b.getButtonID().indexOf('.')+1,b.getButtonID().length());
				
				String buttonText = "Do you want to save the current level before loading?";
				int textWidth = gameCanvas.getGameCanvas().getFontMetrics(drawnObject.DEFAULT_FONT).stringWidth(buttonText);
				
				areYouSureSave = new ArrayList<interfaceObject>();
				
				text textPart = new text(
						buttonText,
						gameCanvas.getActualWidth()/2 - textWidth/2,
						gameCanvas.getActualHeight()*3/4,
						Color.white
						);
				areYouSureSave.add(textPart);
				
				areYouSureSave.add(new button(
						"Yes",
						"LoadSave." + levelName, 
						gameCanvas.getActualWidth()/2 - textWidth/2 + textWidth*1/4 - 70/2,
						gameCanvas.getActualHeight()*3/4 + 30,
						70,
						50
						));
				
				areYouSureSave.add(new button(
						"No",
						"LoadNotSave." + levelName,
						gameCanvas.getActualWidth()/2 - textWidth/2 + textWidth*3/4 - 70/2,
						gameCanvas.getActualHeight()*3/4 + 30,
						70,
						50
						));
			}
			
			// Loading a level for the first time.
			else {
				// Create the player.
				player p = player.loadPlayer(null,null,0,0,"Up");
				String levelName = b.getButtonID().substring(b.getButtonID().indexOf('.')+1,b.getButtonID().length());
				developer.levelName = levelName;
				levelSave.loadSaveState(developer.levelName);
			}
		}
		
		// LoadSave
		if(b.getButtonID().contains("LoadSave.")) {
			
			// Save current level.
			levelSave.createSaveState(developer.levelName);
			
			// Destroy all objects
			drawnObject.destroyAll();
			
			// Create the player.
			player p = player.loadPlayer(null,null,0,0,"Up");
			
			// Open the new one.
			String levelName = b.getButtonID().substring(b.getButtonID().indexOf('.')+1,b.getButtonID().length());
			developer.levelName = levelName;
			levelSave.loadSaveState(developer.levelName);
		}
		
		// LoadNotSave
		if(b.getButtonID().contains("LoadNotSave.")) {
			
			// Destroy all objects
			drawnObject.destroyAll();
			
			// Create the player.
			player p = player.loadPlayer(null,null,0,0,"Up");
			
			// Open the new level.
			String levelName = b.getButtonID().substring(b.getButtonID().indexOf('.')+1,b.getButtonID().length());
			developer.levelName = levelName;
			levelSave.loadSaveState(developer.levelName);
		}
		
		// Test level button
		if(b.getButtonID().equals("Test")) {
			testLevel();
		}
		
		///////////////////////
		//// UNIT COMMANDS ////
		///////////////////////
		
		// Select a unit command
		if(b.getButtonID().contains("units.unitCommands.")) {
			
			currentObjectClass = b.getButtonID();
			
		}
		
		
		//////////////////////
		///// EDITOR MODE ////
		//////////////////////
		if(b.getButtonID().equals("editorModeGround Tile")) {
			
			if(selectedThingDisplay != null) selectedThingDisplay.destroy();
			selectedThingDisplay = new text("Select a Ground Tile Type", 150, 70 + 2*DEFAULT_BUTTON_HEIGHT*4/3, Color.green, 1.3f);
			
			// Set editor type.
			editorType = "Ground Tile";
			
			resetEditorStuff();
			
			button typeButton = new button("Ground Tile Type","groundTileTypeButton",
					editorMode.getIntX(),
					editorMode.getIntY() + DEFAULT_BUTTON_HEIGHT*4/3,
					DEFAULT_BUTTON_WIDTH,
					DEFAULT_BUTTON_HEIGHT);
			
			groundTileButtons.add(typeButton);
			
			// Show type buttons.
			int j = 0;
			int changeX = 0;
			int changeY = 0;
			for(String s : listOfGroundTiles) {
				
				String noDoodads = s.substring(s.indexOf('.')+1,s.length());
				String noFolder = noDoodads.substring(noDoodads.indexOf('.')+1,noDoodads.length());
				
				// Create each button for each type.
				button groundTileButton = new button(noFolder,s,
						150 + changeX*100,
						editorMode.getIntY() + DEFAULT_BUTTON_HEIGHT*4/3 + changeY*DEFAULT_BUTTON_HEIGHT*4/3,
						DEFAULT_BUTTON_WIDTH,
						DEFAULT_BUTTON_HEIGHT);
				typeButton.addChild(groundTileButton);
				
				j++;
				changeY++;
				if(editorMode.getIntY() + DEFAULT_BUTTON_HEIGHT*4/3 + changeY*DEFAULT_BUTTON_HEIGHT*4/3 >= gameCanvas.getDefaultHeight()) {
					changeY = 0;
					changeX++;
				}
			}
		}
		
		if(b.getButtonID().equals("editorModeUnit")) {
			
			if(selectedThingDisplay != null) selectedThingDisplay.destroy();
			selectedThingDisplay = new text("Select a Unit Type", 150, 70 + 2*DEFAULT_BUTTON_HEIGHT*4/3, Color.green, 1.3f);
			
			// Set editor type.
			editorType = "Unit";
			
			resetEditorStuff();
			
			////////////
			/// TYPE BUTTON
			///////
			
			button typeButton = new button("Unit Type","unitTypeButton",
					editorMode.getIntX(),
					editorMode.getIntY() + DEFAULT_BUTTON_HEIGHT*4/3,
					DEFAULT_BUTTON_WIDTH,
					DEFAULT_BUTTON_HEIGHT);
			
			unitButtons.add(typeButton);
			
			// Show type buttons.
			int j = 0;
			String currentFolder = "<NONE>";
			button currentFolderButton = null;
			int numFolders = 0;
			int changeX = 0;
			int changeY = 0;
			for(String s : listOfUnits) {
				
				String noUnits = s.substring(s.indexOf('.')+1,s.length());
				String noUnitTypes = noUnits.substring(noUnits.indexOf('.')+1,noUnits.length());
				
				// Create a new button for the new folder.
				if(!s.contains(currentFolder)) {
					changeX = 0;
					changeY = 0;
					currentFolder = noUnitTypes.substring(0, noUnitTypes.indexOf('.'));
					currentFolderButton = new button(currentFolder,currentFolder,
							150,
							editorMode.getIntY() + DEFAULT_BUTTON_HEIGHT*4/3 + numFolders*DEFAULT_BUTTON_HEIGHT*4/3,
							DEFAULT_BUTTON_WIDTH,
							DEFAULT_BUTTON_HEIGHT);
					typeButton.addChild(currentFolderButton);
					numFolders++;
				}
				
				// No folder
				String noFolder = noUnitTypes.substring(noUnitTypes.indexOf('.')+1,noUnitTypes.length());
				
				// Create each button for each type.
				button unitTypeButton = new button(noFolder,s,
						250 + changeX*100,
						editorMode.getIntY() + DEFAULT_BUTTON_HEIGHT*4/3 + changeY*DEFAULT_BUTTON_HEIGHT*4/3,
						DEFAULT_BUTTON_WIDTH,
						DEFAULT_BUTTON_HEIGHT);
				currentFolderButton.addChild(unitTypeButton);
				
				j++;
				changeY++;
				if(editorMode.getIntY() + DEFAULT_BUTTON_HEIGHT*4/3 + changeY*DEFAULT_BUTTON_HEIGHT*4/3 >= gameCanvas.getDefaultHeight()) {
					changeY = 0;
					changeX++;
				}
			}
			
			////////////
			/// COMMANDS BUTTON
			///////
			
			button commandsButton = new button("Unit Commands","unitCommandsButton",
					editorMode.getIntX(),
					editorMode.getIntY() + 2*DEFAULT_BUTTON_HEIGHT*4/3,
					DEFAULT_BUTTON_WIDTH,
					DEFAULT_BUTTON_HEIGHT);
			
			unitButtons.add(typeButton);
			
			// Show type buttons.
			j = 0;
			numFolders = 0;
			changeX = 0;
			changeY = 0;
			for(String s : listOfUnitCommands) {
				
				String noUnits = s.substring(s.indexOf('.')+1,s.length());
				String noCommands = noUnits.substring(noUnits.indexOf('.')+1,noUnits.length());
			
				// No folder
				String noFolder = noCommands.substring(noCommands.indexOf('.')+1,noCommands.length());
				
				// Create each button for each type.
				button unitCommandButton = new button(noFolder,s,
						150 + changeX*100,
						editorMode.getIntY() + 2*DEFAULT_BUTTON_HEIGHT*4/3 + changeY*DEFAULT_BUTTON_HEIGHT*4/3,
						DEFAULT_BUTTON_WIDTH,
						DEFAULT_BUTTON_HEIGHT);
				commandsButton.addChild(unitCommandButton);
				
				j++;
				changeY++;
				if(editorMode.getIntY() + DEFAULT_BUTTON_HEIGHT*4/3 + changeY*DEFAULT_BUTTON_HEIGHT*4/3 >= gameCanvas.getDefaultHeight()) {
					changeY = 0;
					changeX++;
				}
			}
		}
		if(b.getButtonID().equals("editorModeChunk")) {
			
			if(selectedThingDisplay != null) selectedThingDisplay.destroy();
			selectedThingDisplay = new text("Select a Chunk Type", 150, 70 + 2*DEFAULT_BUTTON_HEIGHT*4/3, Color.green, 1.3f);
			
			editorType = "Chunk";
			
			resetEditorStuff();
			
			button typeButton = new button("Chunk Type","chunkTypeButton",
					editorMode.getIntX(),
					editorMode.getIntY() + DEFAULT_BUTTON_HEIGHT*4/3,
					DEFAULT_BUTTON_WIDTH,
					DEFAULT_BUTTON_HEIGHT);
			
			chunkButtons.add(typeButton);
			
			// Show type buttons.
			int j = 0;
			String currentFolder = "<NONE>";
			button currentFolderButton = null;
			int numFolders = 0;
			int changeX = 0;
			int changeY = 0;
			for(String s : listOfChunks) {
				
				String noDoodads = s.substring(s.indexOf('.')+1,s.length());
				
				// Create a new button for the new folder.
				if(!s.contains(currentFolder)) {
					changeX = 0;
					changeY = 0;
					currentFolder = noDoodads.substring(0, noDoodads.indexOf('.'));
					currentFolderButton = new button(currentFolder,currentFolder,
							150,
							editorMode.getIntY() + DEFAULT_BUTTON_HEIGHT*4/3 + numFolders*DEFAULT_BUTTON_HEIGHT*4/3,
							DEFAULT_BUTTON_WIDTH,
							DEFAULT_BUTTON_HEIGHT);
					typeButton.addChild(currentFolderButton);
					numFolders++;
				}
				
				// No folder
				String noFolder = noDoodads.substring(noDoodads.indexOf('.')+1,noDoodads.length());
				
				// Create each button for each type.
				button chunkTypeButton = new button(noFolder,s,
						250 + changeX*100,
						editorMode.getIntY() + DEFAULT_BUTTON_HEIGHT*4/3 + changeY*DEFAULT_BUTTON_HEIGHT*4/3,
						DEFAULT_BUTTON_WIDTH,
						DEFAULT_BUTTON_HEIGHT);
				currentFolderButton.addChild(chunkTypeButton);
				
				j++;
				changeY++;
				if(editorMode.getIntY() + DEFAULT_BUTTON_HEIGHT*4/3 + changeY*DEFAULT_BUTTON_HEIGHT*4/3 >= gameCanvas.getDefaultHeight()) {
					changeY = 0;
					changeX++;
				}
			}
		}
		
		////////////////////////////////////
		///// GROUNDTILE TYPE SELECT //////
		///////////////////////////////////
		if(b.getButtonID().contains("terrain.chunkTypes.") || 
				b.getButtonID().contains("units.unitTypes.") ||
				b.getButtonID().contains("doodads.")) {
			currentObjectClass = b.getButtonID();
		}
		
	}
	
	// Update interface
	public void updateInterface() {
		
		// Record mouse position.
		Point p = gameCanvas.getGameCanvas().getMousePosition();
		if(p!=null) lastMousePos = toInGamePos(p);
		
		// Update the editor mode.
		if(levelName != null) displayFileName.setTheText("Level: " + levelName);
		if(displayEditorMode != null) {
			if(editorType.equals("")) displayEditorMode.setTheText("Select an Edit Mode");
			else displayEditorMode.setTheText("Edit Mode: " + editorType + "s");
		}
		if(currentObjectClass != null) selectedThingDisplay.setTheText("Type: " + currentObjectClass);
	}
	
	// Update unit
	@Override
	public void updateUnit() {
	
		// Update interface
		updateInterface();
		
		// Move things
		moveThings();
	}
	
	// Move things
	public void moveThings() {
		
		if(movingObject) {
			
			// In game point
			Point inGamePointCurrent = lastMousePos;
			
			// Move all of our selected objects.
			if(selectedThings!=null) {
				
				// Check if we are selecting a unit command while also selecting a unit. Don't move units then.
				if(selectedThing instanceof commandIndicator) {
					for(int i = 0; i < selectedThings.size(); i++) {
						if(selectedThings.get(i) instanceof commandIndicator) {
							selectedThings.get(i).setDoubleX(inGamePointCurrent.getX() - selectedThings.get(i).getRelativeX());
							selectedThings.get(i).setDoubleY(inGamePointCurrent.getY() - selectedThings.get(i).getRelativeY());
						}
					}
				}
				else {
					for(int i = 0; i < selectedThings.size(); i++) {
						selectedThings.get(i).setDoubleX(inGamePointCurrent.getX() - selectedThings.get(i).getRelativeX());
						selectedThings.get(i).setDoubleY(inGamePointCurrent.getY() - selectedThings.get(i).getRelativeY());
						if(selectedThings.get(i) instanceof unit) {
							((unit)(selectedThings.get(i))).setRiseRun();
						}
					}
				}
			}
		}
		
	}
	
	// Responding to mouse presses
	public static void devMousePressed(MouseEvent e) {
		
		button touchedButton = button.getButtonAt(e.getX(), e.getY());
		
		// If we have touched a button
		if(touchedButton != null) {
			selectButton(touchedButton);
		}
		
		// If we haven't touched a button.
		if(touchedButton == null) {
			leftClickStartPoint = toInGamePos(new Point(e.getX(), e.getY()));
			
			// In game point
			Point inGamePoint = leftClickStartPoint;
			
			// Determine whether or not we are touching something.
			ArrayList<drawnObject> touchedObjects  = drawnObject.getObjectsInRadius((int)inGamePoint.getX(), (int)inGamePoint.getY(), DEFAULT_CLICK_RADIUS);
			ArrayList<drawnObject> touchedUnitCommands = new ArrayList<drawnObject>();
			ArrayList<drawnObject> touchedUnits = new ArrayList<drawnObject>();
			ArrayList<drawnObject> touchedChunks = new ArrayList<drawnObject>();
			ArrayList<drawnObject> touchedGroundTiles = new ArrayList<drawnObject>();
			
			if(touchedObjects!=null) for(int i = 0; i < touchedObjects.size(); i++) 
				if(touchedObjects.get(i) instanceof unit && !(touchedObjects.get(i) instanceof player)) 
					touchedUnits.add(touchedObjects.get(i));
			
			if(touchedObjects!=null) for(int i = 0; i < touchedObjects.size(); i++) 
				if(touchedObjects.get(i) instanceof commandIndicator) 
					touchedUnitCommands.add(touchedObjects.get(i));
			
			if(touchedObjects!=null) for(int i = 0; i < touchedObjects.size(); i++) 
				if(touchedObjects.get(i) instanceof chunk && !(touchedObjects.get(i) instanceof groundTile)) 
					touchedChunks.add(touchedObjects.get(i));
			
			if(touchedObjects!=null) for(int i = 0; i < touchedObjects.size(); i++) 
				if(touchedObjects.get(i) instanceof groundTile) 
					touchedGroundTiles.add(touchedObjects.get(i));
			
			//////////////////
			// UNIT COMMANDS//
			//////////////////
			if(editorType.equals("Unit") && touchedUnitCommands != null && touchedUnitCommands.size() > 0) {
				movingObject = true;
				
				commandIndicator touchedUnitCommand = (commandIndicator)drawnObject.getClosestToFrom(
						(int)inGamePoint.getX(), 
						(int)inGamePoint.getY(),
						touchedUnitCommands);
				selectedThing = touchedUnitCommand;
				
				
				// Move all selected things.
				if(selectedThings != null && selectedThings.contains(touchedUnitCommand)) {
					for(int i = 0; i < selectedThings.size(); i++) {
						selectedThings.get(i).setRelativeX(inGamePoint.getX() - selectedThings.get(i).getIntX());
						selectedThings.get(i).setRelativeY(inGamePoint.getY() - selectedThings.get(i).getIntY());
					}
				}
				
				// De-select and move only the newly selected object.
				else {
					touchedUnitCommand.setRelativeX(inGamePoint.getX() - touchedUnitCommand.getIntX());
					touchedUnitCommand.setRelativeY(inGamePoint.getY() - touchedUnitCommand.getIntY());
					ArrayList<drawnObject> touchTheseThings = new ArrayList<drawnObject>();
					touchTheseThings.add(touchedUnitCommand);
					selectAll(touchTheseThings);
				}
			}
			
			////////////
			// UNITS ///
			////////////
			else if(editorType.equals("Unit") && touchedUnits != null && touchedUnits.size() > 0) {
				
				movingObject = true;
				
				unit touchedUnit = (unit)drawnObject.getClosestToFrom(
						(int)inGamePoint.getX(), 
						(int)inGamePoint.getY(),
						touchedUnits);
				selectedThing = touchedUnit;
				
				// Move all selected things.
				if(selectedThings != null && selectedThings.contains(touchedUnit)) {
					for(int i = 0; i < selectedThings.size(); i++) {
						selectedThings.get(i).setRelativeX(inGamePoint.getX() - selectedThings.get(i).getIntX());
						selectedThings.get(i).setRelativeY(inGamePoint.getY() - selectedThings.get(i).getIntY());
					}
				}
				
				// De-select and move only the newly selected object.
				else {
					
					touchedUnit.setRelativeX(inGamePoint.getX() - touchedUnit.getIntX());
					touchedUnit.setRelativeY(inGamePoint.getY() - touchedUnit.getIntY());
					ArrayList<drawnObject> touchTheseThings = new ArrayList<drawnObject>();
					touchTheseThings.add(touchedUnit);
					selectAll(touchTheseThings);
				}
			}
			
			////////////
			// CHUNKS //
			////////////
			else if(editorType.equals("Chunk") && touchedChunks != null && touchedChunks.size() > 0) {
				
				movingObject = true;
				
				chunk touchedChunk = (chunk)drawnObject.getClosestToFrom(
						(int)inGamePoint.getX(), 
						(int)inGamePoint.getY(),
						touchedChunks);
				selectedThing = touchedChunk;
				
				// Move all selected things.
				if(selectedThings != null && selectedThings.contains(touchedChunk)) {
					for(int i = 0; i < selectedThings.size(); i++) {
						selectedThings.get(i).setRelativeX(inGamePoint.getX() - selectedThings.get(i).getIntX());
						selectedThings.get(i).setRelativeY(inGamePoint.getY() - selectedThings.get(i).getIntY());
					}
				}
				
				// De-select and move only the newly selected object.
				else {
					
					touchedChunk.setRelativeX(inGamePoint.getX() - touchedChunk.getIntX());
					touchedChunk.setRelativeY(inGamePoint.getY() - touchedChunk.getIntY());
					ArrayList<drawnObject> touchTheseThings = new ArrayList<drawnObject>();
					touchTheseThings.add(touchedChunk);
					selectAll(touchTheseThings);
					
				}
				
			}
			
			/////////////////
			// GROUNDTILES //
			/////////////////
			else if(editorType.equals("Ground Tile") && touchedGroundTiles != null && touchedGroundTiles.size() > 0) {
				
				movingObject = true;
				
				groundTile touchedGroundTile = (groundTile)drawnObject.getClosestToFrom(
						(int)inGamePoint.getX(), 
						(int)inGamePoint.getY(),
						touchedGroundTiles);
				selectedThing = touchedGroundTile;
				
				// Move all selected things.
				if(selectedThings != null && selectedThings.contains(touchedGroundTile)) {
					for(int i = 0; i < selectedThings.size(); i++) {
						selectedThings.get(i).setRelativeX(inGamePoint.getX() - selectedThings.get(i).getIntX());
						selectedThings.get(i).setRelativeY(inGamePoint.getY() - selectedThings.get(i).getIntY());
					}
				}
				
				// De-select and move only the newly selected object.
				else {
					touchedGroundTile.setRelativeX(inGamePoint.getX() - touchedGroundTile.getIntX());
					touchedGroundTile.setRelativeY(inGamePoint.getY() - touchedGroundTile.getIntY());
					ArrayList<drawnObject> touchTheseThings = new ArrayList<drawnObject>();
					touchTheseThings.add(touchedGroundTile);
					selectAll(touchTheseThings);
				}
				
			}
			
			// Nothing was touched, draw our selection square.
			else {
				selecting = true;
			}
		}
	}
	
	// Unselect all things
	public static void unSelectAll() {
		
		if(selectedThings != null) {
			for(; selectedThings.size() > 0; ) {
				
				// Remove their unit commands, if they exist.
				if(selectedThings.get(0) instanceof unit) {
					unit u  = (unit)selectedThings.get(0);
					if(u.getRepeatCommands()!=null) {
						for(int i = 0; i < u.getRepeatCommands().size(); i++) {
							if(unitCommands != null) {
								for(int j = 0; j < unitCommands.size(); j++) {
									if(u.getRepeatCommands().get(i) == unitCommands.get(j).getCommand()) {
										unitCommands.get(j).destroy();
										unitCommands.remove(j);
										j--;
									}
								}
							}
						}
					}
				}
				
				selectedThings.get(0).dontShowHitBox();
				selectedThings.remove(0);
			}
		}
		selectedThings = new ArrayList<drawnObject>();
	}
	
	// Unselect all things
	public static void unSelectUnitCommands() {
	
		if(selectedThings != null) {
			for(int i =0; selectedThings.size() > i; i++) {
				if(selectedThings.get(i) instanceof commandIndicator) {
					selectedThings.get(i).dontShowHitBox();
					selectedThings.remove(i);
					i--;
				}
			}
		}
	}
	
	// Select all 
	public static void selectAll(ArrayList<drawnObject> d) {
		
		// Create selected things if they don't exist
		if(selectedThings == null) selectedThings = new ArrayList<drawnObject>();
		
		// If a unit is selected, draw unit commands.
		if(d != null && d.size() > 0 && d.get(0) instanceof unit) {
			for(int i = 0; i < d.size(); i++) {
				selectedThings.add(d.get(i));
				d.get(i).showHitBox();
			}
			drawingThings.createUnitCommandsText(selectedThings);
		}
		
		else {
			// If we select a commandIndicator after first selecting a unit or units.
			if(d != null && d.size() > 0 && d.get(0) instanceof commandIndicator) {
				unSelectUnitCommands();
			}
			else {
				unSelectAll();
			}
	
			for(int i = 0; i < d.size(); i++) {
				selectedThings.add(d.get(i));
				d.get(i).showHitBox();
			}
		}
		
	}

	// Responding to mouse release
	public static void devMouseReleased(MouseEvent e) {
		
		if(leftClickStartPoint!=null) {
			Rectangle rect= new Rectangle(leftClickStartPoint);
			rect.add(lastMousePos);
			
			// Deal with box selecting
			if(selecting) {
				
				// Chunks
				if(editorType.equals("Chunk")) {
					
					unSelectAll();
					
					ArrayList<drawnObject> selectTheseObjects = drawnObject.getObjectsInBox(
							rect.x, 
							rect.y, 
							rect.x + rect.width, 
							rect.y + rect.height);
					ArrayList<chunk> selectTheseChunks = new ArrayList<chunk>();
					if(selectTheseObjects!=null) {
						for(int i = 0; i < selectTheseObjects.size(); i++) {
							if(selectTheseObjects.get(i) instanceof chunk && !(selectTheseObjects.get(i) instanceof groundTile)) {
								selectTheseChunks.add((chunk)selectTheseObjects.get(i));
							}
						}
					}
					
					if(selectTheseChunks!=null && selectTheseChunks.size() > 0) {
						
						ArrayList<drawnObject> selectTheseThings = new ArrayList<drawnObject>();
						for(int i = 0; i < selectTheseChunks.size(); i++) {
							selectTheseThings.add(selectTheseChunks.get(i));
						}
					
						selectAll(selectTheseThings);
					}
				}
				
				// Ground Tiles
				if(editorType.equals("Ground Tile")) {
					
					unSelectAll();
					
					ArrayList<drawnObject> selectTheseObjects = drawnObject.getObjectsInBox(
							rect.x, 
							rect.y, 
							rect.x + rect.width, 
							rect.y + rect.height);
					ArrayList<groundTile> selectTheseChunks = new ArrayList<groundTile>();
					if(selectTheseObjects!=null) {
						for(int i = 0; i < selectTheseObjects.size(); i++) {
							if(selectTheseObjects.get(i) instanceof groundTile) {
								selectTheseChunks.add((groundTile)selectTheseObjects.get(i));
							}
						}
					}
					
					if(selectTheseChunks!=null && selectTheseChunks.size() > 0) {
						
						ArrayList<drawnObject> selectTheseThings = new ArrayList<drawnObject>();
						for(int i = 0; i < selectTheseChunks.size(); i++) {
							selectTheseThings.add(selectTheseChunks.get(i));
						}
					
						selectAll(selectTheseThings);
					}
				}
				
				// Units
				if(editorType.equals("Unit")) {
					
					ArrayList<drawnObject> selectTheseObjects = drawnObject.getObjectsInBox(
							rect.x, 
							rect.y, 
							rect.x + rect.width, 
							rect.y + rect.height);

					
					if(selectTheseObjects != null) {
						for(int i = 0; i < selectTheseObjects.size(); i++) {
							if(!(selectTheseObjects.get(i) instanceof unit || selectTheseObjects.get(i) instanceof commandIndicator) || selectTheseObjects.get(i) instanceof player) {
								selectTheseObjects.remove(i);
								i--;
							}
						}
					}
					
					if(selectTheseObjects != null && selectTheseObjects.size() > 0) {

						selectAll(selectTheseObjects);
					}
					else {
						unSelectAll();
					}
				}
			}
			
			selecting = false;
			movingObject = false;
		}
	}
	
	// Unit selected?
	public static boolean unitSelected() {
		for(int i = 0; i < selectedThings.size(); i++) if(selectedThings.get(i) instanceof unit) return true;
		return false;
	}
	
	// Command selected
	public static boolean commandSelected() {
		for(int i = 0; i < selectedThings.size(); i++) if(selectedThings.get(i) instanceof commandIndicator) return true;
		return false;
	}
	
	// Draw unit particular stuff.
	public void drawUnitSpecialStuff(Graphics g) {
		drawingThings.drawHighLightBox(g, selecting, lastMousePos, leftClickStartPoint);
		drawingThings.drawUnitCommands(g, selectedThings);
	}
	
	// Test level
	public static void testLevel() {
		if(developer.levelName != null) {
			// Test level
			levelSave.createSaveState(developer.levelName);	
			saveState.createSaveState();
			
			// Development mode?
			player.setDeveloper(false);
				
			// Create the player.
			player p = player.loadPlayer(null,null,0,0,"Up");
			levelSave.loadSaveState(developer.levelName);
			
			// Tell them to press y to go back
			tooltipString t = new tooltipString("Press 'y' to go back to developer mode.");
		}
		else {
			tooltipString t = new tooltipString("You must save before testing the level.");
		}
	}
	
	// Add unit command to selected units
	public static void addUnitCommandToSelectedUnits(unitCommand c) {
		if(selectedThings != null) {
			for(int i = 0; i < selectedThings.size(); i++) {
				if(selectedThings.get(i) instanceof unit) {
					if(((unit)selectedThings.get(i)).getRepeatCommands() == null) ((unit)selectedThings.get(i)).setRepeatCommands(new commandList());
					((unit)selectedThings.get(i)).getRepeatCommands().add(c);
					
					// Draw the commands
					if(unitCommands == null) drawingThings.createUnitCommandsText(selectedThings);
					else unitCommands.add(new commandIndicator(c, (unit)selectedThings.get(i)));
				}
			}
			if(selectedThings.size() == 0) {
				tooltipString t = new tooltipString("You must select a unit to add this command to.");
			}
		}
		if(selectedThings == null) {
			tooltipString t = new tooltipString("You must select a unit to add this command to.");
		}
	}
	
	// Controls
	public void keyPressed(KeyEvent k) {
		
		// Player presses excape
		if(k.getKeyCode() == KeyEvent.VK_ESCAPE) { 
			button.hideAllChildButtons();
			unSelectAll();
			closePrompts();
		}
		
		// Player presses delete key.
		if(k.getKeyCode() == KeyEvent.VK_DELETE) { 
			deleteSelected();
		}
		
		// Player presses left key.
		if(k.getKeyCode() == KeyEvent.VK_A) { 
			startMove("left");
		}
		
		// Player presses right key.
		if(k.getKeyCode() == KeyEvent.VK_D) { 
			startMove("right");
		}
		
		// Player presses up key, presumably to jump!
		if(k.getKeyCode() == KeyEvent.VK_W) { 
			startMove("up");
		}
		
		// Player presses down key
		if(k.getKeyCode() == KeyEvent.VK_S) { 
			startMove("down");
		}
	
		// Player presses bar key
		if(k.getKeyCode() == KeyEvent.VK_SPACE) {
			spawningThings.spawnThing(editorType, currentObjectClass, lastMousePos, leftClickStartPoint, selecting);
		}
		
		// Test level
		if(k.getKeyCode() == KeyEvent.VK_Y) {
			testLevel();
		}
	}
	
}