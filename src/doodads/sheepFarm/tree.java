package doodads.sheepFarm;

import java.util.Random;

import drawing.camera;
import drawing.drawnObject;
import drawing.gameCanvas;
import effects.effectTypes.fire;
import effects.effectTypes.rainFall;
import interactions.interactBox;
import interactions.textSeries;
import modes.mode;
import sounds.sound;
import terrain.chunk;
import terrain.chunkType;
import terrain.generalChunkType;
import units.humanType;
import units.player;
import units.unit;
import units.unitType;
import utilities.stringUtils;
import utilities.time;
import utilities.utility;
import zones.zone;
import zones.farmLand.forest;
import zones.farmLand.sheepFarm;

public class tree extends chunk {
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default name.
	private static String DEFAULT_CHUNK_NAME = "tree";
	
	// Sprite stuff.
	private static String DEFAULT_CHUNK_SPRITESHEET = "images/doodads/farmLand/sheepFarm/"+ DEFAULT_CHUNK_NAME + ".png";
	
	// Dimensions
	public static int DEFAULT_CHUNK_WIDTH = 125;
	public static int DEFAULT_CHUNK_HEIGHT = 133;
	
	// The actual type.
	private static generalChunkType typeReference = new generalChunkType(DEFAULT_CHUNK_NAME, DEFAULT_CHUNK_SPRITESHEET, DEFAULT_CHUNK_WIDTH, DEFAULT_CHUNK_HEIGHT); 
	
	// Ignite time
	long igniteTime = 0;
	float igniteEvery = 0.15f;
	boolean ignitedAround = false;
	
	////////////////
	/// FIELDS /////
	////////////////
	
	// Sequence
	private interactBox interactSequence;
	
	// Is the tree on fire?
	private boolean ignited = false;
	
	// Type of tree
	private int type = 0;
	
	// Default width
	static int DEFAULT_WIDTH = 30;
	static int DEFAULT_HEIGHT = 16;
	
	///////////////
	/// METHODS ///
	///////////////
	
	// Create interact sequence
	public interactBox makeNormalInteractSequence() {
		
		// Placeholder for each individual textSeries.
		textSeries s;
					
		// Start of conversation.
		textSeries startOfConversation = null;
		
		if(ignited) {
			startOfConversation = new textSeries(null, "It's an ordinary tree.");
			s = startOfConversation.addChild(null, "But also on fire.");
			s.setEnd();
		}
		else {
			startOfConversation = new textSeries(null, "It's an ordinary tree.");
			startOfConversation.setEnd();
		}
		
		return new interactBox(startOfConversation, this);
	}
	
	// Interact stuff.
	public void doInteractStuff() {
	}
	
	// Update
	@Override
	public void update() {
		doInteractStuff();
		
		// Ignite other trees
		if(ignited && !ignitedAround && time.getTime() - igniteTime > igniteEvery*1000) {
			ignitedAround = true;
			fire.igniteRuffageInBox(getIntX()-20, getIntY()-20, getIntX() + getWidth()+20, getIntY() + getHeight()+20);
		}
	}
	
	// Interact with object. Should be over-ridden.
	public void interactWith() { 
		interactSequence = makeNormalInteractSequence();
		interactSequence.toggleDisplay();
	}
	
	// Ignite
	public static void igniteArea(int newX, int newY) {
			// Tree top
			int minX = newX - 35;
			int maxX = newX + DEFAULT_WIDTH + 35;
			int minY = newY - 105;
			int maxY = newY-45;
			for(int i = minX; i < maxX; i += fire.getDefaultWidth()/3) {
				for(int j = minY; j < maxY; j += fire.getDefaultWidth()/3) {
					int rand = utility.RNG.nextInt(15);
					int randX = 4 - 8*utility.RNG.nextInt(2);
					int randY = 4 - 8*utility.RNG.nextInt(2);
					if(rand == 1) { fire f = new fire(i - fire.getDefaultWidth()/2 + randX,j - fire.getDefaultHeight()/2+randY); }
				}
			}
			
			// Trunk
			int minYTrunk = newY-30;
			int maxYTrunk = newY+10;
			for(int j = minYTrunk; j < maxYTrunk; j += fire.getDefaultWidth()/4) {
				int rand = utility.RNG.nextInt(2);
				int randX = 4 - 8*utility.RNG.nextInt(2);
				int randY = 4 - 8*utility.RNG.nextInt(2);
				if(rand==1) { fire f = new fire(newX + randX,j - fire.getDefaultHeight()/2+randY); }
			}
	}
	
	// Ignite
	@Override
	public void ignite() {
		
		if(!ignited) {
			
			// Set ignite time.
			igniteTime = time.getTime();
			ignited = true;
			
			// Tree top
			int minX = getIntX() - 35;
			int maxX = getIntX() + getWidth() + 35;
			int minY = getIntY() - 105;
			int maxY = getIntY()-45;
			for(int i = minX; i < maxX; i += fire.getDefaultWidth()/3) {
				for(int j = minY; j < maxY; j += fire.getDefaultWidth()/3) {
					int rand = utility.RNG.nextInt(15);
					int randX = 4 - 8*utility.RNG.nextInt(2);
					int randY = 4 - 8*utility.RNG.nextInt(2);
					if(rand == 1) { fire f = new fire(i - fire.getDefaultWidth()/2 + randX,j - fire.getDefaultHeight()/2+randY); }
				}
			}
			
			// Trunk
			int minYTrunk = getIntY()-30;
			int maxYTrunk = getIntY()+10;
			for(int j = minYTrunk; j < maxYTrunk; j += fire.getDefaultWidth()/4) {
				int rand = utility.RNG.nextInt(2);
				int randX = 4 - 8*utility.RNG.nextInt(2);
				int randY = 4 - 8*utility.RNG.nextInt(2);
				if(rand==1) { fire f = new fire(getIntX() + randX,j - fire.getDefaultHeight()/2+randY); }
			}
		}
	}
	
	// Create function
	public static tree createTree(int newX, int newY, int i) {
		if(!zone.loadedOnce) {
			tree t = new tree(newX,newY,i);
			t.setReloadObject(false);
			return t;
		}
		else {
			// Get the tree at the x and y to ignite it.
			if(zone.getCurrentZone() != null && zone.getCurrentZone().getParentName().equals("farmLand") && 
					((forest.isOnFire != null && forest.isOnFire.isCompleted()) || 
					  (sheepFarm.isOnFire != null && sheepFarm.isOnFire.isCompleted()))) {
				igniteArea(newX,newY);
			}
			return null;
		}
	}
	
	///////////////
	/// METHODS ///
	///////////////
	// Constructor
	public tree(int newX, int newY, int i) {
		super(typeReference, newX, newY, i, 0);
		type = i;
		if(mode.getCurrentMode().equals("topDown")) {
			setHitBoxAdjustmentY(58);
			setWidth(30);
			setHeight(13);
		}
		else {
			setHitBoxAdjustmentY(0);
			setHeight(DEFAULT_CHUNK_HEIGHT);
			setWidth(DEFAULT_CHUNK_WIDTH);
		}
		setInteractable(true);
		setPassable(false);
		
		// Set to be flammable
		setFlammable(true);
		
		// If we are in sheepFarm and it's on fire, light everything on fire.
		if(zone.getCurrentZone() != null && zone.getCurrentZone().getParentName().equals("farmLand") && 
				((forest.isOnFire != null && forest.isOnFire.isCompleted()) || 
				  (sheepFarm.isOnFire != null && sheepFarm.isOnFire.isCompleted()))) {
			ignite();
		}
	}
}
