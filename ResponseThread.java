/*
 *	 COSC1179 - Network Programming
 *  	 Assignment - Multicasting Response Thread Q2.2: Sends date & time data 
 *  													 to first client.
 *  	 Alyssa Biasi s3328976
 *   
 */

import java.net.*;
import java.text.*;
import java.util.*;
import java.io.*;

public class MResponseThread02 extends Thread {
	private final String HOST = "localhost";
	private boolean exit;
	private ArrayList<Integer> clientPorts;
	private long lastTime;
	
	public MResponseThread02() {
		exit = false;
		lastTime = 0;
	}

	
	public void updateList(ArrayList<Integer> array) {
		clientPorts = array;
	}
	
	
	public void run() {
		try {
			while(!exit) {
				if(needToSend()) {
					send();
				}
				else {
					Thread.sleep(3000);
				}
			}

			//To send exit command.
			if(needToSend()) {
				send();
			}
		} catch(InterruptedException e) {
			System.err.println("ResponseThread run IE:" + e.getMessage());
		}
		
		System.exit(0);
	}
	
	
	private boolean needToSend() {
		if(clientPorts.size() == 0) {
			return false;
		}
		
		Calendar cal = Calendar.getInstance();
		long now = cal.getTimeInMillis();
		
		double diff = (double) now - lastTime;   //Difference in ms.
	//	double diffSec = diff/1000;
		diff = diff/ (60*1000);   //Convert to mins.
		
		if(diff >= 1) {
			lastTime = now;
			return true;
		}
		
		if(exit) {
			return true;
		}
		else {
			return false;
		}
	}
	
	
	private void send() {
		String ports = "";
		for(int i=0; i<clientPorts.size(); i++) {
			ports += " " + clientPorts.get(i);
		}
		
		try {
			Socket client = new Socket(HOST, clientPorts.get(0));
			
			PrintWriter toClient = new PrintWriter(client.getOutputStream(), true);
			
			Calendar cal = Calendar.getInstance();
			DateFormat df = new SimpleDateFormat("HH:mm:ss:SS dd:MM:yyyy");
			Date date = cal.getTime();
			
			if(exit) {
				toClient.println("Exit now." + ports);
			}
			else {
				toClient.println(df.format(date) + ports);
				System.out.println(df.format(date) + ports);
			}
		} catch(IOException e) {
			System.err.println("ResponseThread send: " + e.getMessage());
			System.exit(1);
		}
	}
	

	public void exit() {
		exit = true;
	}
}
