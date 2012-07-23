package com.autoupdater.server.models;

import javax.xml.bind.annotation.XmlElement;

public class ChangelogVer {

	String version;
	
	String changelog;

	public ChangelogVer() {}
	
	public ChangelogVer(String version, String changelog) {
		this.version = version;
		this.changelog = changelog;
	}
	
	@XmlElement
	public String getChangelog() {
		return changelog;
	}
	
	@XmlElement
	public String getVersion() {
		return version;
	}
	
	public void setVersion(String version) {
		this.version = version;
	}

	public void setChangelog(String changelog) {
		this.changelog = changelog;
	}
}
