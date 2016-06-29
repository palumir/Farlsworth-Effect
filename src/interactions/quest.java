package interactions;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import drawing.drawnObject;
import drawing.gameCanvas;
import drawing.userInterface.interfaceObject;
import drawing.userInterface.text;
import units.player;
import units.unit;
import utilities.saveState;

public class quest extends text {
	
	/////////////////////
	////// GLOBALS //////
	/////////////////////
	
	// Dimensions and color.
	public static Color DEFAULT_QUEST_COLOR = Color.white;
	public static int DEFAULT_X = 15;
	public static int DEFAULT_Y = 102;

	/////////////////////
	////// FIELDS ///////
	/////////////////////
	
	// Read the current quests
	private static ArrayList<String> currentQuests = new ArrayList<String>();

	// Dialogue with quest giver.
	private interactBox dialogue;
	
	// Who gave it.
	private unit questGiver;
	
	// Quest completed?
	private event completed;
	private event started;
	
	/////////////////////
	////// METHODS //////
	/////////////////////
	public quest(String newText, unit giver, interactBox dialogueFromQuestGiver) {
		super(newText, DEFAULT_X, DEFAULT_Y, DEFAULT_QUEST_COLOR);
		
		// Load events.
		completed = new event("Quest:" + newText + "completed");
		started = new event("Quest:" + newText + "started");
		
		// Set fields.
		questGiver = giver;
		setDialogue(dialogueFromQuestGiver);
		
		// Don't draw object.
		setDrawObject(false);
	}
	
	@Override
	public void drawObject(Graphics g) {
		// Set font.
		g.setFont(drawnObject.DEFAULT_FONT);
		
		if(isStarted() && !isCompleted()) {
			// Color and string.
			g.setColor(theColor);
			g.drawString("Quest: " + getTheText(),
					   (int)(gameCanvas.getScaleX()*getX()),
					   (int)(gameCanvas.getScaleY()*getY()));
		}
	}
	
	// Start quest.
	public void startQuest() {
		
		// Start quest.
		setStarted(true);
		questGiver.noQuest();
		
		// Set current quests.
		if(!getCurrentQuests().contains(this.getTheText())) getCurrentQuests().add(this.getTheText());
		
		// Save game.
		saveState.setQuiet(true);
		saveState.createSaveState();
		saveState.setQuiet(false);
		
	}
	
	// Completed quest
	public void completeQuest() {
		
		// End quest.
		setCompleted(true);
		
		// Set current quests.
		if(getCurrentQuests().contains(this.getTheText())) getCurrentQuests().remove(this.getTheText());
		
		// Save game.
		saveState.createSaveState();
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
		return started.isCompleted();
	}

	public void setStarted(boolean s) {
		started.setCompleted(s);
	}

	public boolean isCompleted() {
		return completed.isCompleted();
	}

	public void setCompleted(boolean c) {
		completed.setCompleted(c);
	}

	public interactBox getDialogue() {
		return dialogue;
	}

	public void setDialogue(interactBox dialogue) {
		this.dialogue = dialogue;
	}

	public static ArrayList<String> getCurrentQuests() {
		return currentQuests;
	}

	public static void setCurrentQuests(ArrayList<String> currentQuests) {
		quest.currentQuests = currentQuests;
	}
}