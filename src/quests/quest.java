package quests;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import drawing.drawnObject;
import drawing.gameCanvas;
import units.unit;
import userInterface.interactBox;
import userInterface.interfaceObject;
import userInterface.text;

public class quest extends text {
	
	/////////////////////
	////// GLOBALS //////
	/////////////////////
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
		super("Quest:" + newText, DEFAULT_X, DEFAULT_Y, DEFAULT_QUEST_COLOR);
		questGiver = giver;
		dialogue = dialogueFromQuestGiver;
	}
	
	@Override
	public void drawObject(Graphics g) {
		// Set font.
		g.setFont(drawnObject.DEFAULT_FONT);
		
		if(started && !completed) {
			// Color and string.
			g.setColor(theColor);
			g.drawString(getTheText(),
					   getX(),
					   getY());
		}
	}
	
	// Start quest.
	public void startQuest() {
		started = true;
		questGiver.noQuest();
	}
	
	// Completed quest
	public void completeQuest() {
		completed = true;
		setDrawObject(false);
	}
	
	// Completed?
	public boolean completed() {
		return completed;
	}
	
	// Get interact box.
	public interactBox getInteractBox() {
		return dialogue;
	}
	
	// Set interact.
	public void setInteractBox(interactBox i) {
		dialogue = i;
	}
}