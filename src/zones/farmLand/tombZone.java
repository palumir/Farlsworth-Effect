package zones.farmLand;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import doodads.sheepFarm.well;
import doodads.tomb.stairsUp;
import doodads.tomb.wallTorch;
import drawing.background;
import drawing.spriteSheet;
import interactions.event;
import modes.platformer;
import sounds.music;
import terrain.chunk;
import terrain.atmosphericEffects.fog;
import terrain.chunkTypes.dirt;
import terrain.chunkTypes.tombBackground;
import terrain.chunkTypes.tombDirt;
import units.unit;
import units.unitTypes.farmLand.tomb.lightDude;
import units.unitTypes.farmLand.tomb.shadowDude;
import utilities.intTuple;
import zones.zone;

public class tombZone extends zone {
	
	//////////////////////////////////
	// FIELDS, GLOBALS, CONSTRUCTOR //
	//////////////////////////////////
	
	// Static caller of the zone.
	private static zone zoneReference;
	
	// Default background.
	private static BufferedImage DEFAULT_ZONE_BACKGROUND = spriteSheet.getSpriteFromFilePath("images/terrain/backgrounds/tombBackground.png");
	
	// Zone music.
	private static music zoneMusic = new music("sounds/music/farmLand/tomb/tomb.wav");
	
	// References we will use throughout.
	unit u;
	chunk c;
	ArrayList<intTuple> path;
	
	// Some defaults.
	public static int BACKGROUND_Z = -100;
	
	// Zone events.
	public static event enteredtombZoneBefore;
	
	// Defaults
	public static intTuple DEFAULT_SPAWN_TUPLE = new intTuple(0,-50);
	
	// Zone fog
	public static fog zoneFog;
	
	// Constructor
	public tombZone() {
		super("tombZone", "farmLand");
	}
		
	///////////////////////////////
	// SPAWN PATTERNS/GENERATORS //
	///////////////////////////////
	
	// Spawn grass dirt x to y.
	public void spawnTombRect(int x1, int y1, int x2, int y2, String type) {
		int numX = (x2 - x1)/dirt.DEFAULT_CHUNK_WIDTH;
		int numY = (y2 - y1)/dirt.DEFAULT_CHUNK_HEIGHT;
		for(int i = 0; i < numX; i++) {
			for(int j = 0; j < numY; j++) {
				if((i == numX-1 || i == 0 || j == 0 || j == numY-1)) {
					if(j==0 && type.equals("ground")) {
						c = new tombDirt(i*dirt.DEFAULT_CHUNK_WIDTH + x1, j*dirt.DEFAULT_CHUNK_HEIGHT + y1, 0);
					}
					else if(i == numX - 1 && type.equals("leftWall")) {
						c = new tombDirt(i*dirt.DEFAULT_CHUNK_WIDTH + x1, j*dirt.DEFAULT_CHUNK_HEIGHT + y1, 1);
					}
					else if(i==0 && type.equals("rightWall")) {
						c = new tombDirt(i*dirt.DEFAULT_CHUNK_WIDTH + x1, j*dirt.DEFAULT_CHUNK_HEIGHT + y1, 3);
					}
					else if(j==numY - 1 && type.equals("roof")) {
						c = new tombDirt(i*dirt.DEFAULT_CHUNK_WIDTH + x1, j*dirt.DEFAULT_CHUNK_HEIGHT + y1, 2);
					}
					else {
						c = new dirt(i*dirt.DEFAULT_CHUNK_WIDTH + x1, j*dirt.DEFAULT_CHUNK_HEIGHT + y1);
					}
				}
				else { 
					 c = new dirt(i*dirt.DEFAULT_CHUNK_WIDTH + x1, j*dirt.DEFAULT_CHUNK_HEIGHT + y1);
					 c.setPassable(true);
				}
			}
		}
	}
	
