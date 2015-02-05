package com.ftp.server;

public final class ftpEtat {

	/* l etat initial */
	final static int FS_WAIT_LOGIN = 0;
	
	/* etat après la commande USER */
	final static int FS_WAIT_PASS = 1;
	
	/* utlisateur identifié après la commande PASS */
	final static int FS_LOGGED = 2;
}
