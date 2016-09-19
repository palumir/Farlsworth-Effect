package units;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import UI.button;
import UI.playerActionBar;
import UI.tooltipString;
import UI.menus.deathMenu;
import doodads.sheepFarm.rock;
import drawing.camera;
import drawing.drawnObject;
import drawing.gameCanvas;
import drawing.animation.animation;
import drawing.animation.animationPack;
import effects.effect;
import effects.effectTypes.critBloodSquirt;
import effects.effectTypes.savePoint;
import effects.interfaceEffects.interactBlurb;
import interactions.interactBox;
import items.bottle;
import items.inventory;
import main.main;
import modes.mode;
import modes.platformer;
import modes.topDown;
import sounds.music;
import units.developer.developer;
import units.developer.propertiesButton;
import utilities.saveState;
import utilities.time;
import utilities.utility;
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
	private static int DEFAULT_TOPDOWN_ADJUSTMENT_Y = 20;
	
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
	
	// Default shield color
	private Color DEFAULT_SHIELD_COLOR = Color.cyan;
	
	///////////////
	/// GLOBALS ///
	///////////////
	
	// Is this player a developer?
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
	public Point lastSaveBottle;
	public savePoint lastSaveBottleChargeIndicator;
	
	// Player inventory
	private inventory playerInventory = new inventory();
	
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
		
		// Set sounds.
		setAttackSound(DEFAULT_ATTACK_SOUND);
		
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
		healthPoints = DEFAULT_PLAYER_HP;
		maxHealthPoints = DEFAULT_PLAYER_HP;
		
		// Set dimensions
		setHeight(getDefaultHeight());
		setWidth(getDefaultWidth());
		platformerHeight = DEFAULT_PLATFORMER_HEIGHT;
		platformerWidth = DEFAULT_PLATFORMER_WIDTH;
		topDownHeight = DEFAULT_TOPDOWN_HEIGHT;
		topDownWidth = DEFAULT_TOPDOWN_WIDTH;
		setHitBoxAdjustmentY(getDefaultHitBoxAdjustmentY());
		
		// Set player animations
		setNoWeaponStats();
		
		// Set z.
		setZ(1);
	}
	
	// Set animations
	public void setNoWeaponStats() {
		
		// Damage
		setAttackDamage(DEFAULT_ATTACK_DAMAGE);
		
		// Set attack sound
		setAttackSound(DEFAULT_ATTACK_SOUND);
		
		// Attack time.
		setAttackFrameStart(3);
		setAttackFrameEnd(5);
		setAttackTime(DEFAULT_ATTACK_TIME);
		
		// Attack range.
		setAttackWidth(DEFAULT_ATTACK_WIDTH);
		setAttackLength(DEFAULT_ATTACK_LENGTH);
		
		// Unit stats.
		setAttackVariability(DEFAULT_ATTACK_VARIABILITY); // Percentage
		setCritChance(DEFAULT_CRIT_CHANCE);
		setCritDamage(DEFAULT_CRIT_DAMAGE);
		
		// Deal with animations
		animationPack unitTypeAnimations = new animationPack();
		
		// Attacking left animation.
		animation attackingLeft = new animation("attackingLeft", getObjectSpriteSheet().getAnimation(13), 0, 8, DEFAULT_ATTACK_TIME);
		unitTypeAnimations.addAnimation(attackingLeft);
		
		// Attacking left animation.
		animation attackingRight = new animation("attackingRight", getObjectSpriteSheet().getAnimation(15), 0, 8, DEFAULT_ATTACK_TIME);
		unitTypeAnimations.addAnimation(attackingRight);
		
		// Attacking left animation.
		animation attackingUp = new animation("attackingUp", getObjectSpriteSheet().getAnimation(12), 0, 8, DEFAULT_ATTACK_TIME);
		unitTypeAnimations.addAnimation(attackingUp);
		
		// Attacking left animation.
		animation attackingDown = new animation("attackingDown", getObjectSpriteSheet().getAnimation(14), 0, 8, DEFAULT_ATTACK_TIME);
		unitTypeAnimations.addAnimation(attackingDown);
		
		// Jumping left animation.
		animation jumpingLeft = new animation("jumpingLeft", getObjectSpriteSheet().getAnimation(1), 5, 5, 1);
		unitTypeAnimations.addAnimation(jumpingLeft);
		
		// Jumping right animation.
		animation jumpingRight = new animation("jumpingRight", getObjectSpriteSheet().getAnimation(3), 5, 5, 1);
		unitTypeAnimations.addAnimation(jumpingRight);
		
		// Standing left animation.
		animation standingLeft = new animation("standingLeft", getObjectSpriteSheet().getAnimation(9), 0, 0, 1);
		unitTypeAnimations.addAnimation(standingLeft);
		
		// Standing up animation.
		animation standingUp = new animation("standingUp", getObjectSpriteSheet().getAnimation(8), 0, 0, 1);
		unitTypeAnimations.addAnimation(standingUp);
		
		// Standing right animation.
		animation standingRight = new animation("standingRight", getObjectSpriteSheet().getAnimation(11), 0, 0, 1);
		unitTypeAnimations.addAnimation(standingRight);
		
		// Standing down animation.
		animation standingDown = new animation("standingDown", getObjectSpriteSheet().getAnimation(10), 0, 0, 1);
		unitTypeAnimations.addAnimation(standingDown);
		
		// Running left animation.
		animation runningLeft = new animation("runningLeft", getObjectSpriteSheet().getAnimation(9), 1, 8, 0.75f);
		unitTypeAnimations.addAnimation(runningLeft);		
		
		// Running up animation.
		animation runningUp = new animation("runningUp", getObjectSpriteSheet().getAnimation(8), 1, 8, 0.75f);
		unitTypeAnimations.addAnimation(runningUp);
		
		// Running right animation.
		animation runningRight = new animation("runningRight", getObjectSpriteSheet().getAnimation(11), 1, 8, 0.75f);
		unitTypeAnimations.addAnimation(runningRight);
		
		// Running down animation.
		animation runningDown = new animation("runningDown", getObjectSpriteSheet().getAnimation(10), 1, 8, 0.75f);
		unitTypeAnimations.addAnimation(runningDown);
		
		// Set animations.
		setAnimations(unitTypeAnimations);
	}
	
	// React to pain.
	public void reactToPain() {
	}

	// Player AI controls the interface
	public void updateUnit() {
		
		// TODO: dev stuff
		if(drawnObject.dontReloadTheseObjects!=null); //System.out.println(drawnObject.dontReloadTheseObjects.size());
		
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
		
		if(!unitIsDead) {
			
			// Tell the player
			tooltipString t = new tooltipString("You died.");
			
			// Tell the player death timer to start.
			unitDiedAt = time.getTime();
			unitIsDead = true;
			
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
		if(unitIsDead && time.getTime() - unitDiedAt >= deathAnimationLasts*1000) {
			killPlayerFinally();
		}
	}
	
	// Load player.
	public static player loadPlayer(player alreadyPlayer, zone z, int spawnX, int spawnY, String direction) {
		
		// Initiate all.
		saveState s = main.initiateAll();
		
		// The player, depending on whether or not we have a save file.
		player thePlayer = loadPlayerSaveData(alreadyPlayer, s, z, spawnX, spawnY, direction);
		
		// Load the player into the zone.
		thePlayer.currentZone.loadZone();
		
		// Make adjustments on hitbox if we're in topDown.
		if(mode.getCurrentMode().equals("topDown")) {
			thePlayer.setHeight(DEFAULT_TOPDOWN_HEIGHT);
			thePlayer.setHitBoxAdjustmentY(DEFAULT_TOPDOWN_ADJUSTMENT_Y);
		}
		else {
			thePlayer.setHeight(DEFAULT_PLATFORMER_HEIGHT);
			thePlayer.setHitBoxAdjustmentY(DEFAULT_PLATFORMER_ADJUSTMENT_Y);
		}
		
		return thePlayer;
	}
	
	public static player loadPlayerSaveData(player alreadyPlayer, saveState s, zone z, int spawnX, int spawnY, String direction) {
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
		inventory loadedInventory = new inventory(); // empty inventory
		bottle loadedEquippedBottle = null;
		
		// If no zone is given and we don't have the save file. First time running game. 
		if(z == null && s==null && alreadyPlayer == null) {
			loadZone = zone.getStartZone();
			playerX = loadZone.getDefaultLocation().x;
			playerY = loadZone.getDefaultLocation().y;
			newFacingDirection = "Right";
			tooltipString t = new tooltipString("Use 'wasd' to move.");
		}
		
		// If we have the savestate.
		if(s != null && alreadyPlayer == null) {
			loadZone = zone.getZoneByName(s.getZoneName());
			playerX = s.getPlayerX();
			playerY = s.getPlayerY();
			newFacingDirection = s.getFacingDirection();
			loadedInventory = s.getPlayerInventory();
			loadedEquippedBottle = s.getEquippedBottle();
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
				
				// These will be carried over.
				if(alreadyPlayer.getEquippedBottle() != null)
				loadedEquippedBottle = (bottle) alreadyPlayer.getEquippedBottle();
			}
		}
		
		// Create the player. If we are a developer, give developer functions.
		if(!isDeveloper()) {
			thePlayer = new player(playerX, playerY, loadZone);
			thePlayer.actionBar = new playerActionBar(5,5);
		}
		else thePlayer = new developer(playerX, playerY, loadZone);
		
		// Set our fields
		thePlayer.setFacingDirection(newFacingDirection);
		thePlayer.setPlayerInventory(loadedInventory);
		
		// If there's a savestate.
		if(s != null) {
			thePlayer.chaosChoices = s.getChaosChoices();
			thePlayer.orderChoices = s.getOrderChoices();
			
			thePlayer.lastWell = s.lastWell;
			
			thePlayer.lastSaveBottle = s.lastSaveBottle;
			
			// Create an indicator
			if(s.lastSaveBottle != null) savePoint.createSavePoint();
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
		
		// Telepathy
		else {
			//playerMousePressed(e);
		}
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
			else if(k.getKeyCode() == KeyEvent.VK_Y) { 
				developer.toggleTestMode();
			}
			
			else if(unitIsDead) { 
				// Player presses bar key
				if(k.getKeyCode() == KeyEvent.VK_ENTER) {
					if(deathMenu.menu != null && objects.contains(deathMenu.menu.respawnAtSaveBottle)) deathMenu.menu.selectButton(deathMenu.menu.respawnAtSaveBottle);
				}
			}
			else {
				
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
					if(k.getKeyCode() == KeyEvent.VK_SHIFT) {
						//shield(true);
					}
					
					// Player presses left key.
					if(k.getKeyCode() == KeyEvent.VK_A) { 
						startMove("left");
					}
					
					// Player presses right key.
					if(k.getKeyCode() == KeyEvent.VK_D) { 
						startMove("right");
					}
					
					// Player presses up key, presumably to jump!
					if(k.getKeyCode() == KeyEvent.VK_W) { 
						if(mode.getCurrentMode() == platformer.name) {
							startMove("up");
							startJump();
						}
						else if(mode.getCurrentMode() == topDown.name) {
							startMove("up");
						}
					}
					
					// Player presses down key
					if(k.getKeyCode() == KeyEvent.VK_S) { 
						if(mode.getCurrentMode() == platformer.name) {
							startMove("down");
						}
						else if(mode.getCurrentMode() == topDown.name) {
							startMove("down");
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
	
		setNoWeaponStats();
	}
	
	// Show possible interactions
	public void showPossibleInteractions() {
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
			if(possibleInteractObjects.get(i).canInteract()) 
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
			if(possibleInteractObjects.get(i).canInteract()) 
				interactObjects.add(possibleInteractObjects.get(i));

		// Interact with the first thing.
		if(interactObjects!=null && interactObjects.size() != 0) {
			interactBlurb iBlurb = interactObjects.get(0).getAttachedInteractBlurb();
			if(iBlurb != null) iBlurb.end();
			getClosestToFrom(interactObjects).interactWith();
		}
	}
	
	// Draw special stuff
	@Override
	public void drawUnitSpecialStuff(Graphics g) {
		
		// Highlight box for telepathy
		//drawHighLightBox(g);
		
		// Are we shielding and topDown
		if(isShielding()) {
			g.setColor(DEFAULT_SHIELD_COLOR);
			if(getCurrentAnimation()!=null)
			g.fillOval(getDrawX() - (int)(gameCanvas.getScaleX()*(- (getCurrentAnimation().getCurrentFrame().getWidth()/2 - getWidth()/2) - getHitBoxAdjustmentX())),
					   ((getDrawY() + getHitBoxAdjustmentY() + getCurrentAnimation().getCurrentFrame().getHeight()/2 - getHeight()/2) + getHeight() - DEFAULT_PLATFORMER_HEIGHT), 
					   (int)(gameCanvas.getScaleX()*DEFAULT_PLATFORMER_WIDTH),
					   (int)(gameCanvas.getScaleY()*DEFAULT_PLATFORMER_HEIGHT));
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