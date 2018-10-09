package edu.usu.cs.algorithms;

import edu.usu.cs.graph.Edge;
import edu.usu.cs.graph.Graph;
import edu.usu.cs.graph.Node;
import edu.usu.cs.graph.Path;
import edu.usu.cs.graph.PathContainer;

import java.awt.Toolkit;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * Abstract Algorithm Class
 * Creation date: (4/10/2002 10:39:24 PM)
 * @author Randy Secrist
 */
public abstract class Algorithm {
	private JFrame parent;
	protected Visitor visitor;

	/**
	 * This method runs the algorithm, and produces a PathContainer
	 * which is what the GEdit GUI will expect.
	 * Should be overridden by each subclass.
	 * 
	 * @param theGraph A graph ADT object.
	 * @return A path which lists the results of this algorithm.
	 */
	public PathContainer doAlgorithm(Graph theGraph) {
		return new PathContainer();
	}
	
	/**
	 * This method runs the algorithm, and produces a PathContainer
	 * which is what the GEdit GUI will expect.
	 * Should be overridden by each subclass.
	 * 
	 * @param theGraph A graph ADT object.
	 * @param begin The entry point in the graph to begin with.
	 * @return A path which lists the results of this algorithm.
	 */
	public PathContainer doAlgorithm(Graph theGraph, int begin) {
		return doAlgorithm(theGraph);
	}
	
	/**
	 * This method runs the algorithm, and produces a PathContainer
	 * which is what the GEdit GUI will expect.
	 * Should be overridden by each subclass.
	 * 
	 * @param theGraph A graph ADT object.
	 * @param begin The entry point in the graph to begin with.
	 * @param end The ending point in the graph to end with.
	 * @return A path which lists the results of this algorithm.
	 */
	public PathContainer doAlgorithm(Graph theGraph, int begin, int end) {
		return doAlgorithm(theGraph, begin);
	}

    /**
     * Returns the first path in the PathContainer if it exists,
     * otherwise returns the null.
     *
     * @param pc The path container to search for a valid path.
     * @return The first path.
     */
    public static Path getFirstPath(PathContainer pc) {
        if ((pc == null) || pc.isEmpty()) {
            return null;
        }

        Path[] paths = pc.getPaths();
        if (paths == null) {
            return null;
        }

        for (int i = 0; i < paths.length; i++) {
            if (paths[i] == null) {
                continue;
            }
            if ((paths[i].getNodePath() != null) && (paths[i].getNodePath().length > 0)) {
                return paths[i];
            }
            else if ((paths[i].getEdgePath() != null) && (paths[i].getEdgePath().length > 0)) {
                return paths[i];
            }
            else {
                continue;
            }
        }

        return null;
    }

	/**
	 * Returns the display name this algorithm should display in a menu.
	 * @return A string which represents the display name.
	 */	
	public abstract String getMenuName();
	
	/**
	 * Init's an executes the algorithm by calling startAlgorithm.
	 * @param theGraph The graph to use to run with.
	 * @return A path which lists the results of this algorithm.
	 */
	public PathContainer runMe(Graph theGraph) {
		theGraph.updateWeighted();
		theGraph.updateDirected();

		return startAlgorithm(theGraph);
	}
	
	/**
	 * Sets the parent of this algorithm, which is the GUI
	 * GEdit runs in.
	 * @param f A swing based frame.
	 */
	public void setParent(JFrame f) {
		this.parent = f;
	}
	
	/**
	 * Sets the visitor of this algorithm, which may implements
	 * a specific action as the algorithm visits each node / edge.
	 * @param v The new visitor.
	 */
	public void setVisitor(Visitor v) {
		this.visitor = v;
	}
	
