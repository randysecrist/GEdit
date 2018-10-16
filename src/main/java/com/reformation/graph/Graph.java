package com.reformation.graph;

import com.reformation.graph.utils.MathUtils;
import com.reformation.graph.utils.Pair;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.UUID;

/**
 * Generic Graph Class, uses the edge class and node class in it's
 * implementation.  This class can store variable data types.
 * (See Data Interface)
 * Creation date: (2/4/2002 5:43:48 PM)
 * 
 * @author Seth Humphries
 * @author Randy Secrist
 */
public class Graph implements Serializable {
    /**
     * The default size of a Graph.
     */
    private static final int INITIAL_SIZE = 10;

    /**
     * Max size this graph ADT may support.
     * by solving for x in this equation and rounding to the nearest integer
     * value, then double checking its
     * validity: 2^31-x^2+x=0, 2^31 is the max int value.
     */
    private static final int MAX_SIZE = 46341; //this value was found

    /**
     * Serial Version UID
     */
    static final long serialVersionUID = -3110725019141300218L;

    /**
     * Used to uniquely identify a graph.
     */
    private String uuid;

    /**
     * Used to nodecount the number of nodes.
     */
    private int nodecount;

    /**
     * Used to determine if the graph is directional.
     */
    private boolean directed;

    /**
     * number of edges in the graph class, max
     * edges=(max nodes)^2 -(max nodes) since there are
     * no self referencing nodes
     */
    private long edgecount;

    /**
     * The heap array, the heap key (heap index) is the id of the node.
     */
    private Node[] heap;

    /**
     * The number of connected components in the graph.
     */
    private int islandcount;

    /**
     * The components in the graph.
     */
    private Node[][] islands;

    /**
     * The adjacency matrix.
     */
    private Edge[][] matrix;

    /**
     * Optional variable used by clients to remember save state.
     */
    private boolean changed;

    /**
     * The size of the adjacency matrix.
     */
    private int size;

    /**
     * Used to determine if the edges contain weights > 0.
     */
    private boolean weighted;

    /**
     * Default Graph constructor
     */
    public Graph() {
        super();
        this.size = INITIAL_SIZE;
        build();
    }

    /**
     * Graph overloaded constructor
     */
    public Graph(int s) {
        super();
        if (s > 0 && s <= MAX_SIZE)
            this.size = s;
        else
            throw new IllegalArgumentException("Size is outside of acceptable range!");
        build();
    }

    public void build() {
        try {
            this.buildHeap();
        }
        catch (GraphException e1) {
            //dump except1.message to client
            return;
        }
        try {
            this.buildMatrix();
        }
        catch (GraphException e5) {
            //dump except5.message to client
            return;
        }
        this.uuid = UUID.randomUUID().toString();
        this.changed = true;
        this.nodecount = 0;
        this.edgecount = 0;
        this.islandcount = 0;
        updateDirected();
        updateWeighted();
    }

    /**
     * Returns the generated UUID of this graph.
     * @return The UUID of this graph.
     */
    public String getId() {
        return this.uuid;
    }

    /**
     * Returns the size (node nodecount) of this graph.  Same as calling {@link #getNodeCount()}
     * @return
     */
    public synchronized int size() {
        this.changed = false;
        return this.nodecount;
    }

    public synchronized int getNodeCount() {
        this.changed = false;
        return this.size();
    }

    public synchronized int hasNodeWithData(Data d)  {
        for (int i = 0; i < this.size; i++) {
            Data temp = this.heap[i].getData();
            if (d.equals(temp)) {
                return i;
            }
        }
        return -1;
    }

    public synchronized Pair<Integer, Integer> addEdge(Data source, Data target) throws GraphException {
        return this.addEdge(source, target, 1.0, (Data[]) null);
    }
    public synchronized Pair<Integer, Integer> addEdge(Data source, Data target, double weight) throws GraphException {
        return this.addEdge(source, target, weight, (Data[]) null);
    }
    public synchronized Pair<Integer, Integer> addEdge(Data source, Data target, Data ... edgeData) throws GraphException {
        return this.addEdge(source, target, 1.0, edgeData);
    }
    public synchronized Pair<Integer, Integer> addEdge(Data source, Data target, double weight, Data ... edgeData) throws GraphException {
        int sourceId = this.hasNodeWithData(source);
        if (sourceId == -1) {
            sourceId = this.addNode(source);
        }
        int targetId = this.hasNodeWithData(target);
        if (targetId == -1) {
            targetId = this.addNode(target);
        }
        return addEdge(new Edge(sourceId, targetId, weight).setData(edgeData));
    }

