package edu.usu.cs.gui;

import java.awt.Color;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.WindowConstants;
import javax.swing.event.InternalFrameListener;

/**
 * Used within a parent frame.
 * Creation date (3/17/2002 10:20:02 AM)
 * @author Randy Secrist
 */
public class IslandWindow extends JInternalFrame {
	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = 7725113845369998827L;
	
	private int ISLAND_ID;
	
	/**
	 * Finalizes this window for destruction.
	 */
	protected void finalize() throws Throwable {
		super.finalize();
	}

	/**
	 * This constructor does not specify a window listener, so
	 * by default the closing option is disabled.
	 */ 
	public IslandWindow(int id, String title, JDesktopPane p) {
		super(title, true, false, true, true);
		this.ISLAND_ID = id;

		// Default Window Properties
		this.setBackground(Color.white);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}

	/**
	 * This constructor specifies a window listener to allow
	 * the calling frame to determine how to process window events.
	 */
	public IslandWindow(int id, String title, JDesktopPane p, InternalFrameListener l) {
		super(title, true, true, true, true);
		this.ISLAND_ID = id;
		this.addInternalFrameListener(l);

		// Default Window Properties
		this.setBackground(Color.white);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}

	/**
	 * Returns the island id of this window.
	 * @return The assigned id of this island window.
	 */
	public int getId() {
		return ISLAND_ID;
	}
}