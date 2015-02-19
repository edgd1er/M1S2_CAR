package com.ftp.server;

/**
 * Main class start the server thread
 */
public class Main {
	/** Creation, initialisation and démarrage du server **/
	public static void main(String[] args) {
		Server myServeur = new Server();
		String usersHomedir = "", messageLog = "";
		boolean debugMode = false;

		messageLog = "Ftp Server will be started with users home directory defined by ";

		if (args.length > 0) {
			if (args[0].startsWith("/")) {
				usersHomedir = args[0].endsWith("/") ? args[0].substring(0,
						args[0].length() - 1) : args[0];
				messageLog += " an argument at runtime: " + usersHomedir;
			}
		} else {
			messageLog += "default without argument at runtime: "
					+ usersHomedir;
		}

		if (args.length > 1) {
			debugMode = args[1].equals("1") ? true : false;
			if (debugMode) {
				messageLog += " in debugMode (ie verbose)";
			}
		}
		System.out.println(messageLog);

		myServeur.initialization(2100, usersHomedir, debugMode);
		myServeur.start();
	}
}