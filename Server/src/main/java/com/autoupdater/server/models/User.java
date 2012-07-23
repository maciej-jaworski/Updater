package com.autoupdater.server.models;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

/**
 * User object used to communicate with database.
 */
public class User {
	/**
	 * User's ID.
	 */
	int id;
	
	/**
	 * Login.
	 */
	@NotNull
	@Length(min=4, max=20)
	String username;
	
	/**
	 * Password.
	 */
	@Length(min=5, max=100)
	String password;
	
	/**
	 * Password confirmation.
	 */
	String confirmPassword;
	
	/**
	 * Name.
	 */
	@NotNull
	String name = "";
	
	/**
	 * Whether user is admin.
	 */
	boolean admin;
	
	/**
	 * Whether user is package admin.
	 */
	boolean packageAdmin;
	
	String user_type;
	
	public User () {}
	public User (String username, String password) {
		this.username = username;
		this.password = password;
	}
	
	public User (int id, String username, String password, String name) {
		this.id = id;
		this.username = username;
		this.password = password;
		this.name = name;
	}
	
	public User (int id, String username, String password, String name, boolean Admin, boolean packageAdmin) {
		this.id = id;
		this.username = username;
		this.password = password;
		this.name = name;
		this.admin = Admin;
		this.packageAdmin = packageAdmin;
	}
	
	public int 		getId() { return this.id; }
	public String	getUsername () { return this.username; }
	public String	getPassword () { return this.password; }
	public String	getConfirmPassword () { return this.confirmPassword; }
	public String	getName () { return this.name; }
	public boolean 	isAdmin() {	return admin; }
	public boolean 	isPackageAdmin() {	return packageAdmin; }
	public boolean 	getAdmin() {	return admin; }
	public boolean 	getPackageAdmin() {	return packageAdmin; }
	public String 	getUser_type() { return user_type; }

	
	public void		setId (int id) { this.id = id; }
	public void		setUsername (String username) { this.username = username; }
	public void 	setPassword (String password) { this.password = password; }
	public void		setConfirmPassword (String confirmPassword) { this.confirmPassword = confirmPassword; }
	public void 	setName (String name) { this.name = name; }
	public void 	setAdmin(boolean admin) {this.admin = admin;}
	public void 	setPackageAdmin(boolean packageAdmin) {this.packageAdmin = packageAdmin; }
	public void 	setUser_type(String user_type) { this.user_type = user_type; }

	@Override
	public String toString () { return this.username; }
}