    /**
     * public graph function...adds an edge to the graph
     */
    public synchronized Pair<Integer, Integer> addEdge(Edge newEdge) throws GraphException {
        this.updateDirected();
        this.updateWeighted();
        if (this.nodecount < 2)
            throw new GraphException("Not enough Nodes to connect");
        else if (this.edgecount >= (this.nodecount * this.nodecount - this.nodecount))
            throw new GraphException("Too many Edges");
        else if (newEdge.getDest() == newEdge.getSource())
            // self referencing node
            throw new GraphException("Self Referencing Node");
        else if (newEdge.getWeight() < 0)
            // illegal weight, nonexistent
            throw new GraphException("Negative Edge Weight");
        else if (newEdge.getSource() < 0 || newEdge.getSource() > this.nodecount)
            // non-existent node
            throw new GraphException("Non-Existent Source Node");
        else if (newEdge.getDest() < 0 || newEdge.getDest() > this.nodecount)
            // non-existent node
            throw new GraphException("Non-Existent Destination Node");
        else if (this.matrix[newEdge.getSource()][newEdge.getDest()].getWeight() != -1)
            // edge already exists
            throw new GraphException("An Edge Already Exists Between the Destination and the Source Nodes");
        else if (!this.weighted && newEdge.getWeight() > 0) {
            this.matrix[newEdge.getSource()][newEdge.getDest()] = newEdge;
            this.edgecount++;
            // Create a duplicate edge if the graph is undirected:
            if (!this.directed) {
                if (this.matrix[newEdge.getDest()][newEdge.getSource()].getWeight() == -1) {
                    this.matrix[newEdge.getDest()][newEdge.getSource()] = newEdge;
                    this.edgecount++;
                }
            }
        }
        else if (!this.directed) {
            //creates a duplicate edge because the graph is undirected
            this.matrix[newEdge.getSource()][newEdge.getDest()] = newEdge;
            this.edgecount++;
            if (this.matrix[newEdge.getDest()][newEdge.getSource()].getWeight() == -1) {
                this.matrix[newEdge.getDest()][newEdge.getSource()] = newEdge;
                this.edgecount++;
            }
        }
        else {
            this.matrix[newEdge.getSource()][newEdge.getDest()] = newEdge;
            this.edgecount++;
        }
        this.changed = false;
        this.updateDirected();
        this.updateWeighted();
        return new Pair<>(newEdge.getSource(), newEdge.getDest());
    }

    /**
     * adds a node to the graph fills the first "hole" in the graph left by
     * deletion, and updates the size of the graph if needed
     *
     * @param newNode The data to store with the node.
     * @return The ID of the new node, or -1 if a new node can not be created.
     * @throws GraphException If an error occurs while creating the node.
     */
    public synchronized int addNode(Data newNode) throws GraphException {
        int newId;
        if (this.nodecount < this.size) {
            Data temp;
            for (int i = 0; i < this.size; i++) {
                temp = this.heap[i].getData();
                if (newNode.equals(temp))
                    throw new GraphException("Duplicate Node");
            }
            for (int i = 0; i < this.size; i++) {
                if (this.heap[i].getId() == -1) {
                    newId = i;
                    this.heap[newId] = new Node(newNode, newId);
                    this.nodecount++;
                    this.changed = false;
                    return newId;
                }
            }
        }
        else {
            //resize heap
            try {
                this.resizeHeap();
            }
            catch (Exception e4) {
                throw new GraphException("Unable to Add Node Because " + e4.getMessage());
            }
            int k = addNode(newNode);
            this.changed = false;
            return k;
        }
        return -1;
    }

    /**
     * Writes a instance to a buffered writer.  See {@link #read(BufferedReader)}
     * @param output The output buffer to write to.
     */
    public void write(BufferedWriter output) {
        PrintWriter out = new PrintWriter(output);
        // Output directed status.
        if (this.isDirected())
            out.println("DIRECTED");
        else
            out.println("NOT DIRECTED");
        char delimiter = '\t';
        Node[] nodes = this.getNodes();
        Edge[] edges = this.getEdges();
        // Save the nodes:
        for (Node node : nodes) {
            Data d = node.getData();
            out.println("N" + delimiter + node.getId() + delimiter + d.getClass().getName() + delimiter + d.toString());
        }
        // Save the edges:
        for (Edge edge : edges) {
            out.println("E" + delimiter + edge.getSource() + delimiter + edge.getDest() + delimiter + edge.getWeight());
        }
    }

