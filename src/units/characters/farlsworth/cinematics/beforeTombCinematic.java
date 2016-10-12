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
import zones.sheepFarm.subZones.sheepFarm;

public class beforeTombCinematic extends cinematic {
	
	// Event
	public static event isCompleted = event.createEvent(MethodHandles.lookup().lookupClass().getName() + "isCompleted");

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
	    textSeries s = new textSeries(null, "How are you finding it so far, bud?");
	    if(units.characters.farlsworth.farlsworth.farlsworth!=null) {
		    farlsworth = units.characters.farlsworth.farlsworth.farlsworth;
		    farlsworth.setFacingDirection("Left");
		    
		    interactSequence = new interactBox(s, farlsworth);
			interactSequence.setUnescapable(true);
			interactSequence.toggleDisplay();
	    }
	}

	@Override
	public void finish() {
	}

	@Override
	public void updateCinematic() {
		
		textSeries s;
		int numIfs = 0;
		
		if(isSequence(numIfs++) && goNextTextSeries()) {
			
			// Move right.
			addTextSeries("'Hard'", null,farlsworth, "Order");
			addTextSeries("'Easy'", null,farlsworth,"Chaos");
			interactSequence.goToNext();
			
			// Set the next text and advance it.
			advanceSequence();
		}
		
		if(choiceIs("'Hard'")) {
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				addTextSeries(null, "Good thing you have that bottle then.",farlsworth);
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				addTextSeries(null, "Or you'd be royally boned.",farlsworth);
				advanceSequence();
			}
			
		}
		
		if(choiceIs("'Easy'")) {
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				addTextSeries(null, "Oh yeah?",farlsworth);
				farlsworth.moveTo(player.getPlayer().getIntX()+40, player.getPlayer().getIntY()+3);
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries() && !farlsworth.isMoving()) {
				addTextSeries(null, "That's because you have this thing.",farlsworth);
				farlsworth.putItemInMouth(player.getPlayer().getPlayerInventory().get("Save Bottle"));
				farlsworth.moveTo(player.getPlayer().getIntX()+33, player.getPlayer().getIntY()+3);
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				addTextSeries(null, "You're a friggin cheater!",farlsworth);
				farlsworth.moveTo(-3463, -5550);
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && !farlsworth.isMoving()) {
				farlsworth.placeItemOnGround();
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && !farlsworth.isMoving()) {
				farlsworth.moveTo(farlsworth.getIntX()-25, -5550);
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				addTextSeries(null, "That's why it was easy, you ding-dong.",farlsworth);
				advanceSequence();
			}
			
		}
		
		if(isSequence(numIfs++) && goNextTextSeries()) {
			
			// Set the next text and advance it.
			addTextSeries(null, "Let's hope you don't end up in one of these graves!", farlsworth);
			advanceSequence();
		}
		
		if(isSequence(numIfs++) && goNextTextSeries()) {
			
			// Set the next text and advance it.
			addTextSeries(null, "Hey, maybe these people are all wolf victims.", farlsworth);
			advanceSequence();
		}
		
		if(isSequence(numIfs++) && goNextTextSeries()) {
			
			// Set the next text and advance it.
			addTextSeries(null, "Just kidding, it's actually much worse than that.", farlsworth);
			advanceSequence();
		}
		
		
		if(isSequence(numIfs++) && goNextTextSeries()) {
			
			// Set the next text and advance it.
			addTextSeries(null, "An old buddy of mine used to take care of this place.", farlsworth);
			advanceSequence();
		}
		
		if(isSequence(numIfs++) && goNextTextSeries()) {
			
			// Set the next text and advance it.
			addTextSeries(null, "He was getting real bored of digging graves.", farlsworth);
			advanceSequence();
		}
		
		if(isSequence(numIfs++) && goNextTextSeries()) {
			
			// Set the next text and advance it.
			addTextSeries(null, "And I think it got to his head a bit.", farlsworth);
			advanceSequence();
		}
		
		if(isSequence(numIfs++) && goNextTextSeries()) {
			
			// Set the next text and advance it.
			addTextSeries(null, "He eventually started building a tomb.", farlsworth);
			advanceSequence();
		}
		
		if(isSequence(numIfs++) && goNextTextSeries()) {
			
			// Set the next text and advance it.
			addTextSeries(null, "The tomb of a psycho gravekeeper. Spooky, isn't it?", farlsworth);
			advanceSequence();
		}
		
		if(isSequence(numIfs++) && goNextTextSeries()) {
			
			// Set the next text and advance it.
			addTextSeries(null, "Word of advice, pal...", farlsworth);
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
			addTextSeries(null, "See you on the other side, dildo!", farlsworth);
			
			if(choiceIs("'Easy'")) {
				farlsworth.giveBottleBackEventually.setCompleted(true);
				sequencePart=100;
			}
			else sequencePart = 101;
			runFarlsworthAway();
		}
		
		if(sequencePart == 100 && farlsworth.getAllCommands().size() == 1) {
			
			// Set the next text and advance it.
			farlsworth.putItemInMouth(player.getPlayer().getPlayerInventory().get("Save Bottle"));
			sequencePart=101;
		}
		
		if(sequencePart == 101 && farlsworth.getAllCommands()!=null && (farlsworth.getAllCommands().size() == 0)) {
			
			farlsworth.destroy();
			stop();
		}
	}
	
	// Run Farlsworth away
	public void runFarlsworthAway() {
		
		farlsworth.setMoveSpeed(4.5f);
		commandList commands = new commandList();
		commands.add(new moveCommand(-3463, -5550));
		commands.add(new moveCommand(-3420,-5803));
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
