
/**
 * This thread is created by the client to connect the server
 *
 *  <p>
 *
 *
 *  @author Jerry Tafoya
 *  @version HW <FINAL>, #<1>
 *  @bugs Same report here as for clients
 */


import java.io.*;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Scanner;

public class ServerThread implements Runnable {
	private Socket client;
	private String name;
	private GomokuBoard board;
	private final LinkedList<String> messages;


	/**
	 * Sends the move message to the server when the client makes a move so that the other client can update
	 * their game
	 * @param new_message Message to save on the stack
	 */
	public void newMessage(String new_message) {
		synchronized(this.messages) {
			this.messages.push(new_message);
		}
	}

	/**
	 * Creates a new server thread for the client so that i can run concurrently with the other client
	 * @param client The client socket
	 * @param name the name of the client
	 * @param board The board who's information will be sent through the server
	 */
	public ServerThread(Socket client, String name, GomokuBoard board) {
		this.client = client;
		this.name = name;
		this.board = board;
		this.messages = new LinkedList<>();
	}

	/**
	 * This code will run while the thread and the server thread is alive
	 */
	public void run() {
		System.out.println("Welcome " + this.name);

		try {
			PrintWriter out_stream = new PrintWriter(this.client.getOutputStream());
			InputStream in_stream = this.client.getInputStream();
			Scanner in = new Scanner(in_stream);

			while(!this.client.isClosed()) {
				if (in_stream.available() > 0 && in.hasNextLine()) {
					if(board.getpt().equals("B")){
						//board.incBlackScore();
						board.resetpt();
					} else if (board.getpt().equals("W")){
						//board.incWhiteScore();
						board.resetpt();
					} else {
						String line = in.nextLine();
						int index = line.indexOf(" ");
						int x = Integer.parseInt(line.substring(0, index));
						int y = Integer.parseInt(line.substring(index + 1));
						board.newmove(x , y);
					}
				}

				if (!this.messages.isEmpty()) {
					String next = null;
					synchronized(this.messages) {
						next = (String) this.messages.pop();
					}

					out_stream.println(next);
					out_stream.flush();
				}
			}
		} catch (IOException var8) {
			var8.printStackTrace();
		}
	}
}
