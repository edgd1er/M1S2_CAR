package com.restgateway.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 * Exception to produce html response when trigerred by a not uploaded file.
 * 
 * @author Salomon Emmeline & Dubiez François.
 * 
 * 
 */

public class FileNotUploadedException extends WebApplicationException {
	private static final long serialVersionUID = -2894269137259898072L;

	/**
	 * create html response within the exception
	 * 
	 * @param fileName
	 *            File that should have been uploaded.
	 */
	public FileNotUploadedException(final String fileName) {
		super(
				Response.status(Status.NOT_FOUND)
						.entity("<html><meta http-equiv=\"refresh\" content=\"1; URL=\"javascript:history.back()\""
								+ "><body><h1>File not uploaded: "
								+ fileName
								+ "</h1></body></html>").build());
	}
}
