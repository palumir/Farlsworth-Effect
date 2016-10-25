package units.bosses.wolfless;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import doodads.sheepFarm.clawMarkRed;
import doodads.tomb.stairsUp;
import drawing.drawnObject;
import drawing.gameCanvas;
import drawing.spriteSheet;
import drawing.spriteSheet.spriteSheetInfo;
import drawing.animation.animation;
import drawing.animation.animationPack;
import effects.effectTypes.platformGlow;
import modes.mode;
import sounds.music;
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
import utilities.mathUtils;
import utilities.time;
import utilities.utility;
import zones.endZone.subZones.endZone;
import zones.sheepFarm.subZones.sheepFarm;

public class wolfless extends boss {
	
	// Default name.
	private static String DEFAULT_UNIT_NAME = "(wolfless)";
	
	// Default jump speed
	private static int DEFAULT_UNIT_JUMPSPEED = 13;
	
	// Beta stats
	private static float DEFAULT_MOVESPEED_BETA = 2f;
	
	// Default movespeed of shadows
	private float DEFAULT_SHADOWCAGE_MOVESPEED = 0.75f;
	
	// How long to shadow puke for
	private float pukeFor = 1f;
	private static int DEFAULT_PUKE_EVERY_BASE = 1;
	
	// Puke every
	private int pukeEvery = DEFAULT_PUKE_EVERY_BASE;
	
	// How long does platform glow last for
	private static float DEFAULT_PLATFORM_GLOW_LASTS_FOR = 10f;
	
	// Number of hits total
	private static int numberOfHitsToDieTotal = 4; // 6
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
	
	// Phase
	private int phase = 1; // Start in phase 1
	
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
	protected float shadowDudeMoveSpeed = 4;
	
	// Next abiltiy shadow puke?
	protected boolean nextAbilityShadowPuke = false;

	//////////////////
	//// METHODS /////
	//////////////////
	
