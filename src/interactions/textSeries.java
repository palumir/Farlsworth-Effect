package interactions;

import java.util.ArrayList;

public class textSeries {

	/////////////////////
	////// FIELDS ///////
	/////////////////////
	private String talker = null;
	private String buttonText; // If there's no button, just display textOnPress and go next.
	private String textOnPress;
	private textSeries parent = null;
	private ArrayList<textSeries> children;
	private textSeries end = null;
	private boolean isEnd = false;
	
	/////////////////////
	////// METHODS //////
	/////////////////////
	
	// Constructor
	public textSeries(String newButtonText, String newTextOnPress) {
		setButtonText(newButtonText);
		setTextOnPress(newTextOnPress);
		setChildren(new ArrayList<textSeries>());
	}
	
	// Add child.
	public textSeries addChild(String newButtonText, String newTextOnPress) {
		textSeries child = new textSeries(newButtonText, newTextOnPress);
		getChildren().add(child);
		child.parent = this;
		return child;
	}
	
	// Add child.
	public textSeries addChild(textSeries newChild) {
		getChildren().add(newChild);
		newChild.parent = this;
		return newChild;
	}
	
	public String getTextOnPress() {
		return textOnPress;
	}
	
	public void setEnd() {
		isEnd = true;
		
		// Tell the top parent where the end is.
		textSeries currSeries = this.parent;
		if(currSeries !=null) {
			while(currSeries.parent != null) {
				currSeries = currSeries.parent;
			}
			currSeries.end = this;
		}
	}
	
	public boolean isEnd() {
		return isEnd;
	}

	public void setTextOnPress(String textOnPress) {
		this.textOnPress = textOnPress;
	}

	public String getButtonText() {
		return buttonText;
	}

	public void setButtonText(String buttonText) {
		this.buttonText = buttonText;
	}

	public ArrayList<textSeries> getChildren() {
		return children;
	}

	public void setChildren(ArrayList<textSeries> children) {
		this.children = children;
	}

	public textSeries getEnd() {
		return end;
	}

	public void setEnd(textSeries end) {
		this.end = end;
	}

	public String getTalker() {
		return talker;
	}

	public void setTalker(String talker) {
		this.talker = talker;
	}
}