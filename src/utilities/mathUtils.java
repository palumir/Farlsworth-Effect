package utilities;

public class mathUtils {
	public static double angleBetweenTwoPointsWithFixedPoint(double point1X, double point1Y, 
	        double point2X, double point2Y, 
	        double fixedX, double fixedY) {

	    double angle1 = Math.atan2(point1Y - fixedY, point1X - fixedX);
	    double angle2 = Math.atan2(point2Y - fixedY, point2X - fixedX);

	    return Math.toDegrees(angle1 - angle2); 
	}
	
	public static int gcd(int a, int b) { return b==0 ? a : gcd(b, a%b); }
}