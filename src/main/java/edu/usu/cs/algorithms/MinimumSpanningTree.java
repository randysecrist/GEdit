package edu.usu.cs.algorithms;

import edu.usu.cs.graph.Edge;
import edu.usu.cs.graph.Graph;
import edu.usu.cs.graph.Node;
import edu.usu.cs.graph.Path;
import edu.usu.cs.graph.PathContainer;
import edu.usu.cs.graph.PathException;

import java.util.ArrayList;

/**
 * Performs a minimum spanning tree algorithm on a graph data type.
 * 
 * @author Seth Humphries
 */
public class MinimumSpanningTree extends Algorithm {

    public boolean works(Graph g) {
        if (g.getIslandCount() > 1)
            return false;
        else if (g.isDirected() == true)
            return false;
        else if (g.size() <= 1 || g.getEdgeCount() < 1)
            return false;
        return true;
    }

    public PathContainer doAlgorithm(Graph g) {
        PathContainer pc = new PathContainer();
        ArrayList<Edge> edges = g.getEdgesV();
        ArrayList<Edge> pathEdges = new ArrayList<Edge>();
        Node[] nodes = g.getNodes();
        Integer[] idArray = new Integer[nodes.length];
        int clusterCount = nodes.length, source, dest;
        for (int i = 0; i < idArray.length; i++)
            idArray[i] = new Integer(i);
        while (clusterCount > 1) {
            Edge eTmp = getSmallest(edges);
            source = eTmp.getSource();
            dest = eTmp.getDest();
            edges.remove(eTmp);
            if (idArray[source] != idArray[dest]) {
                regroup(idArray, idArray[source], idArray[dest]);
                pathEdges.add(eTmp);
                clusterCount--;
            }
        }
        try {
            Edge[] etmp = new Edge[1];
            Node[] ntmp = new Node[1];
            Path p = new Path(ntmp, etmp, 1);
            synchronizePath(nodes, pathEdges, 0, p);
            pc.addPath(p);
        }
        catch (PathException x) {}
        return pc;
    }

    private Edge getSmallest(ArrayList<Edge> edge) {
        Edge smallest = (Edge) edge.get(0);
        Edge cur = new Edge();
        for (int i = 1; i < edge.size(); i++) {
            cur = (Edge) edge.get(i);
            if (smallest.getWeight() > cur.getWeight())
                smallest = cur;
        }
        return smallest;
    }

    private void synchronizePath(Node[] node, ArrayList<Edge> edge, int pathId, Path tmp) {
        Edge[] e = new Edge[3 * edge.size()];
        Node[] n = new Node[3 * edge.size()];
        for (int i = 0; i < edge.size(); i++) {
            Edge eTemp = (Edge) edge.get(i);
            e[3 * i] = eTemp;
            n[3 * i] = node[eTemp.getSource()];
            n[3 * i + 1] = node[eTemp.getDest()];
        }
        tmp.setEdgePath(e);
        tmp.setNodePath(n);
        tmp.setId(pathId);
    }

    public String getMenuName() {
        return "Minimum Spanning Tree";
    }

    protected PathContainer startAlgorithm(Graph theGraph) {
        PathContainer pc = doAlgorithm(theGraph);
        if (pc.size() == 0)
            this.showWarningDialog("No Minimum Spanning Trees found for this Graph.");
        return pc;
    }

    public void regroup(Integer[] idArray, Integer changeTo, Integer changeFrom) {
        int cFrom = changeFrom.intValue();
        for (int i = 0; i < idArray.length; i++) {
            int id = idArray[i].intValue();
            if (id == cFrom) {
                idArray[i] = changeTo;
            }
        }
    }
}