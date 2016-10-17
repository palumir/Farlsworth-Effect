package units;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import UI.playerActionBar;
import UI.menus.deathMenu;
import doodads.general.well;
import doodads.sheepFarm.clawMarkRed;
import drawing.camera;
import drawing.drawnObject;
import drawing.animation.animation;
import drawing.animation.animationPack;
import effects.effect;
import effects.effectTypes.critBloodSquirt;
import effects.effectTypes.items.pushBottleSplash;
import effects.effectTypes.items.savePoint;
import effects.interfaceEffects.interactBlurb;
import effects.interfaceEffects.tooltipString;
import interactions.interactBox;
import items.bottle;
import items.inventory;
import main.main;
import modes.mode;
import modes.platformer;
import modes.topDown;
import sounds.sound;
import terrain.chunk;
import terrain.atmosphericEffects.storm;
import units.developer.developer;
import units.unitCommands.commands.slashCommand;
import utilities.saveState;
import utilities.time;
import zones.zone;

public class player extends unit {
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default attack sound
	private String DEFAULT_ATTACK_SOUND = "sounds/effects/combat/punch.wav";
	
	// Default dimensions.
	private static int DEFAULT_PLATFORMER_HEIGHT = 46;
	private static int DEFAULT_PLATFORMER_WIDTH = humanType.DEFAULT_UNIT_WIDTH;
	private static int DEFAULT_TOPDOWN_HEIGHT = 20;
	private static int DEFAULT_TOPDOWN_WIDTH = humanType.DEFAULT_UNIT_WIDTH;
	
	// Platformer and topDown default adjustment
	private static int DEFAULT_PLATFORMER_ADJUSTMENT_Y = 6;
	private static int DEFAULT_TOPDOWN_ADJUSTMENT_Y = 18;
	
	// Default name.
	private static String DEFAULT_PLAYER_NAME = "IanRetard";
	
	// Default gender.
	public static String DEFAULT_PLAYER_GENDER = "female";
	
	// Default zone.
	private static zone DEFAULT_ZONE = null;
	
	// Default movespeed. 
	private static float DEFAULT_PLAYER_MOVESPEED = 3.5f;
	// 3.5 is default
	
	// Default HP
	private static int DEFAULT_PLAYER_HP = 1;
	
	// Player sprite stuff.
	private static String DEFAULT_PLAYER_SPRITESHEET = "images/units/player/" + DEFAULT_PLAYER_GENDER + "/noItems.png";
	
	// Default interact range.
	private static int DEFAULT_INTERACT_RANGE = 30;
	private static int DEFAULT_INTERACT_WIDTH = 20;
	
	///////////////
	/// GLOBALS ///
	///////////////
	
	// CAN the player become a developer?
	private static boolean hasDeveloperPowers = true;
	
	// Is this player currently a developer?
	private static boolean isDeveloper = false;
	
	// Player is loaded
	public static boolean playerLoaded = false;
	
	// Main player global.
	private static player currentPlayer = null;
	
	// Player unitType.
	public static unitType
	playerType = 
	new humanType(
				"player",  // Name of unitType 
				 DEFAULT_PLAYER_SPRITESHEET,
			     humanType.DEFAULT_HUMAN_MOVESPEED, // Movespeed
			     (int) unit.DEFAULT_JUMPSPEED // Jump speed
				);	   
	
	
	// Tutorial stuff
	private static tooltipString useWASD;
	
	////////////////
	//// FIELDS ////
	////////////////
	
	// Player specific fields.
	private String playerName = DEFAULT_PLAYER_NAME;
	private zone currentZone = DEFAULT_ZONE;
	
	// Choices
	public int chaosChoices = 0;
	public int orderChoices = 0;
	
	// Savestate we loaded from
	public saveState playerSaveState;
	
	// Last well coordinates
	public Point lastWell;
	
	// Last save bottle coordinates
	public ArrayList<Point> lastSaveBottles;
	public savePoint lastSaveBottleChargeIndicator;
	
