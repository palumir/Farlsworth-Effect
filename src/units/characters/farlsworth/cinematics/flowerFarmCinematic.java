package units.characters.farlsworth.cinematics;

import java.lang.invoke.MethodHandles;

import cinematics.cinematic;
import interactions.event;
import interactions.interactBox;
import interactions.textSeries;
import sounds.sound;
import units.player;
import units.characters.farlsworth.farlsworth;
import units.unitCommands.commandList;
import units.unitCommands.commands.moveCommand;
import utilities.saveState;
import utilities.time;

public class flowerFarmCinematic extends cinematic {
	
	// Event
	public static event isCompleted = new event(MethodHandles.lookup().lookupClass().getName() + "isCompleted");

	public flowerFarmCinematic() {
		super("flowerFarmCinematic");
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
	    textSeries s = new textSeries(null, "Boy, you're slow, eh?");
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
			addTextSeries(null, "Physically, I mean.", farlsworth);
			farlsworth.setMoveSpeed(2);
			farlsworth.moveTo(farlsworth.getIntX()+15, farlsworth.getIntY());
			advanceSequence();
		}
		
		if(isSequence(numIfs++) && goNextTextSeries()) {
			
			// Set the next text and advance it.
			addTextSeries(null, "I wouldn't insult you. I'm not a bully.", farlsworth);
			advanceSequence();
		}
		
		if(isSequence(numIfs++) && goNextTextSeries()) {
			
			// Set the next text and advance it.
			addTextSeries(null, "Unlike these friggin wolves.", farlsworth);
			advanceSequence();
		}
		if(isSequence(numIfs++) && goNextTextSeries()) {
			
			// Set the next text and advance it.
			addTextSeries(null, "Jumping around, mindlessly one-hitting anything that they touch.", farlsworth);
			advanceSequence();
		}
		
		if(isSequence(numIfs++) && goNextTextSeries()) {
			
			// Set the next text and advance it.
			addTextSeries(null, "I guess that's their way of dealing with everything.", farlsworth);
			farlsworth.moveTo(farlsworth.getIntX()-15, farlsworth.getIntY());
			advanceSequence();
		}
		
		if(isSequence(numIfs++) && goNextTextSeries()) {
			
			// Set the next text and advance it.
			addTextSeries(null, "It can't be nice to lose your home to a Disruption.", farlsworth);
			farlsworth.moveTo(farlsworth.getIntX()+15, farlsworth.getIntY());
			advanceSequence();
		}
		
		if(isSequence(numIfs++) && goNextTextSeries()) {
			
			// Set the next text and advance it.
			addTextSeries(null, "Of course, I wouldn't know.", farlsworth);
			farlsworth.setFacingDirection("Down");
			advanceSequence();
		}
		
		if(isSequence(numIfs++) && goNextTextSeries()) {
			
			// Set the next text and advance it.
			addTextSeries(null, "The farm and my pen are still intact.", farlsworth);
			advanceSequence();
		}
		
		
		if(isSequence(numIfs++) && goNextTextSeries()) {
			
			// Set the next text and advance it.
			addTextSeries(null, "And so is the damn farmer.", farlsworth);
			advanceSequence();
		}
		
		if(isSequence(numIfs++) && goNextTextSeries()) {
			
			// Set the next text and advance it.
			addTextSeries(null, "I hope a Disruption destroys his house.", farlsworth);
			advanceSequence();
		}
		
		if(isSequence(numIfs++) && goNextTextSeries()) {
			
			// Set the next text and advance it.
			farmer = new units.characters.farmer.farmer(0,0);
			farmer.setDoubleX(farlsworth.getIntX()+540);
			farmer.setDoubleY(player.getPlayer().getIntY());
			addTextSeries(null, "Hey, what are you guys up to?", farmer);
			player.getPlayer().setFacingDirection("Right");
			farlsworth.setFacingDirection("Right");
			farmer.setMoveSpeed(2);
			farmer.moveTo(farmer.getIntX() - 100,farmer.getIntY());
			advanceSequence();
		}
		
		if(isSequence(numIfs++) && goNextTextSeries()) {
			
			// Set the next text and advance it.
			addTextSeries(null, "What?", farlsworth);
			player.getPlayer().setFacingDirection("Left");
			advanceSequence();
		}
		
		if(isSequence(numIfs++) && goNextTextSeries()) {
			
			// Set the next text and advance it.
			addTextSeries(null, "Hey, what are you guys up to?", farmer);
			player.getPlayer().setFacingDirection("Right");
			advanceSequence();
		}
		
		if(isSequence(numIfs++) && goNextTextSeries()) {
			
			// Set the next text and advance it.
			addTextSeries(null, "No, no.", farlsworth);
			player.getPlayer().setFacingDirection("Left");
			advanceSequence();
		}
		
