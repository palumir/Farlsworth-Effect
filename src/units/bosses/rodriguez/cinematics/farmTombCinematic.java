package units.bosses.rodriguez.cinematics;

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
import units.bosses.wolfless.wolfless;
import units.unitCommands.commandList;
import units.unitCommands.commands.jumpCommand;
import units.unitCommands.commands.moveCommand;
import utilities.saveState;
import utilities.time;
import zones.farmTomb.subZones.farmTomb;

public class farmTombCinematic extends cinematic {
	
	// Event
	public static event isCompleted = new event(MethodHandles.lookup().lookupClass().getName() + "isCompleted");

	public farmTombCinematic() {
		super("farmTombCinematic");
	}
	
	// Scene music
	private String sceneMusic = "sounds/music/farmLand/unused/newSong.wav";
	private String bellToll = "sounds/effects/horror/bellToll.wav";
	
	rodriguez rodriguez;
	fernando fernando;

	@Override
	public void play() {
		
		// Spawn the boys.
		rodriguez = new rodriguez(7400,1796){{setInteractable(false);}};
	    fernando = new fernando(6662,1796){{setInteractable(false);}};
	    
	    // Lock the player's movement.
	    player.getPlayer().stopMove("all");
	    
	    // Create interactSequence (first thing he says to you)
	    textSeries s = new textSeries(null, "Hello, my friend.");
	    
	    // Move left.
	 	rodriguez.moveTo(rodriguez.getIntX() + -109, rodriguez.getIntY());
	    interactSequence = new interactBox(s, rodriguez);
	    interactSequence.toggleDisplay();
		interactSequence.setUnescapable(true);
	    music.currMusic.fadeOut(3f);
	    save = false;
	}

	@Override
	public void finish() {
	}

