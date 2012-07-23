package gui;

import java.awt.Toolkit;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;

/** Loading screen */
public class SplashScreen extends Shell implements Runnable {

	private Composite composite;

	/** varaibles for images of background and progress bar */
	private Image background;
	private Image arrow;
	/** screen resolution width and height */
	private double screenWidth;
	private double screenHeight;
	/** position of progress bar and how much time left */
	private int bar_x, bar_y, time_left;
	/** varaible for duration in milisecond and delay time in miliseconds */
	private final int second = 2000, delay = 50;
	/** current rotation */
	private float rotation;

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			Display display = Display.getDefault();
			SplashScreen shell = new SplashScreen(display);

			shell.open();
			shell.layout();
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}

			display.dispose();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the shell.
	 * 
	 * @param display
	 */
	public SplashScreen(Display display) {
		super(display, SWT.SYSTEM_MODAL);
		// load images
		background = SWTResourceManager.getImage(Main.class,
				"/images/ss_background.png");
		arrow = SWTResourceManager.getImage(Main.class, "/images/ss_arrow.png");
		// get screen resolution
		screenWidth = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		screenHeight = Toolkit.getDefaultToolkit().getScreenSize().getHeight();

		createContents();
		centerWindow();
		// set position, rotation and time_left
		bar_y = 81;
		bar_x = 88;
		rotation = 0;
		time_left = delay;
		// start nwe therad
		new Thread(this).start();
	}

	/**
	 * Create contents of the shell.
	 */
	protected void createContents() {
		setText("SWT Application");
		setSize(200, 250);

		composite = new Composite(this, SWT.DOUBLE_BUFFERED);
		composite.setBounds(0, 0, 198, 248);
		composite.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent arg0) {
				// draw progress bar - rotating arrow
				arg0.gc.drawImage(background, 0, 0);

				Transform oldTransform = new Transform(arg0.gc.getDevice());
				arg0.gc.getTransform(oldTransform);

				Transform transform = new Transform(Display.getDefault());
				transform.translate(bar_x + arrow.getBounds().width / 2, bar_y
						+ arrow.getBounds().height / 2);
				transform.rotate(rotation);
				transform.translate(-bar_x - arrow.getBounds().width / 2,
						-bar_y - arrow.getBounds().height / 2);

				arg0.gc.setTransform(transform);
				arg0.gc.drawImage(arrow, bar_x, bar_y);
				arg0.gc.setTransform(oldTransform);

				transform.dispose();
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
	 * Redraw composite
	 */
	private void updateComposite() {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				composite.redraw();
			}
		});
	}

	/**
	 * Dispose all resource - images, shell
	 */
	private void cleanUp() {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				background.dispose();
				arrow.dispose();
				getShell().dispose();
			}
		});
	}

	/**
	 * Simulates loding
	 */
	@Override
	public void run() {
		while (time_left < second) {
			rotation = rotation + 5;
			if (rotation == 360) {
				rotation = 0;
			}

			updateComposite();

			try {
				Thread.sleep(delay);
			} catch (InterruptedException e) {
				return;
			}

			time_left += delay;
		}

		cleanUp();
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
