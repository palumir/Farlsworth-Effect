package units.characters.farmer.cinematics;

import java.lang.invoke.MethodHandles;

import cinematics.cinematic;
import interactions.event;
import interactions.interactBox;
import interactions.textSeries;
import items.keys.farmGateKey;
import items.keys.farmKey;
import units.player;
import units.characters.farlsworth.farlsworth;
import units.unitCommands.commandList;
import units.unitCommands.commands.moveCommand;
import utilities.saveState;
import zones.sheepFarm.subZones.sheepFarm;

public class farmerIntroCinematic extends cinematic {
	
	// Event
	public static event isCompleted = new event(MethodHandles.lookup().lookupClass().getName() + "isCompleted");
	public static event playerPressedNoABunch = new event("farmerIntroCinematicPlayerPresseNoABunch");

	public farmerIntroCinematic() {
		super("farmerIntroCinematic");
	}
	
	units.characters.farmer.farmer farmer;
	farlsworth farlsworth;

	@Override
	public void play() {
		
	    // Lock the player's movement.
	    player.getPlayer().stopMove("all");
	    
	    // Create interactSequence (first thing he says to you)
	    textSeries s = new textSeries(null, "Holy heck, where did you come from!?");
	    farmer = units.characters.farmer.farmer.farmer;
	    farlsworth = units.characters.farlsworth.farlsworth.farlsworth;
	    farmer.faceTowardPlayer();
	    
	    interactSequence = new interactBox(s, farmer);
		interactSequence.setUnescapable(true);
	    interactSequence.toggleDisplay();
	}

	@Override
	public void finish() {
	}
	
	
	// Boolean yesHasBeenSaid
	boolean yesHasBeenSaid = false;

	@Override
	public void updateCinematic() {
		
		textSeries s;
		int numIfs = 0;
		
		
		if(farmer.getIntY() > player.getPlayer().getIntY()) {
			

			player.getPlayer().setFacingDirection("Down");
		}
		
		if(isSequence(numIfs++) && goNextTextSeries()) {
			
			// Move right.
			addTextSeries("'From over the bridge'", null,farmer,"Order");
			addTextSeries("'What are you doing?'", null,farmer,"Chaos");
			interactSequence.goToNext();
			
			// Set the next text and advance it.
			advanceSequence();
		}
		
		// From over the bridge? CHOICE
		if(goNextTextSeries() && choiceIs("'From over the bridge'")) {
					
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				addTextSeries(null, "There's a bridge in my farm?",farmer);
				
				// Set the next text and advance it.
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				addTextSeries(null, "Holy knockers! I thought that was a boat.",farmer);

				farmer.setMoveSpeed(3.5f);
				farmer.moveTo(farmer.getIntX(), farmer.getIntY()+100);
				// Set the next text and advance it.
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				addTextSeries(null, "That's neat.",farmer);

				farmer.setFacingDirection("Up");
				// Set the next text and advance it.
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				addTextSeries(null, "I guess I need to cancel the boating trip.",farmer);
				
				// Set the next text and advance it.
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				addTextSeries(null, "Farlsworth is going to be so disappointed.",farmer);
				
				// Set the next text and advance it.
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				addTextSeries(null, "He's my pet sheep. And my best friend.",farmer);
				
				// Set the next text and advance it.
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				addTextSeries(null, "Actually, it's about that time of day again.",farmer);
				
				// Set the next text and advance it.
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				addTextSeries(null, "His wool is ready to be collected!",farmer);
				
				// Set the next text and advance it.
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				addTextSeries(null, "Could you collect his wool for me?",farmer);
				
				// Set the next text and advance it.
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				addTextSeries(null, "I have a pepperoni pizza pie in the oven, so I can't.",farmer);
				
				// Set the next text and advance it.
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				// Move right.
				addTextSeries("'Yes'", null,farmer);
				addTextSeries("'No'", null,farmer);
				interactSequence.goToNext();
				
				// Set the next text and advance it.
				advanceSequence();
			}
			
			// 'yes' CHOICE
			if(goNextTextSeries() && choiceIs("'Yes'")) {
						
				if(sequencePart >= numIfs++ && !yesHasBeenSaid && goNextTextSeries()) {
					
					sequencePart = numIfs++-1;
					addTextSeries(null, "Alright. Here's the key to his pen.",farmer);
					farmKey f = new farmKey(0,0);
					f.pickUp();
					yesHasBeenSaid = true;
					
					// Set the next text and advance it.
					advanceSequence();
				}
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					
					addTextSeries(null, "Farlsworth's pen is to the far East. Good luck.",farmer);
					
					normalQuestGo();
					// Set the next text and advance it.
					advanceSequence();
				}
			}
			
			// 'no' CHOICE
			if(goNextTextSeries() && choiceIs("'No'") && !yesHasBeenSaid) {
						
				if(isSequence(numIfs++) && goNextTextSeries()) {
					
					addTextSeries(null, "Please?",farmer);
					
					// Set the next text and advance it.
					advanceSequence();
				}
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					
					// Move right.
					addTextSeries("'Yes'", null,farmer);
					addTextSeries("'No'", null,farmer);
					interactSequence.goToNext();
					
					// Set the next text and advance it.
					advanceSequence();
				}
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					
					addTextSeries(null, "Come on. My pie's going to burn!",farmer);
					
