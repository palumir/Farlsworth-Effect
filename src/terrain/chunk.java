package terrain;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import drawing.drawnObject;
import drawing.spriteSheet;
import utilities.intTuple;

public class chunk extends drawnObject {
	////////////////
	/// DEFAULTS ///
	////////////////
	// All chunks
	public static ArrayList<chunk> allChunks;
	
	// Default passable boolean
	private boolean DEFAULT_PASSABLE = true;
	
	//////////////
	/// FIELDS ///
	//////////////
	// The image of the chunk.
	protected BufferedImage chunkImage;
	
	// Is the chunk passable or impassable?
	private boolean passable;

	//////////////
	/// MEHODS ///
	//////////////
	// Constructor for choosing a random variation of the chunk.
	public chunk(chunkType c, int newX, int newY) {
		super(c.getChunkTypeSpriteSheet(), newX, newY, c.getWidth(), c.getHeight());
		
		// Set our image field.
		chunkImage = c.getChunkImage();
		
		// Set default passable
		passable = DEFAULT_PASSABLE;
		
		// Remember our chunks.
		allChunks.add(this);
	}
	
	// Constructor for choosing a given variation of the chunk.
	public chunk(chunkType c, int newX, int newY, int i, int j) {
		super(c.getChunkTypeSpriteSheet(), newX, newY, c.getWidth(), c.getHeight());
		
		// Set our image field.
		chunkImage = c.getChunkImage(i, j);
		
		// Set default passable
		passable = DEFAULT_PASSABLE;
		
		// Remember our chunks.
		allChunks.add(this);
	}
	
	// Check if a unit collides with any chunk. Returns by how much.
	public static intTuple collidesWith(drawnObject u, int newX, int newY) {
		
		// Check if it collides in x or y position.
		boolean tX = false;
		boolean tY = false;
		
		if(allChunks != null) {
			
			// Scan all chunks.
			for(int i = 0; i < allChunks.size(); i++) {
				
				// If the chunk is passable, just ignore it. 
				if(!allChunks.get(i).isPassable()) {
					
					// If we collide with one, return the tuple containing by how much.
					if(u.collides(newX, u.getY(), allChunks.get(i))) tX = true;
					if(u.collides(u.getX(), newY, allChunks.get(i))) tY = true;
				}
			}
		}
		
		// Make an intTuple for the return.
		int txInt = 0;
		int tyInt = 0;
		if(tX) txInt = 1;
		if(tY) tyInt = 1;
		
		if(tX || tY) return new intTuple(txInt, tyInt);
		else return intTuple.emptyTuple;
	}

	// Draw the chunk. 
	@Override
	public void drawObject(Graphics g) {
		// Draw it. 
		if(chunkImage != null) {
			g.drawImage(chunkImage, 
					drawX, 
					drawY, 
					chunkImage.getWidth(), 
					chunkImage.getHeight(), 
					null);
			
			// Draw the outskirts of the sprite.
			if(showSpriteBox) {
				g.setColor(Color.red);
				g.drawRect(drawX,
						   drawY, 
						   getObjectSpriteSheet().getSpriteWidth(), 
						   getObjectSpriteSheet().getSpriteHeight());
			}
			
			// Draw the hitbox of the image in green.
			if(showHitBox) {
				g.setColor(Color.green);
				g.drawRect(drawX - (- (getObjectSpriteSheet().getSpriteWidth()/2 - width/2) - getHitBoxAdjustmentX()),
						   drawY - (- (getObjectSpriteSheet().getSpriteHeight()/2 - height/2) - getHitBoxAdjustmentY()), 
					       width, 
					       height);
			}
			
			// Draw the x,y coordinates of the unit.
			if(showUnitPosition) {
				g.setColor(Color.white);
				g.drawString(getX() + "," + getY(),
						   drawX,
						   drawY);
			}
		}
	}
	
	// Initiate chunks
	public static void initiate() {
		allChunks = new ArrayList<chunk>();
	}
	
	////////////////////////////////////
	////// GETTERS AND SETTERS /////////
	////////////////////////////////////
	public boolean isPassable() {
		return passable;
	}
	
	public void setPassable(boolean b) {
		passable = b;
	}
	
}