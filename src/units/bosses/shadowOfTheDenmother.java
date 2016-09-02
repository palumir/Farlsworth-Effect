package units.bosses;

import java.util.ArrayList;

import doodads.sheepFarm.clawMarkRed;
import drawing.spriteSheet;
import drawing.animation.animation;
import drawing.animation.animationPack;
import effects.effectTypes.platformGlow;
import drawing.spriteSheet.spriteSheetInfo;
import modes.mode;
import sounds.sound;
import terrain.chunk;
import terrain.groundTile;
import terrain.chunkTypes.tombEdge;
import units.boss;
import units.player;
import units.unit;
import units.unitType;
import units.unitCommands.commands.slashCommand;
import units.unitTypes.sheepFarm.wolf;
import units.unitTypes.tomb.shadowDude;
import utilities.intTuple;
import utilities.time;
import utilities.utility;

public class shadowOfTheDenmother extends boss {
	
	// Default name.
	private static String DEFAULT_UNIT_NAME = "Shadow of the Denmother";
	
	// Default jump speed
	private static int DEFAULT_UNIT_JUMPSPEED = 14;
	
	// Beta stats
	private static float DEFAULT_MOVESPEED_BETA = 2f;
	
	// How long to shadow puke for
	private static float DEFAULT_PUKE_FOR = 1f;
	private static int DEFAULT_PUKE_EVERY_BASE = 2;
	
	// How long does platform glow last for
	private static float DEFAULT_PLATFORM_GLOW_LASTS_FOR = 10f;
	
	// Number of hits total
	private static int numberOfHitsToDieTotal = 6;
	private int numberOfHitsToDie = numberOfHitsToDieTotal;

	// Unit sprite stuff.
	private static spriteSheet DEFAULT_UPDOWN_SPRITESHEET = new spriteSheet(new spriteSheetInfo(
			"images/units/animals/blackWolfUpDown.png", 
			64, 
			128,
			0,
			0
			));
	private static spriteSheet DEFAULT_LEFTRIGHT_SPRITESHEET = new spriteSheet(new spriteSheetInfo(
			"images/units/animals/blackWolfLeftRight.png",
			128, 
			64,
			0,
			0
			));
	
	// The actual type.
	private static unitType unitTypeRef =
			new unitType(DEFAULT_UNIT_NAME,  // Name of unitType 
					     null,
					     null,
					     100,
					     60,
					     DEFAULT_MOVESPEED_BETA, // Movespeed
					     DEFAULT_UNIT_JUMPSPEED // Jump speed
						);	
	
	
	/////////////////
	///// FIELDS ////
	/////////////////
	
	// Claw attacking?
	protected boolean clawAttacking = false;
	protected boolean hasClawSpawned = false;
	protected boolean slashing = false;
	protected long startOfClawAttack = 0;
	protected long lastClawAttack = 0;
	
	// Claw
	protected chunk currClaw = null;
	protected boolean hasStartedJumping = false;
	
	// Start rise and run
	protected double startX = 0;
	protected double startY = 0;
	
	// Claw attacking x and y
	protected int clawAttackingX = 0;
	protected int clawAttackingY = 0;
	
	// Jumping stuff
	protected int jumpingToX = 0;
	protected int jumpingToY = 0;
	protected boolean hasSlashed = false;
	protected boolean riseRunSet = false;
	protected double rise = 0;
	protected double run = 0;


	//////////////////
	//// METHODS /////
	//////////////////
	
	public shadowOfTheDenmother() {
		super(unitTypeRef, DEFAULT_UNIT_NAME, 0, 0);
		killsPlayer = true;
		spawnPlatforms();
		addAnimations();
	}
	
