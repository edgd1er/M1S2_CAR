package com.restgateway.services;

import java.io.File;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


import org.apache.commons.net.ftp.FTPFile;

/**
 * This class will generate response to enhance browser experience.
 * 
 * @author Salomon Emmeline & Dubiez François.
 *
 */
public class HTMLGenerator {


	/**
	 * @author   dubiez
	 */
	private static class SingletonHolder {
		/**
		 * @uml.property  name="instance"
		 * @uml.associationEnd  
		 */
		private final static HTMLGenerator instance = new HTMLGenerator();
	}

	public static HTMLGenerator getInstance() {
		return SingletonHolder.instance;
	}

	/**
	 * Return a default HTML content page parameters has to been provide by the
	 * rest gateway
	 * 
	 * @param cwd
	 *            current working directory
	 * @param fileList
	 *            list of Files
	 * @return String containing HTML content, listing all files and directory
	 */
	public String getFileListWith(String cwd, FTPFile[] fileList) {

		String html = "<html>"+getCssContent()+"<body><h1>" + getPathCD(cwd)
				+ "</h1><div id=\"corps\">";
		for (FTPFile myfile : fileList) {
			html += this.getFnameAndLink(cwd, myfile);
		}

		return html += getUpLoadButton(cwd)+getLogoutButton()+"</body></html>";
	}

	private String getPathCD(String cwd) {
		String[] aString = cwd.split(File.separator);
		String html = "", iterPath = "";

		html += "<a href=\"" + "list?path=%2F\">/</a>";
		for (String str : aString) {
			if (!str.equals("")) {
				iterPath += "%2F" + str;
				html += "<a href=\"" +  "list?path="
						+ iterPath + "\">" + str + "</a>/";
			}

		}
		return html;
	}

	private String getFnameAndLink(String cwd, FTPFile file) {
		String tmp = "";
		if (file.isDirectory())
			tmp += "<img src=\"http://agingparentsauthority.com/wp-content/plugins/sem-theme-pro/icons/folder.png\" alt=\"[ ]\" /> <a href='"
					+ "list?path="
					+ cwd.replace("/", "%2F")
					+ "%2F"
					+ file.getName()
					+ "'>"
					+ file.getName() + "</a></br>\n";
		if (file.isFile()) {
			tmp += "<img src=\"http://png.findicons.com/files/icons/2015/24x24_free_application/24/new_document.png\" alt=\"[ ]\" /> "
					+ "<a href=\""
					+ "delete?path="
					+ cwd.replace("/", "%2F")
					+ "&file="
					+ file.getName()
					+ "\"><img src=\"http://findicons.com/files/icons/2139/uidesign/16/delete.png\" /></a> "
					+ "<a href=\""
					+ "getfile?path="
					+ cwd.replace("/", "%2F")
					+ "&file="
					+ file.getName() + "\">" + file.getName() + "</a></br>";
		}
		return tmp;
	}

	public String getLogoutButton() {
		return "<a class=\"btn btn-primary btn-large\" href=\"" 
				+ "logout\">Logout </a>";
	}

	public String getUpLoadButton(String cwd) {
		return "<a href=\"" +  "getUpLoadForm\">Upload</a>";
	}

	/******************************************************
	 * Inner tools
	 */


	

	
	// *******************************************************************************************************
	// *******************************************************************************************************
	// FTP ERROR HANDLING
	// *******************************************************************************************************
	// *******************************************************************************************************

	/*******************************************************************************************************
	 * getFtpErrorConnectionContent
	 * 
	 * 
	 * @param message
	 * @return
	 */
	public String getFtpErrorConnectionContent(String message) {
		return message;
	}

	/**
	 * getErrror return an html encoded message to transmit
	 * 
	 * @param message
	 *            to process
	 * @return html Encoded page with message.
	 */
	public String getError(String message) {
		return message;
	}

	public String getFTPLoggedContent(String res) {
		return res;
	}

	public Response getUploadContent() {
		String html = "<html>"+getCssContent()+"<body><h1>JAX-RS Upload Form</h1>";
		html += "<form method=\"post\" enctype=\"multipart/form-data\" action=\"/rest/api/ftp/uploadfile\" >\n";
		html += "<p>\n";
		html += "Select a file : <input type=\"file\" name=\"formfilefield\" id=\"formfilefield\"  />\n";
		html += "</p>\n";
		html += "<input type=\"submit\" name=\"press\" value=\"UploadIt\" />\n";
		html += "</form>\n";
		html += "</body></html>";

		return Response.ok(html, MediaType.TEXT_HTML).build();
	}