					// Set the next text and advance it.
					advanceSequence();
				}
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					
					// Move right.
					addTextSeries("'Yes'", null,farmer);
					addTextSeries("'No'", null,farmer);
					interactSequence.goToNext();
					
					// Set the next text and advance it.
					advanceSequence();
				}
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					
					addTextSeries(null, "Nobody likes a burnt pizza pie.",farmer);
					
					// Set the next text and advance it.
					advanceSequence();
				}
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					
					// Move right.
					addTextSeries("'Yes'", null,farmer);
					addTextSeries("'No'", null,farmer);
					interactSequence.goToNext();
					
					// Set the next text and advance it.
					advanceSequence();
				}
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					
					addTextSeries(null, "Why not?",farmer);
					
					// Set the next text and advance it.
					advanceSequence();
				}
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					
					// Move right.
					addTextSeries("'Yes'", null,farmer);
					addTextSeries("'No'", null,farmer);
					interactSequence.goToNext();
					
					// Set the next text and advance it.
					advanceSequence();
				}
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					
					addTextSeries(null, "Wait, what?",farmer);
					
					// Set the next text and advance it.
					advanceSequence();
				}
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					
					// Move right.
					addTextSeries("'Yes'", null,farmer);
					addTextSeries("'No'", null,farmer);
					interactSequence.goToNext();
					
					// Set the next text and advance it.
					advanceSequence();
				}
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					
					addTextSeries(null, "Oh, okay, I wasn't sure if you said yes.",farmer);
					
					// Set the next text and advance it.
					advanceSequence();
				}
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					
					// Move right.
					addTextSeries("'Yes'", null,farmer);
					addTextSeries("'No'", null,farmer);
					interactSequence.goToNext();
					
					// Set the next text and advance it.
					advanceSequence();
				}
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					
					addTextSeries(null, "Wait, did you say yes that time?",farmer);
					
					// Set the next text and advance it.
					advanceSequence();
				}
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					
					// Move right.
					addTextSeries("'No'", null,farmer);
					addTextSeries("'Yes'", null,farmer);
					interactSequence.goToNext();
					
					// Set the next text and advance it.
					advanceSequence();
				}
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					
					addTextSeries(null, "Okay, I'll go grab it then.",farmer);
					farmerGoGrabWool();
					
					// Set the next text and advance it.
					advanceSequence();
				}
			}
		}
		
		// What are you doing? CHOICE
		if(goNextTextSeries() && choiceIs("'What are you doing?'")) {
					
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				addTextSeries(null, "I can't figure out which building is my house.",farmer);
				farmer.moveTo(farmer.getIntX(), farmer.getIntY()-20);
				
				// Set the next text and advance it.
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				addTextSeries(null, "I know it's definitely not the middle one.",farmer);

				// Set the next text and advance it.
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				addTextSeries(null, "That's a squirrel house.",farmer);
				
				// Set the next text and advance it.
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				addTextSeries(null, "So by process of elimination my house is either...",farmer);
				
				// Set the next text and advance it.
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				addTextSeries(null, "The sheep house on the left.",farmer);
				farmer.moveTo(farmer.getIntX()-20, farmer.getIntY());
				
				// Set the next text and advance it.
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				addTextSeries(null, "Or my house on the right.",farmer);
				farmer.moveTo(farmer.getIntX()+20, farmer.getIntY());
				
				// Set the next text and advance it.
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				addTextSeries(null, "But I don't know which one.",farmer);
				farmer.setFacingDirection("Down");
				
				// Set the next text and advance it.
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				addTextSeries(null, "Farlsworth usually figures these things out for me.",farmer);
				
				farmer.moveTo(farmer.getIntX(), farmer.getIntY()+20);
				// Set the next text and advance it.
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				addTextSeries(null, "He's my pet sheep. And my best friend.",farmer);
				
				// Set the next text and advance it.
				advanceSequence();
			}

			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				addTextSeries(null, "Actually, it's about that time of day again.",farmer);
				
				// Set the next text and advance it.
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				addTextSeries(null, "His wool is ready to be collected!",farmer);
				
				// Set the next text and advance it.
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				addTextSeries(null, "Could you collect his wool for me?",farmer);
				
				// Set the next text and advance it.
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				addTextSeries(null, "I have a pepperoni pizza pie in the oven, so I can't.",farmer);
				
				// Set the next text and advance it.
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				// Move right.
				addTextSeries("'Yes'", null,farmer);
				addTextSeries("'No'", null,farmer);
				interactSequence.goToNext();
				
				// Set the next text and advance it.
				advanceSequence();
			}
			
			// 'yes' CHOICE
			if(goNextTextSeries() && choiceIs("'Yes'")) {
						
				if(sequencePart >= numIfs++ && !yesHasBeenSaid && goNextTextSeries()) {
					
					sequencePart = numIfs++-1;
					addTextSeries(null, "Alright. Here's the key to his pen.",farmer);
					farmKey f = new farmKey(0,0);
					f.pickUp();
					yesHasBeenSaid = true;
					
					// Set the next text and advance it.
					advanceSequence();
				}
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					
					addTextSeries(null, "Farlsworth's pen is to the far East. Good luck.",farmer);
					
					normalQuestGo();
					// Set the next text and advance it.
					advanceSequence();
				}
			}
			
			// 'no' CHOICE
			if(goNextTextSeries() && choiceIs("'No'") && !yesHasBeenSaid) {
						
				if(isSequence(numIfs++) && goNextTextSeries()) {
					
					addTextSeries(null, "Please?",farmer);
					
					// Set the next text and advance it.
					advanceSequence();
				}
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					
					// Move right.
					addTextSeries("'Yes'", null,farmer);
					addTextSeries("'No'", null,farmer);
					interactSequence.goToNext();
					
					// Set the next text and advance it.
					advanceSequence();
				}
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					
					addTextSeries(null, "Come on. My pie's going to burn!",farmer);
					
					// Set the next text and advance it.
					advanceSequence();
				}
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					
					// Move right.
					addTextSeries("'Yes'", null,farmer);
					addTextSeries("'No'", null,farmer);
					interactSequence.goToNext();
					
					// Set the next text and advance it.
					advanceSequence();
				}
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					
					addTextSeries(null, "Nobody likes a burnt pizza pie.",farmer);
					
					// Set the next text and advance it.
					advanceSequence();
				}
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					
					// Move right.
					addTextSeries("'Yes'", null,farmer);
					addTextSeries("'No'", null,farmer);
					interactSequence.goToNext();
					
					// Set the next text and advance it.
					advanceSequence();
				}
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					
					addTextSeries(null, "Why not?",farmer);
					
					// Set the next text and advance it.
					advanceSequence();
				}
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					
					// Move right.
					addTextSeries("'Yes'", null,farmer);
					addTextSeries("'No'", null,farmer);
					interactSequence.goToNext();
					
					// Set the next text and advance it.
					advanceSequence();
				}
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					
					addTextSeries(null, "Wait... what?",farmer);
					
					// Set the next text and advance it.
					advanceSequence();
				}
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					
					// Move right.
					addTextSeries("'Yes'", null,farmer);
					addTextSeries("'No'", null,farmer);
					interactSequence.goToNext();
					
					// Set the next text and advance it.
					advanceSequence();
				}
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					
					addTextSeries(null, "Oh, okay, I wasn't sure if you said yes.",farmer);
					
					// Set the next text and advance it.
					advanceSequence();
				}
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					
					// Move right.
					addTextSeries("'Yes'", null,farmer);
					addTextSeries("'No'", null,farmer);
					interactSequence.goToNext();
					
					// Set the next text and advance it.
					advanceSequence();
				}
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					
					addTextSeries(null, "Wait, did you say yes that time?",farmer);
					
					// Set the next text and advance it.
					advanceSequence();
				}
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					
					// Move right.
					addTextSeries("'No'", null,farmer);
					addTextSeries("'Yes'", null,farmer);
					interactSequence.goToNext();
					
					// Set the next text and advance it.
					advanceSequence();
				}
				
				if(isSequence(numIfs++) && goNextTextSeries()) {
					
					addTextSeries(null, "Okay, I'll go grab it then.",farmer);
					farmerGoGrabWool();
					
					// Set the next text and advance it.
					advanceSequence();
				}
			}
		}
		
		if(sequencePart >= 1000 && farmer.getAllCommands()!=null && (farmer.getAllCommands().size() == 0)) {
			farmer.setFacingDirection("Right");
			farlsworth.setFacingDirection("Left");
			stop();
		}
		
	}
	
	public void normalQuestGo() {
		farmer.setFacingDirection("Down");
		interactSequence.getTextSeries().setEnd();
		interactSequence.setLocked(false);
		interactSequence.setUnescapable(false);
		stop();
	}
	
	public void farmerGoGrabWool() {
		save = false;
		farmer.setMoveSpeed(3.5f);
		farmer.setCollisionOn(false);
		sheepFarm.farlsworthGate.forceOpen();
		commandList commands = new commandList();
		commands.add(new moveCommand(-636,-10));
		commands.add(new moveCommand(420,-10));
		commands.add(new moveCommand(260,-394));
		farmer.doCommandsOnce(commands);
		interactSequence.getTextSeries().setEnd();
		interactSequence.setLocked(false);
		interactSequence.setUnescapable(false);
		
		// Set it to be completed as soon as he runs, instead of when he's teleported to flower farm.
		cinematicCompleted.setCompleted(true);
		playerPressedNoABunch.setCompleted(true);
		
		// Save by default.
		saveState.setQuiet(true);
		saveState.createSaveState();
		saveState.setQuiet(false);
		sequencePart = 1000;
	}
	
	@Override
	public event isCompleted() {
		return isCompleted;
	}
	
}