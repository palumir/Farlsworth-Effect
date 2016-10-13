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
import units.characters.farmer.cinematics.farmerIntroCinematic;
import units.unitCommands.commandList;
import units.unitCommands.commands.moveCommand;
import zones.sheepFarm.subZones.sheepFarm;

public class farmIntroCinematic extends cinematic {
	
	// Event
	public static event isCompleted = event.createEvent(MethodHandles.lookup().lookupClass().getName() + "isCompleted");
	
	// lol!
	private static String laughtrack = "sounds/effects/characters/farlsworth/laughTrack.wav";

	public farmIntroCinematic() {
		super("farmIntroCinematic");
	}
	
	farlsworth farlsworth;
	units.characters.farmer.farmer farmer;

	@Override
	public void play() {
		
	    // Lock the player's movement.
	    player.getPlayer().stopMove("all");
	    
	    // Create interactSequence (first thing he says to you)
	    textSeries s;
	    if(farmerIntroCinematic.playerPressedNoABunch.isCompleted()) {
	    	s = new textSeries(null, "How many friggin times do I have to tell you?");
	    }
	    else {
	    	s = new textSeries(null, "He's sent somebody to gather my wool, has he?");
	    }
	    farlsworth = units.characters.farlsworth.farlsworth.farlsworth;
	    farmer = units.characters.farmer.farmer.farmer;
	    farmer.setFacingDirection("Right");
	    
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
		if(farmerIntroCinematic.playerPressedNoABunch.isCompleted()) {
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				// Set the next text and advance it.
				addTextSeries(null, "Your name isn't Farmer just because you run a farm.", farlsworth);
				farlsworth.setMoveSpeed(2);
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				// Set the next text and advance it.
				addTextSeries(null, "You are a farmer. But your name is Christopher.", farlsworth);
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				// Set the next text and advance it.
				addTextSeries(null, "But I don't run a christoph.", farmer);
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				// Set the next text and advance it.
				addTextSeries(null, "Damnit.", farlsworth);
				advanceSequence();
			}
			
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				// Set the next text and advance it.
				addTextSeries(null, "We're going in circles again.", farlsworth);
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				// Set the next text and advance it.
				addTextSeries(null, "Whose this?", farlsworth);
				farlsworth.setFacingDirection("Down");
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				// Set the next text and advance it.
				addTextSeries(null, "My new friend.", farmer);
				farmer.setFacingDirection("Down");
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				// Set the next text and advance it.
				addTextSeries(null, "She's the mischievous type.", farmer);
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				// Set the next text and advance it.
				addTextSeries(null, "Oh yeah?", farlsworth);
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				// Set the next text and advance it.
				addTextSeries(null, "What are you really doing here?", farlsworth);
				farlsworth.moveTo(farlsworth.getIntX(), farlsworth.getIntY()+25);
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				// Move right.
				addTextSeries("'Talking to you'", null,farlsworth, "Order");
				addTextSeries("'Looking for an adventure'", null,farlsworth,"Chaos");
				interactSequence.goToNext();
				
				// Set the next text and advance it.
				advanceSequence();
			}
			
			// Talking to you CHOICE
			if(goNextTextSeries() && choiceIs("'Talking to you'")) {
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					addTextSeries(null, "Fair enough, I guess.",farlsworth);
					
					// Set the next text and advance it.
					advanceSequence();
				}
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					addTextSeries(null, "But it's pretty dangerous out there these days.",farlsworth);
					
