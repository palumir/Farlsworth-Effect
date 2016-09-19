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

public class farmFenceCinematic extends cinematic {
	
	// Event
	public static event isCompleted = new event(MethodHandles.lookup().lookupClass().getName() + "isCompleted");

	public farmFenceCinematic() {
		super("farmFenceCinematic");
		save = false;
	}
	
	farlsworth farlsworth;

	@Override
	public void play() {
		
	    // Lock the player's movement.
	    player.getPlayer().stopMove("all");
	    
	    // Create interactSequence (first thing he says to you)
	    textSeries s = new textSeries(null, "Uh, this is a kind of awkward ...");
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
			addTextSeries(null, "But there appears to be a gate here.", farlsworth);
			farlsworth.setMoveSpeed(2);
			farlsworth.moveTo(farlsworth.getIntX(), farlsworth.getIntY() + 15);
			advanceSequence();
		}
		
		if(isSequence(numIfs++) && goNextTextSeries()) {
			
			// Set the next text and advance it.
			addTextSeries(null, "And being a sheep, I am totally incapable of opening it.", farlsworth);
			farlsworth.faceTowardPlayer();
			advanceSequence();
		}
		
		if(isSequence(numIfs++) && goNextTextSeries()) {
			
			// Set the next text and advance it.
			addTextSeries(null, "Can you open it for me, buddy?", farlsworth);
			farlsworth.faceTowardPlayer();
			advanceSequence();
		}
		
		if(isSequence(numIfs++) && goNextTextSeries()) {
			
			// Move right.
			addTextSeries("'Yes'", null,farlsworth, "Order");
			addTextSeries("'No'", null,farlsworth,"Chaos");
			interactSequence.goToNext();
			
			// Set the next text and advance it.
			advanceSequence();
		}
		
		if(goNextTextSeries() && choiceIs("'Yes'")) {
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				sheepFarm.forestGate.open();
				addTextSeries(null, "Uh... thanks.",farlsworth);
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				addTextSeries(null, "Catch me if you can, I guess.",farlsworth);
				runFarlsworthAway();
				sequencePart = 100;
			}
			
		}
		
		if(goNextTextSeries() && choiceIs("'No'")) {
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				addTextSeries(null, "Damn.",farlsworth);
				advanceSequence();
			}

			if(isSequence(numIfs++) && goNextTextSeries()) {
				addTextSeries(null, "I guess I'll have to stay inside the fence then.",farlsworth);
				waitFor(1f);
				advanceSequence();
			}
			
			if(isSequence(numIfs++)) {
				farlsworth.setFacingDirection("Up");
				waitFor(1f);
				advanceSequence();
			}
			
			if(isSequence(numIfs++)) {
				farlsworth.setFacingDirection("Down");
				waitFor(1f);
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				addTextSeries(null, "Catch me if you can.",farlsworth);
				farlsworth.attachFence();
				runFarlsworthAway();
				sequencePart = 100;
			}
			
		}
		
		if(sequencePart == 100 && farlsworth.getAllCommands()!=null && (farlsworth.getAllCommands().size() == 0)) {
			farlsworth.destroyFence();
			farlsworth.setDoubleX(-1550);
			farlsworth.setDoubleY(-5258+30);
			stop();
		}
		
	}
	
	// Run Farlsworth away
	public void runFarlsworthAway() {
		farlsworth.setMoveSpeed(4.5f);
		commandList commands = new commandList();
		commands.add(new moveCommand(-7,-2219));
		commands.add(new moveCommand(-7,-2824));
		commands.add(new moveCommand(-7,-2824));
		farlsworth.doCommandsOnce(commands);
		sound s = new sound(farlsworth.bleet);
		s.setPosition(farlsworth.getIntX(), farlsworth.getIntY(), sound.DEFAULT_SOUND_RADIUS);
		s.start();
		interactSequence.getTextSeries().setEnd();
		interactSequence.setLocked(false);
		interactSequence.setUnescapable(false);
		
		// Set it to be completed as soon as he runs, instead of when he's teleported to flower farm.
		cinematicCompleted.setCompleted(true);
		
		// Save by default.
		saveState.setQuiet(true);
		saveState.createSaveState();
		saveState.setQuiet(false);
	}

	@Override
	public event isCompleted() {
		return isCompleted;
	}
	
}