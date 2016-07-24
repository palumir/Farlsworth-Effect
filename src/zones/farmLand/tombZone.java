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
	
public void makeShadowRectangle(int topLeftDudePosX, int topLeftDudePosY, int spreadX, int spreadY, int numDudesWidth, int numDudesHeight, float speed, boolean clockwise) {
		
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
	
	public void shadowDudePatrol(int shadowDudeX, int shadowDudeY, int patrolToX, int patrolToY, float moveSpeed) {
		u = new shadowDude(shadowDudeX,shadowDudeY);
		u.patrolTo(patrolToX, patrolToY);
		u.setMoveSpeed(moveSpeed);
	}
	
	public void lightDudePatrol(int lightDudeX, int lightDudeY, int patrolToX, int patrolToY, float moveSpeed) {
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
		
		// First floor
		
		spawnTombRect(350,40,500,791,"ground");
		
		c = new wallTorch(410,-40);
		
		shadowDudePatrol (350,-6,455,-6,2);
		
		// Second floor
		
		spawnTombRect(550,40,1000,791,"ground");
		
		c = new wallTorch(617,-40);
		c = new wallTorch(928,-40);
		
		for(int i=0; i <2; i++) {
			u = new shadowDude(695,-6 - i*50);
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
			u = new shadowDude(1370,-6 - i*50);
			path = new ArrayList<intTuple>();
			path.add(new intTuple(1500,-6 - i*50));
			path.add(new intTuple(1080, -6 - i*50));
			u.patrolPath(path);
			u.setMoveSpeed(3);
		}
			
		c = new wallTorch(1523,-40);
		
		for(int i=0; i <3; i++) {
			u = new shadowDude(1870,-6 - i*50);
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
			path = new ArrayList<intTuple>();
			path.add(new intTuple(2525,-6 - i*50));
			path.add(new intTuple(3285,-6 - i*50));
			shadowDudePatrolPath(2800,-6 - i*50, path, 5);
		}
		
		lightDudePatrol (2603,-6, 3300, -6,3);
		
		c = new wallTorch(3311,-40);
		
		for(int i=0; i <3; i++) {
			path = new ArrayList<intTuple>();
			path.add(new intTuple(3311,-6 - i*50));
			path.add(new intTuple(4075,-6 - i*50));
			shadowDudePatrolPath(3700,-6 - i*50, path, 5);
		}
		
		lightDudePatrol (3388,-6,4085,-6,3);
		
		c = new wallTorch(4097,-40);
		
		// Fifth floor
		
		spawnTombRect(4220+15,40,4340+15,791,"ground");
		
		c = new well(4252,4,0);
		c.setPassable(true);
		c.setZ(-2);
		
		// Sixth floor
		
		spawnTombRect(4863,-69,4895,517,"rightWall");
		
		spawnTombRect(4330,155,4800,188,"ground");
		shadowDudePatrol (4335,110,4374,110,2);
		makeShadowSquare(4400,60,70,75,3,true);
		makeShadowSquare(4700,60,70,75,3,true);
		
		spawnTombRect(4519,308,4893,340,"ground");
		shadowDudePatrol (4519,261,4857,261,2);
		
		spawnTombRect(4430,460,4815,492,"ground");
		shadowDudePatrol (4510,343,4510,415,2);
		makeShadowSquare(4575,340,70,75,3,false);
		shadowDudePatrol (4785,343,4785,415,2);
		
		// Seventh floor
		
		spawnTombRect(4863,615,4895,1070,"leftWall");
		
		spawnTombRect(5572,-69,5604,1100,"rightWall");
		
		c = new well(4840, 560,0);
		c.setPassable(true);
		c.setZ(-2);
		
		spawnTombRect(4794,596,5465+32,628,"ground");
		makeShadowSquareTopRight (4998,470, 77, 82,2 ,true);
		makeShadowSquareTopLeft (5198,470, 77, 82,2 ,false);
		
		spawnTombRect(4972+32,811,5604,843,"ground");
		makeShadowRectangle (4950,764,135,125,5,3,2,false);
		
		spawnTombRect(4888,1061,5480,1093,"ground");
		for(int i=0; i <4; i++) {
			u = new shadowDude(5123,1014 - i*50);
		}
		for(int i=0; i <4; i++) {
			u = new shadowDude(5323,1014 - i*50);
		}
		lightDudePatrol (5015,890,5131,890,0.7f);
		lightDudePatrol (5314,890,5460,890,0.7f);
		
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
		
		makeShadowSquare(7720,1017,99,99,2.8f,false);
		
		spawnTombRect(8082+73,1062,8300,1094,"ground");
		
		// Middle
		
		spawnTombRect(6353,1162,7103,1194,"ground");
		shadowDudePatrol (6351,1117,6682,1117,3);
		shadowDudePatrol (6844,1095,6844,1117,1);
		shadowDudePatrol (7037,1117,7037,1095,1);
		lightDudePatrol (6796, 1117, 7070,1117,3.5f);
		
		spawnTombRect(7103+73,1162,7303,1194,"ground");
		shadowDudePatrol (7169,1117,7350,1117,4);
		
		spawnTombRect(7376,1162,7464,1194,"ground");
		
		u = new shadowDude(8206,1117);

		
		// Bottom
		
		spawnTombRect(6323,1262,7376,1294,"ground");
		shadowDudePatrol (6702,1215,6325,1215,3);
		shadowDudePatrol (6752,1215,7010,1215,3);
		u = new shadowDude(7060,1215);
		
		spawnTombRect(7376+73,1262,8300,1294,"ground");
		makeShadowSquare(7494,1120,75,80,2.5f,true);
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
		
		spawnTombRect(9073,1150,9500,2374,"ground");
		spawnTombRect(9481,512,9999,1154,"rightWall");
		spawnTombRect(9481,1152,11000,1502,"none");
		
		//brady Turner is fucking faggot wooo!
		
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