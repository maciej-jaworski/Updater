package gui;

import java.awt.Toolkit;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolTip;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;
import org.eclipse.wb.swt.SWTResourceManager;

/**
 * MyTray class is responsible for getting events in tray. 
 * By this class we can from the tray check options, check updates, 
 * update programs, show main window gui and exit from application.
 *
 */
public class MyTray {

	private Display display;
	private Shell shell;
	private Tray tray;
	private TrayItem item;
	private Menu menu;				//Menu which store all items from tray.
	private MenuItem checkItem;		//item by which we can check updates for programs.
	private MenuItem exitItem;		//by this item we can exit from application.
	private MenuItem optionsItem;	//this item show the options.
	private MenuItem new_item;		//helpful item to add new item to menu.

	private Gui gui;				//Main gui which is show after click on icon on tray.
	private OptionsGui optionsGui;	//Gui of options which is show when we click on Options in tray.
	private int minItemCount;		
	private double screenWidth;		
	private double screenHeight;

	/**
	 * Constructor of MyTray class.
	 */
	public MyTray() {
		display = new Display();
		shell = new Shell(display);
		tray = display.getSystemTray();

		screenWidth = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		screenHeight = Toolkit.getDefaultToolkit().getScreenSize().getHeight();

		gui = new Gui(this, screenWidth, screenHeight);
		optionsGui = new OptionsGui(shell, screenWidth, screenHeight);
		minItemCount = 5;
	}
	
	
	/**
	 * Function which initialize all menu items and their listeners.
	 * @return
	 */
	public boolean initTray() {
		if (tray == null) {
			MessageBox box = new MessageBox(shell, SWT.ICON_ERROR);
			box.setMessage("Zainicjalizowanie tray'a jest niemo¿liwe. Aplikacja zakoñczy dzia³anie");
			box.setText("Error");
			box.open();
			return false;
		}

		item = new TrayItem(tray, SWT.NONE);
		item.setToolTipText("AutoUpdater");
		item.setImage(SWTResourceManager.getImage(MyTray.class,
				"/images/icon.png"));
		item.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				if(!optionsGui.isVisible())
					gui.Open();
				else{
					gui.Open();
					optionsGui.setActive();
				}
			}
		});

		item.addListener(SWT.MenuDetect, new Listener() {

			@Override
			public void handleEvent(Event event) {
				menu.setVisible(true);
			}
		});

		menu = new Menu(shell, SWT.POP_UP);

		checkItem = new MenuItem(menu, SWT.PUSH);
		checkItem.setText("Check Updates");
		checkItem.setEnabled(false);
		checkItem.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				if (!gui.isBlockTrayMenu()&& !optionsGui.isVisible()) {
					gui.clickCheckUpdatesButton();
				} else {
					showInfo(
							"Menu has been disabled until end of current action.",
							"Information");
				}
			}
		});

		new MenuItem(menu, SWT.SEPARATOR);

		optionsItem = new MenuItem(menu, SWT.PUSH);
		optionsItem.setText("Options");
		optionsItem.setImage(SWTResourceManager.getImage(MyTray.class,
				"/images/mini_options.png"));
		optionsItem.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				if (!gui.isBlockTrayMenu() && !optionsGui.isVisible()) {

					optionsGui.setProxyAddress(gui.getProxyAddress());
					optionsGui.setProxyPort(gui.getProxyPort());

					if (optionsGui.open() != null) {
						String address = optionsGui.getProxy_address();
						int port = optionsGui.getProxy_port();

						if (!address.isEmpty()) {
							gui.saveProxy(address, port);
						} else {
							gui.removeProxy();
						}
						optionsGui.setVisible(true);
					}
				} else {
					if(optionsGui.isVisible()){
						optionsGui.setActive();
					}else{
					showInfo(
							"Menu has been disabled until end of current action.",
							"Information");
					}
				}
			}
		});

		new MenuItem(menu, SWT.SEPARATOR);

		exitItem = new MenuItem(menu, SWT.PUSH);
		exitItem.setText("Wyjœcie");
		exitItem.setImage(SWTResourceManager.getImage(MyTray.class,
				"/images/mini_exit.png"));
		exitItem.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				if (!gui.isBlockTrayMenu() && !optionsGui.isVisible()) {
					gui.cleanUp();
					cleanUp();
				} else {
					showInfo(
							"Menu has been disabled until end of current action.",
							"Information");
				}
			}
		});

		return true;
	}
	
	/**
	 * Function which show some message.
	 * @param message contents of message
	 * @param title title of message
	 */
	private void showInfo(final String message, final String title) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				MessageBox box = new MessageBox(shell, SWT.ICON_INFORMATION);
				box.setMessage(message);
				box.setText(title);
				box.open();
			}
		});
	}

	/**
	 * Method which call initData() in Gui class.
	 * @return
	 */
	public boolean initData() {
		return gui.initData();
	}

	/**
	 * This method add items to menu. Mostly it is program which is added to AutoUpdater.
	 * @param name name of program.
	 */
	public void addItemToList(String name) {
		int ItemCount = menu.getItemCount();

		checkItem.setEnabled(true);

		if (ItemCount == minItemCount) {
			new MenuItem(menu, SWT.SEPARATOR, 2);
		}

		new_item = new MenuItem(menu, SWT.PUSH, 2 + (menu.getItemCount()
				- minItemCount - 1));
		new_item.setText(name);
		new_item.setImage(SWTResourceManager.getImage(MyTray.class,
				"/images/mini_orange.png"));
		new_item.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				MenuItem item = (MenuItem) event.widget;

				if (!gui.isBlockTrayMenu() && !optionsGui.isVisible()) {
					gui.clickUpdateButton(item.getText());
				} else {
					showInfo(
							"Menu has been disabled until end of current action.",
							"Information");
				}
			}
		});
	}
	
	/**
	 * This method remove item from menu. Mostly it is program which is added to AutoUpdater.
	 * @param name name of program.
	 */
	public void removeItemFromList(String name) {
		menu.getItem(findSoftwareInMenu(name)).dispose();

		if (menu.getItemCount() == minItemCount + 1) {
			menu.getItem(1).dispose();
		}

		if (menu.getItemCount() == minItemCount) {
			checkItem.setEnabled(false);
		}
	}

	/**
	 * This method looking for the item in menu with the specific name.
	 * @param name of item.
	 * @return
	 */
	public int findSoftwareInMenu(String name) {
		for (int i = 0; i < menu.getItemCount(); ++i) {
			if (name.equals(menu.getItem(i).getText())) {
				return i;
			}
		}

		return -1;
	}

	/**
	 * This method edit name of item in menu
	 * @param old_name old name of item, which will be replaced
	 * @param new_name	new name of item.
	 */
	public void editMenuItem(String old_name, String new_name) {
		int index = findSoftwareInMenu(old_name);
		menu.getItem(index).setText(new_name);
	}
	
	
	/**
	 * This method set appropriate icon of status. If exist some new update for program, the icon will be red. 
	 * Which means the program hasn't the newest version. Otherwise icon will be green.
	 * @param program_name name of program 
	 * @param status status of program
	 */
	public void setStatus(String program_name, boolean status) {
		if (status) {
			getItem(program_name).setImage(
					SWTResourceManager.getImage(MyTray.class,
							"/images/mini_green.png"));
		} else {
			getItem(program_name).setImage(
					SWTResourceManager.getImage(MyTray.class,
							"/images/mini_red.png"));
		}
	}
	
	/**
	 * Returns item by his name.
	 * @param name of item
	 * @return MenuItem.
	 */
	private MenuItem getItem(String name) {
		for (int i = 0; i < menu.getItemCount(); ++i) {
			if (menu.getItem(i).getText().equals(name))
				return menu.getItem(i);
		}

		return null;
	}
	
	/**
	 * Returns appropriate message. 
	 * @param message contents of message.
	 */
	public void showInfoToolTip(String message) {
		ToolTip tip = new ToolTip(shell, SWT.BALLOON | SWT.ICON_INFORMATION);
		tip.setMessage(message);
		tip.setText("Information");
		item.setToolTip(tip);
		tip.setVisible(true);
	}
	
	
	/**
	 * Returns display.
	 * @return
	 */
	public Display getDisplay() {
		return this.display;
	}
	
	/**
	 * Loop of event. 
	 */
	public void EventLoop() {
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
	
	/**
	 * New thread which make shell and display dispose.
	 */
	public void cleanUp() {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if (!shell.isDisposed()) {
					shell.dispose();
				}

				if (!display.isDisposed()) {
					display.dispose();
				}
			}
		});
	}
}
