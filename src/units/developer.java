package units;

import java.awt.Color;
import java.awt.event.KeyEvent;

import doodads.sheepFarm.bone;
import doodads.sheepFarm.bush;
import doodads.sheepFarm.flower;
import doodads.sheepFarm.grave;
import doodads.sheepFarm.haystack;
import doodads.sheepFarm.log;
import doodads.sheepFarm.rock;
import doodads.sheepFarm.tree;
import doodads.sheepFarm.well;
import doodads.tomb.wallTorch;
import drawing.userInterface.text;
import modes.topDown;
import terrain.chunk;
import units.unitTypes.farmLand.sheepFarm.redWolf;
import units.unitTypes.farmLand.sheepFarm.wolf;
import units.unitTypes.farmLand.tomb.lightDude;
import units.unitTypes.farmLand.tomb.shadowDude;
import utilities.saveState;
import utilities.utility;
import zones.zone;

public class developer extends player {
	
	// Interface
	public text instructionOne;
	public text instructionTwo;
	public text currChunkPre;
	public text currChunkType;
	public text instructionThree;
	public text hitBoxOnOrOff;
	public text instructionFour;
	public text collisionOnOrOff;
	public text instructionFive;
	public text visibilityOnOrOff;
	public text instructionSix;
	
	// Show hitboxes?
	public boolean showHitBoxes = false;
	
	// Show visibility
	public boolean visible = true;
	
	// Current stuff.
	public int whatThing = 0;
	public String[] listOfThings = {"tree", 
									"flower",
									"bush",
									"bone",
									"grave",
									"log",
									"haystack",
									"rock",
									"well",
									"wolf",
									"wallTorch",
									"shadowDude",
									"lightDude"};
	
	// Developer mode.
	public developer(int newX, int newY, zone z) {
		super(newX, newY, z);
		collisionOn = false;
		setStuck(true);
		topDown.setMode();
		
		// Give a million hp
		healthPoints = 100000;
		moveSpeed = 3.5f;
		
		// Destroy healthbar.
		getHealthBar().destroy();
		
		// Create dev interface
		createDevInterface();
	}
	
	// Adjust X and Y
	int adjustX = -150;
	int adjustY = -150;
	
	// Create dev interface
	public void createDevInterface() {
		
		// Instructions and stuff for now
		instructionOne = new text("Press 'space' to add something", 200+adjustX,200+adjustY, Color.white, 1.5f);
		instructionTwo = new text("Press 'tab' to tab through things", 200+adjustX, 220+adjustY, Color.white, 1.5f);
		currChunkPre = new text("Current thing: ", 200+adjustX, 240+adjustY, Color.white, 1.5f);
		currChunkType = new text(listOfThings[whatThing], 320+adjustX, 240+adjustY, Color.cyan, 1.5f);
		instructionThree = new text("Press 'h' to toggle hitboxes of spawned things", 200+adjustX, 280+adjustY, Color.white, 1.5f);
		hitBoxOnOrOff = new text("Off", 540+adjustX, 280+adjustY, Color.red, 1.5f);
		instructionFour = new text("Press 't' to toggle your collision", 200+adjustX, 300+adjustY, Color.white, 1.5f);
		collisionOnOrOff = new text("Off", 440+adjustX, 300+adjustY, Color.red, 1.5f);
		instructionFive = new text("Press 'r' to make spawned chunks visible", 200+adjustX, 320+adjustY, Color.white, 1.5f);
		visibilityOnOrOff = new text("On", 520+adjustX, 320+adjustY, Color.green, 1.5f);
		instructionSix = new text("Press 'y' to save", 200+adjustX, 340+adjustY, Color.white, 1.5f);
	}
	
	// Show hitboxes
	public void toggleSpriteVisibility() {
		visible = !visible;
		if(visible) {
			visibilityOnOrOff.setTheText("On");
			visibilityOnOrOff.setTheColor(Color.green);
		}
		else {
			visibilityOnOrOff.setTheText("Off");
			visibilityOnOrOff.setTheColor(Color.red);
		}
	}
	
