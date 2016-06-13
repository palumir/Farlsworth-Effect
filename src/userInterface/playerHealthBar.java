package userInterface;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import drawing.drawnObject;
import drawing.spriteSheet;
import units.player;

public class playerHealthBar extends interfaceObject  {
	
	/////////////////////////
	////// DEFAULTS /////////
	/////////////////////////
	
	// Background.
	public static BufferedImage background = spriteSheet.getSpriteFromFilePath("images/interface/healthBar.png");
	
	// Width/height.
	private int DEFAULT_HEALTHBAR_WIDTH = 130;
	private int DEFAULT_HEALTHBAR_HEIGHT = 15;
	
	// Colors
	private Color DEFAULT_HEALTH_COLOR = new Color(6,228,1);
	private Color DEFAULT_LOST_HEALTH_COLOR = Color.red;
	private Color DEFAULT_BORDER_COLOR = new Color(85,58,30);
	private Color DEFAULT_EXP_COLOR = new Color(239,255,40);
	private Color DEFAULT_LOST_EXP_COLOR = new Color(15,15,0);
	private Color DEFAULT_LEVEL_COLOR = new Color(64,48,38);
	private Color DEFAULT_OUTOF_COLOR = new Color(100,48,38);
	
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
		int healthChunkSize = 1;
		int expChunkSize = 1;
		if(player.getCurrentPlayer()!=null) {
			healthChunkSize = (int)((float)player.getCurrentPlayer().getHealthPoints()/player.getCurrentPlayer().getMaxHealthPoints())*DEFAULT_HEALTHBAR_WIDTH;
			expChunkSize = (int)((float)player.getCurrentPlayer().getExpIntoLevel()/(float)player.expRequiredForLevel()*DEFAULT_HEALTHBAR_WIDTH);
		}

		
		// Draw the background.
		g.drawImage(background, 
				getX(), 
				getY(), 
				background.getWidth(), 
				background.getHeight(), 
				null);
		
		// Draw player level.
		int levelAdjustX = 9;
		int levelAdjustY = 22;
		g.setColor(DEFAULT_LEVEL_COLOR);
		g.drawString("Level", getX()+16+levelAdjustX-g.getFontMetrics().stringWidth("Level")/2, getY()+levelAdjustY+1);
		
		String levelText = "" + player.getCurrentPlayer().getPlayerLevel();
		g.drawString(levelText, getX()+levelAdjustX + 16 - g.getFontMetrics().stringWidth(levelText)/2, getY()+levelAdjustY+14);
		
		// Adjustment
		int hpAdjustX = 71;
		int hpAdjustY = 10;
		
		// HP
		g.setColor(DEFAULT_HEALTH_COLOR);
		g.drawString("HP", getX()+55, getY() + hpAdjustY+12);
		
		// Draw the red.
		g.setColor(DEFAULT_LOST_HEALTH_COLOR);
		g.fillRect(getX() + hpAdjustX,
				   getY() + hpAdjustY,
				   DEFAULT_HEALTHBAR_WIDTH,
				   DEFAULT_HEALTHBAR_HEIGHT);
		
		// Draw the green chunks.
		g.setColor(DEFAULT_HEALTH_COLOR);
		g.fillRect(getX()+ hpAdjustX,
				   getY() + hpAdjustY,
				   healthChunkSize,
				   DEFAULT_HEALTHBAR_HEIGHT);
		
		// Draw health number
		g.setColor(DEFAULT_OUTOF_COLOR);
		String hpOutOfText = player.getCurrentPlayer().getHealthPoints() + "/" + player.getCurrentPlayer().getMaxHealthPoints(); 
		g.drawString(hpOutOfText, 
				getX() + hpAdjustX - g.getFontMetrics().stringWidth(hpOutOfText)/2 + DEFAULT_HEALTHBAR_WIDTH/2, 
				getY() + hpAdjustY + 5 + DEFAULT_HEALTHBAR_HEIGHT/2);
		
		// Draw border.
		g.setColor(DEFAULT_BORDER_COLOR);
		g.drawRect(getX() + hpAdjustX,
				   getY() + hpAdjustY,
				   DEFAULT_HEALTHBAR_WIDTH,
				   DEFAULT_HEALTHBAR_HEIGHT);
		
		// Adjustment
		int expAdjustX = 71;
		int expAdjustY = 28;
		
		// Exp
		g.setColor(DEFAULT_EXP_COLOR);
		g.drawString("EXP", getX()+48, getY() + expAdjustY+12);
		
		// Draw the red.
		g.setColor(DEFAULT_LOST_EXP_COLOR);
		g.fillRect(getX() + expAdjustX,
				   getY() + expAdjustY,
				   DEFAULT_HEALTHBAR_WIDTH,
				   DEFAULT_HEALTHBAR_HEIGHT);
		
		// Draw the yellow chunks.
		g.setColor(DEFAULT_EXP_COLOR);
		g.fillRect(getX() + expAdjustX,
					   getY() + expAdjustY,
					   expChunkSize,
					   DEFAULT_HEALTHBAR_HEIGHT);
		
		// Draw health number
				g.setColor(DEFAULT_OUTOF_COLOR);
				String expOutOfText = player.getCurrentPlayer().getExpIntoLevel() + "/" + player.expRequiredForLevel(); 
				g.drawString(expOutOfText, 
						getX() + expAdjustX - g.getFontMetrics().stringWidth(expOutOfText)/2 + DEFAULT_HEALTHBAR_WIDTH/2, 
						getY() + expAdjustY + 5 + DEFAULT_HEALTHBAR_HEIGHT/2);
		
		// Draw border.
		g.setColor(DEFAULT_BORDER_COLOR);
		g.drawRect(getX() + expAdjustX,
				   getY() + expAdjustY,
				   DEFAULT_HEALTHBAR_WIDTH,
				   DEFAULT_HEALTHBAR_HEIGHT);
	}
	
	// Update unit
	@Override
	public void update() {
	}
	
}
