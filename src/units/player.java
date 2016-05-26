package units;

import java.awt.event.KeyEvent;

import drawing.camera;
import modes.mode;
import modes.platformer;
import modes.topDown;

public class player extends unit {
	
	// Defaults
	public static int DEFAULT_PLAYER_MOVESPEED = 2;
	
	// Main player
	private static player mainPlayer = null;
	public static unitType player;

	// Init player;
	public static void initiate() {
		player = new unitType("player", "square", 10, 10);
		player.setMoveSpeed(DEFAULT_PLAYER_MOVESPEED);
	}
	
	// Constructor
	public player(int newX, int newY) {
		super(player, newX, newY);
		camera c = new camera(this, 1);
		camera.setCurrent(c);
		attachedCamera = c;
		this.makeMainPlayer();
	}
	
	// Make the player the main player.
	public void makeMainPlayer() {
		setMainPlayer(this);
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
				//jump(true);
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
				//jump(true);
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
	
	/////////////////////////
	// Getters and setters //
	/////////////////////////
	
	public static player getMainPlayer() {
		return mainPlayer;
	}

	public static void setMainPlayer(player newMainPlayer) {
		mainPlayer = newMainPlayer;
	}
}