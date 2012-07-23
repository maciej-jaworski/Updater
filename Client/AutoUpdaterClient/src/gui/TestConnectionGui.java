package gui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

/** Window for testing connection setings */
public class TestConnectionGui extends Dialog implements Runnable {

	protected Object result;
	protected Shell shell;

	/** proxy server */
	private String host;
	/** proxy port */
	private int port;
	/** screen resolution width and height */
	private double screenWidth;
	private double screenHeight;

	/**
	 * Create the dialog
	 * 
	 * @param parent
	 *            the parent shell, or null to create a top-level shell
	 * @param screenWidth
	 *            screen resolution width
	 * @param screenHeight
	 *            screen resolution height
	 */
	public TestConnectionGui(Shell parent, double screenWidth,
			double screenHeight) {
		super(parent, SWT.TITLE);
		setText("Test connection");

		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
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

		// start new thread
		new Thread(this).start();

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
		shell.setSize(420, 89);
		shell.setText(getText());

		Composite composite = new Composite(shell, SWT.NONE);
		composite.setBounds(0, 0, 416, 64);

		CLabel lblItMayTake = new CLabel(composite, SWT.NONE);
		lblItMayTake.setAlignment(SWT.CENTER);
		lblItMayTake.setBounds(10, 20, 396, 21);
		lblItMayTake.setText("It may take some time...");
	}

	/**
	 * Tests proxy connection
	 * 
	 * @param host
	 *            proxy server
	 * @param port
	 *            proxy port
	 * @return true if the connection is successful, false otherwise
	 * @throws MalformedURLException
	 *             if no protocol is specified, or an unknown protocol is found,
	 *             or spec is null
	 * @throws IOException
	 *             if an I/O exception occurs
	 */
	private boolean testProxyServer(String host, int port)
			throws MalformedURLException, IOException {
		Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host,
				port));
		URL url = new URL("http://www.google.pl");
		HttpURLConnection uc = (HttpURLConnection) url.openConnection(proxy);
		uc.connect();

		String page = "", line = "";
		BufferedReader in = new BufferedReader(new InputStreamReader(
				uc.getInputStream()));
		while ((line = in.readLine()) != null) {
			page += line + "\n";
		}

		if (page.isEmpty()) {
			return false;
		} else {
			return true;
		}
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
				MessageBox box = new MessageBox(shell, SWT.ICON_ERROR);
				box.setMessage(message);
				box.setText(title);
				box.open();
				disposeShell();
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
				MessageBox box = new MessageBox(shell, SWT.ICON_INFORMATION);
				box.setMessage(message);
				box.setText(title);
				box.open();
				disposeShell();
			}
		});
	}

	/**
	 * Close shell
	 */
	private void disposeShell() {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				shell.dispose();
			}
		});
	}

	/**
	 * Starts connection test
	 */
	@Override
	public void run() {
		try {
			if (!testProxyServer(host, port)) {
				showError("Proxy test connection failed.", "Error");
				result = -1;
				return;
			} else {
				showInfo("Proxy test connection succeeded.", "Error");
				result = 1;
				return;
			}
		} catch (IOException e) {
			showError("Proxy test failed.", "Error");
			result = -1;
		}
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
	 * Sets proxy server
	 * 
	 * @param host
	 *            proxy server
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * Sets proxy port
	 * 
	 * @param port
	 *            proxy port
	 */
	public void setPort(int port) {
		this.port = port;
	}
}