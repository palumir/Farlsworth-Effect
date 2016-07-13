package units.bosses;

import java.util.ArrayList;

import doodads.sheepFarm.woolPiece;
import drawing.spriteSheet;
import drawing.animation.animation;
import drawing.animation.animationPack;
import drawing.spriteSheet.spriteSheetInfo;
import interactions.event;
import interactions.interactBox;
import interactions.textSeries;
import modes.mode;
import sounds.sound;
import terrain.chunk;
import units.boss;
import units.player;
import units.unitType;
import units.unitTypes.farmLand.sheepFarm.sheep;
import utilities.intTuple;
import utilities.saveState;
import utilities.stringUtils;
import utilities.time;
import zones.farmLand.sheepFarm;

public class farlsworth extends boss {
	
	// Platformer real dimensions
	public static int DEFAULT_PLATFORMER_HEIGHT = 18;
	public static int DEFAULT_PLATFORMER_WIDTH = 20;
	public static int DEFAULT_PLATFORMER_ADJUSTMENT_Y = 0;
	
	// TopDown real dimensions
	public static int DEFAULT_TOPDOWN_HEIGHT = 18;
	public static int DEFAULT_TOPDOWN_WIDTH = 20;
	public static int DEFAULT_TOPDOWN_ADJUSTMENT_Y = 4;
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default name.
	private static String DEFAULT_UNIT_NAME = "Farlsworth";
	
	// Default movespeed.
	private static int DEFAULT_UNIT_MOVESPEED = 5;
	
	// Default jump speed
	private static int DEFAULT_UNIT_JUMPSPEED = 10;
	
	// FARLSWORTH sprite stuff.
	private static String DEFAULT_FARLSWORTH_SPRITESHEET = "images/units/animals/sheep.png";
	
