package utilities;

// Coordinate class for returning x,y
public class intTuple {
		
	// Fields 
	public int x;
	public int y;
		
	// Empty tuple
	public static intTuple emptyTuple = new intTuple(0,0);
		
	// Constructor
	public intTuple(int newX, int newY) {
		x = newX;
		y = newY;
	}
		
}