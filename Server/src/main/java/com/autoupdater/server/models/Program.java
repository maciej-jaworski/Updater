package com.autoupdater.server.models;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.validator.constraints.NotEmpty;

@XmlRootElement(name = "program")
public class Program {
	
	List<Package> packages;
	
	int id;
	
	@NotNull
	@NotEmpty
	String name;
	
	public Program() {}
	
	public Program(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public Program(List<Package> packages, int id, String name) {
		this.packages = packages;
		this.id = id;
		this.name = name;
	}

	public List<Package> getPackages() {
		return packages;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setPackages(List<Package> packages) {
		this.packages = packages;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

}
