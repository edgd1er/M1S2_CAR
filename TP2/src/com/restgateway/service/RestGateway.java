package com.restgateway.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;

import com.restgateway.exceptions.NoLoginPasswordException;
import com.restgateway.services.Credentials;
import com.restgateway.services.FTPService;
import com.restgateway.services.HTMLGenerator;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

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

	/**
	 * Welcome message from the Rest gateway to validate effectiveness of
	 * request processing
	 * 
	 * @return a Html page saying "This is a gateway to reach ...."
	 */
	@GET
	@Produces("text/html")
	public Response sayHello() {
		String msg = "<html>" + HTMLGenerator.getInstance().getCssContent()
				+ "<body><h1>" + "This is a Web gateway to reach a FTP Server "
				+ ftpHostName + ":" + String.valueOf(ftpPort)
				+ "</h1></body></html>";

		return Response.ok().entity(msg).build();
	}

	// ************************************************************************
	// login / logout
	// ************************************************************************
	/**
	 * Welcome message from the FTP server to validate effectiveness of
	 * communication with the FTP server.
	 * 
	 * @return 
	 *         "220 Service ready for new user on 127.0.0.1.. login using the USER command."
	 * 
	 */
	@GET
	@Path("/welcome")
	public Response welcomeFtp() {
		String msg = "<html>" + HTMLGenerator.getInstance().getCssContent()
				+ "<body><h1>";
		if (ftpService.getFtpClient() == null) {
			ftpService.setFtpClient(new FTPClient());
		}
		List<String> ret = ftpService.getWelcomeMsg(ftpHostName, ftpPort,
				isPASV);

		for (String tmp : ret) {
			msg += tmp + "\n";
		}
		msg += "</h1></body></html>";
		return Response.ok().entity(msg).build();
	}

	/**
	 * login to ftp server with a http get method.
	 * 
	 * @param loginName
	 * @param loginPass
	 * @return current working directory or ftp error
	 * @throws IOException
	 */
	@GET
	@Path("/login/{lname}/password/{lpass}")
	public Response loginToFtp(@PathParam("lname") final String loginName,
			@PathParam("lpass") final String loginPass,
			@Context HttpServletRequest request,
			@Context HttpServletResponse response) throws IOException {
		String msg = "<html>" + HTMLGenerator.getInstance().getCssContent()
				+ "<body><h1>";

		this.nameStorage.setLogin(loginName);
		this.nameStorage.setPassword(loginPass);

		msg = ftpService.loginToFtp(ftpHostName, ftpPort,
				this.nameStorage.getLogin(), this.nameStorage.getPassword(),
				isPASV);
		ftpService.disconnectClient();

		if (msg.toLowerCase().contains("error")) {
			this.nameStorage.setLogin("");
			this.nameStorage.setPassword("");
			return new NoLoginPasswordException().getResponse();
		}

		String contextPath = request.getContextPath();
		response.sendRedirect(contextPath + "/api/ftp/list");
		return Response.status(Status.ACCEPTED).build();

	}

	/**
	 * login to ftp server with a http post method (From login Form) .
	 * 
	 * @param lName
	 * @param lpass
	 * @param request
	 *            get HttpServletRequest in order to manipulate refresh page
	 * @param response
	 *            get HttpServletResponse in order to manipulate refresh page
	 * @return current working directory or ftp error
	 * @throws IOException
	 */
	@POST
	@Path("/loginPost")
	public Response loginToFtpPost(@Context SecurityContext sc,@FormParam("lname") final String loginName,
			@FormParam("lpass") final String loginPass,
			@Context HttpServletRequest request,
			@Context HttpServletResponse response) throws IOException {

		String msg = "<html>" + HTMLGenerator.getInstance().getCssContent()
				+ "<body><h1>";

		this.nameStorage.setLogin(loginName);
		this.nameStorage.setPassword(loginPass);

		msg += ftpService.loginToFtp(ftpHostName, ftpPort,
				this.nameStorage.getLogin(), this.nameStorage.getPassword(),
				isPASV);

		ftpService.disconnectClient();

		if (msg.toLowerCase().contains("error")) {
			this.nameStorage.setLogin("");
			this.nameStorage.setPassword("");
			return new NoLoginPasswordException().getResponse();
		}
		System.out.println("auth: "+sc.getAuthenticationScheme()+" \n" 
				+ "user: "+sc.getUserPrincipal()+"\n" +
				"secure: " + sc.isSecure());
		
		 String contextPath = request.getContextPath();
		 
		response.sendRedirect(contextPath + "/api/ftp/list");
		return Response.status(Status.ACCEPTED).build();
	}

	/**
	 * Login out from FTP server with http delete method
	 * 
	 * @param request
	 *            get HttpServletRequest in order to manipulate refresh page
	 * @param response
	 *            get HttpServletResponse in order to manipulate refresh page
	 * @return html page to login.
	 * @throws IOException
	 */
	@DELETE
	@Path("/logout")
	public Response logout(@Context HttpServletRequest request,
			@Context HttpServletResponse response) throws IOException {

		this.nameStorage.setLogin("");
		this.nameStorage.setPassword("");

		ftpService.disconnectClient();
		ftpService.setFtpClient(null);

		String contextPath = request.getContextPath();
		response.sendRedirect(contextPath + "/api/ftp/LoginForm");
		return Response.status(Status.ACCEPTED).build();
	}

	/**
	 * Login out from FTP server with http get method more suitable to use with
	 * a browser.
	 * 
	 * @param request
	 *            get HttpServletRequest in order to manipulate refresh page
	 * @param response
	 *            get HttpServletResponse in order to manipulate refresh page
	 * @return html page to login.
	 * @throws IOException
	 */
	@GET
	@Path("/logout")
	public Response logoutGet(@Context HttpServletRequest request,
			@Context HttpServletResponse response) throws IOException {

		this.nameStorage.setLogin("");
		this.nameStorage.setPassword("");

		ftpService.disconnectClient();
		ftpService.setFtpClient(null);

		String contextPath = request.getContextPath();
		response.sendRedirect(contextPath + "/api/ftp/LoginForm");
		return Response.status(Status.OK).build();
	}

	/**
	 * LIST PART with http Get Method
	 * 
	 * GetFileList return the root file list directory of the server This is the
	 * default behavior when you get file/ without any parameter
	 * 
	 * @param path
	 * @return String containing HTML content
	 * 
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
	 * @param fname
	 *            Filename of the file to retrieve
	 * @return Response containing OCTET Stream
	 */
	@GET
	@Path("/getfile/{file}")
	@Produces({ MediaType.APPLICATION_OCTET_STREAM })
	public Response getFile(@PathParam("file") String fname) {
		return ftpService.getFile(ftpHostName, ftpPort,
				this.nameStorage.getLogin(), this.nameStorage.getPassword(),
				"", fname, isPASV);
	}

	/**
	 * getFile allows user to retrieve a file through a HTTP Get request using
	 * queryparameters. Furthermore a direct access is possible with the path.
	 * 
	 * @param fname
	 *            Filename of the file to retrieve
	 * @param path
	 *            Path of the filename.
	 * @return Response containing OCTET Stream
	 */
	@GET
	@Path("/getfile")
	@Produces({ MediaType.APPLICATION_OCTET_STREAM })
	public Response getFile(@QueryParam("file") String fname,
			@QueryParam("path") String path) {
		return ftpService.getFile(ftpHostName, ftpPort,
				this.nameStorage.getLogin(), this.nameStorage.getPassword(),
				path, fname, isPASV);
	}

	// *********************************************************
	// Login & Upload Forms
	// *********************************************************

	/**
	 * Produce the UploadForm in html.
	 * 
	 * @return Response containing the UploadForm in html
	 */
	@GET
	@Path("/getUpLoadForm")
	@Produces("text/html")
	public Response getUploadForm() {
		return HTMLGenerator.getInstance().getUploadContent();
	}

	/**
	 * Produces the LoginForm in html format
	 * 
	 * @return Response containing the LoginForm in html
	 */
	@GET
	@Path("/LoginForm")
	@Produces("text/html")
	public Response getLoginForm() {
		return HTMLGenerator.getInstance().getLoginFormContent();
	}

	/**
	 * File Upload through a request with POST Method
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
    //@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Consumes("multipart/form-data")
	@Produces(MediaType.TEXT_HTML)
	public Response uploadFile(
		       @DefaultValue("true") @FormDataParam("enabled") boolean enabled,
		       @FormDataParam("fform") InputStream is2,
	           @Multipart(value="filename", type="text/plain") String filename,
               @Multipart(value="file", type="*/*") InputStream is) throws WebApplicationException{

		
		Response response = null;
		
		response = ftpService.postFile(ftpHostName, ftpPort,
				this.nameStorage.getLogin(), this.nameStorage.getPassword(),
				"", filename, is,
				isPASV);
		return response;
	}

	
	/**
	 * Upload file through a put method 
	 * 
	 * @param filePath
	 * @return
	 */
	@PUT
	@Path("/upload/")
	public Response putUpload(@QueryParam("file") String filePath) {
	Response response = null;
	
	response = ftpService.postFile(ftpHostName, ftpPort,
			this.nameStorage.getLogin(), this.nameStorage.getPassword(),
			"",filePath,isPASV);
	return response;
	
	}
	
	/**
	 * Delete with delete method
	 * 
	 * 
	 * @param file
	 *            FileName of the file.
	 * @return Operation status
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
	 * Delete with GET method, pathname & filename. More suitable for Browser
	 * use.
	 * 
	 * @param path
	 *            pathname of the file.
	 * @param file
	 *            FileName of the file.
	 * 
	 * @return Operation status
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
