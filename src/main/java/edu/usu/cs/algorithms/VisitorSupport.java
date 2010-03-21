package edu.usu.cs.algorithms;

import edu.usu.cs.graph.Edge;
import edu.usu.cs.graph.Node;

/**
 * VisitorSupport is an abstract base class which is useful for
 * implementation inheritence or when using anonymous inner classes
 * to create simple Visitor  implementations.
 * 
 * Created on Sep 18, 2004
 * @author Randy Secrist
 */
public abstract class VisitorSupport implements Visitor {

	/**
	 * Implementing classes should override this method.
	 * @see edu.usu.cs.algorithms.Visitor#visit(edu.usu.cs.graph.Edge)
	 */
	public void visit(Edge e) {
	}

	/**
	 * Implementing classes should override this method.
	 * @see edu.usu.cs.algorithms.Visitor#visit(edu.usu.cs.graph.Node)
	 */
	public void visit(Node n) {
	}

}