	// Movement disabled?
	public boolean movementDisabled = false;
	
	// Player inventory
	private inventory playerInventory;
	
	// Combat
	private bottle equippedBottle = null;
	
	// Player interface
	private playerActionBar actionBar;

	///////////////
	/// METHODS ///
	///////////////

	// Constructor
	public player(int newX, int newY, zone z) {
		super(playerType, newX, newY);
		
		// Set movespeed.
		setMoveSpeed(DEFAULT_PLAYER_MOVESPEED);
		oldMoveSpeed=DEFAULT_PLAYER_MOVESPEED;
		setAllowSlowMovement(true);
		
		// Set-up the camera.
		camera c = new camera(this, 1);
		camera.setCurrent(c);
		c.setAttachedUnit(this);
		attachedCamera = c;
		
		// Set this player to be the main player.
		this.makeCurrentPlayer();
		
		// Has the player never played before? Put them in the start zone.
		if(z == null && getCurrentZone() == DEFAULT_ZONE) {
			currentZone = zone.getStartZone();
		}
		
		// Load the zone, even if it's the default.
		if(z != null) {
			currentZone = z;
		}

		// Combat.
		setKillable(true);
		
		// Define HP
		setHealthPoints(DEFAULT_PLAYER_HP);
		
		// Set dimensions
		setHeight(getDefaultHeight());
		setWidth(getDefaultWidth());
		platformerHeight = DEFAULT_PLATFORMER_HEIGHT;
		platformerWidth = DEFAULT_PLATFORMER_WIDTH;
		topDownHeight = DEFAULT_TOPDOWN_HEIGHT;
		topDownWidth = DEFAULT_TOPDOWN_WIDTH;
		setHitBoxAdjustmentY(getDefaultHitBoxAdjustmentY());
		
		// Set player animations
		setAnimations();
		
		// Set z.
		setZ(1);
	}
	
	// Set animations
	public void setAnimations() {

	}
	
	// React to pain.
	public void reactToPain() {
	}

	// Player AI controls the interface
	public void updateUnit() {
		//System.out.println(isTouchingGround());
		//if(lastWell!=null)System.out.println(lastWell.getX() + " " + lastWell.getY());
		dealWithPushBottling();
		showPossibleInteractions();
		isPlayerDead();
	}
	
	// Deal with player being alive or dead.
	@Override
	public void aliveOrDead() {
		// Kill the player if they've left the map.
		if(hasLeftMap()) {
			if(isKillable()) killPlayer();
		}
		
		// Dead
		if(getHealthPoints() <= 0) {
			killPlayer();
		}
	}
	
	// Kill player.
	public void killPlayer() {
		
		if(!isUnitIsDead()) {
			
			// Tell the player
			tooltipString t = new tooltipString("You died.");
			
			// Close current box.
			if(interactBox.getCurrentDisplay()!=null) {
				interactBox.getCurrentDisplay().setDisplayOn(false);
				interactBox.getCurrentDisplay().destroy();
				interactBox.setCurrentDisplay(null);
			}
			
			// Tell the player death timer to start.
			unitDiedAt = time.getTime();
			setUnitIsDead(true);
			
			// Destroy player.
			drawnObject.objects.remove(this);
			
			// Make the player invinsible.
			setTargetable(false);
			setKillable(false);
			
			// Play blood squirt.
			effect blood = new critBloodSquirt(getIntX() - critBloodSquirt.getDefaultWidth()/2 + getDefaultWidth()/2,
					   getIntY() - getHitBoxAdjustmentY() + critBloodSquirt.getDefaultHeight()/2 - critBloodSquirt.getDefaultHeight()/2);
		}
	}

	// Kill player finally.
	public void killPlayerFinally() {
		
		// Create buttons to respawn at well or bottle.
		new deathMenu();
	}
	
	// Is player dead
	public void isPlayerDead() {
		if(isUnitIsDead() && time.getTime() - unitDiedAt >= deathAnimationLasts*1000) {
			killPlayerFinally();
		}
	}
	
