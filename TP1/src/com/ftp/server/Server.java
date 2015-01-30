package com.ftp.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import sun.applet.Main;

public class Server {

	/**
	 * @param args
	 */
	static String prepath ="/home/m1/";
	
	
	
	public static void main(String[] args) {
		ServerSocket serverskt=null;
		Boolean keepServingRunning =  true;
		
		try {
			serverskt = new ServerSocket(2100);
			
			while(keepServingRunning){
				   Socket cltSocCtrl = serverskt.accept() ;
				   System.out.println("Server:incoming connection from " + cltSocCtrl.getInetAddress().getHostName());
				   FtpRequest ftpreq = new FtpRequest(serverskt,cltSocCtrl);
				   ftpreq.msgAcceuil();
				}
		} catch (IOException e) {
			System.err.println(Main.class.getName()+": erreur\n");
			e.printStackTrace();
		}
		
	
	}

}
