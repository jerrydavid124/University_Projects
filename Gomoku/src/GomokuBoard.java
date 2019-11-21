/**
 * Implements the paint component and the mouse clicked action component for the Gomoku Board
 *
 *  <p>
 *
 *
 *  @author Jerry Tafoya
 *  @version HW <FINAL>, #<1>
 *  @bugs None that I know of
 */

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;

public class GomokuBoard extends JComponent {
	private GoPlayer ai;
	private Stone[][] stones;
	private int blackScore;
	private int whiteScore;
	private boolean lastplay;
	private boolean gameOver;
	private String addpt;
	private String premov;

	public String getmov(){
		//System.out.println("Getmov call = " + premov);
		return this.premov;
	}

	public String getpt(){
		return this.addpt;
	}

	public void resetpt(){
		this.addpt = "";
	}

	public void incBlackScore(){
		this.blackScore = this.blackScore + 1;
	}

	public void incWhiteScore(){
		this.whiteScore = this.whiteScore + 1;
	}

	public void setPlayer(boolean play){
		this.lastplay = play;
	}

	public int getBlackScore() {
		return blackScore;
	}

	public int getWhiteScore() {
		return whiteScore;
	}

	public boolean isGameOver() {
		return gameOver;
	}


	/**
	 * Inner class that handles when a mouse press has happened on the board and determines if the player
	 * can place a Black piece on there
	 */
	public class PlayHandler extends MouseAdapter {
		@Override

		public void mouseClicked(MouseEvent event){
			//System.out.println("BEFORE: " + lastplay);
			boolean check = false;
			int x = (int) Math.floor((event.getX() - 32.0)/32.0);
			int y = (int) Math.floor((event.getY() - 32.0)/32.0);
			int xmod = event.getX() % 32;
			int ymod = event.getY() % 32;
			if(xmod >= 16)
				x++;
			if(ymod >= 16)
				y++;
			if(lastplay){
				check = playBlack(x,y);
			} else {
				check = playWhite(x,y);
			}
			if(!check){
				return;
			} else {
				premov = x + " " + y;
				//System.out.println(x + " " + y);
				Stone winner = ai.checkWinner(stones);
				if(winner == Stone.BLACK){
					blackScore++;
					addpt = "B";
					gameOver = true;
					resetGame();
				} else if(winner == Stone.WHITE){
					whiteScore++;
					addpt = "W";
					gameOver = true;
					resetGame();
				}
			}
		}
	}

	/**
	 * Default method for GomokuBoard, where a human player plays against an ai
	 * @param ai The AI created by the GomokuAI file
	 */
	public GomokuBoard(GoPlayer ai){
		this.ai = ai;
		setPreferredSize(new Dimension(720,720));
		this.lastplay = true;
		addMouseListener(new PlayHandler());
		this.blackScore = 0;
		this.whiteScore = 0;
		this.gameOver = false;
		stones = new Stone[19][19];
		this.addpt = "";

		int i;
		int c;
		for(i = 0; i < 19; i++){
			for(c = 0; c < 19; c++){
				stones[i][c] = Stone.EMPTY;
			}
		}
	}

	/**
	 * Will place a new stone that was givin from another client in ServerThread
	 * @param x The x position
	 * @param y The y position
	 */
	public void newmove(int x, int y){
		boolean check;
		if(lastplay){
			check = playBlack(x,y);
		} else {
			check = playWhite(x,y);
		}
		if(!check){
			return;
		} else {
			//premov = x + " " + y;
			//System.out.println(x + " " + y);
			Stone winner = ai.checkWinner(stones);
			if(winner == Stone.BLACK){
				blackScore++;
				gameOver = true;
				resetGame();
			} else if(winner == Stone.WHITE){
				whiteScore++;
				gameOver = true;
				resetGame();
			}
		}
	}

	/**
	 * Creates the board on the screen, 19x19 arrangement of black squares
	 * @param g Graphics value
	 */
	public void paintComponent(Graphics g){
		try{
			//System.out.println("REPAINT: " + lastplay);
			Graphics2D g2 = (Graphics2D) g;
			BufferedImage bg = ImageIO.read(new File("C:\\Users\\Jerry\\IdeaProjects\\Gomoku\\src\\bamboo.jpg"));
			g2.drawImage(bg, 0,0, null);

			int i;
			int j;
			for(i = 0; i < 18; i++){
				for(j = 0; j < 18; j++){
					Point lowerleft = new Point(  32 + i * 32, j * 32);
					Point upperright = new Point(64 + i * 32,  32 + j * 32);
					Rectangle add = new Rectangle(lowerleft, upperright);
					new DrawShape().drawRectangle(g2,add,Color.BLACK);
				}
			}

			for(i = 0; i < 19; i++) {
				for (j = 0; j < 19; j++) {
					if (this.stones[i][j] != Stone.EMPTY) {
						Point addd = new Point(32 + i * 32, 32 + j * 32);
						Circle add = new Circle(addd, 16);
						new DrawShape().drawCircle(g2, add, this.stones[i][j].getColor());
					}
				}
			}

		} catch (IOException e){
			e.printStackTrace();
		}
	}

	/**
	 * Checks to see if the human players move is valid
	 * @param i the x coordinate, (In terms of 19x19 squares)
	 * @param j the y coordinate, (In terms of 19x19 squares)
	 * @return returns true if a stone has been set
	 */
	public boolean playBlack(int i, int j){
		if((i < 19 && i >= 0) && (j < 19 && j >= 0)){
			if(stones[i][j] == Stone.EMPTY && !isGameOver()){
				stones[i][j] = Stone.BLACK;
				this.setPlayer(false);
				repaint();
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	/**
	 * Checks to see if the human players move is valid
	 * @param i the x coordinate, (In terms of 19x19 squares)
	 * @param j the y coordinate, (In terms of 19x19 squares)
	 * @return returns true if a stone has been set
	 */
	public boolean playWhite(int i, int j){
		if((i < 19 && i >= 0) && (j < 19 && j >= 0)){
			if(stones[i][j] == Stone.EMPTY && !isGameOver()){
				stones[i][j] = Stone.WHITE;
				this.setPlayer(true);
				repaint();
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * resets the game when it is called in the code
	 */
	public void resetGame(){
		int i;
		int c;
		for(i = 0; i < 19; i++){
			for(c = 0; c < 19; c++){
				this.stones[i][c] = Stone.EMPTY;
			}
		}
		this.gameOver = false;
		if(lastplay){
			lastplay = false;
		} else {
			lastplay = true;
		}
		repaint();
	}

}
