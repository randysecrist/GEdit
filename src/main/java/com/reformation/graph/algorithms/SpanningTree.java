package com.reformation.graph.algorithms;

import com.reformation.graph.Edge;
import com.reformation.graph.Graph;
import com.reformation.graph.Node;
import com.reformation.graph.Path;
import com.reformation.graph.PathContainer;

/**
 * Finds all spanning trees for a given graph.
 * 
 * @author Seth Humphries
 */
public class SpanningTree extends Algorithm {

    /** determines if algorithm can be run on graph g * */
    public boolean works(Graph g) {
        if (g.isDirected())
            return false;
        else if (g.getIslandCount() > 1)
            return false;
        else if (g.getNodes().length <= 1 || g.getEdgeCount() < 1)
            return false;
        return true;
    }

    /**
     * creates spanning tree out of given graph, starting at node id start
     */
    public PathContainer doAlgorithm(Graph g, int start) {
        Edge[] list = g.getEdges();
        if (start < 0 || start >= g.size())
            return new PathContainer();
        if (g.size() <= 1 || g.getEdgeCount() < 1)
            return new PathContainer();
        BreadthFirstTraversal bft = new BreadthFirstTraversal();
        DepthFirstTraversal dft = new DepthFirstTraversal();
        PathContainer bpc = bft.doAlgorithm(g, start);
        PathContainer dpc = dft.doAlgorithm(g, start);
        Path p = dpc.getPath(0);
        p.setId(1);
        bpc.addPath(p);
        return bpc;
    }

    public String getMenuName() {
        return "Spanning Tree";
    }

    protected PathContainer startAlgorithm(Graph theGraph) {
        int begin;
        Node node = showSourceDialog(theGraph);
        if (node != null) {
            begin = node.getId();
            PathContainer pc = doAlgorithm(theGraph, begin);
            if (pc.size() == 0)
                this.showWarningDialog("No Spanning Trees found for this Graph.");
            return pc;
        }
        else
            return new PathContainer();
    }
}