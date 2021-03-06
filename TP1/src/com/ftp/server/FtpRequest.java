package com.ftp.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import com.ftp.tools.ErrorCode;
import com.ftp.tools.Tools;

/**
 * Main class to handle the control channel. this thread is launched by the
 * server thread. Most of the FTP commands are implemented.
 * 
 * @author Emeline Salomon & François Dubiez
 */
public class FtpRequest extends Thread {

	/**
	 * client Socket
	 */
	private Socket cltSocketCtrl;
	/**
	 * private ServerSocket srvSocketCtrl;
	 */
	private ServerSocket dataSrvSocket;
	/**
	 * client IP Addr & Port
	 */
	private String cltDataAddr;
	private Integer cltDataPort;

	private String currentUser = "";
	private String usersRootFolder = "";
	private String currentDir = "";
	private Integer ftpetat;
	/**
	 * Keep the thread running or not
	 */
	private boolean KeepRunning = true;
	/**
	 * File Type Ascii=A or binary=I
	 */
	private String ftpType = "";
	/**
	 * # of clients reached ?
	 */
	boolean tooManyClient = false;
	/**
	 * Passive or active mode
	 */
	boolean isPASV = false;
	/**
	 * tools class for IO operations (files, sockets).
	 * 
	 * @uml.property name="mytools"
	 * @uml.associationEnd
	 */
	Tools mytools = null;
	/**
	 * Class to retrieve message associated with an FTP errorCode
	 * 
	 * @uml.property name="myErrorCode"
	 * @uml.associationEnd
	 */
	ErrorCode myErrorCode = null;
	/**
	 * Thread for listing directory, sending or receiving data.
	 * 
	 * @uml.property name="myftpData"
	 * @uml.associationEnd
	 */
	private FtpData myftpData;
	/**
	 * File where credentials are stored
	 */
	String TableDesMdps = "mdp.txt";
	/**
	 * Answer Code to FTP command
	 */
	String rep = null;
	/**
	 * Text to replace in the response texte (PARAM =>"paramCode")
	 */
	String paramCode = null;
	/**
	 * Command and parameter received by server
	 */
	private String commande = "";
	private String parametre = "";
	/**
	 * verbose mode
	 */
	boolean debugMode = false;
	/**
	 * InputStream for password File
	 * 
	 */
	InputStream pwdInputStream;

	/**
	 * Constructeur du thread FTPRequest Ce thread recoit les commandes via le
	 * canal de controle, traites les operations sauf list, prepare et retrieve
	 * pour lequel un autre thread sera lancé pour traiter ces commandes.
	 * 
	 * @param _clskt
	 *            Client socket to handle connection with client.
	 * @param _tooMAny
	 *            Maximum number of clients to acccept.
	 * @throws IOException
	 */
	public FtpRequest(Socket _clskt, String _usersRootFolder, boolean _debugMode) {
		cltSocketCtrl = _clskt;
		isPASV = false;
		ftpetat = FtpEtat.FS_WAIT_LOGIN;
		debugMode = _debugMode;
		myErrorCode = new ErrorCode(debugMode);
		usersRootFolder = _usersRootFolder;

		try {
			mytools = new Tools(cltSocketCtrl, debugMode);

		} catch (IOException e) {
			System.err
					.println("ohoh, seems like the client has disconnected ....");
			KeepRunning = false;
		}
	}

