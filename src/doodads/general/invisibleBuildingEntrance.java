package doodads.general;

import interactions.interactBox;
import interactions.textSeries;
import modes.mode;
import terrain.chunk;
import terrain.generalChunkType;
import units.player;
import zones.zone;

public class invisibleBuildingEntrance extends chunk {
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default name.
	private static String DEFAULT_CHUNK_NAME = "Invisible Doodad";
	
	// Dimensions
	private static int DEFAULT_SPRITE_WIDTH = 33;
	private static int DEFAULT_SPRITE_HEIGHT = 29;

	// Topdown
	private static int DEFAULT_TOPDOWN_ADJUSTMENT_Y = 0;
	private static int DEFAULT_TOPDOWN_WIDTH = DEFAULT_SPRITE_WIDTH;
	private static int DEFAULT_TOPDOWN_HEIGHT = DEFAULT_SPRITE_HEIGHT;

	// Platformer.
	private static int DEFAULT_PLATFORMER_ADJUSTMENT_Y = 0;
	private static int DEFAULT_PLATFORMER_HEIGHT = DEFAULT_SPRITE_HEIGHT;
	private static int DEFAULT_PLATFORMER_WIDTH = DEFAULT_SPRITE_WIDTH;
	
	// The actual type.
	private static generalChunkType typeReference = new generalChunkType(DEFAULT_CHUNK_NAME, DEFAULT_SPRITE_WIDTH, DEFAULT_SPRITE_HEIGHT);
	
	// Fields
	private zone toZone;
	private int spawnX;
	private int spawnY;
	private String spawnDirection;
	
	// Sequence
	private interactBox interactSequence;
	
	// Create interact sequence
	public interactBox makeNormalInteractSequence() {
		
		// Placeholder for each individual textSeries.
		textSeries s;
					
		// Start of conversation.
		textSeries startOfConversation = null;
		
		// Start of conversation.
		startOfConversation = new textSeries("StartWithButtons", "StartWithButtons");
		
		s = startOfConversation.addChild("Enter", "Okay.");
		s.setEnd();
		s = startOfConversation.addChild("Don't enter", " u're not rude as frig. con frigin gratys");
		s.setEnd();
		
		return new interactBox(startOfConversation, this);
	}
	
	// Update
	@Override
	public void update() {
	}
	
	// Constructor
	public invisibleBuildingEntrance(int newX, int newY, zone newZone, int newSpawnX, int newSpawnY, String direction) {
		super(typeReference, newX, newY, 0, 0);
		toZone = newZone;
		spawnDirection = direction;
		spawnX = newSpawnX;
		spawnY = newSpawnY;
		if(mode.getCurrentMode().equals("topDown")) {
			setHitBoxAdjustmentY(DEFAULT_TOPDOWN_ADJUSTMENT_Y);
			setWidth(DEFAULT_TOPDOWN_WIDTH);
			setHeight(DEFAULT_TOPDOWN_HEIGHT);
		}
		else {
			setHitBoxAdjustmentY(DEFAULT_PLATFORMER_ADJUSTMENT_Y);
			setHeight(DEFAULT_PLATFORMER_HEIGHT);
			setWidth(DEFAULT_PLATFORMER_WIDTH);
		}
		
		// Passable.
		setPassable(false);
		
		// Don't draw object
		setDrawObject(false);
		
		// Interactable
		setInteractable(true);
	}
	
	// Enter the cave
	public void enter() {
		zone.switchZones(player.getPlayer(), player.getPlayer().getCurrentZone(), toZone, spawnX, spawnY, spawnDirection);
	}
}
