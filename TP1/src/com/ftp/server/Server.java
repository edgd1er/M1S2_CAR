package com.ftp.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

	/**
	 * @param args
	 */
	static String prepath ="/home/m1/";
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ServerSocket serverskt=null;
		
		try {
			serverskt = new ServerSocket(2100);
			
			while(true){
				   Socket clskt = serverskt.accept() ;
				   FtpRequest ftpreq = new FtpRequest(clskt);
				   ftpreq.msgAcceuil();
				}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	
	}

}
