package interactions;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import UI.interfaceObject;
import drawing.drawnObject;
import drawing.gameCanvas;
import drawing.spriteSheet;
import effects.interfaceEffects.textBlurb;
import sounds.sound;
import units.player;
import units.unit;

public class interactBox extends interfaceObject  {
	
	///////////////////////
	////// GLOBALS ////////
	///////////////////////
	
	// Background.
    public static BufferedImage DEFAULT_BACKGROUND = spriteSheet.getSpriteFromFilePath("images/interface/interactBox.png");
    public static BufferedImage arrow = spriteSheet.getSpriteFromFilePath("images/interface/arrow.png");
    
	// Position
	private static int DEFAULT_X = (int) (gameCanvas.getDefaultWidth()*0.5f) - DEFAULT_BACKGROUND.getWidth()/2;
	private static int DEFAULT_Y = (int) (gameCanvas.getDefaultHeight()*0.80f) - DEFAULT_BACKGROUND.getHeight()/2;
    
    // Color
	private Color DEFAULT_TEXT_COLOR = Color.black;
	private Color DEFAULT_SELECTED_COLOR = new Color(100,48,38);
	
	// Text font.
	private static Font DEFAULT_FONT = null;
	private static Font DEFAULT_FONT_TITLE = null;
	
	///////////////////////
	////// FIELDS /////////
	///////////////////////
	
	// Background
	public BufferedImage background = DEFAULT_BACKGROUND;
    
    // Text to display and the color of the text.
	private textSeries theText;
	private String displayedText = "";
	private int displayIterator = 0;
	private float DEFAULT_DISPLAY_FOR = 2; // Frames
	
	// Not talking
	private boolean notTalking = false;
	
	// Go next
	private boolean goNext = false;
	
	// Selected button.
	private int selectedButton = 0;
	
	// Text blurb
	private textBlurb blurb;
	
	// Display
	private boolean buttonMode = false;
	private boolean textMode = false;
	private boolean displayOn = false;
	private boolean unescapable = false;
	private boolean locked = false;
	private static interactBox currentDisplay = null;
	
	// Allow select? (Prevents retards from holding e)
	private boolean allowSelect = true;
	
	// Sounds.
	private static String UIMove = "sounds/effects/player/UI/UIMove.wav";
	private static String typing = "sounds/effects/player/UI/typing.wav";
	
	///////////////
	/// METHODS ///
	///////////////
	
	// Is text fully written out?
	public boolean textScrollingFinished() {
		return displayedText.equals(theText.getTextOnPress());
	}
	
	// Constructor
	public interactBox(textSeries newText, drawnObject newWhoIsTalking) {
		super(null, DEFAULT_X, DEFAULT_Y, DEFAULT_BACKGROUND.getWidth(), DEFAULT_BACKGROUND.getHeight());	
		
		// Set fields.
		if(newText != null && newText.getButtonText() != null) setButtonMode(true);
		else textMode = true;
		newText.setWhoIsTalking(newWhoIsTalking);
		setTheText(newText);
		
		// If there's a custom dialogue box, set our properties
			
		if(newWhoIsTalking.getDialogueBox()!=null) {
			setBackground();
		}
		
	}
	