	// Add animations.
	public void addAnimations() {
		
		// Deal with animations
		animationPack unitTypeAnimations = new animationPack();
		
		// Jumping left animation.
		animation jumpingLeft = new animation("jumpingLeft", DEFAULT_LEFTRIGHT_SPRITESHEET.getAnimation(6), 4, 4, 1);
		unitTypeAnimations.addAnimation(jumpingLeft);
		
		// Jumping down animation.
		animation jumpingDown = new animation("jumpingDown", DEFAULT_UPDOWN_SPRITESHEET.getAnimation(3), 2, 2, 1);
		unitTypeAnimations.addAnimation(jumpingDown);
		
		// Jumping up animation.
		animation jumpingUp = new animation("jumpingUp", DEFAULT_UPDOWN_SPRITESHEET.getAnimation(3), 7, 7, 1);
		unitTypeAnimations.addAnimation(jumpingUp);
		
		// Jumping right animation.
		animation jumpingRight = new animation("jumpingRight", DEFAULT_LEFTRIGHT_SPRITESHEET.getAnimation(2), 4, 4, 1);
		unitTypeAnimations.addAnimation(jumpingRight);
		
		// Standing left animation.
		animation standingLeft = new animation("standingLeft", DEFAULT_LEFTRIGHT_SPRITESHEET.getAnimation(5), 0, 0, 1);
		unitTypeAnimations.addAnimation(standingLeft);
		
		// Standing right animation.
		animation standingRight = new animation("standingRight", DEFAULT_LEFTRIGHT_SPRITESHEET.getAnimation(3), 0, 0, 1);
		unitTypeAnimations.addAnimation(standingRight);
		
		// Running left animation.
		animation runningLeft = new animation("runningLeft", DEFAULT_LEFTRIGHT_SPRITESHEET.getAnimation(5), 0, 4, 1f);
		unitTypeAnimations.addAnimation(runningLeft);		
		
		// Running right animation.
		animation runningRight = new animation("runningRight", DEFAULT_LEFTRIGHT_SPRITESHEET.getAnimation(1), 0, 4, 1f);
		unitTypeAnimations.addAnimation(runningRight);
		
		// Standing up animation.
		animation standingUp = new animation("standingUp", DEFAULT_UPDOWN_SPRITESHEET.getAnimation(4), 5, 5, 1);
		unitTypeAnimations.addAnimation(standingUp);
		
		// Standing down animation.
		animation standingDown = new animation("standingDown", DEFAULT_UPDOWN_SPRITESHEET.getAnimation(0), 0, 0, 1);
		unitTypeAnimations.addAnimation(standingDown);
		
		// Running up animation.
		animation runningUp = new animation("runningUp", DEFAULT_UPDOWN_SPRITESHEET.getAnimation(2), 5, 8, 1f);
		unitTypeAnimations.addAnimation(runningUp);
		
		// Running down animation.
		animation runningDown = new animation("runningDown", DEFAULT_UPDOWN_SPRITESHEET.getAnimation(2), 0, 3, 1f);
		unitTypeAnimations.addAnimation(runningDown);
		
		// Sleeping animation
		animation sleepingLeft = new animation("sleepingLeft", DEFAULT_LEFTRIGHT_SPRITESHEET.getAnimation(4), 3, 3, 0.5f);
		unitTypeAnimations.addAnimation(sleepingLeft);
		
		// Howling starting left.
		animation howlingStartLeft = new animation("howlingStartLeft", DEFAULT_LEFTRIGHT_SPRITESHEET.getAnimation(7), 0, 3, DEFAULT_PUKE_FOR/2);
		unitTypeAnimations.addAnimation(howlingStartLeft);
		
		// Howling middle left.
		animation howlingLeft = new animation("howlingMiddleLeft", DEFAULT_LEFTRIGHT_SPRITESHEET.getAnimation(7), 4, 4, DEFAULT_PUKE_FOR);
		unitTypeAnimations.addAnimation(howlingLeft);
		
		// Howling end animation
		animation howlingEndLeft = new animation("howlingEndLeft", DEFAULT_LEFTRIGHT_SPRITESHEET.getAnimation(7), 5, 9, DEFAULT_PUKE_FOR/2);
		unitTypeAnimations.addAnimation(howlingEndLeft);
		
		// Howling starting right
		animation howlingStartRight = new animation("howlingStartRight", DEFAULT_LEFTRIGHT_SPRITESHEET.getAnimation(3), 0, 3, DEFAULT_PUKE_FOR/2);
		unitTypeAnimations.addAnimation(howlingStartRight);
		
		// Howling middle left.
		animation howlingRight = new animation("howlingMiddleRight", DEFAULT_LEFTRIGHT_SPRITESHEET.getAnimation(3), 4,4, DEFAULT_PUKE_FOR);
		unitTypeAnimations.addAnimation(howlingRight);
		
		// Howling end animation
		animation howlingEndRight = new animation("howlingEndRight", DEFAULT_LEFTRIGHT_SPRITESHEET.getAnimation(3), 5, 9, DEFAULT_PUKE_FOR/2);
		unitTypeAnimations.addAnimation(howlingEndRight);
		
		// Set animations.
		setAnimations(unitTypeAnimations);
	
	}
	
