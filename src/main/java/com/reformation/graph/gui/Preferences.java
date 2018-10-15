package com.reformation.graph.gui;

import com.reformation.graph.algorithms.Algorithm;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.border.TitledBorder;

/**
 * This is dialog which allows users to choose preferences
 * Creation date: (3/7/2002 6:32:41 PM)
 * @author Randy Secrist
 */
public class Preferences extends JDialog {
    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = -7890803180771947417L;

    private static Preferences _instance = null;

    // Algorithm Properties
    private String ALGORITHM_PACKAGE = null;
    private String ALGORITHMS = null;
    private String[] loadedAlgorithmNames = null;

    // Color Preferences:
    private ColorButtonListener cbl = null;
    private Color ARC_COLOR_1 = Globals.getColor(Globals.ARC_COLOR_1);
    private Color ARC_COLOR_2 = Globals.getColor(Globals.ARC_COLOR_2);
    private Color ARC_COLOR_3 = Globals.getColor(Globals.ARC_COLOR_3);
    private Color FIXED_COLOR = Globals.getColor(Globals.FIXED_COLOR);
    private Color NODE_COLOR = Globals.getColor(Globals.NODE_COLOR);
    private Color SELECT_COLOR = Globals.getColor(Globals.SELECT_COLOR);
    private Color TRAVERSED_EDGE = Globals.getColor(Globals.TRAVERSED_EDGE);
    private Color TRAVERSED_NODE = Globals.getColor(Globals.TRAVERSED_NODE);

    // Display Preferences:
    private String BALANCE_OPTION = null;
    private JRadioButton[] balanceOptions = new JRadioButton[2];
    private String EDGE_VIEW_OPTION = null;
    private String NODE_VIEW_OPTION = null;
    private JRadioButton[] edgeViewOptions = new JRadioButton[2];
    private JRadioButton[] nodeViewOptions = new JRadioButton[2];
    private Log log = null;
    private JFrame parent = null;
    private String path;
    private Properties props = null;

    class ColorButtonListener implements ActionListener {
        // Node Color Preferences
        JButton base;
        JButton dragged;

        JButton edge;
        JButton lessStressed;
        JButton moreStressed;
        JButton selected;
        JButton traversedEdge;
        JButton traversedNode;

        public void actionPerformed(ActionEvent e) {
            Color current = null;

            // Remember which color we might be changing.
            // Nodes
            if (e.getSource().equals(base)) {
                current = NODE_COLOR;
            }
            else if (e.getSource().equals(dragged)) {
                current = SELECT_COLOR;
            }
            else if (e.getSource().equals(selected)) {
                current = FIXED_COLOR;
            }
            else if (e.getSource().equals(traversedNode)) {
                current = TRAVERSED_NODE;
            }
            // Edges
            else if (e.getSource().equals(edge)) {
                current = ARC_COLOR_1;
            }
            else if (e.getSource().equals(lessStressed)) {
                current = ARC_COLOR_2;
            }
            else if (e.getSource().equals(moreStressed)) {
                current = ARC_COLOR_3;
            }
            else if (e.getSource().equals(traversedEdge)) {
                current = TRAVERSED_EDGE;
            }
            // Bring up a color chooser
            Color c = JColorChooser.showDialog(parent, "Choose A Color:", current);

            if (c == null) return;

            // Reset Global Color If Needed
            // Nodes
            if (e.getSource().equals(base)) {
                NODE_COLOR = c;
                base.setIcon(new ColorSwatch(NODE_COLOR));
            }
            else if (e.getSource().equals(dragged)) {
                SELECT_COLOR = c;
                dragged.setIcon(new ColorSwatch(SELECT_COLOR));
            }
            else if (e.getSource().equals(selected)) {
                FIXED_COLOR = c;
                selected.setIcon(new ColorSwatch(FIXED_COLOR));
            }
            else if (e.getSource().equals(traversedNode)) {
                TRAVERSED_NODE = c;
                traversedNode.setIcon(new ColorSwatch(TRAVERSED_NODE));
            }
            // Edges
            else if (e.getSource().equals(edge)) {
                ARC_COLOR_1 = c;
                edge.setIcon(new ColorSwatch(ARC_COLOR_1));
            }
            else if (e.getSource().equals(lessStressed)) {
                ARC_COLOR_2 = c;
                lessStressed.setIcon(new ColorSwatch(ARC_COLOR_2));
            }
            else if (e.getSource().equals(moreStressed)) {
                ARC_COLOR_3 = c;
                moreStressed.setIcon(new ColorSwatch(ARC_COLOR_3));
            }
            else if (e.getSource().equals(traversedEdge)) {
                TRAVERSED_EDGE = c;
                traversedEdge.setIcon(new ColorSwatch(TRAVERSED_EDGE));
            }
        }
    }

