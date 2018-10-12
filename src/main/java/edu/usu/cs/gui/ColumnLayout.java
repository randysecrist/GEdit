package edu.usu.cs.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;

class ColumnLayout implements LayoutManager {
    private int xInset = 5;
    private int yInset = 5;
    private int yGap = 2;

    /**
     * Adds the specified component with the specified name to
     * the layout.
     * @param name the component name
     * @param comp the component to be added
     */
    public void addLayoutComponent(String name, Component comp) {
    }
    /**
     * Lays out the container in the specified panel.
     * @param comp the component which needs to be laid out
     */
    public void layoutContainer(Container comp) {
        Insets insets = comp.getInsets();
        int height = yInset + insets.top;

        Component[] children = comp.getComponents();
        Dimension compSize = null;
        for (int i = 0; i < children.length; i++) {
            compSize = children[i].getPreferredSize();
            children[i].setSize(compSize.width, compSize.height);
            children[i].setLocation(xInset + insets.left, height);
            height += compSize.height + yGap;
        }

    }
    /**
     * Calculates the minimum size dimensions for the specified
     * panel given the components in the specified parent container.
     * @param parent the component to be laid out
     * @see #preferredLayoutSize
     */
    public Dimension minimumLayoutSize(Container parent) {
        Insets insets = parent.getInsets();
        int height = yInset + insets.top;
        int width = 0 + insets.left + insets.right;

        Component[] children = parent.getComponents();
        Dimension compSize = null;
        for (int i = 0; i < children.length; i++) {
            compSize = children[i].getPreferredSize();
            height += compSize.height + yGap;
            width =
                Math.max(width, compSize.width + insets.left + insets.right + xInset * 2);
        }
        height += insets.bottom;
        return new Dimension(width, height);
    }
    /**
     * Calculates the preferred size dimensions for the specified
     * panel given the components in the specified parent container.
     * @param parent the component to be laid out
     */
    public Dimension preferredLayoutSize(Container parent) {
        return minimumLayoutSize(parent);
    }
    /**
     * Removes the specified component from the layout.
     * @param comp the component to be removed
     */
    public void removeLayoutComponent(Component comp) {
    }
}