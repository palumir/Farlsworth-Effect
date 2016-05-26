package units;

public class unitType {
	
	public static unitType smallUnit;
	public static unitType bigUnit;
	
	public static void initiate() {
		// Static unit types
		smallUnit = new unitType("smallUnit", "square", 10, 10);
		smallUnit.setMoveSpeed(1);
		
		unitType bigUnit = new unitType("bigUnit", "square", 20, 20);
		bigUnit.setMoveSpeed(2);
	}
	
	// Fields
	private String name;
	private int width;
	private int height;
	private String shape;
	private int moveSpeed;
	// TODO sprites, etc.
	
	// Constructor
	public unitType(String newName, String newShape, int newWidth, int newHeight) {
		name = newName;
		setShape(newShape);
		setWidth(newWidth);
		setHeight(newHeight);
	}
	
	/////////////////////////
	// Getters and Setters //
	/////////////////////////
	public void setMoveSpeed(int i) {
		moveSpeed = i;
	}
	
	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public String getShape() {
		return shape;
	}

	public void setShape(String shape) {
		this.shape = shape;
	}

	public int getMoveSpeed() {
		return moveSpeed;
	}
}