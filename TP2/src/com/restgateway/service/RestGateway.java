package com.restgateway.service;

import java.io.IOException;

import javax.inject.Inject;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;


import com.restgateway.services.FTPService;

/**
 * FTP Gateway to allow browser to operate as a FTP Client.
 * 
 * Salomon Emeline & Dubiez François.
 * 
 * Based on: * http://localhost:8080/rest/api/helloworld * @author Lionel
 * Seinturier <Lionel.Seinturier@univ-lille1.fr>
 */

@Path("/ftpgateway")
public class RestGateway {
	@Inject	private FTPService ftpService;
	
	//FTPService ftpService = new FTPService();

	private Credentials nameStorage = new Credentials();
	private static String ftpHostName = "127.0.0.1";
	private static int ftpPort = 2100;

	@GET
	@Produces("text/html")
	public String sayHello() {
		String msg = "<h1>This is Web gateway to reach a FTP Server "
				+ ftpHostName + ":" + String.valueOf(ftpPort) + "</h1>";

		return msg;
	}

	// ************************************************************************
	// login / logout
	// ************************************************************************

	@Path("/login/{lname}/password/{lpass}")
	@GET
	public String loginToFtp(@FormParam("lname") final String loginName,
			@FormParam("lpass") final String loginPass) {

		this.nameStorage.setLogin(loginName);
		this.nameStorage.setPassword(loginPass);

		String msg = "";

		msg= ftpService.getWelcomeMsg(ftpHostName,ftpPort,this.nameStorage.getLogin(),this.nameStorage.getPassword());
		
		return msg; 
	}
	
	/*
	public Response addPerson(@Context final UriInfo uriInfo,
			@FormParam("lname") final String loginName,
			@FormParam("lpass") final String loginPass) {

		this.nameStorage.setLogin(loginName);
		this.nameStorage.setPassword(loginPass);

		String msg = "";

		ftpService.getWelcomeMsg(ftpHostName,ftpPort,this.nameStorage.getLogin(),this.nameStorage.getPassword());
		
		
		//return Response.created(
		//		uriInfo.getRequestUriBuilder().path(email).build()).build();
	}
*/
	// ************************************************************************
	// file op
	// ************************************************************************

	/**
	 * LIST PART
	 * 
	 * GetFileList return the root file list directory of the server This is the
	 * default behavior when you get file/ without any parameter
	 * 
	 * @return String containing HTML content
	 */
	@GET
	@Path("/file")
	@Produces("text/html")
	public String getFileList() {
		FTPClient client = new FTPClient();
		try {
			client.connect(ftpHostName, ftpPort);
		} catch (IOException ex) {

			return HTMLGenerator.getInstance().getFtpErrorConnectionContent(
					ex.getMessage());
		}
		try {
			if (client.login(this.nameStorage.getLogin(),
					this.nameStorage.getPassword())) {
				FTPFile[] fileList = client.listFiles();
				String cwd = client.printWorkingDirectory();
				client.disconnect();
				return HTMLGenerator.getInstance().getFileListWith(cwd,
						fileList);
			}
		} catch (IOException ex) {
			return HTMLGenerator.getInstance().getError(ex.getMessage());
		}
		return "";
	}
}
