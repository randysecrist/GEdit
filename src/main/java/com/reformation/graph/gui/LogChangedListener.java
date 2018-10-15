package com.reformation.graph.gui;

import java.util.EventListener;
import java.util.EventObject;

public interface LogChangedListener extends EventListener {
    void logChanged(EventObject e);
}