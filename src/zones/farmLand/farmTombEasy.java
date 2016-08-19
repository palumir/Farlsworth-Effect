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
import terrain.chunkTypes.tomb;
import terrain.chunkTypes.tombEdge;
import units.player;
import units.unit;
import units.bosses.playerOne;
import units.unitCommands.commandList;
import units.unitCommands.commands.moveCommand;
import units.unitCommands.commands.waitCommand;
import units.unitTypes.tomb.lightDude;
import units.unitTypes.tomb.shadowDude;
import utilities.intTuple;
import utilities.saveState;
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
	static commandList commands;
	
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
	
	 // Elevator loaded?
	static boolean elevatorLoaded = false;
	
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
		
		// Shadow elevator not loaded
		elevatorLoaded = false;
		
		// Set the mode of the zone of course.
		platformer.setMode();
		
		// Set the darkness.
		zoneFog = new fog();
		zoneFog.setTo(0.4f);//fog.setTo(0.75f);
		
		// Elevator not initiated
		shadowElevatorInitiated = false;
		
		// Load zone events.
		loadZoneEvents();
				
		// Background
		background.setGameBackground(DEFAULT_ZONE_BACKGROUND);
		
		// Spawn area.
		createSpawnArea();
		
		// Play zone music.
		if(!shadowElevatorStarted.isCompleted()) { music.startMusic(zoneMusic);  }
		else {
			// Load elevator
			createShadowElevatorAroundPlayer(true);
		}
		
		// Sort chunks.
		chunk.sortChunks();
		
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
		
		commandList squareCommands = new commandList();
		for(int i = 0; i < numDudesWidth; i++) {
			squareCommands.add(new moveCommand(i,0));
		}
		for(int i = 0; i < numDudesHeight; i++) {
			squareCommands.add(new moveCommand(numDudesWidth-1,i));
		}
		for(int i = numDudesWidth-1; i > 0; i--) {
			squareCommands.add(new moveCommand(i,numDudesHeight-1));
		}
		for(int i = numDudesHeight-1; i > 0; i--) {
			squareCommands.add(new moveCommand(0,i));
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
		    		  commands = new commandList();
		    		  
		    		  // Searches squareCommands for our shadowDude's start position.
		    		  int n = 0;
		    		  for(; n < squareCommands.size(); n++) {
		    			  moveCommand currentTuple = (moveCommand)squareCommands.get(n);
		    			  if(currentTuple.getX() == i && currentTuple.getY() == j) break;
		    		  }
		    		  
		    		  if(clockwise) n++;
		    		  if(!clockwise) n--;
		    		  
		    		  // Walk him on the path.
		    		  for(int m = 0; m < squareCommands.size()-1; m++) {
		    			  if(n >= squareCommands.size()) n = 0;
		    			  if(n < 0) n = squareCommands.size() - 1;
		    			  moveCommand currentTuple = (moveCommand)squareCommands.get(n);
		    			  commands.add(new moveCommand(topLeftDudePosX + currentTuple.getX()*spreadOutX,topLeftDudePosY + currentTuple.getY()*spreadOutY));
		    			  if(clockwise) n++;
		    			  if(!clockwise) n--;
		    		  }
		    		  
		    		  commands.add(new moveCommand(u.getIntX(),u.getIntY()));
		    		  u.repeatCommands(commands);
		    	  }
		      }
			}
	}
	
	public void makeShadowSquarePosMissing(int topLeftDudePosX, int topLeftDudePosY, int spreadX, int spreadY, float speed, boolean clockwise, int missingPosX, int missingPosY) {
		
		commandList squareCommands = new commandList();
		squareCommands.add(new moveCommand(0,0));
		squareCommands.add(new moveCommand(1,0));
		squareCommands.add(new moveCommand(2,0));
		squareCommands.add(new moveCommand(2,1));
		squareCommands.add(new moveCommand(2,2));
		squareCommands.add(new moveCommand(1,2));
		squareCommands.add(new moveCommand(0,2));
		squareCommands.add(new moveCommand(0,1));
		
		for(int i = 0; i < 3; i++) {
		      for(int j = 0; j < 3; j++) {
		       
		    	  if(i == 1 && j == 1) {
		    		  // Do nothing.
		    	  }
		    	  else if(i == missingPosX && j==missingPosY) {
		    		  // Do nothing.
		    	  }
		    	  else {
		    		  
		    		  // How far they are spread
		    		  int spreadOutX = spreadX;
		    		  int spreadOutY = spreadY;
		    		    
		    		  // Spawn the shadow dude
		    		  u = new shadowDude(topLeftDudePosX + i*spreadOutX,topLeftDudePosY + spreadOutY*j);
		    		  u.setMoveSpeed(speed);
		    		  commands = new commandList();
		    		  
		    		  // Searches squareCommands for our shadowDude's start position.
		    		  int n = 0;
		    		  for(; n < squareCommands.size(); n++) {
		    			  moveCommand currentTuple = (moveCommand)squareCommands.get(n);
		    			  if(currentTuple.getX() == i && currentTuple.getY() == j) break;
		    		  }
		    		  
		    		  if(clockwise) n++;
		    		  if(!clockwise) n--;
		    		  
		    		  // Walk him on the path.
		    		  for(int m = 0; m < squareCommands.size()-1; m++) {
		    			  if(n >= squareCommands.size()) n = 0;
		    			  if(n < 0) n = squareCommands.size() - 1;
		    			  moveCommand currentTuple = (moveCommand)squareCommands.get(n);
		    			  commands.add(new moveCommand(topLeftDudePosX + currentTuple.getX()*spreadOutX,topLeftDudePosY + currentTuple.getY()*spreadOutY));
		    			  if(clockwise) n++;
		    			  if(!clockwise) n--;
		    		  }
		    		  
		    		  commands.add(new moveCommand(u.getIntX(),u.getIntY()));
		    		  u.repeatCommands(commands);
		    	  }
		      }
			}
	}
	
	
	public void shadowDudePatrolPath(int shadowDudeX, int shadowDudeY, commandList commands2, float moveSpeed) {
		u = new shadowDude(shadowDudeX,shadowDudeY);
		u.repeatCommands(commands2);
		u.setMoveSpeed(moveSpeed);
	}
	
	public static void shadowDudePatrol(int shadowDudeX, int shadowDudeY, int patrolToX, int patrolToY, float moveSpeed) {
		u = new shadowDude(shadowDudeX,shadowDudeY);
		commands = new commandList();
		commands.add(new moveCommand(patrolToX, patrolToY));
		commands.add(new moveCommand(shadowDudeX, shadowDudeY));
		u.repeatCommands(commands);
		u.setMoveSpeed(moveSpeed);
	}
	
	public static void lightDudePatrol(int lightDudeX, int lightDudeY, int patrolToX, int patrolToY, float moveSpeed) {
		u = new lightDude (lightDudeX,lightDudeY);
		commands = new commandList();
		commands.add(new moveCommand(patrolToX, patrolToY));
		commands.add(new moveCommand(lightDudeX, lightDudeY));
		u.repeatCommands(commands);
		u.setMoveSpeed(moveSpeed);
	}
	
	//////////////////////
	// INDIVIDUAL AREAS //
	//////////////////////
	public void createTerrain() {
		
	}
	
	public void createSpawnArea() {
		
		/////////////////////////
		//////// ENTRANCE ///////
		/////////////////////////
		
		// Entrance
		stairsUp tombZoneEnterance = new stairsUp(30,-8,0,sheepFarm.getZone(),2320,-3896,"Down");
		tombZoneEnterance.setZ(BACKGROUND_Z);
		
		// Roof
		/*spawnTombRect(-18, -747, 7500,-220,"roof"); // the roof
		spawnTombRect(-500+2, -747, 0,-220,"none"); // roof dirt top left
	
		// Floor
		spawnTombRect(-18,40,300,820,"ground");
		spawnTombRect(-500+2,46,0,820,"none");
		
		// Left wall
		spawnTombRect(-338,-242,7,60,"leftWall");
		
		// Right wall
		spawnTombRect(7000,-750,7000,1000,"rightWall");*/
		
		// Kill player when they fall
		/*u = new shadowDude(270,116);
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

///////////////////////////////////
////// SHADOW TUTORIAL AREA //////
/////////////////////////////////
		
		// First floor
		
		spawnTombRect(350,40,500,791,"ground");
		
		shadowDudePatrol (350,-6,455,-6,1f);
		
		// Second floor
		
		spawnTombRect(550,40,1000,791,"ground");
		
		c = new wallTorch(617,-40);
		c = new wallTorch(928,-40);
		
		for(int i=0; i <3; i++) {
			u = new shadowDude(620,-6 - i*50);
			commands = new commandList();
			commands.add(new moveCommand(555,-6 - i*50));
			commands.add(new moveCommand(970,-6 - i*50));
			commands.add(new moveCommand(620,-6 - i*50));
			u.repeatCommands(commands);
			u.setMoveSpeed(3);
		}
		
		// Third floor
		
		spawnTombRect(1080,40,2380,791,"ground");
		
		c = new wallTorch(1141,-40);
		
		for(int i=0; i < 2; i++) {
			u = new shadowDude(1320,-6 - i*50);
			commands = new commandList();
			commands.add(new moveCommand(1500,-6 - i*50));
			commands.add(new moveCommand(1080, -6 - i*50));
			commands.add(new moveCommand(1320,-6 - i*50));
			u.repeatCommands(commands);
			u.setMoveSpeed(3);
		}
			
		c = new wallTorch(1523,-40);
		
		for(int i=0; i <3; i++) {
			u = new shadowDude(1707,-6 - i*50);
			commands = new commandList();
			commands.add(new moveCommand(1880,-6 - i*50));
			commands.add(new moveCommand(1523,-6 - i*50));
			commands.add(new moveCommand(1707,-6 - i*50));
			u.repeatCommands(commands);
			u.setMoveSpeed(3);
		}
		
		c = new wallTorch(1905,-40);
		
		for(int i=0; i <2; i++) {
			u = new shadowDude(2285,-6 - i*50);
			commands = new commandList();
			commands.add(new moveCommand(1905,-6 - i*50));
			commands.add(new moveCommand(2325,-6 - i*50));
			commands.add(new moveCommand(2285,-6 - i*50));
			u.repeatCommands(commands);
			u.setMoveSpeed(3);
		}	
		
		c = new wallTorch(2287,-40);
		
		// Fourth floor
		
		spawnTombRect(2460,40,4160,791,"ground");
		
		c = new wallTorch(2525,-40);
		
		for(int i=0; i <3; i++) {
			commands = new commandList();
			commands.add(new moveCommand(2525,-6 - i*50));
			commands.add(new moveCommand(3285,-6 - i*50));
			commands.add(new moveCommand(2800,-6 - i*50));
			shadowDudePatrolPath(2800,-6 - i*50, commands, 5);
		}
		
		u = new lightDude(2583,-6);
		commands = new commandList();
		commands.add(new moveCommand(2580,-6));
		commands.add(new moveCommand(3235, -6));
		u.repeatCommands(commands);
		u.setMoveSpeed(3.5f);
		
		c = new wallTorch(3311,-40);
		
		for(int i=0; i <3; i++) {
			commands = new commandList();
			commands.add(new moveCommand(3301,-6 - i*50));
			commands.add(new moveCommand(4075,-6 - i*50));
			shadowDudePatrolPath(3700,-6 - i*50, commands, 5);
		}
		
		u = new lightDude(3813,-6);
		commands = new commandList();
		commands.add(new moveCommand(4017,-6));
		commands.add(new moveCommand(3375,-6));
		u.repeatCommands(commands);
		u.setMoveSpeed(3.5f);
		
		c = new wallTorch(4097,-40);
		
////////////////////////////////
////// SHADOW FIRST AREA //////
//////////////////////////////
		
		// First floor
		
		spawnTombRect(4220+15,40,4340+15,791,"ground");
		
		c = new well(4252,4,0);
		c.setPassable(true);
		c.setZ(-2);
		
		// Second floor
		
		spawnTombRect(4863+32,-69,4895+32,517,"rightWall");
		
		spawnTombRect(4330,155,4832,188,"ground");
		makeShadowSquarePosMissing(4400,40,75,80,2.7f,true,0,2);
		makeShadowSquarePosMissing(4700,40,75,80,2.7f,true,2,0);
		
		spawnTombRect(4519-8,308+32,4893+24,340+32,"ground");
		u = new shadowDude(4750,293);
		commands = new commandList();
		commands.add(new moveCommand(4857,293));
		commands.add(new moveCommand(4519,293));
		u.repeatCommands(commands);
		u.setMoveSpeed(2);
		
		spawnTombRect(4330,460+48,4815,492+48,"ground");
		u = new shadowDude(4500,420);
		commands = new commandList();
		commands.add(new moveCommand(4500,391-20));
		commands.add(new moveCommand(4500,463));
		u.repeatCommands(commands);
		u.setMoveSpeed(1.2f);
		makeShadowSquarePosMissing(4575,340+48,75,80,2.7f,false,0,0);
		shadowDudePatrol (4790,391-20,4790,415+48,1.2f);
		u = new shadowDude(4790,550);
		u.setFacingDirection("Down");
		
////////////////////////////////
//////SHADOW SECOND AREA //////
//////////////////////////////

		// First floor
		
		spawnTombRect(4864,615-32-2+32,4896,1070+32+24-2,"leftWall");
		
		spawnTombRect(5572,-69,5604,1100,"rightWall");
		
		c = new well(4843, 560,0);
		c.setPassable(true);
		c.setZ(-2);
		
		shadowDudePatrol(4334,110,4370,110,4f);
		spawnTombRect(4794,596,5465+32,628,"ground");
		makeShadowSquarePosMissing (4998,470, 80, 85,2f ,true,0,2);
		makeShadowSquarePosMissing (5198,470, 80, 85,2f ,false,2,2);
		
		spawnTombRect(5004-8,811,5604-8,843,"ground");
		makeShadowRectangle (4965,764,125,125,5,3,1.5f,false);
		u = new shadowDude(5448,1130-14);
		u.setFacingDirection("Down");
		
		spawnTombRect(4888,1061,5480,1093,"ground");
		for(int i=0; i <4; i++) {
			u = new shadowDude(5123,1014 - i*50);
		}
		for(int i=0; i <4; i++) {
			u = new shadowDude(5323,1014 - i*50);
		}
		lightDudePatrol (5000,890,5160,890,0.82f);
		lightDudePatrol (5400,890,5310,890,0.82f);
		
		// Second floor
		
		spawnTombRect(5450,1162,6274,1194,"ground");
		c = new well(5555,1126,0);
		c.setPassable(true);
		c.setZ(-2);
		
////////////////////////////////
//////SHADOW PUZZLE AREA //////
//////////////////////////////
		
		// First floor (3 floors)
		
		// Top
		
		spawnTombRect(6393,1062,6727,1094,"ground");
		for(int i=0; i <2; i++) {
			commands = new commandList();
			commands.add(new moveCommand(6396,1016 - i*50));
			commands.add(new moveCommand(6692,1016 - i*50));
			shadowDudePatrolPath(6692,1016 - i*50, commands ,3);
		}
		
		spawnTombRect(6800,1062,7494+32+32,1094,"ground");
		
		for(int i=0; i <3; i++) {
			commands = new commandList();
			commands.add(new moveCommand(7086,1016 - i*50));
			commands.add(new moveCommand(6899,1016 - i*50));
			shadowDudePatrolPath(6899,1016 - i*50, commands ,2);
		}
		for(int i=0; i <3; i++) {
			commands = new commandList();
			commands.add(new moveCommand(7293,1016 - i*50));
			commands.add(new moveCommand(7106,1016 - i*50));
			shadowDudePatrolPath(7106,1016 - i*50, commands ,2);
		}
		for(int i=0; i <3; i++) {
			commands = new commandList();
			commands.add(new moveCommand(7500,1016 - i*50));
			commands.add(new moveCommand(7313,1016 - i*50));
			shadowDudePatrolPath(7313,1016 - i*50, commands ,2);
		}
		
		lightDudePatrol (6884, 926, 7515+32,926, 1.4f);
		
		spawnTombRect(7494+73+32,1062,8082,1094,"ground");
		
		for(int i=0; i <3; i++) {
			u = new shadowDude (7656,1016 - i*50);
		}
		
		makeShadowSquarePosMissing(7770,1017,99,99,2.7f,false,0,0);
		
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
		makeShadowSquarePosMissing(7494,1120,80,85,2f,true,0,0);
		u = new shadowDude(8206,1218);
		
		
//////////////////////////////////
//////STRAIGHT FLOOR JUMP AREA //
////////////////////////////////
		
		spawnTombRect(7606+73,1162,10000,1194,"ground");
		
		c = new well(8207,1026,0);
		c.setPassable(true);
		c.setZ(-2);
		
		for(int i=0; i <2; i++) {
			commands = new commandList();
			commands.add(new moveCommand(8396,1116 - 75 - i*50));
			commands.add(new moveCommand(8396,1116 - i*50));
			shadowDudePatrolPath(8396,1116 - i*50, commands, 1);
		}
		
		for(int i=0; i <2; i++) {
			commands = new commandList();
			commands.add(new moveCommand(8486,1116 - 120 - i*50));
			commands.add(new moveCommand(8486,1116 - 175 - i*50));
			shadowDudePatrolPath(8486,1116 - 175 - i*50, commands, 1);
		}
		
		u = new shadowDude(8486,1116);
		
		for(int i=0; i <2; i++) {
			commands = new commandList();
			commands.add(new moveCommand(8576,1116 - i*50));
			commands.add(new moveCommand(8576,1116 - 75 - i*50));
			shadowDudePatrolPath(8576,1116 - 75 - i*50, commands ,1);
		}
		
		for(int i=0; i <2; i++) {
			commands = new commandList();
			commands.add(new moveCommand(8656 + i*40 + 50,1116 - 75));
			commands.add(new moveCommand(8656 + i*40,1116 - 75));
			shadowDudePatrolPath(8656 + i*40,1116 - 75, commands ,1);
		}
		
		shadowDudePatrol (8656,1116,8902,1116,1);
		
		for(int i=0; i <2; i++) {
			commands = new commandList();
			commands.add(new moveCommand(8842 + i*40 + 50,1116 - 75));
			commands.add(new moveCommand(8842 + i*40,1116 - 75));
			shadowDudePatrolPath(8842 + i*40,1116 - 75, commands ,1);
		}
		
		for(int i = 0; i < 30; i++) {
			shadowDudePatrol (9038+i*30,1116-230,9038+i*30,1116,1.2f);
		}
		u = new shadowDude(9038+3*30,1116);
		u.setFacingDirection("Left");
		u = new shadowDude(9038+3*30,1116-50*1);
		u.setFacingDirection("Left");
		
		u = new shadowDude(9038+9*30,1116);
		u.setFacingDirection("Left");
		u = new shadowDude(9038+9*30,1116-50*1);
		u.setFacingDirection("Left");
		
		u = new lightDude(9038+14*30,1116);
		u.setFacingDirection("Left");
		
		u = new shadowDude(9038+18*30,1116);
		u.setFacingDirection("Left");
		u = new shadowDude(9038+19*30,1116);
		u.setFacingDirection("Left");
		
		u = new shadowDude(9038+22*30,1116);
		u.setFacingDirection("Left");
		u = new shadowDude(9038+23*30,1116);
		u.setFacingDirection("Left");
		
		u = new shadowDude(9038+26*30,1116);
		u.setFacingDirection("Left");
		u = new shadowDude(9038+26*30,1116-50);
		u.setFacingDirection("Left");
		
		// Exit
		c = new well(10066,1113,0);
		c.setPassable(true);
		c.setZ(-2);
		spawnTombRect(10050,1150,13000,2374,"ground");
		//spawnTombRect(9962,512,10500,1154,"rightWall");
		//spawnTombRect(9965,1152,10500,1502,"none");*/
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
	
/////////////////////////////
//////SHADOW ELEVATOR //////
///////////////////////////
	
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
		
		if(!elevatorLoaded) {
			// Left wall.
			shadowElevator = createRectangleOfShadows(11500 - 580, 
													  1150-30 - 1500, 
													  11500 - 250,
													  1150-30 + 600, eyeless);
			
			// Floor
			shadowElevator.addAll(createRectangleOfShadows(11500 - 220-25+1, 
					  1150-30+200+3-92, 
					  11500 +250,
					  1150-30 + 600, eyeless));
			
			// Right wall.
			shadowElevator.addAll(createRectangleOfShadows(11500 + 260, 
													  1150-30 - 1500, 
													  11500 + 600,
													  1150-30 + 600, eyeless));
			
			// First platform
			spawnReloadableTombRect(11500 -80,
					      1150-30 - 35 - 32-20,
					      11500-30,
					      1150-30 - 35,
					      "ground");
			
			// Third platform
			spawnReloadableTombRect(11500,
					      1150-30 - 35 - 32 - 64*2+3,
					      11500 + 200,
						  1150-30 - 35 - 64*2+3,
							      "ground");
			
			// Fourth platform
			spawnReloadableTombRect(11500 + 170,
					      1150-30 - 35 - 32 - 64*4 + 20,
					      11500 + 220,
						  1150-30 - 35 - 64*4 + 20,
							      "ground");
			
			// Fifth platform
			spawnReloadableTombRect(11500 - 200,
					      1150-30 - 35 - 32 - 64*5,
					      11500 + 150,
						  1150-30 - 35 - 64*5,
							      "ground");
			shadowDudePatrol(11500 - 210, 1150-30 - 35 - 32 - 64*5 - 46, 11500 + 100, 1150-30 - 35 - 32 - 64*5 - 46, 1.5f);
			shadowDudePatrol(11500 - 210, 1150-30 - 35 - 32 - 64*5 - 46 - 50, 11500 + 100, 1150-30 - 35 - 32 - 64*5 - 46-50, 2.8f);
			
			// Sixth platform
			spawnReloadableTombRect(11500 - 200,
					      1150-30 - 35 - 32 - 64*7 + 20,
					      11500 -150,
						  1150-30 - 35 - 64*7 + 20,
							      "ground");
			
			// Seventh platform
			spawnReloadableTombRect(11500 - 140,
					      1150-30 - 35 - 32 - 64*8,
					      11500 + 150,
						  1150-30 - 35 - 64*8,
							      "ground");
			u = new shadowDude(11500 + 5 - 10,
				      1150-30 - 35 - 32 - 64*8 - 46);
			u.setFacingDirection("Left");
			u = new shadowDude(11500 + 5 - 10,
				      1150-30 - 35 - 32 - 64*8 - 46 - 50);
			u.setFacingDirection("Left");
			u = new shadowDude(11500 + 5 - 10,
				      1150-30 - 35 - 32 - 64*8 - 46 - 50*2);
			u.setFacingDirection("Left");
			lightDudePatrol(11500 - 140,
				      1150-30 - 35 - 32 - 64*8 - 46 - 50*2 + 10,
				      11500 + 130,
				      1150-30 - 35 - 32 - 64*8 - 46 - 50*2 + 10,
				      2.6f);
			
			// Eighth Platform
			spawnReloadableTombRect(11500 + 150,
				      1150-30 - 35 - 32 - 64*10 + 20,
				      11500 + 200,
					  1150-30 - 35 - 64*10 + 20,
						      "ground");
			
			// Ninth Platform
			spawnReloadableTombRect(11500 + 200,
						      1150-30 - 35 - 32 - 64*12 + 40,
						      11500 + 250,
							  1150-30 - 35 - 64*12 + 40,
								      "ground");
			
			// Ten
			spawnReloadableTombRect(11500 + 130,
						      1150-30 - 35 - 32 - 64*14 + 20*3,
						      11500 + 170,
							  1150-30 - 35 - 64*14 + 20*3,
								      "ground");
			
			// Eleven
			spawnReloadableTombRect(11500 - 10 ,
						      1150-30 - 35 - 32 - 64*13 + 20*2,
						      11500 + 32 - 10,
							  1150-30 - 35 - 64*13 + 20*2,
								      "ground");
			
			// Twelve
			spawnReloadableTombRect(11500 - 150 ,
						      1150-30 - 35 - 32 - 64*13 + 20*2,
						      11500 + 40 - 150,
							  1150-30 - 35 - 64*13 + 20*2,
								      "ground");
			
			// Thirteen
			spawnReloadableTombRect(11500 - 230 ,
						      1150-30 - 35 - 32 - 64*15 + 20*3,
						      11500 - 190,
							  1150-30 - 35 - 64*15 + 20*3,
								      "ground");
			
			// Fourteen
			spawnReloadableTombRect(11500 - 150 ,
						      1150-30 - 35 - 32 - 64*17 + 20*5,
						      11500 - 150 + 40,
							  1150-30 - 35 - 64*17 + 20*5,
								      "ground");
			shadowDudePatrol(11500 - 200 ,
							1150-30 - 35 - 32 - 64*17 + 20*5 + 100, 
							11500 + 220,
							1150-30 - 35 - 32 - 64*17 + 20*5 + 100,
							2f);
			
			// Fifteen
			spawnReloadableTombRect(11500 - 80 ,
						      1150-30 - 35 - 32 - 64*19 + 20*7,
						      11500 + 220,
							  1150-30 - 35 - 64*19 + 20*7,
								      "ground");
			for(int i = 0; i < 16; i++) {
				shadowDudePatrol(11500 - 230 + i*30,
						1150-30 - 35 - 32 - 64*19 + 20*7 - 46-20, 
						11500  - 230 + i*30,
						1150-30 - 35 - 32 - 64*19 + 20*7 - 46 - 50*3,
						2f);
			}
			u = new lightDude(11500 + 70, 1150-30 - 35 - 32 - 64*19 + 20*7 - 46);
			u.setFacingDirection("Left");
			
			// Sixteen
			spawnReloadableTombRect(11500 + 200 ,
						      1150-30 - 35 - 32 - 64*21 + 20*8,
						      11500 + 240,
							  1150-30 - 35 - 64*21+ 20*8,
								      "ground");
			u = new lightDude(11500 + 200 + 15 - 40, 1150-30 - 35 - 32 - 64*21 + 20*10 - 46);
			commands = new commandList();
			commands.add(new moveCommand(11500 + 200 + 15 - 40,1150-30 - 35 - 32 - 64*19 + 20*7 - 200));
			commands.add(new moveCommand(11500 + 200 + 15 - 40, 1150-30 - 35 - 32 - 64*21 + 20*10 - 46));
			u.repeatCommands(commands);
			u.setMoveSpeed(1f);
			
			// Seventeen
			spawnReloadableTombRect(11500 + 160 ,
						      1150-30 - 35 - 32 - 64*23 + 20*9,
						      11500 + 200,
							  1150-30 - 35 - 64*23+ 20*9,
								      "ground");
			
			// Eighteen
			spawnReloadableTombRect(11500 + 160 ,
						      1150-30 - 35 - 32 - 64*23 + 20*9,
						      11500 + 200,
							  1150-30 - 35 - 64*23+ 20*9,
								      "ground");
			
			// Nineteen
			spawnReloadableTombRect(11500 + -200 ,
						      1150-30 - 35 - 32 - 64*25 + 20*10,
						      11500 + 150,
							  1150-30 - 35 - 64*25+ 20*10,
								      "ground");
			
			// Left wall
			spawnReloadableTombRect(11500 + -228 ,
				      1150-30 - 35 - 32 - 64*25 + 20*10 - 480 - 32*9,
				      11500 -190,
					  1150-30 - 35 - 64*25+ 20*10,
						      "none");
			
			// Right wall
			spawnReloadableTombRect(11500 + +200 ,
				      1150-30 - 35 - 32 - 64*25 + 20*10 - 480 - 32*9,
				      11500 +250,
					  1150-30 - 35 - 64*25+ 20*10,
						      "none");
			
			// Nineteen second platform
			spawnReloadableTombRect(11500 + -200 + 50 ,
				      1150-30 - 35 - 32 - 64*27 + 20*11,
				      11500 + 150 + 80,
					  1150-30 - 35 - 64*27+ 20*11,
						      "ground");
			//makeShadowRectangle(int topLeftDudePosX, int topLeftDudePosY, int spreadX, int spreadY, int numDudesWidth, int numDudesHeight, float speed, boolean clockwise)
			makeShadowRectangle(11500 + -200 + 50 - 170 + 100 - 5-15 ,
				      1150-30 - 35 - 32 - 64*27 + 20*11 - 155, 
				      115,
				      110,
				      5,
				      2,
				      0.6f,
				      true);
			u = new lightDude(11500 + -200 + 50 - 170 + 210,1150-30 - 35 - 32 - 64*27 + 20*11 - 45);
			u.setFacingDirection("Left");
			u = new lightDude(11500 + -200 + 50 - 170 + 210+180,1150-30 - 35 - 32 - 64*27 + 20*11 - 45);
			u.setFacingDirection("Left");
			u = new lightDude(11500 + -200 + 70 - 170 + 210+180,1150-30 - 35 - 32 - 64*27 + 20*11 - 45-110);
			u.setFacingDirection("Right");
			u = new lightDude(11500 + -300 - 170 + 210+180,1150-30 - 35 - 32 - 64*27 + 20*11 - 45-110);
			u.setFacingDirection("Right");
					
			
			// Nineteen third
			spawnReloadableTombRect(11500 + -200 ,
					  1150-30 - 35 - 32 - 64*29 + 20*12,
				      11500 + 150,
				      1150-30 - 35 - 64*29+ 20*12,
						      "ground");
			
			
			// Nineteen fourth
			spawnReloadableTombRect(11500 + -200 + 50 ,
					1150-30 - 35 - 32 - 64*31 + 20*13,
				      11500 + 150 + 80,
				      1150-30 - 35 - 64*31+ 20*13,
						      "ground");
			u = new lightDude(11500 + -300 - 170 + 210+140,1150-30 - 35 - 32 - 64*27 + 20*11 - 45-110*2 + 2);
			u.setFacingDirection("Left");
			u = new lightDude(11500 + -300 - 170 + 210+300,1150-30 - 35 - 32 - 64*27 + 20*11 - 45-110*2 + 2);
			u.setFacingDirection("Left");
			
			// Fast guy
			u = new shadowDude(11500 + -300 - 210 + 210+140,1150-30 - 35 - 32 - 64*27 + 20*11 - 45-110*2 + 2);
			commands = new commandList();
			commands.add(new moveCommand(11500 + -200 - 170 + 400+140,1150-30 - 35 - 32 - 64*27 + 20*11 - 45-110*2 + 2));
			commands.add(new moveCommand(11500 + -300 - 210 + 210+140,1150-30 - 35 - 32 - 64*27 + 20*11 - 45-110*2 + 2));
			u.repeatCommands(commands);
			u.setMoveSpeed(5);
			
	
			
			// Nineteen fifth
			spawnReloadableTombRect(11500 + -200 ,
					1150-30 - 35 - 32 - 64*33 + 20*14,
				      11500 + 150,
				      1150-30 - 35 - 64*33+ 20*14,
						      "ground");
			
			for(int i = 0; i < 6; i++) {
				u = new shadowDude(11500 + -200 ,
						1150-30 - 35 - 32 - 64*33 + 20*14-47 - i*50);
				commands = new commandList();
				commands.add(new moveCommand(11500+200,
						1150-30 - 35 - 32 - 64*33 + 20*14-47 - i*50));
				commands.add(new moveCommand(
						11500 + -200 ,
						1150-30 - 35 - 32 - 64*33 + 20*14-47 - i*50));
				u.repeatCommands(commands);
				u.setMoveSpeed(1);
			}
			
			u = new lightDude(11500+100,
					1150-30 - 35 - 32 - 64*33 + 20*14-47);
			commands = new commandList();
			commands.add(new moveCommand(11500 + -200 ,
					1150-30 - 35 - 32 - 64*33 + 20*14-47));
			commands.add(new moveCommand(
					11500+100,
					1150-30 - 35 - 32 - 64*33 + 20*14-47));
			u.repeatCommands(commands);
			u.setMoveSpeed(3.5f);
			
			// Nineteen Sixth
			spawnReloadableTombRect(11500 + -200 + 50 ,
					1150-30 - 35 - 32 - 64*35 + 20*15,
				      11500 + 150 + 80,
				      1150-30 - 35 - 64*35+ 20*15,
						      "ground");
			u = new lightDude(11500-130,
					1150-30 - 35 - 32 - 64*35 + 20*15 - 47);
			u.setFacingDirection("Left");
			
			// Nineteen seventh
			spawnReloadableTombRect(11500 + -200 ,
					1150-30 - 35 - 32 - 64*37 + 20*16,
				      11500 + 150,
				      1150-30 - 35 - 64*37+ 20*16,
						      "ground");
			u = new lightDude(11500-130,
					1150-30 - 35 - 32 - 64*37 + 20*16 - 47);
			u.setFacingDirection("Right");
			
			// Nineteen eighth
			spawnReloadableTombRect(11500 + -200 + 50 ,
					1150-30 - 35 - 32 - 64*39 + 20*17,
				      11500 + 150 + 80,
				      1150-30 - 35 - 64*39+ 20*17,
						      "ground");
			
			// 20th
			// Right
			spawnReloadableTombRect(11500+100,
					-1103-170,
				    11500 + 100 + 32,
				    -1103-170+ 32,
					"ground");
			
			// Middle
			spawnReloadableTombRect(11500-20,
					-1103-170-40,
				    11500 - 30 + 32+10,
				    -1103-170+ -40 + 32,
					"ground");
			
			// End the wall on right.
			spawnReloadableTombRect(11500+200,
					-1103-70,
				    11500 + 200 + 32,
				    -1103-60+ 32*3,
					"ground");
			
			// End the wall on Left
				spawnReloadableTombRect(11500-200-28,
							-1103-200,
						    11500-180,
						    -1103-60+ 32*3,
							"ground");
				
			// 22
				
			// Left
			spawnReloadableTombRect(11500-140,
					-1103-300-10,
				    11500-32,
				    -1103-300-10+ 32,
					"ground");
			
			// Right
			spawnReloadableTombRect(11500 + 64-32,
					-1103-300-20-32-10,
				    11500 + 64 + 32*3,
				    -1103-300+ 32-20-32,
					"ground");
			
			for(int i = 0; i < 8; i++) {
				u = new shadowDude(11500,
					-1103-300-40 - 47 - i*50);
			}
			u = new lightDude(11500,
					-1103-300-40 - 47);
			commands = new commandList();
			commands.add(new moveCommand(11500,
					-1103-300-40 - 47 - 8*50));
			commands.add(new moveCommand(
					11500,
					-1103-300-40 - 47));
			u.repeatCommands(commands);
			u.setMoveSpeed(2);
			
			// 23
			spawnReloadableTombRect(11500 + 64+20-32,
					-1103-300-20-32-140+20+10,
				    11500 + 20+64,
				    -1103-300+ 32-20-32-140+20+10,
					"ground");
			
			// 24
			spawnReloadableTombRect(11500 -32-20+20,
					-1103-300-20-32-250+30+20,
				    11500-20+20,
				    -1103-300+ 32-20-32-250+30+20,
					"ground");
			
			// End
			spawnReloadableTombRect(11500 + 64+20-32,
					-1103-300-20-32-340+20+30,
				    11500 + 20+200,
				    -1103-300+ 32-20-32-340+20+30,
					"ground");
			stairsUp tombExit = new stairsUp(11500 + 64+20-32+130,-1103-300-20-32-340+20-47+30,0,forest.getZone(),-394,-3915,"Left");
			tombExit.setZ(-1);
			
			elevatorLoaded = true;
		}
	}
	
	// Deal with the first well we encounters.
	public void dealWithRegionStuff() {
		player currPlayer = player.getPlayer();
		if(currPlayer != null && currPlayer.isWithin(10966,744,10966+1500,1268) && shadowElevatorStarted!=null && !shadowElevatorStarted.isCompleted()) {
			shadowElevatorStarted.setCompleted(true);
			shadowElevatorFirstTime = true;
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