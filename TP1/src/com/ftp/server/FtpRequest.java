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

/**
 * 
 * Classe principale lancée par la classe server, qui prend la main ensuite pour
 * gerer la communication sur le FTP-Control channel. La plupart des commandes
 * FTP sont implementees.
 * 
 * 
 * @author user
 * 
 */
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

	/**
	 * Constructeur du thread FTPRequest Ce thread recoit les commandes via le
	 * canal de controle, traites les operations sauf list, prepare et retrieve
	 * pour lequel un autre thread sera lancé pour traiter ces commandes.
	 * 
	 * @param _srvSocket
	 *            ServerSocket correspond a la socket serveur.
	 * @param _clskt
	 *            socket correspond a socket de connexion vers le client.
	 * @param _tooMAny
	 *            Limite max de client a accepter.
	 */
	public FtpRequest(ServerSocket _srvSocket, Socket _clskt, boolean _tooMAny) {
		cltSocketCtrl = _clskt;
		srvSocketCtrl = _srvSocket;
		tooManyClient = _tooMAny;
		ftpetat = ftpEtat.FS_WAIT_LOGIN;
	}

	/**
	 * Lancement du thread FtpData
	 * 
	 */
	public void run() {

		String rep = "", paramCode = "", messageLog = "";
		int nbCommandeVide = 0;

		messageLog = this.getClass().toString();

		try {
			mytools = new Tools(cltSocketCtrl);
		} catch (IOException e) {
			System.out
					.println("ohoh, seems like the client has disconnected ....");
			KeepRunning = false;
		}

		if (tooManyClient) {
			rep = "421";
			paramCode = " Clients number has reach it limit, please try again later.";
			try {
				ErrorCode.sendCodeMessage(mytools, rep, paramCode, messageLog);
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
				System.out.println("Received command to process:" + commande
						+ " " + parametre);
				processRequest();
				// TODO resoudre ce probleme lors d'un deconnexion brutale du
				// client, le socket serveur reçoit en permance des commandes
				// vides.
				if (commande.equals("")) {
					nbCommandeVide++;
				} else {
					nbCommandeVide = 0;
				}
				if (nbCommandeVide >= 500) {
					KeepRunning = false;
					System.out.println(this.getClass().toString()
							+ " Too many Empty packet (" + nbCommandeVide
							+ "), closing the connexion");
				}
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
		Server.nbClients--;
		System.out.println("Thread End.");
	}

	/**
	 * Suite a la connexion du client, le serveur envoie le message d'accueil
	 * sur le canal de control.
	 * 
	 */
	public void msgAccueil() {

		String rep = "220", messageLog = this.getClass() + " :client ip: ";
		String paramCode = cltSocketCtrl.getLocalAddress().getHostAddress()
				+ ". At, the moment " + Server.nbClients + " are connected.";

		try {
			ErrorCode.sendCodeMessage(mytools, rep, paramCode, messageLog);
		} catch (IOException e) {
			System.err.println(this.getClass().getName()
					+ ": msgAccueil error\n");
			e.printStackTrace();
		}
	}

	/**
	 * Cette fonction permet la reconnaissance des commandes et définit la
	 * partie commande de la partie parametre.
	 * http://www.iana.org/assignments/ftp
	 * -commands-extensions/ftp-commands-extensions.xhtml
	 * 
	 * @param receiveMessage
	 */
	private void parseCommande(String receiveMessage) {
		int idx = -1;
		String tempcom = "", temppar = "";
		String[] ftpcmdList = { "ABOR", "ACCT", "ALLO", "APPE", "CWD", "DELE",
				"FEAT", "HELP", "LIST", "MODE", "NLST", "NOOP", "PASS", "PASV",
				"PORT", "QUIT", "REIN", "REST", "RETR", "RNFR", "RNTO", "SITE",
				"STAT", "STOR", "STRU", "TYPE", "USER", "CDUP", "MKD", "PWD",
				"RMD", "SMNT", "STOU", "SYST", "LIST", "NLST", "PASV", "REST",
				"SITE", "STOU", "QUOTE" };
		if (receiveMessage != null) {
			idx = receiveMessage.indexOf(" ");
			tempcom = receiveMessage.toUpperCase();
			if (idx != -1) {
				tempcom = receiveMessage.substring(0, idx);
				temppar = receiveMessage.substring(idx + 1,
						receiveMessage.length());
			}

			if (Arrays.asList(ftpcmdList).contains(tempcom)) {
				commande = tempcom;
				parametre = temppar;
			} else {
				parametre = tempcom + " " + temppar;
				commande = "XXBAD";
				
			}
		}
	}

	/**
	 * Gestion des commandes autorises via un Pattern ETAT. Dans l'etat 1, USER
	 * et QUIT sont permis. Dans l'etat 2, USER, QUIT et PASS sont permis Dans
	 * l'etat 3, toutes les commandes sont permises.
	 * 
	 * ecoute les commandes envoyés et lance le process adéquat
	 */
	//
	public void processRequest() {
		String rep = "500", paramCode = "Syntax error, command unrecognized", messageLog = this
				.getClass().toString() + ":ProcessRequest: ";

		boolean treated = false;

		try {
			switch (ftpetat) {
			case ftpEtat.FS_WAIT_LOGIN:
				if (commande.toUpperCase().startsWith("USER")) {
					processUSER();
					treated = true;
				}
				if (commande.toUpperCase().startsWith("QUIT")) {
					processQUIT();
					treated = true;
				}
				if (!commande.equals("USER") && (!commande.equals("PASS"))) {
					rep = "503";
					ErrorCode.sendCodeMessage(mytools, rep, paramCode,
							messageLog);
				}
				break;
			case ftpEtat.FS_WAIT_PASS:
				if (commande.toUpperCase().startsWith("USER")) {
					processUSER();
				}
				if (commande.toUpperCase().startsWith("PASS")) {
					processPASS();
				}
				if (commande.toUpperCase().startsWith("QUIT")) {
					processQUIT();
				}
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
				} else if (commande.toUpperCase().startsWith("DELE")) {
					processDELE();
				} else if (commande.equals("")) {
					messageLog += "Empty Command, keep alive packet ?";
					System.out.println(messageLog);
				} else {
					rep = "500";
					paramCode = "Syntax error, command unrecognized";
					ErrorCode.sendCodeMessage(mytools, rep, paramCode + " "
							+ parametre, messageLog);
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

	/**
	 * Suite a la reception de la commande RETR, la methode prepare le thread
	 * FtpData et le lance.
	 * 
	 * @throws IOException
	 */
	private void processRETR() throws IOException {
		String rep = "501", paramCode = " uninitilized Thread.";
		String messageLog = this.getClass().toString()
				+ " RETR: receiving file from server " + parametre;
		;

		if (this.commande.toUpperCase().startsWith("RETR")) {

			if (myftpData != null) {
				myftpData.setCommande(commande);
				myftpData.setParametre(currentDir + File.separator + parametre);
				myftpData.setASCII(ftpType.equals("A") ? true : false);
			} else {
				myftpData = new ftpData(dataSrvSocket, cltDataAddr,
						cltDataPort, null, commande, parametre);
			}

			rep = "150";
			paramCode = "Opening " + parametre + " en mode data connection.\n";
			ErrorCode.sendCodeMessage(mytools, rep, paramCode, messageLog);
			// mode thread
			// myftpData.start();
			// mode bloquant
			myftpData.run();

			while (myftpData.isAlive()) {
			}
			rep = myftpData.getReturnstatus();

			mytools.sendMessage(rep);
			System.out.println(messageLog);
		} else {
			ErrorCode.sendErrorMessage(mytools, rep, paramCode, messageLog);
		}
	}

	// reception d'un fichier et stockage local (sur le serveur)
	// TODO A faire
	/**
	 * Suite a la reception de la commande STOR, la methode prepare le thread
	 * FtpData et le lance.
	 * 
	 * @throws IOException
	 * 
	 */
	private void processSTOR() throws IOException {
		String rep = "501";
		String paramCode = " Uninitialized thread";
		String messageLog = this.getClass().toString()
				+ " STOR: sending file to server " + parametre;
		;

		if (this.commande.toUpperCase().startsWith("STOR")) {

			if (myftpData != null) {
				myftpData.setCommande(commande);
				myftpData.setParametre(currentDir + File.separator + parametre);
			} else {
				myftpData = new ftpData(dataSrvSocket, cltDataAddr,
						cltDataPort, null, commande, currentDir
								+ File.separator + parametre);
			}

			myftpData.setDataAddr(cltDataAddr);
			myftpData.setDataPort(cltDataPort);
			myftpData.setPASV(isPASV);

			myftpData.setASCII(ftpType.equals("A") ? true : false);
			// preparation du client
			rep = "150";
			paramCode = "Opening " + parametre + " en mode data connection.\n";
			ErrorCode.sendCodeMessage(mytools, rep, paramCode, messageLog);

			// Envoi des données vers le client
			myftpData.run();

			// myftpData.start();

			// attente de la fin du transfert (thread super utile :( )
			while (myftpData.isAlive()) {
			}
			rep = myftpData.getReturnstatus();

			mytools.sendMessage(rep);

			System.out.println(messageLog + "  " + rep);
		} else {
			ErrorCode.sendErrorMessage(mytools, rep, paramCode, messageLog);
		}
	}

	/**
	 * Remonte vers le repertoire parent
	 * 
	 * @throws IOException
	 */
	private void processCDUP() throws IOException {
		String rep = "550", paramCode = "This is the root folder.", tempDir;
		String messageLog = this.getClass().toString()
				+ " CDUP: avec currentDir=" + currentDir;

		if (this.commande.toUpperCase().startsWith("CDUP")) {

			tempDir = mytools.getParentDir(currentDir);
			if (!tempDir.equalsIgnoreCase(currentDir)) {
				currentDir = tempDir;
				rep = "250";
				paramCode = "Directory changed to " + currentDir;
			}

			ErrorCode.sendCodeMessage(mytools, rep, paramCode, messageLog);
		} else {
			ErrorCode.sendErrorMessage(mytools, rep, paramCode, messageLog);
		}

	}

	/**
	 * Recuperation du login utilisateur passe en parametre de la commande USER
	 * 
	 * @throws IOException
	 */
	private void processUSER() throws IOException {
		String rep = "501", paramCode = parametre;
		String messageLog = this.getClass().toString() + " USER: " + parametre;

		// Verification des conditions d'entrées
		if (this.commande.toUpperCase().startsWith("USER")
				&& (parametre.length() > 1)) {
			this.currentUser = parametre;
			rep = "331";
			ftpetat = ftpEtat.FS_WAIT_PASS;
			ErrorCode.sendCodeMessage(mytools, rep, paramCode, messageLog);
		} else {
			ErrorCode.sendErrorMessage(mytools, rep, paramCode, messageLog);
		}
	}

	/**
	 * verification du mot de passe envoye par le client.
	 * 
	 * @throws IOException
	 */

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

			// le client rage quitte, fermeture du thread serveur.
			KeepRunning = false;
			ErrorCode.sendErrorMessage(mytools, rep, paramCode, messageLog);

		}

		// gestion du mode anonyme
		if (currentUser.equals("anonymous")) {
			if (mytools.isEmail(parametre) == true) {
				ftpetat = ftpEtat.FS_LOGGED;
				rep = "230";
				paramCode = "valid password.";
			} else {
				// mdp pour anonymous invalide
				rep = "430";
				KeepRunning = false;
				paramCode = "invalid password(email expected).";
			}
			ErrorCode.sendCodeMessage(mytools, rep, paramCode, messageLog);
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
						KeepRunning = false;
						messageLog += " .Invalid password(email expected).";
					}
				}
				// utilisateur non trouvé
				else {
					messageLog += " . User not found";
					KeepRunning = false;
				}

				// envoi du resultat au client ftp
				ErrorCode.sendCodeMessage(mytools, rep, paramCode, messageLog);

			} catch (IOException ioe) {
				System.out.println(messageLog
						+ " error: Cannot load the users list.\n");
				ioe.printStackTrace();
			} catch (Exception e) {
				System.out
						.println(messageLog
								+ " error: user is not in the users lists. You should log as anonymous.\n");
				e.printStackTrace();

			}
		}

		// Homedir
		currentDir = (ftpetat == ftpEtat.FS_LOGGED) ? home + File.separator
				+ currentUser : "";
		messageLog += " status= " + ftpetat
				+ "(0=wait_login, 1=wait_pass, 2=logged)";
		System.out.println(messageLog);

		if (ftpetat != ftpEtat.FS_LOGGED) {
			// rien ne va plus, envoi du refus de pass et cloture de la
			// connection. (rfc959)
			killConnection();
		}
	}

	/**
	 * Chargement de la liste des users et des mdp...
	 * 
	 */

	private HashMap<String, String> loadPasswordList() throws IOException {

		String TableDesMdps = "";
		// université
		// TableDesMdps += "TP1" + File.separator + "mdp.txt";
		// @home
		TableDesMdps += "mdp.txt";
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

	/**
	 * Envoie des informations techniques du serveur.
	 * 
	 * @throws IOException
	 */
	private void processSYST() throws IOException {
		String rep = "500", paramCode = "";
		String messageLog = this.getClass().toString() + " commande: "
				+ commande + " ";

		if (commande.equalsIgnoreCase("syst")) {
			rep = "215";

			ErrorCode.sendCodeMessage(mytools, rep, paramCode, messageLog);
		} else {
			paramCode = "";
			ErrorCode.sendErrorMessage(mytools, rep, paramCode, messageLog);
		}
	}

	/**
	 * retourne le chemin courant défini sur le serveur, au client
	 * 
	 * @throws IOException
	 */
	private void processPWD() throws IOException {
		String rep = "550", paramCode = " invalude path.", messageLog = this
				.getClass().toString() + " commande: " + commande + " ";

		if (commande.equalsIgnoreCase("pwd")) {
			rep = "257";
			paramCode = currentDir.length() > 0 ? currentDir : Server.prepath
					+ File.separator + currentUser;
			currentDir = paramCode;
			ErrorCode.sendCodeMessage(mytools, rep, paramCode, messageLog);
		} else {
			ErrorCode.sendErrorMessage(mytools, rep, paramCode, messageLog);
		}
	}

	/**
	 * definition du type de fichier a transferer (ascii ou binaire)
	 * 
	 * @throws IOException
	 */
	private void processTYPE() throws IOException {
		String rep = "502", paramCode = "Mode Type unknown", messageLog = this
				.getClass().toString()
				+ " commande: "
				+ commande
				+ " "
				+ parametre;

		if (commande.equalsIgnoreCase("type")) {
			if (parametre.equalsIgnoreCase("a")) {
				ftpType = "A";
				paramCode = "ASCII transfer mode set.";
				rep = "200";
			} else if (parametre.equalsIgnoreCase("i")) {
				ftpType = "I";
				paramCode = "BINARY transfer mode set.";
				rep = "200";
			} else {
				ErrorCode.sendErrorMessage(mytools, rep, paramCode, messageLog);
			}

			ErrorCode.sendCodeMessage(mytools, rep, paramCode, messageLog);
		}
	}

	/**
	 * Le serveur FTP envoie au client les "features" qu'il suppporte afin que
	 * le client s'y adapte.
	 * 
	 */
	private void processFEAT() throws IOException {
		mytools.sendMessage("211- Features");
		mytools.sendMessage("uhm, i'm afraid there is none");
		mytools.sendMessage("I mean really none");
		mytools.sendMessage("211 EndFeature");
	}

	/**
	 * Le client informe le serveur par cette commande quels ports sont en
	 * ecoute du coté client. Le serveur devra s'y connecter pour mettre en
	 * place le canal Data.
	 * 
	 * @throws IOException
	 */
	private void processPORT() throws IOException {
		String rep = "501", paramCode = "Bad port given.", messageLog = this
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
			ErrorCode.sendCodeMessage(mytools, rep, paramCode, messageLog);
		} else {
			ErrorCode.sendErrorMessage(mytools, rep, paramCode, messageLog);
		}
	}

	/**
	 * passage en mode passive ( ie, le serveur ouvre un port, en informe le
	 * client et attend la connection.
	 * 
	 * @throws IOException
	 */
	private void processPASV() throws IOException {
		// 227 Entering Passive Mode (192,168,150,90,195,149).
		String rep = "502", paramCode = "", port_url = null, messageLog = this
				.getClass().toString()
				+ "processPASV"
				+ " command: "
				+ commande;

		if (commande.equalsIgnoreCase("pasv")) {
			isPASV = true;
			// lancement du thread
			myftpData = new ftpData(cltSocketCtrl.getLocalAddress()
					.getHostAddress(), true);

			port_url = myftpData.getPort_url();
			if (port_url != null) {
				rep = "227";
			} else {
				// echec, on repasse en mode direct et on tue le thread
				isPASV = false;
				myftpData.setKeepRunning(false);
			}

			paramCode = port_url;
			messageLog += myftpData.getDataAddr() + ":"
					+ myftpData.getDataPort();

			ErrorCode.sendCodeMessage(mytools, rep, paramCode, messageLog);
		} else {
			ErrorCode.sendErrorMessage(mytools, rep, paramCode, messageLog);
		}

	}

	/**
	 * Cloture des sockets ouvertes.
	 * 
	 */
	private void killConnection() {
		try {
			KeepRunning = false;
			currentUser = "Anonymous";
			System.out.println(this.getClass().toString()
					+ " Killing the connection: remaining "
					+ (Server.nbClients - 1) + " connected user(s).");

			mytools.CloseStreams();
			this.cltSocketCtrl.close();

		} catch (Exception e) {
			System.out.println(this.getClass().toString()
					+ " error: requesting connection closure \n");
			e.printStackTrace();
		}

	}

	/**
	 * Envoi la liste des dossiers et fichiers du repertoire courant. vers le
	 * client ftp selon le mode (actif / passif défini
	 * 
	 * @throws IOException
	 */
	private void processLIST() throws IOException {
		// thread d'envoi des données
		//
		String rep = "502", paramCode = "Error while processing directory listing.", messageLog = this
				.getClass().toString()
				+ "processList"
				+ " command: "
				+ commande;

		if (commande.equalsIgnoreCase("list")) {

			// prepatation des données a envoyer
			// tableau des infos a envoyer
			List<String> aInfo2Send = new ArrayList<String>();
			// Liste des fichiers et dossiers
			currentDir = currentDir.length() > 0 ? currentDir : Server.prepath
					+ File.separator + currentUser;
			List<String> myDir = mytools.getDirectoryListing(currentDir);

			// Ajout des infos
			for (String strTemp : myDir) {
				aInfo2Send.add((strTemp));
			}

			// envoi du listing
			// TODO gerer le mode actif aussi
			// ici on ne gere que le passif
			if (myftpData == null) {
				// creation du thread
				myftpData = new ftpData(dataSrvSocket, cltDataAddr,
						cltDataPort, aInfo2Send, commande, parametre);
			} else {
				// on affecte les nouvelles données de ports reçues du client.
				myftpData.setDataAddr(cltDataAddr);
				myftpData.setDataPort(cltDataPort);
				// myftpData.reconnect();
			}

			// parametrage du thread
			myftpData.setaString(aInfo2Send);
			myftpData.setCommande(commande);
			myftpData.setParametre(parametre);
			myftpData.setPASV(isPASV);

			// amorce de l'envoi
			rep = "150";
			paramCode = "";
			ErrorCode.sendCodeMessage(mytools, rep, paramCode, messageLog);

			// envoi du contenu par le thread
			myftpData.run();
			while (myftpData.isAlive()) {
			}

			// cloture de l envoi
			rep = myftpData.getReturnstatus();
			ErrorCode.sendCodeMessage(mytools, rep, "", messageLog);

		} else {
			// bah, ce n'est pas list qui a ete recu.
			ErrorCode.sendErrorMessage(mytools, rep, paramCode, messageLog);
		}
	}

	/**
	 * changement de repertoire currentDir selon le parametre reçu.
	 * 
	 * 
	 * @throws IOException
	 */
	private void processCWD() throws IOException {

		String rep = "550", paramCode = "Unknown direcotory. Current path is still "
				+ currentDir, tempDir = null, messageLog = this.getClass()
				.toString()
				+ "processCWD"
				+ " command: "
				+ commande
				+ " "
				+ parametre;

		if (commande.equalsIgnoreCase("cwd")) {

			// TODO verification des droits de changement
			tempDir = mytools.getNewDirectory(currentDir, parametre);
			if (!tempDir.equalsIgnoreCase(currentDir)) {
				currentDir = tempDir;
				rep = "250";
				paramCode = "Directory changed to " + currentDir;
			} else {
				rep = "250";
				paramCode = "Directory not changed:" + currentDir;
			}
		}
		ErrorCode.sendCodeMessage(mytools, rep, paramCode, messageLog);
	}

	/**
	 * 
	 * Reception de la commande pour terminer la connexion.
	 * 
	 * 
	 * @throws IOException
	 */
	private void processQUIT() throws IOException {
		String rep = "221", paramCode = "Requesting end of session.", messageLog = this
				.getClass().toString() + "processQUIT" + " command: ";

		if (commande.equalsIgnoreCase("quit")) {
			ErrorCode.sendCodeMessage(mytools, rep, paramCode, messageLog);
			killConnection();
		}
	}

	/**
	 * Le client FTP a demandé la suppression d'un fichier sur le serveur
	 * execution de la demande et retour selon le résultat.
	 * 
	 * @throws IOException
	 */
	private void processDELE() throws IOException {
		String rep = "502", paramCode = commande + " unsuccessful for "
				+ parametre, messageLog = this.getClass().toString()
				+ "processDELE" + " commande: " + commande + " " + parametre;
		Boolean resDel = false;

		if (commande.equalsIgnoreCase("dele")) {

			File todel = new File(currentDir + File.separator + parametre);
			if (todel.exists()) {
				resDel = todel.delete();
				if (resDel) {
					rep = "250";
					paramCode = commande + " successful for " + parametre;
				} else {
					rep = "550";
					paramCode = "Error, no right to delele file " + parametre
							+ ".";
				}
			} else {
				rep = "550";
				paramCode = "Error, file " + parametre + " does not exist.";
			}
			ErrorCode.sendCodeMessage(mytools, rep, paramCode, messageLog);
		}
	}
}