	// Deal with animations.
	@Override
	public void dealWithAnimations(int moveX, int moveY) {
			
		// No hitboxadjustment.
		setHitBoxAdjustmentY(0);
		setHitBoxAdjustmentX(0);
		if(howling) {
			howl();
		}
		else if(jumping || clawAttacking) {
			if(clawAttacking && !jumping) {
				animate("standing" + getFacingDirection());
			}
			else {
				animate("jumping" + getFacingDirection());
			}
		}
		else if(isMoving()) {
			animate("running" + getFacingDirection());
		}
		else {
			animate("standing" + getFacingDirection());
		}
	}
	
	// Claw attack.
	public void clawAttack(int x, int y) {
		clawAttacking = true;
		hasClawSpawned = false;
		slashing = false;
		clawAttackingX = x;
		clawAttackingY = y;
		
		// Start of claw attack.
		startOfClawAttack = time.getTime();
	}
	
	// Claw destroy
	public void clawDestroy() {
		currClaw.destroy();
	}
	
	// Jump
	public void slashTo(chunk c) {
		stopMove("all");
		collisionOn = false;
		setStuck(true);
		
		// Jump there
		sound s = new sound(bark);
		s.setPosition(getIntX(), getIntY(), sound.DEFAULT_SOUND_RADIUS);
		s.start();
		jumpingToX = c.getIntX() + c.getWidth()/2 - getWidth()/2;
		jumpingToY = c.getIntY() + c.getHeight()/2 - getHeight()/2;
		
		jumping = true;
		slashing = true;
		hasSlashed = false;
		riseRunSet = false;
	}
	
	// Slash to a point (issued by the command function)
	public void slashTo(int x, int y) {
		clawAttack(x,y);
	}
	
	// Deal with claw attacks.
	public void dealWithClawAttacks() {
		if(clawAttacking) {
			
			// Spawn claw phase.
			if(!hasClawSpawned && time.getTime() - startOfClawAttack < spawnClawPhase*1000) {
				hasClawSpawned = true;
				spawnClaw(clawAttackingX, clawAttackingY);
			}
			
			// Slashing phase.
			else if(hasClawSpawned && time.getTime() - startOfClawAttack > spawnClawPhase*1000 && !hasStartedJumping) {
				hasStartedJumping = true;
				slashTo((chunk)currClaw);
			}
		}
	}
	