	// Load player.
	public static player loadPlayer(player alreadyPlayer, zone z, int spawnX, int spawnY, String direction, String whereToSpawn) {
		
		// Initiate all.
		saveState s = main.initiateAll();
		
		// The player, depending on whether or not we have a save file.
		player thePlayer = loadPlayerSaveData(alreadyPlayer, s, z, spawnX, spawnY, direction, whereToSpawn);
		
		// Load the player into the zone.
		thePlayer.currentZone.loadZone();
		
		return thePlayer;
	}
	
	public static player loadPlayerSaveData(player alreadyPlayer, saveState s, zone z, int spawnX, int spawnY, String direction, String whereToSpawn) {

		// If we are in development mode and testing, do that stuff.
		if(hasDeveloperPowers) {
			developer.doTestStuff();
		}
		
		// Get player data from saveState.
		// The player, depending on whether or not we have a save file.
		player thePlayer;
		
		// If the game just started, check what zone they're in using their savefile.
		// If we don't have a savefile, load them into the starting zone.
		
		// The fields we will try to load.
		zone loadZone = null;
		int playerX = 0;
		int playerY = 0;
		String newFacingDirection = null;
		inventory loadedInventory = null; // empty inventory
		
		// If no zone is given and we don't have the save file. First time running game. 
		if(z == null && s==null && alreadyPlayer == null) {
			loadZone = zone.getStartZone();
			playerX = loadZone.getDefaultLocation().x;
			playerY = loadZone.getDefaultLocation().y;
			newFacingDirection = "Right";
			useWASD = new tooltipString("Use 'wasd' to move.");
			useWASD.setHasATimer(false);
		}
		
		// If we have the savestate.
		if(s != null && alreadyPlayer == null) {
			loadZone = zone.getZoneByName(s.getZoneName());
			playerX = s.getPlayerX();
			playerY = s.getPlayerY();
			newFacingDirection = s.getFacingDirection();
			loadedInventory = s.getPlayerInventory();
		}
		
		// If the zone, z, is given, we should have all of these details.
		if(z != null) {
			loadZone = z;
			playerX = spawnX;
			playerY = spawnY;
			newFacingDirection = direction;
			
			// If we are given the player, copy new inventory, etc.
			if(alreadyPlayer != null) {
				
				// Draw the carried over inventory.
				loadedInventory = alreadyPlayer.getPlayerInventory();
				drawnObject.objects.add(loadedInventory);
			}
		}
		
		// Preload the zone's mode.
		if(loadZone != null) {
			mode.setCurrentMode(loadZone.getMode());
		}
		
		// Create the player. If we are a developer, give developer functions.
		if(!isDeveloper()) {
			thePlayer = new player(playerX, playerY, loadZone);
			thePlayer.actionBar = new playerActionBar(5,5);
		}
		else thePlayer = new developer(playerX, playerY, loadZone);
		
		// If inventory isn't loaded make a new one.
		if(loadedInventory == null) loadedInventory = new inventory();
		
		// Set our fields
		thePlayer.setFacingDirection(newFacingDirection);
		thePlayer.setPlayerInventory(loadedInventory);
		
		// Deal with an error from loading between dev and test.
		if(!objects.contains(loadedInventory)) objects.add(loadedInventory);
		
		// If there's a savestate.
		if(s != null) {
			thePlayer.chaosChoices = s.getChaosChoices();
			thePlayer.orderChoices = s.getOrderChoices();
			
			thePlayer.lastWell = s.lastWell;
			
			thePlayer.lastSaveBottles = s.lastSaveBottles;
			
			// Create an indicator
			if(s.lastSaveBottles != null && s.lastSaveBottles.size() > 0) {
				savePoint.createSavePoint(thePlayer);
			}
			
			// If we are trying to respawn at a well.
			if(whereToSpawn.equals("respawnAtWell")) {
				if(thePlayer.lastWell == null) {
				}
				
				// Nice, there's a well.
				else {
					well.refreshPlayer("respawnAtWell");
					thePlayer.setDoubleX(thePlayer.lastWell.getX());
					thePlayer.setDoubleY(thePlayer.lastWell.getY());
				}
				saveState.setQuiet(true);
				saveState.createSaveState();
				saveState.setQuiet(false);
			}
		}
		
		// Set that we have loaded the player once.
		playerLoaded = true;
		
		// Set the player saveState so it can be used in zone loading.
		thePlayer.playerSaveState = s;
		
		return thePlayer;
	}
	
