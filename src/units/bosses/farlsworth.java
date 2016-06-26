package units.bosses;

import java.util.ArrayList;

import doodads.farmLand.woolPiece;
import interactions.event;
import interactions.interactBox;
import interactions.textSeries;
import modes.mode;
import sounds.sound;
import units.animalType;
import units.boss;
import units.player;
import units.unitType;
import utilities.intTuple;
import utilities.stringUtils;
import zones.farmLand.sheepFarm;

public class farlsworth extends boss {
	
	// Platformer real dimensions
	public static int DEFAULT_PLATFORMER_HEIGHT = 32;
	public static int DEFAULT_PLATFORMER_WIDTH = 32;
	public static int DEFAULT_PLATFORMER_ADJUSTMENT_Y = 0;
	
	// TopDown real dimensions
	public static int DEFAULT_TOPDOWN_HEIGHT = 18;
	public static int DEFAULT_TOPDOWN_WIDTH = 20;
	public static int DEFAULT_TOPDOWN_ADJUSTMENT_Y = 4;
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default name.
	private static String DEFAULT_FARLSWORTH_NAME = "Farlsworth";
	
	// Default movespeed.
	private static int DEFAULT_FARLSWORTH_MOVESPEED = 4;
	
	// Default jump speed
	private static int DEFAULT_FARLSWORTH_JUMPSPEED = 10;
	
	// FARLSWORTH sprite stuff.
	private static String DEFAULT_FARLSWORTH_SPRITESHEET = "images/units/animals/sheep.png";
	
	// The actual type.
	private static unitType sheepType =
			new animalType( "farlsworth",  // Name of unitType 
						 DEFAULT_FARLSWORTH_SPRITESHEET,
					     DEFAULT_FARLSWORTH_MOVESPEED, // Movespeed
					     DEFAULT_FARLSWORTH_JUMPSPEED // Jump speed
						);	
	
	//////////////
	/// FIELDS ///
	//////////////
	
	// Interaction
	private interactBox interactSequence;
	
	// Path to follow on interaction.
	private ArrayList<intTuple> p;
	
	// Sound.
	private static String bleet = "sounds/effects/animals/sheep2.wav";
	
	// Are we in boss fight mode?
	private boolean bossFight = false;
	
	// Interact times.
	private int interactTimes = 0;
	private boolean interactMoved = false;
	
	// Is he lost?
	private event farlsworthLost;
	
	///////////////
	/// METHODS ///
	///////////////
	
	// Create interact sequence
	public interactBox makeNormalInteractSequence() {
	
		// Placeholder for each individual textSeries.
		textSeries s;
		
		// Start.
		textSeries startOfConversation;
		
		// 0;
		if(interactTimes == 0) {
			// Start of conversation.
			startOfConversation = new textSeries(null, "<insert greeting here>.");
			s = startOfConversation.addChild(null, "<insert conversation where you piss him off here>.");
			s.setEnd();
		} 
		
		// 1
		else if(interactTimes == 1) {
			// Start of conversation.
			startOfConversation = new textSeries(null, "<insert complaint about being enslaved>");
			startOfConversation.setEnd();
		}
		// 1
		else if(interactTimes == 2) {
			// Start of conversation.
			startOfConversation = new textSeries(null, "<insert some other complaint about not being a slave anymore>");
			s = startOfConversation.addChild(null, "<some other shit>");
			s.setEnd();
		}
		else {
			startOfConversation = new textSeries(null, "Bah.");
			startOfConversation.setEnd();
		}
		
		return new interactBox(startOfConversation, stringUtils.toTitleCase(DEFAULT_FARLSWORTH_NAME), true);
	}
	
	// Do interact stuff.
	public void doInteractStuff() {
		
		// Pissy Farlsworth runs away first time.
		if(!interactMoved && interactSequence != null && interactSequence.getTheText().isEnd() && interactTimes == 0) {
			interactTimes++;
			moveTo(74,-58);
			interactMoved = true;
			sound s = new sound(bleet);
			s.setPosition(getX(), getY(), sound.DEFAULT_SOUND_RADIUS);
			s.start();
			
		}
		
		// Pissy Farlsworth runs away second time.
		if(!interactMoved && interactSequence != null && interactSequence.getTheText().isEnd() && interactTimes == 1) {
			interactTimes++;
			moveTo(74,-406);
			interactMoved = true;
			sound s = new sound(bleet);
			s.setPosition(getX(), getY(), sound.DEFAULT_SOUND_RADIUS);
			s.start();
		}
		
		// Pissy Farlsworth runs away third time.
		if(!interactMoved && interactSequence != null && interactSequence.getTheText().isEnd() && interactTimes == 2) {
			interactTimes++;
			p = new ArrayList<intTuple>();
			p.add(new intTuple(425,-70));
			p.add(new intTuple(427,5));
			p.add(new intTuple(-8,-1));
			p.add(new intTuple(-8,-400));
			p.add(new intTuple(-8,-499));
			p.add(new intTuple(-249,-808));
			p.add(new intTuple(-329,-1036));
			p.add(new intTuple(-385,-1216));
			followPath(p);
			interactMoved = true;
			sound s = new sound(bleet);
			s.setPosition(getX(), getY(), sound.DEFAULT_SOUND_RADIUS);
			s.start();
		}
		
		// Make him open the gate if he's by the gate.
		if(!sheepFarm.forestGate.isPassable() && Math.abs(4 - getX()) < closeEnough && Math.abs(-389 - getY()) < closeEnough) {
			sheepFarm.forestGate.forceOpen();
		}
		
		// Drop a piece of wool.
		if(Math.abs(-151 - getX()) < closeEnough+40 && Math.abs(-619 - getY()) < closeEnough+40) {
			woolPiece w = new woolPiece(-151,-619,0);
		}
		
		// Make him disappear when he runs far enough away.
		if(p != null && p.size() == 0) {
			farlsworthLost.setCompleted(true);
			this.destroy();
		}
	}
	
	// Interact with object. 
	public void interactWith() { 
		interactMoved = false;
		this.facingDirection = stringUtils.oppositeDir(player.getCurrentPlayer().getFacingDirection());
		interactSequence = makeNormalInteractSequence();
		interactSequence.toggleDisplay();
	}

	///////////////
	/// METHODS ///
	///////////////
	// Constructor
	public farlsworth(int newX, int newY) {
		super(sheepType, "Farlsworth", newX, newY);
		
		// Facing direction.
		facingDirection = "Up";
		
		// Set interactable.
		interactable = true;
		
		// Get whether or not he's lost.
		farlsworthLost = new event("farlsworthLost");
		
		// If he's lost, don't spawn him in the farm.
		if(farlsworthLost.isCompleted() && 
		   player.getCurrentPlayer().getCurrentZone().getName().equals("sheepFarm")) {
			this.destroy();
		}
		
		// Set dimensions
		setHeight(getDefaultHeight());
		setWidth(getDefaultWidth());
		platformerHeight = DEFAULT_PLATFORMER_HEIGHT;
		platformerWidth = DEFAULT_PLATFORMER_WIDTH;
		topDownHeight = DEFAULT_TOPDOWN_HEIGHT;
		topDownWidth = DEFAULT_TOPDOWN_WIDTH;
		setHitBoxAdjustmentY(getDefaultHitBoxAdjustmentY());
	}
	
	// React to pain.
	public void reactToPain() {
	}
	
	// Farlsworth AI
	public void updateUnit() {
		
		// Stuff to do in non-boss fight mode.
		if(!bossFight) {
			doInteractStuff();
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
