package units.bosses;

import java.util.ArrayList;

import doodads.sheepFarm.woolPiece;
import interactions.event;
import interactions.interactBox;
import interactions.textSeries;
import modes.mode;
import sounds.sound;
import terrain.chunk;
import units.animalType;
import units.boss;
import units.player;
import units.unitType;
import utilities.intTuple;
import utilities.stringUtils;
import utilities.time;
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
	private boolean standingInFrontOfFence = false;
	
	// Fence attached
	private static ArrayList<chunk> attachedFence = null;
	
	// Events
	private static event isFenceAttached;
	private static event farlsworthRan;
	private static event pastDenmother;
	
	///////////////
	/// METHODS ///
	///////////////
	
	// Create interact sequence
	public interactBox makeNormalInteractSequence() {
	
		// Placeholder for each individual textSeries.
		textSeries s;
		
		// Start.
		textSeries startOfConversation;
		
		if(farlsworthRan.isCompleted() && !hittingDenMother) {
			// Start of conversation.
			startOfConversation = new textSeries(null, "Boy, this pup is fast asleep, isn't she?");
			s = startOfConversation.addChild(null, "She would probably be pretty angry if ... ");
			textSeries s2 = s.addChild(null, "... somebody rudely woke her up.");
			s = s2.addChild("You're crazy", "You're the one talking to a sheep.");
			s.setEnd(); // Hits the dog and runs off.
			s = s2.addChild("Relax", "You're the one who should relax.");
			s.setEnd(); // TODO: work on this dialogue
		}
		
		// Farlsworth in barn.
		else {
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
				if(standingInFrontOfFence) {
					startOfConversation = new textSeries(null, "This is pretty awkward ...");
					s = startOfConversation.addChild(null, "But I was trying to run away.");
					s = s.addChild(null, "And there appears to be a fence here.");
					textSeries question = s.addChild(null, "Can you open this for me?");
					s = question.addChild("Yes", "Thanks ... uh ...");
					s = s.addChild(null, "Catch me if you can, I guess.");
					s.setEnd();
					s = question.addChild("No", "Okay, I guess I'll just stay in the fence then.");
					s.setEnd();
				}
				else {
					startOfConversation = new textSeries(null, "Bah.");
					startOfConversation.setEnd();
				}
			}
		}
		
		return new interactBox(startOfConversation, stringUtils.toTitleCase(DEFAULT_FARLSWORTH_NAME), true);
	}
	
	// Do interact stuff.
	public void doInteractStuff() {
		
		// Load player.
		player currPlayer = player.getCurrentPlayer();
		
		// If we are in the farm.
		if(!farlsworthRan.isCompleted()) {
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
				p.add(new intTuple(-8,-420));
				followPath(p);
				interactMoved = true;
				sound s = new sound(bleet);
				s.setPosition(getX(), getY(), sound.DEFAULT_SOUND_RADIUS);
				s.start();
				System.out.println(true);
			}
			
			// Turn him at the fence.
			if(!standingInFrontOfFence && interactMoved && p != null && p.size() == 0) {
				//farlsworthRan.setCompleted(true);
				standingInFrontOfFence = true;
			}
			
			// Do we attach fence to him?
			if(!isFenceAttached.isCompleted() && standingInFrontOfFence && (interactSequence != null && interactSequence.isDisplayOn() && interactSequence.getTheText().isEnd() && interactSequence.getTheText().getButtonText()!=null) &&
					(interactSequence.getTheText().getButtonText().equals("No"))) {
				isFenceAttached.setCompleted(true);
				
			}
			
		}
		
		// At denmother
		if(!pastDenmother.isCompleted()) {
			
			// Start the event if we enter a region.
			if((interactSequence == null || (interactSequence != null && !interactSequence.isDisplayOn() && runFromDenmotherStart == 0)) && 
				currPlayer != null && currPlayer.isWithin(1383,-3349,1650,-3180)) {
				interactSequence = makeNormalInteractSequence();
				if(interactBox.getCurrentDisplay() != null) {
					interactBox.getCurrentDisplay().toggleDisplay();
				}
				interactSequence.toggleDisplay();
				interactSequence.setUnescapable(true);
				currPlayer.stopMove("all");
			}
			
			// If we fuck up the dialogue.
			if(!hittingDenMother && (interactSequence != null && interactSequence.isDisplayOn() && interactSequence.getTheText().isEnd() && interactSequence.getTheText().getButtonText()!=null) &&
					(interactSequence.getTheText().getButtonText().equals("You're crazy"))) {
				hittingDenMother = true;
				moveTo(1455,-3280);
			}
			
			// If he is hitting Denmother.
			if(hittingDenMother) {
				
				// When he's in front of denmother.
				if(!isMoving() && bahDenmotherStartTime == 0) {
					bahDenmotherStartTime = time.getTime();
				}
				
				// Make him face right after a little bit
				if(!hasBleetedInDenmothersFace && time.getTime() - bahDenmotherStartTime > 0.7f*1000) {
					hasBleetedInDenmothersFace = true;
					sound s = new sound(bleet);
					s.start();
					facingDirection = "Right";
				}
				
				// After he's bah'd
				if(runFromDenmotherStart == 0 && bahDenmotherStartTime != 0 && time.getTime() - bahDenmotherStartTime > bahDuration*1000) {
					moveTo(400,-3280);
					interactSequence.toggleDisplay();
					if(denmother.bossRef != null) denmother.bossRef.wakeUp();
					runFromDenmotherStart = time.getTime();
				}
				
				// Despawn 
				if(runFromDenmotherStart != 0 && time.getTime() - runFromDenmotherStart > runFromDenmotherDuration*1000) {
					hittingDenMother = false;
					pastDenmother.setCompleted(true);
					this.destroy(); // TODO: Destroy for now, but should move to next location.
				}
			}
		}
	}
	
	private boolean hasBleetedInDenmothersFace = false;
	private boolean hittingDenMother = false;
	private long bahDenmotherStartTime = 0;
	private float bahDuration = 2f;
	private float runFromDenmotherDuration = 5f;
	private long runFromDenmotherStart = 0;
	
	// Interact with object. 
	public void interactWith() { 
		if(interactSequence == null || (interactSequence != null && !interactSequence.isUnescapable())) {
			interactMoved = false;
			this.facingDirection = stringUtils.oppositeDir(player.getCurrentPlayer().getFacingDirection());
			interactSequence = makeNormalInteractSequence();
			interactSequence.toggleDisplay();
		}
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
		isFenceAttached = new event("farlsworthFenceAttached");
		farlsworthRan = new event("farlsworthRan");
		pastDenmother = new event("farlsworthPastDenmother");
		
		// If he's lost, don't spawn him in the farm.
		if(farlsworthRan.isCompleted() && 
		   player.getCurrentPlayer().getCurrentZone().getName().equals("sheepFarm")) {
			
			// If we aren't past Denmother, spawn Farlsworth there.
			if(!pastDenmother.isCompleted()) {
				// Spawn him in front of Denmother.
				setX(1584);
				setY(-3217);
				facingDirection = "Right";
			}
			
			// Despawn, we've done all the Farlsworth stuff for the zone.
			else {
				this.destroy(); // TODO: Destroy for now, but should move to next location.
			}
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