	// Show hitboxes
	public void showHitBoxes() {
		showHitBoxes = !showHitBoxes;
		if(showHitBoxes) {
			hitBoxOnOrOff.setTheText("On");
			hitBoxOnOrOff.setTheColor(Color.green);
		}
		else {
			hitBoxOnOrOff.setTheText("Off");
			hitBoxOnOrOff.setTheColor(Color.red);
		}
	}
	
	// Show hitboxes
	public void toggleDevCollision() {
		collisionOn = !collisionOn;
		if(collisionOn) {
			collisionOnOrOff.setTheText("On");
			collisionOnOrOff.setTheColor(Color.green);
		}
		else {
			collisionOnOrOff.setTheText("Off");
			collisionOnOrOff.setTheColor(Color.red);
		}
	}
	
	// Update interface
	public void updateInterface() {
		
		// Update the displayed chunk
		if(currChunkType != null) currChunkType.setTheText(listOfThings[whatThing]);
	}
	
	// Update unit
	@Override
	public void updateUnit() {
		updateInterface();
	}
	
	// What thing increase
	public void increaseWhatThing() {
		whatThing++;
		if(whatThing>=listOfThings.length) whatThing = 0;
	}
	
	// Spawn the thing and output.
	public void spawnThing() {
		
		// Already printed?
		boolean alreadyPrinted = false;
		
		// Our things.
		chunk c = null;
		unit u = null;
		int random = 0;
		
		/////////////
		/// TREE ////
		/////////////
		if(listOfThings[whatThing].equals("tree")) {
			
			// Get random.
			random = utility.RNG.nextInt(3);
			
			// Output what we make.
			c = new tree(getIntX() + getWidth()/2, getIntY() + getHeight()/2, random);
		}
		
		///////////////
		/// FLOWER ////
		///////////////
		if(listOfThings[whatThing].equals("flower")) {
			
			// Get random.
			random = utility.RNG.nextInt(11);
			
			// Output what we make.
			c = new flower(getIntX() + getWidth()/2, getIntY() + getHeight()/2, random);
		}
		
		///////////////
		/// HAYSTACK ////
		///////////////
		if(listOfThings[whatThing].equals("haystack")) {
			
			// Get random.
			random = utility.RNG.nextInt(1);
			
			// Output what we make.
			c = new haystack(getIntX() + getWidth()/2, getIntY() + getHeight()/2, random);
		}
		
		///////////////
		/// ROCK ////
		///////////////
		if(listOfThings[whatThing].equals("rock")) {
			
			// Get random.
			random = utility.RNG.nextInt(2);
			
			// Output what we make.
			c = new rock(getIntX() + getWidth()/2, getIntY() + getHeight()/2, random);
		}
		
		///////////////
		/// WELL ////
		///////////////
		if(listOfThings[whatThing].equals("well")) {
			
			// Output what we make.
			c = new well(getIntX() + getWidth()/2, getIntY() + getHeight()/2, 0);
		}
		
		///////////////
		/// LOG ////
		///////////////
		if(listOfThings[whatThing].equals("log")) {
			
			
			// Output what we make.
			c = new log(getIntX() + getWidth()/2, getIntY() + getHeight()/2,0);
		}
		
		///////////////
		/// Bone ////
		///////////////
		if(listOfThings[whatThing].equals("bone")) {
			
			// Get random.
			random = utility.RNG.nextInt(5);
			
			// Output what we make.
			c = new bone(getIntX() + getWidth()/2, getIntY() + getHeight()/2, random);
		}
		
		/////////////
		/// BUSH ////
		/////////////
		if(listOfThings[whatThing].equals("bush")) {
			
			// Get random.
			random = utility.RNG.nextInt(3);
			
			// Output what we make.
			c = new bush(getIntX() + getWidth()/2, getIntY() + getHeight()/2, random);
		}
		
		/////////////
		/// GRAVE ///
		/////////////
		if(listOfThings[whatThing].equals("grave")) {
			
			// Get random.
			random = utility.RNG.nextInt(3);
			
			// Output what we make.
			c = new grave(getIntX() + getWidth()/2, getIntY() + getHeight()/2, random);
		}
		
		/////////////////
		/// WALLTORCH ///
		/////////////////
		if(listOfThings[whatThing].equals("wallTorch")) {
			
			// Get random.
			random = utility.RNG.nextInt(5);
			
			// Output what we make.
			c = new wallTorch(getIntX() + getWidth()/2, getIntY() + getHeight()/2);
			
			c.setFloatX(c.getFloatX() -c.getWidth()/2);
			c.setFloatY(c.getFloatY() -c.getHeight()/2);
			System.out.println("c = new " + listOfThings[whatThing] + "("+(getIntX()+getWidth()/2-c.getWidth()/2) + "," + (getIntY() + getHeight()/2 - c.getHeight()/2) + ");");
			// Set some stuff.
			if(showHitBoxes) c.showHitBox();
			c.setDrawSprite(visible);
			alreadyPrinted = true;
		}
		
		/////////////
		/// WOLF  ///
		/////////////
		if(listOfThings[whatThing].equals("wolf")) {
			
			// Output what we make.
			u = new redWolf(getIntX() + getWidth()/2, getIntY() + getHeight()/2);
		}
		
		/////////////
		/// LIGHTDUDE  ///
		/////////////
		if(listOfThings[whatThing].equals("lightDude")) {
			
			// Output what we make.
			u = new lightDude(getIntX() + getWidth()/2, getIntY() + getHeight()/2);
		}
		
		/////////////
		/// SHADOWDUDE  ///
		/////////////
		if(listOfThings[whatThing].equals("shadowDude")) {
			
			// Output what we make.
			u = new shadowDude(getIntX() + getWidth()/2, getIntY() + getHeight()/2);
		}
		
		// Adjust
		if(c!=null && !alreadyPrinted) {
			c.setFloatX(c.getFloatX() -c.getWidth()/2);
			c.setFloatY(c.getFloatY() -c.getHeight()/2);
			System.out.println("c = new " + listOfThings[whatThing] + "("+(getIntX()+getWidth()/2-c.getWidth()/2) + "," + (getIntY() + getHeight()/2 - c.getHeight()/2) + "," + random + ");");
			// Set some stuff.
			if(showHitBoxes) c.showHitBox();
			c.setDrawSprite(visible);
		}
		if(u!=null && !alreadyPrinted) {
			u.setFloatX(u.getFloatX() -u.getWidth()/2);
			u.setFloatY(u.getFloatY() -u.getHeight()/2);
			System.out.println("u = new " + listOfThings[whatThing] + "("+(getIntX()+getWidth()/2-u.getWidth()/2) + "," + (getIntY() + getHeight()/2 - u.getHeight()/2)+ ");");
			// Set some stuff.
			if(showHitBoxes) u.showHitBox();
			u.setDrawSprite(visible);
		}
		
	}
	
