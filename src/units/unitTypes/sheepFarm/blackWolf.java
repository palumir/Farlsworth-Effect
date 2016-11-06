package units.unitTypes.sheepFarm;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

import doodads.sheepFarm.bone;
import doodads.sheepFarm.bush;
import doodads.sheepFarm.clawMarkRed;
import drawing.drawnObject;
import drawing.gameCanvas;
import drawing.spriteSheet;
import drawing.spriteSheet.spriteSheetInfo;
import drawing.animation.animation;
import drawing.animation.animationPack;
import interactions.interactBox;
import interactions.textSeries;
import sounds.sound;
import units.player;
import units.unit;
import units.unitType;
import utilities.intTuple;
import utilities.time;
import utilities.utility;

public class blackWolf extends wolf {
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default name.
	private static String DEFAULT_UNIT_NAME = "Black Wolf";
	
	// Health.
	private int DEFAULT_HP = 6;
	
	// Default jump speed
	private static int DEFAULT_UNIT_JUMPSPEED = 8;

	// Spawn claw stuff
	protected int DEFAULT_SLASH_DAMAGE = 1;
	
	// Beta stats
	private static float DEFAULT_MOVESPEED_BETA = 2f;
	private static float DEFAULT_CLAW_ATTACK_EVERY_BASE_BETA = 2.5f;
	private static float DEFAULT_SPAWN_CLAW_PHASE_TIME_BETA = 1f;
	private static int DEFAULT_FOLLOW_UNTIL_RANGE_BASE_BETA = 90;
	private static int DEFAULT_FOLLOW_UNTIL_RANGE_RANDOM_BETA = 15;
	
	////////////////
	/// FIELDS ///
	////////////////
	
	// Unit sprite stuff.
	private static spriteSheet DEFAULT_UPDOWN_SPRITESHEET = new spriteSheet(new spriteSheetInfo(
			"images/units/animals/blackWolfUpDown.png", 
			32, 
			64,
			0,
			0
			));
	private static spriteSheet DEFAULT_LEFTRIGHT_SPRITESHEET = new spriteSheet(new spriteSheetInfo(
			"images/units/animals/blackWolfLeftRight.png",
			64, 
			32,
			0,
			0
			));
	
	// Unit sprite stuff.
	private static spriteSheet DEFAULT_UPDOWN_SPRITESHEET_ANGRY = new spriteSheet(new spriteSheetInfo(
			"images/units/animals/blackWolfUpDownAlpha.png", 
			32, 
			64,
			0,
			0
			));
	private static spriteSheet DEFAULT_LEFTRIGHT_SPRITESHEET_ANGRY = new spriteSheet(new spriteSheetInfo(
			"images/units/animals/blackWolfLeftRightAlpha.png",
			64, 
			32,
			0,
			0
			));
	
	private interactBox interactSequence;
	
	// Create interact sequence
	public interactBox makeNormalInteractSequence() {
					
		// Start of conversation.
		textSeries startOfConversation = null;
		startOfConversation = new textSeries("STARTWITHBUTTONS", "STARTWITHBUTTONS");
		textSeries bellyRub = startOfConversation.addChild("Pet", "You pet the wolf. He is fast asleep.");
		textSeries shake = bellyRub.addChild("Give belly rub", "The wolf barks quietly in his sleep. He's dreaming.");
		textSeries walk = shake.addChild("Say 'want to go for a walk?'", "The wolf wakes up violently.");
		
		textSeries dontWake = startOfConversation.addChild("Walk away", "It's probably for the best.");
		bellyRub.addChild(dontWake);
		shake.addChild(dontWake);
		walk.setEnd();
		dontWake.setEnd();
			
		return new interactBox(startOfConversation, this);
	}
	
	// Interact with object. Should be over-ridden.
	public void interactWith() { 
		barked = false;
		interactSequence = makeNormalInteractSequence();
		interactSequence.setNotTalking(true);
		interactSequence.toggleDisplay();
	}
	
	// Barked
	boolean barked = false;
	
	public void doInteractStuff() {
		// DO IT.
		if(interactSequence !=null 
				&& interactSequence.getTextSeries().getButtonText() != null 
				&& interactSequence.getTextSeries().getButtonText().contains("want to go for")) {
			happy = false;
		}
		
		if(interactSequence !=null 
				&& interactSequence.getTextSeries().getButtonText() != null 
				&& interactSequence.getTextSeries().getButtonText().contains("rub")
				&& !barked) {
			barked = true;
		}
	}
	
