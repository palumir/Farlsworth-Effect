package units.unitTypes.farmLand.sheepFarm;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

import doodads.sheepFarm.clawMarkYellow;
import doodads.sheepFarm.clawMarkRed;
import doodads.sheepFarm.rock;
import drawing.camera;
import drawing.drawnObject;
import drawing.gameCanvas;
import drawing.animation.animation;
import effects.effect;
import effects.effectTypes.bloodSquirt;
import effects.effectTypes.darkExplode;
import effects.effectTypes.poisonExplode;
import effects.effectTypes.explodingRock;
import modes.mode;
import sounds.sound;
import terrain.chunk;
import units.animalType;
import units.humanType;
import units.player;
import units.unit;
import units.unitType;
import utilities.intTuple;
import utilities.mathUtils;
import utilities.pathFindingNode;
import utilities.time;
import utilities.utility;
import zones.zone;

public class yellowWolf extends unit {
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default name.
	private static String DEFAULT_WOLF_NAME = "yellowWolf";
	
	// Platformer real dimensions
	public static int DEFAULT_PLATFORMER_HEIGHT = 32;
	public static int DEFAULT_PLATFORMER_WIDTH = 32;
	public static int DEFAULT_PLATFORMER_ADJUSTMENT_Y = 0;
	
	// TopDown real dimensions
	public static int DEFAULT_TOPDOWN_HEIGHT = 18;
	public static int DEFAULT_TOPDOWN_WIDTH = 30;
	public static int DEFAULT_TOPDOWN_ADJUSTMENT_Y = 5;
	
	// Follow until range
	static private int followUntilRange = 80;
	
	// How close to attack?
	static private int DEFAULT_ATTACK_RADIUS = 300;
	static private int DEFAULT_DEAGGRO_RADIUS = 400;
	
	// Damage stats
	static private int DEFAULT_ATTACK_DIFFERENTIAL = 0; // the range within the attackrange the unit will attack.
	static private int DEFAULT_ATTACK_DAMAGE = 2;
	static private float DEFAULT_BAT = 0.30f;
	static private float DEFAULT_ATTACK_TIME = 2f;
	static private int DEFAULT_ATTACK_WIDTH = 400;
	static private int DEFAULT_ATTACK_LENGTH = 400;
	static private float DEFAULT_CRIT_CHANCE = .15f;
	static private float DEFAULT_CRIT_DAMAGE = 1.6f;
	
	// Dosile?
	private boolean dosile = false;
	
	// Health.
	private int DEFAULT_HP = 15;
	
	// Default movespeed.
	private static int DEFAULT_WOLF_MOVESPEED = 2;
	
	// Default jump speed
	private static int DEFAULT_WOLF_JUMPSPEED = 12;
	
	// wolf sprite stuff.
	private static String DEFAULT_WOLF_SPRITESHEET = "images/units/animals/yellowWolf.png";
	
	// The actual type.
	private static unitType wolfType =
			new animalType( "jumpingWolf",  // Name of unitType 
						 DEFAULT_WOLF_SPRITESHEET,
					     DEFAULT_WOLF_MOVESPEED, // Movespeed
					     DEFAULT_WOLF_JUMPSPEED // Jump speed
						);	
	// Sounds
	private static String howl = "sounds/effects/animals/wolfHowl.wav";
	private static String growl = "sounds/effects/animals/wolfGrowl.wav";
	private static String bark1 = "sounds/effects/animals/wolfBark1.wav";
	private static String bark2 = "sounds/effects/animals/wolfBark2.wav";
	private static String wolfAttack = "sounds/effects/player/combat/swingWeapon.wav";
	private int lastBarkSound = 0;
	private long lastHowl = 0;
	private float randomHowl = 0;
	private int soundRadius = 1200;
	
	// Rockpile
	private static float DEFAULT_ROCK_DURATION = 2f;
	private static int DEFAULT_ROCK_RADIUS = 35;
	private static int DEFAULT_ROCK_DAMAGE = 1;
	
	//////////////
	/// FIELDS ///
	//////////////
	private boolean aggrod = false;
	
	// Claw attacking?
	private boolean clawAttacking = false;
	private boolean hasClawSpawned = false;
	private boolean slashing = false;
	private long startOfClawAttack = 0;
	private float spawnClawPhaseTime = .5f;
	private float clawAttackEvery = 3f;
	private long lastClawAttack = 0;
	
	// Claw
	private clawMarkYellow currClaw = null;
	private boolean hasStartedJumping = false;
	
