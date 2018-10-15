package com.reformation.graph;

public interface DataVisitor {
    /**
     * Maps data types to specific UI input dialogs.
     * @param data The data type to display.
     * @param parent The UI composite to follow.
     * @return A string which represents the data type.
     */
    String showInputDialog(Object data, Object parent);
}
