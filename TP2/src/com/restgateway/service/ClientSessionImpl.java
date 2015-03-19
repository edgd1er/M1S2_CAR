package com.restgateway.service;

import com.restgateway.exceptions.ClientSessionException;
import org.apache.commons.net.ftp.FTPClient;

/**
 * This class stores all Session information of a spesific client.
 *
 * @author Thomas Durieux
 */
public class ClientSessionImpl implements ClientSession {

  private FTPClient ftpClient;
  String username;
  String password;
  private boolean isLogged = false;
  private boolean isConnected = false;

  public ClientSessionImpl() {
    ftpClient = new FTPClient();
  }

  public ClientSessionImpl(String username, String password) {
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
  public void connect() throws ClientSessionException {
    if (isConnected) {
      return;
    }
    try {
      FTPCommand.INSTANCE.connectClient(this);
      isConnected = true;
    } catch (FTPCommandException e) {
      isLogged = false;
      throw new ClientSessionException(e);
    }
  }

  /**
   * @see ClientSession
   */
  public void login() throws ClientSessionException {
    if (isLogged) {
      return;
    }
    try {
      FTPCommand.INSTANCE.loginClient(this);
      isLogged = true;
    } catch (FTPCommandException e) {
      isLogged = false;
      throw new ClientSessionException(e);
    }
  }

  /**
   * @see ClientSession
   */
  public void disconnect() throws ClientSessionException {
    try {
      FTPCommand.INSTANCE.disconnectClient(this);
      AuthenticationManager.INSTANCE.removeClientSession(this);
      isLogged = false;
      isConnected = false;
    } catch (FTPCommandException e) {
      throw new ClientSessionException(e);
    }
  }

  /**
   * @see ClientSession
   */
  public boolean isLogged() {
    return isLogged;
  }
}
