package xml_creator;

import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.DocumentException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;

public class Gui extends Shell {
	// graphic elements
	private Composite mainPanel;
	private StyledText output;
	private Menu popupMenu;
	private MenuItem createEmptyXmlItem;
	private MenuItem loadXmlItem;
	private MenuItem addProgramItem;
	private MenuItem editProgramItem;
	private MenuItem removeProgramItem;
	private MenuItem addInstallationItem;
	private MenuItem generateItem;
	private MenuItem infoItem;
	private MenuItem closeItem;
	/** map for environmental varaibles */
	private Map<String, String> env;
	/** path to xml files and name for data xml file */
	private String path2xmls, dataFileName;
	/** variables for programs parameters */
	private String program_name, program_path, program_server, program_exe;
	/** variables for external processes paths */
	private String program_EH, program_install, program_checker;
	/** max line width */
	private int lineWidth;
	/** max line count */
	private int maxLineCount;
	/** current selected line number */
	private int line_number;
	/** resolution width and height */
	private double screenWidth;
	private double screenHeight;
	/** variable for remember if there are any unsaved changes */
	private boolean saved;
	/** variables for regular expresions */
	private Pattern pattern_program, pattern_processes;
	private Matcher matcher;
	/** handles xml operation */
	private XMLParser xml;

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			Display display = Display.getDefault();
			Gui shell = new Gui(display);
			shell.open();
			shell.layout();
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the shell.
	 * 
	 * @param display
	 */
	public Gui(Display display) {
		super(display, SWT.CLOSE | SWT.MIN | SWT.TITLE);

		xml = new XMLParser();

		// get resolution width and height
		screenWidth = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		screenHeight = Toolkit.getDefaultToolkit().getScreenSize().getHeight();

		// regular expresion for program
		pattern_program = Pattern
				.compile("<Program name=\"(.+?)\" path=\"(.+?)\" "
						+ "exe=\"(.*?)\" server=\"(.+?)\"/>");
		// regular expresion for processes
		pattern_processes = Pattern
				.compile("<Option name=\"(.+?)\" ElevateHandler=\"(.+?)\" "
						+ "Installation=\"(.+?)\" Checker=\"(.+?)\"/>");

		// get system environmental varaibles
		env = System.getenv();
		// set path to xml files
		path2xmls = getAppDataEnv() + File.separator + "AutoUpdater";
		// set name for main xml data file
		dataFileName = "AutoUpdaterData.xml";

		saved = false;
		line_number = 0;

		program_name = "";
		program_path = "";
		program_server = "";
		program_exe = "";
		program_install = "";
		program_EH = "";
		program_checker = "";

		createContents();
		centerWindow();

		// get maximum line width and maximum line count
		GC gc = new GC(output);
		FontMetrics fm = gc.getFontMetrics();
		int charWidth = fm.getAverageCharWidth();
		gc.dispose();

		lineWidth = (output.getSize().x / charWidth) + 5;
		maxLineCount = output.getSize().y / output.getLineHeight();

		setVerticalScrollBar();
		setHorizontalScrollBar();
	}

