package units.unitCommands;

import java.awt.Color;
import java.awt.Graphics;

import drawing.drawnObject;
import drawing.gameCanvas;
import units.unit;
import units.unitCommand;

public class commandIndicator extends drawnObject {

	// The command we actually draw.
	private unitCommand command;
	private Color color;
	private unit unit;
	
	// Constructor
	public commandIndicator(unitCommand c, unit u) {
		super(null, c.getName(), 0, 0, gameCanvas.getGameCanvas().getFontMetrics(drawnObject.DEFAULT_FONT).stringWidth(c.getName())+4, gameCanvas.getGameCanvas().getFontMetrics(drawnObject.DEFAULT_FONT).getHeight());
		
		if(c instanceof positionedCommand) {
			setDoubleX((int)((positionedCommand) c).getX());
			setDoubleY((int)((positionedCommand) c).getY());
		}
		
		else {
			
			positionedCommand p = null;
			int x = 0;
			
			// Get previous pos command
			if(u.getRepeatCommands() != null && u.getRepeatCommands().size() > 0) {
				
				// Get the previous command that is positioned
				int newN = u.getRepeatCommands().size()-1;
				if(u.getRepeatCommands().contains(c)) newN = u.getRepeatCommands().indexOf(c);
				for(int n = newN; ; n--) {
					if(x >= u.getRepeatCommands().size()) break;
					if(n < 0) n = u.getRepeatCommands().size() - 1;
					if(u.getRepeatCommands().get(n) instanceof positionedCommand) {
						p = (positionedCommand)u.getRepeatCommands().get(n);
						break;
					}
					x++;
				}
			}
			
			// If we found a command.
			if(p!=null) {
				setDoubleX((int)(p.getX()));
				setDoubleY((int)(p.getY()+(x+1)*15));
			}
			
			// Otherwise.
			else {
				setDoubleX(u.getIntX());
				setDoubleY(u.getIntY());
			}
		}
		
		setUnit(u);
		setCommand(c);
	}

	@Override
	public void setDoubleX(double x) {
		
		// Can only set positionedCommands for now.
		if(getCommand() instanceof positionedCommand) {
			this.doubleX = x;
			((positionedCommand)(getCommand())).setX(x);
		}
		else {
			this.doubleX = x;
		}
	}
	
	@Override
	public void setDoubleY(double y) {
		
		// Can only set positionedCommands for now.
		if(getCommand() instanceof positionedCommand) {
			this.doubleY = y;
			((positionedCommand)(getCommand())).setY(y);
		}
		else {
			this.doubleY = y;
		}
	}

	@Override
	public void drawObject(Graphics g) {
		
		if(getColor() == null) g.setColor(Color.green);
		else g.setColor(getColor());
		if(getCommand()!=null) g.drawString(getCommand().getName(),
				  getDrawX() - (int)(gameCanvas.getScaleX()*gameCanvas.getGameCanvas().getFontMetrics(drawnObject.DEFAULT_FONT).stringWidth(getCommand().getName())/2),
				  getDrawY());
		
		// Show hitbox?
		if(showHitBox) {
			g.setColor(Color.green);
			g.drawRect(getDrawX() - (int)(gameCanvas.getScaleX()*( getWidth()/2 - getHitBoxAdjustmentX())),
					   getDrawY() - (int)(gameCanvas.getScaleY()*( getHeight()/2 - getHitBoxAdjustmentY())), 
					   (int)(gameCanvas.getScaleX()*getWidth()), 
					   (int)(gameCanvas.getScaleY()*getHeight()));
		}

	}

	public unitCommand getCommand() {
		return command;
	}

	public void setCommand(unitCommand command) {
		this.command = command;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public unit getUnit() {
		return unit;
	}

	public void setUnit(unit unit) {
		this.unit = unit;
	}
}

