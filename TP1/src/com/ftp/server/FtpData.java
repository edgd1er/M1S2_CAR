package com.ftp.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

import com.ftp.tools.ErrorCode;

/**
 * ftpData: this thread will handle transfer on the data channel.
 * Potential long time running command: ls, stor, retrieve
 * 
 * @author emeline Salomon & françois Dubiez
 *
 */
public class FtpData extends Thread {

	private Socket datasocket;
	private String DataAddr;
	private Integer DataPort;
	private List<String> aString;
	private boolean isPASV;
	private String commande;
	private String parametre;
	private ServerSocket dataSrvSocket;
	private String port_url = null;
	private boolean KeepRunning = true;
	private String returnstatus;
	private boolean isASCII = false;
	private int bufferSize = 8192;
	
	/**
	 * 
	 * constructeur du mode client -> server
	 */
	public FtpData(String clientIp, boolean _isPASV) throws IOException {

		String messageLog = null;
		this.DataAddr = clientIp;

		messageLog = this.getClass().toString() + " Cons: Debut: " + _isPASV;
		// mode PASV, on va recuperer le port de connection du client.
		setPASV(_isPASV);

		if (Server.debugMode){System.out.println(messageLog);}

	}

	// mode Direct
	public FtpData(String cltDataAddr, Integer cltDataPort,
			List<String> aInfo2Send, String commande, String parametre) {

		this.dataSrvSocket = null;
		this.isPASV = false;
		// communs
		this.DataAddr = cltDataAddr;
		this.DataPort = cltDataPort;
		this.aString = aInfo2Send;
		this.commande = commande;
		this.parametre = parametre;
		this.isASCII = false; // mode par defaut
	}

	// mode PASV
	public FtpData(ServerSocket dataServerSocket, String cltDataAddr,
			Integer cltDataPort, List<String> aInfo2Send, String commande,
			String parametre) {

		// Mode server
		this.dataSrvSocket = dataServerSocket;
		// communs
		this.DataAddr = cltDataAddr;
		this.DataPort = cltDataPort;
		this.aString = aInfo2Send;
		this.commande = commande;
		this.parametre = parametre;
		this.isASCII = false; // mode par defaut
	}

	// separation creation socket serveur ou client selon le mode.
	public void run() {

		try {
			/*
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}   
			 */
		// si PASV alors on attend la connexion client.
			if (isPASV) {
				this.datasocket = dataSrvSocket.accept();
			} else {
				//sinon on est en mode actif
				this.datasocket = new Socket(DataAddr, DataPort);
			}
		}

		catch (UnknownHostException uhe) {
			String messalog = this.getClass().toString() + " Run"
					+ " commande: " + commande + " " + parametre;
			System.err.println(messalog);
			KeepRunning = false;
			uhe.printStackTrace();
		}

		catch (IOException ioe) {
			String messalog = this.getClass().toString() + " Run"
					+ " commande: " + commande + " " + parametre;
			System.err.println(messalog);
			KeepRunning = false;
			ioe.printStackTrace();

		}

		if (KeepRunning) {
			switch (commande) {

			case ("LIST"):
				sendList();
				break;
			case ("RETR"):
				// retrieveFile();
				// TODO
				retrieveAsciiBin();
				break;
			case ("STOR"):
				storeAsciiBin();
				break;
			default:
				break;
			}
			commande = "";
			parametre = "";
		}
	}

	public boolean getPort() throws IOException {
		String messageLog = "";
		port_url = getEncPort();
		messageLog = this.getClass().toString()
				+ " creation de la ServerSocket "
				+ dataSrvSocket.getInetAddress().getHostAddress() + ":"
				+ dataSrvSocket.getLocalPort() + " PORT " + port_url;

		if (Server.debugMode){System.out.println(messageLog);}

		// attente du client sur le canal data
		// recuperation de la socket.
		datasocket = dataSrvSocket.accept();

		messageLog += " ftpData :  constructeur:  le client s'est connecte: "
				+ datasocket.getLocalPort() + " / " + datasocket.getPort();

		if (Server.debugMode){System.out.println(messageLog);}
		return true;
	}

	/**
	 * @return the isPASV
	 * @throws IOException
	 */
	public boolean getPASV() throws IOException {

		if (!isPASV) {
			if (dataSrvSocket != null) {
				if (dataSrvSocket.isBound()) {
					dataSrvSocket.close();
				}
				dataSrvSocket = null;
			}
		}
		return isPASV;
	}

