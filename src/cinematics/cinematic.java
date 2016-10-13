package cinematics;

import java.util.ArrayList;

import drawing.drawnObject;
import interactions.event;
import interactions.interactBox;
import interactions.textSeries;
import utilities.intTuple;
import utilities.saveState;
import utilities.time;

public abstract class cinematic {
	
	////////////////
	//// GLOBALS ///
	////////////////
	
	// Only one cinematic can be in progress at once.
	public static cinematic currCinematic;
	
	////////////////
	//// FIELDS ////
	////////////////
	
	// Does it complete once?
	private boolean completeOnce = true;
	
	// Name.
	private String name;
	
	// Interaction
	protected interactBox interactSequence;
	
	// Path to follow on interaction.
	private ArrayList<intTuple> p;
	
	// What part of the sequence are we at?
	protected int sequencePart = 0;
	
	// Choice array
	protected ArrayList<String> choiceArray;
	
	// In progress?
	private boolean inProgress = false;
	
	// Save?
	protected boolean save = true;
	
	// Waiting.
	private long waitStart = 0;
	private float waitFor = 0;
	
	/////////////////////
	////// METHODS //////
	/////////////////////
	public cinematic(String s) {
		setName(s);
	}
	
	// Start
	public void start() {
		
		// If the cinematic isn't completed.
		if(!isCompleteOnce() || !isCompleted().isCompleted()) {
			
			// Report error if another cinematic is in progress.
			if(currCinematic != null) System.err.println("Error: Another cinematic is currently in progress. Playing anyway.");
			
			// Set current cinamatic.
			currCinematic = this;
			setInProgress(true);
			
			// Play it.
			play();
		}
	}
	
	// Play
	public abstract void play();
	
	// Stop
	public void stop() {
	
		
		// Set in progress and current cinematic.
		setInProgress(false);
		currCinematic = null;
		
		// Save that the cinematic is completed.
		if(isCompleteOnce()) {
			isCompleted().setCompleted(true);
		
			// Save by default.
			if(save) {
				saveState.setQuiet(true);
				saveState.createSaveState();
				saveState.setQuiet(false);
			}
		}
		
		// Finish
		finish();
	}
	
	// Finish
	public abstract void finish();
	
	// Update
	public void update() {
		
		updateCinematic();
	}
	
	// Wait for
	public void waitFor(float seconds) {
		waitFor = seconds;
		waitStart = time.getTime();
	}
	
	// Done waiting?
	public boolean doneWaiting() {
		return time.getTime() - waitStart > waitFor*1000;
	}
	
	// Set next text
	public void addTextSeries(String s, String y, drawnObject u) {
		interactSequence.setGoNext(false);
		textSeries series = new textSeries(s, y);
		interactSequence.getTextSeries().addChild(series);
		series.setWhoIsTalking(u);
		if(s==null) interactSequence.goToNext();
	}
	
	// Set next text
	public void addTextSeries(String s, String y, drawnObject u, String choiceType) {
		interactSequence.setGoNext(false);
		textSeries series = new textSeries(s, y);
		series.setChoiceType(choiceType);
		interactSequence.getTextSeries().addChild(series);
		series.setWhoIsTalking(u);
		if(s==null) interactSequence.goToNext();
	}
	
	// Go next text series?
	public boolean goNextTextSeries() {
		return interactSequence.isGoNext();
	}
	
	// Set talker
	public void setTalker(drawnObject u) {
		interactSequence.getTextSeries().setWhoIsTalking(u);
	}
	
	// Choice is
	public boolean choiceIs(String s) {
		if(choiceArray == null) choiceArray = new ArrayList<String>();
		boolean choiceIs = choiceArray.contains(s) || !interactSequence.isButtonMode() && s.equals(interactSequence.getTextSeries().getButtonText());
		if(choiceIs && !choiceArray.contains(s)) choiceArray.add(s);
		return choiceIs;
	}
	
	// Choice is
	public boolean choiceIs(String s, int numTimes) {
		if(choiceArray == null) choiceArray = new ArrayList<String>();
		boolean choiceIs = choiceArray.contains(s) || !interactSequence.isButtonMode() && s.equals(interactSequence.getTextSeries().getButtonText());
		if(choiceIs && !choiceArray.contains(s)) choiceArray.add(s);
		int count = 0;
		for(int i = 0; i < choiceArray.size(); i++) {
			if(choiceArray.get(i).equals(s)) count++;
		}
		return count >= numTimes;
	}
	
	
	// Advance sequence
	public void advanceSequence() {
		sequencePart++;
	}
	
	// Is sequence
	public boolean isSequence(int i) {
		return i == sequencePart && doneWaiting();
	}
	
	// Update cinematic
	public abstract void updateCinematic();
	
	public abstract event isCompleted();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isInProgress() {
		return inProgress;
	}

	public void setInProgress(boolean inProgress) {
		this.inProgress = inProgress;
	}
	
	// Update current cinematic only.
	public static void updateCurrentCinematic() {
		if(currCinematic != null) currCinematic.update();
	}

	public boolean isCompleteOnce() {
		return completeOnce;
	}

	public void setCompleteOnce(boolean completeOnce) {
		this.completeOnce = completeOnce;
	}
}