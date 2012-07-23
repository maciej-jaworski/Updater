package gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.UnsupportedLookAndFeelException;

import org.dom4j.DocumentException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.wb.swt.SWTResourceManager;

import updater.Actualization;
import updater.Updater;

/** Application main window */
public class Gui extends Shell implements Runnable {
	// graphic elements
	private CTabFolder tabFolder;
	private CTabItem tabUpdate;
	private CTabItem tabInfo;
	private Composite updatePanel;
	private Composite infoPanel;
	private Tree tree;
	private Tree treeChangelog;
	private TreeColumn trclmnProgram;
	private TreeColumn trclmnVersion;
	private TreeColumn trclmnType;
	private TreeColumn trclmnPackageName;
	private Button btnUpdate;
	private Button btnCheckUpdates;
	private Button btnCheckChangeLog;
	private Combo comboPrograms;
	private Label lblInformation;
	private Label lblChoose;
	private StyledText changelog_output;
	/** object to communicate with tray */
	private MyTray myTray;
	/** object to communicate with AutoUpdater library */
	private Updater updater;
	/** screen resolution width and height */
	private double screenWidth, screenHeight;
	/** variable indicates whether the menu is locked */
	private boolean blockTrayMenu;
	/** image of waiting progress bar */
	private Image circle_pb;
	/** current rotation of progress bar */
	private float rotation;
	/** changelog window parameters - mac line count and mac line width */
	private int maxLineCount, lineWidth;

	/**
	 * Constructor
	 * 
	 * @param temp_myTray
	 *            object of class MyTray to communication
	 * @param screenWidth
	 *            screen resolution width
	 * @param screenHeight
	 *            screen resolution height
	 */
	public Gui(MyTray temp_myTray, double screenWidth, double screenHeight) {
		super(temp_myTray.getDisplay(), SWT.CLOSE | SWT.TITLE | SWT.BORDER);
		myTray = temp_myTray;
		updater = new Updater();

		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		// load image and set rotation
		circle_pb = SWTResourceManager.getImage(Gui.class, "/images/pbc.png");
		rotation = -1;

		createContents();
		centerWindow();
		// get line width and max line count
		GC gc = new GC(changelog_output);
		FontMetrics fm = gc.getFontMetrics();
		int charWidth = fm.getAverageCharWidth();
		gc.dispose();

		lineWidth = (changelog_output.getSize().x / charWidth) + 5;
		maxLineCount = changelog_output.getSize().y
				/ changelog_output.getLineHeight();
	}

	/**
	 * Starts new thread
	 */
	private void start() {
		new Thread(this).start();
	}

	/**
	 * Depending on the action performed: check updates, check changelog, update
	 */
	@Override
	public void run() {
		switch (updater.getAction()) {
		// waits until the library has finished check for updates and change
		// rotation of wait progress bar
		case CHECK_UPDATES: {
			rotation = 0;

			while (true) {
				rotation = rotation + 10;
				if (rotation == 360) {
					rotation = 0;
				}
				// redraw progress bar
				redrawComposite();

				try {
					Thread.sleep(25);
				} catch (InterruptedException e) {
				}

				if (!updater.isCheckUpdatesInProgress())
					break;
			}

			rotation = -1;
			redrawComposite();
			// updates button status and programs tree
			updateCheckUpdatesButton(true);
			updateTree();

			blockTrayMenu = false;

			break;
		}
		// waits until the library has finished check for changelogs
		case CHECK_CHANGELOGS: {
			while (true) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
				}

				if (!updater.isCheckChangeLogsInProgress())
					break;
			}
			// update button status and changelog tree
			updateCheckChangeLogsButton(true);
			updateChangeLogTree();

			blockTrayMenu = false;

			break;
		}
		// waits until the library has finished check for update
		case UPDATE: {
			while (true) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
				}

