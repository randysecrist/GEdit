package edu.usu.cs.gui;

import java.awt.Color;
import java.util.List;
import java.util.Collections;

import edu.usu.cs.algorithms.Algorithm;

/**
 * Used to store global variables available to anyone in the
 * same virtual machine.
 *
 * Creation date: (3/29/2002 1:39:56 PM)
 * @author Randy Secrist
 * @version 1.2
 */ 
public class Globals {
	private static Globals _instance = null;
	private Algorithm[] algorithms = null;
	private String algorithmsPackage = "edu.usu.cs.algorithms";

	// Global graph wrappers
	private NodeWrapper selectedNode = null;	
	private List<NodeWrapper> nodes = Collections.synchronizedList(new java.util.ArrayList<NodeWrapper>());
	private List<EdgeWrapper> edges = Collections.synchronizedList(new java.util.ArrayList<EdgeWrapper>());
	private int numNodes = 0;
	private int numEdges = 0;

	public static final int FIXED_COLOR = 0;
	public static final int SELECT_COLOR = 1;
	public static final int NODE_COLOR = 2;
	public static final int STRESS_COLOR = 3;
	public static final int ARC_COLOR_1 = 4;
	public static final int ARC_COLOR_2 = 5;
	public static final int ARC_COLOR_3 = 6;
	public static final int TRAVERSED_NODE = 7;
	public static final int TRAVERSED_EDGE = 8;
	private static Color[] colors = {Color.red, Color.pink, new Color(250, 220, 100), Color.darkGray, Color.black, Color.pink, Color.red, new Color(25, 150, 50), new Color(45, 175, 200)};
	/**
	 * Globals constructor.
	 *
	 * <p>This class is a singleton - hence has a private constructor.
	 * @see Globals#getInstance()
	 */
	private Globals() {
		super();
	}
	/**
	 * Adds an edge to the global edge collection.
	 * @param e The edge to add.
	 */
	public synchronized void addEdge(EdgeWrapper e) {
		edges.add(e);
		numEdges = edges.size();
	}
	/**
	 * Adds an node to the global node collection.
	 * @param n The node to add.
	 */	
	public synchronized void addNode(NodeWrapper n) {
		nodes.add(n);
		numNodes = nodes.size();
	}
	/**
	 * Clears all nodes and edges.
	 */
	public synchronized void clearAll() {
		nodes.clear();
		edges.clear();
		numNodes = 0;
		numEdges = 0;
	}
	/**
	 * Forces the singleton instance to reload.
	 */
	public void forceReload() {
		_instance = new Globals();
	}
	/**
	 * Returns all current algorithm classes in use.
	 * @return Returns an array of algorithm instances.
	 */
	public synchronized Algorithm[] getAlgorithms() {
		if (algorithms == null) return new Algorithm[0];
		else return algorithms;
	}
	/**
	 * Returns the package name where all algorithm instances are loaded.
	 * @return The fully qualified package name of the algorithms package.
	 */
	public String getAlgorithmsPackage() {
		return algorithmsPackage;
	}
	/**
	 * Returns the ith color within the current color array.
	 * (The color array is a list of all colurs a node can be).
	 * 
	 * @param i The index of the color to lookup.
	 * @return The ith color within the current color array.
	 */
	public static Color getColor(int i) {
		if (i >= 0 && i < colors.length) return colors[i];
		else return null;
	}
	/**
	 * Returns the ith edge as a wrapped edge.
	 * @param i The index of the edge to lookup.
	 * @return The ith edge as a wrapped edge.
	 */
	public synchronized EdgeWrapper getEdge(int i) {
		return edges.get(i);
	}
	/**
	 * Globals is a singleton - and only one instance is loadable
	 * for each virtual machine running.  As such, it's constructors
	 * are private and can only be accessed by calls to this method.
	 *
	 * <p>Usage: Globals ref = Globals.getInstance();
	 */
	public static synchronized Globals getInstance() {
		if (_instance == null) {
			_instance = new Globals();
			return _instance;
		}
		else {
			return _instance;
		}
	}
	/**
	 * Returns the ith node as a wrapped node.
	 * @param i The index of the node to lookup.
	 * @return The ith node as a wrapped node.
	 */
	public synchronized NodeWrapper getNode(int i) {
		return nodes.get(i);
	}
	/**
	 * Returns the node based upon it's internal id.
	 * Note this is different than it's index in this classes
	 * internal collection.
	 *  
	 * @param id The node id to lookup. 
	 * @return A node as a wrapped node.
	 */
	public synchronized NodeWrapper getNodeById(int id) {
		for (int i = 0; i < numNodes; i++) {
			NodeWrapper n = nodes.get(i);
			if (id == n.getNode().getId()) return n;
		}
		return null;
	}
	/**
	 * Returns the number of edges in the current graph.
	 * @return The current number of edges in this graph.
	 */
	public synchronized int getNumEdges() {
		return numEdges;
	}
	/**
	 * Returns the number of nodes in the current graph.
	 * @return The current number of nodes in this graph.
	 */
	public synchronized int getNumNodes() {
		return numNodes;
	}
	/**
	 * Returns any node selected by the mouse, which could be null.
	 * @return Any node currently selected by the mouse.
	 */
	public synchronized NodeWrapper getSelectedNode() {
		return selectedNode;
	}
	/**
	 * Removes an edge from the display.
	 * @param e The edge to remove.
	 */
	public synchronized void removeEdge(EdgeWrapper e) {
		numEdges = edges.size()-1;
		edges.remove(e);
	}
	/**
	 * Removes a node from the display.
	 * @param n The node to remove.
	 */
	public synchronized void removeNode(NodeWrapper n) {
		numNodes = nodes.size()-1;
		nodes.remove(n);
	}
	/**
	 * Reset's the internal variables of this class,
	 * except for the loaded algorithms.
	 */
	public synchronized void reset() {
		Algorithm[] temp = algorithms;
		_instance = new Globals();
		_instance.algorithms = temp;
	}
	/**
	 * Sets all traversable booleans to false.
	 */
	public void resetAfterTraversal() {
		for (NodeWrapper node : nodes) {
			node.isTraversed = false;
		}
		for (EdgeWrapper edge : edges) {
			edge.isTraversed = false;
		}
	}
	/**
	 * Set's loaded algorithm collection.
	 * @param algs The algorithms to load.
	 */
	public synchronized void setAlgorithms(Algorithm[] algs) {
		this.algorithms = algs;
	}
	/**
	 * Set's a color within the color array.
	 * @param i The index within the array to set.
	 * @param c The color to set.
	 */
	public static void setColor(int i, Color c) {
		if (i >= 0 && i < colors.length) colors[i] = c;
	}
	/**
	 * Should be called by a listener which selects nodes from
	 * a graph surface.  Normally this is done with a mouse click.
	 *
	 * @param n the node selected to store.
	 */
	public synchronized void setSelectedNode(NodeWrapper n) {
		selectedNode = n;
	}
}
