package edu.usu.cs.graph;

/**
 * Represents a set of Paths obtained from a graph algorithm. 
 * Creation date: (4/4/2002 3:36:15 PM)
 * 
 * @author Randy Secrist
 */
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class PathContainer {

    /**
     * A synchronized list of paths.
     */
    private List<Path> theList = Collections.synchronizedList(new ArrayList<Path>());

    /**
     * PathContainer constructor comment.
     */
    public PathContainer() {
        super();
    }

    /**
     * Adds a path to the path container.
     * @param p The path to add.
     */
    public void addPath(Path p) {
        theList.add(p);
    }

    /**
     * Deletes any paths in this container.
     */
    public void clear() {
        theList.clear();
    }

    /**
     * Determines if a path already exists in the container.
     * @param p The path to verify.
     * @return Returns true if the path is in the container.  False otherwise.
     */
    public boolean contains(Path p) {
        return theList.contains(p);
    }

    /**
     * Gets the path at index i.
     * @param i The path index to retreive.
     * @return The path at index i.
     */
    public Path getPath(int i) {
        return (Path) theList.get(i);
    }

    /**
     * Returns all possible paths in this container.
     * @return An array of Paths.
     */
    public Path[] getPaths() {
        Path[] p = new Path[theList.size()];
        System.arraycopy(theList.toArray(), 0, p, 0, theList.size());
        return p;
    }

    /**
     * Returns the index a Path can be found at.
     * @param p The path to lookup.
     * @return An integer which represents the index of a path.
     */
    public int indexOf(Path p) {
        return theList.indexOf(p);
    }

    /**
     * Determines if this container is empty.
     * @return True if empty, false otherwise.
     */
    public boolean isEmpty() {
        return theList.isEmpty();
    }

    /**
     * Removes a path from the container.
     * @param p The path to remove.
     */
    public void removePath(Path p) {
        theList.remove(p);
    }

    /**
     * Returns the number of paths in this container.
     * @return The number of paths in the container.
     */
    public int size() {
        return theList.size();
    }
}