	// The actual type.
	private static unitType unitTypeRef =
			new unitType(DEFAULT_UNIT_NAME,  // Name of unitType 
					     null,
					     null,
					     DEFAULT_TOPDOWN_WIDTH,
					     DEFAULT_TOPDOWN_WIDTH,
					     DEFAULT_MOVESPEED_BETA, // Movespeed
					     DEFAULT_UNIT_JUMPSPEED // Jump speed
						);	
	
	// Alpha type
	private static unitType alphaUnitTypeRef =
			new unitType(DEFAULT_UNIT_NAME,  // Name of unitType 
					     null,
					     null,
					     DEFAULT_TOPDOWN_WIDTH,
					     DEFAULT_TOPDOWN_WIDTH,
					     DEFAULT_MOVESPEED_BETA, // Movespeed
					     DEFAULT_UNIT_JUMPSPEED // Jump speed
						);	
	
	///////////////
	/// METHODS ///
	///////////////
	// Constructor
	public blackWolf(int newX, int newY) {
		super(unitTypeRef, newX, newY);
		
		// Set wolf combat stuff.
		setCombatStuff();
		collisionOn = true;
		changeCombat();
		setSaveFields(true);
		setCanSlash(true);
		
		if(happy) {
			startSleepLoop();
		}
	}
	
	// Set important stuff from save.
	@Override
	public void setImportantStuffFromSave() {
		happy = false;
		
		if(unit.getSavedUnits() != null) {
			for(int i = 0; i < unit.getSavedUnits().size(); i++) {
				HashMap<Object,Object> map = unit.getSavedUnits().get(i);
				
				// If we have found a blackwolf! BINGO!
				if(map.get("unitName").equals(blackWolf.class.getName())) {
					
					// Set fields if it's the right one.
					if(map.get("spawnedAtX").equals(getSpawnedAtX()) &&
					  map.get("spawnedAtY").equals(getSpawnedAtY())) {
						happy = (boolean)map.get("happy");
						
						if(happy) {
							setDoubleX((int)map.get("x"));
							setDoubleY((int)map.get("y"));
						}
						break;
					}
				}
			}
		}
	}
	
	// Add animations.
	public void addAngryAnimations() {
		
		// Deal with animations
		animationPack unitTypeAnimations = new animationPack();
		
		// Jumping left animation.
		animation jumpingLeft = new animation("jumpingLeft", DEFAULT_LEFTRIGHT_SPRITESHEET_ANGRY.getAnimation(6), 4, 4, 1){{
			setRepeats(true);
		}};
		unitTypeAnimations.addAnimation(jumpingLeft);
		
		// Jumping down animation.
		animation jumpingDown = new animation("jumpingDown", DEFAULT_UPDOWN_SPRITESHEET_ANGRY.getAnimation(3), 2, 2, 1){{
			setRepeats(true);
		}};
		unitTypeAnimations.addAnimation(jumpingDown);
		
		// Jumping up animation.
		animation jumpingUp = new animation("jumpingUp", DEFAULT_UPDOWN_SPRITESHEET_ANGRY.getAnimation(3), 7, 7, 1){{
			setRepeats(true);
		}};
		unitTypeAnimations.addAnimation(jumpingUp);
		
		// Jumping right animation.
		animation jumpingRight = new animation("jumpingRight", DEFAULT_LEFTRIGHT_SPRITESHEET_ANGRY.getAnimation(2), 4, 4, 1){{
			setRepeats(true);
		}};
		unitTypeAnimations.addAnimation(jumpingRight);
		
		// Standing left animation.
		animation standingLeft = new animation("standingLeft", DEFAULT_LEFTRIGHT_SPRITESHEET_ANGRY.getAnimation(5), 0, 0, 1){{
			setRepeats(true);
		}};
		unitTypeAnimations.addAnimation(standingLeft);
		
		// Standing right animation.
		animation standingRight = new animation("standingRight", DEFAULT_LEFTRIGHT_SPRITESHEET_ANGRY.getAnimation(3), 0, 0, 1){{
			setRepeats(true);
		}};
		unitTypeAnimations.addAnimation(standingRight);
		
		// Running left animation.
		animation runningLeft = new animation("runningLeft", DEFAULT_LEFTRIGHT_SPRITESHEET_ANGRY.getAnimation(5), 0, 4, 1f){{
			setRepeats(true);
		}};
		unitTypeAnimations.addAnimation(runningLeft);		
		
		// Running right animation.
		animation runningRight = new animation("runningRight", DEFAULT_LEFTRIGHT_SPRITESHEET_ANGRY.getAnimation(1), 0, 4, 1f){{
			setRepeats(true);
		}};
		unitTypeAnimations.addAnimation(runningRight);
		
		// Standing up animation.
		animation standingUp = new animation("standingUp", DEFAULT_UPDOWN_SPRITESHEET_ANGRY.getAnimation(4), 5, 5, 1){{
			setRepeats(true);
		}};
		unitTypeAnimations.addAnimation(standingUp);
		
		// Standing down animation.
		animation standingDown = new animation("standingDown", DEFAULT_UPDOWN_SPRITESHEET_ANGRY.getAnimation(0), 0, 0, 1){{
			setRepeats(true);
		}};
		unitTypeAnimations.addAnimation(standingDown);
		
		// Running up animation.
		animation runningUp = new animation("runningUp", DEFAULT_UPDOWN_SPRITESHEET_ANGRY.getAnimation(2), 5, 8, 1f){{
			setRepeats(true);
		}};
		unitTypeAnimations.addAnimation(runningUp);
		
		// Running down animation.
		animation runningDown = new animation("runningDown", DEFAULT_UPDOWN_SPRITESHEET_ANGRY.getAnimation(2), 0, 3, 1f){{
			setRepeats(true);
		}};
		unitTypeAnimations.addAnimation(runningDown);
		
		// Set animations.
		setAnimations(unitTypeAnimations);
	}

