/**
 2 * Circle superclass that supports Ellpise
 3 *
 4 * <p> holds the center point and radius of the circle
 6 *
 8 *
 9 * @author Jerry Tafoya
 10 * @version HW <FINAL>, #<1>
 11 * @bugs None that I know of
 */

public class Circle extends Shape implements Movable, Cloneable{
	private Point center;
	private double radius;


	//getters and setters


	public Point getCenter() {
		return center;
	}

	public void setCenter(Point center) {
		this.center = center;
	}

	public double getRadius() {
		return radius;
	}

	public void setRadius(double radius) {
		this.radius = radius;
	}

	//constructors
	/**
	 * This is the default constructor which will create point 0,0 with radius 1
	 */
	public Circle(){
		this.center = new Point();
		this.radius = 1;
	}
	/**
	 * This is the main constructor that takes in a point and radius
	 * center - the point in the middle of the circle
	 * radius - radius of circle
	 */
	public Circle(Point center, double radius){
		this.center = center;
		this.radius = Math.abs(radius);
	}
	/**
	 * This finds the area of a circle
	 */
	public double area(){
		return Math.PI * this.radius * this.radius;
	}
	/**
	 * This finds the perimeter (Circumference) of the circle
	 */
	public double perimeter(){
		return 2 * Math.PI * this.radius;
	}

	/**
	 * This finds the center points distance from (0,0)
	 */
	public double distanceFromOrigin(){
		return Math.sqrt(this.center.getX()*this.center.getX() + this.center.getY()*this.center.getY());
	}
	/**
	 * This checks if a point is inside the circle
	 * x - x coordinate
	 * y - y coordinate
	 */
	public boolean inBounds(Point point){
		double check = Math.pow(this.radius, 2);
		double xvalue = Math.pow(point.getX() - this.center.getX(), 2);
		double yvalue = Math.pow(point.getY() - this.center.getY(), 2);
		boolean ans = true;
		if(xvalue + yvalue > check){ //If its not outside, then its either inside or on the perimeter
			ans = false;
		}
		return ans;
	}
	/**
	 * This creates the points that will make a box outside the circle
	 */
	public Rectangle getBoundingBox(){
		Point bottom = new Point(this.center.getX() + radius, this.center.getY() + radius);
		Point top = new Point(this.center.getX() - radius, this.center.getY() - radius);
		return new Rectangle(bottom, top);
	}
	public void setPosition(double x, double y){
		center.setX(x);
		center.setY(y);
	}
	public void setPolar(double radius, double angle){
		center.setRadius(radius);
		center.setAngle(angle);
	}
	public void move(double dx, double dy){
		center.setX(center.getX() + dx);
		center.setY(center.getY() + dy);
	}
	public void movePolar(double radius, double angle){
		center.setAngle(center.getAngle() + angle);
		center.setRadius(center.getRadius() + radius);
	}
	@Override
	public Circle clone(){
		Circle copy = null;
		try{
			copy = (Circle) super.clone();
			copy.center = this.center;
			copy.radius = this.radius;
		} catch (CloneNotSupportedException e){
			copy = new Circle(this.center, this.radius);
		}
		return copy;
	}
}
