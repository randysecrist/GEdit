package edu.usu.cs.graph;

import java.io.Serializable;

/**
 * Interface for Nodes containing any User Defined Types 
 * Creation date: (2/9/2002 3:19:24 PM)
 * 
 * @author Randy Secrist
 */
public interface Data extends Serializable {

	/**
	 * Returns a negative number if this < x; returns zero is this = x; returns
	 * a positive number otherwise.
	 */
	public boolean equals(Data x);

	/**
	 * Returns the display name for this data object.
	 */
	public String getDisplayName();

	/**
	 * Specified how a new Data object can be created from a String.
	 */
	public Data getInstance(String val);

	/**
	 * String representation for this Data Object
	 */
	public String toString();
}
