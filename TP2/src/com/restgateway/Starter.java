package com.restgateway;

import org.apache.cxf.transport.servlet.CXFServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import com.restgateway.config.AppConfig;
/**
 * Main class to start Rest services.
 * 
 * 
 * @author Emmeline Salomon & François Dubiez
 *
 */
public class Starter {
	/**
	 * Main Method to start the rest server on port 8080.
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main( final String[] args ) throws Exception {
		Server server = new Server( 8080 );
		        
 		// Register and map the dispatcher servlet
 		final ServletHolder servletHolder = new ServletHolder( new CXFServlet() );
 		final ServletContextHandler context = new ServletContextHandler(); 		
 		context.setContextPath( "/rest" );
 		context.addServlet( servletHolder, "/*" ); 	
 		context.addEventListener( new ContextLoaderListener() );
 		
 		context.setInitParameter( "contextClass", AnnotationConfigWebApplicationContext.class.getName() );
 		context.setInitParameter( "contextConfigLocation", AppConfig.class.getName() );
 		 		
        server.setHandler( context );
        server.start();
        server.join();	
	}
}