	@Override
	public void updateCinematic() {
		
		textSeries s;
		int numIfs = 0;
		
		if(!bellTolled) {
		// Begin the sequence.
		if(isSequence(numIfs++) && goNextTextSeries()) {
			
			advanceSequence();
			music m = new music(sceneMusic);
			m.fadeIn(3f);
		}
		
		// Begin the sequence.
		if(isSequence(numIfs++) && goNextTextSeries() && !rodriguez.isMoving()) {
			
			
			// Set the next text and advance it.
			addTextSeries(null, "I have been waiting patiently for somebody like you.",rodriguez);
			advanceSequence();
		}
		
		if(isSequence(numIfs++) && goNextTextSeries()) {
			
			
			// Set the next text and advance it.
			addTextSeries(null, "Our meeting has been a long time coming.",rodriguez);
			advanceSequence();
		}
		
		if(isSequence(numIfs++) && goNextTextSeries()) {
			
			// Set the next text and advance it.
			addTextSeries(null, "However, we are limited on time, so I will cut to the chase.",rodriguez);
			advanceSequence();
		}
		
		if(isSequence(numIfs++) && goNextTextSeries()) {
			
			// Set the next text and advance it.
			addTextSeries(null, "I believe that you have an extraordinary power.",rodriguez);
			advanceSequence();
		}
		
		if(isSequence(numIfs++) && goNextTextSeries()) {
			
			// Set the next text and advance it.
			addTextSeries(null, "And with extraordinary power comes great spiderman jokes.",rodriguez);
			advanceSequence();
		}
		
		if(isSequence(numIfs++) && goNextTextSeries()) {
			
			// Set the next text and advance it.
			addTextSeries(null, "And what would you know about spiderman jokes, Rodriguez?",fernando);
			
			// Move right.
			fernando.moveTo(fernando.getIntX() + 85, fernando.getIntY());
			player.getPlayer().setFacingDirection("Left");
			waitFor(1f);
			advanceSequence();
		}
		
		if(isSequence(numIfs++) && goNextTextSeries()) {
			
			// Set the next text and advance it.
			addTextSeries(null, "You are getting quicker, Fernando.",rodriguez);
			
			// Move right.
			player.getPlayer().setFacingDirection("Right");
			advanceSequence();
		}
		
		if(isSequence(numIfs++) && goNextTextSeries()) {
			
			// Set the next text and advance it.
			addTextSeries(null, "But that was just a meta joke.",rodriguez);
			
			advanceSequence();
		}
		
		if(isSequence(numIfs++) && goNextTextSeries()) {
			
			// Set the next text and advance it.
			addTextSeries(null, "I want to help her control her power.",rodriguez);
			
			advanceSequence();
		}
		
		if(isSequence(numIfs++) && goNextTextSeries() && !fernando.isMoving()) {
			
			addTextSeries(null, "Oh yeah?",fernando);
			player.getPlayer().setFacingDirection("Left");
			
			// Set the next text and advance it.
			advanceSequence();
		}
		
		
		if(isSequence(numIfs++) && goNextTextSeries() && !fernando.isMoving()) {
			
			addTextSeries(null, "You can barely understand your own power, let alone control it.",fernando);
			
			// Set the next text and advance it.
			advanceSequence();
		}
		
		if(isSequence(numIfs++) && goNextTextSeries()) {
			
			// Move right.
			addTextSeries(null, "What makes you think you can control her?",fernando);
			
			// Set the next text and advance it.
			advanceSequence();
		}
		
		if(isSequence(numIfs++) && goNextTextSeries()) {
			
			// Move right.
			addTextSeries(null, "What makes you think you should?",fernando);
			
			// Set the next text and advance it.
			advanceSequence();
		}
		
		if(isSequence(numIfs++) && goNextTextSeries()) {
			
			// Move right.
			addTextSeries(null, "She has a right to choose, doesn't she?",fernando);
			
			// Set the next text and advance it.
			advanceSequence();
		}
		
		if(isSequence(numIfs++) && goNextTextSeries()) {
			
			// Move right.
			addTextSeries(null, "Also I know what meta jokes are. Your's just wasn't funny.",fernando);
			
			// Set the next text and advance it.
			advanceSequence();
		}
		
		if(isSequence(numIfs++) && goNextTextSeries()) {
			
			// Move right.
			addTextSeries("'Who are you two?'", null,fernando, "Chaos");
			addTextSeries("Do nothing", null,rodriguez, "Order");
			interactSequence.goToNext();
			player.getPlayer().setFacingDirection("Down");
			
			// Set the next text and advance it.
			advanceSequence();
		}
		
		// Who are you? CHOICE
		if(choiceIs("'Who are you two?'") && goNextTextSeries()) {
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				addTextSeries(null, "Ah, allow me to introduce you to ... ",fernando);
				player.getPlayer().setFacingDirection("Left");
				// Set the next text and advance it.
				advanceSequence();
			}
			
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				addTextSeries(null, "... my unconditionally naive and asinine brother, Rodriguez!",fernando);
				
				// Set the next text and advance it.
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				addTextSeries(null, "And I'm Fernando.",fernando);
				
				// Set the next text and advance it.
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				addTextSeries(null, "He's an idiot. A very powerful, destructive idiot.",rodriguez);
				player.getPlayer().setFacingDirection("Right");
				
				// Set the next text and advance it.
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				addTextSeries(null, "In freedom there is destruction. A necessary evil, no doubt.",fernando);
				player.getPlayer().setFacingDirection("Left");
				
				// Set the next text and advance it.
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				addTextSeries(null, "A necessary evil? Are you forgetting that people disappear?",rodriguez);
				player.getPlayer().setFacingDirection("Right");
				
				// Set the next text and advance it.
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				addTextSeries(null, "Are you forgetting that people NEVER come back?",rodriguez);
				
				// Set the next text and advance it.
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				addTextSeries(null, "How can you be so care-free about it? People need structure.",rodriguez);
				
				// Set the next text and advance it.
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				addTextSeries(null, "This world needs order.",rodriguez);
				
				// Set the next text and advance it.
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				addTextSeries(null, "And I am going to restore it to the world.",rodriguez);
				
				// Set the next text and advance it.
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				addTextSeries(null, "How subtle, Rodriguez.",fernando);
				player.getPlayer().setFacingDirection("Left");
				// Set the next text and advance it.
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				addTextSeries(null, "And how are you going to do that?",fernando);
				// Set the next text and advance it.
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				addTextSeries(null, "By whatever means necessary, of course.",rodriguez);
				player.getPlayer().setFacingDirection("Right");
				// Set the next text and advance it.
				advanceSequence();
				waitFor(1.5f);
			}
		
		if(isSequence(numIfs++)) {
			music.currMusic.fadeOut(4f);
			sound bell = new sound(wolfless.howl);
			sequencePart = 0;
			bellTolled = true;
			bellTollStart = time.getTime();
			bell.start();
			waitFor(3f);
			advanceSequence();
		}
			
			
		}
		