	/**
	 * @param isPASV
	 *            the isPASV to set
	 * @throws IOException
	 */
	public void setPASV(boolean isPASV) throws IOException {
		this.isPASV = isPASV;
		String messageLog = this.getClass().toString() + " Mode PASV=" + isPASV;

		if (isPASV) {
			if ((this.dataSrvSocket == null) || (this.dataSrvSocket.isClosed())) {
				this.dataSrvSocket = new ServerSocket(0);
				DataPort = this.dataSrvSocket.getLocalPort();

				messageLog += " création";
			} else {
				messageLog += " réutilisation";
			}
			port_url = getEncPort();
			messageLog += " de la dataServerSocket:" + DataAddr + ":"
					+ DataPort;

		} else {
			// gestion du mode actif
			this.datasocket = new Socket(DataAddr, DataPort);
			dataSrvSocket = null;
		}

		if (Server.debugMode){System.out.println(messageLog);}
	}

	// send from client to server
	private void storeAsciiBin() {
		String rep, paramCode, messageLog, line;
		InputStream in = null;
		File file = null;
		int nread = 0, ntotalRead = 0;
		messageLog = this.getClass().toString() + " StoreFile (Ascii:"
				+ isASCII + "), commande: " + commande + " " + parametre;

		try {

			file = new File(parametre);
			in = datasocket.getInputStream();

			// mode ascii
			if (isASCII) {
				FileWriter fwr = new FileWriter(file);
				PrintWriter pwr = new PrintWriter(fwr);
				InputStreamReader isr = new InputStreamReader(in);
				BufferedReader br = new BufferedReader(isr);
				while ((line = br.readLine()) != null) {
					pwr.write(line + "\r\n");
					ntotalRead += line.length() + 2;
				}
				isr.close();
				pwr.close();
			} else
			// mode binaire
			{
				byte[] b = new byte[8192];
				FileOutputStream fos = new FileOutputStream(file);
				BufferedInputStream bis = new BufferedInputStream(in);

				while ((nread = bis.read(b)) > 0) {
					fos.write(b, 0, nread);
					ntotalRead += nread;
				}
				bis.close();
				fos.close();
				fos = null;
			}

			in.close();
			datasocket.close();
			if (dataSrvSocket != null) {
				dataSrvSocket.close();
			}
			KeepRunning = false;
			rep = "226";
			paramCode = "File sent: " + parametre + " " + ntotalRead
					+ " bytes sent.";
			returnstatus = ErrorCode.getMessage(rep, paramCode);

		} catch (FileNotFoundException fnf) {
			rep = "550";
			paramCode = "Fichier non accessible: " + parametre;
			returnstatus = ErrorCode.getMessage(rep, paramCode);

		} catch (IOException ioe) {
			rep = "425";
			paramCode = "Can't Open data connection";
			returnstatus = ErrorCode.getMessage(rep, paramCode);
			messageLog += " " + returnstatus;
		}

		if (Server.debugMode){System.out.println(messageLog);}

	}

	// envoi du fichier du serveur vers le client.
	private void retrieveAsciiBin() {
		// Gestion ASCII / BIN
		String rep, paramCode, messageLog;
		int nread = 0, ntotalread = 0;
		OutputStream out = null;

		// TODO Gestion des mode ASCII / BIN
		messageLog = this.getClass().toString() + " StoreFile (Ascii:"
				+ isASCII + "), commande: " + commande + " " + parametre;

		try {

			out = datasocket.getOutputStream();

			// mode ascii
			if (isASCII) {
				// preparation des I/O
				FileReader frd = new FileReader(parametre);
				BufferedReader brd = new BufferedReader(frd);
				OutputStreamWriter sow = new OutputStreamWriter(out);
				BufferedWriter bwr = new BufferedWriter(sow);

				String readString = "";
				while ((readString = brd.readLine()) != null) {
					sow.write(readString + "\n");
					ntotalread += readString.length() + 1;
				}
				brd.close();
				sow.close();
				frd.close();
				bwr.close();

			} else
			// mode binaire
			{
				FileInputStream fis = new FileInputStream(parametre);
				BufferedInputStream bis = new BufferedInputStream(fis);
				DataOutputStream dos = new DataOutputStream(out);
				BufferedOutputStream bdos = new BufferedOutputStream(dos);

				byte buf[] = new byte[bufferSize];
				while ((nread = bis.read(buf)) > 0) {
					ntotalread += nread;
					bdos.write(buf, 0, nread);
				}
				bdos.close();
				dos.flush();
				dos.close();
				bis.close();
			}

			datasocket.close();
			if (dataSrvSocket != null) {
				dataSrvSocket.close();
			}
			KeepRunning = false;
			rep = "226";
			paramCode = "File sent: " + parametre + " written "
					+ String.valueOf(ntotalread)
					+ (isASCII ? " Char" : " bytes");
			returnstatus = ErrorCode.getMessage(rep, paramCode);

		} catch (FileNotFoundException fnf) {
			rep = "550";
			paramCode = "Fichier non accessible: " + parametre;
			returnstatus = ErrorCode.getMessage(rep, paramCode);

			returnstatus = ErrorCode.getMessage(rep, paramCode);
		} catch (IOException ioe) {
			rep = "425";
			paramCode = "Can't Open data connection";
			returnstatus = ErrorCode.getMessage(rep, paramCode);
			messageLog += " " + returnstatus;
		}

		if (Server.debugMode){System.out.println(messageLog);}

	}

