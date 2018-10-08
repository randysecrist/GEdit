package edu.usu.cs.algorithms;

import java.util.ArrayList;

import edu.usu.cs.graph.Edge;
import edu.usu.cs.graph.Graph;
import edu.usu.cs.graph.Node;
import edu.usu.cs.graph.Path;
import edu.usu.cs.graph.PathContainer;
import edu.usu.cs.graph.PathException;

/**
 * breadth first search Creation Date: 2/9/2002
 * 
 * @author Chad Coats
 */
public class BreadthFirstSearch extends Algorithm {

	public boolean works(Graph g) {
		if (g.isDirected() == true) //  if directed cannot traverse
			return false;
		else if (g.size() < 1) //  if 0 nodes, cannot traverse
			return false;
		return true; // can successfully run algorithms
	}

	/**
	 * performs breadth first search of graph 'g'. takes as arguments id of
	 * starting node 'start', and id of search key returns a Vector of nodes
	 * visited in search
	 */
	public PathContainer doAlgorithm(Graph g, int start, int key) {
		PathContainer pc = new PathContainer();
		if (g.size() == 1) {
			Node[] oneNode = g.getNodes();
			Edge[] dummy = new Edge[1];
			try {
				Path p = new Path(oneNode, dummy, 0);
				pc.addPath(p);
				return pc;
			}
			catch (PathException p) {}
		}
		final int size = g.getHeapSize();
		int index = 0; // iterator for vector
		Node[] nodeArray = new Node[size];
		Edge[][] matrix = g.getEdgesMatrix();
		ArrayList<Node> visited = new ArrayList<Node>(); // vector of nodes traversed
		ArrayList<Edge> edges = new ArrayList<Edge>();
		nodeArray = g.getHeap();
		visited.add(nodeArray[start]);
		while (index < visited.size()) {
			Node temp = (Node) visited.get(index);
			for (int i = 0; i < size; i++)
				if (g.isEdge(temp.getId(), i) && !isAllowed(visited, i)) {
					visited.add(nodeArray[i]);
					edges.add(matrix[temp.getId()][i]);
					if (i == key) {
						try {
							Node[] n = new Node[1];
							Edge[] e = new Edge[1];
							Path p = new Path(n, e, 1);
							synchronizePath(nodeArray, edges, 0, p);
							pc.addPath(p);
						}
						catch (PathException x) {
							x.printStackTrace();
						}
						return pc;
					}
				}
			index++;
		}
		return new PathContainer();
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
		return "Breadth First Search";
	}

	protected PathContainer startAlgorithm(Graph theGraph) {
		int begin;
		int end;
		Edge edge = showSourceDestDialog(theGraph);
		if (edge != null) {
			begin = edge.getSource();
			end = edge.getDest();
			PathContainer pc = doAlgorithm(theGraph, begin, end);
			if (pc.size() == 0)
				this.showWarningDialog("Destination Node not found");
			return pc;
		}
		else
			return new PathContainer();
	}
}