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
import java.util.Arrays;
import java.util.HashMap;

public class FtpRequest extends Thread {

	private Socket cltSocketCtrl;
	private ServerSocket srvSocketCtrl;
	private String currentUser = "";
	private String home = Server.prepath;
	private String currentDir = "";
	private Integer ftpetat;;
	private String commande = "";
	private String parametre = "";
	private boolean KeepRunning = true;
	private String ftpType = "";

	public FtpRequest(ServerSocket _srvSocket, Socket _clskt) {
		cltSocketCtrl = _clskt;
		srvSocketCtrl = _srvSocket;
		ftpetat = ftpEtat.FS_WAIT_LOGIN;
	}

	// lancement du futur thread
	public void run() {
		msgAccueil();
		try {
			while (KeepRunning) {
				parseCommande(Tools.receiveMessage(cltSocketCtrl));
				switch (ftpetat) {
				case ftpEtat.FS_WAIT_LOGIN:
					processUSER();
					break;
				case ftpEtat.FS_WAIT_PASS:
					processPASS();
					break;
				case ftpEtat.FS_LOGGED:
					processRequest();
					break;
				}
			}
		} catch (Exception e) {
			killConnection();
			KeepRunning = false;
			e.printStackTrace();
		}
	}


	public void msgAccueil() {

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

	//reconnaissance des commandes
	//http://www.iana.org/assignments/ftp-commands-extensions/ftp-commands-extensions.xhtml
	//
	private void parseCommande(String receiveMessage) {
		String tempcom="", temppar="";
		String[] ftpcmdList= { "ABOR","ACCT", "ALLO", "APPE", "CWD", "DELE", "HELP", "LIST", "MODE", "NLST", "NOOP",
			    "PASS", "PASV", "PORT", "QUIT", "REIN", "REST", "RETR", "RNFR", "RNTO", "SITE", "STAT",
			    "STOR", "STRU", "TYPE", "USER","CDUP", "MKD", "PWD", "RMD", "SMNT","STOU", "SYST", "LIST","NLST", "PASV", "REST",
			    "SITE" , "STOU" };
		if (receiveMessage!=null){
		int idx= receiveMessage.indexOf(" ");
		if (idx!=-1 ){
			tempcom=receiveMessage.substring(0, idx);
			temppar=receiveMessage.substring(idx+1, receiveMessage.length());
		}

		if (Arrays.asList(ftpcmdList).contains(tempcom)){
			commande= tempcom;
			parametre= temppar;
		} 
		}
	}

	
	// ecoute les commandes envoyés et lance le process adéquat
	public void processRequest() {

		try {
			if (commande.toUpperCase().startsWith("USER")) {
				processUSER();
			} else if (commande.toUpperCase().startsWith("PASS")) {
				processPASS();
			} else if (commande.toUpperCase().startsWith("SYST")) {
				processSYST();
			} else if (commande.toUpperCase().startsWith("PWD")) {
				processPWD();
			} else if (commande.toUpperCase().startsWith("TYPE")) {
				processTYPE();
			} else if (commande.toUpperCase().startsWith("FEAT")){
				processFEAT(); 
			} else {
				System.out.println(this.getClass().toString()
						+ " Erreur, commande non reconnue:" + commande + " "
						+ parametre);

				Tools.sendMessage(cltSocketCtrl,"500 Syntax error, command unrecognized");
			}
	
		} catch (IOException e1) {
			System.out.println(this.getClass().toString() + " erreur\n");
			e1.printStackTrace();
			KeepRunning = false;
			killConnection();
		}
		/*
		 
		 * (strcmd.startsWith("PASV")) {processPASV(strcmd);} else { if
		 * (strcmd.startsWith("LIST")) {processLIST(strcmd);} else { if
		 * (strcmd.startsWith("CWD")) {processCWD(strcmd);} else { if
		 * (strcmd.startsWith("CDUP")) {processCDUP(strcmd);} else { if
		 * (strcmd.startsWith("RETR")) {processRETR(strcmd);} else { if
		 * (strcmd.startsWith("STOR")) {processSTOR(strcmd);} }}}}}}}}}}}
		 */
	}

	// recuperation du login utilisateur
	private void processUSER() throws IOException {

		// Verification des conditions d'entrées
		if (this.commande.toUpperCase().startsWith("USER")	&& (parametre.length() > 1)) {
			this.currentUser = parametre;
			String Rep = ErrorCode.getMessage("331", "");
			Tools.sendMessage(this.cltSocketCtrl, Rep);
			System.out.println(this.getClass().toString() + " " + parametre	
					+ "> currentUser:" + this.currentUser);
			ftpetat= ftpEtat.FS_WAIT_PASS;
		} else {
			ErrorParametre("430", ErrorCode.getMessage("430", ""));
		}

	}

	private void processPASS() {

		HashMap<String, String> usrMap;
		String rep = "";
		// Verification des conditions d'entrées
		if (!this.commande.toUpperCase().startsWith("PASS")
				|| (currentUser.equals("anonymous"))
				|| (currentUser.length() < 1)) {

			try{
			ErrorParametre("430", ErrorCode.getMessage("430", ""));
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}

		try {
			// error par défaut
			rep = ErrorCode.getMessage("430", "");
			// Chargement de la liste des mdp
			usrMap = loadPasswordList();

			// verification du login/pwd
			if (usrMap.containsKey(currentUser)) {
				if (parametre.equals(usrMap.get(currentUser))) {
					rep = ErrorCode.getMessage("230", "");
					ftpetat=ftpEtat.FS_LOGGED;
				}
			}

			Tools.sendMessage(cltSocketCtrl, rep);
			System.out.println(this.getClass().toString() + " user: "
					+ currentUser + ".isAuth= " + ftpetat);

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
			KeepRunning = false;
			currentUser = "Anonymous";
			System.out.println(this.getClass().toString()
					+ " Killing the connection");
			this.cltSocketCtrl.close();

		} catch (Exception e) {
			System.out.println(this.getClass().toString()
					+ " erreur: requesting close connection\n");
			e.printStackTrace();
		}

	}

	private void processSYST() throws IOException {

		if (commande.equalsIgnoreCase("syst")) {
			String Rep = ErrorCode.getMessage("215", "");
			Tools.sendMessage(this.cltSocketCtrl, Rep);
			System.out.println(this.getClass().toString() + ": SYST" + Rep);
		} else {
			ErrorParametre("502", ErrorCode.getMessage("502", ""));
		}
	}

	// recuperation du chemin du serveur
	private void processPWD() throws IOException {
		String PWD, message;

		if (commande.equalsIgnoreCase("pwd")) {

			PWD = currentDir.equals("") ? home + File.separator + currentUser
					: currentDir;
			message = ErrorCode.getMessage("257", PWD);

			Tools.sendMessage(cltSocketCtrl, message);
			System.out.println(this.getClass().toString() + ": user: "
					+ currentUser + " PWD:" + PWD);
		} else {
			ErrorParametre("502", ErrorCode.getMessage("502", ""));
		}
	}

	// definition du type de fichier a transferer
	private void processTYPE() throws IOException {
		
		String rep="502", paramCode="Mode Type inconnu";
		if (commande.equalsIgnoreCase("type")){
		if (parametre.equalsIgnoreCase("A")) {
			ftpType = "A";
			paramCode = "Le mode ASCII a été défini";
		}else  if (parametre.equalsIgnoreCase("A")){
			ftpType = "I";
			paramCode = "Le mode BINAIRE a été défini";
		} 

		rep = ErrorCode.getMessage("200", paramCode);
		Tools.sendMessage(cltSocketCtrl, rep);
		System.out.println(this.getClass().toString() + ": TYPE  " + ftpType);
		}
	}

	// presentation des features du serveur
	private void processFEAT() throws IOException {
			Tools.sendMessage(cltSocketCtrl, "211- Features");
			Tools.sendMessage(cltSocketCtrl, "Ben, en fait il n'ya en pas");
			Tools.sendMessage(cltSocketCtrl, "vraiment pas");
			Tools.sendMessage(cltSocketCtrl, "211 EndFeature");
	}
	// envoi du message d'erreur sur la socket cliente
	private void ErrorParametre(String string, String message) throws IOException {

		ErrorCode.sendErrorMessage(cltSocketCtrl, string, message);

	}
}
