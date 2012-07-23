package install;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.EmptyBorder;

import org.dom4j.DocumentException;

import updater.Updater;

/** Installation window */
public class InstallGui extends JFrame implements Observer, Runnable {

	private static final long serialVersionUID = 1L;
	/** grraphic elements */
	private final JPanel contentPanel = new JPanel();
	private JPanel panel;
	private JLabel lblconsole;
	private JProgressBar progressBar;
	/** handles files operation */
	private InstallAndBackUp install;
	/** handles xml operation */
	private XMLParser xmlparser;

	private Map<String, String> env;
	/** list of actualization */
	private ArrayList<Actualization> updates;
	/** list of indexes of uncompleted actualizations */
	private ArrayList<Integer> indexes_list;
	/** path to xml files */
	private String path2xmls;
	/** AutoUpdater name */
	private String autoUpdaterClient;
	/** help varaible */
	private Thread install_thread;

	public InstallGui() {
		env = System.getenv();
		updates = new ArrayList<Actualization>();
		indexes_list = new ArrayList<Integer>();

		path2xmls = getAppDataEnv() + File.separator + "AutoUpdater";
		autoUpdaterClient = "AutoUpdater";

		install = new InstallAndBackUp();
		install.addObserver(this);
		xmlparser = new XMLParser();

		setIconImage(Toolkit.getDefaultToolkit().getImage(
				InstallGui.class.getResource("/images/icon.png")));
		setTitle("Auto Updater - Installation");
		setAlwaysOnTop(true);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				showInfo("Installation in progress.", "Information");
			}
		});
		setResizable(false);
		setBounds(100, 100, 611, 94);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);

		panel = new JPanel();
		panel.setBackground(Color.WHITE);
		panel.setBounds(0, 0, 605, 68);
		contentPanel.add(panel);
		panel.setLayout(null);

		lblconsole = new JLabel("");
		lblconsole.setBounds(10, 8, 585, 14);
		panel.add(lblconsole);

		progressBar = new JProgressBar();
		progressBar.setBounds(9, 25, 550, 14);
		panel.add(progressBar);

		centerWindow();
	}

	/**
	 * Sets location of the window to center of the screen
	 */
	private void centerWindow() {
		double screenWidth = Toolkit.getDefaultToolkit().getScreenSize()
				.getWidth();
		double screenHeight = Toolkit.getDefaultToolkit().getScreenSize()
				.getHeight();

		setLocation((int) (screenWidth / 2) - (getBounds().width / 2),
				(int) (screenHeight / 2) - (getBounds().height / 2));
	}

	/**
	 * This method is called whenever the observed object is changed.
	 * 
	 * o - the observable object. arg - an argument passed to the
	 * notifyObservers method.
	 */
	@Override
	public void update(Observable o, Object arg) {
		if (arg instanceof Message) {
			Message message = (Message) arg;

			// set new message to console label
			if (message.isConsole()) {
				if (lblconsole != null)
					lblconsole.setText(message.getText());
				// sets new value to progress bar
			} else if (message.isProgressbar()) {
				int selection = Integer.parseInt(message.getText());

				if (progressBar != null)
					progressBar.setValue(selection);
				// show error message
			} else if (message.isError()) {
				updateIndexesList(message.getActualization_id());
				showError(message.getText(), "Error");
			}
		}
	}

	/**
	 * Begins installation process and dispose resource
	 */
	@Override
	public void run() {
		installation();
		dispose();
		System.exit(0);
	}

	/**
	 * Displays installation window and starts installation
	 */
	public void open() {
		setVisible(true);
		new Thread(this).start();
	}

	/**
	 * Displays window with error message
	 * 
	 * @param message
	 *            error message content
	 * @param title
	 *            window title
	 */
	private void showError(String message, String title) {
		JOptionPane pane = new JOptionPane(message, JOptionPane.ERROR_MESSAGE);
		JDialog dialog = pane.createDialog(title);
		dialog.setIconImage(Toolkit.getDefaultToolkit().getImage(
				Updater.class.getResource("/images/dialog_error.png")));
		dialog.setAlwaysOnTop(true);
		dialog.setVisible(true);

		dialog.dispose();
	}

	/**
	 * Displays window with information message
	 * 
	 * @param message
	 *            information message content
	 * @param title
	 *            window title
	 */
	private void showInfo(String message, String title) {
		JOptionPane pane = new JOptionPane(message,
				JOptionPane.INFORMATION_MESSAGE);
		JDialog dialog = pane.createDialog(title);
		dialog.setIconImage(Toolkit.getDefaultToolkit().getImage(
				Updater.class.getResource("/images/dialog_info.png")));
		dialog.setAlwaysOnTop(true);
		dialog.setVisible(true);
	}

	/**
	 * Adds index of uncompleted actualization to list of uncompleted
	 * actualizations
	 * 
	 * @param actualization_id
	 */
	private void updateIndexesList(String actualization_id) {
		for (int i = 0; i < updates.size(); ++i) {
			if (updates.get(i).getId().equals(actualization_id)) {
				indexes_list.add(i);
			}
		}
	}

	/**
	 * Initializes - gets list of actualization to install
	 * 
	 * @throws DocumentException
	 *             if an error occurs during parsing
	 */
	public void init() throws DocumentException {
		if (!xmlparser.parseXml(new File(getTempEnv() + File.separator
				+ "updater" + File.separator + "installation.xml"))) {
			showError("File installation.xml not found.", "Error");
			return;
		}

		updates = xmlparser.getActualizations();
	}

	/**
	 * Installation process
	 */
	private void installation() {
		int max = -1;

		// iterate over all actualizations
		for (int i = 0; i < updates.size(); ++i) {
			// get number of files in source directory
			max = install.getFileNumber(updates.get(i).getSource_path());

			// if error then remove actualization from list and continue
			if (max == -1) {
				xmlparser.removeActualization(updates.get(i).getId());
				updates.remove(i);
				install_thread = null;
				continue;
			}

			// preparation for copying files
			install.setActualization_id(updates.get(i).getId());
			install.setSource_path(updates.get(i).getSource_path());
			install.setDest_path(updates.get(i).getDest_path());

			install.setBackup_path(getTempEnv() + File.separator + "updater"
					+ File.separator + updates.get(i).getId()
					+ updates.get(i).getVersion() + "_backup");

			// set progress bar maximum value
			progressBar.setMaximum(max);

			// start new thread which copies the files and wait for it
			install_thread = new Thread(install);
			install_thread.start();

			while (install_thread.isAlive()) {
				try {
					Thread.sleep(250);
				} catch (InterruptedException e) {
				}
			}

			install_thread = null;
		}

		// iterate over all uncompleted actualization, try to copy back up files
		// and remove actualization from updates list
		int k = 0;
		for (int i = 0; i < indexes_list.size(); ++i) {
			// get index of actualization in updates list
			k = indexes_list.get(i);
			// get number of files in source directory
			max = install.getFileNumber(getTempEnv() + File.separator
					+ "updater" + File.separator + updates.get(k).getId()
					+ updates.get(k).getVersion() + "_backup");
			// if error then remove actualization from list and continue
			if (max == -1) {
				xmlparser.removeActualization(updates.get(k).getId());
				updates.remove(k);
				install_thread = null;
				continue;
			}
			// preparation for copying files
			install.setActualization_id(updates.get(k).getId());
			install.setSource_path(getTempEnv() + File.separator + "updater"
					+ File.separator + updates.get(k).getId()
					+ updates.get(k).getVersion() + "_backup");
			install.setDest_path(updates.get(k).getDest_path());
			install.setBackup_path(null);
			// set progress bar maximum value
			progressBar.setMaximum(max);

			install_thread = new Thread(install);
			install_thread.start();
			// start new thread which copies the files and wait for it
			while (install_thread.isAlive()) {
				try {
					Thread.sleep(250);
				} catch (InterruptedException e) {
				}
			}
			// remove actualization from list and xml
			xmlparser.removeActualization(updates.get(k).getId());
			updates.remove(k);
			install_thread = null;
		}
		// save installation xml - it contains right now only complete
		// actualizations
		try {
			xmlparser.save(new File(getTempEnv() + File.separator + "updater"
					+ File.separator + "installation.xml"));
		} catch (IOException e) {
			showError("Save the file installation.xml failed.", "Error");
		}

		// check if AutoUpdater was updated
		boolean updateAutoUpdater = false;
		for (int i = 0; i < updates.size(); ++i) {
			if (updates.get(i).getProgramName().equals(autoUpdaterClient)) {
				updateAutoUpdater = true;
			}
		}

		// if AutoUpdater was updated then all changes in the XML files you need
		// to do now
		if (updateAutoUpdater) {
			// iterate over complete actualizations
			for (int i = 0; i < updates.size(); ++i) {
				// try to parse file with list of installed actualization
				try {
					if (!xmlparser.parseXml(new File(path2xmls + File.separator
							+ updates.get(i).getProgramName() + ".xml"))) {
						showError("File " + updates.get(i).getProgramName()
								+ ".xml not found.", "Error");
						continue;
					} else {
						// add new actualization and save file
						xmlparser.addActualization(updates.get(i));
						xmlparser.save(new File(path2xmls + File.separator
								+ updates.get(i).getProgramName() + ".xml"));
					}
				} catch (IOException e) {
					showError("Saving the file "
							+ updates.get(i).getProgramName()
							+ ".xml not found.", "Error");
					continue;
				} catch (DocumentException e) {
					showError("Parsing the file "
							+ updates.get(i).getProgramName()
							+ ".xml not found.", "Error");
					continue;
				}

				// try to remove all temporary files
				try {
					cleanAfterUpdate(new File(getTempEnv() + File.separator
							+ "updater"),
							updates.get(i).getId()
									+ updates.get(i).getVersion());
				} catch (IOException e) {
					showError("Deleting temporary files failed.", "Error");
				}
			}
		} else {
			// iterate over complete actualizations
			for (int i = 0; i < updates.size(); ++i) {
				// try to remove all temporary files
				try {
					cleanAfterUpdate(new File(getTempEnv() + File.separator
							+ "updater"),
							updates.get(i).getId()
									+ updates.get(i).getVersion());
				} catch (IOException e) {
					showError("Deleting temporary files failed.", "Error");
				}
			}
		}
	}

	/**
	 * Removes specified path
	 * 
	 * @param path
	 *            path to remove
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	private void cleanAfterUpdate(File path) throws IOException {
		if (path.exists()) {
			File[] files = path.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					cleanAfterUpdate(files[i]);
					files[i].delete();
				} else {
					files[i].delete();
				}
			}

			path.delete();
		}
	}

	/**
	 * Removes in specified path all files with specified name, used after
	 * installation
	 * 
	 * @param path
	 *            path to directory
	 * @param file_name
	 *            file name to remove
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	private void cleanAfterUpdate(File path, String file_name)
			throws IOException {
		if (path.exists()) {
			File[] files = path.listFiles();
			for (int i = 0; i < files.length; i++) {
				// if file is directory wit specified name or directory with
				// specified name and "_backup" then remove it
				if (files[i].isDirectory()) {
					if (files[i].getName().equals(file_name)
							|| files[i].getName().equals(file_name + "_backup")) {
						cleanAfterUpdate(new File(path + File.separator
								+ files[i].getName()));
					}
				} else {
					if (files[i].getName().equals(file_name + ".zip")) {
						files[i].delete();
					}
				}
			}
		}
	}

	/**
	 * Gets environmental variable Temp
	 * 
	 * @return enviromental variable Temp
	 */
	private String getTempEnv() {
		return env.get("TEMP");
	}

	/**
	 * Gets environmental variable Local AppData
	 * 
	 * @return enviromental variable Local AppData
	 */
	private String getAppDataEnv() {
		return env.get("LOCALAPPDATA");
	}
}
