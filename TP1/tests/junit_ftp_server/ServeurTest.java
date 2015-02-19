package junit_ftp_server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.UnknownHostException;

import org.junit.Before;
import org.junit.Test;

import com.ftp.client.FtpClient;
import com.ftp.server.Server;

public class ServeurTest {

	public Server ftpServer;
	public FtpClient ftpClient;
	static int serverPort = 2100;
	static String homedir = "/tmp/homedir";

	@Before
	public void setUp() throws Exception {
		ftpServer = new Server();
		ftpServer.initialization(serverPort, homedir, false);
		ftpServer.start();
		this.ftpClient = new FtpClient(serverPort);
		System.out.println("Connected");
	}

	@Test
	public void testRunning() throws UnknownHostException, IOException {

		assertNotNull(ftpServer.getServeurSocket());
		@SuppressWarnings("unused")
		FtpClient ftpclient = new FtpClient(serverPort);
		assertNotNull(ftpServer.getSocket());
		assertEquals(ftpServer.getServeurSocket().getLocalPort(), serverPort);
		ftpServer.setKeepServingRunning(false);
	}

}
