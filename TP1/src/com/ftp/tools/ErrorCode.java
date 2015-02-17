package com.ftp.tools;

import java.io.IOException;
import java.util.HashMap;

import com.ftp.server.Server;

/**
 * 
 * Tool class to send an error code and its associated text to ftp client,
 * sysout or syserr if needed.
 * 
 * @author user
 *
 */
public class ErrorCode {

	HashMap<String, String> ftpErrorMap=null;
	ErrorCode myErrorCode = null;

	public ErrorCode(){
		@SuppressWarnings("unused")
		HashMap<String, String> ftpErrorMap = new HashMap<String, String>();
		populateCode();
	}


	/**
	 * Return a complete message given an errorcode, a texte to replace PARAM by
	 * paramCode content in the stored message.
	 * 
	 * 
	 * @param _ErrorCode
	 *            Ftp ErrorCode to process.
	 * @param _paramCode
	 *            string to replace "PARAM" in the message.
	 * 
	 * @return a complete string errorCode + text.
	 */
	public String getMessage(String _ErrorCode, String _paramCode) {

		String message = "";
		try {
			message = _ErrorCode + " " + ftpErrorMap.get(_ErrorCode);
		} catch (Exception e) {
			message = _ErrorCode + " " + ftpErrorMap.get("501");
			_paramCode = "";
			System.err.println("ErrorCode: interesting, unknown error: "
					+ _ErrorCode + "\n");
			e.printStackTrace();
		} finally {
			if (!(_paramCode == null)) {
				return message.contains("PARAM") ? message.replace("PARAM",
						_paramCode) : message;

			}
		}
		return message;

	}

	/**
	 * Send on the control channel a string containing the errorcode and its
	 * associated text message, message is send to system error output.
	 * 
	 * @param _mytools
	 *            Tools class to handle IO on sockets
	 * @param rep
	 *            String containing ftp errorcode and an associated text
	 * @param ParamCode
	 *            piece of string to complete the send message. the PARAM text
	 *            will be replaced by ParamCode
	 * @param message
	 *            printed message on sysout.
	 *
	 * @throws IOException
	 */
	public static void sendErrorMessage(Tools _mytools, String rep,
			String messageLog) throws IOException {
		System.err.println("!!!!!!Erreur: " + " " + messageLog + " " + rep);
		_mytools.sendMessage(rep);

	}

	/**
	 * Send on the control channel the errorcode, its associated text message,
	 * eventually completed by paramCode, message is send to system error
	 * output.
	 * 
	 * @param _mytools
	 *            Tools class to handle IO on sockets
	 * @param errorcode
	 *            errorcode according to ftp rfc
	 * @param ParamCode
	 *            piece of string to complete the send message. the PARAM text
	 *            will be replaced by ParamCode
	 * @param message
	 *            printed message on sysout.
	 */
	public void sendErrorMessage(Tools _mytools, String errorcode,
			String ParamCode, String message) throws IOException {
		String tempString = getMessage(errorcode, ParamCode);
		System.err.println("!!!!!!Erreur: " + " " + message + tempString);
		_mytools.sendMessage(tempString);
	}

	/**
	 * Send on the control channel the errorcode, its associated text message,
	 * eventually completed by paramCode, message is send to system out.
	 * 
	 * @param _mytools
	 *            Tools class to handle IO on sockets
	 * @param errorcode
	 *            errorcode according to ftp rfc
	 * @param ParamCode
	 *            piece of string to complete the send message. the PARAM text
	 *            will be replaced by ParamCode
	 * @param message
	 *            printed message on sysout.
	 */
	public void sendCodeMessage(Tools _mytools, String errorcode,
			String ParamCode, String message) throws IOException {
		String tempString = getMessage(errorcode, ParamCode);
		if (Server.debugMode) {
			System.out.println(message + " " + tempString);
		}
		_mytools.sendMessage(tempString);
	}

	/**
	 * Sends on the control channel, the rep as a complete message sends
	 * messageLog to the standard output
	 * 
	 * @param _mytools
	 *            Tools handle
	 * @param rep
	 *            Complete message to send
	 * @param messageLog
	 *            message to write to sysout.
	 * @throws IOException
	 */
	public static void sendCodeMessage(Tools _mytools, String rep,
			String messageLog) throws IOException {
		if (Server.debugMode) {
			System.out.println(messageLog + " " + rep);
		}
		_mytools.sendMessage(rep);

	}

