package units.developer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;

import UI.tooltipString;
import drawing.drawnObject;
import units.unit;
import units.unitCommand;
import units.unitCommands.commandIndicator;
import units.unitCommands.commandList;
import units.unitCommands.positionedCommand;
import units.unitCommands.positionedMovementCommand;
import units.unitCommands.commands.waitCommand;
import utilities.imageUtils;
import utilities.userMouseTracker;

public class unitCommandsAndHighlight {
	
	// Highlight box variables
	private static Color DEFAULT_HIGHLIGHT_COLOR = Color.green;
	
	// Draw the commands as different colors.
	static ArrayList<String> knownCommands;
	public static ArrayList<Color> possibleColors = new ArrayList<Color>() {{
		add(Color.yellow);
		add(Color.cyan);
		add(Color.blue);
		add(Color.orange);
		add(Color.pink);
		add(Color.gray);
		add(Color.green);
		add(Color.white);
		}};
	
	// Draw selected units commands
	public static void drawUnitCommands(Graphics g, ArrayList<drawnObject> selectedThings) {
		
		// If we are selecting at least one unit.
		if(selectedThings != null && selectedThings.size() > 0) {
			
			// Go through the units and draw their commands.
			for(int i = 0; i < selectedThings.size(); i++) {
				
				if(selectedThings.get(i) instanceof unit) {
					unit u = (unit)selectedThings.get(i);
					 
					// Draw commands
					if(u.getRepeatCommands() != null && u.getRepeatCommands().size() > 0) {
						
						for(int j = 0; j < u.getRepeatCommands().size(); j++) {
							
							unitCommand command = u.getRepeatCommands().get(j);
							
							// Initialize if we haven't.
							if(knownCommands == null) {
								knownCommands = new ArrayList<String>();
							}
							
							// If it's not a known command, add it.
							if(!knownCommands.contains(command.getName())) {
								knownCommands.add(command.getName());
							}
							
							// Get the command color.
							Color c = possibleColors.get(knownCommands.indexOf(command.getName()));
							
							// For commands that have an x and y coordinate
							if(command instanceof positionedMovementCommand) {
								
								// Cast
								positionedMovementCommand p = (positionedMovementCommand)command;
								
								// Get the draw position
								Point inGamePointP = userMouseTracker.toDrawPos(new Point((int)p.getX(),(int) p.getY()));
								
								// Draw from the spawn position to first command.
								if(j == 0) {
									
									g.setColor(c);
									
									// Get previous command point
									positionedCommand prevCommand = u.getPreviousPosCommand(u.getRepeatCommands().size() - 1);
									
									Point inGamePointU = userMouseTracker.toDrawPos(new Point((int)prevCommand.getX(), (int)prevCommand.getY()));
									
									// Draw arrow
									imageUtils.drawArrow(g,(int)inGamePointU.getX(), (int)inGamePointU.getY(), (int)inGamePointP.getX(),(int)inGamePointP.getY());
									
								}
								
								// Else draw from the previous positioned command
								else {
									
									// Get previous command point
									positionedCommand prevCommand = u.getPreviousPosCommand(j-1);
									
									Point inGamePointPrev = userMouseTracker.toDrawPos(new Point((int)prevCommand.getX(),(int) prevCommand.getY()));
									
									g.setColor(c);
									imageUtils.drawArrow(g,(int)inGamePointPrev.getX(),(int)inGamePointPrev.getY(), (int)inGamePointP.getX(),(int)inGamePointP.getY());
									
								}
								
							}
							
							// Other commands
							else {
							}
							
						}
						
					}
					
				}
				
			}
			
		}
	}
	
	// Highlight box
	public static void drawHighLightBox(Graphics g, boolean selecting, Point leftClickStartPoint, Point lastMousePos) {
		
		// Draw the box.
		if(selecting) {
			
			Rectangle rect= new Rectangle(userMouseTracker.toDrawPos(leftClickStartPoint));
			rect.add(userMouseTracker.toDrawPos(lastMousePos));

			g.setColor(DEFAULT_HIGHLIGHT_COLOR);
			g.drawRect(rect.x, rect.y, rect.width, rect.height);
			
		}
	}
	
	
	// Add unit command to selected units
	public static void addUnitCommandToSelectedUnits(unitCommand c, ArrayList<drawnObject> selectedThings) {
		if(selectedThings != null) {
			for(int i = 0; i < selectedThings.size(); i++) {
				if(selectedThings.get(i) instanceof unit) {
					if(((unit)selectedThings.get(i)).getRepeatCommands() == null) ((unit)selectedThings.get(i)).setRepeatCommands(new commandList());
					((unit)selectedThings.get(i)).getRepeatCommands().add(c);
					
					// Draw the commands
					if(developer.unitCommands == null) unitCommandsAndHighlight.createUnitCommandsText(selectedThings, null);
					else {
						
						// Initialize if we haven't.
						if(knownCommands == null) {
							knownCommands = new ArrayList<String>();
						}
						
						// If it's not a known command, add it.
						if(!knownCommands.contains(c.getName())) {
							knownCommands.add(c.getName());
						}
						
						commandIndicator indicator = new commandIndicator(c, (unit)selectedThings.get(i));
						developer.unitCommands.add(indicator);
						Color color = possibleColors.get(knownCommands.indexOf(c.getName()));
						indicator.setColor(color);
					}
				}
			}
			if(selectedThings.size() == 0) {
				tooltipString t = new tooltipString("You must select a unit to add this command to.");
			}
		}
		if(selectedThings == null) {
			tooltipString t = new tooltipString("You must select a unit to add this command to.");
		}
	}
	
	// Draw the text for unit commands
	public static void createUnitCommandsText(ArrayList<drawnObject> d, ArrayList<drawnObject> selectedThings) {
		
		// If we are selecting at least one unit.
		if(d != null && d.size() > 0) {
			
			// Go through the units and draw their commands.
			for(int i = 0; i < d.size(); i++) {
				
				// Only draw for units that aren't already drawn.
				if(d.get(i) instanceof unit && (selectedThings == null || !selectedThings.contains(d.get(i)))) {
					unit u = (unit)d.get(i);
					 
					// Draw commands
					if(u.getRepeatCommands() != null && u.getRepeatCommands().size() > 0) {
						
						for(int j = 0; j < u.getRepeatCommands().size(); j++) {
							
							unitCommand command = u.getRepeatCommands().get(j);
							
							// Initialize if we haven't.
							if(knownCommands == null) {
								knownCommands = new ArrayList<String>();
							}
							
							// If it's not a known command, add it.
							if(!knownCommands.contains(command.getName())) {
								knownCommands.add(command.getName());
							}
							
							// Get the command color.
							Color c = possibleColors.get(knownCommands.indexOf(command.getName()));
								
							if(developer.unitCommands==null) developer.unitCommands = new ArrayList<commandIndicator>();
							commandIndicator t = new commandIndicator(command, u);
							t.setColor(c);
							developer.unitCommands.add(t);
							
						}
						
					}
					
				}
				
			}
			
		}
	}
				
	
}