package drawing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import terrain.atmosphericEffects.fog;
import units.player;
import units.unit;
import utilities.time;
import utilities.utility;

// The actual canvas the game is drawn on.
public class gameCanvas extends JComponent {
	
	////////////////////
	///// DEFAULTS /////
	////////////////////
	
	// Screen title.
	private String DEFAULT_GAME_NAME = "Game";
	
	//////////////////
	///// FIELDS /////
	//////////////////
	// The game canvas. Only one.
	private static gameCanvas gameCanvas;
	
	// How often we update the animation.
	private static int maxFPS = 80; 
	
	// Screen information
	private static int DEFAULT_START_WIDTH = 650;
	private static int DEFAULT_START_HEIGHT = 650;
	private static int defaultWidth;
	private static int defaultHeight;
	private static int actualWidth;
	private static int actualHeight;
	private static float scaleX = 1f;
	private static float scaleY = 1f;
	
	// Frame size
	//private static boolean changeFrameSize = false;

	// The thing that performs the tasks every time the timer ticks.
	ActionListener drawPerformer = new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
			/*if(changeFrameSize == true) {
				Component c = SwingUtilities.getRoot(gameCanvas);
			    float W = (float)defaultWidth;  
			    float H = (float)defaultHeight;  
			    Rectangle b2 = c.getBounds();
			    c.setBounds(b2.x,b2.y,(int)b2.width,(int)(b2.width*H/W));
			    changeFrameSize = false;
			}*/
			repaint();
		}
	};
	
	ActionListener gamePerformer = new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
			/*if(changeFrameSize == true) {
				Component c = SwingUtilities.getRoot(gameCanvas);
			    float W = (float)defaultWidth;  
			    float H = (float)defaultHeight;  
			    Rectangle b2 = c.getBounds();
			    c.setBounds(b2.x,b2.y,(int)b2.width,(int)(b2.width*H/W));
			    changeFrameSize = false;
			}*/
			utility.updateGame();
		}
	};

	// Constructor. Pretty basic.
	public gameCanvas() {
		
		// Just set the latest GameCanvas to be the global, of course.
		gameCanvas = this;

		// Start the game timer.
		this.setOpaque(true); // we paint every pixel; Java can optimize
		time.initiateGameTimer(12, gamePerformer);
		time.initiateDrawTimer(1000/maxFPS, drawPerformer);
		this.setFocusable(true);
		this.setFocusTraversalKeysEnabled(false);
		requestFocus(); 
		
		this.addComponentListener(new ComponentAdapter() {
			@Override
	        public void componentResized(ComponentEvent evt) {
	            Component c = SwingUtilities.getRoot(gameCanvas);
				setActualWidth(gameCanvas.getWidth());
				setActualHeight(gameCanvas.getHeight());
				if(getActualWidth()/(float)defaultWidth != Float.POSITIVE_INFINITY) setScaleX(getActualWidth()/(float)defaultWidth);
				if(getActualHeight()/(float)defaultHeight != Float.POSITIVE_INFINITY) setScaleY(getActualHeight()/(float)defaultHeight);
				
				// Change the font.
				drawnObject.DEFAULT_FONT = new Font(drawnObject.DEFAULT_FONT_NAME, Font.PLAIN, (int)(drawnObject.DEFAULT_FONT_SIZE*scaleX));
				
	            repaint();
	        }
		});
		
		// Deal with keypresses 
		this.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent k) {
				if(player.getCurrentPlayer()!=null) player.getCurrentPlayer().keyPressed(k);
			}

			@Override
			public void keyReleased(KeyEvent k) {
				if(player.getCurrentPlayer()!=null) player.getCurrentPlayer().keyReleased(k);
			} 

			@Override
			public void keyTyped(KeyEvent k) {
				// Do nothing. We don't care yet.
			}
		});
		
		this.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

		});
		
		// Create the actual game frame on the computer screen.
		JFrame frame = new JFrame(DEFAULT_GAME_NAME);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(DEFAULT_START_WIDTH, DEFAULT_START_HEIGHT);
		frame.setContentPane(getGameCanvas());
		frame.setVisible(true);
	//	frame.setResizable(false);
		
		// Set width.
        defaultWidth = this.getWidth();
        defaultHeight = this.getHeight();
	}

	// Paint the game canvas.
	public void paintComponent(Graphics g) {

		// Set up the g2 object and paint itself.
		Graphics2D g2 = (Graphics2D) g;
		super.paintComponent(g2);

		// Paint the background.
		background.paintBackground(g2, getDefaultWidth(), getDefaultHeight());

		// Paint all drawable things.
		drawnObject.drawObjects(g);
	}

	// ************************************
	// ******   Getters and setters! ******
	// ************************************

	public static gameCanvas getGameCanvas() {
		return gameCanvas;
	}

	public static void setGameCanvas(gameCanvas gameCanvas) {
		gameCanvas.gameCanvas = gameCanvas;
	}

	public static int getDefaultWidth() {
		return defaultWidth;
	}

	public static int getDefaultHeight() {
		return defaultHeight;
	}

	public static int getFPS() {
		return maxFPS;
	}

	public static void setFPS(int FPS) {
		maxFPS = FPS;
	}

	public static int getActualWidth() {
		return actualWidth;
	}

	public static void setActualWidth(int actualWidth) {
		gameCanvas.actualWidth = actualWidth;
	}

	public static int getActualHeight() {
		return actualHeight;
	}

	public static void setActualHeight(int actualHeight) {
		gameCanvas.actualHeight = actualHeight;
	}

	public static float getScaleX() {
		return scaleX;
	}

	public static void setScaleX(float scaleX) {
		gameCanvas.scaleX = scaleX;
	}

	public static float getScaleY() {
		return scaleY;
	}

	public static void setScaleY(float scaleY) {
		gameCanvas.scaleY = scaleY;
	}

}