package drawing.userInterface;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import drawing.drawnObject;
import drawing.gameCanvas;
import drawing.spriteSheet;
import interactions.quest;
import items.bottle;
import units.player;
import utilities.imageUtils;

public class playerHealthBar extends interfaceObject  {
	
	/////////////////////////
	////// DEFAULTS /////////
	/////////////////////////
	
	// Width/height.
	private int DEFAULT_HEALTHBAR_HEIGHT = 20;
	
	// Colors
	public static Color DEFAULT_HEART_COLOR = Color.red;
	public static Color DEFAULT_BORDER_COLOR = new Color(45,45,45);
	public static Color DEFAULT_BOTTLE_COLOR = Color.white;
	public static Color DEFAULT_QUEST_COLOR = Color.white;
	public static Color DEFAULT_ENERGY_COLOR = Color.cyan;
	
	///////////////////////
	////// FIELDS /////////
	///////////////////////
	
	///////////////
	/// METHODS ///
	///////////////

	// Constructor
	public playerHealthBar(int newX, int newY) {
		super(null, newX, newY, 1, 1);	
	}
	
	// Draw the unit. 
	@Override
	public void drawObject(Graphics g) {
		
		// Set font.
		g.setFont(drawnObject.DEFAULT_FONT);
		
		// Chunk width
		int energyChunkSize = 20;
		
		// Adjustment
		int hpAdjustX = 25;
		int hpAdjustY = 10;
		
		// HP
		g.setColor(DEFAULT_HEART_COLOR);
		g.drawString("HP", (int)(gameCanvas.getScaleX()*(getIntX())), (int)(gameCanvas.getScaleY()*(getIntY() + hpAdjustY+12)));

		// Load every chunk.
		player currPlayer = player.getCurrentPlayer();
		int addPerChunk = 0;
		for(int i = 0; i < currPlayer.getHealthPoints(); i++) {
			g.setColor(DEFAULT_BORDER_COLOR);
			imageUtils.drawHeart(g,
					   (int)(gameCanvas.getScaleX()*(getIntX() + hpAdjustX + addPerChunk)-1),
					   (int)(gameCanvas.getScaleY()*(getIntY() + hpAdjustY)-1),
					   (int)(gameCanvas.getScaleX()*(energyChunkSize)+2),
					   (int)(gameCanvas.getScaleY()*(DEFAULT_HEALTHBAR_HEIGHT))+2);
			
			g.setColor(DEFAULT_HEART_COLOR);
			imageUtils.drawHeart(g,
					   (int)(gameCanvas.getScaleX()*(getIntX() + hpAdjustX + addPerChunk)),
					   (int)(gameCanvas.getScaleY()*(getIntY() + hpAdjustY)),
					   (int)(gameCanvas.getScaleX()*(energyChunkSize)),
					   (int)(gameCanvas.getScaleY()*(DEFAULT_HEALTHBAR_HEIGHT)));
			
			addPerChunk += energyChunkSize + energyChunkSize/3;
		}
		
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
				   
		// Draw bottle UI.
		int bottleAdjustX = 9;
		int bottleAdjustY = /*energyAdjustY + energyChunkSize*/ + energyChunkSize/3;
		if(player.getCurrentPlayer().getEquippedBottle() != null) {
			bottleAdjustX += 0;
			bottleAdjustY += 53-25;
			
			bottle b = player.getCurrentPlayer().getEquippedBottle();
			g.setColor(DEFAULT_BOTTLE_COLOR);
			g.drawImage(b.getImage(), 
					(int)(gameCanvas.getScaleX()*(getIntX() + bottleAdjustX)), 
					(int)(gameCanvas.getScaleY()*(getIntY() + bottleAdjustY)), 
					(int)(gameCanvas.getScaleX()*(b.getImage().getWidth())), 
					(int)(gameCanvas.getScaleY()*(b.getImage().getHeight())), 
					null);
			g.drawString(b.getChargesLeft() + "/" + b.getMaxCharges(),
					(int)(gameCanvas.getScaleX()*(getIntX() + bottleAdjustX + 25)),
					(int)(gameCanvas.getScaleY()*(getIntY() + bottleAdjustY + b.getImage().getHeight()/2 + 5)));
		}
		
		// Draw quest UI
		int questAdjustX = gameCanvas.getDefaultWidth() - gameCanvas.getDefaultWidth()/6;
		int questAdjustY = gameCanvas.getDefaultHeight()/45;
		
		g.setColor(DEFAULT_QUEST_COLOR);
		if(quest.getCurrentQuests()!=null) {
			for(int i = 0; i < quest.getCurrentQuests().size(); i++) {
				g.drawString("Quest: " + quest.getCurrentQuests().get(i),
						(int)(gameCanvas.getScaleX()*(getIntX() + questAdjustX - g.getFontMetrics().stringWidth("Quest: " + quest.getCurrentQuests().get(i))/2)),
						(int)(gameCanvas.getScaleY()*(getIntY() + questAdjustY + 5)));
				questAdjustY += 15;
			}
		}
	}
	
	// Update unit
	@Override
	public void update() {
	}
	
}