    /**
     * Instantiates a graph from a BufferedReader.  See {@link #write(BufferedWriter)}
     * @param input The input stream.
     * @return Graph
     * @throws IOException If it can't import the graph.
     */
    public static Graph read(BufferedReader input) throws IOException {
        Graph g = new Graph();
        String line;
        // Read first line and get directed status:
        line = input.readLine();
        if (line.equalsIgnoreCase("DIRECTED"))
            g.setDirected(true);
        else
            g.setDirected(false);
        while ((line = input.readLine()) != null) {
            StringTokenizer delimiter = new StringTokenizer(line, "\t");
            String type = delimiter.nextToken();
            if (type.equalsIgnoreCase("N")) { // a node
                try {
                    int id = Integer.parseInt(delimiter.nextToken());
                    String classType = delimiter.nextToken();
                    String dataStr = delimiter.nextToken();
                    Data data = ((Data) Class.forName(classType).newInstance()).getInstance(dataStr);
                    int newId = g.addNode(data);
                    if (id != newId) {
                        // Reset node id
                        Node n = g.getNode(newId);
                        n.setId(id);
                    }
                }
                catch (java.lang.Throwable e) {
                    if (e instanceof ClassNotFoundException) {
                        throw new IOException("Could not find the specified data type class! " + e.getLocalizedMessage());
                    }
                    else if (e instanceof InstantiationException) {
                        throw new IOException("Could not instantiate the specified data type! " + e.getLocalizedMessage());
                    }
                    else if (e instanceof IllegalAccessException) {
                        throw new IOException("Could not access the specified data type." + e.getLocalizedMessage());
                    }
                    else if (e instanceof IOException) {
                        throw (IOException) e;
                    }
                    else if (e instanceof Exception)
                        continue;
                }
            }
            else { // an edge
                try {
                    int src = Integer.parseInt(delimiter.nextToken());
                    int dest = Integer.parseInt(delimiter.nextToken());
                    double wgt = Double.parseDouble(delimiter.nextToken());
                    g.addEdge(new Edge(src, dest, wgt));
                }
                catch (java.lang.Throwable e) {
                    if (e instanceof IOException) {
                        throw (IOException) e;
                    }
                    else if (e instanceof Exception)
                        continue;
                }
            }
        }
        return g;
    }

    public synchronized int getHeapSize() {
        this.changed = false;
        return this.size;
    }

    /**
     *
     * returns an edge of source, destination
     */
    public synchronized Edge getEdge(int source, int dest) {
        if (dest >= this.size || source >= this.size || source < 0 || dest < 0 || source == dest)
            return (new Edge());
        return (this.matrix[source][dest]);
    }

    public synchronized long getEdgeCount() {
        this.changed = false;
        updateDirected();
        if (!this.directed)
            return (this.edgecount / 2);
        return (this.edgecount);
    }

    /**
     * returns the edgematrix as a 1-D array, with no holes
     */
    public synchronized Edge[] getEdges() {
        updateDirected();
        if (this.directed) {
            int cnt = (int) (this.edgecount);
            Edge[] temp = new Edge[cnt];
            for (int i = 0; i < cnt; i++)
                temp[i] = new Edge();
            for (int i = 0; i < cnt; i++) {
                for (int j = 0; j < this.size; j++) {
                    for (int k = 0; k < this.size; k++) {
                        if (k != j && i < cnt && isEdge(k, j)) {
                            temp[i] = this.matrix[k][j];
                            i++;
                        }
                    }
                }
            }
            this.changed = false;
            return temp;
        }
        else {
            int cnt = (int) (this.edgecount / 2);
            Edge[] temp = new Edge[cnt];
            for (int i = 0; i < cnt; i++)
                temp[i] = new Edge();
            for (int i = 0; i < cnt; i++) {
                for (int j = 0; j < this.size; j++) {
                    for (int k = j; k < this.size; k++) {
                        if (k != j && i < cnt && isEdge(k, j)) {
                            temp[i] = this.matrix[k][j];
                            i++;
                        }
                    }
                }
            }
            this.changed = false;
            return temp;
        }
    }

