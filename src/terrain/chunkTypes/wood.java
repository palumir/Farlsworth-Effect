package terrain.chunkTypes;

import terrain.chunk;
import terrain.generalChunkType;
import terrain.groundTile;
import units.player;
import utilities.time;
import zones.zone;
import effects.buff;
import effects.buffs.slideEffect;

public class wood extends groundTile {
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default name.
	private static String DEFAULT_CHUNK_NAME = "wood";
	
	// Tile sprite stuff
	private static String DEFAULT_CHUNK_SPRITESHEET = "images/terrain/" + DEFAULT_CHUNK_NAME + ".png";
	
	// Dimensions
	public static int DEFAULT_CHUNK_WIDTH = 32;
	public static int DEFAULT_CHUNK_HEIGHT = 32;
	
	// The actual type.
	private static generalChunkType typeReference = new generalChunkType(DEFAULT_CHUNK_NAME, DEFAULT_CHUNK_SPRITESHEET, DEFAULT_CHUNK_WIDTH, DEFAULT_CHUNK_HEIGHT);  
	
	///////////////
	/// METHODS ///
	///////////////
	// Constructor
	public wood(int newX, int newY) {
		super(typeReference, newX, newY,0);
		this.setPassable(true);
	}
	
	// Constructor
	public wood(int newX, int newY, int n) {
		super(typeReference, newX, newY,n);
		this.setPassable(true);
	}
	
	// TODO: TESTING SLIDE EFFECT 
	@Override
	public void update() {
		if(player.getPlayer().isWithin(this.getIntX(),this.getIntY(),this.getIntX()+this.getWidth(),this.getIntY()+this.getHeight())) {
			boolean containsSlide = false;
			for(int i = 0; i < player.getPlayer().getMovementBuffs().size(); i++) {
				buff b = player.getPlayer().getMovementBuffs().get(i);
				if(b instanceof slideEffect) containsSlide = true;
			}
			if(!containsSlide) { 
				slideEffect s = new slideEffect(player.getPlayer()); 
			}
		}
	}

		// Create function
	public static chunk createChunk(int newX, int newY, int i) {
		if(!zone.loadedOnce) {
			chunk t = new wood(newX,newY);
			t.setReloadObject(false);
			return t;
		}
		else {
			return null;
		}
	}
}
