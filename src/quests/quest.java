package quests;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import drawing.drawnObject;
import drawing.gameCanvas;
import units.player;
import units.unit;
import userInterface.interactBox;
import userInterface.interfaceObject;
import userInterface.text;
import utilities.saveState;

public class quest extends text {
	
	/////////////////////
	////// GLOBALS //////
	/////////////////////
	
	// List of all quests.
	public static ArrayList<quest> loadedQuests = new ArrayList<quest>();
	
	// Dimensions and color.
	public static Color DEFAULT_QUEST_COLOR = Color.white;
	public static int DEFAULT_X = gameCanvas.getDefaultWidth()/2;
	public static int DEFAULT_Y = gameCanvas.getDefaultHeight()/2;

	/////////////////////
	////// FIELDS ///////
	/////////////////////

	// Dialogue with quest giver.
	private interactBox dialogue;
	
	// Who gave it.
	private unit questGiver;
	
	// Quest completed?
	private boolean completed = false;
	private boolean started = false;
	
	/////////////////////
	////// METHODS //////
	/////////////////////
	public quest(String newText, unit giver, interactBox dialogueFromQuestGiver) {
		super(newText, DEFAULT_X, DEFAULT_Y, DEFAULT_QUEST_COLOR);
		
		// Set fields.
		questGiver = giver;
		setDialogue(dialogueFromQuestGiver);
		
		// If the quest is loaded, set it's data.
		int i = 0;
		if(loadedQuests != null) {
			
			// Go through the list and return the quest with the same name.
			while(i < loadedQuests.size()) {
				if(loadedQuests.get(i).getTheText().equals(newText)) {
					completed = loadedQuests.get(i).isCompleted();
					started = loadedQuests.get(i).isStarted();
					loadedQuests.remove(i);
				}
				else {
					i++;
				}
			}
		}
		
		// If the quest is started, move to the end of the dialogue.
		
		
		// Add the loaded quest the list.
		loadedQuests.add(this);
	}
	
	@Override
	public void drawObject(Graphics g) {
		// Set font.
		g.setFont(drawnObject.DEFAULT_FONT);
		
		if(isStarted() && !isCompleted()) {
			// Color and string.
			g.setColor(theColor);
			g.drawString(getTheText(),
					   getX(),
					   getY());
		}
	}
	
	// Load quest data.
	public static void loadQuestData() {
		
		// Load the savestate
		saveState s = player.getCurrentPlayer().playerSaveState;
		
		// Populate allQuests with quests from the saveState.
		if(s != null) {
			loadedQuests = s.getAllQuests();
		}
	}
	
	// Start quest.
	public void startQuest() {
		setStarted(true);
		questGiver.noQuest();
	}
	
	// Completed quest
	public void completeQuest() {
		setCompleted(true);
		setDrawObject(false);
	}
	
	// Completed?
	public boolean completed() {
		return isCompleted();
	}
	
	// Get interact box.
	public interactBox getInteractBox() {
		return getDialogue();
	}
	
	// Set interact.
	public void setInteractBox(interactBox i) {
		setDialogue(i);
	}

	public boolean isStarted() {
		return started;
	}

	public void setStarted(boolean started) {
		this.started = started;
	}

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

	public interactBox getDialogue() {
		return dialogue;
	}

	public void setDialogue(interactBox dialogue) {
		this.dialogue = dialogue;
	}
}