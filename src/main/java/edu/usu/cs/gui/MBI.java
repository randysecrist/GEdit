package edu.usu.cs.gui;

import edu.usu.cs.algorithms.Algorithm;

import java.awt.event.KeyEvent;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;

/**
 * Menu Bar Interface
 * Creation date: (3/7/2002 7:00:59 PM)
 * @author Randy Secrist
 */
public class MBI {
    private Hashtable<String,String> menuHash = new Hashtable<String,String>();

    // JMenuItem Action Listener
    private MenuListener ml = null;

    // Menu, and menu items
    private JMenuBar menuBar = new JMenuBar();

    private static MBI _instance = null;
    public static final String EDGE_STRESS = "Edge Stress";
    public static final String EDGE_WEIGHTS = "Edge Weights";
    private MyButtonGroup edgeList = new MyButtonGroup();
    private TreeSet<String> eventSet = new TreeSet<String>();
    public static final String NODE_ID = "Node ID";
    public static final String NODE_NAME = "Node Name";
    private MyButtonGroup nodeList = new MyButtonGroup();

    public String getFunctionName(String name) {
        String s = menuHash.get(name);
        if (s != null) {
            return s;
        }
        else return "doNothing";
    }
    public JMenuBar getJMenuBar() {
        return menuBar;
    }


    public class MyButtonGroup {
        private Vector<JCheckBoxMenuItem> group = new Vector<>();
        public MyButtonGroup() {
        }
        /**
         * Returns an enumeration of elements stored in the button group.
         */
        public Enumeration<JCheckBoxMenuItem> getGroup() {
            return group.elements();
        }
        public void clearState() {
            Enumeration<JCheckBoxMenuItem> enumeration = getGroup();
            while (enumeration.hasMoreElements()) {
                Object o = enumeration.nextElement();
                if (o != null && o instanceof JCheckBoxMenuItem) {
                    ((JCheckBoxMenuItem)o).setState(false);
                }
            }
        }
        /**
         * Sets every other element in group to off.
         */
        public void setGroupState(JCheckBoxMenuItem theSource) {
            // Set All Other Check Box's to off.
            Enumeration<JCheckBoxMenuItem> enumeration = getGroup();
            while (enumeration.hasMoreElements()) {
                JCheckBoxMenuItem item = enumeration.nextElement();
                if (!theSource.equals(item)) item.setState(false);
            }
        }
        /**
         * Sets every other element in group to off.
         */
        public void setGroupState(String theAction) {
            // Set All Other Check Box's to off.
            if (theAction != null && theAction.length() > 0) {
                Enumeration<JCheckBoxMenuItem> enumeration = getGroup();
                while (enumeration.hasMoreElements()) {
                    JCheckBoxMenuItem item = enumeration.nextElement();
                    if (!theAction.equalsIgnoreCase(item.getText())) item.setState(false);
                    else item.setState(true);
                }
            }
        }
        public void add(JCheckBoxMenuItem newItem) {
            group.add(newItem);
        }
    }

