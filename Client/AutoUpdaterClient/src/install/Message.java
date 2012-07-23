package install;

/** Is used to send information between the objects using the observer */
public class Message {
	/** true, if message is for gui console label */
	private boolean console;
	/** true, if message is for gui statistics label */
	private boolean statistics;
	/** true, if message is for gui progress bar */
	private boolean progressbar;
	/** true, if error message */
	private boolean error;
	/** content of the message */
	private String text;
	/** id of actualization which ended with an error */
	private String actualization_id;

	/**
	 * Information constructor
	 * 
	 * @param text
	 *            content
	 * @param console
	 *            whether it is on console label
	 * @param statistics
	 *            whether it is on statistics label
	 * @param progressbar
	 *            whether it is on progress bar
	 */
	public Message(String text, boolean console, boolean statistics,
			boolean progressbar) {
		this.text = text;
		this.console = console;
		this.statistics = statistics;
		this.progressbar = progressbar;
		this.error = false;
	}

	/**
	 * Error constructor
	 * 
	 * @param text
	 *            error content
	 * @param actualization_id
	 *            if of current actualization
	 */
	public Message(String text, String actualization_id) {
		this.text = text;
		this.actualization_id = actualization_id;
		this.error = true;
	}

	/**
	 * Gets message content
	 * 
	 * @return message content
	 */
	public String getText() {
		return text;
	}

	/**
	 * Gets acutlization id
	 * 
	 * @return actualization id
	 */
	public String getActualization_id() {
		return actualization_id;
	}

	/**
	 * Gets whether it is on console label
	 * 
	 * @return true - if it is, false - otherwise
	 */
	public boolean isConsole() {
		return console;
	}

	/**
	 * Gets whether it is on statistics label
	 * 
	 * @return true - if it is, false - otherwise
	 */
	public boolean isStatistics() {
		return statistics;
	}

	/**
	 * Gets whether it is on progress bar
	 * 
	 * @return true - if it is, false - otherwise
	 */
	public boolean isProgressbar() {
		return progressbar;
	}

	/**
	 * Gets whether it is error message
	 * 
	 * @return true - if it is, false - otherwise
	 */
	public boolean isError() {
		return error;
	}
}
