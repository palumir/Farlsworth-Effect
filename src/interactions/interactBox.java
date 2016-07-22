package interactions;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import drawing.drawnObject;
import drawing.gameCanvas;
import drawing.spriteSheet;
import drawing.userInterface.interfaceObject;
import sounds.sound;

public class interactBox extends interfaceObject  {
	
	///////////////////////
	////// GLOBALS ////////
	///////////////////////
	
	// Background.
    public static BufferedImage background = spriteSheet.getSpriteFromFilePath("images/interface/interactBox.png");
    public static BufferedImage arrow = spriteSheet.getSpriteFromFilePath("images/interface/arrow.png");
    
    // Color
	private Color DEFAULT_TEXT_COLOR = Color.black;
	private Color DEFAULT_SELECTED_COLOR = new Color(100,48,38);
	
	// Position
	private static int DEFAULT_X = (int) (gameCanvas.getDefaultWidth()*0.5f) - background.getWidth()/2;
	private static int DEFAULT_Y = (int) (gameCanvas.getDefaultHeight()*0.80f) - background.getHeight()/2;
	
	// Text font.
	private static Font DEFAULT_FONT = null;
	private static Font DEFAULT_FONT_TITLE = null;
	
	///////////////////////
	////// FIELDS /////////
	///////////////////////
    
    // Text to display and the color of the text.
	private String whoIsTalking;
	private boolean isUnit = false;
	private textSeries theText;
	private String displayedText = "";
	private int displayIterator = 0;
	private float DEFAULT_DISPLAY_FOR = 2; // Frames
	
	// Selected button.
	private int selectedButton = 0;
	
	// Display
	private boolean buttonMode = false;
	private boolean textMode = false;
	private boolean displayOn = false;
	private boolean unescapable = false;
	private boolean locked = false;
	private static interactBox currentDisplay = null;
	
	// Sounds.
	private static String UIMove = "sounds/effects/player/UI/UIMove.wav";
	private static String typing = "sounds/effects/player/UI/typing.wav";
	
	///////////////
	/// METHODS ///
	///////////////

	// Constructor
	public interactBox(textSeries newText, String newWhoIsTalking, boolean newIsUnit) {
		super(null, DEFAULT_X, DEFAULT_Y, background.getWidth(), background.getHeight());	
		
		// Is unit.
		isUnit = newIsUnit;
		
		// Set fields.
		if(newText.getButtonText() != null) buttonMode = true;
		else textMode = true;
		whoIsTalking = newWhoIsTalking;
		setTheText(newText);
	}
	
	// Constructor
	public interactBox(textSeries newText, String newWhoIsTalking) {
		super(null, DEFAULT_X, DEFAULT_Y, background.getWidth(), background.getHeight());	
		
		// Is unit.
		isUnit = false;
		
		// Set fields.
		if(newText.getButtonText() != null) buttonMode = true;
		else textMode = true;
		whoIsTalking = newWhoIsTalking;
		setTheText(newText);
	}
	
