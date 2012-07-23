package updater;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Observable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Handles extracting files. Is able to communicate over the Observer and used
 * for this class Message.
 */
public class UnZip extends Observable implements Runnable {
	/** max number of bytes which can be read/write in one iteration of loop */
	private final int buffer = 1024;
	/** current size of the unzipped files */
	private int currentSize;
	/** path to zip file and path where it should be unzipped */
	private String source_path, dest_path;
	/** id of actualization which files are currently extracting */
	private String actualization_id;

	/**
	 * Constructor
	 * 
	 * @param source
	 *            path to zip file
	 * @param dest
	 *            path to extract
	 * @param actualization_id
	 *            id of current actualization
	 */
	public UnZip(String source, String dest, String actualization_id) {
		this.source_path = source;
		this.dest_path = dest;
		this.actualization_id = actualization_id;
		this.currentSize = 0;
	}

	/**
	 * Notifies all observers
	 * 
	 * @param message
	 *            message to send
	 */
	public void setMessage(Message message) {
		setChanged();
		notifyObservers(message);
	}

	/**
	 * Begins unpacking
	 */
	@Override
	public void run() {
		setMessage(new Message("", false, true, false));
		setMessage(new Message("Start extracting...", true, false, false));
		extractFile();
		setMessage(new Message("Finish extracting...", true, false, false));
		setMessage(new Message("0", false, false, true));
		setMessage(new Message("", true, false, false));
	}

	/**
	 * Copies bytes from one place to another
	 * 
	 * @param inStream
	 *            source file
	 * @param outStream
	 *            destination path
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	private void copyBits(InputStream inStream, OutputStream outStream)
			throws IOException {

		byte[] buf = new byte[buffer];
		int l;
		while ((l = inStream.read(buf)) >= 0) {
			outStream.write(buf, 0, l);
			currentSize += l;
			setMessage(new Message(Integer.toString(currentSize), false, false,
					true));
		}

		inStream.close();
		outStream.close();
	}

	/**
	 * Extract source file to destination path
	 */
	public void extractFile() {
		Enumeration<? extends ZipEntry> enumEntries;
		ZipFile zip = null;
		currentSize = 0;

		// creates destination path if does not exists
		File file1 = new File(dest_path);
		if (!file1.exists()) {
			file1.mkdirs();
		}

		try {
			zip = new ZipFile(source_path);
			enumEntries = zip.entries();
			// iterates over all elements
			while (enumEntries.hasMoreElements()) {
				ZipEntry zipentry = (ZipEntry) enumEntries.nextElement();
				// if a directory, it checks whether there is, as it does not
				// exist it creates
				if (zipentry.isDirectory()) {
					setMessage(new Message("Extract directory : "
							+ zipentry.getName(), true, false, false));

					File file = new File(dest_path + File.separator
							+ zipentry.getName());
					if (!file.exists()) {
						file.mkdirs();
					}
					// go to next element
					continue;
				}
				setMessage(new Message("Extract fille : " + zipentry.getName(),
						true, false, false));
				// and if the file, it copies it
				copyBits(zip.getInputStream(zipentry), new FileOutputStream(
						dest_path + File.separator + zipentry.getName()));
			}
		} catch (IOException e) {
			setMessage(new Message("Extracting the zip file " + zip.getName()
					+ " failed.", actualization_id));
		} finally {
			// try to close file
			try {
				if (zip != null)
					zip.close();
			} catch (IOException e) {
				setMessage(new Message("Closing the zip file " + zip.getName()
						+ " failed.", actualization_id));
				return;
			}
		}
	}

	/**
	 * Gets the size of the source zip file
	 * 
	 * @return zip file size or -1 if error
	 */
	public int getZipSize() {
		int temp_size = 0;
		ZipFile zipFile = null;

		try {
			zipFile = new ZipFile(source_path);
			Enumeration<? extends ZipEntry> e = zipFile.entries();

			while (e.hasMoreElements()) {
				ZipEntry entry = (ZipEntry) e.nextElement();
				temp_size += entry.getSize();
				entry = null;
			}
			zipFile.close();
		} catch (IOException ex) {
			setMessage(new Message("Download the file size " + source_path
					+ " failed.", actualization_id));
			return -1;
		}

		return temp_size;
	}

	/**
	 * Gets source file path
	 * 
	 * @return file path
	 */
	public String getsource_path() {
		return source_path;
	}

	/**
	 * Sets source file path
	 * 
	 * @param source_path
	 *            path to zip file
	 */
	public void setsource_path(String source_path) {
		this.source_path = source_path;
	}

	/**
	 * Gets destination path
	 * 
	 * @return destination path
	 */
	public String getdest_path() {
		return dest_path;
	}

	/**
	 * Sets destination path
	 * 
	 * @param dest_path
	 *            destination path
	 */
	public void setdest_path(String dest_path) {
		this.dest_path = dest_path;
	}
}