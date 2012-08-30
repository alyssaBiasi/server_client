/*
 *	 COSC1179 - Network Programming
 *  	 Assignment - Multicasting Client Thread Q2.1: Accepts connections to receive 
 *														data, outputs & passes it on.
 *														Adjusts for message delay.
 *  	 Alyssa Biasi s3328976
 *   
 */

import java.text.*;
import java.util.*;
import java.io.*;
import java.net.*;

public class MClientThread02 extends Thread {
	private final String HOST = "localhost";
	private int myPort;
	private boolean exit;
	
	public MClientThread02(int p) {
		this.myPort = p;
		exit = false;
	}
	

	public void run() {
		try {
			//Opens server socket to listen to assigned port.
			ServerSocket listening = new ServerSocket(myPort);
			
			while(!exit) {
				//Accepts connection to receive incoming message.
				Socket incoming = listening.accept();
				BufferedReader in = new BufferedReader(new InputStreamReader(incoming.getInputStream()));
				
				String message = in.readLine();
				if(message == null) {
					System.err.print("Error in communication.");
					break;
				}
				
				//Tokenizes message around " "
				ArrayList<String> tokens = new ArrayList<String>();
				StringTokenizer st = new StringTokenizer(message, " ");

				while(st.hasMoreTokens()) {
					tokens.add(st.nextToken());
				}
				
				//Date & time stored as first element.
				String dateTime = tokens.get(0) + " " + tokens.get(1);
				//This client's port stores as second element.
				tokens.remove(2);
				
				if(dateTime.equals("Exit now.")) {
					System.out.println("\nServer closing.\n");
					exit = true;
				}
				else {
					dateTime = adjustTime(dateTime);
					System.out.println(dateTime);
				}
				
				//Determines if there are more clients.
				if(tokens.size() <= 2) {
					continue;
				}

				int nextPort = Integer.parseInt(tokens.get(2));
				passOnMessage(tokens, nextPort);
				
				incoming.close();
			}
		} catch(NumberFormatException e) {
			System.err.println("ClientThread run NF: " + e.getMessage());
			System.exit(1);
		} catch(IOException e) {
			System.err.println("ClientThread run IO: " +e.getMessage());
			System.exit(1);
		} catch(IndexOutOfBoundsException e) {
			System.err.println("ClientThread run IOB: " +e.getMessage());
			System.exit(1);
		}
		
		System.exit(0);
	}
	
	
	private void passOnMessage(ArrayList<String> a, int nextPort) {
		String message = "";
		
		for(int i=0; i<a.size(); i++) {
			message += " " + a.get(i);
		}

		try {
			Socket nextClient = new Socket(HOST, nextPort);
			PrintWriter toNext = new PrintWriter(nextClient.getOutputStream(), true);
			toNext.println(message);
			
			toNext.close();
			nextClient.close();
		} catch(IOException e) {
			System.err.println("ClientThread run IO: " +e.getMessage());
			System.exit(1);
		}
	}
	
	
	//Calculates the time it took to receive data & adjusts output string.
	private String adjustTime(String dateTime) {
		StringTokenizer st = new StringTokenizer(dateTime, " ");
		String time = st.nextToken();
		String date = st.nextToken();
		
		//Time
		st = new StringTokenizer(time, ":");
		int hour = Integer.parseInt(st.nextToken());
		int min = Integer.parseInt(st.nextToken());
		int sec = Integer.parseInt(st.nextToken());
		int ms = Integer.parseInt(st.nextToken());
		
		//Date
		st = new StringTokenizer(date, ":");
		int day = Integer.parseInt(st.nextToken());
		int month = Integer.parseInt(st.nextToken());
		int yr = Integer.parseInt(st.nextToken());

		//Calendar: Created using received values.
		Calendar received = Calendar.getInstance();
		received.set(yr, (month-1), day, hour, min, sec);
		received.set(Calendar.MILLISECOND, ms);
		
		//Calculating delay: Uses calendar set with current time
		//					 & finds the difference in ms from epoch.
		Calendar now = Calendar.getInstance();
		long nowTime = now.getTimeInMillis();
		long receivedTime = received.getTimeInMillis();
		double delay = (double) nowTime - receivedTime;
		
		//Adjusts values.
		ms += delay;
		if(ms >=1000) {
			sec++;
			ms = 0;
			if(sec>=60) {
				min++;
				sec = 0;
			}
			if(min>=60) {
				hour++;
				min = 0;
			}
		}
		
		//Creates new string.
		DecimalFormat df = new DecimalFormat("00.##");
		dateTime = df.format(hour)+":"+df.format(min)+":"+df.format(sec)+":"+df.format(ms);
		dateTime += " "+df.format(day)+":"+df.format(month)+":"+df.format(yr);
		return dateTime;
	}
	
	
	public void exit() {
		exit = true;
	}
}
