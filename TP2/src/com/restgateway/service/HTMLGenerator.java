package com.restgateway.service;

import org.apache.commons.net.ftp.FTPFile;

public class HTMLGenerator {

	public String path = "http://localhost:8080/RestGateway/";

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
	public String getFileListWith(String cwd, FTPFile[] dirlist, FTPFile[] fileList) {
		// return "<html>" + this.getCssContent() + "<body><h1>" + cwd +
		// "</h1><div id=\"corps\">" + this.processFileList(cwd, fileList) +
		// "</div>" + this.getDisconnectionButton() + this.getUploadButton(cwd)
		// + "</body></html>";
		return this.processFileStringList(cwd, dirlist,fileList);

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
	private String processFileStringList(String cwd, FTPFile[] dirList, FTPFile[] fileList) {
		String tmp = "\n";
		tmp += cwd+"\n DirList\n";
		for (FTPFile fTPFile : dirList) {
			tmp += fTPFile.getName()+"\n";
		}
		tmp+="\n File List";
				
		for (FTPFile fTPFile : fileList) {
			tmp += fTPFile.getName()+"\n";
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
					+ "resources/file"
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

	
	//*******************************************************************************************************
	//*******************************************************************************************************
	//					FTP ERROR HANDLING
	//*******************************************************************************************************
	//*******************************************************************************************************
	
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
	 * @param message to process
	 * @return		html Encoded page with message.
	 */
	public String getError(String message) {
		return message;
	}

}
