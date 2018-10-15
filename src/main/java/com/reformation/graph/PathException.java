package com.reformation.graph;

/**
 * I am thrown when path creation fails.
 * Creation date: (3/1/2002 11:27:53 AM)
 * 
 * @author Randy Secrist
 */
public class PathException extends Throwable {
    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = 7842117815073054310L;

    /**
     * PathException constructor comment.
     */
    public PathException() {
        super();
    }

    /**
     * PathException constructor comment.
     *
     * @param message
     *            java.lang.String
     */
    public PathException(String message) {
        super(message);
    }
}