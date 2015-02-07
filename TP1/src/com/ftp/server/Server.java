package com.ftp.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

	/**
	 * @param args
	 */
	static String prepath ;
	public static int nbClients;
	
	
	
	public static void main(String[] args) {
		ServerSocket serverskt=null;
		Boolean keepServingRunning =  true;
		int nbMAxCLients=1;
		FtpRequest ftpreq=null;
				 
		 prepath = "/tmp/homedir";
		
		try {
			serverskt = new ServerSocket(2100);
			
			while(keepServingRunning){
				   Socket cltSocCtrl = serverskt.accept() ;
				   System.out.println("Server:incoming connection from " + cltSocCtrl.getInetAddress().getHostName());
				   
				   if (nbClients>= nbMAxCLients) {
					   ftpreq = new FtpRequest(serverskt,cltSocCtrl,true);
				   }else {
					   ftpreq = new FtpRequest(serverskt,cltSocCtrl,false);
				   }

				   if (ftpreq !=null){
					   ftpreq.start();
					   nbClients++;}
				   
				   // thread start
				   //ftpreq.start();
				   System.out.println("Server:started thread FTPRequest from " + cltSocCtrl.getInetAddress().getHostName());
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
