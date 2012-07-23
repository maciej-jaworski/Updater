package com.autoupdater.server.models;

public class ProgramPackageModel {
	
	String programName;
	
	String packageName;
	
	int programId;
	
	int packageId;
	
	public ProgramPackageModel() {}
	
	public ProgramPackageModel(String programName, String packageName) {
		this.programName = programName;
		this.packageName = packageName;
	}
	
	public ProgramPackageModel(String programName, String packageName, int programId, int packageId) {
		this.programName = programName;
		this.packageName = packageName;
		this.programId = programId;
		this.packageId = packageId;
	}

	public int getProgramId() {
		return programId;
	}

	public int getPackageId() {
		return packageId;
	}

	public void setProgramId(int programId) {
		this.programId = programId;
	}

	public void setPackageId(int packageId) {
		this.packageId = packageId;
	}

	public String getProgramName() {
		return programName;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setProgramName(String programName) {
		this.programName = programName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	
}
