package terrain;

import java.awt.Color;
import java.awt.image.BufferedImage;

import drawing.drawnObject;
import drawing.sprites.spriteSheet;

public abstract class chunkType {
	
	//////////////
	/// FIELDS ///
	//////////////
	// Name 
	private String name;
	
	// Dimensions
	private int height;
	private int width;

	// Spritesheet
	private spriteSheet chunkTypeSpriteSheet;

	//////////////
	/// METHODS //
	//////////////
	// Constructor
	public chunkType(String newName, spriteSheet newSpriteSheet) {
		name = newName;
		setChunkTypeSpriteSheet(newSpriteSheet);
		setHeight(getChunkTypeSpriteSheet().getSpriteHeight());
		setWidth(getChunkTypeSpriteSheet().getSpriteWidth());
	}
	
	/////////////////////////
	// Getters and setters //
	/////////////////////////
	
	public abstract BufferedImage getChunkImage();
	
	public abstract BufferedImage getChunkImage(int i, int j);

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}
	
	public int getSpriteHeight() {
		return chunkTypeSpriteSheet.getSpriteHeight();
	}
	
	public int getSpriteWidth() {
		return chunkTypeSpriteSheet.getSpriteWidth();
	}

	public spriteSheet getChunkTypeSpriteSheet() {
		return chunkTypeSpriteSheet;
	}

	public void setChunkTypeSpriteSheet(spriteSheet chunkTypeSpriteSheet) {
		this.chunkTypeSpriteSheet = chunkTypeSpriteSheet;
	}
	
}