	// Draw the unit. 
	@Override
	public void drawObject(Graphics g) {
		if(isDisplayOn()) {
			
			// Set default font.
			Font currentFont = g.getFont();
			DEFAULT_FONT = currentFont.deriveFont(currentFont.getSize() * 1.4F);
			DEFAULT_FONT_TITLE = currentFont.deriveFont(currentFont.getSize() * 1.5F);
			
			// Set color.
			g.setColor(DEFAULT_TEXT_COLOR);
			
			// Background
			g.drawImage(background, (int)(gameCanvas.getScaleX()*getIntX()), 
					(int)(gameCanvas.getScaleY()*getIntY()),
					(int)(gameCanvas.getScaleX()*background.getWidth()),
					(int)(gameCanvas.getScaleY()*background.getHeight())
					,null);
			
			// Text
			if(textMode) {
	
				
				if(isUnit) {
					// Set font.
					g.setFont(DEFAULT_FONT_TITLE);
					
					// Display the name of the person or thing talking/interacting
					String theTalker = getTheText().getTalker();
					if(theTalker == null) theTalker = whoIsTalking;
					g.drawString(theTalker,
							(int)(gameCanvas.getScaleX()*getIntX()) + 
							(int)(gameCanvas.getScaleX()*background.getWidth()/2) - 
							g.getFontMetrics().stringWidth(theTalker)/2,
							(int)(gameCanvas.getScaleY()*getIntY()) + 
							(int)(gameCanvas.getScaleY()*background.getHeight()/5) + 
							(int)(gameCanvas.getScaleY()*4));
				}
				
				// Set font.
				g.setFont(DEFAULT_FONT);
				
				// Increase displayedText
				if(displayedText.length() != getTheText().getTextOnPress().length()) {
					displayIterator++;
					if(displayIterator == DEFAULT_DISPLAY_FOR) {
						displayIterator = 0;
						displayedText += getTheText().getTextOnPress().charAt(displayedText.length());
						sound s = new sound(typing);
						s.setVolume(0.9f);
						s.start();
					}
				}
				
				// Draw the text.
				g.drawString(displayedText,
						(int)(gameCanvas.getScaleX()*getIntX()) + (int)(gameCanvas.getScaleX()*background.getWidth()/2) - g.getFontMetrics().stringWidth(getTheText().getTextOnPress())/2,
					   (int)(gameCanvas.getScaleY()*(getIntY() + background.getHeight()/2 + 4)));
			}
			
			// Button
			if(buttonMode) {
				
				if(isUnit) {
					// Set font.
					g.setFont(DEFAULT_FONT_TITLE);
					
					// Display the name of the person or thing talking/interacting
					g.drawString(whoIsTalking,
							(int)(gameCanvas.getScaleX()*getIntX()) + 
							(int)(gameCanvas.getScaleX()*background.getWidth()/2) - 
							g.getFontMetrics().stringWidth(whoIsTalking)/2,
							(int)(gameCanvas.getScaleY()*getIntY()) + 
							(int)(gameCanvas.getScaleY()*background.getHeight()/5) + 
							(int)(gameCanvas.getScaleY()*4));
				}
				
				
				// Set font.
				g.setFont(DEFAULT_FONT);
				float percent = 1;
				if(getTheText().getChildren().size()!=0) percent = 1f/((float)getTheText().getChildren().size() + 1);
					for(int i = 0; i < getTheText().getChildren().size(); i++) {
						
						// Get text.
						String buttText = getTheText().getChildren().get(i).getButtonText();
						
						if(i == selectedButton) {
							g.setColor(DEFAULT_SELECTED_COLOR);
							
							// Draw arrow
							g.drawImage(arrow, (int) ((int)(gameCanvas.getScaleX()*getIntX())
									+ (int)(gameCanvas.getScaleX()*((2*(i+1))*percent*background.getWidth()/2 - 2 - arrow.getWidth()))
									- g.getFontMetrics().stringWidth(buttText)/2),
									(int)(gameCanvas.getScaleY()*(getIntY() + background.getHeight()/2 - 2 - arrow.getHeight()/2)), 
									(int)(gameCanvas.getScaleX()*arrow.getWidth()), 
									(int)(gameCanvas.getScaleY()*arrow.getHeight()), null);
						}
						else {
							g.setColor(DEFAULT_TEXT_COLOR);
						}
						
						// Draw text
						g.drawString(buttText,
							   (int) ((int)(gameCanvas.getScaleX()*(getIntX() + (2*(i+1))*percent*background.getWidth()/2)) - g.getFontMetrics().stringWidth(buttText)/2),
							   (int)(gameCanvas.getScaleY()*(getIntY() + background.getHeight()/2 + 4)));
					}
			}
		}
	}
	
	// Initiate
	public static void initiate() {
		
		// Untoggle current display
		if(currentDisplay != null && currentDisplay.displayOn) {
			currentDisplay.toggleDisplay();
			currentDisplay = null;
		}
	}
	
	// Display on.
	public void toggleDisplay() {
		setDisplayOn(!isDisplayOn());
		if(getCurrentDisplay() != this) {
			setCurrentDisplay(this);
		}
		else {
			setCurrentDisplay(null);
		}
	}
	
