package com.restgateway.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.restgateway.services.HTMLGenerator;
/**
 * Exception raised when incorrect path is send to FTP server.
 * 
 * 
 * @author Emmeline Salomon & François Dubiez
 *
 */
public class IncorrectPathException extends WebApplicationException {
	private static final long serialVersionUID = 6817489620338221395L;

	public IncorrectPathException(String msg) {
		super(
				Response.status(Status.BAD_REQUEST)
						.entity("<html>"
								+ HTMLGenerator.getInstance().getCssContent()
								+ "<meta http-equiv=\"refresh\" content=\"1; URL=\"javascript:history.back()\""
								+ "\"><body><h1>!Incorrect path.(" + msg + ")"
								+ "</h1></body></html>").build());
	}
}
