package units.bosses.rodriguez.cinematics;

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

public class farmTombCinematic extends cinematic {
	
	// Event
	public static event isCompleted = new event(MethodHandles.lookup().lookupClass().getName() + "isCompleted");

	public farmTombCinematic() {
		super("farmTombCinematic");
	}
	
	// Scene music
	private String sceneMusic = "sounds/music/farmLand/unused/newSong.wav";
	
	rodriguez rodriguez;
	fernando fernando;

	@Override
	public void play() {
		
		// Spawn the boys.
		rodriguez = new rodriguez(7400,1796);
	    fernando = new fernando(6662,1796);
	    
	    // Lock the player's movement.
	    player.getPlayer().stopMove("all");
	    
	    // Create interactSequence (first thing he says to you)
	    textSeries s = new textSeries(null, "Hello, my friend.");
	    
	    // Move left.
	 	rodriguez.moveTo(rodriguez.getIntX() + -109, rodriguez.getIntY());
	    interactSequence = new interactBox(s, rodriguez);
	    interactSequence.toggleDisplay();
	    music.currMusic.fadeOut(3f);
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
			addTextSeries(null, "But first, I need to know if you understand.",rodriguez);
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
			addTextSeries(null, "But that's not what I meant.",rodriguez);
			
			advanceSequence();
		}
		
		if(isSequence(numIfs++) && goNextTextSeries()) {
			
			// Set the next text and advance it.
			addTextSeries(null, "I can help her understand her power.",rodriguez);
			
			advanceSequence();
		}
		
		if(isSequence(numIfs++) && goNextTextSeries() && !fernando.isMoving()) {
			
			addTextSeries(null, "Oh yeah?",fernando);
			player.getPlayer().setFacingDirection("Left");
			
			// Set the next text and advance it.
			advanceSequence();
		}
		
		
		if(isSequence(numIfs++) && goNextTextSeries() && !fernando.isMoving()) {
			
			addTextSeries(null, "You can barely comprehend your own power, let alone control it.",fernando);
			
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
			addTextSeries("'Who are you two?'", "Ah, let me introduce to you ... ",fernando);
			addTextSeries("Do nothing", "Missing the bigger picture, as usual.",rodriguez);
			interactSequence.goToNext();
			player.getPlayer().setFacingDirection("Down");
			
			// Set the next text and advance it.
			advanceSequence();
		}
		
		// Facing direction stuff.
		if(isSequence(numIfs) && !interactSequence.isButtonMode() && choiceIs("Do nothing")) {
			player.getPlayer().setFacingDirection("Right");
		}
		
		// Facing direction stuff.
		if(isSequence(numIfs) && !interactSequence.isButtonMode() && choiceIs("'Who are you two?'")) {
			player.getPlayer().setFacingDirection("Left");
		}
		
		// Who are you? CHOICE
		if(goNextTextSeries() && choiceIs("'Who are you two?'")) {
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				addTextSeries(null, "Ah, allow me to introduce you to ... ",fernando);
				
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
				addTextSeries(null, "And I am going to restore order to the world.",rodriguez);
				
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
				addTextSeries(null, "By whatever means necessary, of course. <attacks>",rodriguez);
				player.getPlayer().setFacingDirection("Right");
				// Set the next text and advance it.
				advanceSequence();
			}
			
			
		}
		
		// Do nothing CHOICE
		if(goNextTextSeries() && choiceIs("Do nothing")) {
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				addTextSeries(null, "She has the right to live in a world that is stable.",rodriguez);
				
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
				
				addTextSeries(null, "You want to remove from humanity the thing that makes us human.",fernando);
				
				// Set the next text and advance it.
				advanceSequence();
			}
			
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				addTextSeries(null, "And I could never let you do that.",fernando);
				
				// Set the next text and advance it.
				advanceSequence();
			}

			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				addTextSeries(null, "Surely if every choice is perfect, then removing choice does nothing.",rodriguez);
				player.getPlayer().setFacingDirection("Right");
				// Set the next text and advance it.
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				addTextSeries(null, "Is that not what we work toward anyway?",rodriguez);
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
			}
		}
		
	}

	@Override
	public event isCompleted() {
		return null;
	}
	
}