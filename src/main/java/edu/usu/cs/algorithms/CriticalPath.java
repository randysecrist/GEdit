package edu.usu.cs.algorithms;

import edu.usu.cs.graph.Edge;
import edu.usu.cs.graph.Graph;
import edu.usu.cs.graph.Node;
import edu.usu.cs.graph.Path;
import edu.usu.cs.graph.PathContainer;
import edu.usu.cs.graph.PathException;

/**
 * Calculates the most expensive path
 * in a graph data type.
 * 
 * @author Seth Humphries
 * @author Randy Secrist
 * @author Karl Smith
 */
public class CriticalPath extends Algorithm {
    static private int count, size;
    static private Node[] nodes;
    static private Edge[][] edges;
    static private Graph g;
    static private long edgecount;

    public boolean works(Graph grf) {
        // grf.updateWeighted();
        if (grf.size() < 1)
            return false;
        return (grf.isWeighted() && grf.isDirected());
    }

    public String getMenuName() {
        return "Critical Path";
    }

    /**
     * The entry point into this algorithm
     * @param theGraph The graph to use as input to execute this algorithm.
     */
    protected PathContainer startAlgorithm(Graph theGraph) {
        if (theGraph.isCyclic()) {
            this.showWarningDialog("The Graph is Cyclic and Critical Path cannot run");
            return null;
        }
        PathContainer p=doAlgorithm(theGraph);
        if (p == null || p.size() < 1) {
            this.showWarningDialog("Critical Path cannot run on this graph");
            return null;
        }
        return p;
    }

    public PathContainer doAlgorithm(Graph grf) {
        //returns all the shortest paths of equal length, who ever gets this needs to choose which one to use
        g = grf;
        size = g.getHeapSize();
        count = g.size();
        PathContainer contain = new PathContainer();
        Path pathy;
        nodes = g.getHeap();
        edges = g.getEdgesMatrix();
        edgecount = g.getEdgeCount();
        if (count == 1) {
            try {
                pathy = new Path(g.getNodes(),new Edge[1],0);
                contain.addPath(pathy);
                return contain;
            }
            catch (PathException junk) {
            }
        }

        contain=getCritical();
        for (int i = 0; i < size; i++)
            nodes[i].cleanUp();
        return (contain);
    }
    /**
     * Returns the critical path container.
     * @return The PathContainer which represents the critical path.
     */
    private PathContainer getCritical() {
        Path p;
        Node[] n = new Node[3];
        Edge[] e = new Edge[3];
        if (edgecount == 0) {
            return null;
        }
        if (edgecount == 1) {
            Edge solo = new Edge();
            for (int i = 0; i < size; i++)
                for (int j = 0; j < size; j++) {
                    if (edges[i][j].getWeight() != -1)
                        solo = edges[i][j];
                }
            n[0] = nodes[solo.getSource()];
            n[1] = null;
            n[2] = nodes[solo.getSource()];
            e[0] = solo;
            e[1] = null;
            e[2] = null;

            try {
                p = new Path(n, e, 0);
                PathContainer c = new PathContainer();
                c.addPath(p);
                return c;
            }
            catch (PathException error) {
            }
            return null;
        }
        int[] nosource = new int[size];
        int[] nodest = new int[size];
        int[] index = new int[size];
        for (int i = 0; i < size; i++) {
            index[i] = -1;
            nosource[i] = -1;
            nodest[i] = -1;
        }
        int noS = 0, noD = 0;
        for (int i = 0; i < size; i++) {
            if (noSource(i)) {
                nosource[noS] = i;
                noS++;
            }
            if (noDest(i)) {
                nodest[noD] = i;
                noD++;
            }
        }
        double[] weights = new double[size];
        int[] trackingId = new int[size];
        PathContainer getcontain = new PathContainer();
        PathContainer contain = new PathContainer();
        p = new Path();
        for (int j = 0; j < size; j++) {
            for (int i = 0; i < size; i++) {
                while (i < size && j < size && (nosource[i] == -1 || nodest[j] == -1)) {
                    if (nosource[i] == -1)
                        i++;
                    if (nodest[j] == -1)
                        j++;
                }
                if (i < size && j < size) {
                    getcontain = dfs(nosource[i], nodest[j]);
                    for (int k = 0; k < getcontain.size(); k++) {
                        contain.addPath(getcontain.getPath(k));
                    }
                }
            }
        }
        getcontain = new PathContainer();
        double[] lengths = new double[contain.size()];

        for (int i = 0; i < contain.size(); i++) {
            lengths[i] = calcLength(contain.getPath(i));
        }

        double max = 0;
        for (int i = 0; i < contain.size(); i++) {
            if (lengths[i] > max)
                max = lengths[i];
        }

        Double maxD = new Double(max);
        for (int i = 0; i < contain.size(); i++) {
            Double len = new Double(lengths[i]);
            if (len.compareTo(maxD) == 0)
                getcontain.addPath(contain.getPath(i));
        }

        return getcontain;
    }


    private boolean noDest(int key) {
        for (int i = 0; i < size; i++) {
            if (g.isEdge(key, i))
                return false;
        }
        return true;
    }

    private boolean noSource(int key) {
        for (int i = 0; i < size; i++) {
            if (g.isEdge(i, key))
                return false;
        }
        return true;
    }

    public PathContainer dfs(int start, int end)
    {
        PathContainer c=new PathContainer();
        if(nodes[start].getId() == -1) return c;
        if(nodes[end].getId() == -1) return c;

        for(int i = 0; i < size; i++) {
            nodes[i].cleanUp();
        }

        int[] work = new int[size];
        int place = 0;

        work[place] = start;
        place++;
        nodes[start].setVisited(Node.TRUE);
        int i = -1;
        while(place > 0) {
            for(i++ ; i < size; i++) {
                if(nodes[i].getId() == -1) continue;
                if(g.isEdge(work[place-1], i)) {
                    if(nodes[i].getVisited() == Node.FALSE) {
                        work[place++] = i;
                        nodes[i].setVisited(Node.TRUE);
                        if(i == end) {
                            ripPath(c, work, place);
                            nodes[i].cleanUp();
                            i = work[--place];
                        }
                        else {
                            i = -1;
                        }
                    }
                }
            }
            i = work[--place];
            nodes[work[place]].cleanUp();
        }

        for(int j = 0; j < size; j++) {
            nodes[j].cleanUp();
        }
        return c;
    }

    private void ripPath(PathContainer c, int[] path, int len) {
        Node[] n = new Node[len * 2];
        Edge[] e = new Edge[len * 2];
        int place = 2;

        n[0] = nodes[path[0]];
        e[0] = e[1] = null;
        n[1] = null;

        for(int i = 1; i < len; i++) {
            n[place] = nodes[path[i]];
            e[place] = edges[path[i-1]][path[i]];
            place++;
            n[place] = null;
            e[place] = null;
            place++;
        }
        Path p = null;
        try {
            p = new Path(n, e, c.size() + 1);
        }
        catch (PathException pexc) {
        }
        c.addPath(p);
    }

    private double calcLength(Path p) {
        double len = 0;
        for(int i = 0; i < p.getEdgePath().length; i++) {
            if(p.getEdgePath()[i] != null) {
                len += p.getEdgePath()[i].getWeight();
            }
        }
        return len;
    }
}
