package gui;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;

public class OptionsGui extends Dialog {

	protected Object result;
	protected Shell shell;
	private Text textProxyAddress;		//Text field which content address of proxy.
	private Text textProxyPort;			//Text field which content port of proxy.
	private Button btnCancel;			//Button which cancel the settings
	private Button btnApply;			//Button which save the setting
	private Button btnAutostart;		//Button which set the autostart
	private Button btnDefaultProxyPort;	//Button which set the default port of proxy
	private Button btnUseProxy;			//Button which set to use new write proxy.
	private Button btnTestConnection;	//By this button we can check the test connection.
	private Button btnDeleteFromTemp;	//By this button we can delete trash from Temp directory.
	private Group grpGeneral;			
	private CLabel lblProxyAddress;
	private CLabel lblProxyPort;
	private Group grpConnection;
	private Label lblFilesSize;			//Label which show files size in temp.
	private TestConnectionGui testConnectionGui;	//Gui which is showing when the test connection is started.
	private String proxy_address;		//address of proxy
	private int proxy_port;				//port of proxy
	private double screenWidth;
	private double screenHeight;
	private long size_of_files;
	private boolean isVisible = false;


	/**
	 * Constructor of dialog options gui.
	 * @param parent Shell of dialog.
	 * @param screenWidth width of window.
	 * @param screenHeight	height of window.
	 */
	public OptionsGui(Shell parent, double screenWidth, double screenHeight) {
		super(parent, SWT.TITLE);
		setText("Options");

		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;

		testConnectionGui = new TestConnectionGui(parent, screenWidth,
				screenHeight);
		proxy_address = "";
		proxy_port = 0;
	}

	/**
	 * Open the dialog.
	 * 
	 * @return the result
	 */
	public Object open() {
		createContents();
		centerWindow();
		size_of_files = 0;
		if (checkAutostart()) {
			btnAutostart.setSelection(true);
		} else {
			btnAutostart.setSelection(false);
		}
		setVisible(true);
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
		
		
		shell = new Shell(getParent(), SWT.TITLE | SWT.SYSTEM_MODAL);
		shell.setSize(328, 316);
		shell.setText(getText());

		grpConnection = new Group(shell, SWT.NONE);
		grpConnection.setText("Connection");
		grpConnection.setBounds(6, 4, 309, 151);

		lblProxyAddress = new CLabel(grpConnection, SWT.NONE);
		lblProxyAddress.setEnabled(false);
		lblProxyAddress.setForeground(Display.getDefault().getSystemColor(
				SWT.COLOR_TITLE_INACTIVE_FOREGROUND));
		lblProxyAddress.setBounds(25, 41, 88, 21);
		lblProxyAddress.setText("Proxy address:");

		lblProxyPort = new CLabel(grpConnection, SWT.NONE);
		lblProxyPort.setForeground(Display.getDefault().getSystemColor(
				SWT.COLOR_TITLE_INACTIVE_FOREGROUND));
		lblProxyPort.setBounds(25, 68, 88, 21);
		lblProxyPort.setText("Proxy port:");
		
		
		configTextProxyAdress();
		configTextProxyPort();
		configBtnDefaultProxyPort();
		configBtnUseProxy();
		configBtnTestConnection();

		grpGeneral = new Group(shell, SWT.NONE);
		grpGeneral.setText("General");
		grpGeneral.setBounds(6, 159, 309, 81);

		configBtnAutostart();
		configBtnApply();
		configBtnCancel();

		if (!proxy_address.isEmpty()) {
			btnUseProxy.setSelection(true);
			lblProxyAddress.setForeground(Display.getDefault().getSystemColor(
					SWT.COLOR_TITLE_FOREGROUND));
			lblProxyPort.setForeground(Display.getDefault().getSystemColor(
					SWT.COLOR_TITLE_FOREGROUND));
			textProxyAddress.setEnabled(true);
			textProxyPort.setEnabled(true);
			btnDefaultProxyPort.setEnabled(true);
			btnTestConnection.setEnabled(true);

			if (proxy_port == 0) {
				textProxyPort.setEnabled(false);
				btnDefaultProxyPort.setSelection(true);
			}
		}

		Control[] list_1 = new Control[] { btnUseProxy, textProxyAddress,
				textProxyPort, btnDefaultProxyPort, btnTestConnection };
		grpConnection.setTabList(list_1);
		Control[] list_2 = new Control[] { btnAutostart };
		grpGeneral.setTabList(list_2);
		Control[] list_3 = new Control[] { grpConnection, grpGeneral,
				btnCancel, btnApply };

		configBtnDeleteFromTemp();
		Label lblTemporarySize = new Label(grpGeneral, SWT.NONE);
		lblTemporarySize.setBounds(23, 52, 106, 15);
		lblTemporarySize.setText("Temporary size:");
		
		lblFilesSize = new Label(grpGeneral, SWT.NONE);
		lblFilesSize.setBounds(195, 52, 65, 15);
		checkSize();
		
		shell.setTabList(list_3);
	}

