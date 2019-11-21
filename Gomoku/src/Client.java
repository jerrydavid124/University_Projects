/**
 * This is the client that will connect to the server
 *
 *  <p> This client has its own board and gui for the board
 *  It communicates with another client to play a game of
 *  Gomoku
 *
 *
 *  @author Jerry Tafoya
 *  @version HW <FINAL>, #<1>
 *  @bugs As many clients can join as they want
 *  No Start Button, Winning points does not output to other
 *  Client correctly. After a few games the points become desynchronized
 *
 * @BUG I cannot figure out how to create two jar files (one for server and client)
 */


import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {
	private int port;
	private String name;
	private GomokuFrame gui;
	private GomokuBoard board;

	/**
	 * Create the client to join to the server socket
	 * @param port The port to connect to
	 * @param name The clients name
	 */
	public Client(int port, String name) {
		this.port = port;
		this.name = name;
		board = new GomokuBoard(new GomokuAI());
		gui = new GomokuFrame(board, this.name);
		this.startClient();
	}

	/**
	 * This will run the server thread while the server is alive so that i can communicate with another client
	 */
	private void startClient() {
		try {
			Socket client = new Socket("localhost", this.port);
			Thread.sleep(1000L);
			ServerThread server_thread = new ServerThread(client, this.name , board);
			Thread server = new Thread(server_thread);
			server.start();
			//Scanner scan = new Scanner(System.in);
			String test = " "; //null at first

			while(server.isAlive()) {
				if (board.getmov() != null) {
					if(!test.equals(board.getmov())){
						test = board.getmov();
						//System.out.println("TEST = " + test);
						server_thread.newMessage(board.getmov());
					}
				}
			}
		} catch (IOException var5) {
			var5.printStackTrace();
		} catch (InterruptedException var6) {
			System.out.println("error");
			var6.printStackTrace();
		}

	}

	/**
	 * Main function that will ask the user for hte port number and client
	 * @param args
	 */
	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
		System.out.print("Enter a port number: ");
		int port = Integer.parseInt(scan.nextLine());
		String name = null;
		System.out.print("Enter name: ");
		name = scan.nextLine();
		new Client(port, name);
	}
}
