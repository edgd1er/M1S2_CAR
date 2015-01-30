package com.ftp.server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import sun.org.mozilla.javascript.tools.shell.QuitAction;

public class FtpRequest {

	Socket cltSocketCtrl;
	ServerSocket srvSocketCtrl;
	String currentUser;
	String home = Server.prepath;
	DataOutputStream dataOutControl;
	Boolean bUserConnected = true;
	BufferedReader bufRdr;

	public FtpRequest(ServerSocket _srvSocket, Socket _clskt) {
		cltSocketCtrl = _clskt;
		srvSocketCtrl = _srvSocket;
	}

	// lancement du futur thread
	public void run() {

		try {
			BufferedReader bufRdr = new BufferedReader(new InputStreamReader(
					cltSocketCtrl.getInputStream()));
			DataOutputStream dataOutControl = new DataOutputStream(
					cltSocketCtrl.getOutputStream());
		} catch (Exception e) {
			System.err.println(this.getClass().getName()
					+ ": erreur, cloture de la connection\n");
			e.printStackTrace();
			killConnection();
			return;
		}
		msgAcceuil();
		processRequest();

	}

	public void msgAcceuil() {

		System.out.println(this.getClass() + " :client ip: "
				+ cltSocketCtrl.getInetAddress().toString());
		String adr = srvSocketCtrl.getInetAddress().getHostName();
		String message = ErrorCode.getMessage("220", srvSocketCtrl
				.getInetAddress().getHostName().toString());

		try {
			Tools.sendMessage(cltSocketCtrl, message);
		} catch (IOException e) {
			System.err.println(this.getClass().getName() + ": erreur\n");
			e.printStackTrace();
		}

		this.processRequest();
	}

	// ecoute les commandes envoyés et lance le process adéquat
	public void processRequest() {

		String strcmd = null;

		try {
			strcmd = Tools.receiveMessage(cltSocketCtrl);

			while (strcmd != null && bUserConnected) {
				String[] acmd = strcmd.split(" ");

				switch (acmd[0]) {
				case "USER":
					processUSER(acmd[1]);
					break;

				case "PASS":
					processPASS(acmd[1]);
					break;

				default:
					System.out.println(this.getClass().toString()
							+ " Erreur, commande non reconnue:" + strcmd);
					Tools.sendMessage(cltSocketCtrl, ErrorCode.getMessage("502",""));
					break;
				}

				strcmd = Tools.receiveMessage(cltSocketCtrl);
			}// while

		} catch (IOException e1) {
			System.out.println(this.getClass().toString() + " erreur\n");
			e1.printStackTrace();

		}
		/*
		 * (strcmd.startsWith("SYST")) {processSYST(strcmd);} else { if
		 * (strcmd.startsWith("FEAT")) {processFEAT(strcmd);} else { if
		 * (strcmd.startsWith("PWD")) {processPWD(strcmd);} else { if
		 * (strcmd.startsWith("TYPE I")) {processTYPE(strcmd);} else { if
		 * (strcmd.startsWith("PASV")) {processPASV(strcmd);} else { if
		 * (strcmd.startsWith("LIST")) {processLIST(strcmd);} else { if
		 * (strcmd.startsWith("CWD")) {processCWD(strcmd);} else { if
		 * (strcmd.startsWith("CDUP")) {processCDUP(strcmd);} else { if
		 * (strcmd.startsWith("RETR")) {processRETR(strcmd);} else { if
		 * (strcmd.startsWith("STOR")) {processSTOR(strcmd);} }}}}}}}}}}}
		 */

		try {
			strcmd = Tools.receiveMessage(cltSocketCtrl);
		} catch (IOException e) {
			System.out.println(this.getClass().toString()
					+ " erreur: requesting close connection\n");
			e.printStackTrace();
			this.bUserConnected = false;
		}

		killConnection();

	}

	private void processPASS(String strcmd) {

		HashMap<String, String> usrMap;
		String rep = "";
		String temppwd = "";

		try {
			rep = ErrorCode.getMessage("430", "");
			usrMap = loadPasswordList();
			
			if (usrMap.containsKey(currentUser)) {
			temppwd = usrMap.get(currentUser);
			boolean b = strcmd.equals(usrMap.get(currentUser));
			if (strcmd.equals(usrMap.get(currentUser))) {
				rep = ErrorCode.getMessage("230", "");
			}
			}
			Tools.sendMessage(cltSocketCtrl, rep);

		} catch (IOException ioe) {
			System.out
					.println(this.getClass().toString()
							+ " erreur: Impossible de charger la listes des utilisateurs,mdp\n");
			ioe.printStackTrace();
			killConnection();
		}

	}

	// Chargement de la liste des users et des mdp...
	private HashMap<String, String> loadPasswordList() throws IOException {

		String TableDesMdps = "mdp.txt";
		HashMap<String, String> usrMap = new HashMap<String, String>();

		InputStream ipss = new FileInputStream(TableDesMdps);
		InputStreamReader ipsrr = new InputStreamReader(ipss);
		BufferedReader brr = new BufferedReader(ipsrr);
		String ligne;
		while ((ligne = brr.readLine()) != null) {
			String[] aligne = ligne.split(":");
			usrMap.put(aligne[0], aligne[1]);
		}
		return usrMap;
	}

	private void killConnection() {
		try {
			System.out.println(this.getClass().toString()
					+ " Killing the connection");
			this.cltSocketCtrl.close();
			this.srvSocketCtrl.close();
		} catch (Exception e) {
			System.out.println(this.getClass().toString()
					+ " erreur: requesting close connection\n");
			e.printStackTrace();
		}

	}

	private void processUSER(String strcmd) throws IOException {

		this.currentUser = strcmd.trim();
		String Rep = ErrorCode.getMessage("331", "");
		Tools.sendMessage(this.cltSocketCtrl, Rep);
		System.out.println(this.getClass().toString() + " " + strcmd
				+ "> currentUser:" + this.currentUser);

	}

}