    /**
     * MBI constructor comment.
     */
    private MBI() {
        super();

        menuBar = new JMenuBar();

        // JMenuItem Action Listener
        ml = new MenuListener(this);

        // File Menu
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        fileMenu.add(this.buildJMenuItem("New", "doNew", true, KeyEvent.VK_N, KeyEvent.VK_N, KeyEvent.CTRL_MASK));
        fileMenu.add(this.buildJMenuItem("Open", "doOpen", true, KeyEvent.VK_O, KeyEvent.VK_O, KeyEvent.CTRL_MASK));
        fileMenu.add(this.buildJMenuItem("Close", "doClose", false, KeyEvent.VK_C));
        fileMenu.add(new JSeparator());
        fileMenu.add(this.buildJMenuItem("Save", "doSave", false, KeyEvent.VK_S, KeyEvent.VK_S, KeyEvent.CTRL_MASK));
        fileMenu.add(this.buildJMenuItem("Save As", "doSaveAs", false, KeyEvent.VK_A));
        fileMenu.add(new JSeparator());
        fileMenu.add(this.buildJMenuItem("Import", "doImport", true, KeyEvent.VK_I));
        fileMenu.add(this.buildJMenuItem("Export", "doExport", false, KeyEvent.VK_E));
        fileMenu.add(new JSeparator());
        fileMenu.add(this.buildJMenuItem("Exit", "doExit", true, KeyEvent.VK_X, KeyEvent.VK_Q, KeyEvent.CTRL_MASK));
        menuBar.add(fileMenu);

        // Edit Menu
        JMenu editMenu = new JMenu("Edit");
        editMenu.setMnemonic(KeyEvent.VK_E);
        editMenu.add(this.buildJMenuItem("Edge Weights...", "doEdgeWeights", false, KeyEvent.VK_W, KeyEvent.VK_W, KeyEvent.CTRL_MASK));
        editMenu.add(this.buildJMenuItem("Node Data...", "doEditNodeData", false, KeyEvent.VK_D, KeyEvent.VK_D, KeyEvent.CTRL_MASK));
        editMenu.add(new JSeparator());
        editMenu.add(this.buildJMenuItem("Add Node", "doAddNode", false));
        editMenu.add(this.buildJMenuItem("Add Edge", "doAddEdge", false));
        editMenu.add(this.buildJMenuItem("Remove Node", "doRemoveNode", false));
        editMenu.add(this.buildJMenuItem("Remove Edge", "doRemoveEdge", false));
        editMenu.add(new JSeparator());
        editMenu.add(this.buildJMenuItem("Remove All Edges", "doRemoveEdges",false));
        editMenu.add(this.buildJMenuItem("Delete Island", "doDeleteIsland", false));
        editMenu.add(new JSeparator());
        editMenu.add(this.buildJMenuItem("Preferences", "doPreferences", true, KeyEvent.VK_P, KeyEvent.VK_ENTER, KeyEvent.ALT_MASK));
        menuBar.add(editMenu);

        // View Menu
        JMenu viewMenu = new JMenu("View");
        viewMenu.setMnemonic(KeyEvent.VK_V);
        viewMenu.add(this.buildJCheckBoxMenuItem("Balance Graph", "doBalance", false, true, KeyEvent.VK_B, KeyEvent.VK_B, KeyEvent.CTRL_MASK));
        viewMenu.add(new JSeparator());
        /*
        viewMenu.add(this.buildJMenuItem("Zoom In", "doZoomIn", false, KeyEvent.VK_PLUS));
        viewMenu.add(this.buildJMenuItem("Zoom Out", "doZoomOut", false, KeyEvent.VK_MINUS));
        viewMenu.add(this.buildJMenuItem("100%", "doZoom100", false, KeyEvent.VK_1, KeyEvent.VK_1, KeyEvent.CTRL_MASK));
        viewMenu.add(this.buildJMenuItem("50%", "doZoom50", false, KeyEvent.VK_5, KeyEvent.VK_5, KeyEvent.CTRL_MASK));
        viewMenu.add(this.buildJMenuItem("25%", "doZoom25", false, KeyEvent.VK_2, KeyEvent.VK_2, KeyEvent.CTRL_MASK));
        viewMenu.add(new JSeparator());
        */
        JCheckBoxMenuItem name = this.buildJCheckBoxMenuItem(NODE_NAME, "doToggleViewState", false, false, KeyEvent.VK_N, -1, -1);
        JCheckBoxMenuItem id = this.buildJCheckBoxMenuItem(NODE_ID, "doToggleViewState", false, false, KeyEvent.VK_I, -1, -1);
        JCheckBoxMenuItem weight = this.buildJCheckBoxMenuItem(EDGE_WEIGHTS, "doToggleViewState", false, false, KeyEvent.VK_W, -1, -1);
        JCheckBoxMenuItem stress = this.buildJCheckBoxMenuItem(EDGE_STRESS, "doToggleViewState", false, false, KeyEvent.VK_S, -1, -1);
        nodeList.add(name);
        nodeList.add(id);
        edgeList.add(weight);
        edgeList.add(stress);
        viewMenu.add(name);
        viewMenu.add(id);
        viewMenu.add(new JSeparator());
        viewMenu.add(weight);
        viewMenu.add(stress);
        eventSet.add(NODE_NAME);
        eventSet.add(NODE_ID);
        eventSet.add(EDGE_STRESS);
        eventSet.add(EDGE_WEIGHTS);
        menuBar.add(viewMenu);

        // Window Menu
        JMenu windowMenu = new JMenu("Window");
        windowMenu.setMnemonic(KeyEvent.VK_W);
        windowMenu.add(this.buildJMenuItem("Cascade", "doCascade", true, KeyEvent.VK_C));
        windowMenu.add(this.buildJMenuItem("Tile", "doTile", true, KeyEvent.VK_T));
    //	windowMenu.add(this.buildJMenuItem("Tile Horizontally", "doTileHorz", false, KeyEvent.VK_H));
    //	windowMenu.add(this.buildJMenuItem("Tile Vertically", "doTileVert", false, KeyEvent.VK_V));
        windowMenu.add(this.buildJMenuItem("Minimize All", "doMinimizeAll", false, KeyEvent.VK_M));
        windowMenu.add(new JSeparator());
        windowMenu.add(this.buildJMenuItem("Log Window...", "doLogDialog", true, KeyEvent.VK_L));
        windowMenu.add(this.buildJMenuItem("Memory Usage", "doShowMemory", true, KeyEvent.VK_U, KeyEvent.VK_U, KeyEvent.CTRL_MASK));
        windowMenu.add(new JSeparator());
        menuBar.add(windowMenu);

        // Algorithm Menu
        JMenu algorithmMenu = new JMenu("Algorithm");
        Algorithm[] algorithms = Globals.getInstance().getAlgorithms();
        for (Algorithm algorithm : algorithms) {
            if (algorithm.getMenuName() != null) {
                algorithmMenu.add(this.buildJMenuItem(algorithm.getMenuName(), "doRunAlgorithm", false));
                eventSet.add(algorithm.getMenuName());
            }
        }
        algorithmMenu.setMnemonic(KeyEvent.VK_A);
        menuBar.add(algorithmMenu);

        // Help Menu
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic(KeyEvent.VK_H);
        helpMenu.add(this.buildJMenuItem("Graph Info...", "doGraphInfo", true, KeyEvent.VK_I));
        helpMenu.add(new JSeparator());
        helpMenu.add(this.buildJMenuItem("Contents", "doContents", true, KeyEvent.VK_C));
        helpMenu.add(this.buildJMenuItem("Glossary", "doGlossary", true, KeyEvent.VK_G));
        helpMenu.add(this.buildJMenuItem("Java Doc", "doJavaDoc", true, KeyEvent.VK_J));
        helpMenu.add(new JSeparator());
        helpMenu.add(this.buildJMenuItem("About", "doAbout", true, KeyEvent.VK_A));
        menuBar.add(helpMenu);

    }

