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
	
	// What part of the sequence are we at?
	private int sequencePart = 0;
	
	// Fence attached
	private static ArrayList<chunk> attachedFence = null;
	
	// Events
	public static event isFenceAttached;
	private static event pastSpawnFarm;
	private static event pastFlowerPatch;
	private static event pastTombEntrance;
	private static event pastDenmother;
	
	// Events that make him like you more TODO:
	private static event didYouTellHimAboutYourAdventure; // You didn't grab him twice.
	private static event didYouLieToHimAboutHavingTheKey; // Did you lie to him about having the gate key?
	private static event didYouOpenTheGateForHim; // Did you open the fence for him?
	private static event didYouTryToGrabHim; // You tried to grab him at the flower field.
	private static event doYouSpeakSheep;
	
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
				startOfConversation = new textSeries(null, "He's sent somebody to gather my wool, has he?");
				s = startOfConversation.addChild(null,"Well, if that's what you want, you can't have it.");
				s = s.addChild(null, "And don't try anything fishy, buddy.");
				textSeries rodeo = s.addChild(null, "This isn't my first rodeo.");
				
				// Give me your wool
				textSeries giveMeYourWool = rodeo.addChild("\'Give me your wool\'", "Is that all I'm good for?");
				s =  giveMeYourWool.addChild(null, "My wool?");
				s = s.addChild(null, "I don't think so, pal.");
				s = s.addChild(null, "I will not be friggin objectified.");
				s =  s.addChild(null, "Leave me alone.");
				s.setEnd();
				
				// I'm on an adventure
				textSeries adventure = rodeo.addChild("\'I'm on an adventure\'", "Neat.");
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
					textSeries speciesist = s.addChild(null, "I am a strong independent sheep who don't need no goat.");
					
					// Ask for his wool
					textSeries grabHisWool = speciesist.addChild("\'Give me your wool\'", "Boy, you're persistent, aren't you?");
					s = grabHisWool.addChild(null, "Some guy you just met asked you to get my wool.");
					s = s.addChild(null, "And now I'm telling you that you can't have it.");
					s = s.addChild(null, "Shouldn't those cancel out?");
					s = s.addChild(null, "Oh right, it's because I'm a sheep and he's a human.");
					s = s.addChild(null, "Pfft. Typical.");
					s = s.addChild(null, "You're just like the rest.");
					s.setEnd();
					
					// Ask for his wool
					textSeries adventure = speciesist.addChild("\'I'm on an adventure\'", "Oh boy, that sounds really fun.");
					s = adventure.addChild(null, "Mind if I tag along?");
					s = s.addChild(null, "Just kidding, that sounds really awful.");
					s = s.addChild(null, "Adventures suck.");
					s = s.addChild(null, "I'm going to go eat some dirty old dry grass.");
					s = s.addChild(null, "See you later.");
					s.setEnd();
				}
			}
			// 1
			else if(interactTimes == 2) {
				// Did you tell him about the adventure?
				if(didYouTellHimAboutYourAdventure.isCompleted()) {
					startOfConversation = new textSeries(null, "You're really itching for any adventure aren't you?");
					s = startOfConversation.addChild(null, "Well, then ...");
					s = s.addChild(null, "Fine.");
					s = s.addChild(null, "You wanted my wool?");
					s = s.addChild(null, "Then come and get it.");
					s.setEnd();
				}
				
				// No, you fucking didn't.
				else {
					startOfConversation = new textSeries(null, "How hard do I have to make this?");
					s = startOfConversation.addChild(null, "You know what? Sure.");
					s = s.addChild(null, "You want my wool?");
					s = s.addChild(null, "You can have it.");
					s = s.addChild(null, "If you can catch me.");
					s.setEnd();
				}
			}
			else {
				startOfConversation = new textSeries(null, "... this is kind of awkward.");
				s = startOfConversation.addChild(null, "But I was trying to run away.");
				s = s.addChild(null, "And there appears to be a gate here.");
				
				textSeries doYouHaveTheKey = s.addChild(null, "Do you have the key for this?");
				textSeries canYouOpenTheFence = new textSeries(null, "Can you open this for me?");
				s = doYouHaveTheKey.addChild("Yes", "Great. That's some good stuff.");
				s.addChild(canYouOpenTheFence);
				s = doYouHaveTheKey.addChild("No", "Oh boy, uh ...");
				s = s.addChild(null, "That was more of a rhetorical question.");
				s = s.addChild(null, "You were in my pen so you obviously have the key.");
				s = s.addChild(null, "Why you gotta lie to a sheep like that, man?");
				s.addChild(canYouOpenTheFence);
				
				s = canYouOpenTheFence.addChild("Yes", "Thanks ... uh ...");
				s = s.addChild(null, "Catch me if you can, I guess.");
				s.setEnd();
				
				s = canYouOpenTheFence.addChild("No", "Well ...");
				s = s.addChild(null, "I suppose I'll have to stay in the fence then.");
				s = s.addChild(null, "Catch me if you can.");
				s.setEnd();
			}
		}
		else if(!pastFlowerPatch.isCompleted()) {
			startOfConversation = new textSeries(null, "Pretty flowers, aren't they?");
			s = startOfConversation.addChild(null, "Weeds can be pretty too, you know.");
			s = s.addChild(null, "Some weeds even taste better than flowers.");
			s = s.addChild(null, "Except the prickly ones.");
			s = s.addChild(null, "Those taste like immense pain.");
			s = s.addChild(null, "Shouldn't you be trying to retrieve my wool?");
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
			textSeries dontGrab = givingTheOption.addChild("Don't grab him", "Hmm ...");
			s = dontGrab.addChild(null, "I guess everybody responds differently to taunting.");
			s = s.addChild(null, "Maybe I misjudged a book by it's cover.");
			s = s.addChild(null, "An ugly, stupid cover. But still misjudged.");
			s = s.addChild(null, "Time will tell.");
			s.setEnd();
		}
		else if(!pastTombEntrance.isCompleted()) {
			startOfConversation = new textSeries(null, "Bah.");
			
			textSeries conversation1 = startOfConversation.addChild("\'What are you doing?\'", "Bah. Baaah?");
			textSeries conversation2 = startOfConversation.addChild("\'Give me your wool.\'", "Baah, baah bah?");
			
			textSeries what = conversation1.addChild("\'What?\'", "Baah.");
			conversation2.addChild(what);
			
			textSeries bah = conversation1.addChild("\'Bah.\'", "Baah.");
			conversation2.addChild(bah);
			
			textSeries who1 = what.addChild(null, "Who are you talking to?");
			s = bah.addChild(null, "Oh, I didn't know you spoke sheep.");
			textSeries who2 = s.addChild(null, "Wait, who were you talking to?");
			
			// Conversation with Ben
			textSeries ben = who2.addChild(null, "Ben?");
			who1.addChild(ben);
			s = ben.addChild(null, "Bah.");
			s.setTalker("Ben");
			s = s.addChild(null, "What the frig are you doing here?");
			s = s.addChild(null, "This is my adventurer.");
			s = s.addChild(null, "Get your own.");
			s = s.addChild(null, "Get your wool together, man.");
			s = s.addChild(null, "Go home.");
			s = s.addChild(null, "Baaaah.");
			s.setTalker("Ben");
			s = s.addChild(null, "Sorry about him, he's been going through rough times.");
			s = s.addChild(null, "Fighting with the goat at home, lambs acting out at school.");
			s = s.addChild(null, "But, hey, a new sun rises every morning.");
			s = s.addChild(null, "He'll be fine.");
			s = s.addChild(null, "You may not be, though.");
			s = s.addChild(null, "That is, if you're not careful in this tomb.");
			s = s.addChild(null, "Spooky stuff, in there.");
			s = s.addChild(null, "If you find yourself afraid of your own shadow ...");
			s = s.addChild(null, "... just shed some light on it.");
			s = s.addChild(null, "You'll find that there's nothing to be scared of.");
			s = s.addChild(null, "Good luck.");
			s.setEnd();
		}
		
		return new interactBox(startOfConversation, stringUtils.toTitleCase(DEFAULT_UNIT_NAME), true);
	}
	
	// Booleans
	private boolean movedFromFence = false;
	private long waitStart = 0;
	private float waitFor = 0;
	private boolean waiting = false;
	
	// Ben.
	sheep ben;
	
	// Do interact stuff.
	public void doInteractStuff() {
		
		// Load player.
		player currPlayer = player.getCurrentPlayer();
		
		// If we are in the farm.
		if(pastSpawnFarm != null && !pastSpawnFarm.isCompleted()) {
			
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
			
			// You lied to him.
			if(interactSequence!=null && interactSequence.getTheText()!=null && interactSequence.getTheText().getTextOnPress()!=null &&
					interactSequence.getTheText().getTextOnPress().contains("Oh boy")) {
				didYouLieToHimAboutHavingTheKey.setCompleted(true);
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
				sound s = new sound(bleet);
				s.setPosition(getIntX(), getIntY(), sound.DEFAULT_SOUND_RADIUS);
				s.start();
			}
			
			// Pissy Farlsworth runs away third time.
			if(!interactMoved && interactSequence != null && interactSequence.getTheText().isEnd() && interactTimes == 2) {
				interactTimes++;
				p = new ArrayList<intTuple>();
				p.add(new intTuple(425,-70));
				p.add(new intTuple(425,5));
				p.add(new intTuple(5,-1));
				p.add(new intTuple(5,-420));
				followPath(p);
				interactMoved = true;
				sound s = new sound(bleet);
				s.setPosition(getIntX(), getIntY(), sound.DEFAULT_SOUND_RADIUS);
				s.start();
			}
			
			// Check if we need to do comedic timing.
			if(!waiting && (interactSequence != null && interactSequence.isDisplayOn() && interactSequence.getTheText().getTextOnPress()!=null) &&
					(interactSequence.getTheText().getTextOnPress().equals("I suppose I'll have to stay in the fence then."))) {
				waitStart = time.getTime();
				waitFor = 2.5f;
				waiting = true;
				interactSequence.setLocked(true);
			}
			
			// Run off with the fence.
			if(waiting && time.getTime() - waitStart > waitFor*1000) {
				interactSequence.goToNext();
				waiting = false;
			}
			
			// Do we attach fence to him?
			if(!isFenceAttached.isCompleted() && (interactSequence != null && interactSequence.isDisplayOn() && interactSequence.getTheText().isEnd() && interactSequence.getTheText().getTextOnPress()!=null) &&
					(interactSequence.getTheText().getTextOnPress().equals("Catch me if you can."))) {
				attachFence();
				p = new ArrayList<intTuple>();
				p.add(new intTuple(13,-1003));
				p.add(new intTuple(366,-1266));
				p.add(new intTuple(842,-1312));
				p.add(new intTuple(1322,-1210));
				p.add(new intTuple(1413,-912));
				followPath(p);
				sound s = new sound(bleet);
				s.setPosition(getIntX(), getIntY(), sound.DEFAULT_SOUND_RADIUS);
				s.start();
				pastSpawnFarm.setCompleted(true);
				saveState.setQuiet(true);
				saveState.createSaveState();
				saveState.setQuiet(false);
				movedFromFence = true;
			}
			
			// Do we attach fence to him?
			if(!isFenceAttached.isCompleted() && (interactSequence != null && interactSequence.isDisplayOn() && interactSequence.getTheText().isEnd() && interactSequence.getTheText().getTextOnPress()!=null) &&
					(interactSequence.getTheText().getTextOnPress().equals("Catch me if you can, I guess."))) {
				sheepFarm.forestGate.open();
				p = new ArrayList<intTuple>();
				p.add(new intTuple(13,-1003));
				p.add(new intTuple(366,-1266));
				p.add(new intTuple(842,-1312));
				p.add(new intTuple(1322,-1210));
				p.add(new intTuple(1413,-912));
				followPath(p);
				sound s = new sound(bleet);
				s.setPosition(getIntX(), getIntY(), sound.DEFAULT_SOUND_RADIUS);
				s.start();
				pastSpawnFarm.setCompleted(true);
				didYouOpenTheGateForHim.setCompleted(true);
				saveState.setQuiet(true);
				saveState.createSaveState();
				saveState.setQuiet(false);
				movedFromFence = true;
			}
		}
		
		// At the flower patch
		else if(pastFlowerPatch!= null && !pastFlowerPatch.isCompleted()) {
			
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
					(interactSequence.getTheText().getTextOnPress().equals("Time will tell."))) {
				
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
				interactSequence.setUnescapable(false);
				p = new ArrayList<intTuple>();
				p.add(new intTuple(2307,-121));
				p.add(new intTuple(2562,-121));
				p.add(new intTuple(2758,-437));
				p.add(new intTuple(2926,-437));
				p.add(new intTuple(2871,-564));
				p.add(new intTuple(2697,-805));
				p.add(new intTuple(2697,-1120));
				p.add(new intTuple(2697,-1405));
				sound s = new sound(bleet);
				s.setPosition(getIntX(), getIntY(), sound.DEFAULT_SOUND_RADIUS);
				s.start();
				followPath(p);
				pastFlowerPatch.setCompleted(true);
				saveState.setQuiet(true);
				saveState.createSaveState();
				saveState.setQuiet(false);
				sequencePart = 0;
			}
		}
		
		// At the tomb
		else if(pastTombEntrance != null && !pastTombEntrance.isCompleted()) {
			
			// Spawn Farlsworth at the tomb entrance.
			if(sequencePart == 0 && (p == null || p.size() == 0)) {
				
				// Spawn Ben in front of the tomb
				stopMove("all");
				setFloatX(-10000);
				setFloatY(-10000);
				ben = new sheep(-10000,-10000);
				ben.setMeanders(false);
				ben.setFloatX(2227);
				ben.setFloatY(-3818);
				ben.setFacingDirection("Left");
				ben.setBenAnimations();
				sequencePart++;
			}
			
			// Talk to player if he/she walks to Farlsworth at flower patch.
			if(sequencePart == 1 && (interactSequence == null || (interactSequence != null && !interactSequence.isDisplayOn())) && 
				currPlayer != null && currPlayer.isWithin(2100,-3884,2411,-3655)) {
				interactSequence = makeNormalInteractSequence();
				if(interactBox.getCurrentDisplay() != null) {
					interactBox.getCurrentDisplay().toggleDisplay();
				}
				interactSequence.toggleDisplay();
				interactSequence.setUnescapable(true);
				currPlayer.stopMove("all");
				sequencePart++;
			}
			
			// Set wait timer for comedic timing.
			if(sequencePart == 2 && (interactSequence != null && interactSequence.isDisplayOn() && interactSequence.getTheText().getTextOnPress()!=null) &&
				(interactSequence.getTheText().getTextOnPress().equals("Baah."))) {
				
				// Wait.
				waiting = true;
				waitFor = 0.75f;
				waitStart = time.getTime();
				
				// Lock sequence.
				interactSequence.setLocked(true);
				
				// Run away
				sequencePart++;
			}
			
			// Spawn Farlsworth.
			if(sequencePart == 3 && time.getTime() - waitStart > waitFor*1000) {
				this.setFloatX(1507);
				this.setFloatY(-3911);
				this.moveTo(1900,-3911);
				sequencePart++;
			}
			
			// If Farlsworth has stopped moving.
			if(sequencePart == 4 && !isMoving()) {
				interactSequence.goToNext();
				sequencePart++;
				interactSequence.setLocked(false);
			}
			
			if(sequencePart == 5 && (interactSequence != null && interactSequence.isDisplayOn() && interactSequence.getTheText().getTextOnPress()!=null) &&
					(interactSequence.getTheText().getTextOnPress().contains("spoke sheep"))) {
				doYouSpeakSheep.setCompleted(true);
			}
			
			// If we are moving forward to see who is talking.
			if(sequencePart == 5 && (interactSequence != null && interactSequence.isDisplayOn() && interactSequence.getTheText().getTextOnPress()!=null) &&
					(interactSequence.getTheText().getTextOnPress().contains("talking to?"))) {
				
				// Lock sequence for movement of Farlsworth.
				interactSequence.setLocked(true);
				sequencePart++;
				
				// Wait.
				waiting = true;
				waitFor = 2f;
				waitStart = time.getTime();
			}
			
			// Wait for comedic timing.
			if(sequencePart == 6 && time.getTime() - waitStart > waitFor*1000) {
				
				// Move to in front of Ben.
				ArrayList<intTuple> p = new ArrayList<intTuple>();
				p.add(new intTuple(2079,-3875));
				p.add(new intTuple(2150,-3825));
				followPath(p);
				sequencePart++;
			}
			
			// Talk to Ben.
			if(sequencePart == 7 && !isMoving() && !isFollowingAPath()) {
				interactSequence.setLocked(false);
				
				// Continue conversation.
				sequencePart++;
				interactSequence.goToNext();
			}
			
			// Make Ben leave.
			if(sequencePart == 8 && (interactSequence != null && interactSequence.isDisplayOn() && interactSequence.getTheText().getTextOnPress()!=null) &&
					(interactSequence.getTheText().getTextOnPress().contains("Baaaah."))) {
				ArrayList<intTuple> path = new ArrayList<intTuple>();
				path.add(new intTuple(2104,-3866));
				path.add(new intTuple(1943,-3866));
				path.add(new intTuple(1794,-3807));
				path.add(new intTuple(1279,-3807));
				ben.followPath(path);
				sound s = new sound(bleet);
				s.setPosition(ben.getIntX(), ben.getIntY(), sound.DEFAULT_SOUND_RADIUS);
				s.start();
				sequencePart++;
				
				// Lock the sequence until Ben leaves.
				interactSequence.setLocked(true);
			}
			
			// Turn Farlsworth.
			if(sequencePart == 9 && ben.getIntX() < getIntX()) {
				setFacingDirection("Left");
				sequencePart++;
			}
			
			// Turn the player.
			if(sequencePart == 10 && ben.getIntX() < currPlayer.getIntX()) {
				currPlayer.setFacingDirection("Left");
				sequencePart++;
			}
			
			// When Ben has left, allow the conversation to continue.
			if(sequencePart == 11 && !ben.isOnScreen()) {
				
				// Destroy Ben, the fucker.
				ben.destroy();
				
				// Set facing direction right.
				currPlayer.setFacingDirection("Right");
				
				// Wait.
				waiting = true;
				waitFor = 0.5f;
				waitStart = time.getTime();
				sequencePart++;
			}
			
			// Wait for comedic timing
			if(sequencePart == 12 && time.getTime() - waitStart > waitFor*1000) {
				
				// Move conversation forward.
				sequencePart++;
				interactSequence.setLocked(false);
				interactSequence.goToNext();
			}
			
			// Run into the tomb.
			if(sequencePart == 13 && (interactSequence != null && interactSequence.isDisplayOn() && interactSequence.getTheText().getTextOnPress()!=null) &&
					(interactSequence.getTheText().getTextOnPress().contains("ood luck"))) {
				
				// Move conversation forward.
				sequencePart = 100;
			}
			
			// Follow the path
			if(sequencePart == 100) {
				p = new ArrayList<intTuple>();
				interactSequence.setUnescapable(false);
				p.add(new intTuple(2330,-3800));
				p.add(new intTuple(2330,-3980));
				p.add(new intTuple(2330,-3990));
				followPath(p);
				pastTombEntrance.setCompleted(true);
				saveState.setQuiet(true);
				saveState.createSaveState();
				saveState.setQuiet(false);
				sequencePart = 0;
			}
			
		}
		else {
			if(sequencePart == 0 && (p == null || p.size() == 0)) {
				stopMove("all");
				setFloatX(-10000);
				setFloatY(-10000);
			}
		}
	}
	
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
		
		// Standing left animation.
		animation standingLeft = new animation("standingLeft", getObjectSpriteSheet().getAnimation(1), 3, 3, 1);
		unitTypeAnimations.addAnimation(standingLeft);
		
		// Standing right animation.
		animation standingRight = new animation("standingRight", getObjectSpriteSheet().getAnimation(3), 3, 3, 1);
		unitTypeAnimations.addAnimation(standingRight);
		
		// Running left animation.
		animation runningLeft = new animation("runningLeft", getObjectSpriteSheet().getAnimation(1), 0, 3, 0.75f/5);
		unitTypeAnimations.addAnimation(runningLeft);		
		
		// Running right animation.
		animation runningRight = new animation("runningRight", getObjectSpriteSheet().getAnimation(3), 0, 3, 0.75f/5);
		unitTypeAnimations.addAnimation(runningRight);
		
		// Standing up animation.
		animation standingUp = new animation("standingUp", getObjectSpriteSheet().getAnimation(0), 3, 3, 1);
		unitTypeAnimations.addAnimation(standingUp);
		
		// Standing down animation.
		animation standingDown = new animation("standingDown", getObjectSpriteSheet().getAnimation(2), 3, 3, 1);
		unitTypeAnimations.addAnimation(standingDown);
		
		// Running up animation.
		animation runningUp = new animation("runningUp", getObjectSpriteSheet().getAnimation(0), 0, 3, 0.75f/5);
		unitTypeAnimations.addAnimation(runningUp);
		
		// Running down animation.
		animation runningDown = new animation("runningDown", getObjectSpriteSheet().getAnimation(2), 0, 3, 0.75f/5);
		unitTypeAnimations.addAnimation(runningDown);
		
		// Set animations.
		setAnimations(unitTypeAnimations);
		
		// Get whether or not he's lost.
		isFenceAttached = new event("farlsworthFenceAttached");
		pastSpawnFarm = new event("farlsworthRan");
		pastDenmother = new event("farlsworthPastDenmother");
		pastFlowerPatch = new event("farlsworthPastFlowerPatch");
		pastTombEntrance = new event("farlsworthPastTombEntrance");
		
		// Good/bad events
		didYouOpenTheGateForHim = new event("farlsworthDidYouOpenTheGateForHim");
		didYouTryToGrabHim = new event("farlsworthDidYouTryToGrabHim");
		didYouTellHimAboutYourAdventure = new event("farlsworthDidYouTellHimAboutYourAdventure");
		didYouLieToHimAboutHavingTheKey = new event("farlsworthDidYouLieToHimAboutHavingTheKey");
		doYouSpeakSheep = new event("farlsworthDoYouSpeakSheep");
		
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
	
	// Print state of affairs with farlsworth
	public void printFarlsworthEvents() {
		System.out.println("farlsworthDidYouOpenTheGateForHim" + didYouOpenTheGateForHim.isCompleted());
		System.out.println("farlsworthDidYouTryToGrabHim" + didYouTryToGrabHim.isCompleted());
		System.out.println("didYouTellHimAboutYourAdventure" + didYouTellHimAboutYourAdventure.isCompleted());
		System.out.println("didYouLieToHimAboutHavingTheKey" + didYouLieToHimAboutHavingTheKey.isCompleted());
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
