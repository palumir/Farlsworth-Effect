package units.developer;

import java.awt.Point;
import java.awt.Rectangle;
import java.lang.reflect.Constructor;

import UI.tooltipString;
import drawing.drawnObject;
import terrain.chunk;
import terrain.groundTile;
import units.unitCommand;
import units.unitCommands.positionedCommand;
import utilities.utility;

public class spawningThings {
	
	// Spawn the thing and output.
	public static void spawnThing(String editorType, String currentObjectClass, Point lastMousePos, Point leftClickStartPoint, boolean selecting) {
		
		if(editorType.equals("Ground Tile")) {
			spawnGroundTile(currentObjectClass, lastMousePos, leftClickStartPoint, selecting);
		}
		
		if(editorType.equals("Chunk")) {
			spawnChunk(currentObjectClass, lastMousePos, leftClickStartPoint, selecting);
		}
		
		if(editorType.equals("Unit") && currentObjectClass != null && currentObjectClass.contains("units.unitCommands.")) {
			spawnUnitCommand(currentObjectClass, lastMousePos);
		}
		else if(editorType.equals("Unit")) {
			spawnUnit(currentObjectClass, lastMousePos, leftClickStartPoint, selecting);
		}
	}
	
	// Spawn ground tile.
	public static void spawnGroundTile(String currentObjectClass, Point lastMousePos, Point leftClickStartPoint, boolean selecting) {
		
		// If we don't have a class, give an error
		if(currentObjectClass == null) {
			new tooltipString("You need to select a ground tile type to place.");
		}
		// If selecting, make groundtiles in box.
		else if(selecting) {
			
			// Make rectangle.
			Rectangle rect= new Rectangle(leftClickStartPoint);
			rect.add(lastMousePos);
			
			// Make groundtiles in box.
			for(int i = (int) rect.getX(); i < rect.getX() + rect.getWidth(); i += groundTile.DEFAULT_TILE_WIDTH) {
				for(int j = (int) rect.getY(); j < rect.getY() + rect.getHeight(); j += groundTile.DEFAULT_TILE_HEIGHT) {
					try {
						Class<?> clazz = Class.forName(currentObjectClass);
						Constructor<?> ctor = clazz.getConstructor(int.class, int.class, int.class);
						Object object = ctor.newInstance(new Object[] { i,
								j,
								0});
					}
					catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
			
			
		}
		
		// Otherwise, make one ground tile on mouse.
		else {
			try {
				Class<?> clazz = Class.forName(currentObjectClass);
				Constructor<?> ctor = clazz.getConstructor(int.class, int.class, int.class);
				Object object = ctor.newInstance(new Object[] { (int)(lastMousePos.getX() - groundTile.DEFAULT_TILE_WIDTH/2),
						(int)(lastMousePos.getY() - groundTile.DEFAULT_TILE_HEIGHT/2),
						0});
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	// Spawn chunk.
	public static void spawnChunk(String currentObjectClass, Point lastMousePos, Point leftClickStartPoint, boolean selecting) {
		
		// If we don't have a class, give an error
		if(currentObjectClass == null) {
			new tooltipString("You need to select a chunk type to place.");
		}
		// If selecting, make groundtiles in box.
		else if(selecting) {
			
			// Make rectangle.
			Rectangle rect= new Rectangle(leftClickStartPoint);
			rect.add(lastMousePos);
			
			// Height
			int objHeight = 0;
			int objWidth = 0;
			
			// Make chunks in box.
			for(int i = (int) rect.getX(); i < rect.getX() + rect.getWidth(); ) {
				for(int j = (int) rect.getY(); j < rect.getY() + rect.getHeight(); ) {
					try {
						Class<?> clazz = Class.forName(currentObjectClass);
						Constructor<?> ctor = clazz.getConstructor(int.class, int.class, int.class);
						Object object = ctor.newInstance(new Object[] { i,
								j,
								0});
						drawnObject d = (drawnObject)(object);
						if(objHeight == 0) objHeight = d.getHeight();
						if(objWidth == 0) objWidth = d.getWidth();
						j += objHeight;
					}
					catch(Exception e) {
						e.printStackTrace();
					}
				}
				i += objWidth;
			}
		}
		
		// Otherwise, make one chunk tile on mouse.
		else {
			try {
				Class<?> clazz = Class.forName(currentObjectClass);
				Constructor<?> ctor = clazz.getConstructor(int.class, int.class, int.class);
				Object object = ctor.newInstance(new Object[] { (int)(lastMousePos.getX()),
						(int)(lastMousePos.getY()),
						0});
				drawnObject d = (drawnObject)(object);
				int numI = d.getObjectSpriteSheet().getAnimation(0).size();
				int randomI = utility.RNG.nextInt(numI);
				
				// Set the variation
				((chunk)(d)).setVariationI(randomI);
				((chunk)(d)).setChunkImage((d.getObjectSpriteSheet().getAnimation(0).get(randomI)));
				
				d.setDoubleX(d.getDoubleX() - d.getWidth()/2);
				d.setDoubleY(d.getDoubleY() - d.getHeight()/2);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	// Spawn unit
	public static void spawnUnit(String currentObjectClass, Point lastMousePos, Point leftClickStartPoint, boolean selecting) {
		
		// If we don't have a class, give an error
		if(currentObjectClass == null) {
			new tooltipString("You need to select a unit type to place.");
		}
		// If selecting, make groundtiles in box.
		else if(selecting) {
			
			// Make rectangle.
			Rectangle rect= new Rectangle(leftClickStartPoint);
			rect.add(lastMousePos);
			
			// Height
			int objHeight = 0;
			int objWidth = 0;
			
			// Make chunks in box.
			for(int i = (int) rect.getX(); i < rect.getX() + rect.getWidth(); ) {
				for(int j = (int) rect.getY(); j < rect.getY() + rect.getHeight(); ) {
					try {
						Class<?> clazz = Class.forName(currentObjectClass);
						Constructor<?> ctor = clazz.getConstructor(int.class, int.class);
						Object object = ctor.newInstance(new Object[] { i,
								j});
						drawnObject d = (drawnObject)(object);
						if(objHeight == 0) objHeight = d.getHeight();
						if(objWidth == 0) objWidth = d.getWidth();
						j += objHeight;
					}
					catch(Exception e) {
						e.printStackTrace();
					}
				}
				i += objWidth;
			}
		}
		
		// Otherwise, make one chunk tile on mouse.
		else {
			try {
				Class<?> clazz = Class.forName(currentObjectClass);
				Constructor<?> ctor = clazz.getConstructor(int.class, int.class);
				Object object = ctor.newInstance(new Object[] { (int)(lastMousePos.getX()),
						(int)(lastMousePos.getY())});
				drawnObject d = (drawnObject)(object);
				d.setDoubleX(d.getDoubleX() - d.getWidth()/2);
				d.setDoubleY(d.getDoubleY() - d.getHeight()/2);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	// Spawn unit command
	public static void spawnUnitCommand(String currentObjectClass, Point lastMousePos) {
		
		// If we don't have a class, give an error
		if(currentObjectClass == null) {
			new tooltipString("You need to select a unit command type to place.");
		}
		try {
			Class<?> clazz = Class.forName(currentObjectClass);
			Constructor<?> ctor = clazz.getConstructor(double.class, double.class);
			Object object = ctor.newInstance(new Object[] { 
					0,
					0});
			
			// If we are a positionedCommand, move to mouse position.
			if(object instanceof positionedCommand) {
				positionedCommand p = (positionedCommand)object;
				p.setX(lastMousePos.getX());
				p.setY(lastMousePos.getY());
			}
			
			unitCommand d = (unitCommand)(object);
			unitCommandsAndHighlight.addUnitCommandToSelectedUnits(d,developer.selectedThings);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
}