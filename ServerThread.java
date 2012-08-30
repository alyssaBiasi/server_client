/*
 *	 COSC1179 - Network Programming
 *  	 Assignment - Multicasting Server Thread Q2.2: Listens to port, accepts 
 *  												   connections & maintains client list.
 *  	 Alyssa Biasi s3328976
 *   
 */

import java.net.*;
import java.util.*;
import java.io.*;

public class MServerThread02 extends Thread {
	private ServerSocket server;
	private Socket client;
	private boolean exit;
	private MResponseThread02 t;
	
	public MServerThread02(ServerSocket s) {
		this.server = s;
		exit = false;
	}

	public void run() {
		final int SERVER_PORT = 40101;
		final int FIRST_PORT = SERVER_PORT + 1;
		final int MAX = 5;
		final String JOIN = "Join.";
		final String LEAVE = "Leave.";
	    ArrayList<Integer> ports = new ArrayList<Integer>();
	    int i;
	    
		try {
			t = new MResponseThread02();
			t.start();
			t.updateList(ports);
			
			while(!exit) {
				client = server.accept();
				
				PrintWriter toClient = new PrintWriter(client.getOutputStream(), true);
				BufferedReader fromClient = new BufferedReader(new InputStreamReader(client.getInputStream()));
				
				String input = fromClient.readLine();
				
				//Joining: Notifies client of port number 
				//			(first available port + array index)
				if(input.equals(JOIN)) {
					//Checks that there's room in the group.
					if(ports.size() == MAX) {
						toClient.println("Group full.");
						continue;
					}
					
					//Finds a spot on the list.
					for(i=0; i<MAX; i++) {
						if(!ports.contains(FIRST_PORT+i)) {
							ports.add(FIRST_PORT+i);
							Collections.sort(ports);
							toClient.println(ports.get(i));
							break;
						}
					}
				}
				else if(input.equals(LEAVE)) {
					input = fromClient.readLine();
					Integer clientPort = Integer.parseInt(input);
					
					ports.remove(clientPort);
				}
				
				//Updating response thread.
				t.updateList(ports);
			}
		} catch(IOException e) {
			System.out.println("Server main IO: " + e.getMessage());
			System.exit(1);
		} catch(NumberFormatException e) {
			System.out.println("Server main NF: " + e.getMessage());
			System.exit(1);
		}
		
		return;
	}
	

	public void exit() {
		exit = true;
		t.exit();
	}
}
