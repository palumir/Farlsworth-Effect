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
	public static event isCompleted = event.createEvent(MethodHandles.lookup().lookupClass().getName() + "isCompleted");
	
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
				waitFor(2f);
				advanceSequence();
			}
			if(isSequence(numIfs++)) {
			    
			    // Lock the player's movement.
			    player.getPlayer().stopMove("all");
			    
			    s = new textSeries(null, "That's a good dog...");
			    interactSequence = new interactBox(s, graveKeeper);
				interactSequence.setUnescapable(true);
			    interactSequence.toggleDisplay();

				// Set the next text and advance it.
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				// Set the next text and advance it.
				addTextSeries(null, "Whose a big beautiful baby boy?", graveKeeper);
				farmTomb.zoneFog.fadeTo(0.5f, 1);
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				// Set the next text and advance it.
				farmTomb.zoneFog.fadeTo(0.6f, 1);
				addTextSeries(null, "NO. No growling!", graveKeeper);
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				// Set the next text and advance it.
				addTextSeries(null, "You'll get your dinner soon.", graveKeeper);
				farmTomb.zoneFog.fadeTo(0.7f, 1);
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				// Set the next text and advance it.
				farmTomb.zoneFog.fadeTo(0.8f, 1);
				addTextSeries(null, "Sit, boy.", graveKeeper);
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				// Set the next text and advance it.
				addTextSeries(null, "I told you to sit. Settle down, boy.", graveKeeper);
				farmTomb.zoneFog.fadeTo(0.9f, 1);
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				// Set the next text and advance it.
				addTextSeries(null, "Oh, is there somebody here?", graveKeeper);
				farmTomb.zoneFog.fadeTo(1f, 1);
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				// Set the next text and advance it.
				addTextSeries(null, "I hope this one gets out alive.", graveKeeper);
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				// Set the next text and advance it.
				addTextSeries(null, "But it might be too late.", graveKeeper);
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