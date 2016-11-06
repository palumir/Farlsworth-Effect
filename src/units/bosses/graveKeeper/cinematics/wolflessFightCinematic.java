package units.bosses.graveKeeper.cinematics;

import java.lang.invoke.MethodHandles;

import cinematics.cinematic;
import interactions.event;
import interactions.interactBox;
import interactions.textSeries;
import sounds.sound;
import units.player;
import units.bosses.wolfless.wolfless;
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
			    
			    s = new textSeries(null, "Wow!!! That's a good boy!!");
			    interactSequence = new interactBox(s, graveKeeper);
				interactSequence.setUnescapable(true);
			    interactSequence.toggleDisplay();

				// Set the next text and advance it.
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				// Set the next text and advance it.
				addTextSeries(null, "Whose a BIG BEAUTIFUL baby boy?", graveKeeper);
				farmTomb.zoneFog.fadeTo(0.5f, 1);
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				// Set the next text and advance it.
				farmTomb.zoneFog.fadeTo(0.6f, 1);
				addTextSeries(null, "NO. No growling at daddy!", graveKeeper);
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				// Set the next text and advance it.
				addTextSeries(null, "Are you a HUNGRY BOY??", graveKeeper);
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
				addTextSeries(null, "...", graveKeeper);
				sound s2 = new sound(wolfless.howl);
				farmTomb.zoneFog.fadeTo(0.9f, 1);
				s2.start();
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				// Set the next text and advance it.
				addTextSeries(null, "SIT DOWN OR I'LL TEAR OFF YOUR TOES!", graveKeeper);
				farmTomb.zoneFog.fadeTo(1f, 1);
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				// Set the next text and advance it.
				addTextSeries(null, "Huh?", graveKeeper);
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				// Set the next text and advance it.
				addTextSeries(null, "What is it, boy?", graveKeeper);
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				// Set the next text and advance it.
				addTextSeries(null, "Is there somebody here?", graveKeeper);
				farmTomb.zoneFog.fadeTo(1f, 1);
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				
				// Set the next text and advance it.
				addTextSeries(null, "Ah, looks like dinner is early!!!", graveKeeper);
				advanceSequence();
			}
			
			if(isSequence(numIfs++) && goNextTextSeries()) {
				// Set the next text and advance it.
				addTextSeries(null, "Dig in, boy.", graveKeeper);
				
				// Wait a bit.
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