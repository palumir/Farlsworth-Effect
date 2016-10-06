package UI.menus;

import UI.button;
import drawing.gameCanvas;
import items.bottles.saveBottle;
import main.main;
import sounds.music;
import units.player;

public class deathMenu extends UI.menu {
	
	// Current deathMenu.
	public static deathMenu menu;
	
	// Button defaults.
	static int DEFAULT_BUTTON_WIDTH = 140;
	static int DEFAULT_BUTTON_HEIGHT = 40;
	
	// Buttons for death.
	button respawnAtWell;
	public button respawnAtSaveBottle;
	
	// Death menu
	public deathMenu() {
		super();
		
		// Destroy old menu.
		if(menu != null) menu.destroy();
		
		// Set new menu.
		menu = this;
		
		// Create buttons.
		respawnAtSaveBottle = new button("Respawn at Last Save Point (Press E)","respawnAtSaveBottle",
				gameCanvas.getDefaultWidth()/2 - (DEFAULT_BUTTON_WIDTH+100)/2, 
				gameCanvas.getDefaultHeight() - 300,
				(DEFAULT_BUTTON_WIDTH+100), 
				DEFAULT_BUTTON_HEIGHT);
		this.add(respawnAtSaveBottle);
		if(player.getPlayer().lastWell != null && player.getPlayer().getPlayerInventory().containsBottleType(saveBottle.class)) { 
			respawnAtWell = new button("Respawn at Last Well","respawnAtWell",
				gameCanvas.getDefaultWidth()/2 - (DEFAULT_BUTTON_WIDTH+100)/2, 
				gameCanvas.getDefaultHeight() - 230,
				(DEFAULT_BUTTON_WIDTH+100), 
				DEFAULT_BUTTON_HEIGHT);
			this.add(respawnAtWell);
		}
	}
	
	// Select button
	public void selectButton(button b) {
		
		// Bottle button
		if(b.getButtonID().contains("respawnAtSaveBottle")) {
			music.playerDied();
			main.restartGame("respawnAtSaveBottle");
		}
		
		// Well button.
		if(b.getButtonID().contains("respawnAtWell")) {
			music.playerDied();
			main.restartGame("respawnAtWell");
		}
		
	}

	
}