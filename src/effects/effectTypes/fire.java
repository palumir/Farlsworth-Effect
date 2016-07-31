package effects.effectTypes;

import java.util.ArrayList;

import drawing.spriteSheet;
import drawing.spriteSheet.spriteSheetInfo;
import effects.effect;
import effects.effectType;
import modes.mode;
import terrain.chunk;

public class fire extends effect {
	
	// Default dimensions.
	public static int DEFAULT_SPRITE_WIDTH = 75;
	public static int DEFAULT_SPRITE_HEIGHT = 150;
	
	// Platformer real dimensions
	public static int DEFAULT_PLATFORMER_HEIGHT = 120;
	public static int DEFAULT_PLATFORMER_WIDTH = 30;
	public static int DEFAULT_PLATFORMER_ADJUSTMENT_Y = 65;
	
	// TopDown real dimensions
	public static int DEFAULT_TOPDOWN_HEIGHT = 120;
	public static int DEFAULT_TOPDOWN_WIDTH = 30;
	public static int DEFAULT_TOPDOWN_ADJUSTMENT_Y = 65;
	
	// Ignite sound
	public static String forestFire = "sounds/effects/natural/forestFire.wav";
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default name.
	public static String DEFAULT_EFFECT_NAME = "fire";
	
	// Effect sprite stuff.
	private static String DEFAULT_EFFECT_SPRITESHEET = "images/effects/" + DEFAULT_EFFECT_NAME + ".png";
	
	// Duration
	private static float DEFAULT_ANIMATION_DURATION = 1f;
	
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
	
	
	
	//////////////
	/// FIELDS ///
	//////////////
	
	///////////////
	/// METHODS ///
	///////////////
	// Constructor
	public fire(int newX, int newY) {
		super(theEffectType, newX, newY, true);
		
		// Force in front
		setForceInFront(true);
		
		// Make adjustments on hitbox if we're in topDown.
		setHeight(getDefaultHeight());
		setWidth(getDefaultWidth());
		setHitBoxAdjustmentY(getDefaultHitBoxAdjustmentY());
		
		// Has no timer.
		hasATimer = false;

	}
	
	// Ignite ruffage with fire.
	public static void igniteRuffageInBox(int x1, int y1, int x2, int y2) {
		ArrayList<chunk> chunksInArea = chunk.getImpassableChunksInBox(x1, y1, x2, y2);
		ArrayList<chunk> flammableChunks = new ArrayList<chunk>();
		
		if(chunksInArea!=null) {
			for(int i = 0; i < chunksInArea.size(); i++) {
				if(chunksInArea.get(i).isFlammable()) {
					flammableChunks.add(chunksInArea.get(i));
				}
			}
			
			// Ignite all flammable chunks.
			for(int i = 0; i < flammableChunks.size(); i++) {
				flammableChunks.get(i).ignite();
			}
		}
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

}
