package zones.farmLand;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import doodads.sheepFarm.well;
import doodads.tomb.stairsUp;
import doodads.tomb.wallTorch;
import drawing.background;
import drawing.drawnObject;
import drawing.spriteSheet;
import drawing.userInterface.tooltipString;
import interactions.event;
import modes.platformer;
import sounds.music;
import terrain.chunk;
import terrain.atmosphericEffects.fog;
import terrain.chunkTypes.tomb;
import terrain.chunkTypes.tombEdge;
import units.player;
import units.unit;
import units.bosses.playerOne;
import units.unitTypes.farmLand.tomb.lightDude;
import units.unitTypes.farmLand.tomb.shadowDude;
import utilities.intTuple;
import utilities.saveState;
import utilities.time;
import zones.zone;

public class farmTombEasy extends zone {
	
	//////////////////////////////////
	// FIELDS, GLOBALS, CONSTRUCTOR //
	//////////////////////////////////
	
	// Static caller of the zone.
	private static zone zoneReference;
	
	// Default background.
	private static BufferedImage DEFAULT_ZONE_BACKGROUND = spriteSheet.getSpriteFromFilePath("images/terrain/backgrounds/tombBackground.png");
	
	// Zone music.
	private static String zoneMusic = "sounds/music/farmLand/tomb/tomb.wav";
	private static String zoneMusicFrantic = "sounds/music/farmLand/tomb/tombElevator.wav";
	
	// References we will use throughout.
	static unit u;
	static chunk c;
	static ArrayList<intTuple> path;
	
	// Some defaults.
	public static int BACKGROUND_Z = -100;
	
	// Zone events.
	public static event enteredtombZoneBefore;
	public static event shadowElevatorStarted;
	public static boolean shadowElevatorFirstTime;
	
	// Initiated?
	public boolean shadowElevatorInitiated = false;
	
	// Defaults
	public static intTuple DEFAULT_SPAWN_TUPLE = new intTuple(0,-50);
	
	// 
	
	// Zone fog
	public static fog zoneFog;
	
	// Constructor
	public farmTombEasy() {
		super("farmTombEasy", "farmLand");
	}
		
	///////////////////////////////
	// SPAWN PATTERNS/GENERATORS //
	///////////////////////////////
	
	// Spawn grass dirt x to y.
	public static void spawnReloadableTombRect(int x1, int y1, int x2, int y2, String type) {
			
			int numX = (x2 - x1)/tomb.DEFAULT_CHUNK_WIDTH;
			int numY = (y2 - y1)/tomb.DEFAULT_CHUNK_HEIGHT;
			for(int i = 0; i < numX; i++) {
				for(int j = 0; j < numY; j++) {
					if((i == numX-1 || i == 0 || j == 0 || j == numY-1)) {
						if(j==0 && type.equals("ground")) {
							c = new tombEdge(i*tomb.DEFAULT_CHUNK_WIDTH + x1, j*tomb.DEFAULT_CHUNK_HEIGHT + y1, 0);
						}
						else if(i == numX - 1 && type.equals("leftWall")) {
							c = new tombEdge(i*tomb.DEFAULT_CHUNK_WIDTH + x1, j*tomb.DEFAULT_CHUNK_HEIGHT + y1, 1);
						}
						else if(i==0 && type.equals("rightWall")) {
							c = new tombEdge(i*tomb.DEFAULT_CHUNK_WIDTH + x1, j*tomb.DEFAULT_CHUNK_HEIGHT + y1, 3);
						}
						else if(j==numY - 1 && type.equals("roof")) {
							c = new tombEdge(i*tomb.DEFAULT_CHUNK_WIDTH + x1, j*tomb.DEFAULT_CHUNK_HEIGHT + y1, 2);
						}
						else {
							c = new tomb(i*tomb.DEFAULT_CHUNK_WIDTH + x1, j*tomb.DEFAULT_CHUNK_HEIGHT + y1);
						}
					}
					else { 
						c = new tomb(i*tomb.DEFAULT_CHUNK_WIDTH + x1, j*tomb.DEFAULT_CHUNK_HEIGHT + y1);
						 if(c!=null) c.setPassable(true);
					}
				}
			}
	}
	
	// Spawn grass dirt x to y.
	public static void spawnTombRect(int x1, int y1, int x2, int y2, String type) {
			
			int numX = (x2 - x1)/tomb.DEFAULT_CHUNK_WIDTH;
			int numY = (y2 - y1)/tomb.DEFAULT_CHUNK_HEIGHT;
			for(int i = 0; i < numX; i++) {
				for(int j = 0; j < numY; j++) {
					if((i == numX-1 || i == 0 || j == 0 || j == numY-1)) {
						if(j==0 && type.equals("ground")) {
							tombEdge.createChunk(i*tomb.DEFAULT_CHUNK_WIDTH + x1, j*tomb.DEFAULT_CHUNK_HEIGHT + y1, 0);
						}
						else if(i == numX - 1 && type.equals("leftWall")) {
							tombEdge.createChunk(i*tomb.DEFAULT_CHUNK_WIDTH + x1, j*tomb.DEFAULT_CHUNK_HEIGHT + y1, 1);
						}
						else if(i==0 && type.equals("rightWall")) {
							tombEdge.createChunk(i*tomb.DEFAULT_CHUNK_WIDTH + x1, j*tomb.DEFAULT_CHUNK_HEIGHT + y1, 3);
						}
						else if(j==numY - 1 && type.equals("roof")) {
							tombEdge.createChunk(i*tomb.DEFAULT_CHUNK_WIDTH + x1, j*tomb.DEFAULT_CHUNK_HEIGHT + y1, 2);
						}
						else {
							tomb.createChunk(i*tomb.DEFAULT_CHUNK_WIDTH + x1, j*tomb.DEFAULT_CHUNK_HEIGHT + y1);
						}
					}
					else { 
						 c = tomb.createChunk(i*tomb.DEFAULT_CHUNK_WIDTH + x1, j*tomb.DEFAULT_CHUNK_HEIGHT + y1);
						 if(c!=null) c.setPassable(true);
					}
				}
			}
	}
	