	// Deal with jumping.
	public void dealWithJumping() {
		if(clawAttacking) {
			
			// Get current player.
			player currPlayer = player.getPlayer();
			
			// Jump to the location.
			if(jumping) {
				
				// Set rise/run
				if(!riseRunSet) {
					riseRunSet = true;
					float yDistance = (jumpingToY - getIntY());
					float xDistance = (jumpingToX - getIntX());
					float distanceXY = (float) Math.sqrt(yDistance * yDistance
							+ xDistance * xDistance);
					
					rise = 0;
					run = 0;
					
					if(distanceXY != 0) {
						rise = ((yDistance/distanceXY)*getJumpSpeed());
						run = ((xDistance/distanceXY)*getJumpSpeed());
					}
					startX = getDoubleX();
					startY = getDoubleY();
				}
				
				setDoubleX(getDoubleX() + run);
				setDoubleY(getDoubleY() + rise);
				
				// Don't let him not move at all or leave region.
				if((run == 0 && rise == 0) || ((Math.abs(jumpingToX - getIntX()) < getJumpSpeed() && Math.abs(jumpingToY - getIntY()) < getJumpSpeed()))) {
					if(currClaw != null) {
						setDoubleX(jumpingToX);
						setDoubleY(jumpingToY);
						clawDestroy();
					}
					if(getAllCommands() != null && getAllCommands().size() > 0 && getAllCommands().get(0) instanceof slashCommand) currentCommandComplete = true;
					jumping = false;
					clawAttacking = false;
					hasStartedJumping = false;
				}
				
				// If slashing, hurt the player.
				if(slashing && !hasSlashed && currPlayer.isWithin(getIntX(), getIntY(), getIntX() + getWidth(), getIntY() + getHeight())) {
					hasSlashed = true;
					slashing = false;
				}
			}
			else {
				if(mode.getCurrentMode().equals("platformer")) collisionOn = true;
				setStuck(false);
				//jumpingFinished();
			}
		}
		else {
			if(mode.getCurrentMode().equals("platformer")) collisionOn = true;
			setStuck(false);
			//jumpingFinished();
		}
	}
	
	// Fighting?
	private boolean fightInProgress = false;
	
	// Sequence number for abilities
	private int sequenceNumber = 0;

	// Casting special ability?
	public String currentAbility = "";
	public ArrayList<String> recentlyCastAbilities;
	
	// Start fight
	public void startFight() {
		fightInProgress = true;
	}
	
	// Slash number
	int slashNumber = 0;
	
	// Randomly select an ability
	public void randomlySelectAnAbility() {
		// Select a random ability to cast.
		if(recentlyCastAbilities == null) recentlyCastAbilities = new ArrayList<String>();
	
		String randomlySelectedAbility;
		if(slashNumber < DEFAULT_PUKE_EVERY_BASE) {
			randomlySelectedAbility = "slashPlatform";
			slashNumber++;
		}
		else {
			int random = utility.RNG.nextInt(2);
			if(random==0) {
				randomlySelectedAbility = "slashPlatform";
				slashNumber++;
			}
			else {
				slashNumber = 0;
				randomlySelectedAbility = "shadowPuke";
			}
		}
		
		// Set ability.
		currentAbility = randomlySelectedAbility;
		recentlyCastAbilities.add(currentAbility);
	}
	
	// Cast fight abilities
	public void castAbilities() {
		
		// Select an ability if we need to cast one.
		if(currentAbility.equals("")) randomlySelectAnAbility();
		
		if(currentAbility.equals("shadowPuke")) {
			castShadowPuke();
		}
		
		if(currentAbility.equals("slashPlatform")) {
			castSlashPlatform();
		}
		
	}
	
	// Cast slash platform
	public void castSlashPlatform() {
			
		// Slash.
		if(sequenceNumber == 0) {
			slashTowardPlayer();
			sequenceNumber++;
		}
		
		// Land and face the player.
		if(sequenceNumber == 1 && !clawAttacking) {
			sequenceNumber++;
			currentAbility = "";
			sequenceNumber = 0;
			
			// Make the platform unusable.
			ArrayList<groundTile> currPlatform = getCurrentPlatform();
			platformGlow e = new platformGlow(currPlatform.get(0).getIntX(),currPlatform.get(0).getIntY(),DEFAULT_PLATFORM_GLOW_LASTS_FOR);
		}
				
	}
	