	// The actual type.
	private static unitType sheepType  =
			new unitType(DEFAULT_UNIT_NAME,  // Name of unitType 
					  new spriteSheet(new spriteSheetInfo(
							"images/units/animals/sheep.png", 
							90, 
							90,
							0,
							DEFAULT_TOPDOWN_ADJUSTMENT_Y
							)),
					     null,
					     DEFAULT_TOPDOWN_WIDTH,
					     DEFAULT_TOPDOWN_HEIGHT,
					     DEFAULT_UNIT_MOVESPEED, // Movespeed
					     DEFAULT_UNIT_JUMPSPEED // Jump speed
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
	private static String otherBleet = "sounds/effects/animals/sheep1.wav";
	
	// Are we in boss fight mode?
	private boolean bossFight = false;
	
	// Interact times.
	private int interactTimes = 0;
	private boolean interactMoved = false;
	private boolean standingInFrontOfFence = false;
	
	// What part of the sequence are we at?
	private int sequencePart = 0;
	
	// Fence attached
	private static ArrayList<chunk> attachedFence = null;
	
	// Events
	public static event isFenceAttached;
	private static event pastSpawnFarm;
	private static event pastFlowerPatch;
	private static event pastDenmother;
	
	// Events that make him like you more TODO:
	private static event didYouOpenTheGateForHim;
	private static event didYouTryToGrabHim;
	private static event didYouTellHimAboutYourAdventure;
	
	///////////////
	/// METHODS ///
	///////////////
	
	// Create interact sequence
	public interactBox makeNormalInteractSequence() {
	
		// Placeholder for each individual textSeries.
		textSeries s = null;
		
		// Start.
		textSeries startOfConversation = null;
		
		// Farlsworth in barn.
		if(!pastSpawnFarm.isCompleted()) {
			
			// 0;
			if(interactTimes == 0) {
				// Start of conversation.
				startOfConversation = new textSeries(null, "He's sent another dumby to gather my wool, has he?");
				
				// Give me your wool
				textSeries giveMeYourWool = startOfConversation.addChild("\"Give me your wool\"", "Is that all I'm good for?");
				s =  giveMeYourWool.addChild(null, "My wool?");
				s = s.addChild(null, "I don't think so, buddy.");
				s = s.addChild(null, "I will not be friggin objectified.");
				s =  s.addChild(null, "Leave me alone.");
				s.setEnd();
				
				// I'm on an adventure
				textSeries adventure = startOfConversation.addChild("\"I'm on an adventure\"", "Neat.");
				s =  adventure.addChild(null, "I'm not sure what that has to do with me.");
				s =  s.addChild(null, "Hope you have a friggin good one.");
				s.setEnd();
			} 
			
			// 1
			else if(interactTimes == 1) {
				
				// Did you tell him about the adventure?
				if(didYouTellHimAboutYourAdventure.isCompleted()) {
					startOfConversation = new textSeries(null, "Do you know what happens when a sheep gets too old?");
					s = startOfConversation.addChild(null, "Well, let's just say ... ");
					s = s.addChild(null, "My wool doesn't grow like it used to, buddy.");
					s = s.addChild(null, "I want to relax and enjoy the time I have left.");
					s = s.addChild(null, "My petty adventuring years are over.");
					s = s.addChild(null, "Save that for the kids, dog.");
					s = s.addChild(null, "Let me eat my dirty old grass in peace.");
					s.setEnd();
				}
				
				// No, you fucking didn't.
				else {
					startOfConversation = new textSeries(null, "My wool. My choice.");
					s = startOfConversation.addChild(null, "I refuse to be a slave anymore.");
					s = s.addChild(null, "I am a strong independent sheep who don't need no goat.");
					textSeries speciestist = s.addChild(null, "And you're a speciesist.");
					
					// Ask for his wool
					textSeries grabHisWool = speciestist.addChild("\"I need your wool.\"", "Boy, you're persistent, aren't you?");
					s = grabHisWool.addChild(null, "Some guy you just met asked you to get my wool.");
					s = s.addChild(null, "And now I'm telling you that you can't have it.");
					s = s.addChild(null, "Shouldn't those cancel out?");
					s = s.addChild(null, "Oh right, it's because I'm a sheep and he's a human.");
					s = s.addChild(null, "Pfft. Typical. You're just like the rest.");
					s = s.addChild(null, "Go away.");
					s.setEnd();
					
					// Ask for his wool
					textSeries adventure = speciestist.addChild("\"I'm on an adventure.\"", "Oh boy, that sounds really fun.");
					s = adventure.addChild(null, "Mind if I tag along?");
					s = s.addChild(null, "Just kidding, that sounds really lame.");
					s = s.addChild(null, "Adventures suck.");
					s = s.addChild(null, "I'm going to go eat dry dirty old grass.");
					s = s.addChild(null, "See you later.");
					s.setEnd();
				}
			}
			// 1
			else if(interactTimes == 2) {
				// Did you tell him about the adventure?
				if(didYouTellHimAboutYourAdventure.isCompleted()) {
					startOfConversation = new textSeries(null, "You'll really take any adventure, won't you?");
					s = startOfConversation.addChild(null, "Well, then ...");
					s = s.addChild(null, "Fine.");
					s = s.addChild(null, "You wanted my wool?");
					s = s.addChild(null, "Then come and get it.");
					s.setEnd();
				}
				
				// No, you fucking didn't.
				else {
					startOfConversation = new textSeries(null, "How hard do I have to make this?");
					s = startOfConversation.addChild(null, "What is it that will make you go away?");
					s = s.addChild(null, "You know what?");
					s = s.addChild(null, "You want my wool?");
					s = s.addChild(null, "You can have it.");
					s = s.addChild(null, "If you can catch me.");
					s.setEnd();
				}
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
					s = s.addChild(null, "Catch me if you can.");
					s.setEnd();
				}
				else {
					startOfConversation = new textSeries(null, "Bah.");
					startOfConversation.setEnd();
				}
			}
		}
		else if(!pastFlowerPatch.isCompleted()) {
			startOfConversation = new textSeries(null, "Beautiful flowers, aren't they?");
			s = startOfConversation.addChild(null, "Weeds are pretty too, you know.");
			s = s.addChild(null, "What is it that makes weed and a flower different?");
			s = s.addChild(null, "Some weeds taste better than flowers.");
			s = s.addChild(null, "Except the prickly ones.");
			s = s.addChild(null, "Those taste like immense pain.");
			s = s.addChild(null, "Aren't you supposed to be retrieving my wool?");
			s = s.addChild(null, "Why don't you just grab me while I'm talking?");
			s = s.addChild(null, "You can't, can you?");
			textSeries givingTheOption = s.addChild(null, "Because I'm not giving you the option.");
			
			// Grab him
			textSeries grab = givingTheOption.addChild("Grab him", "Hold your horses, buddy.");
			s = grab.addChild(null, "I said I wasn't giving you the option.");
			s = s.addChild(null, "Didn't you hear me?");
			s = s.addChild(null, "Frig you.");
			s.setEnd();
			
			// Don't grab him
			textSeries dontGrab = givingTheOption.addChild("Don't grab him", "I might be wrong about you.");
			s = dontGrab.addChild(null, "We'll see, I guess.");
			s.setEnd();
		}
		
		return new interactBox(startOfConversation, stringUtils.toTitleCase(DEFAULT_UNIT_NAME), true);
	}
	
