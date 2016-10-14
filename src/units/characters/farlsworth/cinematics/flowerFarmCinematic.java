package units.characters.farlsworth.cinematics;

import java.lang.invoke.MethodHandles;

import cinematics.cinematic;
import interactions.event;
import interactions.interactBox;
import interactions.textSeries;
import sounds.music;
import sounds.sound;
import units.player;
import units.characters.farlsworth.farlsworth;
import units.unitCommands.commandList;
import units.unitCommands.commands.moveCommand;
import utilities.saveState;
import utilities.time;
import zones.sheepFarm.subZones.sheepFarm;

public class flowerFarmCinematic extends cinematic {
	
	// Event
	public static event isCompleted = event.createEvent(MethodHandles.lookup().lookupClass().getName() + "isCompleted");

	public flowerFarmCinematic() {
		super("flowerFarmCinematic");
		save = false;
	}
	
	farlsworth farlsworth;
	units.characters.farmer.farmer farmer;
	
	private static String cheering = "sounds/effects/characters/farlsworth/cheering.wav";
	private static String booing = "sounds/effects/characters/farlsworth/booing.wav";

	@Override
	public void play() {
		
	    // Lock the player's movement.
	    player.getPlayer().stopMove("all");
	    
	    // Create interactSequence (first thing he says to you)
	    textSeries s = new textSeries(null, "How about these wolves, eh?");
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
			
			farlsworth.setMoveSpeed(2);
			
			// Set the next text and advance it.
			addTextSeries(null, "They're a bunch of friggin pricks, aren't they?", farlsworth);
			farlsworth.setFacingDirection("Right");
			advanceSequence();
		}
		
		if(isSequence(numIfs++) && goNextTextSeries()) {
			
			// Set the next text and advance it.
			addTextSeries(null, "I guess that's their way of coping with everything.", farlsworth);
			farlsworth.moveTo(farlsworth.getIntX()-25, farlsworth.getIntY());
			advanceSequence();
		}
		
		if(isSequence(numIfs++) && goNextTextSeries()) {
			
			// Set the next text and advance it.
			addTextSeries(null, "It's tough times out here, man.", farlsworth);
			farlsworth.faceTowardPlayer();
			advanceSequence();
		}
		
		if(isSequence(numIfs++) && goNextTextSeries()) {
			
			// Set the next text and advance it.
			addTextSeries(null, "These days things are either disappearing or slowly getting worse.", farlsworth);
			advanceSequence();
		}
		
		if(isSequence(numIfs++) && goNextTextSeries()) {
			
			// Set the next text and advance it.
			addTextSeries(null, "And now it's friggin raining!", farlsworth);
			farlsworth.moveTo(farlsworth.getIntX()+15, farlsworth.getIntY());
			advanceSequence();
		}
		
		if(isSequence(numIfs++) && goNextTextSeries()) {
			
			// Set the next text and advance it.
			addTextSeries(null, "Heh, insult to injury, right?", farlsworth);
			advanceSequence();
		}
		
		if(isSequence(numIfs++) && goNextTextSeries()) {
			
			// Set the next text and advance it.
			addTextSeries(null, "But I do love the sound of rain.", farlsworth);
			advanceSequence();
		}
		
		if(isSequence(numIfs++) && goNextTextSeries()) {
			
			// Set the next text and advance it.
			addTextSeries(null, "Don't you?", farlsworth);
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
				
				// Set the next text and advance it.
				addTextSeries(null, "It's so calming.", farlsworth);
				music.currMusic.fadeOut(2f);
				sheepFarm.musicOff.setCompleted(true);
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				// Set the next text and advance it.
				addTextSeries(null, "You'll enjoy the serenity in the next area.", farlsworth);
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				// Set the next text and advance it.
				addTextSeries(null, "Maybe it'll make you die less.", farlsworth);
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				// Set the next text and advance it.
				addTextSeries(null, "But probably not.", farlsworth);
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				// Set the next text and advance it.
				addTextSeries(null, "Good luck.", farlsworth);
				runFarlsworthAway();
				sequencePart = 100;
			}
		}
		
		if(goNextTextSeries() && choiceIs("'No'")) {
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				// Set the next text and advance it.
				addTextSeries(null, "It's too bad that it's raining then, huh?", farlsworth);
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				// Set the next text and advance it.
				addTextSeries(null, "Hopefully it doesn't make you lose your mojo.", farlsworth);
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				// Set the next text and advance it.
				addTextSeries(null, "Just kidding, your mojo's a load of bumbo.", farlsworth);
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				// Set the next text and advance it.
				addTextSeries(null, "Try not to die so much, buddy.", farlsworth);
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				// Set the next text and advance it.
				addTextSeries(null, "Good luck.", farlsworth);
				runFarlsworthAway();
				sequencePart = 100;
			}
		}
		
		if(sequencePart==100 && farlsworth.getAllCommands()!=null && (farlsworth.getAllCommands().size() == 0)) {
			farlsworth.setDoubleX(-3463);
			farlsworth.setDoubleY(-5550);
			stop();
		}
		
	}
	
	// Run Farlsworth away
	public void runFarlsworthAway() {
		
		save = false;
		
		farlsworth.setMoveSpeed(6f);
		commandList commands = new commandList();
		commands.add(new moveCommand(-1677,-4866));
		commands.add(new moveCommand(-2108,-4866));
		commands.add(new moveCommand(-2108,-6223));
		commands.add(new moveCommand(-2108,-6223));
		farlsworth.doCommandsOnce(commands);
		sound s = new sound(farlsworth.bleet);
		s.setPosition(farlsworth.getIntX(), farlsworth.getIntY(), sound.DEFAULT_SOUND_RADIUS);
		s.start();
		interactSequence.getTextSeries().setEnd();
		interactSequence.setLocked(false);
		interactSequence.setUnescapable(false);
		
		// Set it to be completed as soon as he runs, instead of when he's teleported to flower farm.
		isCompleted.setCompleted(true);
		
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
