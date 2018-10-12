package edu.usu.cs.gui;

/**
 * Listens for hyperlink clicks within an JEditorPane
 * Creation date: (3/23/2002 6:56:12 PM)
 * @author: Randy Secrist
 * @version 1.2
 */

import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;

public class HyperLinkActivator implements javax.swing.event.HyperlinkListener {
/**
 * Called when a hypertext link is updated.
 *
 * @param e the event responsible for the update
 */
public void hyperlinkUpdate(javax.swing.event.HyperlinkEvent e) {
    if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
        JEditorPane pane = (JEditorPane) e.getSource();
        if (e instanceof HTMLFrameHyperlinkEvent) {
            HTMLFrameHyperlinkEvent  evt = (HTMLFrameHyperlinkEvent)e;
            HTMLDocument doc = (HTMLDocument)pane.getDocument();
            doc.processHTMLFrameHyperlinkEvent(evt);
        }
        else {
            try {
                pane.setPage(e.getURL());
            }
            catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }
}
}
