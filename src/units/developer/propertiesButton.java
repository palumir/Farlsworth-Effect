package units.developer;

import java.util.ArrayList;
import java.util.Scanner;

import UI.button;
import UI.propertyEditBox;
import drawing.drawnObject;
import drawing.gameCanvas;
import units.unit;
import utilities.utility;

public class propertiesButton {
	
	static button propertiesButton;
	
	// Make unit properties button
	public static void makePropertiesButton() {
		
		// Only show unit properties if one unit is selected
		ArrayList<drawnObject> units = developer.getSelectedUnits();
			
		////////////
		/// Properties
		///////
		
		propertiesButton = new button("Properties","propertiesButton",
				developer.editorMode.getIntX(),
				developer.editorMode.getIntY() + 2*developer.DEFAULT_BUTTON_HEIGHT*4/3,
				developer.DEFAULT_BUTTON_WIDTH,
				developer.DEFAULT_BUTTON_HEIGHT);
		
		// Adding commands only applies to units
		if(units != null && units.size() >= 1) {
			button commandsButton = new button("Add A Command","unitCommandsButton",
					developer.editorMode.getIntX()+100,
					developer.editorMode.getIntY() + 2*developer.DEFAULT_BUTTON_HEIGHT*4/3,
					developer.DEFAULT_BUTTON_WIDTH,
					developer.DEFAULT_BUTTON_HEIGHT);
			propertiesButton.addChild(commandsButton);
			
			developer.unitButtons.add(propertiesButton);
			
			// Show type buttons.
			int j = 0;
			int changeX = 0;
			int changeY = 0;
			for(String s : developer.listOfUnitCommands) {
				
				String noUnits = s.substring(s.indexOf('.')+1,s.length());
				String noCommands = noUnits.substring(noUnits.indexOf('.')+1,noUnits.length());
			
				// No folder
				String noFolder = noCommands.substring(noCommands.indexOf('.')+1,noCommands.length());
				
				// Create each button for each type.
				button unitCommandButton = new button(noFolder,s,
						250 + changeX*100,
						developer.editorMode.getIntY() + 2*developer.DEFAULT_BUTTON_HEIGHT*4/3 + changeY*developer.DEFAULT_BUTTON_HEIGHT*4/3,
						developer.DEFAULT_BUTTON_WIDTH,
						developer.DEFAULT_BUTTON_HEIGHT);
				commandsButton.addChild(unitCommandButton);
				
				j++;
				changeY++;
				if(developer.editorMode.getIntY() + developer.DEFAULT_BUTTON_HEIGHT*4/3 + changeY*developer.DEFAULT_BUTTON_HEIGHT*4/3 >= gameCanvas.getDefaultHeight()) {
					changeY = 0;
					changeX++;
				}
			}
		}
			
		// Edit properties
		button editPropertiesButton = new button("Edit Properties","editProperties",
				developer.editorMode.getIntX()+100,
				developer.editorMode.getIntY() + 3*developer.DEFAULT_BUTTON_HEIGHT*4/3,
				developer.DEFAULT_BUTTON_WIDTH,
				developer.DEFAULT_BUTTON_HEIGHT);
		propertiesButton.addChild(editPropertiesButton);
			
		// For all units, add a x and y property editor.
		button editXButton = new button("X Position","editProperties.x",
				developer.editorMode.getIntX()+200,
				developer.editorMode.getIntY()+3*developer.DEFAULT_BUTTON_HEIGHT*4/3,
				developer.DEFAULT_BUTTON_WIDTH,
				developer.DEFAULT_BUTTON_HEIGHT);
		editPropertiesButton.addChild(editXButton);
			
		button editYButton = new button("Y Position","editProperties.y",
				developer.editorMode.getIntX()+200,
				developer.editorMode.getIntY()+4*developer.DEFAULT_BUTTON_HEIGHT*4/3,
				developer.DEFAULT_BUTTON_WIDTH,
				developer.DEFAULT_BUTTON_HEIGHT);
		editPropertiesButton.addChild(editYButton);
		
		button editMoveSpeedButton = new button("Movespeed","editProperties.moveSpeed",
				developer.editorMode.getIntX()+200,
				developer.editorMode.getIntY()+4*developer.DEFAULT_BUTTON_HEIGHT*4/3,
				developer.DEFAULT_BUTTON_WIDTH,
				developer.DEFAULT_BUTTON_HEIGHT);
		editPropertiesButton.addChild(editMoveSpeedButton);
	}
	
