package com.ftp.server;

/**
 * Pattern Etat qui permet de permettre seulement un certain nombre
 * de commandes selon l'etat.
 * Etat 1: USER et QUIT autorises.
 * Etat 2: USER, PASS et QUIT autorises.
 * Etat 3: Toutes les commandes sont permises.
 * 
 * @author user
 *
 */
public final class ftpEtat {

	/**
	 *  l etat initial 
	 */
	final static int FS_WAIT_LOGIN = 0;
	
	/**
	 *  etat après la commande USER 
	 */
	final static int FS_WAIT_PASS = 1;
	
	/**
	 *  utlisateur identifié après la commande PASS 
	 *  */
	final static int FS_LOGGED = 2;
}
