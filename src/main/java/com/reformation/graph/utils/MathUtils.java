package com.reformation.graph.utils;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * This class performs extra math functions not found in the standard JDK.
 * When possible, these functions are implemented using native optimization.
 *
 * TOOD implement the ability to use the native methods or not. (reflection delegate?)
 *
 * Created on May 5, 2004
 */
public class MathUtils {
    /*
     * Math Constants
     */
    public static final double EPSILON = 2.220446049250313e-16;
    public static final double SQRT_EPSILON = 1.4901161193847656e-8;
    public static final double SQRT_SQRT_EPSILON = 1.220703125e-4;

    /**
     * Returns the diagonal in a matrix as an array of integers.
     * @param x The square matrix to retreive the diagonal from.
     * @return A single dimention array representing the diagonal values.
     */
    public static native int[] diag(int[][] x);

    /**
     * Returns the diagonal in a matrix.
     * @param x The square matrix to retreive the diagonal from.
     * @return A single dimention array representing the diagonal values.
     */
    public static native double[] diag(double[][] x);

    /**
     * Multiplies square matrix x with square matrix y.
     * @param x The first operand.
     * @param y The second operand.
     * @return A new matrix of integer values.
     */
    public static native int[][] mult(int[][] x, int[][] y);

    /**
     * Multiplies square matrix x with square matrix y.
     * @param x The first operand.
     * @param y The second operand.
     * @return A new matrix of double percision floating point values.
     */
    public static native double[][] mult(double[][] x, double[][] y);

    /**
     * Computes EPSILON from scratch.
     * @return A computed EPSILON.
     */
    public static double computeEpsilon() {
        double eps = 1.0;
        while ((eps + 1.0) != 1.0) {
            eps /= 2.0;
        }
        eps *= 2.0;
        return eps;
    }

    /**
     * Returns true if the relative difference between two numbers
     * is smaller than the SQRT_EPSILON.
     * @param x The first number.
     * @param y The second number.
     * @return True if relative difference is <= SQRT_EPSILON.
     */
    public static boolean sameEpsilon(double x, double y) {
        return Math.abs((x / y) - 1.0) <= SQRT_EPSILON;
    }

    /**
     * Returns true if matrix x is equal to matrix y.
     * @param x The first operand.
     * @param y The second operand
     * @return True if matrix x is equal to matrix y.
     *
     * CHECKSTYLE:OFF
     */
    public static boolean matrixEquals(int[][] x, int[][] y) {
        if ((x.length <= 0) || (y.length <= 0)) {
            return false;
        }
        else {
            assert ((x.length > 0) && (y.length > 0));
        }

        int xsize = x[0].length;
        int ysize = y[0].length;
        if ((x.length != xsize) || (y.length != ysize)) {
            return false;
        }
        if ((x.length != y.length) || (xsize != ysize)) {
            return false;
        }

        for (int row = 0; row < x.length; row++) {
            for (int column = 0; column < x[row].length; column++) {
                if (x[row][column] != y[row][column]) {
                    return false;
                }
            }
        }
        return true;
    }
    // CHECKSTYLE:ON

    /**
     * Multipies square matrix x with square matrix y.
     * @param x The first operand.
     * @param y The second operand.
     * @return int[][] A new matrix of integer values.
     */
    public static int[][] matrixMult(int[][] x, int[][] y) {
        int[][] z = new int[x.length][y.length];
        for (int row = 0; row < x.length; row++) {
            for (int i = 0; i < y.length; i++) {
                z[row][i] = 0;
                for (int j = 0; j < x.length; j++) {
                    z[row][i] += (x[row][j] * y[j][i]);
                }
            }
        }
        return z;
    }

    /**
     * Multipies square matrix x with square matrix y.
     * @param x The first operand.
     * @param y The second operand.
     * @return A new matrix of double values.
     */
    public static double[][] matrixMult(double[][] x, double[][] y) {
        RealMatrix xM = new Array2DRowRealMatrix(x);
        RealMatrix yM = new Array2DRowRealMatrix(y);
        return xM.multiply(yM).getData();
    }

