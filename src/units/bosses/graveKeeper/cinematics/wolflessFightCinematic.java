package units.bosses.graveKeeper.cinematics;

import java.lang.invoke.MethodHandles;

import cinematics.cinematic;
import interactions.event;
import interactions.interactBox;
import interactions.textSeries;
import sounds.sound;
import units.player;
import units.bosses.wolfless.wolfless;
import units.characters.farlsworth.farlsworth;
import units.characters.farmer.cinematics.farmerIntroCinematic;
import units.unitCommands.commandList;
import units.unitCommands.commands.moveCommand;
import zones.farmTomb.farmTomb;

public class wolflessFightCinematic extends cinematic {
	
	// Event
	public static event isCompleted = new event(MethodHandles.lookup().lookupClass().getName() + "isCompleted");
	
	public wolflessFightCinematic() {
		super("wolflessFightCinematic");
	}
	
	units.bosses.graveKeeper.graveKeeper graveKeeper = new units.bosses.graveKeeper.graveKeeper(Integer.MIN_VALUE, Integer.MIN_VALUE);

	@Override
	public void play() {
	    
	    // Create interactSequence (first thing he says to you)
		waitFor(4f);
	}

	@Override
	public void finish() {
	}

	@Override
	public void updateCinematic() {
		
		textSeries s;
		int numIfs = 0;
		if(isSequence(numIfs++)) {
		    
		    // Lock the player's movement.
		    player.getPlayer().stopMove("all");
		    
		    s = new textSeries(null, "How DARE I let them CORRUPT me?");
		    interactSequence = new interactBox(s, graveKeeper);
			interactSequence.setUnescapable(true);
		    interactSequence.toggleDisplay();
		    

			
			// Set the next text and advance it.
			advanceSequence();
		}
		if(isSequence(numIfs++) && goNextTextSeries()) {
			
			// Set the next text and advance it.
			farmTomb.zoneFog.fadeTo(0.4f, 1);
			addTextSeries(null, "I don't HAVE to be thinking these thoughts.", graveKeeper);
			advanceSequence();
		}
		
		if(isSequence(numIfs++) && goNextTextSeries()) {
			
			// Set the next text and advance it.
			addTextSeries(null, "I just have to KEEP digging.", graveKeeper);
			advanceSequence();
		}
		
		if(isSequence(numIfs++) && goNextTextSeries()) {
			
			// Set the next text and advance it.
			farmTomb.zoneFog.fadeTo(0.5f, 1);
			addTextSeries(null, "I can never stop DIGGING.", graveKeeper);
			advanceSequence();
		}
		
		if(isSequence(numIfs++) && goNextTextSeries()) {
			
			// Set the next text and advance it.
			addTextSeries(null, "But you WON'T stop SCREAMING at me.", graveKeeper);
			advanceSequence();
		}
		
		if(isSequence(numIfs++) && goNextTextSeries()) {
			
			// Set the next text and advance it.
			farmTomb.zoneFog.fadeTo(0.6f, 1);
			addTextSeries(null, "Even though I am the ONLY one who REMEMBERS YOU.", graveKeeper);
			advanceSequence();
		}
		
		if(isSequence(numIfs++) && goNextTextSeries()) {
			
			// Set the next text and advance it.
			addTextSeries(null, "WHY ARE YOU MAKING IT SO HARD!?", graveKeeper);
			advanceSequence();
		}
		
		if(isSequence(numIfs++) && goNextTextSeries()) {
			
			// Set the next text and advance it.
			addTextSeries(null, "I'm sorry. I'M SO SORRY. WE did this to you.", graveKeeper);
			farmTomb.zoneFog.fadeTo(0.7f, 1);
			advanceSequence();
		}
		
		if(isSequence(numIfs++) && goNextTextSeries()) {
			
			// Set the next text and advance it.
			addTextSeries(null, "No, no, NO. NO, NO, NO NO NO NO!", graveKeeper);
			advanceSequence();
		}
		
		if(isSequence(numIfs++) && goNextTextSeries()) {
			
			// Set the next text and advance it.
			addTextSeries(null, "THEY DID IT. THEY DID IT! NOT ME.", graveKeeper);
			farmTomb.zoneFog.fadeTo(0.8f, 1);
			advanceSequence();
		}
		
		if(isSequence(numIfs++) && goNextTextSeries()) {
			
			// Set the next text and advance it.
			addTextSeries(null, "I HAVE TO DIG. I JUST HAVE TO KEEP DIGGING.", graveKeeper);
			advanceSequence();
		}
		
		if(isSequence(numIfs++) && goNextTextSeries()) {
			
			// Set the next text and advance it.
			addTextSeries(null, "WHY YOU WON'T STOP SCREAMING? YOU'RE INSIDE MY HEAD.", graveKeeper);
			farmTomb.zoneFog.fadeTo(0.9f, 1);
			advanceSequence();
		}
		
		if(isSequence(numIfs++) && goNextTextSeries()) {
			
			// Set the next text and advance it.
			addTextSeries(null, "YOU'RE INSIDE MY HEAD. Get OUT. GET OUT!", graveKeeper);
			advanceSequence();
		}
		
		if(isSequence(numIfs++) && goNextTextSeries()) {
			
			// Set the next text and advance it.
			farmTomb.zoneFog.fadeTo(1f, 1);
			addTextSeries(null, "PLEASE, please... just LEAVE ME ALONE.", graveKeeper);
			advanceSequence();
		}
		
		if(isSequence(numIfs++) && goNextTextSeries()) {
			
			// Set the next text and advance it.
			farmTomb.zoneFog.fadeTo(1f, 1);
			addTextSeries(null, "GET OUT OF MY HEAD.", graveKeeper);
			advanceSequence();
		}
		
		if(isSequence(numIfs++) && goNextTextSeries()) {
			// Chime
			sound sound = new sound(wolfless.howl);
			sound.start();
			
			// Wait a bit.
			waitFor(4f);
			advanceSequence();
		}
		
		// Reveal boss.
		if(isSequence(numIfs++) && goNextTextSeries()) {
			
			// Create elevator and set fog.
			if(interactSequence != null) {
				interactSequence.setUnescapable(false);
				interactSequence.toggleDisplay();
			}
			farmTomb.createShadowBossFightAroundPlayer();
			farmTomb.zoneFog.fadeTo(.3f, .2f);
			
			// Wait for next chime.
			waitFor(1f);
			advanceSequence();
		}
		
		// Start boss
		if(isSequence(numIfs++)) {
			farmTomb.startBossFight();
			advanceSequence();
		}
	}
	
	@Override
	public event isCompleted() {
		return isCompleted;
	}
	
}