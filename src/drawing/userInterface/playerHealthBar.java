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
	public static Color DEFAULT_HEALTH_COLOR = new Color(6,228,1);
	public static Color DEFAULT_LOST_HEALTH_COLOR = Color.red;
	public static Color DEFAULT_BORDER_COLOR = new Color(85,58,30);
	public static Color DEFAULT_EXP_COLOR = new Color(239,255,40);
	public static Color DEFAULT_LOST_EXP_COLOR = new Color(15,15,0);
	public static Color DEFAULT_LEVEL_COLOR = new Color(64,48,38);
	public static Color DEFAULT_OUTOF_COLOR = new Color(100,48,38);
	public static Color DEFAULT_BOTTLE_COLOR = Color.white;
	public static Color DEFAULT_QUEST_COLOR = Color.white;
	
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
			healthChunkSize = (int)(((float)player.getCurrentPlayer().getHealthPoints()/(float)player.getCurrentPlayer().getMaxHealthPoints())*DEFAULT_HEALTHBAR_WIDTH);
			expChunkSize = (int)((float)player.getCurrentPlayer().getExpIntoLevel()/(float)player.expRequiredForLevel()*DEFAULT_HEALTHBAR_WIDTH);
		}

		// Draw the background.
		g.drawImage(background, 
				(int)(gameCanvas.getScaleX()*(getX())), 
				(int)(gameCanvas.getScaleY()*(getY())), 
				(int)(gameCanvas.getScaleX()*(background.getWidth())), 
				(int)(gameCanvas.getScaleY()*(background.getHeight())), 
				null);
		
		// Draw player level.
		int levelAdjustX = 9;
		int levelAdjustY = 22;
		g.setColor(DEFAULT_LEVEL_COLOR);
		g.drawString("Level", (int)(gameCanvas.getScaleX()*(getX()+16+levelAdjustX))-g.getFontMetrics().stringWidth("Level")/2, 
				              (int)(gameCanvas.getScaleY()*(getY()+levelAdjustY+1)));
		
		String levelText = "" + player.getCurrentPlayer().getPlayerLevel();
		g.drawString(levelText, (int)(gameCanvas.getScaleX()*(getX()+levelAdjustX + 16)) - g.getFontMetrics().stringWidth(levelText)/2, 
				(int)(gameCanvas.getScaleY()*(getY()+levelAdjustY+14)));
		
		// Adjustment
		int hpAdjustX = 71;
		int hpAdjustY = 10;
		
		// HP
		g.setColor(DEFAULT_HEALTH_COLOR);
		g.drawString("HP", (int)(gameCanvas.getScaleX()*(getX()+55)), (int)(gameCanvas.getScaleY()*(getY() + hpAdjustY+12)));
		
		// Draw the red.
		g.setColor(DEFAULT_LOST_HEALTH_COLOR);
		g.fillRect((int)(gameCanvas.getScaleX()*(getX() + hpAdjustX)),
				   (int)(gameCanvas.getScaleY()*(getY() + hpAdjustY)),
				   (int)(gameCanvas.getScaleX()*(DEFAULT_HEALTHBAR_WIDTH)),
		           (int)(gameCanvas.getScaleY()*(DEFAULT_HEALTHBAR_HEIGHT)));
		
		// Draw the green chunks.
		g.setColor(DEFAULT_HEALTH_COLOR);
		g.fillRect((int)(gameCanvas.getScaleX()*(getX()+ hpAdjustX)),
				   (int)(gameCanvas.getScaleY()*(getY() + hpAdjustY)),
				   (int)(gameCanvas.getScaleX()*(healthChunkSize)),
				   (int)(gameCanvas.getScaleY()*(DEFAULT_HEALTHBAR_HEIGHT)));
		
		// Draw health number
		g.setColor(DEFAULT_OUTOF_COLOR);
		String hpOutOfText = player.getCurrentPlayer().getHealthPoints() + "/" + player.getCurrentPlayer().getMaxHealthPoints(); 
		g.drawString(hpOutOfText, 
				(int)(gameCanvas.getScaleX()*(getX() + hpAdjustX  + DEFAULT_HEALTHBAR_WIDTH/2)) - g.getFontMetrics().stringWidth(hpOutOfText)/2, 
				(int)(gameCanvas.getScaleY()*(getY() + hpAdjustY + 5 + DEFAULT_HEALTHBAR_HEIGHT/2)));
		
		// Draw border.
		g.setColor(DEFAULT_BORDER_COLOR);
		g.drawRect((int)(gameCanvas.getScaleX()*(getX() + hpAdjustX)),
				   (int)(gameCanvas.getScaleY()*(getY() + hpAdjustY)),
				   (int)(gameCanvas.getScaleX()*(DEFAULT_HEALTHBAR_WIDTH)),
				   (int)(gameCanvas.getScaleY()*(DEFAULT_HEALTHBAR_HEIGHT)));
		
		// Adjustment
		int expAdjustX = 71;
		int expAdjustY = 28;
		
		// Exp
		g.setColor(DEFAULT_EXP_COLOR);
		g.drawString("EXP", (int)(gameCanvas.getScaleX()*(getX()+48)), (int)(gameCanvas.getScaleY()*(getY() + expAdjustY+12)));
		
		// Draw the red.
		g.setColor(DEFAULT_LOST_EXP_COLOR);
		g.fillRect((int)(gameCanvas.getScaleX()*(getX() + expAdjustX)),
				   (int)(gameCanvas.getScaleY()*(getY() + expAdjustY)),
				   (int)(gameCanvas.getScaleX()*(DEFAULT_HEALTHBAR_WIDTH)),
				   (int)(gameCanvas.getScaleY()*(DEFAULT_HEALTHBAR_HEIGHT)));
		
		// Draw the yellow chunks.
		g.setColor(DEFAULT_EXP_COLOR);
		g.fillRect((int)(gameCanvas.getScaleX()*(getX() + expAdjustX)),
				   (int)(gameCanvas.getScaleY()*(getY() + expAdjustY)),
				   (int)(gameCanvas.getScaleX()*(expChunkSize)),
				   (int)(gameCanvas.getScaleY()*(DEFAULT_HEALTHBAR_HEIGHT)));
		
		// Draw health number
		g.setColor(DEFAULT_OUTOF_COLOR);
		String expOutOfText = player.getCurrentPlayer().getExpIntoLevel() + "/" + player.expRequiredForLevel(); 
		g.drawString(expOutOfText, 
						(int)(gameCanvas.getScaleX()*(getX() + expAdjustX + DEFAULT_HEALTHBAR_WIDTH/2)) - g.getFontMetrics().stringWidth(expOutOfText)/2, 
						(int)(gameCanvas.getScaleY()*(getY() + expAdjustY + 5 + DEFAULT_HEALTHBAR_HEIGHT/2)));
		
		// Draw border.
		g.setColor(DEFAULT_BORDER_COLOR);
		g.drawRect((int)(gameCanvas.getScaleX()*(getX() + expAdjustX)),
				   (int)(gameCanvas.getScaleY()*(getY() + expAdjustY)),
				   (int)(gameCanvas.getScaleX()*(DEFAULT_HEALTHBAR_WIDTH)),
				   (int)(gameCanvas.getScaleY()*(DEFAULT_HEALTHBAR_HEIGHT)));
		
		// Draw bottle UI.
		int bottleAdjustX = 10;
		int bottleAdjustY = 25;
		if(player.getCurrentPlayer().getEquippedBottle() != null) {
			bottleAdjustX += 0;
			bottleAdjustY += 53-25;
			
			bottle b = player.getCurrentPlayer().getEquippedBottle();
			g.setColor(DEFAULT_BOTTLE_COLOR);
			g.drawImage(b.getImage(), 
					(int)(gameCanvas.getScaleX()*(getX() + bottleAdjustX)), 
					(int)(gameCanvas.getScaleY()*(getY() + bottleAdjustY)), 
					(int)(gameCanvas.getScaleX()*(b.getImage().getWidth())), 
					(int)(gameCanvas.getScaleY()*(b.getImage().getHeight())), 
					null);
			g.drawString(b.getChargesLeft() + "/" + b.getMaxCharges(),
					(int)(gameCanvas.getScaleX()*(getX() + bottleAdjustX + 25)),
					(int)(gameCanvas.getScaleY()*(getY() + bottleAdjustY + b.getImage().getHeight()/2 + 5)));
		}
		
		// Draw quest UI
		int questAdjustX = bottleAdjustX;
		int questAdjustY = bottleAdjustY + 38;
		
		g.setColor(DEFAULT_QUEST_COLOR);
		if(quest.getCurrentQuests()!=null) {
			for(int i = 0; i < quest.getCurrentQuests().size(); i++) {
				g.drawString("Quest: " + quest.getCurrentQuests().get(i),
						(int)(gameCanvas.getScaleX()*(getX() + questAdjustX)),
						(int)(gameCanvas.getScaleY()*(getY() + questAdjustY + 5)));
				questAdjustY += 15;
			}
		}
	}
	
	// Update unit
	@Override
	public void update() {
	}
	
}
