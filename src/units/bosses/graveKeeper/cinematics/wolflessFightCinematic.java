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
import utilities.saveState;
import zones.farmTomb.subZones.farmTomb;

public class wolflessFightCinematic extends cinematic {
	
	// Event
	public static event isCompleted = new event(MethodHandles.lookup().lookupClass().getName() + "isCompleted");
	
	public wolflessFightCinematic() {
		super("wolflessFightCinematic");
		setCompleteOnce(false);
	}
	
	units.bosses.graveKeeper.graveKeeper graveKeeper = new units.bosses.graveKeeper.graveKeeper(Integer.MIN_VALUE, Integer.MIN_VALUE);

	@Override
	public void play() {
	}

	@Override
	public void finish() {
	}
	
	// Start boss!
	public boolean startBossImmediately = false;

	@Override
	public void updateCinematic() {
		
		textSeries s;
		int numIfs = 0;
		
		if(!startBossImmediately) {
			if(isSequence(numIfs++)) {
				waitFor(4f);
				advanceSequence();
			}
			if(isSequence(numIfs++)) {
			    
			    // Lock the player's movement.
			    player.getPlayer().stopMove("all");
			    
			    s = new textSeries(null, "DO NOT let them CORRUPT you.");
			    interactSequence = new interactBox(s, graveKeeper);
				interactSequence.setUnescapable(true);
			    interactSequence.toggleDisplay();

				// Set the next text and advance it.
				advanceSequence();
			}
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				// Set the next text and advance it.
				farmTomb.zoneFog.fadeTo(0.4f, 1);
				addTextSeries(null, "YOU don't have to DECIDE.", graveKeeper);
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				// Set the next text and advance it.
				addTextSeries(null, "YOU can just DIG.", graveKeeper);
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				// Set the next text and advance it.
				farmTomb.zoneFog.fadeTo(0.5f, 1);
				addTextSeries(null, "Just DIG and never stop DIGGING.", graveKeeper);
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				// Set the next text and advance it.
				addTextSeries(null, "WHY...? PLEASE stop SCREAMING at me.", graveKeeper);
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				// Set the next text and advance it.
				farmTomb.zoneFog.fadeTo(0.6f, 1);
				addTextSeries(null, "I AM TRYING TO SPEAK WITH HER.", graveKeeper);
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				// Set the next text and advance it.
				addTextSeries(null, "WHY ARE YOU MAKING THIS SO HARD!?", graveKeeper);
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				// Set the next text and advance it.
				addTextSeries(null, "I'm sorry. I'm so sorry... WE did this to you.", graveKeeper);
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
				addTextSeries(null, "WHY YOU WON'T STOP SCREAMING? I'M TIRED OF HEARING YOU.", graveKeeper);
				farmTomb.zoneFog.fadeTo(0.9f, 1);
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				// Set the next text and advance it.
				addTextSeries(null, "BE GONE. Get OUT. GET OUT OF MY HEAD!", graveKeeper);
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
				saveState.setQuiet(true);
				saveState.createSaveState();
				saveState.setQuiet(false);
				stop();
			}
		}
		
		else {
			
			// Reveal boss.
			if(isSequence(numIfs++)) {
				
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
				saveState.setQuiet(true);
				saveState.createSaveState();
				saveState.setQuiet(false);
				stop();
			}
		}
		
	}
	
	@Override
	public event isCompleted() {
		return isCompleted;
	}
	
}