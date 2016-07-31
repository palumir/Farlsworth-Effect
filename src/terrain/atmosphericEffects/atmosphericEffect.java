package terrain.atmosphericEffects;

import drawing.gameCanvas;
import drawing.userInterface.interfaceObject;

public abstract class atmosphericEffect extends interfaceObject {
	
	public atmosphericEffect() {
		super(null, 0, 0, gameCanvas.getDefaultWidth(), gameCanvas.getDefaultHeight());
	}
}