	// Start rise and run
	private int startX = 0;
	private int startY = 0;
	
	// Jumping stuff
	private int jumpingToX = 0;
	private int jumpingToY = 0;
	private boolean hasSlashed = false;
	private boolean riseRunSet = false;
	private int rise = 0;
	private int run = 0;
	
	// Charged units
	private ArrayList<unit> chargeUnits;
	
	///////////////
	/// METHODS ///
	///////////////
	// Constructor
	public yellowWolf(int newX, int newY) {
		super(wolfType, newX, newY);
		//showAttackRange();
		// Set wolf combat stuff.
		setCombatStuff();
		attackSound = wolfAttack;
		
		// Add attack animations.
		// Attacking left animation.
		animation slashingLeft = new animation("slashingLeft", wolfType.getUnitTypeSpriteSheet().getAnimation(5), 2, 2, DEFAULT_BAT);
		getAnimations().addAnimation(slashingLeft);
		
		// Attacking left animation.
		animation slashingRight = new animation("slashingRight", wolfType.getUnitTypeSpriteSheet().getAnimation(6), 2, 2, DEFAULT_BAT);
		getAnimations().addAnimation(slashingRight);
		
		// Attacking left animation.
		animation attackingLeft = new animation("attackingLeft", wolfType.getUnitTypeSpriteSheet().getAnimation(5), 0, 5, DEFAULT_BAT);
		getAnimations().addAnimation(attackingLeft);
		
		// Attacking left animation.
		animation attackingRight = new animation("attackingRight", wolfType.getUnitTypeSpriteSheet().getAnimation(6), 0, 5, DEFAULT_BAT);
		getAnimations().addAnimation(attackingRight);
		
		// Set dimensions
		setHeight(getDefaultHeight());
		setWidth(getDefaultWidth());
		platformerHeight = DEFAULT_PLATFORMER_HEIGHT;
		platformerWidth = DEFAULT_PLATFORMER_WIDTH;
		topDownHeight = DEFAULT_TOPDOWN_HEIGHT;
		topDownWidth = DEFAULT_TOPDOWN_WIDTH;
		setHitBoxAdjustmentY(getDefaultHitBoxAdjustmentY());
	}
	
	// Combat defaults.
	public void setCombatStuff() {
		// Set to be attackable.
		this.setKillable(true);
		
		// Wolf damage.
		setAttackFrameStart(2);
		setAttackFrameEnd(3);
		setAttackDamage(DEFAULT_ATTACK_DAMAGE);
		setAttackTime(DEFAULT_ATTACK_TIME);
		setAttackWidth(DEFAULT_ATTACK_WIDTH);
		setAttackLength(DEFAULT_ATTACK_LENGTH);
		setCritDamage(DEFAULT_CRIT_DAMAGE);
		setCritChance(DEFAULT_CRIT_CHANCE);
		
		// HP
		setMaxHealthPoints(DEFAULT_HP);
		setHealthPoints(DEFAULT_HP);
		
	}
	
	// React to pain.
	public void reactToPain() {
		// Play a bark on pain.
		sound s = new sound(bark1);
		s.setPosition(getX(), getY(), soundRadius);
		s.start();
	}
	
	// Wolf random noises
	public void makeSounds() {
		
			// Create a new random growl interval
			float newRandomHowlInterval = 10f + utility.RNG.nextInt(10);
			
			// Make the wolf howl
			if(randomHowl == 0f) {
				randomHowl = newRandomHowlInterval;
			}
			if(!dosile && !aggrod && time.getTime() - lastHowl > randomHowl*1000) {
				
				// Set the last time they howled
				lastHowl = time.getTime();
				randomHowl = newRandomHowlInterval;
				sound s = new sound(howl);
				s.setPosition(getX(), getY(), soundRadius);
				s.start();
			}
	}

	// Claw attack.
	public void clawAttack(unit u) {
		clawAttacking = true;
		hasClawSpawned = false;
		slashing = false;
		
		// Start of claw attack.
		startOfClawAttack = time.getTime();
	}

	// Charge units
	public void chargeUnits() {
		int chargeStartX = this.getX() + this.getWidth()/2;
		int chargeStartY = this.getY() + this.getHeight()/2;
		int radius = 30;
		chargeUnits = unit.getUnitsInRadius(chargeStartX, chargeStartY, radius);
		if(chargeUnits != null) {
			for(int i = 0; i < chargeUnits.size(); i++) {
				if(chargeUnits.get(i)!=this) {
					stopMove("all");
					chargeUnits.get(i).move(run,rise);
					chargeUnits.get(i).setUnitLocked(true);
				}
			}
		}
	}
	
