package terrain;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.CopyOnWriteArrayList;

import doodads.sheepFarm.bone;
import drawing.drawnObject;
import drawing.gameCanvas;
import units.unit;
import utilities.intTuple;
import utilities.utility;

public class chunk extends drawnObject {
	////////////////
	/// DEFAULTS ///
	////////////////

	public static Comparator<chunk> chunkComparator = new Comparator<chunk>() {
	       public int compare(chunk c1, chunk c2) {
	         int result = Double.compare(c1.getIntX(), c2.getIntX());
	         if ( result == 0 ) {
	           // both X are equal -> compare Y too
	           result = Double.compare(c1.getIntY(), c2.getIntY());
	         } 
	         return result;
	      }
	};

	// All chunks.
	public static ArrayList<chunk> allChunks;
	public static CopyOnWriteArrayList<chunk> impassableChunks;
	
	// Largest chunk size.
	protected static int largestChunkWidth = 0;
	private static int largestChunkHeight = 0;
	
	// Default passable boolean
	private boolean DEFAULT_PASSABLE = true;
	
	//////////////
	/// FIELDS ///
	//////////////
	
	// Chunk variation.
	private int variationI;
	private int variationJ;
	
	// The image of the chunk.
	private BufferedImage chunkImage;
	
	// Short
	private boolean shortChunk = false;
	
	// Is the chunk passable or impassable?
	private boolean passable;
	
	// Is the chunk flammable?
	private boolean flammable = false;

	//////////////
	/// MEHODS ///
	//////////////
	
	// Constructor for choosing a random variation of the chunk.
	public chunk(chunkType c, int newX, int newY) {
		super(c.getChunkTypeSpriteSheet(), c.getClass().getName(), newX, newY, c.getWidth(), c.getHeight());
		
		// Get random chunk image
		int randomFirstRow = utility.RNG.nextInt(c.getChunkTypeSpriteSheet().getSheetWidth()/c.getChunkTypeSpriteSheet().getSpriteWidth());
		
		// Set our image field and chunktype
		setChunkImage(c.getChunkImage(randomFirstRow, 0));
		
		// Set variation I and J
		setVariationI(randomFirstRow);
		setVariationJ(0);
		
		// Load other chunk stuff (same for both constructors)
		loadChunkStuff(c);
	}
	
	// Constructor for choosing a given variation of the chunk.
	public chunk(chunkType c, int newX, int newY, int i, int j) {
		super(c.getChunkTypeSpriteSheet(), c.getClass().getName(), newX, newY, c.getWidth(), c.getHeight());
		
		// Set variation i and j
		setVariationI(i);
		setVariationJ(j);

		// Set our image field and chunkType
		if(c.getChunkTypeSpriteSheet() != null) setChunkImage(c.getChunkImage(i, j));
		
		// Load other chunk stuff (same for both constructors)
		loadChunkStuff(c);
		
	}
	
	// Load chunk stuff
	public void loadChunkStuff(chunkType c) {
		// Set default passable
		passable = DEFAULT_PASSABLE;
		
		// Remember our chunks.
		allChunks.add(this);
		
		// Set largest.
		if(c.getWidth() > largestChunkWidth) largestChunkWidth = c.getWidth();
		if(c.getHeight() > largestChunkHeight) largestChunkHeight = c.getHeight();
	}
	
