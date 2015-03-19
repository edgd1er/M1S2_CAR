package com.restgateway.service;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriInfo;
import com.restgateway.exceptions.AuthenticationException;

/**
 * Singleton used to authenticate users
 * @author Emmeline Salomon & François Dubiez
 */
public interface AuthenticationManager {

    /**
     * Singleton
     */


    /**
     * Get the session of a client based on the header of a request (basic
     * authentification)
     *
     * @param httpHeaders the header of a reaquest
     * @param uriInfo
     * @return The session of the client
     * @throws AuthenticationException If the user are not authenticate
     */
    ClientSession getSession(HttpHeaders httpHeaders, UriInfo uriInfo) throws AuthenticationException;

    /**
     * Remove the session of a client
     *
     * @param session the session to remove
     */
    void removeClientSession(ClientSession session);

	AuthenticationManager getInstance();
}
