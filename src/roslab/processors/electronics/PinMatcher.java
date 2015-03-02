/**
 *
 */
package roslab.processors.electronics;

import java.util.HashMap;
import java.util.Map;

/**
 * Pin Matching algorithm
 *
 * @author Peter Gebhard
 */
public class PinMatcher {

    /**
     * @return the schematic's name
     */
    public static Map<Integer, Integer> match(Integer[][] pinMatrix) {
        Map<Integer, Integer> result = new HashMap<Integer, Integer>();

        while (!fullyConnected(pinMatrix)) {
            if (!remainingConnectables(pinMatrix)) {
                return null;
            }

            Integer[] rowSums = rowSums(pinMatrix);
            Integer[] colSums = columnSums(pinMatrix);

            int i = minNonZeroSumIndex(rowSums);
            int j = maskedMinNonZeroSumIndex(colSums, pinMatrix[i]);
            result.put(i, j);

            maskMatrixRow(pinMatrix, i);
            maskMatrixColumn(pinMatrix, j);
        }

        // Return the match result
        return result;
    }

    private static int minNonZeroSumIndex(Integer[] sums) {
        if (sums.length == 0) {
            throw new IllegalArgumentException();
        }

        int sum = Integer.MAX_VALUE;
        int index = -1;

        for (int i = 0; i < sums.length; i++) {
            if (sums[i] > 0 && sums[i] < sum) {
                sum = sums[i];
                index = i;
            }
        }

        return index;
    }

    private static int maskedMinNonZeroSumIndex(Integer[] sums, Integer[] mask) {
        if (sums.length == 0 || mask.length == 0 || sums.length != mask.length) {
            throw new IllegalArgumentException();
        }

        int sum = Integer.MAX_VALUE;
        int index = -1;

        for (int i = 0; i < sums.length; i++) {
            if (sums[i] > 0 && sums[i] < sum && mask[i] > 0) {
                sum = sums[i];
                index = i;
            }
        }

        return index;
    }

    private static void maskMatrixRow(Integer[][] pinMatrix, int row) {
        for (int j = 0; j < pinMatrix[row].length; j++) {
            pinMatrix[row][j] = -1;
        }
    }

    private static void maskMatrixColumn(Integer[][] pinMatrix, int col) {
        for (int i = 0; i < pinMatrix.length; i++) {
            pinMatrix[i][col] = -1;
        }
    }

    private static Integer[] rowSums(Integer[][] pinMatrix) {
        Integer[] result = new Integer[pinMatrix[0].length];
        for (int e = 0; e < result.length; e++) {
            result[e] = 0;
        }

        for (int i = 0; i < pinMatrix.length; i++) {
            for (int j = 0; j < pinMatrix[i].length; j++) {
                if (pinMatrix[i][j] >= 0) {
                    result[i] += pinMatrix[i][j];
                }
            }
        }

        return result;
    }

    private static Integer[] columnSums(Integer[][] pinMatrix) {
        Integer[] result = new Integer[pinMatrix.length];
        for (int e = 0; e < result.length; e++) {
            result[e] = 0;
        }

        for (int i = 0; i < pinMatrix.length; i++) {
            for (int j = 0; j < pinMatrix[i].length; j++) {
                if (pinMatrix[i][j] >= 0) {
                    result[j] += pinMatrix[i][j];
                }
            }
        }

        return result;
    }

    private static boolean fullyConnected(Integer[][] pinMatrix) {
        for (int i = 0; i < pinMatrix.length; i++) {
            for (int j = 0; j < pinMatrix[i].length; j++) {
                if (pinMatrix[i][j] >= 0) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean remainingConnectables(Integer[][] pinMatrix) {
        for (int i = 0; i < pinMatrix.length; i++) {
            for (int j = 0; j < pinMatrix[i].length; j++) {
                if (pinMatrix[i][j] >= 1) {
                    return true;
                }
            }
        }
        return false;
    }

}
