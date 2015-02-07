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
import java.util.List;

public class FtpRequest extends Thread {

	private Socket cltSocketCtrl;
	private ServerSocket srvSocketCtrl;
	private ServerSocket dataSrvSocket;
	private Socket datasocket;
	private String cltDataAddr;
	private Integer cltDataPort;
	private String currentUser = "";
	private String home = Server.prepath;
	private String currentDir = "";
	private Integer ftpetat;;
	private String commande = "";
	private String parametre = "";
	private boolean KeepRunning = true;
	private String ftpType = "";
	boolean tooManyClient = false;
	boolean isPASV = false;
	Tools mytools = null;

	public FtpRequest(ServerSocket _srvSocket, Socket _clskt, boolean _tooMAny) {
		cltSocketCtrl = _clskt;
		srvSocketCtrl = _srvSocket;
		tooManyClient = _tooMAny;
		ftpetat = ftpEtat.FS_WAIT_LOGIN;
	}

	// lancement du futur thread
	public void run() {

		try {
			mytools = new Tools(cltSocketCtrl);
		} catch (IOException e) {
			System.out.println("ca commence mal, le client est parti....");
			KeepRunning = false;
		}

		if (tooManyClient) {
			String message = ErrorCode
					.getMessage(
							"421",
							" La limite du nombre de client connectes a ete depasse. Veuillez reessayer plus tard.");
			try {
				mytools.sendMessage(message);
				killConnection();
			} catch (Exception e) {
				// Rien A Faire: cloture de la connection de tt façon.
				e.printStackTrace();
			}
		} else {
			msgAccueil();
		}

		try {
			while (KeepRunning) {
				parseCommande(mytools.receiveMessage());
				System.out.println("Commande reçue a traiter :" + commande
						+ " " + parametre);
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
				commande = "";
				parametre = "";
			}
		} catch (Exception e) {
			e.printStackTrace();
			killConnection();
			KeepRunning = false;

		}
		// fin de try
		System.out.println("fin de cette connection.");
	}

	public void msgAccueil() {

		System.out.println(this.getClass() + " :client ip: "
				+ cltSocketCtrl.getInetAddress().toString());
		String message = ErrorCode.getMessage("220", srvSocketCtrl
				.getInetAddress().toString()
				+ ". il y a actuellement "
				+ Server.nbClients + " utilisateur(s) en ligne");

		try {
			mytools.sendMessage(message);
		} catch (IOException e) {
			System.err.println(this.getClass().getName() + ": erreur\n");
			e.printStackTrace();
		}
	}

	// reconnaissance des commandes
	// http://www.iana.org/assignments/ftp-commands-extensions/ftp-commands-extensions.xhtml
	//
	private void parseCommande(String receiveMessage) {
		String tempcom = "", temppar = "";
		String[] ftpcmdList = { "ABOR", "ACCT", "ALLO", "APPE", "CWD", "DELE",
				"HELP", "LIST", "MODE", "NLST", "NOOP", "PASS", "PASV", "PORT",
				"QUIT", "REIN", "REST", "RETR", "RNFR", "RNTO", "SITE", "STAT",
				"STOR", "STRU", "TYPE", "USER", "CDUP", "MKD", "PWD", "RMD",
				"SMNT", "STOU", "SYST", "LIST", "NLST", "PASV", "REST", "SITE",
				"STOU" };
		if (receiveMessage != null) {
			int idx = receiveMessage.indexOf(" ");
			if (idx != -1) {
				tempcom = receiveMessage.substring(0, idx);
				temppar = receiveMessage.substring(idx + 1,
						receiveMessage.length());
			} else {
				tempcom = receiveMessage;
				temppar = "";
			}

			if (Arrays.asList(ftpcmdList).contains(tempcom)) {
				commande = tempcom;
				parametre = temppar;
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
			} else if (commande.toUpperCase().startsWith("FEAT")) {
				processFEAT();
			} else if (commande.toUpperCase().startsWith("PASV")) {
				processPASV();
			} else if (commande.toUpperCase().startsWith("PORT")) {
				processPORT();
			} else if (commande.toUpperCase().startsWith("LIST")) {
				processLIST();
			} else if (commande.toUpperCase().startsWith("RETR")) {
				processRETR();
			} else if (commande.toUpperCase().startsWith("CWD")) {
				processCWD();
			} else if (commande.toUpperCase().startsWith("QUIT")) {
				processQUIT();
			} else {
				System.out.println(this.getClass().toString()
						+ " Erreur, commande non reconnue:" + commande + " "
						+ parametre);

				mytools.sendMessage("500 Syntax error, command unrecognized");
			}

		} catch (IOException e1) {
			System.out.println(this.getClass().toString() + " erreur\n");
			e1.printStackTrace();
			KeepRunning = false;
			killConnection();
		}
		/*
		 * (strcmd.startsWith("CWD")) {processCWD(strcmd);} else { if
		 * (strcmd.startsWith("CDUCommandeP")) {processCDUP(strcmd);} else { if
		 * (strcmd.startsWith("RETR")) {processRETR(strcmd);} else { if
		 * (strcmd.startsWith("STOR")) {processSTOR(strcmd);} }}}}}}}}}}}
		 */
	}

