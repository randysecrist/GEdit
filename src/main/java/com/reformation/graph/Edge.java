package com.reformation.graph;

import java.util.Optional;

/**
 * Edge class to hold node id's and edge weights, it is independent of the
 * nodes
 * 
 * @author Seth Humphries
 */
public class Edge implements java.io.Serializable {

    // Serial Version Id
    static final long serialVersionUID = -711906394888805294L;
    private int fromID;
    private int toID;
    private double weight;

    /**
     * The data this edge holds.
     */
    private Data[] data;

    public Edge() {
        super();
        this.fromID = -1;
        this.toID = -1;
        this.weight = -1;
    }

    /**
     * ACCEPTS source, target and weights for an edge error checking for
     * the node id (int) is done in the function {@link Graph#addEdge(Edge)}.
     *
     * @param source The id of the source node.
     * @param target The id of the target node.
     * @param weight The weight of the edge, should be >= 0.
     */
    public Edge(int source, int target, double weight) {
        this.toID = target;
        this.fromID = source;
        this.weight = weight;
    }

    /**
     * Returns the destination node this edge points to.
     * @return The ID of the connected node.
     */
    public int getDest() {
        return (toID);
    }

    /**
     * Returns the source node this edge protrudes from.
     * @return The ID of the connected node.
     */
    public int getSource() {
        return (fromID);
    }

    /**
     * Returns the weight of this edge.
     * @return A double percision floating point value.
     */
    public double getWeight() {
        return (weight);
    }

    /**
     * Returns the data this edge holds.
     * @return The data object.
     */
    public Optional<Data[]> getData() {
        if (data == null) { return Optional.empty(); }
        return Optional.of(data);
    }

    /**
     * Sets the destination node of this edge.
     * @param dest The ID of the destination node.
     */
    synchronized Edge setDest(int dest) {
        this.toID = dest;
        return this;
    }

    /**
     * Sets the source node of this edge.
     * @param src The ID of the source node.
     */
    synchronized Edge setSource(int src) {
        this.fromID = src;
        return this;
    }

    /**
     * Sets the weight of this edge.
     * @param weight Sets a double percision floating point value.
     */
    synchronized Edge setWeight(double weight) {
        this.weight = weight;
        return this;
    }

    /**
     * Sets the data to a new value.
     * @param data The new data object.
     */
    synchronized Edge setData(Data[] data) {
        this.data = data;
        return this;
    }
}