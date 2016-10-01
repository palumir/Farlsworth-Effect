package items.other;

import java.awt.image.BufferedImage;

import UI.tooltipString;
import drawing.spriteSheet;
import drawing.spriteSheet.spriteSheetInfo;
import items.bottle;
import items.bottleShard;
import items.item;
import units.player;

public class bottleExpander extends item {
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default sprite stuff
	public static int DEFAULT_SPRITE_WIDTH = 20;
	public static int DEFAULT_SPRITE_HEIGHT = 29;
	protected static int DEFAULT_SPRITE_ADJUSTMENT_X = 0;
	protected static int DEFAULT_SPRITE_ADJUSTMENT_Y = 0;
	
	public static spriteSheet bottleSpriteSheetRef = new spriteSheet(new spriteSheetInfo(
			"images/doodads/items/bottleShard.png", 
			bottleShard.DEFAULT_SPRITE_WIDTH, 
			bottleShard.DEFAULT_SPRITE_HEIGHT,
			0,
			0
			));

	//////////////
	/// FIELDS ///
	//////////////
	
	// Bottle sheet.
	protected static spriteSheet bottleSpriteSheet = null;
	
	// Does the player actually own the item?
	public static boolean inInventory = false;
	
	///////////////
	/// METHODS ///
	///////////////
	
	// For weapon being in your floor
	public bottleExpander(int x, int y) {
		super("Bottle Expander",null,x,y,0,0);
		
		// Set the width and height.
		setWidth(getImage().getWidth());
		setHeight(getImage().getHeight());
		
		// It is, of course, equippable.
		usedOnItems = true;
	}
	
	// Use
	@Override
	public void use() {
		
		// Increase the charge of the bottle we're selecting (make sure it's a bottle)
		item useOnItem = player.getPlayer().getPlayerInventory().get(player.getPlayer().getPlayerInventory().getSelectedSlot());
		
		// Is the quality incorrect?
		if(!quality.equals(useOnItem.quality)) {
			new tooltipString("This can only be used to upgrade " + quality + " bottles.");
		}
		
		// Make sure it's a bottle.
		else if(useOnItem instanceof bottle) {
			this.drop();
			((bottle)useOnItem).setMaxCharges(((bottle)useOnItem).getMaxCharges() + 1);
		}
		
		// Not a bottle, you dinky!
		else {
			new tooltipString("This Bottle Expander only works on a bottle, you dinky!");
		}
	}

	@Override
	public BufferedImage getImage() {
		// TODO Auto-generated method stub
		return bottleSpriteSheetRef.getSprite(0, 0);
	}

}