    /**
     * Returns the diagonal in a matrix as an array of integers.
     * @param x The square matrix to retreive the diagonal from.
     * @return A single dimention array representing the diagonal values.
     */
    public static int[] getDiagonal(int[][] x) {
        if (x.length <= 0) {
            return null;
        }
        else {
            assert (x.length > 0);
        }

        int isize = x[0].length;
        if (x.length != isize) {
            throw new IllegalArgumentException("Matrix must be square!");
        }

        int[] diag = new int[isize];
        for (int i = 0; i < x.length; i++) {
            diag[i] = x[i][i];
        }
        return diag;
    }

    /**
     * Returns the diagonal in a matrix as an array of doubles.
     * @param x The square matrix to retreive the diagonal from.
     * @return A single dimention array representing the diagonal values.
     */
    public static double[] getDiagonal(double[][] x) {
        RealMatrix xM = new Array2DRowRealMatrix(x);
        if (!xM.isSquare()) {
            throw new IllegalArgumentException("Matrix must be square!");
        }

        int isize = xM.getColumnDimension();
        double[] diag = new double[isize];
        for (int i = 0; i < x.length; i++) {
            diag[i] = x[i][i];
        }
        return diag;
    }

    /**
     * Returns the  trace of the matrix (the sum of the elements on the main diagonal).
     * @param x The input matrix.
     * @return The matrix trace.
     */
    public static double getTrace(double[][] x) {
        RealMatrix xM = new Array2DRowRealMatrix(x);
        return xM.getTrace();
    }

    /**
     * Returns the PI calculated out to a specific number of digits.
     * This uses the basic principal that Pi = 16ATN(1/5) - 4ATN(1/239),
     * which follows the geometric series -> ATN(x) = x - x^3/3 + x^5/5 - x^7/7 ...
     *
     * For best accuracy, use the roundmode ROUND_HALF_DOWN.
     *
     * Note that the overall complexity of this function is O(n^2).
     *
     * @param digits The number of digits to calculate PI out to,
     * @param roundmode The BigDecimal round mode to follow.
     * @return A BigDecimal which represents PI.
     */
    public static BigDecimal PI(int digits, int roundmode) {
        digits += 4;
        BigDecimal one = new BigDecimal("1");
        BigDecimal four = new BigDecimal("4");
        BigDecimal sixteen = new BigDecimal("16");
        BigDecimal oneby5 = new BigDecimal("0.2");
        BigDecimal oneby239 = one.divide(new BigDecimal("239"), digits, roundmode);

        BigDecimal atn15 = oneby5; // initialize to first term of arctan series.
        BigDecimal powercum = oneby5; // start with first power.
        BigDecimal xsq = oneby5.multiply(oneby5);

        for (int i = 3; i <= ((3 * digits) / 2); i += 4) {
            powercum = powercum.multiply(xsq);
            atn15 = atn15.subtract(powercum.divide(new BigDecimal(String.valueOf(i)), digits, roundmode));
            powercum = powercum.multiply(xsq);
            atn15 = atn15.add(powercum.divide(new BigDecimal(String.valueOf(i + 2)), digits, roundmode));
            atn15 = atn15.setScale(digits, roundmode);
        }

        // calculate arctan(1/239) to tscale digits of precision:
        // Based on log10(1/239) need about 0.5*digits maximum exponent.
        BigDecimal atn239 = oneby239; // initialize to first term of arctan series.
        powercum = oneby239; // start with first power.
        xsq = oneby239.multiply(oneby239);

        for (int i = 3; i <= (digits / 2); i += 4) {
            powercum = (powercum.multiply(xsq)).setScale(digits, roundmode);
            atn239 = atn239.subtract(powercum.divide(new BigDecimal(String.valueOf(i)), roundmode));
            powercum = (powercum.multiply(xsq)).setScale(digits, roundmode);
            atn239 = atn239.add(powercum.divide(new BigDecimal(String.valueOf(i + 2)), roundmode));
            atn239 = atn239.setScale(digits, roundmode);
        }

        BigDecimal bigPI = sixteen.multiply(atn15).subtract(four.multiply(atn239));

        // trunc inaccurate part
        return bigPI.setScale(digits -= 4, BigDecimal.ROUND_FLOOR);
    }

