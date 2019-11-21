/**
 * This is the server that will connect clients to play gomoku
 *
 *  <p> The server has its own game that handles which client wins or loses, the clients will update
 *  their points automatically
 *
 *
 *  @author Jerry Tafoya
 *  @version HW <FINAL>, #<1>
 *  @bugs The server has the same AI as the clients so its not doing anything
 *  The server allows a many clients as it wants onto the socket server
 *
 *  @BUG I cannot figure out how to create two jar files (one for server and client)
 */

import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Server extends JFrame{
	private int port;
	private ArrayList<ClientThread> clients;
	private GomokuBoard game;
	//private ObjectOutputStream out;

	/**
	 * This creates a new client on the port
	 * @param port The port to be created on
	 * @game: This large chunk of code is the method for determining the winner of the function
	 */
	public Server(int port) {
		game = new GomokuBoard(new GoPlayer() {
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
		}); //AI checks for winner
		this.port = port;
		this.clients = new ArrayList();
		ServerSocket server_socket = null;

		try {
			server_socket = new ServerSocket(port);
			this.acceptClient(server_socket);
		} catch (IOException var4) {
			System.out.println("Failed to start the server on port: " + port);
			var4.printStackTrace();
		}

	}

	/**
	 * Forever waits for clients to join the server that it can connect them to a ClientSocket thread
	 * @param server_socket The server socket to which the client will be added to
	 */
	private void acceptClient(ServerSocket server_socket) {
		while(true) {
			try {
				Socket client = server_socket.accept();
				System.out.println("Accept new connection from: " + client.getRemoteSocketAddress());
				ClientThread client_thread = new ClientThread(this, client);
				Thread new_client = new Thread(client_thread);
				new_client.start();
				this.clients.add(client_thread);
			} catch (IOException var5) {
				var5.printStackTrace();
			}
		}
	}

	/**
	 * This returns the whole list of clients currently connected to the server
	 * @return returns an ArrayList of clients
	 */
	public ArrayList<ClientThread> getClients() {
		return this.clients;
	}


	/**
	 * The main function which starts the server, and thus the whole final...
	 * @param args potential commend line arguments
	 */
	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
		System.out.print("Enter a port number: ");
		int port = scan.nextInt();
		scan.close();
		new Server(port);
	}
}
