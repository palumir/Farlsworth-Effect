package zones.farmLand;
import modes.topDown;
import terrain.chunk;
import terrain.chunkTypes.grass;
import terrain.chunkTypes.water;
import terrain.chunkTypes.wood;
import terrain.doodads.farmLand.barn;
import terrain.doodads.farmLand.bush;
import terrain.doodads.farmLand.farmHouse;
import terrain.doodads.farmLand.fenceBars;
import terrain.doodads.farmLand.fenceBarsSmall;
import terrain.doodads.farmLand.fencePost;
import terrain.doodads.farmLand.hay;
import terrain.doodads.farmLand.horizontalGate;
import terrain.doodads.farmLand.rock;
import terrain.doodads.farmLand.tree;
import terrain.doodads.farmLand.verticalFence;
import units.unit;
import units.unitTypes.farmLand.farmer;
import units.unitTypes.farmLand.sheep;
import utilities.intTuple;
import zones.zone;

public class sheepFarm extends zone {
	
	//////////////////////////////////
	// FIELDS, GLOBALS, CONSTRUCTOR //
	//////////////////////////////////
	
	// Static caller of the zone.
	private static zone zoneReference;
	
	// References we will use throughout.
	unit u;
	chunk c;
	
	// Defaults
	public static intTuple DEFAULT_SPAWN_TUPLE = new intTuple(-3,33);
	
	// Constructor
	public sheepFarm() {
		super("sheepFarm", "farmLand");
	}
	
	///////////////////////////////
	// SPAWN PATTERNS/GENERATORS //
	///////////////////////////////
	
	// Spawn grass from x to y.
	public void spawnGrassRect(int x1, int y1, int x2, int y2) {
		int numX = (x2 - x1)/grass.DEFAULT_CHUNK_WIDTH;
		int numY = (y2 - y1)/grass.DEFAULT_CHUNK_HEIGHT;
		for(int i = 0; i < numX; i++) {
			for(int j = 0; j < numY; j++) {
				c = new grass(i*grass.DEFAULT_CHUNK_WIDTH + x1, j*grass.DEFAULT_CHUNK_HEIGHT + y1);
			}
		}
	}
	
	// Spawn water from x to y.
	public void spawnWaterRect(int x1, int y1, int x2, int y2) {
		int numX = (x2 - x1)/water.DEFAULT_CHUNK_WIDTH;
		int numY = (y2 - y1)/water.DEFAULT_CHUNK_HEIGHT;
		for(int i = 0; i < numX; i++) {
			for(int j = 0; j < numY; j++) {
				c = new water(i*water.DEFAULT_CHUNK_WIDTH + x1, j*water.DEFAULT_CHUNK_HEIGHT + y1);
			}
		}
	}
	
	// Spawn wood from x to y.
	public void spawnWoodRect(int x1, int y1, int x2, int y2) {
		int numX = (x2 - x1)/wood.DEFAULT_CHUNK_WIDTH;
		int numY = (y2 - y1)/wood.DEFAULT_CHUNK_HEIGHT;
		for(int i = 0; i < numX; i++) {
			for(int j = 0; j < numY; j++) {
				c = new wood(i*wood.DEFAULT_CHUNK_WIDTH + x1, j*wood.DEFAULT_CHUNK_HEIGHT + y1);
			}
		}
	}
	
	// Spawn wood from x to y. TODO: JUST WORKS FOR VERTICAL
	public void spawnFence(int x1, int y1, int x2, int y2) {
		// The fence is vertical.
		if(x2==x1) {
			int numY = (y2 - y1)/verticalFence.DEFAULT_CHUNK_HEIGHT;
			for(int j = 0; j < numY; j++) {
					
				// Bottom of fence.
				if(j==numY-1) c = new verticalFence(verticalFence.DEFAULT_CHUNK_WIDTH + x1, j*60 + y1, 1);
					
				// Anything in between.
				else c = new verticalFence(verticalFence.DEFAULT_CHUNK_WIDTH + x1, j*60 + y1, 0);
			}
		}
		if(y2==y1) {
				int numX = (x2 - x1)/(fencePost.DEFAULT_CHUNK_WIDTH + fenceBars.DEFAULT_CHUNK_WIDTH);
				for(int j = 0; j < numX; j++) {
					// Far left of fence.
					if(j==0) c = new fencePost(j*fencePost.DEFAULT_CHUNK_WIDTH + x1, fencePost.DEFAULT_CHUNK_HEIGHT + y1, 0);
						
					// Middle fence
					else {
						c = new fenceBars((j-1)*fenceBars.DEFAULT_CHUNK_WIDTH + j*fencePost.DEFAULT_CHUNK_WIDTH + x1,fenceBars.DEFAULT_CHUNK_HEIGHT + y1,0);
						c = new fencePost(j*fenceBars.DEFAULT_CHUNK_WIDTH + j*fencePost.DEFAULT_CHUNK_WIDTH + x1, fencePost.DEFAULT_CHUNK_HEIGHT + y1, 0);
					}
				}
		}
	}
	
