package edu.usu.cs.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * Listens & Dispatches Menu Bar Events.
 *
 * Events are dispatched by calling a method
 * from the calling Frame using Reflection. 
 *
 * Creation date: (3/7/2002 6:32:41 PM)
 * @author Randy Secrist
 */
public class MenuListener implements ActionListener {
	private MBI mbi = null;
	
	/**
	 * Precondition:
	 *  All handlers for ActionEvent e are in the GEdit class.
	 *  Handlers can either be void functions with no parameters, or
	 *  they can be void functions with 1 parameter - an ActionEvent.
	 *
	 *  <p>In order to differentiate between the two, the MBI class must
	 *  register functions that require passing the ActionEvent in the
	 *  MBI variable eventSet.  This class passes any handlers in that
	 *  set to the appropriate 1 parameter handler via reflection.
	 *	
	 * <p>Postcondition:
	 *	This method only performs actions on events comming from the
	 *	menu bar.
	 */
	public void actionPerformed (ActionEvent e) {
		String theAction = e.getActionCommand();

		GEdit gEdit = GEdit.getInstance();

		// GEdit can not be null and still have a menu bar.
		if (gEdit == null) return;

		TreeSet<String> ts = mbi.getEventSet();
		Iterator<String> i = ts.iterator();

		boolean needsTheAction = false;
		while (i.hasNext()) {
			Object obj = i.next();
			if (obj instanceof String) {
				String menuItemName = (String) obj;				
				if (theAction.equalsIgnoreCase(menuItemName))
					needsTheAction = true;
			}
		}

		String handler = mbi.getFunctionName(theAction);
		
		// Process event - (lookup and call event handler)
		// (This uses reflection to lookup the handler function and call it.)
		if (handler != null) {
			
			// Call function passing it the ActionEvent.
			if (needsTheAction) {
				// Handler Requires Action Event Parameter.
				this.passActionEvent(e, handler, gEdit);
				return;
			}
			
			// Call function not passing any parameters.
			try {
				Class<?> aClass = gEdit.getClass();
				Method method = aClass.getMethod(handler, new Class[0]);
				method.invoke(gEdit, new Object[0]);
			}
			catch (java.lang.Throwable ex) {
				System.out.println("actionPerformed::MenuListener - Invalid Action Attempted: " + theAction + " - " + handler);
				ex.printStackTrace();
			}
		}
		else {
			gEdit.getLog().write("actionPerformed::MenuListener - Invalid Action Attempted: " + theAction);
		}
	}

	/**
	 * Constructs a new menu lister which listens for events from the menu bar.
	 */
	public MenuListener(MBI mbi) {
		super();
		this.mbi = mbi;
	}

	/**
	 * Called to pass handlers and a ActionEvent to the
	 * appropriate function in GEdit - via reflection.
	 */
	public void passActionEvent(ActionEvent e, String handler, GEdit gEdit) {
		try {
			Class<?>[] params = { e.getClass() };
			Object[] args = { e } ;
			Class<?> aClass = gEdit.getClass();
			Method method = aClass.getMethod(handler, params);
			method.invoke(gEdit, args);
		}
		catch (java.lang.Throwable ex) {
			System.out.println("passActionEvent::MenuListener - Invalid Action Attempted: " + e.getActionCommand() + " - " + handler);
			ex.printStackTrace();
		}
	}
}