package com.restgateway.services;

import java.io.IOException;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.stereotype.Service;

import com.restgateway.service.HTMLGenerator;

@Service
public class FTPService {
	
	
	
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
	public String getWelcomeMsg(String ftpHostName, int ftpPort, String login,
			String passwd) {
		
		FTPClient ftpClient= new FTPClient();
		
		
		try {
			ftpClient.connect(ftpHostName, ftpPort);
		} catch (IOException ex) {

			return HTMLGenerator.getInstance().getFtpErrorConnectionContent(
					ex.getMessage());
		}
		try {
			if (ftpClient.login(login, passwd)) {
				String[] msg= ftpClient.getReplyStrings();
				ftpClient.disconnect();
				//Todo generer le code html
				return msg.toString();
			}
		} catch (IOException ex) {
			return HTMLGenerator.getInstance().getError(ex.getMessage());
		}
		
		return "Oups, this should not be seen !!!";

	}

	public String getFileList(String ftpHostName, int ftpPort,
			String loginName, String passwd) {
		FTPClient ftpClient= new FTPClient();

		try {
			ftpClient.connect(ftpHostName, ftpPort);
		} catch (IOException ex) {

			return HTMLGenerator.getInstance().getFtpErrorConnectionContent(
					ex.getMessage());
		}
		try {
			if (ftpClient.login(loginName,passwd)) {
				FTPFile[] fileList = ftpClient.listFiles();
				String cwd = ftpClient.printWorkingDirectory();
				ftpClient.disconnect();
				return HTMLGenerator.getInstance().getFileListWith(cwd,
						fileList);
			}
		} catch (IOException ex) {
			return HTMLGenerator.getInstance().getError(ex.getMessage());
		}
		return "";
	}

	// ************************************************************************************************************

	/*
	public Person getByEmail(final String email) {
		final Person person = persons.get(email);

		if (person == null) {
			throw new PersonNotFoundException(email);
		}

		return person;
	}

	public Person addPerson(final String email, final String firstName,
			final String lastName) {
		final Person person = new Person(email);
		person.setFirstName(firstName);
		person.setLastName(lastName);

		if (persons.putIfAbsent(email, person) != null) {
			throw new PersonAlreadyExistsException(email);
		}

		return person;
	}

	public void removePerson(final String email) {
		if (persons.remove(email) == null) {
			throw new PersonNotFoundException(email);
		}
	}*/
	
}
