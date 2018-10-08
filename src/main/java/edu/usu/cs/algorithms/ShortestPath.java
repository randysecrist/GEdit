package edu.usu.cs.algorithms;

import edu.usu.cs.graph.Edge;
import edu.usu.cs.graph.Graph;
import edu.usu.cs.graph.Node;
import edu.usu.cs.graph.Path;
import edu.usu.cs.graph.PathContainer;
import edu.usu.cs.graph.PathException;

/**
 * Finds the shortest path in a graph data type.
 * 
 * @author Seth Humphries
 * @author Karl Smith
 * @author Randy Secrist
 */
public class ShortestPath extends Algorithm {

	private int			count, size;
	private Node[]		nodes;
	private Edge[][]	edges;
	private Graph		g;
	boolean				found;
	static private long	edgecount;

	public boolean works(Graph grf) {
		//    grf.updateWeighted();
		if (grf.size() < 1)
			return false;
		return (grf.isWeighted());
	}

	public PathContainer getShort(int source, int dest) {
		PathContainer contain = new PathContainer();
		if (source >= size || dest >= size || source < 0 || dest < 0)
			return contain;
		if (source == dest || nodes[source].getId() == -1 || nodes[dest].getId() == -1)
			return contain;
		Path p;
		Node[] n = new Node[3];
		Edge[] e = new Edge[3];
		if (edgecount == 0) {
			return null;
		}
		if (edgecount == 1) {
			Edge solo = new Edge();
			for (int i = 0; i < size; i++)
				for (int j = 0; j < size; j++) {
					if (edges[i][j].getWeight() != -1)
						solo = edges[i][j];
				}
			n[0] = nodes[solo.getSource()];
			n[1] = null;
			n[2] = nodes[solo.getSource()];
			e[0] = solo;
			e[1] = null;
			e[2] = null;
			try {
				p = new Path(n, e, 0);
				PathContainer c = new PathContainer();
				c.addPath(p);
				return c;
			}
			catch (PathException error) {}
			return null;
		}
		PathContainer getcontain = new PathContainer();
		getcontain = dfs(source, dest);
		double[] lengths = new double[getcontain.size()];
		for (int i = 0; i < getcontain.size(); i++) {
			lengths[i] = calcLength(getcontain.getPath(i));
		}
		double max = -1;
		for (int i = 0; i < getcontain.size(); i++) {
			if (lengths[i] < max || max < 0)
				max = lengths[i];
		}
		Double maxD = new Double(max);
		int pid = 0;
		for (int i = 0; i < getcontain.size(); i++) {
			Double len = new Double(lengths[i]);
			if (len.compareTo(maxD) == 0) {
				p = getcontain.getPath(i);
				p.setId(pid++);
				contain.addPath(p);
			}
		}
		return contain;
	}
	
	/**
	 * Returns all the shortests paths of equal length, who ever gets
	 * this need to choose which one to use.
	 */
	public PathContainer doAlgorithm(Graph grf, int source, int dest) {
		PathContainer contain = new PathContainer();
		g = grf;
		size = g.getHeapSize();
		nodes = g.getHeap();
		edges = g.getEdgesMatrix();
		edgecount = g.getEdgeCount();
		Path pathy;
		if (count == 1) {
			try {
				pathy = new Path(g.getNodes(), new Edge[1], 0);
				contain.addPath(pathy);
				return contain;
			}
			catch (PathException junk) {}
		}
		contain = getShort(source, dest);
		for (int i = 0; i < size; i++)
			nodes[i].cleanUp();
		return (contain);
	}

	public String getMenuName() {
		return "Shortest Path";
	}

	protected PathContainer startAlgorithm(Graph theGraph) {
		int begin;
		int end;
		Edge edge = showSourceDestDialog(theGraph);
		if (edge != null) {
			begin = edge.getSource();
			end = edge.getDest();
			PathContainer p = doAlgorithm(theGraph, begin, end);
			if (p == null || p.size() < 1) {
				this.showWarningDialog("Shortest Path found no solution on this graph");
				return null;
			}
			return p;
		}
		else {
			this.showWarningDialog("You entered bad data or the graph was unable to alllocate more memory");
			return new PathContainer();
		}
	}

	private double calcLength(Path p) {
		double len = 0;
		for (int i = 0; i < p.getEdgePath().length; i++) {
			if (p.getEdgePath()[i] != null) {
				len += p.getEdgePath()[i].getWeight();
			}
		}
		return len;
	}

    /**
     * Used to calculate the shortest path between a start and end node.
     * 
     * @param start The start node.
     * @param end The end node.
     * @return The PathContainer which represents the shortest path.
     */
	public PathContainer dfs(int start, int end) {
		PathContainer c = new PathContainer();
		if (nodes[start].getId() == -1)
			return c;
		if (nodes[end].getId() == -1)
			return c;
		for (int i = 0; i < size; i++) {
			nodes[i].cleanUp();
		}
		int[] work = new int[size];
		int place = 0;
		work[place] = start;
		place++;
		nodes[start].setVisited(Node.TRUE);
		int i = -1;
		while (place > 0) {
			for (i++; i < size; i++) {
				if (nodes[i].getId() == -1)
					continue;
				if (g.isEdge(work[place - 1], i)) {
					if (nodes[i].getVisited() == Node.FALSE) {
						work[place++] = i;
						nodes[i].setVisited(Node.TRUE);
						if (i == end) {
							ripPath(c, work, place);
							nodes[i].cleanUp();
							i = work[--place];
						}
						else {
							i = -1;
						}
					}
				}
			}
			i = work[--place];
			nodes[work[place]].cleanUp();
		}
		for (int j = 0; j < size; j++) {
			nodes[j].cleanUp();
		}
		return c;
	}

	private void ripPath(PathContainer c, int[] path, int len) {
		Node[] n = new Node[len * 2];
		Edge[] e = new Edge[len * 2];
		int place = 2;
		n[0] = nodes[path[0]];
		e[0] = e[1] = null;
		n[1] = null;
		for (int i = 1; i < len; i++) {
			n[place] = nodes[path[i]];
			e[place] = edges[path[i - 1]][path[i]];
			place++;
			n[place] = null;
			e[place] = null;
			place++;
		}
		Path p = null;
		try {
			p = new Path(n, e, c.size() + 1);
		}
		catch (PathException pexc) {}
		c.addPath(p);
	}
}
