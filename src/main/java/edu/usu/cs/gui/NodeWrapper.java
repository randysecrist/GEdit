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

import java.awt.geom.Rectangle2D;
import edu.usu.cs.graph.Node;

class NodeWrapper {
	double x;
	double y;

	double dx;
	double dy;

	boolean fixed;
	Node node;

	Rectangle2D borderRect;
	Rectangle2D fillRect;
	int islandId;
	boolean isPressed;
	boolean isTraversed;

	/**
	 * Constructs a new NodeWrapper
	 */
	public NodeWrapper(Node n) {
		this.node = n;
	}

	/**
	 * Return's this wrappers internal node.
	 */
	public Node getNode() {
		return node;
	}

	/**
	 * Stores a new node in this wrapper class.
	 *
	 * @param aNode the node to wrap into this class.
	 * @see edu.usu.cs.graph.Node
	 */
	public void setNode(Node aNode) {
		this.node = aNode;
	}

	/**
	 * Return's the border area of this node.
	 */
	public Rectangle2D getBorderRect() {
		return borderRect;
	}

	/**
	 * Return's the fill area (background) of this node.
	 */
	public Rectangle2D getFillRect() {
		return fillRect;
	}

	/**
	 * Sets the paintable border size of this node.
	 */ 
	public void setBorderRect(Rectangle2D r) {
		this.borderRect = r;
	}

	/**
	 * Sets the fillable rectangle size of this node.
	 */ 
	public void setFillRect(Rectangle2D r) {
		this.fillRect = r;
	}
}