		if(isSequence(numIfs++) && goNextTextSeries()) {
			
			// Set the next text and advance it.
			addTextSeries(null, "I wasn't asking for you to repeat yourself.", farlsworth);
			advanceSequence();
		}
		
		if(isSequence(numIfs++) && goNextTextSeries()) {
			
			// Set the next text and advance it.
			addTextSeries(null, "I meant it as in 'what are you doing here?'", farlsworth);
			advanceSequence();
		}
		
		if(isSequence(numIfs++) && goNextTextSeries()) {
			
			// Set the next text and advance it.
			addTextSeries(null, "That's a pretty deep question dude.", farmer);
			player.getPlayer().setFacingDirection("Right");
			advanceSequence();
		}
		
		if(isSequence(numIfs++) && goNextTextSeries()) {
			
			// Set the next text and advance it.
			addTextSeries(null, "I think ancient aliens put us here as a test.", farmer);
			advanceSequence();
		}
		
		if(isSequence(numIfs++) && goNextTextSeries()) {
			
			// Set the next text and advance it.
			addTextSeries(null, "Alright, this conversation is going no where.", farlsworth);
			player.getPlayer().setFacingDirection("Left");
			advanceSequence();
		}
		
		
		if(isSequence(numIfs++) && goNextTextSeries()) {
			
			// Set the next text and advance it.
			addTextSeries(null, "Ignore Farmer. He'll get bored and leave eventually.", farlsworth);
			advanceSequence();
		}
		
		if(isSequence(numIfs++) && goNextTextSeries()) {
			
			// Set the next text and advance it.
			addTextSeries(null, "So anyway, as I was saying.", farlsworth);
			advanceSequence();
		}
		
		if(isSequence(numIfs++) && goNextTextSeries()) {
			
			// Set the next text and advance it.
			addTextSeries(null, "It must be hard to have your home destroyed by a Disruption.", farlsworth);
			advanceSequence();
		}
		
		if(isSequence(numIfs++) && goNextTextSeries()) {
			
			// Set the next text and advance it.
			addTextSeries(null, "But the Disruptions in this forest are measly.", farlsworth);
			advanceSequence();
		}
		
		if(isSequence(numIfs++) && goNextTextSeries()) {
			
			// Set the next text and advance it.
			addTextSeries(null, "There are far, FAR worse ones that exist.", farlsworth);
			advanceSequence();
		}
		
		if(isSequence(numIfs++) && goNextTextSeries()) {
			
			// Set the next text and advance it.
			addTextSeries(null, "That's what I meant when I said the world is being torn apart.", farlsworth);
			advanceSequence();
		}
		
		if(isSequence(numIfs++) && goNextTextSeries()) {
			
			// Set the next text and advance it.
			addTextSeries(null, "I meant that supernatural Disruptions are destroying reality.", farlsworth);
			advanceSequence();
		}
		
		if(isSequence(numIfs++) && goNextTextSeries()) {
			
			// Set the next text and advance it.
			addTextSeries(null, "This farm's pretty dope. But not as dope as mine.", farmer);
			advanceSequence();
		}
		
		if(isSequence(numIfs++) && goNextTextSeries()) {
			
			// Set the next text and advance it.
			addTextSeries(null, "What do you guys think?", farmer);
			advanceSequence();
		}
		
		if(isSequence(numIfs++) && goNextTextSeries()) {
			
			// Move right.
			time.setTimeSpeed(0.1f);
			addTextSeries("Ignore Farmer", null,farlsworth, "Order");
			addTextSeries("Don't ignore Farmer", null,farlsworth,"Chaos");
			interactSequence.goToNext();
			
			// Set the next text and advance it.
			advanceSequence();
		}
		
		if(goNextTextSeries() && choiceIs("Don't ignore Farmer")) {
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				// Set the next text and advance it.
				addTextSeries(null, "This farm doesn't even have sheep!", farmer);
				sound sound = new sound(cheering);
				sound.start();
				time.setTimeSpeed(1f);
				player.getPlayer().setFacingDirection("Right");
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				// Set the next text and advance it.
				addTextSeries(null, "Except for Fransburns. He's right behind you.", farmer);
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				// Set the next text and advance it.
				addTextSeries(null, "Damnit. I told you to ignore him.", farlsworth);
				player.getPlayer().setFacingDirection("Left");
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				// Set the next text and advance it.
				addTextSeries(null, "Have you met Cransfurns yet?", farmer);
				player.getPlayer().setFacingDirection("Right");
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				// Set the next text and advance it.
				addTextSeries(null, "That's not even my name.", farlsworth);
				player.getPlayer().setFacingDirection("Left");
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				// Set the next text and advance it.
				addTextSeries(null, "I'm getting too old for this crap.", farlsworth);
				player.getPlayer().setFacingDirection("Left");
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				// Set the next text and advance it.
				addTextSeries(null, "I'm friggin done!", farlsworth);
				farlsworth.setFacingDirection("Left");
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				// Set the next text and advance it.
				addTextSeries(null, "Frig you both!", farlsworth);
				runFarlsworthAwayFarmer();
				advanceSequence();
			}
			