	/////////////////
	// ZONE LOADER //
	/////////////////
	// Load the zone.
	public void loadSpecificZoneStuff() {
		
		// Set the mode of the zone of course.
		platformer.setMode();
		
		// Set the darkness.
		zoneFog = new fog();
		zoneFog.setTo(0.3f);//fog.setTo(0.75f);
		
		// Elevator not initiated
		shadowElevatorInitiated = false;
		
		// Load zone events.
		loadZoneEvents();
		
		// Load units
		loadUnits();
		
		// Background
		background.setGameBackground(DEFAULT_ZONE_BACKGROUND);
		
		// Spawn area.
		createSpawnArea();
		
		// Sort chunks.
		chunk.sortChunks();
		
		// Play zone music.
		if(!shadowElevatorStarted.isCompleted()) { music.startMusic(zoneMusic);  }
	}
	
	// Load units
	public void loadUnits() {
		
		// Load the villain player.
		u = new playerOne(Integer.MIN_VALUE,Integer.MIN_VALUE);
	}
	
	// Load zone events.
	public void loadZoneEvents() {
		
		// Have we entered the dirt before?
		enteredtombZoneBefore = new event("enteredtombZoneBefore");
		
		// Has the elevator started?
		shadowElevatorStarted = new event("tombZoneShadowElevatorStarted");
	}
	
public static void makeShadowRectangle(int topLeftDudePosX, int topLeftDudePosY, int spreadX, int spreadY, int numDudesWidth, int numDudesHeight, float speed, boolean clockwise) {
		
		ArrayList<intTuple> squarePath = new ArrayList<intTuple>();
		for(int i = 0; i < numDudesWidth; i++) {
			squarePath.add(new intTuple(i,0));
		}
		for(int i = 0; i < numDudesHeight; i++) {
			squarePath.add(new intTuple(numDudesWidth-1,i));
		}
		for(int i = numDudesWidth-1; i > 0; i--) {
			squarePath.add(new intTuple(i,numDudesHeight-1));
		}
		for(int i = numDudesHeight-1; i > 0; i--) {
			squarePath.add(new intTuple(0,i));
		}
		
		for(int i = 0; i < numDudesWidth; i++) {
		      for(int j = 0; j < numDudesHeight; j++) {
		       
		    	  if(i != 0 && j != 0 && j != numDudesHeight - 1 && i != numDudesWidth - 1) {
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
	
	public void makeShadowSquareTopLeft (int topLeftDudePosX, int topLeftDudePosY, int spreadX, int spreadY, float speed, boolean clockwise) {
		
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
		    	  else if(i == 0 && j==0) {
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
	
public void makeShadowSquareTopRight (int topLeftDudePosX, int topLeftDudePosY, int spreadX, int spreadY, float speed, boolean clockwise) {
		
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
		    	  else if(i == 2 && j==0) {
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
	
	public void shadowDudePatrolPath(int shadowDudeX, int shadowDudeY, ArrayList<intTuple> path, float moveSpeed) {
		u = new shadowDude(shadowDudeX,shadowDudeY);
		u.patrolPath(path);
		u.setMoveSpeed(moveSpeed);
	}
	
	public static void shadowDudePatrol(int shadowDudeX, int shadowDudeY, int patrolToX, int patrolToY, float moveSpeed) {
		u = new shadowDude(shadowDudeX,shadowDudeY);
		u.patrolTo(patrolToX, patrolToY);
		u.setMoveSpeed(moveSpeed);
	}
	
	public static void lightDudePatrol(int lightDudeX, int lightDudeY, int patrolToX, int patrolToY, float moveSpeed) {
		u = new lightDude (lightDudeX,lightDudeY);
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
		
		// Kill player when they fall
		
		u = new shadowDude(270,116);
		u.setFacingDirection("Down");
		u = new shadowDude(290,116);
		u.setFacingDirection("Down");
		u = new shadowDude(310,116);
		u.setFacingDirection("Down");
		u = new shadowDude(330,116);
		u.setFacingDirection("Down");
		
		u = new shadowDude(476,116);
		u.setFacingDirection("Up");
		u = new shadowDude(496,116);
		u.setFacingDirection("Up");
		u = new shadowDude(516,116);
		u.setFacingDirection("Up");
		u = new shadowDude(536,116);
		u.setFacingDirection("Up");
		
		u = new shadowDude(1000,116);
		u.setFacingDirection("Down");
		u = new shadowDude(1020,116);
		u.setFacingDirection("Down");
		u = new shadowDude(1040,116);
		u.setFacingDirection("Down");
		u = new shadowDude(1060,116);
		u.setFacingDirection("Down");
		
		u = new shadowDude(2359,116);
		u.setFacingDirection("Up");
		u = new shadowDude(2379,116);
		u.setFacingDirection("Up");
		u = new shadowDude(2399,116);
		u.setFacingDirection("Up");
		u = new shadowDude(2419,116);
		u.setFacingDirection("Up");
		u = new shadowDude(2439,116);
		u.setFacingDirection("Up");
		
		u = new shadowDude(4155,116);
		u.setFacingDirection("Down");
		u = new shadowDude(4175,116);
		u.setFacingDirection("Down");
		u = new shadowDude(4195,116);
		u.setFacingDirection("Down");
		u = new shadowDude(4215,116);
		u.setFacingDirection("Down");

		
		// First floor
		
		spawnTombRect(350,40,500,791,"ground");
		
		c = new wallTorch(410,-40);
		
		shadowDudePatrol (350,-6,455,-6,2);
		
		// Second floor
		
		spawnTombRect(550,40,1000,791,"ground");
		
		c = new wallTorch(617,-40);
		c = new wallTorch(928,-40);
		
		for(int i=0; i <2; i++) {
			u = new shadowDude(620,-6 - i*50);
			path = new ArrayList<intTuple>();
			path.add(new intTuple(555,-6 - i*50));
			path.add(new intTuple(970,-6 - i*50));
			u.patrolPath(path);
			u.setMoveSpeed(3);
		}
		
		// Third floor
		
		spawnTombRect(1080,40,2380,791,"ground");
		
		c = new wallTorch(1141,-40);
		
		for(int i=0; i <2; i++) {
			u = new shadowDude(1320,-6 - i*50);
			path = new ArrayList<intTuple>();
			path.add(new intTuple(1500,-6 - i*50));
			path.add(new intTuple(1080, -6 - i*50));
			u.patrolPath(path);
			u.setMoveSpeed(3);
		}
			
		c = new wallTorch(1523,-40);
		
		for(int i=0; i <3; i++) {
			u = new shadowDude(1707,-6 - i*50);
			path = new ArrayList<intTuple>();
			path.add(new intTuple(1880,-6 - i*50));
			path.add(new intTuple(1523,-6 - i*50));
			u.patrolPath(path);
			u.setMoveSpeed(3);
		}
		
		c = new wallTorch(1905,-40);
		
		for(int i=0; i <2; i++) {
			u = new shadowDude(2285,-6 - i*50);
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
			path = new ArrayList<intTuple>();
			path.add(new intTuple(2525,-6 - i*50));
			path.add(new intTuple(3285,-6 - i*50));
			shadowDudePatrolPath(2800,-6 - i*50, path, 5);
		}
		
		u = new lightDude(2583,-6);
		path = new ArrayList<intTuple>();
		path.add(new intTuple(2580,-6));
		path.add(new intTuple(3235, -6));
		u.patrolPath(path);
		u.setMoveSpeed(3.5f);
		
		c = new wallTorch(3311,-40);
		
		for(int i=0; i <3; i++) {
			path = new ArrayList<intTuple>();
			path.add(new intTuple(3301,-6 - i*50));
			path.add(new intTuple(4075,-6 - i*50));
			shadowDudePatrolPath(3700,-6 - i*50, path, 5);
		}
		
		u = new lightDude(3813,-6);
		path = new ArrayList<intTuple>();
		path.add(new intTuple(4017,-6));
		path.add(new intTuple(3375,-6));
		u.patrolPath(path);
		u.setMoveSpeed(3.5f);
		
		c = new wallTorch(4097,-40);
		
		// Fifth floor
		
		spawnTombRect(4220+15,40,4340+15,791,"ground");
		
		c = new well(4252,4,0);
		c.setPassable(true);
		c.setZ(-2);
		
		// Sixth floor
		
		spawnTombRect(4863+32,-69,4895+32,517,"rightWall");
		
		spawnTombRect(4330,155,4832,188,"ground");
		makeShadowSquare(4400,40,75,80,1.8f,true);
		makeShadowSquare(4700,40,75,80,2.7f,true);
		
		spawnTombRect(4519-8,308+32,4893+24,340+32,"ground");
		shadowDudePatrol (4519,261+32,4857,261+32,2);
		
		spawnTombRect(4330,460+48,4815,492+48,"ground");
		shadowDudePatrol (4500,343+48,4500,415+48,1f);
		makeShadowSquare(4575,340+48,75,80,1.5f,false);
		shadowDudePatrol (4790,343+48,4790,415+48,1f);
		u = new shadowDude(4790,550);
		u.setFacingDirection("Down");

		// Seventh floor
		
		spawnTombRect(4864,615-32-2+32,4896,1070+32+24-2,"leftWall");
		
		spawnTombRect(5572,-69,5604,1100,"rightWall");
		
		c = new well(4843, 560,0);
		c.setPassable(true);
		c.setZ(-2);
		
		spawnTombRect(4794,596,5465+32,628,"ground");
		makeShadowSquareTopRight (4998,470, 80, 85,1.7f ,true);
		makeShadowSquareTopLeft (5198,470, 80, 85,1.7f ,false);
		
		spawnTombRect(5004-8,811,5604-8,843,"ground");
		makeShadowRectangle (4965,764,250,250,3,2,1.2f,false);
		
		spawnTombRect(4888,1061,5480,1093,"ground");
		for(int i=0; i <4; i++) {
			u = new shadowDude(5123,1014 - i*50);
		}
		for(int i=0; i <4; i++) {
			u = new shadowDude(5323,1014 - i*50);
		}
		lightDudePatrol (5015,890,5131,890,0.9f);
		lightDudePatrol (5314,890,5460,890,0.9f);
		
		// Eighth floor
		
		spawnTombRect(5450,1162,6274,1194,"ground");
		c = new well(5555,1126,0);
		c.setPassable(true);
		c.setZ(-2);
		
		// Ninth floor (3 floors)
		
		// Top
		
		spawnTombRect(6393,1062,6727,1094,"ground");
		for(int i=0; i <2; i++) {
			path = new ArrayList<intTuple>();
			path.add(new intTuple(6396,1016 - i*50));
			shadowDudePatrolPath(6692,1016 - i*50, path ,3);
		}
		
		spawnTombRect(6800,1062,7494+32+32,1094,"ground");
		
		for(int i=0; i <3; i++) {
			path = new ArrayList<intTuple>();
			path.add(new intTuple(7086,1016 - i*50));
			shadowDudePatrolPath(6899,1016 - i*50, path ,2);
		}
		for(int i=0; i <3; i++) {
			path = new ArrayList<intTuple>();
			path.add(new intTuple(7293,1016 - i*50));
			shadowDudePatrolPath(7106,1016 - i*50, path ,2);
		}
		for(int i=0; i <3; i++) {
			path = new ArrayList<intTuple>();
			path.add(new intTuple(7500,1016 - i*50));
			shadowDudePatrolPath(7313,1016 - i*50, path ,2);
		}
		
		lightDudePatrol (6884, 926, 7515+32,926, 1.4f);
		
		spawnTombRect(7494+73+32,1062,8082,1094,"ground");
		
		for(int i=0; i <3; i++) {
			u = new shadowDude (7656,1016 - i*50);
		}
		
		makeShadowSquare(7770,1017,99,99,2f,false);
		
		spawnTombRect(8082+73,1062,8300,1094,"ground");
		
		// Middle
		
		spawnTombRect(6353,1162,7103,1194,"ground");
		shadowDudePatrol (6351,1117,6682,1117,3);
		shadowDudePatrol (6844,1095,6844,1117,1);
		shadowDudePatrol (7037,1117,7037,1095,1);
		lightDudePatrol (6796, 1117, 7070,1117,3f);
		
		spawnTombRect(7103+73,1162,7303,1194,"ground");
		shadowDudePatrol (7169,1117,7350,1117,2);
		
		spawnTombRect(7376,1162,7464,1194,"ground");
		
		u = new shadowDude(8206,1117);

		
		// Bottom
		
		spawnTombRect(6323,1262,7376,1294,"ground");
		shadowDudePatrol (6702,1215,6325,1215,3);
		shadowDudePatrol (6752,1215,7010,1215,3);
		u = new shadowDude(7060,1215);
		
		spawnTombRect(7376+73,1262,8300,1294,"ground");
		makeShadowSquare(7494,1120,80,85,1.5f,true);
		u = new shadowDude(8206,1218);
		
		// Tenth floor
		
		spawnTombRect(7606+73,1162,9000,1194,"ground");
		
		c = new well(8207,1026,0);
		c.setPassable(true);
		c.setZ(-2);
		
		for(int i=0; i <2; i++) {
			path = new ArrayList<intTuple>();
			path.add(new intTuple(8396,1116 - 75 - i*50));
			shadowDudePatrolPath(8396,1116 - i*50, path, 1);
		}
		
		for(int i=0; i <2; i++) {
			path = new ArrayList<intTuple>();
			path.add(new intTuple(8486,1116 - 120 - i*50));
			shadowDudePatrolPath(8486,1116 - 175 - i*50, path, 1);
		}
		
		u = new shadowDude(8486,1116);
		
		for(int i=0; i <2; i++) {
			path = new ArrayList<intTuple>();
			path.add(new intTuple(8576,1116 - i*50));
			shadowDudePatrolPath(8576,1116 - 75 - i*50, path ,1);
		}
		
		for(int i=0; i <2; i++) {
			path = new ArrayList<intTuple>();
			path.add(new intTuple(8656 + i*40 + 50,1116 - 75));
			shadowDudePatrolPath(8656 + i*40,1116 - 75, path ,1);
		}
		
		shadowDudePatrol (8656,1116,8902,1116,2);
		
		for(int i=0; i <2; i++) {
			path = new ArrayList<intTuple>();
			path.add(new intTuple(8842 + i*40 + 50,1116 - 75));
			shadowDudePatrolPath(8842 + i*40,1116 - 75, path ,1);
		}
		
		// Exit
		
		spawnTombRect(9075,1150,13000,2374,"ground");
		//spawnTombRect(9962,512,10500,1154,"rightWall");
		//spawnTombRect(9965,1152,10500,1502,"none");
	}
	
	// Spawn a rectangle of shadow dudes
	public static ArrayList<shadowDude> createRectangleOfShadows(int x1, int y1, int x2, int y2, boolean eyeless) {
		
		ArrayList<shadowDude> retDudes = new ArrayList<shadowDude>();
		for(int i = x1; i < x2; i += shadowDude.getDefaultWidth()) {
			for(int j = y1; j < y2; j += shadowDude.getDefaultHeight()) {
				u = new shadowDude(i,j);
				((shadowDude)u).setEyeless(true);
				retDudes.add((shadowDude) u);
			}
		}
		return retDudes;
	}
	
	// Shadow elevator
	public static ArrayList<shadowDude> shadowElevator;
	
	// Give elevator eyes
	public static void giveElevatorEyes() {
		for(int i = 0; i < shadowElevator.size(); i++) {
			shadowElevator.get(i).setEyeless(false);
			shadowElevator.get(i).setIgnoreIllumination(true);
			shadowElevator.get(i).setMoveSpeed(0.70f);
		}
	}
	
	// Move elevator up
	public static void moveElevatorUp() {
	
		// Play frantic music.
		music m = music.startMusic(zoneMusicFrantic);
		if(m!=null) m.stopOnDeath = true;
		 
		// Move the elevator.
		for(int i = 0; i < shadowElevator.size(); i++) {
			shadowElevator.get(i).movingUp = true;
		}
	}
	
	// Create shadow dude elevator
	public static void createShadowElevatorAroundPlayer(boolean eyeless) {
		player currPlayer = player.getPlayer();
		
		// Left wall.
		shadowElevator = createRectangleOfShadows(currPlayer.getIntX() - 580, 
												  1150-30 - 1500, 
												  currPlayer.getIntX() - 250,
												  1150-30 + 600, eyeless);
		
		// Floor
		shadowElevator.addAll(createRectangleOfShadows(currPlayer.getIntX() - 220-25+1, 
				  1150-30+200+3-92, 
				  currPlayer.getIntX() +250,
				  1150-30 + 600, eyeless));
		
		// Right wall.
		shadowElevator.addAll(createRectangleOfShadows(currPlayer.getIntX() + 260, 
												  1150-30 - 1500, 
												  currPlayer.getIntX() + 600,
												  1150-30 + 600, eyeless));
		
		// First platform
		spawnReloadableTombRect(currPlayer.getIntX() -80,
				      1150-30 - 35 - 32,
				      currPlayer.getIntX()-30,
				      1150-30 - 35,
				      "ground");
		
		// Second platform
		spawnReloadableTombRect(currPlayer.getIntX() - 100-10,
				      1150-30 - 35 - 32 - 64*1-20,
				      currPlayer.getIntX() -70,
				      1150-30 - 35 - 64*1-20,
				      "ground");
		
		// Third platform
		spawnReloadableTombRect(currPlayer.getIntX(),
				      1150-30 - 35 - 32 - 64*2,
				      currPlayer.getIntX() + 200,
					  1150-30 - 35 - 64*2,
						      "ground");
		
		// Fourth platform
		spawnReloadableTombRect(currPlayer.getIntX() + 170,
				      1150-30 - 35 - 32 - 64*4 + 20,
				      currPlayer.getIntX() + 220,
					  1150-30 - 35 - 64*4 + 20,
						      "ground");
		
		// Fifth platform
		spawnReloadableTombRect(currPlayer.getIntX() - 200,
				      1150-30 - 35 - 32 - 64*5,
				      currPlayer.getIntX() + 150,
					  1150-30 - 35 - 64*5,
						      "ground");
		shadowDudePatrol(currPlayer.getIntX() - 210, 1150-30 - 35 - 32 - 64*5 - 46, currPlayer.getIntX() + 100, 1150-30 - 35 - 32 - 64*5 - 46, 1.5f);
		shadowDudePatrol(currPlayer.getIntX() - 210, 1150-30 - 35 - 32 - 64*5 - 46 - 50, currPlayer.getIntX() + 100, 1150-30 - 35 - 32 - 64*5 - 46-50, 2.8f);
		
		// Sixth platform
		spawnReloadableTombRect(currPlayer.getIntX() - 200,
				      1150-30 - 35 - 32 - 64*7 + 20,
				      currPlayer.getIntX() -150,
					  1150-30 - 35 - 64*7 + 20,
						      "ground");
		
		// Seventh platform
		spawnReloadableTombRect(currPlayer.getIntX() - 140,
				      1150-30 - 35 - 32 - 64*8,
				      currPlayer.getIntX() + 150,
					  1150-30 - 35 - 64*8,
						      "ground");
		u = new shadowDude(currPlayer.getIntX() + 5 - 10,
			      1150-30 - 35 - 32 - 64*8 - 46);
		u.setFacingDirection("Left");
		u = new shadowDude(currPlayer.getIntX() + 5 - 10,
			      1150-30 - 35 - 32 - 64*8 - 46 - 50);
		u.setFacingDirection("Left");
		u = new shadowDude(currPlayer.getIntX() + 5 - 10,
			      1150-30 - 35 - 32 - 64*8 - 46 - 50*2);
		u.setFacingDirection("Left");
		lightDudePatrol(currPlayer.getIntX() - 140,
			      1150-30 - 35 - 32 - 64*8 - 46 - 50*2 + 10,
			      currPlayer.getIntX() + 130,
			      1150-30 - 35 - 32 - 64*8 - 46 - 50*2 + 10,
			      2.6f);
		
		// Eighth Platform
		spawnReloadableTombRect(currPlayer.getIntX() + 150,
			      1150-30 - 35 - 32 - 64*10 + 20,
			      currPlayer.getIntX() + 200,
				  1150-30 - 35 - 64*10 + 20,
					      "ground");
		
		// Ninth Platform
		spawnReloadableTombRect(currPlayer.getIntX() + 200,
					      1150-30 - 35 - 32 - 64*12 + 40,
					      currPlayer.getIntX() + 250,
						  1150-30 - 35 - 64*12 + 40,
							      "ground");
		
		// Ten
		spawnReloadableTombRect(currPlayer.getIntX() + 130,
					      1150-30 - 35 - 32 - 64*14 + 20*3,
					      currPlayer.getIntX() + 170,
						  1150-30 - 35 - 64*14 + 20*3,
							      "ground");
		
		// Eleven
		spawnReloadableTombRect(currPlayer.getIntX() - 10 ,
					      1150-30 - 35 - 32 - 64*13 + 20*2,
					      currPlayer.getIntX() + 32 - 10,
						  1150-30 - 35 - 64*13 + 20*2,
							      "ground");
		
		// Twelve
		spawnReloadableTombRect(currPlayer.getIntX() - 150 ,
					      1150-30 - 35 - 32 - 64*13 + 20*2,
					      currPlayer.getIntX() + 40 - 150,
						  1150-30 - 35 - 64*13 + 20*2,
							      "ground");
		
		// Thirteen
		spawnReloadableTombRect(currPlayer.getIntX() - 230 ,
					      1150-30 - 35 - 32 - 64*15 + 20*3,
					      currPlayer.getIntX() - 190,
						  1150-30 - 35 - 64*15 + 20*3,
							      "ground");
		
		// Fourteen
		spawnReloadableTombRect(currPlayer.getIntX() - 150 ,
					      1150-30 - 35 - 32 - 64*17 + 20*5,
					      currPlayer.getIntX() - 150 + 40,
						  1150-30 - 35 - 64*17 + 20*5,
							      "ground");
		shadowDudePatrol(currPlayer.getIntX() - 200 ,
						1150-30 - 35 - 32 - 64*17 + 20*5 + 100, 
						currPlayer.getIntX() + 220,
						1150-30 - 35 - 32 - 64*17 + 20*5 + 100,
						2f);
		
		// Fifteen
		spawnReloadableTombRect(currPlayer.getIntX() - 80 ,
					      1150-30 - 35 - 32 - 64*19 + 20*7,
					      currPlayer.getIntX() + 220,
						  1150-30 - 35 - 64*19 + 20*7,
							      "ground");
		for(int i = 0; i < 16; i++) {
			shadowDudePatrol(currPlayer.getIntX() - 230 + i*30,
					1150-30 - 35 - 32 - 64*19 + 20*7 - 46-20, 
					currPlayer.getIntX()  - 230 + i*30,
					1150-30 - 35 - 32 - 64*19 + 20*7 - 46 - 50*3,
					2f);
		}
		u = new lightDude(currPlayer.getIntX() + 70, 1150-30 - 35 - 32 - 64*19 + 20*7 - 46);
		u.setFacingDirection("Left");
		
		// Sixteen
		spawnReloadableTombRect(currPlayer.getIntX() + 200 ,
					      1150-30 - 35 - 32 - 64*21 + 20*8,
					      currPlayer.getIntX() + 240,
						  1150-30 - 35 - 64*21+ 20*8,
							      "ground");
		u = new lightDude(currPlayer.getIntX() + 200 + 15 - 40, 1150-30 - 35 - 32 - 64*21 + 20*10 - 46);
		u.patrolTo(currPlayer.getIntX() + 200 + 15 - 40,1150-30 - 35 - 32 - 64*19 + 20*7 - 200);
		u.setMoveSpeed(1f);
		
		// Seventeen
		spawnReloadableTombRect(currPlayer.getIntX() + 160 ,
					      1150-30 - 35 - 32 - 64*23 + 20*9,
					      currPlayer.getIntX() + 200,
						  1150-30 - 35 - 64*23+ 20*9,
							      "ground");
		
		// Eighteen
		spawnReloadableTombRect(currPlayer.getIntX() + 160 ,
					      1150-30 - 35 - 32 - 64*23 + 20*9,
					      currPlayer.getIntX() + 200,
						  1150-30 - 35 - 64*23+ 20*9,
							      "ground");
		
		// Nineteen
		spawnReloadableTombRect(currPlayer.getIntX() + -200 ,
					      1150-30 - 35 - 32 - 64*25 + 20*10,
					      currPlayer.getIntX() + 150,
						  1150-30 - 35 - 64*25+ 20*10,
							      "ground");
		
		// Left wall
		spawnReloadableTombRect(currPlayer.getIntX() + -228 ,
			      1150-30 - 35 - 32 - 64*25 + 20*10 - 480 - 32*9,
			      currPlayer.getIntX() -190,
				  1150-30 - 35 - 64*25+ 20*10,
					      "none");
		
		// Right wall
		spawnReloadableTombRect(currPlayer.getIntX() + +200 ,
			      1150-30 - 35 - 32 - 64*25 + 20*10 - 480 - 32*9,
			      currPlayer.getIntX() +250,
				  1150-30 - 35 - 64*25+ 20*10,
					      "none");
		
		// Nineteen second platform
		spawnReloadableTombRect(currPlayer.getIntX() + -200 + 50 ,
			      1150-30 - 35 - 32 - 64*27 + 20*11,
			      currPlayer.getIntX() + 150 + 80,
				  1150-30 - 35 - 64*27+ 20*11,
					      "ground");
		//makeShadowRectangle(int topLeftDudePosX, int topLeftDudePosY, int spreadX, int spreadY, int numDudesWidth, int numDudesHeight, float speed, boolean clockwise)
		makeShadowRectangle(currPlayer.getIntX() + -200 + 50 - 170 + 100 - 5-15 ,
			      1150-30 - 35 - 32 - 64*27 + 20*11 - 155, 
			      115,
			      110,
			      5,
			      2,
			      0.6f,
			      true);
		u = new lightDude(currPlayer.getIntX() + -200 + 50 - 170 + 210,1150-30 - 35 - 32 - 64*27 + 20*11 - 45);
		u.setFacingDirection("Left");
		u = new lightDude(currPlayer.getIntX() + -200 + 50 - 170 + 210+180,1150-30 - 35 - 32 - 64*27 + 20*11 - 45);
		u.setFacingDirection("Left");
		u = new lightDude(currPlayer.getIntX() + -200 + 70 - 170 + 210+180,1150-30 - 35 - 32 - 64*27 + 20*11 - 45-110);
		u.setFacingDirection("Right");
		u = new lightDude(currPlayer.getIntX() + -300 - 170 + 210+180,1150-30 - 35 - 32 - 64*27 + 20*11 - 45-110);
		u.setFacingDirection("Right");
				
		
		// Nineteen third
		spawnReloadableTombRect(currPlayer.getIntX() + -200 ,
				  1150-30 - 35 - 32 - 64*29 + 20*12,
			      currPlayer.getIntX() + 150,
			      1150-30 - 35 - 64*29+ 20*12,
					      "ground");
		
		
		// Nineteen fourth
		spawnReloadableTombRect(currPlayer.getIntX() + -200 + 50 ,
				1150-30 - 35 - 32 - 64*31 + 20*13,
			      currPlayer.getIntX() + 150 + 80,
			      1150-30 - 35 - 64*31+ 20*13,
					      "ground");
		u = new lightDude(currPlayer.getIntX() + -300 - 170 + 210+140,1150-30 - 35 - 32 - 64*27 + 20*11 - 45-110*2 + 2);
		u.setFacingDirection("Left");
		u = new lightDude(currPlayer.getIntX() + -300 - 170 + 210+300,1150-30 - 35 - 32 - 64*27 + 20*11 - 45-110*2 + 2);
		u.setFacingDirection("Left");
		
		// Fast guy
		u = new shadowDude(currPlayer.getIntX() + -300 - 210 + 210+140,1150-30 - 35 - 32 - 64*27 + 20*11 - 45-110*2 + 2);
		u.patrolTo(currPlayer.getIntX() + -200 - 170 + 400+140,1150-30 - 35 - 32 - 64*27 + 20*11 - 45-110*2 + 2);
		u.setMoveSpeed(5);
		

		
		// Nineteen fifth
		spawnReloadableTombRect(currPlayer.getIntX() + -200 ,
				1150-30 - 35 - 32 - 64*33 + 20*14,
			      currPlayer.getIntX() + 150,
			      1150-30 - 35 - 64*33+ 20*14,
					      "ground");
		
		for(int i = 0; i < 6; i++) {
			u = new shadowDude(currPlayer.getIntX() + -200 ,
					1150-30 - 35 - 32 - 64*33 + 20*14-47 - i*50);
			u.patrolTo(currPlayer.getIntX()+200,
					1150-30 - 35 - 32 - 64*33 + 20*14-47 - i*50);
			u.setMoveSpeed(1);
		}
		
		u = new lightDude(currPlayer.getIntX()+100,
				1150-30 - 35 - 32 - 64*33 + 20*14-47);
		u.patrolTo(currPlayer.getIntX() + -200 ,
				1150-30 - 35 - 32 - 64*33 + 20*14-47);
		u.setMoveSpeed(3.5f);
		
		// Nineteen Sixth
		spawnReloadableTombRect(currPlayer.getIntX() + -200 + 50 ,
				1150-30 - 35 - 32 - 64*35 + 20*15,
			      currPlayer.getIntX() + 150 + 80,
			      1150-30 - 35 - 64*35+ 20*15,
					      "ground");
		u = new lightDude(currPlayer.getIntX()-130,
				1150-30 - 35 - 32 - 64*35 + 20*15 - 47);
		u.setFacingDirection("Left");
		
		// Nineteen seventh
		spawnReloadableTombRect(currPlayer.getIntX() + -200 ,
				1150-30 - 35 - 32 - 64*37 + 20*16,
			      currPlayer.getIntX() + 150,
			      1150-30 - 35 - 64*37+ 20*16,
					      "ground");
		u = new lightDude(currPlayer.getIntX()-130,
				1150-30 - 35 - 32 - 64*37 + 20*16 - 47);
		u.setFacingDirection("Right");
		
		// Nineteen eighth
		spawnReloadableTombRect(currPlayer.getIntX() + -200 + 50 ,
				1150-30 - 35 - 32 - 64*39 + 20*17,
			      currPlayer.getIntX() + 150 + 80,
			      1150-30 - 35 - 64*39+ 20*17,
					      "ground");
		
		// 20th
		// Right
		spawnReloadableTombRect(currPlayer.getIntX()+100,
				-1103-170,
			    currPlayer.getIntX() + 100 + 32,
			    -1103-170+ 32,
				"ground");
		
		// Middle
		spawnReloadableTombRect(currPlayer.getIntX()-20,
				-1103-170-40,
			    currPlayer.getIntX() - 30 + 32+10,
			    -1103-170+ -40 + 32,
				"ground");
		
		// End the wall on right.
		spawnReloadableTombRect(currPlayer.getIntX()+200,
				-1103-70,
			    currPlayer.getIntX() + 200 + 32,
			    -1103-60+ 32*3,
				"ground");
		
		// End the wall on Left
			spawnReloadableTombRect(currPlayer.getIntX()-200-28,
						-1103-200,
					    currPlayer.getIntX()-180,
					    -1103-60+ 32*3,
						"ground");
			
		// 22
			
		// Left
		spawnReloadableTombRect(currPlayer.getIntX()-140,
				-1103-300-10,
			    currPlayer.getIntX()-32,
			    -1103-300-10+ 32,
				"ground");
		
		// Right
		spawnReloadableTombRect(currPlayer.getIntX() + 64-32,
				-1103-300-20-32-10,
			    currPlayer.getIntX() + 64 + 32*3,
			    -1103-300+ 32-20-32,
				"ground");
		
		for(int i = 0; i < 8; i++) {
			u = new shadowDude(currPlayer.getIntX(),
				-1103-300-40 - 47 - i*50);
		}
		u = new lightDude(currPlayer.getIntX(),
				-1103-300-40 - 47);
		u.patrolTo(currPlayer.getIntX(),
				-1103-300-40 - 47 - 8*50);
		u.setMoveSpeed(2);
		
		// 23
		spawnReloadableTombRect(currPlayer.getIntX() + 64+20-32,
				-1103-300-20-32-140+20+10,
			    currPlayer.getIntX() + 20+64,
			    -1103-300+ 32-20-32-140+20+10,
				"ground");
		
		// 24
		spawnReloadableTombRect(currPlayer.getIntX() -32-20+20,
				-1103-300-20-32-250+30+20,
			    currPlayer.getIntX()-20+20,
			    -1103-300+ 32-20-32-250+30+20,
				"ground");
		
		// End
		spawnReloadableTombRect(currPlayer.getIntX() + 64+20-32,
				-1103-300-20-32-340+20+30,
			    currPlayer.getIntX() + 20+200,
			    -1103-300+ 32-20-32-340+20+30,
				"ground");
		stairsUp tombExit = new stairsUp(currPlayer.getIntX() + 64+20-32+130,-1103-300-20-32-340+20-47+30,0,forest.getZone(),-394,-3915,"Left");
		tombExit.setZ(-1);
	
	}
	
	// Deal with the first well we encounters.
	public void dealWithRegionStuff() {
		player currPlayer = player.getPlayer();
		if(currPlayer != null && currPlayer.isWithin(9335+600,744,9817+600,1268) && shadowElevatorStarted!=null && !shadowElevatorStarted.isCompleted()) {
			shadowElevatorStarted.setCompleted(true);
			shadowElevatorFirstTime = true;
			saveState.setQuiet(true);
			saveState.createSaveState();
			saveState.setQuiet(false);
		}
	}
	
	// Deal with shadow elevator stuff
	public void dealWithShadowElevatorStuff() {
		
		// It's game time, bro!! turner!! wooo!
		if(shadowElevatorStarted!=null && shadowElevatorStarted.isCompleted()) {

			if(!shadowElevatorInitiated && zoneLoaded) {
				
				// If it's the first time.
				if(shadowElevatorFirstTime) {
					music.currMusic.fadeOut(5f);
					shadowElevatorFirstTime = false;
					playerOne.initiateShadowElevatorScene();
					shadowElevatorInitiated = true;
				}
				else {
					playerOne.initiateShadowElevatorScene();
					playerOne.setSequenceTo(10);
					shadowElevatorInitiated = true;
				}
			}
		}
		
	}
	
	// Do zone specific tasks that aren't monitored by
	// zone specific units. 
	@Override
	public void update() {
		dealWithRegionStuff();
		dealWithShadowElevatorStuff();
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