	// Slash toward player.
	public void slashTowardPlayer() {
		faceTowardPlayer();
		ArrayList<groundTile> nextPlatform = getClosestPlatformInDirection(getFacingDirection());
		if(nextPlatform != null) slashTo(nextPlatform.get(1).getIntX(), nextPlatform.get(1).getIntY() - getHeight() + 10);
	}
	
	// Get current paltform
	public ArrayList<groundTile> getCurrentPlatform() {
		ArrayList<groundTile> closestPlatform = platforms.get(0);
		double closestDistance = Math.sqrt(Math.pow(platforms.get(0).get(1).getIntX() + platforms.get(0).get(1).getWidth()/2 - (getIntX() + getWidth()/2),2) + 
									     Math.pow(platforms.get(0).get(1).getIntY() + platforms.get(0).get(1).getHeight()/2 - (getIntY() + getHeight()/2),2));
		for(int i = 0; i < platforms.size(); i++) {
			ArrayList<groundTile> currPlatform = platforms.get(i);
			double distance = Math.sqrt(Math.pow(platforms.get(i).get(1).getIntX() + platforms.get(i).get(1).getWidth()/2 - (getIntX() + getWidth()/2),2) + 
				     Math.pow(platforms.get(i).get(1).getIntY() + platforms.get(i).get(1).getHeight()/2 - (getIntY() + getHeight()/2),2));
			if(distance < closestDistance) {
				closestDistance = distance;
				closestPlatform = currPlatform;
			}
		}
		return closestPlatform;
	}
	
	// Get closest platform to player
	public ArrayList<groundTile> getClosestPlatformToPlayer() {
		ArrayList<groundTile> closestPlatform = platforms.get(0);
		double closestDistance = Math.sqrt(Math.pow(platforms.get(0).get(1).getIntX() + platforms.get(0).get(1).getWidth()/2 - (player.getPlayer().getIntX() + player.getPlayer().getWidth()/2),2) + 
									     Math.pow(platforms.get(0).get(1).getIntY() + platforms.get(0).get(1).getHeight()/2 - (player.getPlayer().getIntY() + player.getPlayer().getHeight()/2),2));
		for(int i = 0; i < platforms.size(); i++) {
			ArrayList<groundTile> currPlatform = platforms.get(i);
			double distance = Math.sqrt(Math.pow(platforms.get(i).get(1).getIntX() + platforms.get(i).get(1).getWidth()/2 - (player.getPlayer().getIntX() + player.getPlayer().getWidth()/2),2) + 
				     Math.pow(platforms.get(i).get(1).getIntY() + platforms.get(i).get(1).getHeight()/2 - (player.getPlayer().getIntY() + player.getPlayer().getHeight()/2),2));
			if(distance < closestDistance) {
				closestDistance = distance;
				closestPlatform = currPlatform;
			}
		}
		return closestPlatform;
	}
	
	// Get closest platform to from
	public ArrayList<groundTile> getClosestPlatformToFrom(ArrayList<groundTile> to, ArrayList<ArrayList<groundTile>> from) {
		ArrayList<groundTile> closestPlatform = from.get(0);
		double closestDistance = Math.sqrt(Math.pow(from.get(0).get(1).getIntX() + from.get(0).get(1).getWidth()/2 - 
												(to.get(1).getIntX() + to.get(1).getWidth()/2),2) + 
									     Math.pow(from.get(0).get(1).getIntY() + from.get(0).get(1).getHeight()/2 - 
									    		 (to.get(1).getIntY() + to.get(1).getHeight()/2),2));
		for(int i = 0; i < from.size(); i++) {
			ArrayList<groundTile> currPlatform = from.get(i);
			double distance = Math.sqrt(Math.pow(from.get(i).get(1).getIntX() + from.get(i).get(1).getWidth()/2 - (to.get(1).getIntX() + to.get(1).getWidth()/2),2) + 
				     Math.pow(from.get(i).get(1).getIntY() + from.get(i).get(1).getHeight()/2 - (to.get(1).getIntY() + to.get(1).getHeight()/2),2));
			if(distance < closestDistance) {
				closestDistance = distance;
				closestPlatform = currPlatform;
			}
		}
		return closestPlatform;
	}
	
