package effects.effectTypes;

import java.util.ArrayList;

import doodads.sheepFarm.sandRock;
import effects.effect;
import effects.effectType;
import effects.projectiles.rockPiece;
import modes.mode;
import sounds.sound;
import terrain.chunk;
import utilities.utility;

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
	private static String DEFAULT_EFFECT_NAME = "explodingRock";
	
	// Effect sound
	protected String rockSpawn = "sounds/effects/combat/rockSpawn.wav";
	protected String rockExplode = "sounds/effects/combat/rockExplode.wav";
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
	private int howMany = 0;
	private float rockSpeed = 0;
	
	// Rock angles
	private ArrayList<Integer> rockAngles;
	
	///////////////
	/// METHODS ///
	///////////////
	// Constructor
	public explodingRock(int newX, int newY, boolean isAllied, int howMany, float rockSpeed, int radius, int damage, float duration) {
		super(theEffectType, newX, newY);
		
		// Allied?
		allied = isAllied;
		
		// Set how many
		this.howMany = howMany;
		this.rockSpeed = rockSpeed;
		
		// Damage.
		this.damage = damage;
		
		// Duration
		this.setAnimationDuration(duration);
		
		// Set sound.
		sound s = new sound(rockSpawn);
		s.setPosition(getIntX(), getIntY(), sound.DEFAULT_SOUND_RADIUS);
		s.start();
		
		// Make adjustments on hitbox if we're in topDown.
		setHeight(getDefaultHeight());
		setWidth(getDefaultWidth());
		setHitBoxAdjustmentY(getDefaultHitBoxAdjustmentY());
		
		// Rocks
		rock = spawnRock(getIntX(),getIntY(),radius);

	}
	
	// Spawn circle of rocks
	public chunk spawnRock(int atX, int atY, int radius) {		
		sandRock r = new sandRock(this.getIntX(), this.getIntY(), 0);
		r.setBackgroundDoodad(true);
		return r;
	}
	
	// Respond to destroy
	@Override
	public void respondToDestroy() {
			rock.destroy();
			// Set sound.
			sound s = new sound(rockExplode);
			s.setPosition(getIntX(), getIntY(), sound.DEFAULT_SOUND_RADIUS);
			s.start();
			makeCircleOfRockPieces();
	}
	
	// Make circle of rock pieces
	public void makeCircleOfRockPieces() {
		
		// How many rock pieces?
		int explodeRadius = 1000;
		
		if(rockAngles!=null && rockAngles.size()>0) {
			for(int i = 0; i < rockAngles.size(); i++) {
				int newX = (int) (getIntX() + 0*Math.cos(Math.toRadians(rockAngles.get(i)-90))); 
				int newY = (int) (getIntY() + 0*Math.sin(Math.toRadians(rockAngles.get(i)-90)));
				int goToX = (int) (getIntX() + explodeRadius*Math.cos(Math.toRadians(rockAngles.get(i)-90))); 
				int goToY = (int) (getIntY() + explodeRadius*Math.sin(Math.toRadians(rockAngles.get(i)-90))); 
				rockPiece r = new rockPiece(newX,newY,goToX,goToY,damage,rockSpeed);
				r.setAllied(allied);
			}
		}
		else {
			int n = howMany;
			
			// Spawn rocks
			double currentDegree = 0;
			double degreeChange = (double) 360/n;
			for(int i = 0; i < n; i++){
				int newX = (int) (getIntX() + 0*Math.cos(Math.toRadians(currentDegree))); 
				int newY = (int) (getIntY() + 0*Math.sin(Math.toRadians(currentDegree)));
				int goToX = (int) (getIntX() + explodeRadius*Math.cos(Math.toRadians(currentDegree))); 
				int goToY = (int) (getIntY() + explodeRadius*Math.sin(Math.toRadians(currentDegree))); 
				rockPiece r = new rockPiece(newX,newY,goToX,goToY,damage,rockSpeed);
				r.setAllied(allied);
				currentDegree += degreeChange;
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

	public ArrayList<Integer> getRockAngles() {
		return rockAngles;
	}

	public void setRockAngles(ArrayList<Integer> rockAngles) {
		this.rockAngles = rockAngles;
	}

}