	// Combat defaults.
	@Override
	public void setCombatStuff() {
		
		// Set to be attackable.
		this.setKillable(true);
		setHealthPoints(DEFAULT_HP);
	}
		
	// Remove claw.
	@Override
	public void reactToDeath() {
		if(currClaw != null) currClaw.destroy();
	}
	
	// Trail stuff.
	private float trailInterval = 0.0125f;
	private long lastTrail = 0;
	private int trailLength = 10;
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
					trailImage.add(getAnimations().getAnimation("trail" + getFacingDirection()).getSprites().get(0));
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
					g2d.setComposite(AlphaComposite.SrcOver.derive(alpha));
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
						g2d.setComposite(AlphaComposite.SrcOver.derive(alpha));
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

	@Override
	public void spawnTrail() {
	}
	
	@Override
	// Is on screen?
	public boolean isOnScreen() {
		// Get the correct sprite width and height.
		int spriteWidth = 0;
		int spriteHeight = 0;
		if(this instanceof unit && ((unit)this).getCurrentAnimation() != null) {
			spriteWidth = ((unit)this).getCurrentAnimation().getCurrentFrame().getWidth();
			spriteHeight = ((unit)this).getCurrentAnimation().getCurrentFrame().getHeight();
		}
		if(trail!=null) {
			for(int i = 0; i < trail.size(); i++) {
				int trailDrawX = drawnObject.calculateDrawX(this, trail.get(i).x);
				int trailDrawY = drawnObject.calculateDrawY(this, trail.get(i).y);
				if(trailDrawX + gameCanvas.getScaleX()*spriteWidth > 0 && 
						trailDrawY + gameCanvas.getScaleY()*spriteHeight > 0 && 
						   trailDrawX < gameCanvas.getActualWidth() && 
						   trailDrawY < gameCanvas.getActualHeight()) return true;
			}
		}
		return (this.getDrawX() + gameCanvas.getScaleX()*spriteWidth > 0 && 
				   this.getDrawY() + gameCanvas.getScaleY()*spriteHeight > 0 && 
				   this.getDrawX() < gameCanvas.getActualWidth() && 
				   this.getDrawY() < gameCanvas.getActualHeight());
	}

	@Override
	public void jumpingFinished() {
	}
	
	@Override
	public void respondToDestroy() {
	}
	
	// Default bone radius
	public int BONE_RADIUS = 20;
	
	// Near bone
	public bone nearBone() {
		if(bone.bones != null) {
			for(int i = 0; i < bone.bones.size(); i++) {
				bone currentBone = bone.bones.get(i);
				if(currentBone.isDrawObject() && isWithinRadius(currentBone.getIntX() + currentBone.getWidth()/2,
								  currentBone.getIntY() + currentBone.getHeight()/2,
								  BONE_RADIUS)) {
					return currentBone;
				}
			}
		}
		return null;
	}
	
	// Happy?
	private boolean happy;
	
	// Happy dog
	public void happyDog() {
		setHappy(true);
		if(followingUnit) {
			sound s = new sound(bush.eating);
			s.setPosition(getIntX(), getIntY(), sound.DEFAULT_SOUND_RADIUS);
			s.start();
			addAnimations();
			unfollow();
		}
		killsPlayer = false;
		if(getFacingDirection().equals("Up")) setFacingDirection("Left");
		if(getFacingDirection().equals("Down")) setFacingDirection("Right");
		setInteractable(true);
		startSleepAnimation();
	}
	
