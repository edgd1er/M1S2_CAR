package junit_ftp_server;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import com.ftp.client.FtpClient;
import com.ftp.server.Server;

public class FtpRequestTest {
	public static Server ftpServer;
	public FtpClient ftpClient;
	static int serverPort=2100;
	static String homedir="/tmp/homedir";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		ftpServer = new Server();
		ftpServer.initialization(serverPort,homedir,false);
		ftpServer.start();
	}

	@SuppressWarnings("deprecation")
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		ftpServer.stop();
	}

	@Before
	public void setUp() throws Exception {
		this.ftpClient = new FtpClient(serverPort);
		System.out.println("Connected");
	}

	@After
	public void tearDown() throws Exception {
		this.ftpClient.close();
	}
	
		
}