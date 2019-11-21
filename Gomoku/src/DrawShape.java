/**
 2 * This class will allow number 2 and 3 to draw the Point Rectangle and Circle objects
 3 *
 4 * <p>
 6 *
 8 *
 9 * @author Jerry Tafoya
 10 * @version HW <FINAL>, #<1><
 11 * @bugs None that I know of
 */

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

public class DrawShape {
	public void drawPoint(Graphics2D g2, Point pt, Color col){
		Color temp = g2.getColor();
		g2.setColor(col);
		g2.draw(new Line2D.Double((int) pt.getX(), (int) pt.getY(),
			(int) pt.getX(), (int) pt.getY()));
		g2.setColor(temp);
	}
	public void drawRectangle(Graphics2D g2, Rectangle req, Color col){
		Color temp = g2.getColor();
		g2.setColor(col);
		g2.draw(new Rectangle2D.Double(req.getLowerLeft().getX(),req.getUpperRight().getY(),
			(int) req.getUpperRight().getX() - (int) req.getLowerLeft().getX(),
			(int) req.getUpperRight().getY() - (int) req.getLowerLeft().getY()));
		g2.setColor(temp);
	}
	public void drawCircle(Graphics2D g2, Circle cir, Color col){
		Color temp = g2.getColor();
		g2.setColor(col);
		g2.fillOval((int) cir.getCenter().getX() - (int) cir.getRadius(),
			(int) cir.getCenter().getY() - (int) cir.getRadius(),
			2 *(int) cir.getRadius(), 2 * (int) cir.getRadius());
		g2.setColor(temp);
	}
}
