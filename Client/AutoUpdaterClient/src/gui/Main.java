package gui;

import xml_creator.Gui;

/** Main class */
public class Main {
	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// if first argument is -config then open configuration mode else show
		// loading screen and try to start application
		if (args.length == 1 && args[0].equals("-config")) {
			Gui.main(null);
		} else {
			SplashScreen.main(null);

			MyTray tray = new MyTray();
			if (!tray.initTray()) {
				return;
			}
			if (tray.initData())
				tray.EventLoop();

			System.exit(0);
		}
	}
}