    class ColorSwatch implements Icon {
        Color myColor = null;
        public ColorSwatch(Color color) {
            myColor = color;
        }

        public int getIconHeight() {
            return 15;
        }
        public int getIconWidth() {
            return 15;
        }
        public void paintIcon(Component c, Graphics g, int x, int y) {
            g.setColor(Color.black);
            g.fillRect(x, y, getIconWidth(), getIconHeight());

            g.setColor(myColor);

            g.fillRect(x + 2, y + 2, getIconWidth() - 4, getIconHeight() - 4);
        }
    }

    /**
     * Precondition:
     *   JFrame f should be the parent window responsible for drawing
     *   dialog boxes so this class can properly display preferences.
     *   Log l should be the log class used to write to a log window.
     *
     * <p>Postcondition:
     *   Forces the instance (if any) of Preferences to reload all
     *   properties from disk.  Any calls made to Preferences after
     *   this would then reflect the properties file.
     *
     * @param parent the component that is responsible for drawing windows.
     * @param l the log class responsible for message logging.
     */
    public static synchronized Preferences forceReload(JFrame parent, Log l) {
        _instance = new Preferences(parent,l);
        return _instance;
    }

    /**
     * Precondition:
     *   This is called upon application startup, or at any other time
     *   during runtime.  As such, since GEdit loads an instance of this
     *   class, it should never upon initalization load GEdit or an infinte
     *   class load loop would result.
     *
     * <p>Postcondition:
     *   Ensures that only ONE copy of Preferences is instantiated within
     *   a specific java virtual machine.
     * @param parent the component that is responsible for drawing windows.
     * @param l the log class responsible for message logging.
     */
    public static synchronized Preferences getInstance(JFrame parent, Log l) {
        if (_instance == null) {
            _instance = new Preferences(parent, l);
            return _instance;
        }
        else {
            return _instance;
        }
    }

