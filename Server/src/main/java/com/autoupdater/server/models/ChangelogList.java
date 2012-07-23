package com.autoupdater.server.models;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "package")
public class ChangelogList {
	
	List<ChangelogVer> changelogs;

	public ChangelogList() {}

	public ChangelogList(List<ChangelogVer> changelogs) {
		this.changelogs = changelogs;
	}
	
	@XmlElement
	public List<ChangelogVer> getChangelogs() {
		return changelogs;
	}

	public void setChangelogs(List<ChangelogVer> changelogs) {
		this.changelogs = changelogs;
	}
	

}