	// Make the player the main player.
	public void makeCurrentPlayer() {
		setCurrentPlayer(this);
	}
	
	// Make the current player stop doing things
	public void stop() {
		stopMove("all");
		stopJump();
	}
	
	// Responding to mouse presses
	public void mousePressed(MouseEvent e) {
		
		// Developer stuff
		if(player.isDeveloper()) developer.devMousePressed(e);
	}
	
	// Responding to mouse release
	public void mouseReleased(MouseEvent e) {
		
		// Developer stuff
		if(player.isDeveloper()) developer.devMouseReleased(e);
	}
	
	// Responding to key presses.
	public void keyPressed(KeyEvent k) {
	
		// Developer
		if(isDeveloper()) {
			((developer)(this)).devKeyPressed(k);
		}
		
		else {
			// Respond to dialogue/interact presses.
			if(interactBox.getCurrentDisplay() != null) {
				interactBox.getCurrentDisplay().respondToKeyPress(k);
			}
			// Player presses y key.
			else if(k.getKeyCode() == KeyEvent.VK_Y && hasDeveloperPowers) { 
				developer.toggleTestMode();
			}
			
			else if(isUnitIsDead()) { 
				// Player presses bar key
				if(k.getKeyCode() == KeyEvent.VK_ENTER || k.getKeyCode() == KeyEvent.VK_SPACE || k.getKeyCode() == KeyEvent.VK_E)  {
					if(deathMenu.menu != null && objects.contains(deathMenu.menu.respawnAtSaveBottle)) deathMenu.menu.selectButton(deathMenu.menu.respawnAtSaveBottle);
				}
			}
			else if(zone.getCurrentZone()!=null && zone.getCurrentZone().isZoneLoaded() && playerLoaded) {
				
				// Player presses i (inventory) key.
				if(k.getKeyCode() == KeyEvent.VK_I) { 
					playerInventory.toggleDisplay();
				}
				
				// Respond to inventory presses.
				else if(playerInventory.isDisplayOn()) {
					playerInventory.respondToKeyPress(k);
				}
				
				// Respond to active item presses.
				else if(playerInventory != null && 
						playerInventory.activeSlots != null && 
						inventory.activeSlotKeys.contains(k.getKeyCode())) {
					
					// Use the item for the key.
					for(int i = 0; i < playerInventory.activeSlots.size(); i++) {
						if(playerInventory.activeSlots.get(i).slot == k.getKeyCode()) {
							playerInventory.activeSlots.get(i).use();
							break;
						}
					}
					
				}
				
				// Respond to other presses (movement)
				else {
					// Shield on.
					if(k.getKeyCode() == KeyEvent.VK_P) {
						sound s = new sound(storm.rainSound);
						s.fadeIn(2);
						s.start();
					}
			
				// Player presses left key.
				if(k.getKeyCode() == KeyEvent.VK_A) { 
					startMove("left");
					if(useWASD!=null && !useWASD.fadingOut) useWASD.fadeOut();
				}
				
				// Player presses right key.
				if(k.getKeyCode() == KeyEvent.VK_D) { 
					startMove("right");
					if(useWASD!=null && !useWASD.fadingOut) useWASD.fadeOut();
				}
				
				// Player presses up key, presumably to jump!
				if(k.getKeyCode() == KeyEvent.VK_W) { 
					if(mode.getCurrentMode() == platformer.name) {
						startMove("up");
						startJump();
					}
					else if(mode.getCurrentMode() == topDown.name) {
						startMove("up");
						if(useWASD!=null && !useWASD.fadingOut) useWASD.fadeOut();
					}
				}
				
				// Player presses down key
				if(k.getKeyCode() == KeyEvent.VK_S) { 
					if(mode.getCurrentMode() == platformer.name) {
						startMove("down");
					}
					else if(mode.getCurrentMode() == topDown.name) {
						startMove("down");
						if(useWASD!=null && !useWASD.fadingOut) useWASD.fadeOut();
					}
				}
					
					// Player presses e key
					if(k.getKeyCode() == KeyEvent.VK_E) {
						interact();
					}
				}
			}
		}
	}
	
