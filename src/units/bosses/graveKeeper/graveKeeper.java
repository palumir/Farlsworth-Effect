package units.bosses.graveKeeper;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import drawing.spriteSheet;
import drawing.animation.animation;
import drawing.animation.animationPack;
import interactions.interactBox;
import interactions.textSeries;
import modes.mode;
import sounds.sound;
import units.boss;
import units.humanType;
import units.player;
import units.unitType;
import units.bosses.wolfless.wolfless;
import utilities.intTuple;
import utilities.saveState;
import utilities.stringUtils;
import utilities.time;
import zones.farmTomb.subZones.farmTomb;

public class graveKeeper extends boss {
	
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
	private static String DEFAULT_UNIT_NAME = "Gravekeeper Brady";
	
	// Default movespeed.
	private static float DEFAULT_UNIT_MOVESPEED = 3f;
	
	// Default jump speed
	private static int DEFAULT_UNIT_JUMPSPEED = 10;
	
	// The actual type.
	private static unitType unitType  =
			new humanType(DEFAULT_UNIT_NAME,  // Name of unitType 
						"images/units/bosses/graveKeeper/human.png", 
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
	
	// Are we in boss fight mode?
	private boolean bossFight = false;
	
	// What part of the sequence are we at?
	private int sequencePart = 0;
	
	// Current player one.
	public static graveKeeper graveKeeper;
	
	// Farlsworth dialogue box
	private static BufferedImage DEFAULT_DIALOGUE_BOX = spriteSheet.getSpriteFromFilePath("images/units/dialogueBoxes/gravekeeperBox.png");
	
	///////////////
	/// METHODS ///
	///////////////
	
	// Create interact sequence
	public interactBox makeNormalInteractSequence() {
	
		// Placeholder for each individual textSeries.
		textSeries s = null;
		
		// Start.
		textSeries startOfConversation = null;
	
		/*if(shadowBossFightSceneInProgress) {
			
			// Start of conversation.
			startOfConversation = new textSeries(null, "Can you feel the shadows slinking around you?");
			s = startOfConversation.addChild(null, "They are getting closer and closer.");
			s = s.addChild(null, "Sadly, they are not his nor mine.");
			s = s.addChild(null, "But they are truly interesting.");
			s = s.addChild(null, "Hearken their call.");
			s = s.addChild(null, "Let them embrace you.");
			s = s.addChild(null, "But do not let them consume you.");
			s = s.addChild(null, "View them as an opportunity.");
			s = s.addChild(null, "An opportunity to test your power of will.");
			s = s.addChild(null, "He wants to know if you are strong ...");
			s = s.addChild(null, "What interests me is far more important than that.");
			s = s.addChild(null, "I want to know if you understand.");
			s.setEnd();
		}*/
			
			
		return new interactBox(startOfConversation, this);
	}
	
	// Booleans
	private long waitStart = 0;
	private float waitFor = 0;
	
	// Do interact stuff.
	public void doInteractStuff() {
		
		// Load player.
		player currPlayer = player.getPlayer();
		
		// Scenes.
		/*if(shadowBossFightSceneInProgress) {
			
			// Hold the player for a bit.
			if(sequencePart == 0) {
				waitStart = time.getTime();
				waitFor = 5f;
				sequencePart++;
			}
			
			// Initiate the dialogue
			if(sequencePart == 1  && time.getTime() - waitStart > waitFor*1000) {
				graveKeeper.interactSequence = graveKeeper.makeNormalInteractSequence();
				if(interactBox.getCurrentDisplay() != null) {
					interactBox.getCurrentDisplay().toggleDisplay();
				}
				graveKeeper.interactSequence.toggleDisplay();
				graveKeeper.interactSequence.setUnescapable(true);
				player.getPlayer().stopMove("all");
				sequencePart++;
			}
			
			// Fade slowly.
			if(sequencePart == 2 && interactSequence!=null && interactSequence.getTextSeries()!=null && interactSequence.getTextSeries().getTextOnPress()!=null &&
					interactSequence.getTextSeries().getTextOnPress().contains("Can you feel")) {
				farmTomb.zoneFog.fadeTo(.4f, 1);
				sequencePart++;
			}
			if(sequencePart == 3 && interactSequence!=null && interactSequence.getTextSeries()!=null && interactSequence.getTextSeries().getTextOnPress()!=null &&
					interactSequence.getTextSeries().getTextOnPress().contains("closer and closer")) {
				farmTomb.zoneFog.fadeTo(.5f, 1);
				sequencePart++;
			}
			if(sequencePart == 4 && interactSequence!=null && interactSequence.getTextSeries()!=null && interactSequence.getTextSeries().getTextOnPress()!=null &&
					interactSequence.getTextSeries().getTextOnPress().contains("his nor mine")) {
				farmTomb.zoneFog.fadeTo(.6f, 1);
				sequencePart++;
			}
			if(sequencePart == 5 && interactSequence!=null && interactSequence.getTextSeries()!=null && interactSequence.getTextSeries().getTextOnPress()!=null &&
					interactSequence.getTextSeries().getTextOnPress().contains("Hearken")) {
				farmTomb.zoneFog.fadeTo(.7f, 1);
				sequencePart++;
			}
			if(sequencePart == 6 && interactSequence!=null && interactSequence.getTextSeries()!=null && interactSequence.getTextSeries().getTextOnPress()!=null &&
					interactSequence.getTextSeries().getTextOnPress().contains("embrace")) {
				farmTomb.zoneFog.fadeTo(.8f, 1);
				sequencePart++;
			}
			if(sequencePart == 7 && interactSequence!=null && interactSequence.getTextSeries()!=null && interactSequence.getTextSeries().getTextOnPress()!=null &&
					interactSequence.getTextSeries().getTextOnPress().contains("consume")) {
				farmTomb.zoneFog.fadeTo(.9f, 1);
				sequencePart++;
			}
			if(sequencePart == 8 && interactSequence!=null && interactSequence.getTextSeries()!=null && interactSequence.getTextSeries().getTextOnPress()!=null &&
					interactSequence.getTextSeries().getTextOnPress().contains("opportun")) {
				farmTomb.zoneFog.fadeTo(1f, 1);
				sequencePart++;
			}
			
			// We are done our talk.
			if(sequencePart == 9 && interactSequence!=null && interactSequence.getTextSeries()!=null && interactSequence.getTextSeries().getTextOnPress()!=null &&
					interactSequence.getTextSeries().getTextOnPress().contains("understand")) {
				
				// Move player.
				currPlayer.setDoubleX(13226);
				currPlayer.setDoubleY(418);
				
				// Save.
				saveState.setQuiet(true);
				saveState.createSaveState();
				saveState.setQuiet(false);
				
				// Wait a bit.
				waitFor = 1f;
				waitStart = time.getTime();
				sequencePart++;
			}
			
			if(sequencePart == 10 && time.getTime() - waitStart > waitFor*1000) {
				// Chime
				sound s = new sound(wolfless.howl);
				s.start();
				
				// Wait a bit.
				waitFor = 4f;
				waitStart = time.getTime();
				sequencePart++;
			}
			
			// Reveal elevator.
			if(sequencePart == 11 && time.getTime() - waitStart > waitFor*1000) {
				
				// Create elevator and set fog.
				if(interactSequence != null) {
					interactSequence.setUnescapable(false);
					interactSequence.toggleDisplay();
				}
				farmTomb.createShadowBossFightAroundPlayer();
				farmTomb.zoneFog.fadeTo(.3f, .2f);
				
				// Wait for next chime.
				waitFor = 1f;
				waitStart = time.getTime();
				sequencePart++;
			}
			
			// Start fight
			if(sequencePart == 12 && time.getTime() - waitStart > waitFor*1000) {
				
				// Show eyes of elevator.
				farmTomb.startBossFight();
				sequencePart++;
			}
		}
		*/
	}
	
	// Set sequence to
	public static void setSequenceTo(int n) {
		graveKeeper.sequencePart = n;
	}
	
	// Interact with object. 
	public void interactWith() { 
		if(interactSequence == null || (interactSequence != null && !interactSequence.isUnescapable())) {
			this.facingDirection = stringUtils.oppositeDir(player.getPlayer().getFacingDirection());
			interactSequence = makeNormalInteractSequence();
			interactSequence.toggleDisplay();
		}
	}

	///////////////
	/// METHODS ///
	///////////////
	// Constructor
	public graveKeeper(int newX, int newY) {
		super(unitType, "Farlsworth", newX, newY);
		
		setDialogueBox(DEFAULT_DIALOGUE_BOX);

		// He has no collision
		collisionOn = true;
		
		// Set interactable.
		setInteractable(true);
		
		// Load events
		loadEvents();
		
		// Set current player one.
		graveKeeper = this;
		
		// Set dimensions
		setHeight(getDefaultHeight());
		setWidth(getDefaultWidth());
		platformerHeight = DEFAULT_PLATFORMER_HEIGHT;
		platformerWidth = DEFAULT_PLATFORMER_WIDTH;
		topDownHeight = DEFAULT_TOPDOWN_HEIGHT;
		topDownWidth = DEFAULT_TOPDOWN_WIDTH;
		setHitBoxAdjustmentY(getDefaultHitBoxAdjustmentY());
	}
	
	// Load events
	public void loadEvents() {
	}
	
	// Initiate shadow elevator scene.
	public static void initiateShadowBossFightScene() {
	}
	
	// React to pain.
	public void reactToPain() {
	}
	
	// Farlsworth AI
	public void updateUnit() {
		//printFarlsworthEvents();
		
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
