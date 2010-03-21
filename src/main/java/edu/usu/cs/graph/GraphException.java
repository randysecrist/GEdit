package edu.usu.cs.graph;

/**
 * I am thrown when a graph exception occurs.
 * Creation date: (3/1/2002 11:27:53 AM)
 * @author Randy Secrist
 */
public class GraphException extends Throwable {
	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = 6360273994184111877L;

	/**
	 * Standard exception thrown when critical events occur.
	 */
	public GraphException() {
		super();
	}
	
	/**
	 * GraphException constructor comment.
	 * @param message java.lang.String
	 */
	public GraphException(String message) {
		super(message);
	}
}