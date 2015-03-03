package com.restgateway.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public class FileNotFoundException extends WebApplicationException {
	private static final long serialVersionUID = -2894269137259898072L;

	public FileNotFoundException(final String fileName) {
		super(
				Response.status(Status.NOT_FOUND)
						.entity("<html><meta http-equiv=\"refresh\" content=\"1; URL=\"javascript:history.back()\""
								+ "\"><body><h1>File not found: "
								+ fileName
								+ "</h1></body></html>").build());
	}
}
