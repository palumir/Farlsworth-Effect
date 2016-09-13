package units.characters.farlsworth.cinematics;

import java.awt.Event;
import java.lang.invoke.MethodHandles;

import cinematics.cinematic;
import interactions.event;
import interactions.interactBox;
import interactions.textSeries;
import sounds.music;
import units.player;
import units.bosses.fernando.fernando;
import units.bosses.rodriguez.rodriguez;
import units.characters.farlsworth.farlsworth;

public class farmIntroCinematic extends cinematic {
	
	// Event
	public static event isCompleted = new event(MethodHandles.lookup().lookupClass().getName() + "isCompleted");

	public farmIntroCinematic() {
		super("farmIntroCinematic");
	}

	@Override
	public void play() {
		
	    // Lock the player's movement.
	    player.getPlayer().stopMove("all");
	    
	    // Create interactSequence (first thing he says to you)
	    textSeries s = new textSeries(null, "Hello, my friend.");
	    
	    interactSequence = new interactBox(s, farlsworth.farlsworth);
	    interactSequence.toggleDisplay();
	}

	@Override
	public void finish() {
	}

	@Override
	public void updateCinematic() {
		
		textSeries s;
		int numIfs = 0;
		
		// Begin the sequence.
		if(isSequence(numIfs++) && goNextTextSeries()) {
		
		}
		
		
	}

	@Override
	public event isCompleted() {
		return null;
	}
	
}