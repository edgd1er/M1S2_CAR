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

	//est ce un email
	public static Boolean isEmail(String emailaTester){
		
		boolean res= false;
	
		boolean mytest= emailaTester.matches("[a-zA-Z0-9.-]*@[a-z0-9]*.[a-z]{2,3}");
		if (mytest) {
			res = true;
		}
		
		return res;
	} 
	// returns the localDir contents
	public static List<String> getDirectoryListing(String localDir) {

		String strTemp=null;
		File thisDir = new File(localDir);
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
		if (message.isEmpty()){message.add("");}
		return message;
	}

	public static String checkDir(String parametre, String _CDir) {
		// TODO
		String tempdir=null, res="0 ";
		
		if (parametre.startsWith("/"))
		{
			tempdir= parametre;
		}
		else {
			//gestion des clients qui envoient les chemins de façon différentes.
			tempdir = _CDir.endsWith(File.separator)?_CDir :_CDir + File.separator ;
			tempdir += parametre.startsWith(File.separator)?parametre.substring(2):parametre;
		}
		
		//tempdir=tempdir.endsWith("/")?tempdir:tempdir+"/";
		File myFile = new File(tempdir);
		if (myFile.isDirectory()){res="1 ";}
		
		return res+tempdir;
		
	}

}
