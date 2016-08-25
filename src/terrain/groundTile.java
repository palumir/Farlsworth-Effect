package terrain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.CopyOnWriteArrayList;

import drawing.drawnObject;
import utilities.intTuple;
import zones.zone;

// Something to differentiate doodads from simple tiles.
public class groundTile extends chunk {
	
	// Width and height
	public static int DEFAULT_TILE_WIDTH = 32;
	public static int DEFAULT_TILE_HEIGHT = 32;
	
	// Gather a list of all groundTiles.
	public static CopyOnWriteArrayList<groundTile> groundTiles;

	// Constructor
	public groundTile(chunkType c, int newX, int newY, int i) {
		super(c, newX, newY, i, 0);
		
		// Add to our list of groundTiles.
		groundTiles.add(this);
		
		// If the zone is loaded already and we add a chunk, sort it into the list properly.
		//if(zone.getCurrentZone() != null && zone.getCurrentZone().isZoneLoaded()) {
			sortGroundTiles();
		//}
	}
	
	public static intTuple collidesWithOffMap(drawnObject u, int newX, int newY) {
		
		// If there's no impassable chunks
		if(groundTiles.size() == 0) return intTuple.emptyTuple;
		
		// Check if it collides in x or y position.
		boolean tXTopLeft = false;
		boolean tYTopLeft = false;
		boolean tXBottomRight = false;
		boolean tYBottomRight = false;
		
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
		T = newX + u.getWidth();
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
		
		
		// Check between our interval for top left
		for(int i = i1; i <= i2; i++) {
			chunk currChunk = groundTiles.get(i);
			if(currChunk.contains(newX, u.getIntY())) tXTopLeft = true;
			if(currChunk.contains(u.getIntX(), newY)) tYTopLeft = true;
		}
		
		// Check between our interval for bottom right
		for(int i = i1; i <= i2; i++) {
			chunk currChunk = groundTiles.get(i);
			if(currChunk.contains(newX + u.getWidth(), u.getIntY() + u.getHeight())) tXBottomRight = true;
			if(currChunk.contains(u.getIntX() + u.getWidth(), newY + u.getHeight())) tYBottomRight = true;
		}
		
		// Make an intTuple for the return.
		int txInt = 1; // 1 = disallow movement x
		int tyInt = 1; // 1 = disallow movement y
		if(tXTopLeft && tXBottomRight) txInt = 0;
		if(tYTopLeft && tYBottomRight) tyInt = 0;
		
		return new intTuple(txInt, tyInt);
	}
	
	// Is on ground tile?
	public static boolean isOnGroundTile(drawnObject u) {
		
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
			if(u.collides(u.getIntX(), u.getIntY(),currChunk)) tX = true;
		}
		
		if(tX) return true;
		else return false;
	}
	
	// Sort groundTiles
	public static void sortGroundTiles() {
		if(groundTiles != null) Collections.sort(groundTiles, chunk.chunkComparator);
	}
	
	// Initiate
	public static void initiate() {
		groundTiles = new CopyOnWriteArrayList<groundTile>();
	}
	
	// Respond to destroy
	@Override 
	public void respondToDestroy() {
		if(groundTiles.contains(this)) groundTiles.remove(this);
	}
	
}