	// Make copy
	@Override
	public drawnObject makeCopy() {
		
		try {
			Class<?> clazz = Class.forName(this.getClass().getName());
			Constructor<?> ctor = clazz.getConstructor(int.class, int.class, int.class);
			Object object = ctor.newInstance(new Object[] { this.getIntX(),
					this.getIntY(),
					this.getVariationI()});
			
			chunk d = (chunk)object;
			
			return d;
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
			
	}
	
	// Check if a unit collides with any chunk. Returns by how much.
		public static chunk getGroundChunk(drawnObject u, int newX, int newY) {
			
			// Check if it collides in x or y position.
			boolean tX = false;
			boolean tY = false;
			
			// Get x interval on left. (where foundX < ourChunkX - largestChunkWidth)
			int i1 = 0;
			int L = 0;
			int R = impassableChunks.size()-1;
			int T = newX - largestChunkWidth;
			int Am = 0;
			while(true) {
				int m = (L+R)/2;
				Am = impassableChunks.get(m).getIntX();
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
			R = impassableChunks.size()-1;
			T = newX + u.getWidth();
			Am = 0;
			while(true) {
				int m = (L+R)/2;
				Am = impassableChunks.get(m).getIntX();
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
			
			chunk currChunk = null;
			// Check between our interval
			for(;i1 <= i2; i1++) {
				currChunk = impassableChunks.get(i1);
				if(u.collides(u.getIntX(), newY,currChunk)) {
					tY = true;
					break;
				}
			}
			
			if(tY) return currChunk;
			else return null;
		}
	
	// Check if a unit collides with any chunk. Returns by how much.
	public static intTuple collidesWith(drawnObject u, int newX, int newY) {
		
		// If there's no impassable chunks
		if(impassableChunks.size() == 0) return intTuple.emptyTuple;
		
		// Check if it collides in x or y position.
		boolean tX = false;
		boolean tY = false;
		
		// Get x interval on left. (where foundX < ourChunkX - largestChunkWidth)
		int i1 = 0;
		int L = 0;
		int R = impassableChunks.size()-1;
		int T = newX - largestChunkWidth;
		int Am = 0;
		while(true) {
			int m = (L+R)/2;
			Am = impassableChunks.get(m).getIntX();
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
		R = impassableChunks.size()-1;
		T = newX + u.getWidth();
		Am = 0;
		while(true) {
			int m = (L+R)/2;
			Am = impassableChunks.get(m).getIntX();
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
			chunk currChunk = impassableChunks.get(i1);
			if(u.collides(newX, u.getIntY(),currChunk)
					&& !(u instanceof unit && ((unit)u).isFloating() && impassableChunks.get(i1).isShortChunk())) tX = true;
			if(u.collides(u.getIntX(), newY,currChunk)
					&& !(u instanceof unit && ((unit)u).isFloating() && impassableChunks.get(i1).isShortChunk())) tY = true;
		}
		
		// Make an intTuple for the return.
		int txInt = 0;
		int tyInt = 0;
		if(tX) txInt = 1;
		if(tY) tyInt = 1;
		
		if(tX || tY) return new intTuple(txInt, tyInt);
		else return intTuple.emptyTuple;
	}

	// Draw the chunk. 
	@Override
	public void drawObject(Graphics g) {
		//showHitBox();
		
		// Draw it. 
		if(getChunkImage() != null) {
			int changeFactor = 0;
			if(gameCanvas.getScaleX() != 1f || gameCanvas.getScaleY() != 1f) changeFactor = 1;
			if(isDrawSprite()) g.drawImage(getChunkImage(), 
					getDrawX(), 
					getDrawY(), 
					(int)(gameCanvas.getScaleX()*getChunkImage().getWidth() + changeFactor), 
					(int)(gameCanvas.getScaleY()*getChunkImage().getHeight() + changeFactor), 
					null);
			
			// Draw the outskirts of the sprite.
			if(showSpriteBox) {
				g.setColor(Color.red);
				g.drawRect(getDrawX(),
						   getDrawY(), 
						   (int)(gameCanvas.getScaleX()*getObjectSpriteSheet().getSpriteWidth()), 
						   (int)(gameCanvas.getScaleY()*getObjectSpriteSheet().getSpriteHeight()));
			}
			
			// Draw the hitbox of the image in green.
			if(showHitBox) {
				g.setColor(Color.green);
				g.drawRect(getDrawX() - (int)(gameCanvas.getScaleX()*(- (getObjectSpriteSheet().getSpriteWidth()/2 - getWidth()/2) - getHitBoxAdjustmentX())),
						   getDrawY() - (int)(gameCanvas.getScaleY()*(- (getObjectSpriteSheet().getSpriteHeight()/2 - getHeight()/2) - getHitBoxAdjustmentY())), 
						   (int)(gameCanvas.getScaleX()*getWidth()), 
						   (int)(gameCanvas.getScaleY()*getHeight()));
			}
			
			// Draw the x,y coordinates of the unit.
			if(showUnitPosition) {
				g.setColor(Color.white);
				g.drawString((int)(gameCanvas.getScaleX()*getIntX()) + "," + (int)(gameCanvas.getScaleX()*getIntY()),
						   getDrawX(),
						   getDrawY());
			}
		}
	}
	
	// Get units in box.
	public static ArrayList<chunk> getImpassableChunksInBox(int x1, int y1, int x2, int y2) {
		ArrayList<chunk> returnList = new ArrayList<chunk>();
		for(int i = 0; i < impassableChunks.size(); i++) {
			chunk c = impassableChunks.get(i);
			if(c.isWithin(x1, y1, x2, y2)) {
				returnList.add(c);
			}
		}
		if(returnList.size()==0) return null;
		return returnList;
	}
	
	// Check if a chunk is within 
	public boolean isWithin(int x1, int y1, int x2, int y2) {
		return getIntX() < x2 && 
		 getIntX() + getWidth() > x1 && 
		 getIntY() < y2 && 
		 getIntY() + getHeight() > y1;
	}
	
	// Initiate chunks
	public static void initiate() {
		
		// Initiate subchunks.
		bone.initiate();
		
		// Initiate chunks
		allChunks = new ArrayList<chunk>();
		groundTile.initiate();
	}
	
	// Ignite. Only does something for flammable chunks.
	public void ignite() {
		
	}
	
	// Sort chunks.
	public static void sortChunks() {
		if(impassableChunks != null) Collections.sort(impassableChunks, chunkComparator);
	}
	
	////////////////////////////////////
	////// GETTERS AND SETTERS /////////
	////////////////////////////////////
	// Get chunk image.
	public BufferedImage getChunkImage() {
		return chunkImage;
	}
	
	public boolean isPassable() {
		return passable;
	}
	
	public void setPassable(boolean b) {
		
		// Remove?
		if(b == true && impassableChunks.contains(this)) {
			impassableChunks.remove(this);
		}
		
		if(passable != b) {
			// Set passable.
			passable = b;
			
			// Remember our chunks.
			if(!passable) {
				impassableChunks.add(this);
				sortChunks();
			}
		}
	}

	public boolean isFlammable() {
		return flammable;
	}

	public void setFlammable(boolean flammable) {
		this.flammable = flammable;
	}

	public boolean isImportantEnoughToReload() {
		return reloadObject;
	}

	public int getVariationI() {
		return variationI;
	}

	public void setVariationI(int variationI) {
		this.variationI = variationI;
	}

	public int getVariationJ() {
		return variationJ;
	}

	public void setVariationJ(int variationJ) {
		this.variationJ = variationJ;
	}

	public void setChunkImage(BufferedImage chunkImage) {
		this.chunkImage = chunkImage;
	}

	public boolean isShortChunk() {
		return shortChunk;
	}

	public void setShortChunk(boolean shortChunk) {
		this.shortChunk = shortChunk;
	}

	
}