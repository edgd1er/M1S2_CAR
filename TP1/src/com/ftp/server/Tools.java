package com.ftp.server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Tools {

	static Socket mysocket = null;
	static BufferedReader br =null;
	
	static PrintWriter osw = null;
	private Charset ASCII  = Charset.forName("ASCII");
	private Charset UTF8  = Charset.forName("UTF-8");
	//private Charset cp8850 = Charset.forName("ISO-8850-1");
	private CharsetEncoder encoder = null; 
	CharBuffer cBuf;
	//BinMode
	static DataOutputStream dos=null;
	static InputStreamReader isr = null;
 
	
	public Tools(Socket _s) throws IOException{
		mysocket= _s;
		osw = new PrintWriter(new OutputStreamWriter(mysocket.getOutputStream()),true);
		//mysocket.setKeepAlive(true);
		//binMode
		//dos = new DataOutputStream(mysocket.getOutputStream());
		br = new BufferedReader(new InputStreamReader(mysocket.getInputStream()));

	}
	
	// Read message on socket
	String receiveMessage() throws IOException {
		return  br.readLine();
	}

	// Write Message on socket
	void sendMessage(String message) throws IOException {
		//dos.writeBytes(message + (message.endsWith("\n")?"":"\n"));
		//dos.flush();
		osw.println(message);
		osw.flush();
	}
	
	// ça dit ce que ça fait et l'inverse aussi ;)
	public void CloseStreams() {
		try {
			
			if (mysocket!=null){
				System.out.println(" closing input/output streams");
			if (mysocket.isConnected()){
				sendMessage(ErrorCode.getMessage("221", ""));
			}
			osw.flush();
			br.close();
			osw.close();
			br=null;
			osw=null;
			}
			
		} catch (Exception e) {
			System.out.println(" erreur: Client closed already his sockets.... closing streams properly");
			//e.printStackTrace();
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
