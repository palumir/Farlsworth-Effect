package units.bosses;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import doodads.sheepFarm.clawMarkRed;
import drawing.drawnObject;
import drawing.gameCanvas;
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
import units.unitTypes.tomb.lightDude;
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
	private float pukeFor = 1f;
	private static int DEFAULT_PUKE_EVERY_BASE = 2;
	
	// How long does platform glow last for
	private static float DEFAULT_PLATFORM_GLOW_LASTS_FOR = 15f;
	
	// Number of hits total
	private static int numberOfHitsToDieTotal = 6;
	private int numberOfHitsToDie = numberOfHitsToDieTotal;

	// Unit sprite stuff.
	private static spriteSheet DEFAULT_LEFTRIGHT_SPRITESHEET = new spriteSheet(new spriteSheetInfo(
			"images/units/bosses/wolfless/blackWolfLeftRight.png",
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
	
	// Phase stuff
	protected boolean shadowPuke = false;
	protected boolean advancedShadowPuke = false;
	protected float shadowDudeMoveSpeed = 4;

	//////////////////
	//// METHODS /////
	//////////////////
	
	public shadowOfTheDenmother() {
		super(unitTypeRef, DEFAULT_UNIT_NAME, 0, 0);
		killsPlayer = true;
		lineSpawnsX = new ArrayList<Integer>();
		lineSpawnsY = new ArrayList<Integer>();
		lineTypes = new ArrayList<String>();
		
		// Platforms
		spawnPlatforms();
		
		// Frig you.
		addAnimations();
		
		// Preload shadowDude stuff.
		int widthOfShadowDude = shadowDude.getDefaultWidth();
	}
	
	// Add animations.
	public void addAnimations() {
		
		// Deal with animations
		animationPack unitTypeAnimations = new animationPack();
		
		// Jumping left animation.
		animation jumpingLeft = new animation("jumpingLeft", DEFAULT_LEFTRIGHT_SPRITESHEET.getAnimation(6), 4, 4, 1);
		unitTypeAnimations.addAnimation(jumpingLeft);
		
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
		
		// Sleeping animation
		animation sleepingLeft = new animation("sleepingLeft", DEFAULT_LEFTRIGHT_SPRITESHEET.getAnimation(4), 3, 3, 0.5f);
		unitTypeAnimations.addAnimation(sleepingLeft);
		
		// Howling starting left.
		animation howlingStartLeft = new animation("howlingStartLeft", DEFAULT_LEFTRIGHT_SPRITESHEET.getAnimation(7), 0, 3, pukeFor/2);
		unitTypeAnimations.addAnimation(howlingStartLeft);
		
		// Howling middle left.
		animation howlingLeft = new animation("howlingMiddleLeft", DEFAULT_LEFTRIGHT_SPRITESHEET.getAnimation(7), 4, 4, pukeFor);
		unitTypeAnimations.addAnimation(howlingLeft);
		
		// Howling end animation
		animation howlingEndLeft = new animation("howlingEndLeft", DEFAULT_LEFTRIGHT_SPRITESHEET.getAnimation(7), 5, 9, pukeFor/2);
		unitTypeAnimations.addAnimation(howlingEndLeft);
		
		// Howling starting right
		animation howlingStartRight = new animation("howlingStartRight", DEFAULT_LEFTRIGHT_SPRITESHEET.getAnimation(3), 0, 3, pukeFor/2);
		unitTypeAnimations.addAnimation(howlingStartRight);
		
		// Howling middle left.
		animation howlingRight = new animation("howlingMiddleRight", DEFAULT_LEFTRIGHT_SPRITESHEET.getAnimation(3), 4,4, pukeFor);
		unitTypeAnimations.addAnimation(howlingRight);
		
		// Howling end animation
		animation howlingEndRight = new animation("howlingEndRight", DEFAULT_LEFTRIGHT_SPRITESHEET.getAnimation(3), 5, 9, pukeFor/2);
		unitTypeAnimations.addAnimation(howlingEndRight);
		
		// Attacking left animation.
		animation trailLeft = new animation("trailLeft", DEFAULT_LEFTRIGHT_SPRITESHEET.getAnimation(8), 0, 0, 1);
		unitTypeAnimations.addAnimation(trailLeft);
		
		// Attacking right animation.
		animation trailRight = new animation("trailRight", DEFAULT_LEFTRIGHT_SPRITESHEET.getAnimation(9), 0, 0, 1);
		unitTypeAnimations.addAnimation(trailRight);

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
	
	// Respond to destroy
	@Override
	public void respondToDestroy() {
		if(currentLightDude != null) currentLightDude.destroy();
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
			if(shadowPuke) slashNumber++;
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
			
			// Sound
			sound s = new sound(land);
			s.start();
			
			// Make the platform unusable.
			ArrayList<groundTile> currPlatform = getCurrentPlatform();
			platformGlow e = new platformGlow(currPlatform.get(0).getIntX(),currPlatform.get(0).getIntY(),DEFAULT_PLATFORM_GLOW_LASTS_FOR);
		}	
	}
	
	// Slash toward player.
	public void slashTowardPlayer() {
		faceTowardPlayer();
		ArrayList<groundTile> nextPlatform = getClosestPlatformInDirection(getFacingDirection());
		if(nextPlatform != null) slashTo(nextPlatform.get(1).getIntX(), nextPlatform.get(1).getIntY() - getHeight()+10);
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
		
		ArrayList<groundTile> currentPlatform = getCurrentPlatform();
		ArrayList<groundTile> playerPlatform = getClosestPlatformToPlayer();
		
		// Exclude current platform and all platforms behind 
		ArrayList<ArrayList<groundTile>> outOfPlatforms = new ArrayList<ArrayList<groundTile>>();
		for(int i = 0; i < platforms.size(); i++) {
			if(direction.equals("Left") && platforms.get(i).get(0).getIntX() <= playerPlatform.get(0).getIntX()) {
				outOfPlatforms.add(platforms.get(i));
			}
			if(direction.equals("Right") && platforms.get(i).get(0).getIntX() >= playerPlatform.get(0).getIntX()) {
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
			if(shadowPuke) startHowl("shadowPuke");
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
	private ArrayList<Integer> lineSpawnsX;
	private ArrayList<Integer> lineSpawnsY;
	private ArrayList<String> lineTypes;
	
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
			//if(lastShadowSpawn == 0) lastShadowSpawn = time.getTime();
			if(time.getTime() - lastShadowSpawn > spawnEvery*1000) {
				for(int i = 0; i < lineSpawnsX.size(); i++) {
					unit u = new shadowDude(lineSpawnsX.get(i), lineSpawnsY.get(i));
					u.setMoveSpeed(shadowDudeMoveSpeed);
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
			
			if(advancedShadowPuke) {
				spawnShadowLineAt("verticalDown", getIntX()-15,getIntY());
				spawnShadowLineAt("verticalUp", getIntX()-15,getIntY());
			}
		}
		
		if(facingDirection.equals("Right")) {
			spawnShadowLineAt("horizontalRight", getIntX()+getWidth()+15-25,getIntY());
			
			if(advancedShadowPuke) {
				spawnShadowLineAt("verticalDown", getIntX()+getWidth()+15-25,getIntY());
				spawnShadowLineAt("verticalUp", getIntX()+getWidth()+15-25,getIntY());
			}
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
	static String howl = "sounds/effects/bosses/shadowOfTheDenmother/spookyHowl.wav";
	private static String growl = "sounds/effects/bosses/shadowOfTheDenmother/spookyGrowl.wav";
	private static String bark = "sounds/effects/bosses/shadowOfTheDenmother/wolfBark.wav";
	private static String land = "sounds/effects/bosses/shadowOfTheDenmother/land.wav";
	private static String scream = "sounds/effects/bosses/shadowOfTheDenmother/scream.wav";
	private static String lightDudeBreak = "sounds/effects/bosses/shadowOfTheDenmother/lightDudeBreak.wav";

	// Type of howl
	private String typeOfHowl;
	
	// Starthowl.
	public void startHowl(String type) {
		
		// Howl.
		typeOfHowl = type;
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
			
			if(typeOfHowl.equals("shadowPuke")) {
				sound s = new sound(growl);
				s.start();
			}
			animate("howlingMiddle" + getFacingDirection());
			startOfHowl = time.getTime();
			
			// Spawn shadow dudes.
			if(typeOfHowl.equals("shadowPuke")) spawnLineAtMouth();
		}
		
		// Move to end of howl.
		else if(getCurrentAnimation().getName().contains("howlingMiddle") &&
				time.getTime() - startOfHowl >= getCurrentAnimation().getTimeToComplete()*1000) {
			animate("howlingEnd" + getFacingDirection());
			startOfHowl = time.getTime();
			if(typeOfHowl.equals("shadowPuke")) stopSpawningLineAtMouth();
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
	private ArrayList<ArrayList<groundTile>> insidePlatforms;
	
	// How often to cast claw ability.
	private float spawnClawPhase = 1.5f;
	
	// Spawn platforms
	public void spawnPlatforms() {
		
		// Spawn platforms.
		platforms = new ArrayList<ArrayList<groundTile>>();
		insidePlatforms = new ArrayList<ArrayList<groundTile>>();
		for(int i = 0; i < 6; i++) {
			for(int j = 0; j < 6; j++) {
				platforms.add(new ArrayList<groundTile>());
				
				if(i != 0 && i != 5 && j != 0 && j != 5) {
					insidePlatforms.add(new ArrayList<groundTile>());
				}
				
				for(int m = 0; m < 3; m++) {
					groundTile tile;
					if(j%2 != 0) {
						tile = new tombEdge(platformsStart.x + i*216 + m*32, platformsStart.y + j*120, 0);
					}
					else {
						tile = new tombEdge(platformsStart.x + i*216 + 108 + m*32, platformsStart.y + j*120, 0);
					}
					platforms.get(i*6 + j).add(tile);
					
					// Add to inside platforms.
					if(i != 0 && i != 5 && j != 0 && j != 5) {	
						insidePlatforms.get(insidePlatforms.size()-1).add(tile);
					}
					
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
	
	@Override
	public void hurtPeople(int leniency) {
		player currPlayer = player.getPlayer();

		// If someone is in the explosion radius, hurt.
		if(currPlayer.isWithin(this.getIntX() + leniency, this.getIntY() + leniency, this.getIntX() + this.getWidth() - leniency, this.getIntY() + this.getHeight() - leniency) 
					&& !currPlayer.isIlluminated() && !isIlluminated()) {
				currPlayer.hurt(1, 1);
		}
		
	}

	public void spawnClaw(int x, int y) {	
		int spawnX = x;
		int spawnY = y;
		currClaw = new clawMarkRed(spawnX,spawnY,0);
		faceTowardThing(currClaw);
	}
	
	// Current light dude.
	lightDude currentLightDude;
	
	// Get hurt by light.
	public void getHurtByLight() {
		
		// Get hurt.
		numberOfHitsToDie--;
		sound s = new sound(lightDudeBreak);
		s.start();
		s = new sound(scream);
		s.start();
		
		// Change phase.
		if(numberOfHitsToDie==5) {
			spawnClawPhase = 1.05f;
			spawnEvery = 0.1f;
			shadowDudeMoveSpeed = 4f;
			pukeFor = 1f;
			shadowPuke = true;
		}
		
		if(numberOfHitsToDie==4) {
			spawnClawPhase = 0.90f;
			advancedShadowPuke = true;
			spawnEvery = 0.09f;
			shadowDudeMoveSpeed = 4.5f;
			pukeFor = 0.85f;
		}
		
		if(numberOfHitsToDie==3) {
			spawnClawPhase = 0.75f;
			spawnEvery = 0.08f;
			shadowDudeMoveSpeed = 5f;
			pukeFor = 0.7f;
		}
		
		if(numberOfHitsToDie==2) {
			spawnClawPhase = 0.6f;
			spawnEvery = 0.07f;
			shadowDudeMoveSpeed = 5.5f;
			pukeFor = 0.55f;
		}
		
		// Kill boss.
		if(numberOfHitsToDie<=0) {
			defeatBoss();
		}
	}
	
	// Defeat boss
	public void defeatBoss() {
		defeat();
		die();
	}
	
	// Deal with light dudes
	public void dealWithLightDudes() {
		
		// Spawn a new one.
		if(currentLightDude == null || !currentLightDude.isExists()) {
			
			ArrayList<ArrayList<groundTile>> chooseFrom = new ArrayList<ArrayList<groundTile>>(insidePlatforms);
			int random = utility.RNG.nextInt(chooseFrom.size());
			while(chooseFrom.get(random).get(1).isOnScreen()) {
				chooseFrom.remove(random);
				random = utility.RNG.nextInt(chooseFrom.size());
			}
			currentLightDude = new lightDude(chooseFrom.get(random).get(1).getIntX(), chooseFrom.get(random).get(1).getIntY() - lightDude.getDefaultHeight());
			
		}
		
		// Get hurt.
		if(isIlluminated()) {
			
			currentLightDude.destroy();
			getHurtByLight();
			
		}
	}
	
	@Override
	public void drawObject(Graphics g) {
		drawUnitSpecialStuff(g);
	}
	
	// Trail stuff.
	private float trailInterval = 0.0125f;
	private long lastTrail = 0;
	private int trailLength = 20;
	private ArrayList<intTuple> trail;
	private ArrayList<BufferedImage> trailImage;
	
	@Override
	public void drawUnitSpecialStuff(Graphics g) {
		
		// Of course only draw if the animation is not null.
		if(getCurrentAnimation() != null) {
			if(jumping) {
				
				// If it's empty, make it.
				if(trail == null) trail = new ArrayList<intTuple>();
				if(trailImage == null) trailImage = new ArrayList<BufferedImage>();
				// If we've passed trailInterval seconds. Add a new intTuple to trail and remove oldest one.
				if(time.getTime() - lastTrail > trailInterval*1000) {
					lastTrail = time.getTime();
					if(trail.size() >= trailLength) {
						trailImage.remove(0);
						trail.remove(0);
					}
					trailImage.add(getAnimations().getAnimation("jumping" + getFacingDirection()).getSprites().get(0));
					trail.add(new intTuple(getIntX(),getIntY()));
				}
				
				g.drawImage(getCurrentAnimation().getCurrentFrame(), 
						getDrawX(), 
						getDrawY(), 
						(int)(gameCanvas.getScaleX()*getCurrentAnimation().getCurrentFrame().getWidth()), 
						(int)(gameCanvas.getScaleY()*getCurrentAnimation().getCurrentFrame().getHeight()), 
						null);
				
				// Draw trail.
				for(int i = 0; i < trail.size(); i++) {
					float alpha = ((float)i)/(float)trail.size();
					Graphics2D g2d = (Graphics2D) g.create();
					g2d.setComposite(AlphaComposite.SrcOver.derive(alpha*.3f));
					g2d.drawImage(trailImage.get(i), 
							drawnObject.calculateDrawX(this, trail.get(i).x), 
							drawnObject.calculateDrawY(this, trail.get(i).y), 
							(int)(gameCanvas.getScaleX()*trailImage.get(i).getWidth()), 
							(int)(gameCanvas.getScaleY()*trailImage.get(i).getHeight()), 
							null);
				}
			}
			
			else {
				if(trail != null) {
					
					// If we've passed trailInterval seconds. Add a new intTuple to trail and remove oldest one.
					if(time.getTime() - lastTrail > trailInterval*1000 && trail.size() > 0) {
						lastTrail = time.getTime();
						trail.remove(0);
						trailImage.remove(0);
					}
				}
				g.drawImage(getCurrentAnimation().getCurrentFrame(), 
						getDrawX(), 
						getDrawY(), 
						(int)(gameCanvas.getScaleX()*getCurrentAnimation().getCurrentFrame().getWidth()), 
						(int)(gameCanvas.getScaleY()*getCurrentAnimation().getCurrentFrame().getHeight()), 
						null);
				
				if(trail != null) {
					// Draw trail.
					for(int i = 0; i < trail.size(); i++) {
						float alpha = ((float)i)/(float)trail.size();
						Graphics2D g2d = (Graphics2D) g.create();
						g2d.setComposite(AlphaComposite.SrcOver.derive(alpha*.3f));
						g2d.drawImage(trailImage.get(i), 
								drawnObject.calculateDrawX(this, trail.get(i).x), 
								drawnObject.calculateDrawY(this, trail.get(i).y), 
								(int)(gameCanvas.getScaleX()*trailImage.get(i).getWidth()), 
								(int)(gameCanvas.getScaleY()*trailImage.get(i).getHeight()), 
								null);
					}
				}
			}
			// Of course only draw if the animation is not null.
			g.drawImage(getCurrentAnimation().getCurrentFrame(), 
					getDrawX(), 
					getDrawY(), 
					(int)(gameCanvas.getScaleX()*getCurrentAnimation().getCurrentFrame().getWidth()), 
					(int)(gameCanvas.getScaleY()*getCurrentAnimation().getCurrentFrame().getHeight()), 
					null);
		}
	}

	// Update.
	@Override
	public void updateUnit() {
		
		// Wolf jumping
		dealWithJumping();
		dealWithClawAttacks();
		
		// Light dudes
		dealWithLightDudes();
		
		// Abilities
		dealWithShadowLines();
		
		// Only do fight things if the fight is in progress.
		if(fightInProgress) {
			castAbilities();
		}
	}
	
}