	// envoie vers le client d'un fichier
	private void processRETR() throws IOException {
		String rep = "502";
		if (this.commande.toUpperCase().startsWith("RETR")) {

		}
		mytools.sendMessage(rep);
		System.out.println(this.getClass().toString()
				+ " RETR: envoie du fichier" + parametre);
	}

	// recuperation du login utilisateur
	private void processUSER() throws IOException {

		// Verification des conditions d'entrées
		if (this.commande.toUpperCase().startsWith("USER")
				&& (parametre.length() > 1)) {
			this.currentUser = parametre;
			String Rep = ErrorCode.getMessage("331", "");
			mytools.sendMessage(Rep);
			System.out.println(this.getClass().toString() + " " + parametre
					+ "> currentUser:" + this.currentUser);
			ftpetat = ftpEtat.FS_WAIT_PASS;
			commande = "";
			parametre = "";
		} else {
			ErrorParametre("430", ErrorCode.getMessage("430", ""));
		}

	}

	private void processPASS() throws IOException {

		HashMap<String, String> usrMap;
		String rep = ErrorCode.getMessage("430", "");

		// Verification des conditions d'entrées
		if (!this.commande.toUpperCase().startsWith("PASS")
				|| (currentUser.length() < 1)) {
			rep = ErrorCode.getMessage("430", "");
			// gestion du mode anonyme
		} else if (currentUser.equals("anonymous")) {
			if (mytools.isEmail(parametre) == true) {
				ftpetat = ftpEtat.FS_LOGGED;
				rep = ErrorCode.getMessage("230", "");
			}
		} else {
			try {
				// Chargement de la liste des mdp
				usrMap = loadPasswordList();

				// verification du login/pwd
				if (usrMap.containsKey(currentUser)) {
					if (parametre.equals(usrMap.get(currentUser))) {
						rep = ErrorCode.getMessage("230", "");
						ftpetat = ftpEtat.FS_LOGGED;
					}
				}
			} catch (IOException ioe) {
				System.out
						.println(this.getClass().toString()
								+ " erreur: Impossible de charger la listes des utilisateurs,mdp\n");
				ioe.printStackTrace();
			} catch (Exception e) {
				System.out
						.println(this.getClass().toString()
								+ " erreur: utilisateur absent de la liste des utilisateurs. Ce cas ne devrait pas arriver.( anonymous ou \n");
				e.printStackTrace();

			}
		}

		// Homedir
		currentDir = (ftpetat == ftpEtat.FS_LOGGED) ? home + File.separator
				+ currentUser : "";
		mytools.sendMessage(rep);
		// log
		System.out.println(this.getClass().toString() + " user: " + currentUser
				+ ".status= " + ftpetat
				+ "(0=wait_login, 1=wait_pass, 2=logged)");

		if (ftpetat != ftpEtat.FS_LOGGED) {
			// rien ne va plus, envoi du refus de pass et cloture de la
			// connection.
			killConnection();
		}

	}

