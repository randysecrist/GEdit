package com.reformation.graph.gui;

import java.awt.Component;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import javax.swing.JComponent;
import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;

/**
 * Verifies that the current thread is a Swing thread.
 * 
 * This class can optionally verify that a component is showing
 * before making the check.  This is needed because some of the
 * Swing components construct themselves outside of the event
 * thread before they are realized (show, pack, setVisible).
 * 
 * Created on Apr 11, 2005
 * @author Randy Secrist
 */
public class ThreadAccess extends RepaintManager {
    private int tabCount = 0;
    private boolean checkIsShowing = false;

    public ThreadAccess() {
        super();
    }

    public ThreadAccess(boolean checkIsShowing) {
        super();
        this.checkIsShowing = checkIsShowing;
    }

    public synchronized void addInvalidComponent(JComponent jComponent) {
        checkThread(jComponent);
        super.addInvalidComponent(jComponent);
    }

    private void checkThread(JComponent c) {
        if (!SwingUtilities.isEventDispatchThread() && checkIsShowing(c)) {
            Throwable t = new Exception();
            
            boolean repaint = false;
            boolean fromSwing = false;
            StackTraceElement[] stackTrace = t.getStackTrace();
            for (StackTraceElement st : stackTrace) {
                if (repaint && st.getClassName().startsWith("javax.swing.")) {
                    fromSwing = true;
                }
                if ("repaint".equals(st.getMethodName())) {
                    repaint = true;
                }
            }
            
            if (repaint && !fromSwing) {
                // no problems here, since repaint() is thread safe
                return;
            }
            
            // comment this out if deadlock is observed
            if (repaint) {
                return;
            }
            
            // report potential problems
            // (3/7/2010)
            // - GEdit 2305
            //   - traversal thread writes repaints (which should be safe - no deadlock observed).
            // - OS Color Picker causes deadlock - seems to route through LOG
            //   - partially resovled by changing Log.java (103) to TO_LOG_WINDOW over Integer.MAX
            //   - was hanging up in a RandomAccessFile -
            //   - OS X color picker spawns a thread and writes a repaint (which should be safe).
            // - root cause of both of these was logging here even though deadlock doesn't occur
            // - leaves me wondering w/o more knowledge from SUN as to what is allowed and what is not,
            //   how to detect breaks here.  Seems like we need this wide open.
            //   given that we need this detection wide open, perhaps we should either
            //     - push this to a specific log file, or disconnect sys.err from log
            System.err.println("----------Illegal Thread Access START");
            System.err.println(getStracktraceAsString(t));
            dumpComponentTree(c);
            System.err.println("----------Illegal Thread Access END");
        }
    }

    private String getStracktraceAsString(Throwable e) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(byteArrayOutputStream);
        e.printStackTrace(printStream);
        printStream.flush();
        return byteArrayOutputStream.toString();
    }

    private boolean checkIsShowing(JComponent c) {
        if (this.checkIsShowing == false)
            return true;
        else
            return c.isShowing();
    }

    public synchronized void addDirtyRegion(JComponent jComponent, int x, int y, int w, int h) {
        checkThread(jComponent);
        super.addDirtyRegion(jComponent, x, y, w, h);
    }

   private void dumpComponentTree(Component c) {
        System.err.println("----------Component Tree");
        resetTabCount();
        for (; c != null; c = c.getParent()) {
            printTabIndent();
            System.err.println(c);
            printTabIndent();
            System.err.println("Showing:" + c.isShowing() + " Visible: " + c.isVisible());
            incrementTabCount();
        }
    }

    private void resetTabCount() {
        this.tabCount = 0;
    }

    private void incrementTabCount() {
        this.tabCount++;
    }

    private void printTabIndent() {
        for (int i = 0; i < this.tabCount; i++)
            System.err.print("\t");
    }	

}