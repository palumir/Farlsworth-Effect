package units;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

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
import drawing.camera;
import drawing.drawnObject;
import drawing.gameCanvas;
import drawing.userInterface.text;
import modes.topDown;
import terrain.chunk;
import units.unitTypes.farmLand.sheepFarm.redWolf;
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
		moveSpeed = 20f;
		
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
		
		// Record mouse position.
		Point p = gameCanvas.getGameCanvas().getMousePosition();
		if(p!=null) lastMousePos = p;
		
		// Update the displayed chunk
		if(currChunkType != null) currChunkType.setTheText(listOfThings[whatThing]);
	}
	
	// Update unit
	@Override
	public void updateUnit() {
	
		// Update interface
		updateInterface();
		
		// Move things
		moveThings();
	}
	
	// Move things
	public void moveThings() {
		
		if(movingObject) {
			
			// In game point
			Point inGamePointCurrent = new Point((int)lastMousePos.getX() + camera.getCurrent().getX() + camera.getCurrent().getAttachedUnit().getWidth()/2 - gameCanvas.getDefaultWidth()/2, 
				      (int)lastMousePos.getY() + camera.getCurrent().getY() + camera.getCurrent().getAttachedUnit().getHeight()/2 - gameCanvas.getDefaultHeight()/2);
			
			// Move all of our selected objects.
			if(selectedThings!=null) {
				
				for(int i = 0; i < selectedThings.size(); i++) {
					int diffX = (int)relativeDifferences.get(i).getX();
					int diffY = (int)relativeDifferences.get(i).getY();
					selectedThings.get(i).setDoubleX(inGamePointCurrent.getX() - diffX);
					selectedThings.get(i).setDoubleY(inGamePointCurrent.getY() - diffY);
				}
				
			}
		}
		
	}
	
	// Selected units
	private static ArrayList<drawnObject> selectedThings;
	private static ArrayList<Point> relativeDifferences;
	
	// Selection type
	private static String selectionType = "Unit";
	
	// Is left click held?
	private static Point leftClickStartPoint;
	private static Point lastMousePos;
	
	// Are we selecting?
	private static boolean selecting = false;
	private static boolean movingObject = false;
	
	// Click radius
	private static int DEFAULT_CLICK_RADIUS = 10;
	
	// Responding to mouse presses
	public static void devMousePressed(MouseEvent e) {
		leftClickStartPoint = new Point(e.getX(), e.getY());
		
		// In game point
		Point inGamePoint = new Point(e.getX() + camera.getCurrent().getX() + camera.getCurrent().getAttachedUnit().getWidth()/2 - gameCanvas.getDefaultWidth()/2, 
				e.getY() + camera.getCurrent().getY() + camera.getCurrent().getAttachedUnit().getHeight()/2 - gameCanvas.getDefaultHeight()/2);
		
		// Determine whether or not we are touching something.
		ArrayList<drawnObject> touchedObjects  = drawnObject.getObjectsInRadius((int)inGamePoint.getX(), (int)inGamePoint.getY(), DEFAULT_CLICK_RADIUS);
		ArrayList<drawnObject> touchedUnits = new ArrayList<drawnObject>();
		if(touchedObjects!=null) for(int i = 0; i < touchedObjects.size(); i++) if(touchedObjects.get(i) instanceof unit) touchedUnits.add(touchedObjects.get(i));
		
		if(selectionType.equals("Unit") && touchedUnits != null && touchedUnits.size() > 0) {
			
			movingObject = true;
			
			unit touchedUnit = (unit)drawnObject.getClosestToFrom(
					(int)inGamePoint.getX(), 
					(int)inGamePoint.getY(),
					touchedUnits);
			
			// Move all selected things.
			if(selectedThings != null && selectedThings.contains(touchedUnit)) {
				relativeDifferences = new ArrayList<Point>();
				for(int i = 0; i < selectedThings.size(); i++) {
					relativeDifferences.add(new Point(touchedUnit.getIntX() - selectedThings.get(i).getIntX(),
													  touchedUnit.getIntY() - selectedThings.get(i).getIntY()));
				}
			} 
			
			// De-select and move only the newly selected object.
			else {
				
				relativeDifferences = new ArrayList<Point>();
				relativeDifferences.add(new Point(0,0));
				ArrayList<drawnObject> touchTheseThings = new ArrayList<drawnObject>();
				touchTheseThings.add(touchedUnit);
				selectAll(touchTheseThings);
				
			}
			
		}
		
		// Nothing was touched, draw our selection square.
		else {
			selecting = true;
		}
	}
	
	// Unselect all things
	public static void unSelectAll() {
		
		// Deselect old things.
		if(selectedThings != null) {
			for(; selectedThings.size() > 0; ) {
				selectedThings.get(0).dontShowHitBox();
				selectedThings.remove(0);
			}
		}
		
	}
	
	// Select all 
	public static void selectAll(ArrayList<drawnObject> d) {
		
		unSelectAll();
		
		if(selectedThings == null) {
			selectedThings = new ArrayList<drawnObject>();
		}
		
		for(int i = 0; i < d.size(); i++) {
			selectedThings.add(d.get(i));
			d.get(i).showHitBox();
		}
		
	}
	
	// Responding to mouse release
	public static void devMouseReleased(MouseEvent e) {
		
		Rectangle rect= new Rectangle(leftClickStartPoint);
		rect.add(lastMousePos);
		
		// Deal with box selecting
		if(selecting) {
			
			// Units
			if(selectionType.equals("Unit")) {
				
				unSelectAll();
				
				ArrayList<unit> selectTheseUnits = unit.getUnitsInBox(
						rect.x + camera.getCurrent().getX() + camera.getCurrent().getAttachedUnit().getWidth()/2 - gameCanvas.getDefaultWidth()/2, 
						rect.y + camera.getCurrent().getY() + camera.getCurrent().getAttachedUnit().getHeight()/2 - gameCanvas.getDefaultHeight()/2, 
						rect.x + rect.width + camera.getCurrent().getX() - gameCanvas.getDefaultWidth()/2, 
						rect.y + rect.height + camera.getCurrent().getY() - gameCanvas.getDefaultHeight()/2);
				
				if(selectTheseUnits!=null) {
					
					ArrayList<drawnObject> selectTheseThings = new ArrayList<drawnObject>();
					for(int i = 0; i < selectTheseUnits.size(); i++) {
						selectTheseThings.add(selectTheseUnits.get(i));
					}
				
					selectAll(selectTheseThings);
				}
			}
		}
		
		selecting = false;
		movingObject = false;
		
	}
	
	// Highlight box variables
	private static Color DEFAULT_HIGHLIGHT_COLOR = Color.green;
	
	// Highlight box
	public static void drawHighLightBox(Graphics g) {
		
		// Draw the box.
		if(selecting) {
			
			Rectangle rect= new Rectangle(leftClickStartPoint);
			rect.add(lastMousePos);

			g.setColor(DEFAULT_HIGHLIGHT_COLOR);
			g.drawRect(rect.x, rect.y, rect.width, rect.height);
			
		}
		
	}
	
	// Draw unit particular stuff.
	public void drawUnitSpecialStuff(Graphics g) {
		drawHighLightBox(g);
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
			
			c.setDoubleX(c.getDoubleX() -c.getWidth()/2);
			c.setDoubleY(c.getDoubleY() -c.getHeight()/2);
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
			c.setDoubleX(c.getDoubleX() -c.getWidth()/2);
			c.setDoubleY(c.getDoubleY() -c.getHeight()/2);
			System.out.println("c = new " + listOfThings[whatThing] + "("+(getIntX()+getWidth()/2-c.getWidth()/2) + "," + (getIntY() + getHeight()/2 - c.getHeight()/2) + "," + random + ");");
			// Set some stuff.
			if(showHitBoxes) c.showHitBox();
			c.setDrawSprite(visible);
		}
		if(u!=null && !alreadyPrinted) {
			u.setDoubleX(u.getDoubleX() -u.getWidth()/2);
			u.setDoubleY(u.getDoubleY() -u.getHeight()/2);
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