	// Controls
	public void keyPressed(KeyEvent k) {
			
		// Player presses left key.
		if(k.getKeyCode() == KeyEvent.VK_A) { 
			startMove("left");
		}
		
		// Player presses right key.
		if(k.getKeyCode() == KeyEvent.VK_D) { 
			startMove("right");
		}
		
		// Player presses up key, presumably to jump!
		if(k.getKeyCode() == KeyEvent.VK_W) { 
			startMove("up");
		}
		
		// Player presses down key
		if(k.getKeyCode() == KeyEvent.VK_S) { 
			startMove("down");
		}
		
		// Make sprites invisible
		if(k.getKeyCode() == KeyEvent.VK_R) { 
			toggleSpriteVisibility();
		}
	
		// Player presses bar key
		if(k.getKeyCode() == KeyEvent.VK_SPACE) {
			spawnThing();
		}
		
		// Show hitboxes?
		if(k.getKeyCode() == KeyEvent.VK_H) {
			showHitBoxes();
		}
		
		// Show hitboxes?
		if(k.getKeyCode() == KeyEvent.VK_T) {
			toggleDevCollision();
		}
		
		// Show hitboxes?
		if(k.getKeyCode() == KeyEvent.VK_Y) {
			saveState.createSaveState();		
			
			// Development mode?
			player.setDeveloper(false);
				
			// Create the player.
			player p = player.loadPlayer(null,null,0,0,"Up");
		}
		
		// Player presses e key
		if(k.getKeyCode() == KeyEvent.VK_TAB) {
			increaseWhatThing();
		}
	}
	
}