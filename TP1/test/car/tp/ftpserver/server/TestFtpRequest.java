package car.tp.ftpserver.server;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.junit.Before;
import org.junit.Test;

import com.ftp.server.*;

public class TestFtpRequest {
	
	private FtpRequest ftpreq;

	@Before
	public void setUp() throws Exception {
		String prepath = "/tmp/homedir";
		//boolean debugMode = false;
		ServerSocket serverskt = null;
		Boolean keepServingRunning = true;
		int serverPort = 2100;
		ftpreq = null;
		//FtpClient myclient=new FtpClient(2100);

		try {
			serverskt = new ServerSocket(serverPort);

			while (keepServingRunning) {
				Socket cltSocCtrl = serverskt.accept();
				String clientIP = cltSocCtrl.getLocalAddress().getHostAddress();
				System.out.println("Server:incoming connection from "
						+ clientIP);

					ftpreq = new FtpRequest(cltSocCtrl, prepath,true);


				if (ftpreq != null) {
					ftpreq.start();
				}

				System.out.println("Server:started thread FtpRequest for "
						+ cltSocCtrl.getInetAddress().getHostName());
			}
			// server is stopping
			serverskt.close();
		} catch (IOException e) {
			System.err.println(Thread.currentThread().getClass().getName()
					+ ": errorr\n");
			e.printStackTrace();
			try {
				serverskt.close();
			} catch (Exception e1) {
				System.err.println(Thread.currentThread().getClass().getName()
						+ ": errorr\n");
				e1.printStackTrace();
			}
		}

	}

	@Test
	public void testProcessRETR() {
		ftpreq.setCommande("RETR file.txt");
		ftpreq.processRequest();
		
		assertEquals(ftpreq.getRep(), "150");
		assertEquals(ftpreq.getParamCode(), "Opening file.txt en mode data connection.\n");
	}

	@Test
	public void testProcessSTOR() {
		ftpreq.setCommande("STOR file.txt");
		ftpreq.processRequest();
		
		assertEquals(ftpreq.getRep(), "150");
		assertEquals(ftpreq.getParamCode(), "Opening file.txt en mode data connection.\n");
	}
	
	@Test
	public void testProcessCDUP() {
		ftpreq.setCommande("CDUP file.txt");
		ftpreq.processRequest();
		
		assertEquals(ftpreq.getRep(), "250");
		assertEquals(ftpreq.getParamCode(), "Directory changed to tmp/myDir");
	}
	
	@Test
	public void testProcessUSER() {
		ftpreq.setCommande("USER a");
		ftpreq.processRequest();
		
		assertEquals(ftpreq.getRep(), "331");
		assertEquals(ftpreq.getParamCode(), "a");
	}
	
	@Test
	public void testProcessPASS() {
		ftpreq.setCommande("PASS b");
		ftpreq.processRequest();
		
		assertEquals(ftpreq.getRep(), "430");
	}
	
	@Test
	public void testProcessSYST() {
		ftpreq.setCommande("SYST");
		ftpreq.processRequest();
		
		assertEquals(ftpreq.getRep(), "500");
	}
	
	@Test
	public void testProcessPWD() {
		ftpreq.setCommande("PWD");
		ftpreq.processRequest();
		
		assertEquals(ftpreq.getRep(), "550");
	}
	
	@Test
	public void testProcessTYPE() {
		ftpreq.setCommande("TYPE");
		ftpreq.processRequest();
		
		assertEquals(ftpreq.getRep(), "502");
	}
	
	@Test
	public void testProcessPORT() {
		ftpreq.setCommande("PORT 50, 102, 192, 168, 0");
		ftpreq.processRequest();
		
		assertEquals(ftpreq.getRep(), "501");
	}
	
	@Test
	public void testProcessPASV() {
		ftpreq.setCommande("PASV");
		ftpreq.processRequest();
		
		assertEquals(ftpreq.getRep(), "502");
	}
	
	@Test
	public void testProcessLIST() {
		ftpreq.setCommande("PASV");
		ftpreq.processRequest();
		
		assertEquals(ftpreq.getRep(), "150");
	}
	
	@Test
	public void testProcessCWD() {
		ftpreq.setCommande("CWD /tmp/myDir");
		ftpreq.processRequest();
		
		assertEquals(ftpreq.getRep(), "250");
		assertEquals(ftpreq.getParamCode(), "Directory changed to /tmp/myDir");
	}
	
	@Test
	public void testProcessQUIT() {
		ftpreq.setCommande("QUIT");
		ftpreq.processRequest();
		
		assertEquals(ftpreq.getRep(), "221");
		assertEquals(ftpreq.getParamCode(), "Requesting end of session.");
	}
	
	@Test
	public void testProcessDELE() {
		ftpreq.setCommande("QUIT");
		ftpreq.processRequest();
		
		assertEquals(ftpreq.getRep(), "450");
		assertEquals(ftpreq.getParamCode(), "a cannot delete or upload file. Please use an account.");
	}

}
