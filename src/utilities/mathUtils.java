package utilities;

import drawing.drawnObject;

public class mathUtils {
	// Get angle between (in degrees) 
	public static int getAngleBetween(int x1, int y1, int x2, int y2) {
		  float angle = (float) Math.toDegrees(Math.atan2(y2 - y1, x2 - x1));
		  angle -= 90;

		  if(angle < 0){
		     angle += 360;
		  }
		  
		  return (int)angle;
	}
	
	public static int gcd(int a, int b) { return b==0 ? a : gcd(b, a%b); }
}