	public static void saveProperty(String b, String value) {
		
		// x value
		if(b.contains("editProperties.x")) {
			int diff = (int) (Integer.parseInt(value) - saveThing.getDoubleX());
			for(int i = 0; i < saveThings.size(); i++) {
				saveThings.get(i).setDoubleX(saveThings.get(i).getDoubleX() + diff);
				if(saveThings.get(i) instanceof unit) ((unit) saveThings.get(i)).setRiseRun();
			}
		}
		
		// y value
		if(b.contains("editProperties.y")) {
			int diff = (int) (Integer.parseInt(value) - saveThing.getDoubleY());
			for(int i = 0; i < saveThings.size(); i++) {
				saveThings.get(i).setDoubleY(saveThings.get(i).getDoubleY() + diff);
				if(saveThings.get(i) instanceof unit) ((unit) saveThings.get(i)).setRiseRun();
			}
		}
		
		// moveSpeed
		if(b.contains("editProperties.moveSpeed")) {
			double newSpeed = Double.parseDouble(value);
			for(int i = 0; i < saveThings.size(); i++) {
				if(saveThings.get(i) instanceof unit) { 
					((unit) saveThings.get(i)).setMoveSpeed((float)newSpeed);
					((unit) saveThings.get(i)).setRiseRun();
				}
			}
		}
		
	}
	
	// The thing we are editing
	static drawnObject saveThing;
	static ArrayList<drawnObject> saveThings;
	
	public static void editPropertiesButtons(button b) {
		
		// cancel
		if(b.getButtonID().contains("editProperties") && b.getButtonID().contains(".cancel")) {
			propertyEditBox.currentBox.destroy();
			saveThing = null;
		}
		
		// save
		if(b.getButtonID().contains("editProperties") && b.getButtonID().contains(".okay")) {
			saveProperty(b.getButtonID(), propertyEditBox.currentBox.getCurrentValue());
			propertyEditBox.currentBox.destroy();
			saveThing = null;
		}
		
		// x value
		if(b.getButtonID().equals("editProperties.x")) {
			ArrayList<drawnObject> objects = developer.getSelectedUnits();
			saveThings = new ArrayList<drawnObject>(developer.selectedThings);
			if(objects != null && objects.size() > 0 && developer.containsUnitCommand()) {
				saveThings.removeAll(objects);
			}
			saveThing = drawnObject.getTopLeftFrom(saveThings);
			propertyEditBox p = new propertyEditBox(b.getButtonID(),b.getButtonID(), saveThing.getIntX() + "");
		}
		
		// y value
		if(b.getButtonID().equals("editProperties.y")) {
			ArrayList<drawnObject> objects = developer.getSelectedUnits();
			saveThings = new ArrayList<drawnObject>(developer.selectedThings);
			if(objects != null && objects.size() > 0 && developer.containsUnitCommand()) {
				saveThings.removeAll(objects);
			}
			saveThing = drawnObject.getTopLeftFrom(saveThings);
			propertyEditBox p = new propertyEditBox(b.getButtonID(),b.getButtonID(), saveThing.getIntY() + "");
		}
		
		// moveSpeed
		if(b.getButtonID().equals("editProperties.moveSpeed")) {
			ArrayList<drawnObject> objects = developer.getSelectedUnits();
			saveThings = new ArrayList<drawnObject>(developer.selectedThings);
			if(objects != null && objects.size() > 0 && developer.containsUnitCommand()) {
				saveThings.removeAll(objects);
			}
			saveThing = drawnObject.getTopLeftFrom(saveThings);
			propertyEditBox p = new propertyEditBox(b.getButtonID(),b.getButtonID(), ((unit)saveThing).getMoveSpeed() + "");
		}
	}
}