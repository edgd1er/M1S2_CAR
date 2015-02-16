package com.ftp.server;

/**
 * State Pattern to allow commands only according to its state 
 * 
 * State 1: USER and QUIT allowed.
 * State 2: USER, PASS and QUIT allowed.
 * State 3: All commands allowed.
 * 
 * @author user
 *
 */
public final class FtpEtat {

	/**
	 *  initial state. user unknown.not logged 
	 */
	final static int FS_WAIT_LOGIN = 0;
	
	/**
	 *  User known, waiting for password. 
	 */
	final static int FS_WAIT_PASS = 1;
	
	/**
	 *  user known and authentified. 
	 *  */
	final static int FS_LOGGED = 2;
}
