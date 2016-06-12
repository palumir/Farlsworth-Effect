package drawing;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import utilities.imageUtils;

public class spriteSheet {
	
	///////////////
	/// GLOBALS ///
	///////////////
	public static class spriteSheetInfo {
		
		////////////////
		//// FIELDS ////
		////////////////
		
		// Dimensions.
		private int spriteWidth;
		private int spriteHeight;
		private int hitBoxAdjustmentX;
		private int hitBoxAdjustmentY;
		
		// The actual file location.
		public String sheetFile;
		
		///////////////
		/// METHODS ///
		///////////////
		public spriteSheetInfo(String newSheetFile, int newSpriteWidth, int newSpriteHeight, int newHitBoxX,int newHitBoxY) {
			
			// Set fields.
			hitBoxAdjustmentX = newHitBoxX;
			hitBoxAdjustmentY = newHitBoxY;
			sheetFile = newSheetFile;
			spriteWidth = newSpriteWidth;
			spriteHeight = newSpriteHeight;
		}
	}
	
	// Default transparency color
	private static Color DEFAULT_COLOR_TO_TRANSPARENT = Color.green;
	
	////////////////
	//// FIELDS ////
	////////////////
	private BufferedImage sheet;
	private ArrayList<ArrayList<BufferedImage>> sprites;
	private spriteSheetInfo sheetInfo;
	private int sheetHeight;
	private int sheetWidth;

	
	///////////////
	/// METHODS ///
	///////////////
	public spriteSheet(spriteSheetInfo info) {
		try {
			// Set the sheet to be the image at the filename.
			sheet = ImageIO.read(new File(info.sheetFile));
			
			// Add transparency to image.
			sheet = imageUtils.makeColorTransparent(sheet, DEFAULT_COLOR_TO_TRANSPARENT);
			
			// Set the sheet info.
			sheetInfo = info;
			
			// Seperate the sheet into sprites.
			sheetHeight = sheet.getHeight();
			setSheetWidth(sheet.getWidth());
			int rows = sheet.getHeight()/getSpriteHeight();
			int cols = sheet.getWidth()/getSpriteWidth();
			setSprites(new ArrayList<ArrayList<BufferedImage>>());
			for (int i = 0; i < rows; i++){
				getSprites().add(new ArrayList<BufferedImage>());
			    for (int j = 0; j < cols; j++){
			        getSprites().get(i).add(sheet.getSubimage(
			            j * getSpriteWidth(),
			            i * getSpriteHeight(),
			            getSpriteWidth(),
			            getSpriteHeight()
			        ));
			    }
			}
		} 
		catch (IOException e) {
			// Do nothing, but an error happened.
		}
	}
	
	/////////////////////////////
	//// GETTERS AND SETTERS ////
	/////////////////////////////
	
	// For returning an individual sprite from a file.
	public static BufferedImage getSpriteFromFilePath(String s) {
		
		try {
			// Set the sheet to be the image at the filename.
			BufferedImage retIMG = ImageIO.read(new File(s));
			
			// Add transparency to image.
			retIMG = imageUtils.makeColorTransparent(retIMG, DEFAULT_COLOR_TO_TRANSPARENT);
			return retIMG;
		}
		catch(Exception e) {
			
		}
		
		return null;
	}
	
	// Get an image in the xth and yth position the sheet.
	public BufferedImage getSprite(int x, int y) { 
		return getSprites().get(y).get(x);
	}
	
	// Get animation
	public ArrayList<BufferedImage> getAnimation(int y) {
		return getSprites().get(y);
	}

	public int getSpriteWidth() {
		return sheetInfo.spriteWidth;
	}

	public void setSpriteWidth(int spriteWidth) {
		this.sheetInfo.spriteWidth = spriteWidth;
	}

	public int getSpriteHeight() {
		return sheetInfo.spriteHeight;
	}

	public void setSpriteHeight(int spriteHeight) {
		this.sheetInfo.spriteHeight = spriteHeight;
	}
	
	public int getHitBoxAdjustmentX() {
		return sheetInfo.hitBoxAdjustmentX;
	}
	
	public int getHitBoxAdjustmentY() {
		return sheetInfo.hitBoxAdjustmentY;
	}

	public int getSheetWidth() {
		return sheetWidth;
	}

	public void setSheetWidth(int sheetWidth) {
		this.sheetWidth = sheetWidth;
	}

	public ArrayList<ArrayList<BufferedImage>> getSprites() {
		return sprites;
	}

	public void setSprites(ArrayList<ArrayList<BufferedImage>> sprites) {
		this.sprites = sprites;
	}
}