	// Draw the unit. 
	@Override
	public void drawObject(Graphics g) {
		if(isDisplayOn()) {
			
			// Set default font.
			DEFAULT_FONT = drawnObject.DEFAULT_FONT.deriveFont(drawnObject.DEFAULT_FONT.getSize() * 1.4F);
			DEFAULT_FONT_TITLE = drawnObject.DEFAULT_FONT_BOLD.deriveFont(drawnObject.DEFAULT_FONT_BOLD.getSize() * 1.41F);
			
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
				
				/*if(getTextSeries().getWhoIsTalking() != null) {
					// Set font.
					g.setFont(DEFAULT_FONT_TITLE);
					
					// Display the name of the person or thing talking/interacting
					Graphics2D g2 = (Graphics2D)g;
					g2.setPaint(Color.BLACK);
					String theTalker = getTextSeries().getTalker();
					if(theTalker==null) theTalker="";
					if(getTextSeries().getWhoIsTalking() != null && getTextSeries().getWhoIsTalking() instanceof unit) { 
						theTalker = stringUtils.toTitleCase(getTextSeries().getWhoIsTalking().getName());
						g2.setPaint(((unit)getTextSeries().getWhoIsTalking()).getNameColor());
					}
					g2.drawString(theTalker,
							(int)(gameCanvas.getScaleX()*getIntX()) + 
							(int)(gameCanvas.getScaleX()*background.getWidth()/2) - 
							g.getFontMetrics().stringWidth(theTalker)/2,
							(int)(gameCanvas.getScaleY()*getIntY()) + 
							(int)(gameCanvas.getScaleY()*background.getHeight()/5) + 
							(int)(gameCanvas.getScaleY()*4));
					g2.setPaint(Color.BLACK);
				}*/
				
				// Set font.
				g.setFont(DEFAULT_FONT);
				
				// Increase displayedText
				if(getTextSeries().getTextOnPress() != null && displayedText.length() != getTextSeries().getTextOnPress().length()) {
					displayIterator++;
					if(displayIterator == DEFAULT_DISPLAY_FOR) {
						displayIterator = 0;
						displayedText += getTextSeries().getTextOnPress().charAt(displayedText.length());
						sound s = new sound(typing);
						s.setVolume(0.9f);
						s.start();
					}
				}
				
				// Draw the text.
				if(getTextSeries().getTextOnPress() != null) g.drawString(displayedText,
						(int)(gameCanvas.getScaleX()*getIntX()) + (int)(gameCanvas.getScaleX()*background.getWidth()/2) - g.getFontMetrics().stringWidth(getTextSeries().getTextOnPress())/2,
					   (int)(gameCanvas.getScaleY()*(getIntY() + background.getHeight()/2 + 4)));
			}
			
			// Button
			if(isButtonMode()) {
				
				// Set font.
				g.setFont(DEFAULT_FONT);
				float percent = 1;
				if(getTextSeries().getChildren().size()!=0) percent = 1f/((float)getTextSeries().getChildren().size() + 1);
					for(int i = 0; i < getTextSeries().getChildren().size(); i++) {
						
						// Get text.
						String buttText = getTextSeries().getChildren().get(i).getButtonText();
						
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
						
						// First choice.
						/*if(i==0) {
							g.drawString(buttText,
									   (int)((int)(gameCanvas.getScaleX()*(getIntX() + (2*(i+1))*percent*background.getWidth()/2)) - g.getFontMetrics().stringWidth(buttText)/2),
									   (int)(gameCanvas.getScaleY()*(getIntY() + background.getHeight()/2 + 4)));
						}
						
						// Second choice
						if(i==1) {
							
						}*/
						
						// Draw text
						g.drawString(buttText,
							   (int)((int)(gameCanvas.getScaleX()*(getIntX() + (2*(i+1))*percent*background.getWidth()/2)) - g.getFontMetrics().stringWidth(buttText)/2),
							   (int)(gameCanvas.getScaleY()*(getIntY() + background.getHeight()/2 + 4)));
						
						// Draw order/chaos TODO:PLACEHOLDER.
						String type = getTextSeries().getChildren().get(i).getChoiceType();
						if(type!=null) {
							/*g.drawString(type,
								   (int) ((int)(gameCanvas.getScaleX()*(getIntX() + (2*(i+1))*percent*background.getWidth()/2)) - g.getFontMetrics().stringWidth(type)/2),
								   (int)(gameCanvas.getScaleY()*(getIntY() + background.getHeight()/2 + 15)));*/
						}
					}
			}
		}
	}
	
	// Set dialogue background
	public void setBackground() {
		if(!isButtonMode() &&
				getTextSeries().getWhoIsTalking() != null 
				&& (getTextSeries().getWhoIsTalking()).getDialogueBox() != null) {
			background = getTextSeries().getWhoIsTalking().getDialogueBox();
			setDoubleX((int) (gameCanvas.getDefaultWidth()*0.5f) - background.getWidth()/2);
			setDoubleY((int) (gameCanvas.getDefaultHeight()*0.80f) - background.getHeight()/2);
			setWidth(background.getWidth());
			setHeight(background.getHeight());
		}
		else {
			background = DEFAULT_BACKGROUND;
			setDoubleX((int) (gameCanvas.getDefaultWidth()*0.5f) - background.getWidth()/2);
			setDoubleY((int) (gameCanvas.getDefaultHeight()*0.80f) - background.getHeight()/2);
			setWidth(background.getWidth());
			setHeight(background.getHeight());
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
		if(isDisplayOn()) {
			
			if(getTextSeries().getWhoIsTalking() instanceof unit && !isNotTalking()) {
				
				// Create a blurb effect on who is talking
				setBlurb(new textBlurb(getTextSeries().getWhoIsTalking().getIntX()-textBlurb.getDefaultWidth(),getTextSeries().getWhoIsTalking().getIntY()-getTextSeries().getWhoIsTalking().getHitBoxAdjustmentY()-textBlurb.getDefaultWidth()));
				getBlurb().attachToObject(getTextSeries().getWhoIsTalking());
			}
			
			// Set that the object is being interacted.
			getTextSeries().getWhoIsTalking().setBeingInteracted(true);
			
			// Stop the player
			player.getPlayer().stop();
		}
		if(!isDisplayOn()) {
			if(getBlurb()!=null) getBlurb().end();
			
			// Set that the objest is not being interacted.
			if(getTextSeries().getWhoIsTalking()!=null) getTextSeries().getWhoIsTalking().setBeingInteracted(false);
		}
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
		if(textMode && displayedText.length() != getTextSeries().getTextOnPress().length()) {
			displayedText = getTextSeries().getTextOnPress();
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
		
		// No end set but no children.
		else {
			// Set that we need to go to the next one.
			setGoNext(true);
		}
	}
	

	public void goToNext(int i) {
		
		// If we are currently in text mode.
		if(textMode) {
			
			// If there's only one thing, assume it's just more text.
			if(theText.getChildren().size() == 1 && theText.getChildren().get(0).getButtonText() == null) {
				selectedButton = 0;
				textSeries oldText = theText;
				theText = theText.getChildren().get(0);
				if(theText.getWhoIsTalking() != oldText.getWhoIsTalking()) {
					// Create a blurb effect on who is talking
					if(getBlurb()!=null) getBlurb().end();
					setBlurb(new textBlurb(getTextSeries().getWhoIsTalking().getIntX()-textBlurb.getDefaultWidth(),getTextSeries().getWhoIsTalking().getIntY()-getTextSeries().getWhoIsTalking().getHitBoxAdjustmentY()-textBlurb.getDefaultWidth()));
					getBlurb().attachToObject(getTextSeries().getWhoIsTalking());
				}
			}
		
			// Otherwise, there will be buttons to select from. Go to button mode.
			else {
				
				if(getTextSeries().getWhoIsTalking() instanceof unit && !isNotTalking()) {
					
					// Create a blurb effect on who is talking
					if(getBlurb()!=null) getBlurb().end();
					setBlurb(new textBlurb(player.getPlayer().getIntX()-textBlurb.getDefaultWidth(),player.getPlayer().getIntY()-player.getPlayer().getHitBoxAdjustmentY()-textBlurb.getDefaultWidth()));
					getBlurb().attachToObject(player.getPlayer());
				}
				
				setButtonMode(true);
				textMode = false;
			}
			
			// Reset the text.
			displayedText = "";
			
		}
		
		// If we're in button mode.
		else if(isButtonMode()) {
			
			// Increase player choices if we are making a chaos/order choice.
			if(theText.getChildren().get(i).getChoiceType() == null); // Do nothing.
			else if(theText.getChildren().get(i).getChoiceType().equals("Chaos"))
				player.getPlayer().chaosChoices++;
			else if(theText.getChildren().get(i).getChoiceType().equals("Order")) 
				player.getPlayer().orderChoices++;
			
		
			// Select the button.
			theText = theText.getChildren().get(i);
			
			if(getTextSeries().getWhoIsTalking() instanceof unit && !isNotTalking()) {
				
				// Send textblurb back to who is speaking.
				if(getBlurb()!=null) getBlurb().end();
				setBlurb(new textBlurb(getTextSeries().getWhoIsTalking().getIntX()-textBlurb.getDefaultWidth(),getTextSeries().getWhoIsTalking().getIntY()-getTextSeries().getWhoIsTalking().getHitBoxAdjustmentY()-textBlurb.getDefaultWidth()));
				getBlurb().attachToObject(getTextSeries().getWhoIsTalking());
			}
			
			setButtonMode(false);
			textMode = true;
			selectedButton = 0;
			goNext = true;
		}
		
		setBackground();
	}
	
	// Go to next
	public void goToNext() {
		goToNext(selectedButton);
	}
	
	// Move select
	public void moveSelect(String direction) {
		
		if(isButtonMode()) {
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
		if(allowSelect && (k.getKeyCode() == KeyEvent.VK_SPACE || k.getKeyCode() == KeyEvent.VK_ENTER || k.getKeyCode() == KeyEvent.VK_E)) { 
			select();
			allowSelect = false;
		}
	}
	
	// Respond to key press.
	public void respondToKeyRelease(KeyEvent k) {
		
		// Player presses e key.
		if(k.getKeyCode() == KeyEvent.VK_SPACE || k.getKeyCode() == KeyEvent.VK_ENTER || k.getKeyCode() == KeyEvent.VK_E) { 
			allowSelect = true;
		}
	}
	
	// Update
	@Override
	public void update() {
	}

	public textSeries getTextSeries() {
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

	public textBlurb getBlurb() {
		return blurb;
	}

	public void setBlurb(textBlurb blurb) {
		this.blurb = blurb;
	}

	public boolean isButtonMode() {
		return buttonMode;
	}

	public void setButtonMode(boolean buttonMode) {
		this.buttonMode = buttonMode;
	}

	public boolean isGoNext() {
		return goNext;
	}

	public void setGoNext(boolean goNext) {
		this.goNext = goNext;
	}

	public boolean isNotTalking() {
		return notTalking;
	}

	public void setNotTalking(boolean notTalking) {
		this.notTalking = notTalking;
	}
	
}
