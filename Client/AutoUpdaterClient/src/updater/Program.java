package updater;

import java.util.ArrayList;

/** Contains information about program */
public class Program {
	/** program name */
	private String name;
	/** program directory */
	private String path;
	/** server address which contains information about actualizations */
	private String server;
	/** program executable file */
	private String exe;
	/** list of available actualizations */
	private ArrayList<Actualization> availableActualizations;
	/** list of installed actualizations */
	private ArrayList<Actualization> installedActualizations;

	/**
	 * Constructor
	 * 
	 * @param name
	 *            program name
	 * @param path
	 *            program directory
	 * @param exe
	 *            program exe
	 * @param server
	 *            server address which contains information about actualizations
	 */
	public Program(String name, String path, String exe, String server) {
		this.name = name;
		this.path = path;
		this.server = server;
		this.exe = exe;
		this.availableActualizations = new ArrayList<Actualization>();
		this.installedActualizations = new ArrayList<Actualization>();
	}

	/**
	 * Sets program name
	 * 
	 * @param name
	 *            program name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Sets program directory
	 * 
	 * @param path
	 *            program directory
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * Sets server address which contains information about actualizations
	 * 
	 * @param server
	 *            server address which contains information about actualizations
	 */
	public void setServer(String server) {
		this.server = server;
	}

	/**
	 * Sets program exetuable file
	 * 
	 * @param exe
	 *            exetuable file
	 */
	public void setExe(String exe) {
		this.exe = exe;
	}

	/**
	 * Sets list of available actualizations
	 * 
	 * @param availableActualizations
	 *            list of available actualizations
	 */
	public void setAvailableActualizations(
			ArrayList<Actualization> availableActualizations) {
		this.availableActualizations = availableActualizations;
	}

	/**
	 * Removes actualization from list of available actualizations
	 * 
	 * @param actualization
	 *            actualization to remove
	 */
	public void removeAvailableActualization(Actualization actualization) {
		for (int i = 0; i < availableActualizations.size(); ++i) {
			if (actualization.getId() == availableActualizations.get(i).getId()) {
				availableActualizations.remove(i);
			}
		}
	}

	/**
	 * Sets list of installed actualizations
	 * 
	 * @param installedActualizations
	 *            list of installed actualizations
	 */
	public void setInstalledActualizations(
			ArrayList<Actualization> installedActualizations) {
		this.installedActualizations = installedActualizations;
	}

	/**
	 * Adds actualization to list of installed actualizations
	 * 
	 * @param actualization
	 *            actualization to add
	 */
	public void addInstalledActualization(Actualization actualization) {
		this.installedActualizations.add(actualization);
	}

	/**
	 * Gets program name
	 * 
	 * @return program name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets program directory
	 * 
	 * @return program directory
	 */
	public String getPath() {
		return path;
	}

	/**
	 * Gets server address
	 * 
	 * @return server address
	 */
	public String getServer() {
		return server;
	}

	/**
	 * Gets program exectuable file
	 * 
	 * @return program exectuable file
	 */
	public String getExe() {
		return exe;
	}

	/**
	 * Gets list of available actualizations
	 * 
	 * @return list of available actualizations
	 */
	public ArrayList<Actualization> getAvailableActualizations() {
		return availableActualizations;
	}

	/**
	 * Gets actualization from list of available actualization
	 * 
	 * @param name
	 *            actualization name
	 * @param type
	 *            actualization type
	 * @return actualization - if find, null - otherwise
	 */
	public Actualization getAvailableActualization(String name, String type) {
		for (int i = 0; i < availableActualizations.size(); ++i) {
			if (availableActualizations.get(i).getName().equals(name)) {
				if (availableActualizations.get(i).getType().equals(type)) {
					return availableActualizations.get(i);
				}
			}
		}

		return null;
	}

	/**
	 * Gets actualization from list of available actualization
	 * 
	 * @param id
	 *            actualizayion id
	 * @return actualization - if find, null - otherwise
	 */
	public Actualization getAvailableActualization(String id) {
		for (int i = 0; i < availableActualizations.size(); ++i) {
			if (availableActualizations.get(i).getId().equals(id)) {
				return availableActualizations.get(i);
			}
		}

		return null;
	}

	/**
	 * Removes all actualizations from list of available actualizations
	 */
	public void cleanAvailableActualizations() {
		this.availableActualizations.clear();
	}

	/**
	 * Gets list of installed actualizations
	 * @return	list of installed actualizationss
	 */
	public ArrayList<Actualization> getInstalledActualizations() {
		return installedActualizations;
	}

	/**
	 * Checks if actualization is installed
	 * @param actualization		actualization
	 * @return					true - if it is, false - if it is not
	 */
	public boolean isInstalled(Actualization actualization) {
		for (int i = 0; i < installedActualizations.size(); ++i) {
			if (installedActualizations.get(i).getId()
					.equals(actualization.getId())) {
				return !isNewer(installedActualizations.get(i).getVersion(),
						actualization.getVersion());
			}
		}

		return false;
	}

	/**
	 * Checks if there is a newer version of actualization
	 * @param installedVersion		version of installed actualization
	 * @param availableVersion		version of available actualization
	 * @return						true - if is newer, false - otherwise
	 */
	private boolean isNewer(String installedVersion, String availableVersion) {
		int Major1 = 0, Minor1 = 0, Release1 = 0, Build1 = 0;
		int Major2 = 0, Minor2 = 0, Release2 = 0, Build2 = 0;
		String[] insVer = installedVersion.split("\\.");
		String[] avaVer = availableVersion.split("\\.");

		Major1 = Integer.parseInt(insVer[0]);
		Minor1 = Integer.parseInt(insVer[1]);
		Release1 = Integer.parseInt(insVer[2]);
		Build1 = Integer.parseInt(insVer[3]);

		Major2 = Integer.parseInt(avaVer[0]);
		Minor2 = Integer.parseInt(avaVer[1]);
		Release2 = Integer.parseInt(avaVer[2]);
		Build2 = Integer.parseInt(avaVer[3]);

		if (Major1 < Major2) {
			return true;
		}

		if (Major1 == Major2 && Minor1 < Minor2) {
			return true;
		}

		if (Major1 == Major2 && Minor1 == Minor2 && Release1 < Release2) {
			return true;
		}

		if (Major1 == Major2 && Minor1 == Minor2 && Release1 == Release2
				&& Build1 < Build2) {
			return true;
		}

		return false;
	}

	/**
	 * Returns program information
	 */
	public String toString() {
		return "Name: " + name + ", Path: " + path + ", Exe: " + exe
				+ ", Server: " + server;
	}
}
