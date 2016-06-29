package units;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import doodads.sheepFarm.bone;
import drawing.camera;
import drawing.drawnObject;
import drawing.animation.animation;
import drawing.animation.animationPack;
import drawing.userInterface.playerHealthBar;
import drawing.userInterface.text;
import drawing.userInterface.tooltipString;
import effects.effect;
import effects.effectTypes.bloodSquirt;
import effects.effectTypes.floatingString;
import interactions.event;
import interactions.interactBox;
import interactions.quest;
import items.bottle;
import items.inventory;
import items.item;
import items.weapon;
import main.main;
import modes.mode;
import modes.platformer;
import modes.topDown;
import sounds.music;
import sounds.sound;
import utilities.saveState;
import utilities.utility;
import zones.zone;

public class player extends unit {
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
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
	private static int DEFAULT_PLAYER_MOVESPEED = 10;//3;
	// 3 is default
	
	// Default jump speed
	private static int DEFAULT_PLAYER_JUMPSPEED = 10;
	
	// Player sprite stuff.
	private static String DEFAULT_PLAYER_SPRITESHEET = "images/units/player/" + DEFAULT_PLAYER_GENDER + "/noItems.png";
	
	// Combat defaults
	private static int DEFAULT_BASE_HP = 20;
	
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
	private zone currentZone = DEFAULT_ZONE;
	
	// Savestate we loaded from
	public saveState playerSaveState;
	
	// Player inventory
	private inventory playerInventory = new inventory();
	
	// Combat
	private weapon equippedWeapon = null;
	private bottle equippedBottle = null;
	
	// Holding space to attack?
	private boolean holdingSpace = false;
	
	// Levels
	private int playerLevel = 1;
	private int expIntoLevel = 20;
	
	// Level up sounds
	private String levelUp = "sounds/effects/player/levelUp.wav";
	
