
/**
 * This AI Player checks who wins and has random values for its placements
 *
 *  <p> Non-Human-Player
 *
 *
 *  @author Jerry Tafoya
 *  @version HW <FINAL>, #<1>
 *  @bugs checkWinner testing shows a bit of bugs, especially in diagonal cases and corner cases
 */
import java.util.Random;

public class GomokuAI implements GoPlayer {

	@Override
	public Stone checkWinner(Stone[][] e) {
		//Check 19x19 board for winner
		int i;
		int col;
		int row;
		int count;
		int colplace;
		int rowplace;
		int x;
		Stone current;

		//Checking Horizontal
		for(col = 0; col < 19; col++) {
			row = 0;
			count = 0;
			current = e[col][row];
			for (row = 0; row < 19; row++) {
				if (e[col][row] == current) {
					count++;
				} else {
					count = 0;
					current = e[col][row];
				}
				if (count == 4 && current != Stone.EMPTY) {
					return current;
				}
			}
		}

		//checking Vertical (Flip Hoizontal)
		for(row = 0; row < 19; row++) {
			col = 0;
			count = 0;
			current = e[col][row];
			for (col = 0; col < 19; col++) {
				if (e[col][row] == current) {
					count++;
				} else {
					count = 0;
					current = e[col][row];
				}
				if (count == 4 && current != Stone.EMPTY) {
					return current;
				}
			}
		}


		//checking diagonal-down (bottom right to top left)
		//	|\
		//	| \   <- Checking this
		// 	|__\__
		count = 0;
		colplace = 18;
		rowplace = 4;
		i = 0;

		while(i < 15){	//checking starting at bottom first
			col = 0;
			current = e[colplace][rowplace];
			for(row = 1; row < rowplace+1; row++){
				col++;
				if(e[colplace-col][rowplace-row] == current){
					count++;
				} else {
					count = 0;
					current = e[colplace-col][rowplace-row];
				}
				if (count == 4 && current != Stone.EMPTY){
					return current;
				}
			}
			i++;
			rowplace++;
		}

		count = 0;
		colplace = 17;
		rowplace = 18;
		i = 0;

		while(i < 13) { //checking right side now
			row = 0;
			current = e[colplace][rowplace];
			for(col = 1; col < colplace+1; col++){
				row++;
				if(e[colplace-col][rowplace-row] == current){
					count++;
				} else {
					count = 0;
					current = e[colplace-col][rowplace-row];
				}
				if (count == 4 && current != Stone.EMPTY){
					return current;
				}
			}
			i++;
			colplace--;
		}


		//checking diagonal-up (bottom left to top right)
		count = 0;
		colplace = 4;
		i = 0;
		x = colplace;


		while(i < 15){	//checking starting at left face
			//This goes like	col = x & row = 0,
			//     |  /	    col = x - 1 & row = 0 + 1
			//     | /  <- Checking this   ...
			//     |/____	        col = 0 & row = x

			col = x;
			row = 0;
			current = e[col][row];
			while(col != 0){
				col--;
				row++;
				if(e[col][row] == current){
					count++;
				} else {
					count = 0;
					current = e[col][row];
				}
				if (count == 4 && current != Stone.EMPTY){
					return current;
				}
			}

			i++;
			x++;

		}

		//Now checking the bottom set of circles

		count = 0;
		colplace = 18;
		rowplace = 14;
		i = 0;

		while(i < 13){	//checking starting at bottom first
			col = 0;
			current = e[colplace][rowplace];
			for(row = 1; row < (18-rowplace+1); row++){
				col++;
				if(e[colplace-col][rowplace+row] == current){
					count++;
				} else {
					count = 0;
					current = e[colplace-col][rowplace+row];
				}
				if (count == 4 && current != Stone.EMPTY){
					return current;
				}
			}
			i++;
			rowplace--;
		}

		return Stone.EMPTY;
	}

	//@Override
	public int[] getMove(Stone[][] e) {
		int i = new Random().nextInt(19);
		int j = new Random().nextInt(19);
		int[] Array = {i, j};
		boolean taken = true;
		while(taken){
			if(e[i][j] == Stone.EMPTY){
				Array[0] = i;
				Array[1] = j;
				e[i][j] = Stone.WHITE;
				taken = false;
			} else {
				i = new Random().nextInt(19);
				j = new Random().nextInt(19);
			}
		}
		return Array;
	}
}
