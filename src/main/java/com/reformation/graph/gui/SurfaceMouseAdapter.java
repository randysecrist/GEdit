package com.reformation.graph.gui;
 
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.AbstractAction;
import javax.swing.JPopupMenu;

/**
 * Handles mouse clicks within an islands surface.
 * Creation date: (3/24/2002 4:34:41 PM)
 * @author Randy Secrist
 * @version 1.2
 */
public class SurfaceMouseAdapter extends MouseAdapter implements MouseMotionListener {
    private GraphPanel.Surface surf;
    private PopupMenu popup = new PopupMenu();
    class PopupMenu extends JPopupMenu {
        /**
         * Serial Version UID
         */
        private static final long serialVersionUID = 2624663024670666122L;

        AddNodeAction addNodeAction;
        EditNodeDataAction editNodeDataAction;
        NewGraphAction newGraphAction;
        PopupMenu() {
            /*
            this.addMouseListener(new MouseAdapter() {
                private boolean hasExited = false;
                public void mouseExited(MouseEvent event) {
                    if (hasExited) hasExited = false;
                    else {
                        hasExited = true;
                        setVisible(false);
                    }
                }
            });
            */
            addNodeAction = new AddNodeAction();
            editNodeDataAction = new EditNodeDataAction();
            newGraphAction = new NewGraphAction();

            // add options to popup menu
            add(addNodeAction);
            add(newGraphAction);
            this.addSeparator();
            add(editNodeDataAction);
        }
    }
    class AddNodeAction extends AbstractAction {
        /**
         * Serial Version UID
         */
        private static final long serialVersionUID = -6139111827860305310L;

        AddNodeAction() {
            super("Add Node");
        }

        public void actionPerformed(ActionEvent event) {
            // if (GEdit.getInstance().getTraversalStatus()) return;
            GEdit.getInstance().doAddNode();
        }
    }
    class EditNodeDataAction extends AbstractAction {
        /**
         * Serial Version UID
         */
        private static final long serialVersionUID = -9196958544841923397L;

        EditNodeDataAction() {
            super("Edit Node Data");
        }

        public void actionPerformed(ActionEvent event) {
            NodeWrapper nw = Globals.getInstance().getSelectedNode();
            if (nw == null) return;

            GEdit.getInstance().modifyNodeData(nw.getNode());
        }
    }
    class NewGraphAction extends AbstractAction {
        /**
         * Serial Version UID
         */
        private static final long serialVersionUID = 4998495925741066424L;

        NewGraphAction() {
            super("Save Island As Graph");
        }

