package updater;
/** Obsluga wlasnych bledow */
public class MyException extends Exception {
	
	private static final long serialVersionUID = 1L;
	/** Pusty */
	public MyException() {		
	}
	/** 
	 * Wywoluje super(msg)
	 * @param msg wiadomosc o bledzie
	 */
	public MyException(String msg) {
		super(msg);
	}	
}
