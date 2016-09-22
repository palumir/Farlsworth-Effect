package drawing.backgrounds;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import drawing.background;
import drawing.gameCanvas;

public class rotatingBackground extends background {
	
	private float degree = 0;
	public rotatingBackground(BufferedImage b) {
		super(b);
	}
	
	// Paint the background
	@Override
	public void paintBackground(Graphics2D g2, int width, int height) {
		AffineTransform identity = new AffineTransform();
		AffineTransform trans = new AffineTransform();
		trans.setTransform(identity);
		trans.rotate(Math.toRadians(degree), gameCanvas.getScaleX()*width/2, gameCanvas.getScaleY()*height/2);
		trans.translate(gameCanvas.getActualWidth()/2 - gameCanvas.getScaleX()*getGameBackground().getWidth()/2,gameCanvas.getActualHeight()/2 - gameCanvas.getScaleY()*getGameBackground().getHeight()/2);
		trans.scale(gameCanvas.getScaleX(), gameCanvas.getScaleY());
		g2.drawImage(getGameBackground(), trans, null);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		degree += 0.05f;
	}
	
}