package com.ftp.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

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
public class Server extends Thread {

	String prepath;
	int nbClients;
	boolean debugMode;
	ServerSocket serverskt;
	Boolean keepServingRunning;
	int serverPort;
	FtpRequest ftpreq;
	private Socket cltSocCtrl;
	private String clientIP;
	ConcurrentLinkedQueue<FtpRequest> ftpReqList;

	public void initialization(int _serverPort, String _homedir,
			boolean _debugMode) {

		debugMode = false;
		serverPort = -1;
		debugMode = _debugMode;
		keepServingRunning = true;

		prepath = "/tmp/homedir";

		if (_serverPort < 1024 || serverPort > 65535) {
			String messageLog = "\n Ouch, the port given is not in a valid range ( 1024-65535)!!! Exit\n";
			System.err.println(messageLog);
			return;
		}

		serverPort = _serverPort;

		try {
			serverskt = new ServerSocket(serverPort);
		} catch (IOException e) {
			System.err.print("!!!!!Error, cannot open ServerSocket on port "
					+ serverPort + " !!!!!!!!!!!!");
			e.printStackTrace();
		}
	}

	public Boolean getKeepServingRunning() {
		return keepServingRunning;
	}

	public void setKeepServingRunning(Boolean keepServingRunning) {
		this.keepServingRunning = keepServingRunning;
	}

	public void run() {

		String messageLog = "\nStarting FTP Server on port "
				+ String.valueOf(serverPort)
				+ "\nServer homedir has been set to " + prepath;

		System.out.println(messageLog);

		try {

			while (keepServingRunning) {
				cltSocCtrl = serverskt.accept();
				clientIP = cltSocCtrl.getLocalAddress().getHostAddress();
				System.out.println("Server:incoming connection from "
						+ clientIP);

				ftpreq = new FtpRequest(cltSocCtrl, prepath,debugMode);

				if (ftpreq != null) {
					ftpreq.start();
				}

				System.out.println("Server:started thread FTPRequest for "
						+ cltSocCtrl.getInetAddress().getHostName() + ".");
			}
			// server is stopping
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


	/**
	 * Return the serveur socket mainly for test purpose.
	 * 
	 * @return serverSocket
	 */
	public ServerSocket getServeurSocket() {
		return serverskt;
	}

	/**
	 * Return accepted socket from client
	 * 
	 * @return socket
	 */
	public Socket getSocket() {
		return cltSocCtrl;
	}

}
