package effects.effectTypes;

import java.awt.Point;

import doodads.general.well;
import drawing.spriteSheet;
import drawing.spriteSheet.spriteSheetInfo;
import drawing.animation.animation;
import drawing.animation.animationPack;
import effects.effect;
import effects.effectType;
import interactions.interactBox;
import interactions.textSeries;
import items.bottle;
import items.bottles.saveBottle;
import modes.mode;
import sounds.sound;
import units.player;
import utilities.saveState;

public class savePoint extends effect {
	
	// Default dimensions.
	public static int DEFAULT_SPRITE_WIDTH = 25;
	public static int DEFAULT_SPRITE_HEIGHT = 35;
	
	// Platformer real dimensions
	public static int DEFAULT_PLATFORMER_HEIGHT = 35;
	public static int DEFAULT_PLATFORMER_WIDTH = 25;
	public static int DEFAULT_PLATFORMER_ADJUSTMENT_Y = 0;
	
	// TopDown real dimensions
	public static int DEFAULT_TOPDOWN_HEIGHT = 7;
	public static int DEFAULT_TOPDOWN_WIDTH = 20;
	public static int DEFAULT_TOPDOWN_ADJUSTMENT_Y = 13;
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default name.
	private static String DEFAULT_EFFECT_NAME = "savePoint";
	
	// Effect sprite stuff.
	private static String DEFAULT_EFFECT_SPRITESHEET = "images/effects/bottleEffects/" + DEFAULT_EFFECT_NAME + ".png";
	
	// Duration
	private static float DEFAULT_ANIMATION_DURATION = 3f; // multiple of 0.25f
	
	// The actual type.
	private static effectType theEffectType =
			new effectType(DEFAULT_EFFECT_NAME,
					new spriteSheet(new spriteSheetInfo(
							DEFAULT_EFFECT_SPRITESHEET, 
							DEFAULT_SPRITE_WIDTH, 
							DEFAULT_SPRITE_HEIGHT,
							0,
							0
							)),
							DEFAULT_ANIMATION_DURATION);	
	
	// Fields
	private interactBox interactSequence;
	
	///////////////
	/// METHODS ///
	///////////////
	// Constructor
	public savePoint(int newX, int newY) {
		super(theEffectType, newX, newY);	
		// Deal with animations.
		// Set-up animations.
		animationPack newAnimationPack =  new animationPack();
		
		animation pulseAnimation = new animation("pulseAnimation", 
				theEffectType.getEffectTypeSpriteSheet().getAnimation(0), 
				0, 
				9, 
				0.9f); 
		newAnimationPack.addAnimation(pulseAnimation);
		animations = newAnimationPack;
		
		// Set the animation.
		setCurrentAnimation(pulseAnimation);
		
		// Make adjustments on hitbox if we're in topDown.
		setHeight(getDefaultHeight());
		setWidth(getDefaultWidth());
		setHitBoxAdjustmentY(getDefaultHitBoxAdjustmentY());
		setHasATimer(false);
		
		setInteractable(true);

	}
	
	// Create interact sequence
		public interactBox makeInteractSequence() {
			
			// Placeholder for each individual textSeries.
			textSeries s;
			
			// Start of conversation.
			textSeries startOfConversation = new textSeries("StartWithButtons", "StartWithButtons");
			
			// Save and reset.
			textSeries saveGame = startOfConversation.addChild("Pick up", "This will REMOVE the save point. Are you sure?");
			
			// Cancel
			textSeries cancel = startOfConversation.addChild("Cancel", "No charge restored.");
			cancel.setEnd();
			
			// Warning
			s = saveGame.addChild("Yes", "Charge restored.");
			s.setEnd();
			
			s = saveGame.addChild("No", "No charge restored.");
			s.setEnd();

			return new interactBox(startOfConversation, this);
		}
		