	// Spawn background  from x to y.
	public void spawnBackgroundRect(int x1, int y1, int x2, int y2) {
		int numX = (x2 - x1)/dirt.DEFAULT_CHUNK_WIDTH;
		int numY = (y2 - y1)/dirt.DEFAULT_CHUNK_HEIGHT;
		for(int i = 0; i < numX; i++) {
			for(int j = 0; j < numY; j++) {
				c = new tombBackground(i*dirt.DEFAULT_CHUNK_WIDTH + x1, j*dirt.DEFAULT_CHUNK_HEIGHT + y1);
				c.setZ(BACKGROUND_Z);
				c.setBackgroundDoodad(true);
				c.setPassable(true);
			}
		}
	}
	
	/////////////////
	// ZONE LOADER //
	/////////////////
	// Load the zone.
	public void loadSpecificZoneStuff() {
		
		// Set the mode of the zone of course.
		//topDown.setMode();
		platformer.setMode();
		
		// Set the darkness.
		zoneFog = new fog();
		zoneFog.setTo(0.3f);//fog.setTo(0.75f);
		
		// Load zone events.
		loadZoneEvents();
		
		// Background
		background.setGameBackground(DEFAULT_ZONE_BACKGROUND);
		
		// Spawn area.
		createSpawnArea();
		
		// Sort chunks.
		chunk.sortChunks();
		
		// Play zone music.
		zoneMusic.loopMusic();
	}
	
	// Load zone events.
	public void loadZoneEvents() {
		
		// Have we entered the dirt before?
		enteredtombZoneBefore = new event("enteredtombZoneBefore");
	}
	
	
	public void makeShadowSquare(int topLeftDudePosX, int topLeftDudePosY, int spreadX, int spreadY, float speed, boolean clockwise) {
		
		ArrayList<intTuple> squarePath = new ArrayList<intTuple>();
		squarePath.add(new intTuple(0,0));
		squarePath.add(new intTuple(1,0));
		squarePath.add(new intTuple(2,0));
		squarePath.add(new intTuple(2,1));
		squarePath.add(new intTuple(2,2));
		squarePath.add(new intTuple(1,2));
		squarePath.add(new intTuple(0,2));
		squarePath.add(new intTuple(0,1));
		
		for(int i = 0; i < 3; i++) {
		      for(int j = 0; j < 3; j++) {
		       
		    	  if(i == 1 && j == 1) {
		    		  // Do nothing.
		    	  }
		    	  else if(i == 2 && j==1) {
		    		  // Do nothing.
		    	  }
		    	  else {
		    		  
		    		  // How far they are spread
		    		  int spreadOutX = spreadX;
		    		  int spreadOutY = spreadY;
		    		    
		    		  // Spawn the shadow dude
		    		  u = new shadowDude(topLeftDudePosX + i*spreadOutX,topLeftDudePosY + spreadOutY*j);
		    		  u.setMoveSpeed(speed);
		    		  path = new ArrayList <intTuple> ();
		    		  
		    		  // Searches squarePath for our shadowDude's start position.
		    		  int n = 0;
		    		  for(; n < squarePath.size(); n++) {
		    			  intTuple currentTuple = squarePath.get(n);
		    			  if(currentTuple.x == i && currentTuple.y == j) break;
		    		  }
		    		  
		    		  if(clockwise) n++;
		    		  if(!clockwise) n--;
		    		  
		    		  // Walk him on the path.
		    		  for(int m = 0; m < squarePath.size()-1; m++) {
		    			  if(n >= squarePath.size()) n = 0;
		    			  if(n < 0) n = squarePath.size() - 1;
		    			  intTuple currentTuple = squarePath.get(n);
		    			  path.add(new intTuple(topLeftDudePosX + currentTuple.x*spreadOutX,topLeftDudePosY + currentTuple.y*spreadOutY));
		    			  if(clockwise) n++;
		    			  if(!clockwise) n--;
		    		  }
		    		  
		    		  u.patrolPath(path);
		    	  }
		      }
			}
	}
	
	public void shadowDudePatrol(int shadowDudeX, int shadowDudeY, int patrolToX, int patrolToY, float moveSpeed) {
		u = new shadowDude(shadowDudeX,shadowDudeY);
		u.patrolTo(patrolToX, patrolToY);
		u.setMoveSpeed(moveSpeed);
	}
	
	//////////////////////
	// INDIVIDUAL AREAS //
	//////////////////////
	
