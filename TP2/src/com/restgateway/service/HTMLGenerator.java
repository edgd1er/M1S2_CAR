package com.restgateway.service;

import java.io.File;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.net.ftp.FTPFile;

public class HTMLGenerator {

	public String path = "http://localhost:8080/rest/api/ftp/";

	private static class SingletonHolder {
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

		String html = "<html><body><h1>" + getPathCD(cwd) + "</h1><div id=\"corps\">";
		for (FTPFile myfile : fileList) {
			html += this.getFnameAndLink(cwd, myfile);
		}

		return html += "</body></html>";
	}

	private String getPathCD(String cwd) {
		String[] aString = cwd.split(File.separator);
		String html="", iterPath="";

		html+="<a href=\""+path + "moveto?path=/> / </a> ";
		for (String str : aString){
			iterPath+="/"+str;
			html+="<a href=\""+path + "moveto?path="+iterPath.substring(1)+"\">/"+str+"</a> ";
			
		}
		return html;
	}

	private String getFnameAndLink(String cwd, FTPFile file) {
		String tmp = "";
		if (file.isDirectory())
			tmp += "<img src=\"http://agingparentsauthority.com/wp-content/plugins/sem-theme-pro/icons/folder.png\" alt=\"[ ]\" /> <a href='"
					+ path
					+ "/moveTo?cd="
					+ cwd
					+ "/"
					+ file.getName()
					+ "'>" + file.getName() + "</a></br>\n";
		if (file.isFile()) {
			tmp += "<img src=\"http://png.findicons.com/files/icons/2015/24x24_free_application/24/new_document.png\" alt=\"[ ]\" /> "
					+ "<a href=\""
					+ path
					+ "delete?path="
					+ cwd
					+ "/&file="
					+ file.getName()
					+ "\"><img src=\"http://findicons.com/files/icons/2139/uidesign/16/delete.png\" /></a> "
					+ "<a href=\""
					+ path
					+ "getfile?path="					
					+ cwd
					+ "&file=" 
					+ file.getName()
					+ "\">"
					+ file.getName()
					+ "</a></br>";
		}
		return tmp;
	}

	public String getLogoutButton() {
		return "<a class=\"btn btn-primary btn-large\" href=\"" + path
				+ "/logout\">Logout </a>";
	}

	public String getUpLoadButton(String cwd) {
		return "<a href=\"" + path + "resources/store" + cwd + "\">Upload</a>";
	}

	/******************************************************
	 * Inner tools
	 */

	/***
	 * return an simple string containing the list of fileList.
	 * 
	 * 
	 * @param fileList
	 *            list of Files
	 * @return String containing HTML part containing href link for download
	 *         files and browse directory
	 */
	private String processFileStringList(String cwd, FTPFile[] dirList,
			FTPFile[] fileList) {
		String tmp = "\n";
		tmp += cwd + "\n DirList\n";
		for (FTPFile fTPFile : dirList) {
			tmp += fTPFile.getName() + "\n";
		}
		tmp += "\n File List";

		for (FTPFile fTPFile : fileList) {
			tmp += fTPFile.getName() + "\n";
		}
		return tmp + "\n";
	}

	/***
	 * return an HTML generated code for list.
	 * 
	 * 
	 * @param fileList
	 *            list of Files
	 * @return String containing HTML part containing href link for download
	 *         files and browse directory
	 */
	private String processFileList(String cwd, FTPFile[] fileList) {
		String tmp = "\n";
		tmp += this.getLinkForParentDirectory(cwd);
		for (FTPFile fTPFile : fileList) {
			tmp += this.getLinkForFileName(cwd, fTPFile);
		}
		return tmp + "\n";
	}

	/**
	 * This method return HTML source code than provide user to browse files, or
	 * retrive one
	 * 
	 * @param file
	 *            list of Files
	 * @return String containing HTML part containing href link
	 */
	private String getLinkForFileName(String cwd, FTPFile file) {
		String tmp = "";
		if (file.isDirectory())
			tmp += "<img src=\"http://agingparentsauthority.com/wp-content/plugins/sem-theme-pro/icons/folder.png\" alt=\"[ ]\" /> <a href='"
					+ path
					+ "/file"
					+ cwd
					+ "/"
					+ file.getName()
					+ "'>" + file.getName() + "</a></br>\n";
		if (file.isFile()) {
			tmp += "<img src=\"http://www.appropedia.org/skins/vector/images/file-icon.png\" alt=\"[ ]\" /> "
					+ "<a href=\""
					+ path
					+ "/resources/file/"
					+ cwd
					+ "/delete/"
					+ file.getName()
					+ "\"><img src=\" \" /></a> "
					+ "<a href=\""
					+ path
					+ "/resources/file/"
					+ cwd
					+ "/download/"
					+ file.getName()
					+ "\">"
					+ file.getName()
					+ "</a></br>";
		}
		return tmp;
	}

	/**
	 * This method return HTML source code than provide cdup ability to the user
	 * 
	 * @return String containing HTML link for cdup dir
	 */
	private String getLinkForParentDirectory(String cwd) {
		String[] split = cwd.split("/");
		String parent = "";
		for (int i = 0; i < split.length - 1; i++) {
			parent += split[i] + "/";
		}
		return "<a href='" + path + "/resources/file/" + parent
				+ "'>Parent directory</a></br>\n";
	}

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
		String html = "<html><body><h1>JAX-RS Upload Form</h1>";
		html += "<form action=\"uploadfile/\" method=\"post\" enctype=\"multipart/form-data\">";
		html += "<p>";
		html += "Select a file : <input type=\"file\" name=\"uploadedFile\" size=\"50\" />";
		html += "</p>";
		html += "<input type=\"submit\" value=\"Upload It\" />";
		html += "</form>";
		html += "</body></html>";

		return Response.ok(html, MediaType.TEXT_HTML).build();
	}

	public Response getLoginFormContent() {
		String html = "<%@page contentType=\"text/html\" pageEncoding=\"UTF-8\"%>";
		html += "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\"";
		html += "\"http://www.w3.org/TR/html4/loose.dtd\">";
		html += "<html>";
		html += "<head>";
		html += "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">";
		html += "<title>FTP Login</title>";
		html += "</head>";
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

}
