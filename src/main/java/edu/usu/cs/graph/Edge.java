package edu.usu.cs.graph;

/**
 * Edge class to hold node id's and edge weights, it is independent of the
 * nodes
 * 
 * @author Seth Humphries
 */
public class Edge implements java.io.Serializable {

	// Serial Version Id
	static final long serialVersionUID = -711906394888805294L;
	private int fromID;
	private int toID;
	private double weight;

	public Edge() {
		super();
		this.fromID = -1;
		this.toID = -1;
		this.weight = -1;
	}

	/**
	 * ACCEPTS source, destination and weights for an edge error checking for
	 * the ints is done in the graph function addedge
	 * 
	 * @param source The id of the source node.
	 * @param target The id of the target node.
	 * @param weight The weight of the edge, should be >= 0.
	 */
	public Edge(int source, int target, double weight) {
		this.toID = target;
		this.fromID = source;
		this.weight = weight;
	}

	/**
	 * Returns the destination node this edge points to.
	 * @return The ID of the connected node.
	 */
	public int getDest() {
		return (toID);
	}

	/**
	 * Returns the source node this edge protrudes from.
	 * @return The ID of the connected node.
	 */
	public int getSource() {
		return (fromID);
	}

	/**
	 * Returns the weight of this edge.
	 * @return A double percision floating point value.
	 */
	public double getWeight() {
		return (weight);
	}

	/**
	 * Sets the destination node of this edge.
	 * @param dest The ID of the destination node.
	 */
	synchronized void setDest(int dest) {
		this.toID = dest;
	}

	/**
	 * Sets the source node of this edge. 
	 * @param src The ID of the source node.
	 */
	synchronized void setSource(int src) {
		this.fromID = src;
	}

	/**
	 * Sets the weight of this edge.
	 * @param weight Sets a double percision floating point value.
	 */
	synchronized void setWeight(double weight) {
		this.weight = weight;
	}
}