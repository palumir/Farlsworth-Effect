package units.bosses;

import java.util.ArrayList;

import doodads.sheepFarm.clawMarkRed;
import drawing.spriteSheet;
import drawing.animation.animation;
import drawing.animation.animationPack;
import drawing.spriteSheet.spriteSheetInfo;
import modes.mode;
import sounds.sound;
import terrain.chunk;
import units.boss;
import units.player;
import units.unitType;
import units.unitCommands.commands.slashCommand;
import units.unitTypes.sheepFarm.wolf;
import utilities.intTuple;
import utilities.time;
import utilities.utility;

public class shadowOfTheDenmother extends boss {
	
	// Default name.
	private static String DEFAULT_UNIT_NAME = "Shadow of the Denmother";
	
	// Default jump speed
	private static int DEFAULT_UNIT_JUMPSPEED = 8;
	
	// Beta stats
	private static float DEFAULT_MOVESPEED_BETA = 2f;

	// Unit sprite stuff.
	private static spriteSheet DEFAULT_UPDOWN_SPRITESHEET = new spriteSheet(new spriteSheetInfo(
			"images/units/animals/blackWolfUpDownAlpha.png", 
			32, 
			64,
			0,
			0
			));
	private static spriteSheet DEFAULT_LEFTRIGHT_SPRITESHEET = new spriteSheet(new spriteSheetInfo(
			"images/units/animals/blackWolfLeftRightAlpha.png",
			64, 
			32,
			0,
			0
			));
	
	// The actual type.
	private static unitType unitTypeRef =
			new unitType(DEFAULT_UNIT_NAME,  // Name of unitType 
					     null,
					     null,
					     25,
					     25,
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
	
	public shadowOfTheDenmother(int newX, int newY) {
		super(unitTypeRef, DEFAULT_UNIT_NAME, newX, newY);
		
		killsPlayer = true;
		
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
		
		// Set animations.
		setAnimations(unitTypeAnimations);
	
	}
	
	// Deal with animations.
	@Override
	public void dealWithAnimations(int moveX, int moveY) {
			
		// No hitboxadjustment.
		setHitBoxAdjustmentY(0);
		setHitBoxAdjustmentX(0);
		if(jumping || clawAttacking) {
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
				
				// Charge units to position.
				//chargeUnits();
				
				// Spawn rocks
				//spawnTrail();
				
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
	
	// List of spots
	private static ArrayList<String> abilityList = new ArrayList<String>() {{
		add("shadowBombardmentTopLeft");
		add("shadowBombardmentTopRight");
	}};
	
	// Start fight
	public void startFight() {
		fightInProgress = true;
	}
	
	// Randomly select an ability
	public void randomlySelectAnAbility() {
		// Select a random ability to cast.
		if(recentlyCastAbilities == null) recentlyCastAbilities = new ArrayList<String>();
	
		String randomlySelectedAbility = "";
		
		// First ability.
		if(recentlyCastAbilities.size() == 0) randomlySelectedAbility = abilityList.get(utility.RNG.nextInt(abilityList.size()));
		else {
			while(!randomlySelectedAbility.equals(recentlyCastAbilities.get(recentlyCastAbilities.size() - 1))) {
				abilityList.get(utility.RNG.nextInt(abilityList.size()));
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
		
		// Cast abilities.
		if(currentAbility.equals("shadowBombardmentTopLeft")) {
			castShadowBombardment("topLeft");
		}
		
	}
	
	// Cast shadow bombardment
	public void castShadowBombardment(String where) {
		
		// Top left bombardment.
			
			// Slash.
			if(sequenceNumber == 0) {
				if(where.equals("topLeft")) slashTo(13059,338);
				if(where.equals("topRight")) slashTo(13388,338);
				sequenceNumber++;
			}
			
			// Land and face the player.
			if(sequenceNumber == 1 && !clawAttacking) {
				faceTowardPlayer();
				sequenceNumber++;
			}
			
			// Howl.
			if(sequenceNumber == 2) {
				
			}
		
	}
	
	// List of spots
	private static ArrayList<intTuple> clawSpotsPlatformer = new ArrayList<intTuple>() {{
		add(new intTuple(13223,233));
		add(new intTuple(13059,338));
		add(new intTuple(13388,338));
		add(new intTuple(13225,430));
		add(new intTuple(13061,521));
		add(new intTuple(13387,521));
		add(new intTuple(13224,612));
	}};
	
	// How often to cast claw ability.
	private float clawToSpotAbilityEvery = 2f;
	private double lastClawToSpotAbility = 0;
	private static float spawnClawPhase = 2f;

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
		
		// Only do fight things if the fight is in progress.
		if(fightInProgress) {
			castAbilities();
		}
	}
	
}