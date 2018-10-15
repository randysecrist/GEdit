package com.reformation.graph.gui;

import com.reformation.graph.Edge;

import java.awt.Color;

/**
 * Simple wrapper which serves to act as a interface for edges used
 * in a graph class, and edges drawn to a graphical display.
 * 
 * @author Randy Secrist
 */
class EdgeWrapper {
    int from;
    int to;

    double len;
    Edge edge;

    Color edgeColor = Color.black;
    int islandId;
    boolean isTraversed;

    /**
     * EdgeWrapper constructor:
     * This constructor wraps inside itself an com.reformation.graph.Edge class.
     *
     * @param e the edge to wrap into this class.
     * @see com.reformation.graph.Edge
     */
    public EdgeWrapper(Edge e) {
        this.edge = e;
    }

    /**
     * Return's this wrappers internal edge.
     */
    public Edge getEdge() {
        return edge;
    }

    /**
     * Stores a new edge in this wrapper class.
     *
     * @param e the edge to wrap into this class.
     * @see com.reformation.graph.Edge
     */
    public void setEdge(Edge aEdge) {
        this.edge = aEdge;
    }
}