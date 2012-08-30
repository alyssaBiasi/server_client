/*
 *	 COSC1179 - Network Programming
 *  	 Assignment - Multicasting Server Q2.2
 *  	 Alyssa Biasi s3328976
 *   
 */

import java.io.*;
import java.net.*;

public class Server02 {

	public static void main(String[]args) {
		final int PORT = 40101;
		final String EXIT = "Exit.";
		try {
			ServerSocket server = new ServerSocket(PORT);

			boolean exit = false;

			System.out.println("\nServer online.\n");
			BufferedReader user = new BufferedReader(new InputStreamReader(System.in));

			// Thread for accepting and serving connections.
			MServerThread02 t = new MServerThread02(server);
			t.start();

			while(!exit) {
				//Checking for exit prompt from user.
				String userInput = user.readLine();
				if(userInput.equals(EXIT)) {
					System.out.println("\nServer offline.\n");
					exit = true;
					t.exit();
				}
			}

			//Disconnecting.
		//	server.close();
		}
		catch(IOException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
	}
}
