package UI;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import drawing.gameCanvas;
import drawing.spriteSheet;
import units.boss;

public class bossHealthBar extends interfaceObject  {
	
	/////////////////////////
	////// DEFAULTS /////////
	/////////////////////////
	
	// Background.
	public static BufferedImage background = spriteSheet.getSpriteFromFilePath("images/interface/healthBar.png");
	
	// Width/height.
	private int DEFAULT_HEALTHBAR_WIDTH = gameCanvas.getDefaultWidth() - 50;
	private int DEFAULT_HEALTHBAR_HEIGHT = 13;
	
	// X and Y
	public static int DEFAULT_X = 25;
	public static int DEFAULT_Y = gameCanvas.getDefaultHeight() - 30;
	
	// Colors
	public static Color DEFAULT_HEALTH_COLOR = new Color(6,228,1);
	public static Color DEFAULT_LOST_HEALTH_COLOR = Color.red;
	public static Color DEFAULT_BORDER_COLOR = new Color(85,58,30);
	public static Color DEFAULT_LEVEL_COLOR = new Color(64,48,38);
	public static Color DEFAULT_NAME_COLOR = Color.white;
	
	///////////////////////
	////// FIELDS /////////
	///////////////////////
	private boss b;
	
	///////////////
	/// METHODS ///
	///////////////

	// Constructor
	public bossHealthBar(boss newBoss) {
		super(null, DEFAULT_X, DEFAULT_Y, 1, 1);	
		b = newBoss;
	}
	
	// Draw the unit. 
	@Override
	public void drawObject(Graphics g) {
		
		// Chunk width
		int healthChunkSize = 1;
		if(b!=null) {
			healthChunkSize = (int)(((float)b.getHealthPoints()/(float)b.getMaxHealthPoints())*DEFAULT_HEALTHBAR_WIDTH);
		}
		
		// Adjustment
		int hpAdjustX = 0;
		int hpAdjustY = 0;
		
		// Set font
		Font currentFont = g.getFont();
		g.setFont(currentFont.deriveFont(currentFont.getSize() * 1.5F));
		
		// Draw name.
		g.setColor(DEFAULT_NAME_COLOR);
		g.drawString(b.getDisplayName(),
				   (int)(gameCanvas.getScaleX()*(getIntX() + hpAdjustX + DEFAULT_HEALTHBAR_WIDTH/2) - g.getFontMetrics().stringWidth(b.getDisplayName())/2),
				   (int)(gameCanvas.getScaleY()*(getIntY() + hpAdjustY - 5)));
		
		// Draw the red.
		g.setColor(DEFAULT_LOST_HEALTH_COLOR);
		g.fillRect((int)(gameCanvas.getScaleX()*(getIntX() + hpAdjustX)),
				   (int)(gameCanvas.getScaleY()*(getIntY() + hpAdjustY)),
				   (int)(gameCanvas.getScaleX()*(DEFAULT_HEALTHBAR_WIDTH)),
		           (int)(gameCanvas.getScaleY()*(DEFAULT_HEALTHBAR_HEIGHT)));
		
		// Draw the green chunks.
		g.setColor(DEFAULT_HEALTH_COLOR);
		g.fillRect((int)(gameCanvas.getScaleX()*(getIntX()+ hpAdjustX)),
				   (int)(gameCanvas.getScaleY()*(getIntY() + hpAdjustY)),
				   (int)(gameCanvas.getScaleX()*(healthChunkSize)),
				   (int)(gameCanvas.getScaleY()*(DEFAULT_HEALTHBAR_HEIGHT)));
		
		// Draw border.
		g.setColor(DEFAULT_BORDER_COLOR);
		g.drawRect((int)(gameCanvas.getScaleX()*(getIntX() + hpAdjustX)),
				   (int)(gameCanvas.getScaleY()*(getIntY() + hpAdjustY)),
				   (int)(gameCanvas.getScaleX()*(DEFAULT_HEALTHBAR_WIDTH)),
				   (int)(gameCanvas.getScaleY()*(DEFAULT_HEALTHBAR_HEIGHT)));	
		
		
	}
	
	// Update unit
	@Override
	public void update() {
		if(b.getHealthPoints() <= 0) {
			this.destroy();
		}
	}
	
}
