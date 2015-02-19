package junit_ftp_server;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

import com.ftp.client.FtpClient;
import com.ftp.server.Server;

public class FtpRequestTest {
	public Server ftpServer;
	public FtpClient ftpClient;
	static int serverPort=2101;
	static String homedir="/tmp/homedir";
	static boolean debugMode =true;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

	}

	/*
	@SuppressWarnings("deprecation")
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		ftpServer.stop();
	}
	*/

	@Before
	public void setUp() throws Exception {
		ftpServer = new Server();
		ftpServer.initialization(serverPort,homedir,debugMode);
		ftpServer.start();
		this.ftpClient = new FtpClient(serverPort);
		System.out.println("Connected");
	}

	@After
	public void tearDown() throws Exception {
		this.ftpClient.close();
		this.ftpServer.setKeepServingRunning(false);
	}
	
		
}