package doodads.sheepFarm;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import drawing.gameCanvas;
import modes.mode;
import terrain.chunk;
import terrain.generalChunkType;
import utilities.time;

public class clawMarkBlack extends chunk {
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default name.
	private static String DEFAULT_CHUNK_NAME = "clawMarkBlack";
	
	// Sprite stuff.
	private static String DEFAULT_CHUNK_SPRITESHEET = "images/units/bosses/denmother/"+ DEFAULT_CHUNK_NAME + ".png";
	
	// Dimensions
	public static int DEFAULT_CHUNK_WIDTH = 32;
	public static int DEFAULT_CHUNK_HEIGHT = 32;
	
	// The actual type.
	private static generalChunkType typeReference = new generalChunkType(DEFAULT_CHUNK_NAME, DEFAULT_CHUNK_SPRITESHEET, DEFAULT_CHUNK_WIDTH, DEFAULT_CHUNK_HEIGHT);  
	
	// Fade the claw in
	private long clawSpawnStart = 0;
	private float clawSpawnTime = 0.4f;
	
	///////////////
	/// METHODS /// 
	///////////////
	// Constructor
	public clawMarkBlack(int newX, int newY, int i) {
		super(typeReference, newX, newY, i, 0);
		
		// Claw spawned now.
		clawSpawnStart = time.getTime();
		
		// Set height/width.
		if(mode.getCurrentMode().equals("topDown")) {
			setHitBoxAdjustmentY(0);
			setHeight(DEFAULT_CHUNK_HEIGHT);
			setWidth(DEFAULT_CHUNK_WIDTH);
		}
		else {
			setHitBoxAdjustmentY(0);
			setHeight(DEFAULT_CHUNK_HEIGHT);
			setWidth(DEFAULT_CHUNK_WIDTH);
		}
		setPassable(true);
	}
	
	@Override
	public void drawObject(Graphics g) {	
		// Draw it. 
		if(chunkImage != null && this.isDrawObject()) {
			
			// Scaling.
			int changeFactor = 0;
			if(gameCanvas.getScaleX() != 1f || gameCanvas.getScaleY() != 1f) changeFactor = 1;
			
			// Create alpha based on time elapsed.
			float alpha = (time.getTime() - clawSpawnStart)/(clawSpawnTime*1000);
			if(alpha>=1) alpha = 1;
			
			// Fade in image.
			Graphics2D g2d = (Graphics2D) g.create();
			g2d.setComposite(AlphaComposite.SrcOver.derive(alpha));
			g2d.drawImage(chunkImage, 
					getDrawX(), 
					getDrawY(), 
					(int)(gameCanvas.getScaleX()*chunkImage.getWidth() + changeFactor), 
					(int)(gameCanvas.getScaleY()*chunkImage.getHeight() + changeFactor), 
					null);
			
			// Draw the outskirts of the sprite.
			if(showSpriteBox) {
				g.setColor(Color.red);
				g.drawRect(getDrawX(),
						   getDrawY(), 
						   (int)(gameCanvas.getScaleX()*getObjectSpriteSheet().getSpriteWidth()), 
						   (int)(gameCanvas.getScaleY()*getObjectSpriteSheet().getSpriteHeight()));
			}
			
			// Draw the hitbox of the image in green.
			if(showHitBox) {
				g.setColor(Color.green);
				g.drawRect(getDrawX() - (int)(gameCanvas.getScaleX()*(- (getObjectSpriteSheet().getSpriteWidth()/2 - getWidth()/2) - getHitBoxAdjustmentX())),
						   getDrawY() - (int)(gameCanvas.getScaleY()*(- (getObjectSpriteSheet().getSpriteHeight()/2 - getHeight()/2) - getHitBoxAdjustmentY())), 
						   (int)(gameCanvas.getScaleX()*getWidth()), 
						   (int)(gameCanvas.getScaleY()*getHeight()));
			}
			
			// Draw the x,y coordinates of the unit.
			if(showUnitPosition) {
				g.setColor(Color.white);
				g.drawString((int)(gameCanvas.getScaleX()*getIntX()) + "," + (int)(gameCanvas.getScaleX()*getIntY()),
						   getDrawX(),
						   getDrawY());
			}
		}
	}
	
}
