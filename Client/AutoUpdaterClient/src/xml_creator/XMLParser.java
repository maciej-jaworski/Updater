package xml_creator;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

/** Handles operations on xml files */
public class XMLParser {

	private Document document;
	private SAXReader reader;
	private ByteArrayOutputStream os;
	private PrintStream ps;
	private PrintStream original;

	/**
	 * Constructor
	 */
	public XMLParser() {
		reader = new SAXReader();
		os = new ByteArrayOutputStream();
		original = new PrintStream(System.out);
		ps = new PrintStream(os);
	}

	/**
	 * Creates new xml data document - main xml for AutoUpdater
	 */
	public void createDataXml() {
		document = DocumentHelper.createDocument();
		Element root = document.addElement("Data");

		root.addElement("Programs");
		root.addElement("Options");
	}

	/**
	 * Parses the specified file to xml document
	 * 
	 * @param file
	 *            file to parse
	 * @throws DocumentException
	 *             if an error occurs during parsing
	 */
	public void parse(File file) throws DocumentException {
		document = reader.read(file);
	}

	/**
	 * Gets xml content
	 * 
	 * @return xml content
	 * @throws UnsupportedEncodingException
	 *             the character encoding is not supported
	 * @throws IOException
	 *             if an I/O error occur
	 */
	public String getText() throws UnsupportedEncodingException, IOException {
		System.setOut(ps);
		XMLWriter w = new XMLWriter(OutputFormat.createPrettyPrint());
		w.write(document);
		String output = os.toString("UTF8");
		os.reset();
		System.setOut(original);
		return output;
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

	/**
	 * Adds program to xml document
	 * 
	 * @param name
	 *            program name
	 * @param path
	 *            program directory
	 * @param exe
	 *            program executable file
	 * @param server
	 *            server address which contains information about actualizations
	 * @return true if program is already in xml document, false otherwise
	 */
	public boolean addProgram(String name, String path, String exe,
			String server) {
		// get list of programs from document
		List<? extends Node> list = document
				.selectNodes("/Data/Programs/Program/@name");
		// get root element
		Element programs = document.getRootElement().element("Programs");
		boolean onList = false;

		// iterate over list
		for (Iterator<? extends Node> iter = list.iterator(); iter.hasNext();) {
			Attribute attribute = (Attribute) iter.next();
			// if the program name is the same the set onList to true and break;
			if (attribute.getValue().equals(name)) {
				onList = true;
				break;
			}
		}

		// if the program was not on the list then adds it
		if (!onList) {
			programs.addElement("Program").addAttribute("name", name)
					.addAttribute("path", path).addAttribute("exe", exe)
					.addAttribute("server", server);
		}

		return !onList;
	}

	/**
	 * Removes program from xml document
	 * 
	 * @param name
	 *            program name
	 * @return true if program was removed, false otherwise
	 */
	public boolean removeProgram(String name) {
		// get list of porgrams from document
		List<? extends Node> list = document
				.selectNodes("/Data/Programs/Program/@name");
		boolean onList = false;

		// iterate over list
		for (Iterator<? extends Node> iter = list.iterator(); iter.hasNext();) {
			Attribute attribute = (Attribute) iter.next();
			// if the program name is the same then removes it and break
			if (attribute.getValue().equals(name)) {
				attribute.getParent().detach();
				onList = true;
				break;
			}
		}

		return onList;
	}

	/**
	 * Updates program from xml document
	 * 
	 * @param old_name
	 *            old program name
	 * @param new_name
	 *            new program name
	 * @param new_path
	 *            new program directory
	 * @param new_exe
	 *            new program exectuable file
	 * @param new_server
	 *            new server address which contains information about
	 *            actualizations
	 * @return true if program was updated and false otherwise
	 */
	public boolean updateProgram(String old_name, String new_name,
			String new_path, String new_exe, String new_server) {
		// get list of porgrams from document
		List<? extends Node> list = document
				.selectNodes("/Data/Programs/Program/@name");
		boolean onList = false;

		// iterate over list
		for (Iterator<? extends Node> iter = list.iterator(); iter.hasNext();) {
			Attribute attribute = (Attribute) iter.next();
			// if the program name is the same then updates the program and
			// break;
			if (attribute.getValue().equals(old_name)) {
				attribute.getParent().attribute("name").setValue(new_name);
				attribute.getParent().attribute("path").setValue(new_path);
				attribute.getParent().attribute("exe").setValue(new_exe);
				attribute.getParent().attribute("server").setValue(new_server);
				onList = true;
				break;
			}
		}

		return onList;
	}

	/**
	 * Adds paths to external processes
	 * 
	 * @param EH
	 *            ElevateHandler, C# process which runs other process as
	 *            administrator
	 * @param install
	 *            java process which copies files
	 * @param checker
	 *            java process which checks if install is over
	 * @return true if processes was updated and false if added
	 */
	public boolean addProcesses(String EH, String install, String checker) {
		// get list of options
		List<? extends Node> list = document
				.selectNodes("/Data/Options/Option/@name");
		// get root element
		Element options = document.getRootElement().element("Options");
		boolean onList = false;

		// iterate over list
		for (Iterator<? extends Node> iter = list.iterator(); iter.hasNext();) {
			Attribute attribute = (Attribute) iter.next();

			// if option name is InstallationData then update paths
			if (attribute.getValue().equals("InstallationData")) {
				attribute.getParent().attribute("ElevateHandler").setValue(EH);
				attribute.getParent().attribute("Installation")
						.setValue(install);
				attribute.getParent().attribute("Checker").setValue(checker);

				onList = true;
				break;
			}
		}

		// if option was not on the list then adds it
		if (!onList) {
			options.addElement("Option")
					.addAttribute("name", "InstallationData")
					.addAttribute("ElevateHandler", EH)
					.addAttribute("Installation", install)
					.addAttribute("Checker", checker);
		}

		return true;
	}
}