	// Responding to key releases.
	public void keyReleased(KeyEvent k) {
		
		// Respond to dialogue/interact presses.
		if(interactBox.getCurrentDisplay() != null) {
			interactBox.getCurrentDisplay().respondToKeyRelease(k);
		}
		
		
		// Shield off
		if(k.getKeyCode() == KeyEvent.VK_SHIFT) {
			//shield(false);
		}
		
		
		// Player releases
		if(k.getKeyCode() == KeyEvent.VK_A) { 
			stopMove("left");
		}
		
		// Player presses right key.
		if(k.getKeyCode() == KeyEvent.VK_D) { 
			stopMove("right");
		}
		
		// Player presses up key, presumably to jump!
		if(k.getKeyCode() == KeyEvent.VK_W) { 
			stopJump();
			stopMove("up");
		}
		
		// Player presses down key
		if(k.getKeyCode() == KeyEvent.VK_S) { 
			stopMove("down");
		}
	}
	// Remove the weapon.
	public void unequipBottle() {
		
		// Dequip the bottle.
		setEquippedBottle(null);
	}
	
	// Remove the weapon.
	public void unequipWeapon() {
	
		setAnimations();
	}
	
	// Show possible interactions
	public void showPossibleInteractions() {
		if(!isUnitIsDead()) {
			int x1 = 0;
			int x2 = 0;
			int y1 = 0;
			int y2 = 0;
			
			// Get the box we will attack in if facing left.
			if(getFacingDirection().equals("Left")) {
				int heightMidPoint = getIntY() + getHeight()/2;
				y1 = heightMidPoint - DEFAULT_INTERACT_WIDTH/2;
				y2 = heightMidPoint + DEFAULT_INTERACT_WIDTH/2;
				x1 = getIntX() - DEFAULT_INTERACT_RANGE;
				x2 = getIntX() + getWidth();
			}
			
			// Get the box we will attack in if facing right.
			if(getFacingDirection().equals("Right")) {
				int heightMidPoint = getIntY() + getHeight()/2;
				y1 = heightMidPoint - DEFAULT_INTERACT_WIDTH/2;
				y2 = heightMidPoint + DEFAULT_INTERACT_WIDTH/2;
				x1 = getIntX();
				x2 = getIntX() + getWidth() + DEFAULT_INTERACT_RANGE;
			}
			
			// Get the box we will attack in facing up.
			if(getFacingDirection().equals("Up")) {
				int widthMidPoint = getIntX() + getWidth()/2;
				x1 = widthMidPoint - DEFAULT_INTERACT_WIDTH/2;
				x2 = widthMidPoint + DEFAULT_INTERACT_WIDTH/2;
				y1 = getIntY() - DEFAULT_INTERACT_RANGE;
				y2 = getIntY() + getHeight();
			}
			
			// Get the box we will attack in facing down.
			if(getFacingDirection().equals("Down")) {
				int widthMidPoint = getIntX() + getWidth()/2;
				x1 = widthMidPoint - DEFAULT_INTERACT_WIDTH/2;
				x2 = widthMidPoint + DEFAULT_INTERACT_WIDTH/2;
				y1 = getIntY();
				y2 = getIntY() + getHeight() + DEFAULT_INTERACT_RANGE;
			}
			
			// Get the units in the box around the front of the player.
			ArrayList<drawnObject> possibleInteractObjects = getObjectsInBox(x1,y1,x2,y2);
			
			// Get the ones we can actually interact with.
			ArrayList<drawnObject> interactObjects = new ArrayList<drawnObject>();
			if(possibleInteractObjects!=null)
			for(int i = 0; i < possibleInteractObjects.size(); i++) 
				if(possibleInteractObjects.get(i).canInteract() && possibleInteractObjects.get(i).isDrawObject()) 
					interactObjects.add(possibleInteractObjects.get(i));
			
			// Put an interact blurb on the closest object.
			drawnObject d = getClosestToFrom(interactObjects);
			if(d != null && !d.isBeingInteracted() && d.isShowInteractable()) {
				interactBlurb iBlurb = d.getAttachedInteractBlurb();
				if(iBlurb == null) {
					
					if(d instanceof unit) {
						// Create a blurb for units
						interactBlurb blurb = new interactBlurb(d.getIntX()-interactBlurb.getDefaultWidth(),d.getIntY()-d.getHitBoxAdjustmentY()-interactBlurb.getDefaultWidth());
						blurb.attachToObject(d);
					}
					else {
						// Create blurb for objects.
						interactBlurb blurb = new interactBlurb(d.getIntX()-interactBlurb.getDefaultWidth() + d.getWidth()/2,d.getIntY()-d.getHitBoxAdjustmentY()+d.getHeight()/2-interactBlurb.getDefaultWidth());
						blurb.attachToObject(d);
					}
				}
				else {
					iBlurb.refreshTimer();
				}
			}
		}
		
	}
	
