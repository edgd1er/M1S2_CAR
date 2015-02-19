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
 * ftpData: this thread will handle transfer on the data channel. Potential long time running command: ls, stor, retrieve
 * @author  emeline Salomon & françois Dubiez
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
	 * @uml.property  name="myErrorCode"
	 * @uml.associationEnd  
	 */
	private ErrorCode myErrorCode = null;
	private boolean debugMode=false;
	
	/**
	 * 
	 * ftpData Thread Constructor client -> server , the most simple
	 */
	public FtpData(String clientIp, boolean _isPASV, boolean _debugMode) throws IOException {

		String messageLog = null;
		this.DataAddr = clientIp;
		debugMode= _debugMode;
		myErrorCode= new ErrorCode(debugMode);

		messageLog = this.getClass().toString() + " Cons: Debut: " + _isPASV;
		// mode PASV, on va recuperer le port de connection du client.
		setPASV(_isPASV);

		if (this. debugMode){System.out.println(messageLog);}

	}

	/**
	 * ftpData Thread Constructor Active mode
	 *  
	 * @param cltDataAddr	Data Channel IP Address
	 * @param cltDataPort	Data Channel Port
	 * @param aInfo2Send	List of String to send, most of the time with the list command
	 * @param commande		Command to process
	 * @param parametre		its associated parameters.
	 */
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
		myErrorCode= new ErrorCode(debugMode);
	}

	/**
	 * ftpData Thread Constructor Passive mode
	 *  
	 * @param cltDataAddr	Data Channel IP Address
	 * @param cltDataPort	Data Channel Port
	 * @param aInfo2Send	List of String to send, most of the time with the list command
	 * @param commande		Command to process
	 * @param parametre		its associated parameters.
	 */
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
		myErrorCode= new ErrorCode(debugMode);
	}

	/**
	 * FtpData Thread Start. Active or passive mode according to the settings 
	 * 
	 */
	public void run() {

		try {
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

	/**
	 * 
	 * 
	 * @return
	 * @throws IOException
	 */
	public boolean getPort() throws IOException {
		String messageLog = "";
		port_url = getEncPort();
		messageLog = this.getClass().toString()
				+ " creation de la ServerSocket "
				+ dataSrvSocket.getInetAddress().getHostAddress() + ":"
				+ dataSrvSocket.getLocalPort() + " PORT " + port_url;

		if (debugMode){System.out.println(messageLog);}

		// attente du client sur le canal data
		// recuperation de la socket.
		datasocket = dataSrvSocket.accept();

		messageLog += " ftpData :  constructeur:  le client s'est connecte: "
				+ datasocket.getLocalPort() + " / " + datasocket.getPort();

		if (debugMode){System.out.println(messageLog);}
		return true;
	}

	/**
	 * Tells if the thread is in pasv mode or not.
	 * 
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
	 * Set the passive or active mode.
	 * @param isPASV  the isPASV to set
	 * @throws IOException
	 * @uml.property  name="isPASV"
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

		if (debugMode){System.out.println(messageLog);}
	}


	/**
	 *	send from client to server 
	 * 	File to send is content of parametre.
	 * 
	 */
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
			returnstatus = myErrorCode.getMessage(rep, paramCode);
			messageLog += " " + returnstatus;

		} catch (FileNotFoundException fnf) {
			rep = "550";
			paramCode = "Fichier non accessible: " + parametre;
			returnstatus = myErrorCode.getMessage(rep, paramCode);
			messageLog += " " + returnstatus;

		} catch (IOException ioe) {
			rep = "425";
			paramCode = "Can't Open data connection";
			returnstatus = myErrorCode.getMessage(rep, paramCode);
			messageLog += " " + returnstatus;
		}

		if (debugMode){System.out.println(messageLog);}

	}

	/**
	 *	send from server to client 
	 * 	File to send is content of parametre.
	 * 
	 */
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
			returnstatus = myErrorCode.getMessage(rep, paramCode);

		} catch (FileNotFoundException fnf) {
			rep = "550";
			paramCode = "Fichier non accessible: " + parametre;
			returnstatus = myErrorCode.getMessage(rep, paramCode);

			returnstatus = myErrorCode.getMessage(rep, paramCode);
		} catch (IOException ioe) {
			rep = "425";
			paramCode = "Can't Open data connection";
			returnstatus = myErrorCode.getMessage(rep, paramCode);
			messageLog += " " + returnstatus;
		}

		if (debugMode){System.out.println(messageLog);}

	}

	/**
	 * send the content of aString array to the client.
	 * 
	 */
	public void sendList() {
		String messageLog = this.getClass().toString() + " sendList"
				+ " commande: " + commande + " " + parametre;
		if (debugMode){System.out.println(messageLog);}

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
			returnstatus = myErrorCode.getMessage("226", "");

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

	/**
	 * 
	 * Determine the result for the port command
	 * where w,x,y,z is the ip address separated by comma
	 * a = port /256
	 * b= port % 256
	 * 
	 * w,x,y,z,a,b
	 * 
	 * @return	then encoded url 
	 */
	// calcul de l'url a partir de l'@ IP et du port
	private String getEncPort() {
		String thisport_url = "";

		// dataSrvSocket ecoutant sur tout les ports, il faut utiliser l'@ IP
		// du client fourni par le serveur lors de la connexion d'accroche
		// thisport_url = dataSrvSocket.getInetAddress().getHostAddress();
		thisport_url = DataAddr.replace(".", ",");
		DataPort = dataSrvSocket.getLocalPort();

		/*
		if (isPASV) {
			thisport_url = DataAddr.replace(".", ",");
			DataPort = dataSrvSocket.getLocalPort();

		} else {
			thisport_url = DataAddr.replace(".", ",");
			DataPort = dataSrvSocket.getLocalPort();
		}
		 */
		String p1 = String.valueOf(DataPort / 256);
		String p2 = String.valueOf(DataPort % 256);

		port_url = thisport_url + "," + p1 + "," + p2;
		return port_url;
	}

	/**
	 * @return
	 * @uml.property  name="dataPort"
	 */
	public int getDataPort() {
		// TODO Auto-generated method stub
		return DataPort;
		// return datasocket.getLocalPort();
	}

	/**
	 * @return  the datasocket
	 * @uml.property  name="datasocket"
	 */
	public Socket getDatasocket() {
		return datasocket;
	}

	/**
	 * @param datasocket  the datasocket to set
	 * @uml.property  name="datasocket"
	 */
	public void setDatasocket(Socket datasocket) {
		this.datasocket = datasocket;
	}

	/**
	 * @return  the dataAddr
	 * @uml.property  name="dataAddr"
	 */
	public String getDataAddr() {
		return DataAddr;
	}

	/**
	 * @param dataAddr  the dataAddr to set
	 * @uml.property  name="dataAddr"
	 */
	public void setDataAddr(String dataAddr) {
		DataAddr = dataAddr;
	}

	/**
	 * @return  the aString
	 * @uml.property  name="aString"
	 */
	public List<String> getaString() {
		return aString;
	}

	/**
	 * @param aString  the aString to set
	 * @uml.property  name="aString"
	 */
	public void setaString(List<String> aString) {
		this.aString = aString;
	}

	/**
	 * @return  the commande
	 * @uml.property  name="commande"
	 */
	public String getCommande() {
		return commande;
	}

	/**
	 * @param commande  the commande to set
	 * @uml.property  name="commande"
	 */
	public void setCommande(String commande) {
		this.commande = commande;
	}

	/**
	 * @return  the parametre
	 * @uml.property  name="parametre"
	 */
	public String getParametre() {
		return parametre;
	}

	/**
	 * @param parametre  the parametre to set
	 * @uml.property  name="parametre"
	 */
	public void setParametre(String parametre) {
		this.parametre = parametre;
	}

	/**
	 * @return  the dataSrvSocket
	 * @uml.property  name="dataSrvSocket"
	 */
	public ServerSocket getDataSrvSocket() {
		return dataSrvSocket;
	}

	/**
	 * @param dataSrvSocket  the dataSrvSocket to set
	 * @uml.property  name="dataSrvSocket"
	 */
	public void setDataSrvSocket(ServerSocket dataSrvSocket) {
		this.dataSrvSocket = dataSrvSocket;
	}

	/**
	 * @return  the port_url
	 * @uml.property  name="port_url"
	 */
	public String getPort_url() {
		return port_url;
	}

	/**
	 * @param port_url  the port_url to set
	 * @uml.property  name="port_url"
	 */
	public void setPort_url(String port_url) {
		this.port_url = port_url;
	}

	/**
	 * @param dataPort  the dataPort to set
	 * @uml.property  name="dataPort"
	 */
	public void setDataPort(Integer dataPort) {
		DataPort = dataPort;
	}

	/**
	 * @return  the keepRunning
	 * @uml.property  name="keepRunning"
	 */
	public boolean isKeepRunning() {
		return KeepRunning;
	}

	/**
	 * @param keepRunning  the keepRunning to set
	 * @uml.property  name="keepRunning"
	 */
	public void setKeepRunning(boolean keepRunning) {
		KeepRunning = keepRunning;
	}

	/**
	 * @return  the returnstatus
	 * @uml.property  name="returnstatus"
	 */
	public String getReturnstatus() {
		return returnstatus;
	}

	/**
	 * @return  the isASCII
	 * @uml.property  name="isASCII"
	 */
	public boolean isASCII() {
		return isASCII;
	}

	/**
	 * @param isASCII  the isASCII to set
	 * @uml.property  name="isASCII"
	 */
	public void setASCII(boolean isASCII) {
		this.isASCII = isASCII;
	}

}
