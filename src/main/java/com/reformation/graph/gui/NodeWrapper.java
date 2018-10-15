package com.reformation.graph.gui;

import com.reformation.graph.Node;

import java.awt.geom.Rectangle2D;

class NodeWrapper {
    double x;
    double y;

    double dx;
    double dy;

    boolean fixed;
    Node node;

    Rectangle2D borderRect;
    Rectangle2D fillRect;
    int islandId;
    boolean isPressed;
    boolean isTraversed;

    /**
     * Constructs a new NodeWrapper
     */
    public NodeWrapper(Node n) {
        this.node = n;
    }

    /**
     * Return's this wrappers internal node.
     */
    public Node getNode() {
        return node;
    }

    /**
     * Stores a new node in this wrapper class.
     *
     * @param aNode the node to wrap into this class.
     * @see com.reformation.graph.Node
     */
    public void setNode(Node aNode) {
        this.node = aNode;
    }

    /**
     * Return's the border area of this node.
     */
    public Rectangle2D getBorderRect() {
        return borderRect;
    }

    /**
     * Return's the fill area (background) of this node.
     */
    public Rectangle2D getFillRect() {
        return fillRect;
    }

    /**
     * Sets the paintable border size of this node.
     */
    public void setBorderRect(Rectangle2D r) {
        this.borderRect = r;
    }

    /**
     * Sets the fillable rectangle size of this node.
     */
    public void setFillRect(Rectangle2D r) {
        this.fillRect = r;
    }
}