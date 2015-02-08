package com.ftp.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class FtpRequest extends Thread {

	private Socket cltSocketCtrl;
	private ServerSocket srvSocketCtrl;
	private ServerSocket dataSrvSocket;
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
	private ftpData myftpData;

	public FtpRequest(ServerSocket _srvSocket, Socket _clskt, boolean _tooMAny) {
		cltSocketCtrl = _clskt;
		srvSocketCtrl = _srvSocket;
		tooManyClient = _tooMAny;
		ftpetat = ftpEtat.FS_WAIT_LOGIN;
	}

	// lancement du futur thread
	public void run() {

		String rep = "", paramCode = "";
		try {
			mytools = new Tools(cltSocketCtrl);
		} catch (IOException e) {
			System.out.println("ca commence mal, le client est parti....");
			KeepRunning = false;
		}

		if (tooManyClient) {
			rep = "421";
			paramCode = " La limite du nombre de client connectes a ete depasse. Veuillez reessayer plus tard.";
			try {
				mytools.sendMessage(ErrorCode.getMessage(rep, paramCode));
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
				processRequest();
				commande = "";
				parametre = "";
			}
		}
		// Exception dans le KeepRunning ou le code sous jacent
		catch (Exception e) {
			e.printStackTrace();
			killConnection();
			KeepRunning = false;

		}
		// fin de try
		System.out.println("Fin de ce Thread.");
	}

	public void msgAccueil() {

		String rep = "220";
		String paramCode = srvSocketCtrl.getInetAddress().toString()
				+ ". il y a actuellement " + Server.nbClients
				+ " utilisateur(s) en ligne";

		System.out.println(this.getClass() + " :client ip: "
				+ ErrorCode.getMessage(rep, paramCode));

		try {
			mytools.sendMessage(ErrorCode.getMessage(rep, paramCode));
		} catch (IOException e) {
			System.err.println(this.getClass().getName()
					+ ": msgAccueil erreur\n");
			e.printStackTrace();
		}
	}

	// reconnaissance des commandes
	// http://www.iana.org/assignments/ftp-commands-extensions/ftp-commands-extensions.xhtml
	//
	private void parseCommande(String receiveMessage) {
		int idx = -1;
		String tempcom = "", temppar = "";
		String[] ftpcmdList = { "ABOR", "ACCT", "ALLO", "APPE", "CWD", "DELE",
				"HELP", "LIST", "MODE", "NLST", "NOOP", "PASS", "PASV", "PORT",
				"QUIT", "REIN", "REST", "RETR", "RNFR", "RNTO", "SITE", "STAT",
				"STOR", "STRU", "TYPE", "USER", "CDUP", "MKD", "PWD", "RMD",
				"SMNT", "STOU", "SYST", "LIST", "NLST", "PASV", "REST", "SITE",
				"STOU" };
		if (receiveMessage != null) {
			idx = receiveMessage.indexOf(" ");
			tempcom = receiveMessage;
			if (idx != -1) {
				tempcom = receiveMessage.substring(0, idx);
				temppar = receiveMessage.substring(idx + 1,
						receiveMessage.length());
			}

			if (Arrays.asList(ftpcmdList).contains(tempcom)) {
				commande = tempcom;
				parametre = temppar;
			}
		}
	}

	//Pattern ETAT
	// ecoute les commandes envoyés et lance le process adéquat
	public void processRequest() {
		
		try {
			switch (ftpetat) {
			case ftpEtat.FS_WAIT_LOGIN:
				if (commande.toUpperCase().startsWith("USER")) {processUSER();}
				break;
			case ftpEtat.FS_WAIT_PASS:
				if (commande.toUpperCase().startsWith("USER")) {processUSER();}
				if (commande.toUpperCase().startsWith("PASS")) {processPASS();}
				break;
			case ftpEtat.FS_LOGGED:
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
			} else if (commande.toUpperCase().startsWith("STOR")) {
				processSTOR();
			} else if (commande.toUpperCase().startsWith("RETR")) {
				processRETR();
			} else if (commande.toUpperCase().startsWith("CDUP")) {
				processCDUP();
			} else {
				String rep ="500",
				messageSoc="Syntax error, command unrecognized",
				messageLog="";
				ErrorParametre(rep,messageSoc ,messageLog);
			}
			break;
			}

		} catch (IOException e1) {
			KeepRunning = false;
			System.out.println(this.getClass().toString() + " erreur\n");
			e1.printStackTrace();
			killConnection();
		}
	}

	// envoie vers le client d'un fichier
	// TODO A faire
	private void processRETR() throws IOException {
		String rep = "501";
		String paramCode = " le Thread n a pas ete initialisé";
		String messageLog = this.getClass().toString()
				+ " RETR: depot du fichier sur le serveur " + parametre;
		;

		if (this.commande.toUpperCase().startsWith("RETR")) {
			
			if (myftpData!=null){
				myftpData.setCommande(commande);
				myftpData.setParametre(parametre);
				myftpData.run();
				
				rep="150";
				paramCode="Opening " + parametre + " en mode data connection.\n";
				mytools.sendMessage(ErrorCode.getMessage(rep, paramCode));
				while(myftpData.isAlive()){}
				rep=myftpData.getReturnstatus();
				
			}
			
			mytools.sendMessage(ErrorCode.getMessage(rep, paramCode));
			System.out.println(messageLog);


			System.out.println(messageLog);
		} else {
			ErrorParametre(rep, ErrorCode.getMessage(rep, paramCode),
					messageLog + " !!!!ERREUR !!!! ");
		}
	}

	// reception d'un fichier et stockage local (sur le serveur)
	// TODO A faire
	private void processSTOR() throws IOException {
		String rep = "501";
		String paramCode = " le Thread n a pas ete initialisé";
		String messageLog = this.getClass().toString()
				+ " STOR: depot du fichier sur le serveur " + parametre;
		;

		if (this.commande.toUpperCase().startsWith("STOR")) {
			
			if (myftpData!=null){
				myftpData.setCommande(commande);
				myftpData.setParametre(parametre);
				myftpData.run();
				
				rep="150";
				paramCode="Opening " + parametre + " en mode data connection.\n";
				mytools.sendMessage(ErrorCode.getMessage(rep, paramCode));
				while(myftpData.isAlive()){}
				rep=myftpData.getReturnstatus();
				
			}
			
			mytools.sendMessage(ErrorCode.getMessage(rep, paramCode));
			System.out.println(messageLog);


			System.out.println(messageLog);
		} else {
			ErrorParametre(rep, ErrorCode.getMessage(rep, paramCode),
					messageLog + " !!!!ERREUR !!!! ");
		}
	}

	// Remonte vers le repertoire parent
	// TODO verif si CDUP possible
	private void processCDUP() throws IOException {
		String rep = "500", paramCode = "";
		String messageLog = this.getClass().toString()
				+ " CDUP: avec currentDir=" + currentDir;

		if (this.commande.toUpperCase().startsWith("CDUP")) {

			mytools.sendMessage(ErrorCode.getMessage(rep, paramCode));
			System.out.println(messageLog);
		} else {
			ErrorParametre(rep, ErrorCode.getMessage(rep, paramCode),
					messageLog);
		}

	}

	// recuperation du login utilisateur
	private void processUSER() throws IOException {
		String rep = "501", paramCode = parametre;
		String messageLog = this.getClass().toString() + " USER: " + parametre;

		// Verification des conditions d'entrées
		if (this.commande.toUpperCase().startsWith("USER")
				&& (parametre.length() > 1)) {
			this.currentUser = parametre;
			rep = "331";
			ftpetat = ftpEtat.FS_WAIT_PASS;
			mytools.sendMessage(ErrorCode.getMessage(rep, paramCode));
			System.out.println(messageLog);
		} else {
			ErrorParametre(rep, ErrorCode.getMessage(rep, paramCode),
					messageLog);
			return;
		}
	}

	// verification du mot de passe
	private void processPASS() throws IOException {
		// early return mode
		HashMap<String, String> usrMap;
		String rep = "430", paramCode = "";
		String messageLog = this.getClass().toString() + " USER: " + parametre
				+ " PASS: " + parametre;

		// Verification des conditions d'entrées
		if (!this.commande.toUpperCase().startsWith("PASS")
				|| (currentUser.length() < 1)) {
			rep = "500";
			paramCode = "Commande invalide";
			ErrorParametre(rep, ErrorCode.getMessage(rep, paramCode),
					messageLog);
			return;
		}

		// gestion du mode anonyme
		if (currentUser.equals("anonymous")) {
			if (mytools.isEmail(parametre) == true) {
				ftpetat = ftpEtat.FS_LOGGED;
				rep = "230";
				paramCode = "mot de passe valide (email).";
			} else {
				// mdp pour anonymous invalide
				rep = "430";
				paramCode = "mot de passe valide (email).";
			}

			System.out.println(messageLog
					+ ErrorCode.getMessage(rep, paramCode));
			mytools.sendMessage(ErrorCode.getMessage(rep, paramCode));

		} else {
			try {
				// Chargement de la liste des mdp
				usrMap = loadPasswordList();

				// verification du login/pwd
				if (usrMap.containsKey(currentUser)) {
					if (parametre.equals(usrMap.get(currentUser))) {
						rep = "230";
						ftpetat = ftpEtat.FS_LOGGED;
					}
					// mdp incorret
					else {
						messageLog += " .mot de passe incorrect.";
					}
				}
				// utilisateur non trouvé
				else {
					messageLog += " .utilsateur non trouvé.";
				}

				// envoi du resultat au client ftp
				System.out.println(messageLog
						+ ErrorCode.getMessage(rep, paramCode));
				mytools.sendMessage(ErrorCode.getMessage(rep, paramCode));
				return;

			} catch (IOException ioe) {
				System.out
						.println(messageLog
								+ " erreur: Impossible de charger la listes des utilisateurs,mdp\n");
				ioe.printStackTrace();
			} catch (Exception e) {
				System.out
						.println(messageLog
								+ " erreur: utilisateur absent de la liste des utilisateurs. Ce cas ne devrait pas arriver.( anonymous ou \n");
				e.printStackTrace();

			}
		}

		// Homedir
		currentDir = (ftpetat == ftpEtat.FS_LOGGED) ? home + File.separator
				+ currentUser : "";
		mytools.sendMessage(ErrorCode.getMessage(rep, paramCode));
		// log
		System.out.println(messageLog + ".status= " + ftpetat
				+ "(0=wait_login, 1=wait_pass, 2=logged)");

		if (ftpetat != ftpEtat.FS_LOGGED) {
			// rien ne va plus, envoi du refus de pass et cloture de la
			// connection. (rfc959)
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
		String rep = "500", paramCode = "";
		String messageLog = this.getClass().toString() + " commande: "
				+ commande;

		if (commande.equalsIgnoreCase("syst")) {
			rep = "215";
			mytools.sendMessage(ErrorCode.getMessage(rep, paramCode));
			System.out.println(messageLog + ": SYST"
					+ ErrorCode.getMessage(rep, paramCode));
		} else {
			ErrorParametre("502", ErrorCode.getMessage("502", ""), messageLog);
		}
	}

	// recuperation du chemin du serveur
	private void processPWD() throws IOException {
		String rep = "502", paramCode = " Chemin invalide.", messageLog = this
				.getClass().toString() + " commande: " + commande;

		String PWD = null;

		if (commande.equalsIgnoreCase("pwd")) {

			PWD = currentDir.equals("") ? home + File.separator + currentUser
					: currentDir;
			rep = "257";
			paramCode = "\"" + PWD + "\" cree.";

			mytools.sendMessage(ErrorCode.getMessage(rep, paramCode));
			System.out.println(messageLog + ": SYST"
					+ ErrorCode.getMessage(rep, paramCode));

		} else {
			ErrorParametre(rep, ErrorCode.getMessage(rep, paramCode),
					messageLog);
		}
	}

	// definition du type de fichier a transferer
	private void processTYPE() throws IOException {
		String rep = "502", paramCode = "Mode Type inconnu", messageLog = this
				.getClass().toString()
				+ " commande: "
				+ commande
				+ " "
				+ parametre;

		if (commande.equalsIgnoreCase("type")) {
			if (parametre.equalsIgnoreCase("a")) {
				ftpType = "A";
				paramCode = "Le mode ASCII a été défini";
				rep = "200";
			} else if (parametre.equalsIgnoreCase("i")) {
				ftpType = "I";
				paramCode = "Le mode BINAIRE a été défini";
				rep = "200";
			} else {
				ErrorParametre(rep, ErrorCode.getMessage(rep, paramCode),
						messageLog);
			}

			mytools.sendMessage(ErrorCode.getMessage(rep, paramCode));
			System.out.println(messageLog);
		}
	}

	// presentation des features du serveur
	private void processFEAT() throws IOException {
		mytools.sendMessage("211- Features");
		mytools.sendMessage("Ben, en fait il n'ya en pas");
		mytools.sendMessage("vraiment pas");
		mytools.sendMessage("211 EndFeature");
	}

	// ouverture du port sur le serveur et attente de connection de la part du
	// client.
	private void processPORT() throws IOException {
		String rep = "501", paramCode = "Erreur dans le numero du port fourni.", messageLog = this
				.getClass().toString()
				+ "processPORT"
				+ " commande: "
				+ commande + " " + parametre;

		// 227 Entering Passive Mode (192,168,150,90,195,149).
		if (commande.equalsIgnoreCase("port")) {
			if (parametre.matches("([0-9]{1,3},){5}[0-9]{1,3}")) {
				// recuperation de l @ et port client
				String[] atmp = parametre.split(",");
				cltDataAddr = atmp[0] + "." + atmp[1] + "." + atmp[2] + "."
						+ atmp[3];
				cltDataPort = Integer.parseInt(atmp[4]) * 256
						+ Integer.parseInt(atmp[5]);
				// Port correct ?
				if (cltDataPort < 65535 && cltDataPort > 1023) {
					rep = "200";
					paramCode = "Port accepted on " + cltDataAddr + ":"
							+ String.valueOf(cltDataPort);
				} else {
					paramCode += "( " + cltDataPort + ")";
				}
			}
			rep = ErrorCode.getMessage(rep, paramCode);
			mytools.sendMessage(rep);
		} else {
		ErrorParametre(rep, ErrorCode.getMessage(rep, paramCode), messageLog);
		}
	}

	// passage en mode passive ( ie, le serveur ouvre un port, en informe le
	// client et attend la connection.)
	private void processPASV() throws IOException {
		// 227 Entering Passive Mode (192,168,150,90,195,149).
		String rep = "502", paramCode = "", port_url = null, messageLog = this
				.getClass().toString()
				+ "processPASV"
				+ " commande: "
				+ commande;

		if (commande.equalsIgnoreCase("pasv")) {
			isPASV = true;
			//lancement du thread
			myftpData = new ftpData(true);
			
			port_url = myftpData.getPort_url();
			if (port_url!=null){
				rep = "227";	
			}else {
				// echec, on repasse en mode direct et on tue le thread
				isPASV=false;
				myftpData.setKeepRunning(false);
				}
			
			paramCode = port_url;
			mytools.sendMessage(ErrorCode.getMessage(rep, paramCode));
			System.out.println(messageLog);

		} else {
			ErrorParametre(rep, ErrorCode.getMessage(rep, paramCode),
					messageLog);
		}

	}


	// envoi du message d'erreur sur la socket cliente et en log
	private void ErrorParametre(String errorcode, String messageSoc,
			String messageLog) throws IOException {
		ErrorCode.sendErrorMessage(mytools, errorcode, messageSoc);
		System.out.println("!!!ERREUR: " + messageLog);
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

	// Envoi la liste des dossiers et fichiers du repertoire courant.
	private void processLIST() throws IOException {
		//thread d'envoi des données 
		//
		String rep = "502", paramCode = "Erreur de traitement", messageLog = this
				.getClass().toString()
				+ "processList"
				+ " commande: "
				+ commande;
		
		
		if (commande.equalsIgnoreCase("list")) {

			//prepatation des données a envoyer
			//tableau des infos a envoyer
			List<String> aInfo2Send = new ArrayList<String>();
			//Liste des fichiers et dossiers
			List<String> myDir = mytools.getDirectoryListing(currentDir);

			//Ajout des infos 
			for (String strTemp : myDir) {
				aInfo2Send.add((strTemp));
			}
			
			// envoi du listing
			//TODO gerer le mode actif aussi
				// ici on ne gere que le passif
			
				if(isPASV){
					if (myftpData==null){
						//creation du thread
						myftpData = new ftpData(dataSrvSocket,cltDataAddr,cltDataPort,aInfo2Send,commande,parametre);
					}
					}
					else
				{
						myftpData = new ftpData(cltDataAddr,cltDataPort,aInfo2Send,commande,parametre);
				}
				//parametrage du thread
				myftpData.setaString(aInfo2Send);
				myftpData.setCommande(commande);
				myftpData.setParametre(parametre);

				// amorce de l'envoi
				rep = "150";
				paramCode = "";
				mytools.sendMessage(ErrorCode.getMessage(rep, paramCode));
				// envoi du contenu par le thread
				myftpData.run();
				while(myftpData.isAlive()){}

				//cloture de l envoi
				rep= myftpData.getReturnstatus();
				mytools.sendMessage(ErrorCode.getMessage(rep, paramCode));
				
		
		} else {
			ErrorParametre(rep, ErrorCode.getMessage(rep, paramCode),
					messageLog);
		}
	}

	//TODO FTPDATA
	
	public ftpData send2Client(String cltDataAddr, String cltDataPort,List<String> aInfo2Send,boolean isPASV,String commande,String parametre){
		
		ftpData threadftpData = new ftpData(cltDataAddr,Integer.parseInt(cltDataPort),aInfo2Send,commande,parametre);
	
		return threadftpData;
	}
	
	
	
	// changement de repertoire
	private void processCWD() throws IOException {

		String rep = "502", paramCode = "Dossier inexistant, le dossier actuel reste "
				+ currentDir, tempdir = null, messageLog = this.getClass()
				.toString()
				+ "processCWD"
				+ " commande: "
				+ commande
				+ " "
				+ parametre;

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
		System.out.println(messageLog + " repertoire local" + currentDir);
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
