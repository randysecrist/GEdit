package com.reformation.graph;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.reformation.graph.utils.Pair;
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

    @Test void canNotAddEdgeToSelf() throws GraphException {
        Graph g = new Graph();
        Data a = new StringObj("A");
        Data b = new StringObj("B");
        int aId = g.addNode(a);
        int bId = g.addNode(b);
        assertThrows(GraphException.class, () -> g.addEdge(new Edge(aId, aId, 0)), "Should not be able to add self referencing node.");
    }

    @Test void testMultiNodeDirectedGraphWithEdge() throws GraphException {
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

    @Test void testMultiNodeNonDirectedGraphWithEdge() throws GraphException {
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

    @Test void canRemoveNode() throws GraphException {
        Data testNodeA = new StringObj("Test Node A");
        Data testNodeB = new StringObj("Test Node B");
        Graph g = new Graph();

        assertEquals(0, g.getNodes().length);

        int aId = g.addNode(testNodeA);
        int bId = g.addNode(testNodeB);

        assertEquals(2, g.getNodes().length);

        g.addEdge(new Edge(aId, bId, 0));
        assertEquals(1, g.getEdgeCount());
        g.removeNode(testNodeA);
        assertEquals(0, g.getEdgeCount());
        assertEquals(1, g.size());
        assertEquals(1, g.getNodes().length);

    }

    @Test void canRemoveEdge() throws GraphException {
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

    @Test void nodesAreDescendantsOf() throws GraphException {
        Data a = new StringObj("A");
        Data b = new StringObj("B");
        Data c = new StringObj("C");
        Data d = new StringObj("D");
        Data e = new StringObj("E");
        Data f = new StringObj("F");
        Data g = new StringObj("G");
        Data h = new StringObj("H");


        Graph graph = new Graph(8);
        graph.setDirected(true);
        Pair<Integer, Integer> nodesAB = graph.addEdge(a, b);
        Pair<Integer, Integer> nodesAC = graph.addEdge(a, c);
        Pair<Integer, Integer> nodesAD = graph.addEdge(a, d);
        Pair<Integer, Integer> nodesDE = graph.addEdge(d, e);
        Pair<Integer, Integer> nodesDF = graph.addEdge(d, f);
        Pair<Integer, Integer> nodesDG = graph.addEdge(d, g);
        Pair<Integer, Integer> nodesEF = graph.addEdge(e, f);

        Pair<Integer, Integer> nodesFH = graph.addEdge(f, h);
        assertFalse(graph.isCyclic());
        Pair<Integer, Integer> nodesHA = graph.addEdge(h, a);
        assertTrue(graph.isCyclic());

        assertTrue(graph.isDescendantOf(nodesDF.value, nodesAB.key));
        assertTrue(graph.isDescendantOf(nodesFH.value, nodesAD.value));
        assertTrue(graph.isDescendantOf(nodesEF.value, nodesEF.key));
        assertFalse(graph.isDescendantOf(nodesFH.value, nodesAB.value));
        assertFalse(graph.isDescendantOf(nodesDF.value, nodesAC.value));
        assertFalse(graph.isDescendantOf(nodesDG.value, nodesDF.value));
        assertFalse(graph.isDescendantOf(nodesDG.value, nodesEF.value));
        assertFalse(graph.isDescendantOf(nodesFH.value, nodesDG.value));
    }

}