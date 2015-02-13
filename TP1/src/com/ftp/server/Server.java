package com.ftp.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

	
	static String prepath ;
	public static int nbClients;
	
	
	/**
	 * Server FTP en Java
	 * 
	 * Le main lance le programme principal qui sera chargé d'écouter sur le port 21
	 * et de passer la main a un thread FTPRequest qui va gérer les commandes envoyées par le client FTP
	 * 
	 * @param args	non utilisé
	 */
	public static void main(String[] args) {
		ServerSocket serverskt=null;
		Boolean keepServingRunning =  true;
		int nbMAxCLients=3;
		FtpRequest ftpreq=null;
				 
		 prepath = "/tmp/homedir";
		
		try {
			serverskt = new ServerSocket(2100);
			
			while(keepServingRunning){
				   Socket cltSocCtrl = serverskt.accept() ;
				   String clientIP = cltSocCtrl.getLocalAddress().getHostAddress();
				   System.out.println("Server:incoming connection from " + clientIP );
				   
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
