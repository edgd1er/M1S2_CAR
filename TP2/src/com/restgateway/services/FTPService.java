package com.restgateway.services;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.stereotype.Service;

import com.restgateway.exceptions.FTPClientNotFound;
import com.restgateway.exceptions.FileNotFoundException;
import com.restgateway.exceptions.IncorrectPathException;
import com.restgateway.exceptions.NoLoginPasswordException;
import com.restgateway.services.HTMLGenerator;

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
	public List<String> getWelcomeMsg(String ftpHostName, int ftpPort,
			boolean isPASV) {

		String[] AString = null;
		List<String> myreturn = new ArrayList<String>();
		if (ftpClient == null) {
			this.ftpClient = new FTPClient();
		}

		try {
			if (isPASV) {
				ftpClient.enterLocalPassiveMode();
			}
			ftpClient.connect(ftpHostName, ftpPort);
			AString = ftpClient.getReplyStrings();

			for (String i : AString) {
				myreturn.add(i);
			}
			ftpClient.disconnect();

		} catch (IOException ex) {

			myreturn.add("Error " + ex.getMessage());
		}

		return myreturn;
	}

	/**
	 * Public method to login in FTP server
	 * 
	 * @param ftpHostName
	 *            FTP server name or IP
	 * @param ftpPort
	 *            FTP IP
	 * @param login
	 *            FTP username
	 * @param passwd
	 *            FTP login
	 * @param isPASV
	 *            Passive mode = true
	 * @return Status of the logged user
	 */
	public String loginToFtp(String ftpHostName, int ftpPort, String login,
			String passwd, boolean isPASV) {

		String res = "Error, login and/or password are incorrects. Please login again.";
		try {

			if (intloginToFtp(null, ftpHostName, ftpPort, login, passwd, isPASV)) {
				res = this.getFtpClient().printWorkingDirectory();
			}
			if (isPASV) {
				ftpClient.enterLocalPassiveMode();
			}
			return HTMLGenerator.getInstance().getFTPLoggedContent(res);

		} catch (SocketException sex) {
			return HTMLGenerator.getInstance().getFtpErrorConnectionContent(
					"Error " + sex.getMessage());
		} catch (IOException e) {
			return HTMLGenerator.getInstance().getFtpErrorConnectionContent(
					"Error " + e.getMessage());
		}

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
			String passwd, String path, boolean isPASV) {

		String res = "";

		try {
			if (intloginToFtp(this.ftpClient, ftpHostName, ftpPort, login,
					passwd, isPASV)) {
				String cwd = this.ftpClient.printWorkingDirectory();
				path = path.length() > 0 ? checkPath(path) : cwd;
				this.ftpClient.changeWorkingDirectory(path);
				FTPFile[] files = this.ftpClient.listFiles();

				this.ftpClient.disconnect();
				res = HTMLGenerator.getInstance().getFileListWith(cwd, files);
			} else {
				res = HTMLGenerator
						.getInstance()
						.getFtpErrorConnectionContent(
								"error no correct login/password given. Try again to login.");
			}

			return res;

		} catch (IOException ex) {

			return HTMLGenerator.getInstance().getFtpErrorConnectionContent(
					ex.getMessage());
		}
	}

	public Response getFileList(String ftpHostName, int ftpPort,
			String loginName, String passwd, String path, boolean isPASV) {

		Response response = null;
		path =checkPath(path);
		try {
			if (checkClient()) {
				ftpClient.connect(ftpHostName, ftpPort);
				if (ftpClient.login(loginName, passwd)) {
					if (isPASV) {
						ftpClient.enterLocalPassiveMode();
					}
					ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
					if (path.length() > 0) {
						ftpClient.changeWorkingDirectory(path);
					} else {
						path=ftpClient.printWorkingDirectory();
				}
					FTPFile[] fileList = ftpClient.listFiles(path);
					response = Response.ok(HTMLGenerator.getInstance().getFileListWith(path, fileList)).build();
				}
				disconnectClient();
			}
		} catch (IOException ex) {
			return Response
					.status(Status.NOT_FOUND)
					.entity(HTMLGenerator.getInstance().getError(
							ex.getMessage())).build();
		} catch (FTPClientNotFound | IncorrectPathException
				| NoLoginPasswordException e) {
			return e.getResponse();
		}
		return response;

	}

	/**
	 * Download file to Rest client
	 * 
	 * @param ftpHostName
	 * @param ftpPort
	 * @param loginName
	 * @param passwd
	 * @param path
	 * @param name
	 * @param isPASV
	 * @return
	 */
	public Response getFile(String ftpHostName, int ftpPort, String loginName,
			String passwd, String path, String name, boolean isPASV) {

		String msg = "Error, user not logged in. Please log before";
		Response response = Response.status(Status.PRECONDITION_FAILED)
				.entity(msg).build();
		path = checkPath(path);
		try {
			if (checkClient()) {
				ftpClient.connect(ftpHostName, ftpPort);
				if (ftpClient.login(loginName, passwd)) {
					if (isPASV) {
						ftpClient.enterLocalPassiveMode();
					}
					ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
					ftpClient.changeWorkingDirectory(path);
					InputStream in = ftpClient.retrieveFileStream(name);
					response = Response
							.ok(in, MediaType.APPLICATION_OCTET_STREAM)
							.header("Content-Disposition",
									"attachment; filename=\"" + name + "\"")
							.build();

					if (!ftpClient.completePendingCommand()
							|| ftpClient.getReplyCode() != 226) {
						response = new FileNotFoundException(
								ftpClient.getReplyString() + path + name)
								.getResponse();
					} else {
					}
					disconnectClient();

				} else {
					response = new NoLoginPasswordException().getResponse();
				}
			}
		} catch (IOException ex) {
			response = new FileNotFoundException(path + name).getResponse();
		} catch (FTPClientNotFound ex) {
			response = new FTPClientNotFound().getResponse();
		}
		return response;
	}

	public Response postFile(String ftpHostName, int ftpPort, String login,
			String password, String remotePath, String fname, InputStream in,
			boolean isPASV) {
		Response response = null;
		remotePath=checkPath(remotePath);
		try {
			if (checkClient()) {
				ftpClient.connect(ftpHostName, ftpPort);
				if (ftpClient.login(login, password)) {
					if (isPASV) {
						ftpClient.enterLocalPassiveMode();
					}
					if (remotePath.length() < 1) {
						remotePath = ftpClient.printWorkingDirectory();

					}

					ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
					ftpClient.changeWorkingDirectory(remotePath);
					ftpClient.storeFile(remotePath + fname, in);
					response = Response.ok(in,
							MediaType.APPLICATION_OCTET_STREAM).build();
					if (!ftpClient.completePendingCommand()
							|| ftpClient.getReplyCode() != 226) {
						response = new FileNotFoundException(remotePath + fname)
								.getResponse();
					} else {

					}
					disconnectClient();

				} else {
					response = new NoLoginPasswordException().getResponse();
				}
			}
		} catch (IOException ex) {
			response = new FileNotFoundException(remotePath + fname)
					.getResponse();
		} catch (FTPClientNotFound ex) {
			response = new FTPClientNotFound().getResponse();
		}
		return response;

	}

	/**
	 * delete a File from the server.
	 * 
	 * @param path
	 * @param file
	 */
	public Response deleteFile(String ftpHostName, int ftpPort, String login,
			String password, String path, String file) {
		String msg = "Error, user not logged in. Please log before";
		try {
			if (checkClient()) {

				path = checkPath(path);
				msg = "error, file " + path + file + " not deleted.";

				ftpClient.connect(ftpHostName, ftpPort);
				ftpClient.changeWorkingDirectory(path);
				if (ftpClient.login(login, password)) {
					if (ftpClient.deleteFile(file)) {
						msg = "Ok, file " + path + file + " deleted.";
					}
				}
			}
		} catch (IOException e) {
			msg += e.getMessage();

		}

		if (msg.toLowerCase().contains("error")) {
			return Response.status(Status.ACCEPTED).entity(msg).build();
		}
		return Response.status(Status.OK).entity(msg).build();

	}

	public boolean changeWorkingDirectory(String ftpHostName, int ftpPort,
			String login, String password, String path, String file) {
		String msg = "Error, user not logged in. Please log before";
		try {
			if (checkClient()) {
				path = checkPath(path);
				msg = "error, file " + path + " not changed.";

				ftpClient.connect(ftpHostName, ftpPort);
				if (ftpClient.changeWorkingDirectory(path)) {
					return true;
				} else {
					throw new IncorrectPathException(ftpClient.getReplyString());
				}
			}
		} catch (IOException | FTPClientNotFound e) {
			msg += e.getMessage();
			throw new FTPClientNotFound();
		} catch (IncorrectPathException e) {
			msg = e.getMessage();
			throw new IncorrectPathException(msg);
		} catch (NoLoginPasswordException e) {
			msg += e.getMessage();
			throw new NoLoginPasswordException();
		}
		return false;

	}

	private String checkPath(String path) {
		String properPath = "";
		if (path ==null){return properPath;}
		if (path.length() > 0) {
			path = path.endsWith(File.separator) ? path : path + File.separator;
			path = path.startsWith(File.separator) ? path : File.separator
					+ path;
			properPath = path;
		}
		return properPath;
	}

	/**
	 * Check if the client is initialized with proper credentials.
	 * 
	 * @return True Throw exception
	 */
	private boolean checkClient() throws FTPClientNotFound {
		if (this.getFtpClient() == null) {
			throw new FTPClientNotFound();
		}
		return true;
	}

	/**
	 * internal method for FTP Connection using then internal FTPClient
	 * 
	 * @param client
	 *            instance of Apache client
	 * @param ftpHostName
	 *            FTP Name or IP
	 * @param ftpPort
	 *            FTP Port
	 * @param login
	 *            User login name
	 * @param passwd
	 *            User Password
	 * @return current directory path
	 * @throws IOException
	 * @throws SocketException
	 */
	private boolean intloginToFtp(FTPClient _client, String ftpHostName,
			int ftpPort, String login, String passwd, boolean isPASV)
			throws SocketException, IOException {

		boolean res = false;
		if (ftpClient == null) {
			this.ftpClient = new FTPClient();
		}
		if (_client != null) {
			this.ftpClient = _client;
		}
		ftpClient.connect(ftpHostName, ftpPort);
		res = ftpClient.login(login, passwd);

		if (isPASV) {
			ftpClient.enterLocalPassiveMode();
		}
		return res;
	}

	/**
	 * Disconnect ftpClient on request
	 * 
	 */
	public void disconnectClient() {
		try {
			if (this.getFtpClient()!=null){
				this.getFtpClient().disconnect();
				}
		} catch (IOException e) {
			// nothing to be done
		}

	}

}
