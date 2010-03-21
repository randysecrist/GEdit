/*
 * @(#)Graph.java	1.9 99/08/04
 *
 * Copyright (c) 1997, 1998 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Sun grants you ("Licensee") a non-exclusive, royalty free, license to use,
 * modify and redistribute this software in source and binary code form,
 * provided that i) this copyright notice and license appear on all copies of
 * the software; and ii) Licensee does not utilize the software in a manner
 * which is disparaging to Sun.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
 * OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS
 * LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF
 * OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 *
 * This software is not designed or intended for use in on-line control of
 * aircraft, air traffic, aircraft navigation or aircraft communications; or in
 * the design, construction, operation or maintenance of any nuclear
 * facility. Licensee represents and warrants that it will not use or
 * redistribute the Software for such purposes.
 */
package edu.usu.cs.gui;

import java.awt.Color;

import edu.usu.cs.graph.Edge;

/**
 * Simple wrapper which serves to act as a interface for edges used
 * in a graph class, and edges drawn to a graphical display.
 * 
 * @author Randy Secrist
 */
class EdgeWrapper {
	int from;
	int to;

	double len;
	Edge edge;

	Color edgeColor = Color.black;
	int islandId;
	boolean isTraversed;

	/**
	 * EdgeWrapper constructor:
	 * This constructor wraps inside itself an edu.usu.cs.graph.Edge class.
	 *
	 * @param e the edge to wrap into this class.
	 * @see edu.usu.cs.graph.Edge
	 */
	public EdgeWrapper(Edge e) {
		this.edge = e;
	}

	/**
	 * Return's this wrappers internal edge.
	 */
	public Edge getEdge() {
		return edge;
	}

	/**
	 * Stores a new edge in this wrapper class.
	 *
	 * @param e the edge to wrap into this class.
	 * @see edu.usu.cs.graph.Edge
	 */
	public void setEdge(Edge aEdge) {
		this.edge = aEdge;
	}
}