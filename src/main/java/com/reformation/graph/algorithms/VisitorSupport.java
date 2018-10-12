package com.reformation.graph.algorithms;

import com.reformation.graph.Edge;
import com.reformation.graph.Node;

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
     * @see com.reformation.graph.algorithms.Visitor#visit(com.reformation.graph.Edge)
     */
    public void visit(Edge e) {
    }

    /**
     * Implementing classes should override this method.
     * @see com.reformation.graph.algorithms.Visitor#visit(com.reformation.graph.Node)
     */
    public void visit(Node n) {
    }

}
