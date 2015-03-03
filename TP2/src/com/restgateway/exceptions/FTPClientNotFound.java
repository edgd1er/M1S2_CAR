package com.restgateway.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 * Exception raised when user  not logged in 
 * 
 * @author Emeline Salomon & François Dubiez
 *
 */
public class FTPClientNotFound  extends WebApplicationException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FTPClientNotFound(){
		

	super(
			Response
				.status( Status.BAD_REQUEST )
				.entity( "<html><body><h1>No login, password found. Please, login before any other operation</h1></body></html>"  )
				.build()
		);
	}
}
