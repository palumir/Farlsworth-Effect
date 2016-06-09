package terrain.atmosphericEffects;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.Random;

import drawing.gameCanvas;

public class fog {
	
	// Paint the background
	public static void paintFog(Graphics2D g2) {
		BufferedImage img = new BufferedImage(gameCanvas.getDefaultWidth(),gameCanvas.getDefaultHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = img.createGraphics();
		float alpha = 0.5f;
		Color color = new Color(0, 0, 0, alpha); //Black 
		g.setComposite(AlphaComposite.Src);
		g.setPaint(color);
		g.fillRect(0,0,gameCanvas.getDefaultWidth(),gameCanvas.getDefaultHeight());
		g2.drawImage(img,0,0,null);
	}
}