    /**
     * Precondition:
     *   This class can not be instantied directly.  You must call
     *   Preferences.getInstance() to do so.
     *
     * <p>Postcondition:
     *   Loads a java.util.Properties file into local variable props,
     *   then proceeds to draw each tabbed pane for display.
     * @param parent the component that is responsible for drawing windows.
     * @param theLog the log class responsible for message logging.
     */
    private Preferences(JFrame parent, Log theLog) {
        super(parent, "Preferences", true);
        this.parent = parent;
        this.log = theLog;

        // Load properties
        props = new Properties();

        String seperator = System.getProperty("file.separator");
        path = "." + seperator + "GEdit.Properties"; // (Current Working Director)
        File file = new File(path);
        if (file.canRead()) {
            try {
                FileInputStream in = new FileInputStream(file);
                this.init(in, parent);
            }
            catch (java.lang.Throwable e) {
                // Do Nothing...should never get here if canRead
            }
        }
        else {
            try {
                InputStream in = this.getClass().getResourceAsStream("/GEdit.properties");
                this.init(in, parent);
            }
            catch (java.lang.Throwable e) {
                // Load Default Algorithms (All Other Preferences should already be set.)
                ALGORITHM_PACKAGE = Globals.getInstance().getAlgorithmsPackage();
                ALGORITHMS = "BreadthFirstSearch,BreadthFirstTraversal,CriticalPath,DepthFirstSearch,DepthFirstTraversal,MinimumSpanningTree,ShortestPath,ShortestPathAllPairs,SpanningTree,Topological";
                Globals.getInstance().setAlgorithms(this.getAlgorithms());
                log.write("Preferences::Preferences - Unable to load custom preferences - using defaults.\n" + e.getLocalizedMessage());
            }
        }

        JPanel container = new JPanel(true);
        container.setLayout(new BorderLayout());

        JTabbedPane tabs = new JTabbedPane();
        JPanel startup = buildStartupPanel();
        JPanel colors = buildColorsPanel();
        tabs.addTab("Startup", null, startup);
        tabs.addTab("Colors", null, colors);

        // Add cancel & ok buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(e -> cancelPressed());
        buttonPanel.add(cancel);
        JButton ok = new JButton("OK");
        ok.addActionListener(e -> okPressed());
        buttonPanel.add(ok);
        getRootPane().setDefaultButton(ok);

        container.add(tabs, BorderLayout.CENTER);
        container.add(buttonPanel, BorderLayout.SOUTH);
        getContentPane().add(container);
        pack();
        centerDialog();
    }

    private JPanel buildColorsPanel() {
        JPanel panel = new JPanel(true);
        panel.setLayout(new GridLayout(1, 0));

        cbl = new ColorButtonListener();

        // Node Colors Panel
        JPanel nodeColorPanel = new JPanel(true);
        nodeColorPanel.setLayout(new ColumnLayout());
        nodeColorPanel.setBorder(new TitledBorder("Node Colors"));

        // Node Base
        cbl.base = new JButton("Background");
        cbl.base.setIcon(new ColorSwatch(NODE_COLOR));
        cbl.base.addActionListener(cbl);

        cbl.selected = new JButton("Selected");
        cbl.selected.setIcon(new ColorSwatch(FIXED_COLOR));
        cbl.selected.addActionListener(cbl);

        cbl.dragged = new JButton("Dragged");
        cbl.dragged.setIcon(new ColorSwatch(SELECT_COLOR));
        cbl.dragged.addActionListener(cbl);

        cbl.traversedNode = new JButton("Traversed");
        cbl.traversedNode.setIcon(new ColorSwatch(TRAVERSED_NODE));
        cbl.traversedNode.addActionListener(cbl);

        nodeColorPanel.add(cbl.base);
        nodeColorPanel.add(cbl.selected);
        nodeColorPanel.add(cbl.dragged);
        nodeColorPanel.add(cbl.traversedNode);

        // Edge Colors Panel
        JPanel edgeColorPanel = new JPanel(true);
        edgeColorPanel.setLayout(new ColumnLayout());
        edgeColorPanel.setBorder(new TitledBorder("Edge Colors"));

        cbl.edge = new JButton("Balanced");
        cbl.edge.setIcon(new ColorSwatch(ARC_COLOR_1));
        cbl.edge.addActionListener(cbl);

        cbl.lessStressed = new JButton("Less Stressed");
        cbl.lessStressed.setIcon(new ColorSwatch(ARC_COLOR_2));
        cbl.lessStressed.addActionListener(cbl);

        cbl.moreStressed = new JButton("More Stressed");
        cbl.moreStressed.setIcon(new ColorSwatch(ARC_COLOR_3));
        cbl.moreStressed.addActionListener(cbl);

        cbl.traversedEdge = new JButton("Traversed");
        cbl.traversedEdge.setIcon(new ColorSwatch(TRAVERSED_EDGE));
        cbl.traversedEdge.addActionListener(cbl);

        edgeColorPanel.add(cbl.edge);
        edgeColorPanel.add(cbl.lessStressed);
        edgeColorPanel.add(cbl.moreStressed);
        edgeColorPanel.add(cbl.traversedEdge);

        panel.add(nodeColorPanel);
        panel.add(edgeColorPanel);

        return panel;
    }

