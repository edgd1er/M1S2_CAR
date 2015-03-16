# M1S2_CAR
Conception application repartie


﻿Implémentation d’une passerelle REST pour accèder au serveur FTP en Java

02/03/2015

*** 0/ README

Ce projet permet d'accèder via un navigateur Web d'accéder au travers d'une passerelle à un serveur FTP.
Les principales opérations FTP sont implémentées
- Loging
- Quit
- retrieve
- Store
- delete
- Liste

La liste des services exposées peut etre obtenues a l'URL ci-dessous:
Liste des services: http://localhost:8080/rest/
Descriptions methodes du service api/ftp : http://localhost:8080/rest/api?_wadl


La RFC 2616 indique les utilisations des différents verbes: http://www.w3.org/Protocols/rfc2616/rfc2616-sec9.html

La liste des codes se trouve: http://tools.ietf.org/rfcmarkup?rfc=7231#section-6.2.1

Le Get permet de récupérer une information spécifié par l'URI.
POST permet de déposer un nouvel élément sur le serveur. Trois codes retour sont possibles pour POST, 200 OK, 204 No content, 201 Created.

Le PUT permet une mise a jour d'un élement, si l'élément ne devait pas exister sur le serveur alors un status 201 (created) DOIT etre retourné.
Le principe du PUT n'est que de mettre à jour une ressource et de demander explicitement au client de modifier sa requete pour qu'elle corresponde à un PUT. Nous avons mis en place une implémentation stricte de la RFC.

Le Delete demande la suppression d'un élément communiquer dans l'URI.
Trois états sont possibles ( 200 Ok avec un retour possible, 202 Accepted mais non réalisée, 204 réalisé mais aucun retour n'est à faire.

*** 1/ Design

Design Singleton pour le login/mdp.

La classe RestGateway reçoit les demandes filtrées par JAX-RS, recupére les paramètres et demande à la classe FTPService de traiter la demande.
La classe HTMLGenerator quant a elle produit le code HTML qui sera retourné au client.

*** 2/ Gestion des erreurs

Lorsque qu'une anomalie est rencontrée, une exception est levée, une réponse (Response) est encapsulée dans l'exception, celle ci est transmise par la suite au client.

FileNotFoundException
FileNotUploadedException
FTPClientNotFound
IncorrectPathException
NoLoginPasswordException

Exemple de NoLoginPasswordException
package com.restgateway.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.restgateway.services.HTMLGenerator;

public class NoLoginPasswordException extends WebApplicationException {
	private static final long serialVersionUID = 6817489620338221395L;

	public NoLoginPasswordException() {
		super(
				Response.status(Status.BAD_REQUEST)
						.entity("<html>"
								+ HTMLGenerator.getInstance().getCssContent()
								+ "<meta http-equiv=\"refresh\" content=\"1; URL=\"javascript:history.back()\" >"
								+ "<body><h1>No login, password found. Please, login before any other operation"
								+ "</h1></body></html>").build());
	}
}


*** 3/ Exemple de code
Trois type de fonctions peuvent être identifiées.

Des fonctions comme SayHello, welcomeFTP et getLoginForm ne font que renvoyer du texte au client.

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


Des fonctions comme logintoFTP, logout, delete opèrent un traitement avec ou sans récupération de paramètres.
Ces derniers peuvent être passés sous la forme de QueryParam, PathParam ou FormParam.


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

Enfin, le dernier type du fonction permet les transferts de fichiers en upload ou download.
Il est a noté qu'un petit polymorphisme a été utilisé pour le getFile et  deleteFile

	@GET
	@Path("/getfile/{file}")
	@Produces({ MediaType.APPLICATION_OCTET_STREAM })
	public Response getFile(@PathParam("file") String fname)

	@GET
	@Path("/getfile")
	@Produces({ MediaType.APPLICATION_OCTET_STREAM })
	public Response getFile(@QueryParam("file") String fname,
			@QueryParam("path") String path)

	@Path("/delete/{file}")
	@DELETE
	public Response deleteFile(@PathParam("file") final String file)

	@Path("/delete")
	@GET
	public Response deleteFile(@QueryParam("path") final String path,
			@QueryParam("file") final String file)


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

