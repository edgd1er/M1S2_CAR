package com.ftp.tools;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.CharBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * support class - send/receive messages on control channel - close sockets and buffers - detection of folder/file - path changing/validity check
 * @author  user
 */
public class Tools {

	static Socket mysocket = null;
	static BufferedReader br = null;
	static PrintWriter osw = null;
	static DataOutputStream dos = null;
	static InputStreamReader isr = null;
	CharBuffer cBuf;
	/**
	 * @uml.property  name="myErrorCode"
	 * @uml.associationEnd  
	 */
	public ErrorCode myErrorCode= null;
	boolean debugMode;

	/**
	 * 
	 * 
	 * @param _s
	 *            socket used for control channel
	 * @throws IOException
	 */
	public Tools(Socket _s,boolean _debugMode) throws IOException {
		mysocket = _s;
		debugMode= _debugMode;
		myErrorCode = new ErrorCode(debugMode);
		osw = new PrintWriter(
				new OutputStreamWriter(mysocket.getOutputStream()), true);
		br = new BufferedReader(
				new InputStreamReader(mysocket.getInputStream()));
	}

	/**
	 * Read message on socket
	 * 
	 */
	public String receiveMessage() throws IOException {
		return br.readLine();
	}

	/**
	 * Write Message on socket
	 * 
	 * @param message
	 * @throws IOException
	 */
	public void sendMessage(String message) throws IOException {
		// dos.writeBytes(message + (message.endsWith("\n")?"":"\n"));
		// dos.flush();
		osw.print(message+"\r\n");
		osw.flush();
		}

	/**
	 * users and password list loading.
	 * 
	 */

	public HashMap<String, String> loadPasswordList(InputStream ipss) throws IOException {

		HashMap<String, String> usrMap = new HashMap<String, String>();
		/*usrMap.put("user","a");
		usrMap.put("pollux","b");
		*/
		
		InputStreamReader ipsrr = new InputStreamReader(ipss);
		BufferedReader brr = new BufferedReader(ipsrr);
		String ligne;
		while ((ligne = brr.readLine()) != null) {
			String[] aligne = ligne.split(":");
			usrMap.put(aligne[0], aligne[1]);
	        if (debugMode){System.out.println(ligne);}
		}
		brr.close();
		ipsrr.close();
		ipss.close();
		
		return usrMap;
	}
	
	
	
	/**
	 * Close the streams and buffer, seems obvious neh ? It says what it does
	 * and contrary also
	 */
	public void CloseStreams() {
		try {

			if (mysocket != null) {
				if (debugMode){System.out.println(this.getClass().toString()+ " closing input/output streams");}
				if (mysocket.isConnected()) {
					sendMessage(myErrorCode.getMessage("221", ""));
				}
				osw.flush();
				br.close();
				osw.close();
				br = null;
				osw = null;
			}

		} catch (IOException e) {
			System.err
					.println(" erreur: Client closed already his sockets.... closing streams properly");
			// e.printStackTrace();
		}
	}

	/**
	 * Is this string an email, let's check.
	 * 
	 * @param emailaTester
	 *            a string to test
	 * @return true/false upon the check (email or non email address)
	 */
	public Boolean isEmail(String emailaTester) {

		boolean res = false;

		boolean mytest = emailaTester
				.matches("[a-zA-Z0-9.-]*@[a-z0-9]*.[a-z]{2,3}");
		if (mytest) {
			res = true;
		}

		return res;
	}

	/**
	 * returns the "localDir" contents in EPLF format
	 * http://cr.yp.to/ftp/list/eplf.html
	 * 
	 * @param localDir
	 * @return
	 */

	public List<String> getDirectoryListing(String localDir) {

		String strTemp = null;
		File thisDir = new File(localDir);
		File[] fileList = thisDir.listFiles();
		List<String> message = new ArrayList<String>();

		if (fileList != null) {
			for (File atomicFile : fileList) {
				if (atomicFile.isFile()) {
					strTemp = "+s" + atomicFile.length() + ",m"
							+ atomicFile.lastModified() / 1000 + ",\011"
							+ atomicFile.getName() + "\015\012";
					// first attempt
					// strTemp = "\053,r,i" + atomicFile.length() +",\011" +
					// atomicFile.getName() + "\015\012";
					message.add(strTemp);
				}
				if (atomicFile.isDirectory()) {
					// first attempt
					// strTemp ="\053m" + atomicFile.lastModified() +",/,\011" +
					// atomicFile.getName() + "\015\012";
					strTemp = "+/,m" + atomicFile.lastModified() / 1000
							+ ",\011" + atomicFile.getName() + "\015\012";
					message.add(strTemp);
				}
			}
		}
		if (message.isEmpty()) {
			message.add("");
		}
		return message;
	}

	/**
	 * 
	 * function test a string to check whether, it's a valid directory path
	 * 
	 * @param parametre
	 *            parameter received, could be a absolute or relative path
	 * @param _CDir
	 *            Current path for the current user
	 * @return String in two parts; first char is 0 or 1 whether, the path is a
	 *         directory or not. second part is the tested path
	 */
	public String checkDir(String parametre, String _CDir) {
		String tempdir = null, res = "0 ";

		if (parametre.startsWith("/")) {
			tempdir = parametre;
		} else {
			// gestion des clients qui envoient les chemins de façons
			// différentes.
			tempdir = _CDir.endsWith(File.separator) ? _CDir : _CDir
					+ File.separator;
			tempdir += parametre.startsWith(File.separator) ? parametre
					.substring(2) : parametre;
		}

		File myFile = new File(tempdir);
		if (myFile.isDirectory()) {
			res = "1 ";
		}

		return res + tempdir;

	}

	/**
	 * get new directory path if it's a valid one.
	 * 
	 * @param _CDir
	 *            user's current path
	 * @param parametre
	 *            relative or absolute path to
	 * @return
	 */
	public String getNewDirectory(String _CDir, String parametre) {
		String tempdir = null;
		Path mypath = null;

		if (parametre.startsWith("/")) {
			tempdir = parametre;
		} else {
			// gestion des clients qui envoient les chemins de façon
			// différentes.
			tempdir = _CDir.endsWith(File.separator) ? _CDir : _CDir
					+ File.separator;
			tempdir += parametre.startsWith(File.separator) ? parametre
					.substring(2) : parametre;
		}

		try {
			mypath = Paths.get(tempdir);
			Path absPath = mypath.toAbsolutePath().normalize();
			if (Files.exists(absPath)) {
				tempdir = absPath.toString();
			} else {
				tempdir = _CDir;
			}
			return tempdir;
		} catch (IOError ioe) {
			return (_CDir);
		}
	}

	/**
	 * Return parent path if possible.
	 * 
	 * @param _cDir
	 *            current user's path
	 * @return parent path
	 */
	public String getParentDir(String _cDir) {
		Path mypath = null, tempPath = null;

		mypath = Paths.get(_cDir);
		tempPath = mypath.getParent();

		if (tempPath != null) {
			return tempPath.toAbsolutePath().normalize().toString();
		} else {
			return _cDir;
		}
	}
}