	// Get closest platform in direction.
	public ArrayList<groundTile> getClosestPlatformInDirection(String direction) {
		
		/* COMMENTED OUT MOVES TOWARD THE PLAYER PLATFORM BY PLATFORM
		 * ArrayList<groundTile> currentPlatform = getCurrentPlatform();
		ArrayList<groundTile> playerPlatform = getClosestPlatformToPlayer();
		
		// Exclude current platform and all platforms behind 
		ArrayList<ArrayList<groundTile>> outOfPlatforms = new ArrayList<ArrayList<groundTile>>();
		for(int i = 0; i < platforms.size(); i++) {
			if(direction.equals("Left") && platforms.get(i).get(0).getIntX() < currentPlatform.get(0).getIntX()) {
				outOfPlatforms.add(platforms.get(i));
			}
			if(direction.equals("Right") && platforms.get(i).get(0).getIntX() > currentPlatform.get(0).getIntX() + currentPlatform.get(0).getWidth()) {
				outOfPlatforms.add(platforms.get(i));
			}
		}
		
		// Select two closest platforms out of those platforms.
		ArrayList<groundTile> firstPlatform = getClosestPlatformToFrom(currentPlatform, outOfPlatforms);
		outOfPlatforms.remove(firstPlatform);
		ArrayList<groundTile> secondPlatform = getClosestPlatformToFrom(currentPlatform, outOfPlatforms);
		
		double firstDistanceToPlayer = Math.sqrt(Math.pow(firstPlatform.get(1).getIntX() + firstPlatform.get(1).getWidth()/2 - 
				(playerPlatform.get(1).getIntX() + playerPlatform.get(1).getWidth()/2),2) + 
			     Math.pow(firstPlatform.get(1).getIntY() + firstPlatform.get(1).getHeight()/2 - 
			    		 (playerPlatform.get(1).getIntY() + playerPlatform.get(1).getHeight()/2),2));
		double secondDistanceToPlayer = Math.sqrt(Math.pow(secondPlatform.get(1).getIntX() + secondPlatform.get(1).getWidth()/2 - 
				(playerPlatform.get(1).getIntX() + playerPlatform.get(1).getWidth()/2),2) + 
			     Math.pow(secondPlatform.get(1).getIntY() + secondPlatform.get(1).getHeight()/2 - 
			    		 (playerPlatform.get(1).getIntY() + playerPlatform.get(1).getHeight()/2),2));
		if(firstDistanceToPlayer > secondDistanceToPlayer) {
			return secondPlatform;
		}
		else {
			return firstPlatform;
		}*/
		
		ArrayList<groundTile> currentPlatform = getCurrentPlatform();
		ArrayList<groundTile> playerPlatform = getClosestPlatformToPlayer();
		
		// Exclude current platform and all platforms behind 
		ArrayList<ArrayList<groundTile>> outOfPlatforms = new ArrayList<ArrayList<groundTile>>();
		for(int i = 0; i < platforms.size(); i++) {
			if(direction.equals("Left") && platforms.get(i).get(0).getIntX() <= playerPlatform.get(0).getIntX()) {
				outOfPlatforms.add(platforms.get(i));
			}
			if(direction.equals("Right") && platforms.get(i).get(0).getIntX() >= playerPlatform.get(0).getIntX() + currentPlatform.get(0).getWidth()) {
				outOfPlatforms.add(platforms.get(i));
			}
		}
		
		// Select two closest platforms out of those platforms
		ArrayList<ArrayList<groundTile>> platformsToChooseFrom = new ArrayList<ArrayList<groundTile>>();
		for(int i = 0; i < 3 && 0 < outOfPlatforms.size(); i++) {
			ArrayList<groundTile> currPlatform = getClosestPlatformToFrom(playerPlatform, outOfPlatforms);
			platformsToChooseFrom.add(currPlatform);
			outOfPlatforms.remove(currPlatform);
		}
		
		if(platformsToChooseFrom.size() != 0) {
			int random = utility.RNG.nextInt(platformsToChooseFrom.size());
			return platformsToChooseFrom.get(random);
		}
		else {
			return null;
		}
	}
	
