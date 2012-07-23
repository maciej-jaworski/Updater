package checker;

import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

public class Main {
	/**
	 * checks an number of arguments, waits until specified process works,
	 * starts a new process
	 * 
	 * @param args
	 *            first argument - name of the desired process, second argument
	 *            - path to the program you want to start after the close of the
	 *            process in first argment
	 * 
	 */
	public static void main(String[] args) {
		Process process = null;
		BufferedReader input = null;
		// process_name - name of the desired process
		// program_path - path to the program you want to start after the close
		// of the process in program_name
		String process_name = "", program_path = "";
		// temporary variables
		String line = "", temp = "";
		boolean onList = true;
		// checking number of arguments
		if (args.length == 2) {
			process_name = args[0];
			program_path = args[1];
			// until the process is working
			while (onList) {
				onList = false;
				// get output from tasklist.exe
				try {
					process = Runtime.getRuntime().exec(
							System.getenv("windir") + "\\system32\\"
									+ "tasklist.exe /fo csv /nh");
					input = new BufferedReader(new InputStreamReader(
							process.getInputStream()));
					while ((line = input.readLine()) != null) {
						if (!line.trim().equals("")) {
							line = line.substring(1);
							// if process in output equals process in
							// program_name then set onList to true and break
							temp = line.substring(0, line.indexOf("\""));
							if (process_name.equals(temp)) {
								onList = true;
								break;
							}
						}
					}
				} catch (Exception e) {
					showError(e.getMessage(), "Error");
				} finally {
					if (input != null) {
						try {
							input.close();
						} catch (IOException e) {
							showError("Closing Inputstream failed.", "Error");
						}
					}
				}

				// wait 100 ms before next iteration of loop
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
				}
			}

			// prepare command line
			ArrayList<String> cmdArgs = new ArrayList<String>();
			cmdArgs.add("javaw.exe");
			cmdArgs.add("-jar");
			cmdArgs.add("\"" + program_path + "\"");

			// start new process
			try {
				ProcessBuilder p = new ProcessBuilder(cmdArgs);
				p.start();
			} catch (IOException e) {
				showError("Restarting AutoUpdater failed.", "Error");
			}
		} else {
			showError("Given the wrong number of arguments.", "Error");
		}
	}

	/**
	 * Displays a window with an error
	 * 
	 * @param message
	 *            error message
	 * @param title
	 *            window title
	 */
	private static void showError(String message, String title) {
		JOptionPane pane = new JOptionPane(message, JOptionPane.ERROR_MESSAGE);
		JDialog dialog = pane.createDialog(title);
		dialog.setIconImage(Toolkit.getDefaultToolkit().getImage(
				Main.class.getResource("/images/dialog_error.png")));
		dialog.setAlwaysOnTop(true);
		dialog.setVisible(true);
	}
}
