package edu.usu.cs.algorithms;

import edu.usu.cs.graph.Edge;
import edu.usu.cs.graph.Node;

/**
 * Visitor is used to implement the Visitor pattern in the Graph API.
 * An object of this interface can be passed to an Algorithm which
 * will then call its typesafe methods.
 * 
 * Please refer to the Gang of Four book of Design Patterns for
 * more details on the Visitor pattern.
 * 
 * Created on Sep 18, 2004
 * @author Randy Secrist
 */
public interface Visitor {
	/**
	 * Visits the given Edge.
	 * @param e The edge to visit.
	 */
	void visit(Edge e);
	
	/**
	 * Visits the given Node.
	 * @param n The node to visit.
	 */
	void visit(Node n);
}
