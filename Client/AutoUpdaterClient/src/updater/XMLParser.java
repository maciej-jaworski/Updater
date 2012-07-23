package updater;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

/**
 * Class XMLParser in package updater is responsible for parse xml files. 
 * All data about programss edded to AutoUpdater, options and informations about installation are in xml files.
 * This class writes and reads from the files.
 *
 */
public class XMLParser {

	private Document document; 	//The Document is a container for text that serves as the model for swing text components.
	private SAXReader reader;	//SAXReader creates a DOM4J tree from SAX parsing events.
	
	/**
	 * Constructor of XMLParser class.
	 */
	public XMLParser() {
		reader = new SAXReader();
	}
	
	/**
	 * Initialize xml which is named AutoUpdaterData.xml. 
	 * This xml has data about programs which AutoUpdater can updates and options of AutoUpdater.
	 * @param file name of file
	 * @return
	 * @throws DocumentException DocumentException is a nested Exception which may be thrown during the processing of a DOM4J document.
	 */
	public boolean initDataXml(File file) throws DocumentException {
		if (file.exists()) {
			parse(file);
			return false;
		} else {
			createDataXml();
			return true;
		}
	}
	
	/**
	 * This method initialize the xml filewhich have information about installed package in program.
	 * @param file name of xml file
	 * @return
	 * @throws DocumentException DocumentException is a nested Exception which may be thrown during the processing of a DOM4J document.
	 */
	public boolean initPackageXml(File file) throws DocumentException {
		if (file.exists()) {
			parse(file);
			return false;
		} else {
			createPackageXml();
			return true;
		}
	}

	/**
	 * Initializes xml file which has queue of installation package.
	 * @param file name of xml file
	 * @return
	 * @throws DocumentException DocumentException is a nested Exception which may be thrown during the processing of a DOM4J document.
	 */
	public boolean initInstallXml(File file) throws DocumentException {
		if (file.exists()) {
			if (!file.delete()) {
				return false;
			}
		}

		createInstallXml();

		return true;
	}

