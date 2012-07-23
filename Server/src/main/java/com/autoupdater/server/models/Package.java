package com.autoupdater.server.models;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * Package object used to communicate with database.
 */
@XmlRootElement(name = "package")
public class Package {
	/**
	 * Package's ID.
	 */
	int id;

	/**
	 * name.
	 */
	@NotNull
	@NotEmpty
	String name;
	
	int programId;
	
	/**
	 * Default constructor.
	 */
	public Package() {}
	
	/**
	 * Constructor for name.
	 * 
	 * @param name name
	 */
	public Package (String name) {
		this.name = name;
	}
	
	public Package (String name, int programId) {
		this.name = name;
		this.programId = programId;
	}
	
	/**
	 * Constructor for id, name.
	 * 
	 * @param id ID
	 * @param name name
	 */
	public Package (int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public Package (int id, String name, int programId) {
		this.id = id;
		this.name = name;
		this.programId = programId;
	}
	@XmlTransient
	public int getProgramId() {
		return programId;
	}

	public void setProgramId(int programId) {
		this.programId = programId;
	}

	/**
	 * Getter for ID.
	 * 
	 * @return ID
	 */
	@XmlElement
	public int getId () {
		return id;
	}
	
	/**
	 * Getter for name (filename).
	 * 
	 * @return full_name
	 */
	@XmlElement
	public String getName () {
		return name;
	}
	
	/**
	 * Setter for ID.
	 * 
	 * @param id ID
	 */

	public void setId (int id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

}