	// Interact in front of the player.
	public void interact() {
		int x1 = 0;
		int x2 = 0;
		int y1 = 0;
		int y2 = 0;
		
		// Get the box we will attack in if facing left.
		if(getFacingDirection().equals("Left")) {
			int heightMidPoint = getIntY() + getHeight()/2;
			y1 = heightMidPoint - DEFAULT_INTERACT_WIDTH/2;
			y2 = heightMidPoint + DEFAULT_INTERACT_WIDTH/2;
			x1 = getIntX() - DEFAULT_INTERACT_RANGE;
			x2 = getIntX() + getWidth();
		}
		
		// Get the box we will attack in if facing right.
		if(getFacingDirection().equals("Right")) {
			int heightMidPoint = getIntY() + getHeight()/2;
			y1 = heightMidPoint - DEFAULT_INTERACT_WIDTH/2;
			y2 = heightMidPoint + DEFAULT_INTERACT_WIDTH/2;
			x1 = getIntX();
			x2 = getIntX() + getWidth() + DEFAULT_INTERACT_RANGE;
		}
		
		// Get the box we will attack in facing up.
		if(getFacingDirection().equals("Up")) {
			int widthMidPoint = getIntX() + getWidth()/2;
			x1 = widthMidPoint - DEFAULT_INTERACT_WIDTH/2;
			x2 = widthMidPoint + DEFAULT_INTERACT_WIDTH/2;
			y1 = getIntY() - DEFAULT_INTERACT_RANGE;
			y2 = getIntY() + getHeight();
		}
		
		// Get the box we will attack in facing down.
		if(getFacingDirection().equals("Down")) {
			int widthMidPoint = getIntX() + getWidth()/2;
			x1 = widthMidPoint - DEFAULT_INTERACT_WIDTH/2;
			x2 = widthMidPoint + DEFAULT_INTERACT_WIDTH/2;
			y1 = getIntY();
			y2 = getIntY() + getHeight() + DEFAULT_INTERACT_RANGE;
		}
		
		// Get the units in the box around the front of the player.
		ArrayList<drawnObject> possibleInteractObjects = getObjectsInBox(x1,y1,x2,y2);
		
		// Get the ones we can actually interact with.
		ArrayList<drawnObject> interactObjects = new ArrayList<drawnObject>();
		if(possibleInteractObjects!=null)
		for(int i = 0; i < possibleInteractObjects.size(); i++) 
			if(possibleInteractObjects.get(i).canInteract() && possibleInteractObjects.get(i).isDrawObject()) 
				interactObjects.add(possibleInteractObjects.get(i));

		// Interact with the first thing.
		if(interactObjects!=null && interactObjects.size() != 0) {
			interactBlurb iBlurb = interactObjects.get(0).getAttachedInteractBlurb();
			if(iBlurb != null) iBlurb.end();
			getClosestToFrom(interactObjects).interactWith();
		}
	}
	