	// Cast shadow bombardment
	public void castShadowPuke() {
			
		// Slash.
		if(sequenceNumber == 0) {
			sequenceNumber++;
		}
		
		// Land and face the player.
		if(sequenceNumber == 1) {
			faceTowardPlayer();
			sequenceNumber++;
		}
		
		// Howl (which makes dudes)
		if(sequenceNumber == 2) {
			startHowl();
			sequenceNumber++;
		}
		
		// Finish
		if(sequenceNumber == 3 && !howling) {
			sequenceNumber++;
			currentAbility = "";
			sequenceNumber = 0;
		}
				
	}
	
	// List of shadow lines
	private ArrayList<Integer> lineSpawnsX = new ArrayList<Integer>();
	private ArrayList<Integer> lineSpawnsY = new ArrayList<Integer>();
	private ArrayList<String> lineTypes = new ArrayList<String>();
	
	// Spawn a shadow line at 
	public void spawnShadowLineAt(String from, int x, int y) {
		
		// Add a new line.
		lineSpawnsX.add(x);
		lineSpawnsY.add(y);
		lineTypes.add(from);
	}
	
	// Spawn every.
	private float spawnEvery = 0.1f;
	private long lastShadowSpawn = 0;
	
	// Deal with lines
	public void dealWithShadowLines() {
		
		// Spawn units and move them.
		if(lineSpawnsX != null) {
			if(lastShadowSpawn == 0) lastShadowSpawn = time.getTime();
			else if(time.getTime() - lastShadowSpawn > spawnEvery*1000) {
				for(int i = 0; i < lineSpawnsX.size(); i++) {
					shadowDude u = new shadowDude(lineSpawnsX.get(i), lineSpawnsY.get(i));
					u.setMoveSpeed(4f);
					u.setDestroyTimer(30);
					
					if(lineTypes.get(i).equals("verticalDown")) {
						u.setMovingDown(true);
					}
					if(lineTypes.get(i).equals("verticalUp")) {
						u.setMovingUp(true);
					}
					if(lineTypes.get(i).equals("horizontalRight")) {
						u.setMovingRight(true);
					}
					if(lineTypes.get(i).equals("horizontalLeft")) {
						u.setMovingLeft(true);
					}
				}
				
				lastShadowSpawn = time.getTime();
			}
		}
	}
	
	// Spawn line at mouth.
	public void spawnLineAtMouth() {
		
		if(facingDirection.equals("Left")) {
			spawnShadowLineAt("horizontalLeft", getIntX()-15,getIntY());
			spawnShadowLineAt("verticalDown", getIntX()-15,getIntY());
			spawnShadowLineAt("verticalUp", getIntX()-15,getIntY());
		}
		
		if(facingDirection.equals("Right")) {
			spawnShadowLineAt("horizontalRight", getIntX()+getWidth()+15-shadowDude.getDefaultWidth(),getIntY());
			spawnShadowLineAt("verticalDown", getIntX()+getWidth()+15-shadowDude.getDefaultWidth(),getIntY());
			spawnShadowLineAt("verticalUp", getIntX()+getWidth()+15-shadowDude.getDefaultWidth(),getIntY());
		}
		
	}
	
	// Stop spawning line at mouth.
	public void stopSpawningLineAtMouth() {
		lineTypes.clear();
		lineSpawnsX.clear();
		lineSpawnsY.clear();
	}
	
