package units.characters.farlsworth.cinematics;

import java.lang.invoke.MethodHandles;

import cinematics.cinematic;
import interactions.event;
import interactions.interactBox;
import interactions.textSeries;
import sounds.sound;
import units.player;
import units.characters.farlsworth.farlsworth;
import units.characters.farmer.cinematics.farmerIntroCinematic;
import units.unitCommands.commandList;
import units.unitCommands.commands.moveCommand;

public class farmIntroCinematic extends cinematic {
	
	// Event
	public static event isCompleted = new event(MethodHandles.lookup().lookupClass().getName() + "isCompleted");
	
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
				addTextSeries(null, "Let's see...", farlsworth);
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				// Set the next text and advance it.
				addTextSeries(null, "What do your parents call you?", farlsworth);
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				// Set the next text and advance it.
				addTextSeries(null, "Mummy and daddy?", farmer);
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				// Set the next text and advance it.
				addTextSeries(null, "Yeah, them.", farlsworth);
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				// Set the next text and advance it.
				addTextSeries(null, "No I mean they call me 'Mummy and daddy?'", farmer);
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				// Set the next text and advance it.
				addTextSeries(null, "Apple doesn't fall far from the tree, I see.", farlsworth);
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
					addTextSeries(null, "But it's pretty dangerous out there these days...",farlsworth);
					
