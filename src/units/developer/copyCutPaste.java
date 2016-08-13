package units.developer;

import java.util.ArrayList;

import drawing.drawnObject;
import terrain.chunk;
import units.unit;
import units.unitCommands.positionedCommand;

public class copyCutPaste {
	
	// Copied things
	static ArrayList<drawnObject> copiedThings;
	
	// Copy
	public static void copy() {
		if(developer.selectedThings!=null && developer.selectedThings.size()>0) {
			
			// Make new copied things.
			copiedThings = new ArrayList<drawnObject>();
			
			// Copy them one by one. Depending on the special cases for copy constructors.
			for(int i = 0; i < developer.selectedThings.size(); i++) {
				drawnObject d = developer.selectedThings.get(i);
				drawnObject newThing = d.makeCopy();
				copiedThings.add(newThing);
				newThing.destroy();	
			}
			
			// Make everything relative to the top left thing.
			drawnObject topLeft = drawnObject.getTopLeftFrom(copiedThings);
			for(int i = 0; i < copiedThings.size(); i++) {
				drawnObject currThing = copiedThings.get(i);
				currThing.setRelativeX(currThing.getDoubleX() - topLeft.getDoubleX());
				currThing.setRelativeY(currThing.getDoubleY() - topLeft.getDoubleY());
			}
		}
	}
	
	// Cut
	public static void cut() {
		copy();
		developer.deleteSelected();
	}
	
	// Paste
	public static void paste() {
		if(copiedThings != null && copiedThings.size() > 0) {
			
			// Create the new things from copied things.
			ArrayList<drawnObject> newThings = new ArrayList<drawnObject>();
			for(int i = 0; i < copiedThings.size(); i++) {
				drawnObject d = copiedThings.get(i);
				drawnObject newThing = d.makeCopy();
				newThing.setDoubleX(d.getRelativeX() + developer.lastMousePos.getX());
				newThing.setDoubleY(d.getRelativeY() + developer.lastMousePos.getY());
				newThings.add(newThing);
			
			}
			
			// For units, move the unit commands to be relative.
			for(int i = 0; i < newThings.size(); i++) {
				if(newThings.get(i) instanceof unit) {
					
					// For each unit.
					unit u = (unit)newThings.get(i);
					
					// For each command.
					if(u.getRepeatCommands() != null) {
						for(int j = 0; j < u.getRepeatCommands().size(); j++) {
							if(u.getRepeatCommands().get(j) instanceof positionedCommand) {
								
								// Move the command.
								positionedCommand p = (positionedCommand)u.getRepeatCommands().get(j);
								p.setX(p.getX() + u.getDoubleX() - copiedThings.get(i).getDoubleX());
								p.setY(p.getY() + u.getDoubleY() - copiedThings.get(i).getDoubleY());
								
							}
						}
					}
				}
			}
		}
	}
}