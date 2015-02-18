package junit_ftp_server;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class FtprequestPassTest extends FtpRequestTest {

	@Test
	public void testUserLoginOk() {
	String cmd,ret, exp;
	
	ret= this.ftpClient.receive().substring(0,3);
	exp="220";
	
	assertEquals(ret, exp);
	cmd="USER user";
	this.ftpClient.send(cmd);
	ret= this.ftpClient.receive().substring(0,3);
	exp="331";
	assertEquals(ret, exp);

	cmd="PASS a";
	this.ftpClient.send(cmd);
	ret= this.ftpClient.receive().substring(0,3);
	exp="230";
	assertEquals(ret, exp);
	
	cmd="quit";
	this.ftpClient.send(cmd);
	}
	
	@Test
	public void testUserPassFail() {
		String cmd,ret, exp;
		
		ret= this.ftpClient.receive().substring(0,3);
		exp="220";
		assertEquals(ret, exp);
		cmd="USER user";
		this.ftpClient.send(cmd);
		ret= this.ftpClient.receive().substring(0,3);
		exp="331";
		assertEquals(ret, exp);

		cmd="PASS b";
		this.ftpClient.send(cmd);
		ret= this.ftpClient.receive().substring(0,3);
		System.err.println("Junit:" + ret);
		exp="430";
		assertEquals(ret, exp);
		cmd="quit";
		this.ftpClient.send(cmd);
	}

	@Test
	public void testAnonPassFail() {
		String cmd,ret, exp;
		
		ret= this.ftpClient.receive().substring(0,3);
		exp="220";
		assertEquals(ret, exp);
		cmd="USER anonymous";
		this.ftpClient.send(cmd);
		ret= this.ftpClient.receive().substring(0,3);
		exp="331";
		assertEquals(ret, exp);

		cmd="PASS b";
		this.ftpClient.send(cmd);
		ret= this.ftpClient.receive().substring(0,3);
		System.err.println("Junit:" + ret);
		exp="430";
		assertEquals(ret, exp);
		cmd="quit";
		this.ftpClient.send(cmd);
	}
	
	@Test
	public void testAnonPassOK() {
		String cmd,ret, exp;
		
		ret= this.ftpClient.receive().substring(0,3);
		exp="220";
		assertEquals(ret, exp);
		cmd="USER anonymous";
		this.ftpClient.send(cmd);
		ret= this.ftpClient.receive().substring(0,3);
		exp="331";
		assertEquals(ret, exp);

		cmd="PASS user@asif.com";
		this.ftpClient.send(cmd);
		ret= this.ftpClient.receive().substring(0,3);
		System.err.println("Junit:" + ret);
		exp="230";
		assertEquals(ret, exp);
		cmd="quit";
		this.ftpClient.send(cmd);
	}
}