	public wolfless() {
		super(unitTypeRef, DEFAULT_UNIT_NAME, 0, 0);
		killsPlayer = true;
		lineSpawnsX = new ArrayList<Integer>();
		lineSpawnsY = new ArrayList<Integer>();
		lineAngles = new ArrayList<Integer>();
		
		// Platforms
		spawnPlatforms();
		
		// Frig you.
		addAnimations();
		
		// Preload shadowDude stuff.
		int widthOfShadowDude = shadowDude.getDefaultWidth();
		
		forceInFront = true;
		changePhase();
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
		
		// Attacking left animation.
		animation sleepLeft = new animation("sleepLeft", DEFAULT_LEFTRIGHT_SPRITESHEET.getAnimation(4), 3, 3, 1);
		unitTypeAnimations.addAnimation(sleepLeft);
		
		// Attacking right animation.
		animation sleepRight = new animation("sleepRight", DEFAULT_LEFTRIGHT_SPRITESHEET.getAnimation(0), 3, 3, 1);
		unitTypeAnimations.addAnimation(sleepRight);

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
		
		else if(sleeping) {
			animateSleeping();
		}
		
		else if(isJumping() || clawAttacking) {
			if(clawAttacking && !isJumping()) {
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
		if(currentLightDudes != null) {
			for(int i =0; i < currentLightDudes.size(); i++) {
				currentLightDudes.get(i).destroy();
			}
		}
		if(currClaw != null) currClaw.destroy();
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
		
		setJumping(true);
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
			if(isJumping()) {
				
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
					if(getAllCommands() != null && getAllCommands().size() > 0 && getAllCommands().get(0) instanceof slashCommand) setCurrentCommandComplete(true);
					setJumping(false);
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
		if(nextAbilityShadowPuke) {
			randomlySelectedAbility = "shadowPuke";
			nextAbilityShadowPuke = false;
			slashNumber = 0;
		}
		else {
			if(slashNumber < pukeEvery) {
				randomlySelectedAbility = "slashPlatform";
				if(shadowPuke) slashNumber++;
			}
			else {
				randomlySelectedAbility = "slashPlatform";
				slashNumber++;
			}
		}
		
		// Set ability.
		currentAbility = randomlySelectedAbility;
		recentlyCastAbilities.add(currentAbility);
	}
	
	// Cast fight abilities
	public void castAbilities() {
		
		if(!player.getPlayer().isUnitIsDead()) {
			
			// Select an ability if we need to cast one.
			if(currentAbility.equals("")) randomlySelectAnAbility();
			
			if(currentAbility.equals("shadowPuke")) {
				castShadowPuke();
			}
			
			if(currentAbility.equals("slashPlatform")) {
				castSlashPlatform();
			}
		}
	}
	
	boolean slashSuccess = false;
	
	// Cast slash platform
	public void castSlashPlatform() {
			
		// Slash.
		if(sequenceNumber == 0) {
			slashSuccess = slashTowardPlayer();
			sequenceNumber++;
		}
		
		if(slashSuccess) {
			
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
				isInjured = false;
				platformGlow e = new platformGlow(currPlatform.get(0).getIntX(),currPlatform.get(0).getIntY(),DEFAULT_PLATFORM_GLOW_LASTS_FOR);
				slashSuccess = false;
			}	
		}
		else {
			sequenceNumber = 0;
			currentAbility = "";
		}
	}
	
	// Slash toward player.
	public boolean slashTowardPlayer() {
		faceTowardPlayer();
		ArrayList<groundTile> nextPlatform = null;
		if(phase == 1) {
			nextPlatform = getClosestPlatformInDirection(getFacingDirection());
		}
		else {
			nextPlatform = getClosestPlatformToPlayer(getFacingDirection());
		}
		if(nextPlatform != null) slashTo(nextPlatform.get(1).getIntX(), nextPlatform.get(1).getIntY() - getHeight()+10);
		return nextPlatform != null;
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
	public ArrayList<groundTile> getClosestPlatformToPlayer(String direction) {
		
		ArrayList<groundTile> currentPlatform = getCurrentPlatform();
		ArrayList<groundTile> playerPlatform = getClosestPlatformToPlayer();
		
		// Exclude current platform and all platforms behind 
		ArrayList<ArrayList<groundTile>> outOfPlatforms = new ArrayList<ArrayList<groundTile>>();
		for(int i = 0; i < platforms.size(); i++) {
			if(direction.equals("Left") && platforms.get(i).get(0).getIntX() < currentPlatform.get(0).getIntX()) {
				outOfPlatforms.add(platforms.get(i));
			}
			if(direction.equals("Right") && platforms.get(i).get(0).getIntX() > currentPlatform.get(0).getIntX()) {
				outOfPlatforms.add(platforms.get(i));
			}
		}
		
		// If there's no platforms in front try behind ones. (glitch fix)
		if(outOfPlatforms.size()==0) {
			for(int i = 0; i < platforms.size(); i++) {
				outOfPlatforms.add(platforms.get(i));
			}
			outOfPlatforms.remove(currentPlatform);
		}
		
		// Select two closest platforms out of those platforms
		ArrayList<ArrayList<groundTile>> platformsToChooseFrom = new ArrayList<ArrayList<groundTile>>();
		for(int i = 0; i < 2 && 0 < outOfPlatforms.size(); i++) {
			ArrayList<groundTile> currPlatform = getClosestPlatformToFrom(currentPlatform, outOfPlatforms);
			platformsToChooseFrom.add(currPlatform);
			outOfPlatforms.remove(currPlatform);
		}
		
		// Get the platform up.
		ArrayList<groundTile> upPlatform = null;
		for(int i = 0; i < platformsToChooseFrom.size(); i++) {
			if(upPlatform==null) upPlatform = platformsToChooseFrom.get(i);
			else if(upPlatform.get(0).getIntY() > platformsToChooseFrom.get(i).get(0).getIntY()) upPlatform = platformsToChooseFrom.get(i);
		}
		
		// Get the platform down.
		ArrayList<groundTile> downPlatform = null;
		for(int i = 0; i < platformsToChooseFrom.size(); i++) {
			if(downPlatform==null) downPlatform = platformsToChooseFrom.get(i);
			else if(downPlatform.get(0).getIntY() < platformsToChooseFrom.get(i).get(0).getIntY()) downPlatform = platformsToChooseFrom.get(i);
		}
		
		// Jump to the one closest to the player platform (within the same distance)
		if(platformsToChooseFrom.size() != 0) {
			
			// If one is way further away, jump to the closer one.
			if(Math.sqrt(Math.pow(upPlatform.get(0).getIntY() - currentPlatform.get(0).getIntY(),2)
					+ Math.pow(upPlatform.get(0).getIntX() - currentPlatform.get(0).getIntX(),2))
					>  
					Math.sqrt(Math.pow(downPlatform.get(0).getIntY() - currentPlatform.get(0).getIntY(),2)
					+ Math.pow(downPlatform.get(0).getIntX() - currentPlatform.get(0).getIntX(),2)) + 10) {
				return downPlatform;
			}
			if(Math.sqrt(Math.pow(upPlatform.get(0).getIntY() - currentPlatform.get(0).getIntY(),2)
					+ Math.pow(upPlatform.get(0).getIntX() - currentPlatform.get(0).getIntX(),2) + 10)
					<
					Math.sqrt(Math.pow(downPlatform.get(0).getIntY() - currentPlatform.get(0).getIntY(),2)
					+ Math.pow(downPlatform.get(0).getIntX() - currentPlatform.get(0).getIntX(),2))) {
				return upPlatform;
			}
			
			// Otherwise jump to the one closer to the player.
			if(currentPlatform.get(0).getIntY() <= playerPlatform.get(0).getIntY()) return downPlatform;
			else return upPlatform;
		}
		else {
			return null;
		}
	}
	
	// Get closest platform in direction.
	public ArrayList<groundTile> getClosestPlatformInDirection(String direction) {
		
		ArrayList<groundTile> currentPlatform = getCurrentPlatform();
		ArrayList<groundTile> playerPlatform = getClosestPlatformToPlayer();
		
		// Exclude current platform and all platforms behind 
		ArrayList<ArrayList<groundTile>> outOfPlatforms = new ArrayList<ArrayList<groundTile>>();
		for(int i = 0; i < platforms.size(); i++) {
			if(direction.equals("Left") && platforms.get(i).get(0).getIntX() < currentPlatform.get(0).getIntX()) {
				outOfPlatforms.add(platforms.get(i));
			}
			if(direction.equals("Right") && platforms.get(i).get(0).getIntX() > currentPlatform.get(0).getIntX()) {
				outOfPlatforms.add(platforms.get(i));
			}
		}
		
		// If there's no platforms in front try behind ones. (glitch fix)
		if(outOfPlatforms.size()==0) {
			for(int i = 0; i < platforms.size(); i++) {
				outOfPlatforms.add(platforms.get(i));
			}
			outOfPlatforms.remove(currentPlatform);
		}
		
		// Select two closest platforms out of those platforms
		ArrayList<ArrayList<groundTile>> platformsToChooseFrom = new ArrayList<ArrayList<groundTile>>();
		for(int i = 0; i < 2 && 0 < outOfPlatforms.size(); i++) {
			ArrayList<groundTile> currPlatform = getClosestPlatformToFrom(currentPlatform, outOfPlatforms);
			platformsToChooseFrom.add(currPlatform);
			outOfPlatforms.remove(currPlatform);
		}
		
		// Get the platform up.
		ArrayList<groundTile> upPlatform = null;
		for(int i = 0; i < platformsToChooseFrom.size(); i++) {
			if(upPlatform==null) upPlatform = platformsToChooseFrom.get(i);
			else if(upPlatform.get(0).getIntY() > platformsToChooseFrom.get(i).get(0).getIntY()) upPlatform = platformsToChooseFrom.get(i);
		}
		
		// Get the platform down.
		ArrayList<groundTile> downPlatform = null;
		for(int i = 0; i < platformsToChooseFrom.size(); i++) {
			if(downPlatform==null) downPlatform = platformsToChooseFrom.get(i);
			else if(downPlatform.get(0).getIntY() < platformsToChooseFrom.get(i).get(0).getIntY()) downPlatform = platformsToChooseFrom.get(i);
		}
		
		// Jump to the one closest to the player platform (within the same distance)
		if(platformsToChooseFrom.size() != 0) {
			
			// If one is way further away, jump to the closer one.
			if(Math.sqrt(Math.pow(upPlatform.get(0).getIntY() - currentPlatform.get(0).getIntY(),2)
					+ Math.pow(upPlatform.get(0).getIntX() - currentPlatform.get(0).getIntX(),2))
					>  
					Math.sqrt(Math.pow(downPlatform.get(0).getIntY() - currentPlatform.get(0).getIntY(),2)
					+ Math.pow(downPlatform.get(0).getIntX() - currentPlatform.get(0).getIntX(),2)) + 10) {
				return downPlatform;
			}
			if(Math.sqrt(Math.pow(upPlatform.get(0).getIntY() - currentPlatform.get(0).getIntY(),2)
					+ Math.pow(upPlatform.get(0).getIntX() - currentPlatform.get(0).getIntX(),2) + 10)
					<
					Math.sqrt(Math.pow(downPlatform.get(0).getIntY() - currentPlatform.get(0).getIntY(),2)
					+ Math.pow(downPlatform.get(0).getIntX() - currentPlatform.get(0).getIntX(),2))) {
				return upPlatform;
			}
			
			// Otherwise jump to the one closer to the player.
			if(currentPlatform.get(0).getIntY() <= playerPlatform.get(0).getIntY()) return downPlatform;
			else return upPlatform;
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
	private ArrayList<Integer> lineAngles;
	
	// Spawn a shadow line at 
	public void spawnShadowLineAt(int angle, int x, int y) {
		
		// Add a new line.
		lineSpawnsX.add(x);
		lineSpawnsY.add(y);
		lineAngles.add(angle);
	}
	
	// Spawn every.
	private float spawnEvery = 0.1f;
	private long lastShadowSpawn = 0;
	
	// How many extra lines
	private int howManyExtraLines = 1;
	
	// Deal with lines
	public void dealWithShadowLines() {
		
		// Spawn units and move them.
		if(lineSpawnsX != null) {
			//if(lastShadowSpawn == 0) lastShadowSpawn = time.getTime();
			if(time.getTime() - lastShadowSpawn > spawnEvery*1000) {
				for(int i = 0; i < lineSpawnsX.size(); i++) {
					unit u = new shadowDude(lineSpawnsX.get(i), lineSpawnsY.get(i));
					u.setForceInFront(true);
					u.setMoveSpeed(shadowDudeMoveSpeed);
					u.setDestroyTimer(30);
					
					// Calculate the new X and Y we need to knock them to, based off radius.;
					int walkToX = (int) (getIntX() + (10000)*Math.cos(Math.toRadians(lineAngles.get(i)-90))); 
					int walkToY = (int) (getIntY() + (10000)*Math.sin(Math.toRadians(lineAngles.get(i)-90)));
					u.moveTo(walkToX, walkToY);
				}
				
				lastShadowSpawn = time.getTime();
			}
		}
	}
	
	// Spawn line at mouth.
	public void spawnLineAtMouth() {
		
		if(facingDirection.equals("Left")) {
			spawnShadowLineAt(0, getIntX()-15,getIntY());
			spawnShadowLineAt(180, getIntX()-15,getIntY());
			
			int degreeChange = 180/(howManyExtraLines+1);
			for(int i = 1; i <= howManyExtraLines; i++) {
				spawnShadowLineAt(180 + degreeChange*i, getIntX()-15,getIntY());
			}
			for(int i = 1; i <= howManyExtraLines; i++) {
				spawnShadowLineAt(degreeChange*i, getIntX()-15,getIntY());
			}
		}
		
		if(facingDirection.equals("Right")) {
			spawnShadowLineAt(0, getIntX()+getWidth()+15,getIntY());
			spawnShadowLineAt(180, getIntX()+getWidth()+15,getIntY());
			int degreeChange = 180/(howManyExtraLines+1);
			for(int i = 1; i <= howManyExtraLines; i++) {
				spawnShadowLineAt(degreeChange*i, getIntX()+getWidth()+15,getIntY());
			}
			for(int i = 1; i <= howManyExtraLines; i++) {
				spawnShadowLineAt(180 + degreeChange*i, getIntX()+getWidth()+15,getIntY());
			}
		}
		
	}
	
	// Stop spawning line at mouth.
	public void stopSpawningLineAtMouth() {
		lineAngles.clear();
		lineSpawnsX.clear();
		lineSpawnsY.clear();
	}
	
	// Howl stuff
	private boolean howling = false;
	private long startOfHowl = 0;
	public static String howl = "sounds/effects/bosses/shadowOfTheDenmother/spookyHowl.wav";
	private static String growl = "sounds/effects/bosses/shadowOfTheDenmother/spookyGrowl.wav";
	private static String bark = "sounds/effects/bosses/shadowOfTheDenmother/wolfBark.wav";
	private static String land = "sounds/effects/bosses/shadowOfTheDenmother/land.wav";
	public static String scream = "sounds/effects/bosses/shadowOfTheDenmother/scream.wav";
	public static String screamDeath = "sounds/effects/bosses/shadowOfTheDenmother/screamDead.wav";
	private static String bellToll = "sounds/effects/horror/bellToll.wav";
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
	
	// Sleeping
	public void animateSleeping() {
		if(sleeping) {
			animate("sleep" + getFacingDirection());
		}
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
			else {
				sound s = new sound(howl);
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
	private ArrayList<ArrayList<groundTile>> platformsPhase2;
	private ArrayList<ArrayList<groundTile>> insidePlatformsPhase2;
	
	// How often to cast claw ability.
	private float spawnClawPhase = 1.5f;
	
	// Spawn platforms
	public void spawnPlatforms() {
		
		// Spawn platforms.
		platforms = new ArrayList<ArrayList<groundTile>>();
		insidePlatforms = new ArrayList<ArrayList<groundTile>>();
		
		// Get farthest X left and X right
		int xLeft = Integer.MAX_VALUE;
		int xRight = Integer.MIN_VALUE;
		int floorY = Integer.MIN_VALUE;
		int roofY = Integer.MAX_VALUE;
		for(int i = 0; i < 6; i++) {
			for(int j = 0; j < 6; j++) {
				platforms.add(new ArrayList<groundTile>());
				
				if(i != 0 && i != 5 && j != 0 && j != 5) {
					insidePlatforms.add(new ArrayList<groundTile>());
				}
				
				for(int m = 0; m < 3; m++) {
					groundTile tile;
					if(j%2 != 0) {
						tile = new tombEdge(platformsStart.x + i*220 + m*32, platformsStart.y + j*105, 0);
					}
					else {
						tile = new tombEdge(platformsStart.x + i*220 + 110 + m*32, platformsStart.y + j*105, 0);
					}
					
					if(tile.getIntX() > xRight) xRight = tile.getIntX();
					if(tile.getIntX() < xLeft) xLeft = tile.getIntX();
					if(tile.getIntY() > floorY) floorY = tile.getIntY();
					if(tile.getIntY() < roofY) roofY = tile.getIntY();
					platforms.get(i*6 + j).add(tile);
					
					// Add to inside platforms.
					if(i != 0 && i != 5 && j != 0 && j != 5) {	
						insidePlatforms.get(insidePlatforms.size()-1).add(tile);
					}
					
				}
			}
		}
		
		// Spawn above platforms.
		platformsPhase2 = new ArrayList<ArrayList<groundTile>>();
		insidePlatformsPhase2 = new ArrayList<ArrayList<groundTile>>();
		
		for(int i = 0; i < 6; i++) {
			for(int j = 0; j < 24; j++) {
				platformsPhase2.add(new ArrayList<groundTile>());
				
				if(i != 0 && i != 5 && j != 0 && j != 23) {
					insidePlatformsPhase2.add(new ArrayList<groundTile>());
				}
				
				for(int m = 0; m < 3; m++) {
					groundTile tile;
					if(j%2 != 0) {
						tile = new tombEdge(platformsStart.x + i*220 + m*32, platformsStart.y - 640*4 + j*105, 0);
					}
					else {
						tile = new tombEdge(platformsStart.x + i*220 + 110 + m*32, platformsStart.y - 640*4 + j*105, 0);
					}
					
					platformsPhase2.get(i*6 + j).add(tile);
					
					// Add to inside platforms.
					if(i != 0 && i != 5 && j != 0 && j != 23) {	
						insidePlatformsPhase2.get(insidePlatformsPhase2.size()-1).add(tile);
					}
				}
			}
		}

		spawnShadowCage(xLeft, xRight, floorY, roofY);
		
		// Move wolfie and player to the middle.
		player.getPlayer().setDoubleX(platforms.get(6*3 + 3).get(1).getIntX());
		player.getPlayer().setDoubleY(platforms.get(6*3 + 3).get(1).getIntY()-player.getPlayer().getHeight());
		player.getPlayer().move(0, 0);
		setDoubleX(platforms.get(6*4 + 3).get(1).getIntX()-getWidth()/2 + platforms.get(4*5+3).get(1).getWidth()/2);
		setDoubleY(platforms.get(6*4 + 3).get(1).getIntY()-getHeight());
		faceTowardPlayer();
	}
	
	ArrayList<shadowDude> shadowCage;
	
	public void spawnShadowCage(int xLeft, int xRight, int floorY, int roofY) {
		
		shadowCage = new ArrayList<shadowDude>();
		
		int xChange = 6;
		
		// Spawn shadow floor
		for(int i = xLeft -200+xChange; i < xRight + 200; i+=shadowDude.getDefaultWidth()) {
			for(int j = 0; j < 7; j++) {
				shadowCage.add(new shadowDude(i, floorY+23+32+j*shadowDude.getDefaultHeight()){{setEyeless(true);}});
			}
		}
		
		// Spawn roof
		for(int i = xLeft -200+xChange; i < xRight + 200; i+=shadowDude.getDefaultWidth()) {
			for(int j = 0; j < 7; j++) {
				shadowCage.add(new shadowDude(i, roofY - 170 - j*shadowDude.getDefaultHeight()){{setEyeless(true);}});
			}
		}
		
		// Spawn left wall.
		for(int i = roofY - 470; i < floorY + 400; i+=shadowDude.getDefaultHeight()) {
			for(int j = 0; j < 17; j++) {
				shadowCage.add(new shadowDude(xLeft-shadowDude.getDefaultWidth()+xChange-20-j*shadowDude.getDefaultWidth(), i){{setEyeless(true);}});
			}
		}
		
		// Spawn right wall.
		for(int i = roofY - 470; i < floorY + 400; i+=shadowDude.getDefaultHeight()) {
			for(int j = 0; j < 17; j++) {
				shadowCage.add(new shadowDude(xRight+40+shadowDude.getDefaultWidth()+xChange-14+j*shadowDude.getDefaultWidth(), i){{setEyeless(true);}});
			}
		}
	}
	
	// Is injured by light dude?
	public boolean isInjured = false;
	
	@Override
	public void hurtPeople(int leniency) {
		// If someone is in the explosion radius, hurt.
			player currPlayer = player.getPlayer();
			if(currPlayer.isWithin(this.getIntX() + leniency, this.getIntY() + leniency, this.getIntX() + this.getWidth() - leniency, this.getIntY() + this.getHeight() - leniency) 
					&& ((!currPlayer.isIlluminated() && !isIlluminated() && !isInjured))) {
				currPlayer.hurt(1, 1);
			}
	}

	public void spawnClaw(int x, int y) {	
		int spawnX = x;
		int spawnY = y;
		currClaw = new clawMarkRed(spawnX,spawnY,0);
		currClaw.setForceInFront(true);
		faceTowardThing(currClaw);
	}
	
	// Current light dude.
	ArrayList<lightDude> currentLightDudes;
	
	public void changePhase() {
		
		// Change phase.
		if(numberOfHitsToDie==numberOfHitsToDieTotal) {
			spawnClawPhase = 0.6f;
			spawnEvery = 0.1f;
			shadowDudeMoveSpeed = 4f;
			pukeFor = 1f;
			shadowPuke = true;
			pukeEvery = 1;
			howManyExtraLines = 1;
		}
		else {
			spawnClawPhase -= .09f;
			spawnEvery -= 0.015f;
			shadowDudeMoveSpeed += 0.3f;
			pukeFor -= 0.15f;
			howManyExtraLines++;
		}
	}
	
	// Get hurt by light.
	public void getHurtByLight() {
		
		// Get hurt.
		numberOfHitsToDie--;
		isInjured = true;
		sound s = new sound(lightDudeBreak);
		s.start();
		changePhase();
		nextAbilityShadowPuke = true;
		
		// Kill boss.
		if(numberOfHitsToDie<=0) {
			s = new sound(screamDeath);
			s.start();
			music.currMusic.fadeOut(2f);
			//defeatBoss();
			fakeDeath();
		}
		else {
			s = new sound(scream);
			s.start();
		}
	}
	
	// Wait
	private long waitStart = 0;
	private float waitFor = 0;
	
	private void fakingDeathScene() {
		
		// Dead, wait.
		if(sequenceNumber==0) {
			waitStart = time.getTime();
			waitFor = 7f;
			sequenceNumber++;
		}
		
		// First bell toll.
		if(sequenceNumber==1 && time.getTime() - waitStart > waitFor*1000) {
			for(int i = 0; i < shadowCage.size(); i++) {
				shadowCage.get(i).setMoveSpeed(DEFAULT_SHADOWCAGE_MOVESPEED);
				shadowCage.get(i).eyeless = false;
				shadowCage.get(i).moveSpeed = 0.75f;
			}
			sound s = new sound(bellToll);
			s.start();
			sequenceNumber++;
			waitStart = time.getTime();
			waitFor = 3f;
		}
		
		// Second bell toll
		if(sequenceNumber==2 && time.getTime() - waitStart > waitFor*1000) {
			for(int i = 0; i < shadowCage.size(); i++) {
				shadowCage.get(i).movingUp = true;
			}
			sequenceNumber++;
			waitStart = time.getTime();
			waitFor = 3.2f;
		}
		
		// Third bell toll.
		if(sequenceNumber==3 && time.getTime() - waitStart > waitFor*1000) {
			sequenceNumber++;
			waitStart = time.getTime();
			waitFor = 2f;
			sleeping = false;
			startHowl("Respawn");
		}
		
		// After howl is over.
		if(sequenceNumber == 4 && !howling) {
			platforms.addAll(platformsPhase2);	
			phase = 2;
			fakingDeath = false;
			sequenceNumber = 0;
		}
	}
	
	private boolean fakingDeath = false;
	
	// Fake death
	public void fakeDeath() {
		sequenceNumber = 0;
		fakingDeath = true;
		sleep();
	}
	
	private boolean sleeping = false;
	
	public void sleep() {
		sleeping = true;
	}
	
	// Defeat boss
	public void defeatBoss() {
		stairsUp bossExit = new stairsUp(getCurrentPlatform().get(1).getIntX(),
				getCurrentPlatform().get(1).getIntY()-43,
				endZone.getZone(),
				-1529, 
				-3108,
				"Right");
		bossExit.setZ(-100);
		
		defeat();
		die();
	}
	
	// Deal with light dudes
	public void dealWithLightDudes() {
		
		if(insidePlatforms!=null && insidePlatforms.size()!=0 && numberOfHitsToDie > 0) {
			// Spawn a new one.
			if(currentLightDudes == null) currentLightDudes = new ArrayList<lightDude>();
			if(currentLightDudes != null && currentLightDudes.size() < 1) {
				
				ArrayList<ArrayList<groundTile>> chooseFrom = new ArrayList<ArrayList<groundTile>>(insidePlatforms);
				int random = utility.RNG.nextInt(chooseFrom.size());
				while(chooseFrom.get(random).size() > 1 && chooseFrom.get(random).get(1).isOnScreen()) {
					chooseFrom.remove(random);
					if(chooseFrom.size()==0) break;
					else random = utility.RNG.nextInt(chooseFrom.size());
				}
				currentLightDudes.add(new lightDude(chooseFrom.get(random).get(1).getIntX(), chooseFrom.get(random).get(1).getIntY() - lightDude.getDefaultHeight()));
				
			}
			
			// Get hurt.
			if(isIlluminated()) {
				
				// Destroy the nearest light dudes
				int closestVal = Integer.MAX_VALUE;
				lightDude closestDude = null;
				for(int i = 0; i < currentLightDudes.size(); i++) {
					int distanceBetween = (int) Math.sqrt(Math.pow(currentLightDudes.get(i).getIntX() - getIntX(),2) + 
							Math.pow(currentLightDudes.get(i).getIntY() - getIntY(),2));
					if(distanceBetween < closestVal) {
						closestVal = distanceBetween;
						closestDude = currentLightDudes.get(i);
					}
				}
				
				// Remove him.
				closestDude.destroy();
				currentLightDudes.remove(closestDude);
	
				getHurtByLight();
				
			}
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
			if(isJumping()) {
				
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
		if(fightInProgress && !fakingDeath) {
			castAbilities();
		}
		
		// Faking death scene.
		if(fakingDeath) {
			fakingDeathScene();
		}
	}
	
}