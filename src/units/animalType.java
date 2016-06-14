package units;

import drawing.spriteSheet;
import drawing.animation.animation;
import drawing.animation.animationPack;
import drawing.spriteSheet.spriteSheetInfo;
import modes.mode;

public class animalType extends unitType {
	
	/////////////////////////
	////// DEFAULTS /////////
	/////////////////////////
	// Default dimensions per mode.
	public static int DEFAULT_PLATFORMER_HEIGHT = 46;
	public static int DEFAULT_TOPDOWN_HEIGHT = 20;
	
	// Default adjustment for the modes.
	public static int DEFAULT_PLATFORMER_ADJUSTMENT_Y = 6;
	public static int DEFAULT_TOPDOWN_ADJUSTMENT_Y = 20;
	
	// Default dimensions.
	private static int DEFAULT_UNIT_WIDTH = 24;
	
	// Default sprite stuff
	private static int DEFAULT_SPRITE_WIDTH = 32;
	private static int DEFAULT_SPRITE_HEIGHT = 32;
	private static int DEFAULT_SPRITE_ADJUSTMENT_X = 0;
	private static int DEFAULT_SPRITE_ADJUSTMENT_Y = 0;
	
	///////////////
	/// METHODS ///
	///////////////
	
	public animalType(String newName, String spriteSheetLocation, int newMoveSpeed, int newJumpSpeed) {
		super(newName, 
				new spriteSheet(new spriteSheetInfo(
				spriteSheetLocation, 
				DEFAULT_SPRITE_WIDTH, 
				DEFAULT_SPRITE_HEIGHT,
				DEFAULT_SPRITE_ADJUSTMENT_X,
				DEFAULT_SPRITE_ADJUSTMENT_Y
				)),
				null,
				DEFAULT_UNIT_WIDTH,	   // Width
				DEFAULT_TOPDOWN_HEIGHT,	   // Height
			    newMoveSpeed, // Movespeed
			    newJumpSpeed // Jump speed
			    );
		
		// Deal with animations
		animationPack unitTypeAnimations = new animationPack();
		
		// Jumping left animation.
		animation jumpingLeft = new animation("jumpingLeft", getUnitTypeSpriteSheet().getAnimation(1), 2, 2, 1);
		unitTypeAnimations.addAnimation(jumpingLeft);
		
		// Jumping right animation.
		animation jumpingRight = new animation("jumpingRight", getUnitTypeSpriteSheet().getAnimation(2), 2, 2, 1);
		unitTypeAnimations.addAnimation(jumpingRight);
		
		// Standing left animation.
		animation standingLeft = new animation("standingLeft", getUnitTypeSpriteSheet().getAnimation(1), 1, 1, 1);
		unitTypeAnimations.addAnimation(standingLeft);
		
		// Standing up animation.
		animation standingUp = new animation("standingUp", getUnitTypeSpriteSheet().getAnimation(3), 1, 1, 1);
		unitTypeAnimations.addAnimation(standingUp);
		
		// Standing right animation.
		animation standingRight = new animation("standingRight", getUnitTypeSpriteSheet().getAnimation(2), 1, 1, 1);
		unitTypeAnimations.addAnimation(standingRight);
		
		// Standing down animation.
		animation standingDown = new animation("standingDown", getUnitTypeSpriteSheet().getAnimation(0), 1, 1, 1);
		unitTypeAnimations.addAnimation(standingDown);
		
		// Running left animation.
		animation runningLeft = new animation("runningLeft", getUnitTypeSpriteSheet().getAnimation(1), 0, 2, 0.75f);
		unitTypeAnimations.addAnimation(runningLeft);		
		
		// Running up animation.
		animation runningUp = new animation("runningUp", getUnitTypeSpriteSheet().getAnimation(3), 0, 2, 0.75f);
		unitTypeAnimations.addAnimation(runningUp);
		
		// Running right animation.
		animation runningRight = new animation("runningRight", getUnitTypeSpriteSheet().getAnimation(2), 0, 2, 0.75f);
		unitTypeAnimations.addAnimation(runningRight);
		
		// Running down animation.
		animation runningDown = new animation("runningDown", getUnitTypeSpriteSheet().getAnimation(0), 0, 2, 0.75f);
		unitTypeAnimations.addAnimation(runningDown);
		
		// Set animations.
		setAnimations(unitTypeAnimations);
	}
}