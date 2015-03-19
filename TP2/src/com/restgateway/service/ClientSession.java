package com.restgateway.service;

import org.apache.commons.net.ftp.FTPClient;

/**
 * This class stores all Session information of a spesific client.
 *
 * @author Emmeline Salomon & François Dubiez
 */
public class ClientSession {

  private FTPClient ftpClient;
  String username;
  String password;
  private boolean isLogged = false;
  private boolean isConnected = false;

  public void setLogged(boolean isLogged) {
	this.isLogged = isLogged;
}

public ClientSession() {
    ftpClient = new FTPClient();
  }

  public ClientSession(String username, String password) {
    this();
    this.username = username;
    this.password = password;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public FTPClient getFTPClient() {
    return ftpClient;
  }

  
  /**
   * @see ClientSession
   */
  public boolean isLogged() {
    return isLogged;
  }

public boolean isConnected() {
	return isConnected;
}

public void setConnected(boolean isConnected) {
	this.isConnected = isConnected;
}
}
