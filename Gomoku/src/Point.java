/**
 2 * Super class for hw3 number 3, supports Circle and Rectangle
 3 *
 4 * <p> Holds a x and y point, can convert to Polar coordinates
 6 *
 8 *
 9 * @author Jerry Tafoya
 10 * @version HW <FINAL>, #<1>
 11 * @bugs None that I know of.
 */

public class Point implements Movable, Cloneable {
	private double x;
	private double y;
	private double radius;
	private double angle;

	//Setters //Every time a setter is called update Polar or Cartesian coordinates

	//Getters
	public double getRadius() {
		return radius;
	}
	public double getAngle() {
		return angle;
	}
	public double getX(){
		return this.x;
	}
	public double getY(){
		return this.y;
	}
	public void setX(double x) {
		this.x = x;
	}
	public void setY(double y) {
		this.y = y;
	}
	public void setRadius(double radius) {
		this.radius = radius;
	}
	public void setAngle(double angle) {
		this.angle = angle;
	}

	/**
	 * This is the default constructor which will create point 0,0
	 */
	public Point() {
		this.x = 0;
		this.y = 0;
		this.radius = Math.sqrt(Math.pow(this.x, 2) + Math.pow(this.y, 2));
		this.angle = Math.atan2(this.y, this.x);
		//This is the default point
	}
	/**
	 * This is a constructor which takes two Cartesian coordinates
	 * x - the x point
	 * y - the y point
	 */
	public Point(double x, double y){
		this.x = x;
		this.y = y;
		this.radius = Math.sqrt(Math.pow(this.x, 2) + Math.pow(this.y, 2));
		this.angle = Math.atan2(this.y, this.x);
	}
	/**
	 * This will copy the value of one point with another
	 * dup - the point that will be overwritten
	 */
	public Point(Point dup){
		setX(dup.x);
		setY(dup.y);
		setAngle(dup.angle);
		setRadius(dup.radius);
	}
	/**
	 * This is finds distance from two points
	 * dist - the second point
	 */
	public double distance(Point dist){
		double x1 = Math.abs(this.x - dist.x);
		double y1 = Math.abs(this.y - dist.y);
		double xs = x1*x1;
		double ys = y1*y1;
		return Math.sqrt(xs + ys);
	}
	/**
	 * This finds the points distance from origin
	 */
	public double distanceFromOrigin(){
		return Math.sqrt(this.x*this.x + this.y*this.y);
	}
	/**
	 * This compares two points
	 * dist - the point to be compared to
	 */
	public int compareTo(Point dist){
		if((dist.x == this.x) && (dist.y == this.y)){
			return 0;
		} else if ((dist.x > this.x) && (dist.y > this.y)){
			return -1;
		} else if ((dist.x < this.x)||(dist.y < this.y)){
			return 1;
		} else {
			return 0;
		}
	}
	/**
	 * This returns Cartesian coordinates from point (x,y)
	 */
	public String compareTo(){
		return "(" + this.x + "," + this.y + ")";
	}
	public void setPosition(double x, double y){
		setX(x);
		setY(y);
	}
	public void setPolar(double radius, double angle){
		setRadius(radius);
		setAngle(angle);
	}
	public void move(double dx, double dy){
		setX(getX() + dx);
		setY(getY() + dy);
	}
	public void movePolar(double radius, double angle){
		setRadius(getRadius() + radius);
		setAngle(getAngle() + angle);
	}
}

