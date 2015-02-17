# M1S2_CAR
Conception application repartie


﻿Implémentation d’un serveur FTP en Java

16/02/2015

*** 0/ README


Éxécuter le projet:
        java -jar Serveur.jar /path/to/home/dir [1/0]

1/0 => debugMode: affichage verbeux du traitement.

Ne pas oublier de placer avec le jar un fichier mdp.txt contenant la liste des utilisateurs avec leurs mot de passe sous le forme
user1:password1
user2:password2

*** 1/ Introduction


Ce programme crée un serveur FTP utilisant le port 2100 pour communiquer avec des clients, le serveur gère les requête USER, PASS, QUIT, LIST RETR, STOR, PASV, PWD, CWD, PORT, CDUP et deux autres commandes (DELE, QUOTE) que j'ai ajouté pour mener a bien les tests fonctionnels. 
Les utilisateurs seront placé dans un repertoir /{serverpath}/username, ou serverpath peut etre passer en parametre. Il n'a pas été mis en place un contingentement dans le répertoire de travail.



*** 2/ Architecture


Packages:

	com.ftp.client
		FtpClient

	com.ftpServer
		Server
		FtpRequest
		ftpEtat

	com.ftp.tools
		ErrorCode		
		Tools





Design patterns:
	Etat



Catch:
catch (IOError ioe)
catch (IOException e)
catch (FileNotFoundException)


Throw:
	FTPData, FTPRequest


*** 3/ Parcours du code.


Server se lance en mode ecoute et lance un FTPRequest en thread pour traiter les clients.


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



FTPRequest va traiter les requetes envoyés sur le canal de data et lancer si besoin un autre thread pour les transferts de données.

Dans le run() on peut remarquer la boucle infinie (presque) pour traiter les commandes et gerer la deconnexion du client.

package com.ftp.server;


/**
 * 
 * Main class to handle the control channel. this thread is launched by the
 * server thread. Most of the FTP commands are implemented.
 * 
 * 
 * @author Emeline Salomon & François Dubiez
 * 
 */
public class FtpRequest extends Thread {...

	public void run() {

		String rep = "", paramCode = "", messageLog = "";
		int nbCommandeVide = 0;

		messageLog = this.getClass().toString();

		try {
			mytools = new Tools(cltSocketCtrl);
		} catch (IOException e) {
			System.err
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
			}
		} else {
			msgAccueil();
		}

		try {
			while (KeepRunning) {
				parseCommande(mytools.receiveMessage());
				if (Server.debugMode) {
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
		Server.nbClients--;
		System.out.println("Thread End.("+String.valueOf(Server.nbClients)+" clients remaining)");
	}

**** 5 / Tests Fonctionnels

#################################################
##       tests login des utilisateurs          ##
#################################################
Test de refus d envoi de fichier pour un anonyme: (attendu 532):  OK
Test de refus login inconnu / pass: (attendu 430):  OK
Test de refus login connu / pass incorrect: (attendu 430):  OK
Test d acceptation login connu / pass correct: (attendu 230):  OK
Test de changement de repertoire: (attendu 250):  OK


#################################################
##           test en mode actif ASCII          ##
#################################################

Test de l affichage du contenu d'un repertoire: (attendu 226):  OK
Test de l envoi d'un fichier ascii: (attendu successful):  OK
test de la taille du fichier:20131: ok


Test de la reception d'un fichier ascii: (attendu successful):  OK
test de la taille du fichier:20066: ok
test du md5 du fichier:1: ok


#################################################
##           test en mode actif BINAIRE        ##
#################################################
Test de l envoi d'un fichier binaire: (attendu successful):  OK
test de la taille du fichier:176679: ok


Test de la reception d'un fichier binaire: (attendu successful):  OK
test de la taille du fichier:176679: ok
test du md5 du fichier:1: ok


#################################################
##           test en mode passif ASCII        ##
#################################################

Test de l affichage du contenu d'un repertoire: (attendu 226):  OK
Test de l envoi d'un fichier ascii: (attendu successful):  OK
test de la taille du fichier:20131: ok


Test de la reception d'un fichier ascii: (attendu successful):  OK
test de la taille du fichier:20066: ok
test du md5 du fichier:1: ok


#################################################
##           test en mode passif BINAIRE        ##
#################################################
Test de l envoi d'un fichier bin: (attendu successful):  OK
test de la taille du fichier:176679: ok


Test de la reception d'un fichier bin: (attendu successful):  OK
test de la taille du fichier:176679: ok
test du md5 du fichier:1: ok


******************************************************
