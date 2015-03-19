package com.restgateway.services;

import javax.inject.Singleton;

/**
 * NameStorage is a singleton containing login and password information of the current user. This is a simple POJO.
 * @author   Salomon Emmeline & Dubiez François.
 */
@Singleton
public class Credentials {


	/**
	 * @uml.property  name="login"
	 */
	private String login;
	/**
	 * @uml.property  name="password"
	 */
	private String password;

	/**
	 * @return
	 * @uml.property  name="login"
	 */
	public String getLogin() {
		return this.login;
	}

	/**
	 * @return
	 * @uml.property  name="password"
	 */
	public String getPassword() {
		return this.password;
	}

	/**
	 * @param  login
	 * @uml.property  name="login"
	 */
	public void setLogin(String login) {
		this.login = login;
	}

	/**
	 * @param  password
	 * @uml.property  name="password"
	 */
	public void setPassword(String password) {
		this.password = password;
	}

}