    /**
     * Postcondition:
     *   Creates a Panel component which lists any startup preferences
     *   that may exist.
     */
    private JPanel buildStartupPanel() {
        JPanel generals = new JPanel(true);
        generals.setLayout(new GridLayout(1, 0));

        // Node View Preference
        JPanel nodeViewPanel = new JPanel(true);
        nodeViewPanel.setLayout(new ColumnLayout());
        nodeViewPanel.setBorder(new TitledBorder("Node View"));

        ButtonGroup nodeViewGroup = new ButtonGroup();
        nodeViewOptions[0] = new JRadioButton(MBI.NODE_NAME);
        nodeViewOptions[1] = new JRadioButton(MBI.NODE_ID);
        // Set Default Selected Property:
        nodeViewOptions[0].setSelected(true);
        // Add to dialog and set selected to match property.
        for (JRadioButton nodeViewOption : nodeViewOptions) {
            nodeViewGroup.add(nodeViewOption);
            nodeViewPanel.add(nodeViewOption);

            if (NODE_VIEW_OPTION != null &&
                NODE_VIEW_OPTION.equalsIgnoreCase(nodeViewOption.getText())) {
                nodeViewOption.setSelected(true);
            }
        }
        generals.add(nodeViewPanel);

        // Edge View Preference
        JPanel edgeViewPanel = new JPanel(true);
        edgeViewPanel.setLayout(new ColumnLayout());
        edgeViewPanel.setBorder(new TitledBorder("Edge View"));

        ButtonGroup edgeViewGroup = new ButtonGroup();
        edgeViewOptions[0] = new JRadioButton(MBI.EDGE_WEIGHTS);
        edgeViewOptions[1] = new JRadioButton(MBI.EDGE_STRESS);
        // Set Default Selected Property:
        edgeViewOptions[0].setSelected(true);
        // Add to dialog and set selected to match property.
        for (int i = 0; i < nodeViewOptions.length; i++) {
            edgeViewGroup.add(edgeViewOptions[i]);
            edgeViewPanel.add(edgeViewOptions[i]);

            if (EDGE_VIEW_OPTION != null &&
                EDGE_VIEW_OPTION.equalsIgnoreCase(edgeViewOptions[i].getText())) {
                edgeViewOptions[i].setSelected(true);
            }
        }
        generals.add(edgeViewPanel);

        // Balance On Load Preference
        JPanel balancePanel = new JPanel(true);
        balancePanel.setLayout(new ColumnLayout());
        balancePanel.setBorder(new TitledBorder("Balance"));
        ButtonGroup balanceGroup = new ButtonGroup();
        balanceOptions[0] = new JRadioButton("On");
        balanceOptions[1] = new JRadioButton("Off");
        // Set Default Selected Property:
        balanceOptions[0].setSelected(true);
        // Add to dialog and set selected to match property.
        for (JRadioButton balanceOption : balanceOptions) {
            balanceGroup.add(balanceOption);
            balancePanel.add(balanceOption);

            if (BALANCE_OPTION != null &&
                BALANCE_OPTION.equalsIgnoreCase(balanceOption.getText())) {
                balanceOption.setSelected(true);
            }
        }
        generals.add(balancePanel);

        return generals;
    }

    /**
     * Postcondition:
     *   Hides the prefernces dialog when a user hits cancel.
     */
    public void cancelPressed() {
        this.setVisible(false);
        this.NODE_COLOR = Globals.getColor(Globals.NODE_COLOR);
        this.ARC_COLOR_1 = Globals.getColor(Globals.ARC_COLOR_1);
        cbl.base.setIcon(new ColorSwatch(NODE_COLOR));
        cbl.edge.setIcon(new ColorSwatch(ARC_COLOR_1));
    }

