package updater;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;

import org.dom4j.DocumentException;


/**
 * Class Updater is responsible for management all process of downloading and installing.
 * @author Pawel
 *
 */
public class Updater extends JFrame implements Observer, Runnable {

	private static final long serialVersionUID = 1L;
	//graphics elements
	private final JPanel contentPanel = new JPanel();	//main content Panel.
	private JPanel panel;								
	private JLabel lblconsole;							//in panel show what currently is happening .
	private JLabel lblstats;							//in panel show status of downloading, speed of downloading.
	private JLabel lblResume;							//button which starts downloading if before was paused.
	private JLabel lblPause;							//button which interrupts downloading.
	private JProgressBar progressBar;					//progress bar where is showed process of downloading.

	private final String mb = "MB", kb = "KB", perSec = "/s";	//helpful values to show speed of downloading.
	private final int exitCodeAccessDenied = 5;
	
	/** flag on which depends what part of function run() will be doing.*/
	private Action action;		
	/** flag which say about status of downloading. */
	private Status status;								
	/** xml parser. */
	private XMLParser xmlpars;	
	
	/** update which is being executed. */
	private URLdownload selectedUpdate;		
	/** variable to starts new threads */
	private ExecutorService newSingleThreadExecutor; 
	/** map stores programs, which AutoUpdater supports */
	private Map<String, Program> programs;			
	/** map stores changelogs of programs */
	private Map<String, Map<String, String>> changelogs;//
	/** map gives as paths by environment variable. */
	private Map<String, String> env;					
	/** list of updates which are downloading. */
	private ArrayList<Actualization> updates;
	/** list of servers from where updates are downloading*/
	private ArrayList<String> servers_list;				
	/** list of programs */
	private ArrayList<String> programs_list;			
	/** flag saying that checking updates is in progress*/
	private boolean checkUpdatesInProgress;				
	/** flag saying that checking changelogs is in progress*/
	private boolean checkChangeLogsInProgress;			
	/** flag saying that updating is in progress*/
	private boolean updateInProgress;					
	/** flag saying that updating AutoUpdater is in progress*/
	private boolean updateAutoUpdater;					
	/** address of proxy */
	private String proxy_address;						
	/** port of proxy */
	private int proxy_port;								
	/**path of ElevateHandler.exe, file responsible for administrator rights	 */
	private String elevateHandlerPath;				    
	/**path of Installation.jar, file responsible for installation */
	private String installJarPath;					
	/** path of Checker.jar, file responsible for restart autoupdater after aktualization of autoupdater*/
	private String checkerJarPath;						
	/** path where are xml files.*/
	private String path2xmls;							
	/** path where files are downloading and unzipping*/
	private String temp_path;							
	/**name of xml with list of programs and options */
	private String dataFileName;						
	/** name of jar with main program*/
	private String autoUpdaterClient;					
	/** name of program which is changelog is checking */
	private String changelog_program;					
	private DecimalFormat df;							//helpful to show less digits after decimal.
	
