package install;

/** Contains information about actualization */
public class Actualization {
	/** actualization id */
	private String id;
	/** actualization name */
	private String name;
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
	/** directory of downloaded files */
	private String source_path;
	/** directory of program to update */
	private String dest_path;

	/**
	 * Constructor
	 * 
	 * @param id
	 *            actualization id
	 * @param name
	 *            actualization name
	 * @param server
	 *            server that contains the actualization
	 * @param version
	 *            actualization version
	 * @param type
	 *            actualization type
	 * @param download_link
	 *            actualization download link
	 * @param programName
	 *            program name which contains actualization
	 * @param source_path
	 *            directory of downloaded files
	 * @param dest_path
	 *            directory of program to update
	 */
	public Actualization(String id, String name, String server, String version,
			String type, String download_link, String programName,
			String source_path, String dest_path) {
		this.id = id;
		this.name = name;
		this.server = server;
		this.version = version;
		this.type = type;
		this.download_link = download_link;
		this.programName = programName;
		this.source_path = source_path;
		this.dest_path = dest_path;
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
	 * Gets actualization name
	 * 
	 * @return actualization name
	 */
	public String getName() {
		return name;
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
	public String getDownload_link() {
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
	 * Gets directory of downloaded files
	 * 
	 * @return directory of downloaded files
	 */
	public String getSource_path() {
		return source_path;
	}

	/**
	 * Gets directory of program to update
	 * 
	 * @return directory of program to update
	 */
	public String getDest_path() {
		return dest_path;
	}
}
