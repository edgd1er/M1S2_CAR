package com.restgateway.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 * Exception to produce html response when trigerred by a not found file.
 * 
 * @author Salomon Emmeline & Dubiez François.
 * 
 * 
 */
public class FileNotFoundException extends WebApplicationException {
	private static final long serialVersionUID = -2894269137259898072L;

	/**
	 * Create HTML Response within the exception.
	 * 
	 * @param fileName	File that trigerred the exception.
	 */
	public FileNotFoundException(final String fileName) {
		super(
				Response.status(Status.NOT_FOUND)
						.entity("<html><meta http-equiv=\"refresh\" content=\"1; URL=\"javascript:history.back()\""
								+ "><body><h1>File not Found: "
								+ fileName
								+ "</h1></body></html>").build());
	}
}