	// Claw attacking?
	protected boolean hasClawSpawned = false;
	protected boolean slashing = false;
	protected long startOfClawAttack = 0;
	private float spawnClawPhaseTime = .5f;
	protected float clawAttackEveryBase = 3f;
	private float clawAttackEvery = 0f;
	protected long lastClawAttack = 0;
	
	// Claw
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

	// Previous gravity state
	private Boolean prevGravityState = null;
	
	// pushTime.
	private long pushStart = 0;
	private float pushTime = 0;
	
	// Deal with jumping for topDown
	// TODO: make this more of an arch?
	public void dealWithPushBottling() {
			
			// Push to the location.
			if(isPushing()) {
				
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
						pushTime = distanceXY/(getJumpSpeed()*(1000/time.DEFAULT_TICK_RATE));
					}
					startX = getDoubleX();
					startY = getDoubleY();
				}
				
				setStunned(true);
				move(run,rise);
				
				// Don't let him not move at all or leave region.
				if(time.getTime() - pushStart > pushTime*1000) {
					
					// Turn gravity back on
					setGravity(prevGravityState);
					prevGravityState = null;
					setPushing(false);
					
					// Set old movespeed.
					setStunned(false);
					moveSpeed = oldMoveSpeed;
					
					// Make player killable
					forceInFront = false;
				}
			}
			else {
			}
	}
	
	// Jump
	public void slashTo(int x, int y) {
		
		// When we started jumping.
		pushStart = time.getTime();
		
		// Turn off moving.
		moveSpeed = 0;
		setStunned(true);
		setMomentumX(0);
		setMomentumY(0);
		setFallSpeed(0);
		if(prevGravityState == null) prevGravityState = isGravity();
		setGravity(false);
		
		// Collision is off.
		//collisionOn = false;
		
		// Make player unkillable
		forceInFront = true;
		
		// Jump there
		jumpingToX = x;
		jumpingToY = y;
		
		// Setup for our jumping function.
		setPushing(true);
		slashing = true;
		hasSlashed = false;
		riseRunSet = false;
	}
	
	// Get jump speed override
	@Override
	public float getJumpSpeed() {
		if(mode.getCurrentMode().equals("topDown")) {
			return jumpSpeed/2;
		}
		else {
			return jumpSpeed;
		}
	}
	
	// Initiate does nothing.
	public static void initiate() {
	}
	
	/////////////////////////
	// Getters and setters //
	/////////////////////////
	
	public static player getPlayer() {
		return currentPlayer;
	}

	public static void setCurrentPlayer(player newCurrentPlayer) {
		currentPlayer = newCurrentPlayer;
	}

	public zone getCurrentZone() {
		return currentZone;
	}

	public void setCurrentZone(zone playerZone) {
		this.currentZone = playerZone;
	}

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public inventory getPlayerInventory() {
		return playerInventory;
	}

	public void setPlayerInventory(inventory playerInventory) {
		this.playerInventory = playerInventory;
	}

	public bottle getEquippedBottle() {
		return equippedBottle;
	}

	public void setEquippedBottle(bottle equippedBottle) {
		this.equippedBottle = equippedBottle;
	}
	
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

	public playerActionBar getHealthBar() {
		return actionBar;
	}

	public void setHealthBar(playerActionBar healthBar) {
		this.actionBar = healthBar;
	}

	public static boolean isDeveloper() {
		return isDeveloper;
	}

	public static void setDeveloper(boolean isDeveloper) {
		player.isDeveloper = isDeveloper;
	}
}