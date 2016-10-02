package items.other;

import java.awt.image.BufferedImage;

import UI.tooltipString;
import drawing.spriteSheet;
import drawing.spriteSheet.spriteSheetInfo;
import interactions.event;
import items.bottle;
import items.bottleShard;
import items.item;
import units.player;

public class bottleExpander extends item {
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default sprite stuff
	public static int DEFAULT_SPRITE_WIDTH = 25;
	public static int DEFAULT_SPRITE_HEIGHT = 13;
	
	public static spriteSheet bottleSpriteSheetRef = new spriteSheet(new spriteSheetInfo(
			"images/doodads/items/bottleExpander.png", 
			DEFAULT_SPRITE_WIDTH, 
			DEFAULT_SPRITE_HEIGHT,
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
	
	// Event
	public static event bottleExpanderFirstTime = new event("bottleExpanderFirstTime");
	
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
	
	// React to pickup
	@Override
	public void reactToPickup() {
		new tooltipString("Press 'i' to open inventory.");
	}
	
	// Use
	@Override
	public void use() {
		
		// Increase the charge of the bottle we're selecting (make sure it's a bottle)
		item useOnItem = player.getPlayer().getPlayerInventory().get(player.getPlayer().getPlayerInventory().getSelectedSlot());
		
		// Is the quality incorrect?
		if(!quality.equals(useOnItem.quality)) {
			new tooltipString("This can only be used to upgrade " + quality + " bottles.");
			player.getPlayer().getPlayerInventory().setWaitingToUseitem(false);
		}
		
		// Make sure it's a bottle.
		else if(useOnItem instanceof bottle) {
			this.drop();
			useOnItem.upgrade();
			((bottle)useOnItem).setMaxCharges(((bottle)useOnItem).getMaxCharges() + 1);
			player.getPlayer().getPlayerInventory().setWaitingToUseitem(false);
		}
		
		// Not a bottle, you dinky!
		else {
			new tooltipString("This Bottle Expander only works on a bottle, you dinky!");
			player.getPlayer().getPlayerInventory().setWaitingToUseitem(false);
		}
	}

	@Override
	public BufferedImage getImage() {
		// TODO Auto-generated method stub
		return bottleSpriteSheetRef.getSprite(0, 0);
	}

}