					// Set the next text and advance it.
					advanceSequence();
				}
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					addTextSeries(null, "I don't know if I'd want to wander around socializing.",farlsworth);
					
					// Set the next text and advance it.
					advanceSequence();
				}
			
				if(isSequence(numIfs++) && goNextTextSeries()) {
					addTextSeries(null, "But, since you're bored, how about we have some fun?",farlsworth);
					music.currMusic.fadeOut(2f);
					
					// Set the next text and advance it.
					advanceSequence();
				}
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					addTextSeries(null, "Catch me if you can, dumbo!",farlsworth);
					runFarlsworthAway();
					// Set the next text and advance it.
					advanceSequence();
				}
			}
			
			// Looking for an adventure CHOICE
			if(goNextTextSeries() && choiceIs("'Looking for an adventure'")) {
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					addTextSeries(null, "Pfft. You don't know mumbo bumbo about adventures.",farlsworth);
					
					// Set the next text and advance it.
					advanceSequence();
				}
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					addTextSeries(null, "Nobody does it better than ol' Farlsworth.",farlsworth);
					
					// Set the next text and advance it.
					advanceSequence();
				}
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					addTextSeries(null, "But it's dangerous out there these days.",farlsworth);
					
					// Set the next text and advance it.
					advanceSequence();
				}
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					addTextSeries(null, "Not that danger would stop a real adventurer!",farlsworth);
					
					music.currMusic.fadeOut(2f);
					// Set the next text and advance it.
					advanceSequence();
				}
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					addTextSeries(null, "So let's go, dumbo!",farlsworth);
					runFarlsworthAway();
					// Set the next text and advance it.
					advanceSequence();
				}
			}
		}
		else {
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				// Set the next text and advance it.
				addTextSeries(null, "Well ...", farlsworth);
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				// Set the next text and advance it.
				farlsworth.setMoveSpeed(2);
				addTextSeries(null, "Frig you.", farlsworth);
				farlsworth.moveTo(farlsworth.getIntX(),farlsworth.getIntY()+25);
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				// Set the next text and advance it.
				addTextSeries(null, "You aint getting jack, buddy!", farlsworth);
				farlsworth.moveTo(farlsworth.getIntX(),farlsworth.getIntY()-25);
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				// Move right.
				addTextSeries("'Can I have your wool?'", null,farlsworth,"Order");
				addTextSeries("'Did that sheep just talk?'", null,farlsworth);
				interactSequence.goToNext();
				
				// Set the next text and advance it.
				advanceSequence();
			}
			
			// Did that sheep just talk? CHOICE
			if(goNextTextSeries() && choiceIs("'Did that sheep just talk?'")) {
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					
					addTextSeries(null, "Yeah, you did!",farlsworth);
					farlsworth.moveTo(farlsworth.getIntX(),farlsworth.getIntY()+25);
					
					sound sound = new sound(laughtrack);
					sound.start();
					
					// Set the next text and advance it.
					advanceSequence();
				}
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					addTextSeries(null, "Although... ",farlsworth);
					
					// Set the next text and advance it.
					advanceSequence();
				}
				
				
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					addTextSeries(null, "You're a pretty weird looking sheep.",farlsworth);
					
					// Set the next text and advance it.
					advanceSequence();
				}
				
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					addTextSeries(null, "And I don't like weird looking sheep in my pen.",farlsworth);
					
					// Set the next text and advance it.
					advanceSequence();
				}
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					addTextSeries(null, "So take a friggin hike, bozo!",farlsworth);
					
					// Set the next text and advance it.
					advanceSequence();
				}
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					
					// Move right.
					addTextSeries("'Can I have your wool?'", null,farlsworth, "Order");
					addTextSeries("'I'm on an adventure'", null,farlsworth,"Chaos");
					interactSequence.goToNext();
					
					// Set the next text and advance it.
					advanceSequence();
				}
				
				// Give wool choice CHOICE
				if(goNextTextSeries() && choiceIs("'I'm on an adventure'")) { 
					
					if(isSequence(numIfs++) && goNextTextSeries()) {
						addTextSeries(null, "Pfft. You don't know mumbo bumbo about adventures.",farlsworth);
						
						// Set the next text and advance it.
						advanceSequence();
					}
					
					if(isSequence(numIfs++) && goNextTextSeries()) {
						addTextSeries(null, "Nobody does it better than ol' Farlsworth.",farlsworth);
						
						// Set the next text and advance it.
						advanceSequence();
					}
					
					if(isSequence(numIfs++) && goNextTextSeries()) {
						addTextSeries(null, "But it's dangerous out there these days.",farlsworth);
						
						// Set the next text and advance it.
						advanceSequence();
					}
					
					if(isSequence(numIfs++) && goNextTextSeries()) {
						addTextSeries(null, "Not that danger would stop a real adventurer!",farlsworth);
						music.currMusic.fadeOut(2f);
						
						// Set the next text and advance it.
						advanceSequence();
					}
					
					if(isSequence(numIfs++) && goNextTextSeries()) {
						addTextSeries(null, "So let's go, dumbo!",farlsworth);
						runFarlsworthAway();
						// Set the next text and advance it.
						advanceSequence();
					}
				}
				
				// Give wool choice CHOICE
				if(goNextTextSeries() && choiceIs("'Can I have your wool?'")) {
						
					if(isSequence(numIfs++) && goNextTextSeries()) {
						addTextSeries(null, "My wool?",farlsworth);
						
						// Set the next text and advance it.
						advanceSequence();
					}
					
					if(isSequence(numIfs++) && goNextTextSeries()) {
						addTextSeries(null, "I'm not a friggin object, you jackass!",farlsworth);
						
						// Set the next text and advance it.
						advanceSequence();
					}
				
					if(isSequence(numIfs++) && goNextTextSeries()) {
						addTextSeries(null, "You want my wool?",farlsworth);
						music.currMusic.fadeOut(2f);
						
						// Set the next text and advance it.
						advanceSequence();
					}
					
					if(isSequence(numIfs++) && goNextTextSeries()) {
						addTextSeries(null, "You're gonna have to work for it!",farlsworth);
						runFarlsworthAway();
						// Set the next text and advance it.
						advanceSequence();
					}
				}
			}
			// Give wool choice CHOICE
			if(goNextTextSeries() && choiceIs("'Can I have your wool?'")) {
				if(isSequence(numIfs++) && goNextTextSeries()) {
					addTextSeries(null, "My wool?",farlsworth);
					farlsworth.moveTo(farlsworth.getIntX(),farlsworth.getIntY()+25);
					
					// Set the next text and advance it.
					advanceSequence();
				}
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					addTextSeries(null, "I'm not a friggin object, you jackass!",farlsworth);
					
					// Set the next text and advance it.
					advanceSequence();
				}
			
				if(isSequence(numIfs++) && goNextTextSeries()) {
					addTextSeries(null, "You want my wool?",farlsworth);
					
					music.currMusic.fadeOut(2f);
					// Set the next text and advance it.
					advanceSequence();
				}
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					addTextSeries(null, "You're gonna have to work for it!",farlsworth);
					runFarlsworthAway();
					// Set the next text and advance it.
					advanceSequence();
				}
			}
		}
	}
	
	// Run Farlsworth away
	public void runFarlsworthAway() {
		music.endAll();
		music.startMusic(sheepFarm.forestMusic);
		farlsworth.setMoveSpeed(4.5f);
		commandList commands = new commandList();
		commands.add(new moveCommand(425,farlsworth.getIntY()));
		commands.add(new moveCommand(425,-70));
		commands.add(new moveCommand(425,5));
		commands.add(new moveCommand(5,-1));
		commands.add(new moveCommand(5,-420));
		farlsworth.doCommandsOnce(commands);
		sound s = new sound(farlsworth.bleet);
		s.setPosition(farlsworth.getIntX(), farlsworth.getIntY(), sound.DEFAULT_SOUND_RADIUS);
		s.start();
		interactSequence.getTextSeries().setEnd();
		interactSequence.setLocked(false);
		interactSequence.setUnescapable(false);
		stop();
	}

	@Override
	public event isCompleted() {
		return isCompleted;
	}
	
}