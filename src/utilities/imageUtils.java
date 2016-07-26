package utilities;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;

public class imageUtils {
	
	// SOURCE: http://stackoverflow.com/questions/665406/how-to-make-a-color-transparent-in-a-bufferedimage-and-save-as-png
	
	// Convert Image to BufferedImage
    private static BufferedImage imageToBufferedImage(Image image) {

    	BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
    	Graphics2D g2 = bufferedImage.createGraphics();
    	g2.drawImage(image, 0, 0, null);
    	g2.dispose();

    	return bufferedImage;
    }
    
	// Draw a heart.
	public static void drawHeart(Graphics g, int drawAtX, int drawAtY, int drawWidth, int drawHeight) {
		int triangleXLeft = drawAtX - 2*drawWidth/24;
		int triangleXRight = drawAtX + drawWidth + 2*drawWidth/20;
		int[] triangleX = {
				triangleXLeft,
				triangleXRight,
				(triangleXLeft + triangleXRight)/2};
    	int[] triangleY = { 
    			drawAtY + drawHeight - 2*drawHeight/3, 
    			drawAtY + drawHeight - 2*drawHeight/3, 
    			drawAtY + drawHeight };
	    g.fillOval(
	    		drawAtX - drawWidth/12,
	    		drawAtY, 
	    		drawWidth/2 + drawWidth/6, 
	    		drawHeight/2); 
	    g.fillOval(
	    		drawAtX + drawWidth/2 - drawWidth/12,
	    		drawAtY,
	    		drawWidth/2 + drawWidth/6,
	    		drawHeight/2);
	    g.fillPolygon(triangleX, triangleY, triangleX.length);
	}

    // Make an certain color in a BufferedImage transparent.
    public static BufferedImage makeColorTransparent(BufferedImage im, final Color color) {
    	ImageFilter filter = new RGBImageFilter() {

    		// the color we are looking for... Alpha bits are set to opaque
    		public int markerRGB = color.getRGB() | 0xFF000000;

    		public final int filterRGB(int x, int y, int rgb) {
    			if ((rgb | 0xFF000000) == markerRGB) {
    				// Mark the alpha bits as zero - transparent
    				return 0x00FFFFFF & rgb;
    			} else {
    				// nothing to do
    				return rgb;
    			}
    		}
    	};

    	ImageProducer ip = new FilteredImageSource(im.getSource(), filter);
    	return imageToBufferedImage(Toolkit.getDefaultToolkit().createImage(ip));
    }
    
    public static BufferedImage replaceWithClear(BufferedImage image, int target) {
    	int preferred = Color.TRANSLUCENT; //AlphaComposite.CLEAR;
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage newImage = new BufferedImage(width, height, image.getType());
        int color;

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                color = image.getRGB(i, j);
                if (color == target) {
                    newImage.setRGB(i, j, preferred);
                }
                else {
                    newImage.setRGB(i, j, color);
                }
            }
        }

        return newImage;
    }
}