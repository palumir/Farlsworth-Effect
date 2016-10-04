package terrain.chunkTypes;

import drawing.drawnObject;
import effects.buff;
import effects.buffs.slideEffect;
import modes.mode;
import terrain.chunk;
import terrain.generalChunkType;
import terrain.groundTile;
import units.player;
import utilities.utility;
import zones.zone;

public class mud extends groundTile {
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default name.
	private static String DEFAULT_CHUNK_NAME = "mud";
	
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
	public mud(int newX, int newY) {
		super(typeReference, newX, newY,utility.RNG.nextInt(typeReference.getChunkTypeSpriteSheet().getAnimation(0).size()));
		this.setPassable(true);
	}
	
	// Constructor
	public mud(int newX, int newY, int n) {
		super(typeReference, newX, newY,utility.RNG.nextInt(typeReference.getChunkTypeSpriteSheet().getAnimation(0).size()));
		this.setPassable(true);
	}

	@Override
	public void update() {
		if(player.getPlayer().isWithin(this.getIntX(),this.getIntY(),this.getIntX()+this.getWidth(),this.getIntY()+this.getHeight())
				|| (mode.getCurrentMode().equals("platformer") && player.getPlayer().isWithin(this.getIntX(),this.getIntY()-10,this.getIntX()+this.getWidth(),this.getIntY()+this.getHeight()))) {
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
	
	// Is on ground tile?
	public static boolean isOnNonMud(drawnObject u) {
		
		// If there's no impassable chunks
		if(groundTiles == null || groundTiles.size() == 0) return false;
		
		// Check if it collides in x or y position.
		boolean tX = false;
		
		// Get x interval on left. (where foundX < ourChunkX - largestChunkWidth)
		int i1 = 0;
		int L = 0;
		int R = groundTiles.size()-1;
		int T = u.getIntX() - largestChunkWidth;
		int Am = 0;
		while(true) {
			int m = (L+R)/2;
			Am = groundTiles.get(m).getIntX();
			if(Am < T) {
				if(m+1>R) {
					i1 = L;
					break;
				}
				L = m + 1;
			}
			else if(Am > T) {
				if(L > m - 1) {
					i1 = L;
					break;
				}
				R = m - 1;
			}
			else {
				i1 = m; // Found it exactly.
				break;
			}
		}
		
		// Get x interval on right. (where foundX > ourChunkX + ourChunk.width)
		int i2 = 0;
		L = 0;
		R = groundTiles.size()-1;
		T = u.getIntX() + u.getWidth();
		Am = 0;
		while(true) {
			int m = (L+R)/2;
			Am = groundTiles.get(m).getIntX();
			if(Am < T) {
				if(m+1>R) {
					i2 = R;
					break;
				}
				L = m + 1;
			}
			else if(Am > T) {
				if(L > m - 1) {
					i2 = R;
					break;
				}
				R = m - 1;
			}
			else {
				i2 = m; // Found it exactly.
				break;
			}
		}
		
		// Check between our interval
		for(;i1 <= i2; i1++) {
			chunk currChunk = groundTiles.get(i1);
			if(mode.getCurrentMode().equals("topDown")) if(u.collides(u.getIntX(), u.getIntY(),currChunk) && !(currChunk instanceof mud)) tX = true;
			
			// TODO: doesnt work for platformer
			if(mode.getCurrentMode().equals("platformer")) if(u.collides(u.getIntX(), u.getIntY()+10,currChunk) && !(currChunk instanceof mud)) tX = true;
		}
		
		if(tX) return true;
		else return false;
	}
	
	// Is on ground tile?
	public static boolean isOnMud(drawnObject u) {
		
		// If there's no impassable chunks
		if(groundTiles == null || groundTiles.size() == 0) return false;
		
		// Check if it collides in x or y position.
		boolean tX = false;
		
		// Get x interval on left. (where foundX < ourChunkX - largestChunkWidth)
		int i1 = 0;
		int L = 0;
		int R = groundTiles.size()-1;
		int T = u.getIntX() - largestChunkWidth;
		int Am = 0;
		while(true) {
			int m = (L+R)/2;
			Am = groundTiles.get(m).getIntX();
			if(Am < T) {
				if(m+1>R) {
					i1 = L;
					break;
				}
				L = m + 1;
			}
			else if(Am > T) {
				if(L > m - 1) {
					i1 = L;
					break;
				}
				R = m - 1;
			}
			else {
				i1 = m; // Found it exactly.
				break;
			}
		}
		
		// Get x interval on right. (where foundX > ourChunkX + ourChunk.width)
		int i2 = 0;
		L = 0;
		R = groundTiles.size()-1;
		T = u.getIntX() + u.getWidth();
		Am = 0;
		while(true) {
			int m = (L+R)/2;
			Am = groundTiles.get(m).getIntX();
			if(Am < T) {
				if(m+1>R) {
					i2 = R;
					break;
				}
				L = m + 1;
			}
			else if(Am > T) {
				if(L > m - 1) {
					i2 = R;
					break;
				}
				R = m - 1;
			}
			else {
				i2 = m; // Found it exactly.
				break;
			}
		}
		
		// Check between our interval
		for(;i1 <= i2; i1++) {
			chunk currChunk = groundTiles.get(i1);
			if(mode.getCurrentMode().equals("topDown")) if(u.collides(u.getIntX(), u.getIntY(),currChunk) && currChunk instanceof mud) tX = true;
			
			// TODO: doesnt work for platformer
			if(mode.getCurrentMode().equals("platformer")) if(u.collides(u.getIntX(), u.getIntY()+10,currChunk) && currChunk instanceof mud) tX = true;
		}
		
		if(tX) return true;
		else return false;
	}
	

		// Create function
	public static chunk createChunk(int newX, int newY, int i) {
		if(!zone.loadedOnce) {
			chunk t = new mud(newX,newY);
			t.setReloadObject(false);
			return t;
		}
		else {
			return null;
		}
	}
}
