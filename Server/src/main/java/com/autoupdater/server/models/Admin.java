package com.autoupdater.server.models;

import javax.validation.constraints.NotNull;

public class Admin {
	@NotNull
	private String username;
	@NotNull
	private String password;
	@NotNull
	private String name = "";
	@NotNull
	private String surname = "";
	private boolean canAddPackage = false;
	private boolean canAddUpdate = false;
	
	public Admin () {
	}
	
	public String getUsername() {
		return username;
	}
	public String getPassword() {
		return password;
	}
	public String getName() {
		return name;
	}
	public String getSurname() {
		return surname;
	}
	public boolean getCanAddPackage() {
		return canAddPackage;
	}
	public boolean getCanAddUpdate() {
		return canAddUpdate;
	}
	
	public void setUsername (String username) {
		this.username = username;
	}
	public void setPassword (String password) {
		this.password = password;
	}
	public void setName (String name) {
		this.name = name;
	}
	public void setSurname (String surname) {
		this.surname = surname;
	}
	public boolean setCanAddPackage (boolean canAddPackage) {
		this.canAddPackage = canAddPackage;
		return canAddPackage;
	}
	public boolean setCanAddUpdate (boolean canAddUpdate) {
		this.canAddUpdate = canAddUpdate;
		return canAddUpdate;
	}
}