		// Do nothing CHOICE
		if(goNextTextSeries() && choiceIs("Do nothing")) {
			
			if(isSequence(numIfs) && goNextTextSeries()) {
				
				addTextSeries(null, "She has the right to live in a world that is stable.",rodriguez);
				player.getPlayer().setFacingDirection("Right");
				
				// Set the next text and advance it.
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				addTextSeries(null, "A world which naturally tends to harmony.",rodriguez);
				
				// Set the next text and advance it.
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				addTextSeries(null, "Which is something that you and your cronies have disrupted.",rodriguez);
				
				// Set the next text and advance it.
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				addTextSeries(null, "My cronies? I control nobody. People choose to believe.",fernando);
				player.getPlayer().setFacingDirection("Left");
				
				// Set the next text and advance it.
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				addTextSeries(null, "My friends serve nobody but themselves.",fernando);
				
				// Set the next text and advance it.
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				addTextSeries(null, "What makes you so incapable of seeing the beauty in that?",fernando);
				
				// Set the next text and advance it.
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				addTextSeries(null, "Isn't it obvious?",rodriguez);
				player.getPlayer().setFacingDirection("Right");
				// Set the next text and advance it.
				advanceSequence();
			}

			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				addTextSeries(null, "If everything is perfect then nobody will have to choose.",rodriguez);
				player.getPlayer().setFacingDirection("Right");
				// Set the next text and advance it.
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				addTextSeries(null, "So who cares about choice?",rodriguez);
				// Set the next text and advance it.
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				addTextSeries(null, "Is perfection not what we work toward anyway?",rodriguez);
				// Set the next text and advance it.
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				addTextSeries(null, "Speeding up the process is nothing less than compassionate.",rodriguez);
				// Set the next text and advance it.
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				addTextSeries(null, "You are twisted.",fernando);
				player.getPlayer().setFacingDirection("Left");
				// Set the next text and advance it.
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				addTextSeries(null, "I am right. And my jokes are way better than your's.",rodriguez);
				player.getPlayer().setFacingDirection("Right");
				// Set the next text and advance it.
				advanceSequence();
				waitFor(1.5f);
			}
			