	/**
	 * ErrorCode, Message dictionary.
	 * 
	 * Populate will fill ftpErrorMap.
	 */
	private void populateCode() {
		ftpErrorMap = new HashMap<String,String>();
		ftpErrorMap
				.put("100",
						"The requested action is being initiated, expect another reply before proceeding with a new command..");
		ftpErrorMap.put("110", "Restart marker replay .");
		ftpErrorMap.put("120", "Service ready in 02 minutes.");
		ftpErrorMap.put("125",
				"Data connection already open; transfer starting.");
		ftpErrorMap.put("150",
				"File status okay; about to open data connection.");
		ftpErrorMap.put("200", "PARAM.");
		ftpErrorMap.put("202",
				"Command not implemented, superfluous at this site.");
		ftpErrorMap.put("211", " 	System status, or system help reply.");
		ftpErrorMap.put("212", "Directory status.");
		ftpErrorMap.put("213", "File status.");
		ftpErrorMap.put("214", "Help message.");
		ftpErrorMap.put("215", "Master 1 CAR 2014-2015 FTP Java server. PARAM");
		ftpErrorMap
				.put("220",
						" 	Service ready for new user on PARAM. login using the USER command.");
		ftpErrorMap.put("221", "Service closing control connection.PARAM");
		ftpErrorMap.put("225",
				"Data connection open; no transfer in progress.PARAM");
		ftpErrorMap
				.put("226",
						"Closing data connection. Requested file action successful.PARAM");
		ftpErrorMap.put("227", "Entering Passive Mode (PARAM).");
		ftpErrorMap.put("228", "Entering Long Passive Mode (PARAM).");
		ftpErrorMap.put("229", "Entering Extended Passive Mode (|||port|).");
		ftpErrorMap.put("230",
				"User logged in, proceed. Logged out if appropriate.PARAM");
		ftpErrorMap.put("231", "User logged out; service terminated.PARAM");
		ftpErrorMap.put("232",
				"Logout command noted, will complete when transfer done.");
		ftpErrorMap.put("250", "PARAM");
		ftpErrorMap.put("257", "PARAM");
		ftpErrorMap
				.put("300",
						"The command has been accepted, but the requested action is on hold, pending receipt of further information.");
		ftpErrorMap.put("331", "User name okay, need password.PARAM");
		ftpErrorMap.put("332", "Need account for login.");
		ftpErrorMap.put("350",
				"Requested file action pending further information.PARAM");
		ftpErrorMap
				.put("400",
						"The command was not accepted and the requested action did not take place, but the error condition is temporary and the action may be requested again.");
		ftpErrorMap
				.put("421",
						"Service not available, closing control connection. This may be a reply to any command if the service knows it must shut down.PARAM");
		ftpErrorMap.put("425", "Can't open data connection.");
		ftpErrorMap.put("426", "Connection closed; transfer aborted.");
		ftpErrorMap.put("430", "Invalid username or password");
		ftpErrorMap.put("434", "Requested host unavailable.");
		ftpErrorMap.put("450", "Requested file action not taken.PARAM");
		ftpErrorMap.put("451",
				"Requested action aborted. Local error in processing.");
		ftpErrorMap
				.put("452",
						"Requested action not taken. Insufficient storage space in system.File unavailable (e.g., file busy).");
		ftpErrorMap
				.put("500",
						"Syntax error, command unrecognized and the requested action did not take place. This may include errors such as command line too long.");
		ftpErrorMap.put("501", "Syntax error in parameters or arguments.PARAM");
		ftpErrorMap.put("502", "Command not implemented.");
		ftpErrorMap.put("503", "bad sequence of command.");
		ftpErrorMap.put("504", "Command not implemented for that parameter.");
		ftpErrorMap.put("530", "Not logged in.");
		ftpErrorMap.put("532", "Need account for storing files.");
		ftpErrorMap
				.put("550",
						"Requested action not taken. File unavailable (e.g., file not found, no access).PARAM");
		ftpErrorMap.put("551", "Requested action aborted. Page type unknown.");
		ftpErrorMap
				.put("552",
						"Requested file action aborted. Exceeded storage allocation (for current directory or dataset).");
		ftpErrorMap.put("553",
				"Requested action not taken. File name not allowed.");
		ftpErrorMap.put("600",
				"Replies regarding confidentiality and integrity");
		ftpErrorMap.put("631", "Integrity protected reply.");
		ftpErrorMap.put("632",
				" 	Confidentiality and integrity protected reply.");
		ftpErrorMap.put("633", " 	Confidentiality protected reply.");
		ftpErrorMap.put("10000", "");
		ftpErrorMap.put("10054", "");
		ftpErrorMap.put("10060", "Cannot connect to remote server.");
		ftpErrorMap
				.put("10061",
						"Cannot connect to remote server. The connection is actively refused by the server.");
		ftpErrorMap.put("10066", "Directory not empty.");
		ftpErrorMap.put("10068", "Too many users, server is full.");

	}

}