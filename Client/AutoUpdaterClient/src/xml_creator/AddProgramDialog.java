package xml_creator;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

/** Window for adding and editing programs */
public class AddProgramDialog extends Dialog {

	protected Object result;
	protected Shell shell;
	// graphic elements
	private Group dataPanel;
	private Text textDir;
	private Text textServer;
	private Button btnAdd;
	private Button btnCancel;
	private Button btnDir;
	private Button btnExe;
	private Text textExe;
	private Label lblServer;
	private Label lblDir;
	private Label lblExe;

	/** new program parameters */
	private String name, path, server, exe;
	/** existing program parameters - used to edit */
	private String old_path, old_server, old_exe;
	/** window title */
	private String title;
	/** path to window image */
	private String image;
	/** apply button text - "add", "edit" */
	private String button_text;
	/** resolution width */
	private double screenWidth;
	/** resolution height */
	private double screenHeight;

	/**
	 * Create the dialog
	 * 
	 * @param parent
	 *            the parent shell, or null to create a top-level shell
	 * @param temp_title
	 *            window title
	 * @param temp_image
	 *            path to window image
	 * @param button_text
	 *            apply button text
	 * @param temp_path
	 *            if edit then existing path else null
	 * @param temp_server
	 *            if edit then existing server else null
	 * @param temp_exe
	 *            if edit then existing exe else null
	 * @param screenWidth
	 *            resolution width
	 * @param screenHeight
	 *            resolution height
	 */
	public AddProgramDialog(Shell parent, String temp_title, String temp_image,
			String button_text, String temp_path, String temp_server,
			String temp_exe, double screenWidth, double screenHeight) {

		super(parent, SWT.CLOSE | SWT.SYSTEM_MODAL);

		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;

		this.old_path = "";
		this.old_server = "";
		this.old_exe = "";

		if (temp_path != null) {
			old_path = temp_path;
		}

		if (temp_server != null) {
			old_server = temp_server;
		}

		if (temp_exe != null) {
			old_exe = temp_exe;
		}

		this.title = temp_title;
		this.image = temp_image;
		this.button_text = button_text;

		this.name = null;
		this.path = null;
		this.server = null;
		this.exe = null;
	}

