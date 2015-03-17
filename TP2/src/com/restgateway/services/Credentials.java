package com.restgateway.services;

import javax.inject.Singleton;

/**
 * NameStorage is a singleton containing login and password information of
 * the current user.
 * 
 * This is a simple POJO.
 * 
 * @author Salomon Emmeline & Dubiez François.
 * 
 */
@Singleton
public class Credentials {


	private String login;
	private String password;

	public String getLogin() {
		return this.login;
	}

	public String getPassword() {
		return this.password;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
