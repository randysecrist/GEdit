package edu.usu.cs.gui;

/**
 * I am thrown when it is not possible to log, but should be.
 * Creation date: (3/21/2002 4:57:29 PM)
 * @author Randy Secrist
 */

public class LogNotInstantiatedException extends Throwable {
	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = -6916311240552426068L;
	
	/**
	 * LogNotInstantiatedException constructor.
	 */
	public LogNotInstantiatedException() {
		super();
	}
	/**
	 * LogNotInstantiatedException constructor.
	 * @param message The associated message with this exception.
	 */
	public LogNotInstantiatedException(String message) {
		super(message);
	}
}