	public void sendList() {
		// TODO Auto-generated method stub
		String messageLog = this.getClass().toString() + " sendList"
				+ " commande: " + commande + " " + parametre;
		if (Server.debugMode){System.out.println(messageLog);}

		try {
			// ouverture du canal DATA en sortie.
			DataOutputStream DataOutStream = new DataOutputStream(
					datasocket.getOutputStream());
			// on envoie le fourbi
			for (String strTemp : aString) {
				DataOutStream.writeBytes(strTemp);
			}
			// nice and quiet close
			DataOutStream.flush();
			DataOutStream.close();
			datasocket.close();
			returnstatus = ErrorCode.getMessage("226", "");

		}
		// aie, ça n'a pas marché
		catch (IOException ioe) {
			messageLog = this.getClass().toString() + " sendList Error"
					+ " commande: " + commande + " " + parametre;
			System.err.println(messageLog);
			ioe.printStackTrace();
			returnstatus = "400 Ereur durant l'operation.";

		}
	}

	// calcul de l'url a partir de l'@ IP et du port
	private String getEncPort() {
		String thisport_url = "";

		// dataSrvSocket ectoutant sur tout les ports, il faut utiliser l'@ IP
		// du client fourni par le serveur lors de la connexion d'accroche
		// thisport_url = dataSrvSocket.getInetAddress().getHostAddress();
		if (isPASV) {
			thisport_url = DataAddr.replace(".", ",");
			DataPort = dataSrvSocket.getLocalPort();

		} else {
			thisport_url = DataAddr.replace(".", ",");
			DataPort = dataSrvSocket.getLocalPort();
		}

		String p1 = String.valueOf(dataSrvSocket.getLocalPort() / 256);
		String p2 = String.valueOf(dataSrvSocket.getLocalPort() % 256);

		port_url = thisport_url + "," + p1 + "," + p2;
		return port_url;
	}

	public int getDataPort() {
		// TODO Auto-generated method stub
		return DataPort;
		// return datasocket.getLocalPort();
	}

	/**
	 * @return the datasocket
	 */
	public Socket getDatasocket() {
		return datasocket;
	}

	/**
	 * @param datasocket
	 *            the datasocket to set
	 */
	public void setDatasocket(Socket datasocket) {
		this.datasocket = datasocket;
	}

	/**
	 * @return the dataAddr
	 */
	public String getDataAddr() {
		return DataAddr;
	}

	/**
	 * @param dataAddr
	 *            the dataAddr to set
	 */
	public void setDataAddr(String dataAddr) {
		DataAddr = dataAddr;
	}

	/**
	 * @return the aString
	 */
	public List<String> getaString() {
		return aString;
	}

	/**
	 * @param aString
	 *            the aString to set
	 */
	public void setaString(List<String> aString) {
		this.aString = aString;
	}

	/**
	 * @return the commande
	 */
	public String getCommande() {
		return commande;
	}

	/**
	 * @param commande
	 *            the commande to set
	 */
	public void setCommande(String commande) {
		this.commande = commande;
	}

	/**
	 * @return the parametre
	 */
	public String getParametre() {
		return parametre;
	}

	/**
	 * @param parametre
	 *            the parametre to set
	 */
	public void setParametre(String parametre) {
		this.parametre = parametre;
	}

	/**
	 * @return the dataSrvSocket
	 */
	public ServerSocket getDataSrvSocket() {
		return dataSrvSocket;
	}

	/**
	 * @param dataSrvSocket
	 *            the dataSrvSocket to set
	 */
	public void setDataSrvSocket(ServerSocket dataSrvSocket) {
		this.dataSrvSocket = dataSrvSocket;
	}

	/**
	 * @return the port_url
	 */
	public String getPort_url() {
		return port_url;
	}

	/**
	 * @param port_url
	 *            the port_url to set
	 */
	public void setPort_url(String port_url) {
		this.port_url = port_url;
	}

	/**
	 * @param dataPort
	 *            the dataPort to set
	 */
	public void setDataPort(Integer dataPort) {
		DataPort = dataPort;
	}

	/**
	 * @return the keepRunning
	 */
	public boolean isKeepRunning() {
		return KeepRunning;
	}

	/**
	 * @param keepRunning
	 *            the keepRunning to set
	 */
	public void setKeepRunning(boolean keepRunning) {
		KeepRunning = keepRunning;
	}

	/**
	 * @return the returnstatus
	 */
	public String getReturnstatus() {
		return returnstatus;
	}

	/**
	 * @return the isASCII
	 */
	public boolean isASCII() {
		return isASCII;
	}

	/**
	 * @param isASCII
	 *            the isASCII to set
	 */
	public void setASCII(boolean isASCII) {
		this.isASCII = isASCII;
	}

}