	// Exp required
	public static int expRequiredForLevel() {
		int i = 0;
		if(currentPlayer != null) 
		i = player.getCurrentPlayer().getPlayerLevel() + 1;
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
	private playerHealthBar healthBar = new playerHealthBar(5,5);

	///////////////
	/// METHODS ///
	///////////////

	// Constructor
	public player(int newX, int newY, zone z) {
		super(playerType, newX, newY);
		
		/// TODO: Dev stuff
		showUnitPosition();
		//showHitBox();
		//setCollision(false);
		//setMoveSpeed(10);
		//showAttackRange(); 
		
		// Set sounds.
		attackSound = "sounds/effects/player/combat/swingWeapon.wav";
		
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
		
		// Set dimensions
		setHeight(getDefaultHeight());
		setWidth(getDefaultWidth());
		platformerHeight = DEFAULT_PLATFORMER_HEIGHT;
		platformerWidth = DEFAULT_PLATFORMER_WIDTH;
		topDownHeight = DEFAULT_TOPDOWN_HEIGHT;
		topDownWidth = DEFAULT_TOPDOWN_WIDTH;
		setHitBoxAdjustmentY(getDefaultHitBoxAdjustmentY());
	}
	
	// React to pain.
	public void reactToPain() {
		// Squirt blood
		int randomX = -getWidth()/3 + utility.RNG.nextInt(getWidth()/3);
		int randomY = -getHeight()/2 + utility.RNG.nextInt(getHeight()/2);
		effect e = new bloodSquirt(getX() - bloodSquirt.getDefaultWidth()/2 + getWidth()/2 + randomX,
				   getY() - bloodSquirt.getDefaultHeight()/2 + getHeight()/2 + randomY);
	}
	
	// Player AI controls the interface
	public void updateUnit() {
		potentiallyAttack();
		levelUp();
	}
	
	// Attack?
	public void potentiallyAttack() {
		if(holdingSpace) attack();
	}
	
	// Deal with player being alive or dead.
	@Override
	public void aliveOrDead() {
		
		// Dead
		if(getHealthPoints() <= 0) {
			music.endAll();
			killPlayer();
		}
	}
	
	// Kill player.
	public void killPlayer() {
		main.restartGame("Death");
	}
	
	// Load player.
	public static player loadPlayer(player alreadyPlayer, zone z, int spawnX, int spawnY, String direction) {
		
		// Initiate all.
		saveState s = utility.initiateAll();
		
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
		int newPlayerLevel = 1;
		int newPlayerExpIntoLevel = 0;
		String newFacingDirection = null;
		inventory loadedInventory = new inventory(); // empty inventory
		weapon loadedEquippedWeapon = null;
		bottle loadedEquippedBottle = null;
		
		// If no zone is given and we don't have the save file. First time running game. 
		if(z == null && s==null && alreadyPlayer == null) {
			loadZone = zone.getStartZone();
			playerX = loadZone.getDefaultLocation().x;
			playerY = loadZone.getDefaultLocation().y;
			newFacingDirection = "Up";
			tooltipString t = new tooltipString("Use 'wasd' or arrow keys to move.");
		}
		
		// If we have the savestate.
		if(s != null && alreadyPlayer == null) {
			loadZone = zone.getZoneByName(s.getZoneName());
			playerX = s.getPlayerX();
			playerY = s.getPlayerY();
			newFacingDirection = s.getFacingDirection();
			loadedInventory = s.getPlayerInventory();
			loadedEquippedWeapon = s.getEquippedWeapon();
			loadedEquippedBottle = s.getEquippedBottle();
			newPlayerLevel = s.getPlayerLevel();
			newPlayerExpIntoLevel = s.getExpIntoLevel();
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
				if(alreadyPlayer.getEquippedWeapon() != null)
				loadedEquippedWeapon = (weapon) alreadyPlayer.getEquippedWeapon().getItemRef();
				if(alreadyPlayer.getEquippedBottle() != null)
				loadedEquippedBottle = (bottle) alreadyPlayer.getEquippedBottle().getItemRef();
				newPlayerLevel = alreadyPlayer.getPlayerLevel();
				newPlayerExpIntoLevel = alreadyPlayer.getExpIntoLevel();
			}
		}
		
		// Create the player in the zone. Start zone if no zone was loaded from the save.
		thePlayer = new player(playerX, playerY, loadZone);
		
		// Set our fields
		thePlayer.setFacingDirection(newFacingDirection);
		thePlayer.setPlayerInventory(loadedInventory);
		thePlayer.setPlayerLevel(newPlayerLevel);
		thePlayer.setExpIntoLevel(newPlayerExpIntoLevel);
		if(loadedEquippedWeapon!=null) loadedEquippedWeapon.equip();
		if(loadedEquippedBottle!=null) loadedEquippedBottle.equip();
		
		// Update our player stats to match our level.
		thePlayer.updateStats();
		
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
	
	// Responding to key presses.
	public void keyPressed(KeyEvent k) {
		
		// Respond to dialogue/interact presses.
		if(interactBox.getCurrentDisplay() != null) {
			interactBox.getCurrentDisplay().respondToKeyPress(k);
		}
		
		// Player presses i (inventory) key.
		else if(k.getKeyCode() == KeyEvent.VK_I) { 
			playerInventory.toggleDisplay();
		}
		
		// Respond to inventory presses.
		else if(playerInventory.isDisplayOn()) {
			playerInventory.respondToKeyPress(k);
		}
		
		// Respond to other presses (movement)
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
					startMove("up");
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
					startMove("down");
				}
				else if(mode.getCurrentMode() == topDown.name) {
					startMove("down");
				}
			}
		
			// Player presses bar key
			if(k.getKeyCode() == KeyEvent.VK_SPACE) {
				startAttack();
			}
			
			// Player presses bar key
			if(k.getKeyCode() == KeyEvent.VK_Q || k.getKeyCode() == KeyEvent.VK_SHIFT) {
				if(equippedBottle!=null) equippedBottle.useCharge();
			}
			
			
			// Player presses e key
			if(k.getKeyCode() == KeyEvent.VK_E || k.getKeyCode() == KeyEvent.VK_ENTER) {
				interact();
			}
		
