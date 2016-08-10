package utilities;

import drawing.drawnObject;

public class mathUtils {
	public static double angleBetweenTwoPointsWithFixedPoint(double point1X, double point1Y, 
	        double point2X, double point2Y, 
	        double fixedX, double fixedY) {

	    double angle1 = Math.atan2(point1Y - fixedY, point1X - fixedX);
	    double angle2 = Math.atan2(point2Y - fixedY, point2X - fixedX);

	    return Math.toDegrees(angle1 - angle2); 
	}
	
	// Get angle between (in degrees) 
	public static int getAngleBetween(int x1, int y1, int x2, int y2) {
		  float angle = (float) Math.toDegrees(Math.atan2(y2 - y1, x2 - x1));

		  if(angle < 0){
		     angle += 360;
		  }

		  return (int)angle;
	}
	
	public static int gcd(int a, int b) { return b==0 ? a : gcd(b, a%b); }
}