package doodads.sheepFarm;

import doodads.openable;
import interactions.event;
import interactions.interactBox;
import interactions.textSeries;
import modes.mode;
import sounds.sound;
import terrain.chunk;
import terrain.generalChunkType;
import units.player;
import units.characters.farlsworth.farlsworth;
import zones.sheepFarm.subZones.sheepFarm;

public class gate extends openable {

	////////////////
	/// DEFAULTS ///
	////////////////

	// Default name.
	private static String DEFAULT_CHUNK_NAME = "gate";

	// Sprite stuff.
	private static String DEFAULT_CHUNK_SPRITESHEET = "images/doodads/farmLand/sheepFarm/" + DEFAULT_CHUNK_NAME
			+ ".png";

	// Dimensions
	public static int DEFAULT_CHUNK_WIDTH = 46;
	public static int DEFAULT_CHUNK_HEIGHT = 34;

	// Key name.
	private String keyName;

	// Talked to the gate? For forest joke.
	private boolean talkedToForestGateOnce = false;
	private int interactTimes = 0;
	private boolean screamingJoke = false;

	// The actual type.
	private static generalChunkType typeReference = new generalChunkType(DEFAULT_CHUNK_NAME, DEFAULT_CHUNK_SPRITESHEET,
			DEFAULT_CHUNK_WIDTH, DEFAULT_CHUNK_HEIGHT);

	///////////////
	/// METHODS ///
	///////////////
	// Constructor
	public gate(String newKeyName, int newX, int newY) {
		super(typeReference, newKeyName, newX, newY);

		// Key name.
		keyName = newKeyName;

		// Check if we have save data on the gate.
		if (isOpen.isCompleted()) {
			forceOpen();
		} else {
			setPassable(false);
		}

		if (mode.getCurrentMode().equals("topDown")) {
			setHitBoxAdjustmentY(14);
			setHeight(6);
		} else {
			setHitBoxAdjustmentY(0);
			setHeight(DEFAULT_CHUNK_HEIGHT);
			setWidth(DEFAULT_CHUNK_WIDTH);
		}
		setInteractable(true);
	}

	// Create interact sequence
	@Override
	public interactBox makeNormalInteractSequence() {

		if (!hasBeenOpened.isCompleted()) {
			// Placeholder for each individual textSeries.
			textSeries s;

			// Start of conversation.
			textSeries startOfConversation = null;

			if (!isOpen()) {
				startOfConversation = new textSeries("StartWithButtons", "StartWithButtons");
				startOfConversation.setTalker("");
				if (hasBeenOpened.isCompleted()) {

				}
				if ((player.getPlayer() != null && player.getPlayer().getPlayerInventory().hasKey(keyName))
						|| hasBeenOpened.isCompleted()) {

					// Screaming joke
					if (screamingJoke) {
						s = startOfConversation.addChild(null, "HOLY SHNIKIES!! SOMEBODY HELP!!!");
						s.setTalker("Talking Gate");
						s.setEnd();
					}
					// Farlsworth gate joke
					else if (isForestGateAndUnopenable() && !talkedToForestGateOnce) {

						if (interactTimes == 0) {
							sheepFarm.talkingGateJokeExperienced.setCompleted(true);
							s = startOfConversation.addChild("Open", "No.");
							s.setTalker("Talking Gate");
							s = s.addChild(null, "That's a pretty rude way to ask, don't you think?");
							s.setTalker("Talking Gate");
							s.setEnd();
							interactTimes++;
						} else if (interactTimes == 1) {
							s = startOfConversation.addChild("Please open?", "Still no.");
							s.setTalker("Talking Gate");
							s = s.addChild(null, "That was way more polite though, thanks.");
							s.setTalker("Talking Gate");
							s.setEnd();
							interactTimes++;
						} else if (interactTimes == 2) {
							s = startOfConversation.addChild("Are you just joshing around?",
									"If I was just joshing hard I would tell you.");
							s.setTalker("Talking Gate");
							s = s.addChild(null, "People usually only josh big time if they let you know.");
							s.setTalker("Talking Gate");
							s = s.addChild(null, "Or it's just a trash josh.");
							s.setTalker("Talking Gate");
							s = s.addChild(null, "Farmer just asked you to do a quest, right?");
							s.setTalker("Talking Gate");
							s = s.addChild(null, "To get Farnsgurn's wool or something like that?");
							s.setTalker("Talking Gate");
							s = s.addChild(null, "Go do it man don't commit and dip.");
							s.setTalker("Talking Gate");
							s = s.addChild(null, "Nobody likes a commit and dip.");
							s.setTalker("Talking Gate");
							s = s.addChild(null, "Or a dine and dash.");
							s.setTalker("Talking Gate");
							s = s.addChild(null, "Or a fart and dart.");
							s.setTalker("Talking Gate");
							s = s.addChild(null, "You're just going to josh yourself.");
							s.setTalker("Talking Gate");
							s.setEnd();
							interactTimes++;
						} else {
							s = startOfConversation.addChild("Open", "Farlsbones is in the pen to your right.");
							s.setTalker("Talking Gate");
							s = s.addChild(null, "Go get his wool man.");
							s.setTalker("Talking Gate");
							s = s.addChild(null, "Quit joshing me hard.");
							s.setTalker("Talking Gate");
							s.setEnd();
							talkedToForestGateOnce = true;
							interactTimes++;
						}
					} else if (isForestGateAndUnopenable() && talkedToForestGateOnce) {
						s = startOfConversation.addChild("Open", "Go get that wool.");
						s.setTalker("Talking Gate");
						s = s.addChild(null, "Frannyburns is in the pen to your right.");
						s.setTalker("Talking Gate");
						s = s.addChild(null, "I have nothing more to say.");
						s.setTalker("Talking Gate");
						s = s.addChild(null, "The gate life is a dull one.");
						s.setTalker("Talking Gate");
					} else {
						s = startOfConversation.addChild("Open", "You open the gate.");
					}

				} else {
					s = startOfConversation.addChild("Open", "You don't have the key.");
				}

				s.setEnd();
			}

			else {
				startOfConversation = new textSeries("StartWithButtons", "StartWithButtons");
				s = startOfConversation.addChild("Close", "You close the gate.");
				s.setEnd();
			}

			if (isForestGateAndUnopenable()) {
			}

			return new interactBox(startOfConversation, this);
		} else {

			// Placeholder for each individual textSeries.
			textSeries s;

			// Start of conversation.
			textSeries startOfConversation = null;

			if (!isOpen()) {
				startOfConversation = new textSeries("StartWithButtons", "StartWithButtons");
				startOfConversation.setTalker("");

				if ((player.getPlayer() != null && player.getPlayer().getPlayerInventory().hasKey(getKeyName()))
						|| hasBeenOpened.isCompleted() || keyName == null) {
					s = startOfConversation.addChild("Open", "You open it.");
				} else {
					s = startOfConversation.addChild("Open", "You don't have the key.");
				}

				s.setEnd();
			}

			else {
				startOfConversation = new textSeries("StartWithButtons", "StartWithButtons");
				s = startOfConversation.addChild("Close", "You close it.");
				s.setEnd();
			}

			return new interactBox(startOfConversation, this);
		}
	}

	// Is it the forest gate
	public boolean isForestGateAndUnopenable() {
		return sheepFarm.forestGate != null && this.equals(sheepFarm.forestGate) &&
				 player.getPlayer() != null
				&& player.getPlayer().getPlayerInventory().hasKey(keyName);
	}

	@Override
	public generalChunkType getTypeReference() {
		// TODO Auto-generated method stub
		return typeReference;
	}
}
