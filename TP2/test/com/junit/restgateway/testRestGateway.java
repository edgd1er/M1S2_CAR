package com.junit.restgateway;
import static org.junit.Assert.assertEquals;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientFactory;
import javax.ws.rs.client.WebTarget;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.restgateway.service.RestGateway;

/**
 * Junit Test for RestGateway
 * 
 * @author user
 *
 */
public class testRestGateway {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	RestGateway myRest;
	Client client;
	 String ServerPath;
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		RestGateway myRest = new RestGateway();
		myRest.sayHello();
		
	}

	@Before
	public void setUp() throws Exception {
		
	
		
	}

	@After
	public void tearDown() throws Exception {
	}


	@Test
	public void testWelcomeGateway() {
		 client = ClientFactory.newClient();
		 ServerPath="http://localhost:8080/rest/api/ftp/";
		WebTarget toTest=client.target(ServerPath);
		String expected ="<h1>This is Web gateway to reach a FTP Server 127.0.0.1 2100</h1>";
		//String toto= toTest.toString();
		assertEquals(expected, toTest.toString());
	}

}
