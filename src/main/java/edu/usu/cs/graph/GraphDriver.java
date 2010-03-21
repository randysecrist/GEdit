package edu.usu.cs.graph;

import edu.usu.cs.algorithms.SpanningTree;

/**
 * Command line driver for a graph class. This was used as a quick and dirty
 * testing tool to verify both the graph class, and the graphical interface. * 
 * Creation date: (2/2/2002 1:26:23 PM)
 * 
 * @author Randy Secrist
 */
class GraphDriver {

	private static final String DEFAULT_DATA_WRAPPER = "edu.usu.cs.graph.StringObj";
	private static String namedWrapper;

	public static void main(String[] argv) {
		/*
		 * // is there anything to do? if (argv.length == 0) { printUsage();
		 * System.exit(1); }
		 */
		// process arguments
		for (int i = 0; i < argv.length; i++) {
			String arg = argv[i];
			if (arg.startsWith("-")) {
				String option = arg.substring(1);
				if (option.equals("d")) {
					// get data wrapper name
					if (++i == argv.length) {
						System.err.println("error: Missing argument to -d option.");
					}
					namedWrapper = argv[i];
				}
				if (option.equals("h")) {
					printUsage();
					continue;
				}
			}
		}
		GraphDriver gd = new GraphDriver();
	}

	//
	// Private static methods
	//
	/** Prints the usage. */
	private static void printUsage() {
		System.err.println("usage: java dom.GraphDriver (options) uri ...");
		System.err.println();
		System.err.println("options:");
		System.err.println("  -d name  Select data wrapper by name.");
		System.err.println("  -h       This help screen.");
		System.err.println();
		System.err.println("defaults:");
		System.err.println("  Data Wrapper:     " + DEFAULT_DATA_WRAPPER);
	}

	private Data dataSpawn = null;

	/**
	 * Constructs a new graph driver.
	 */
	public GraphDriver() {
		super();
		Graph graph = new Graph();
		graph.setDirected(false);
		System.out.println("Directed Status - " + graph.getDirected());
		try {
			int a = graph.addNode(getData("a"));
			int b = graph.addNode(getData("b"));
			int c = graph.addNode(getData("c"));
			int d = graph.addNode(getData("d"));
			//int e = graph.addNode(getData("e"));
			int f = graph.addNode(getData("f"));
			int g = graph.addNode(getData("g"));
			// Test node integrity.
			System.out.println("Node List");
			Node[] nodes = graph.getNodes();
			for (int i = 0; i < nodes.length; i++) {
				if (nodes[i] != null) {
					Data obj = nodes[i].getData();
					if (obj != null)
						System.out.println(nodes[i].getData().getDisplayName());
					else
						System.out.println("DATA NULL AT " + i);
				}
				else
					System.out.println("NODE NULL AT " + i);
			}
			graph.addEdge(new Edge(a, f, 9.0));
			graph.addEdge(new Edge(f, b, 2.0));
			graph.addEdge(new Edge(a, b, 1.0));
			graph.addEdge(new Edge(c, d, 7.0));
			graph.addEdge(new Edge(g, d, 5));
			// Test edge integrity
			System.out.println("Edge List");
			Edge[] edges = graph.getEdges();
			for (int i = 0; i < edges.length; i++) {
				if (edges[i] != null) {
					System.out.println(edges[i].getSource() + " - " + edges[i].getDest() + " - " + edges[i].getWeight());
				}
				else
					System.out.println("EDGE NULL AT " + i);
			}
			System.out.println("Island Count: " + graph.getIslandcount());
			for (int i = 0; i < graph.getIslandcount(); i++) {
				Node[] nod = graph.getNodes(i);
				Edge[] edg = graph.getEdges(i);
				for (int j = 0; j < nod.length; j++) {
					System.out.println("Island #" + i + "::" + nod[j].getData().getDisplayName() + "::" + nod[j].getId());
				}
				for (int k = 0; k < edg.length; k++) {
					System.out.println("Island #" + i + "::" + edg[k].getSource() + "::" + edg[k].getDest() + "::" + edg[k].getWeight());
				}
			}
			/*
			 * // Remove Node (can remove a thru f) g.removeNode(f); // Remove
			 * edges //g.removeEdge(g.getEdge(a,b)); // Print Status:
			 * System.out.println("\n\n----- NODES REMOVED -----\n");
			 * System.out.println("Island Count: " + g.getIslandcount()); for
			 * (int i = 0; i < g.getIslandcount(); i++) { Node[] nod =
			 * g.getNodes(i); Edge[] edg = g.getEdges(i); for (int j = 0; j <
			 * nod.length; j++) { System.out.println( "Island #" + i + "::" +
			 * nod[j].getData().getDisplayName() + "::" + nod[j].getId()); }
			 * for (int k = 0; k < edg.length; k++) { System.out.println(
			 * "Island #" + i + "::" + edg[k].getSource() + "::" +
			 * edg[k].getDest() + "::" + edg[k].getWeight()); } }
			 * this.runAlgorithm(g); System.out.println("Directed Status after
			 * run - " + g.getDirected());
			 * System.out.println("GraphDriver::GraphDriver - All Done! -
			 * Exiting..."); System.exit(0);
			 */
		}
		catch (java.lang.Throwable e) {
			e.printStackTrace();
		}
	}

	private Data getData(String val) throws Exception {
		if (dataSpawn == null) {
			// create data
			try {
				dataSpawn = ((Data) Class.forName(namedWrapper).newInstance()).getInstance(val);
			}
			catch (Exception e) {
				dataSpawn = null;
				System.out.println("getNewData::GraphDriver - Unable to instantiate named data wrapper! (" + namedWrapper + ")");
			}
			// use default wrapper?
			if (dataSpawn == null) {
				try {
					dataSpawn = ((Data) Class.forName(DEFAULT_DATA_WRAPPER).newInstance()).getInstance(val);
				}
				catch (Exception e) {
					// Unable to create data object - give up.
					e.printStackTrace();
					System.exit(1);
				}
			}
			return dataSpawn;
		}
		else
			return dataSpawn.getInstance(val);
	}

	public void runAlgorithm(Graph g) {
		// Replace the following with your algorithm.
		SpanningTree algorithm = new SpanningTree();
		PathContainer pc = algorithm.runMe(g);
		StringBuffer buf = new StringBuffer();
		Path[] paths = pc.getPaths();
		for (int i = 0; i < paths.length; i++) {
			// # Nodes will == # of Edges (but edges could be null.
			// For ith element, print Node first, then Edge.
			Node[] nodes = paths[i].getNodePath();
			Edge[] edges = paths[i].getEdgePath();
			for (int j = 0; j < nodes.length; j++) {
				// Print Node
				if (nodes != null && j < nodes.length && j >= 0 && nodes[j] != null)
					buf.append("Path #" + (i + 1) + "\tNode #" + (j + 1) + "\t" + nodes[j].getData().getDisplayName() + "\n");
				// Print Edge
				if (edges != null && j < edges.length && j >= 0 && edges[j] != null)
					buf.append("Path #" + (i + 1) + "\tEdge #" + (j + 1) + " - (Src) - (Dest)\t" + edges[j].getSource() + " - " + edges[j].getDest() + "\n");
			}
		}
		System.out.println("--- ALGORITHM RESULTS ---\n");
		System.out.println(buf.toString());
	}
}