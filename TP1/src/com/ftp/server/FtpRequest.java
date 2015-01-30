package com.ftp.server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;


public class FtpRequest {

	Socket clskt;
	String currentUser;
	String home = Server.prepath;
	OutputStream osControl;
	DataOutputStream dosControl;
	

	public FtpRequest(Socket _clskt) {
		// TODO Auto-generated constructor stub
		clskt = _clskt;
		
		
	}

	public void processRequest(String _command) {
		// TODO Auto-generated method stub
		String[] acmd = _command.split(" ");
		String cmd = acmd[0];
		Switch s = new Switch();
		 
		if (acmd.length<1){ try {
			Tools.sendMessage(clskt, "502 	Command not implemented.");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} }
		if (acmd.length > 1) {
			String param = acmd[1];
		}

		/*
	      try {
	         if (args[0].equalsIgnoreCase("ON")) {
	            s.storeAndExecute(switchUp);
	            System.exit(0);
	         }
	         if (args[0].equalsIgnoreCase("OFF")) {
	            s.storeAndExecute(switchDown);
	            System.exit(0);
	         }
	         System.out.println("Argument \"ON\" or \"OFF\" is required.");
	      } catch (Exception e) {
	         System.out.println("Argument's required.");
	      }
*/
	   }

	public void msgAcceuil() {
		
	try {
		Tools.sendMessage(clskt, "Bienvenue sur le serveur FTP, identifiez vous avec la commande user.");
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	String cmd = null;
	try {
		while(cmd==null)
		{cmd = Tools.receiveMessage(clskt);}
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	System.out.println("commande recue: " + cmd +"\n");
	processRequest(cmd);
		
	}
		
}
