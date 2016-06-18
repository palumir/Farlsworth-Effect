package units.unitTypes.farmLand;

import java.util.Random;

import drawing.camera;
import drawing.userInterface.interactBox;
import effects.effect;
import effects.effectTypes.bloodSquirt;
import effects.effectTypes.tooltipString;
import interactions.quest;
import interactions.textSeries;
import modes.mode;
import units.humanType;
import units.player;
import units.unit;
import units.unitType;
import utilities.stringUtils;
import utilities.time;
import utilities.utility;
import zones.zone;

public class farmer extends unit {
	
	// Default dimensions.
	private static int DEFAULT_PLATFORMER_HEIGHT = 46;
	private static int DEFAULT_PLATFORMER_WIDTH = humanType.DEFAULT_UNIT_WIDTH;
	private static int DEFAULT_TOPDOWN_HEIGHT = 20;
	private static int DEFAULT_TOPDOWN_WIDTH = humanType.DEFAULT_UNIT_WIDTH;
	
	// Platformer and topdown default adjustment
	private static int DEFAULT_PLATFORMER_ADJUSTMENT_Y = 6;
	private static int DEFAULT_TOPDOWN_ADJUSTMENT_Y = 20;
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default name.
	private static String unitName = "farmer";
	
	// Default movespeed.
	private static int DEFAULT_FARMER_MOVESPEED = 1;
	
	// Default jump speed
	private static int DEFAULT_FARMER_JUMPSPEED = 10;
	
	// farmer sprite stuff.
	private static String DEFAULT_FARMER_SPRITESHEET = "images/units/humanoid/farmer.png";
	
	// The actual type.
	private static unitType farmerType =
			new humanType( "farmer",  // Name of unitType 
						 DEFAULT_FARMER_SPRITESHEET,
					     DEFAULT_FARMER_MOVESPEED, // Movespeed
					     DEFAULT_FARMER_JUMPSPEED // Jump speed
						);	   
	
	//////////////
	/// FIELDS ///
	//////////////
	
	// Interacting with farmer.
	public quest farlsworthQuest;
	
	// Tooltip help
	private boolean tooltipShown = false;
	
	///////////////
	/// METHODS ///
	///////////////
	// Constructor
	public farmer(int newX, int newY) {
		super(farmerType, newX, newY);
		
		// Interactable.
		interactable = true;
		
		// Make adjustments on hitbox if we're in topDown.
		// Set dimensions
		setHeight(getDefaultHeight());
		setWidth(getDefaultWidth());
		platformerHeight = DEFAULT_PLATFORMER_HEIGHT;
		platformerWidth = DEFAULT_PLATFORMER_WIDTH;
		topDownHeight = DEFAULT_TOPDOWN_HEIGHT;
		topDownWidth = DEFAULT_TOPDOWN_WIDTH;
		setHitBoxAdjustmentY(getDefaultHitBoxAdjustmentY());
		setFacingDirection("Down");
		
		// Create quest.
		farlsworthQuest = makeQuest();
		hasQuest();
	}
	
