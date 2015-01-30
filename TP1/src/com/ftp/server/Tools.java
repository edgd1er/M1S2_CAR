package com.ftp.server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class Tools {
	
	  public static String receiveMessage(Socket s) throws IOException{
	        InputStream is = s.getInputStream();
	        InputStreamReader isr = new InputStreamReader(is);
	        BufferedReader br = new BufferedReader(isr);
	        return br.readLine();
	    }
	     
	    public static void sendMessage(Socket s, String message) throws IOException{
	        OutputStream os = s.getOutputStream();
	        DataOutputStream dos = new DataOutputStream(os);
	        if (!message.endsWith("\n")){message+="\n";}
	        dos.writeBytes(message);
	         
	    }

}
