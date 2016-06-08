package units;

import drawing.sprites.animation;
import drawing.sprites.animationPack;
import drawing.sprites.spriteSheet;
import drawing.sprites.spriteSheet.spriteSheetInfo;
import modes.mode;

public class humanType extends unitType {
	
	/////////////////////////
	////// DEFAULTS /////////
	/////////////////////////
	
	// Default dimensions.
	private static int DEFAULT_UNIT_WIDTH = 24;
	private static int DEFAULT_UNIT_HEIGHT = 20;
	
	// Default sprite stuff
	public static int DEFAULT_SPRITE_WIDTH = 64;
	public static int DEFAULT_SPRITE_HEIGHT = 64;
	private static int DEFAULT_SPRITE_ADJUSTMENT_X = 0;
	private static int DEFAULT_SPRITE_ADJUSTMENT_Y = 6;
	
	///////////////
	/// METHODS ///
	///////////////
	
	public humanType(String newName, String spriteSheetLocation, int newMoveSpeed, int newJumpSpeed) {
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
				DEFAULT_UNIT_HEIGHT,	   // Height
			    newMoveSpeed, // Movespeed
			    newJumpSpeed // Jump speed
			    );
		
		// Deal with animations
		animationPack unitTypeAnimations = new animationPack();
		
		// Jumping left animation.
		animation jumpingLeft = new animation("jumpingLeft", getUnitTypeSpriteSheet().getAnimation(1), 5, 5, 1);
		jumpingLeft.repeat(true);
		unitTypeAnimations.addAnimation(jumpingLeft);
		
		// Jumping right animation.
		animation jumpingRight = new animation("jumpingRight", getUnitTypeSpriteSheet().getAnimation(3), 5, 5, 1);
		jumpingRight.repeat(true);
		unitTypeAnimations.addAnimation(jumpingRight);
		
		// Standing left animation.
		animation standingLeft = new animation("standingLeft", getUnitTypeSpriteSheet().getAnimation(9), 0, 0, 1);
		standingLeft.repeat(true);
		unitTypeAnimations.addAnimation(standingLeft);
		
		// Standing up animation.
		animation standingUp = new animation("standingUp", getUnitTypeSpriteSheet().getAnimation(8), 0, 0, 1);
		standingUp.repeat(true);
		unitTypeAnimations.addAnimation(standingUp);
		
		// Standing right animation.
		animation standingRight = new animation("standingRight", getUnitTypeSpriteSheet().getAnimation(11), 0, 0, 1);
		standingRight.repeat(true);
		unitTypeAnimations.addAnimation(standingRight);
		
		// Standing down animation.
		animation standingDown = new animation("standingDown", getUnitTypeSpriteSheet().getAnimation(10), 0, 0, 1);
		standingDown.repeat(true);
		unitTypeAnimations.addAnimation(standingDown);
		
		// Running left animation.
		animation runningLeft = new animation("runningLeft", getUnitTypeSpriteSheet().getAnimation(9), 0, 9, 1);
		runningLeft.repeat(true);
		unitTypeAnimations.addAnimation(runningLeft);		
		
		// Running up animation.
		animation runningUp = new animation("runningUp", getUnitTypeSpriteSheet().getAnimation(8), 0, 9, 1);
		runningUp.repeat(true);
		unitTypeAnimations.addAnimation(runningUp);
		
		// Running right animation.
		animation runningRight = new animation("runningRight", getUnitTypeSpriteSheet().getAnimation(11), 0, 9, 1);
		runningRight.repeat(true);
		unitTypeAnimations.addAnimation(runningRight);
		
		// Running down animation.
		animation runningDown = new animation("runningDown", getUnitTypeSpriteSheet().getAnimation(10), 0, 9, 1);
		runningDown.repeat(true);
		unitTypeAnimations.addAnimation(runningDown);
		
		// Set animations.
		setAnimations(unitTypeAnimations);
	}
}