	// Create conversation
	public quest makeQuest() {
		
		// Description
		String DEFAULT_QUEST_DESC = "Retrieve Farlsworth's wool";
		
		// Placeholder for each individual textSeries.
		textSeries s;
		
		// Start of conversation.
		textSeries startOfConversation = new textSeries(null, "For flock's sake ...");
		s = startOfConversation.addChild(null, "These sheep wool be the death of me.");
		s = s.addChild(null, "The shear thought of collecting more wool pains me.");
		s = s.addChild(null, "If ewe were conveniently looking for a quest ...");
		textSeries firstSpeakToFarmer = s.addChild(null, "... that wool-d be quite ... convenient.");
		
		// Path 1
		textSeries noPuns = firstSpeakToFarmer.addChild("Quit making puns.", "Sorry. It gets pretty boring around here.");
		s = noPuns.addChild(null, "When I was a boy I wanted to be a marine biologist.");
		s = s.addChild(null, "But life happens, and things change.");
		s = s.addChild(null, "Anyway ...");
		s = s.addChild(null, "I require wool from a particular sheep.");
		s = s.addChild(null, "He never makes it easy.");
		s = s.addChild(null, "But he needs to be sheared.");
		s = s.addChild(null, "You'll find him in the pen to the far East.");
		s = s.addChild(null, "Can you collect his wool for me?");
		
		// Saying yes.
		textSeries yes = s.addChild("Yes.","Great. Good luck!");
		
		// Saying no to path 1.
		textSeries noHelp = s.addChild("No.","Please?");
		
		// Keep saying no like an asshole.
		noHelp.addChild(yes);
		s = noHelp.addChild("No.","I'll make it worth your while.");
		s.addChild(yes);
		s = s.addChild("No.","Come on ... ");
		s.addChild(yes);
		s = s.addChild("No.","Well, why not?");
		s.addChild(yes);
		s = s.addChild("No.","That doesn't even make sense.");
		s.addChild(yes);
		s = s.addChild("No.","Well, you can always just sheep on it.");
		s.addChild(yes);
		s = s.addChild("No.","Come on, that was a good one.");
		s.addChild(yes);
		s = s.addChild("No.","Why won't you take the quest?");
		s.addChild(yes);
		s = s.addChild("No.","What's your issue?");
		s.addChild(yes);
		s = s.addChild("No.","Madam, you are harassing me.");
		s.addChild(yes);
		s = s.addChild("No.","Leave me alone.");
		s.addChild(yes);
		s = s.addChild("No.","Can you please just take the quest?");
		s.addChild(yes);
		s = s.addChild("No.","Can you please just take the quest?");
		s.addChild(yes);
		s = s.addChild("No.","Can you please just take the quest?");
		s.addChild(yes);
		s = s.addChild("No.","Can you please just take the quest?");
		s.addChild(yes);
		s = s.addChild("No.","Can you please just take the quest?");
		s.addChild(yes);
		s = s.addChild("No.","Oh, COME ON. How can you STILL possibly be saying no?");
		s.addChild(yes);
		s = s.addChild("No.","You're relentless, really.");
		s.addChild(yes);
		s = s.addChild("No.","That's not a compliment, by the way.");
		s.addChild(yes);
		s = s.addChild("No.","You need counselling.");
		s.addChild(yes);
		s = s.addChild("No.","You can't say no forever.");
		s.addChild(yes);
		s = s.addChild("No.","Actually, you can.");
		s.addChild(yes);
		s.addChild(s);
		
		// Path 2
		textSeries morePuns = firstSpeakToFarmer.addChild("I can help.", "Well ...");
		s = morePuns.addChild(null, "I require wool from a particular sheep.");
		s = s.addChild(null, "He's quite ...");
		s = s.addChild(null, "... sheepish, to say the least.");
		s = s.addChild(null, "Wait, is that even a pun? Whatever ...");
		s = s.addChild(null, "Anyway, this particular sheep is difficult.");
		s = s.addChild(null, "But he needs to be sheared.");
		s = s.addChild(null, "You'll find him in the pen to the far East.");
		s = s.addChild(null, "Be a lamb and go grab his wool for me?");
		s.addChild(yes);
		s.addChild(noHelp);
		yes.setEnd();
		
		// Create the whole quest and add dialogue.
		quest q = new quest(DEFAULT_QUEST_DESC, this, new interactBox(startOfConversation, stringUtils.toTitleCase(unitName)));
		
		// If the quest is started, don't allow the person to do the whole dialogue.
		if(q.isStarted()) {
			q.getDialogue().setTheText(q.getDialogue().getTheText().getEnd());
		}
		
		return q;
	}
	
	// Quest stuff.
	public void doQuestStuff() {

		// If we have reached the end of our quest conversation (and they clicked yes, of course, since it's all they can do.)
		if(farlsworthQuest != null && !farlsworthQuest.completed() && farlsworthQuest.getInteractBox().getTheText().isEnd()) {
			farlsworthQuest.startQuest();
		}
	}
	
	// Interact with object. Should be over-ridden.
	public void interactWith() { 
		if(!farlsworthQuest.completed()) farlsworthQuest.getInteractBox().toggleDisplay();
	}
	
	// React to pain.
	public void reactToPain() {
	}
	
	// Tutorial stuff.
	public void doTutorialStuff() {
		player currPlayer = player.getCurrentPlayer();
		if(!tooltipShown && !farlsworthQuest.isStarted() && currPlayer != null && currPlayer.isWithin(-1017,-283,-450,25)) {
			tooltipShown = true;
			tooltipString t = new tooltipString("Press 'e' to interact with something.");
		}
	}
	
	// Does nothing yet.
	public void updateUnit() {
		doTutorialStuff();
		doQuestStuff();
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
