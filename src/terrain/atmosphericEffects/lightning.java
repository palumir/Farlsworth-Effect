package terrain.atmosphericEffects;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import drawing.gameCanvas;
import utilities.time;

public class lightning extends atmosphericEffect {

	long screenWhiteStart = 0;
	float screenWhiteFor = 0;
	Color lightningColor;
	
	public static lightning currentLightning = null;
	
	public lightning(float f, Color c) {
		super();
		
		// Set color
		lightningColor = c;
		
		// Lightning exists until.
		screenWhiteStart = time.getTime();
		screenWhiteFor = f;
		
		// Be above fog.
		setZ(-1);
		
	}
	
	// Paint the fog
	@Override
	public void drawObject(Graphics g) {
			
			// Make the screen white for lightning
			if(time.getTime() - screenWhiteStart < screenWhiteFor*1000) {
				// Create an image the size of the screen to draw on.
				BufferedImage img = new BufferedImage(gameCanvas.getActualWidth(),gameCanvas.getActualHeight(), BufferedImage.TYPE_INT_ARGB);
				Graphics2D g2 = img.createGraphics();
				
				// Draw the fog.
				float alpha = 0.3f;
				g2.setComposite(AlphaComposite.Src);
				Color black = new Color(lightningColor.getRed()/255f, lightningColor.getGreen()/255f, lightningColor.getBlue()/255f, alpha); //Black 
				g2.setComposite(AlphaComposite.Src);
				g2.setPaint(black);
				g2.fillRect(0,0,gameCanvas.getActualWidth(),gameCanvas.getActualHeight());
				g.drawImage(img,0,0,null);
	
			}
			else {
				this.destroy();
			}
	}
	
	
}