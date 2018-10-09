package edu.usu.cs.gui;

import java.io.StringWriter;
import java.io.PrintWriter;

/**
 * Thrown when a resource can't be delivered.
 *
 * @author Randy Secrist
 */
public class ResourceFailure extends Exception {
	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = 3291046799098163904L;
	
	private java.lang.Throwable theException;
	public ResourceFailure() {
		super();
	}
	public ResourceFailure(String s) {
		super(s);
	}
	public ResourceFailure(java.lang.Throwable e) {
		this.theException = e;
	}
	public String getRelevantMessage() {
		if (theException == null) return this.getLocalizedMessage();
		
		if (theException instanceof java.sql.SQLException) {
			return theException.getClass().getName() + " - " + theException.getLocalizedMessage() + " - " + ((java.sql.SQLException)theException).getErrorCode();
		}
		else if (theException instanceof javax.naming.NamingException) {
			return theException.getClass().getName() + " - " + ((javax.naming.NamingException) theException).getExplanation() + " - " + ((javax.naming.NamingException) theException).getRemainingName();
		}
		else {
			return theException.getClass().getName() + " - " + theException.getLocalizedMessage();
		}
	}
	public String getRootStackTrace() {
		StringWriter strWriter = new StringWriter();
		if (theException == null) {
			this.printStackTrace(new PrintWriter(strWriter));
		}
		else theException.printStackTrace(new PrintWriter(strWriter));
		return strWriter.toString();
	}
	public String getRootType() {
		if (theException == null) return this.getClass().getName();
		else return theException.getClass().getName();
	}
}