			///// THIS IS BROKEN AT THIS POINT!!! FRIGINN FIX 'ER
			
			if(isSequence(numIfs++) && farlsworth.getAllCommands()!=null && (farlsworth.getAllCommands().size() == 0)) {
				farlsworth.setDoubleX(-3463);
				farlsworth.setDoubleY(-5550);
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				// Set the next text and advance it.
				addTextSeries(null, "He must have had to go potty really bad.", farmer);
				player.getPlayer().setFacingDirection("Right");
				advanceSequence();
			}
			
		}
	
		if(goNextTextSeries() && choiceIs("Ignore Farmer")) {
		
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				// Set the next text and advance it.
				time.setTimeSpeed(1f);
				addTextSeries(null, "But I can't just accept that Disruptions are happening.", farlsworth);
				sound sound = new sound(booing);
				sound.start();
				player.getPlayer().setFacingDirection("Left");
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				// Set the next text and advance it.
				addTextSeries(null, "I'm not like everybody else.", farlsworth);
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				// Set the next text and advance it.
				addTextSeries(null, "This is where we live. This is our home.", farlsworth);
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				// Set the next text and advance it.
				addTextSeries(null, "I can't just let it get destroyed because I'm comfortable.", farlsworth);
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				// Set the next text and advance it.
				addTextSeries(null, "There isn't any other reality we can go to.", farlsworth);
				advanceSequence();
			}
					
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				// Set the next text and advance it.
				player.getPlayer().setFacingDirection("Right");
				addTextSeries(null, "I'm having a great time hanging out with you guys.", farmer);
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				// Set the next text and advance it.
				addTextSeries(null, "But nobody seems to care, let alone notice.", farlsworth);
				player.getPlayer().setFacingDirection("Left");
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				// Set the next text and advance it.
				addTextSeries(null, "If only I knew what was causing these Disruptions.", farlsworth);
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				// Set the next text and advance it.
				addTextSeries(null, "I would do whatever it takes to stop them from happening.", farlsworth);
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				// Set the next text and advance it.
				addTextSeries(null, "But things just aren't that simple.", farlsworth);
				advanceSequence();
			}
	
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				// Set the next text and advance it.
				addTextSeries(null, "I'm going to go eat Captain Crunch and watch cartoons.", farmer);
				player.getPlayer().setFacingDirection("Right");
				farmer.moveTo(farmer.getIntX(), farmer.getIntY()+500);
				advanceSequence();
			}
			
			if(sequencePart >= 20 && !farmer.isOnScreen()) {
				farmer.destroy();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				// Set the next text and advance it.
				addTextSeries(null, "We can't lose anymore good people.", farlsworth);
				player.getPlayer().setFacingDirection("Left");
				farlsworth.moveTo(farlsworth.getIntX()-20, farlsworth.getIntY());
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				// Set the next text and advance it.
				addTextSeries(null, "I can't.", farlsworth);
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				// Set the next text and advance it.
				addTextSeries(null, "Uh ...", farlsworth);
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				// Set the next text and advance it.
				addTextSeries(null, "I just can't, okay?", farlsworth);
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				// Set the next text and advance it.
				addTextSeries(null, "Just be careful on this next area. It's pouring rain.", farlsworth);
				farlsworth.setFacingDirection("Right");
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				// Set the next text and advance it.
				addTextSeries(null, "The ground may get a bit slippery.", farlsworth);
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				// Set the next text and advance it.
				addTextSeries(null, "Try not to die so much.", farlsworth);
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
	public void runFarlsworthAwayFarmer() {
		save = false;
		farlsworth.setMoveSpeed(10f);
		commandList commands = new commandList();
		commands.add(new moveCommand(-1677,-4866));
		commands.add(new moveCommand(-2108,-4866));
		commands.add(new moveCommand(-2108,-6223));
		commands.add(new moveCommand(-2108,-6223));
		farlsworth.doCommandsOnce(commands);
		sound s = new sound(farlsworth.bleet);
		s.setPosition(farlsworth.getIntX(), farlsworth.getIntY(), sound.DEFAULT_SOUND_RADIUS);
		s.start();
		
		// Set it to be completed as soon as he runs, instead of when he's teleported to flower farm.
		isCompleted.setCompleted(true);
		
		// Save by default.
		/*saveState.setQuiet(true);
		saveState.createSaveState();
		saveState.setQuiet(false);*/
	}
	
	// Run Farlsworth away
	public void runFarlsworthAway() {
		save = false;
		farlsworth.setMoveSpeed(4.5f);
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
