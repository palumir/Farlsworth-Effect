package units;

import java.awt.event.KeyEvent;

import drawing.camera;
import drawing.sprites.animation;
import drawing.sprites.animationPack;
import drawing.sprites.spriteSheet;
import drawing.sprites.spriteSheet.spriteSheetInfo;
import modes.mode;
import modes.platformer;
import modes.topDown;
import utilities.saveState;
import utilities.utility;
import zones.zone;
import zones.farmLand.sheepFarm;
import zones.farmLand.spiderCave;

public class player extends unit {
	
	////////////////
	/// DEFAULTS ///
	////////////////
	// Default dimensions.
	private static int DEFAULT_PLATFORMER_HEIGHT = 46;
	private static int DEFAULT_TOPDOWN_HEIGHT = 20;
	
	// Platformer and topdown default adjustment
	private static int DEFAULT_PLATFORMER_ADJUSTMENT_Y = 6;
	private static int DEFAULT_TOPDOWN_ADJUSTMENT_Y = 20;
	
	// Default name.
	private static String DEFAULT_PLAYER_NAME = "IanRetard";
	
	// Default gender.
	private static String DEFAULT_PLAYER_GENDER = "female";
	
	// Default zone.
	private static zone DEFAULT_ZONE = null;
	
	// Default movespeed.
	private static int DEFAULT_PLAYER_MOVESPEED = 3;
	
	// Default jump speed
	private static int DEFAULT_PLAYER_JUMPSPEED = 10;
	
	// Player sprite stuff.
	private static String DEFAULT_PLAYER_SPRITESHEET = "images/units/player/" + DEFAULT_PLAYER_GENDER + "/noItems.png";
	
	// 
	
	///////////////
	/// GLOBALS ///
	///////////////
	
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

	///////////////
	/// METHODS ///
	///////////////
	
	// Constructor
	public player(int newX, int newY, zone z) {
		super(playerType, newX, newY);
		
		// TODO: Dev stuff
		showUnitPosition();
		showHitBox();
		setCollision(false);
		
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
		
		playerZone.loadZone();
		
		// Make adjustments on hitbox if we're in topDown.
		if(mode.getCurrentMode().equals("topDown")) {
			height = DEFAULT_TOPDOWN_HEIGHT;
			setHitBoxAdjustmentY(DEFAULT_TOPDOWN_ADJUSTMENT_Y);
		}
		else {
			height = DEFAULT_PLATFORMER_HEIGHT;
			setHitBoxAdjustmentY(DEFAULT_PLATFORMER_ADJUSTMENT_Y);
		}
	}
	
	// Player AI does nothing. Controlled by user!
	public void AI() {}
	
	// Load player from save state.
	public static player loadPlayer(zone z) {
		
		// Initiate all.
		utility.initiateAll();
		
		// The player, depending on whether or not we have a save file.
		player thePlayer;
		
		// If the game just started, check what zone they're in using their savefile.
		// If we don't have a savefile, load them into the starting zone.
		
		// The fields we will try to load.
		saveState s = null;
		int playerX = 0;
		int playerY = 0;
		String newFacingDirection = null;
		
		if(z == null) {
			s = saveState.loadSaveState();
			if(s != null) {
				z = zone.getZoneByName(s.getZoneName());
				playerX = s.getPlayerX();
				playerY = s.getPlayerY();
				newFacingDirection = s.getFacingDirection();
			}
			else {
				z = zone.getStartZone();
				playerX = z.getDefaultLocation().x;
				playerY = z.getDefaultLocation().y;
				newFacingDirection = "Up";
			}
		}
		else {
			playerX = z.getDefaultLocation().x;
			playerY = z.getDefaultLocation().y;
			newFacingDirection = "Up";
		}
		
		// If we didn't load any save data and z is still null.
		if(z == null && s==null) {
			z = zone.getStartZone();
		}
		
		// Create the player in the zone. Start zone if no zone was loaded from the save.
		thePlayer = new player(playerX, playerY, z);
		
		// Set our fields.
		thePlayer.setFacingDirection(newFacingDirection);
		
		return thePlayer;
	}
	
	// Make the player the main player.
	public void makeCurrentPlayer() {
		setCurrentPlayer(this);
	}
	
	// Responding to key presses.
	public void keyPressed(KeyEvent k) {
		
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
		//////////////////////////////////////////
		// TODO: TESTING STUFF.
		//////////////////////////////////////
		if(k.getKeyCode() == KeyEvent.VK_P) {
			//saveState.createSaveState();
			// Switch from zone 1 to 2
			if(playerZone.getName() == spiderCave.getZone().getName()) {
				System.out.println("Switching from zone 1 to 2");
				zone.switchZones(this, playerZone, sheepFarm.getZone());
			}
			
			// Switch from zone 2 to 1
			else if(playerZone.getName() == sheepFarm.getZone().getName()) {
				System.out.println("Switching from zone 2 to 1");
				zone.switchZones(this, playerZone, spiderCave.getZone());
			}
		}
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
}