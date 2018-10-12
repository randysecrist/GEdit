package edu.usu.cs.algorithms;

import edu.usu.cs.graph.Edge;
import edu.usu.cs.graph.Graph;
import edu.usu.cs.graph.Node;
import edu.usu.cs.graph.Path;
import edu.usu.cs.graph.PathContainer;

/**
 * Simple algorithm which was used to perform test cases against our application.
 * Creation date: (4/20/2002 4:28:36 PM)
 * @author Randy Secrist
 */
public class TestAlgorithm extends Algorithm {
    /**
     * Constructs a test algorithm.
     */
    public TestAlgorithm() {
        super();
    }

    /**
     * Performs the algorithm upon a graph adt.
     */
    public PathContainer doAlgorithm(Graph g) {
        PathContainer pc = new PathContainer();
        int id0 = 0;
        Node[] nodes0 = { g.getNode(3), null, g.getNode(5), null, g.getNode(4), null, g.getNode(6), null, g.getNode(2) };
        Edge[] edges0 = { null, null, g.getEdge(3, 5), null, g.getEdge(5, 4),null, g.getEdge(4, 6), null, g.getEdge(6, 2) };

        int id1 = 1;
        Node[] nodes1 = { g.getNode(0), null, g.getNode(4), null, g.getNode(6), null, g.getNode(2) };
        Edge[] edges1 = { null, null, g.getEdge(0, 4),null, g.getEdge(4, 6), null, g.getEdge(6, 2) };

        try {
            Path p0 = new Path(nodes0, edges0, id0);
            Path p1 = new Path(nodes1, edges1, id1);
            pc.addPath(p0);
            pc.addPath(p1);
        }
        catch (java.lang.Throwable e) {
            System.out.println(e.toString());
        }
        return pc;
    }

    /**
     * Returns the name of this algorithm.
     */
    public String getMenuName() {
        return "Test Algorithm";
    }

    /**
     * Executes this algorithm.
     */
    public PathContainer startAlgorithm(Graph g) {
        return doAlgorithm(g);
    }

    /**
     * Tests the graph g to determine if this algorithm will work.
     */
    public boolean works(Graph g) {
        return true;
    }
}