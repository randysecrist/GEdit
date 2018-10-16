package com.reformation.graph.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

/**
 * Test case for the com.secrist.util.MathUtils class.
 * Created on May 22, 2004
 * @author Randy Secrist
 */
@TestInstance(Lifecycle.PER_CLASS)
class MathUtilsTest {

    private Logger log = LoggerFactory.getLogger(MathUtilsTest.class);

    /**
     * Size of matrices to test.
     */
    private int size = 6;

    /*
     * Four sets of matrix variables to test with.
     */
    private int[][] a = new int[size][size];
    private int[][] b = new int[size][size];
    private double[][] c = new double[size][size];
    private double[][] d = new double[size][size];
    private double[] constants = new double[size];

    /**
     * Sets up this JUnit TestCase.
     */
    @BeforeAll
    void setUp() throws Exception {
        // Init Matrix Variables
        for(int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {
                a[i][j] = (int)(MathUtils.random() * 11);
                b[i][j] = (int)(MathUtils.random() * 11);
                c[i][j] = MathUtils.random() * 11;
                d[i][j] = MathUtils.random() * 11;
            }
            constants[i] = MathUtils.random() * 11;
        }
    }

    /**
     * Tests the ability to find a diagonal within a matrix.
     */
    @Test void testDiagonal() {
        log.debug(MathUtils.printMatrix("testDiagonal\nA", a));
        int[] resultA = MathUtils.getDiagonal(a);

        log.debug(MathUtils.printArray("Diagonal", resultA));

        log.debug(MathUtils.printMatrix("testDiagonal\nC", c));
        double[] resultC = MathUtils.getDiagonal(c);

        log.debug(MathUtils.printArray("Diagonal", resultC));
    }

    /**
     * Tests the ability to multiply two integer matrices.
     */
    @Test void testMatrixMultInt() {
        log.debug(MathUtils.printMatrix("A", a));
        log.debug(MathUtils.printMatrix("B", b));

        // Process
        int[][] result = MathUtils.matrixMult(a, b);

        // Print result
        log.debug(MathUtils.printMatrix("testMatrixMultInt", result));
    }

    /**
     * Tests the ability to multiply two double matrices.
     */
    @Test void testMatrixMultDouble() {
        log.debug(MathUtils.printMatrix("C", c));
        log.debug(MathUtils.printMatrix("D", d));

        // Process
        double[][] result = MathUtils.matrixMult(c, d);

        // Print result
        log.debug(MathUtils.printMatrix("testMatrixMultDouble", result));
    }

    /**
     * Calculates PI out to 100 digits.
     */
    @Test void testPI() {
        String pi = "MathUtils.PI:\t" + MathUtils.PI(100, BigDecimal.ROUND_HALF_DOWN);
        log.debug(pi);
    }

    /**
     * Solves a random linear system.
     */
    @Test void testSolveLinearSystem() {
        log.debug(MathUtils.printMatrix("C", c));
        log.debug(MathUtils.printArray("Constants", constants));

        // Solve
        double[] solution = MathUtils.solve(c, constants);

        log.debug(MathUtils.printArray("Solution", solution));
    }

    /**
     * Finds the reachability of a given adjacency matrix.
     */
    @Test void testReachabilityMatrix() {
        //int[][] a = {{0,1,0},{0,0,1},{1,0,0}};
        int[][] a = {
            {0,1,0,0,0,1,0,0,1,0,0,0},
            {0,0,1,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,1,0,0,0,0,0},
            {0,0,0,0,0,0,0,1,0,0,0,1},
            {0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,1,0,0},
            {0,1,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,1,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,1,0,0,0,0,0,0,0,1,0},
            {0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0}
        };

        int[][] expected = {
            {0,1,2,0,0,1,3,0,1,2,3,0},
            {0,3,1,0,0,0,2,0,0,0,0,0},
            {0,2,3,0,0,0,1,0,0,0,0,0},
            {0,0,0,0,2,0,0,1,0,0,0,1},
            {0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,2,0,0,0,3,0,0,1,2,0},
            {0,1,2,0,0,0,3,0,0,0,0,0},
            {0,0,0,0,1,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0},
            {0,3,1,0,0,0,2,0,0,0,1,0},
            {0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0}
        };

        log.debug(MathUtils.printMatrix("A", a));
        int[][] result = MathUtils.getReachableMatrix(a);
        log.debug(MathUtils.printMatrix("testReachablilityMatrix", result));
        assertTrue(MathUtils.matrixEquals(expected, result));
    }

    @Test void testRandom() {
        int zero = MathUtils.random(0, 0);
        assertEquals(0, zero);

        int one = MathUtils.random(1, 1);
        assertEquals(1, 1);

        int range = MathUtils.random(0, 3);
        assertTrue(range >= 0 && range <= 3);
    }

    /**
     * Tears down this JUnit TestCase.
     */
    @AfterAll
    public void tearDown() throws Exception {
        a = b = null;
        c = d = null;
    }
}