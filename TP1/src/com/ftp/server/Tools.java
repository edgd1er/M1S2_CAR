package com.ftp.server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Tools {

	static InputStream is =null;
	static InputStreamReader isr = null;
	static BufferedReader br =null;
	static OutputStream os =null;
	static DataOutputStream dos=null;
	
	Tools(Socket s) throws IOException{
		is = s.getInputStream();
		isr = new InputStreamReader(is);
		br = new BufferedReader(isr);
		os = s.getOutputStream();
		dos = new DataOutputStream(os);
		s.setKeepAlive(true);

	}
	
	// Read mesasge on socket
	public String receiveMessage() throws IOException {
		 //is = s.getInputStream();
		//isr = new InputStreamReader(is);
		 br = new BufferedReader(isr);
		return br.readLine();
	}

	// Write Message on socket
	public void sendMessage(String message) throws IOException {
		//os = s.getOutputStream();
		//dos = new DataOutputStream(os);
		if (!message.endsWith("\n")) {
			message += "\n";
		}
		dos.writeBytes(message);
		dos.flush();

	}
	
	// ça dit ce que ça fait et l'inverse aussi ;)
	public void CloseStreams() {
		try {
			System.out.println(" closing input/output streams");
			dos.writeBytes("226 Fermeture de la connection");
			dos.close();
			os.close();
			br.close();
			isr.close();
			is.close();
			
		} catch (Exception e) {
			System.out.println(" erreur: requesting closing streams");
			e.printStackTrace();
		}
	}
	//est ce un email
	public Boolean isEmail(String emailaTester){
		
		boolean res= false;
	
		boolean mytest= emailaTester.matches("[a-zA-Z0-9.-]*@[a-z0-9]*.[a-z]{2,3}");
		if (mytest) {
			res = true;
		}
		
		return res;
	} 
	// returns the localDir contents
	public List<String> getDirectoryListing(String localDir) {

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

	//retourne 1 si l'element testé est un dossier
	public String checkDir(String parametre, String _CDir) {
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

	// retourne le chemin demandé s'il existe 
	public String getNewDirectory(String _CDir, String parametre) {
		// TODO Auto-generated method stub
		String tempdir=null;
		Path mypath=null;
		
		if (parametre.startsWith("/"))
		{
			tempdir= parametre;
		}
		else {
			//gestion des clients qui envoient les chemins de façon différentes.
			tempdir = _CDir.endsWith(File.separator)?_CDir :_CDir + File.separator ;
			tempdir += parametre.startsWith(File.separator)?parametre.substring(2):parametre;
		}
		
		try{
			mypath = Paths.get(tempdir);
			Path absPath = mypath.toAbsolutePath().normalize();
			if (Files.exists(absPath))
				{
				tempdir= absPath.toString();
				}else {tempdir=_CDir;}
			return tempdir;
		}
		catch(IOError ioe) {
			return (_CDir);
			}
		}

	// retourne le dossier parent s'il existe
	public String getParentDir(String _cDir) {
		// TODO Auto-generated method stub
		Path mypath=null, tempPath=null;
		
		mypath = Paths.get(_cDir);
			tempPath=  mypath.getParent();
			
			if (tempPath!=null){
				return tempPath.toAbsolutePath().normalize().toString();
		}
			else {
			return _cDir;
		}
	}

}