	// Select
	public void select() {
		
		// If we are speeding up text.
		// If we aren't done showing text, show it all.
		if(textMode && displayedText.length() != getTheText().getTextOnPress().length()) {
			displayedText = getTheText().getTextOnPress();
			sound s = new sound(typing);
			s.setVolume(1f);
			s.start();
		}
		
		// If there's children.
		else if(!isLocked() && theText.getChildren() != null && theText.getChildren().size() > 0) {
			goToNext();
		}
		
		// It's the end of an interaction. Exit. Caller should deal with the end.
		else if(theText.isEnd()) {
			if(!unescapable) toggleDisplay();
		}
	}
	

	public void goToNext(int i) {
		// If we are currently in text mode.
		if(textMode) {
			
			// If there's only one thing, assume it's just more text.
			if(theText.getChildren().size() == 1 && theText.getChildren().get(0).getButtonText() == null) {
				selectedButton = 0;
				theText = theText.getChildren().get(0);
			}
		
			// Otherwise, there will be buttons to select from. Go to button mode.
			else {
				buttonMode = true;
				textMode = false;
			}
			
			// Reset the text.
			displayedText = "";
		}
		
		// If we're in button mode.
		else if(buttonMode) {
			
			// Select the button.
			theText = theText.getChildren().get(i);
			buttonMode = false;
			textMode = true;
			selectedButton = 0;
		}
	}
	
	// Go to next
	public void goToNext() {
		// If we are currently in text mode.
		if(textMode) {
			
			// If there's only one thing, assume it's just more text.
			if(theText.getChildren().size() == 1 && theText.getChildren().get(0).getButtonText() == null) {
				selectedButton = 0;
				theText = theText.getChildren().get(0);
			}
		
			// Otherwise, there will be buttons to select from. Go to button mode.
			else {
				buttonMode = true;
				textMode = false;
			}
			
			// Reset the text.
			displayedText = "";
		}
		
		// If we're in button mode.
		else if(buttonMode) {
			
			// Select the button.
			theText = theText.getChildren().get(selectedButton);
			buttonMode = false;
			textMode = true;
			selectedButton = 0;
		}
	}
	
	// Move select
	public void moveSelect(String direction) {
		
		if(buttonMode) {
			// Move select right.
			if(direction=="right") {
				if(selectedButton + 1 < theText.getChildren().size()) {
					selectedButton++;
					sound s = new sound(UIMove);
					s.start();
				}
			}
			
			// Move select left.
			if(direction=="left") {
				if(selectedButton - 1 >= 0) {
					selectedButton--;
					sound s = new sound(UIMove);
					s.start();
				}
			}
		}
	}
	
	// Respond to key press.
	public void respondToKeyPress(KeyEvent k) {
		
		// Player presses esc (inventory) key.
		if(k.getKeyCode() == KeyEvent.VK_ESCAPE) { 
			if(!isLocked() && !unescapable) toggleDisplay();
		}
		
		// Player presses left key.
		if(k.getKeyCode() == KeyEvent.VK_A) { 
			if(!isLocked()) moveSelect("left");
		}
		
		// Player presses right key.
		if(k.getKeyCode() == KeyEvent.VK_D) { 
			if(!isLocked()) moveSelect("right");
		}
		
		// Player presses up key
		if(k.getKeyCode() == KeyEvent.VK_W) { 
			//moveSelect("up");
		}
		
		// Player presses down key
		if(k.getKeyCode() == KeyEvent.VK_S) { 
			//moveSelect("down");
		}
		
		// Player presses e key.
		if(k.getKeyCode() == KeyEvent.VK_SPACE || k.getKeyCode() == KeyEvent.VK_ENTER || k.getKeyCode() == KeyEvent.VK_E) { 
			select();
		}
	}
	
	// Update
	@Override
	public void update() {
	}

	public textSeries getTheText() {
		return theText;
	}

	public void setTheText(textSeries theText) {
		this.theText = theText;
	}

	public static interactBox getCurrentDisplay() {
		return currentDisplay;
	}

	public static void setCurrentDisplay(interactBox theDisplay) {
		currentDisplay = theDisplay;
	}

	public boolean isDisplayOn() {
		return displayOn;
	}

	public void setDisplayOn(boolean displayOn) {
		this.displayOn = displayOn;
	}

	public boolean isUnescapable() {
		return unescapable;
	}

	public void setUnescapable(boolean unescapable) {
		this.unescapable = unescapable;
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}
	
}
