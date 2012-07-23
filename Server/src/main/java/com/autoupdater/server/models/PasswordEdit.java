package com.autoupdater.server.models;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

public class PasswordEdit {

	int userId;
	
	@NotNull
	@Length(min=4, max=20)
	String currentPw;
	
	@NotNull
	@Length(min=4, max=20)
	String newPw;
	
	String confirmPw;

	public PasswordEdit() {}
	
	public PasswordEdit(String currentPw, String newPw, String confirmPw) {
		this.currentPw = currentPw;
		this.newPw = newPw;
		this.confirmPw = confirmPw;
	}

	public String getCurrentPw() {
		return currentPw;
	}

	public String getNewPw() {
		return newPw;
	}

	public String getConfirmPw() {
		return confirmPw;
	}

	public void setCurrentPw(String currentPw) {
		this.currentPw = currentPw;
	}

	public void setNewPw(String newPw) {
		this.newPw = newPw;
	}

	public void setConfirmPw(String confirmPw) {
		this.confirmPw = confirmPw;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}
}