	/**
	 * Initialize parse xml file by name of this file
	 * @param file name of xml file
	 * @return
	 * @throws DocumentException DocumentException is a nested Exception which may be thrown during the processing of a DOM4J document.
	 */
	public boolean parseXml(File file) throws DocumentException {
		if (file.exists()) {
			parse(file);
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Creates xml about data of programs which AutoUpdater can updates and options of AutoUpdater.
	 */
	private void createDataXml() {
		document = DocumentHelper.createDocument();
		Element root = document.addElement("Data");

		root.addElement("Programs");
		root.addElement("Options");
	}
	
	/**
	 * Creates xml about data of program's packages.
	 */
	private void createPackageXml() {
		document = DocumentHelper.createDocument();
		document.addElement("Actualizations");
	}

	/**
	 * Creates xml about informations of installations.
	 */
	private void createInstallXml() {
		document = DocumentHelper.createDocument();
		document.addElement("List");
	}
	
	/**
	 * Starts reading file to document.
	 * @param file name of xml file.
	 * @throws DocumentException DocumentException is a nested Exception which may be thrown during the processing of a DOM4J document.
	 */
	private void parse(File file) throws DocumentException {
		document = reader.read(file);
	}

	/**
	 * Saves the changes in xml file.
	 * @param file name of xml file.
	 * @throws IOException
	 */
	public void save(File file) throws IOException {
		XMLWriter writer = new XMLWriter(new FileWriter(file),
				OutputFormat.createPrettyPrint());
		writer.write(document);
		writer.close();
	}
	
	/**
	 * Adds data about proxy to xml file.
	 * @param address address of proxy.
	 * @param port port of proxy.
	 * @return
	 */
	public boolean addProxy(String address, int port) {
		List<? extends Node> list = document
				.selectNodes("/Data/Options/Option/@name");
		Element options = document.getRootElement().element("Options");
		boolean onList = false;

		for (Iterator<? extends Node> iter = list.iterator(); iter.hasNext();) {
			Attribute attribute = (Attribute) iter.next();
			if (attribute.getValue().equals("Proxy")) {
				attribute.getParent().attribute("address").setValue(address);
				attribute.getParent().attribute("port")
						.setValue(Integer.toString(port));
				onList = true;
				break;
			}
		}

		if (!onList) {
			options.addElement("Option").addAttribute("name", "Proxy")
					.addAttribute("address", address)
					.addAttribute("port", Integer.toString(port));
		}

		return true;
	}
	
	/**
	 * Removes proxy from xml file.
	 * @return
	 */
	public boolean removeProxy() {
		List<? extends Node> list = document
				.selectNodes("/Data/Options/Option/@name");

		for (Iterator<? extends Node> iter = list.iterator(); iter.hasNext();) {
			Attribute attribute = (Attribute) iter.next();
			if (attribute.getValue().equals("Proxy")) {
				attribute.getParent().detach();
				break;
			}
		}

		return true;
	}

	/**
	 * Gets address and port of proxy form xml file.
	 * @return Array of String with address and port of proxy.
	 */
	public String[] getProxy() {
		String[] proxy = new String[2];
		List<? extends Node> list = document
				.selectNodes("/Data/Options/Option");

		proxy[0] = proxy[1] = "";

		for (Iterator<? extends Node> iter = list.iterator(); iter.hasNext();) {
			Element element = (Element) iter.next();

			if (element.attributeValue("name").equals("Proxy")) {
				proxy[0] = element.attributeValue("address");
				proxy[1] = element.attributeValue("port");

				break;
			}
		}

		return proxy;
	}

	/**
	 * Returns paths to necessary files responsible for installation like ElevateHandler, Installation.jar and Checker.jar
	 * @return Array of String with three paths.
	 */
	public String[] getInstallationProcessPath() {
		String[] data = new String[3];
		List<? extends Node> list = document
				.selectNodes("/Data/Options/Option");

		data[0] = data[1] = data[2] = "";

		for (Iterator<? extends Node> iter = list.iterator(); iter.hasNext();) {
			Element element = (Element) iter.next();

			if (element.attributeValue("name").equals("InstallationData")) {
				data[0] = element.attributeValue("ElevateHandler");
				data[1] = element.attributeValue("Installation");
				data[2] = element.attributeValue("Checker");

				break;
			}
		}

		return data;
	}

	/**
	 * Returns map with programs data from xml file.
	 * @return
	 */
	public Map<String, Program> getPrograms() {
		List<? extends Node> list = document
				.selectNodes("/Data/Programs/Program");
		Map<String, Program> programs = new HashMap<String, Program>();

		for (Iterator<? extends Node> iter = list.iterator(); iter.hasNext();) {
			Element element = (Element) iter.next();

			programs.put(
					element.attributeValue("name"),
					new Program(element.attributeValue("name"), element
							.attributeValue("path"), element
							.attributeValue("exe"), element
							.attributeValue("server")));
		}

		return programs;
	}

	/**
	 * Adds actualization to xml file after correct update.
	 * @param actualization Object instance of Actualization which has name, version, type, id and link of actualization.
	 * @return
	 */
	public boolean addActualization(Actualization actualization) {
		List<? extends Node> list = document
				.selectNodes("/Actualizations/Server");
		Element actualizations = document.getRootElement();
		boolean onList = false;

		for (Iterator<? extends Node> iter = list.iterator(); iter.hasNext();) {
			Element element = (Element) iter.next();

			if (element.attributeValue("address").equals(
					actualization.getServer())) {
				for (Iterator<? extends Node> i = element.elementIterator(); i
						.hasNext();) {
					Element child = (Element) i.next();

					if (child.attributeValue("id")
							.equals(actualization.getId())) {
						child.attribute("version").setValue(
								actualization.getVersion());
						child.attribute("download_link").setValue(
								actualization.getDownloadLink());

						onList = true;
					}
				}

				if (!onList) {
					element.addElement("Actualization")
							.addAttribute("name", actualization.getName())
							.addAttribute("version", actualization.getVersion())
							.addAttribute("type", actualization.getType())
							.addAttribute("id", actualization.getId())
							.addAttribute("download_link",
									actualization.getDownloadLink());
				}

				return !onList;
			}
		}

		actualizations.addElement("Server")
				.addAttribute("address", actualization.getServer())
				.addElement("Actualization")
				.addAttribute("name", actualization.getName())
				.addAttribute("version", actualization.getVersion())
				.addAttribute("type", actualization.getType())
				.addAttribute("id", actualization.getId())
				.addAttribute("download_link", actualization.getDownloadLink());

		return true;
	}

	/**
	 * Returns list of actualizations from xml file.
	 * @return
	 */
	public ArrayList<Actualization> getActualizations() {
		List<? extends Node> list = document
				.selectNodes("/Actualizations/Server");
		ArrayList<Actualization> actualizations = new ArrayList<Actualization>();

		for (Iterator<? extends Node> iter = list.iterator(); iter.hasNext();) {
			Element element = (Element) iter.next();

			for (Iterator<? extends Node> iter_childs = element
					.elementIterator(); iter_childs.hasNext();) {
				Element child = (Element) iter_childs.next();

				actualizations.add(new Actualization(child
						.attributeValue("name"), child.attributeValue("id"),
						child.attributeValue("version"), child
								.attributeValue("type"), element
								.attributeValue("address"), child
								.attributeValue("download_link")));
			}
		}

		return actualizations;
	}

	/**
	 * Returns object instance of Actualization from xml file.
	 * @return
	 */
	public Actualization getActualizationInfo() {
		Element root = document.getRootElement();
		String type = Boolean.valueOf(root.elementText("dev")) ? "developer"
				: "release";

		Actualization actualization = new Actualization(
				root.elementText("package_name"),
				root.elementText("package_id"), root.elementText("version"),
				type, null, root.elementText("id"));

		return actualization;
	}

	/**
	 * Returns names of actualizations from xml files. 
	 * @param program_name name of program which actualization we want to get.
	 * @return
	 */
	public ArrayList<String> getActualizationsNames(String program_name) {
		List<? extends Node> list = document
				.selectNodes("//programs//programName");
		ArrayList<String> actualizations_names = new ArrayList<String>();

		for (Iterator<? extends Node> iter = list.iterator(); iter.hasNext();) {
			Element element = (Element) iter.next();

			if (element.getText().equals(program_name)) {
				Element parent = element.getParent();

				for (Iterator<? extends Node> i = parent.elementIterator(); i
						.hasNext();) {
					Element child = (Element) i.next();

					actualizations_names.add(child.elementText("name"));
					actualizations_names.add(child.elementText("id"));
				}

				actualizations_names.remove(actualizations_names.size() - 1);
				actualizations_names.remove(actualizations_names.size() - 1);

				break;
			}
		}

		return actualizations_names;
	}

	/**
	 * Returns map of changelogs from xml file.
	 * @return
	 */
	public Map<String, String> getChangeLogs() {
		Map<String, String> changelogs = new HashMap<String, String>();
		List<? extends Node> list = document.selectNodes("/package/changelogs");

		for (Iterator<? extends Node> iter = list.iterator(); iter.hasNext();) {
			Element element = (Element) iter.next();

			changelogs.put(element.elementText("version"),
					element.elementText("changelog"));
		}

		return changelogs;
	}
	
	/**
	 * Adds information to xml file which has data about installation.
	 * @param program_name name of program which we want to update
	 * @param actualization Object instance of Actualization which has data about actualization.
	 * @param source_path path to files which will be installing.
	 * @param dest_path path to directory where files from package will be copying.
	 */
	public void addToInstall(String program_name, Actualization actualization,
			String source_path, String dest_path) {
		Element root = document.getRootElement();

		root.addElement("Actualization")
				.addAttribute("program_name", program_name)
				.addAttribute("actualization_name", actualization.getName())
				.addAttribute("actualization_id", actualization.getId())
				.addAttribute("server", actualization.getServer())
				.addAttribute("type", actualization.getType())
				.addAttribute("download_link", actualization.getDownloadLink())
				.addAttribute("version", actualization.getVersion())
				.addAttribute("source", source_path)
				.addAttribute("destination", dest_path);
	}

	/**
	 * Returns list of program and actualization of this program.
	 * @return
	 */
	public ArrayList<String> getFromInstall() {
		ArrayList<String> install = new ArrayList<String>();
		List<? extends Node> list = document.selectNodes("/List/Actualization");

		for (Iterator<? extends Node> iter = list.iterator(); iter.hasNext();) {
			Element element = (Element) iter.next();

			install.add(element.attributeValue("program_name"));
			install.add(element.attributeValue("actualization_id"));
		}

		return install;
	}
}