    /**
     * returns the edges of island# key in a compressed 1D array (w/o holes)
     */
    public synchronized Edge[] getEdges(int islandKey) {
        if (islandKey < 0 || islandKey >= this.islandcount) {
            return (new Edge[0]);
        }
        if (this.edgecount < 1) {
            return getEdges();
        }
        updateDirected();
        int cnt = 0, tempid;
        for (int i = 0; i < this.nodecount; i++) {
            tempid = this.islands[islandKey][i].getId();
            if (tempid != -1 && tempid < this.size) {
                for (int j = 0; j < this.size; j++) {
                    if (isEdge(tempid, j))
                        cnt++;
                }
            }
        }
        Edge[] temp;
        if (this.directed) {
            temp = new Edge[cnt];
            int max = cnt;
            cnt = 0;
            int myid;
            for (int i = 0; i < this.nodecount && cnt < max; i++) {
                myid = this.islands[islandKey][i].getId();
                for (int j = 0; j < this.size && myid != -1 && cnt < max; j++) {
                    if (isEdge(myid, j) && cnt < max) {
                        temp[cnt] = this.matrix[myid][j];
                        cnt++;
                    }
                }
            }
        }
        else {
            cnt = cnt / 2;
            temp = new Edge[cnt];
            int max = cnt;
            cnt = 0;
            int myid;
            for (int i = 0; i < this.size && cnt < max; i++) {
                myid = this.islands[islandKey][i].getId();
                for (int j = 0; j < this.size && cnt < max; j++) {
                    if (isEdge(myid, j)) {
                        if (!inArray(temp, myid, j)) {
                            temp[cnt] = this.matrix[myid][j];
                            cnt++;
                        }
                    }
                }
            }
        }
        this.changed = false;
        return temp;
    }

    /**
     * This returns a list of edges (and hence) nearest neighbor children nodes
     * to a node (if any).  Empty list if none are found.
     *
     * @param nodeId The source node id.
     * @return A list of connected edges.
     */
    public synchronized Edge[] getEdgesByNodeId(int nodeId) {
        return Arrays.stream(this.matrix[nodeId]).filter(edge -> edge.getSource() == nodeId)
                     .toArray(Edge[]::new);
    }

    public synchronized Edge[][] getEdgesMatrix() {
        this.changed = false;
        return this.matrix;
    }

    /**
     * Returns a copy of the edge matrix in integer form.
     * Note, this is not the actual reference to the Edge matrix.
     * @return An adjacency matrix.
     */
    public int[][] getMatrixInt() {
        int[][] theCropOInts = new int[this.size][this.size];
        for (int i = 0; i < this.size; i++)
            for (int j = 0; j < this.size; j++)
                if (isEdge(i, j))
                    theCropOInts[i][j] = 1;
        return theCropOInts;
    }

    /**
     * Returns a copy of the edge matrix in boolean form.
     * Note, this is not the actual reference to the Edge matrix.
     * @return An adjacency matrix.
     */
    public boolean[][] getMatrixBoolean() {
        boolean[][] theCropOBools = new boolean[this.size][this.size];
        for (int i = 0; i < this.size; i++)
            for (int j = 0; j < this.size; j++)
                if (isEdge(i, j))
                    theCropOBools[i][j] = true;
        return theCropOBools;
    }

    public synchronized ArrayList<Edge> getEdgesV() {
        Edge[] edges = getEdges();
        //returns an array with all the edges
        //getEdges() returns only half the edges matrix for an undirected graph
        ArrayList<Edge> list = new ArrayList<>();
        int length = edges.length;
        //the size of the edges array same as
        //the edgecount but taking into
        //consideration the state of the graph
        list.addAll(Arrays.asList(edges).subList(0, length));
        this.changed = false;
        return list;
    }

    public synchronized Node[] getHeap() {
        return (this.heap);
    }

    public synchronized int getIslandCount() {
        getIslands();
        this.changed = false;
        return (this.islandcount);
    }

    /**
     * returns the #of nodes in each island in a 1D array
     */
    public synchronized int[] getIslandIndexs(int key) {
        int cnt = 0;
        for (int i = 0; i < this.size; i++) {
            if (this.islands[key][i].getId() != -1)
                cnt++;
        }
        int[] temp = new int[cnt];
        cnt = 0;
        for (int i = 0; i < this.size; i++) {
            if (this.islands[key][i].getId() != -1) {
                temp[cnt] = this.islands[key][i].getId();
                cnt++;
            }
        }
        this.changed = false;
        return temp;
    }