	// Spawn rock every
	private float spawnRockEvery = 0.05f;
	private long lastSpawnRock = 0;
	
	// Deal with jumping.
	public void dealWithJumping() {
		if(clawAttacking) {
			
			// Get current player.
			player currPlayer = player.getCurrentPlayer();
			
			// Reset rise and run if we're close.
			if(Math.abs(jumpingToX - getX()) < jumpSpeed) {
				run = 0;
			}
			if(Math.abs(jumpingToY - getY()) < jumpSpeed) {
				rise = 0;
			}
			
			// Jump to the location.
			if(jumping) {
				
				// Set rise/run
				if(!riseRunSet) {
					riseRunSet = true;
					float yDistance = (jumpingToY - getY());
					float xDistance = (jumpingToX - getX());
					float distanceXY = (float) Math.sqrt(yDistance * yDistance
							+ xDistance * xDistance);
					rise = (int) ((yDistance/distanceXY)*jumpSpeed);
					run = (int) ((xDistance/distanceXY)*jumpSpeed);
					startX = getX();
					startY = getY();
				}
				
				// Charge units to position.
				chargeUnits();
				
				// Spawn rocks
				int stopAt = 50;
				int howClose = (int) Math.sqrt(Math.pow(this.getX() - currClaw.getX(),2) + Math.pow(this.getY() - currClaw.getY(), 2));
				if(time.getTime() - lastSpawnRock > spawnRockEvery*1000 && (howClose > stopAt)) {
					lastSpawnRock = time.getTime();
					explodingRock r = new explodingRock(this.getX() + this.getWidth()/2,
							  this.getY() + this.getHeight()/2,
							  false,
							  DEFAULT_ROCK_RADIUS,
							  DEFAULT_ROCK_DAMAGE,
							  DEFAULT_ROCK_DURATION);
				}
				
				setX(getX() + run);
				setY(getY() + rise);
				
				// Don't let him not move at all or leave region.
				if((run == 0 && rise == 0) || ((Math.abs(jumpingToX - getX()) < 1 && Math.abs(jumpingToY - getY()) < 1))) {
					if(currClaw != null) {
						currClaw.destroy();
					}
					jumping = false;
					clawAttacking = false;
					hasStartedJumping = false;
				}
				
				// If slashing, hurt the player.
				if(slashing && !hasSlashed && currPlayer.isWithin(getX(), getY(), getX() + getWidth(), getY() + getHeight())) {
					hasSlashed = true;
					slashing = false;
				}
			}
		}
		else {
			if(chargeUnits != null && chargeUnits.size() >= 1) {
				for(int i = 0; i < chargeUnits.size(); i++) {
					chargeUnits.get(i).setUnitLocked(false);
				}
				chargeUnits = new ArrayList<unit>();
			}
		}
	}
	