	// Booleans
	private boolean movedFromFence = false;
	
	// Do interact stuff.
	public void doInteractStuff() {
		
		// Load player.
		player currPlayer = player.getCurrentPlayer();
		
		// If we are in the farm.
		if(!pastSpawnFarm.isCompleted()) {
			// Pissy Farlsworth runs away first time.
			if(!interactMoved && interactSequence != null && interactSequence.getTheText().isEnd() && interactTimes == 0) {
				
				// What did you pick?
				if(interactSequence.getTheText().getTextOnPress()!=null &&
					interactSequence.getTheText().getTextOnPress().equals("Hope you have a friggin good one.")) {
					didYouTellHimAboutYourAdventure.setCompleted(true);
				}
				
				interactTimes++;
				moveTo(74,-58);
				interactMoved = true;
				sound s = new sound(bleet);
				s.setPosition(getIntX(), getIntY(), sound.DEFAULT_SOUND_RADIUS);
				s.start();
			}
			
			// Pissy Farlsworth runs away second time.
			if(!interactMoved && interactSequence != null && interactSequence.getTheText().isEnd() && interactTimes == 1) {
				
				// What did you pick?
				if(interactSequence.getTheText().getTextOnPress()!=null &&
					interactSequence.getTheText().getTextOnPress().equals("See you later.")) {
					didYouTellHimAboutYourAdventure.setCompleted(true);
				}
				
				interactTimes++;
				moveTo(74,-406);
				interactMoved = true;
				sound s = new sound(otherBleet);
				s.setPosition(getIntX(), getIntY(), sound.DEFAULT_SOUND_RADIUS);
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
				s.setPosition(getIntX(), getIntY(), sound.DEFAULT_SOUND_RADIUS);
				s.start();
			}
			
			// Turn him at the fence.
			if(!standingInFrontOfFence && interactMoved && p != null && p.size() == 0) {
				standingInFrontOfFence = true;
			}
			
			// Do we attach fence to him?
			if(!isFenceAttached.isCompleted() && standingInFrontOfFence && (interactSequence != null && interactSequence.isDisplayOn() && interactSequence.getTheText().isEnd() && interactSequence.getTheText().getTextOnPress()!=null) &&
					(interactSequence.getTheText().getTextOnPress().equals("Catch me if you can."))) {
				attachFence();
				standingInFrontOfFence = false;
				p = new ArrayList<intTuple>();
				p.add(new intTuple(13,-1003));
				p.add(new intTuple(366,-1266));
				p.add(new intTuple(842,-1312));
				p.add(new intTuple(1322,-1210));
				p.add(new intTuple(1413,-912));
				followPath(p);
				pastSpawnFarm.setCompleted(true);
				saveState.setQuiet(true);
				saveState.createSaveState();
				saveState.setQuiet(false);
				movedFromFence = true;
			}
			
			// Do we attach fence to him?
			if(!isFenceAttached.isCompleted() && standingInFrontOfFence && (interactSequence != null && interactSequence.isDisplayOn() && interactSequence.getTheText().isEnd() && interactSequence.getTheText().getTextOnPress()!=null) &&
					(interactSequence.getTheText().getTextOnPress().equals("Catch me if you can, I guess."))) {
				standingInFrontOfFence = false;
				sheepFarm.forestGate.open();
				p = new ArrayList<intTuple>();
				p.add(new intTuple(13,-1003));
				p.add(new intTuple(366,-1266));
				p.add(new intTuple(842,-1312));
				p.add(new intTuple(1322,-1210));
				p.add(new intTuple(1413,-912));
				followPath(p);
				pastSpawnFarm.setCompleted(true);
				didYouOpenTheGateForHim.setCompleted(true);
				saveState.setQuiet(true);
				saveState.createSaveState();
				saveState.setQuiet(false);
				movedFromFence = true;
			}
		}
		
		// At the flower patch
		else if(!pastFlowerPatch.isCompleted()) {
			
			// Spawn Farlsworth at the Flower patch
			if(sequencePart == 0 && (p == null || p.size() == 0)) {
				stopMove("all");
				destroyFence();
				movingToAPoint = false;
				setFloatX(2238);
				setFloatY(-456);
				facingDirection = "Left";
				sequencePart++;
			}
			
			// Talk to player if he/she walks to Farlsworth at flower patch.
			if(sequencePart == 1 && (interactSequence == null || (interactSequence != null && !interactSequence.isDisplayOn())) && 
				currPlayer != null && currPlayer.isWithin(2080,-478,2242,-363)) {
				interactSequence = makeNormalInteractSequence();
				if(interactBox.getCurrentDisplay() != null) {
					interactBox.getCurrentDisplay().toggleDisplay();
				}
				interactSequence.toggleDisplay();
				interactSequence.setUnescapable(true);
				currPlayer.stopMove("all");
				sequencePart++;
			}
			
			// Don't grab him
			if(sequencePart == 2 && (interactSequence != null && interactSequence.isDisplayOn() && interactSequence.getTheText().getTextOnPress()!=null) &&
					(interactSequence.getTheText().getTextOnPress().equals("We'll see, I guess."))) {
				
				// Run away
				sequencePart = 100; // Put the sequence on the last event.
			}
			
			// Grab him
			if(sequencePart == 2 && (interactSequence != null && interactSequence.isDisplayOn() && interactSequence.getTheText().getTextOnPress()!=null) &&
					(interactSequence.getTheText().getTextOnPress().equals("Hold your horses, buddy."))) {
				
				// Move player and Farlsworth
				sound s = new sound(bleet);
				s.start();
				moveTo(this.getIntX() + 70, this.getIntY());
				player.getCurrentPlayer().moveTo(player.getCurrentPlayer().getIntX()+70, player.getCurrentPlayer().getIntY());
				
				// Run away
				didYouTryToGrabHim.setCompleted(true);
				sequencePart++;
				interactSequence.setLocked(true);
			}
			
			// Turn back
			else if(sequencePart == 3 && !movingToAPoint) {
				moveTo(this.getIntX() -1, this.getIntY());
				interactSequence.setLocked(false);
				sequencePart++;
			}
			
			// Run at the end of the conversation
			if(sequencePart == 4 && (interactSequence != null && interactSequence.isDisplayOn() && interactSequence.getTheText().getTextOnPress()!=null) &&
					(interactSequence.getTheText().getTextOnPress().equals("Frig you."))) {
				
				// Run away
				sequencePart = 100;
			}
			
			// Run
			if(sequencePart == 100) {
				sound s = new sound(otherBleet);
				s.start();
				interactSequence.setUnescapable(false);
				p = new ArrayList<intTuple>();
				p.add(new intTuple(2366,-107));
				p.add(new intTuple(2630,-128));
				p.add(new intTuple(2762,-419));
				p.add(new intTuple(2921,-419));
				p.add(new intTuple(2856,-579));
				p.add(new intTuple(2680,-944));
				p.add(new intTuple(2680,-1322));
				followPath(p);
				pastFlowerPatch.setCompleted(true);
				saveState.setQuiet(true);
				saveState.createSaveState();
				saveState.setQuiet(false);
				sequencePart = 0;
			}
			
			/*s = s.addChild(null, "Because I'm not giving you the option.");
			
			// Grab him
			textSeries grab = s.addChild("Grab him", "Hold your horses, buddy.");
			s = grab.addChild(null, "I said I wasn't giving you the option.");
			s = s.addChild(null, "Didn't you hear me?");
			s = s.addChild(null, "Frig you.");
			s.setEnd();
			
			// Don't grab him
			textSeries dontGrab = s.addChild("Don't grab him", "I might be wrong about you.");
			s = dontGrab.addChild(null, "We'll see, I guess.");*/
		}
		
		// At denmother
		else if(!pastDenmother.isCompleted()) {
			
			// Spawn Farlsworth at Denmother TODO: he needs to go somewhere else.
			if(sequencePart == 0 && (p == null || p.size() == 0)) {
				
				// Spawn him in front of Denmother.
				destroyFence();
				stopMove("all");
				setFloatX(-100000);
				setFloatY(-100000);
				attachFence();
				facingDirection = "Up";
				sequencePart++;
			}
			
			// Start the event if we enter a region.
			/*if((interactSequence == null || (interactSequence != null && !interactSequence.isDisplayOn() && runFromDenmotherStart == 0)) && 
				currPlayer != null && currPlayer.isWithin(1383,-3349,1650,-3180)) {
				interactSequence = makeNormalInteractSequence();
				if(interactBox.getCurrentDisplay() != null) {
					interactBox.getCurrentDisplay().toggleDisplay();
				}
				interactSequence.toggleDisplay();
				interactSequence.setUnescapable(true);
				currPlayer.stopMove("all");
			}*/
			
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
		setInteractable(true);
		
		// Deal with animations
		animationPack unitTypeAnimations = new animationPack();
		
		// Jumping left animation.
		//animation jumpingLeft = new animation("jumpingLeft", getObjectSpriteSheet().getAnimation(6), 4, 4, 1);
		//unitTypeAnimations.addAnimation(jumpingLeft);
		
		// Jumping right animation.
		//animation jumpingRight = new animation("jumpingRight", getObjectSpriteSheet().getAnimation(2), 4, 4, 1);
		//unitTypeAnimations.addAnimation(jumpingRight);
		
		// Standing left animation.
		animation standingLeft = new animation("standingLeft", getObjectSpriteSheet().getAnimation(1), 3, 3, 1);
		unitTypeAnimations.addAnimation(standingLeft);
		
		// Standing right animation.
		animation standingRight = new animation("standingRight", getObjectSpriteSheet().getAnimation(3), 3, 3, 1);
		unitTypeAnimations.addAnimation(standingRight);
		
		// Running left animation.
		animation runningLeft = new animation("runningLeft", getObjectSpriteSheet().getAnimation(1), 0, 3, 1f);
		unitTypeAnimations.addAnimation(runningLeft);		
		
		// Running right animation.
		animation runningRight = new animation("runningRight", getObjectSpriteSheet().getAnimation(3), 0, 3, 1f);
		unitTypeAnimations.addAnimation(runningRight);
		
		// Standing up animation.
		animation standingUp = new animation("standingUp", getObjectSpriteSheet().getAnimation(0), 3, 3, 1);
		unitTypeAnimations.addAnimation(standingUp);
		
		// Standing down animation.
		animation standingDown = new animation("standingDown", getObjectSpriteSheet().getAnimation(2), 3, 3, 1);
		unitTypeAnimations.addAnimation(standingDown);
		
		// Running up animation.
		animation runningUp = new animation("runningUp", getObjectSpriteSheet().getAnimation(0), 0, 3, 1f);
		unitTypeAnimations.addAnimation(runningUp);
		
		// Running down animation.
		animation runningDown = new animation("runningDown", getObjectSpriteSheet().getAnimation(2), 0, 3, 1f);
		unitTypeAnimations.addAnimation(runningDown);
		
		// Set animations.
		setAnimations(unitTypeAnimations);
		
		// Get whether or not he's lost.
		isFenceAttached = new event("farlsworthFenceAttached");
		pastSpawnFarm = new event("farlsworthRan");
		pastDenmother = new event("farlsworthPastDenmother");
		pastFlowerPatch = new event("farlsworthPastFlowerPatch");
		
		// Good/bad events
		didYouOpenTheGateForHim = new event("farlsworthDidYouOpenTheGateForHim");
		didYouTryToGrabHim = new event("farlsworthDidYouTryToGrabHim");
		didYouTellHimAboutYourAdventure = new event("farlsworthDidYouTellHimAboutYourAdventure");
		
		// If he's lost, don't spawn him in the farm.
		if(pastSpawnFarm.isCompleted() && 
		   player.getCurrentPlayer().getCurrentZone().getName().equals("sheepFarm")) {
			
			// If we aren't past Denmother, spawn Farlsworth there.
			if(!pastDenmother.isCompleted()) {
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
		
		// If fence is attached, attach it
		if(isFenceAttached.isCompleted()) {
			attachFence();
		}
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
	
	// Attach fence.
	public void attachFence() {
		isFenceAttached.setCompleted(true);
		
		// Only add the sheepFarm fence if it already exists, otherwise
		// we need to create the fence.
		if(sheepFarm.farlsworthFence != null) {
			attachedFence = sheepFarm.farlsworthFence;
		}
		
		// Make the fence passable.
		if(attachedFence != null) {
			for(int i = 0; i < attachedFence.size(); i++) {
				attachedFence.get(i).setPassable(true);
			}
		}
	}
	
	// Destroy fence
	public void destroyFence() {
		if(attachedFence != null) {
			for(int i = 0; i < attachedFence.size(); i++) {
				attachedFence.get(i).destroy();
			}
			attachedFence = null;
		}
	}
	
	// Do unit specific movement.
	@Override
	public void unitSpecificMovement(float moveX, float moveY) {
		
		// Move the fence for the joke.
		if(attachedFence != null) {
			for(int i = 0; i < attachedFence.size(); i++) {
				attachedFence.get(i).setFloatX(attachedFence.get(i).getFloatX() + moveX);
				attachedFence.get(i).setFloatY(attachedFence.get(i).getFloatY() + moveY);
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
