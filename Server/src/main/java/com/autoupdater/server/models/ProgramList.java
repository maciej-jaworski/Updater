package com.autoupdater.server.models;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "program")
public class ProgramList {
	
	List<ProgramPackages> programs;

	public ProgramList() {}
	
	public ProgramList(List<ProgramPackages> programs) {
		this.programs = programs;
	}

	@XmlElement
	public List<ProgramPackages> getPrograms() {
		return programs;
	}

	public void setPrograms(List<ProgramPackages> programs) {
		this.programs = programs;
	}
	
	

}
