package terrain.atmosphericEffects;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

import doodads.general.lightSource;
import drawing.drawnObject;
import drawing.gameCanvas;
import drawing.spriteSheet;
import drawing.userInterface.interfaceObject;
import effects.effectTypes.darkHole;
import utilities.imageUtils;
import utilities.time;

public abstract class atmosphericEffect extends interfaceObject {
	
	public atmosphericEffect() {
		super(null, 0, 0, gameCanvas.getDefaultWidth(), gameCanvas.getDefaultHeight());
	}
}