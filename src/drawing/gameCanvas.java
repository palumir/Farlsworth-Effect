package drawing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
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

import units.player;
import units.unit;
import utilities.time;
import utilities.utility;

// The actual canvas the game is drawn on.
public class gameCanvas extends JComponent {
	
	////////////////////
	///// DEFAULTS /////
	////////////////////
	private String DEFAULT_GAME_NAME = "Game";
	
	//////////////////
	///// FIELDS /////
	//////////////////
	// The game canvas. Only one.
	private static gameCanvas gameCanvas;
	
	// How often we update the animation.
	private static int maxFPS = 80; 
	
	// Screen information
	private static int defaultWidth = 500;
	private static int defaultHeight = 500;

	// The thing that performs the tasks every time the timer ticks.
	ActionListener taskPerformer = new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
			utility.updateGame();
			repaint();
		}
	};

	// Constructor. Pretty basic.
	public gameCanvas() {
		
		// Just set the latest GameCanvas to be the global, of course.
		gameCanvas = this;

		// Start the game timer.
		this.setOpaque(true); // we paint every pixel; Java can optimize
		time.initiateTimer(1000/maxFPS, taskPerformer);
		this.setFocusable(true);
		this.setFocusTraversalKeysEnabled(false);
		requestFocus(); 
		
		this.addComponentListener(new ComponentAdapter() {
	        public void componentResized(ComponentEvent evt) {
	            Component c = (Component)evt.getSource();
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
		frame.setSize(getDefaultWidth(), getDefaultHeight());
		frame.setContentPane(getGameCanvas());
		frame.setVisible(true);
		frame.setResizable(false);
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
		
		// Paint the fog TODO: not done
		//fog.paintFog(g2);
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

	public static void setDefaultWidth(int defaultWidth) {
		gameCanvas.defaultWidth = defaultWidth;
	}

	public static int getDefaultHeight() {
		return defaultHeight;
	}

	public static void setDefaultHeight(int defaultHeight) {
		gameCanvas.defaultHeight = defaultHeight;
	}

	public static int getFPS() {
		return maxFPS;
	}

	public static void setFPS(int FPS) {
		maxFPS = FPS;
	}

}