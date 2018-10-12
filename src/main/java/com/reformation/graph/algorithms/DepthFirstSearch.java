package com.reformation.graph.algorithms;

import com.reformation.graph.Edge;
import com.reformation.graph.Graph;
import com.reformation.graph.Node;
import com.reformation.graph.Path;
import com.reformation.graph.PathContainer;
import com.reformation.graph.PathException;

/**
 * depth first search
 * Creation Date: 2/9/2002 
 * @author Chad Coats
 */
public class DepthFirstSearch extends Algorithm {

    public boolean works(Graph g) {
        if (g.isDirected()) //  if directed cannot traverse
            return false;
        else if (g.size() < 1) //  if 1 or less nodes, cannot traverse
            return false;
        return true; // can successfully run algorithms
    }

    /**
     * performs depth first search from start to key in a graph returns empty
     * PC if key is not found
     */
    public PathContainer doAlgorithm(Graph g, int start, int key) {
        DepthFirstTraversal dft = new DepthFirstTraversal();
        PathContainer pc = new PathContainer();
        if (g.size() == 1) {
            Node[] oneNode = g.getNodes();
            Edge[] dummy = new Edge[1];
            try {
                Path p = new Path(oneNode, dummy, 0);
                pc.addPath(p);
                return pc;
            }
            catch (PathException p) {}
        }
        pc = dft.doAlgorithm(g, start);
        Path p = pc.getPath(0);
        pc.removePath(p);

        /*
         * Remove after debuging. 
         */
        /*for (int i = 0; i < p.getEdgePath().length; i++) {
            Edge edge = p.getEdgePath()[i];
            if (edge != null)
                System.out.println("Edge " + i + ":\t" + edge.getSource() + "::" + edge.getDest());
        }*/

        boolean found = modifyPath(p, key);
        if (!found)
            return pc;
        pc.addPath(p);
        return pc;
    }

    /**
     * copies path returned from DF travesal until key is found
     */
    private boolean modifyPath(Path p, int key) {
        Edge[] pathEdges = p.getEdgePath();
        Node[] pathNodes = p.getNodePath();
        Edge[] newEPath = new Edge[pathEdges.length];
        Node[] newNPath = new Node[pathEdges.length];
        for (int i = 0; i < pathEdges.length; i++) {
            newEPath[i] = pathEdges[i];
            newNPath[i] = pathNodes[i];
            if (pathEdges[i] != null && (pathEdges[i].getDest() == key || pathEdges[i].getSource() == key)) {
                newNPath[i + 1] = pathNodes[i + 1];
                p.setEdgePath(newEPath);
                p.setNodePath(newNPath);
                return true;
            }
        }
        return false;
    }

    public String getMenuName() {
        return "Depth First Search";
    }

    protected PathContainer startAlgorithm(Graph theGraph) {
        int begin;
        int end;
        Edge edge = showSourceDestDialog(theGraph);
        if (edge != null) {
            begin = edge.getSource();
            end = edge.getDest();
            PathContainer pc = doAlgorithm(theGraph, begin, end);
            if (pc.size() == 0)
                this.showWarningDialog("Destination node not found.");
            return pc;
        }
        else
            return new PathContainer();
    }
}
