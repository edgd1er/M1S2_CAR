package com.ftp.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.List;

public class ftpData extends Thread {

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
	private int bufferSize=1024;
	private Charset ASCII  = Charset.forName("ASCII");

	/*
	 * constructeur du mode client -> server
	 * 
	 * 
	 * */
	public ftpData(String clientIp, boolean _isPASV) throws IOException {
		
		String messageLog = null;
		this.DataAddr =clientIp;
		
		messageLog = this.getClass().toString() + " Cons: Debut: "+ _isPASV;
		// mode PASV, on va recuperer le port de connection du client.
		setPASV(_isPASV);
		System.out.println(messageLog);

	}

	// mode Direct
	public ftpData(String cltDataAddr, Integer cltDataPort,
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
	public ftpData(ServerSocket dataServerSocket, String cltDataAddr,
			Integer cltDataPort, List<String> aInfo2Send, String commande,
			String parametre) {

		// Mode server
		this.dataSrvSocket = dataServerSocket;
		this.isPASV = true;
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
			//le set pasv a si besoin ouvert la connection vers le client.
			if (datasocket == null) {
					datasocket = new Socket(DataAddr, DataPort);
			}
		}

		catch (UnknownHostException uhe) {
			String messalog = this.getClass().toString() + " Run"
					+ " commande: " + commande + " " + parametre;
			System.out.println(messalog);
			KeepRunning = false;
			uhe.printStackTrace();
		}

		catch (IOException ioe) {
			String messalog = this.getClass().toString() + " Run"
					+ " commande: " + commande + " " + parametre;
			System.out.println(messalog);
			KeepRunning = false;
			ioe.printStackTrace();

		}

		if (KeepRunning) {
			switch (commande) {

			case ("LIST"):
				sendList();
				break;
			case ("RETR"):
				//retrieveFile();
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

		System.out.println(messageLog);

		// attente du client sur le canal data
		// recuperation de la socket.
		datasocket = dataSrvSocket.accept();

		messageLog += " ftpData :  constructeur:  le client s'est connecte: "
				+ datasocket.getLocalPort() + " / " + datasocket.getPort();

		System.out.println(messageLog);
		return true;
	}

	/**
	 * @return the isPASV
	 * @throws IOException
	 */
	public boolean isPASV() throws IOException {
	
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
		String messageLog = this.getClass().toString() + " Mode PASV="+ isPASV;
		
		if (isPASV) {
			if ((this.dataSrvSocket==null) || (this.dataSrvSocket.isClosed()))
			{
				this.dataSrvSocket = new ServerSocket(0);
				DataPort = this.dataSrvSocket.getLocalPort();
				messageLog+= " création";
				}
			else {
				 messageLog+= " réutilisation";
			}
			port_url = getEncPort();
			messageLog+= " de la dataServerSocket:" + DataAddr + ":" + DataPort;
			
			}else{
				//gestion du mode actif
				//TODO ne sera pas faite, puisque ce code ne tournera pas en mode root ( écoute du port 20)
				this.datasocket = new Socket(DataAddr,DataPort);
			}
		
		System.out.println(messageLog);
	}

	//send from client to server
	private void storeAsciiBin() {
		// TODO Gestion ASCII / BIN
		String rep, paramCode, messageLog, line;
		InputStream in = null;
		File file = null;
		int nread=0;
		// pr le mode ascii -- equivalent filewriter
		OutputStreamWriter fow = null;
		// pr le mode bin
		FileOutputStream fos = null;

		// TODO Gestion des mode ASCII / BIN
		messageLog = this.getClass().toString() + " StoreFile (Ascii:"
				+ isASCII + "), commande: " + commande + " " + parametre;

		try {

			file = new File(parametre);
			in = datasocket.getInputStream();

			// mode ascii
			if (isASCII) {
				char[] aChar=new char[bufferSize];
				
				//fow = new OutputStreamWriter(new FileOutputStream(file), ASCII);
				//InputStreamReader isr = new InputStreamReader(in,ASCII);
				fow = new OutputStreamWriter(new FileOutputStream(file));
				InputStreamReader isr = new InputStreamReader(in);
				BufferedReader br = new BufferedReader(isr);
				while((line=br.readLine())!=null){
					fow.write(line);
				}
				/*while ((nread=isr.read(aChar))>0){
					fow.write(aChar, 0, nread);
				}*/
				
				//brd.close();
				isr.close();
				fow.close();
			} else
			// mode binaire
			{
				fos = new FileOutputStream(file);
				BufferedOutputStream bos = new BufferedOutputStream(fos);
				BufferedInputStream bis = new BufferedInputStream(in);
				DataInputStream dis = new DataInputStream(bis);
				DataOutputStream dos = new DataOutputStream(bos);
				
				While (dis.available() != 0)
				{ 
					dis.readLine()
				}
				//while ( (nread = in.read(buf)) > 0 ) {
					// nread obligatoire, sinon ajout de byte qd nread < buffersize
					//fos.write(buf,0,nread);
				//}
				fos.flush();
				bos.close();
				fos.close();
				bos=null;
				fos=null;
			}

			in.close();
			datasocket.close();
			if (dataSrvSocket != null) {
				dataSrvSocket.close();
			}
			KeepRunning = false;
			rep = "226";
			paramCode = "File sent: " + parametre;
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

		System.out.println(messageLog);

	}

	// envoi du fichier du serveur vers le client.
	private void retrieveFile() {
		String rep, paramCode, messageLog;
		File file;
		FileInputStream fis;
		// TODO Gestion des mode ASCII / BIN
		messageLog = this.getClass().toString() + " retrieveFile"
				+ " commande: " + commande + " " + parametre;

		file = new File(parametre);

		try {
			fis = new FileInputStream(file);

			OutputStream outstream = datasocket.getOutputStream();

			byte buf[] = new byte[bufferSize];
			int nread;
			while ((nread = fis.read(buf)) > 0) {
				outstream.write(buf, 0, nread);
			}
			if (outstream != null) {
				outstream.flush();
				outstream.close();
			}
			if (datasocket != null)
				datasocket.close();

			if (dataSrvSocket != null) {
				dataSrvSocket.close();
			}
			KeepRunning = false;
			rep = "226";
			paramCode = "File rerieved: " + parametre;
			System.out.println(messageLog);
			returnstatus = ErrorCode.getMessage(rep, paramCode);
		} catch (FileNotFoundException fnf) {
			rep = "550";
			paramCode = "Cannot access: " + parametre;
			returnstatus = ErrorCode.getMessage(rep, paramCode);

			returnstatus = ErrorCode.getMessage(rep, paramCode);
		} catch (IOException ioe) {
			rep = "425";
			paramCode = "Can't Open data connection";
			returnstatus = ErrorCode.getMessage(rep, paramCode);
		}
		System.out.println(messageLog);
	}

	// envoi du fichier du serveur vers le client.
	private void retrieveAsciiBin() {
		//Gestion ASCII / BIN
		String rep, paramCode, messageLog;
		
		OutputStream out=null;
		
		//sortie sur la socket
		//lecture du fichier
		FileInputStream fis =null;
		// pr le mode ascii -- equivalent filewriter
		InputStreamReader isr=null;
		BufferedReader brd = null;
		OutputStreamWriter sow=null;
		int nread=0,nwrite=0,ntotalread=0,ntotalwrite=0;

		// pr le mode bin
		DataOutputStream dos=null;

		// TODO Gestion des mode ASCII / BIN
		messageLog = this.getClass().toString() + " StoreFile (Ascii:"
				+ isASCII + "), commande: " + commande + " " + parametre;

		try {

			fis = new FileInputStream(parametre);
			
			out = datasocket.getOutputStream();

			// mode ascii
			if (isASCII) {
				// preparation des I/O
				isr = new InputStreamReader(fis,ASCII);
				brd = new BufferedReader(isr);
				sow = new OutputStreamWriter(out, ASCII);
				BufferedWriter bwr = new BufferedWriter(sow);
				
				String readString;
				while ((readString = brd.readLine()) != null) {
					//bwr.write(readString);
					sow.write(readString);
					ntotalread+=readString.length();
				}

				sow.flush();
				sow.close();
				isr.close();
				brd.close();
			} else
			// mode binaire
			{
				
				BufferedInputStream bis = new BufferedInputStream(fis, bufferSize);
				dos = new DataOutputStream(out);
				BufferedOutputStream bdos =new BufferedOutputStream(dos);
				
				byte buf[] = new byte[bufferSize];
				while((nread=bis.read(buf))>0)
				{
					ntotalread+=nread;
					dos.write(buf,0,nread-1);
				}
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
			paramCode = "File sent: " + parametre + " written " + String.valueOf(ntotalread) + (isASCII?" Char":" bytes") ;
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

		System.out.println(messageLog);

	}
	
	public void sendList() {
		// TODO Auto-generated method stub
		String messalog = this.getClass().toString() + " sendList"
				+ " commande: " + commande + " " + parametre;
		System.out.println(messalog);

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
			messalog = this.getClass().toString() + " sendList Error"
					+ " commande: " + commande + " " + parametre;
			System.out.println(messalog);
			ioe.printStackTrace();
			returnstatus = "400 Ereur durant l'operation.";

		}
	}

	// calcul de l'url a partir de l'@ IP et du port
	private String getEncPort() {
		String thisport_url = "";

		//dataSrvSocket ectoutant sur tout les ports, il faut utiliser l'@ IP du client fourni par le serveur lors de la connexion d'accroche
		//thisport_url = dataSrvSocket.getInetAddress().getHostAddress();
		if (isPASV){
			
		}
		else{
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
		//return datasocket.getLocalPort();
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
	 * @param isASCII the isASCII to set
	 */
	public void setASCII(boolean isASCII) {
		this.isASCII = isASCII;
	}

}