	// Chargement de la liste des users et des mdp...
	private HashMap<String, String> loadPasswordList() throws IOException {

		// String TableDesMdps = "TP1" + File.separator + "mdp.txt";
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

	private void processSYST() throws IOException {

		if (commande.equalsIgnoreCase("syst")) {
			String Rep = ErrorCode.getMessage("215", "");
			mytools.sendMessage(Rep);
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
			message = ErrorCode.getMessage("257", "\"" + PWD + "\" ");

			mytools.sendMessage(message);
			System.out.println(this.getClass().toString() + ": user: "
					+ currentUser + " PWD:" + PWD);

		} else {
			ErrorParametre("502", ErrorCode.getMessage("502", ""));
		}
	}

	// definition du type de fichier a transferer
	private void processTYPE() throws IOException {

		String rep = "502", paramCode = "Mode Type inconnu";
		if (commande.equalsIgnoreCase("type")) {
			if (parametre.equalsIgnoreCase("A")) {
				ftpType = "A";
				paramCode = "Le mode ASCII a été défini";
			} else if (parametre.equalsIgnoreCase("I")) {
				ftpType = "I";
				paramCode = "Le mode BINAIRE a été défini";
			}

			rep = ErrorCode.getMessage("200", paramCode);
			mytools.sendMessage(rep);
			System.out.println(this.getClass().toString() + ": TYPE  "
					+ ftpType);
		}
	}

	// presentation des features du serveur
	private void processFEAT() throws IOException {
		mytools.sendMessage("211- Features");
		mytools.sendMessage("Ben, en fait il n'ya en pas");
		mytools.sendMessage("vraiment pas");
		mytools.sendMessage("211 EndFeature");
	}

	private void processPORT() throws IOException {
		// 227 Entering Passive Mode (192,168,150,90,195,149).
		String rep = "501", paramCode = "Erreur dans le numero du port fourni.";
		if (commande.equalsIgnoreCase("port")) {
			if (parametre.matches("([0-9]{1,3},){5}[0-9]{1,3}")) {
				System.out.println("processPORT" + commande + " " + parametre);
				// recuperation de l @ et port client
				String[] atmp = parametre.split(",");
				cltDataAddr = atmp[0] + "." + atmp[1] + "." + atmp[2] + "."
						+ atmp[3];
				cltDataPort = Integer.parseInt(atmp[4]) * 256
						+ Integer.parseInt(atmp[5]);
				// Port correct ?
				if (cltDataPort<65535 && cltDataPort>1023){
					rep="200";

					paramCode = "Port accepted on "
							+ cltDataAddr + ":" + String.valueOf(cltDataPort);
				}
			}
				rep = ErrorCode.getMessage(rep, paramCode);
				mytools.sendMessage(rep);
		}

	}

	private void processPASV() throws IOException {
		// 227 Entering Passive Mode (192,168,150,90,195,149).
		String rep = "502", paramCode = "";
		if (commande.equalsIgnoreCase("pasv")) {
			isPASV = true;
			dataSrvSocket = new ServerSocket(0);

			String port_url = getEncPort();
			String msg = "ProcessPASV: creation de la ServerSocket "
					+ dataSrvSocket.getInetAddress().getHostAddress() + ":"
					+ dataSrvSocket.getLocalPort();
			rep = ErrorCode.getMessage("227", port_url);

			mytools.sendMessage(rep);
			System.out.println(msg);

		}

	}

	// calcul de l'url a partir de l'@ IP et du port
	private String getEncPort() {
		String port_url = "";
		port_url = dataSrvSocket.getInetAddress().getHostAddress();
		// .replace(".", ",");

		String p1 = String.valueOf(dataSrvSocket.getLocalPort() / 256);
		String p2 = String.valueOf(dataSrvSocket.getLocalPort() % 256);

		return port_url + "," + p1 + "," + p2;

	}

	// envoi du message d'erreur sur la socket cliente
	private void ErrorParametre(String errorcode, String message)
			throws IOException {

		ErrorCode.sendErrorMessage(mytools, errorcode, message);

	}

	private void killConnection() {
		try {
			KeepRunning = false;
			currentUser = "Anonymous";
			System.out.println(this.getClass().toString()
					+ " Killing the connection: il reste " + --Server.nbClients
					+ " utilisateur(s).");

			mytools.CloseStreams();
			this.cltSocketCtrl.close();

		} catch (Exception e) {
			System.out.println(this.getClass().toString()
					+ " erreur: requesting close connection\n");
			e.printStackTrace();
		}

	}

	private void processLIST() throws IOException {
		//
		String rep = "502", paramCode = "Erreur de traitement";
		if (commande.equalsIgnoreCase("list")) {

			// amorce de l'envoi
			mytools.sendMessage(ErrorCode.getMessage("150", ""));
			List<String> msg = mytools.getDirectoryListing(currentDir);
			// envoi du listing
			if (cltDataAddr != null) {
				datasocket = new Socket(cltDataAddr, cltDataPort);
				DataOutputStream DataOutStream = new DataOutputStream(
						datasocket.getOutputStream());

				for (String strTemp : msg) {
					DataOutStream.writeBytes(strTemp);
				}
				// nice and quiet close
				DataOutStream.flush();
				DataOutStream.close();
			}

			datasocket.close();

			mytools.sendMessage(ErrorCode.getMessage("226", ""));

			String strTemp = "ProcessList: contenu du repertoire local"
					+ msg.toString();
			System.out.println(strTemp);

		}

	}

	// changement de repertoire
	private void processCWD() throws IOException {

		String rep = "502", paramCode = "Dossier inexistant, le dossier actuel reste "
				+ currentDir;
		String tempdir = null;

		if (commande.equalsIgnoreCase("cwd")) {

			// TODO verification des droits de changement
			tempdir = mytools.checkDir(parametre, currentDir);
			if (tempdir.startsWith("1")) {
				rep = "200";
				currentDir = tempdir.substring(2);
				paramCode = "dossier courant devient " + currentDir;
			}
		}

		mytools.sendMessage(ErrorCode.getMessage(rep, paramCode));

		String strTemp = "ProcessCWD: repertoire local" + currentDir;
		System.out.println(strTemp);

	}

	// changement de repertoire
	private void processQUIT() throws IOException {
		String rep = "221", paramCode = "Fin de la connection demandee.";

		if (commande.equalsIgnoreCase("quit")) {
			mytools.sendMessage(ErrorCode.getMessage(rep, paramCode));
			killConnection();
			String strTemp = "ProcessQuit: fin de la connection FTP demandee.";
			System.out.println(strTemp);
		}

	}
}
