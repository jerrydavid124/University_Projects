/**
 * Enumeration Class holding values of circles
 *
 *  <p> holds the center point and radius of the circle
 *
 *
 *  @author Jerry Tafoya
 *  @version HW <FINAL>, #<2>
 *  @bugs None that I know of
 */

import java.awt.*;

public enum Stone {

	EMPTY, BLACK , WHITE ;

	public Color getColor(){
		if(this == BLACK){
			return Color.BLACK;
		} else if (this == WHITE){
			return Color.WHITE;
		} else {
			return null;
		}
	}
}
