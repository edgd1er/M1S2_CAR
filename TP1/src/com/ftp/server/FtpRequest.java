package com.ftp.server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class FtpRequest extends Thread {

	private Socket cltSocketCtrl;
	private ServerSocket srvSocketCtrl;
	private ServerSocket dataSrvSocket;
	private Socket datasocket ;
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
				commande = "";
				parametre = "";
			}
		} catch (Exception e) {
			e.printStackTrace();
			killConnection();
			KeepRunning = false;
			
		}
	}

	public void msgAccueil() {

		System.out.println(this.getClass() + " :client ip: "
				+ cltSocketCtrl.getInetAddress().toString());
		String message = ErrorCode.getMessage("220", srvSocketCtrl
				.getInetAddress().getHostName().toString());

		try {
			Tools.sendMessage(cltSocketCtrl, message);
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
			} else {
				System.out.println(this.getClass().toString()
						+ " Erreur, commande non reconnue:" + commande + " "
						+ parametre);

				Tools.sendMessage(cltSocketCtrl,
						"500 Syntax error, command unrecognized");
			}

		} catch (IOException e1) {
			System.out.println(this.getClass().toString() + " erreur\n");
			e1.printStackTrace();
			KeepRunning = false;
			killConnection();
		}
		/*
		 * (strcmd.startsWith("CWD")) {processCWD(strcmd);} else { if
		 * (strcmd.startsWith("CDUP")) {processCDUP(strcmd);} else { if
		 * (strcmd.startsWith("RETR")) {processRETR(strcmd);} else { if
		 * (strcmd.startsWith("STOR")) {processSTOR(strcmd);} }}}}}}}}}}}
		 */
	}

	// recuperation du login utilisateur
	private void processUSER() throws IOException {

		// Verification des conditions d'entrées
		if (this.commande.toUpperCase().startsWith("USER")
				&& (parametre.length() > 1)) {
			this.currentUser = parametre;
			String Rep = ErrorCode.getMessage("331", "");
			Tools.sendMessage(this.cltSocketCtrl, Rep);
			System.out.println(this.getClass().toString() + " " + parametre
					+ "> currentUser:" + this.currentUser);
			ftpetat = ftpEtat.FS_WAIT_PASS;
			commande = "";
			parametre = "";
		} else {
			ErrorParametre("430", ErrorCode.getMessage("430", ""));
		}

	}

	private void processPASS() {

		HashMap<String, String> usrMap;
		String rep= ErrorCode.getMessage("430", "");

		// Verification des conditions d'entrées
		if (!this.commande.toUpperCase().startsWith("PASS")
		|| (currentUser.length() < 1)) {
			rep= ErrorCode.getMessage("430", "");
		} else if (currentUser.equals("anonymous")) {
			ftpetat=ftpEtat.FS_LOGGED;
			rep = ErrorCode.getMessage("230", "");
		}
		else {
		try {
			// Chargement de la liste des mdp
			usrMap = loadPasswordList();

			// verification du login/pwd
			if ((usrMap.containsKey(currentUser)) && (parametre.equals(usrMap.get(currentUser)))) {
					rep = ErrorCode.getMessage("230", "");
					ftpetat=ftpEtat.FS_LOGGED;
				} 
			
			//Homedir
			currentDir=(ftpetat==ftpEtat.FS_LOGGED)?home + File.separator + currentUser:"";
			Tools.sendMessage(cltSocketCtrl, rep);
			// log
			System.out.println(this.getClass().toString() + " user: "
					+ currentUser + ".status= " + ftpetat +"(0=wait_login, 1=wait_pass, 2=logged)");
			
			if (ftpetat!=ftpEtat.FS_LOGGED) {
				//rien ne va plus, envoi du refus de pass et cloture de la connection.
				killConnection();
			}

		} catch (SocketException se) {
			System.out
			.println(this.getClass().toString()
					+ " erreur: Perte de la connection avec la socket\n");
			se.printStackTrace();

		}
		catch (IOException ioe) {
			System.out
					.println(this.getClass().toString()
							+ " erreur: Impossible de charger la listes des utilisateurs,mdp\n");
			ioe.printStackTrace();
		}
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
			Tools.sendMessage(cltSocketCtrl, rep);
			System.out.println(this.getClass().toString() + ": TYPE  "
					+ ftpType);
		}
	}

	// presentation des features du serveur
	private void processFEAT() throws IOException {
		Tools.sendMessage(cltSocketCtrl, "211- Features");
		Tools.sendMessage(cltSocketCtrl, "Ben, en fait il n'ya en pas");
		Tools.sendMessage(cltSocketCtrl, "vraiment pas");
		Tools.sendMessage(cltSocketCtrl, "211 EndFeature");
	}

	private void processPORT() throws IOException {
		// 227 Entering Passive Mode (192,168,150,90,195,149).
		String rep = "502", paramCode = "Mode Type inconnu";
		if (commande.equalsIgnoreCase("port")) {
			if (parametre.matches("([0-9]{1,3},){5}[0-9]{1,3}")) {
				System.out.println("processPORT"+commande + " " + parametre);
				//recuperation de l @ et port client
				String[] atmp =parametre.split(",");
				cltDataAddr =atmp[0]+"."+atmp[1]+"."+atmp[2]+"."+atmp[3];
				cltDataPort = Integer.parseInt(atmp[4])*256+Integer.parseInt(atmp[5]);
				
				rep = ErrorCode.getMessage("200", "Port accepted on "+ cltDataAddr + ":" + String.valueOf(cltDataPort));
				Tools.sendMessage(cltSocketCtrl, rep);
			}
		}

	}

	private void processPASV() throws IOException {
		// 227 Entering Passive Mode (192,168,150,90,195,149).
		String rep = "502", paramCode = "";
		if (commande.equalsIgnoreCase("pasv")) {
			dataSrvSocket = new ServerSocket(0);
			
			String port_url = getEncPort();
			String msg ="ProcessPASV: creation de la ServerSocket " +dataSrvSocket.getInetAddress().getHostAddress() +":"+dataSrvSocket.getLocalPort();
			rep = ErrorCode.getMessage("227", port_url);

			Tools.sendMessage(cltSocketCtrl, rep);
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
	private void ErrorParametre(String string, String message)
			throws IOException {

		ErrorCode.sendErrorMessage(cltSocketCtrl, string, message);

	}
	
	private void processLIST() throws IOException {
		// 
		String rep = "502", paramCode = "";
		if (commande.equalsIgnoreCase("list")) {
			
			//amorce de l'envoi
			Tools.sendMessage(cltSocketCtrl,ErrorCode.getMessage("150", ""));
			List<String> msg= Tools.getDirectoryListing(currentDir);
			// envoi du listing
			if (cltDataAddr!=null)
			{
				datasocket = new Socket(cltDataAddr, cltDataPort);
				DataOutputStream DataOutStream = new DataOutputStream(datasocket.getOutputStream()); 
				
				for(String strTemp: msg){
					DataOutStream.writeBytes(strTemp);
				}
				// nice and quiet close
				DataOutStream.flush();
				DataOutStream.close();
			}
			
			
			
			datasocket.close();
			Tools.sendMessage(cltSocketCtrl,ErrorCode.getMessage("226", ""));

			String strTemp ="ProcessList: contenu du repertoire local" +msg.toString();
			System.out.println(strTemp );

		}

	}
}
