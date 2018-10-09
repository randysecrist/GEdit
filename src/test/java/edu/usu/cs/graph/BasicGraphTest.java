package edu.usu.cs.graph;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class BasicGraphTest {

    @Test void canBuildEmptyNonDirectedGraph() {
        Graph g = new Graph();
        assertEquals(0, g.size());
        assertEquals(0, g.getEdgeCount());
        assertFalse(g.isDirected());
        assertFalse(g.isWeighted());
        assertFalse(g.isCyclic());
    }

    @Test void canBuildEmptyDirectedGraph() {
        Graph g = new Graph();
        g.setDirected(true);
        assertEquals(0, g.size());
        assertEquals(0, g.getEdgeCount());
        assertTrue(g.isDirected());
        assertFalse(g.isWeighted());
        assertFalse(g.isCyclic());
    }

    @Test void canBuildGraphWithOneNode() throws GraphException {
        Graph g = new Graph(1);
        assertEquals(1, g.getHeapSize());

        Data a = new StringObj("A");
        Data b = new StringObj("B");
        g.addNode(a);
        g.addNode(b);
        assertEquals(2, g.size());
        assertEquals(2, g.getHeapSize());
        assertEquals(0, g.getEdgeCount());
        assertFalse(g.isDirected());
        assertFalse(g.isWeighted());
        assertFalse(g.isCyclic());
    }

    @Test void canNotAddDuplicateNode() throws GraphException {
        Graph g = new Graph();

        Data a = new StringObj("A");
        g.addNode(a);

        assertThrows(GraphException.class, () -> g.addNode(a), "Should throw exception when adding duplicated node.");
    }

    @Test void canNotAddEdgeToSelf() throws GraphException, GraphWarning {
        Graph g = new Graph();
        Data a = new StringObj("A");
        Data b = new StringObj("B");
        int aId = g.addNode(a);
        int bId = g.addNode(b);
        assertThrows(GraphException.class, () -> g.addEdge(new Edge(aId, aId, 0)), "Should not be able to add self referencing node.");
    }

    @Test void testMultiNodeDirectedGraphWithEdge() throws GraphException, GraphWarning {
        Data testNodeA = new StringObj("Test Node A");
        Data testNodeB = new StringObj("Test Node B");

        int heapSize = 3;
        Graph g = new Graph(heapSize);
        g.setDirected(true);
        assertEquals(heapSize, g.getHeapSize());

        int aId = g.addNode(testNodeA);
        int bId = g.addNode(testNodeB);
        assertEquals(2, g.size());
        assertEquals(2, g.getIslandCount());
        assertFalse(g.isCyclic());
        assertFalse(g.isWeighted());

        Edge e = new Edge(aId, bId, 1);
        g.addEdge(e);
        assertEquals(1, g.getEdgeCount());
        assertTrue(g.isWeighted());
        assertFalse(g.isCyclic());

        // add cycle
        Edge e1 = new Edge(bId, aId, 1);
        g.addEdge(e1);
        assertTrue(g.isCyclic());
    }

    @Test void testMultiNodeNonDirectedGraphWithEdge() throws GraphException, GraphWarning {
        Data testNodeA = new StringObj("Test Node A");
        Data testNodeB = new StringObj("Test Node B");

        int heapSize = 2;
        Graph g = new Graph(heapSize);
        g.setDirected(false);
        assertEquals(heapSize, g.getHeapSize());

        int aId = g.addNode(testNodeA);
        int bId = g.addNode(testNodeB);
        assertEquals(2, g.size());
        assertEquals(2, g.getIslandCount());
        assertFalse(g.isCyclic());
        assertFalse(g.isWeighted());


        Edge e = new Edge(aId, bId, 0);
        g.addEdge(e);
        assertEquals(1, g.getEdgeCount());
        assertFalse(g.isWeighted());
        assertTrue(g.isCyclic()); // non directed graphs are cyclic
    }

    @Test void canRemoveNode() throws GraphException, GraphWarning {
        Data testNodeA = new StringObj("Test Node A");
        Data testNodeB = new StringObj("Test Node B");
        Graph g = new Graph();

        int aId = g.addNode(testNodeA);
        int bId = g.addNode(testNodeB);

        g.addEdge(new Edge(aId, bId, 0));
        assertEquals(1, g.getEdgeCount());
        g.removeNode(testNodeA);
        assertEquals(0, g.getEdgeCount());
        assertEquals(1, g.size());
    }

    @Test void canRemoveEdge() throws GraphException, GraphWarning {
        Data testNodeA = new StringObj("Test Node A");
        Data testNodeB = new StringObj("Test Node B");
        Graph g = new Graph();

        int aId = g.addNode(testNodeA);
        int bId = g.addNode(testNodeB);

        Edge e = new Edge(aId, bId, 0);
        g.addEdge(e);
        g.removeEdge(e);

        assertEquals(2, g.size());
        assertEquals(0, g.getEdgeCount());
    }

}