    /**
     * finds the connected components in a graph
     */
    public synchronized void getIslands() {
        this.islandcount = 0;
        this.islands = null;
        this.islands = new Node[this.nodecount][this.nodecount];
        for (int i = 0; i < this.nodecount; i++) {
            for (int j = 0; j < this.nodecount; j++) {
                this.islands[i][j] = new Node();
            }
        }
        if (this.nodecount <= 0) {
            return;
        }
        int i = 0, cnt = 0;
        if (this.edgecount <= 0) {
            for (int j = 0; j < this.size && cnt < this.nodecount; j++) {
                if (this.heap[j].getId() != -1) {
                    this.islands[this.islandcount][0] = this.heap[j];
                    this.islandcount++;
                    cnt++;
                }
            }
            if (this.islandcount != this.nodecount)
                this.islandcount = this.nodecount;
            return;
        }
        cnt = 0;
        while (cnt < this.size && i < this.size) {
            if (this.heap[i].getId() != -1 || this.heap[i].getVisited() == Node.FALSE) {
                AbstractList<Node> list = connected(this.heap[i].getId());
                if (list != null) {
                    cnt += list.size();
                    if (list.size() > 0) {
                        for (int j = 0; j < list.size() && j < this.nodecount && this.islandcount < this.nodecount; j++)
                            this.islands[this.islandcount][j] = list.get(j);
                        this.islandcount++;
                    }
                }
                else
                    i++;
            }
            else {
                i++;
            }
        }
        for (int j = 0; j < this.size; j++)
            this.heap[j].cleanUp();
        this.changed = false;
    }

    /**
     * returns a node
     */
    public synchronized Node getNode(int id) {
        if (id >= this.size || id < 0)
            return (new Node());
        return this.heap[id];
    }

    /**
     * returns the nodes in a 1D array w/o holes
     */
    public synchronized Node[] getNodes() {
        Node[] temp = new Node[this.nodecount];
        int cnt = 0;
        for (int i = 0; i < this.size; i++) {
            if (this.heap[i].getId() != -1) {
                temp[cnt] = this.heap[i];
                cnt++;
            }
        }
        this.changed = false;
        return temp;
    }

    /**
     * returns the nodes in the island# key in a 1D array w/o holes
     */
    public synchronized Node[] getNodes(int key) {
        if (key < 0 || this.islandcount > this.nodecount || key >= this.islandcount) {
            //throws out bad stuff
            return (new Node[0]);
        }
        int cnt = 0;
        for (int i = 0; i < this.nodecount; i++) {
            if (this.islands[key][i].getId() != -1)
                cnt++;
        }
        if (cnt == this.nodecount)
            return getNodes();
        Node[] temp = new Node[cnt];
        int tempcnt = 0;
        for (int i = 0; i < this.nodecount && tempcnt < cnt; i++) {
            if (this.islands[key][i].getId() != -1) {
                temp[tempcnt] = this.heap[this.islands[key][i].getId()];
                tempcnt++;
            }
        }
        this.changed = false;
        return temp;
    }

    public synchronized boolean getChanged() {
        return (this.changed);
    }

    public synchronized boolean isWeighted() {
        this.changed = false;
        return (this.weighted);
    }

    public synchronized int[][] getReachabilityMatrix() {
        return MathUtils.getReachableMatrix(this.getMatrixInt());
    }

    /**
     * Returns true if a given node (childId) is a descendant of the given node (parentId).
     *
     * @param childId The childId (leaf node) to begin the search.
     * @param parentId The parentId (upstream) node which may or may not be a ancestor.
     * @return True if the childId is a descendant of the parentId.
     */
    public boolean isDescendantOf(int childId, int parentId) {
        int[][] r = this.getReachabilityMatrix();
        int[][] t = MathUtils.transpose(r);
        return t[childId][parentId] > 0;
    }

    /**
     * Scans the graph and determines if there is a cycle.
     * @return boolean True if a cycle exists, false otherwise.
     */
    public synchronized boolean isCyclic() {
        boolean[][] theCropOBools = this.getMatrixBoolean();
        for (int h = 0; h < this.size; h++)
            for (int i = 0; i < this.size; i++) {
                boolean sum = true;
                for (int j = 0; j < this.size; j++)
                    if (theCropOBools[j][i])
                        sum = false;
                if (sum)
                    for (int k = 0; k < this.size; k++)
                        theCropOBools[i][k] = false;
            }
        for (int i = 0; i < this.size; i++)
            for (int j = 0; j < this.size; j++)
                if (theCropOBools[i][j])
                    return true;
        return false;
    }

    public synchronized boolean isDirected() {
        this.changed = false;
        return this.directed;
    }

