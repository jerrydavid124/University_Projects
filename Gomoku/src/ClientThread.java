//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

/**
 * This thread is created by the server to connect to the client
 *
 *  <p>
 *
 *
 *  @author Jerry Tafoya
 *  @version HW <FINAL>, #<1>
 *  @bugs Same report here as for server
 */

import java.io.*;
import java.net.Socket;
import java.util.Iterator;
import java.util.Scanner;

public class ClientThread implements Runnable {
	private Server server;
	private Socket client;
	private PrintWriter out;
	//private ObjectInputStream in;
	//private ObjectOutputStream out;

	/**
	 * Creates a client thred to connect to the socket client
	 * @param server the server socket
	 * @param client the client socket
	 */
	public ClientThread(Server server, Socket client) {
		this.server = server;
		this.client = client;
	}

	/**
	 * The server will run this chunk of code while the client is alive
	 */
	public void run() {
		try {


			this.out = new PrintWriter(this.client.getOutputStream(), false);
			Scanner in = new Scanner(this.client.getInputStream());


			while(true) {
				do {
					if (this.client.isClosed()) {
						return;
					}
				} while(!in.hasNextLine());

				String input = in.nextLine();
				Iterator var3 = this.server.getClients().iterator();

				while(var3.hasNext()) {
					ClientThread c = (ClientThread)var3.next();
					PrintWriter cout = c.getWriter();
					if (cout != null) {
						cout.write(input + "\r\n");
						cout.flush();
					}
				}
			}
		} catch (IOException var6) {
			var6.printStackTrace();
		}
	}

	/**
	 * Returns the output of printWriter
	 * @return printwriter value
	 */
	public PrintWriter getWriter() {
		return this.out;
	}
}
