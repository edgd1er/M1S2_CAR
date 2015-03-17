package com.restgateway.rs;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
/**
 * 
 * JaxRS application definition
 * 
 * @author Emmeline Salomon & François Dubiez
 *
 *The applicationPath annotation add the word api to the Rest's service URI.
 *
 */
@ApplicationPath("api")
public class JaxRsApiApplication extends Application {


}
