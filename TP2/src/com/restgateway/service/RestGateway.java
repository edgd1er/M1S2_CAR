package com.restgateway.service;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

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

@Path("/ftp")
public class RestGateway {
	@Inject
	private FTPService ftpService;


	/**
	 * credentials store
	 */
	private Credentials nameStorage = new Credentials();
	private static String ftpHostName = "127.0.0.1";
	private static int ftpPort = 2100;
	/**
	 * FTP Passive mode
	 */
	private static boolean isPASV= true;
	
	
	
	// private static String ftpHostName = "ftps.fil.univ-lille1.fr";
	// private static int ftpPort = 21;

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

	@GET
	@Path("/welcome")
	public String welcomeFtp() {
		String msg = "";
		if (ftpService.getFtpClient()==null){ftpService.setFtpClient(new FTPClient());}
		List<String> ret = ftpService.getWelcomeMsg(ftpHostName, ftpPort, isPASV);
		for (String tmp : ret) {
			msg += tmp + "\n";
		}
		return msg;
	}

	@GET
	@Path("/login/{lname}/password/{lpass}")
	public String loginToFtp(@PathParam("lname") final String loginName,
			@PathParam("lpass") final String loginPass) {
		String msg = "";

		this.nameStorage.setLogin(loginName);
		this.nameStorage.setPassword(loginPass);
		msg = ftpService.loginToFtp((FTPClient)null, ftpHostName, ftpPort,
				this.nameStorage.getLogin(), this.nameStorage.getPassword(),isPASV);
		return msg;
	}

	/*
	 * public Response addPerson(@Context final UriInfo uriInfo,
	 * 
	 * @FormParam("lname") final String loginName,
	 * 
	 * @FormParam("lpass") final String loginPass) {
	 * 
	 * this.nameStorage.setLogin(loginName);
	 * this.nameStorage.setPassword(loginPass);
	 * 
	 * String msg = "";
	 * 
	 * ftpService.getWelcomeMsg(ftpHostName,ftpPort,this.nameStorage.getLogin(),this
	 * .nameStorage.getPassword());
	 * 
	 * 
	 * //return Response.created( //
	 * uriInfo.getRequestUriBuilder().path(email).build()).build(); }
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
	@Path("file/login/{lname}/password/{lpass}/list")
	@Produces("text/html")
	public String getFileList(@PathParam("lname") final String loginName,
			@PathParam("lpass") final String loginPass) {
		String cwd = "";
		FTPClient ftpClient=null;
		
		this.nameStorage.setLogin(loginName);
		this.nameStorage.setPassword(loginPass);

		try {
			cwd = ftpService.loginToFtp((FTPClient)null, ftpHostName, ftpPort,
					this.nameStorage.getLogin(), this.nameStorage.getPassword(),isPASV);
			ftpClient = ftpService.getFtpClient();
			FTPFile[] fileList = ftpClient.listFiles(cwd);
			FTPFile[] dirList = ftpClient.listDirectories(cwd);
			ftpClient.disconnect();
			return HTMLGenerator.getInstance().getFileListWith(cwd, dirList,
					fileList);
		} catch (IOException ex) {
			return HTMLGenerator.getInstance().getError(ex.getMessage());
		}
	}
		/**
		 * FILE GET with Method POST
		 * 
		 * Send a file with a post method
		 * 
		 * @return String containing HTML content
		 */
	@Produces( { MediaType.APPLICATION_OCTET_STREAM  } )
		@POST
		public Response  getFile( @Context final UriInfo uriInfo,
					@FormParam( "cmd" ) final String cmd,
					@FormParam( "path" ) final String path, 
					@FormParam( "name" ) final String fname)
		{
			if (cmd.equals("get"))
			{
				ftpService.getFile(ftpHostName, ftpPort, this.nameStorage.getLogin(), this.nameStorage.getPassword(),path,name,isPASV);
			return 
					;
			}
		}
	
	
	/**
	 * Delete with delete method
	 * 
	 *
	 * @param path	pathname of the file.
	 * @param file	FileName of the file.

	 * @return 
	 */
	@Path( "/delete/path/{path}/file/{file}" )
	@DELETE
	public Response deletePerson( @PathParam( "path" ) final String path, @PathParam( "file" ) final String file) {
		ftpService.deleteFile( path ,file );
		return Response.ok().build();

	}
	
	
}
