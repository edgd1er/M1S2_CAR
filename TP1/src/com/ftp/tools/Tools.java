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
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.GroupPrincipal;
import java.nio.file.attribute.PosixFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * support class - send/receive messages on control channel - close sockets and
 * buffers - detection of folder/file - path changing/validity check
 * 
 * @author user
 */
public class Tools {

	static Socket mysocket = null;
	static BufferedReader br = null;
	static PrintWriter osw = null;
	static DataOutputStream dos = null;
	static InputStreamReader isr = null;
	CharBuffer cBuf;
	/**
	 * @uml.property name="myErrorCode"
	 * @uml.associationEnd
	 */
	public ErrorCode myErrorCode = null;
	boolean debugMode;

	/**
	 * 
	 * 
	 * @param _s
	 *            socket used for control channel
	 * @throws IOException
	 */
	public Tools(Socket _s, boolean _debugMode) throws IOException {
		mysocket = _s;
		debugMode = _debugMode;
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
		osw.print(message + "\r\n");
		osw.flush();
	}

	/**
	 * users and password list loading.
	 * 
	 */

	public HashMap<String, String> loadPasswordList(InputStream ipss)
			throws IOException {

		HashMap<String, String> usrMap = new HashMap<String, String>();
		/*
		 * usrMap.put("user","a"); usrMap.put("pollux","b");
		 */

		InputStreamReader ipsrr = new InputStreamReader(ipss);
		BufferedReader brr = new BufferedReader(ipsrr);
		String ligne;
		while ((ligne = brr.readLine()) != null) {
			String[] aligne = ligne.split(":");
			usrMap.put(aligne[0], aligne[1]);
			if (debugMode) {
				System.out.println(ligne);
			}
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
				if (debugMode) {
					System.out.println(this.getClass().toString()
							+ " closing input/output streams");
				}
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

		String size, date, permTypeString, hardlink, user, group;
		File thisDir = new File(localDir);
		File[] fileList = thisDir.listFiles();
		List<String> message = new ArrayList<String>();

		StringBuilder sb;

		if (fileList != null) {
			for (File atomicFile : fileList) {

				//FTPFile regex
				//([bcdelfmpSs-])(((r|-)(w|-)([xsStTL-]))((r|-)(w|-)([xsStTL-]))((r|-)(w|-)([xsStTL-])))\+?\s*(\d+)\s+(?:(\S+(?:\s\S+)*?)\s+)?(?:(\S+(?:\s\S+)*)\s+)?(\d+(?:,\s*\d+)?)\s+((?:\d+[-/]\d+[-/]\d+)|(?:\S{3}\s+\d{1,2})|(?:\d{1,2}\s+\S{3}))\s+(\d+(?::\d+)?)\s+(\S*)(\s*.*)
				//-rwxrwxrwx 1 user 02 25 2015 9685 000 26 00 Class_server.svg
				
				//http://cr.yp.to/ftpparse/ftpparse.c
				/* UNIX-style listing, without inum and without blocks */
				/* "-rw-r--r--   1 root     other        531 Jan 29 03:26 README" */
				/* "dr-xr-xr-x   2 root     other        512 Apr  8  1994 etc" */

				permTypeString = getTypePermString(atomicFile);
				date = getFileDate(atomicFile);
				size = String.valueOf(atomicFile.length());
				try {
					hardlink = java.nio.file.Files.getAttribute(
							atomicFile.toPath(), "unix:nlink").toString();
				} catch (IOException e) {
					hardlink = "1";
				}

				try {
					user = Files.getOwner(atomicFile.toPath()).toString();
				} catch (IOException e) {
					user = "unknown";
				}
				user = user.length()>8?user.substring(0,7):user;
				GroupPrincipal groupP;
				try {
					groupP = Files.readAttributes(
							atomicFile.toPath(), PosixFileAttributes.class,
							LinkOption.NOFOLLOW_LINKS).group();
					group = groupP.toString();
				} catch (IOException e) {
					group="unknown";
				}
				group = group.length()>8?group.substring(0,7):group;

				sb = new StringBuilder();
				sb.append(permTypeString);
				sb.append(' ');
				sb.append(hardlink);
				sb.append(' ');
				sb.append(user);
				sb.append(' ');
				sb.append(group);
				sb.append(' ');
				sb.append(size);
				sb.append(' ');
				sb.append(date);
				sb.append(" ");
				sb.append(atomicFile.getName());

				message.add(sb.toString());
			}

			/*
			 * if (atomicFile.isFile()) { strTemp = "+s" + atomicFile.length() +
			 * ",m" + atomicFile.lastModified() / 1000 + ",\011" +
			 * atomicFile.getName() + "\015\012";
			 * 
			 * // first attempt // strTemp = "\053,r,i" + atomicFile.length()
			 * +",\011" + // atomicFile.getName() + "\015\012";
			 * 
			 * } if (atomicFile.isDirectory()) { // first attempt // strTemp
			 * ="\053m" + atomicFile.lastModified() +",/,\011" + //
			 * atomicFile.getName() + "\015\012"; strTemp = "+/,m" +
			 * atomicFile.lastModified() / 1000 + ",\011" + atomicFile.getName()
			 * + "\015\012"; message.add(strTemp); }
			 * 
			 * }
			 */
		}
		if (message.isEmpty()) {
			message.add("");
		}
		return message;
	}

	/**
	 * Return the last modified data as MMM DD YYYY for file older than 6 months
	 * MM DD YYYY HH:MM for newer ones
	 * 
	 * @param thisFile
	 * @return
	 */
	public String getFileDate(File thisFile) {

		DateTime fdate = new DateTime(thisFile.lastModified());
		Interval inter = new Interval(fdate, new DateTime());
		DateTimeFormatter fmt;

		// detail for recent files
		if (inter.contains(fdate)) {
			fmt = DateTimeFormat.forPattern("MMM dd HH:mm").withLocale(Locale.US);
		} else {
			fmt = DateTimeFormat.forPattern("MM dd yyyy").withLocale(Locale.US);
		}
		String date = (new DateTime(thisFile.lastModified())).toString(fmt);
		return date;
	}

	/**
	 * return type d for directory, f for file, - for unknown and permissions
	 * RWX for user, group and others
	 * 
	 * @param thisFile
	 * @return
	 */
	public String getTypePermString(File thisFile) {
		String d = "", r = "", x = "", w = "", rights = "";

		d = thisFile.isDirectory() ? "d" : "-";
		r = thisFile.canRead() ? "r" : "-";
		w = thisFile.canWrite() ? "w" : "-";
		x = thisFile.canExecute() ? "x" : "-";

		/*
		 * PosixFileAttributes attrs = Files.getFileAttributeView(thisFile,
		 * PosixFileAttributeView.class).readAttributes(); rights =
		 * PosixFilePermissions.toString(attrs.permissions());
		 * java.nio.file.Files.getAttribute(thisFile.toPath(), "unix:");
		 */

		rights = d;
		for (int i = 0; i <= 2; i++) {
			rights += r + w + x;
		}
		return rights;
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
