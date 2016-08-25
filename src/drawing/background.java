package drawing;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

public class background {
	// Defaults
	private static Color defaultBackgroundColor = Color.BLACK;
	
	// BufferedImage
	private BufferedImage backgroundImage = null;
	
	// Current background
	public static background currentBackground = null;
	
	// Paint the background
	public void paintBackground(Graphics2D g2, int width, int height) {
		if(getGameBackground() == null) {
			g2.setColor(getColor());
			g2.fillRect(0, 0, (int)(gameCanvas.getScaleX()*width), (int)(gameCanvas.getScaleY()*height));
		}
		else {
			g2.setColor(getColor());
			g2.fillRect(0, 0, (int)(gameCanvas.getScaleX()*width), (int)(gameCanvas.getScaleY()*height));
			g2.drawImage(getGameBackground(), 0, 0, (int)(gameCanvas.getScaleX()*width), (int)(gameCanvas.getScaleY()*height), null);
		}
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
	}
	
	// Make new background
	public background(BufferedImage b) {
		backgroundImage = b;
		currentBackground = this;
	}
	
	// ************************************
	// ******   Getters and setters! ******
	// ************************************
	
	public static void setBackground(Color c) {
		setColor(c);
	}

	public static Color getColor() {
		return defaultBackgroundColor;
	}

	public static void setColor(Color c) {
		defaultBackgroundColor = c;
	}

	public static BufferedImage getGameBackground() {
		return currentBackground.backgroundImage;
	}

	public static void setGameBackground(BufferedImage gameBackground) {
		new background(gameBackground);
	}
	
	// Update background
	public void update() {
		
	}
}