package UI;

import java.awt.event.MouseEvent;
import java.util.ArrayList;

import UI.button;
import drawing.drawnObject;
import drawing.gameCanvas;
import main.main;
import sounds.music;

public abstract class menu extends ArrayList<drawnObject> {
	
	// List of menus
	public static ArrayList<menu> listOfMenus;
	
	// Death menu
	public menu() {
		super();
		
		if(listOfMenus == null) listOfMenus = new ArrayList<menu>();
		listOfMenus.add(this);
	}
	
	// Select button
	public abstract void selectButton(button b);
	
	// Mouse pressed.
	public static void mousePressed(MouseEvent e) {
		
		button touchedButton = button.getButtonAt(e.getX(), e.getY());
		
		// If we have touched a button
		if(touchedButton != null) {
			menu m = getMenuFor(touchedButton);
			if(m != null) {
				m.selectButton(touchedButton);
			}
		}
	}
	
	// Mouse released 
	public static void mouseReleased(MouseEvent e) {
	}
	
	// Get menu for a specific button
	public static menu getMenuFor(button b) {
		
		if(listOfMenus != null) {
			
			for(int i = 0; i < listOfMenus.size(); i++) {
				if(listOfMenus.get(i).contains(b)) return listOfMenus.get(i);
			}
			
		}
		
		return null;
	}
	
	// Destroy
	public void destroy() {
		for(int i = 0; i < size(); i++) {
			get(i).destroy();
		}
		listOfMenus.remove(this);
	}
	
}