	/*public int heuristicCostEstimate(pathFindingNode start, pathFindingNode goal) {
		    int dx = Math.abs(start.x - goal.x);
		    int dy = Math.abs(start.y - goal.y);
		    return (int) ((dx + dy) + (1.4f - 2) * Math.min(dx, dy));
	}
	
	
	// Pathfinding
	public ArrayList<intTuple> pathFindingAlgorithm() {
		
		// Closed and open
		ArrayList<pathFindingNode> closedSet = new ArrayList<pathFindingNode>();
		ArrayList<pathFindingNode> openSet = new ArrayList<pathFindingNode>();
		
		// Only do it within default deaggroradius
		pathFindingNode start = new pathFindingNode(this.getX() + this.getWidth()/2, this.getY() + this.getHeight()/2);
		start.i = (DEFAULT_DEAGGRO_RADIUS+100)/moveSpeed;
		start.j = start.i = (DEFAULT_DEAGGRO_RADIUS+100)/moveSpeed;
		
		// End goal is the player position.
		pathFindingNode goal = new pathFindingNode(player.getCurrentPlayer().getX() + player.getCurrentPlayer().getWidth()/2,
												  player.getCurrentPlayer().getY() + player.getCurrentPlayer().getHeight()/2);
		
		// Initiate
		start.g = 0;
		start.f = heuristicCostEstimate(start,goal);
		openSet.add(start);
		pathFindingNode[][] nodes = new pathFindingNode[(DEFAULT_DEAGGRO_RADIUS+100)*2/moveSpeed][(DEFAULT_DEAGGRO_RADIUS+100)*2/moveSpeed];
		int numNodes = 0;
		
		// Initiate nodes. TODO: make these null for pathfinding nodes
		for(int i = 0; i < (DEFAULT_DEAGGRO_RADIUS+100)*2/moveSpeed; i++) {
			int atX = start.x + i*moveSpeed - ((DEFAULT_DEAGGRO_RADIUS+100)/moveSpeed);
			for(int j = 0; j < (DEFAULT_DEAGGRO_RADIUS+100)*2/moveSpeed; j++) {
				int atY = start.y + j*moveSpeed - ((DEFAULT_DEAGGRO_RADIUS+100)/moveSpeed);
				nodes[i][j] = new pathFindingNode(atX,atY);
				nodes[i][j].h = heuristicCostEstimate(nodes[i][j],goal);
				nodes[i][j].j = j;
				nodes[i][j].i = i;
				numNodes++;
			}
		}
		
		// Null areas
		int nullAreas = 0;
		
		// Make the collide areas null.
		int atXStart = start.x - ((DEFAULT_DEAGGRO_RADIUS+100)/moveSpeed);
		int atYStart = start.y - ((DEFAULT_DEAGGRO_RADIUS+100)/moveSpeed);
		int atXEnd = start.x + ((DEFAULT_DEAGGRO_RADIUS+100)/moveSpeed);
		int atYEnd = start.y + ((DEFAULT_DEAGGRO_RADIUS+100)/moveSpeed);
		ArrayList<chunk> collideChunks = chunk.getImpassableChunksInBox(atXStart, atYStart, atXEnd, atYEnd);
		if(collideChunks!=null) {
			System.out.println("We collide with some chunks in our area.");
			for(int i = 0; i < collideChunks.size(); i++) {
				chunk currChunk = collideChunks.get(i);
				System.out.println(currChunk);
				int x1 = currChunk.getX() + currChunk.getWidth()/2;
				int y1 = currChunk.getY() + currChunk.getHeight()/2 + currChunk.getHitBoxAdjustmentY();
				int i1 = start.i - ((x1 - start.x)/moveSpeed);
				int j1 = start.j - ((y1 - start.y)/moveSpeed);
				
				for(int n = 0; n < (this.getWidth() + currChunk.getWidth() + moveSpeed*2)/moveSpeed; n++) {
					
					int currI = i1 + n - (this.getWidth()/2 + currChunk.getWidth()/2 + moveSpeed)/moveSpeed;
					
					for(int m = 0; m < (this.getHeight() + currChunk.getHeight())/moveSpeed + moveSpeed*2; m++) {
						
						int currJ = j1 + m - (this.getHeight()/2 + currChunk.getHeight()/2 + moveSpeed)/moveSpeed;

						if(currI >= 0 && currI < ((DEFAULT_DEAGGRO_RADIUS+100)/moveSpeed)*2 && 
						   currJ >= 0 && currJ < ((DEFAULT_DEAGGRO_RADIUS+100)/moveSpeed)*2) {
							nullAreas++;
							nodes[currI][currJ].blocked = true;
						}
					}
				}
			}
		}
		
		System.out.println("DONE MAKING AREAS NULL NUMBER OF THEM IS " + nullAreas);
		System.out.println("DONE MAKING AREAS NUMBER OF NODES " + numNodes);
		
		// Add start to nodes.
		nodes[(DEFAULT_DEAGGRO_RADIUS+100)/moveSpeed][(DEFAULT_DEAGGRO_RADIUS+100)/moveSpeed] = start;
		
		// Current node.
		pathFindingNode currentNode = null;
		
		// A* Algorithm
		while(openSet.size()!=0) {
			
			// Get node with lowest f in open.
			currentNode = null;
			for(int i = 0; i < openSet.size(); i++) {
				if(currentNode == null) currentNode = openSet.get(i);
				if(currentNode.f >= openSet.get(i).f) currentNode = openSet.get(i);
			}
			
			// We are done.
			if(Math.abs(goal.x - currentNode.x) < moveSpeed*3 && Math.abs(goal.y - currentNode.y) < moveSpeed*3) {
				break;
			}
			
			openSet.remove(currentNode);
			closedSet.add(currentNode);
			
			// Check neighbors. 
			pathFindingNode[] neighbours = new pathFindingNode[8];
			if(currentNode.i-1 >= 0 && currentNode.j-1 >= 0) neighbours[0] = nodes[currentNode.i-1][currentNode.j-1];
			if(currentNode.j-1 >= 0) neighbours[1] = nodes[currentNode.i][currentNode.j-1];
			if(currentNode.j-1 >= 0 && currentNode.i+1 < (DEFAULT_DEAGGRO_RADIUS+100)*2/moveSpeed) neighbours[2] = nodes[currentNode.i+1][currentNode.j-1];
			if(currentNode.i-1 >= 0) neighbours[3] = nodes[currentNode.i-1][currentNode.j];
			if(currentNode.i+1 < (DEFAULT_DEAGGRO_RADIUS+100)*2/moveSpeed) neighbours[4] = nodes[currentNode.i+1][currentNode.j];
			if(currentNode.i-1 >= 0 && currentNode.j+1 < (DEFAULT_DEAGGRO_RADIUS+100)*2/moveSpeed) neighbours[5] = nodes[currentNode.i-1][currentNode.j+1];
			if(currentNode.j+1 < (DEFAULT_DEAGGRO_RADIUS+100)*2/moveSpeed) neighbours[6] = nodes[currentNode.i][currentNode.j+1];
			if(currentNode.j+1 < (DEFAULT_DEAGGRO_RADIUS+100)*2/moveSpeed && currentNode.i+1 < (DEFAULT_DEAGGRO_RADIUS+100)*2/moveSpeed) neighbours[7] = nodes[currentNode.i+1][currentNode.j+1];
			for(int i = 0; i < 8; i++) {
				pathFindingNode currNeighbour = neighbours[i];
				if(currNeighbour!=null) {
					System.out.println("Found neighbour " + neighbours[i].i + " " + neighbours[i].j);

					if(closedSet.contains(currNeighbour)) {
						continue;
					}
					int tentativeG = currentNode.g + distanceBetween(currentNode,currNeighbour);
					if(!openSet.contains(currNeighbour)) {
						openSet.add(currNeighbour);
					}
					else if(tentativeG >= currNeighbour.g) {
						continue;
					}
					
					
					// Best path so far.
					currNeighbour.parent = currentNode;
					currNeighbour.g = tentativeG;
					currNeighbour.f = currNeighbour.g + heuristicCostEstimate(currNeighbour,goal);
				}
			}
		}
		
		if(currentNode != null) {
			ArrayList<intTuple> returnList = new ArrayList<intTuple>();
			while(currentNode!=null) {
				returnList.add(new intTuple(currentNode.x, currentNode.y));
				currentNode = currentNode.parent;
			}
			return returnList;
			
		}
		else {
			return null;
		}
	}
		

	private int distanceBetween(pathFindingNode currentNode, pathFindingNode currNeighbour) {
		return 1;
	}*/

