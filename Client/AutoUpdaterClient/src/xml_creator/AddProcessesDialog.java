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

/** Window for adding and editing external processes */
public class AddProcessesDialog extends Dialog {

	protected Object result;
	protected Shell shell;
	// graphic elements
	private Group dataPanel;
	private Text textDir;
	private Text textInstall;
	private Text textEH;
	private Text textChecker;
	private Button btnAdd;
	private Button btnCancel;
	private Button btnDir;
	private Button btnEH;
	private Button btnInstall;
	private Button btnChecker;
	private Label lblInstall;
	private Label lblDir;
	private Label lblEH;
	private Label lblChecker;

	/** resolution width and height */
	private double screenWidth, screenHeight;
	/**
	 * variables for paths to external processes: EH - ElevateHandler, C#
	 * process which runs other process as administrator, install - java process
	 * which copies files, checker - java process which checks if install is
	 * over
	 * */
	private String EH, install, checker;
	/** variables for existing paths to external prcesses, used to edit */
	private String old_EH, old_install, old_checker;

	/**
	 * Create the dialog
	 * 
	 * @param parent
	 *            the parent shell, or null to create a top-level shell
	 * @param temp_EH
	 *            ElevateHandler, C# process which runs other process as
	 *            administrator
	 * @param temp_install
	 *            java process which copies files
	 * @param temp_checker
	 *            java process which checks if install is over
	 * @param screenWidth
	 *            resolution width
	 * @param screenHeight
	 *            resolution height
	 */
	public AddProcessesDialog(Shell parent, String temp_EH,
			String temp_install, String temp_checker, double screenWidth,
			double screenHeight) {
		super(parent, SWT.CLOSE | SWT.SYSTEM_MODAL);

		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;

		if (temp_EH != null) {
			this.old_EH = temp_EH;
		} else {
			this.old_EH = "";
		}

		if (temp_install != null) {
			this.old_install = temp_install;
		} else {
			this.old_install = "";
		}

		if (temp_checker != null) {
			this.old_checker = temp_checker;
		} else {
			this.old_checker = "";
		}
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
		shell.setSize(476, 216);
		shell.setText("Add instalation process location");
		shell.setImage(SWTResourceManager.getImage(AddProcessesDialog.class,
				"/images/xc_big_option.png"));

		btnAdd = new Button(shell, SWT.NONE);
		btnAdd.setEnabled(false);
		btnAdd.setBounds(380, 154, 75, 25);
		btnAdd.setText("Add");
		btnAdd.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				// validate the data and set new parameters
				if (checkData()) {
					EH = textEH.getText();
					install = textInstall.getText();
					checker = textChecker.getText();

					result = 1;
					shell.dispose();
				}
			}
		});

		btnCancel = new Button(shell, SWT.NONE);
		btnCancel.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent event) {
				shell.dispose();
			}
		});
		btnCancel.setBounds(299, 154, 75, 25);
		btnCancel.setText("Cancel");

		dataPanel = new Group(shell, SWT.NONE);
		dataPanel.setText("AutoUpdater data");
		dataPanel.setBounds(10, 10, 445, 138);

		lblDir = new Label(dataPanel, SWT.NONE);
		lblDir.setText("AutoUpdater directory:");
		lblDir.setBounds(10, 23, 141, 15);

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
				if (!textInstall.getText().isEmpty()
						&& !textEH.getText().isEmpty()) {
					btnAdd.setEnabled(true);
				} else {
					btnAdd.setEnabled(false);
				}
			}
		});

		textDir.setTabs(1);
		textDir.setBounds(157, 20, 235, 21);

		btnDir = new Button(dataPanel, SWT.NONE);
		btnDir.setImage(SWTResourceManager.getImage(AddProgramDialog.class,
				"/images/xc_dir.png"));
		btnDir.setBounds(398, 16, 36, 25);
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

				unblockItems();
			}
		});

		textEH = new Text(dataPanel, SWT.BORDER);
		textEH.setEnabled(false);
		textEH.setBounds(157, 45, 235, 21);
		textEH.setText(old_EH);
		textEH.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent arg0) {
				// checks if apply button can be unlocked
				if (!textInstall.getText().isEmpty()
						&& !textChecker.getText().isEmpty()
						&& !textEH.getText().isEmpty()) {
					btnAdd.setEnabled(true);
				} else {
					btnAdd.setEnabled(false);
				}
			}
		});

		btnEH = new Button(dataPanel, SWT.NONE);
		btnEH.setEnabled(false);
		btnEH.setImage(SWTResourceManager.getImage(AddProgramDialog.class,
				"/images/xc_dir.png"));
		btnEH.setBounds(398, 42, 36, 25);
		btnEH.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				FileDialog dialog = new FileDialog(shell);
				dialog.setText("Select the executable file");
				dialog.setFilterPath(textDir.getText() + File.separator);
				dialog.setFilterExtensions(new String[] { "*.exe" });
				String result = dialog.open();
				if (result != null) {
					result = result.substring(textDir.getText().length() + 1);
					textEH.setText(result);

					// checks if apply button can be unlocked
					if (!textInstall.getText().isEmpty()
							&& !textChecker.getText().isEmpty()
							&& !textEH.getText().isEmpty()) {
						btnAdd.setEnabled(true);
					} else {
						btnAdd.setEnabled(false);
					}
				}
			}
		});

		lblEH = new Label(dataPanel, SWT.NONE);
		lblEH.setForeground(Display.getDefault().getSystemColor(
				SWT.COLOR_TITLE_INACTIVE_FOREGROUND));
		lblEH.setBounds(10, 48, 141, 15);
		lblEH.setText("ElevateHandler directory:");

		lblInstall = new Label(dataPanel, SWT.NONE);
		lblInstall.setForeground(Display.getDefault().getSystemColor(
				SWT.COLOR_TITLE_INACTIVE_FOREGROUND));
		lblInstall.setBounds(10, 74, 141, 15);
		lblInstall.setText("Installation jar directory:");

		textInstall = new Text(dataPanel, SWT.BORDER);
		textInstall.setEnabled(false);
		textInstall.setBounds(157, 72, 235, 21);
		textInstall.setText(old_install);
		textInstall.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent arg0) {
				// checks if apply button can be unlocked
				if (!textInstall.getText().isEmpty()
						&& !textChecker.getText().isEmpty()
						&& !textEH.getText().isEmpty()) {
					btnAdd.setEnabled(true);
				} else {
					btnAdd.setEnabled(false);
				}
			}
		});

		btnInstall = new Button(dataPanel, SWT.NONE);
		btnInstall.setEnabled(false);
		btnInstall.setImage(SWTResourceManager.getImage(
				AddProcessesDialog.class, "/images/xc_dir.png"));
		btnInstall.setBounds(398, 69, 36, 25);
		btnInstall.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				FileDialog dialog = new FileDialog(shell);
				dialog.setText("Select the jar file");
				dialog.setFilterPath(textDir.getText() + File.separator);
				dialog.setFilterExtensions(new String[] { "*.jar" });
				String result = dialog.open();
				if (result != null) {
					result = result.substring(textDir.getText().length() + 1);
					textInstall.setText(result);

					// checks if apply button can be unlocked
					if (!textInstall.getText().isEmpty()
							&& !textChecker.getText().isEmpty()
							&& !textEH.getText().isEmpty()) {
						btnAdd.setEnabled(true);
					} else {
						btnAdd.setEnabled(false);
					}
				}
			}
		});

		lblChecker = new Label(dataPanel, SWT.NONE);
		lblChecker.setText("Checker jar directory:");
		lblChecker.setForeground(SWTResourceManager
				.getColor(SWT.COLOR_TITLE_INACTIVE_FOREGROUND));
		lblChecker.setBounds(10, 101, 141, 15);

		textChecker = new Text(dataPanel, SWT.BORDER);
		textChecker.setText(old_checker);
		textChecker.setEnabled(false);
		textChecker.setBounds(157, 99, 235, 21);
		textChecker.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent arg0) {
				// checks if apply button can be unlocked
				if (!textInstall.getText().isEmpty()
						&& !textChecker.getText().isEmpty()
						&& !textEH.getText().isEmpty()) {
					btnAdd.setEnabled(true);
				} else {
					btnAdd.setEnabled(false);
				}
			}
		});

		btnChecker = new Button(dataPanel, SWT.NONE);
		btnChecker.setImage(SWTResourceManager.getImage(
				AddProcessesDialog.class, "/images/xc_dir.png"));
		btnChecker.setEnabled(false);
		btnChecker.setBounds(398, 96, 36, 25);
		btnChecker.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				FileDialog dialog = new FileDialog(shell);
				dialog.setText("Select the jar file");
				dialog.setFilterPath(textDir.getText() + File.separator);
				dialog.setFilterExtensions(new String[] { "*.jar" });
				String result = dialog.open();
				if (result != null) {
					result = result.substring(textDir.getText().length() + 1);
					textChecker.setText(result);

					// checks if apply button can be unlocked
					if (!textInstall.getText().isEmpty()
							&& !textChecker.getText().isEmpty()
							&& !textEH.getText().isEmpty()) {
						btnAdd.setEnabled(true);
					} else {
						btnAdd.setEnabled(false);
					}
				}
			}
		});

		Control[] list_1 = new Control[] { textDir, btnDir, textEH, btnEH,
				textInstall };
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
	 * Unblock interface items
	 */
	private void unblockItems() {
		lblEH.setForeground(Display.getDefault().getSystemColor(
				SWT.COLOR_TITLE_FOREGROUND));
		textEH.setEnabled(true);
		btnEH.setEnabled(true);
		lblInstall.setForeground(Display.getDefault().getSystemColor(
				SWT.COLOR_TITLE_FOREGROUND));
		textInstall.setEnabled(true);
		btnInstall.setEnabled(true);
		lblChecker.setForeground(Display.getDefault().getSystemColor(
				SWT.COLOR_TITLE_FOREGROUND));
		textChecker.setEnabled(true);
		btnChecker.setEnabled(true);
	}

	/**
	 * Block interface items
	 */
	private void blockItems() {
		lblEH.setForeground(Display.getDefault().getSystemColor(
				SWT.COLOR_TITLE_INACTIVE_FOREGROUND));
		textEH.setEnabled(false);
		btnEH.setEnabled(false);
		lblInstall.setForeground(Display.getDefault().getSystemColor(
				SWT.COLOR_TITLE_INACTIVE_FOREGROUND));
		textInstall.setEnabled(false);
		btnInstall.setEnabled(false);
		lblChecker.setForeground(Display.getDefault().getSystemColor(
				SWT.COLOR_TITLE_INACTIVE_FOREGROUND));
		textChecker.setEnabled(false);
		btnChecker.setEnabled(false);
	}

	/**
	 * Validate data
	 * 
	 * @return true if correct, false otherwise
	 */
	private boolean checkData() {
		if (textEH.getText().equals("")) {
			MessageBox box = new MessageBox(shell, SWT.ICON_ERROR);
			box.setMessage("ElevateHandler directory is empty.");
			box.setText("Error");
			box.open();
			return false;
		}

		if (textInstall.getText().equals("")) {
			MessageBox box = new MessageBox(shell, SWT.ICON_ERROR);
			box.setMessage("Installation jar directory id empry.");
			box.setText("Error");
			box.open();
			return false;
		}

		File file1 = new File(textDir.getText() + File.separator
				+ textEH.getText());
		if (!file1.exists()) {
			MessageBox box = new MessageBox(shell, SWT.ICON_ERROR);
			box.setMessage("Can not find the ElevateHandler file.");
			box.setText("Error");
			box.open();
			return false;
		}

		File file2 = new File(textDir.getText() + File.separator
				+ textInstall.getText());
		if (!file2.exists()) {
			MessageBox box = new MessageBox(shell, SWT.ICON_ERROR);
			box.setMessage("Can not find the Installation jar file.");
			box.setText("Error");
			box.open();
			return false;
		}

		return true;
	}

	/**
	 * Gets C# process path
	 * 
	 * @return C# process path
	 */
	public String getEH() {
		return EH;
	}

	/**
	 * Gets java install process path
	 * 
	 * @return java install process path
	 */
	public String getInstall() {
		return install;
	}

	/**
	 * Gets java checker process path
	 * 
	 * @return java checker process path
	 */
	public String getChecker() {
		return checker;
	}
}