		// Interact stuff.
		public void doInteractStuff() {
			
			if(interactSequence != null) {
				// Save
				if(interactSequence.getTextSeries().getButtonText().equals("Yes")) {
				
					// Play sound
					sound s = new sound(well.waterSplash);
					s.start();
					
					// Remove the save point.
					player p = player.getPlayer();
					if(p.lastSaveBottles!=null && p.lastSaveBottles.size() != 0) p.lastSaveBottles.remove(p.lastSaveBottles.size()-1);
					
					// Increase player bottle charges.
					bottle saveBottle = ((bottle)p.getPlayerInventory().get("Save Bottle"));
					saveBottle.setChargesLeft(saveBottle.getChargesLeft()+1);
					
					// Replace the save indicator
					if(p.lastSaveBottleChargeIndicator != null) {
						p.lastSaveBottleChargeIndicator.destroy();
						p.lastSaveBottleChargeIndicator = null;
					}
					
					// Save the players coordinates at the last Save Bottle or Well.
					if(p.lastSaveBottles==null || (p.lastSaveBottles.size() == 0 && p.lastWell != null)) {
						double x = p.getDoubleX();
						double y = p.getDoubleY();
						p.setDoubleX(p.lastWell.getX());
						p.setDoubleY(p.lastWell.getY());
						saveState.setQuiet(true);
						saveState.createSaveState();
						saveState.setQuiet(false);
						p.setDoubleX(x);
						p.setDoubleY(y);
					}
					else if(p.lastSaveBottles!=null && p.lastSaveBottles.size() > 0) {
						double x = p.getDoubleX();
						double y = p.getDoubleY();
						p.setDoubleX(p.lastSaveBottles.get(p.lastSaveBottles.size()-1).getX());
						p.setDoubleY(p.lastSaveBottles.get(p.lastSaveBottles.size()-1).getY());
						createSavePoint();
						saveState.setQuiet(true);
						saveState.createSaveState();
						saveState.setQuiet(false);
						p.setDoubleX(x);
						p.setDoubleY(y);
					}
						
					interactSequence = null;
				}
				
				// Don't save.
				else if(interactSequence.getTextSeries().getButtonText().equals("No")) {
					interactSequence = null;
				}
			}
		}
		
	// Interacting with heals you and saves.
	@Override
	public void interactWith() {
		
		// Restart sequence.
		interactSequence = makeInteractSequence();

		// Toggle display.
		interactSequence.toggleDisplay();

	}
		
	@Override
	public void doSpecificEffectStuff() {
		doInteractStuff();
	}
		
	
	// Respond to ending
	@Override
	public void respondToFrame(int j) {
	}
	
	// Respond to destroy
	@Override
	public void respondToDestroy() {
	}
	
	///////////////////////////
	/// GETTERS AND SETTERS ///
	///////////////////////////
	
	// Get default width.
	public static int getDefaultWidth() {
		if(mode.getCurrentMode().equals("topDown")) {
			return DEFAULT_TOPDOWN_WIDTH;
		}
		else {
			return DEFAULT_PLATFORMER_WIDTH;
		}
	}
	
	// Get default height.
	public static int getDefaultHeight() {
		if(mode.getCurrentMode().equals("topDown")) {
			return DEFAULT_TOPDOWN_HEIGHT;
		}
		else {
			return DEFAULT_PLATFORMER_HEIGHT;
		}
	}
	
	// Get default hitbox adjustment Y.
	public static int getDefaultHitBoxAdjustmentY() {
		if(mode.getCurrentMode().equals("topDown")) {
			return DEFAULT_TOPDOWN_ADJUSTMENT_Y;
		}
		else {
			return DEFAULT_PLATFORMER_ADJUSTMENT_Y;
		}
	}

	public static void createSavePoint() {
		createSavePoint(player.getPlayer());
	}
	
	public static void createSavePoint(player p) {
		if(mode.getCurrentMode().equals("platformer")) {
			p.lastSaveBottleChargeIndicator = new savePoint(
					(int)p.lastSaveBottles.get(p.lastSaveBottles.size()-1).getX(),
					p.getIntY() - 
					(
					((int)p.lastSaveBottles.get(p.lastSaveBottles.size()-1).getY() 
							+ savePoint.getDefaultHeight())
					- (p.getIntY() + p.getHeight()))
					);
		}
		else {
			p.lastSaveBottleChargeIndicator = new savePoint(
					(int)p.lastSaveBottles.get(p.lastSaveBottles.size()-1).getX(),
					p.getIntY() - 
					(
					((int)p.lastSaveBottles.get(p.lastSaveBottles.size()-1).getY() + savePoint.getDefaultHeight())
					- (p.getIntY() + p.getHeight()))
					);
		}
	}

}
