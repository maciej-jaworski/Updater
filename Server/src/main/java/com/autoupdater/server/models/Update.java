package com.autoupdater.server.models;

import java.sql.Date;
import java.util.Arrays;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

/**
 * Update object used to communicate with database.
 */
@XmlRootElement(name = "update")
public class Update {
	/**
	 * Updates's ID.
	 */
	int id;
	
	/**
	 * Package's ID.
	 */
	int package_id;
	
	/**
	 * Uploader's ID.
	 */
	int uploader_id;
	
	/**
	 * Changelog.
	 */
	String changelog;
	
	/**
	 * Date.
	 */
	Date data;
	
	/**
	 * Major version number.
	 */
	int ver1;
	
	/**
	 * Minor version number.
	 */
	int ver2;
	
	/**
	 * Realease number.
	 */
	int ver3;
	
	/**
	 * Build number.
	 */
	int ver4;
	
	/**
	 * Package name.
	 */
	String package_name;
	
	/**
	 * Version number.
	 */
	@NotNull
	String version;

	/**
	 * File.
	 */
	@NotNull
	CommonsMultipartFile filedata;
	
	
	List<String> updateTypes = Arrays.asList("Release", "Developer");
	
	String type;
	
	boolean dev;
	
	public Update () {}	
	
	public Update (int id, int package_id, int uploader_id, String changelog,
			Date data, int ver1, int ver2, int ver3, int ver4) {
		this.id = id;
		this.package_id = package_id;
		this.uploader_id = uploader_id;
		this.changelog = changelog;
		this.data = data;
		this.ver1 = ver1;
		this.ver2 = ver2;
		this.ver3 = ver3;
		this.ver4 = ver4;
		this.version = Integer.toString(ver1) + "." + Integer.toString(ver2) + "." + Integer.toString(ver3) + "." + Integer.toString(ver4);
	}
	
	
	private void parseVersion () {
		String temp[] = version.split("\\.");
		if (temp.length >= 1)  this.setVer1(Integer.parseInt(temp[0])); else this.setVer1(Integer.parseInt(version));
		if (temp.length >= 2)  this.setVer2(Integer.parseInt(temp[1])); else this.setVer2(0);
		if (temp.length >= 3)  this.setVer3(Integer.parseInt(temp[2])); else this.setVer3(0);
		if (temp.length >= 4)  this.setVer4(Integer.parseInt(temp[3])); else this.setVer4(0);
	}
	
	public byte[] getFiledataAsBytes() {
		return filedata.getBytes();
	}
	@XmlTransient
	public int getUploader_id() {
		return uploader_id;
	}

	@XmlElement
	public String getVersion() {
		return version;
	}
	@XmlTransient
	public CommonsMultipartFile getFiledata() {
		return filedata;
	}
	public int getId() {
		return id;
	}
	public int getPackage_id() {
		return package_id;
	}
	@XmlElement
	public String getChangelog() {
		return changelog;
	}
	
	@XmlTransient
	public Date getData() {
		return data;
	}
	@XmlTransient
	public int getVer1() {
		return ver1;
	}
	@XmlTransient
	public int getVer2() {
		return ver2;
	}
	@XmlTransient
	public int getVer3() {
		return ver3;
	}
	@XmlTransient
	public int getVer4() {
		return ver4;
	}
	@XmlElement
	public String getPackage_name() {
		return package_name;
	}
	
	
	public void setVersion(String version) {
		this.version = version;
		this.parseVersion();
	}
	public void setFiledata(CommonsMultipartFile filedata) {
		this.filedata = filedata;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setPackage_id(int package_id) {
		this.package_id = package_id;
	}
	public void setUploader_id(int uploader_id) {
		this.uploader_id = uploader_id;
	}
	public void setChangelog(String changelog) {
		this.changelog = changelog;
	}
	public void setData(Date data) {
		this.data = data;
	}
	public void setVer1(int ver1) {
		this.ver1 = ver1;
	}
	public void setVer2(int ver2) {
		this.ver2 = ver2;
	}
	public void setVer3(int ver3) {
		this.ver3 = ver3;
	}
	public void setVer4(int ver4) {
		this.ver4 = ver4;
	}
	public void setPackage_name(String package_name) {
		this.package_name = package_name;
	}

	@XmlTransient
	public String getType() {
		return type;
	}

	public boolean isDev() {
		return dev;
	}
	
	public boolean getDev() {
		return dev;
	}

	public void setType(String type) {
		this.type = type;
		if(type.equals("Developer"))
			this.dev = true;
		else
			this.dev = false;
	}

	public void setDev(boolean dev) {
		this.dev = dev;
	}
	
	@XmlTransient
	public List<String> getUpdateTypes() {
		return updateTypes;
	}

	public void setUpdateTypes(List<String> updateTypes) {
		this.updateTypes = updateTypes;
	}
	
	
}