	/**
	 * Set configuration of text field with address of proxy.
	 */
	public void configTextProxyAdress(){
		textProxyAddress = new Text(grpConnection, SWT.BORDER);
		textProxyAddress.setEnabled(false);
		textProxyAddress.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				btnApply.setEnabled(true);
			}
		});
		textProxyAddress.setBounds(112, 41, 184, 21);
		if (!proxy_address.isEmpty()) {
			textProxyAddress.setText(proxy_address);
		}
	}
	
	/**
	 * Set configuration of text field with port of proxy.
	 */
	public void configTextProxyPort(){
		textProxyPort = new Text(grpConnection, SWT.BORDER);
		textProxyPort.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				btnApply.setEnabled(true);
			}
		});
		textProxyPort.setBounds(112, 68, 184, 21);
		textProxyPort.setEnabled(false);
		if (proxy_port != 0) {
			textProxyPort.setText(Integer.toString(proxy_port));
		}
	}
	
	/**
	 * Set configuration of button which set the default port of proxy.
	 */
	public void configBtnDefaultProxyPort(){
		btnDefaultProxyPort = new Button(grpConnection, SWT.CHECK);
		btnDefaultProxyPort.setEnabled(false);
		btnDefaultProxyPort.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				btnApply.setEnabled(true);
				textProxyPort.setText("");

				if (btnDefaultProxyPort.getSelection()) {
					textProxyPort.setEnabled(false);
				} else {
					textProxyPort.setEnabled(true);
				}
			}
		});
		btnDefaultProxyPort.setBounds(25, 95, 122, 25);
		btnDefaultProxyPort.setText("Default proxy port ");
	}
	
	/**
	 * Set configuration of button which start to use new write proxy.
	 */
	public void configBtnUseProxy(){
		btnUseProxy = new Button(grpConnection, SWT.CHECK);
		btnUseProxy.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				btnApply.setEnabled(true);
				textProxyAddress.setText("");
				textProxyPort.setText("");
	
				if (btnUseProxy.getSelection()) {
					lblProxyAddress.setForeground(Display.getDefault()
							.getSystemColor(SWT.COLOR_TITLE_FOREGROUND));
					lblProxyPort.setForeground(Display.getDefault()
							.getSystemColor(SWT.COLOR_TITLE_FOREGROUND));
					textProxyAddress.setEnabled(true);
					textProxyPort.setEnabled(true);
					btnDefaultProxyPort.setEnabled(true);
					btnTestConnection.setEnabled(true);
				} else {
					lblProxyAddress
							.setForeground(Display.getDefault().getSystemColor(
									SWT.COLOR_TITLE_INACTIVE_FOREGROUND));
					lblProxyPort
							.setForeground(Display.getDefault().getSystemColor(
									SWT.COLOR_TITLE_INACTIVE_FOREGROUND));
					textProxyAddress.setEnabled(false);
					textProxyPort.setEnabled(false);
					btnDefaultProxyPort.setEnabled(false);
					btnTestConnection.setEnabled(false);
				}
			}
		});
		btnUseProxy.setBounds(10, 17, 75, 25);
		btnUseProxy.setText("Use proxy");
	}
	
	/**
	 * Set configuration of button responsible for test connection.
	 */
	public void configBtnTestConnection(){
		btnTestConnection = new Button(grpConnection, SWT.NONE);
		btnTestConnection.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				if (checkData()) {
					testConnectionGui.setHost(proxy_address);
					testConnectionGui.setPort(proxy_port);
					testConnectionGui.open();
				}
			}
		});
		btnTestConnection.setBounds(192, 112, 104, 25);
		btnTestConnection.setEnabled(false);
		btnTestConnection.setText("Test connection");
	}
	
	/**
	 * Set configuration of button which set autostart.
	 */
	public void configBtnAutostart(){
		btnAutostart = new Button(grpGeneral, SWT.CHECK);
		btnAutostart.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				btnApply.setEnabled(true);
			}
		});
		btnAutostart.setBounds(23, 21, 75, 25);
		btnAutostart.setText("Autostart");
	}
	
	/**
	 * Set configuration of button which apply and save settings.
	 */
	public void configBtnApply(){
		btnApply = new Button(shell, SWT.NONE);
		btnApply.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				if (checkData()) {
					if (btnAutostart.getSelection()) {
						setAutostart(true);
					} else {
						setAutostart(false);
					}
					result = 1;
					setVisible(false);
					shell.dispose();
				}
				
			}
		});
		btnApply.setBounds(240, 253, 75, 25);
		btnApply.setText("Apply");
		btnApply.setEnabled(false);
	}
	
	/**
	 * Set configuration of button which cancel the settings.
	 */
	public void configBtnCancel(){
		btnCancel = new Button(shell, SWT.NONE);
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				result = null;
				setVisible(false);
				shell.dispose();
			}
		});
		btnCancel.setBounds(159, 253, 75, 25);
		btnCancel.setText("Cancel");
	}
	
	/**
	 * Set configuration of button which deletes trash from Temp directory.
	 */
	public void configBtnDeleteFromTemp(){
		btnDeleteFromTemp = new Button(grpGeneral, SWT.NONE);
		btnDeleteFromTemp.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				try {
					MessageBox messageBox = new MessageBox(shell, SWT.ICON_QUESTION
				            | SWT.YES | SWT.NO);
				        messageBox.setMessage("Files will be deleted irrevocably. Are you sure?");
				        messageBox.setText("Deleting files.");
				        int response = messageBox.open();
				        if (response == SWT.YES)
				        	cleanFromTemp(new File(getTempEnv()+"\\updater"));
				    MessageBox messageBox2 = new MessageBox(shell, SWT.ICON_INFORMATION);
					    messageBox2.setMessage("Files have been deleted.");
					    messageBox2.setText("Deleting files.");
					    messageBox2.open();
				} catch (IOException e) {
					MessageBox box = new MessageBox(shell, SWT.ICON_ERROR);
					box.setMessage("Deleting temporary files failed.");
					box.setText("Error");
					box.open();
				}
			}
		});
		btnDeleteFromTemp.setImage(SWTResourceManager.getImage(OptionsGui.class, "/images/bin1.jpg"));
		btnDeleteFromTemp.setBounds(260, 47, 39, 25);
		
		
	}
	
	/**
	 * Check autostart is set on true or false.
	 * @return
	 */
	private boolean checkAutostart() {
		String keyName = "HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Run";
		String valueName = "AutoUpdater";

		try {
			Process p = Runtime.getRuntime().exec(
					"REG QUERY " + keyName + " /f " + valueName);
			BufferedReader input = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			String line = "";
			String output = "";

			while ((line = input.readLine()) != null) {
				output += line;
			}

			if (output.contains(valueName)) {
				return true;
			} else {
				return false;
			}
		} catch (IOException e) {
			MessageBox box = new MessageBox(shell, SWT.ICON_ERROR);
			box.setMessage("Reading the startup options failed.");
			box.setText("Error");
			box.open();
		}

		return false;
	}
	
	/**
	 * Set status of autostart on true or false.
	 * @param status
	 */
	private void setAutostart(boolean status) {
		File file = new File(MyTray.class.getProtectionDomain().getCodeSource()
				.getLocation().getPath());
		String exe_path = file.getAbsolutePath();
		exe_path = exe_path.substring(0, exe_path.lastIndexOf(File.separator));
		exe_path = exe_path.replaceAll("%20", " ");

		String keyName = "HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Run";
		String valueName = "AutoUpdater";
		String type = "REG_SZ";
		String data = "\"javaw.exe -jar \\\"" + exe_path
				+ "\\AutoUpdater.jar\\\"\"";

		try {
			if (status) {
				Runtime.getRuntime().exec(
						"REG ADD " + keyName + " /v " + valueName + " /t "
								+ type + " /d " + data + " /f");
			} else {
				Runtime.getRuntime().exec(
						"REG DELETE " + keyName + " /v " + valueName + " /f ");
			}

		} catch (IOException e) {
			MessageBox box = new MessageBox(shell, SWT.ICON_ERROR);
			box.setMessage("Setting the startup failed.");
			box.setText("Error");
			box.open();
		}
	}
	
	/**
	 * This method check data about proxy.
	 * @return
	 */
	private boolean checkData() {
		if (btnUseProxy.getSelection()) {
			if (btnDefaultProxyPort.getSelection()) {
				proxy_port = 0;
			} else if (textProxyPort.getText().isEmpty()) {
				MessageBox box = new MessageBox(shell, SWT.ICON_ERROR);
				box.setMessage("Proxy port is empty.");
				box.setText("Error");
				box.open();
				return false;
			} else {
				int port = 0;
				try {
					port = Integer.parseInt(textProxyPort.getText());
				} catch (NumberFormatException e) {
					MessageBox box = new MessageBox(shell, SWT.ICON_ERROR);
					box.setMessage("Proxy port must be a positive integer.");
					box.setText("Error");
					box.open();
					return false;
				}

				if (port < 0) {
					MessageBox box = new MessageBox(shell, SWT.ICON_ERROR);
					box.setMessage("Proxy port must be a positive integer.");
					box.setText("Error");
					box.open();
					return false;
				}

				proxy_port = port;
			}

			if (textProxyAddress.getText().isEmpty()) {
				MessageBox box = new MessageBox(shell, SWT.ICON_ERROR);
				box.setMessage("Proxy server is empty.");
				box.setText("Error");
				box.open();
				return false;
			}

			proxy_address = textProxyAddress.getText();
		} else {
			proxy_address = "";
			proxy_port = 0;
		}

		return true;
	}
	
	/**
	 * Center the window.
	 */
	private void centerWindow() {
		shell.setLocation((int) (screenWidth / 2)
				- (shell.getBounds().width / 2), (int) (screenHeight / 2)
				- (shell.getBounds().height / 2));
	}
	
	/**
	 * Checks size of files in temp directory and set this size in label.
	 */
	private void checkSize(){
		try {
			checkSizeFiles(new File(getTempEnv()+"\\updater"));
		} catch (IOException e) {
			
		}

		if(size_of_files < 1024){

			lblFilesSize.setText(size_of_files + " B");
			
		}else if(size_of_files >= 1024 && size_of_files < 1024*1024 ){
			
			float size1 = round((double) (size_of_files/1024),2);

			lblFilesSize.setText(size1 + " kB");
			
		}else if(size_of_files >= 1024*1024 && size_of_files < 1024*1024*1024){
			float size1 = round((double) (size_of_files/(1024*1024)),2);
			lblFilesSize.setText(size1 + " MB");
		}else{
			float size1 = round((double) size_of_files/(1024*1024*1024),2);
			lblFilesSize.setText(size1 + " GB");
		}
	}
	
	/**
	 * Checks size of files in temp directory.
	 */
	private void checkSizeFiles(File path) throws IOException {
		if (path.exists()) {
			File[] files = path.listFiles();
			for (int i = 0; i < files.length; i++) {				
				if (files[i].isDirectory()) {
					checkSizeFiles(files[i]);
				} else {
					size_of_files = size_of_files + files[i].length();
				}
			}
		}
	}
	
	/**
	 * Cleans from Temp directory.
	 * @param path path of Temp directory.
	 * @throws IOException 	Signals that an I/O exception of some sort has occurred. 
	 * 						This class is the general class of exceptions produced by failed or interrupted I/O operations.
	 */
	private void cleanFromTemp(File path) throws IOException {
		if (path.exists()) {
			File[] files = path.listFiles();
			for (int i = 0; i < files.length; i++) {				
				if (files[i].isDirectory()) {
					cleanFromTemp(files[i]);
					files[i].delete();
				} else {
					files[i].delete();
				}
			}

			path.delete();
		}
	}
	
	/**
	 * Sets shell of options gui to active.
	 */
	public void setActive(){
		this.shell.setActive();
	}
	
	/**
	 * Sets address of proxy.
	 * @param address address of proxy.
	 */
	public void setProxyAddress(String address) {
		this.proxy_address = address;
	}
	
	/**
	 * Sets port of proxy.
	 * @param port
	 */
	public void setProxyPort(int port) {
		this.proxy_port = port;
	}
	
	/**
	 * Gets address of proxy.
	 * @return
	 */
	public String getProxy_address() {
		return proxy_address;
	}
	
	/**
	 * Returns port of proxy.
	 * @return
	 */
	public int getProxy_port() {
		return proxy_port;
	}
	
	/**
	 * Returns path of Temp directory.
	 * @return
	 */
	private String getTempEnv() {
		Map<String, String> env;
		env = System.getenv();
		return env.get("TEMP");
	}
	
	/**
	 * Returns visibility of this window.
	 * @return
	 */
	public boolean isVisible() {
		return isVisible;
	}
	
	/**
	 * Sets visibility on true or false.
	 * @param isVisible
	 */
	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}
	
	/**
	 * Helpful method to set limit of digits after decimal.
	 * @param f	number which will be rounded.
	 * @param places after decimal.
	 * @return
	 */
	public static float round(double f, int places){  
		float temp = (float) (f * (Math.pow(10, places)));

		temp = (Math.round(temp));

		temp = temp / (int) (Math.pow(10, places));

		return temp;

	  }
}
