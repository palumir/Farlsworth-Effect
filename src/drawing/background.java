package drawing;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.Random;

public class background {
	// Defaults
	private static Color defaultBackgroundColor = Color.BLACK;
	
	// BufferedImage
	private static BufferedImage gameBackground = null;
	
	// Paint the background
	public static void paintBackground(Graphics2D g2, int width, int height) {
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
		return gameBackground;
	}

	public static void setGameBackground(BufferedImage gameBackground) {
		background.gameBackground = gameBackground;
	}
}