	/**
	 * Brings up a standardized JOptionPane which prompts a user
	 * for a source node and a destination node, using a given graph
	 * for input.
	 * 
	 * @param theGraph The input graph to use to prompt the user.
	 * @return An edge which represents the source and destination.
	 */
	protected Edge showSourceDestDialog(Graph theGraph) {
 		Node[] nodes = theGraph.getNodes();

 		// Messages
 		Object[] message = new Object[4]; 
 		message[0] = "Source Node:";
 		
 		JComboBox srcNodes = new JComboBox();
 		srcNodes.getAccessibleContext().setAccessibleName("node.source");
 		for (int i = 0; i < nodes.length; i++) {
	 		srcNodes.addItem(nodes[i]);
 		}
 		message[1] = srcNodes;
 		
 		message[2] = "Destination Node:";
  		JComboBox destNodes = new JComboBox();
 		destNodes.getAccessibleContext().setAccessibleName("node.destination");
 		for (int i = 0; i < nodes.length; i++) {
	 		destNodes.addItem(nodes[i]);
 		}
 		message[3] = destNodes;
  
 		// Options
 		String[] options = { 
	 		"Ok", 
	 		"Cancel"
	 	};
 		int result = JOptionPane.showOptionDialog( 
	 		parent,                                     // the parent that the dialog blocks 
	 		message,                                    // the dialog message array 
	 		"Source & Destination Query:",              // the title of the dialog window 
	 		JOptionPane.DEFAULT_OPTION,                 // option type 
 		    JOptionPane.QUESTION_MESSAGE,               // message type 
 		    null,                                       // optional icon, use null to use the default icon 
 		    options,                                    // options string array, will be made into buttons 
 		    options[0]                                  // option that should be made into a default button 
 		);

 		// Validate and Add
 		switch(result) { 
 		   case 0: // yes
 		     Node n1 = (Node) srcNodes.getSelectedItem();
 		     Node n2 = (Node) destNodes.getSelectedItem();
 		     return new Edge(n1.getId(), n2.getId(), 0);
 		   case 1: // cancel
 		     break;
 		   default: 
 		     break; 
 		}
 		return null;
	}
	
	/**
	 * Shows a standardized JOptionPane which prompts the user
	 * for just a source node.
	 * 
	 * @param theGraph The input graph to use to list the nodes.
	 * @return The selected Node.
	 */
	protected Node showSourceDialog(Graph theGraph) {
 		Node[] nodes = theGraph.getNodes();

 		// Messages
 		Object[] message = new Object[2]; 
 		message[0] = "Source Node:";
 		
 		JComboBox srcNodes = new JComboBox();
 		srcNodes.getAccessibleContext().setAccessibleName("node.source");
 		for (int i = 0; i < nodes.length; i++) {
	 		srcNodes.addItem(nodes[i]);
 		}
 		message[1] = srcNodes;
 		
  
 		// Options
 		String[] options = { "Ok", "Cancel" };
 		int result = JOptionPane.showOptionDialog( 
	 		parent,                                       // the parent that the dialog blocks 
	 		message,                                    // the dialog message array 
	 		"Source Query:",                            // the title of the dialog window 
	 		JOptionPane.DEFAULT_OPTION,                 // option type 
 		    JOptionPane.QUESTION_MESSAGE,               // message type 
 		    null,                                       // optional icon, use null to use the default icon 
 		    options,                                    // options string array, will be made into buttons 
 		    options[0]                                  // option that should be made into a default button 
 		);

 		// Validate and Add
 		switch(result) { 
 		   case 0: // yes
 		     Node n1 = (Node) srcNodes.getSelectedItem();
 		     return n1;
 		   case 1: // cancel
 		     break;
 		   default: 
 		     break; 
 		}
 		return null;
	}
	/**
	 * An overridden method which implements the algorithm.
	 * @param theGraph The input graph to use to execute the
	 * algorithm.
	 * 
	 * @return A path which contains the results of the execution.
	 */
	protected abstract PathContainer startAlgorithm(Graph theGraph);
	
	/**
	 * Determines if a the algorithm will execute using the given Graph.
	 * @param theGraph The graph to test.
	 * @return True if the algorithm can execute on this Graph, false otherwise.
	 */
	public abstract boolean works(Graph theGraph);

	/**
	 * Displays a standard warning message which can be used to
	 * alert the user of problems or critical messages.
	 * @param msg Shows the user a warning dialog when appropriate.
	 */
	public void showWarningDialog(String msg) {
		Toolkit.getDefaultToolkit().beep();
		JOptionPane.showMessageDialog(parent, msg, "Algorithm Warning", JOptionPane.OK_OPTION);			
	}
}
