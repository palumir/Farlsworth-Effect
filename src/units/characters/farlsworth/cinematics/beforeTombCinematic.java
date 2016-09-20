package units.characters.farlsworth.cinematics;

import java.awt.Event;
import java.lang.invoke.MethodHandles;

import cinematics.cinematic;
import interactions.event;
import interactions.interactBox;
import interactions.textSeries;
import sounds.music;
import sounds.sound;
import units.player;
import units.bosses.fernando.fernando;
import units.bosses.rodriguez.rodriguez;
import units.characters.farlsworth.farlsworth;
import units.unitCommands.commandList;
import units.unitCommands.commands.moveCommand;
import utilities.saveState;
import zones.sheepFarm.sheepFarm;

public class beforeTombCinematic extends cinematic {
	
	// Event
	public static event isCompleted = new event(MethodHandles.lookup().lookupClass().getName() + "isCompleted");

	public beforeTombCinematic() {
		super("beforeTombCinematic");
		save = false;
	}
	
	farlsworth farlsworth;
	
	@Override
	public void play() {
		
	    // Lock the player's movement.
	    player.getPlayer().stopMove("all");
	    
	    // Create interactSequence (first thing he says to you)
	    textSeries s = new textSeries(null, "Slippery, huh?");
	    farlsworth = units.characters.farlsworth.farlsworth.farlsworth;
	    
	    interactSequence = new interactBox(s, farlsworth);
		interactSequence.setUnescapable(true);
		interactSequence.toggleDisplay();
	}

	@Override
	public void finish() {
	}

	@Override
	public void updateCinematic() {
		
		textSeries s;
		int numIfs = 0;
		
		if(isSequence(numIfs++) && goNextTextSeries()) {
			
			// Set the next text and advance it.
			addTextSeries(null, "Apparently wolf paws have anti-slip grip.", farlsworth);
			farlsworth.setMoveSpeed(2);
			advanceSequence();
		}
		
		if(isSequence(numIfs++) && goNextTextSeries()) {
			
			// Set the next text and advance it.
			addTextSeries(null, "I hope these graves aren't all wolf victims.", farlsworth);
			advanceSequence();
		}
		
		if(isSequence(numIfs++) && goNextTextSeries()) {
			
			// Set the next text and advance it.
			addTextSeries(null, "That would be down-right tragic.", farlsworth);
			advanceSequence();
		}
		
		if(isSequence(numIfs++) && goNextTextSeries()) {
			
			// Set the next text and advance it.
			addTextSeries(null, "You know, the guy in charge of these graves...", farlsworth);
			advanceSequence();
		}
		
		if(isSequence(numIfs++) && goNextTextSeries()) {
			
			// Set the next text and advance it.
			addTextSeries(null, "I don't see him around anymore.", farlsworth);
			advanceSequence();
		}
		
		if(isSequence(numIfs++) && goNextTextSeries()) {
			
			// Set the next text and advance it.
			addTextSeries(null, "But it was starting to look like he was getting a bit fed-up.", farlsworth);
			advanceSequence();
		}
		
		if(isSequence(numIfs++) && goNextTextSeries()) {
			
			// Set the next text and advance it.
			addTextSeries(null, "And maybe even lose his mind a litte. He did dig a lot.", farlsworth);
			advanceSequence();
		}
		
		if(isSequence(numIfs++) && goNextTextSeries()) {
			
			// Set the next text and advance it.
			addTextSeries(null, "He eventually started filling this tomb instead.", farlsworth);
			advanceSequence();
		}
		
		if(isSequence(numIfs++) && goNextTextSeries()) {
			
			// Set the next text and advance it.
			addTextSeries(null, "It's pretty spooky in there, so be careful.", farlsworth);
			advanceSequence();
		}
		
		if(isSequence(numIfs++) && goNextTextSeries()) {
			
			// Set the next text and advance it.
			addTextSeries(null, "If you find yourself afraid of your own shadow...", farlsworth);
			advanceSequence();
		}
		
		if(isSequence(numIfs++) && goNextTextSeries()) {
			
			// Set the next text and advance it.
			addTextSeries(null, "Just shed some light on it.", farlsworth);
			advanceSequence();
		}
		
		if(isSequence(numIfs++) && goNextTextSeries()) {
			
			// Set the next text and advance it.
			addTextSeries(null, "You'll find that there's nothing to be afraid of.", farlsworth);
			advanceSequence();
		}
		
		if(isSequence(numIfs++) && goNextTextSeries()) {
			
			// Set the next text and advance it.
			addTextSeries(null, "See you later.", farlsworth);
			advanceSequence();
		}
	}
	
	// Run Farlsworth away
	public void runFarlsworthAway() {
	}

	@Override
	public event isCompleted() {
		return isCompleted;
	}
	
}