	// Remove claw.
	@Override
	public void reactToDeath() {
		if(currClaw != null) currClaw.destroy();
	}
	
	// Jump
	public void slashTo(clawMarkYellow c) {
		stopMove("all");
		sound s = new sound(bark1);
		s.setPosition(getX(), getY(), soundRadius);
		s.start();
		
		// Set facing direction.
		if(this.getX() - c.getX() < 0) {
			setFacingDirection("Right");
		}
		else {
			setFacingDirection("Left");
		}
		
		// Jump there
		jumpingToX = c.getX() + c.getWidth()/2 - getWidth()/2;
		jumpingToY = c.getY() + c.getHeight()/2 - getHeight()/2;
		jumping = true;
		slashing = true;
		hasSlashed = false;
		riseRunSet = false;
	}
	
	// Spawn claw
	public void spawnClaw() {
		int howFarBack = 100;
		int spawnX = player.getCurrentPlayer().getX()+player.getCurrentPlayer().getWidth()/2;
		int spawnY = player.getCurrentPlayer().getY()+player.getCurrentPlayer().getHeight()/2;
		int degree = (int) mathUtils.angleBetweenTwoPointsWithFixedPoint(
				spawnX, spawnY,
				this.getX()+this.getWidth()/2, this.getY()+this.getHeight()/2, 
				this.getX()+this.getWidth()/2, this.getY()+this.getHeight()/2);
		int distance = (int) Math.sqrt(Math.pow(spawnX - (this.getX()+this.getWidth()/2),2) + Math.pow(spawnY - (this.getY()+this.getHeight()/2),2));
		int newX = (int) (getX() + (distance+howFarBack)*Math.cos(Math.toRadians(degree))); 
		int newY = (int) (getY() + (distance+howFarBack)*Math.sin(Math.toRadians(degree)));
		currClaw = new clawMarkYellow(newX - clawMarkYellow.DEFAULT_CHUNK_WIDTH/2, 
									 newY - clawMarkYellow.DEFAULT_CHUNK_HEIGHT/2,
									 0);
	}
	