	public void createSpawnArea() {
		
		// Entrance
		stairsUp tombZoneEnterance = new stairsUp(30,-8,0,sheepFarm.getZone(),2320,-3896,"Down");
		tombZoneEnterance.setZ(BACKGROUND_Z);
		
		// Background 
		//spawnBackgroundRect(-65,-269, 7500,787);
		
		// Roof
		spawnTombRect(-18, -747, 7500,-220,"roof"); // the roof
		spawnTombRect(-500+2, -747, 0,-220,"none"); // roof dirt top left
	
		// Floor
		spawnTombRect(-18,40,300,820,"ground");
		spawnTombRect(-500+2,46,0,820,"none");
		
		// Left wall
		spawnTombRect(-338,-242,7,60,"leftWall");
		
		// Right wall
		spawnTombRect(7000,-750,7000,1000,"rightWall");
		
		// First floor

		spawnTombRect(350,40,500,791,"ground");
		c = new wallTorch(410,-40);
		u = new shadowDude(350,-6);
		u.patrolTo(455, -6);
		u.setMoveSpeed(2);
		
		// Second floor
		spawnTombRect(550,40,1000,791,"ground");
		c = new wallTorch(617,-40);
		c = new wallTorch(928,-40);
		
		for(int i=0; i <2; i++) {
			u = new shadowDude(555,-6 - i*50);
			u.patrolTo (970,-6 - i*50);
			u.setMoveSpeed(3);
		}
		
		// Third floor
		spawnTombRect(1080,40,2380,791,"ground");
		
		c = new wallTorch(1141,-40);
		
		for(int i=0; i <2; i++) {
			u = new shadowDude(1500,-6 - i*50);
			u.patrolTo (1080,-6 - i*50);
			u.setMoveSpeed(3);
		}	
			
		c = new wallTorch(1523,-40);
		
		for(int i=0; i <3; i++) {
			u = new shadowDude(1750,-6 - i*50);
			path = new ArrayList<intTuple>();
			path.add(new intTuple(1523,-6 - i*50));
			path.add(new intTuple(1880,-6 - i*50));
			u.patrolPath(path);
			u.setMoveSpeed(4);
		}
		
		c = new wallTorch(1905,-40);
		
		for(int i=0; i <2; i++) {
			u = new shadowDude(2150,-6 - i*50);
			path = new ArrayList<intTuple>();
			path.add(new intTuple(1905,-6 - i*50));
			path.add(new intTuple(2325,-6 - i*50));
			u.patrolPath(path);
			u.setMoveSpeed(3);
		}	
		
		c = new wallTorch(2287,-40);
		
		// Fourth floor
		spawnTombRect(2460,40,4160,791,"ground");
		
		c = new wallTorch(2525,-40);
		
		for(int i=0; i <3; i++) {
			u = new shadowDude(2800,-6 - i*50);
			path = new ArrayList<intTuple>();
			path.add(new intTuple(2525,-6 - i*50));
			path.add(new intTuple(3285,-6 - i*50));
			u.patrolPath(path);
			u.setMoveSpeed(5);
		}
		
		u = new lightDude(2603,-6);
		u.patrolTo(3300, -6);
		u.setMoveSpeed(3);
		
		c = new wallTorch(3311,-40);
		
		for(int i=0; i <3; i++) {
			u = new shadowDude(3700,-6 - i*50);
			path = new ArrayList<intTuple>();
			path.add(new intTuple(3311,-6 - i*50));
			path.add(new intTuple(4075,-6 - i*50));
			u.patrolPath(path);
			u.setMoveSpeed(5);
		}
		
		u = new lightDude(3388,-6);
		u.patrolTo(4085, -6);
		u.setMoveSpeed(3);
		
		c = new wallTorch(4097,-40);
		
		
		// Fifth floor
		spawnTombRect(4220+15,40,4340+15,791,"ground");
		c = new well(4252,4,0);
		c.setPassable(true);
		c.setZ(-2);
		
		// Sixth floor
		
		spawnTombRect(4330,155,4800,188,"ground");
		spawnTombRect(4863,-69,4895,541,"rightWall");
		
		u = new shadowDude(4335,110);
		u.patrolTo(4374, 110);

		makeShadowSquare(4400,60,70,75,3,true);
		makeShadowSquare(4700,60,70,75,3,true);
		
		spawnTombRect(4519,308,4893,340,"ground");
		
		u = new shadowDude(4519,261);
		u.patrolTo(4857, 261);
		
		spawnTombRect(4430,460,4815,492,"ground");
		
		u = new shadowDude(4519,343);
		u.patrolTo(4519, 415);
		u.setMoveSpeed(1);
		
		makeShadowSquare(4575,350,70,75,3,true);
		
		u = new shadowDude(4776,343);
		u.patrolTo(4776, 415);
		u.setMoveSpeed(1);
		
		// Seventh floor (down)
		
		spawnTombRect(4794,596,4977,628,"ground");
		
		c = new well(4840, 560,0);
		c.setPassable(true);
		c.setZ(-2);
		
		spawnTombRect(5008,681+40,5700,713+40,"ground");
		
		shadowDudePatrol(5074,675,5074,575,1);
		
		/*THIS IS EQUIVALENT TO THIS:
 		u = new shadowDude(5074,675);
		u.patrolTo(5074, 575);
		u.setMoveSpeed(1);*/
	
		u = new shadowDude(5124,575);
		u.patrolTo(5124, 675);
		u.setMoveSpeed(1);
	
		u = new shadowDude(5174,675);
		u.patrolTo(5174, 575);
		u.setMoveSpeed(1);
		
		u = new shadowDude(5224,575);
		u.patrolTo(5224, 675);
		u.setMoveSpeed(1);
		
		u = new shadowDude(5274,675);
		u.patrolTo(5274, 575);
		u.setMoveSpeed(1);
	
		u = new shadowDude(5324,625);
		path = new ArrayList<intTuple>();
		path.add(new intTuple(5324,675));
		path.add(new intTuple(5324,575));
		u.setMoveSpeed(1);
		u.patrolPath(path);
		
		u = new shadowDude(5374,575);
		u.patrolTo(5374, 675);
		u.setMoveSpeed(1);
		
		u = new shadowDude(5424,625);
		path = new ArrayList<intTuple>();
		path.add(new intTuple(5424,575));
		path.add(new intTuple(5424,675));
		u.setMoveSpeed(1);
		u.patrolPath(path);
		
		u = new shadowDude(5474,575);
		u.patrolTo(5474, 675);
		u.setMoveSpeed(2);
		
		u = new shadowDude(5524,675);
		u.patrolTo(5524, 575);
		u.setMoveSpeed(2);
		
		u = new shadowDude(5574,575);
		u.patrolTo(5574, 675);
		u.setMoveSpeed(2);
		
		u = new shadowDude(5624,675);
		u.patrolTo(5624, 575);
		u.setMoveSpeed(2);
		
		spawnTombRect(5773,679,5960,711,"ground");
		
		for(int i=0; i <2; i++) {
			u = new shadowDude(5998,686 - i*50);
			u.patrolTo (5998, 640 - i*50);
			u.setMoveSpeed(1);
		}
		
			
		for(int i=0; i <2; i++) {
			u = new shadowDude(5998,506 - i*50);
			u.patrolTo (5998, 460 - i*50);
			u.setMoveSpeed(1);
		
		spawnTombRect(6086,679,6273,711,"ground");
		
		spawnTombRect(6300,609,6452,641,"ground");
		
		makeShadowSquare(6279,490,70,75,3,true);
		
		// Seventh floor (up)
		
		spawnTombRect(5000,522,5152,554,"ground");
		
		spawnTombRect(5170,472,5322,504,"ground");
		
		spawnTombRect(5000,402,5152,434,"ground");
		
		spawnTombRect(5170,322,5322,354,"ground");
		
		spawnTombRect(5340,262,5472,294,"ground");
		
		makeShadowSquare(5150,380,70,75,3,true);
		
		makeShadowSquare(5323,162,70,75,3,true);

		}
	}
	

	// Get the player location in the zone.
	public intTuple getDefaultLocation() {
		return DEFAULT_SPAWN_TUPLE;
	}

	/////////////////////////
	// Getters and setters //
	/////////////////////////
	public static zone getZone() {
		return zoneReference;
	}

	public static void setZone(zone z) {
		zoneReference = z;
	}
	
}