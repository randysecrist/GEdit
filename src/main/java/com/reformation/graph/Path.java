package com.reformation.graph;

/**
 * Represents a path obtained from running an algorithm on a Graph Object. 
 * Creation date: (4/4/2002 3:26:29 PM)
 * 
 * @author Randy Secrist
 */
public class Path {
    /**
     * The set of edges in this path.
     */
    private Edge[] ePath;

    /**
     * The set of nodes in this path.
     */
    private Node[] nPath;

    /**
     * A unique id assigned to this path.
     */
    private int pathId;

    /**
     * Sets all internals to default values.
     */
    public Path() {
        super();
        this.nPath = null;
        this.ePath = null;
        this.pathId = -1;
    }

    /**
     * Creates a new path using a set of nodes, a set of edges, and a unique id
     * determined by the caller.
     *
     * Performs basic sanity checks on the input parameters.
     */
    public Path(Node[] n, Edge[] e, int id) throws PathException {
        super();
        if (n == null || e == null)
            throw new PathException("Illegal Path: Node and Edge array can not be null.");
        if (n.length != e.length)
            throw new PathException("Illegal Path: - Node and Edge array lengths are not equals.");
        // Proceed to Construct
        this.nPath = n;
        this.ePath = e;
        this.pathId = id;
    }

    /**
     * Returns the edge path.
     * @return A set of edges.
     */
    public Edge[] getEdgePath() {
        return this.ePath;
    }

    /**
     * Returns the id of this path.
     * @return An integer representing a (hopefully) unique id.
     */
    public int getId() {
        return this.pathId;
    }

    /**
     * Returns the nodes in this path.
     * @return A set of nodes.
     */
    public Node[] getNodePath() {
        return this.nPath;
    }

    /**
     * Sets the edges used in this path.
     * @param e A new set of edges.
     */
    public void setEdgePath(Edge[] e) {
        this.ePath = e;
    }

    /**
     * Sets the unique id of this path, as determined by the caller.
     * @param id The unique id of this path.
     */
    public void setId(int id) {
        this.pathId = id;
    }

    /**
     * Sets the nodes involved in this path.
     * @param n A new set of nodes.
     */
    public void setNodePath(Node[] n) {
        this.nPath = n;
    }
}