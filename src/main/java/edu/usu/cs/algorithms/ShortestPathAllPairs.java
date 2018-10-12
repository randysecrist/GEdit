package edu.usu.cs.algorithms;

import edu.usu.cs.graph.Edge;
import edu.usu.cs.graph.Graph;
import edu.usu.cs.graph.Node;
import edu.usu.cs.graph.Path;
import edu.usu.cs.graph.PathContainer;

/**
 * Finds all possible shortest paths within a graph data type.
 * 
 * @author Seth Humphries
 */
public class ShortestPathAllPairs extends Algorithm {

    private int			count, size;
    private Node[]		nodes;
    private Edge[][]	edges;
    private Graph		g;
    static private long	edgecount;

    public boolean works(Graph grf) {
        //    grf.updateWeighted();
        if (grf.size() < 1)
            return false;
        return (grf.isWeighted());
    }

    public PathContainer doAlgorithm(Graph grf) {
        PathContainer contain = new PathContainer();
        PathContainer contains = new PathContainer();
        ShortestPath getShort = new ShortestPath();
        g = grf;
        nodes = g.getHeap();
        edges = g.getEdgesMatrix();
        size = g.getHeapSize();
        count = g.size();
        Path p = new Path();
        int cnt = 0;
        int rnd = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                contain = getShort.doAlgorithm(g, i, j);
                if (contain != null && contain.size() > 0) {
                    for (int k = 0; k < contain.size(); k++) {
                        p = contain.getPath(k);
                        p.setId(cnt);
                        cnt++;
                        contains.addPath(p);
                    }
                }
            }
        }
        return contains;
    }

    public String getMenuName() {
        return "Shortest Path - All Pairs";
    }

    protected PathContainer startAlgorithm(Graph theGraph) {
        PathContainer p = doAlgorithm(theGraph);
        if (p == null || p.size() < 1) {
            this.showWarningDialog("All Pairs Shortest Path found no solution on this graph");
            return null;
        }
        return p;
    }
}