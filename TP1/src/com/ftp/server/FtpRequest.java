package com.ftp.server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class FtpRequest extends Thread {

	private Socket cltSocketCtrl;
	private ServerSocket srvSocketCtrl;
	private String currentUser;
	private String home = Server.prepath;
	private String currentDir = "";
	private DataOutputStream dataOutControl;
	private 	Boolean bUserConnected = true;
	private BufferedReader bufRdr;
	private Boolean clientAuthentified = false;
	private String ftpType;

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
		String adr = srvSocketCtrl.getInetAddress().getHostAddress();
		String message = ErrorCode.getMessage("220", srvSocketCtrl
				.getInetAddress().getHostName().toString());

		try {
			Tools.sendMessage(cltSocketCtrl, message);
		} catch (IOException e) {
			System.err.println(this.getClass().getName() + ": erreur\n");
			e.printStackTrace();
		}
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

				case "SYST":
					processSYST();
					break;

				case "PWD":
					processPWD();
					break;
					
				case "TYPE":
					processTYPE(acmd[1]);
					break;

					
				default:
					System.out.println(this.getClass().toString()
							+ " Erreur, commande non reconnue:" + strcmd);
					Tools.sendMessage(cltSocketCtrl,
							ErrorCode.getMessage("502", ""));
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

		try {
			rep = ErrorCode.getMessage("430", "");
			usrMap = loadPasswordList();

			if (usrMap.containsKey(currentUser)) {
				if (strcmd.equals(usrMap.get(currentUser))) {
					rep = ErrorCode.getMessage("230", "");
					clientAuthentified = true;
				}
			}

			Tools.sendMessage(cltSocketCtrl, rep);
			System.out.println(this.getClass().toString() + " user: "
					+ currentUser + ".isAuth= " + clientAuthentified);

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
		brr.close();
		return usrMap;
	}

	private void killConnection() {
		try {
			clientAuthentified = false;
			currentUser = "Anonymous";
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

	private void processSYST() throws IOException {
		String Rep = ErrorCode.getMessage("215", "");
		Tools.sendMessage(this.cltSocketCtrl, Rep);
		System.out.println(this.getClass().toString() + ": SYST" +Rep);
	}

	private void processPWD() throws IOException {
		String PWD= currentDir;
		String message = ErrorCode.getMessage("257", PWD);
		
		if (clientAuthentified){
			if (currentDir.equals("")){
				PWD= home+ File.separator + currentUser;}
				currentDir=PWD;
				message = ErrorCode.getMessage("257", PWD);
		}
		
		Tools.sendMessage(cltSocketCtrl, message);
		System.out.println(this.getClass().toString() + ": user: "+currentUser +" PWD:"+ PWD);
	}
	
	private void processTYPE(String reponse) throws IOException {
	
		String rep, paramCode;
		if (reponse.equals("A")){
			ftpType="A";
			paramCode="Le mode ASCII a été défini";
		} else {ftpType="I";
		paramCode="Le mode BINAIRE a été défini";}
		
		rep=ErrorCode.getMessage("200", paramCode);
		Tools.sendMessage(cltSocketCtrl, rep);
		System.out.println(this.getClass().toString() + ": TYPE  "+ ftpType);
				
		
	}
}