    private JCheckBoxMenuItem buildJCheckBoxMenuItem(String name, String function, boolean enabled, boolean state, int mnemonic, int accelerator, int mask) {
        // if menu listener != null
        if (ml == null) {
            System.out.println("buildJCheckBoxMenuItem::MBI - Menu Listener is NULL!");
            return null;
        }

        JCheckBoxMenuItem newItem = new JCheckBoxMenuItem(name, state);
        newItem.addActionListener(ml);
        newItem.setEnabled(enabled);

        if (mnemonic >= 0) {
            newItem.setMnemonic(mnemonic);
        }

        if (accelerator >= 0) {
            newItem.setAccelerator(KeyStroke.getKeyStroke(accelerator, mask));
        }

        menuHash.put(name, function);

        return newItem;
    }

    private JMenuItem buildJMenuItem(String name, String function, boolean enabled) {
        return this.buildJMenuItem(name, function, enabled, -1, -1, -1);
    }

    private JMenuItem buildJMenuItem(String name, String function, boolean enabled, int mnemonic) {
        return this.buildJMenuItem(name, function, enabled, mnemonic, -1, -1);
    }

    private JMenuItem buildJMenuItem(String name, String function, boolean enabled, int accelerator, int mask) {
        return this.buildJMenuItem(name, function, enabled, -1, accelerator, mask);
    }

    private JMenuItem buildJMenuItem(String name, String function, boolean enabled, int mnemonic, int accelerator, int mask) {
        // if menu listener != null
        if (ml == null) {
            System.out.println("buildJMenuItem::MBI - MenuListener is NULL!");
            return null;
        }

        JMenuItem newItem = new JMenuItem(name);
        newItem.addActionListener(ml);
        newItem.setEnabled(enabled);

        if (mnemonic >= 0) {
            newItem.setMnemonic(mnemonic);
        }

        if (accelerator >= 0) {
            newItem.setAccelerator(KeyStroke.getKeyStroke(accelerator, mask));
        }

        menuHash.put(name, function);

        return newItem;
    }

    private JRadioButtonMenuItem buildJRadioButtonMenuItem(String name, String function, boolean enabled, boolean state, int mnemonic, int accelerator, int mask) {
        // if menu listener != null
        if (ml == null) {
            System.out.println("buildJRadioButtonMenuItem::MBI - Menu Listener is NULL!");
            return null;
        }

        JRadioButtonMenuItem newItem = new JRadioButtonMenuItem(name, state);
        newItem.addActionListener(ml);
        newItem.setEnabled(enabled);

        if (mnemonic >= 0) {
            newItem.setMnemonic(mnemonic);
        }

        if (accelerator >= 0) {
            newItem.setAccelerator(KeyStroke.getKeyStroke(accelerator, mask));
        }

        menuHash.put(name, function);

        return newItem;
    }

    public void clearGroupStates() {
        nodeList.clearState();
        edgeList.clearState();
    }

    public MyButtonGroup getEdgeList() {
        return edgeList;
    }

    public TreeSet<String> getEventSet() {
        return eventSet;
    }

    public static synchronized MBI getInstance() {
        if (_instance == null) {
            _instance = new MBI();
            return _instance;
        }
        else {
            return _instance;
        }
    }

    public MyButtonGroup getNodeList() {
        return nodeList;
    }
}