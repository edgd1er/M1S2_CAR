package com.restgateway.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.net.ftp.FTPClient;

import com.restgateway.services.FTPService;
import com.sun.jersey.core.header.FormDataContentDisposition;

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
	private static boolean isPASV = true;

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
		if (ftpService.getFtpClient() == null) {
			ftpService.setFtpClient(new FTPClient());
		}
		List<String> ret = ftpService.getWelcomeMsg(ftpHostName, ftpPort,
				isPASV);
		for (String tmp : ret) {
			msg += tmp + "\n";
		}

		// TODO faire un retour via une response
		return msg;
	}

	@GET
	@Path("/login/{lname}/password/{lpass}")
	public String loginToFtp(@PathParam("lname") final String loginName,
			@PathParam("lpass") final String loginPass) throws IOException {
		String msg = "Error, Bad login and/or password. Please try again";

		this.nameStorage.setLogin(loginName);
		this.nameStorage.setPassword(loginPass);

		msg = ftpService.loginToFtp(ftpHostName, ftpPort,
				this.nameStorage.getLogin(), this.nameStorage.getPassword(),
				isPASV);
		ftpService.disconnectClient();

		if (msg.toLowerCase().contains("error")) {
			this.nameStorage.setLogin("");
			this.nameStorage.setPassword("");

		}
		// TODO faire un retour via une response
		return msg;
	}

	@POST
	@Path("/loginPost")
	public String loginToFtpPost(@FormParam("lname") final String loginName,
			@FormParam("lpass") final String loginPass) throws IOException {

		String msg = "Error, Bad login and/or password. Please try again";

		this.nameStorage.setLogin(loginName);
		this.nameStorage.setPassword(loginPass);

		msg = ftpService.loginToFtp(ftpHostName, ftpPort,
				this.nameStorage.getLogin(), this.nameStorage.getPassword(),
				isPASV);
		ftpService.disconnectClient();

		if (msg.toLowerCase().contains("error")) {
			this.nameStorage.setLogin("");
			this.nameStorage.setPassword("");

		}
		// TODO faire un retour via une response
		return msg;
	}

	@DELETE
	@Path("/logout")
	public String logout() throws IOException {

		this.nameStorage.setLogin("");
		this.nameStorage.setPassword("");

		ftpService.disconnectClient();
		ftpService.setFtpClient(null);

		// TODO faire un retour via une response
		return "204";
	}

	/**
	 * LIST PART
	 * 
	 * GetFileList return the root file list directory of the server This is the
	 * default behavior when you get file/ without any parameter
	 * 
	 * @return String containing HTML content
	 */
	@GET
	@Path("/list")
	@Produces("text/html")
	public Response getFileList(@QueryParam("path") String path) {

		return ftpService.getFileList(ftpHostName, ftpPort,
				this.nameStorage.getLogin(), this.nameStorage.getPassword(),
				path, isPASV);

	}

	/**
	 * Simplest File download through a request with GET Method No path given.
	 * 
	 * Filename is the only parameter accepted due to limitation of a GET
	 * parameters (not suitable for path)
	 * 
	 * 
	 * @return Response containing OCTET Stream
	 */

	@GET
	@Path("/getfile/{file}")
	@Produces({ MediaType.APPLICATION_OCTET_STREAM })
	public Response getFile(@Context final UriInfo uriInfo,
			@PathParam("file") String fname) {
		Response response = null; // Response.noContent().build();
		response = ftpService.getFile(ftpHostName, ftpPort,
				this.nameStorage.getLogin(), this.nameStorage.getPassword(),
				"", fname, isPASV);
		return response;
	}

	@GET
	@Path("/getfile")
	@Produces({ MediaType.APPLICATION_OCTET_STREAM })
	public Response getFile(@Context final UriInfo uriInfo,
			@QueryParam("file") String fname, @QueryParam("path") String path) {
		Response response = null; // Response.noContent().build();
		response = ftpService.getFile(ftpHostName, ftpPort,
				this.nameStorage.getLogin(), this.nameStorage.getPassword(),
				path, fname, isPASV);
		return response;
	}

	// *********************************************************

	@GET
	@Path("/getUpLoadForm")
	@Produces("text/html")
	public Response getUploadForm() {
		return HTMLGenerator.getInstance().getUploadContent();
	}

	@GET
	@Path("/LoginForm")
	@Produces("text/html")
	public Response getLoginForm() {
		return HTMLGenerator.getInstance().getLoginFormContent();
	}

	/**
	 * File Upoad through a request with POST Method
	 * 
	 * @param uriInfo
	 * @param path
	 *            Path where to create the new file
	 * @param fname
	 *            Filename on the FTP server
	 * @return Octet-Stream containing the file.
	 * 
	 */

	@POST
	@Path("/uploadfile")
	@Consumes({ MediaType.MULTIPART_FORM_DATA })
	public Response uploadtFile(@FormParam("file") InputStream file,
			@FormParam("file") FormDataContentDisposition fileDetail,
			@PathParam("path") String path) {
		Response response = null;
		response = ftpService.putFile(ftpHostName, ftpPort,
				this.nameStorage.getLogin(), this.nameStorage.getPassword(),
				path, fileDetail.getFileName(), file, isPASV);
		return response;
	}

	/**
	 * Delete with delete method
	 * 
	 * 
	 * @param file
	 *            FileName of the file.
	 * 
	 * @return
	 */
	@Path("/delete/{file}")
	@DELETE
	public Response deleteFile(@PathParam("file") final String file) {
		Response res = ftpService.deleteFile(ftpHostName, ftpPort,
				this.nameStorage.getLogin(), this.nameStorage.getPassword(),
				"", file);
		return res;

	}

	/**
	 * Delete with delete method, pathname & filename
	 * 
	 * 
	 * @param path
	 *            pathname of the file.
	 * @param file
	 *            FileName of the file.
	 * 
	 * @return
	 */
	@Path("/delete")
	@GET
	public Response deleteFile(@QueryParam("path") final String path,
			@QueryParam("file") final String file) {
		ftpService.deleteFile(ftpHostName, ftpPort,
				this.nameStorage.getLogin(), this.nameStorage.getPassword(),
				path, file);
		return Response.ok().build();

	}

}
