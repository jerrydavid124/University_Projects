/**
 * Creates the Gomoku board on the screen with a new game button and score counters
 *
 *  <p> Allows the player to place their piece wherever they want to on the board trying to beat the player
 *
 *
 *  @author Jerry Tafoya
 *  @version HW <FINAL>, #<1>
 *  @bugs None that I know of
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serializable;

public class GomokuFrame extends JFrame implements Serializable {
	//private GomokuBoard board;
	private JLabel blackscore;
	private JLabel whitescore;

	/**
	 * Creates a new frame for the client to see when playing
	 * @param board the board to be created from
	 * @param name The name of the player
	 */
	public GomokuFrame(GomokuBoard board, String name){

		setTitle(name + "'s Game");
		pack();
		//board = server;
		pack();

		getContentPane().add(board);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();

		JPanel top = new JPanel(new GridLayout(1,2));
		JPanel bot = new JPanel(new FlowLayout(FlowLayout.CENTER));

		blackscore = new JLabel("Black: " + board.getBlackScore(), SwingConstants.CENTER);
		whitescore = new JLabel("White: " + board.getWhiteScore(), SwingConstants.CENTER);
		blackscore.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 32));
		pack();
		whitescore.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 32));
		pack();
		blackscore.setForeground(Color.BLACK);
		whitescore.setForeground(Color.WHITE);
		top.setBackground(Color.decode("#ffd294"));
		pack();

		JButton new_game = new JButton("new game");
		new_game.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				board.resetGame();
				repaint();
			}
		});

		bot.add(new_game);
		top.add(blackscore);
		top.add(whitescore);

		add(top, BorderLayout.NORTH);
		pack();
		add(bot, BorderLayout.SOUTH);
		pack();

		board.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				blackscore.setText("Black: " + board.getBlackScore());
				whitescore.setText("White: " + board.getWhiteScore());
			}
		});





		setVisible(true);

	}
}
