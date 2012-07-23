package updater;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.MalformedInputException;
import java.util.Observable;

/**
 * The URLdownload class is responsible for downloading data from server.
 * @author Pawel
 *
 */
public class URLdownload extends Observable implements Runnable {

	private final int MAX_BUFFER_SIZE = 1024;
	private final int timeout = 60000;			//value which say how long thread should wait for connecting to server
	private long start_time, elapsed_time;		//values which helps showing speed of downloading
	private boolean override;					//flags which say that the file should be override or not
	private Status status;						//this value say what is the status of downloading
	private String actualization_id;			//id of actualization which files are currently downloading
	private String server_address, dest_path;	
	private String proxy_server;
	private int proxy_port;
	
	/**
	 * Constructor of URLdownload class.
	 * @param proxy_server server proxy
	 * @param proxy_port port proxy
	 * @param server_address server address from where data will be downloading 
	 * @param dest_path destination path where data from server will be saved
	 * @param id helpful variable which has id of actualization
	 */
	public URLdownload(String proxy_server, int proxy_port,
			String server_address, String dest_path, String id) {
		this.proxy_server = proxy_server;
		this.proxy_port = proxy_port;
		this.server_address = server_address;
		this.dest_path = dest_path;
		this.actualization_id = id;
		this.status = Status.ST_WAIT;
		this.override = false;
	}
	
	/**
	* Notifies all observers
	* 
	* @param message
	*			message to send
	*/
	public void setMessage(Message message) {
		setChanged();
		notifyObservers(message);
	}
	
	/**
	 * This function start new thread, which begins
	 * connecting and downloading from server 
	 */
	public void run() {
		setMessage(new Message("Download: " + server_address, true, false,
				false));
		setMessage(new Message("", false, true, false));

		status = Status.ST_DOWNLOAD;
		download(server_address, dest_path);

		if (status != Status.ST_PAUSE) {
			setMessage(new Message("", true, false, false));
			setMessage(new Message("0", false, false, true));
		}
	}
	
	/**
	 * Main function responsible for connecting and downloading from server
	 * @param server_address server address from where data will be downloading 
	 * @param dest_path destination path where data from server will be saved
	 */
	public void download(String server_address, String dest_path) {
		URL url = null;
		RandomAccessFile file = null;
		InputStream stream = null;
		HttpURLConnection connection = null;
		Proxy proxy = null;

		long downloaded = 0;
		int counter = 0;
		int ByteRead, ByteWritten = 0;
		byte[] buf = null;

		try {
			url = new URL(server_address);
		} catch (MalformedURLException e) {
			setMessage(new Message(e.getMessage(), actualization_id));
			return;
		}

		try {
			if (!proxy_server.isEmpty()) {
				proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(
						proxy_server, proxy_port));
				connection = (HttpURLConnection) url.openConnection(proxy);
			} else {
				connection = (HttpURLConnection) url.openConnection();
			}

			file = new RandomAccessFile(new File(dest_path), "rw");

			if (override == false) {
				file.setLength(0);
			} else {
				downloaded = file.length();
				file.seek(downloaded);
			}

			connection.setRequestProperty("Range", "bytes=" + downloaded + "-");
			connection.connect();

			if (connection.getResponseCode() / 100 != 2) {
				setMessage(new Message("Server returned http response code "
						+ connection.getResponseCode() + ", " + server_address,
						actualization_id));
				return;
			}

			stream = connection.getInputStream();
			buf = new byte[MAX_BUFFER_SIZE];
			start_time = System.currentTimeMillis();

			ByteWritten += downloaded;

			while ((ByteRead = stream.read(buf)) != -1) {
				file.write(buf, 0, ByteRead);
				ByteWritten += ByteRead;

				elapsed_time = System.currentTimeMillis() - start_time;
				++counter;

				setMessage(new Message(Integer.toString(ByteWritten), false,
						false, true));

				if (counter % 100 == 0) {
					setMessage(new Message(
							Double.toString(((float) ByteWritten / 1024)
									/ ((float) elapsed_time / 1000)), false,
							true, false));
					counter = 0;
				}

				if (status == Status.ST_PAUSE || status == Status.ST_CANCEL) {
					break;
				}
			}
		} catch (IOException e) {
			setMessage(new Message(e.getMessage(), actualization_id));
			return;
		} finally {
			if (file != null) {
				try {
					file.close();
				} catch (IOException e) {
					setMessage(new Message(e.getMessage(), actualization_id));
				}
			}

			if (stream != null) {
				try {
					stream.close();
				} catch (Exception e) {
					setMessage(new Message(e.getMessage(), actualization_id));
				}
			}
		}
	}

	/**
	 * Function responsible for getting size of file which will be downloading from server
	 * @return size of file in bytes
	 * @throws MalformedInputException 	Checked exception thrown when an input byte sequence 
	 * 								   	is not legal for given charset, or an input character 
	 * 								   	sequence is not a legal sixteen-bit Unicode sequence.
	 * @throws IOException				Signals that an I/O exception of some sort has occurred. 
	 * 									This class is the general class of exceptions produced by failed or 
	 * 									interrupted I/O operations.
	 */
	public int getFileSize() throws MalformedInputException, IOException {
		URL url = new URL(server_address);
		URLConnection uCon = url.openConnection();
		uCon.setConnectTimeout(timeout);
		uCon.setReadTimeout(timeout);
		return uCon.getContentLength();
	}
	
	/**
	 * Function set status to ST_PAUSE. Downloading will be paused.
	 */
	public void pause() {
		this.status = Status.ST_PAUSE;
	}
	
	/**
	 * Function set status to ST_DOWNLOAD. Downloading will be started.
	 */
	public void resume() {
		this.status = Status.ST_DOWNLOAD;
	}
	
	/**
	 * Function set status to ST_CANCEL. Downloading and install will be canceled.
	 */
	public void cancel() {
		this.status = Status.ST_CANCEL;
	}
	
	/**
	 * Function which return currently status
	 * @return status of downloading
	 */
	public Status getStatus() {
		return status;
	}
	
	/**
	 * Function set variable override on false or true depends on file must be override or not.
	 * @param override
	 */
	public void setOverride(boolean override) {
		this.override = override;
	}
	
	/**
	 * Function set server address from where files will be getting 
	 * @param server_address
	 */
	public void setserver_address(String server_address) {
		this.server_address = server_address;
	}
	
	/**
	 * Function set destination path where files will be saving
	 * @param dest_path
	 */
	public void setdest_path(String dest_path) {
		this.dest_path = dest_path;
	}
	
	/**
	 * Function returns server address from where files will be getting 
	 * @return server address
	 */
	public String getserver_address() {
		return server_address;
	}
	
	/**
	 * Function returns destination path where file will be saved after downloading
	 * @return destination path where file will be saved after downloading
	 */
	public String getdest_path() {
		return dest_path;
	}
}