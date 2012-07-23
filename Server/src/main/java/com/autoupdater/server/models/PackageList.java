package com.autoupdater.server.models;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Packages object displayed as XML.
 */
@XmlRootElement(name = "packages")
public class PackageList {
	/**
	 * List of packages.
	 */
	List<Package> pack;
	
	/**
	 * Default constructor.
	 */
	public PackageList() {}

	/**
	 * Constructor for package list.
	 * 
	 * @param pack package list
	 */
	public PackageList(List<Package> pack) {
		this.pack = pack;
	}
	
	/**
	 * Getter for list.
	 * 
	 * @return package list
	 */
	@XmlElement
	public List<Package> getPack() {
		return pack;
	}

	/**
	 * Setter for list.
	 * 
	 * @param pack package list
	 */
	public void setPack (List<Package> pack) {
		this.pack = pack;
	}
}
