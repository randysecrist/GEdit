package edu.usu.cs.algorithms;

import edu.usu.cs.graph.Edge;
import edu.usu.cs.graph.Graph;
import edu.usu.cs.graph.Node;
import edu.usu.cs.graph.Path;
import edu.usu.cs.graph.PathContainer;
import edu.usu.cs.graph.PathException;

import java.util.ArrayList;

/**
 * breadth first traversal Creation Date: 2/9/2002
 * 
 * @author Chad Coats
 */
public class BreadthFirstTraversal extends Algorithm {
	
	/**
	 * Constructs the default BreadthFirstTraversal Sort Algorithm.
	 */
	public BreadthFirstTraversal() {
		super();
	}
	
	/**
	 * Constructs a BreadthFirstTraversal Sort Algorithm with a Visitor.
	 * @param v The visitor to use during traversal.
	 */
	public BreadthFirstTraversal(Visitor v) {
		super();
		this.visitor = v;
	}

	public boolean works(Graph g) {
		if (g.isDirected() == true) //  if directed cannot traverse
			return false;
		else if (g.size() < 1) //  if 1 or less nodes, cannot traverse
			return false;
		return true; // can successfully run algorithms
	}

	/**
	 * performs breadth first traversal of graph 'g' starting with node
	 * 'start'. returns Vector of nodes visited in traversal
	 */
	public PathContainer doAlgorithm(final Graph g, int start) {
		PathContainer pc = new PathContainer();
		if (start < 0 || start >= g.size())
			return new PathContainer();
		if (g.size() == 1) {
			Node[] oneNode = g.getNodes();
			Edge[] dummy = new Edge[1];
			try {
				if (oneNode != null && oneNode.length > 0 && oneNode[0] != null)
					visitor.visit(oneNode[0]);
				Path p = new Path(oneNode, dummy, 0);
				pc.addPath(p);
				return pc;
			}
			catch (PathException p) {}
		}
		final int size = g.getHeapSize();
		int index = 0; // iterator for vector
		Node[] nodeArray = new Node[size];
		ArrayList<Node> visited = new ArrayList<Node>(); // nodes traversed
		ArrayList<Edge> edges = new ArrayList<Edge>();
		Edge[][] matrix = g.getEdgesMatrix();
		nodeArray = g.getHeap();
		visited.add(nodeArray[start]); visitor.visit(nodeArray[start]);
		while (index < visited.size()) {
			Node temp = (Node) visited.get(index);
			for (int i = 0; i < size; i++)
				if (g.isEdge(temp.getId(), i) && !isAllowed(visited, i)) {
					visited.add(nodeArray[i]); visitor.visit(nodeArray[i]);
					Edge e = matrix[temp.getId()][i];
					edges.add(e); visitor.visit(e);
				}
			index++;
		}
		try {
			Node[] ntmp = new Node[1];
			Edge[] etmp = new Edge[1];
			Path p = new Path(ntmp, etmp, 0);
			synchronizePath(nodeArray, edges, 0, p);
			pc.addPath(p);
		}
		catch (PathException x) {}
		return pc;
	}

	/**
	 * determines if 'key' value is in Vector A, where 'key' is the ID of the
	 * node in the Graph
	 */
	private boolean isAllowed(ArrayList<Node> A, int key) {
		for (int i = 0; i < A.size(); i++) {
			Node temp = (Node) A.get(i);
			if (temp.getId() == key)
				return true;
		}
		return false;
	}

	private void synchronizePath(Node[] node, ArrayList<Edge> edge, int pathId, Path tmp) {
		Edge[] e = new Edge[3 * edge.size()];
		Node[] n = new Node[3 * edge.size()];
		for (int i = 0; i < edge.size(); i++) {
			Edge eTemp = (Edge) edge.get(i);
			e[3 * i] = eTemp;
			n[3 * i] = node[eTemp.getSource()];
			n[3 * i + 1] = node[eTemp.getDest()];
		}
		tmp.setEdgePath(e);
		tmp.setNodePath(n);
		tmp.setId(pathId);
	}

	public String getMenuName() {
		return "Breadth First Traversal";
	}

	protected PathContainer startAlgorithm(Graph theGraph) {
		int begin;
		Node node = showSourceDialog(theGraph);
		if (node != null) {
			begin = node.getId();
			PathContainer pc = doAlgorithm(theGraph, begin);
			if (pc.size() == 0)
				this.showWarningDialog("Breadth First Traversal could not be performed.");
			return pc;
		}
		else
			return new PathContainer();
	}
}