    /**
     * Centers the dialog on the screen.
     */
    protected void centerDialog() {
        Dimension screenSize = this.getToolkit().getScreenSize();
        Dimension size = this.getSize();
        screenSize.height = screenSize.height / 2;
        screenSize.width = screenSize.width / 2;
        size.height = size.height / 2;
        size.width = size.width / 2;
        int y = screenSize.height - size.height;
        int x = screenSize.width - size.width;
        this.setLocation(x, y);
    }

    /**
     * <p>Postconditions:
     *  Returns a delimited string of the currently loaded Algorithm Class Names.
     */
    public String getAlgorithmNames(String[] names) {
        StringBuffer buf = new StringBuffer();

        for (int i = 0; i < names.length; i++) {
            if (i != names.length - 1) {
                buf.append(names[i] + ",");
            }
            else {
                buf.append(names[i]);
            }
        }
        return buf.toString();
    }

    /**
     * Preconditions:
     *  Assumes the ALGORITHMS, and ALGORITHM_PACKAGE have been
     *  specified in the GEdit.Properties file.  If they have not,
     *  this will use whatever has been set in the ALGORITHMS and
     *  ALGORITHM_PACKAGE class variables - and may return null.
     *
     *  <p>Currently, the two class variables are set in the constructor if
     *  the properties file can not be found.
     *
     * <p>Postconditions:
     *  Returns an Array of type Algorithm.
     */
    public Algorithm[] getAlgorithms() {
        StringTokenizer token = new StringTokenizer(ALGORITHMS, ",");
        Algorithm[] algorithms = new Algorithm[token.countTokens()];
        loadedAlgorithmNames = new String[token.countTokens()];

        String className = new String();
        try {
            int i = 0;
            while (token.hasMoreTokens()) {
                className = (String) token.nextElement();
                loadedAlgorithmNames[i] = className;
                algorithms[i] = ((Algorithm) Class.forName(ALGORITHM_PACKAGE + "." + className).newInstance());
                i++;
            }
            return algorithms;
        }
        catch (java.lang.Throwable e) {
            if (e instanceof ClassNotFoundException) {
                log.write("getAlgorithms::Preferences - Could not find class: " + className + " - " + e.getLocalizedMessage());
            }
            else if (e instanceof InstantiationException) {
                log.write("getAlgorithms::Preferences - " + e.getLocalizedMessage());
            }
            else if (e instanceof IllegalAccessException) {
                log.write("getAlgorithms::Preferences - " + e.getLocalizedMessage());
            }
            else {
                log.write("getAlgorithms::Preferences - General Error: " + e.getLocalizedMessage());
            }
        }
        return new Algorithm[0];
    }

    /**
     * Postcondition:
     * Returns the user's prefered balance on / off status.
     */
    public boolean getBalanceStatus() {
        if (BALANCE_OPTION  != null &&
            BALANCE_OPTION.equalsIgnoreCase("ON")) return true;
        else return false;
    }

    /**
     * Postcondition:
     * Returns the user's prefered edge label view status.
     */
    public String getEdgeViewStatus() {
        if (EDGE_VIEW_OPTION != null) return EDGE_VIEW_OPTION;
        else return "";
    }

    /**
     * Postcondition:
     * Returns the user's prefered node label view status.
     */
    public String getNodeViewStatus() {
        if (NODE_VIEW_OPTION != null) return NODE_VIEW_OPTION;
        else return "";
    }

