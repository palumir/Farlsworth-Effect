package terrain.atmosphericEffects;

import UI.interfaceObject;
import drawing.gameCanvas;

public abstract class atmosphericEffect extends interfaceObject {
	
	public atmosphericEffect() {
		super(null, 0, 0, gameCanvas.getDefaultWidth(), gameCanvas.getDefaultHeight());
	}
	
	public static void initiate() {
		fog.initiate();
		storm.initiate();
		lightFog.initiate();
	}
}