    /**
     * public function will set the boolean variable "directed" according to
     * what it finds when it scans the matrix
     */
    public synchronized void updateDirected() {
        if (this.edgecount < 1)
            return;
        int subcount = 0;
        for (int i = 0; i < this.size; i++) {
            for (int j = 0; j < this.size; j++) {
                if (i != j && isEdge(i, j) && isEdge(j, i) && this.matrix[i][j].getWeight() == this.matrix[j][i].getWeight()) {
                    subcount++;
                }
            }
        }
        this.directed = subcount != this.edgecount;
        this.changed = false;
    }

    public synchronized boolean isEdge(int source, int dest) {
        return source < this.size &&
               dest < this.size &&
               source >= 0 &&
               dest >= 0 &&
               source != dest &&
               this.matrix[source][dest].getWeight() != -1;
    }

    /**
     * Set the private class boolean variable "weighted" according to
     * the state it finds after a scan of the matrix.
     */
    public synchronized void updateWeighted() {
        if (this.edgecount == 0) {
            this.weighted = false;
            return;
        }
        for (int i = 0; i < this.size; i++) {
            for (int j = 0; j < this.size; j++) {
                if (this.matrix[i][j] != null && this.matrix[j][i] != null) {
                    if (this.matrix[i][j].getWeight() > 0 || this.matrix[j][i].getWeight() > 0) {
                        this.weighted = true;
                        return;
                    }
                }
            }
        }
        this.weighted = false;
    }

    public synchronized void modifyData(Node tempnode, Data tempdata) throws GraphException {
        Data temp;
        for (int i = 0; i < this.size; i++) {
            if (this.heap[i].getId() != -1) {
                temp = this.heap[i].getData();
                if (temp.equals(tempdata))
                    throw new GraphException("Cannot Enter Duplicate Data entered");
            }
        }
        tempnode.setData(tempdata);
    }

    public void modifyEdgeWeight(Edge tempedge, double wgt) throws GraphException {
        if (wgt < 0 && wgt != -1) {
            throw new GraphException("Can't add negative weight");
        }
        this.matrix[tempedge.getSource()][tempedge.getDest()].setWeight(wgt);
        if (!this.directed) {
            this.matrix[tempedge.getDest()][tempedge.getSource()].setWeight(wgt);
        }
        updateWeighted();
    }

    /**
     * removes all the edges in a graph
     */
    public synchronized void removeAllEdges() {
        if (this.edgecount == 0)
            return;
        for (int i = 0; i < this.size; i++) {
            for (int j = 0; j < this.size; j++) {
                this.matrix[i][j] = null;
                this.matrix[i][j] = new Edge();
            }
        }
        this.edgecount = 0;
    }

    /**
     * removes all the edges in an array of edges
     */
    public synchronized void removeAllEdges(Edge[] removal) {
        if (this.edgecount == 0)
            return;
        for (Edge aRemoval : removal) {
            for (int i = 0; i < this.size; i++) {
                for (int j = 0; j < this.size; j++) {
                    if (this.matrix[i][j].getDest() == aRemoval.getDest() && this.matrix[i][j].getSource() == aRemoval.getSource()) {
                        this.matrix[i][j] = null;
                        this.matrix[i][j] = new Edge();
                        this.edgecount--;
                    }
                }
            }
        }
        updateWeighted();
    }

    /**
     * removes all the edges in an island
     */
    public synchronized void removeAllEdges(int islandkey) {
        if (this.nodecount == 0)
            return;
        if (islandkey < 0 || islandkey > this.islandcount)
            return;
        removeAllEdges(getEdges(islandkey));
    }

    /**
     * removes all the nodes in a graph
     */
    public synchronized void removeAllNodes() {
        if (this.nodecount == 0)
            return;
        removeAllEdges();
        for (int i = 0; i < this.size; i++) {
            this.heap[i] = null;
            this.heap[i] = new Node();
        }
        this.nodecount = 0;
    }

    /**
     * removes all the nodes in an island
     */
    public synchronized void removeAllNodes(int islandkey) {
        if (this.nodecount == 0)
            return;
        //    int junk=getIslandCount();
        if (islandkey < 0 || islandkey > this.islandcount)
            return;
        removeAllEdges(getEdges(islandkey));
        removeAllNodes(getNodes(islandkey));
    }