    /**
     * Returns a random integer between (inclusive) the
     * supplied max and min values.
     *
     * @param min The minimum value the random number should be equal to or higher than.
     * @param max The maximum value the random number should be equal to or less than.
     * @return A uniformly distributed random integer.
     */
    public static int random(int min, int max) {
        if (min > max) {
            return max + (int) (((min - max + 1) * random()) / 1.0);
        }
        else {
            return min + (int) (((max - min + 1) * random()) / 1.0);
        }
    }

    /**
     * Uses the java.security.SecureRandom method of generating random numbers.
     * @return  the next pseudorandom, uniformly distributed
     *          <code>double</code> value between <code>0.0</code> and
     *          <code>1.0</code> from this random number generator's sequence.
     */
    public static double random() {
        double randomNumber = 0.0d;
        try {
            SecureRandom randGen = SecureRandom.getInstance("SHA1PRNG");
            randomNumber = randGen.nextDouble();
        }
        catch (NoSuchAlgorithmException e) {
            randomNumber = Math.random();
        }
        return randomNumber;
    }

    /**
     * Checks an adjacency matrix for a cycle.
     * @param x The matrix to check.
     * @return Returns true if a cycle exists, false otherwise.
     */
    public static boolean isCycle(int[][] x) {
        int[] diagonal = MathUtils.getDiagonal(x);
        for (int i = 0; i < diagonal.length; i++) {
            if (diagonal[i] > 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the reachable matrix for a given matrix x.
     * If multiple paths exist, this returns the shortest possible path between
     * any given two nodes.
     *
     * @param x The input matrix.
     * @return A new matrix which represents the reachable matrix.
     */
    public static int[][] getReachableMatrix(int[][] x) {
        int[][][] frames = new int[x.length * 2][x.length][x.length];
        int iterations = recurseRechability(x, x, frames, 0, true);
        int[][] rtnVal = new int[x.length][x.length];
        // Merge Input
        MathUtils.mergeMatrix(rtnVal, x, 1);

        // Merge Frames
        for (int i = 0; (i <= (iterations + 1)) && (i < frames.length); i++) {
            MathUtils.mergeMatrix(rtnVal, frames[i], i + 2);
        }
        return rtnVal;
    }

    /**
     * Returns the transpose matrix.
     * @param x The input matrix.
     * @return transpose matrix
     */
    public static int[][] transpose(int[][] x) {
        if ((x == null) || (x.length == 0)) {
            throw new IllegalArgumentException("Input matrix must be at least one dimension.");
        }
        int nRows = x.length;
        int nCols = x[0].length;
        int[][] rtnVal = new int[nCols][nRows];
        for (int row = 0; row < nRows; row++) {
            for (int col = 0; col < nCols; col++) {
                rtnVal[col][row] = x[row][col];
            }
        }
        return rtnVal;
    }

    /**
     * Returns the transpose matrix.
     * @param x The input matrix.
     * @return transpose matrix
     */
    public static double[][] transpose(double[][] x) {
        RealMatrix xM = new Array2DRowRealMatrix(x);
        return xM.transpose().getData();
    }

    /**
     * Returns the transpose matrix.
     * @param x The input matrix.
     * @return transpose matrix
     */
    public static String[][] transpose(String[][] x) {
        if ((x == null) || (x.length == 0)) {
            throw new IllegalArgumentException("Input matrix must be at least one dimension.");
        }
        int nRows = x.length;
        int nCols = x[0].length;
        String[][] rtnVal = new String[nCols][nRows];
        for (int row = 0; row < nRows; row++) {
            for (int col = 0; col < nCols; col++) {
                rtnVal[col][row] = x[row][col];
            }
        }
        return rtnVal;
    }

    /**
     * Internal recursion function for finding a reachability matrix.
     * @param x The original matrix to use as an operand.
     * @param m1 A second (previously multiplied) matrix to use as the second operand.
     * @param frames An internal array of matricies which is used to hold the results of each multiplication.
     * @param cnt The current level of recursion.
     * @param shortcircutCycles If true, this function returns early if a cycle is detected.
     * @return An integer which represents the levels of recursion (steps) processed.
     */
    private static int recurseRechability(int[][] x, int[][] m1, int[][][] frames, int cnt, boolean shortcircutCycles) {
        if (cnt < (x.length * 2)) {
            frames[cnt] = MathUtils.matrixMult(x, m1);

            // Check diagonal for cycles
            if (shortcircutCycles) {
                int[] diag = MathUtils.getDiagonal(frames[cnt]);
                for (int i = 0; i < diag.length; i++) {
                    if (diag[i] > 0) {
                        return cnt;
                    }
                }
            }
            return recurseRechability(m1, frames[cnt], frames, cnt + 1, shortcircutCycles);
        }
        else {
            return cnt;
        }
    }

    /**
     * Merges matrix y into matrix x, replacing the appropriate cell with
     * the integer steps, which represents how many steps to reach a given node.
     * @param x The master copy.
     * @param y The matrix to merge.
     * @param steps The number up steps to merge into a cell.
     */
    private static void mergeMatrix(int[][] x, int[][] y, int steps) {
        for (int i = 0; i < x.length; i++) {
            for (int j = 0; j < x[i].length; j++) {
                if ((y[i][j] != 0) && (y[i][j] > x[i][j])) {
                    x[i][j] = steps;
                }
            }
        }
        return;
    }

    /**
     * Solves a linear system of equations.
     * @param coefficents The matrix of coefficents.
     * @param constants An array of constants.
     * @return An array of solutions for each variable.
     */
    public static double[] solve(double[][] coefficents, double[] constants) {
        RealMatrix xM = new Array2DRowRealMatrix(coefficents);
        DecompositionSolver solver = new LUDecomposition(xM).getSolver();
        RealVector rV = new ArrayRealVector(constants, false);
        RealVector solution = solver.solve(rV);
        return ((ArrayRealVector)solution).getDataRef();
    }

    /**
     * Writes a matrix to a Log4j logger.
     * @param name The name of the matrix variable.
     * @param matrix The matrix to print.
     */
    public static String printMatrix(String name, int[][] matrix) {
        StringBuffer out = new StringBuffer("\n" + name + " is:\n");
        out.ensureCapacity(matrix.length * matrix.length * 2);
        for (int[] matrix1 : matrix) {
            for (int j = 0; j < matrix1.length; j++) {
                out.append(matrix1[j] + "\t");
            }
            out.append("\n");
        }
        return out.toString();
    }

    /**
     * Writes a matrix to a Log4j logger.
     * @param name The name of the matrix variable.
     * @param matrix The matrix to print.
     */
    public static String printMatrix(String name, double[][] matrix) {
        StringBuffer out = new StringBuffer("\n" + name + " is:\n");
        out.ensureCapacity(matrix.length * matrix.length * 2);
        for (double[] matrix1 : matrix) {
            for (int j = 0; j < matrix1.length; j++) {
                out.append(matrix1[j] + "\t");
            }
            out.append("\n");
        }
        return out.toString();
    }

    /**
     * Writes an array to a Log4j logger.
     * @param name The name of the array variable.
     * @param array The array to print.
     */
    public static String printArray(String name, double[] array) {
        StringBuffer out = new StringBuffer("\n" + name + " is:\n");
        for (double v : array) {
            out.append(v + "\n");
        }
        return out.toString();
    }

    /**
     * Writes an array to a Log4j logger.
     * @param name The name of the array variable.
     * @param array The array to print.
     */
    public static String printArray(String name, int[] array) {
        StringBuffer out = new StringBuffer("\n" + name + " is:\n");
        for (int i1 : array) {
            out.append(i1 + "\t");
        }
        return out.toString();
    }
}