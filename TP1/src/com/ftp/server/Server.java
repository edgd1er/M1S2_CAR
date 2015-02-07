package com.ftp.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

	/**
	 * @param args
	 */
	static String prepath ;
	
	
	
	public static void main(String[] args) {
		ServerSocket serverskt=null;
		Boolean keepServingRunning =  true;
		
				 
		 prepath = "/tmp/homedir";
		
		try {
			serverskt = new ServerSocket(2100);
			
			while(keepServingRunning){
				   Socket cltSocCtrl = serverskt.accept() ;
				   System.out.println("Server:incoming connection from " + cltSocCtrl.getInetAddress().getHostName());
				   FtpRequest ftpreq = new FtpRequest(serverskt,cltSocCtrl);
				   ftpreq.run();
				   // thread start
				   //ftpreq.start();
				   System.out.println("Server:closed connection from " + cltSocCtrl.getInetAddress().getHostName());
				}
			
			serverskt.close();
		} catch (IOException e) {
			System.err.println(Thread.currentThread().getClass().getName()+": erreur\n");
			e.printStackTrace();
			try {
			serverskt.close();}
			catch(Exception e1){
				e1.printStackTrace();
			}
		}
		
	
	}

}