	// Deal with claw attacks.
	public void dealWithClawAttacks() {
		if(clawAttacking) {
			
			// Spawn claw phase.
			if(!hasClawSpawned && time.getTime() - startOfClawAttack < spawnClawPhaseTime*1000) {
				hasClawSpawned = true;
				spawnClaw();
			}
			
			
			// Slashing phase.
			else if(hasClawSpawned && time.getTime() - startOfClawAttack > spawnClawPhaseTime*1000 && !hasStartedJumping) {
				hasStartedJumping = true;
				slashTo(currClaw);
			}
		}
	}
	
	// wolf AI moves wolf around for now.
	public void updateUnit() {
		
		// If player is in radius, follow player, attacking.
		player currPlayer = player.getCurrentPlayer();
		int playerX = currPlayer.getX();
		int playerY = currPlayer.getY();
		float howClose = (float) Math.sqrt((playerX - getX())*(playerX - getX()) + (playerY - getY())*(playerY - getY()));
		
		// Make sounds.
		makeSounds();
		
		// Claw attack
		dealWithClawAttacks();
		dealWithJumping();
		
		// Attack if we're in radius.
		if(!clawAttacking && !isDosile() && (howClose < DEFAULT_ATTACK_RADIUS || (aggrod && howClose < DEFAULT_DEAGGRO_RADIUS))) {
			
			// If we're in attack range, attack.
			if(isInAttackRange(currPlayer, DEFAULT_ATTACK_DIFFERENTIAL) && time.getTime() - lastClawAttack > clawAttackEvery*1000) {
					clawAttackEvery = 4f + 0.1f*(float)utility.RNG.nextInt(5);
					lastClawAttack = time.getTime();
					stopMove("all");
					clawAttack(currPlayer);
			}
			else {
				if(!aggrod) {
					sound s = new sound(growl);
					s.setPosition(getX(), getY(), soundRadius);
					s.start();
				}
				aggrod = true;
				if(howClose > followUntilRange) {
					follow(currPlayer);
				}
				else {
					stopMove("all");
				}
			}
		}
		else if(aggrod && howClose > DEFAULT_DEAGGRO_RADIUS) {
			stopMove("all");
		}
		
		// Even dosile wolves attack if provoked.
		if(dosile && isInAttackRange(currPlayer, DEFAULT_ATTACK_DIFFERENTIAL)) {
			attack();
		}
	}
	
	// Deal with movement animations.
	public void dealWithAnimations(int moveX, int moveY) {
		if(jumping) {
			animate("slashing" + facingDirection);
		}
		else if(isAttacking() && !isAlreadyAttacked()) {
			// Play animation.
			animate("attacking" + facingDirection);
		}
		else if(isMoving()) {
			animate("running" + getFacingDirection());
		}
		else {
			animate("standing" + getFacingDirection());
		}
	}
	
	@Override
	public void drawUnitSpecialStuff(Graphics g) {
	}
	
	
	///////////////////////////
	/// GETTERS AND SETTERS ///
	///////////////////////////
	
	// Get default width.
	public static int getDefaultWidth() {
		if(mode.getCurrentMode().equals("topDown")) {
			return DEFAULT_TOPDOWN_WIDTH;
		}
		else {
			return DEFAULT_PLATFORMER_WIDTH;
		}
	}
	
	// Get default height.
	public static int getDefaultHeight() {
		if(mode.getCurrentMode().equals("topDown")) {
			return DEFAULT_TOPDOWN_HEIGHT;
		}
		else {
			return DEFAULT_PLATFORMER_HEIGHT;
		}
	}
	
	// Get default hitbox adjustment Y.
	public static int getDefaultHitBoxAdjustmentY() {
		if(mode.getCurrentMode().equals("topDown")) {
			return DEFAULT_TOPDOWN_ADJUSTMENT_Y;
		}
		else {
			return DEFAULT_PLATFORMER_ADJUSTMENT_Y;
		}
	}
	
	public boolean isDosile() {
		return dosile;
	}

	public void setDosile(boolean dosile) {
		this.dosile = dosile;
	}
}
