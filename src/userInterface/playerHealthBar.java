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
	private Color DEFAULT_HEALTH_COLOR = Color.green;
	private Color DEFAULT_LOST_HEALTH_COLOR = Color.red;
	private Color DEFAULT_BORDER_COLOR = new Color(85,58,30);
	private Color DEFAULT_EXP_COLOR = Color.yellow;
	private Color DEFAULT_LOST_EXP_COLOR = Color.black;
	private Color DEFAULT_LEVEL_COLOR = Color.black;
	private Color DEFAULT_OUTOF_COLOR = new Color(151,75,0);
	
	///////////////////////
	////// FIELDS /////////
	///////////////////////
	private int maxHealth;
	private int health;
	
	///////////////
	/// METHODS ///
	///////////////

	// Constructor
	public playerHealthBar(int newHealth, int newMaxHealth, int newX, int newY) {
		super(null, newX, newY, 1, 1);	
		
		// Set fields.
		setHealth(newHealth);
		setMaxHealth(newMaxHealth);
	}
	
	// Draw the unit. 
	@Override
	public void drawObject(Graphics g) {
		
		// Chunk width
		int healthChunkSize = DEFAULT_HEALTHBAR_WIDTH/getMaxHealth();
		int expChunkSize = DEFAULT_HEALTHBAR_WIDTH/player.expRequiredForLevel();
		
		// Draw the background.
		g.drawImage(background, 
				getX(), 
				getY(), 
				background.getWidth(), 
				background.getHeight(), 
				null);
		
		// Draw player level.
		int levelAdjustX = 9;
		int levelAdjustY = 24;
		g.setColor(DEFAULT_LEVEL_COLOR);
		g.drawString("Level", getX()+levelAdjustX, getY()+levelAdjustY);
		
		String levelText = "" + player.getCurrentPlayer().getPlayerLevel();
		g.drawString(levelText, getX()+levelAdjustX + 16 - g.getFontMetrics().stringWidth(levelText)/2, getY()+levelAdjustY+15);
		
		// Adjustment
		int hpAdjustX = 71;
		int hpAdjustY = 10;
		
		// HP
		g.setColor(DEFAULT_HEALTH_COLOR);
		g.drawString("HP", getX()+47, getY() + hpAdjustY+12);
		
		// Draw the red.
		g.setColor(DEFAULT_LOST_HEALTH_COLOR);
		g.fillRect(getX() + hpAdjustX,
				   getY() + hpAdjustY,
				   DEFAULT_HEALTHBAR_WIDTH,
				   DEFAULT_HEALTHBAR_HEIGHT);
		
		// Draw the green chunks.
		for(int i = 0; i < getHealth(); i++) {
			g.setColor(DEFAULT_HEALTH_COLOR);
			g.fillRect(getX() + healthChunkSize*i + hpAdjustX,
					   getY() + hpAdjustY,
					   healthChunkSize,
					   DEFAULT_HEALTHBAR_HEIGHT);
		}
		
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
		g.drawString("EXP", getX()+47, getY() + expAdjustY+12);
		
		// Draw the red.
		g.setColor(DEFAULT_LOST_EXP_COLOR);
		g.fillRect(getX() + expAdjustX,
				   getY() + expAdjustY,
				   DEFAULT_HEALTHBAR_WIDTH,
				   DEFAULT_HEALTHBAR_HEIGHT);
		
		// Draw the yellow chunks.
		for(int i = 0; i < player.getCurrentPlayer().getExpIntoLevel(); i++) {
			g.setColor(DEFAULT_EXP_COLOR);
			g.fillRect(getX() + expChunkSize*i + expAdjustX,
					   getY() + expAdjustY,
					   expChunkSize,
					   DEFAULT_HEALTHBAR_HEIGHT);
		}
		
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

	public int getHealth() {
		return health;
	}

	public void setHealth(int health) {
		this.health = health;
	}

	public int getMaxHealth() {
		return maxHealth;
	}

	public void setMaxHealth(int maxHealth) {
		this.maxHealth = maxHealth;
	}
	
}
