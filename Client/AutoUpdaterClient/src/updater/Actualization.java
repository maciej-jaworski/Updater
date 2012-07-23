package updater;

/** Contains information about actualization */
public class Actualization {
	/** actualization name */
	private String name;
	/** actualization id */
	private String id;
	/** server that contains the actualization */
	private String server;
	/** actualization version */
	private String version;
	/** actualization type */
	private String type;
	/** actualization download link */
	private String download_link;
	/** program name which contains actualization */
	private String programName;

	/**
	 * Constructor
	 * 
	 * @param name
	 *            actualization name
	 * @param id
	 *            actualization id
	 * @param version
	 *            actualization version
	 * @param type
	 *            actualization type
	 * @param server
	 *            server that contains the actualization
	 * @param download_link
	 *            actualization download link
	 */
	public Actualization(String name, String id, String version, String type,
			String server, String download_link) {
		this.server = server;
		this.version = version;
		this.type = type;
		this.name = name;
		this.id = id;
		this.download_link = download_link;
	}

	/**
	 * Sets actualization name
	 * 
	 * @param name
	 *            actualization name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Sets actualization id
	 * 
	 * @param id
	 *            actualization id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Sets server that contains the actualization
	 * 
	 * @param server
	 *            server that contains the actualization
	 */
	public void setServer(String server) {
		this.server = server;
	}

	/**
	 * Sets actualization version
	 * 
	 * @param version
	 *            actualization version
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * Sets actualization type
	 * 
	 * @param type
	 *            actualization type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Sets actualization download link
	 * 
	 * @param download_link
	 *            actualization download link
	 */
	public void setDownloadLink(String download_link) {
		this.download_link = download_link;
	}

	/**
	 * Sets program name which contains actualization
	 * 
	 * @param programName
	 *            program name which contains actualization
	 */
	public void setProgramName(String programName) {
		this.programName = programName;
	}

	/**
	 * Gets actualization name
	 * 
	 * @return actualization name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets actualization id
	 * 
	 * @return actualization id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Gets server that contains the actualization
	 * 
	 * @return server that contains the actualization
	 */
	public String getServer() {
		return server;
	}

	/**
	 * Gets actualization version
	 * 
	 * @return actualization version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * Gets actualization type
	 * 
	 * @return actualization type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Gets actualization download link
	 * 
	 * @return actualization download link
	 */
	public String getDownloadLink() {
		return download_link;
	}

	/**
	 * Gets program name which contains actualization
	 * 
	 * @return program name which contains actualization
	 */
	public String getProgramName() {
		return programName;
	}

	/**
	 * Returns actualization information
	 */
	public String toString() {
		return "Name: " + name + ", Version: " + version + ", Type: " + type
				+ ", DownloadLink: " + download_link;
	}
}
