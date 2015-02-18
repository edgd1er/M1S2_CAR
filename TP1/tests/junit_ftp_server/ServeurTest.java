package junit_ftp_server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.UnknownHostException;

import org.junit.Test;

import com.ftp.client.FtpClient;
import com.ftp.server.Server;

public class ServeurTest {

	@Test
public void testRunning() throws UnknownHostException, IOException {
		int serverPort=2100;
Server myServeur = new Server();
myServeur.initialization(serverPort,"/tmp/homedir",true);
assertNotNull(myServeur.getServeurSocket());
myServeur.start();
FtpClient myClient = new FtpClient(serverPort);
assertNotNull(myServeur.getSocket());
assertEquals(myServeur.getServeurSocket().getLocalPort(), serverPort);
}

}
