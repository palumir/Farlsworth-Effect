package effects.effectTypes;

import java.util.ArrayList;
import java.util.Random;

import doodads.sheepFarm.rock;
import drawing.camera;
import drawing.spriteSheet;
import drawing.animation.animation;
import drawing.animation.animationPack;
import drawing.spriteSheet.spriteSheetInfo;
import effects.effect;
import effects.effectType;
import effects.buffs.darkSlow;
import modes.mode;
import sounds.sound;
import terrain.chunk;
import units.animalType;
import units.humanType;
import units.player;
import units.unit;
import units.unitType;
import units.unitTypes.farmLand.spiderCave.poisonSpider;
import utilities.intTuple;
import utilities.time;
import utilities.utility;
import zones.zone;

public class explodingRock extends effect {
	
	// Default dimensions.
	public static int DEFAULT_SPRITE_WIDTH = 150;
	public static int DEFAULT_SPRITE_HEIGHT = 150;
	
	// Platformer real dimensions
	public static int DEFAULT_PLATFORMER_HEIGHT = DEFAULT_SPRITE_WIDTH;
	public static int DEFAULT_PLATFORMER_WIDTH = DEFAULT_SPRITE_HEIGHT;
	public static int DEFAULT_PLATFORMER_ADJUSTMENT_Y = 0;
	
	// TopDown real dimensions
	public static int DEFAULT_TOPDOWN_HEIGHT = 120;
	public static int DEFAULT_TOPDOWN_WIDTH = 120;
	public static int DEFAULT_TOPDOWN_ADJUSTMENT_Y = 0;
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default name.
	private static String DEFAULT_EFFECT_NAME = "darkExplode";
	
	// Effect sprite stuff.
	private static String DEFAULT_EFFECT_SPRITESHEET = "images/effects/" + DEFAULT_EFFECT_NAME + ".png";
	
	// Duration
	private static float DEFAULT_ANIMATION_DURATION = 3f; // multiple of 0.25f
	
	// Effect sound
	protected String effectSound2 = "sounds/effects/combat/rockCircleStart.wav";
	protected static float DEFAULT_VOLUME = 0.8f;
	
	// Damage
	protected int DEFAULT_DAMAGE = 1;
	
	// The actual type.
	private static effectType theEffectType =
			new effectType(DEFAULT_EFFECT_NAME,
					null,
					10f);	
	
	//////////////
	/// FIELDS ///
	//////////////
	private boolean allied = false;
	private int damage = DEFAULT_DAMAGE;
	private chunk rock;
	
	///////////////
	/// METHODS ///
	///////////////
	// Constructor
	public explodingRock(int newX, int newY, boolean isAllied, int radius, int damage, float duration) {
		super(theEffectType, newX, newY);
		
		// Allied?
		allied = isAllied;
		
		// Damage.
		this.damage = damage;
		
		// Duration
		this.animationDuration = duration;
		
		// Set sound.
		sound s = new sound(effectSound2);
		s.setPosition(getX(), getY(), sound.DEFAULT_SOUND_RADIUS);
		s.start();
		
		// Make adjustments on hitbox if we're in topDown.
		setHeight(getDefaultHeight());
		setWidth(getDefaultWidth());
		setHitBoxAdjustmentY(getDefaultHitBoxAdjustmentY());
		
		// Rocks
		rock = spawnRock(getX(),getY(),radius);

	}
	
	// Spawn circle of rocks
	public chunk spawnRock(int atX, int atY, int radius) {
		int n = 1;
		
		// returnlist
		ArrayList<chunk> returnList = new ArrayList<chunk>();
		
		rock r = new rock(this.getX(), this.getY(), 0);
		r.setBackgroundDoodad(true);
		
		// Check if collides with player.
		ArrayList<unit> moveUnits = unit.getUnitsInBox(r.getX(), r.getY() + r.getHitBoxAdjustmentY(), r.getX() + r.getWidth(), r.getY() + r.getHeight() + r.getHitBoxAdjustmentY());
		if(moveUnits!=null) {
			for(int m = 0; m < moveUnits.size(); m++) {
				unit currUnit = moveUnits.get(m);
				int repositionX = (this.getX()) - (currUnit.getX());
				int repositionY = (this.getY()) - (currUnit.getY());
				currUnit.move(repositionX, repositionY);
			}
		}
		return r;
	}
	
	// Respond to destroy
	@Override
	public void respondToDestroy() {
			rock.destroy();
			makeCircleOfRockPieces();
	}
	
	// Make circle of rock pieces
	public void makeCircleOfRockPieces() {
		// How many rock pieces?
		int n = 6;
		
		// Spawn rocks
		int explodeRadius = 1000;
		int spawnRadius = 20;
		double currentDegree = 0;
		double degreeChange = (double) 360/n;
		for(int i = 0; i < n; i++){
			int newX = (int) (getX() + spawnRadius*Math.cos(Math.toRadians(currentDegree))); 
			int newY = (int) (getY() + spawnRadius*Math.sin(Math.toRadians(currentDegree)));
			int goToX = (int) (getX() + explodeRadius*Math.cos(Math.toRadians(currentDegree))); 
			int goToY = (int) (getY() + explodeRadius*Math.sin(Math.toRadians(currentDegree))); 
			rockPiece r = new rockPiece(newX,newY,goToX,goToY,damage);
			currentDegree += degreeChange;
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