	public Response getLoginFormContent() {
		String html = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\"";
		html += "\"http://www.w3.org/TR/html4/loose.dtd\">";
		html += "<html>";
		html += "<head>";
		html += "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">";
		html += "<title>FTP Login</title>";
		html += "</head>";
		html += getCssContent();
		html += "<body>";
		html += "<section class=\"loginform cf\">";
		html += "<form name=\"login\" action=\"loginPost\" method=\"post\" accept-charset=\"utf-8\">";
		html += "<ul>";
		html += "<li>";
		html += "<label for=\"usermail\">Login</label>";
		html += "<input type=\"text\" name=\"lname\" placeholder=\"\" required>";
		html += "</li>";
		html += "<li>";
		html += "<label for=\"password\">Password</label>";
		html += "<input type=\"password\" name=\"lpass\" placeholder=\"\" ></li>";
		html += "<li>";
		html += "<input type=\"submit\" value=\"Login\">";
		html += "</li>";
		html += "</ul>";
		html += "</form>";
		html += "</section>";
		html += "</body>";

		return Response.ok(html, MediaType.TEXT_HTML).build();
	}

	public Response getFileUploadedContent() {
		String html = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\"";
		html += "\"http://www.w3.org/TR/html4/loose.dtd\">";
		html += "<html>";
		html += "<head>";
		html += "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">";
		html += "<title>File Upload to FTP</title>";
		html += "</head>";
		html += getCssContent();
		html += "<body>";
		html += "<h1>";
		html += "File Uploaded to FTP server";
		html += "<h1>";
		html += "</body>";

		return Response.ok(html, MediaType.TEXT_HTML).build();
	}
	
	
	 /**
	* Return default CSS content for HTML pages
	* @return String containing CSS content
	*/
	public String getCssContent(){
	return "<style type=\"text/css\">\n" +
	" \n" +
	"html {\n" +
	" background: url('http://zinzinzibidi.com/Areas/web_tasarim/Content/img/background-sample.png');\n" +
	" font-size: 10pt;\n" +
	"}\n" +
	"label {\n" +
	" display: block;\n" +
	" color: #fff;\n" +
	"}\n" +
	":focus {\n" +
		" outline: 0;\n" +
		"}\n" +
		".loginform {\n" +
		" width: 410px;\n" +
		" margin: 50px auto;\n" +
		" padding: 25px;\n" +
		" background-color: rgba(250,250,250,0.5);\n" +
		" border-radius: 5px;\n" +
		" box-shadow: 0px 0px 5px 0px rgba(0, 0, 0, 0.2), \n" +
		" inset 0px 1px 0px 0px rgba(250, 250, 250, 0.5);\n" +
		" border: 1px solid rgba(0, 0, 0, 0.3);\n" +
		"}\n" +
		".loginform ul {\n" +
		" padding: 0;\n" +
		" margin: 0;\n" +
		"}\n" +
		".loginform li {\n" +
		" display: inline;\n" +
		" float: left;\n" +
		"}\n" +
		".loginform input:not([type=submit]) {\n" +
		" padding: 5px;\n" +
		" margin-right: 10px;\n" +
		" border: 1px solid rgba(0, 0, 0, 0.3);\n" +
		" border-radius: 3px;\n" +
		" box-shadow: inset 0px 1px 3px 0px rgba(0, 0, 0, 0.1), \n" +
		" 0px 1px 0px 0px rgba(250, 250, 250, 0.5) ;\n" +
		"}\n" +
		".loginform input[type=submit] {\n" +
		" border: 1px solid rgba(0, 0, 0, 0.3);\n" +
		" background: #64c8ef; /* Old browsers */\n" +
		" background: -moz-linear-gradient(top, #64c8ef 0%, #00a2e2 100%); /* FF3.6+ */\n" +
		" background: -webkit-gradient(linear, left top, left bottom, color-stop(0%,#64c8ef), color-stop(100%,#00a2e2)); /* Chrome,Safari4+ */\n" +
		" background: -webkit-linear-gradient(top, #64c8ef 0%,#00a2e2 100%); /* Chrome10+,Safari5.1+ */\n" +
		" background: -o-linear-gradient(top, #64c8ef 0%,#00a2e2 100%); /* Opera 11.10+ */\n" +
		" background: -ms-linear-gradient(top, #64c8ef 0%,#00a2e2 100%); /* IE10+ */\n" +
		" background: linear-gradient(to bottom, #64c8ef 0%,#00a2e2 100%); /* W3C */\n" +
		" filter: progid:DXImageTransform.Microsoft.gradient( startColorstr='#64c8ef', endColorstr='#00a2e2',GradientType=0 ); /* IE6-9 */\n" +
		" color: #fff;\n" +
		" padding: 5px 15px;\n" +
		" margin-right: 0;\n" +
		" margin-top: 15px;\n" +
		" border-radius: 3px;\n" +
		" text-shadow: 1px 1px 0px rgba(0, 0, 0, 0.3);\n" +
		"}\n" +
		"\n" +
		"\n" +
		"article,\n" +
		"aside,\n" +
		"details,\n" +
		"figcaption,\n" +
		"figure,\n" +
		"footer,\n" +
		"header,\n" +
		"hgroup,\n" +
		"nav,\n" +
		"section,\n" +
		"summary {\n" +
		" display: block;\n" +
		"}\n" 
		 +
		 " </style>";

	}
	
}
