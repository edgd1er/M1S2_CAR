package com.restgateway.services;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.stereotype.Service;

import com.restgateway.exceptions.FileNotFoundException;
import com.restgateway.service.HTMLGenerator;

@Service
public class FTPService {
	
	FTPClient ftpClient = null;
	
	public FTPClient getFtpClient() {
		return ftpClient;
	}

	public void setFtpClient(FTPClient ftpClient) {
		this.ftpClient = ftpClient;
	}

	/**
	 * Get the welcome message from server
	 * 
	 * @param ftpHostName
	 *            IP or name of the server.
	 * @param ftpPort
	 *            Port of the server
	 * @return Array of string containing the welcome message.
	 */
	public List<String> getWelcomeMsg(String ftpHostName, int ftpPort, boolean isPASV) {

		String[] AString = null;
		List<String> myreturn = new ArrayList<String>();
		if (ftpClient==null){this.ftpClient=new FTPClient(); }

		try {
			if (isPASV) {ftpClient.enterLocalPassiveMode();}
			ftpClient.connect(ftpHostName, ftpPort);
			AString = ftpClient.getReplyStrings();

			for (String i : AString) {
				myreturn.add(i);
			}
			ftpClient.disconnect();

		} catch (IOException ex) {

			myreturn.add(HTMLGenerator.getInstance()
					.getFtpErrorConnectionContent(ex.getMessage()));
		}

		return myreturn;
	}

	// ************************************************************************************************************

	/*
	 * public Person getByEmail(final String email) { final Person person =
	 * persons.get(email);
	 * 
	 * if (person == null) { throw new PersonNotFoundException(email); }
	 * 
	 * return person; }
	 * 
	 * public Person addPerson(final String email, final String firstName, final
	 * String lastName) { final Person person = new Person(email);
	 * person.setFirstName(firstName); person.setLastName(lastName);
	 * 
	 * if (persons.putIfAbsent(email, person) != null) { throw new
	 * PersonAlreadyExistsException(email); }
	 * 
	 * return person; }
	 * 
	 * public void removePerson(final String email) { if (persons.remove(email)
	 * == null) { throw new PersonNotFoundException(email); } }
	 */
	public  String loginToFtp(FTPClient _client, String ftpHostName, int ftpPort,
			String login, String passwd, boolean isPASV) {

		try {
			return intloginToFtp(_client, ftpHostName, ftpPort, login, passwd, isPASV).printWorkingDirectory();	
		} catch (SocketException sex) {
			return HTMLGenerator.getInstance().getFtpErrorConnectionContent(
					sex.getMessage());
		} catch (IOException e) {
			return HTMLGenerator.getInstance().getFtpErrorConnectionContent(
					e.getMessage());
		}
		

		
	}
	/**
	 * internal method for FTP Connection
	 * 
	 * @param client		instance of Apache client
	 * @param ftpHostName	FTP Name or IP
	 * @param ftpPort		FTP Port  
	 * @param login			User login name
	 * @param passwd		User Password
	 * @return				current directory path
	 * @throws IOException 
	 * @throws SocketException 
	 */
	private FTPClient intloginToFtp(FTPClient _client, String ftpHostName, int ftpPort,
			String login, String passwd, boolean isPASV) throws SocketException, IOException {

		if (ftpClient==null){this.ftpClient=new FTPClient(); }
		if (_client != null){ this.ftpClient=_client; }
		ftpClient.connect(ftpHostName, ftpPort);
		ftpClient.login(login, passwd);

		if (isPASV) {
			ftpClient.enterLocalPassiveMode();
		}
		return ftpClient;
	}

	/**
	 * Return the welcome Message from FTP Server
	 * 
	 * @param login
	 *            userLogin
	 * @param ftpPort
	 * @param passwd
	 *            user password
	 * @param string
	 * @return welcome message from Server
	 */
	public String listFiles(String ftpHostName, int ftpPort, String login,
			String passwd, boolean isPASV) {

		FTPClient ftpClient = new FTPClient();

		try {
			loginToFtp(ftpClient, ftpHostName, ftpPort, login, passwd, isPASV);
			FTPFile[] files = ftpClient.listFiles();
			FTPFile[] dirs = ftpClient.listDirectories();
			String cwd = ftpClient.printWorkingDirectory();
			ftpClient.disconnect();
			return HTMLGenerator.getInstance()
					.getFileListWith(cwd, dirs, files);

		} catch (IOException ex) {

			return HTMLGenerator.getInstance().getFtpErrorConnectionContent(
					ex.getMessage());
		}
	}

	public String getFileList(String ftpHostName, int ftpPort,
			String loginName, String passwd) {
		FTPClient ftpClient = new FTPClient();

		try {
			ftpClient.connect(ftpHostName, ftpPort);
		} catch (IOException ex) {

			return HTMLGenerator.getInstance().getFtpErrorConnectionContent(
					ex.getMessage());
		}
		try {
			if (ftpClient.login(loginName, passwd)) {
				String cwd = ftpClient.printWorkingDirectory();
				FTPFile[] fileList = ftpClient.listFiles(cwd);
				FTPFile[] dirList = ftpClient.listDirectories(cwd);

				ftpClient.disconnect();
				return HTMLGenerator.getInstance().getFileListWith(cwd,
						dirList, fileList);
			}
		} catch (IOException ex) {
			return HTMLGenerator.getInstance().getError(ex.getMessage());
		}
		return "";
	}

	/**
	 * delete a File from the server.
	 * 
	 * @param path
	 * @param file
	 */
	public String deleteFile(String path, String file) {
		String msg="";
		
		if (this.getFtpClient()==null){
			return "Error, you are not logged in. Please login first !";
			}
		
		path =path.endsWith(File.pathSeparator)?path:path+ File.pathSeparator;
		msg="error, file "+ path + file + " not deleted.";
		
		try {
			if (ftpClient.deleteFile(path+file)){
				msg="Ok, file "+ path + file + " deleted.";
			}
		} catch (IOException e) {
			msg+=e.getMessage();
		}
		
		return msg;
	}

	public Response getFile(String ftpHostName, int ftpPort,
			String loginName, String passwd,String path, String name) {
		
				
		try {
			ftpClient.connect(ftpHostName, ftpPort);
		} catch (IOException ex) {
	
			return null;
		}
		try {
			if (ftpClient.login(loginName, passwd)) {
				String cwd = ftpClient.printWorkingDirectory();
				 InputStream in = ftpClient.retrieveFileStream(path+name);
				 Response reponse = Response.ok(in, MediaType.APPLICATION_OCTET_STREAM).build();
				ftpClient.disconnect();

			}
		} catch (IOException ex) {
			return new FileNotFoundException();
		}
		return null;
	}

	// ************************************************************************************************************

	/*
	 * public Person getByEmail(final String email) { final Person person =
	 * persons.get(email);
	 * 
	 * if (person == null) { throw new PersonNotFoundException(email); }
	 * 
	 * return person; }
	 * 
	 * public Person addPerson(final String email, final String firstName, final
	 * String lastName) { final Person person = new Person(email);
	 * person.setFirstName(firstName); person.setLastName(lastName);
	 * 
	 * if (persons.putIfAbsent(email, person) != null) { throw new
	 * PersonAlreadyExistsException(email); }
	 * 
	 * return person; }
	 * 
	 * public void removePerson(final String email) { if (persons.remove(email)
	 * == null) { throw new PersonNotFoundException(email); } }
	 */

}
