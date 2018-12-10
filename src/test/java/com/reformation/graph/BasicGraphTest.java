package com.reformation.graph;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.reformation.graph.utils.Pair;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.time.Instant;

class BasicGraphTest {

    @Test void newGraphHasUUIDs() throws InterruptedException {
        Instant beforeCreation = Instant.now();
        Thread.sleep(1);
        Graph g = new Graph();
        assertNotNull(g.getTimestampUUID());
        assertNotNull(g.getRandomUUID());
        assertTrue(beforeCreation.isBefore(g.creationTime()));
    }

    @Test void canBuildEmptyNonDirectedGraph() {
        Graph g = new Graph();
        assertEquals(0, g.size());
        assertEquals(g.size(), g.getNodeCount());
        assertEquals(0, g.getEdgeCount());
        assertFalse(g.isDirected());
        assertFalse(g.isWeighted());
        assertFalse(g.isCyclic());
    }

    @Test void canBuildEmptyDirectedGraph() {
        Graph g = new Graph();
        g.setDirected(true);
        assertEquals(0, g.size());
        assertEquals(g.size(), g.getNodeCount());
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

        // Check for two islands with no edges yet
        assertEquals(1, g.getNodes(0).length);
        assertEquals(1, g.getNodes(1).length);
        assertEquals(0, g.getNodes(2).length);
        assertEquals(0, g.getEdges(0).length);
        Integer[] islandOne = g.getIslandByIndex(0);
        Integer[] islandTwo = g.getIslandByIndex(1);
        assertEquals(1, islandOne.length);
        assertEquals(1, islandTwo.length);
        assertEquals(aId, islandOne[0].intValue());
        assertEquals(bId, islandTwo[0].intValue());

        // Add and check new edge
        Edge e = new Edge(aId, bId, 1);
        g.addEdge(e);
        assertEquals(1, g.getEdgeCount());
        assertTrue(g.isWeighted());
        assertFalse(g.isCyclic());

        // Check island counts (nodes and edges) are updated
        assertEquals(1, g.getIslandCount());
        assertEquals(2, g.getNodes(0).length);
        assertEquals(0, g.getNodes(1).length);
        assertEquals(0, g.getNodes(2).length);
        assertEquals(1, g.getEdges(0).length);
        assertEquals(1, g.getEdgesByNodeId(aId).length);
        assertEquals(0, g.getEdgesByNodeId(bId).length);

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

        // Check for two islands with no edges yet
        assertEquals(1, g.getNodes(0).length);
        assertEquals(1, g.getNodes(1).length);
        assertEquals(0, g.getNodes(2).length);
        assertEquals(0, g.getEdges(0).length);
        Integer[] islandOne = g.getIslandByIndex(0);
        Integer[] islandTwo = g.getIslandByIndex(1);
        assertEquals(1, islandOne.length);
        assertEquals(1, islandTwo.length);
        assertEquals(aId, islandOne[0].intValue());
        assertEquals(bId, islandTwo[0].intValue());

        Edge e = new Edge(aId, bId, 0);
        g.addEdge(e);
        assertEquals(1, g.getEdgeCount());
        assertFalse(g.isWeighted());
        assertTrue(g.isCyclic()); // non directed graphs are cyclic

        // Check island counts (nodes and edges) are updated
        assertEquals(1, g.getIslandCount());
        assertEquals(2, g.getNodes(0).length);
        assertEquals(0, g.getNodes(1).length);
        assertEquals(0, g.getNodes(2).length);
        assertEquals(1, g.getEdges(0).length);
        assertEquals(1, g.getEdgesByNodeId(aId).length);
        assertEquals(0, g.getEdgesByNodeId(bId).length);
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

    @Test void graphCanHaveCycles() throws GraphException {
        Data a = new StringObj("A");
        Data h = new StringObj("H");
        Graph graph = buildDirectedGraphWithNoCycle(a, h);
        assertFalse(graph.isCyclic());
        assertEquals(8, graph.getEdges().length);
        graph.addEdge(h, a); // add cycle
        assertEquals(9, graph.getEdges().length);
        assertTrue(graph.isCyclic());
    }

    @Test void canWriteAndReadGraphWithStreams() throws GraphException, IOException {
        Data a = new StringObj("A");
        Data h = new StringObj("H");
        Graph inputGraph = buildDirectedGraphWithNoCycle(a, h);
        assertEquals(8, inputGraph.getEdges().length);
        StringWriter capture = new StringWriter();
        BufferedWriter writer = new BufferedWriter(capture);
        inputGraph.write(writer);
        writer.flush();
        assertNotNull(capture.toString());
        assertTrue(capture.toString().length() > 0);

        BufferedReader reader = new BufferedReader(new StringReader(capture.toString()));
        Graph outputGraph = Graph.read(reader);

        assertEquals(inputGraph.size(), outputGraph.size());
        assertEquals(inputGraph.isDirected(), outputGraph.isDirected());
        assertEquals(inputGraph.isWeighted(), outputGraph.isWeighted());
        assertEquals(inputGraph.getEdges().length, outputGraph.getEdges().length);
        assertEquals(inputGraph.getEdgeCount(), outputGraph.getEdgeCount());
        assertEquals(inputGraph.getEdgeCount(), inputGraph.getEdges().length);
        assertEquals(inputGraph.getRandomUUID(), outputGraph.getRandomUUID());
        assertEquals(outputGraph.getEdgeCount(), outputGraph.getEdges().length);
    }

    private Graph buildDirectedGraphWithNoCycle(Data a, Data h) throws GraphException {
        Data b = new StringObj("B");
        Data c = new StringObj("C");
        Data d = new StringObj("D");
        Data e = new StringObj("E");
        Data f = new StringObj("F");
        Data g = new StringObj("G");

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

        assertTrue(graph.isDescendantOf(nodesDF.value, nodesAB.key));
        assertTrue(graph.isDescendantOf(nodesFH.value, nodesAD.value));
        assertTrue(graph.isDescendantOf(nodesEF.value, nodesEF.key));
        assertFalse(graph.isDescendantOf(nodesFH.value, nodesAB.value));
        assertFalse(graph.isDescendantOf(nodesDF.value, nodesAC.value));
        assertFalse(graph.isDescendantOf(nodesDG.value, nodesDF.value));
        assertFalse(graph.isDescendantOf(nodesDG.value, nodesEF.value));
        assertFalse(graph.isDescendantOf(nodesFH.value, nodesDG.value));
        return graph;
    }

}