			//////////////////////////////////////////
			// TODO: CHEAT BUTTON
			//////////////////////////////////////
			if(k.getKeyCode() == KeyEvent.VK_P) {
				//healthPoints--;
				saveState.createSaveState();
				giveExp(expRequiredForLevel());
				//System.out.println("u = new wolf(" + getX() + "," + getY() + ");");
			}
		}
	}
	
	// Give exp
	public void giveExp(int i) {
		int expToNextLevel = expRequiredForLevel() - expIntoLevel;
		
		// If it will level us up.
		if(i - expToNextLevel > 0) {
			expIntoLevel = expRequiredForLevel();
			levelUp();
			giveExp(i - expToNextLevel);
		}
		
		else {// Give exp
			expIntoLevel += i;
		}
	}
	
	// Level up
	public void levelUp() {
		
		// Level up if we have max exp
		if(expIntoLevel >= expRequiredForLevel()) {
			// Play sound
			sound s = new sound(levelUp);
			s.setVolume(0.8f);
			s.start();
			
			// Play level-up effect
			floatingString f = new floatingString("+1 Level",playerHealthBar.DEFAULT_EXP_COLOR,getX() + getWidth()/2, getY()+getHeight()/2, 1f, 3f);
			
			// Update stats and level.
			expIntoLevel = 0;
			playerLevel++;
			updateStats();
		}
	}
	
	// Start attack
	public void startAttack() {
		holdingSpace = true;
	}
	
	// Stop attack.
	public void stopAttack() {
		holdingSpace = false;
	}
	
	// Update stats.
	public void updateStats() {
		
		// TODO: Formula for how much their attack increases per level.
		attackMultiplier = 1f + 3*((float)(playerLevel - 1)/10f);
		maxHealthPoints = DEFAULT_BASE_HP + (playerLevel - 1)*3;
		
		// Update health.
		healthPoints = maxHealthPoints;

	}
	
	// Remove the weapon.
	public void unequipBottle() {
		
		// Set the charges to be 0, we can only fill equipped bottles.
		equippedBottle.setChargesLeft(0);
		
		// Dequip the bottle.
		setEquippedBottle(null);
	}
	
	// Remove the weapon.
	public void unequipWeapon() {
		// Equip the weapon.
		setEquippedWeapon(null);
		
		// Damage
		setAttackDamage(DEFAULT_ATTACK_DAMAGE);
		
		// Attack time.
		setBaseAttackTime(DEFAULT_BAT);
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
		animation attackingLeft = new animation("attackingLeft", getTypeOfUnit().getUnitTypeSpriteSheet().getAnimation(13), 0, 5, unit.DEFAULT_ATTACK_TIME);
		unitTypeAnimations.addAnimation(attackingLeft);
		
		// Attacking left animation.
		animation attackingRight = new animation("attackingRight", getTypeOfUnit().getUnitTypeSpriteSheet().getAnimation(15), 0, 5, unit.DEFAULT_ATTACK_TIME);
		unitTypeAnimations.addAnimation(attackingRight);
		
		// Attacking left animation.
		animation attackingUp = new animation("attackingUp", getTypeOfUnit().getUnitTypeSpriteSheet().getAnimation(12), 0, 5, unit.DEFAULT_ATTACK_TIME);
		unitTypeAnimations.addAnimation(attackingUp);
		
		// Attacking left animation.
		animation attackingDown = new animation("attackingDown", getTypeOfUnit().getUnitTypeSpriteSheet().getAnimation(14), 0, 5, unit.DEFAULT_ATTACK_TIME);
		unitTypeAnimations.addAnimation(attackingDown);
		
		// Jumping left animation.
		animation jumpingLeft = new animation("jumpingLeft", getTypeOfUnit().getUnitTypeSpriteSheet().getAnimation(1), 5, 5, 1);
		unitTypeAnimations.addAnimation(jumpingLeft);
		
		// Jumping right animation.
		animation jumpingRight = new animation("jumpingRight", getTypeOfUnit().getUnitTypeSpriteSheet().getAnimation(3), 5, 5, 1);
		unitTypeAnimations.addAnimation(jumpingRight);
		
		// Standing left animation.
		animation standingLeft = new animation("standingLeft", getTypeOfUnit().getUnitTypeSpriteSheet().getAnimation(9), 0, 0, 1);
		unitTypeAnimations.addAnimation(standingLeft);
		
		// Standing up animation.
		animation standingUp = new animation("standingUp", getTypeOfUnit().getUnitTypeSpriteSheet().getAnimation(8), 0, 0, 1);
		unitTypeAnimations.addAnimation(standingUp);
		
		// Standing right animation.
		animation standingRight = new animation("standingRight", getTypeOfUnit().getUnitTypeSpriteSheet().getAnimation(11), 0, 0, 1);
		unitTypeAnimations.addAnimation(standingRight);
		
		// Standing down animation.
		animation standingDown = new animation("standingDown", getTypeOfUnit().getUnitTypeSpriteSheet().getAnimation(10), 0, 0, 1);
		unitTypeAnimations.addAnimation(standingDown);
		
		// Running left animation.
		animation runningLeft = new animation("runningLeft", getTypeOfUnit().getUnitTypeSpriteSheet().getAnimation(9), 0, 8, 1);
		unitTypeAnimations.addAnimation(runningLeft);		
		
		// Running up animation.
		animation runningUp = new animation("runningUp", getTypeOfUnit().getUnitTypeSpriteSheet().getAnimation(8), 0, 8, 1);
		unitTypeAnimations.addAnimation(runningUp);
		
		// Running right animation.
		animation runningRight = new animation("runningRight", getTypeOfUnit().getUnitTypeSpriteSheet().getAnimation(11), 0, 8, 1);
		unitTypeAnimations.addAnimation(runningRight);
		
		// Running down animation.
		animation runningDown = new animation("runningDown", getTypeOfUnit().getUnitTypeSpriteSheet().getAnimation(10), 0, 8, 1);
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
		
		// Player presses right key.
		if(k.getKeyCode() == KeyEvent.VK_SPACE) { 
			stopAttack();
		}
		
		// Player presses up key, presumably to jump!
		if(k.getKeyCode() == KeyEvent.VK_UP || k.getKeyCode() == KeyEvent.VK_W) { 
			stopJump();
			stopMove("up");
			
		}
		
		// Player presses down key
		if(k.getKeyCode() == KeyEvent.VK_DOWN || k.getKeyCode() == KeyEvent.VK_S) { 
			stopMove("down");
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
			int heightMidPoint = getY() + getHeight()/2;
			y1 = heightMidPoint - DEFAULT_INTERACT_WIDTH/2;
			y2 = heightMidPoint + DEFAULT_INTERACT_WIDTH/2;
			x1 = getX() - DEFAULT_INTERACT_RANGE;
			x2 = getX() + getWidth();
		}
		
		// Get the box we will attack in if facing right.
		if(getFacingDirection().equals("Right")) {
			int heightMidPoint = getY() + getHeight()/2;
			y1 = heightMidPoint - DEFAULT_INTERACT_WIDTH/2;
			y2 = heightMidPoint + DEFAULT_INTERACT_WIDTH/2;
			x1 = getX();
			x2 = getX() + getWidth() + DEFAULT_INTERACT_RANGE;
		}
		
		// Get the box we will attack in facing up.
		if(getFacingDirection().equals("Up")) {
			int widthMidPoint = getX() + getWidth()/2;
			x1 = widthMidPoint - DEFAULT_INTERACT_WIDTH/2;
			x2 = widthMidPoint + DEFAULT_INTERACT_WIDTH/2;
			y1 = getY() - DEFAULT_INTERACT_RANGE;
			y2 = getY() + getHeight();
		}
		
		// Get the box we will attack in facing down.
		if(getFacingDirection().equals("Down")) {
			int widthMidPoint = getX() + getWidth()/2;
			x1 = widthMidPoint - DEFAULT_INTERACT_WIDTH/2;
			x2 = widthMidPoint + DEFAULT_INTERACT_WIDTH/2;
			y1 = getY();
			y2 = getY() + getHeight() + DEFAULT_INTERACT_RANGE;
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

	public bottle getEquippedBottle() {
		return equippedBottle;
	}

	public void setEquippedBottle(bottle equippedBottle) {
		this.equippedBottle = equippedBottle;
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

	public playerHealthBar getHealthBar() {
		return healthBar;
	}

	public void setHealthBar(playerHealthBar healthBar) {
		this.healthBar = healthBar;
	}
}