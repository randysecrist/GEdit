package edu.usu.cs.gui;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EventObject;
import java.util.List;
import java.util.Vector;

/**
 * Logger, which appends log statments to window in near real time.
 * @author Randy Secrist
 */
public class Log extends PrintStream {
    public static final int TO_FILE = 1;
    public static final int TO_LOG_WINDOW = 2;
    private static String filename = "graph";
    private static List<String> theLog = Collections.synchronizedList(new ArrayList<String>());
    private static File tempFile;

    static {
        try {
            tempFile = File.createTempFile(filename, ".log");
            tempFile.deleteOnExit();
            filename = tempFile.getPath();
        }
        catch (IOException ignored) { ; }
    }

    public static Object[] getLog() {
        return theLog.toArray();
    }

    /**
     * A synchronized list of listeners.
     */
    private Vector<LogChangedListener> listeners = new Vector<>(); // list of Listeners

    /**
     * Constructs a new Log.
     */
    public Log() {
        super(new PrintStream(new ByteArrayOutputStream()));
    }

    public synchronized void addLogChangedListener(LogChangedListener lcl) {
        @SuppressWarnings("unchecked") Vector<LogChangedListener> v = (Vector) listeners.clone();
        v.addElement(lcl);
        listeners = v;
    }

    /**
     * Clears the current memory log.
     */
    public void clear() {
        theLog.clear();
        notifyLogChanged();
    }

    /**
     * Notifies all log listeners that the log has changed.
     */
    protected void notifyLogChanged() {
        Vector<LogChangedListener> l;
        EventObject e = new EventObject(this);

        /*
        Once a reference to the set of EventListeners is acquired
        its contents will never change because add and remove modify
        a clone of the EventListeners.

        This ensures that any changes made to the Vector
        from a target listener's method, during the delivery of this
        event will not take effect until after the event is delivered
        */

        l = listeners; // Atomic assignment
        for (int i = 0; i < l.size(); i++) // deliver it!
         l.elementAt(i).logChanged(e);
    }

    public synchronized void removeLogChangedListener(LogChangedListener lcl) {
        @SuppressWarnings("unchecked") Vector<LogChangedListener> v = (Vector) listeners.clone();
        v.removeElement(lcl);
        listeners = v;
    }

    /**
     * Writes a byte array to the log.
     * @param bytes The byte array to write.
     */
    public void write(byte[] bytes) {
        this.write(bytes, 0, bytes.length);
    }
    
    public void write(byte[] buf, int off, int len) {
        super.write(buf, off, len);
        int destination = Integer.MAX_VALUE;
        try {
            this.write(destination, buf, off, len);
        }
        catch (Exception e) { ; }
    }
    
    public void flush() {
        super.flush();
    }
    
    public static void setFileName(String filename) {
        Log.filename = filename;
    }
    
    /**
     * Writes an integer to the log.
     * @param b The boolean to write.
     */
    public void write(boolean b) {
        int destination = Integer.MAX_VALUE;
        try {
            write(destination, Boolean.toString(b));
        }
        catch (Exception e) { ; }
    }

    /**
     * Writes an integer to the log.
     * @param i The integer to write.
     */
    public void write(int i) {
        int destination = Integer.MAX_VALUE;
        try {
            write(destination, Integer.toString(i));
        }
        catch (Exception e) { ; }
    }

    /**
     * Lets the caller determine where a log statement gets output to.
     * @param destination The destination to write to.
     * @param bytes The bytes to write.
     * @param off The offset in the bytes to begin writing.
     * @param len The amount of bytes to write.
     */
    private void write(int destination, byte[] bytes, int off, int len) throws IOException {
        try {
            if((destination & TO_FILE) > 0) {
                // Do TO_FILE
                RandomAccessFile file = new RandomAccessFile(filename, "rw");
                long length = file.length();
                file.seek(length);
                file.write(bytes, off, len);
                file.close();
            }
            if((destination & TO_LOG_WINDOW) > 0) {
                // Do TO_LOG_WINDOW
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bos.write(bytes, off, len);
                theLog.add(new String(bos.toByteArray()));
                bos.close();
            }
            this.notifyLogChanged();
        }
        catch (FileNotFoundException e) {
            throw new IOException("write::Log - FileNotFoundException - " + e.getLocalizedMessage());
        }
    }

    /**
     * Lets the caller determine where a log statement gets output to.
     * @param destination The destination to write to.
     * @param str The string to write.
     */
    private void write(int destination, String str) throws IOException {
        try {
            if((destination & TO_FILE) > 0) {
                // Do TO_FILE
                RandomAccessFile file = new RandomAccessFile(filename, "rw");
                long length = file.length();
                file.seek(length);
                str += "\n";
                file.write(str.getBytes());
                file.close();
            }
            if((destination & TO_LOG_WINDOW) > 0) {
                // Do TO_LOG_WINDOW
                theLog.add(str);
            }
            this.notifyLogChanged();
        }
        catch (FileNotFoundException e) {
            throw new IOException("write::Log - FileNotFoundException - " + e.getLocalizedMessage());
        }
    }

    /**
     * Writes an integer to the log.
     * @param l The long to write.
     */
    public void write(long l) {
        int destination = Integer.MAX_VALUE;
        try {
            write(destination, Long.toString(l));
        }
        catch (Exception e) { ; }
    }

    /**
     * Writes a line to the log.
     * @param str The string to write.
     */
    public void write(String str) {
        int destination = Integer.MAX_VALUE;
        try {
            write(destination, str);
        }
        catch (Exception e) { ; }
    }
}