    /**
     * Postconditions:
     * Loads private local variables with static properties.
     *
     *  This also instantiates each algorithm's setParent(JFrame parent)
     *  so that the Algorithms will be able to draw to the screen.
     */
    private void init(InputStream in, JFrame f) throws IOException {
        props.load(in);

        // Proceed to get all needed properties.
        NODE_VIEW_OPTION = props.getProperty("GEDIT.NODE_VIEW_OPTION");
        EDGE_VIEW_OPTION = props.getProperty("GEDIT.EDGE_VIEW_OPTION");
        BALANCE_OPTION = props.getProperty("GEDIT.BALANCE_OPTION");
        ALGORITHMS = props.getProperty("GEDIT.ALGORITHMS");
        ALGORITHM_PACKAGE = props.getProperty("GEDIT.ALGORITHM_PACKAGE");

        // Initialize Traversal Algorithms
        Algorithm[] algs = this.getAlgorithms();
        for (Algorithm alg : algs) {
            alg.setParent(f);
        }
        Globals.getInstance().setAlgorithms(algs);

        // Initalize Colors
        StringTokenizer colorTokens;
        int red, green, blue;

        // Nodes
        colorTokens = new StringTokenizer(props.getProperty("GEDIT.NODE_COLOR"), ",");
        red = new Integer(colorTokens.nextToken());
        green = new Integer(colorTokens.nextToken());
        blue = new Integer(colorTokens.nextToken());
        NODE_COLOR = new Color(red, green, blue);
        Globals.setColor(Globals.NODE_COLOR, NODE_COLOR);

        colorTokens = new StringTokenizer(props.getProperty("GEDIT.FIXED_COLOR"), ",");
        red = new Integer(colorTokens.nextToken());
        green = new Integer(colorTokens.nextToken());
        blue = new Integer(colorTokens.nextToken());
        FIXED_COLOR = new Color(red, green, blue);
        Globals.setColor(Globals.FIXED_COLOR, FIXED_COLOR);

        colorTokens = new StringTokenizer(props.getProperty("GEDIT.SELECT_COLOR"), ",");
        red = new Integer(colorTokens.nextToken());
        green = new Integer(colorTokens.nextToken());
        blue = new Integer(colorTokens.nextToken());
        SELECT_COLOR = new Color(red, green, blue);
        Globals.setColor(Globals.SELECT_COLOR, SELECT_COLOR);

        colorTokens = new StringTokenizer(props.getProperty("GEDIT.TRAVERSED_NODE"), ",");
        red = new Integer(colorTokens.nextToken());
        green = new Integer(colorTokens.nextToken());
        blue = new Integer(colorTokens.nextToken());
        TRAVERSED_NODE = new Color(red, green, blue);
        Globals.setColor(Globals.TRAVERSED_NODE, TRAVERSED_NODE);

        // Edges
        colorTokens = new StringTokenizer(props.getProperty("GEDIT.ARC_COLOR_1"), ",");
        red = new Integer(colorTokens.nextToken());
        green = new Integer(colorTokens.nextToken());
        blue = new Integer(colorTokens.nextToken());
        ARC_COLOR_1 = new Color(red, green, blue);
        Globals.setColor(Globals.ARC_COLOR_1, ARC_COLOR_1);

        colorTokens = new StringTokenizer(props.getProperty("GEDIT.ARC_COLOR_2"), ",");
        red = new Integer(colorTokens.nextToken());
        green = new Integer(colorTokens.nextToken());
        blue = new Integer(colorTokens.nextToken());
        ARC_COLOR_2 = new Color(red, green, blue);
        Globals.setColor(Globals.ARC_COLOR_2, ARC_COLOR_2);

        colorTokens = new StringTokenizer(props.getProperty("GEDIT.ARC_COLOR_3"), ",");
        red = new Integer(colorTokens.nextToken());
        green = new Integer(colorTokens.nextToken());
        blue = new Integer(colorTokens.nextToken());
        ARC_COLOR_3 = new Color(red, green, blue);
        Globals.setColor(Globals.ARC_COLOR_3, ARC_COLOR_3);

        colorTokens = new StringTokenizer(props.getProperty("GEDIT.TRAVERSED_EDGE"), ",");
        red = new Integer(colorTokens.nextToken());
        green = new Integer(colorTokens.nextToken());
        blue = new Integer(colorTokens.nextToken());
        TRAVERSED_EDGE = new Color(red, green, blue);
        Globals.setColor(Globals.TRAVERSED_EDGE, TRAVERSED_EDGE);
    }

