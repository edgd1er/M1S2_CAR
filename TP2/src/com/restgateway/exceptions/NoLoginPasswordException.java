package com.restgateway.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public class NoLoginPasswordException extends WebApplicationException {
	private static final long serialVersionUID = 6817489620338221395L;

	public NoLoginPasswordException() {
		super(
			Response
				.status( Status.BAD_REQUEST )
				.entity( "<html><body><h1>It works!Incorrect Login and/or password. Please login !</h1></body></html>" )
				.build()
		);
	}
}
