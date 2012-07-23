package com.autoupdater.server.models;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "program")
public class ProgramPackages {

	String programName;
	
	List<Package> packages;

	public ProgramPackages() {}
	
	public ProgramPackages(String programName, List<Package> packages) {
		this.programName = programName;
		this.packages = packages;
	}
	
	@XmlElement
	public String getProgramName() {
		return programName;
	}
	
	@XmlElement
	public List<Package> getPackages() {
		return packages;
	}

	public void setProgramName(String programName) {
		this.programName = programName;
	}

	public void setPackages(List<Package> packages) {
		this.packages = packages;
	}
	
}