        public void actionPerformed(ActionEvent event) {
            GEdit.getInstance().doSaveIslandAsGraph(surf.islandId);
        }
    }
    /**
     * SurfaceMouseAdapter constructor comment.
     */
    public SurfaceMouseAdapter(GraphPanel.Surface surf) {
        super();
        this.surf = surf;
    }
    private boolean isNodeSelected(NodeWrapper n) {
        return (n == Globals.getInstance().getSelectedNode());
    }
    private NodeWrapper selectNodeWithClick(int x, int y) {
        for (int i = 0; i < Globals.getInstance().getNumNodes(); i++) {
            NodeWrapper n = Globals.getInstance().getNode(i);
            if (n.islandId != surf.islandId) continue;
            if (n.getBorderRect().contains(x, y)) {
                return n;
            }
        }
        return null;
    }
    /**
     * Selects a Node when the mouse is clicked.
     * (A mouse click occurs after a press and a release).
     *
     * <p>This may appear similar to mousePressed, but it is not.
     * This method does not simulate drag and drop, hence no
     * instance of a MouseMotionListener needs to be created here.
     *
     * <p>Selected nodes can cross surfaces - so they their state
     * had to be stored external to this class since each surface
     * get's it's own mouse listener.  The Globals class is used
     * to store which node get's selected.
     *
     * <p>This method used bestdist to determine how far away
     * the click must be from a node to select that node.
     *
     * <p>If bestdist is set to MAX - this algorithm will select the
     * closest node to the x,y coordinate of the click.
     */
    public void mouseClicked(MouseEvent e) {
        if (MouseEvent.BUTTON1_MASK == e.getModifiers()) {
            // If there is not a node selected - select it.
            if (Globals.getInstance().getSelectedNode() == null) {
                // Mark Selected
                int x = e.getX();
                int y = e.getY();
                NodeWrapper theSelected = selectNodeWithClick(x, y);
                if (theSelected != null) {
                    theSelected.fixed = true;
                    Globals.getInstance().setSelectedNode(theSelected);
                }
                surf.repaint();
                e.consume();
            }
            else { // We have a selected node, so add an edge.
                int x = e.getX();
                int y = e.getY();
                NodeWrapper destNode = this.selectNodeWithClick(x, y);
                if (destNode != null) {
                    // Is this really the destNode?  (compare w/ selected)
                    if (isNodeSelected(destNode)) {
                        // Deselect surf.theSelected.
                        Globals.getInstance().getSelectedNode().fixed = false;
                        Globals.getInstance().setSelectedNode(null);
                    }
                    else { // Prompt for edge creation.
                        if (GEdit.getInstance().getTraversalStatus()) return;
                        GEdit.getInstance().doAddEdge(Globals.getInstance().getSelectedNode(), destNode);
                    }
                }
                surf.repaint();
                e.consume();
            }
        }
        else if (MouseEvent.BUTTON3_MASK  == e.getModifiers()) {
            // Specify when right click options are enabled and disabled:
            popup.addNodeAction.setEnabled(true);
            popup.newGraphAction.setEnabled(true);
            popup.editNodeDataAction.setEnabled(false);

            // check for selected, and enable edit node option
            int x = e.getX();
            int y = e.getY();
            NodeWrapper theSelected = this.selectNodeWithClick(x, y);
            if (theSelected != null) {
                theSelected.fixed = true;
                Globals.getInstance().setSelectedNode(theSelected);
                popup.editNodeDataAction.setEnabled(true);
            }
            surf.repaint();
            e.consume();
            /* // Uncomment to disable or enable only if a node is selected.
            if (Globals.getInstance().getSelectedNode() == null) {
                popup.editNodeDataAction.setEnabled(false);
            }
            else {
                popup.editNodeDataAction.setEnabled(true);
            }
            */
            popup.show(surf, e.getX(), e.getY());
        }
    }
    /**
     * Finds the nearest node to a mouse press, and init's
     * a dragging listener allow a painted object to
     * simulate drag and drop.
     *
     * <p>If bestdist is set to MAX - this algorithm will select the
     * closest node to the x,y coordinate of the click.
     */
    public void mousePressed(MouseEvent e) {
        if (MouseEvent.BUTTON1_MASK == e.getModifiers()) {
            int x = e.getX();
            int y = e.getY();
            NodeWrapper theSelected = this.selectNodeWithClick(x, y);
            if (theSelected != null) {
                surf.addMouseMotionListener(this);
                surf.pick = theSelected;
                surf.pick.isPressed = true;
                surf.pickfixed = surf.pick.fixed;
                surf.pick.fixed = true;
                surf.pick.x = x;
                surf.pick.y = y;
            }
            surf.repaint();
            e.consume();
        }
    }
    /**
     * Invoked when a mouse button has been released on a component.
     */
    public void mouseReleased(MouseEvent e) {
        //log.write("mouseReleased::SurfaceMouseAdapter");
        if (surf.pick != null) {
            Rectangle surfArea = new Rectangle(surf.getSize());
            Point p = new Point(e.getX(), e.getY());

            if (surfArea.contains(p)) {
                // Point lies inside surface area.
                surf.removeMouseMotionListener(this);
                surf.pick.x = p.getX();
                surf.pick.y = p.getY();
                surf.pick.fixed = surf.pickfixed;
                surf.pick.isPressed = false;
                surf.pick = null;
                surf.repaint();
            }
            else {
                // Point lies outside surface area:
                surf.removeMouseMotionListener(this);
                surf.pick.x = surfArea.getWidth() / 2;
                surf.pick.y = surfArea.getHeight() / 2;
                surf.pick.fixed = surf.pickfixed;
                surf.pick.isPressed = false;
                surf.pick = null;
                surf.repaint();
            }
        }
        e.consume();
    }
    /**
     * Invoked when a mouse button is pressed on a component and then
     * dragged.  Mouse drag events will continue to be delivered to
     * the component where the first originated until the mouse button is
     * released (regardless of whether the mouse position is within the
     * bounds of the component).
     */
    public void mouseDragged(MouseEvent e) {
        if (surf.pick == null) return;
        surf.pick.x = e.getX();
        surf.pick.y = e.getY();
        surf.repaint();
        e.consume();
    }
    /**
     * Invoked by MouseListener interface when the mouse has entered
     * a component.
     */
    public void mouseEntered(MouseEvent e) {}
    /**
     * Invoked by MouseListener interface when the mouse has exited
     * a component.
     */
    public void mouseExited(MouseEvent e) {}
    /**
     * Invoked when the mouse button has been moved on a component
     * (with no buttons no down).
     */
    public void mouseMoved(MouseEvent e) {}
}
