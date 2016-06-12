package units;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import animation.animation;
import animation.animationPack;
import drawing.camera;
import drawing.drawnObject;
import effects.effect;
import effects.effectTypes.bloodSquirt;
import items.inventory;
import items.item;
import items.weapon;
import main.main;
import modes.mode;
import modes.platformer;
import modes.topDown;
import sounds.sound;
import userInterface.playerHealthBar;
import userInterface.text;
import utilities.saveState;
import utilities.utility;
import zones.zone;

public class player extends unit {
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default dimensions.
	private static int DEFAULT_PLATFORMER_HEIGHT = 46;
	private static int DEFAULT_TOPDOWN_HEIGHT = 20;
	
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
	private static int DEFAULT_PLAYER_MOVESPEED = 3;
	// 3 is default
	
	// Default jump speed
	private static int DEFAULT_PLAYER_JUMPSPEED = 10;
	
	// Player sprite stuff.
	private static String DEFAULT_PLAYER_SPRITESHEET = "images/units/player/" + DEFAULT_PLAYER_GENDER + "/noItems.png";
	
	// Default interact range.
	private static int DEFAULT_INTERACT_RANGE = 20;
	private static int DEFAULT_INTERACT_WIDTH = 40;
	
	///////////////
	/// GLOBALS ///
	///////////////
	
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
			     DEFAULT_PLAYER_MOVESPEED, // Movespeed
			     DEFAULT_PLAYER_JUMPSPEED // Jump speed
				);	   
	
	////////////////
	//// FIELDS ////
	////////////////
	
	// Player specific fields.
	private String playerName = DEFAULT_PLAYER_NAME;
	private zone playerZone = DEFAULT_ZONE;
	
	// Player inventory
	private inventory playerInventory = new inventory();
	
	// Combat
	private item equippedWeapon = null;
	private item equippedPotion = null;
	
	// Levels
	private int playerLevel = 1;
	private int expIntoLevel = 20;
	
	// Exp required
	public static int expRequiredForLevel() {
		int i = 0;
		if(currentPlayer != null) 
		i = currentPlayer.getCurrentPlayer().getPlayerLevel() + 1;
		if(i==2) return 100;
		if(i==3) return 150;
		if(i==4) return 250;
		if(i==5) return 450;
		if(i==6) return 950;
		if(i==7) return 1900;
		if(i==8) return 3000;
		if(i==9) return 4600;
		if(i==10) return 10000;
		else return 25000;
	}
	
	// Player interface
	private playerHealthBar healthBar = new playerHealthBar(getHealthPoints(),getMaxHealthPoints(),5,5);

	///////////////
	/// METHODS ///
	///////////////
	
	// Constructor
	public player(int newX, int newY, zone z) {
		super(playerType, newX, newY);
		
		/// TODO: Dev stuff
		//showUnitPosition();
		//showHitBox();
		//setCollision(false);
		//showAttackRange(); 
		
		// Set sounds.
		attackSound = new sound("sounds/effects/player/combat/swingWeapon.wav");
		
		// Set-up the camera.
		camera c = new camera(this, 1);
		camera.setCurrent(c);
		c.setAttachedUnit(this);
		attachedCamera = c;
		
		// Set this player to be the main player.
		this.makeCurrentPlayer();
		
		// Has the player never played before? Put them in the start zone.
		if(z == null && getPlayerZone()==DEFAULT_ZONE) {
			playerZone = zone.getStartZone();
		}
		
		// Load the zone, even if it's the default.
		if(z != null) {
			playerZone = z;
		}

		// Combat.
		setAttackable(true);
	}
	
	// React to pain.
	public void reactToPain() {
		// Squirt blood
		int randomX = -width/3 + utility.RNG.nextInt(width/3);
		int randomY = -height/2 + utility.RNG.nextInt(height/2);
		effect e = new bloodSquirt(getX() - bloodSquirt.getDefaultWidth()/2 + width/2 + randomX,
				   getY() - bloodSquirt.getDefaultHeight()/2 + height/2 + randomY);
	}
	
	// Player AI controls the interface
	public void updateUnit() {
		aliveOrDead();
		updateInterface();
	}
	
	// Deal with player being alive or dead.
	public void aliveOrDead() {
		
		// Dead
		if(getHealthPoints() <= 0) {
			killPlayer();
		}
	}
	
	// Kill player.
	public void killPlayer() {
		main.restartGame();
	}
	
	// Update interface.
	public void updateInterface() {
		if(playerLoaded) {
			healthBar.setHealth(getHealthPoints());
			healthBar.setMaxHealth(getMaxHealthPoints());
		}
	}
	
	// Load player from save state.
	public static player loadPlayer(zone z, int spawnX, int spawnY, String direction) {
		
		// Initiate all.
		utility.initiateAll();
		
		// The player, depending on whether or not we have a save file.
		player thePlayer;
		
		// If the game just started, check what zone they're in using their savefile.
		// If we don't have a savefile, load them into the starting zone.
		
		// The fields we will try to load.
		saveState s = null;
		zone loadZone = null;
		int playerX = 0;
		int playerY = 0;
		String newFacingDirection = null;
		inventory loadedInventory = new inventory(); // empty inventory
		weapon loadedEquippedWeapon = null;
		
		// Load save state.
		s = saveState.loadSaveState();
		
		// If no zone is given and we don't have the save file. First time running game. 
		if(z == null && s==null) {
			loadZone = zone.getStartZone();
			playerX = loadZone.getDefaultLocation().x;
			playerY = loadZone.getDefaultLocation().y;
			newFacingDirection = "Up";
		}
		
		// If we have the savestate.
		if(s != null) {
			loadZone = zone.getZoneByName(s.getZoneName());
			playerX = s.getPlayerX();
			playerY = s.getPlayerY();
			newFacingDirection = s.getFacingDirection();
			loadedInventory = s.getPlayerInventory();
			loadedEquippedWeapon = s.getEquippedWeapon();
		}
		
		// If the zone, z, is given, we should have all of these details.
		if(z != null) {
			loadZone = z;
			playerX = spawnX;
			playerY = spawnY;
			newFacingDirection = direction;
		}
		
		// Create the player in the zone. Start zone if no zone was loaded from the save.
		thePlayer = new player(playerX, playerY, loadZone);
		
		// Set our fields
		thePlayer.setFacingDirection(newFacingDirection);
		thePlayer.setPlayerInventory(loadedInventory);
		if(loadedEquippedWeapon!=null) loadedEquippedWeapon.equip();
		
		// Set that we have loaded the player once.
		playerLoaded = true;
		
		// Load the player into the zone.
		thePlayer.playerZone.loadZone();
		
		// Make adjustments on hitbox if we're in topDown.
		if(mode.getCurrentMode().equals("topDown")) {
			thePlayer.height = DEFAULT_TOPDOWN_HEIGHT;
			thePlayer.setHitBoxAdjustmentY(DEFAULT_TOPDOWN_ADJUSTMENT_Y);
		}
		else {
			thePlayer.height = DEFAULT_PLATFORMER_HEIGHT;
			thePlayer.setHitBoxAdjustmentY(DEFAULT_PLATFORMER_ADJUSTMENT_Y);
		}
		
		return thePlayer;
	}
	
	// Make the player the main player.
	public void makeCurrentPlayer() {
		setCurrentPlayer(this);
	}
	
	// Responding to key presses.
	public void keyPressed(KeyEvent k) {
		
		// Player presses i (inventory) key.
		if(k.getKeyCode() == KeyEvent.VK_I) { 
			playerInventory.toggleDisplay();
		}
		
		if(playerInventory.isDisplayOn()) {
			// Player presses i (inventory) key.
			if(k.getKeyCode() == KeyEvent.VK_ESCAPE) { 
				playerInventory.toggleDisplay();
			}
			
			// Player presses left key.
			if(k.getKeyCode() == KeyEvent.VK_LEFT || k.getKeyCode() == KeyEvent.VK_A) { 
				playerInventory.moveSelect("left");
			}
			
			// Player presses right key.
			if(k.getKeyCode() == KeyEvent.VK_RIGHT || k.getKeyCode() == KeyEvent.VK_D) { 
				playerInventory.moveSelect("right");
			}
			
			// Player presses up key
			if(k.getKeyCode() == KeyEvent.VK_UP || k.getKeyCode() == KeyEvent.VK_W) { 
				playerInventory.moveSelect("up");
			}
			
			// Player presses down key
			if(k.getKeyCode() == KeyEvent.VK_DOWN || k.getKeyCode() == KeyEvent.VK_S) { 
				playerInventory.moveSelect("down");
			}
			
			// Player presses e key.
			if(k.getKeyCode() == KeyEvent.VK_E || k.getKeyCode() == KeyEvent.VK_SPACE) { 
				playerInventory.equipSelectedItem();
			}
		}
		else {
			// Player presses left key.
			if(k.getKeyCode() == KeyEvent.VK_LEFT || k.getKeyCode() == KeyEvent.VK_A) { 
				startMove("left");
			}
			
			// Player presses right key.
			if(k.getKeyCode() == KeyEvent.VK_RIGHT || k.getKeyCode() == KeyEvent.VK_D) { 
				startMove("right");
			}
			
			// Player presses up key, presumably to jump!
			if(k.getKeyCode() == KeyEvent.VK_UP || k.getKeyCode() == KeyEvent.VK_W) { 
				if(mode.getCurrentMode() == platformer.name) {
					startJump();
				}
				else if(mode.getCurrentMode() == topDown.name) {
					startMove("up");
				}
			}
			
			// Player presses down key
			if(k.getKeyCode() == KeyEvent.VK_DOWN || k.getKeyCode() == KeyEvent.VK_S) { 
				if(mode.getCurrentMode() == platformer.name) {
					//crouch(true);
				}
				else if(mode.getCurrentMode() == topDown.name) {
					startMove("down");
				}
			}
		
			// Player presses bar key
			if(k.getKeyCode() == KeyEvent.VK_SPACE) {
				if(equippedWeapon!=null) attack();
			}
			
			// Player presses e key
			if(k.getKeyCode() == KeyEvent.VK_E) {
				interact();
			}
		
			//////////////////////////////////////////
			// TODO: TESTING STUFF.
			//////////////////////////////////////
			if(k.getKeyCode() == KeyEvent.VK_P) {
				setHealthPoints(getHealthPoints() - 1);
			}
		}
	}
	
	// Remove the weapon.
	public void unequipWeapon() {
		// Equip the weapon.
		setEquippedWeapon(null);
		
		// Remove player's damage.
		setAttackDamage(0);
		setAttackTime(0);
		setBaseAttackTime(0);
		setAttackWidth(0);
		setAttackLength(0);
		
		// Deal with animations
		animationPack unitTypeAnimations = new animationPack();
		
		// Jumping left animation.
		animation jumpingLeft = new animation("jumpingLeft", typeOfUnit.getUnitTypeSpriteSheet().getAnimation(1), 5, 5, 1);
		unitTypeAnimations.addAnimation(jumpingLeft);
		
		// Jumping right animation.
		animation jumpingRight = new animation("jumpingRight", typeOfUnit.getUnitTypeSpriteSheet().getAnimation(3), 5, 5, 1);
		unitTypeAnimations.addAnimation(jumpingRight);
		
		// Standing left animation.
		animation standingLeft = new animation("standingLeft", typeOfUnit.getUnitTypeSpriteSheet().getAnimation(9), 0, 0, 1);
		unitTypeAnimations.addAnimation(standingLeft);
		
		// Standing up animation.
		animation standingUp = new animation("standingUp", typeOfUnit.getUnitTypeSpriteSheet().getAnimation(8), 0, 0, 1);
		unitTypeAnimations.addAnimation(standingUp);
		
		// Standing right animation.
		animation standingRight = new animation("standingRight", typeOfUnit.getUnitTypeSpriteSheet().getAnimation(11), 0, 0, 1);
		unitTypeAnimations.addAnimation(standingRight);
		
		// Standing down animation.
		animation standingDown = new animation("standingDown", typeOfUnit.getUnitTypeSpriteSheet().getAnimation(10), 0, 0, 1);
		unitTypeAnimations.addAnimation(standingDown);
		
		// Running left animation.
		animation runningLeft = new animation("runningLeft", typeOfUnit.getUnitTypeSpriteSheet().getAnimation(9), 0, 8, 1);
		unitTypeAnimations.addAnimation(runningLeft);		
		
		// Running up animation.
		animation runningUp = new animation("runningUp", typeOfUnit.getUnitTypeSpriteSheet().getAnimation(8), 0, 8, 1);
		unitTypeAnimations.addAnimation(runningUp);
		
		// Running right animation.
		animation runningRight = new animation("runningRight", typeOfUnit.getUnitTypeSpriteSheet().getAnimation(11), 0, 8, 1);
		unitTypeAnimations.addAnimation(runningRight);
		
		// Running down animation.
		animation runningDown = new animation("runningDown", typeOfUnit.getUnitTypeSpriteSheet().getAnimation(10), 0, 8, 1);
		unitTypeAnimations.addAnimation(runningDown);
		
		// Set animations.
		setAnimations(unitTypeAnimations);
	}
	
	// Responding to key releases.
	public void keyReleased(KeyEvent k) {
		// Player releases
		if(k.getKeyCode() == KeyEvent.VK_LEFT || k.getKeyCode() == KeyEvent.VK_A) { 
			stopMove("left");
		}
		
		// Player presses right key.
		if(k.getKeyCode() == KeyEvent.VK_RIGHT || k.getKeyCode() == KeyEvent.VK_D) { 
			stopMove("right");
		}
		
		// Player presses up key, presumably to jump!
		if(k.getKeyCode() == KeyEvent.VK_UP || k.getKeyCode() == KeyEvent.VK_W) { 
			if(mode.getCurrentMode() == platformer.name) {
				stopJump();
			}
			else if(mode.getCurrentMode() == topDown.name) {
				stopMove("up");
			}
		}
		
		// Player presses down key
		if(k.getKeyCode() == KeyEvent.VK_DOWN || k.getKeyCode() == KeyEvent.VK_S) { 
			if(mode.getCurrentMode() == platformer.name) {
				//crouch(true);
			}
			else if(mode.getCurrentMode() == topDown.name) {
				stopMove("down");
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
		if(getFacingDirection() == "Left") {
			int heightMidPoint = getY() + height/2;
			y1 = heightMidPoint - DEFAULT_INTERACT_WIDTH/2;
			y2 = heightMidPoint + DEFAULT_INTERACT_WIDTH/2;
			x1 = getX() - DEFAULT_INTERACT_RANGE;
			x2 = getX() + width;
		}
		
		// Get the box we will attack in if facing right.
		if(getFacingDirection() == "Right") {
			int heightMidPoint = getY() + height/2;
			y1 = heightMidPoint - DEFAULT_INTERACT_WIDTH/2;
			y2 = heightMidPoint + DEFAULT_INTERACT_WIDTH/2;
			x1 = getX();
			x2 = getX() + width + DEFAULT_INTERACT_RANGE;
		}
		
		// Get the box we will attack in facing up.
		if(getFacingDirection() == "Up") {
			int widthMidPoint = getX() + width/2;
			x1 = widthMidPoint - DEFAULT_INTERACT_WIDTH/2;
			x2 = widthMidPoint + DEFAULT_INTERACT_WIDTH/2;
			y1 = getY() - DEFAULT_INTERACT_RANGE;
			y2 = getY() + height;
		}
		
		// Get the box we will attack in facing down.
		if(getFacingDirection() == "Down") {
			int widthMidPoint = getX() + width/2;
			x1 = widthMidPoint - DEFAULT_INTERACT_WIDTH/2;
			x2 = widthMidPoint + DEFAULT_INTERACT_WIDTH/2;
			y1 = getY();
			y2 = getY() + height + DEFAULT_INTERACT_RANGE;
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
		if(interactObjects!=null && interactObjects.size() != 0) interactObjects.get(0).interactWith();
	}
	
	// Initiate does nothing.
	public static void initiate() {
	}
	
	/////////////////////////
	// Getters and setters //
	/////////////////////////
	
	public static player getCurrentPlayer() {
		return currentPlayer;
	}

	public static void setCurrentPlayer(player newCurrentPlayer) {
		currentPlayer = newCurrentPlayer;
	}

	public zone getPlayerZone() {
		return playerZone;
	}

	public void setPlayerZone(zone playerZone) {
		this.playerZone = playerZone;
	}

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public weapon getEquippedWeapon() {
		return (weapon)equippedWeapon;
	}

	public void setEquippedWeapon(weapon equippedWeapon) {
		this.equippedWeapon = equippedWeapon;
	}

	public inventory getPlayerInventory() {
		return playerInventory;
	}

	public void setPlayerInventory(inventory playerInventory) {
		this.playerInventory = playerInventory;
	}

	public item getEquippedPotion() {
		return equippedPotion;
	}

	public void setEquippedPotion(item equippedPotion) {
		this.equippedPotion = equippedPotion;
	}

	public int getExpIntoLevel() {
		return expIntoLevel;
	}

	public void setExpIntoLevel(int expIntoLevel) {
		this.expIntoLevel = expIntoLevel;
	}

	public int getPlayerLevel() {
		return playerLevel;
	}

	public void setPlayerLevel(int playerLevel) {
		this.playerLevel = playerLevel;
	}
}