	/**
	 * Create contents of the shell.
	 */
	protected void createContents() {
		setText("XML Creator");
		setSize(700, 430);

		addListener(SWT.Close, new Listener() {
			public void handleEvent(Event event) {
				// check if there are some unsaved changes and display window
				if (!output.getText().isEmpty() && !saved) {
					MessageBox messageBox = new MessageBox(getShell(),
							SWT.APPLICATION_MODAL | SWT.YES | SWT.NO
									| SWT.ICON_QUESTION);
					messageBox.setText("Information");
					messageBox.setMessage("Close application without saving ?");
					event.doit = messageBox.open() == SWT.YES;
				}
			}
		});

		setImage(SWTResourceManager.getImage(Gui.class, "/images/xc_icon.png"));

		mainPanel = new Composite(this, SWT.NONE);
		mainPanel.setBounds(0, 0, 694, 402);
		mainPanel.setFocus();

		output = new StyledText(mainPanel, SWT.BORDER | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.SHADOW_OUT);
		output.addMouseListener(new MouseAdapter() {
			public void mouseUp(MouseEvent arg0) {
				// if there is any text displayed
				if (!output.getText().isEmpty()) {
					// if selected line is
					if (line_number < output.getLineCount()) {
						output.setLineBackground(line_number, 1, Display
								.getCurrent().getSystemColor(SWT.COLOR_WHITE));
					}

					// get selected line
					line_number = arg0.y / output.getLineHeight();

					// if there is a text
					if (line_number < output.getLineCount()) {
						// highlight
						output.setLineBackground(
								line_number,
								1,
								Display.getCurrent().getSystemColor(
										SWT.COLOR_LIST_SELECTION));
						// prepare regular expresion on Program
						matcher = pattern_program.matcher(output
								.getLine(line_number));

						// if selected line is line with program data then get
						// parameters
						// and enable buttons
						if (matcher.find()) {
							program_name = matcher.group(1);
							program_path = matcher.group(2);
							program_server = matcher.group(4);
							program_exe = matcher.group(3);

							editProgramItem.setEnabled(true);
							removeProgramItem.setEnabled(true);
						} else {
							editProgramItem.setEnabled(false);
							removeProgramItem.setEnabled(false);
						}

						// prepare regular expresion on Processes
						matcher = pattern_processes.matcher(output
								.getLine(line_number));

						// if selected line is line with prcesses data then get
						// parameters
						if (matcher.find()) {
							program_EH = matcher.group(2);
							program_install = matcher.group(3);
							program_checker = matcher.group(4);
						} else {
							program_EH = "";
							program_install = "";
							program_checker = "";
						}
					}
				}
			}
		});

		// hide scroll bars
		output.getVerticalBar().setVisible(false);
		output.getHorizontalBar().setVisible(false);
		output.setBounds(10, 10, 674, 382);
		output.setEditable(false);

		popupMenu = new Menu(output);

		createEmptyXmlItem = new MenuItem(popupMenu, SWT.NONE);
		createEmptyXmlItem.setImage(SWTResourceManager.getImage(Gui.class,
				"/images/xc_new.png"));
		createEmptyXmlItem.setText("Create an empty xml");
		createEmptyXmlItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				// if there is any open unsaved document then show warning else
				// create new document
				if (!output.getText().isEmpty() && !saved) {
					MessageBox messageBox = new MessageBox(getShell(),
							SWT.APPLICATION_MODAL | SWT.YES | SWT.NO
									| SWT.ICON_QUESTION);
					messageBox.setText("Information");
					messageBox
							.setMessage("All unsaved changes will be lost. Are you sure you want to create a new xml ?");
					if (messageBox.open() == SWT.YES) {
						xml.createDataXml();
						updateOutput();

						addProgramItem.setEnabled(true);
						editProgramItem.setEnabled(false);
						removeProgramItem.setEnabled(false);
						generateItem.setEnabled(true);
						addInstallationItem.setEnabled(true);

						setVerticalScrollBar();
						setHorizontalScrollBar();
						saved = false;
					}
				} else {
					xml.createDataXml();
					updateOutput();

					addProgramItem.setEnabled(true);
					editProgramItem.setEnabled(false);
					removeProgramItem.setEnabled(false);
					generateItem.setEnabled(true);
					addInstallationItem.setEnabled(true);

					setVerticalScrollBar();
					setHorizontalScrollBar();
					saved = false;
				}
			}
		});

		loadXmlItem = new MenuItem(popupMenu, SWT.NONE);
		loadXmlItem.setImage(SWTResourceManager.getImage(Gui.class,
				"/images/xc_load.png"));
		loadXmlItem.setText("Load an existing xml");
		loadXmlItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				// if there is any open unsaved document then show warning else
				// try to load existing file
				if (!output.getText().isEmpty() && !saved) {
					MessageBox messageBox = new MessageBox(getShell(),
							SWT.APPLICATION_MODAL | SWT.YES | SWT.NO
									| SWT.ICON_QUESTION);
					messageBox.setText("Information");
					messageBox
							.setMessage("All unsaved changes will be lost. Are you sure you want to load an existing xml ?");
					if (messageBox.open() == SWT.YES) {
						try {
							xml.parse(new File(path2xmls + File.separator
									+ dataFileName));
							updateOutput();

							addProgramItem.setEnabled(true);
							editProgramItem.setEnabled(false);
							removeProgramItem.setEnabled(false);
							generateItem.setEnabled(true);
							addInstallationItem.setEnabled(true);

							setVerticalScrollBar();
							setHorizontalScrollBar();
							saved = true;
						} catch (DocumentException e) {
							showInfo("File not found to be loaded",
									"Information");
							return;
						}
					}
				} else {
					try {
						xml.parse(new File(path2xmls + File.separator
								+ dataFileName));
						updateOutput();

						addProgramItem.setEnabled(true);
						editProgramItem.setEnabled(false);
						removeProgramItem.setEnabled(false);
						generateItem.setEnabled(true);
						addInstallationItem.setEnabled(true);

						setVerticalScrollBar();
						setHorizontalScrollBar();
						saved = true;
					} catch (DocumentException e) {
						showInfo("File not found to be loaded", "Information");
						return;
					}
				}
			}
		});

		new MenuItem(popupMenu, SWT.SEPARATOR);

		addProgramItem = new MenuItem(popupMenu, SWT.NONE);
		addProgramItem.setEnabled(false);
		addProgramItem.setImage(SWTResourceManager.getImage(Gui.class,
				"/images/xc_add.png"));
		addProgramItem.setText("Add Program");
		addProgramItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				// add program
				if (!addProgram()) {
					showError("Adding a program has failed.", "Error");
					return;
				}

				setVerticalScrollBar();
				setHorizontalScrollBar();
				saved = false;
			}
		});

		editProgramItem = new MenuItem(popupMenu, SWT.NONE);
		editProgramItem.setImage(SWTResourceManager.getImage(Gui.class,
				"/images/xc_edit.png"));
		editProgramItem.setEnabled(false);
		editProgramItem.setText("Edit Program");
		editProgramItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				// edit program
				if (!editProgram()) {
					showError("Editing a program has failed.", "Error");
					return;
				}

				setVerticalScrollBar();
				setHorizontalScrollBar();
				saved = false;
			}
		});

		removeProgramItem = new MenuItem(popupMenu, SWT.NONE);
		removeProgramItem.setImage(SWTResourceManager.getImage(Gui.class,
				"/images/xc_remove.png"));
		removeProgramItem.setEnabled(false);
		removeProgramItem.setText("Remove Program");
		removeProgramItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				// remove program
				if (!removeProgram()) {
					showError("Removing a program has failed.", "Error");
					return;
				}

				setVerticalScrollBar();
				setHorizontalScrollBar();
				saved = false;
			}
		});

		new MenuItem(popupMenu, SWT.SEPARATOR);

		addInstallationItem = new MenuItem(popupMenu, SWT.NONE);
		addInstallationItem.setEnabled(false);
		addInstallationItem.setImage(SWTResourceManager.getImage(Gui.class,
				"/images/xc_option.png"));
		addInstallationItem.setText("Add external processes locations.");
		addInstallationItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				// add external processes
				addProcesses();

				setVerticalScrollBar();
				setHorizontalScrollBar();
				saved = false;
			}
		});

		new MenuItem(popupMenu, SWT.SEPARATOR);

		generateItem = new MenuItem(popupMenu, SWT.NONE);
		generateItem.setImage(SWTResourceManager.getImage(Gui.class,
				"/images/xc_save.png"));
		generateItem.setEnabled(false);
		generateItem.setText("Save");
		generateItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				// try to save file
				try {
					File file = new File(path2xmls);
					file.mkdir();

					xml.save(new File(path2xmls + File.separator + dataFileName));

					saved = true;
					showInfo("File was saved successfully.", "Information");
				} catch (IOException e) {
					showError("When saving the file an error has occurred,",
							"Error");
				}
			}
		});

		new MenuItem(popupMenu, SWT.SEPARATOR);

		infoItem = new MenuItem(popupMenu, SWT.NONE);
		infoItem.setImage(SWTResourceManager.getImage(Gui.class,
				"/images/xc_info.png"));
		infoItem.setText("Help");
		infoItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				// show application info
				MessageBox messageBox = new MessageBox(getShell(),
						SWT.ICON_INFORMATION);
				messageBox.setText("Information");
				messageBox.setMessage("coming soon...");
				messageBox.open();
			}
		});

		closeItem = new MenuItem(popupMenu, SWT.NONE);
		closeItem.setImage(SWTResourceManager.getImage(Gui.class,
				"/images/xc_close.png"));
		closeItem.setText("Exit");
		closeItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				// if there is any unsaved data then show warning else close
				// application
				if (!output.getText().isEmpty() && !saved) {
					MessageBox messageBox = new MessageBox(getShell(),
							SWT.APPLICATION_MODAL | SWT.YES | SWT.NO
									| SWT.ICON_QUESTION);
					messageBox.setText("Information");
					messageBox.setMessage("Close application without saving ?");
					if (messageBox.open() == SWT.YES) {
						dispose();
					}
				} else {
					dispose();
				}
			}
		});

		output.setMenu(popupMenu);
	}

	/**
	 * Displays window with error message
	 * 
	 * @param message
	 *            error message content
	 * @param title
	 *            window title
	 */
	private void showError(final String message, final String title) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				MessageBox box = new MessageBox(getShell(), SWT.ICON_ERROR);
				box.setMessage(message);
				box.setText(title);
				box.open();
			}
		});
	}

	/**
	 * Displays window with information message
	 * 
	 * @param message
	 *            information message content
	 * @param title
	 *            window title
	 */
	private void showInfo(final String message, final String title) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				MessageBox box = new MessageBox(getShell(),
						SWT.ICON_INFORMATION);
				box.setMessage(message);
				box.setText(title);
				box.open();
			}
		});
	}

	/**
	 * Opens AddProgramDialog and if any data has been returned, it saves them
	 * 
	 * @return true if program was added, false if program was on the list
	 */
	public boolean addProgram() {
		boolean result = true;
		AddProgramDialog software = new AddProgramDialog(getShell(),
				"Add a Program", "/images/xc_big_add.png", "Add", null, null,
				null, screenWidth, screenHeight);

		if (software.open() != null) {
			String path = software.getDirPath();
			String server = software.getServer();
			String name = software.getName();
			String exe = software.getExe();

			result = xml.addProgram(name, path, exe, server);
			updateOutput();
		}

		return result;
	}

	/**
	 * Removes program
	 * 
	 * @return true if program was removed, false otherwise
	 */
	public boolean removeProgram() {
		boolean result = true;

		result = xml.removeProgram(program_name);
		updateOutput();

		return result;
	}

	/**
	 * Opens AddProgramDialog and if any data has been returned, it saves them
	 * 
	 * @return true if program was updated, false otherwise
	 */
	public boolean editProgram() {
		boolean result = true;
		AddProgramDialog software = new AddProgramDialog(getShell(),
				"Edit a Program", "/images/xc_big_edit.png", "Edit",
				program_path, program_server, program_exe, screenWidth,
				screenHeight);

		if (software.open() != null) {
			String path = software.getDirPath();
			String server = software.getServer();
			String name = software.getName();
			String exe = software.getExe();

			result = xml.updateProgram(program_name, name, path, exe, server);
			updateOutput();
		}

		return result;
	}

	/**
	 * Opens AddProcessesDialog and if any data has been returned, it saves them
	 */
	public void addProcesses() {
		AddProcessesDialog dialog = new AddProcessesDialog(getShell(),
				program_EH, program_install, program_checker, screenWidth,
				screenHeight);

		if (dialog.open() != null) {
			String EH = dialog.getEH();
			String install = dialog.getInstall();
			String checker = dialog.getChecker();

			xml.addProcesses(EH, install, checker);
			updateOutput();
		}
	}

	/**
	 * Checks whether the vertical scroll bar is needed
	 */
	private void setVerticalScrollBar() {
		if (output.getLineCount() > maxLineCount) {
			output.getVerticalBar().setVisible(true);
		} else {
			output.getVerticalBar().setVisible(false);
		}
	}

	/**
	 * Checks whether the horizontal scroll bar is needed
	 */
	private void setHorizontalScrollBar() {
		for (int i = 0; i < output.getLineCount(); ++i) {
			if (output.getLine(i).length() > lineWidth) {
				output.getHorizontalBar().setVisible(true);
				return;
			}
		}

		output.getHorizontalBar().setVisible(false);
	}

	/**
	 * Updates the display text
	 */
	private void updateOutput() {
		try {
			output.setText(xml.getText());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sets location of the window to center of the screen
	 */
	private void centerWindow() {
		setLocation((int) (screenWidth / 2) - (getBounds().width / 2),
				(int) (screenHeight / 2) - (getBounds().height / 2));
	}

	/**
	 * Gets environmental variable Local AppData
	 * 
	 * @return enviromental variable Local AppData
	 */
	private String getAppDataEnv() {
		return env.get("LOCALAPPDATA");
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}