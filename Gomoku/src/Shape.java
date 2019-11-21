/**
 2 * Super class for hw3 number 3, holds info on shapes
 3 *
 4 * <p> This is an abstract class on area perimter and inBounds.
 6 *
 8 *
 9 * @author Jerry Tafoya
 10 * @version HW <FINAL>, #<1>
 11 * @bugs None that I know of.
 */

public abstract class Shape implements Cloneable{
	abstract public double area();

	abstract public double perimeter();

	abstract boolean inBounds(Point point);


}