					// Set the next text and advance it.
					advanceSequence();
				}
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					addTextSeries(null, "I don't know if I'd want to wander around socializing.",farlsworth);
					
					// Set the next text and advance it.
					advanceSequence();
				}
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					addTextSeries(null, "You know, given the whole world being torn apart thing.",farlsworth);
					
					waitFor(3f);
					// Set the next text and advance it.
					advanceSequence();
				}
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					addTextSeries(null, "Wait ...",farlsworth);
					
					// Set the next text and advance it.
					farlsworth.moveTo(farlsworth.getIntX(), farlsworth.getIntY()+20);
					advanceSequence();
				}
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					addTextSeries(null, "You didn't know?",farlsworth);
					
					// Set the next text and advance it.
					advanceSequence();
				}
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					addTextSeries(null, "Well, you should see it for yourself, then.",farlsworth);
					
					// Set the next text and advance it.
					advanceSequence();
				}
			
				if(isSequence(numIfs++) && goNextTextSeries()) {
					addTextSeries(null, "See if you can keep up.",farlsworth);
					
					// Set the next text and advance it.
					advanceSequence();
				}
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					addTextSeries(null, "Catch me if you can, dumby!",farlsworth);
					runFarlsworthAway();
					// Set the next text and advance it.
					advanceSequence();
				}
			}
			
			// Looking for an adventure CHOICE
			if(goNextTextSeries() && choiceIs("'Looking for an adventure'")) {
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					addTextSeries(null, "An adventure, eh?",farlsworth);
					
					// Set the next text and advance it.
					advanceSequence();
				}
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					addTextSeries(null, "Pfft. You don't know diddly-squat about adventures.",farlsworth);
					
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
					addTextSeries(null, "You know, with the whole world being torn apart thing.",farlsworth);
					
					// Set the next text and advance it.
					waitFor(3f);
					advanceSequence();
				}
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					addTextSeries(null, "Wait ...",farlsworth);
					
					// Set the next text and advance it.
					farlsworth.moveTo(farlsworth.getIntX(), farlsworth.getIntY()+20);
					advanceSequence();
				}
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					addTextSeries(null, "You didn't know?",farlsworth);
					
					// Set the next text and advance it.
					advanceSequence();
				}
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					addTextSeries(null, "Boy, have you been living under a rock?",farlsworth);
					
					// Set the next text and advance it.
					advanceSequence();
				}
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					addTextSeries(null, "Well, you're in for quite the adventure, then.",farlsworth);
					
					// Set the next text and advance it.
					advanceSequence();
				}
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					addTextSeries(null, "Let's go.",farlsworth);
					
					// Set the next text and advance it.
					advanceSequence();
				}
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					addTextSeries(null, "Catch me if you can, dumby!",farlsworth);
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
				farlsworth.setMoveSpeed(2);
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				// Set the next text and advance it.
				addTextSeries(null, "Frig Farmer.", farlsworth);
				farlsworth.moveTo(farlsworth.getIntX()-25,farlsworth.getIntY());
				advanceSequence();
			}
			
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				// Set the next text and advance it.
				addTextSeries(null, "And frig you, random person.", farlsworth);
				farlsworth.moveTo(farlsworth.getIntX(),farlsworth.getIntY()+25);
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				// Set the next text and advance it.
				addTextSeries(null, "You both can frig off for all I care.", farlsworth);
				farlsworth.moveTo(farlsworth.getIntX(),farlsworth.getIntY()-25);
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				// Set the next text and advance it.
				addTextSeries(null, "And don't try anything fishy, buddy.", farlsworth);
				farlsworth.faceTowardPlayer();
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				// Set the next text and advance it.
				addTextSeries(null, "This isn't my first rodeo.", farlsworth);
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
					
					addTextSeries(null, "Yeah, Farmer did just talk to you.",farlsworth);
					
					sound sound = new sound(laughtrack);
					sound.start();
					
					// Set the next text and advance it.
					advanceSequence();
				}
	
				if(isSequence(numIfs++) && goNextTextSeries()) {
					
					addTextSeries(null, "Jeez, you can't remember!?",farlsworth);
					
					// Set the next text and advance it.
					advanceSequence();
				}
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					
					addTextSeries(null, "That's one thing you and him have in common.",farlsworth);
					
					// Set the next text and advance it.
					advanceSequence();
				}
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					addTextSeries(null, "Short-term memory.",farlsworth);
					
					// Set the next text and advance it.
					advanceSequence();
				}
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					addTextSeries(null, "Also being really ugly.",farlsworth);
					
					// Set the next text and advance it.
					advanceSequence();
				}
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					addTextSeries(null, "And I don't like ugly people in my pen.",farlsworth);
					
					// Set the next text and advance it.
					advanceSequence();
				}
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					addTextSeries(null, "So what are you hoping to achieve here?",farlsworth);
					
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
						addTextSeries(null, "An adventure, eh?",farlsworth);
						
						// Set the next text and advance it.
						advanceSequence();
					}
					
					if(isSequence(numIfs++) && goNextTextSeries()) {
						addTextSeries(null, "Pfft. You don't know diddly-squat about adventures.",farlsworth);
						
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
						addTextSeries(null, "You know, with the whole world being torn apart thing.",farlsworth);
						
						// Set the next text and advance it.
						waitFor(3f);
						advanceSequence();
					}
					
					if(isSequence(numIfs++) && goNextTextSeries()) {
						addTextSeries(null, "Wait ...",farlsworth);
						
						// Set the next text and advance it.
						farlsworth.moveTo(farlsworth.getIntX(), farlsworth.getIntY()+20);
						advanceSequence();
					}
					
					if(isSequence(numIfs++) && goNextTextSeries()) {
						addTextSeries(null, "You didn't know?",farlsworth);
						
						// Set the next text and advance it.
						advanceSequence();
					}
					
					if(isSequence(numIfs++) && goNextTextSeries()) {
						addTextSeries(null, "Boy, have you been living under a rock?",farlsworth);
						
						// Set the next text and advance it.
						advanceSequence();
					}
					
					if(isSequence(numIfs++) && goNextTextSeries()) {
						addTextSeries(null, "Well, you're in for quite the adventure, then.",farlsworth);
						
						// Set the next text and advance it.
						advanceSequence();
					}
					
					if(isSequence(numIfs++) && goNextTextSeries()) {
						addTextSeries(null, "Let's go.",farlsworth);
						
						// Set the next text and advance it.
						advanceSequence();
					}
					
					if(isSequence(numIfs++) && goNextTextSeries()) {
						addTextSeries(null, "Catch me if you can, dumby!",farlsworth);
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
						addTextSeries(null, "I don't think so, pal.",farlsworth);
						
						// Set the next text and advance it.
						advanceSequence();
					}
					
					if(isSequence(numIfs++) && goNextTextSeries()) {
						addTextSeries(null, "The world as we know it is being torn apart ...",farlsworth);
						
						// Set the next text and advance it.
						advanceSequence();
					}
					
					if(isSequence(numIfs++) && goNextTextSeries()) {
						addTextSeries(null, "And you're asking for my friggin wool?",farlsworth);
						
						// Set the next text and advance it.
						advanceSequence();
					}
					
					if(isSequence(numIfs++) && goNextTextSeries()) {
						addTextSeries(null, "What a ludicrous request.",farlsworth);
						waitFor(3f);
						
						// Set the next text and advance it.
						advanceSequence();
					}
					
					if(isSequence(numIfs++) && goNextTextSeries()) {
						addTextSeries(null, "Wait ...",farlsworth);
						
						// Set the next text and advance it.
						farlsworth.moveTo(farlsworth.getIntX(), farlsworth.getIntY()+20);
						advanceSequence();
					}
					
					if(isSequence(numIfs++) && goNextTextSeries()) {
						addTextSeries(null, "You didn't know?",farlsworth);
						
						// Set the next text and advance it.
						advanceSequence();
					}
					
					if(isSequence(numIfs++) && goNextTextSeries()) {
						addTextSeries(null, "Let's have some fun with this, then.",farlsworth);
						
						// Set the next text and advance it.
						advanceSequence();
					}
				
					if(isSequence(numIfs++) && goNextTextSeries()) {
						addTextSeries(null, "You want my wool?",farlsworth);
						
						// Set the next text and advance it.
						advanceSequence();
					}
					
					if(isSequence(numIfs++) && goNextTextSeries()) {
						addTextSeries(null, "Come and get it.",farlsworth);
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
					
					// Set the next text and advance it.
					advanceSequence();
				}
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					addTextSeries(null, "I don't think so, pal.",farlsworth);
					
					// Set the next text and advance it.
					advanceSequence();
				}
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					addTextSeries(null, "The world as we know it is being torn apart ...",farlsworth);
					
					// Set the next text and advance it.
					advanceSequence();
				}
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					addTextSeries(null, "And you're asking for my friggin wool?",farlsworth);
					
					// Set the next text and advance it.
					advanceSequence();
				}
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					addTextSeries(null, "What a ludicrous request.",farlsworth);
					waitFor(3f);
					
					// Set the next text and advance it.
					advanceSequence();
				}
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					addTextSeries(null, "Wait ...",farlsworth);
					
					farlsworth.moveTo(farlsworth.getIntX(), farlsworth.getIntY()+20);
					// Set the next text and advance it.
					advanceSequence();
				}
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					addTextSeries(null, "You didn't know?",farlsworth);
					
					// Set the next text and advance it.
					advanceSequence();
				}
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					addTextSeries(null, "Let's have some fun with this, then.",farlsworth);
					
					// Set the next text and advance it.
					advanceSequence();
				}
			
				if(isSequence(numIfs++) && goNextTextSeries()) {
					addTextSeries(null, "You want my wool?",farlsworth);
					
					// Set the next text and advance it.
					advanceSequence();
				}
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					addTextSeries(null, "Come and get it.",farlsworth);
					runFarlsworthAway();
					// Set the next text and advance it.
					advanceSequence();
				}
			}
		}
	}
	
	// Run Farlsworth away
	public void runFarlsworthAway() {
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