    /**
     * removes all the nodes in an array of nodes
     */
    public synchronized void removeAllNodes(Node[] removal) {
        if (this.nodecount == 0)
            return;
        for (Node aRemoval : removal) {
            for (int i = 0; i < this.size; i++) {
                for (int j = 0; j < this.size; j++) {
                    if (this.matrix[i][j].getDest() == aRemoval.getId() || this.matrix[i][j].getSource() == aRemoval.getId()) {
                        this.matrix[i][j] = null;
                        this.matrix[i][j] = new Edge();
                        this.edgecount--;
                    }
                }
            }
        }
        for (Node aRemoval : removal) {
            this.heap[aRemoval.getId()] = null;
            this.heap[aRemoval.getId()] = new Node();
            this.nodecount--;
        }
    }

    /**
     * public graph function...removes an edge from the graph
     *
     */
    public synchronized void removeEdge(Edge temp) {
        // Check for a valid edge before trying to remove it.
        if (temp == null || !isEdge(temp.getSource(), temp.getDest()))
            return;
        if (this.directed) {  // Remove directed edges.
            this.matrix[temp.getSource()][temp.getDest()] = null;
            this.matrix[temp.getSource()][temp.getDest()] = new Edge();
            if (this.edgecount > 0)
                this.edgecount--;
        }
        else {  // Remove undirected edges.
            this.matrix[temp.getSource()][temp.getDest()] = null;
            this.matrix[temp.getSource()][temp.getDest()] = new Edge();
            this.matrix[temp.getDest()][temp.getSource()] = null;
            this.matrix[temp.getDest()][temp.getSource()] = new Edge();
            if (this.edgecount > 1)
                this.edgecount -= 2;
        }
        this.changed = false;
        updateWeighted();
    }

    /**
     * removes a node OVERLOADED function REMOVE NODE!!!!! this one takes data
     * to find in the graph and calls the other function
     */
    public synchronized void removeNode(Data temp) {
        if (temp == null)
            return;
        Data tempdata;
        for (int i = 0; i < this.size; i++) { //search for matching node
            if (this.heap[i].getData() != null) {
                tempdata = this.heap[i].getData();
                if (tempdata.equals(temp)) { //found matching node, delete it
                    // and clean up adjacency matrix
                    removeNode(i);
                    this.changed = false;
                    return;
                }
            }
        }
    }

    /**
     * removes a node OVERLOADED REMOVE NODE!!!!! accepts a node key and
     * deletes that node. leaves a hole in the node array to be filled on
     * insertion
     */
    public synchronized void removeNode(int temp) {
        for (int j = 0; j < this.size; j++) {
            // remove it crosswise from the matrix
            if (this.matrix[j][temp].getDest() != -1) {
                this.edgecount--;
                this.matrix[j][temp] = null;
                this.matrix[j][temp] = new Edge();
            }
            if (this.matrix[temp][j].getDest() != -1) {
                this.edgecount--;
                this.matrix[temp][j] = null;
                this.matrix[temp][j] = new Edge();
            }
        }
        this.nodecount--;
        this.heap[temp] = null;
        this.heap[temp] = new Node();
        this.changed = false;
    }

    /**
     * saves the directed variable
     */
    public synchronized void setDirected(boolean tempdirected) {
        //you may only change a graph to undirected if there are no edges in the graph
        if (this.directed != tempdirected) {
            if (!tempdirected && this.edgecount < 1) {
                this.directed = false;
                this.changed = false;
            }
            else {
                this.directed = tempdirected;
                this.changed = false;
            }
        }
    }

    public synchronized void setChanged(boolean temps) {
        this.changed = temps;
    }

    /**
     * saves the variable weighted
     */
    public synchronized void setWeight(boolean temp) {
        /*
         * can only change weightedness to unwieghted if the graph is weighted and
         * non empty
         */
        if (this.edgecount == 0) {
            this.weighted = temp;
            return;
        }
        updateWeighted();
        if (this.weighted) {
            if (!temp && this.nodecount > 0)
                return;
        }
        if (this.weighted != temp) {
            this.weighted = temp;
            this.changed = false;
        }
    }

    public synchronized void writeout(String filename) throws IOException {
        PrintWriter output;
        try {
            output = new PrintWriter(new FileWriter(filename), true);
            // Delimiter
            String d = "~";
            String n = "N";
            String e = "E";
            Data temp = new StringObj();
            for (int i = 0; i < this.size; i++) {
                if (this.heap[i].getId() != -1)
                    temp = this.heap[i].getData();
                output.print(n + this.heap[i].getId() + d + temp.toString() + d + "\n" + temp);
                if (output.checkError()) { //true if error printing using
                    // output.print. (Doesn't really
                    // generate an exception!)
                    throw new IOException("writeout::Graph - Exception in writeout loop!");
                }
            }
            output.close();
        }
        catch (IOException e) {
            throw new IOException("writeout::Graph - Error writing to " + filename + ". " + e.toString());
        }
    }