	/////////////////
	// ZONE LOADER //
	/////////////////
	// Load the zone.
	public void loadZone() {
		
		// Set the mode of the zone of course.
		topDown.setMode();
		
		// Spawn area.
		createSpawnArea();
		
		// Create forest above spawn
		createForest();
		
		// Zone is loaded.
		setZoneLoaded(true);
		
	}
	
	//////////////////////
	// INDIVIDUAL AREAS //
	//////////////////////
	
	// Forest area above spawn.
	public void createForest() {
		///////////////////
		//// FOREST ///////
		///////////////////
		
		// Spawn bushes and trees above the two fields.
		c = new bush(50,-490,0);
		c = new bush(0,-590,1);
		c = new bush(-100,-490,2);
		c = new tree(-150,-700, 2);
		c = new tree(0,-700, 1);
		c = new tree(150,-700, 0);
		
		// Spawn some river rocks left.
		c = new rock(0, 66, 0);
		c = new rock(-20, 89, 1);
		c = new rock(20, 120, 1);
		c = new rock(-20, 150, 0);
	}
	
	// Spawn area.
	public void createSpawnArea() {
		
		//////////////////////////
		//// ENTIRE ZONE STUFF ///
		//////////////////////////
		// Draw the grass around spawn.
		spawnGrassRect(-1500,-1000,1000,64);
		
		// Spawn some grass on the other side of the bridge.
		spawnGrassRect(-1500,184,1000,1000);
		
		//////////////////
		//// PENS  ///////
		//////////////////

		// Draw field on the left of spawn.
		int fenceAdjustX = -6;
		spawnFence(-30+fenceAdjustX,-435,-30+fenceAdjustX,200); // Vertical, right
		spawnFence(-1050+fenceAdjustX+17,-462,10+fenceAdjustX,-462); // Horizontal, top of field
		spawnFence(-168-3+28,17,20-3,17); // Horizontal, right of bridge.
		spawnFence(-1050+fenceAdjustX+17,17,-150,17); // Horizontal, left of bridge.
		spawnFence(-450+fenceAdjustX,-436,-450+fenceAdjustX,200); // Vertical, far left
		u = new sheep(-378,-369);
		u = new sheep(-150,-372);
		u = new sheep(-129,-60);
		u = new sheep(-372,-36);
		c = new hay(-294,-315,0);
		c = new hay(-195,-165,0);
		
		
		// Draw the bridge.
		spawnWoodRect(-180-5,56,-140,200);
		
		// Draw the water to left of bridge spawn.
		spawnWaterRect(-1510-2+20-5,56,-170,200);	
		
		// Draw the water to right of bridge spawn.
		spawnWaterRect(-168+20-5,56,1000,200);
		
		// Draw fence on the right.
		spawnFence(40,-462,500,-462); // Horizontal, top of field
		spawnFence(40,17,500,17); // Horizontal, bottom
		spawnFence(35,-436,35,200); // Vertical, left
		spawnFence(455,-436,455,300); // Vertical, right
		
		// Draw the fence gate above the fields.
		// Fencebars to left of gate.
		c = new fenceBarsSmall(-21+fenceAdjustX,-436,0);
		c = new fenceBarsSmall(-18+fenceAdjustX,-436,0);
		c = new fenceBarsSmall(-15+fenceAdjustX,-436,0);
		c = new fenceBarsSmall(-15+fenceAdjustX+3,-436,0);
		
		// Fencebars to right of gate
		c = new fenceBarsSmall(32-3,-436,0);
		c = new fenceBarsSmall(32,-436,0);
		c = new fenceBarsSmall(35,-436,0);
		c = new fenceBarsSmall(37,-436,0);
		horizontalGate forestGate = new horizontalGate(-13+fenceAdjustX/2,-434,0);
		
		////////////////////////////
		//// FARMHOUSE AREA  ///////
		////////////////////////////
		c = new tree(-720,-325,0);
		c = new farmHouse(-650,-420,0);
		c = new barn(-950,-420,0);
		spawnFence(-1038-6,-436,-1038-6,200+50); // Vertical, far left
		farmer theFarmer = new farmer(-711,-267);
		theFarmer.hasQuest(); 
		u.setFacingDirection("Down");
		c = new tree(-1017, -414, 1);
		c = new tree(-1011, 0, 0);
		c = new hay(-960,-351,1);
		c = new hay(-875,-351,1);
		c = new bush(-1025,-130,0);
		c = new bush(-909,-9,1);
		c = new bush(-510,-330,1);
		
		/////////////////////////////////////
		//// ARMORY/SHED/ACROSS RIVER ///////
		/////////////////////////////////////
		c = new tree(-135,186,1);
		c = new tree(-70,220,2);
		c = new bush(-110,250,0);
		c = new tree(-120,280,1);
		c = new tree(-135,310,2);
		c = new tree(-70,340,0);
		c = new bush(-90,360,1);
		c = new tree(-90,380,1);
		c = new tree(-65,410,2);
		c = new tree(-40,450,1);
		
		// Corner
		c = new tree(-60,480,2);
		c = new tree(-20,500,1);
		c = new tree(-55,525,2);
		c = new tree(-80,545,1);
		c = new tree(-100,570,1);
		c = new bush(-142,360,2);
		c = new bush(-250,465,0);
		
		// The bottom trees.
		c = new tree(-120,600,2);
		c = new tree(-145,580,0);
		c = new tree(-165,605,2);
		c = new tree(-195,585,1);
		c = new tree(-225,585,1);
		c = new tree(-240,610,0);
		c = new tree(-260,600,1);
		c = new tree(-285,575,2);
		c = new tree(-310,610,2);
		c = new tree(-330,565,2);
		c = new tree(-345,585,1);
		c = new tree(-375,615,0);
		c = new tree(-415,600,2);
		c = new tree(-445,575,0);
		c = new tree(-465,605,0);
		c = new tree(-490,585,1);
		c = new tree(-525,580,1);
		c = new tree(-550,615,2);
		c = new tree(-580,610,2);
		c = new tree(-610,580,1);
		c = new tree(-635,610,2);
		c = new tree(-665,580,1);
		c = new tree(-700,580,0);
		c = new tree(-715,615,0);
		
		// The left trees.
		c = new tree(-730,590,0);
		c = new tree(-700,560,1);
		c = new tree(-660,555,2);
		c = new tree(-685,530,0);
		c = new tree(-705,499,2);
		c = new tree(-735,476,1);
		c = new tree(-755,454,2);
		c = new tree(-785,415,0);
		c = new tree(-745,390,1);
		c = new tree(-710,360,0);
		c = new tree(-685,335,2);
		c = new tree(-655,300,1);
		c = new tree(-774,234,1);
		c = new tree(-670,275,2);
		c = new tree(-680,260,1);
		c = new tree(-690,235,0);
		c = new tree(-730,200,1);
		c = new bush(-741,240,2);
		c = new bush(-642,393,1);
		
		// Misc left trees beyond the line of trees that block you
		c = new tree(-850,590,0);
		c = new tree(-775,560,2);
		c = new tree(-1025,555,1);
		c = new tree(-855,530,2);
		c = new tree(-1200,499,1);
		c = new tree(-1100,476,1);
		c = new tree(-929,454,0);
		c = new tree(-828,415,0);
		c = new tree(-979,390,2);
		c = new tree(-1202,360,0);
		c = new tree(-827,335,2);
		c = new tree(-1209,300,1);
		c = new tree(-919,275,0);
		c = new tree(-929,260,2);
		c = new tree(-890,235,0);
		c = new tree(-950,200,2);
		c = new bush(-921,510,1);
		c = new bush(-771,636,2);
		c = new bush(-795,201,0);
		c = new rock(-891,312,1);
		c = new tree(-804,675,1);
		
		// Trees below bottom tree wall.
		c = new tree(-699,672,2);
		c = new tree(-660,770,1);
		c = new tree(-635,890,2);
		c = new tree(-580,840,1);
		c = new tree(-520,780,2);
		c = new tree(-480,827,1);
		c = new tree(-410,820,0);
		c = new tree(-385,890,0);
		c = new tree(-360,760,1);
		c = new tree(-519, 660, 0);
		c = new tree(-310,700,1);
		c = new tree(-280,792,0);
		c = new tree(-265,700,2);
		c = new tree(-200,679,0);
		c = new tree(-160,740,1);
		c = new rock(-612,669,1);
		c = new bush(-429,636,1);
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