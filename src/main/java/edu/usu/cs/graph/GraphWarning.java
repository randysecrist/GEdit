package edu.usu.cs.graph;

/**
 * I am thrown when a graph exception occurs.
 * Creation date: (3/1/2002 11:27:53 AM)
 * 
 * @author Randy Secrist
 */
public class GraphWarning extends Throwable {
    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = -3732322333319924870L;

    /**
     * Standard error thrown when non critical events occur.
     */
    public GraphWarning() {
        super();
    }

    /**
     * GraphException constructor comment.
     *
     * @param message
     *            java.lang.String
     */
    public GraphWarning(String message) {
        super(message);
    }
}