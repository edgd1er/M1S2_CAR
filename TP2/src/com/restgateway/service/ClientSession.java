package com.restgateway.service;

import com.restgateway.exceptions.ClientSessionException;
import org.apache.commons.net.ftp.FTPClient;

/**
 * This interface describes methods to manipulate the session of a client.
 *
 * @author Thomas Durieux
 */
public interface ClientSession {

  /**
   * Connect the client to ftp server
   *
   * @throws ClientSessionException
   */
  void login() throws ClientSessionException;

  /**
   * Connect the client to ftp server
   *
   * @throws ClientSessionException
   */
  void connect() throws ClientSessionException;

  /**
   * Disconnect the client to ftp server
   *
   * @throws ClientSessionException
   */
  void disconnect() throws ClientSessionException;

  String getUsername();

  void setUsername(String username);

  String getPassword();

  void setPassword(String passwword);

  FTPClient getFTPClient();

  /**
   * If the client is connected to the FTP server
   *
   * @return true if the client is connected to the FTP server
   */
  public boolean isLogged();
}