	// Start sleep loop
	public void startSleepLoop() {
		if(getFacingDirection().equals("Up")) setFacingDirection("Left");
		if(getFacingDirection().equals("Down")) setFacingDirection("Right");
		sleepStart = time.getTime() - getAnimations().getAnimation("sleepingStart" + getFacingDirection()).getTimeToComplete()*1000;
		sleepPlaying = true;
		animate("sleepingLoop" + getFacingDirection());
	}
	
	// Sleep animation playing?
	private boolean sleepPlaying = false;
	
	// Time
	private double sleepStart = time.getTime();
	
	public void startSleepAnimation() {
		if(getCurrentAnimation()!=null && !getCurrentAnimation().getName().contains("sleeping")) {
			sleepPlaying = true;
			sleepStart = time.getTime();
			animate("sleepingStart" + getFacingDirection());
		}
	}
	
	// Defaults
	public int DEFAULT_AGGRO_RADIUS = 180;
	
	// Consume bone
	public void consumeBone(bone b) {
		happyDog();
		b.setDrawObject(false);
	}
	
	// Animate sleeping.
	public void animateSleeping() {
		
		// Sleeping start.
		if(getCurrentAnimation().getName().contains("sleepingStart")) {
			if(time.getTime() - sleepStart > getCurrentAnimation().getTimeToComplete()*1000) {
				animate("sleepingLoop" + getFacingDirection());
			}
		}
		else {
			animate("sleepingLoop" + getFacingDirection());
		}
	}
	
	// Deal with animations.
	@Override
	public void dealWithAnimations(int moveX, int moveY) {
			
		// No hitboxadjustment.
		setHitBoxAdjustmentY(DEFAULT_PLATFORMER_ADJUSTMENT_Y);
		setHitBoxAdjustmentX(0);
		if(sleepPlaying && happy) {
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
	
	// Follow player
	public void followPlayer() {
		
		// If there's a boner get happy.
		// Bone!
		bone b = nearBone();
		if(b != null) {
			consumeBone(b);
		}
		
		// If unhappy
		if(!isHappy()) {
			// Follow the player if we are within the radius.
			if(!player.isDeveloper() 
					&& !player.getPlayer().isUnitIsDead()
					&& player.getPlayer().isWithinRadius(this.getIntX() + this.getWidth()/2, this.getIntY() + this.getHeight()/2, DEFAULT_AGGRO_RADIUS)) {
				if(!followingUnit) {
					sound s = new sound(growl);
					s.setPosition(getIntX(), getIntY(), sound.DEFAULT_SOUND_RADIUS);
					s.start();
					addAngryAnimations();
				}
				killsPlayer = true;
				this.follow(player.getPlayer());
			}
			if(player.getPlayer().isUnitIsDead()) {
				if(followingUnit) addAnimations();
				this.unfollow();
			}
			/*else {
				if(followingUnit) addAnimations();
				this.unfollow();
			}*/
		}
	}
	
	// Update unit
	@Override
	public void updateUnit() {
		doInteractStuff();
		followPlayer();
	}

	@Override
	public void spawnClaw(int x, int y) {	
		int spawnX = x;
		int spawnY = y;
		currClaw = new clawMarkRed(spawnX,spawnY,0);
		faceTowardThing(currClaw);
	}
	
	@Override
	public void changeCombat() {
		
		// Beta wolf
		if(!isAlpha()) {
			clawAttackEveryBase = DEFAULT_CLAW_ATTACK_EVERY_BASE_BETA;
			setSpawnClawPhaseTime(DEFAULT_SPAWN_CLAW_PHASE_TIME_BETA);
			followUntilRange = DEFAULT_FOLLOW_UNTIL_RANGE_BASE_BETA + utility.RNG.nextInt(DEFAULT_FOLLOW_UNTIL_RANGE_RANDOM_BETA);
			setMoveSpeed(DEFAULT_MOVESPEED_BETA);
		}

		// Alpha
		else {
		}
	}

	// Get updown spritesheet
	@Override
	public spriteSheet getUpDownSpriteSheet() {
		return DEFAULT_UPDOWN_SPRITESHEET;
	}
	
	// Get leftRight spritesheet
	@Override
	public spriteSheet getLeftRightSpriteSheet() {
		return DEFAULT_LEFTRIGHT_SPRITESHEET;
	}
	
	// Set animations for alpha
	@Override
	public void setAlphaAnimations() {
		addAnimations();

	}

	@Override
	public void chargeUnits() {
		// TODO Auto-generated method stub
		
	}

	public boolean isHappy() {
		return happy;
	}

	public void setHappy(boolean happy) {
		this.happy = happy;
	}
}
