package com.restgateway.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.restgateway.services.HTMLGenerator;

/**
 * Exception raised when incorrect Login and/or password is send to FTP server.
 * 
 * 
 * @author Emmeline Salomon & François Dubiez
 *
 */
public class NoLoginPasswordException extends WebApplicationException {
	private static final long serialVersionUID = 6817489620338221395L;

	public NoLoginPasswordException() {
		super(
				Response.status(Status.BAD_REQUEST)
						.entity("<html>"
								+ HTMLGenerator.getInstance().getCssContent()
								+ "<body><h1>No login, password found. Please, login before any other operation"
								+ "</h1></body></html>").build());
	}
}
