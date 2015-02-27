package com.restgateway.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public class FileNotFoundException extends WebApplicationException {
	private static final long serialVersionUID = -2894269137259898072L;
	
	public FileNotFoundException( final String fileName ) {
		super(
			Response
				.status( Status.NOT_FOUND )
				.entity( "File not found: " + fileName )
				.build()
		);
	}
}
