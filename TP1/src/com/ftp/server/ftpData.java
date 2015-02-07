package com.ftp.server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ftpData extends Thread{

	
	private ServerSocket serversocket;
	private Socket clientsocket;
	
	//constructeur du mode client -> server
	public ftpData(Socket _clientsocket) {
		super();
		this.serversocket = serversocket;
		this.clientsocket = _clientsocket;
	}
	
	//constructeur du mode server -> client actif
	public ftpData(ServerSocket serversocket) {
		super();
		this.serversocket = serversocket;
	}
	
	
	public void connect2Client() throws IOException  {
	// on accepte la connection du client et on recupere la socket
	Socket datasocket  = serversocket.accept();
	// on en tire un canal d'envoi
	OutputStream outStream = datasocket.getOutputStream();
	// que le transforme en dataoutputStream
	DataOutputStream DataOutStream = new DataOutputStream(outStream);

	}
	
	
	
}