    /**
     * Postcondition:
     *  Sets any new states for all properties and stores it back
     *  out on the file system.
     */
    public void okPressed() {
        // Set Properties
        for (JRadioButton nodeViewOption : nodeViewOptions) {
            if (nodeViewOption.isSelected()) {
                props.setProperty("GEDIT.NODE_VIEW_OPTION", nodeViewOption.getText());
            }
        }
        for (JRadioButton edgeViewOption : edgeViewOptions) {
            if (edgeViewOption.isSelected()) {
                props.setProperty("GEDIT.EDGE_VIEW_OPTION", edgeViewOption.getText());
            }
        }
        for (JRadioButton balanceOption : balanceOptions) {
            if (balanceOption.isSelected()) {
                props.setProperty("GEDIT.BALANCE_OPTION", balanceOption.getText());
            }
        }

        // Set Algorithm Properties:
        props.setProperty("GEDIT.ALGORITHM_PACKAGE", ALGORITHM_PACKAGE);
        props.setProperty("GEDIT.ALGORITHMS", this.getAlgorithmNames(loadedAlgorithmNames));

        // Set Color Properties & Update Global Colors
        props.setProperty("GEDIT.NODE_COLOR", NODE_COLOR.getRed() + "," + NODE_COLOR.getGreen() + "," + NODE_COLOR.getBlue());
        props.setProperty("GEDIT.FIXED_COLOR", FIXED_COLOR.getRed() + "," + FIXED_COLOR.getGreen() + "," + FIXED_COLOR.getBlue());
        props.setProperty("GEDIT.SELECT_COLOR", SELECT_COLOR.getRed() + "," + SELECT_COLOR.getGreen() + "," + SELECT_COLOR.getBlue());
        props.setProperty("GEDIT.TRAVERSED_NODE", TRAVERSED_NODE.getRed() + "," + TRAVERSED_NODE.getGreen() + "," + TRAVERSED_NODE.getBlue());
        props.setProperty("GEDIT.ARC_COLOR_1", ARC_COLOR_1.getRed() + "," + ARC_COLOR_1.getGreen() + "," + ARC_COLOR_1.getBlue());
        props.setProperty("GEDIT.ARC_COLOR_2", ARC_COLOR_2.getRed() + "," + ARC_COLOR_2.getGreen() + "," + ARC_COLOR_2.getBlue());
        props.setProperty("GEDIT.ARC_COLOR_3", ARC_COLOR_3.getRed() + "," + ARC_COLOR_3.getGreen() + "," + ARC_COLOR_3.getBlue());
        props.setProperty("GEDIT.TRAVERSED_EDGE", TRAVERSED_EDGE.getRed() + "," + TRAVERSED_EDGE.getGreen() + "," + TRAVERSED_EDGE.getBlue());

        // Set Globals
        Globals.setColor(Globals.NODE_COLOR, NODE_COLOR);
        Globals.setColor(Globals.FIXED_COLOR, FIXED_COLOR);
        Globals.setColor(Globals.SELECT_COLOR, SELECT_COLOR);
        Globals.setColor(Globals.TRAVERSED_NODE, TRAVERSED_NODE);
        Globals.setColor(Globals.ARC_COLOR_1, ARC_COLOR_1);
        Globals.setColor(Globals.ARC_COLOR_2, ARC_COLOR_2);
        Globals.setColor(Globals.ARC_COLOR_3, ARC_COLOR_3);
        Globals.setColor(Globals.TRAVERSED_EDGE, TRAVERSED_EDGE);

        // Save Properties File:
        try {
            OutputStream out = new FileOutputStream(path);
            String header =	"######  GEDIT PROPERTIES FILE\n";
            props.store(out,header);
        }
        catch (java.lang.Throwable e) {
            if (e instanceof FileNotFoundException) {
                log.write("OKPressed::Prefs - Unable to save preferences!\n" + e.getLocalizedMessage());
            }
            else if (e instanceof IOException) {
                log.write("OKPressed::Prefs - Unable to save preferences!\n" + e.getLocalizedMessage());
            }
        }

        // Close Dialog
        this.setVisible(false);
    }
}