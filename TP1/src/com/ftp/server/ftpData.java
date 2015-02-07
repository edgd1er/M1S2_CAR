package com.ftp.server;

import java.io.DataOutputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ftpData extends Thread{

	
	
	
	// on accepte la connection du client et on recupere la socket
	Socket datasocket = dataSrvSocket.accept();
	// on en tire un canal d'envoi
	OutputStream outStream = datasocket.getOutputStream();
	// que le transforme en dataoutputStream
	DataOutputStream DataOutStream = new DataOutputStream(outStream);

	
	
	
	
}
