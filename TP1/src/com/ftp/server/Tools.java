package com.ftp.server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Tools {

	// Read mesasge on socket
	public static String receiveMessage(Socket s) throws IOException {
		InputStream is = s.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		return br.readLine();
	}

	// Write Message on socket
	public static void sendMessage(Socket s, String message) throws IOException {
		OutputStream os = s.getOutputStream();
		DataOutputStream dos = new DataOutputStream(os);
		if (!message.endsWith("\n")) {
			message += "\n";
		}
		dos.writeBytes(message);

	}

	// returns the localDir contents
	public static List<String> getDirectoryListing(String localDir) {

		String strTemp=null;
		//File thisDir = new File(localDir);
		//TODO Changer ce chemin
		File thisDir = new File("/tmp");
		File[] fileList =thisDir.listFiles();
		List<String> message = new ArrayList<String>();
		
		if (fileList!=null){
			for(File atomicFile : fileList){
				if (atomicFile.isFile()){
					strTemp = "\053,r,i" + atomicFile.length() +",\011" + atomicFile.getName() + "\015\012";
				message.add(strTemp);
				}
				if (atomicFile.isDirectory()){
					strTemp ="\053m" + atomicFile.lastModified() +",/,\011" + atomicFile.getName() + "\015\012"; 
				message.add(strTemp);
				}
			}
		}

		return message;
	}

}