    // private functions

    /**
     * creates and initializes the heap array for nodes
     */
    private void buildHeap() throws GraphException {
        try {
            this.heap = new Node[this.size];
            for (int i = 0; i < this.size; i++)
                this.heap[i] = new Node();
        }
        catch (java.lang.Throwable e) {
            throw new GraphException("Unable to Create or Adjust Node Storage Device");
        }
    }

    /**
     * creates and initializes the adjacency matrix
     */
    private void buildMatrix() throws GraphException {
        try {
            this.matrix = new Edge[this.size][this.size];
            for (int i = 0; i < this.size; i++)
                for (int j = 0; j < this.size; j++)
                    this.matrix[i][j] = new Edge();
        }
        catch (Exception except13) {
            throw new GraphException("Unable to Create or Adjust Adjacency Matrix");
        }
    }

    /**
     * returns an AbstractList of connected nodes in one island
     */
    private synchronized AbstractList<Node> connected(int startId) {
        if (startId >= this.size || startId < 0 || this.heap[startId].getId() < 0 || this.heap[startId].getVisited() == Node.TRUE)
            return null;
        ArrayList<Node> list = new ArrayList<Node>();
        this.connected(startId, list);
        return list;
    }

    /**
     * finds a list of connected nodes in one island
     */
    private synchronized void connected(int startId, AbstractList<Node> list) {
        if (startId >= this.size || startId < 0 || this.heap[startId].getId() < 0 || this.heap[startId].getVisited() == Node.TRUE)
            return;
        list.add(this.heap[startId]);
        this.heap[startId].setVisited(Node.TRUE);
        for (int i = 0; i < this.size; i++) {
            if (isEdge(startId, i))
                //if startId points to anything, add it
                connected(i, list);
            else if (isEdge(i, startId))
                //if anything points to startId, add it
                connected(i, list);
        }
    }

    /**
     * copies one matrix to another, helps adjust the size of the adj. matrix
     * accpets the size of the matrices to be copied.
     *
     * This assumes we are dealing only with sqaure matrices.
     */
    private void copyMatrix(Edge[][] to, Edge[][] from, int oldsize) {
        for (int i = 0; i < oldsize; i++)
            System.arraycopy(from[i], 0, to[i], 0, oldsize);
    }

    private synchronized boolean inArray(Edge[] edges, int sour, int dest) {
        if (edges == null)
            return false;
        for (Edge edge : edges) {
            if (edge != null && edge.getSource() == sour && edge.getDest() == dest) {
                return true;
            }
            if (!this.directed) {
                if (edge != null && edge.getSource() == dest && edge.getDest() == sour) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * adjusts the size of the heap if there are too many nodes added for the
     * current size
     */
    private void resizeHeap() {
        if (this.size == MAX_SIZE)
            return;
        Node[] temp = new Node[this.size];
        for (int i = 0; i < this.size; i++)
            temp[i] = new Node();
        for (int i = 0; i < this.size; i++) {
            temp[i].setData(this.heap[i].getData());
            temp[i].setId(this.heap[i].getId());
        }
        int oldsize = this.size;
        if ((this.size + this.size) > MAX_SIZE) {
            this.size = MAX_SIZE;
        }
        else {
            this.size += this.size;
        }
        try {
            buildHeap();
        }
        catch (GraphException except3) {
            //dump except3 to GUI
            return;
        }
        for (int i = 0; i < oldsize; i++) {
            this.heap[i].setData(temp[i].getData());
            this.heap[i].setId(temp[i].getId());
        }
        try {
            resizeMatrix(oldsize);
        }
        catch (Exception except6) {
            //dump except6 to GUI
        }
    }

    /**
     * adjust the size of the adjacency matrix
     */
    private void resizeMatrix(int old) {
        Edge[][] tempmatrix = null;
        try {
            tempmatrix = new Edge[old][old];
            for (int i = 0; i < old; i++)
                for (int j = 0; j < old; j++)
                    tempmatrix[i][j] = new Edge();
        }
        catch (Exception except7) {
            //dump except7 to GUI
            return;
        }
        copyMatrix(tempmatrix, this.matrix, old);
        //copies everything from matrix to tempmatrix
        //check to see if this will cause memory leaks... i think it will
        try {
            buildMatrix();
        }
        catch (GraphException except8) {
            //dump except8 to GUI
            return;
        }
        copyMatrix(this.matrix, tempmatrix, old); //copies everything back
    }
}