	// Howl stuff
	private boolean howling = false;
	private long startOfHowl = 0;
	static String howl = "sounds/effects/bosses/shadowOfTheDenmother/spookyHowl1.wav";
	private static String growl = "sounds/effects/bosses/shadowOfTheDenmother/spookyGrowl.wav";
	private static String bark = "sounds/effects/bosses/shadowOfTheDenmother/wolfBark.wav";
	
	// Starthowl.
	public void startHowl() {
		
		// Howl.
		howling = true;
		startOfHowl = time.getTime();
	}
	
	// Deal with howling
	public void howl() {
		
		// First start howling.
		if(!getCurrentAnimation().getName().contains("howl")) {
			animate("howlingStart" + getFacingDirection());
			startOfHowl = time.getTime();
		}
		
		// Move to middle of howl.
		else if(getCurrentAnimation().getName().contains("howlingStart") &&
				time.getTime() - startOfHowl >= getCurrentAnimation().getTimeToComplete()*1000) {
			sound s = new sound(growl);
			s.start();
			animate("howlingMiddle" + getFacingDirection());
			startOfHowl = time.getTime();
			
			// Spawn shadow dudes.
			spawnLineAtMouth();
		}
		
		// Move to end of howl.
		else if(getCurrentAnimation().getName().contains("howlingMiddle") &&
				time.getTime() - startOfHowl >= getCurrentAnimation().getTimeToComplete()*1000) {
			animate("howlingEnd" + getFacingDirection());
			startOfHowl = time.getTime();
			stopSpawningLineAtMouth();
		}
		
		
		// Move to end of howl.
		else if(getCurrentAnimation().getName().contains("howlingEnd") &&
				time.getTime() - startOfHowl >= getCurrentAnimation().getTimeToComplete()*1000) {
			howling = false;
			startOfHowl = 0;
		}
	}
	
	// List of spots
	private intTuple platformsStart = new intTuple(13000,233);
	private ArrayList<ArrayList<groundTile>> platforms;
	
	// How often to cast claw ability.
	private static float spawnClawPhase = 0.75f;
	
	// Spawn platforms
	public void spawnPlatforms() {
		
		// Spawn platforms.
		platforms = new ArrayList<ArrayList<groundTile>>();
		for(int i = 0; i < 10; i++) {
			for(int j = 0; j < 6; j++) {
				platforms.add(new ArrayList<groundTile>());
				for(int m = 0; m < 3; m++) {
					groundTile tile;
					if(j%2 != 0) {
						tile = new tombEdge(platformsStart.x + i*200 + m*32, platformsStart.y + j*120, 0);
					}
					else {
						tile = new tombEdge(platformsStart.x + i*200 + 100 + m*32, platformsStart.y + j*120, 0);
					}
					platforms.get(i*6 + j).add(tile);
				}
			}
		}
		
		// Move wolfie and player to the middle.
		player.getPlayer().setDoubleX(platforms.get(6*3 + 3).get(1).getIntX());
		player.getPlayer().setDoubleY(platforms.get(6*3 + 3).get(1).getIntY()-player.getPlayer().getHeight());
		setDoubleX(platforms.get(6*4 + 3).get(1).getIntX()-getWidth()/2 + platforms.get(4*5+3).get(1).getWidth()/2);
		setDoubleY(platforms.get(6*4 + 3).get(1).getIntY()-getHeight());
		faceTowardPlayer();
	}

	public void spawnClaw(int x, int y) {	
		int spawnX = x;
		int spawnY = y;
		currClaw = new clawMarkRed(spawnX,spawnY,0);
		faceTowardThing(currClaw);
	}
	
	// Update.
	@Override
	public void updateUnit() {
		
		// Wolf jumping
		dealWithJumping();
		dealWithClawAttacks();
		
		// Abilities
		dealWithShadowLines();
		
		// Only do fight things if the fight is in progress.
		if(fightInProgress) {
			castAbilities();
		}
	}
	
}