	/**
	 * Open the dialog.
	 * 
	 * @return the result
	 */
	public Object open() {
		createContents();
		centerWindow();

		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell = new Shell(getParent(), getStyle());
		shell.setSize(435, 200);
		shell.setText(title);
		shell.setImage(SWTResourceManager.getImage(AddProgramDialog.class,
				image));

		btnAdd = new Button(shell, SWT.NONE);
		btnAdd.setEnabled(false);
		btnAdd.setText(button_text);
		btnAdd.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				// validate the data and set new program parameters
				if (checkData()) {
					path = textDir.getText();
					server = textServer.getText();
					exe = textExe.getText();
					name = path.substring(path.lastIndexOf(File.separator) + 1,
							path.length());

					result = 1;
					shell.dispose();
				}
			}
		});
		btnAdd.setBounds(345, 137, 75, 25);

		btnCancel = new Button(shell, SWT.NONE);
		btnCancel.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent event) {
				// close window
				shell.dispose();
			}
		});
		btnCancel.setBounds(264, 137, 75, 25);
		btnCancel.setText("Cancel");

		dataPanel = new Group(shell, SWT.NONE);
		dataPanel.setText("Program data");
		dataPanel.setBounds(10, 10, 410, 119);

		lblDir = new Label(dataPanel, SWT.NONE);
		lblDir.setText("Program directory:");
		lblDir.setBounds(10, 23, 105, 15);

		textDir = new Text(dataPanel, SWT.BORDER);
		textDir.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent arg0) {
				// checks if program directory is empty and block/unblock other
				// fields
				if (textDir.getText().isEmpty()) {
					blockItems();
				} else {
					unblockItems();
				}

				// checks if apply button can be unlocked
				if (!textServer.getText().isEmpty()
						&& !textDir.getText().isEmpty()) {
					btnAdd.setEnabled(true);
				} else {
					btnAdd.setEnabled(false);
				}
			}
		});

		textDir.setTabs(1);
		textDir.setBounds(121, 22, 235, 21);
		textDir.setText(old_path);

		btnDir = new Button(dataPanel, SWT.NONE);
		btnDir.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent event) {
				DirectoryDialog dialog = new DirectoryDialog(shell);
				dialog.setText("Select the directory");
				dialog.setFilterPath("C:\\");
				String result = dialog.open();
				if (result != null) {
					textDir.setText(result);
				}

				// checks if apply button can be unlocked
				if (!textServer.getText().isEmpty()
						&& !textDir.getText().isEmpty()) {
					btnAdd.setEnabled(true);
				} else {
					btnAdd.setEnabled(false);
				}

				unblockItems();
			}
		});
		btnDir.setImage(SWTResourceManager.getImage(AddProgramDialog.class,
				"/images/xc_dir.png"));
		btnDir.setBounds(362, 20, 36, 25);

		textExe = new Text(dataPanel, SWT.BORDER);
		textExe.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent arg0) {
				// checks if apply button can be unlocked
				if (!textServer.getText().isEmpty()
						&& !textDir.getText().isEmpty()) {
					btnAdd.setEnabled(true);
				} else {
					btnAdd.setEnabled(false);
				}
			}
		});
		if (old_exe.isEmpty()) {
			textExe.setEnabled(false);
		}
		textExe.setBounds(121, 53, 235, 21);
		textExe.setText(old_exe);

		btnExe = new Button(dataPanel, SWT.NONE);
		btnExe.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent event) {
				FileDialog dialog = new FileDialog(shell);
				dialog.setText("Select the executable file");
				dialog.setFilterPath(textDir.getText() + File.separator);
				dialog.setFilterExtensions(new String[] { "*.exe" });
				String result = dialog.open();
				if (result != null) {
					result = result.substring(textDir.getText().length() + 1);
					textExe.setText(result);

					// checks if apply button can be unlocked
					if (!textServer.getText().isEmpty()
							&& !textDir.getText().isEmpty()) {
						btnAdd.setEnabled(true);
					} else {
						btnAdd.setEnabled(false);
					}
				}
			}
		});
		btnExe.setImage(SWTResourceManager.getImage(AddProgramDialog.class,
				"/images/xc_dir.png"));
		btnExe.setBounds(362, 49, 36, 25);
		if (old_exe.isEmpty()) {
			btnExe.setEnabled(false);
		}

		lblServer = new Label(dataPanel, SWT.NONE);
		if (old_server.isEmpty()) {
			lblServer.setForeground(Display.getDefault().getSystemColor(
					SWT.COLOR_TITLE_INACTIVE_FOREGROUND));
		}
		lblServer.setBounds(10, 87, 82, 15);
		lblServer.setText("Server address:");

		textServer = new Text(dataPanel, SWT.BORDER);
		textServer.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent arg0) {
				// checks if apply button can be unlocked
				if (!textServer.getText().isEmpty()
						&& !textDir.getText().isEmpty()) {
					btnAdd.setEnabled(true);
				} else {
					btnAdd.setEnabled(false);
				}
			}
		});
		if (old_server.isEmpty()) {
			textServer.setEnabled(false);
		}
		textServer.setBounds(121, 84, 235, 21);
		textServer.setText(old_server);

		lblExe = new Label(dataPanel, SWT.NONE);
		if (old_exe.isEmpty()) {
			lblExe.setForeground(Display.getDefault().getSystemColor(
					SWT.COLOR_TITLE_INACTIVE_FOREGROUND));
		}
		lblExe.setBounds(10, 54, 105, 15);
		lblExe.setText("Program exe:");

		if (!textDir.getText().isEmpty()) {
			unblockItems();
		}

		Control[] list_1 = new Control[] { textDir, btnDir, textExe, btnExe,
				textServer };
		dataPanel.setTabList(list_1);
		Control[] list_2 = new Control[] { dataPanel, btnCancel, btnAdd };
		shell.setTabList(list_2);
	}

	/**
	 * Sets location of the window to center of the screen
	 */
	private void centerWindow() {
		shell.setLocation((int) (screenWidth / 2)
				- (shell.getBounds().width / 2), (int) (screenHeight / 2)
				- (shell.getBounds().height / 2));
	}

	/**
	 * Validate data
	 * 
	 * @return true if correct, false otherwise
	 */
	private boolean checkData() {
		if (textDir.getText().equals("")) {
			MessageBox box = new MessageBox(shell, SWT.ICON_ERROR);
			box.setMessage("Program directory is empty.");
			box.setText("Error");
			box.open();
			return false;
		}

		if (textServer.getText().equals("")) {
			MessageBox box = new MessageBox(shell, SWT.ICON_ERROR);
			box.setMessage("Server address is empty.");
			box.setText("Error");
			box.open();
			return false;
		}

		if (textDir.getText().equals("[a-z][A-Z]:\\")
				|| !dir_exists(textDir.getText())) {
			MessageBox box = new MessageBox(shell, SWT.ICON_ERROR);
			box.setMessage("Program directory does not exist or is not a directory.");
			box.setText("Error");
			box.open();
			return false;
		}

		if (!textExe.getText().isEmpty()) {
			File file = new File(textDir.getText() + File.separator
					+ textExe.getText());
			if (!file.exists()) {
				MessageBox box = new MessageBox(shell, SWT.ICON_ERROR);
				box.setMessage("Can not find the executable file.");
				box.setText("Error");
				box.open();
				return false;
			}
		}

		return true;
	}

	/**
	 * Unblock interface items
	 */
	private void unblockItems() {
		lblExe.setForeground(Display.getDefault().getSystemColor(
				SWT.COLOR_TITLE_FOREGROUND));
		textExe.setEnabled(true);
		btnExe.setEnabled(true);
		lblServer.setForeground(Display.getDefault().getSystemColor(
				SWT.COLOR_TITLE_FOREGROUND));
		textServer.setEnabled(true);
	}

	/**
	 * Block interface items
	 */
	private void blockItems() {
		lblExe.setForeground(Display.getDefault().getSystemColor(
				SWT.COLOR_TITLE_INACTIVE_FOREGROUND));
		textExe.setEnabled(false);
		btnExe.setEnabled(false);
		lblServer.setForeground(Display.getDefault().getSystemColor(
				SWT.COLOR_TITLE_INACTIVE_FOREGROUND));
		textServer.setEnabled(false);
	}

	/**
	 * Checks if path is a directory
	 * 
	 * @param path
	 *            path to check
	 * @return true if it is and false otherwise
	 */
	private boolean dir_exists(String path) {
		File source = new File(path);
		return source.isDirectory() ? true : false;
	}

	/**
	 * Gets program directory
	 * 
	 * @return program directory
	 */
	public String getDirPath() {
		return this.path;
	}

	/**
	 * Gets server address which contains information about actualizations
	 * 
	 * @return server address which contains information about actualizations
	 */
	public String getServer() {
		return this.server;
	}

	/**
	 * Gets program name
	 * 
	 * @return program name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Gets program exetuable file
	 * 
	 * @return program exetuable file
	 */
	public String getExe() {
		return exe;
	}
}
