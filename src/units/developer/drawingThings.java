package units.developer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;

import drawing.drawnObject;
import units.unit;
import units.unitCommand;
import units.unitCommands.commandIndicator;
import units.unitCommands.positionedCommand;
import utilities.imageUtils;

public class drawingThings {
	
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
							if(command instanceof positionedCommand) {
								
								// Cast
								positionedCommand p = (positionedCommand)command;
								
								// Get the draw position
								Point inGamePointP = drawnObject.toDrawPos(new Point((int)p.getX(),(int) p.getY()));
								
								// Draw from the spawn position to first command.
								if(j == 0) {
									
									g.setColor(c);
									
									// Get previous command point
									positionedCommand prevCommand = u.getPreviousPosCommand(u.getRepeatCommands().size() - 1);
									
									Point inGamePointU = drawnObject.toDrawPos(new Point((int)prevCommand.getX(), (int)prevCommand.getY()));
									
									// Draw arrow
									imageUtils.drawArrow(g,(int)inGamePointU.getX(), (int)inGamePointU.getY(), (int)inGamePointP.getX(),(int)inGamePointP.getY());
									
								}
								
								// Else draw from the previous positioned command
								else {
									
									// Get previous command point
									positionedCommand prevCommand = u.getPreviousPosCommand(j-1);
									
									Point inGamePointPrev = drawnObject.toDrawPos(new Point((int)prevCommand.getX(),(int) prevCommand.getY()));
									
									g.setColor(c);
									imageUtils.drawArrow(g,(int)inGamePointPrev.getX(),(int)inGamePointPrev.getY(), (int)inGamePointP.getX(),(int)inGamePointP.getY());
									
								}
								
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
			
			Rectangle rect= new Rectangle(drawnObject.toDrawPos(leftClickStartPoint));
			rect.add(drawnObject.toDrawPos(lastMousePos));

			g.setColor(DEFAULT_HIGHLIGHT_COLOR);
			g.drawRect(rect.x, rect.y, rect.width, rect.height);
			
		}
	}
	
	// Draw the text for unit commands
	public static void createUnitCommandsText(ArrayList<drawnObject> selectedThings) {
		
		if(developer.unitCommands == null || developer.unitCommands.size() == 0) {
			
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
								if(command instanceof positionedCommand) {
									
									// Cast
									positionedCommand p = (positionedCommand)command;
										
									if(developer.unitCommands==null) developer.unitCommands = new ArrayList<commandIndicator>();
									commandIndicator t = new commandIndicator(p, u);
									t.setColor(c);
									developer.unitCommands.add(t);
	
								}
								
							}
							
						}
						
					}
					
				}
				
			}
		}
				
	}
	
}