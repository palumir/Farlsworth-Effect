package drawing;

import java.awt.Color;
import java.awt.Graphics;

public class shape extends drawnObject {
	
	// Fields
	private String type; // "circle", "square", or "triangle"
	private Color color = Color.black;
	
	// Constructor
	public shape(String newType,
				int newX,
				int newY,
				int newHeight,
				int newWidth) {
		super(newX, newY, newHeight, newWidth);
		type = newType;
	}
	
	// Draw shape.
	@Override
	public void drawObject(Graphics g) {
		if(type == "square") {
			g.setColor(color);
			g.drawRect(drawX,
					   drawY, 
				       width, 
				       height);
		}
	}
	
	/////////////////////////
	// Getters and Setters //
	/////////////////////////
	
	public void setColor(Color c) {
		color = c;
	}
}