	/**
	 * thread FtpData start
	 * 
	 */
	public void run() {

		String messageLog = "";
		int nbCommandeVide = 0;

		messageLog = this.getClass().toString();

		if (tooManyClient) {
			rep = "421";
			paramCode = " Clients number has reach it limit, please try again later.";
			try {
				myErrorCode
						.sendCodeMessage(mytools, rep, paramCode, messageLog);
				killConnection();
			} catch (Exception e) {
				// Rien A Faire: cloture de la connection de tt façon.
			}
		} else {
			msgAccueil();
		}

		try {
			while (KeepRunning) {
				parseCommande(mytools.receiveMessage());
				if (debugMode) {
					System.out.println("Received command to process:"
							+ commande + " " + parametre);
				}
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

		System.out.println("Thread End.("
				+ String.valueOf(this.getThreadGroup().activeGroupCount())
				+ " clients remaining)");
	}

	/**
	 * welcome message to be send on the control channel
	 * 
	 */
	public void msgAccueil() {

		String rep = "220", messageLog = this.getClass() + " :client ip: ";
		String paramCode = cltSocketCtrl.getLocalAddress().getHostAddress()
				+ ".";

		try {
			myErrorCode.sendCodeMessage(mytools, rep, paramCode, messageLog);
		} catch (IOException e) {
			System.err.println(this.getClass().getName()
					+ ": msgAccueil error\n");
			e.printStackTrace();
		}
	}

	/**
	 * is receiveMessage a valid ftp command ? this function will know.
	 * 
	 * http://www.iana.org/assignments/ftp
	 * -commands-extensions/ftp-commands-extensions.xhtml
	 * 
	 * @param receiveMessage
	 *            string to test.
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
				tempcom = receiveMessage.substring(0, idx).toUpperCase();
				temppar = receiveMessage.substring(idx + 1,
						receiveMessage.length());
			}

			if (Arrays.asList(ftpcmdList).contains(tempcom)) {
				commande = tempcom;
				parametre = temppar;
			} else {
				parametre = tempcom.toUpperCase() + " " + temppar;
				commande = "XXBAD";

			}
		}
	}

	/**
	 * Process the FTP request according to actual state.(State Pattern)
	 * 
	 * State 1, USER & QUIT allowed. State 2, USER, QUIT & PASS allowed. State
	 * 3, All commands allowed.
	 * 
	 * internal var used: commande, parametre.
	 */

	public void processRequest() {
		String rep = "500", paramCode = "Syntax error, command unrecognized", messageLog = this
				.getClass().toString() + ":ProcessRequest: ";

		try {
			switch (ftpetat) {
			case FtpEtat.FS_WAIT_LOGIN:
				if (commande.toUpperCase().startsWith("USER")) {
					processUSER();
				}
				if (commande.toUpperCase().startsWith("QUIT")) {
					processQUIT();
				}
				if (!commande.equals("USER") && (!commande.equals("QUIT"))) {
					rep = "503";
					myErrorCode.sendCodeMessage(mytools, rep, paramCode,
							messageLog);
				}
				break;
			case FtpEtat.FS_WAIT_PASS:
				if (commande.toUpperCase().startsWith("USER")) {
					processUSER();
				}
				if (commande.toUpperCase().startsWith("PASS")) {
					processPASS();
				}
				if (commande.toUpperCase().startsWith("QUIT")) {
					processQUIT();
				}
				if (!commande.equals("USER")
						&& (!commande.equals("PASS") && (!commande
								.equals("QUIT")))) {
					rep = "503";
					myErrorCode.sendCodeMessage(mytools, rep, paramCode,
							messageLog);
				}
				break;
			case FtpEtat.FS_LOGGED:
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
					myErrorCode.sendCodeMessage(mytools, rep, paramCode + " "
							+ parametre, messageLog);
				}
				break;
			}

		} catch (IOException e1) {
			KeepRunning = false;
			System.err.println(this.getClass().toString() + " erreur\n");
			e1.printStackTrace();
			killConnection();
		}
	}

	/**
	 * RETR, this function is setting up the FtpData thread and will start it.
	 * The FTP client asked to receive a file
	 * 
	 * @throws IOException
	 */
	private void processRETR() throws IOException {
		String rep = "501", paramCode = " uninitilized Thread.";
		String messageLog = this.getClass().toString()
				+ " RETR: receiving file from server " + parametre;
		;

		if (this.commande.toUpperCase().startsWith("RETR")) {

			if (myftpData == null) {
				myftpData = new FtpData(dataSrvSocket, cltDataAddr,
						cltDataPort, null, commande, parametre);
			}

			myftpData.setCommande(commande);
			if (!parametre.startsWith("/")) {
				parametre = currentDir + File.separator + parametre;
			}
			myftpData.setParametre(parametre);
			myftpData.setASCII(ftpType.equals("A") ? true : false);

			rep = "150";
			paramCode = "Opening " + parametre + " en mode data connection.\n";
			myErrorCode.sendCodeMessage(mytools, rep, paramCode, messageLog);
			// mode thread
			myftpData.start();
			// mode bloquant
			// myftpData.run();

			while (myftpData.isAlive()) {
			}
			rep = myftpData.getReturnstatus();

			mytools.sendMessage(rep);
			if (debugMode) {
				System.out.println(messageLog);
			}
		} else {
			myErrorCode.sendErrorMessage(mytools, rep, paramCode, messageLog);
		}
	}

	/**
	 * STOR, this function is setting up the FtpData thread and will start it.
	 * The FTP client asked to send a file
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

			if (!currentUser.equalsIgnoreCase("anonymous")) {

				if (myftpData != null) {
					myftpData.setCommande(commande);
					if (!parametre.startsWith("/")) {
						parametre = currentDir + File.separator + parametre;
					}
					myftpData.setParametre(parametre);
				} else {
					myftpData = new FtpData(dataSrvSocket, cltDataAddr,
							cltDataPort, null, commande, currentDir
									+ File.separator + parametre);
				}

				myftpData.setDataAddr(cltDataAddr);
				myftpData.setDataPort(cltDataPort);
				myftpData.setASCII(ftpType.equals("A") ? true : false);
				// preparation du client
				rep = "150";
				paramCode = "Opening " + parametre
						+ " en mode data connection.\n";
				myErrorCode
						.sendCodeMessage(mytools, rep, paramCode, messageLog);

				// Envoi des données vers le client
				// mode sequence
				myftpData.run();
				// mode thread
				// myftpData.start();

				// attente de la fin du transfert (thread super utile :( )
				while (myftpData.isAlive()) {
				}
				rep = myftpData.getReturnstatus();
				myErrorCode.sendCodeMessage(mytools, rep, messageLog);
			} else {
				myErrorCode.sendErrorMessage(mytools, "532", paramCode,
						messageLog);
			}
		} else {
			myErrorCode.sendErrorMessage(mytools, rep, paramCode, messageLog);
		}
	}

	/**
	 * CDUP, change current directory to its parent, if possible.
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

			myErrorCode.sendCodeMessage(mytools, rep, paramCode, messageLog);
		} else {
			myErrorCode.sendErrorMessage(mytools, rep, paramCode, messageLog);
		}

	}

	/**
	 * define the current user with the parameter received in the USER command.
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
			ftpetat = FtpEtat.FS_WAIT_PASS;
			myErrorCode.sendCodeMessage(mytools, rep, paramCode, messageLog);
		} else {
			myErrorCode.sendErrorMessage(mytools, rep, paramCode, messageLog);
		}
	}

	/**
	 * Check the password with the internal password list. USER will be in state
	 * 3 if aknowledged, disconnected otherwise.
	 * 
	 * @throws IOException
	 */

	private void processPASS() throws IOException {
		// early return mode
		HashMap<String, String> usrMap;
		String rep = "430", paramCode = "";
		String messageLog = this.getClass().toString() + " USER: "
				+ currentUser + " PASS: " + parametre;

		// Verification des conditions d'entrées
		if (!this.commande.toUpperCase().startsWith("PASS")
				|| (currentUser.length() < 1)) {
			rep = "500";
			paramCode = "Commande invalide";

			// le client rage quitte, fermeture du thread serveur.
			KeepRunning = false;
			myErrorCode.sendErrorMessage(mytools, rep, paramCode, messageLog);

		}

		// gestion du mode anonyme
		if (currentUser.equals("anonymous")) {
			if (mytools.isEmail(parametre) == true) {
				ftpetat = FtpEtat.FS_LOGGED;
				rep = "230";
				paramCode = "valid password.";
			} else {
				// mdp pour anonymous invalide
				rep = "430";
				KeepRunning = false;
				paramCode = "invalid password(email expected).";
			}
			myErrorCode.sendCodeMessage(mytools, rep, paramCode, messageLog);
		} else {
			try {
				// Chargement de la liste des mdp
				String myPath;
				ClassLoader test = this.getClass().getClassLoader();
				if (debugMode) {
					Enumeration<URL> e = test.getResources("");
					while (e.hasMoreElements()) {
						System.err.println("ClassLoader Resource: "
								+ e.nextElement());
					}
					System.err.println("Class Resource: "
							+ this.getClass().getResource("/"));
					myPath = this.getClass().getProtectionDomain()
							.getCodeSource().getLocation().toURI().getPath();
					System.err.println("CodeSource location:" + myPath);
				}

				myPath = new File(".").getAbsolutePath().replaceAll(".$", "");
				if (debugMode) {
					System.err.println("New File(.) location:" + myPath);
				}

				// pwdInputStream =
				// this.getClass().getClassLoader().getResourceAsStream(TableDesMdps);
				pwdInputStream = new FileInputStream(myPath + TableDesMdps);
				// this.getClass().getProtectionDomain().getCodeSource().getLocation().toString()
				if (pwdInputStream == null) {
					throw new Exception(
							"Cannot open password file (not found ?)");
				}
				usrMap = mytools.loadPasswordList(pwdInputStream);
				pwdInputStream.close();
				;

				// verification du login/pwd
				if (usrMap.containsKey(currentUser)) {
					if (parametre.equals(usrMap.get(currentUser))) {
						rep = "230";
						ftpetat = FtpEtat.FS_LOGGED;
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
				myErrorCode
						.sendCodeMessage(mytools, rep, paramCode, messageLog);

			} catch (IOException ioe) {
				System.out.println(messageLog
						+ " error: Cannot load the users list.\n");
				ioe.printStackTrace();
			} catch (Exception e) {
				System.err
						.println(messageLog
								+ " error: user is not in the users lists. You should log as anonymous.\n");
				e.printStackTrace();

			}
		}

		// Homedir
		currentDir = (ftpetat == FtpEtat.FS_LOGGED) ? usersRootFolder
				+ File.separator + currentUser : "";
		messageLog += " status= " + ftpetat
				+ "(0=wait_login, 1=wait_pass, 2=logged)";
		if (debugMode) {
			System.out.println(messageLog);
		}

		if (ftpetat != FtpEtat.FS_LOGGED) {
			// rien ne va plus, envoi du refus de pass et cloture de la
			// connection. (rfc959)
			killConnection();
		}
	}

	/**
	 * Sends system information to client.
	 * 
	 * @throws IOException
	 */
	private void processSYST() throws IOException {
		String rep = "500", paramCode = "";
		String messageLog = this.getClass().toString() + " commande: "
				+ commande + " ";

		if (commande.equalsIgnoreCase("syst")) {
			rep = "215";

			myErrorCode.sendCodeMessage(mytools, rep, paramCode, messageLog);
		} else {
			paramCode = "";
			myErrorCode.sendErrorMessage(mytools, rep, paramCode, messageLog);
		}
	}

	/**
	 * Print working directory as per FTP RFC595
	 * 
	 * @throws IOException
	 */
	private void processPWD() throws IOException {
		String rep = "550", paramCode = " invalude path.", messageLog = this
				.getClass().toString() + " commande: " + commande + " ";

		if (commande.equalsIgnoreCase("pwd")) {
			rep = "257";
			paramCode = currentDir.length() > 0 ? currentDir : usersRootFolder
					+ File.separator + currentUser;
			currentDir = paramCode;
			myErrorCode.sendCodeMessage(mytools, rep, paramCode, messageLog);
		} else {
			myErrorCode.sendErrorMessage(mytools, rep, paramCode, messageLog);
		}
	}

	/**
	 * Define type of file to transfer (ascii or binary)
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
				myErrorCode.sendErrorMessage(mytools, rep, paramCode,
						messageLog);
			}

			myErrorCode.sendCodeMessage(mytools, rep, paramCode, messageLog);
		}
	}

	/**
	 * FTP server sends its extra features like UTF-8, ... so the client can use
	 * them
	 * 
	 */
	private void processFEAT() throws IOException {
		mytools.sendMessage("211- Features");
		mytools.sendMessage("uhm, i'm afraid there is none");
		mytools.sendMessage("I mean really none");
		mytools.sendMessage("211 EndFeature");
	}

	/**
	 * Client tells the server which of his port is in listening mode. Server
	 * will then connect the DATA Channel to the client according to the
	 * information received.
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
			myErrorCode.sendCodeMessage(mytools, rep, paramCode, messageLog);
		} else {
			myErrorCode.sendErrorMessage(mytools, rep, paramCode, messageLog);
		}
	}

	/**
	 * PASSIVE mode: the client behind a router or a firewall is not reachable
	 * for the server. So the other way around is proposed. the server will open
	 * a listening socket and inform the client through the answer. The client
	 * will be able to open a data channel to transfer data.
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
			myftpData = new FtpData(cltSocketCtrl.getLocalAddress()
					.getHostAddress(), isPASV, debugMode);

			port_url = myftpData.getPort_url();
			if (port_url != null) {
				rep = "227";
			} else {
				// echec, on repasse en mode direct et on tue le thread
				isPASV = false;
				myftpData.setPASV(isPASV);
				myftpData.setKeepRunning(false);
			}

			paramCode = port_url;
			messageLog += myftpData.getDataAddr() + ":"
					+ myftpData.getDataPort();

			myErrorCode.sendCodeMessage(mytools, rep, paramCode, messageLog);

		} else {
			myErrorCode.sendErrorMessage(mytools, rep, paramCode, messageLog);
		}
	}

	/**
	 * Sends the directory content of the current directory to the ftp client
	 * according to the chosen mode.
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

			// Liste des fichiers et dossiers
			currentDir = currentDir.length() > 0 ? currentDir : usersRootFolder
					+ File.separator + currentUser;
			// tableau des infos a envoyer
			List<String> aInfo2Send = mytools.getDirectoryListing(currentDir);

			// envoi du listing
			// ici on ne gere que le passif
			if (myftpData == null) {
				// creation du thread
				// PASV le thread est deja crée...
				if (!isPASV) {
					myftpData = new FtpData(dataSrvSocket, cltDataAddr,
							cltDataPort, aInfo2Send, commande, parametre);
				}
			} else {
				if (!isPASV) {
					// on affecte les nouvelles données de ports reçues du
					// client.
					myftpData.setDataAddr(cltDataAddr);
					myftpData.setDataPort(cltDataPort);
					// myftpData.reconnect();
				}
			}

			// parametrage du thread
			myftpData.setaString(aInfo2Send);
			myftpData.setCommande(commande);
			myftpData.setParametre(parametre);
			// myftpData.setPASV(isPASV);

			// amorce de l'envoi
			rep = "150";
			paramCode = "";
			myErrorCode.sendCodeMessage(mytools, rep, paramCode, messageLog);

			// envoi du contenu par le thread
			myftpData.run();
			while (myftpData.isAlive()) {
			}

			// cloture de l envoi
			rep = myftpData.getReturnstatus();
			myErrorCode.sendCodeMessage(mytools, rep, messageLog);

		} else {
			// bah, ce n'est pas list qui a ete recu.
			myErrorCode.sendErrorMessage(mytools, rep, paramCode, messageLog);
		}
	}

	/**
	 * Change Working Directory as per RFC959.
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

			tempDir = mytools.getNewDirectory(currentDir, parametre);
			if (!tempDir.equalsIgnoreCase(currentDir)) {
				currentDir = tempDir;
				rep = "250";
				paramCode = "Directory changed to " + currentDir;
			} else {
				rep = "450";
				paramCode = "Directory not changed:" + currentDir;
			}
		}
		myErrorCode.sendCodeMessage(mytools, rep, paramCode, messageLog);
	}

	/**
	 * 
	 * FTP command to close the connection.
	 * 
	 * 
	 * @throws IOException
	 */
	private void processQUIT() throws IOException {
		String rep = "221", paramCode = "Requesting end of session.", messageLog = this
				.getClass().toString() + "processQUIT" + " command: ";

		if (commande.equalsIgnoreCase("quit")) {
			myErrorCode.sendCodeMessage(mytools, rep, paramCode, messageLog);
			killConnection();
		}
	}

	/**
	 * FTP client requested the removal of a file or directory.
	 * 
	 * @throws IOException
	 */
	private void processDELE() throws IOException {
		String rep = "502", paramCode = commande + " unsuccessful for "
				+ parametre, messageLog = this.getClass().toString()
				+ "processDELE" + " commande: " + commande + " " + parametre;
		Boolean resDel = false;

		if (currentUser.equalsIgnoreCase("anonymous")) {
			rep = "450";
			paramCode = currentUser
					+ " cannot delete or upload file. Please use an account.";
		} else if (commande.equalsIgnoreCase("dele")) {

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

		}
		myErrorCode.sendCodeMessage(mytools, rep, paramCode, messageLog);
	}

	/**
	 * Sockets closure by request.
	 * 
	 */
	private void killConnection() {
		try {
			KeepRunning = false;
			currentUser = "Anonymous";
			if (debugMode) {
				System.out.println(this.getClass().toString()
						+ " Killing the connection");
			}

			mytools.CloseStreams();
			this.cltSocketCtrl.close();

		} catch (Exception e) {
			System.err.println(this.getClass().toString()
					+ " error: requesting connection closure \n");
			e.printStackTrace();
		}

	}

	/**
	 * @param string
	 * @uml.property name="commande"
	 */
	public void setCommande(String string) {
		this.commande = string;

	}

	/**
	 * @return
	 * @uml.property name="rep"
	 */
	public Object getRep() {
		return rep;
	}

	/**
	 * @return
	 * @uml.property name="paramCode"
	 */
	public Object getParamCode() {
		// TODO Auto-generated method stub
		return paramCode;
	}
}
