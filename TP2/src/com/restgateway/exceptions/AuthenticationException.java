package com.restgateway.exceptions;

/**
 * The exception is used indicate a problem during client authentication.
 *
 * @author Thomas Durieux
 */
public class AuthenticationException extends RuntimeException {

  private static final long serialVersionUID = 7674458099465801345L;

  /**
   * Constructor
   */
  public AuthenticationException() {
    super();
  }

  /**
   * Constructor
   *
   * @param message Message of exception
   */
  public AuthenticationException(final String message) {
    super(message);
  }

  /**
   * Constructor
   *
   * @param t Throwable exception
   */
  public AuthenticationException(final Throwable t) {
    super(t);
  }

  /**
   * Constructor
   *
   * @param message Message of exception
   * @param t       Throwable exception
   */
  public AuthenticationException(final String message, final Throwable t) {
    super(message, t);
  }
}
