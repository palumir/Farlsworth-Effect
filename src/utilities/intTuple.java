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
	
	// Equals
	@Override
	public boolean equals(Object o) {
		if(o instanceof intTuple) {
			if(x == ((intTuple)o).x && y == ((intTuple)o).y) {
				return true;
			}
		}
		return false;
	}
	
	// Hashcode.
	@Override
	public int hashCode() {
		int tmp = (y+((x+1)/2));
        return x+(tmp*tmp);
	}
		
}