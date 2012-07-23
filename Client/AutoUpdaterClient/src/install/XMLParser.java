package install;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

/** Handles operations on xml files */
public class XMLParser {

	private Document document;
	private SAXReader reader;

	/**
	 * Constructor
	 */
	public XMLParser() {
		reader = new SAXReader();
	}

	/**
	 * Parses the specified file to xml document
	 * 
	 * @param file
	 *            file to parse
	 * @return true if file exists, false if file does not exist
	 * @throws DocumentException
	 *             if an error occurs during parsing
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
	 * Parses the specified file to xml document
	 * 
	 * @param file
	 *            file to parse
	 * @throws DocumentException
	 *             if an error occurs during parsing
	 */
	private void parse(File file) throws DocumentException {
		document = reader.read(file);
	}

	/**
	 * Gets list of actualizations to installation
	 * 
	 * @return list of actualizations to installation
	 */
	public ArrayList<Actualization> getActualizations() {
		ArrayList<Actualization> actualizations = new ArrayList<Actualization>();
		// get list of actualizations
		List<? extends Node> list = document.selectNodes("/List/Actualization");

		// iterate over list
		for (Iterator<? extends Node> iter = list.iterator(); iter.hasNext();) {
			Element element = (Element) iter.next();

			// add actualization to list
			actualizations.add(new Actualization(element
					.attributeValue("actualization_id"), element
					.attributeValue("actualization_name"), element
					.attributeValue("server"), element
					.attributeValue("version"), element.attributeValue("type"),
					element.attributeValue("download_link"), element
							.attributeValue("program_name"), element
							.attributeValue("source"), element
							.attributeValue("destination")));
		}

		return actualizations;
	}

	/**
	 * Removes actualization from xml document
	 * 
	 * @param actualization_id
	 *            actualization id
	 */
	public void removeActualization(String actualization_id) {
		// get list of actualization
		List<? extends Node> list = document.selectNodes("/List/Actualization");

		// iterate over list
		for (Iterator<? extends Node> iter = list.iterator(); iter.hasNext();) {
			Element element = (Element) iter.next();

			// check actualization id
			if (element.attributeValue("actualization_id").equals(
					actualization_id)) {

				// remove
				element.detach();
				return;
			}
		}
	}

	/**
	 * Adds actualization to xml document
	 * 
	 * @param actualization
	 *            actualization to add
	 */
	public void addActualization(Actualization actualization) {
		// get list of servers
		List<? extends Node> list = document
				.selectNodes("/Actualizations/Server");
		// get root element
		Element actualizations = document.getRootElement();
		boolean onList = false;

		// iterate over list of servers
		for (Iterator<? extends Node> iter = list.iterator(); iter.hasNext();) {
			Element element = (Element) iter.next();

			// if address is the same
			if (element.attributeValue("address").equals(
					actualization.getServer())) {
				// iterate over list of actualization
				for (Iterator<? extends Node> i = element.elementIterator(); i
						.hasNext();) {
					Element child = (Element) i.next();

					// if id is the same the update actualization
					if (child.attributeValue("id")
							.equals(actualization.getId())) {
						child.attribute("version").setValue(
								actualization.getVersion());
						child.attribute("download_link").setValue(
								actualization.getDownload_link());

						onList = true;
					}
				}

				// add new actualization
				if (!onList) {
					element.addElement("Actualization")
							.addAttribute("name", actualization.getName())
							.addAttribute("version", actualization.getVersion())
							.addAttribute("type", actualization.getType())
							.addAttribute("id", actualization.getId())
							.addAttribute("download_link",
									actualization.getDownload_link());
				}

				return;
			}
		}

		// add new server with actualization
		actualizations
				.addElement("Server")
				.addAttribute("address", actualization.getServer())
				.addElement("Actualization")
				.addAttribute("name", actualization.getName())
				.addAttribute("version", actualization.getVersion())
				.addAttribute("type", actualization.getType())
				.addAttribute("id", actualization.getId())
				.addAttribute("download_link", actualization.getDownload_link());

		return;
	}

	/**
	 * Saves current xml document to specified file
	 * 
	 * @param file
	 *            file to save
	 * @throws IOException
	 *             if an I/O error occur
	 */
	public void save(File file) throws IOException {
		XMLWriter writer = new XMLWriter(new FileWriter(file),
				OutputFormat.createPrettyPrint());
		writer.write(document);
		writer.close();
	}
}