				if (!updater.isUpdateInProgress())
					break;
			}
			// if AutoUpdater was updated then close main window
			if (updater.isUpdateAutoUpdater()) {
				myTray.cleanUp();
				cleanUp();
				System.exit(0);
			}
			// update buttons status and programs tree
			updateCheckUpdatesButton(true);
			updateUpdateButton(true);
			updateTree();

			blockTrayMenu = false;

			break;
		}
		default:
			break;
		}
	}

	/**
	 * @wbp.parser.entryPoint
	 */
	protected void createContents() {
		setImage(SWTResourceManager.getImage(Gui.class, "/images/icon.png"));
		addListener(SWT.Close, new Listener() {

			@Override
			public void handleEvent(Event event) {
				// hide main window
				setVisible(false);
				event.doit = false;
			}
		});

		setText("Auto Updater");

		// ERROR when open window builder---------------------------------------
		// uncomment commented out and comment two normal lines in order to edit
		// in
		// window builder
		// Shell shell = new Shell();
		// shell.setSize(491, 490);
		setSize(491, 490);
		// tabFolder = new CTabFolder(shell, SWT.BORDER);
		tabFolder = new CTabFolder(this, SWT.BORDER);
		// ---------------------------------------------------------------------

		tabFolder.setBounds(0, 0, 485, 461);
		tabFolder.setSelectionBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));

		tabUpdate = new CTabItem(tabFolder, SWT.NONE);
		tabUpdate.setImage(SWTResourceManager.getImage(Gui.class,
				"/images/software.png"));
		updatePanel = new Composite(tabFolder, SWT.NONE);
		tabUpdate.setControl(updatePanel);

		btnUpdate = new Button(updatePanel, SWT.CENTER);
		btnUpdate.setBounds(243, 360, 134, 37);
		btnUpdate.setText("Update");
		btnUpdate.setEnabled(false);
		btnUpdate.addSelectionListener(new SelectionAdapter() {
			// prepare update process
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				// list of actualizations to update
				ArrayList<String> actualization_list = new ArrayList<String>();
				// list of processes to close
				ArrayList<String> processesToClose;
				// get tree items
				TreeItem[] items = tree.getItems();
				TreeItem[] childs;

				// iterate over all tree items
				for (int i = 0; i < items.length; ++i) {
					childs = items[i].getItems();
					for (int j = 0; j < childs.length; ++j) {
						// if an item is selected, then add it to the list
						if (childs[j].getChecked()) {
							// program name, actualization name, actualization
							// version
							actualization_list.add(items[i].getText());
							actualization_list.add(childs[j].getText());
							actualization_list.add(childs[j].getText(2));
						}
					}
				}

				while (true) {
					// get list of processes to close
					processesToClose = updater
							.getProcessesToClose(actualization_list);

					// if there are any processes to close then show warning
					// dialog
					if (processesToClose.size() > 0) {
						String info = "";

						for (int i = 0; i < processesToClose.size(); i = i + 2) {
							info += "\n" + processesToClose.get(i) + " : "
									+ processesToClose.get(i + 1);
						}

						MessageBox messageBox = new MessageBox(getShell(),
								SWT.ICON_WARNING | SWT.RETRY | SWT.CANCEL);
						messageBox
								.setMessage("To continue, close the following processes: \n"
										+ info);
						messageBox.setText("Information");
						int rc = messageBox.open();

						if (rc == SWT.RETRY) {
							continue;
						} else {
							return;
						}
					} else {
						break;
					}
				}

				// try to start update
				if (updater.update(actualization_list)) {
					blockTrayMenu = true;

					// start new thread
					start();

					// update buttons status
					btnUpdate.setEnabled(false);
					btnCheckUpdates.setEnabled(false);

				} else {
					showInfo("Update in progress.", "Information");
				}
			}
		});

		tree = new Tree(updatePanel, SWT.BORDER | SWT.MULTI | SWT.CHECK
				| SWT.PaintItem);
		tree.setHeaderVisible(true);
		tree.setBounds(11, 10, 455, 340);
		tree.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				boolean foundChecked = false;
				TreeItem[] items = tree.getItems();

				// change check status in parent and children items
				if (event.detail == SWT.CHECK) {
					TreeItem item = (TreeItem) event.item;

					boolean checked = item.getChecked();
					checkItems(item, checked);
					checkPath(item.getParentItem(), checked, false);
				}
				// if program with available actualization is selected then
				// enable update button
				for (int i = 0; i < items.length; ++i) {
					if (items[i].getChecked() && items[i].getItemCount() > 0) {

						btnUpdate.setEnabled(true);
						foundChecked = true;
						break;
					}
				}

				if (!foundChecked) {
					btnUpdate.setEnabled(false);
				}
			}
		});

		trclmnProgram = new TreeColumn(tree, SWT.NONE);
		trclmnProgram.setWidth(250);
		trclmnProgram.setText("Program");

		trclmnVersion = new TreeColumn(tree, SWT.NONE);
		trclmnVersion.setResizable(false);
		trclmnVersion.setWidth(100);
		trclmnVersion.setText("Version");

		trclmnType = new TreeColumn(tree, SWT.NONE);
		trclmnType.setResizable(false);
		trclmnType.setWidth(100);
		trclmnType.setText("Type");

		btnCheckUpdates = new Button(updatePanel, SWT.NONE);
		btnCheckUpdates.setBounds(101, 360, 134, 37);
		btnCheckUpdates.setText("Check Updates");
		btnCheckUpdates.setEnabled(false);
		btnCheckUpdates.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				// try to start checking for updates
				if (updater.checkUpdates()) {
					blockTrayMenu = true;
					btnCheckUpdates.setEnabled(false);

					start();
				} else {
					showInfo("Checking for updates in progress, please wait.",
							"Information");
				}
			}
		});

		// measure and paint wait progress bar
		Listener paintListener = new Listener() {
			public void handleEvent(Event event) {
				switch (event.type) {
				case SWT.MeasureItem: {
					Rectangle rect = circle_pb.getBounds();
					event.width += rect.width;
					event.height = Math.max(event.height, rect.height + 2);
					break;
				}
				case SWT.PaintItem: {
					TreeItem item = (TreeItem) event.item;

					// progress bar is drawn only with programs (mod 3)
					if (rotation != -1 && event.index % 3 == 0
							&& item.getParentItem() == null) {
						int x = event.x + event.width + 1;
						Rectangle rect = circle_pb.getBounds();
						int offset = Math.max(0,
								(event.height - rect.height) / 2);

						Transform oldTransform = new Transform(
								event.gc.getDevice());
						event.gc.getTransform(oldTransform);

						Transform transform = new Transform(
								Display.getDefault());
						transform.translate(
								x + circle_pb.getBounds().width / 2, event.y
										+ offset + circle_pb.getBounds().height
										/ 2);
						transform.rotate(rotation);
						transform.translate(-x - circle_pb.getBounds().width
								/ 2,
								-(event.y + offset)
										- circle_pb.getBounds().height / 2);

						event.gc.setTransform(transform);
						event.gc.drawImage(circle_pb, x, event.y + offset);
						event.gc.setTransform(oldTransform);

						transform.dispose();
					}

					break;
				}
				}
			}
		};
		tree.addListener(SWT.MeasureItem, paintListener);
		tree.addListener(SWT.PaintItem, paintListener);

		tabInfo = new CTabItem(tabFolder, SWT.NONE);
		tabInfo.setImage(SWTResourceManager.getImage(Gui.class,
				"/images/info.png"));

		infoPanel = new Composite(tabFolder, SWT.NONE);
		tabInfo.setControl(infoPanel);

		btnCheckChangeLog = new Button(infoPanel, SWT.NONE);
		btnCheckChangeLog.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				// if no program is selected then return
				if (comboPrograms.getText().isEmpty()) {
					return;
				}

				// prepare and start checking for changelogs
				if (updater.checkChangeLogs(comboPrograms.getText())) {
					blockTrayMenu = true;

					changelog_output.setText("");

					treeChangelog.setHeaderVisible(false);
					treeChangelog.removeAll();

					TreeItem infoloading = new TreeItem(treeChangelog, SWT.NONE);
					infoloading.setText("Retrieving data from a server...");

					btnCheckChangeLog.setEnabled(false);

					start();
				} else {
					showInfo("Retrieving changelogs in progress, please wait.",
							"Information");
				}
			}
		});
		btnCheckChangeLog.setBounds(325, 33, 138, 23);
		btnCheckChangeLog.setText("Check ChangeLog");

		comboPrograms = new Combo(infoPanel, SWT.NONE);
		comboPrograms.setBounds(15, 34, 300, 23);

		treeChangelog = new Tree(infoPanel, SWT.BORDER);
		treeChangelog.setHeaderVisible(true);
		treeChangelog.setBounds(15, 63, 448, 178);
		treeChangelog.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				TreeItem[] item = treeChangelog.getSelection();

				// if selected item is version
				if (item[0].getParentItem() != null) {
					// get version and actualization/package name
					String version = item[0].getText();
					String actualization_name = item[0].getParentItem()
							.getText();

					// take the appropriate changelog and show it
					changelog_output.setText("");
					changelog_output.setText(updater.getChangeLog(
							actualization_name, version));

					setVerticalScrollBar();
					setHorizontalScrollBar();
				}
			}
		});

		trclmnPackageName = new TreeColumn(treeChangelog, SWT.NONE);
		trclmnPackageName.setWidth(443);
		trclmnPackageName.setText("Name");

		lblInformation = new Label(infoPanel, SWT.NONE);
		lblInformation.setBounds(16, 247, 89, 15);
		lblInformation.setText("Information:");

		lblChoose = new Label(infoPanel, SWT.NONE);
		lblChoose.setBounds(16, 10, 110, 15);
		lblChoose.setText("Choose program:");

		changelog_output = new StyledText(infoPanel, SWT.BORDER | SWT.H_SCROLL
				| SWT.V_SCROLL);
		changelog_output.setEditable(false);
		changelog_output.setBounds(15, 268, 448, 125);
		changelog_output.getVerticalBar().setVisible(false);
		changelog_output.getHorizontalBar().setVisible(false);
	}

	/**
	 * Checks whether the vertical scroll bar is needed
	 */
	private void setVerticalScrollBar() {
		if (changelog_output.getLineCount() > maxLineCount) {
			changelog_output.getVerticalBar().setVisible(true);
		} else {
			changelog_output.getVerticalBar().setVisible(false);
		}
	}

	/**
	 * Checks whether the horizontal scroll bar is needed
	 */
	private void setHorizontalScrollBar() {
		for (int i = 0; i < changelog_output.getLineCount(); ++i) {
			if (changelog_output.getLine(i).length() > lineWidth) {
				changelog_output.getHorizontalBar().setVisible(true);
				return;
			}
		}

		changelog_output.getHorizontalBar().setVisible(false);
	}

	/**
	 * Redraw tree with programs and available actualizations, used to update
	 * wait progress bar
	 */
	private void redrawComposite() {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				tree.redraw();
			}
		});
	}

	/**
	 * Enable / disable update button
	 * 
	 * @param status
	 *            true - enable, false - disable
	 */
	private void updateUpdateButton(final boolean status) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				btnUpdate.setEnabled(status);
			}
		});
	}

	/**
	 * Enable / disable check updates button
	 * 
	 * @param status
	 *            true - enable, false - disable
	 */
	private void updateCheckUpdatesButton(final boolean status) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				btnCheckUpdates.setEnabled(status);
			}
		});
	}

	/**
	 * Enable / disable check changelogs button
	 * 
	 * @param status
	 *            true - enable, false - disable
	 */
	private void updateCheckChangeLogsButton(final boolean status) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				btnCheckChangeLog.setEnabled(status);
			}
		});
	}

	/**
	 * Removes all from changelog tree, gets available changelogs from library
	 * and displays them. Used after checking changelogs
	 */
	private void updateChangeLogTree() {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				// get changelogs from library
				Map<String, Map<String, String>> changelogs = updater
						.getChangeLogs();

				// clear tree
				treeChangelog.removeAll();
				treeChangelog.setHeaderVisible(true);

				// iterate over all actualization/package
				for (Map.Entry<String, Map<String, String>> entry : changelogs
						.entrySet()) {
					// create new item with actualization name
					TreeItem actualization = new TreeItem(treeChangelog,
							SWT.NONE);
					actualization.setText(entry.getKey());

					// iterate over all version of actualization
					for (Map.Entry<String, String> child : entry.getValue()
							.entrySet()) {
						// create item with version
						TreeItem version = new TreeItem(actualization, SWT.NONE);
						version.setText(child.getKey());
					}

					actualization.setExpanded(true);
				}
			}
		});
	}

	/**
	 * Removes all from programs tree, gets available actualization from library
	 * and displays them. Used after checking updates and update.
	 */
	private void updateTree() {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				int counter = 0;
				TreeItem[] items = tree.getItems();
				ArrayList<Actualization> available_updates = new ArrayList<Actualization>();

				// iterate over all programs in the tree
				for (int i = 0; i < items.length; ++i) {
					items[i].setChecked(false);
					// remove all children
					items[i].removeAll();
					// get list of new available actualization
					available_updates = updater
							.getProgramAvailableActualizations(items[i]
									.getText());

					// update program status in tray menu
					if (available_updates.isEmpty()) {
						myTray.setStatus(items[i].getText(), true);
					} else {
						myTray.setStatus(items[i].getText(), false);
					}

					// set new value to available actualization counter
					counter += available_updates.size();

					// add actualizations to tree
					for (int j = 0; j < available_updates.size(); ++j) {
						TreeItem item = new TreeItem(items[i], SWT.CHECK);
						// name, version, type
						item.setText(new String[] {
								available_updates.get(j).getName(),
								available_updates.get(j).getVersion(),
								available_updates.get(j).getType() });
					}

					items[i].setExpanded(true);
				}

				// show how many actualization is available to download
				myTray.showInfoToolTip("Found " + counter + " updates...");
			}
		});
	}

	private void checkItems(TreeItem item, boolean checked) {
		item.setGrayed(false);
		item.setChecked(checked);
		TreeItem[] items = item.getItems();

		for (int i = 0; i < items.length; i++) {
			checkItems(items[i], checked);
		}
	}

	private void checkPath(TreeItem item, boolean checked, boolean grayed) {
		if (item == null)
			return;
		if (grayed) {
			checked = true;
		} else {
			int index = 0;
			TreeItem[] items = item.getItems();
			while (index < items.length) {
				TreeItem child = items[index];
				if (child.getGrayed() || checked != child.getChecked()) {
					checked = grayed = true;
					break;
				}
				index++;
			}
		}

		item.setChecked(checked);
		item.setGrayed(grayed);
		checkPath(item.getParentItem(), checked, grayed);
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
	 * Shows main window
	 */
	public void Open() {
		open();
		layout();
	}

	/**
	 * Dispose all resources
	 */
	public void cleanUp() {
		updater.cleanUp();

		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				dispose();
			}
		});
	}

	/**
	 * Sets location of the window to center of the screen
	 */
	private void centerWindow() {
		setLocation((int) (screenWidth / 2) - (getBounds().width / 2),
				(int) (screenHeight / 2) - (getBounds().height / 2));
	}

	/**
	 * Initializes library and loads information about programs
	 * 
	 * @return true if successful, false otherwise
	 */
	public boolean initData() {
		try {
			if (!updater.init()) {
				return false;
			}

			ArrayList<String> programsList = updater.getProgramsNames();

			for (int i = 0; i < programsList.size(); ++i) {
				addToTree(programsList.get(i));
				addToCombo(programsList.get(i));
				myTray.addItemToList(programsList.get(i));
			}
		} catch (IOException e) {
			showError(e.getMessage(), "Error");
			return false;
		} catch (DocumentException e) {
			showError(e.getMessage(), "Error");
			return false;
		} catch (ClassNotFoundException e) {
			showError(e.getMessage(), "Error");
			return false;
		} catch (IllegalAccessException e) {
			showError(e.getMessage(), "Error");
			return false;
		} catch (InstantiationException e) {
			showError(e.getMessage(), "Error");
			return false;
		} catch (UnsupportedLookAndFeelException e) {
			showError(e.getMessage(), "Error");
			return false;
		}

		return true;
	}

	/**
	 * Adds new item to program tree with specified name
	 * 
	 * @param name
	 *            new item name - program name
	 */
	private void addToTree(String name) {
		TreeItem new_item = new TreeItem(tree, SWT.NONE);
		new_item.setText(name);
		btnCheckUpdates.setEnabled(true);
	}

	/**
	 * Adds new item to combo box used to checking changelogs
	 * 
	 * @param name
	 *            new item name - program name
	 */
	private void addToCombo(String name) {
		comboPrograms.add(name);
	}

	/**
	 * Clicks check updates button
	 */
	public void clickCheckUpdatesButton() {
		btnCheckUpdates.notifyListeners(SWT.Selection, new Event());
	}

	/**
	 * Selects all actualization of specified program and starts update
	 * 
	 * @param program_name
	 *            program name
	 */
	public void clickUpdateButton(String program_name) {
		int item_index = -1;
		TreeItem[] items = tree.getItems();

		for (int i = 0; i < items.length; ++i) {
			if (items[i].getText().equals(program_name)) {
				item_index = i;
				items[i].setChecked(true);
				checkItems(items[i], true);
				checkPath(items[i].getParentItem(), true, false);
				break;
			}
		}

		// if there is something to update
		if (btnUpdate.isEnabled()) {
			btnUpdate.notifyListeners(SWT.Selection, new Event());
		} else {
			items[item_index].setChecked(false);
		}
	}

	/**
	 * Saves new proxy data
	 * 
	 * @param address
	 *            proxy server address
	 * @param port
	 *            proxy port
	 */
	public void saveProxy(String address, int port) {
		try {
			if (updater.addProxy(address, port)) {
				showInfo("Added proxy.", "Information");
			} else {
				showError("Adding proxy failed", "Error");
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Removes current proxy data
	 */
	public void removeProxy() {
		try {
			updater.removeProxy();
		} catch (DocumentException e) {
			showError(e.getMessage(), "Error");
		} catch (IOException e) {
			showError(e.getMessage(), "Error");
		}
	}

	/**
	 * Gets proxy server address
	 * 
	 * @return proxy server address
	 */
	public String getProxyAddress() {
		return updater.getProxyAddress();
	}

	/**
	 * Gets proxy port
	 * 
	 * @return proxy port
	 */
	public int getProxyPort() {
		return updater.getProxyPort();
	}

	/**
	 * Gets status of tray menu - whether is blocked or not
	 * 
	 * @return true if menu is blocked, false otherwise
	 */
	public boolean isBlockTrayMenu() {
		return blockTrayMenu;
	}

	/**
	 * Sets status of tray menu - whether is blocked or not
	 * 
	 * @param blockTrayMenu
	 *            true - to lock the menu, false - to unlock the menu
	 */
	public void setBlockTrayMenu(boolean blockTrayMenu) {
		this.blockTrayMenu = blockTrayMenu;
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
