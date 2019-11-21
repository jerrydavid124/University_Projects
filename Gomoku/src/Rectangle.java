/**
 2 * Supports Parallelogram
 3 *
 4 * <p> This is holds the upperRight and lowerLeft points on a parallelogram
 6 *
 8 *
 9 * @author Jerry Tafoya
 10 * @version HW <FINAL>, #<1>
 11 * @bugs None that I know of.
 */

public class Rectangle extends Shape implements Movable, Cloneable {
	private Point lowerLeft;
	private Point upperRight;

	// Getters


	public Point getLowerLeft() {
		return lowerLeft;
	}

	public Point getUpperRight() {
		return upperRight;
	}

	//Constructors
	/**
	 * This is the default constructor which will create a rectangle from (0,0) to (1,1)
	 */
	public Rectangle(){ //Default Constructor
		this.lowerLeft = new Point(0, 0);
		this.upperRight = new Point(1,1);
	}
	/**
	 * This is the main constructor that will make a rectangle out of two points
	 * lowerLeft - the lower left point
	 * upperRight - the upper right point
	 */
	public Rectangle(Point lowerLeft, Point upperRight){
		this.lowerLeft = lowerLeft;
		this.upperRight = upperRight;
		if (lowerLeft.getX() > upperRight.getX()){// fix lowerLeft > upperRight and lowerRight upperLeft
			Point temp = new Point(upperRight);
			upperRight.setX(lowerLeft.getX());
			lowerLeft.setX(temp.getX());
		}
		if (lowerLeft.getY() > upperRight.getY()){
			Point temp = new Point(upperRight);
			upperRight.setY(lowerLeft.getY());
			lowerLeft.setY(temp.getY());
		}

	}
	/**
	 * This will set the upper Right as width and height, while bottom left will be (0,0)
	 * width - x coordinate
	 * height - y coordinate
	 */
	public Rectangle(double width, double height) {
		this.lowerLeft = new Point();
		width = Math.abs(width);
		height = Math.abs(height);
		this.upperRight = new Point(width, height);
	}
	/**
	 * This finds the area of the rectangle
	 */
	public double area() {
		return (this.upperRight.getX() - this.lowerLeft.getX()) * (this.upperRight.getY() - this.lowerLeft.getY());
	}
	/**
	 * This finds the perimeter of the rectangle
	 */
	public double perimeter(){
		return ((this.upperRight.getX() - this.lowerLeft.getX()) * 2) + ((this.upperRight.getY() - this.lowerLeft.getY()) * 2);
	}
	/**
	 * This checks to see if a point is inside the rectangle
	 * test - the point to be checked
	 */
	public boolean inBounds(Point test){ //If the point is on the Perimeter or inside it's TRUE
		//All else if FALSE
		boolean ans;
		if((test.getX() < this.upperRight.getX() && test.getX() > this.lowerLeft.getX()) && ((test.getY() < this.upperRight.getY() && test.getY() > this.lowerLeft.getY()))){
			//This point is inside the rectangle
			ans = true;
		} else if((test.getX() > this.upperRight.getX() || test.getX() < this.lowerLeft.getX()) || (test.getY() > this.upperRight.getY() || test.getY() < this.lowerLeft.getY())){
			//This point is outside the rectangle
			ans = false;
		} else{
			ans = true;//This checks if its on the perimeter
			//Since we already checked if its either entirely in or outwards the only possible place for it to be
			//is on the perimeter
		}
		return ans;
	}
	public void setPosition(double x, double y){
		lowerLeft.setX(x);
		lowerLeft.setY(y);
	}
	public void setPolar(double radius, double angle){
		lowerLeft.setAngle(angle);
		lowerLeft.setRadius(radius);
	}
	public void move(double dx, double dy){
		lowerLeft.setX(lowerLeft.getX() + dx);
		lowerLeft.setY(lowerLeft.getY() + dy);
		upperRight.setX(upperRight.getX() + dx);
		upperRight.setY(upperRight.getY() + dy);
	}
	public void movePolar(double radius, double angle){
		lowerLeft.setAngle(lowerLeft.getAngle() + angle);
		lowerLeft.setRadius(lowerLeft.getRadius() + radius);
		upperRight.setAngle(upperRight.getAngle() + angle);
		upperRight.setRadius(upperRight.getRadius() + radius);
	}
	@Override
	public Rectangle clone(){
		Rectangle copy = null;
		try{
			copy = (Rectangle) super.clone();
			copy.lowerLeft = this.lowerLeft;
			copy.upperRight = this.upperRight;
		} catch (CloneNotSupportedException e){
			copy = new Rectangle(this.lowerLeft, this.upperRight);
		}
		return copy;
	}
}
