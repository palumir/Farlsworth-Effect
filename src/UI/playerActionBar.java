package UI;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;

import drawing.drawnObject;
import drawing.gameCanvas;
import items.bottle;
import items.item;
import units.player;

public class playerActionBar extends interfaceObject  {
	
	/////////////////////////
	////// DEFAULTS /////////
	/////////////////////////
	
	// Width/height.
	private int DEFAULT_HEALTHBAR_HEIGHT = 20;
	
	// Colors
	public static Color DEFAULT_HEART_COLOR = Color.red;
	public static Color DEFAULT_BORDER_COLOR = new Color(45,45,45);
	public static Color DEFAULT_BOTTLE_COLOR = Color.white;
	public static Color DEFAULT_ENERGY_COLOR = Color.cyan;
	
	///////////////////////
	////// FIELDS /////////
	///////////////////////
	
	///////////////
	/// METHODS ///
	///////////////

	// Constructor
	public playerActionBar(int newX, int newY) {
		super(null, newX, newY, 1, 1);	
	}
	
	// Draw the unit. 
	@Override
	public void drawObject(Graphics g) {
		
		// Set font.
		g.setFont(drawnObject.DEFAULT_FONT);
		
		// Adjustment
		/*int energyAdjustX = 25;
		int energyAdjustY = hpAdjustY + energyChunkSize + energyChunkSize/3;
		
		// Exp
		g.setColor(DEFAULT_ENERGY_COLOR);
		g.drawString("ENE", (int)(gameCanvas.getScaleX()*(getIntX())), (int)(gameCanvas.getScaleY()*(getIntY() + energyAdjustY+12)));

		// Load every chunk.
		currPlayer = player.getCurrentPlayer();
		addPerChunk = 0;
		for(int i = 0; i < currPlayer.getEnergy(); i++) {
			g.fillRect((int)(gameCanvas.getScaleX()*(getIntX() + energyAdjustX + addPerChunk)),
					   (int)(gameCanvas.getScaleY()*(getIntY() + energyAdjustY)),
					   (int)(gameCanvas.getScaleX()*(energyChunkSize)),
					   (int)(gameCanvas.getScaleY()*(DEFAULT_HEALTHBAR_HEIGHT)));
			addPerChunk += energyChunkSize + energyChunkSize/3;
		}*/
				   
		// Draw active item UI.
		int activeAdjustX = 9;
		int activeAdjustY = 0;
		if(player.getPlayer().getPlayerInventory()!=null && player.getPlayer().getPlayerInventory().activeSlots != null) {
			for(int i = 0; i < player.getPlayer().getPlayerInventory().activeSlots.size(); i++) {
				
				item currItem = player.getPlayer().getPlayerInventory().activeSlots.get(i);
				
				
				if(currItem instanceof bottle) {
					bottle b = (bottle)currItem;
					g.setColor(DEFAULT_BOTTLE_COLOR);
					
					// Draw the slot key.
					g.drawString(KeyEvent.getKeyText(currItem.slot),
							(int)(gameCanvas.getScaleX()*(getIntX() + activeAdjustX)),
							(int)(gameCanvas.getScaleY()*(getIntY() + activeAdjustY + b.getImage().getHeight()/2 + 5)));
					
					// Draw bottle image.
					g.drawImage(b.getImage(), 
							(int)(gameCanvas.getScaleX()*(getIntX() + activeAdjustX+5) + g.getFontMetrics().stringWidth(KeyEvent.getKeyText(currItem.slot))), 
							(int)(gameCanvas.getScaleY()*(getIntY() + activeAdjustY)), 
							(int)(gameCanvas.getScaleX()*(b.getImage().getWidth())), 
							(int)(gameCanvas.getScaleY()*(b.getImage().getHeight())), 
							null);
					
					// Draw charges.
					g.drawString(b.getChargesLeft() + "/" + b.getMaxCharges(),
							(int)(gameCanvas.getScaleX()*(getIntX() + activeAdjustX + 30) + g.getFontMetrics().stringWidth(KeyEvent.getKeyText(currItem.slot))),
							(int)(gameCanvas.getScaleY()*(getIntY() + activeAdjustY + b.getImage().getHeight()/2 + 5)));
					
					
				}
				
				activeAdjustX += 0;
				activeAdjustY += 30;
			}
		}
		
	}
	
	// Update unit
	@Override
	public void update() {
	}
	
}