	/**
	 * Constructor of class Updater.
	 */
	public Updater() {
		xmlpars = new XMLParser();
		selectedUpdate = null;
		newSingleThreadExecutor = Executors.newSingleThreadExecutor();

		action = Action.WAIT;
		status = Status.ST_WAIT;

		programs = new HashMap<String, Program>();
		changelogs = new HashMap<String, Map<String, String>>();
		env = System.getenv();

		updates = new ArrayList<Actualization>();
		servers_list = new ArrayList<String>();
		programs_list = new ArrayList<String>();

		checkUpdatesInProgress = false;
		checkChangeLogsInProgress = false;
		updateInProgress = false;
		updateAutoUpdater = false;

		path2xmls = getAppDataEnv() + File.separator + "AutoUpdater";

		temp_path = getTempEnv() + File.separator + "updater";
		dataFileName = "AutoUpdaterData.xml";
		autoUpdaterClient = "AutoUpdater";

		df = new DecimalFormat("#.##");

		setIconImage(Toolkit.getDefaultToolkit().getImage(
				Updater.class.getResource("/images/icon.png")));
		setTitle("Auto Updater - Progress");
		setAlwaysOnTop(true);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (status != Status.ST_PAUSE) {
					showInfo("Update in progress.", "Information");
				} else {
					status = Status.ST_CANCEL;
					selectedUpdate.cancel();

					progressBar.setValue(0);
					lblconsole.setText("");
					lblstats.setText("");
				}
			}
		});
		setResizable(false);
		setBounds(100, 100, 653, 101);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);

		panel = new JPanel();
		panel.setBackground(Color.WHITE);
		panel.setBounds(0, 0, 646, 74);
		contentPanel.add(panel);
		panel.setLayout(null);

		lblconsole = new JLabel("");
		lblconsole.setBounds(11, 8, 599, 14);
		panel.add(lblconsole);

		progressBar = new JProgressBar();
		progressBar.setBounds(10, 25, 600, 19);
		panel.add(progressBar);

		lblstats = new JLabel("");
		lblstats.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblstats.setBounds(11, 46, 599, 20);
		panel.add(lblstats);

		lblPause = new JLabel("");
		lblPause.setIcon(new ImageIcon(Updater.class
				.getResource("/images/pause_1.png")));
		lblPause.setBounds(617, 24, 20, 22);
		lblPause.setToolTipText("Pause");
		lblPause.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				String str = "";
				str += df.format((float) progressBar.getValue() / 1024 / 1024)
						+ " of "
						+ df.format((float) progressBar.getMaximum() / 1024 / 1024)
						+ " MB";

				lblstats.setText("Paused... - " + str);
				selectedUpdate.pause();
				status = Status.ST_PAUSE;
				lblPause.setVisible(false);
				lblResume.setVisible(true);
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
			}

			@Override
			public void mouseClicked(MouseEvent arg0) {
			}
		});
		panel.add(lblPause);

		lblResume = new JLabel("");
		lblResume.setIcon(new ImageIcon(Updater.class
				.getResource("/images/play.png")));
		lblResume.setBounds(616, 24, 20, 22);
		lblResume.setToolTipText("Resume");
		lblResume.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				lblstats.setText("Connecting to server...");
				status = Status.ST_DOWNLOAD;
				lblResume.setVisible(false);
				lblPause.setVisible(true);
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
			}

			@Override
			public void mouseClicked(MouseEvent arg0) {
			}
		});
		panel.add(lblResume);
		lblResume.setVisible(false);

		centerWindow();
	}
	
	/**
	 * Puts window in the middle.
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
	 *  An application calls an Observable object's notifyObservers method 
	 *  to have all the object's observers notified of the change.
	 * @param o 
	 * 			the observable object.
	 * @param arg
	 * 			an argument passed to the notifyObservers method.
	 */
	public void update(Observable o, Object arg) {
		if (arg instanceof Message) {
			Message message = (Message) arg;

			if (message.isConsole()) {
				if (updateInProgress)
					lblconsole.setText(message.getText());
			} else if (message.isProgressbar()) {
				int selection = Integer.parseInt(message.getText());

				if (updateInProgress)
					progressBar.setValue(selection);
			} else if (message.isStatistics()) {
				if (updateInProgress) {
					if (message.getText().isEmpty()) {
						lblstats.setText("");
						return;
					}

					String str = "";
					double speed = Double.parseDouble(message.getText());

					if (progressBar.getMaximum() != -1) {
						double temp_secs = (((float) progressBar.getMaximum() / 1024) - ((float) progressBar
								.getValue() / 1024)) / speed;
						int secs = (int) temp_secs;

						int hr = secs / 3600;
						int min = (secs - (hr * 3600)) / 60;
						int sec = secs - (hr * 3600) - (min * 60);

						str += "Estimated time: " + hr + " hr " + min + " min "
								+ sec + " sec  - ";

						str += df
								.format((float) progressBar.getValue() / 1024 / 1024)
								+ " of "
								+ df.format((float) progressBar.getMaximum() / 1024 / 1024)
								+ " MB";
					} else {
						str += "Estimated time: unknown - ";
						str += df
								.format((float) progressBar.getValue() / 1024 / 1024)
								+ " MB";
					}

					if (speed >= 1024) {
						speed = speed / 1024;
						str += " (" + df.format(speed) + " " + mb + perSec
								+ ") ";
					} else {
						str += " (" + df.format(speed) + " " + kb + perSec
								+ ") ";
					}

					lblstats.setText(str);
				}
			} else if (message.isError()) {
				updateIndexesList(message.getActualization_id());
				showError(message.getText(), "Error");
			}
		}
	}
	
	/**
	 * Starts new thread. Depending on the flags this thread starts checking updates, 
	 * checking changelogs or downloading files.
	 */
	@Override
	public void run() {
		switch (action) {
		case WAIT:
			action = Action.WAIT;
			break;
		case CHECK_UPDATES:
			checkUpdatesThread();
			action = Action.WAIT;
			checkUpdatesInProgress = false;
			break;
		case CHECK_CHANGELOGS:
			checkChangeLogsThread();
			action = Action.WAIT;
			checkChangeLogsInProgress = false;
			break;
		case UPDATE:
			lblResume.setVisible(false);
			lblPause.setVisible(true);
			lblstats.setText("Connecting to server...");
			status = Status.ST_DOWNLOAD;
			updateThread();
			action = Action.WAIT;
			updateInProgress = false;
			break;
		default:
			action = Action.WAIT;
			break;
		}
	}
	
	
	/**
	 * Method which shows frame of downloading.
	 */
	private void open() {
		setVisible(true);
	}

	
	/**
	 * Method which shutdown thread.
	 */
	public void cleanUp() {
		newSingleThreadExecutor.shutdown();
	}

	/**
	 * Method which show error where some exception is catch
	 * @param message
	 * 			what kind of error
	 * @param title
	 * 			title of error
	 */
	private void showError(String message, String title) {
		JOptionPane pane = new JOptionPane(message, JOptionPane.ERROR_MESSAGE);
		JDialog dialog = pane.createDialog(title);
		dialog.setIconImage(Toolkit.getDefaultToolkit().getImage(
				Updater.class.getResource("/images/dialog_error.png")));
		dialog.setAlwaysOnTop(true);
		dialog.setVisible(true);
	}
	
	/**
	 * Method which show some information
	 * @param message 
	 * 				contents of information
	 * @param title 
	 * 				title of message
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
	 * Updates the list of updates by removing actualization.
	 * @param actualization_id
	 * 						id of actualization which will be removed.
	 */
	private void updateIndexesList(String actualization_id) {
		for (int i = 0; i < updates.size(); ++i) {
			if (updates.get(i).getId().equals(actualization_id)) {
				updates.remove(i);
			}
		}
	}
	
	/**
	 * Initializes all options when AutoUpdater is starting. 
	 * @return
	 * @throws IOException Signals that an I/O exception of some sort has occurred. This class is the general class of exceptions produced by failed or interrupted I/O operations.
	 * @throws DocumentException is a nested Exception which may be thrown during the processing of a DOM4J document.
	 * @throws ClassNotFoundException 	Thrown when an application tries to load in a class through its string name using:
										 - The forName method in class Class.
										 - The findSystemClass method in class ClassLoader .
										 - The loadClass method in class ClassLoader.
										but no definition for the class with the specified name could be found.
	 * @throws InstantiationException 	Thrown when an application tries to create an instance of a class using the newInstance method in class Class, 
	 * 									but the specified class object cannot be instantiated. The instantiation can fail for a variety of reasons including but not limited to:
										 - the class object represents an abstract class, an interface, an array class, a primitive type, or void
										 - the class has no nullary constructor
	 * @throws IllegalAccessException	An IllegalAccessException is thrown when an application tries to reflectively create an instance (other than an array), 
	 * 									set or get a field, or invoke a method, but the currently executing 
	 * 									method does not have access to the definition of the specified class, field, method or constructor.
	 * @throws UnsupportedLookAndFeelException An exception that indicates the request look & feel management classes are not present on the user's system.
	 */
	public boolean init() throws IOException, DocumentException,
			ClassNotFoundException, InstantiationException,
			IllegalAccessException, UnsupportedLookAndFeelException {
		UIManager
				.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		SwingUtilities.updateComponentTreeUI(this);

		File file1 = new File(path2xmls);
		if (!file1.exists())
			file1.mkdir();

		File file2 = new File(temp_path);
		if (!file2.exists())
			file2.mkdir();

		try {
			cleanAfterAutoUpdaterUpdate();
		} catch (IOException e) {
			showError("Deleting temporary files failed.", "Error");
		}
		//init AutoUpdaterData.xml
		if (xmlpars.initDataXml(new File(path2xmls + File.separator
				+ dataFileName))) {
			xmlpars.save(new File(path2xmls + File.separator + dataFileName));
		}
		//getting programs from xml
		programs = xmlpars.getPrograms();

		String[] data = new String[2];
		data = xmlpars.getProxy();
		proxy_address = data[0];

		try {
			proxy_port = Integer.parseInt(data[1]);
		} catch (NumberFormatException e) {
			proxy_port = 0;
		}

		int n = 0;
		data = getSystemProxy();
		if (data != null) {
			if (!proxy_address.equals(data[0])) {
				proxy_address = data[0];
			}

			try {
				n = Integer.parseInt(data[1]);
			} catch (NumberFormatException e) {
				n = 0;
			}

			if (proxy_port != n) {
				proxy_port = n;
			}
		}

		data = xmlpars.getInstallationProcessPath();
		elevateHandlerPath = data[0];
		installJarPath = data[1];
		checkerJarPath = data[2];

		if (elevateHandlerPath.isEmpty() || installJarPath.isEmpty()
				|| checkerJarPath.isEmpty()) {
			showError("External processes directories are unknown, "
					+ "you can set it in configuration mode", "Error");
			return false;
		}

		for (Map.Entry<String, Program> entry : programs.entrySet()) {
			if (!servers_list.contains(entry.getValue().getServer())) {
				servers_list.add(entry.getValue().getServer());
			}

			if (xmlpars.initPackageXml(new File(path2xmls + File.separator
					+ entry.getValue().getName() + ".xml"))) {
				xmlpars.save(new File(path2xmls + File.separator
						+ entry.getValue().getName() + ".xml"));
			}

			programs.get(entry.getKey()).setInstalledActualizations(
					xmlpars.getActualizations());
		}

		return true;
	}
	
	/**
	 * Method which is called when we want to check changelog of the program 
	 * @param program_name - name of program which changelog is checking
	 * @return
	 */
	public boolean checkChangeLogs(String program_name) {
		if (action != Action.CHECK_CHANGELOGS) {
			checkChangeLogsInProgress = true;
			action = Action.CHECK_CHANGELOGS;
			this.changelog_program = program_name;
			new Thread(this).start();
			return true;
		} else {
			return false;
		}
	}
	
	
	/**
	 * This method is called when the action is set on CHECK_CHANGELOGS and thread of Updater is started.
	 * From server is downloading xml with changelogs.
	 */
	private void checkChangeLogsThread() {
		Future<?> submit = null;
		XMLParser xmlparser = new XMLParser();
		Program program;
		ArrayList<String> actualizations;

		program = programs.get(changelog_program);

		URLdownload url = new URLdownload(proxy_address, proxy_port,
				program.getServer() + "/update/getpackages", path2xmls
						+ File.separator + "AvailablePackages.xml", null);
		url.setOverride(false);
		url.addObserver(this);

		submit = newSingleThreadExecutor.submit(url);

		try {
			submit.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}

		try {
			if (!xmlparser.parseXml(new File(path2xmls + File.separator
					+ "AvailablePackages.xml"))) {
				showError("File AvailablePackages not found.", "Error");
				cleanTemporaryFiles();
				return;
			}
		} catch (DocumentException e) {
			showError("Parsing file AvailablePackages.xml failed.", "Error");
			cleanTemporaryFiles();
			return;
		}

		actualizations = xmlparser.getActualizationsNames(program.getName());

		for (int k = 0; k < actualizations.size(); k = k + 2) {
			url.setserver_address(program.getServer() + "/update/changelogs/"
					+ actualizations.get(k + 1));
			url.setdest_path(path2xmls + File.separator + "Changelogs.xml");
			url.addObserver(this);

			submit = newSingleThreadExecutor.submit(url);

			try {
				submit.get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}

			try {
				if (!xmlparser.parseXml(new File(path2xmls + File.separator
						+ "Changelogs.xml"))) {
					showError("File Changelogs.xml not found.", "Error");
					continue;
				}
			} catch (DocumentException e) {
				showError("Parsing file Changelogs.xml failed.", "Error");
				continue;
			}

			changelogs.put(actualizations.get(k), xmlparser.getChangeLogs());
		}

		cleanTemporaryFiles();
	}
	
	/**
	 * Method which prepare to check updates of programs setting action on CHECK_UPDATES and start thread.
	 * @return
	 */
	public boolean checkUpdates() {
		if (action != Action.CHECK_UPDATES) {
			checkUpdatesInProgress = true;
			action = Action.CHECK_UPDATES;
			this.programs_list.clear();
			this.programs_list = getProgramsNames();
			new Thread(this).start();
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * This method is called when the action is set on CHECK_UPDATE and thread of Updater is started.
	 * Begin downloading xmls with all updates for program, then is checking which should be install. 
	 */
	private void checkUpdatesThread() {
		Future<?> submit = null;
		XMLParser xmlparser = new XMLParser();
		Program program;
		Actualization actualization;
		ArrayList<Actualization> updates;
		ArrayList<String> actualizations_names = new ArrayList<String>();

		for (String server : servers_list) {
			URLdownload url = new URLdownload(proxy_address, proxy_port, server
					+ "/update/getpackages", path2xmls + File.separator
					+ "AvailablePackages.xml", null);
			url.setOverride(false);
			url.addObserver(this);

			submit = newSingleThreadExecutor.submit(url);

			try {
				submit.get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}

			for (String program_name : programs_list) {
				program = programs.get(program_name);
				program.cleanAvailableActualizations();
				updates = new ArrayList<Actualization>();

				try {
					if (!xmlparser.parseXml(new File(path2xmls + File.separator
							+ "AvailablePackages.xml"))) {
						showError("File AvailablePackages.xml not found.",
								"Error");
						cleanTemporaryFiles();
						return;
					}
				} catch (DocumentException e) {
					showError("Parsing file AvailablePackages.xml failed.",
							"Error");
					cleanTemporaryFiles();
					return;
				}

				actualizations_names = xmlparser.getActualizationsNames(program
						.getName());

				for (int k = 0; k < actualizations_names.size(); k = k + 2) {
					url.setserver_address(program.getServer() + "/update/info/"
							+ actualizations_names.get(k + 1));
					url.setdest_path(path2xmls + File.separator + "info.xml");
					url.addObserver(this);

					submit = newSingleThreadExecutor.submit(url);

					try {
						submit.get();
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (ExecutionException e) {
						e.printStackTrace();
					}

					try {
						if (!xmlparser.parseXml(new File(path2xmls
								+ File.separator + "info.xml"))) {
							showError("File info.xml not found.", "Error");
							continue;
						}
					} catch (DocumentException e) {
						showError("Parsing file info.xml failed.", "Error");
						continue;
					}

					actualization = xmlparser.getActualizationInfo();

					if (!program.isInstalled(actualization)) {
						updates.add(actualization);
						updates.get(updates.size() - 1).setServer(
								program.getServer());
					}
				}

				program.setAvailableActualizations(updates);
			}
		}

		cleanTemporaryFiles();
	}
	
	/**
	 * Method which prepare to updates of programs setting action on UPDATE and start thread. 
	 * @param list - list of actualizations
	 * @return
	 */
	public boolean update(ArrayList<String> list) {
		if (action != Action.UPDATE) {
			updateInProgress = true;
			action = Action.UPDATE;

			this.updates.clear();
			this.selectedUpdate = null;
			Actualization actualization = null;

			if (list.contains(autoUpdaterClient)) {
				updateAutoUpdater = true;
			}

			for (int i = 0; i < list.size(); i = i + 3) {
				actualization = programs.get(list.get(i))
						.getAvailableActualization(list.get(i + 1),
								list.get(i + 2));
				actualization.setProgramName(list.get(i));

				updates.add(actualization);
			}

			open();
			new Thread(this).start();
			return true;
		} else {
			return false;
		}
	}

	/**
	 * This method is called when the action is set on UPDATE and thread of Updater is started.
	 * Begin downloading updates and checking status of downloading. Whene downloading is complete, 
	 * method starts unzipping and installing.
	 */
	private void updateThread() {
		boolean onList = false;
		int installResult = 0;
		Actualization actualization = null;
		ArrayList<String> complete_install = null;

		for (int i = 0; i < updates.size();) {
			while (status == Status.ST_PAUSE) {
				try {
					Thread.sleep(25);
				} catch (InterruptedException e) {
				}
			}

			try {
				if (status != Status.ST_CANCEL) {
					downloadUpdate(updates.get(i));
				}
			} catch (IOException e) {
				updates.remove(i);

				showError(e.getMessage(), "Error");
				continue;
			} catch (DocumentException e) {
				updates.remove(i);

				showError(e.getMessage(), "Error");
				continue;
			}

			if (status == Status.ST_CANCEL) {
				break;
			} else if (status != Status.ST_PAUSE) {
				++i;
			}
		}

		if (status == Status.ST_CANCEL) {
			updateAutoUpdater = false;
			dispose();
			return;
		}

		for (int i = 0; i < updates.size(); ++i) {
			try {
				unzipUpdate(updates.get(i));
			} catch (IOException e) {
				updates.remove(updates.get(i));

				showError(e.getMessage(), "Error");
				continue;
			}
		}

		dispose();

		try {
			installResult = installUpdates();

			if (installResult == exitCodeAccessDenied) {
				showError("The operation was canceled by the user.", "Error");
				return;
			} else if (installResult == -1) {
				updateAutoUpdater = false;
				return;
			}
		} catch (IOException e) {
			showError(e.getMessage(), "Error");
		} catch (DocumentException e) {
			showError(e.getMessage(), "Error");
		} catch (InterruptedException e) {
		}

		try {
			if (!xmlpars.parseXml(new File(temp_path + File.separator
					+ "installation.xml"))) {
				showError("File installation.xml not found.", "Error");
				return;
			}
		} catch (DocumentException e) {
			showError("Parsing file installation.xml failed.", "Error");
			return;
		}

		complete_install = new ArrayList<String>();
		complete_install = xmlpars.getFromInstall();

		for (int i = 0; i < complete_install.size(); i = i + 2) {
			actualization = programs.get(complete_install.get(i))
					.getAvailableActualization(complete_install.get(i + 1));

			try {
				if (!xmlpars.parseXml(new File(path2xmls + File.separator
						+ complete_install.get(i) + ".xml"))) {
					showError("File " + complete_install.get(i)
							+ ".xml not found.", "Error");
					continue;
				} else {
					xmlpars.addActualization(actualization);
					xmlpars.save(new File(path2xmls + File.separator
							+ complete_install.get(i) + ".xml"));
				}
			} catch (IOException e) {
				showError("Saving the file " + complete_install.get(i)
						+ ".xml not found.", "Error");
				continue;
			} catch (DocumentException e) {
				showError("Parsing the file " + complete_install.get(i)
						+ ".xml not found.", "Error");
				continue;
			}

			programs.get(complete_install.get(i)).addInstalledActualization(
					actualization);
			programs.get(complete_install.get(i)).removeAvailableActualization(
					actualization);

			// search for duplicates
			onList = false;

			for (int j = 0; j < i; j = j + 2) {
				if (complete_install.get(i).equals(complete_install.get(j))) {
					onList = true;
					break;
				}
			}

			if (!onList) {
				try {
					runProgram(programs.get(complete_install.get(i)));
				} catch (IOException e) {
					showError("Running the program failed.", "Error");
				}
			}
		}
	}

	/**
	 * This method is called in updateThread(). Downloads currently actualization.
	 * @param actualization - currently actualization which is downloading
	 * @throws IOException - 	Signals that an I/O exception of some sort has occurred. 
	 * 							This class is the general class of exceptions produced by failed or interrupted I/O operations.
	 * @throws DocumentException - DocumentException is a nested Exception which may be thrown during the processing of a DOM4J document.
	 */
	private void downloadUpdate(Actualization actualization)
			throws IOException, DocumentException {
		Future<?> submit = null;
		File file = new File(temp_path + File.separator + actualization.getId()
				+ actualization.getVersion() + ".zip");
		URLdownload url = new URLdownload(proxy_address, proxy_port,
				actualization.getServer() + "/update/download/"
						+ actualization.getDownloadLink(), temp_path
						+ File.separator + actualization.getId()
						+ actualization.getVersion() + ".zip",
				actualization.getId());
		url.setOverride(true);
		url.addObserver(this);
		selectedUpdate = url;
		int max = url.getFileSize();

		if (max != -1 && file.exists()) {
			if (file.length() == max) {
				return;
			}
		}

		progressBar.setIndeterminate(false);
		if (max != -1) {
			progressBar.setMaximum(max);
		} else {
			progressBar.setMaximum(-1);
			progressBar.setIndeterminate(true);
		}

		submit = newSingleThreadExecutor.submit(url);

		try {
			submit.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method start unzipping choosen downloaded actualization.
	 * @param actualization
	 * @throws IOException Signals that an I/O exception of some sort has occurred. This class is the general class of exceptions 
	 * 						produced by failed or interrupted I/O operations.
	 */
	private void unzipUpdate(Actualization actualization) throws IOException {
		Future<?> submit = null;
		UnZip unzip = new UnZip(temp_path + File.separator
				+ actualization.getId() + actualization.getVersion() + ".zip",
				temp_path + File.separator + actualization.getId()
						+ actualization.getVersion(), actualization.getId());

		unzip.addObserver(this);
		int max = unzip.getZipSize();

		progressBar.setIndeterminate(false);
		if (max != -1) {
			progressBar.setMaximum(max);
		} else {
			progressBar.setMaximum(-1);
			progressBar.setIndeterminate(true);
		}

		submit = newSingleThreadExecutor.submit(unzip);

		try {
			submit.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Method starting process of installation. Method called ElevateHandler.exe which is responsible for administrator rights.
	 * @return
	 * @throws IOException 	Signals that an I/O exception of some sort has occurred. This class is the general class of exceptions 
	 * 						produced by failed or interrupted I/O operations.
	 * @throws DocumentException 	DocumentException is a nested Exception which may be thrown during the processing of a DOM4J document.
	 * @throws InterruptedException Thrown when a thread is waiting, sleeping, or otherwise occupied, and the thread is interrupted, either 
	 * 								before or during the activity.
	 */
	private int installUpdates() throws IOException, DocumentException,
			InterruptedException {
		boolean start_instalation = false, needAdministrator = false;
		String dest_path = "";
		String source_path = "";

		File file = new File(Updater.class.getProtectionDomain()
				.getCodeSource().getLocation().getPath());

		String exe_path = file.getAbsolutePath();
		exe_path = exe_path.substring(0, exe_path.lastIndexOf(File.separator));
		exe_path = exe_path.replaceAll("%20", " ");

		xmlpars.initInstallXml(new File(getTempEnv() + File.separator
				+ "updater" + File.separator + "installation.xml"));

		for (int i = 0; i < updates.size(); ++i) {
			start_instalation = true;

			if (needAdministrator(new File(programs.get(
					updates.get(i).getProgramName()).getPath()
					+ File.separator + "temp"))) {
				needAdministrator = true;
			}

			dest_path = programs.get(updates.get(i).getProgramName()).getPath();
			source_path = temp_path + File.separator + updates.get(i).getId()
					+ updates.get(i).getVersion();

			xmlpars.addToInstall(updates.get(i).getProgramName(),
					updates.get(i), source_path, dest_path);
		}

		xmlpars.save(new File(getTempEnv() + File.separator + "updater"
				+ File.separator + "installation.xml"));
		xmlpars.initDataXml(new File(path2xmls + File.separator + dataFileName));

		if (updateAutoUpdater && start_instalation) {
			makeProcessesCopy(exe_path);

			ArrayList<String> cmdArgs = new ArrayList<String>();
			cmdArgs.add("\"" + temp_path + File.separator
					+ "ElevateHandlerCopy.exe" + "\"");
			cmdArgs.add(Boolean.toString(needAdministrator));
			cmdArgs.add("javaw.exe");
			cmdArgs.add("-jar");
			cmdArgs.add("\"" + temp_path + File.separator + "Installation.jar"
					+ "\"");

			ProcessBuilder process = new ProcessBuilder(cmdArgs);
			process.start();

			cmdArgs.clear();
			cmdArgs.add("javaw.exe");
			cmdArgs.add("-jar");
			cmdArgs.add("\"" + temp_path + File.separator + "Checker.jar"
					+ "\"");
			cmdArgs.add("ElevateHandlerCopy.exe");
			cmdArgs.add(exe_path + File.separator + autoUpdaterClient + ".jar");

			ProcessBuilder p = new ProcessBuilder(cmdArgs);
			p.start();

			dispose();
			System.exit(0);
		} else if (start_instalation) {
			ArrayList<String> cmdArgs = new ArrayList<String>();
			cmdArgs.add("\"" + exe_path + File.separator + elevateHandlerPath
					+ "\"");
			cmdArgs.add(Boolean.toString(needAdministrator));
			cmdArgs.add("javaw.exe");
			cmdArgs.add("-jar");
			cmdArgs.add("\"" + exe_path + File.separator + installJarPath
					+ "\"");

			ProcessBuilder p = new ProcessBuilder(cmdArgs);
			Process process = p.start();

			process.waitFor();
			process.destroy();

			return process.exitValue();
		}

		return -1;
	}
	
	/**
	 * Method which check administrator rights are needed.
	 * @param file file by which is checking administrator rights are needed.
	 * @return
	 */
	private boolean needAdministrator(File file) {
		try {
			file.createNewFile();
			file.delete();
			return false;
		} catch (IOException e) {
			return true;
		}
	}
	
	/**
	 * Method which run program after update.
	 * @param program program which will be run after update.
	 * @throws IOException 	Signals that an I/O exception of some sort has occurred. This class is the general class of exceptions 
	 * 						produced by failed or interrupted I/O operations.
	 */
	private void runProgram(Program program) throws IOException {
		if (program.getExe().isEmpty()) {
			return;
		}

		Runtime.getRuntime().exec(
				program.getPath() + File.separator + program.getExe());
	}
	
	/**
	 * Method helpful when AutoUpdaters is updating. This method make copy of AutoUpdaters files when during updating 
	 * other programs AutoUpdater is on list with updated programs. Then all processes are performed by copies.
	 * @param exe_path currently path of AutoUpdater
	 * @throws IOException Signals that an I/O exception of some sort has occurred. This class is the general class of exceptions 
	 * 						produced by failed or interrupted I/O operations.
	 */
	private void makeProcessesCopy(String exe_path) throws IOException {
		copyDirectory(new File(exe_path + File.separator + elevateHandlerPath),
				new File(temp_path + File.separator + "ElevateHandlerCopy.exe"));

		copyDirectory(new File(exe_path + File.separator + installJarPath),
				new File(temp_path + File.separator + "Installation.jar"));

		String installJarLib = installJarPath.substring(0,
				installJarPath.length() - 4)
				+ "_lib";

		copyDirectory(new File(exe_path + File.separator + installJarLib),
				new File(temp_path + File.separator + "Installation_lib"));

		copyDirectory(new File(exe_path + File.separator + checkerJarPath),
				new File(temp_path + File.separator + "Checker.jar"));

		String checkerJarLib = checkerJarPath.substring(0,
				checkerJarPath.length() - 4)
				+ "_lib";

		copyDirectory(new File(exe_path + File.separator + checkerJarLib),
				new File(temp_path + File.separator + "Checker_lib"));
	}
	
	
	/**
	 * This method clean Temp directory from useless files.
	 */
	private void cleanTemporaryFiles() {
		File file1 = new File(path2xmls + File.separator
				+ "AvailablePackages.xml");
		if (file1.exists()) {
			file1.delete();
		}

		File file2 = new File(path2xmls + File.separator + "info.xml");
		if (file2.exists()) {
			file2.delete();
		}

		File file3 = new File(path2xmls + File.separator + "Changelogs.xml");
		if (file3.exists()) {
			file3.delete();
		}
	}
	
	/**
	 * This method clean directory from useless files after update.
	 * @param path path to directory
	 * @throws IOException  Signals that an I/O exception of some sort has occurred. This class is the general class of exceptions 
	 * 						produced by failed or interrupted I/O operations.
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
	 * This method clean directory Temp from useless files after AutoUpdater update.
	 * @throws IOException  Signals that an I/O exception of some sort has occurred. This class is the general class of exceptions 
	 * 						produced by failed or interrupted I/O operations.
	 */
	private void cleanAfterAutoUpdaterUpdate() throws IOException {
		File file1 = new File(getTempEnv() + File.separator + "updater"
				+ File.separator + "ElevateHandlerCopy.exe");
		file1.delete();

		File file2 = new File(getTempEnv() + File.separator + "updater"
				+ File.separator + "Installation.jar");
		file2.delete();

		cleanAfterUpdate(new File(getTempEnv() + File.separator + "updater"
				+ File.separator + "Installation_lib"));

		File file3 = new File(getTempEnv() + File.separator + "updater"
				+ File.separator + "Checker.jar");
		file3.delete();

		cleanAfterUpdate(new File(getTempEnv() + File.separator + "updater"
				+ File.separator + "Checker_lib"));
	}
	
	
	/**
	 * This method copy directory from source location to target location.
	 * @param sourceLocation directory which will be copying.
	 * @param targetLocation directory where files from sourceLocation will be copying.
	 * @throws IOException Signals that an I/O exception of some sort has occurred. This class is the general class of exceptions 
	 * 						produced by failed or interrupted I/O operations.
	 */
	public void copyDirectory(File sourceLocation, File targetLocation)
			throws IOException {

		if (sourceLocation.isDirectory()) {
			if (!targetLocation.exists()) {
				targetLocation.mkdir();
			}

			String[] children = sourceLocation.list();
			for (int i = 0; i < children.length; i++) {
				copyDirectory(new File(sourceLocation, children[i]), new File(
						targetLocation, children[i]));
			}
		} else {
			InputStream in = new FileInputStream(sourceLocation);
			OutputStream out = new FileOutputStream(targetLocation);

			// Copy the bits from instream to outstream
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}

			in.close();
			out.close();
		}
	}
	
	/**
	 * The method add address and port of proxy
	 * @param address address of proxy
	 * @param port port of proxy
	 * @return
	 * @throws DocumentException DocumentException is a nested Exception which may be thrown during the processing of a DOM4J document.
	 * @throws IOException  Signals that an I/O exception of some sort has occurred. This class is the general class of exceptions 
	 * 						produced by failed or interrupted I/O operations.
	 */
	public boolean addProxy(String address, int port) throws DocumentException,
			IOException {
		boolean result;

		proxy_address = address;
		proxy_port = port;

		xmlpars.initDataXml(new File(path2xmls + File.separator + dataFileName));
		result = xmlpars.addProxy(address, port);
		xmlpars.save(new File(path2xmls + File.separator + dataFileName));

		return result;
	}
	
	/**
	 * This method remove Proxy
	 * @return
	 * @throws DocumentException DocumentException is a nested Exception which may be thrown during the processing of a DOM4J document.
	 * @throws IOException Signals that an I/O exception of some sort has occurred. This class is the general class of exceptions 
	 * 						produced by failed or interrupted I/O operations.
	 */
	public boolean removeProxy() throws DocumentException, IOException {
		boolean result;

		proxy_address = "";
		proxy_port = -1;

		xmlpars.initDataXml(new File(path2xmls + File.separator + dataFileName));
		result = xmlpars.removeProxy();
		xmlpars.save(new File(path2xmls + File.separator + dataFileName));

		return result;
	}
	
	/**
	 * This method get default system proxy.
	 * @return data of proxy, port and server of proxy.
	 */
	@SuppressWarnings("rawtypes")
	private String[] getSystemProxy() {
		List l = null;
		String[] data = new String[2];
		data[0] = data[1] = "";

		System.setProperty("java.net.useSystemProxies", "true");

		try {
			l = ProxySelector.getDefault().select(
					new URI("http://www.google.pl"));
		} catch (URISyntaxException e) {
			showError("", "Error");
		}

		if (l != null) {
			for (Iterator iter = l.iterator(); iter.hasNext();) {
				java.net.Proxy proxy = (java.net.Proxy) iter.next();
				InetSocketAddress addr = (InetSocketAddress) proxy.address();
				if (addr == null) {
					data = null;
				} else {
					data[0] = addr.getHostName();
					data[1] = Integer.toString(addr.getPort());
				}
			}
		}

		System.setProperty("java.net.useSystemProxies", "false");

		return data;
	}
	
	
	/**
	 * This method get proccesses which should be closed before start update.
	 * @param actualization_list by this list we can recognize which process should be closed.
	 * @return
	 */
	public ArrayList<String> getProcessesToClose(
			ArrayList<String> actualization_list) {
		ArrayList<String> openProcesses = new ArrayList<String>();

		try {
			String line;
			Process proc = Runtime.getRuntime().exec(
					System.getenv("windir") + "\\system32\\"
							+ "tasklist.exe /fo csv /nh");
			BufferedReader input = new BufferedReader(new InputStreamReader(
					proc.getInputStream()));
			while ((line = input.readLine()) != null) {
				if (!line.trim().equals("")) {
					line = line.substring(1);
					String temp = "";

					for (int i = 0; i < actualization_list.size(); i = i + 3) {
						temp = line.substring(0, line.indexOf("\""));
						if (programs.get(actualization_list.get(i)).getExe()
								.contains(temp)) {
							openProcesses.add(programs.get(
									actualization_list.get(i)).getPath());
							openProcesses.add(temp);
							continue;
						}
					}
				}
			}
			input.close();
		} catch (Exception e) {
			showError(e.getMessage(), "Error");
		}

		return openProcesses;
	}
	/**
	 * This method return names of all programs,
	 * @return
	 */
	public ArrayList<String> getProgramsNames() {
		ArrayList<String> list = new ArrayList<String>();

		for (Map.Entry<String, Program> entry : programs.entrySet()) {
			list.add(entry.getKey());
		}

		return list;
	}
	
	/**
	 * This method get program path.
	 * @param program_name name of program which path is returned.
	 * @return
	 */
	public String getProgramPath(String program_name) {
		return programs.get(program_name).getPath();
	}
	
	/**
	 * This method get available actualization for program.
	 * @param program_name name of program which available actualization is getting.
	 * @return ArrayList<Actualization> list of available actualization.
	 */
	public ArrayList<Actualization> getProgramAvailableActualizations(
			String program_name) {
		return programs.get(program_name).getAvailableActualizations();
	}

	/**
	 * This method returned map with changelogs.
	 * @return Map<String, Map<String, String>> changelogs
	 */
	public Map<String, Map<String, String>> getChangeLogs() {
		return changelogs;
	}
	
	/**
	 * This method get changelog for choosen actualization.
	 * @param actualization_name name of actualization.
	 * @param version version of actualization.
	 * @return String changelog
	 */
	public String getChangeLog(String actualization_name, String version) {
		return changelogs.get(actualization_name).get(version);
	}

	/**
	 * This method return address of proxy.
	 * @return
	 */
	public String getProxyAddress() {
		return proxy_address;
	}

	/**
	 * This method return port of proxy.
	 * @return
	 */
	public int getProxyPort() {
		return proxy_port;
	}

	/**
	 * This method return the path of TEMP directory.
	 * @return
	 */
	private String getTempEnv() {
		return env.get("TEMP");
	}

	/**
	 * This method return the path of LOCALAPPDATA directory.
	 * @return
	 */
	private String getAppDataEnv() {
		return env.get("LOCALAPPDATA");
	}

	/**
	 * This method return the action which is actually in use.
	 * @return
	 */
	public Action getAction() {
		return action;
	}
	
	/**
	 * This method check that checking updates is in progress.
	 * @return
	 */
	public boolean isCheckUpdatesInProgress() {
		return checkUpdatesInProgress;
	}
	
	/**
	 * This method check that checking chengelogs is in progress.
	 * @return
	 */
	public boolean isCheckChangeLogsInProgress() {
		return checkChangeLogsInProgress;
	}
	
	/**
	 * This method check that updating is in progress.
	 * @return
	 */
	public boolean isUpdateInProgress() {
		return updateInProgress;
	}
	
	/**
	 * This method check that updating of AutoUpdater is in progress.
	 * @return
	 */
	public boolean isUpdateAutoUpdater() {
		return updateAutoUpdater;
	}
}