			if(isSequence(numIfs++)) {
				music.currMusic.fadeOut(4f);
				sound bell = new sound(wolfless.howl);
				sequencePart = 0;
				bellTolled = true;
				bellTollStart = time.getTime();
				bell.start();
				waitFor(3f);
				advanceSequence();
			}
		}
			
		}
		
		
		if(bellTolled) {
			
			numIfs++;
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				player.getPlayer().setFacingDirection("Right");
				
				farmTomb.zoneFog.fadeTo(0.4f, 1);
				
				addTextSeries(null, "Uh oh.",rodriguez);

				// Set the next text and advance it.
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				player.getPlayer().setFacingDirection("Left");
				
				farmTomb.zoneFog.fadeTo(0.5f, 1);
				
				addTextSeries(null, "Is that all it takes to get him going these days?",fernando);

				// Set the next text and advance it.
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				player.getPlayer().setFacingDirection("Left");
				
				farmTomb.zoneFog.fadeTo(0.6f, 1);
				
				addTextSeries(null, "We're barely even arguing.",fernando);

				// Set the next text and advance it.
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				player.getPlayer().setFacingDirection("Right");
				
				farmTomb.zoneFog.fadeTo(0.7f, 1);
				
				addTextSeries(null, "Quickly. You need to get to safety.",rodriguez);

				// Set the next text and advance it.
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				// Move right.
				addTextSeries("Run to Fernando", null,fernando, "Chaos");
				addTextSeries("Run to Rodriguez", null,rodriguez, "Order");
				
				farmTomb.zoneFog.fadeTo(0.8f, 1);
				interactSequence.goToNext();
				player.getPlayer().setFacingDirection("Down");
				
				// Set the next text and advance it.
				advanceSequence();
			}
			
			// Fernando CHOICE
			if(choiceIs("Run to Rodriguez")) {
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					
					player.getPlayer().moveTo(rodriguez.getIntX() - 30-20, rodriguez.getIntY());
					
					// Set the next text and advance it.
					advanceSequence();
				}
				
				if(isSequence(numIfs++) && !player.getPlayer().isMoving() && goNextTextSeries()) {
					farmTomb.zoneFog.fadeTo(1f, 1);
					addTextSeries(null, "Do not move an inch.",rodriguez);
					fernando.destroy();
					
					waitFor(2f);
					
					// Set the next text and advance it.
					advanceSequence();
				}
				
				if(isSequence(numIfs++)) {
					sound bell = new sound(wolfless.scream);
					bell.start();
					waitFor(6f);
					advanceSequence();
					
				}
				
				if(isSequence(numIfs++)) {
					addTextSeries(null, "I think we are safe.",fernando);
					advanceSequence();
				}
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					farmTomb.zoneFog.fadeTo(0.3f, 1);
					music m = new music(farmTomb.zoneMusic);
					addTextSeries(null, "A friend of mine watches over this place.",rodriguez);
					advanceSequence();
				}
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					addTextSeries(null, "The years have not been so kind to him.",rodriguez);
					advanceSequence();
				}
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					addTextSeries(null, "However, he has become very powerful.",rodriguez);
					advanceSequence();
				}
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					addTextSeries(null, "But his mind is engulfed in chaos.",rodriguez);
					advanceSequence();
				}
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					addTextSeries(null, "Now this place is filled with his creations.",rodriguez);
					advanceSequence();
				}
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					addTextSeries(null, "He went mad trying to create a more orderly world.",rodriguez);
					advanceSequence();
				}
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					addTextSeries(null, "But there is only one way to create true order.",rodriguez);
					advanceSequence();
				}
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					addTextSeries(null, "And that is why I need your help.",rodriguez);
					advanceSequence();
				}
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					addTextSeries(null, "For now, that is all I shall reveal.",rodriguez);
					advanceSequence();
				}
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					addTextSeries(null, "Until we meet again, be a little more careful.",rodriguez);
					advanceSequence();
				}
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					addTextSeries(null, "There is danger lurking around every corner.",rodriguez);
					advanceSequence();
				}
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					addTextSeries(null, "I hope to see you again.",rodriguez);
					runRodriguezAway();
				}
				
				if(sequencePart == 100 && ((rodriguez.getAllCommands()!=null && (rodriguez.getAllCommands().size() == 0)) || !rodriguez.isOnScreen())) {
					rodriguez.destroy();
					stop();
				}
			}
			
			
			// Fernando CHOICE
			if(choiceIs("Run to Fernando")) {
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					
					player.getPlayer().moveTo(fernando.getIntX() + 30, fernando.getIntY());
					
					// Set the next text and advance it.
					advanceSequence();
				}
				
				if(isSequence(numIfs++) && !player.getPlayer().isMoving() && goNextTextSeries()) {
					farmTomb.zoneFog.fadeTo(1f, 1);
					addTextSeries(null, "Stand still and be very quiet.",fernando);
					rodriguez.destroy();
					
					waitFor(2f);
					System.out.println("here1");
					
					// Set the next text and advance it.
					advanceSequence();
				}
				
				if(isSequence(numIfs++)) {
					System.out.println("here2");
					sound bell = new sound(wolfless.scream);
					bell.start();
					waitFor(6f);
					advanceSequence();
					
				}
				
				if(isSequence(numIfs++)) {
					addTextSeries(null, "I think the coast is clear.",fernando);
					advanceSequence();
				}
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					farmTomb.zoneFog.fadeTo(0.3f, 1);
					music m = new music(farmTomb.zoneMusic);
					addTextSeries(null, "An old friend of our's watches over this place.",fernando);
					advanceSequence();
				}
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					addTextSeries(null, "He's gone mad with power.",fernando);
					advanceSequence();
				}
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					addTextSeries(null, "This place is crawling with his demons.",fernando);
					advanceSequence();
				}
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					addTextSeries(null, "It's quite phenomenal, but sad nonetheless.",fernando);
					advanceSequence();
				}
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					addTextSeries(null, "He realized the world wasn't what he thought it was.",fernando);
					advanceSequence();
				}
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					addTextSeries(null, "And he tried to fix it, like we all do.",fernando);
					advanceSequence();
				}
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					addTextSeries(null, "But there's only two people that can fix everything.",fernando);
					advanceSequence();
				}
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					addTextSeries(null, "And that's where you come in.",fernando);
					advanceSequence();
				}
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					addTextSeries(null, "For now, that's all you can know.",fernando);
					advanceSequence();
				}
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					addTextSeries(null, "Until we meet again, stop dying so much.",fernando);
					advanceSequence();
				}
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					addTextSeries(null, "It's not good for the heart.",fernando);
					advanceSequence();
				}
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					addTextSeries(null, "See you soon.",fernando);
					runFernandoAway();
				}
				
				if(sequencePart == 100 && ((fernando.getAllCommands()!=null && (fernando.getAllCommands().size() == 0)) || !fernando.isOnScreen())) {
					fernando.destroy();
					stop();
				}
			}
		}
		
	}
	
	// Run Farlsworth away
	public void runFernandoAway() {
		sequencePart = 100;
		fernando.setMoveSpeed(5f);
		commandList commands = new commandList();
		commands.add(new moveCommand(fernando.getIntX() - 100,fernando.getIntY()));
		commands.add(new jumpCommand());
		commands.add(new moveCommand(fernando.getIntX() - 1000,fernando.getIntY()));
		fernando.doCommandsOnce(commands);
		interactSequence.getTextSeries().setEnd();
		interactSequence.setLocked(false);
		interactSequence.setUnescapable(false);
		
		// Set it to be completed as soon as he runs
		cinematicCompleted.setCompleted(true);
		
		// Save by default.
		saveState.setQuiet(true);
		saveState.createSaveState();
		saveState.setQuiet(false);
	}
	
	// Run Farlsworth away
	public void runRodriguezAway() {
		sequencePart = 100;
		rodriguez.setMoveSpeed(5f);
		commandList commands = new commandList();
		commands.add(new moveCommand(rodriguez.getIntX() + 100,rodriguez.getIntY()));
		commands.add(new jumpCommand());
		commands.add(new moveCommand(rodriguez.getIntX() + 400,rodriguez.getIntY()));
		rodriguez.doCommandsOnce(commands);
		interactSequence.getTextSeries().setEnd();
		interactSequence.setLocked(false);
		interactSequence.setUnescapable(false);
		
		// Set it to be completed as soon as he runs
		cinematicCompleted.setCompleted(true);
		
		// Save by default.
		saveState.setQuiet(true);
		saveState.createSaveState();
		saveState.setQuiet(false);
	}
	
	boolean bellTolled = false;
	long bellTollStart = 0;

	@Override
	public event isCompleted() {
		return isCompleted;
	}
	
}