package com.ftp.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 
 * FTP Server FTP in Java
 * 
 * main start listening on port 2100 (21 unavailable, not in root mode).
 * whenever a client is connecting, the server starts a FtpRequest thread and
 * continue to listen for following clients.
 * 
 * @param args
 *            1- homedir for users 2- debugmode: 0/1
 * 
 */
public class Server {

	static String prepath;
	public static int nbClients;
	public static boolean debugMode;

	public static void main(String[] args) {
		ServerSocket serverskt = null;
		Boolean keepServingRunning = true;
		int nbMAxCLients = 3, serverPort = 2100;
		FtpRequest ftpreq = null;
		debugMode = false;
		prepath = "/tmp/homedir";

		String messageLog = "\nStarting FTP Server on port "
				+ String.valueOf(serverPort)
				+ "\nMaximum number of clients accepted: "
				+ String.valueOf(nbMAxCLients)
				+ "\nServer homedir has been set by ";

		if (args.length > 0) {
			if (args[0].startsWith("/")) {
				prepath = args[0].endsWith("/") ? args[0].substring(0,
						args[0].length() - 1) : args[0];
				messageLog += " an argument at runtime: " + prepath;
			} else {
				messageLog += "default without argument at runtime: " + prepath;
			}
			if (args.length > 1) {
				Server.debugMode = args[1].equals("1") ? true : false;
			}
		}

		System.out.println(messageLog);

		try {
			serverskt = new ServerSocket(serverPort);

			while (keepServingRunning) {
				Socket cltSocCtrl = serverskt.accept();
				String clientIP = cltSocCtrl.getLocalAddress().getHostAddress();
				System.out.println("Server:incoming connection from "
						+ clientIP);

				if (nbClients >= nbMAxCLients) {
					ftpreq = new FtpRequest(cltSocCtrl, true);
				} else {
					ftpreq = new FtpRequest(cltSocCtrl, false);
				}

				if (ftpreq != null) {
					ftpreq.start();
					nbClients++;
				}

				System.out.println("Server:started thread FTPRequest for "
						+ cltSocCtrl.getInetAddress().getHostName());
			}
			//server is stopping
			serverskt.close();
		} catch (IOException e) {
			System.err.println(Thread.currentThread().getClass().getName()
					+ ": errorr\n");
			e.printStackTrace();
			try {
				serverskt.close();
			} catch (Exception e1) {
				System.err.println(Thread.currentThread().getClass().getName()
						+ ": errorr\n");
				e1.printStackTrace();
			}
		}

	}

}
