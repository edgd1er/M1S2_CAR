package com.restgateway.service;

import javax.inject.Singleton;

@Singleton
public class Credentials {
	
	/**
	* NameStorage is a singleton containing login and password information of the current user
	*
	* Salomon Emeline & Dubiez François.
	* 
	*/


	private String login ;
	private String password ;
	
	public String getLogin(){
	return this.login;
	}
	public String getPassword(){
	return this.password;
	}
	public void setLogin(String login){
	this.login = login;
	}
	public void setPassword(String password){
	this.password = password;
	}

}
