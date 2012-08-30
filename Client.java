/*
 *	 COSC1179 - Network Programming
 *  	 Assignment - Multicasting Client Q2.2: Joins group, listens for 
 *												exit command before leaving the group.
 *  	 Alyssa Biasi s3328976
 *   
 */


import java.io.*;
import java.net.*;

public class Client02 {
	private static final String LEAVE = "Leave.";
	private static final String HOST = "localhost";
	private static final int SERVER_PORT = 40101;
	
	public static void main(String[]args) {
		
		try {
			//Opens connection to server.
			Socket server = new Socket(HOST, SERVER_PORT);
			System.out.println("\nConnection established.\n");
			PrintWriter toServer = new PrintWriter(server.getOutputStream(), true);
			BufferedReader fromServer = new BufferedReader(new InputStreamReader(server.getInputStream()));
			
			//Notifies server that this client wishes to join the group.
			toServer.println("Join.");
			String response = fromServer.readLine();

			if(response.equals("Group full.")) {
				//Unable to join group - exits.
				System.out.println(response);
				System.exit(0);
			}
			
			toServer.close();
			fromServer.close();
			server.close();
			
			final int MY_PORT = Integer.parseInt(response);
			
			MClientThread02 ct = new MClientThread02(MY_PORT);
			ct.start();
			
			//Reading user input until exit command received.
			BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
			String input;
			while(true) {
				input = stdin.readLine();
				
				if(input.equals(LEAVE)) {
					break;
				}
			}
			
			System.out.println("Disconnecting.\n");
			
			leave(server, toServer, MY_PORT);
			ct.exit();
			System.exit(0);
		} catch(NumberFormatException e) {
			System.err.println("Client main: Error retrieving port number.\n");
			System.exit(1);
		} catch(IOException e) {
			System.err.println("Client main: "+ e.getMessage());
			System.exit(1);
		} catch(NullPointerException e) {
			System.err.println("Client main: Error reading input.\n");
		}
	}
	

	//Notifies server that this client is leaving the group.
	private static void leave(Socket server, PrintWriter toServer, int myPort) {
		try {
			server = new Socket(HOST, SERVER_PORT);
			toServer = new PrintWriter(server.getOutputStream(), true);
			
			toServer.println(LEAVE);
			toServer.println(myPort);
			
			server.close();
			toServer.close();
		} catch(IOException e) {
			System.err.println("Client leave: " + e.getMessage());
			System.exit(1);
		}
	}
}
